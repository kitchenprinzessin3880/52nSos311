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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.ds.IRegisterSensorDAO;
import org.n52.sos.ds.insert.IInsertDomainFeatureDAO;
import org.n52.sos.ds.insert.IInsertOfferingDAO;
import org.n52.sos.ds.insert.IInsertPhenomenonDAO;
import org.n52.sos.ds.insert.IInsertProcedureDAO;
import org.n52.sos.ds.insert.IInsertRelationshipsDAO;
import org.n52.sos.ds.insert.pgsql.PGSQLInsertDAOFactory;
import org.n52.sos.ogc.om.SosOffering;
import org.n52.sos.ogc.om.SosPhenomenon;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosRegisterSensorRequest;

/**
 * DAO for accessing standard SOS database for inserting sensor and corresponding offerings, domain features
 * and phenomena
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLRegisterSensorDAO implements IRegisterSensorDAO {

    /** logger */
    private static Logger log = Logger.getLogger(PGSQLRegisterSensorDAO.class);

    /** connection pool which contains the connections to the DB */
    private PGConnectionPool cpool;
    
    /** counter for creating sensorIDs */
    private static int sensorIDcounter = 1;

    /** factory for creating InsertDAOs */
    private PGSQLInsertDAOFactory insertDAOFactory;

    private List<String> offeringsTemp = new ArrayList<String>();

    /**
     * constructor
     * 
     * @param cpool
     *        factory for InsetDAOs
     * @param cpool
     *        factory for creating InsertDAOs
     */
    public PGSQLRegisterSensorDAO(PGConnectionPool cpool) {
    	this.cpool = cpool;
        this.insertDAOFactory = new PGSQLInsertDAOFactory(cpool);
    }

    /**
     * inserts sensor and corresponding offerings, domain features and phenomena contained in passed request
     * 
     * @param request
     *        RegisterSensorRequest
     * @return Returns assignedSensorID
     * @throws OwsExceptionReport
     *         if insertion of sensor failed
     * 
     */
    public String insertSensor(SosRegisterSensorRequest request) throws OwsExceptionReport {

        CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();
        capsCache.update(false); // .refreshMetadata();

        String assignedSensorID = "";
        Connection trCon = null;

        try {

            trCon = cpool.getConnection();
            trCon.setAutoCommit(false);

            // Get ID for sensor system

            if (request.getSystem().getId() == null) {
                assignedSensorID = SosConstants.PROCEDURE_PREFIX + sensorIDcounter;
                sensorIDcounter++;
            }
            else {
                assignedSensorID = request.getSystem().getId();
            }

            // insert sensor into SOS database
            if ( !capsCache.getProcedures().keySet().contains(assignedSensorID)) {
                IInsertProcedureDAO insertProcDAO = this.insertDAOFactory.getInsertProcedureDAO();
                try {
                    insertProcDAO.insertProcedure(request.getSystem(), trCon);
                }
                catch (SQLException e) {
                    String message = "Error while inserting procedure into DB: " + e.getMessage();
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                    log.error(message);
                    throw se;
                }
            }
            else {
                String message = "Sensor with ID: '" + assignedSensorID
                        + "' is already registered at ths SOS!";
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                log.error(message);
                throw se;
            }

            // insert phenomena
            insertPhenomena(request.getPhenomena(), trCon);

            // insert relationship between phenomena-procedure and
            // offering-procedure into DB
            IInsertRelationshipsDAO insertRelDAO = this.insertDAOFactory.getInsertRelationshipsDAO();
            // already inserted offerings are cached, because different phenomena
            // can belong to same offering
            Collection<String> offsAlreadyInserted = new ArrayList<String>(5);
            for (SosPhenomenon phen : request.getPhenomena()) {
                try {
                    insertRelDAO.insertProcPhenRelationship(assignedSensorID,
                                                            phen.getPhenomenonID(),
                                                            trCon);

                    // insert procedure offering relationships
                    Iterator<SosOffering> offiter = phen.getOfferings().iterator();
                    while (offiter.hasNext()) {
                        SosOffering offering = offiter.next();
                        if ( !offsAlreadyInserted.contains(offering.getOfferingID())) {
                            insertRelDAO.insertProcOffRelationship(assignedSensorID,
                                                                   offering.getOfferingID(),
                                                                   trCon);
                            offsAlreadyInserted.add(offering.getOfferingID());
                        }
                    }
                }
                catch (SQLException e) {
                    String message = "Error while inserting relationship between procedure and phenomena into DB: "
                            + e.getMessage();
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                    log.error(message);
                    throw se;
                }
            }

            // insert domain features, if SOS is mobile enabled
            if (request.isMobileEnabled()) {
                if (request.getDomainFeatures() != null && request.getDomainFeatures().size() > 0) {
                    insertDomainFeatures(request.getDomainFeatures(),
                                         assignedSensorID,
                                         request.getPhenomena(),
                                         trCon);
                }
            }

            trCon.commit();
            trCon.setAutoCommit(true);

            capsCache.update(false); // .refreshMetadata();

        }
        catch (SQLException e) {
            String message = "Error while executing registerSensor operation. Values could not be stored in database: "
                    + e.getMessage();
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
            log.error(message);
            throw se;
        }
        finally {
            if (trCon != null) {
                cpool.returnConnection(trCon);
            }
        }

        return assignedSensorID;

    } // end insertSensor

    /**
     * help method for inserting domain features into SOS database; it is checked at first, whether domain
     * features are already contained in SOS;
     * 
     * @param domainFeatures
     *        collection of domainFeatures, which should be inserted
     * @throws OwsExceptionReport
     *         if insertion of domainFeatures failed
     */
    private void insertDomainFeatures(Collection<SosAbstractFeature> domainFeatures,
                                      String procedureID,
                                      Collection<SosPhenomenon> phenomena,
                                      Connection trCon) throws OwsExceptionReport {

        CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();
        IInsertRelationshipsDAO insertRelDAO = this.insertDAOFactory.getInsertRelationshipsDAO();
        IInsertDomainFeatureDAO insertDomFeatDAO = this.insertDAOFactory.getInsertDomainFeatureDAO();

        try {

            for (SosAbstractFeature df : domainFeatures) {

                // if only id is set, insert relationship, if necessary
                if (df.getDescription().equalsIgnoreCase("PARAMETER_"
                        + SosConstants.PARAMETER_NOT_SET)
                        || df.getName().equalsIgnoreCase("PARAMETER_"
                                + SosConstants.PARAMETER_NOT_SET) || df.getGeom() == null) {

                    // if df is not contained in db, throw exception
                    if (capsCache.getDomainFeatures().contains(df.getId())) {
                        // if no procs for df are stored
                        if (capsCache.getProcs4DomainFeature(df.getId()) == null) {
                            // insert relationship
                            insertRelDAO.insertProcDfRelationsship(procedureID, df.getId(), trCon);
                            capsCache.updateFois(); // .refreshFOIs();
                        }
                        // if some procs are contained for df, insert relationship, if procID from request is
                        // not contained
                        else if ( !capsCache.getProcs4DomainFeature(df.getId()).contains(procedureID)) {
                            // insert relationship
                            insertRelDAO.insertProcDfRelationsship(procedureID, df.getId(), trCon);
                            capsCache.updateFois(); // .refreshFOIs();
                        }
                    }
                    else {
                        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                        log.error("Error: domainFeature '"
                                + df.getId()
                                + "' is not contained in database. Please add a domainFeature to your request.");
                        se.addCodedException(ExceptionCode.NoApplicableCode,
                                             null,
                                             "Error: domainFeature '"
                                                     + df.getId()
                                                     + "' is not contained in database. Please add a domainFeature to your request.");
                        throw se;
                    }
                }
                else {

                    if ( !capsCache.getDomainFeatures().contains(df.getId())) {

                        // insert domain feature, if not contained in DB
                        insertDomFeatDAO.insertDomainFeature(df.getId(),
                                                             df.getName(),
                                                             df.getDescription(),
                                                             df.getGeom(),
                                                             df.getFeatureType(),
                                                             df.getSchemaLink(),
                                                             trCon);

                        // insert relationship
                        insertRelDAO.insertProcDfRelationsship(procedureID, df.getId(), trCon);
                        capsCache.updateFois(); // .refreshFOIs();
                    }
                    else {

                        // if no procs for df are stored
                        if (capsCache.getProcs4DomainFeature(df.getId()) == null) {
                            // insert relationship
                            insertRelDAO.insertProcDfRelationsship(procedureID, df.getId(), trCon);
                            capsCache.updateFois(); // .refreshFOIs();
                        }
                        // if some procs are contained for df, insert relationship, if procID from request is
                        // not contained
                        else if ( !capsCache.getProcs4DomainFeature(df.getId()).contains(procedureID)) {
                            // insert relationship
                            insertRelDAO.insertProcDfRelationsship(procedureID, df.getId(), trCon);
                            capsCache.updateFois(); // .refreshFOIs();
                        }
                    }
                }

                // Insert df_off relationships, if necessary
                // get phenomena
                List<String> dfOffTemp = new ArrayList<String>();
                for (SosPhenomenon phen : phenomena) {
                    // get offerings fpr phenomenon
                    for (SosOffering off : phen.getOfferings()) {
                        // if relationship is not contained in DB, insert relationship
                        if ( !capsCache.getOffDomainFeatures().containsKey(off.getOfferingID())
                                && !dfOffTemp.contains(off.getOfferingID())) {
                            insertRelDAO.insertDfOffRelationsip(df.getId(),
                                                                off.getOfferingID(),
                                                                trCon);
                            dfOffTemp.add(off.getOfferingID());
                        }
                    }
                }
                // refresh CapsCache
                capsCache.updateFois(); // .refreshFOIs();
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the data from the database: " + sqle.toString());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

    } // end insertDomainFeatures

    /**
     * help method for inserting phenomena into SOS database; it is checked at first, whether phenomena are
     * already contained, in SOS; same has to be done with offerings, which should be nested in phenomena
     * 
     * @param phenomena
     *        collection of phenomena, which should be inserted
     * @throws OwsExceptionReport
     *         if insertion of phenomena failed
     */
    private void insertPhenomena(Collection<SosPhenomenon> phenomena, Connection trCon) throws OwsExceptionReport {
        CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();
        Iterator<SosPhenomenon> phenIter = phenomena.iterator();
        IInsertOfferingDAO insertOffDAO = this.insertDAOFactory.getInsertOfferingDAO();
        IInsertRelationshipsDAO insertRelDAO = this.insertDAOFactory.getInsertRelationshipsDAO();

        List<String> phenOff = new ArrayList<String>();

        while (phenIter.hasNext()) {
            SosPhenomenon sosPhen = phenIter.next();

            String phenID = sosPhen.getPhenomenonID();
            SosOffering offering = null;

            // fetch offering and
            Collection<SosOffering> offerings = sosPhen.getOfferings();
            if (offerings.size() == 1) {
                offering = (SosOffering) offerings.toArray()[0];
            }

            // if phenomenon is not contained in SOS yet, insert phenomenon
            if ( !capsCache.getAllPhenomenons().contains(phenID)) {
                IInsertPhenomenonDAO insertPhenDao = this.insertDAOFactory.getInsertPhenomenonDAO();
                try {
                    insertPhenDao.insertPhenomenon(sosPhen, trCon);

                    // if offering is also not contained, insert offering
                    if ( !capsCache.getOfferings().contains(offering.getOfferingID())
                            && !offeringsTemp.contains(offering.getOfferingID())) {
                        try {
                            insertOffDAO.insertOffering(offering, trCon);
                            offeringsTemp.add(offering.getOfferingID());
                        }
                        catch (IOException ioe) {
                            String message = "Error while inserting offering into DB: "
                                    + ioe.getMessage();
                            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                            se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                            log.error(message);
                            throw se;
                        }
                    }
                    if ( !phenOff.contains(phenID + offering.getOfferingID())) {
                        insertRelDAO.insertPhenOffRelationship(phenID,
                                                               offering.getOfferingID(),
                                                               trCon);
                        phenOff.add(phenID + offering.getOfferingID());
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         SosConstants.RegisterSensorParams.SensorDescription.name(),
                                         "Phenomenon could not be registered");
                    log.warn(se.getMessage());
                    throw se;
                }
            }
            else {

                // check, whether value type of phenomenon is numerical
                // value due to later inserts of measurements
                // only
                ValueTypes valueType = capsCache.getValueType4Phenomenon(phenID);
                if (valueType != ValueTypes.numericType && valueType != ValueTypes.spatialType
                        && valueType != ValueTypes.textType) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         SosConstants.RegisterSensorParams.SensorDescription.name(),
                                         "Sensor could only registered for phenomena, which have numeric, spatial or text values!");
                    log.warn(se.getMessage());
                    throw se;
                }

                // check, if relationship between offering and phenomenon is
                // already contained in SOS, otherwise insert
                if ( !capsCache.getOfferings4Phenomenon(phenID).contains(offering.getOfferingID())) {
                    // check, if offering is already contained in SOS
                    if ( !capsCache.getOfferings().contains(offering.getOfferingID())
                            && !offeringsTemp.contains(offering.getOfferingID())) {
                        try {
                            insertOffDAO.insertOffering(offering, trCon);
                            offeringsTemp.add(offering.getOfferingID());
                        }
                        catch (Exception e) {
                            String message = "Error while inserting offering into DB: "
                                    + e.getMessage();
                            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                            se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                            log.error(message);
                            throw se;
                        }
                    }
                    try {
                        if ( !phenOff.contains(phenID + offering.getOfferingID())) {
                        insertRelDAO.insertPhenOffRelationship(phenID,
                                                               offering.getOfferingID(),
                                                               trCon);
                        phenOff.add(phenID + offering.getOfferingID());
                        }
                    }
                    catch (SQLException e) {
                        String message = "Error while inserting relationship between offering and phenomenon into DB: "
                                + e.getMessage();
                        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                        se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
                        log.error(message);
                        throw se;
                    }
                }
            }

        }
    }// end insertPhenomena

}// end class
