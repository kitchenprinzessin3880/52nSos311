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

 Author: Christoph Stasch, Stephan Kuenster
 Created: <CREATION DATE>
 Modified: 08/11/2008
***************************************************************/

package org.n52.sos.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ds.IGetObservationByIdDAO;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.SosCategoryObservation;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.om.SosMeasurement;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosGetObservationByIdRequest;

/**
 * class offers access to the datasource, which serves the requested observation(s)
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLGetObservationByIdDAO implements IGetObservationByIdDAO {

    /** logger */
    private static final Logger log = Logger.getLogger(PGSQLGetObservationByIdDAO.class);

    /** connection pool which contains the connections to the DB */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param cpool
     *        PGConnectionPool which contains the connections to the DB
     */
    public PGSQLGetObservationByIdDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * fetches the requested observation(s) from the datasource; currently only ids for single observations
     * (measurements or categoryObservations) or observation collections are supported
     * 
     * @param request
     *        getObservationById request, which should be returnde observations for
     * @return Returns observation collection (also if single observations are returne d) //TODO maybe change
     *         then to single observations!
     * @throws OwsExceptionReport
     */
    public SosObservationCollection getObservationById(SosGetObservationByIdRequest request) throws OwsExceptionReport {
        SosObservationCollection obsCol = new SosObservationCollection();

        String observationID = request.getObservationID();

        AbstractSosObservation absObs = null;
        if (observationID.startsWith(SosConstants.OBS_ID_PREFIX)) {
            absObs = getSingleObservationById(request);
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationByIdParams.ObservationId.toString(),
                                 "The GetObservationById operation is only supported for single observations!!");
            log.warn(se.getMessage());
            throw se;
        }
        if (absObs == null) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationByIdParams.ObservationId.toString(),
                                 "No observation is contained in this SOS for your request!");
            log.warn(se.getMessage());
            throw se;
        }
        ArrayList<AbstractSosObservation> observationMembers = new ArrayList<AbstractSosObservation>();
        if (absObs != null) {
            observationMembers.add(absObs);
        }
        obsCol.setObservationMembers(observationMembers);
        return obsCol;
    }

    /**
     * queries a single observation from the database
     * 
     * @param request
     *        getObservationById request
     * @return Returns observationCollection containing the only element
     * @throws OwsExceptionReport
     *         if query of the single observation failed
     */
    public AbstractSosObservation getSingleObservationById(SosGetObservationByIdRequest request) throws OwsExceptionReport {

        AbstractSosObservation absObs = null;

        // build query
        String query = createQueryString(request);
        QName resultModel = request.getResultModel();

        String tokenSeperator = SosConfigurator.getInstance().getTokenSeperator();
        String tupleSeperator = SosConfigurator.getInstance().getTupleSeperator();
        String noDataValue = SosConfigurator.getInstance().getNoDataValue();

        Connection con = null;
        try {
            con = cpool.getConnection();
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()) {
                String obsID = resultSet.getString(PGDAOConstants.obsIDCn);
                String offeringID = resultSet.getString(PGDAOConstants.offeringIDCn);
                String mimeType = SosConstants.PARAMETER_NOT_SET;
                // create time element
                String timeString = resultSet.getString(PGDAOConstants.timestampCn);
                DateTime timeDate = SosDateTimeUtilities.parseIsoString2DateTime(timeString);
                TimeInstant time = new TimeInstant(timeDate, "");

                String phenID = resultSet.getString(PGDAOConstants.phenIDCn);
                String valueType = resultSet.getString(PGDAOConstants.valueTypeCn);
                String procID = resultSet.getString(PGDAOConstants.procIDCn);

                String foiID = resultSet.getString(PGDAOConstants.foiIDCn);
                String foiName = resultSet.getString(PGDAOConstants.foiNameCn);
                String foiType = resultSet.getString(PGDAOConstants.featureTypeCn);

                String geomWKT = resultSet.getString(PGDAOConstants.geomCn);
                int srid = resultSet.getInt("SRID");
//                Geometry jts_geometry = this.getObsDAO.createJTSGeom(geomWKT, srid);

                String unit = resultSet.getString(PGDAOConstants.unitCn);

                /*
                 * create feature representation
                 */
                
                //feature is sa:SamplingPoint
                SosAbstractFeature foi = ResultSetUtilities.getAbstractFeatureFromValues(foiID,
                                                                foiName,
                                                                SosConstants.PARAMETER_NOT_SET,
                                                                geomWKT,
                                                                srid,
                                                                foiType,
                                                                SosConstants.PARAMETER_NOT_SET);
                
                /*
                 * now create observation
                 */
                
                // if value is numeric, create either measurement or generic observation (depending on
                // resultModel parameter if set)
                if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.toString())) {

                    double value = resultSet.getDouble(PGDAOConstants.numericValueCn);

                    // resultModel parameter not set or measurement --> create measurement
                    if (resultModel == null
                    		|| resultModel.equals(SosConstants.RESULT_MODEL_MEASUREMENT)) {

                        absObs = new SosMeasurement(time,
                                                    obsID,
                                                    procID,
                                                    null,
                                                    phenID,
                                                    foi,
                                                    offeringID,
                                                    mimeType,
                                                    value,
                                                    unit,
                                                    null);

                    }

                    // resultModel parameter set and om:Observation --> create generic observation
                    else if (resultModel.equals(SosConstants.RESULT_MODEL_OBSERVATION)) {
                        ArrayList<String> phenComps = new ArrayList<String>();
                        phenComps.add(phenID);
                        SosGenericObservation genObs = new SosGenericObservation(phenComps,
                                                                                 procID,
                                                                                 offeringID,
                                                                                 tokenSeperator,
                                                                                 tupleSeperator,
                                                                                 noDataValue);
                        genObs.addValue(timeDate, foiID, phenID, "" + value /*change juergen*/,null/*change end*/);
                        // set obsID, then different createGenericObs in OMEncoder
//                        genObs.setObservationID(obsID);
                        absObs = genObs;
                    }

                    // result MOdel parameter set, but neither Measurement nor observation --> throw exception
                    else {
                        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                        se.addCodedException(ExceptionCode.InvalidParameterValue,
                                             SosConstants.GetObservationParams.resultModel.toString(),
                                             "The requested resultModel: " + resultModel
                                                     + " is not valid for the returned observtion!"
                                                     + " Valid resultModels are 'om:Measurement' and 'om:Observation' !");
                        throw se;
                    }
                }

                // do same as above for text values
                else {

                    String value = resultSet.getString(PGDAOConstants.textValueCn);

                    // resultModel is category observation or not set --> create categoryObservation
                    if (resultModel == null
                    		|| resultModel.equals(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)) {
                        SosCategoryObservation categoryObservation = new SosCategoryObservation(time,
                                                                                                obsID,
                                                                                                procID,
                                                                                                foi,
                                                                                                null,
                                                                                                phenID,
                                                                                                offeringID,
                                                                                                mimeType,
                                                                                                value,
                                                                                                unit,
                                                                                                null);
                        absObs = categoryObservation;
                    }
                    else if ( resultModel.equals(SosConstants.RESULT_MODEL_OBSERVATION)) {
                        ArrayList<String> phenComps = new ArrayList<String>();
                        phenComps.add(phenID);
                        SosGenericObservation genObs = new SosGenericObservation(phenComps,
                                                                                 procID,
                                                                                 offeringID,
                                                                                 tokenSeperator,
                                                                                 tupleSeperator,
                                                                                 noDataValue);
                        genObs.addValue(timeDate, foiID, phenID, value/*change juergen*/,null/*change end*/);
                        genObs.setObservationID(obsID);
                        absObs = genObs;
                    }
                    else {
                        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                        se.addCodedException(ExceptionCode.InvalidParameterValue,
                                             SosConstants.GetObservationParams.resultModel.toString(),
                                             "The requested resultModel: " + resultModel
                                                     + "is not valid for the returned observtion!"
                                                     + " Valid resultModels are 'om:CategoryObservation' and 'om:Observation' !");
                        throw se;
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while creating observations from database query result set: "
                                         + sqle.getMessage());
            log.error(se.getMessage());
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }

        return absObs;
    }

    /**
     * creates the queryString for getObservationById request for one single observation
     * 
     * @param request
     *        the request of the getObservationById operation
     * @return Returns String representing the query for the database to get the requested observation
     * @throws OwsExceptionReport
     *         if query failed
     */
    private String createQueryString(SosGetObservationByIdRequest request) throws OwsExceptionReport {
    	String srsName = request.getSrsName();
    	String obsId = request.getObservationID().replace(SosConstants.OBS_ID_PREFIX, "");
        StringBuffer query = new StringBuffer();
        query.append("SELECT iso_timestamp(" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + ") AS " + PGDAOConstants.timestampCn + ", "
                + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn + ", "
                + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn + ", " + PGDAOConstants.obsTn
                + "." + PGDAOConstants.numericValueCn + ", " + PGDAOConstants.obsTn + "."
                + PGDAOConstants.mimeTypeCn + ", " + PGDAOConstants.obsTn + "."
                + PGDAOConstants.offeringIDCn + ", " + PGDAOConstants.phenTn + "."
                + PGDAOConstants.phenIDCn + ", " + PGDAOConstants.phenTn + "."
                + PGDAOConstants.phenDescCn + ", " + PGDAOConstants.phenTn + "."
                + PGDAOConstants.unitCn + "," + PGDAOConstants.phenTn + "."
                + PGDAOConstants.valueTypeCn + "," + PGDAOConstants.obsTn + "."
                + PGDAOConstants.procIDCn + ", " + PGDAOConstants.foiTn + "."
                + PGDAOConstants.foiNameCn + ", " + PGDAOConstants.foiTn + "."
                + PGDAOConstants.foiIDCn + ", " + PGDAOConstants.foiTn + "."
                + PGDAOConstants.featureTypeCn + ", SRID(" + PGDAOConstants.foiTn + "."
                + PGDAOConstants.geomCn + "), ");

        // add geometry column to list, if srsName parameter is set, transform coordinates into request system
        if (srsName != null
                && ( !srsName.equals(SosConstants.PARAMETER_NOT_SET) && !srsName.equals(""))) {
            int srid = ResultSetUtilities.parseSrsName(srsName);
            query.append("AsText(TRANSFORM(" + PGDAOConstants.foiTn + "." + PGDAOConstants.geomCn
                    + "," + srid + ")) AS geom ");
        }
        else {
            query.append("AsText(" + PGDAOConstants.foiTn + "." + PGDAOConstants.geomCn
                    + ") AS geom ");
        }

        // natural join of tables
        query.append("FROM " + PGDAOConstants.phenTn + " NATURAL INNER JOIN "
                + PGDAOConstants.obsTn + " NATURAL INNER JOIN " + PGDAOConstants.foiTn + " WHERE ");

        query.append(PGDAOConstants.obsIDCn + " = '" + obsId + "'");
        return query.toString();
    }

}
