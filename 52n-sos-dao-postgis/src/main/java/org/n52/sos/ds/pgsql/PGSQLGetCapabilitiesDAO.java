/***************************************************************
 Copyright (C) 2008
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under 
 the terms of the GNU General Public License version 2 as published by the 
 Free Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 this program (see gnu-gpl v2.txt). If not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 visit the Free Software Foundation web page, http://www.fsf.org.

 Author: <LIST OF AUTHORS/EDITORS>
 Created: <CREATION DATE>
 Modified: <DATE OF LAST MODIFICATION (optional line)>
 ***************************************************************/

package org.n52.sos.ds.pgsql;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractTimeGeometricPrimitiveType;
import net.opengis.gml.BoundingShapeType;
import net.opengis.gml.CodeType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.ReferenceType;
import net.opengis.gml.TimePeriodType;
import net.opengis.ows.x11.AllowedValuesDocument;
import net.opengis.ows.x11.CapabilitiesBaseType;
import net.opengis.ows.x11.DomainType;
import net.opengis.ows.x11.MimeType;
import net.opengis.ows.x11.OperationsMetadataDocument;
import net.opengis.ows.x11.RangeType;
import net.opengis.ows.x11.ValueType;
import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.OperationDocument.Operation;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.ContentsDocument;
import net.opengis.sos.x10.ObservationOfferingType;
import net.opengis.sos.x10.ResponseModeType;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;
import net.opengis.sos.x10.ContentsDocument.Contents;
import net.opengis.sos.x10.ContentsDocument.Contents.ObservationOfferingList;
import net.opengis.sos.x10.ObservationOfferingType.DomainFeature;
import net.opengis.sos.x10.ObservationOfferingType.DomainFeature.Procedure;
import net.opengis.swe.x101.PhenomenonPropertyType;
import net.opengis.swe.x101.TimeGeometricPrimitivePropertyType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlQName;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.ds.IGetCapabilitiesDAO;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * Abstract DAO of PostgreSQL DB for GetCapabilities Operation. This class is abstract and subclasses must
 * implement the method 'getFoi4Offering', because the different PostGIS versions for the different PGSQL
 * versions return the geometries of the FOIs in different ways. Beside this method the class offers all
 * necessary operations for the getCapabiltiesListener to access Data.
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLGetCapabilitiesDAO implements IGetCapabilitiesDAO {

    /** Connection pool for creating connections to the DB */
    protected PGConnectionPool cpool;

    /** the skeleton file for SOSCapabilities */
    protected File capabilitiesSkeleton;

    /** the skeleton file for SOSCapabilitiesMobile */
    protected File capabilitiesSkeletonMobile;

    /** cache for capabilities data */
    protected CapabilitiesCacheController capsCacheController;

    /** the logger, used to log exceptions and additonaly information */
    protected static Logger log = Logger.getLogger(PGSQLGetCapabilitiesDAO.class);

    // ///////////////////////////////////////////////////////////////////////////////
    // element names and namespaces for elements in substitution groups which
    // must be renamed
    protected final String NS_GML = OMConstants.NS_GML;

    protected final String EN_ABSTRACT_TIME = OMConstants.EN_ABSTRACT_TIME_GEOM_PRIM;

    protected final String EN_TIME_PERIOD = OMConstants.EN_TIME_PERIOD;

    /**
     * constructor, sets up and loads the skeleton capabilities file
     * 
     * @param cpool
     *        the connection pool containing the connections to the DB
     * @throws OwsExceptionReport
     *         if binding of the capabilities skeleton file with XMLBeans classes failed
     */
    public PGSQLGetCapabilitiesDAO(PGConnectionPool cpool) throws OwsExceptionReport {
        SosConfigurator configurator = SosConfigurator.getInstance();
        setCapabilitiesSkeleton(configurator.getCapabilitiesSkeleton());
        if (configurator.isMobileEnabled()) {
            setCapabilitiesSkeletonMobile(configurator.getCapabilitiesSkeletonMobile());
        }
        this.capsCacheController = SosConfigurator.getInstance().getCapsCacheController();
        this.cpool = cpool;
    }

    /**
     * 
     * @return Returns the GetCapabilities response with all sections
     * 
     * @throws OwsExceptionReport
     *         if query of min and max date for observations failed
     */
    public CapabilitiesDocument getCompleteCapabilities() throws OwsExceptionReport {

        CapabilitiesDocument xb_capsDoc = this.loadCapabilitiesSkeleton();
        CapabilitiesBaseType xb_capBaseType = xb_capsDoc.getCapabilities();
        CapabilitiesDocument xb_capsd = CapabilitiesDocument.Factory.newInstance();
        Capabilities xb_caps = xb_capsd.addNewCapabilities();
        xb_caps.setVersion(xb_capBaseType.getVersion());

        // TODO see getUpdateSequence(CapabilitiesDocument)
        // // sets the UpdateSequenceParameter with actual time
        // String updateSequenceString =
        // SosDateTimeUtilities.formatDateTime2ResponseString(getUpdateSequence(xb_capsDoc));
        // xb_caps.setUpdateSequence(updateSequenceString);

        xb_caps.setServiceIdentification(xb_capBaseType.getServiceIdentification());
        xb_caps.setServiceProvider(xb_capBaseType.getServiceProvider());
        xb_caps.setOperationsMetadata(this.getOperationsMetadata(xb_capsDoc));
        xb_caps.setFilterCapabilities(xb_capsDoc.getCapabilities().getFilterCapabilities());
        Contents xb_contents = xb_caps.addNewContents();
        xb_contents.set(getContents());
        return xb_capsd;
    }

    /**
     * 
     * @return Returns the GetCapabilities response with all sections for mobile requests
     * 
     * @throws OwsExceptionReport
     *         if query of min and max date for observations failed
     */
    public CapabilitiesDocument getCompleteCapabilitiesMobile() throws OwsExceptionReport {

        CapabilitiesDocument xb_capsDoc = this.loadCapabilitiesSkeletonMobile();
        CapabilitiesBaseType xb_capBaseType = xb_capsDoc.getCapabilities();
        CapabilitiesDocument xb_capsd = CapabilitiesDocument.Factory.newInstance();
        Capabilities xb_caps = xb_capsd.addNewCapabilities();
        xb_caps.setVersion(xb_capBaseType.getVersion());

        // TODO see getUpdateSequence(CapabilitiesDocument)
        // // sets the UpdateSequenceParameter with actual time
        // String updateSequenceString =
        // SosDateTimeUtilities.formatDateTime2ResponseString(getUpdateSequence(xb_capsDoc));
        // xb_caps.setUpdateSequence(updateSequenceString);

        xb_caps.setServiceIdentification(xb_capBaseType.getServiceIdentification());
        xb_caps.setServiceProvider(xb_capBaseType.getServiceProvider());
        xb_caps.setOperationsMetadata(this.getOperationsMetadataMobile(xb_capsDoc));
        xb_caps.setFilterCapabilities(xb_capsDoc.getCapabilities().getFilterCapabilities());
        Contents xb_contents = xb_caps.addNewContents();
        xb_contents.set(getContentsMobile());
        return xb_capsd;
    }

    /**
     * @return Returns the capabilitiesSkeleton.
     */
    public File getCapabilitiesSkeleton() {
        return capabilitiesSkeleton;
    }

    /**
     * @return Returns the capabilitiesSkeleton mobile.
     */
    public File getCapabilitiesSkeletonMobile() {
        return capabilitiesSkeletonMobile;
    }

    /**
     * @param capabilitiesSkeleton
     *        The capabilitiesSkeleton to set.
     */
    public void setCapabilitiesSkeleton(File capabilitiesSkeleton) {
        this.capabilitiesSkeleton = capabilitiesSkeleton;
    }

    /**
     * @param capabilitiesSkeletonMobile
     *        The capabilitiesSkeletonMobile to set.
     */
    public void setCapabilitiesSkeletonMobile(File capabilitiesSkeletonMobile) {
        this.capabilitiesSkeletonMobile = capabilitiesSkeletonMobile;
    }

    /**
     * (re-)loads the capabilities skeleton file and sets the private capabilitiesSkeleton variable
     * 
     * @throws OwsExceptionReport
     *         if binding of the skeletonFile with XMLBeans classes failed
     */
    public CapabilitiesDocument loadCapabilitiesSkeleton() throws OwsExceptionReport {

        CapabilitiesDocument xb_capsDoc = null;
        try {
            InputStream fis = new FileInputStream(capabilitiesSkeleton);
            xb_capsDoc = CapabilitiesDocument.Factory.parse(fis);
            fis.close();
        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while loading the capabilities skeleton file!", e);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while loading the capabilities skeleton file! Please contact the SOS admin! Following error occurred: "
                                         + e.getMessage());
            throw se;
        }

        return xb_capsDoc;
    }

    /**
     * (re-)loads the capabilities skeleton mobile file and sets the private capabilitiesSkeleton variable
     * 
     * @throws OwsExceptionReport
     *         if binding of the skeletonFile with XMLBeans classes failed
     */
    public CapabilitiesDocument loadCapabilitiesSkeletonMobile() throws OwsExceptionReport {

        CapabilitiesDocument xb_capsDoc = null;
        try {
            InputStream fis = new FileInputStream(capabilitiesSkeletonMobile);
            xb_capsDoc = CapabilitiesDocument.Factory.parse(fis);
            fis.close();
        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while loading the capabilities skeleton mobile file!", e);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, e);
            throw se;
        }

        return xb_capsDoc;
    }

    /**
     * returns the min date of an observation
     * 
     * @return Returns Date containing the min date of the observation in ISO8601 format
     * @throws OwsExceptionReport
     *         if query of the min date failed
     */
    public DateTime getMinDate4Observations() throws OwsExceptionReport {

        return SosConfigurator.getInstance().getFactory().getConfigDAO().getMinDate4Observations();
    }

    /**
     * returns the fois for the requested offering
     * 
     * @param offering
     *        the offering for which the fois should be returned
     * @return Returns ArrayList containing the fois for the offering
     * @throws OwsExceptionReport
     *         if getting the fois failed
     */
    protected ArrayList<String> getFOI4offering(String offering) throws OwsExceptionReport {

        ArrayList<String> result = new ArrayList<String>();

        Connection con = null;
        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT DISTINCT " + PGDAOConstants.foiIDCn + "," + PGDAOConstants.foiNameCn
                + " , AsText(" + PGDAOConstants.geomCn + ") AS geom, SRID(" + PGDAOConstants.geomCn
                + ")" + " FROM " + PGDAOConstants.foiOffTn + " NATURAL INNER JOIN "
                + PGDAOConstants.foiTn);

        // WHERE part of query
        query.append(" WHERE ");

        query.append(PGDAOConstants.foiOffTn + "." + PGDAOConstants.offeringIDCn + " =  '"
                + offering + "';");

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            log.debug(">>>FOI QUERY: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no feature is available give back empty ArrayList
            if (rs == null) {
                return result;
            }
            // get result as string
            while (rs.next()) {

                result.add(rs.getString("feature_of_interest_id"));
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query max date of observation from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("Error while query max date of observation from database!", se);
            throw se;
        }

        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return result;
    }

    /**
     * returns the domain features for the requested offering
     * 
     * @param offering
     *        the offering for which the fois should be returned
     * @return Returns ArrayList containing the domain features for the offering
     * @throws OwsExceptionReport
     *         if getting the domain features failed
     */
    protected ArrayList<String> getDF4offering(String offering) throws OwsExceptionReport {

        ArrayList<String> result = new ArrayList<String>();

        Connection con = null;
        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT DISTINCT " + PGDAOConstants.domainFeatureIDCn + ","
                + PGDAOConstants.domainFeatureNameCn + " , AsText("
                + PGDAOConstants.domainFeatureGeomCn + ") AS geom, SRID("
                + PGDAOConstants.domainFeatureGeomCn + ")" + " FROM " + PGDAOConstants.dfOffTn
                + " NATURAL INNER JOIN " + PGDAOConstants.dfTn);

        // WHERE part of query
        query.append(" WHERE ");

        query.append(PGDAOConstants.dfOffTn + "." + PGDAOConstants.offeringIDCn + " =  '"
                + offering + "';");

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            log.debug(">>>DF QUERY: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no feature is available give back empty ArrayList
            if (rs == null) {
                return result;
            }
            // get result as string
            while (rs.next()) {

                result.add(rs.getString("domain_feature_id"));
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query domain features for the requested offering from database!",
                      sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("Error while query domain features for the requested offering from database!",
                      se);
            throw se;
        }

        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return result;
    }

    /**
     * returns the max date of all observation
     * 
     * @return Returns Date containing the max date of the observations in ISO8601 format
     * @throws OwsExceptionReport
     *         if query of the max date failed
     */
    public DateTime getMaxDate4Observations() throws OwsExceptionReport {

        return SosConfigurator.getInstance().getFactory().getConfigDAO().getMaxDate4Observations();

    }

    /**
     * builds the contents section of the capabilities response. The data values for this section are all read
     * in from configfile or database
     * 
     * @return Returns contents xml bean representing the contents section of the capabilities response
     *         document
     * @throws OwsExceptionReport
     *         if building the contents section failed
     */
    public Contents getContents() throws OwsExceptionReport {

        ContentsDocument xb_contentDoc = ContentsDocument.Factory.newInstance();
        Contents xb_result = xb_contentDoc.addNewContents();
        ObservationOfferingList xb_ooList = xb_result.addNewObservationOfferingList();

        List<String> offerings = this.capsCacheController.getOfferings();

        for (String offering : offerings) {

            ObservationOfferingType xb_oo = xb_ooList.addNewObservationOffering();
            xb_oo.setId(offering);

            // query features of interest and insert them
            ArrayList<String> fois = getFOI4offering(offering);

            // set bounded by element
            BoundingShapeType xb_boundedBy = xb_oo.addNewBoundedBy();
            xb_boundedBy.addNewEnvelope();

            // only if fois are contained for the offering set the values of the envelope
            xb_boundedBy.setEnvelope(getBBOX4Offering(offering));

            // TODO: add intended application
            // xb_oo.addIntendedApplication("");

            // add offering name
            CodeType xb_name = xb_oo.addNewName();
            xb_name.setStringValue(this.capsCacheController.getOfferingName(offering));

            // set up phenomena
            List<String> phenomenons = this.capsCacheController.getPhenomenons4Offering(offering);
            List<String> compositePhenomena = this.capsCacheController.getOffCompPhens().get(offering);
            List<String> componentsOfCompPhens = new ArrayList<String>();

            // set up composite phenomena
            if (compositePhenomena != null) {
                // first add a new compositePhenomenon for every compositePhenomenon
                for (String compositePhenomenon : compositePhenomena) {
                    List<String> components = this.capsCacheController.getPhens4CompPhens().get(compositePhenomenon);
                    componentsOfCompPhens.addAll(components);
                    if (components != null) {
                        PhenomenonPropertyType xb_opType = xb_oo.addNewObservedProperty();
                        xb_opType.set(SosConfigurator.getInstance().getOmEncoder().createCompositePhenomenon(compositePhenomenon,
                                                                                                             components));
                    }
                }
            }

            if (phenomenons != null) {
                for (String phenomenon : phenomenons) {
                    if ( !componentsOfCompPhens.contains(phenomenon)) {
                        PhenomenonPropertyType xb_ootype = xb_oo.addNewObservedProperty();
                        xb_ootype.setHref(phenomenon);
                    }
                }
            }

            // set up time
            TimeGeometricPrimitivePropertyType xb_time = xb_oo.addNewTime();

            DateTime minDate = getMinDate4Offering(offering);
            DateTime maxDate = getMaxDate4Offering(offering);
            if (minDate != null && maxDate != null) {
                AbstractTimeGeometricPrimitiveType xb_gp = xb_time.addNewTimeGeometricPrimitive();
                TimePeriodType xb_timePeriod = (TimePeriodType) SosConfigurator.getInstance().getGmlEncoder().createTime(new TimePeriod(minDate,
                                                                                                                                        maxDate));
                xb_gp.set(xb_timePeriod);
            }

            // rename nodename of geometric primitive to gml:timePeriod
            XmlCursor timeCursor = xb_time.newCursor();
            boolean hasTimePrimitive = timeCursor.toChild(new QName(NS_GML, EN_ABSTRACT_TIME));
            if (hasTimePrimitive) {
                timeCursor.setName(new QName(NS_GML, EN_TIME_PERIOD));
            }

            // add feature of interests
            for (String foi : fois) {
                ReferenceType xb_foiRefType = xb_oo.addNewFeatureOfInterest();
                xb_foiRefType.setHref(foi);
            }

            // insert result models
            List<QName> resultModels = this.capsCacheController.getResultModels4Offering(offering);

            if (resultModels == null || resultModels.isEmpty()) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     null,
                                     "No result models are contained in the database for the offering: "
                                             + offering + "! Please contact the admin of this SOS.");
                throw se;
            }

            for (QName rmString : resultModels) {
                XmlQName xb_resultModel = xb_oo.addNewResultModel();
                

                // don't know why, but it works
                // xb_resultModel.setStringValue(rmString.getPrefix() + ":" + rmString.getLocalPart());
                // xb_resultModel.set(SosConstants.RESULT_MODEL_MEASUREMENT);
                // xb_resultModel.setStringValue("om:Measurement");
                // QName qName = new QName(rmString.getPrefix(), rmString.getLocalPart());
                xb_resultModel.setQNameValue(rmString);
            }

            // set response format
            MimeType xb_respFormat = xb_oo.addNewResponseFormat();
            xb_respFormat.setStringValue(SosConstants.CONTENT_TYPE_OM);
            xb_respFormat = xb_oo.addNewResponseFormat();
            xb_respFormat.setStringValue(SosConstants.CONTENT_TYPE_ZIP);

            // set response Mode
            ResponseModeType xb_respMode = xb_oo.addNewResponseMode();
            xb_respMode.setStringValue(SosConstants.RESPONSE_MODE_INLINE);
            xb_respMode = xb_oo.addNewResponseMode();
            xb_respMode.setStringValue(SosConstants.RESPONSE_RESULT_TEMPLATE);

            // set procedures
            List<String> procedures = this.capsCacheController.getProcedures4Offering(offering);

            if (procedures == null || procedures.isEmpty()) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     null,
                                     "No procedures are contained in the database for the offering: "
                                             + offering + "! Please contact the admin of this SOS.");
                throw se;
            }

            for (String p : procedures) {
                ReferenceType xb_procedureProperty = xb_oo.addNewProcedure();
                xb_procedureProperty.setHref(p);
            }
        }
        
        return xb_result;
    }

    /**
     * builds the contents section of the capabilities response. The data values for this section are all read
     * in from configfile or database
     * 
     * @return Returns contents xml bean representing the contents section of the capabilities response
     *         document
     * @throws OwsExceptionReport
     *         if building the contents section failed
     */
    public Contents getContentsMobile() throws OwsExceptionReport {

        ContentsDocument xb_contentDoc = ContentsDocument.Factory.newInstance();
        Contents xb_result = xb_contentDoc.addNewContents();
        ObservationOfferingList xb_ooList = xb_result.addNewObservationOfferingList();

        List<String> offerings = this.capsCacheController.getOfferings();

        for (String offering : offerings) {

            ObservationOfferingType xb_oo = xb_ooList.addNewObservationOffering();
            xb_oo.setId(offering);

            // query features of interest and insert them
            ArrayList<String> domainfeatures = getDF4offering(offering);

            // set bounded by element
            BoundingShapeType xb_boundedBy = xb_oo.addNewBoundedBy();
            xb_boundedBy.addNewEnvelope();

            // only if fois are contained for the offering set the values of the envelope
            xb_boundedBy.setEnvelope(getBBOX4Offering(offering));

            // TODO: add intended application
            // xb_oo.addIntendedApplication("");

            // add offering name
            CodeType xb_name = xb_oo.addNewName();
            xb_name.setStringValue(this.capsCacheController.getOfferingName(offering));

            // set up phenomena
            List<String> phenomenons = this.capsCacheController.getPhenomenons4Offering(offering);
            List<String> compositePhenomena = this.capsCacheController.getOffCompPhens().get(offering);
            List<String> componentsOfCompPhens = new ArrayList<String>();

            // set up composite phenomena
            if (compositePhenomena != null) {
                // first add a new compositePhenomenon for every compositePhenomenon
                for (String compositePhenomenon : compositePhenomena) {
                    List<String> components = this.capsCacheController.getPhens4CompPhens().get(compositePhenomenon);
                    componentsOfCompPhens.addAll(components);
                    if (components != null) {
                        PhenomenonPropertyType xb_opType = xb_oo.addNewObservedProperty();
                        xb_opType.set(SosConfigurator.getInstance().getOmEncoder().createCompositePhenomenon(compositePhenomenon,
                                                                                                             components));
                    }
                }
            }

            if (phenomenons != null) {
                for (String phenomenon : phenomenons) {
                    if ( !componentsOfCompPhens.contains(phenomenon)) {
                        PhenomenonPropertyType xb_ootype = xb_oo.addNewObservedProperty();
                        xb_ootype.setHref(phenomenon);
                    }
                }
            }

            // set up time
            TimeGeometricPrimitivePropertyType xb_time = xb_oo.addNewTime();

            DateTime minDate = getMinDate4Offering(offering);
            DateTime maxDate = getMaxDate4Offering(offering);
            if (minDate != null && maxDate != null) {
                AbstractTimeGeometricPrimitiveType xb_gp = xb_time.addNewTimeGeometricPrimitive();
                TimePeriodType xb_timePeriod = (TimePeriodType) SosConfigurator.getInstance().getGmlEncoder().createTime(new TimePeriod(minDate,
                                                                                                                                        maxDate));
                xb_gp.set(xb_timePeriod);
            }

            // rename nodename of geometric primitive to gml:timePeriod
            XmlCursor timeCursor = xb_time.newCursor();
            boolean hasTimePrimitive = timeCursor.toChild(new QName(NS_GML, EN_ABSTRACT_TIME));
            if (hasTimePrimitive) {
                timeCursor.setName(new QName(NS_GML, EN_TIME_PERIOD));
            }

            // add domain features and nested procedures
            for (String df : domainfeatures) {
                DomainFeature xb_dfRefType = xb_oo.addNewDomainFeature();
                xb_dfRefType.setHref(df);

                List<String> procedures = capsCacheController.getProcs4DomainFeature(df);
                Iterator<String> procIter = procedures.iterator();
                while (procIter.hasNext()) {
                    Procedure xb_proc = xb_dfRefType.addNewProcedure();
                    String procID = procIter.next();
                    xb_proc.setHref(procID);
                    xb_proc.setIsActive(capsCacheController.getStatus4Proc(procID));
                    xb_proc.setIsMobile(capsCacheController.getMobility4Proc(procID));
                }
            }

            // insert result models
            List<QName> resultModels = this.capsCacheController.getResultModels4Offering(offering);

            if (resultModels == null || resultModels.isEmpty()) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     null,
                                     "No result models are contained in the database for the offering: "
                                             + offering + "! Please contact the admin of this SOS.");
                throw se;
            }

            for (QName rmString : resultModels) {
                XmlQName xb_resultModel = xb_oo.addNewResultModel();

                // xb_resultModel.setStringValue(rmString.getPrefix() + ":" + rmString.getLocalPart());
                // xb_resultModel.set(SosConstants.RESULT_MODEL_MEASUREMENT);
                // xb_resultModel.setStringValue("om:Measurement");
                // QName qName = new QName(rmString.getPrefix(), rmString.getLocalPart());
                xb_resultModel.setQNameValue(rmString);
                // TODO: Change if XmlBeans-Bug is fixed
                XmlCursor cursor = xb_resultModel.newCursor();
                String value = cursor.getTextValue();
                cursor.setTextValue(value.replaceFirst("ns", OMConstants.NS_OM_PREFIX));
            }

            // set response format
            MimeType xb_respFormat = xb_oo.addNewResponseFormat();
            xb_respFormat.setStringValue(SosConstants.CONTENT_TYPE_OM);
            xb_respFormat = xb_oo.addNewResponseFormat();
            xb_respFormat.setStringValue(SosConstants.CONTENT_TYPE_ZIP);

            // set response Mode
            ResponseModeType xb_respMode = xb_oo.addNewResponseMode();
            xb_respMode.setStringValue(SosConstants.RESPONSE_MODE_INLINE);
            xb_respMode = xb_oo.addNewResponseMode();
            xb_respMode.setStringValue(SosConstants.RESPONSE_RESULT_TEMPLATE);

        }

        return xb_result;
    }

    /**
     * method returns the operationsMetadata section and queries therefore the min and max time to fill the
     * eventTime element of the getObservation operations metadata
     * 
     * @param capsDoc
     *        the CapabilitiesDocument bean resulting from parsing the capabilities_skeleton file
     * @return OperationsMetadata bean representing the operations metadata section of the capabilities
     *         response document
     * @throws OwsExceptionReport
     *         if the query of the min and max time for all observations failed
     */
    public OperationsMetadata getOperationsMetadata(CapabilitiesDocument capsDoc) throws OwsExceptionReport {

        OperationsMetadataDocument xb_metaDoc = OperationsMetadataDocument.Factory.newInstance();
        OperationsMetadata xb_meta = xb_metaDoc.addNewOperationsMetadata();
        xb_meta.set(capsDoc.getCapabilities().getOperationsMetadata());

        List<String> offerings = this.capsCacheController.getOfferings();
        List<String> phenomenons = this.capsCacheController.getAllPhenomenons();
        Map<String, SensorSystem> procedures = this.capsCacheController.getProcedures();
        List<String> fois = this.capsCacheController.getFois();

        // List<String> obsIds = this.capsCache.getObservationIds();

        // get Operations and then get the getObservation's metadata element
        Operation[] operations = xb_meta.getOperationArray();
        for (int i = 0; i < operations.length; i++) {
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getObservation.name())) {

                // get the parameters of the getObservation operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get eventTime parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.eventTime.name())) {

                        // set the min value of the eventTime
                        RangeType range = RangeType.Factory.newInstance();
                        ValueType minValue = ValueType.Factory.newInstance();
                        DateTime minDate = getMinDate4Observations();
                        DateTime maxDate = getMaxDate4Observations();
                        if (minDate != null && maxDate != null) {
                            String minDateString = SosDateTimeUtilities.formatDateTime2ResponseString(minDate);
                            minValue.setStringValue(minDateString);
                            range.addNewMinimumValue();
                            range.setMinimumValue(minValue);

                            // set the max value of the eventTime
                            ValueType maxValue = ValueType.Factory.newInstance();
                            String maxDateString = SosDateTimeUtilities.formatDateTime2ResponseString(maxDate);
                            maxValue.setStringValue(maxDateString);
                            range.addNewMaximumValue();
                            range.setMaximumValue(maxValue);

                            // set the range for eventTime element
                            RangeType[] ranges = {range};

                            AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                            xb_allowedValuesDoc.addNewAllowedValues().setRangeArray(ranges);

                            params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                        }

                    }

                    // get offerings parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.offering.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String offering : offerings) {
                            xb_allowedValues.addNewValue().setStringValue(offering);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get phenomenons parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.observedProperty.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String phenomenon : phenomenons) {
                            xb_allowedValues.addNewValue().setStringValue(phenomenon);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get procedure parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.procedure.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();

                        Iterator<String> procs = procedures.keySet().iterator();
                        for (int pi = 0; pi < procedures.size(); pi++) {
                            String procedure_id = (String) procs.next();
                            xb_allowedValues.addNewValue().setStringValue(procedure_id);
                        }

                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get response mode parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.responseMode.name())) {

                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        xb_allowedValues.addNewValue().setStringValue(SosConstants.RESPONSE_RESULT_TEMPLATE);
                        xb_allowedValues.addNewValue().setStringValue(SosConstants.RESPONSE_MODE_INLINE);
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }
                }
            }

            // get observation by id
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getObservationById.name())) {

                // get the parameters of the getObservation operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get observation id parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationByIdParams.ObservationId.name())) {
                        params[j].addNewAnyValue();
                    }
                }

            }

            // describe sensor operation
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.describeSensor.name())) {

                // get the parameters of the describeSensor operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.DescribeSensorParams.procedure.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();

                        Iterator<String> procs = procedures.keySet().iterator();
                        for (int pi = 0; pi < procedures.size(); pi++) {
                            String procedure_id = (String) procs.next();
                            xb_allowedValues.addNewValue().setStringValue(procedure_id);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }
                }
            }

            // getFeatureOfInterest operation
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getFeatureOfInterest.name())) {

                // get the parameters of the getFeatureOfInterest operation
                DomainType[] params = operations[i].getParameterArray();

                for (int j = 0; j < params.length; j++) {

                    // get parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetFeatureOfInterestParams.featureOfInterestID.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String foi : fois) {
                            xb_allowedValues.addNewValue().setStringValue(foi);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }
                }
            }
        }
        return xb_metaDoc.getOperationsMetadata();
    }

    /**
     * method returns the operationsMetadata section and queries therefore the min and max time to fill the
     * eventTime element of the getObservation operations metadata
     * 
     * @param capsDoc
     *        the CapabilitiesDocument bean resulting from parsing the capabilities_skeleton file
     * @return OperationsMetadata bean representing the operations metadata section of the capabilities
     *         response document
     * @throws OwsExceptionReport
     *         if the query of the min and max time for all observations failed
     */
    public OperationsMetadata getOperationsMetadataMobile(CapabilitiesDocument capsDoc) throws OwsExceptionReport {

        OperationsMetadataDocument xb_metaDoc = OperationsMetadataDocument.Factory.newInstance();
        OperationsMetadata xb_meta = xb_metaDoc.addNewOperationsMetadata();
        xb_meta.set(capsDoc.getCapabilities().getOperationsMetadata());

        List<String> offerings = this.capsCacheController.getOfferings();
        List<String> phenomenons = this.capsCacheController.getAllPhenomenons();
        Map<String, SensorSystem> procedures = this.capsCacheController.getProcedures();
        List<String> domainFeatures = this.capsCacheController.getDomainFeatures();
        // Not needed: List<String> fois = this.capsCache.getFois();

        // Not needed: List<String> obsIds = this.capsCache.getObservationIds();

        // get Operations and then get the getObservation's metadata element
        Operation[] operations = xb_meta.getOperationArray();
        for (int i = 0; i < operations.length; i++) {
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getObservation.name())) {

                // get the parameters of the getObservation operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get eventTime parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.eventTime.name())) {

                        // set the min value of the eventTime
                        RangeType range = RangeType.Factory.newInstance();
                        ValueType minValue = ValueType.Factory.newInstance();
                        DateTime minDate = getMinDate4Observations();
                        DateTime maxDate = getMaxDate4Observations();
                        if (minDate != null && maxDate != null) {
                            String minDateString = SosDateTimeUtilities.formatDateTime2ResponseString(minDate);
                            minValue.setStringValue(minDateString);
                            range.addNewMinimumValue();
                            range.setMinimumValue(minValue);

                            // set the max value of the eventTime
                            ValueType maxValue = ValueType.Factory.newInstance();
                            String maxDateString = SosDateTimeUtilities.formatDateTime2ResponseString(maxDate);
                            maxValue.setStringValue(maxDateString);
                            range.addNewMaximumValue();
                            range.setMaximumValue(maxValue);

                            // set the range for eventTime element
                            RangeType[] ranges = {range};

                            AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                            xb_allowedValuesDoc.addNewAllowedValues().setRangeArray(ranges);

                            params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                        }

                    }

                    // get offerings parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.offering.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String offering : offerings) {
                            xb_allowedValues.addNewValue().setStringValue(offering);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get phenomenons parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.observedProperty.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String phenomenon : phenomenons) {
                            xb_allowedValues.addNewValue().setStringValue(phenomenon);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get procedure parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.procedure.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();

                        Iterator<String> procs = procedures.keySet().iterator();
                        for (int pi = 0; pi < procedures.size(); pi++) {
                            String procedure_id = (String) procs.next();
                            xb_allowedValues.addNewValue().setStringValue(procedure_id);
                        }

                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get domain feature parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.domainFeature.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        for (String df : domainFeatures) {
                            xb_allowedValues.addNewValue().setStringValue(df);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }

                    // get response mode parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationParams.responseMode.name())) {

                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
                        xb_allowedValues.addNewValue().setStringValue(SosConstants.RESPONSE_RESULT_TEMPLATE);
                        xb_allowedValues.addNewValue().setStringValue(SosConstants.RESPONSE_MODE_INLINE);
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }
                }
            }

            // get observation by id
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getObservationById.name())) {

                // get the parameters of the getObservation operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get observation id parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.GetObservationByIdParams.ObservationId.name())) {
                        params[j].addNewAnyValue();
                    }
                }

            }

            // describe sensor operation
            if (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.describeSensor.name())) {

                // get the parameters of the describeSensor operation
                DomainType[] params = operations[i].getParameterArray();
                for (int j = 0; j < params.length; j++) {

                    // get parameter
                    if (params[j].getName().equalsIgnoreCase(SosConstants.DescribeSensorParams.procedure.name())) {
                        AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
                        AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();

                        Iterator<String> procs = procedures.keySet().iterator();
                        for (int pi = 0; pi < procedures.size(); pi++) {
                            String procedure_id = (String) procs.next();
                            xb_allowedValues.addNewValue().setStringValue(procedure_id);
                        }
                        params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
                    }
                }
            }

            // getFeatureOfInterest operation (not needed for SOS Mobile!!)
            // if
            // (operations[i].getName().equalsIgnoreCase(SosConstants.Operations.getFeatureOfInterest.name()))
            // {
            //
            // // get the parameters of the getFeatureOfInterest operation
            // DomainType[] params = operations[i].getParameterArray();
            //
            // for (int j = 0; j < params.length; j++) {
            //
            // // get parameter
            // if
            // (params[j].getName().equalsIgnoreCase(SosConstants.GetFeatureOfInterestParams.featureOfInterestID.name()))
            // {
            // AllowedValuesDocument xb_allowedValuesDoc = AllowedValuesDocument.Factory.newInstance();
            // AllowedValues xb_allowedValues = xb_allowedValuesDoc.addNewAllowedValues();
            // for (String foi : fois) {
            // xb_allowedValues.addNewValue().setStringValue(foi);
            // }
            // params[j].setAllowedValues(xb_allowedValuesDoc.getAllowedValues());
            // }
            // }
            // }
        }
        return xb_metaDoc.getOperationsMetadata();
    }

    /**
     * returns the updateSequence value as Date; if no value is set in the skeleton File the updateSequence
     * value is set to the actual date and saved in the capabilities skeleton file
     * 
     * @return Returns Date value of the updateSequence of this capabilities
     * @throws OwsExceptionReport
     *         if parsing the updateSequence from capabilities skeletonFile failed
     */
    public DateTime getUpdateSequence(CapabilitiesDocument capsDoc) throws OwsExceptionReport {

        // TODO: database timestamp which is updated during insert or delete queries.
        // e.g. info table with one field 'updateSequence' as timestamp.
        // trigger on relevant tables which updates the field when insert or delete.
        DateTime result = null;

        try {
            String updateSequence = capsDoc.getCapabilities().getUpdateSequence();

            if (updateSequence == null || updateSequence.equalsIgnoreCase("")) {
                result = new DateTime();
                updateSequence = SosDateTimeUtilities.formatDateTime2ResponseString(result);
                capsDoc.getCapabilities().setUpdateSequence(updateSequence);
                capsDoc.save(capabilitiesSkeleton);
            }
            result = SosDateTimeUtilities.parseIsoString2DateTime(updateSequence);
        }
        catch (IOException ioe) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while getting the updateSequence of the capabilities!", ioe);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, ioe);
            throw se;
        }

        // return result;
        return null;
    }

    /**
     * returns the min date for the observations which belong to the requested offering
     * 
     * @param offering
     *        the offering for which observations the min date should be returned
     * @return Returns Date containing the min date of the observations for the requested offering in GML
     *         format
     * @throws OwsExceptionReport
     *         if querying or parsing the min Date failed
     * 
     */
    public DateTime getMinDate4Offering(String offering) throws OwsExceptionReport {

        /*
         * //get minMaxDate String[] from capabilitiesCache String[] minMaxDate =
         * this.capsCache.getTimes4Offerings().get(offering); String minDate = minMaxDate[0]; if
         * (minDate.equals("null")||minDate.equals("")) { return ""; } else { minDate =minDate.replace(" ",
         * "T"); return minDate; }
         */

        DateTime result = null;
        Connection con = null;

        try {
            // get connection
            con = cpool.getConnection();

            // query max time
            Statement stmt = con.createStatement();

            List<String> phenomenons = this.capsCacheController.getAllPhenomenons4Offering(offering);

            // if no phenomena are contained for the offering, return empty
            // stringt
            if (phenomenons.size() == 0 || phenomenons.isEmpty()) {
                return result;
            }

            StringBuffer query = new StringBuffer();
            query.append("SELECT iso_timestamp(" + PGDAOConstants.minTimeCn + ") AS "
                    + PGDAOConstants.minTimeCn + " FROM " + PGDAOConstants.offTn + " WHERE ");
            query.append(PGDAOConstants.offeringIDCn + " = '" + offering + "';");

            ResultSet rs = stmt.executeQuery(query.toString());

            // if no minDate is available give back empty String
            if (rs == null) {
                return null;
            }

            // get result as string and put into ISO8601 format (replace blank
            // through 'T')
            while (rs.next()) {

                String minTime = rs.getString(PGDAOConstants.minTimeCn);

                if (minTime == null) {
                    return result;
                }
                result = SosDateTimeUtilities.parseIsoString2DateTime(minTime);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            if (sqle.getMessage().contains("function iso_timestamp(timestamp with time zone)")) {
                log.error("Function iso_timestamp(timestamp with time zone)does not exist! "
                        + "Run the update_iso_timestamp_function_postgres82.sql or "
                        + "update_iso_timestamp_function_postgres83.sql for the SOS database. "
                        + "Depending on your PostgreSQL version.", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle.getMessage()
                        + ". Run the update_iso_timestamp_function_postgres82.sql or "
                        + "update_iso_timestamp_function_postgres83.sql for the SOS database. "
                        + "Depending on your PostgreSQL version.");
            }
            else {
                log.error("Error while query min date of observation from database!", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            }
            throw se;
        }
        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return result;
    }

    /**
     * returns the max date for the observations which belong to the requested offering
     * 
     * @param offering
     *        the offering for which observations the max date should be returned
     * @return Returns Date containing the max date of the observations for the requested offering in GML
     *         format
     * @throws OwsExceptionReport
     *         if querying of parsing the max date failed
     * 
     */
    public DateTime getMaxDate4Offering(String offering) throws OwsExceptionReport {

        /*
         * //get minMaxDate String[] from capabilitiesCache String[] minMaxDate =
         * this.capsCache.getTimes4Offerings().get(offering);
         * 
         * //extract maxDate String maxDate = minMaxDate[1]; if (maxDate.equals("null")||maxDate.equals("")) {
         * return ""; } else { maxDate =maxDate.replace(" ", "T"); return maxDate; }
         */

        DateTime result = null;
        Connection con = null;

        try {

            // get connection
            con = cpool.getConnection();

            // query max time
            Statement stmt = con.createStatement();

            List<String> phenomenons = this.capsCacheController.getAllPhenomenons4Offering(offering);

            // if no phenomena are contained for the requested offering return
            // empty string
            if (phenomenons.size() == 0 || phenomenons.isEmpty()) {
                return result;
            }

            StringBuffer query = new StringBuffer();
            query.append("SELECT iso_timestamp(" + PGDAOConstants.maxTimeCn + ") AS "
                    + PGDAOConstants.maxTimeCn + " FROM " + PGDAOConstants.offTn + " WHERE ");
            query.append(PGDAOConstants.offeringIDCn + " = '" + offering + "';");
            log.debug(">>>MAX DATE QUERY: " + query.toString());
            ResultSet rs = stmt.executeQuery(query.toString());

            // if no maxDate is available give back empty String
            if (rs == null) {
                return null;
            }
            // get result as string and parse String to date
            while (rs.next()) {

                String maxTime = rs.getString(PGDAOConstants.maxTimeCn);

                if (maxTime == null) {
                    return result;
                }
                result = SosDateTimeUtilities.parseIsoString2DateTime(maxTime);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            if (sqle.getMessage().contains("function iso_timestamp(timestamp with time zone)")) {
                log.error("Function iso_timestamp(timestamp with time zone)does not exist! "
                        + "Run the update_iso_timestamp_function_postgres82.sql or "
                        + "update_iso_timestamp_function_postgres83.sql for the SOS database. "
                        + "Depending on your PostgreSQL version.", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle.getMessage()
                        + ". Run the update_iso_timestamp_function_postgres82.sql or "
                        + "update_iso_timestamp_function_postgres83.sql for the SOS database. "
                        + "Depending on your PostgreSQL version.");
            }
            else {
                log.error("error while query max date of observation from database!", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            }

            throw se;
        }
        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return result;
    }

    /**
     * queries the bounding box of all requested feature of interest IDs
     * 
     * @param foiIDs
     *        ArrayList with String[]s containing the ids of the feature of interests for which the BBOX
     *        should be returned
     * @return Returns EnvelopeType XmlBean which represents the BBOX of the requested feature of interests
     * @throws OwsExceptionReport
     *         if query of the BBOX failed
     */
    public EnvelopeType getBBOX4Offering(String offeringID) throws OwsExceptionReport {

        String srsID = null;
        double minx = 0;
        double maxx = 0;
        double miny = 0;
        double maxy = 0;
        @SuppressWarnings("unused")
        String minz = null;
        @SuppressWarnings("unused")
        String maxz = null;

        String query = "SELECT EXTENT3D(" + PGDAOConstants.geomCn + ") AS bbox, SRID("
                + PGDAOConstants.geomCn + ") AS srid FROM " + PGDAOConstants.foiTn + " INNER JOIN "
                + PGDAOConstants.foiOffTn + " ON " + PGDAOConstants.foiTn + "."
                + PGDAOConstants.foiIDCn + " = " + PGDAOConstants.foiOffTn + "."
                + PGDAOConstants.foiIDCn + " WHERE " + PGDAOConstants.offeringIDCn + " = '"
                + offeringID + "' GROUP BY srid;";

        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            log.debug(">>>FOI QUERY: " + query);
            ResultSet rs = stmt.executeQuery(query);

            // if resultset is empty
            if (rs == null) {
                return EnvelopeType.Factory.newInstance();
            }
            // get result
            while (rs.next()) {
                String bbox = rs.getString("bbox");
                if (bbox != null) {
                    String[] minMax = bbox.split(",");
                    String minPositionString = minMax[0];
                    String maxPositionString = minMax[1];
                    minPositionString = minPositionString.replace("BOX3D(", "");
                    maxPositionString = maxPositionString.replace(")", "");
                    String[] minPos = minPositionString.split(" ");
                    minx = new Double(minPos[0]).doubleValue();
                    miny = new Double(minPos[1]).doubleValue();
                    String[] maxPos = maxPositionString.split(" ");
                    maxx = new Double(maxPos[0]).doubleValue();
                    maxy = new Double(maxPos[1]).doubleValue();
                    srsID = "" + rs.getInt("srid");
                }
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query max date of observation from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("Error while query max date of observation from database!", se);
            throw se;
        }

        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        EnvelopeType envelopeType = EnvelopeType.Factory.newInstance();

        // set lower corner
        // TODO for full 3D support add minz to parameter in setStringValue
        DirectPositionType lowerCorner = envelopeType.addNewLowerCorner();
        DirectPositionType upperCorner = envelopeType.addNewUpperCorner();

        if (srsID != null) {

            if (!SosConfigurator.getInstance().switchCoordinatesForEPSG(Integer.parseInt(srsID))) {
                lowerCorner.setStringValue(minx + " " + miny);
            }
            else {
                lowerCorner.setStringValue(miny + " " + minx);
            }

            // set upper corner
            // TODO for full 3D support add maxz to parameter in setStringValue
            if (maxx != 0 && maxy != 0) {
                if (!SosConfigurator.getInstance().switchCoordinatesForEPSG(Integer.parseInt(srsID))) {
                    upperCorner.setStringValue(maxx + " " + maxy);
                }
                else {
                    upperCorner.setStringValue(maxy + " " + maxx);
                }
            }

            // set SRS
            envelopeType.setSrsName(SosConfigurator.getInstance().getSrsNamePrefix() + srsID);
        }

        return envelopeType;
    }
    
}