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

package org.n52.sos.ds.insert.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ds.insert.IInsertObservationDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.SosCategoryObservation;
import org.n52.sos.ogc.om.SosMeasurement;
import org.n52.sos.ogc.om.SosSpatialObservation;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class implements the IInsertObservationDAO interface and the methods for inserting observations into the
 * SOSDB which are defined in this interface.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertObservationDAO implements IInsertObservationDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertObservationDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     * @param handlerp
     *        MemoryHandler for the logger
     * @param logLevel
     *        Level for logging
     */
    public InsertObservationDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for inserting an observation
     * 
     * @param time
     *        time of the observation
     * @param procedure_id
     *        String the id of the procedure the observation value belongs to
     * @param feature_of_interest_id
     *        String the id of the feature_of_interest(Station) the observation value belongs to
     * @param phenomenon_id
     *        String the id of the phenomenon the observation value belongs to
     * @param offering_id
     *        id of the offering, which the observation is inserted for
     * @param value
     *        String the value of this observation
     * @param mimeType
     *        mimeType, if value is reference on stream or other file
     * @param valueColumnName
     *        should be name of the column (either 'text_value' or numeric_value')
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public int insertObservation(ISosTime time,
                                 String procedure_id,
                                 String feature_of_interest_id,
                                 String phenomenon_id,
                                 String offering_id,
                                 String value,
                                 String mimeType,
                                 String valueColumnName,
                                 Collection<SosQuality> quality,
                                 Connection trCon) throws SQLException, OwsExceptionReport {
        int observationID = Integer.MIN_VALUE;
        Connection con = null;
        try {
            StringBuilder insertStmt = new StringBuilder();
            insertStmt.append(createInsert4ObsTable(time,
                                                    procedure_id,
                                                    feature_of_interest_id,
                                                    phenomenon_id,
                                                    offering_id,
                                                    value,
                                                    mimeType,
                                                    valueColumnName));
            if (quality != null) {
                Iterator<SosQuality> qualityIter = quality.iterator();
                while (qualityIter.hasNext()) {
                    insertStmt.append(createInsert4QualTable(qualityIter.next()));
                }
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            

            Statement stmt = con.createStatement();
            
        	stmt.execute(insertStmt.toString());
            

//            String query = "SELECT currval(pg_get_serial_sequence('" + PGDAOConstants.obsTn + "','"
//                    + PGDAOConstants.obsIDCn + "')) AS obsID from " + PGDAOConstants.obsTn + ";";
            
            String query = "SELECT currval(pg_get_serial_sequence('" + PGDAOConstants.obsTn + "','"
            	+ PGDAOConstants.obsIDCn + "')) AS obsID;";
            
        	ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                observationID = rs.getInt("obsID");
            }
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

        return observationID;
    }// end insertObservation

    /**
     * method for inserting a spatial observation
     * 
     * @param time
     *        time of the observation
     * @param procedure_id
     *        String the id of the procedure the observation value belongs to
     * @param feature_of_interest_id
     *        String the id of the feature_of_interest(Station) the observation value belongs to
     * @param phenomenon_id
     *        String the id of the phenomenon the observation value belongs to
     * @param offering_id
     *        id of the offering, which the observation is inserted for
     * @param value
     *        Geometry the Geometry of this observation
     * @param valueColumnName
     *        should be name of the column (either 'text_value' or numeric_value')
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public int insertObservation(ISosTime time,
                                 String procedure_id,
                                 String feature_of_interest_id,
                                 String phenomenon_id,
                                 String offering_id,
                                 Geometry value,
                                 String valueColumnName,
                                 Collection<SosQuality> quality,
                                 Connection trCon) throws SQLException, OwsExceptionReport {
        int observationID = Integer.MIN_VALUE;
        Connection con = null;
        try {
            StringBuilder insertStmt = new StringBuilder();
            insertStmt.append(createInsert4ObsTable(time,
                                                    procedure_id,
                                                    feature_of_interest_id,
                                                    phenomenon_id,
                                                    offering_id,
                                                    value,
                                                    "",
                                                    valueColumnName));
            if (quality != null) {
                Iterator<SosQuality> qualityIter = quality.iterator();
                while (qualityIter.hasNext()) {
                    insertStmt.append(createInsert4QualTable(qualityIter.next()));
                }
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt.toString());
            String query = "SELECT currval(pg_get_serial_sequence('" + PGDAOConstants.obsTn + "','"
            	+ PGDAOConstants.obsIDCn + "')) AS obsID;";
            
        	ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                observationID = rs.getInt("obsID");
            }
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

        return observationID;
    }// end insertObservation

    /**
     * invokes ANALYZE statement to analyze tables after inserting observations
     * 
     * @throws ServiceException
     * @throws SQLException
     */
    public void analyze() throws OwsExceptionReport, SQLException {
        String query = "ANALYZE;";
        Connection con = null;
        try {
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            stmt.execute(query);
        }
        finally {
            if (con != null)
                cpool.returnConnection(con);
        }

    }

    /**
     * help method to create an insert statement for the observation table
     * 
     * @param time
     *        String timestring of the observation in PostgreSQL DB format ( yyyy-MM-dd HH:mm:ss)
     * @param procedure_id
     *        String the id of the procedure the observation value belongs to
     * @param feature_of_interest_id
     *        String the id of the feature_of_interest(Station) the observation value belongs to
     * @param phenomenon_id
     *        String the id of the phenomenon the observation value belongs to
     * @param offering_id
     *        id of the offering, which the observation is inserted for
     * @param value
     *        String the value of this observation
     * @param mimeType
     *        mimeType, if value is reference on stream or other file
     * @param valueColumnName
     *        should be name of the column (either 'text_value' or numeric_value')
     * 
     * @return String containing the insert statement for the observation table
     */
    private String createInsert4ObsTable(ISosTime time,
                                         String procedure_id,
                                         String feature_of_interest_id,
                                         String phenomenon_id,
                                         String offering_id,
                                         String value,
                                         String mimeType,
                                         String valueColumnName) {

        String timeString = "null";
        if (time != null && time instanceof TimeInstant) {
            TimeInstant timeInstant = (TimeInstant) time;
            timeString = "'" + SosDateTimeUtilities.formatDateTime2IsoString(timeInstant.getValue()) + "'";

        }

        String insertStmt = " INSERT INTO " + PGDAOConstants.obsTn + "( "
                + PGDAOConstants.timestampCn + ", " + PGDAOConstants.procIDCn + ", "
                + PGDAOConstants.foiIDCn + ", " + PGDAOConstants.phenIDCn + ", "
                + PGDAOConstants.offeringIDCn + ", " + valueColumnName + ", "
                + PGDAOConstants.mimeTypeCn + ") " + " VALUES (" + timeString + ", '"
                + procedure_id + "','" + feature_of_interest_id + "','" + phenomenon_id + "','"
                + offering_id + "','" + value + "','" + mimeType + "');";
        log.debug("InsertObsStatement: " + insertStmt);
        return insertStmt;
    }// end createInsert4ObsTable

    /**
     * help method to create an insert statement for the observation table
     * 
     * @param time
     *        String timestring of the observation in PostgreSQL DB format ( yyyy-MM-dd HH:mm:ss)
     * @param procedure_id
     *        String the id of the procedure the observation value belongs to
     * @param feature_of_interest_id
     *        String the id of the feature_of_interest(Station) the observation value belongs to
     * @param phenomenon_id
     *        String the id of the phenomenon the observation value belongs to
     * @param offering_id
     *        id of the offering, which the observation is inserted for
     * @param value
     *        String the value of this observation
     * @param mimeType
     *        mimeType, if value is reference on stream or other file
     * @param valueColumnName
     *        should be name of the column (either 'text_value' or numeric_value')
     * 
     * @return String containing the insert statement for the observation table
     */
    private String createInsert4ObsTable(ISosTime time,
                                         String procedure_id,
                                         String feature_of_interest_id,
                                         String phenomenon_id,
                                         String offering_id,
                                         Geometry value,
                                         String mimeType,
                                         String valueColumnName) {

        String timeString = "null";
        if (time != null && time instanceof TimeInstant) {
            TimeInstant timeInstant = (TimeInstant) time;
            timeString = "'" + SosDateTimeUtilities.formatDateTime2IsoString(timeInstant.getValue()) + "'";

        }

        String insertStmt = " INSERT INTO " + PGDAOConstants.obsTn + "( "
                + PGDAOConstants.timestampCn + ", " + PGDAOConstants.procIDCn + ", "
                + PGDAOConstants.foiIDCn + ", " + PGDAOConstants.phenIDCn + ", "
                + PGDAOConstants.offeringIDCn + ", " + valueColumnName + ", "
                + PGDAOConstants.mimeTypeCn + ") " + " VALUES (" + timeString + ", '"
                + procedure_id + "','" + feature_of_interest_id + "','" + phenomenon_id + "','"
                + offering_id + "',GeometryFromText('" + value + "'," + value.getSRID() + "),'"
                + mimeType + "');";
        log.debug("InsertObsStatement: " + insertStmt);
        return insertStmt;
    }// end createInsert4ObsTable

    /**
     * help method for creating the insert statement for the quality table
     * 
     * @param quality
     *        quality, which should be inserted into the database
     * @return Returns insert statement for inserting passed quality into DB quality table
     */
    private String createInsert4QualTable(SosQuality quality) {
        String insertStmt = " INSERT INTO " + PGDAOConstants.qualityTn + "( "
                + PGDAOConstants.obsIDCn + ", " + PGDAOConstants.qualUnitCn + ", "
                + PGDAOConstants.qualNameCn + ", " + PGDAOConstants.qualValueCn + ", "
                + PGDAOConstants.qualTypeCn + ") "
                + " VALUES (currval(pg_get_serial_sequence('observation','observation_id')),'"
                + quality.getResultUnit() + "', ";
        if (quality.getResultName() != null) {
            insertStmt += "'" + quality.getResultName() + "', '";
        }
        else {
            insertStmt += "null, '";
        }
        insertStmt += quality.getResultValue() + "','" + quality.getQualityType().name() + "');";
        log.debug("InsertObsStatement: " + insertStmt);
        return insertStmt;
    }// end createInsert4QualTable

    /**
     * method for inserting an observation into the SOS database
     * 
     * @param obs
     *        Observation which should be inserted
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public int insertObservation(AbstractSosObservation obs, Connection trCon) throws SQLException,
            OwsExceptionReport {

        if (obs instanceof SosMeasurement) {
            SosMeasurement meas = (SosMeasurement) obs;
            return insertObservation(obs.getSamplingTime(),
                                     obs.getProcedureID(),
                                     obs.getFeatureOfInterestID(),
                                     obs.getPhenomenonID(),
                                     obs.getOfferingID(),
                                     "" + meas.getValue(),
                                     obs.getMimeType(),
                                     PGDAOConstants.numericValueCn,
                                     obs.getQuality(),
                                     trCon);
        }

        else if (obs instanceof SosCategoryObservation) {
            SosCategoryObservation catObs = (SosCategoryObservation) obs;
            return insertObservation(obs.getSamplingTime(),
                                     obs.getProcedureID(),
                                     obs.getFeatureOfInterestID(),
                                     obs.getPhenomenonID(),
                                     obs.getOfferingID(),
                                     catObs.getTextValue(),
                                     obs.getMimeType(),
                                     PGDAOConstants.textValueCn,
                                     obs.getQuality(),
                                     trCon);
        }

        else if (obs instanceof SosSpatialObservation) {
            SosSpatialObservation spaObs = (SosSpatialObservation) obs;
            return insertObservation(obs.getSamplingTime(),
                                     obs.getProcedureID(),
                                     obs.getFeatureOfInterestID(),
                                     obs.getPhenomenonID(),
                                     obs.getOfferingID(),
                                     spaObs.getResult(),
                                     PGDAOConstants.spatialValueCn,
                                     obs.getQuality(),
                                     trCon);
        }

        else {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
                                 "Only Measurements, SpatialObservations or CategoryObservations could be inserted!!");
            log.warn(se.getMessage());
            throw se;
        }
    } // end insertObservation

    /**
     * method for inserting multiple observations into the database
     * 
     * @param observations
     *        ArrayList with Observation containing the observations which should be inserted
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public void insertObservations(ArrayList<AbstractSosObservation> observations, Connection trCon) throws SQLException,
            OwsExceptionReport {
        /*
         * if (observations != null) { for (AbstractSosObservation sosObs : observations) {
         * insertObservation(sosObs); } }
         */
        StringBuilder insertStmt = new StringBuilder();

        if (observations != null && observations.size() != 0) {

            Iterator<AbstractSosObservation> iter = observations.iterator();
            while (iter.hasNext()) {
                AbstractSosObservation obs = iter.next();
                if (obs instanceof SosMeasurement) {
                    SosMeasurement meas = (SosMeasurement) obs;
                    insertStmt.append(createInsert4ObsTable(obs.getSamplingTime(),
                                                            obs.getProcedureID(),
                                                            obs.getFeatureOfInterestID(),
                                                            obs.getPhenomenonID(),
                                                            obs.getOfferingID(),
                                                            "" + meas.getValue(),
                                                            obs.getMimeType(),
                                                            PGDAOConstants.numericValueCn));
                    if (meas.getQuality() != null) {
                        Iterator<SosQuality> qualIter = meas.getQuality().iterator();
                        while (qualIter.hasNext()) {
                            insertStmt.append(createInsert4QualTable(qualIter.next()));
                        }
                    }
                }

                else if (obs instanceof SosCategoryObservation) {
                    SosCategoryObservation catObs = (SosCategoryObservation) obs;
                    insertStmt.append(createInsert4ObsTable(obs.getSamplingTime(),
                                                            obs.getProcedureID(),
                                                            obs.getFeatureOfInterestID(),
                                                            obs.getPhenomenonID(),
                                                            obs.getOfferingID(),
                                                            catObs.getTextValue(),
                                                            obs.getMimeType(),
                                                            PGDAOConstants.textValueCn));
                    if (catObs.getQuality() != null) {
                        Iterator<SosQuality> qualIter = catObs.getQuality().iterator();
                        while (qualIter.hasNext()) {
                            insertStmt.append(createInsert4QualTable(qualIter.next()));
                        }
                    }
                }
            }
        }
        Connection con = null;
        try {
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt.toString());
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    } // end insertObservations

    /**
     * deletes values in observation table which are before the passed date
     * 
     * @param lastValidDate
     *        Date before which the values in the observation table should be deleted
     * @throws SQLException
     *         if deleting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public void deleteOldObservations(DateTime lastValidDate, Connection trCon) throws OwsExceptionReport,
            SQLException {

        Connection con = null;
        try {
            String statement = "DELETE FROM " + PGDAOConstants.obsTn + " WHERE "
                    + PGDAOConstants.timestampCn + " < '" + SosDateTimeUtilities.formatDateTime2IsoString(lastValidDate) + "';";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                 ResultSet.CONCUR_READ_ONLY);
            stmt.execute(statement);
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }// end deleteOldObservations

} // end of class
