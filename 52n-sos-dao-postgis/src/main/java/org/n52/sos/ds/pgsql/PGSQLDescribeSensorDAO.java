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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.SosConstants.DescribeSensorParams;
import org.n52.sos.ds.IDescribeSensorDAO;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.GMLConstants;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.ogc.sensorML.ProcedureHistory;
import org.n52.sos.ogc.sensorML.ProcedureHistoryEvent;
import org.n52.sos.request.SosDescribeSensorRequest;
import org.n52.sos.resp.SensorDocument;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

/**
 * DAO of PostgreSQL DB for GetCapabilities Operation
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLDescribeSensorDAO implements IDescribeSensorDAO {

    /** the logger, used to log exceptions and additonaly information */
    private static Logger log = Logger.getLogger(PGSQLDescribeSensorDAO.class);

    /** connection pool for getting connections to DB */
    private PGConnectionPool cpool;


    /**
     * standard constructor sets up the logger
     */
    public PGSQLDescribeSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * @return Returns the sensor description as result of the describeSensor operation
     */
    public SensorDocument getSensorDocument(SosDescribeSensorRequest request) throws OwsExceptionReport {

        // sensorDocument which should be returned
        SensorDocument result = null;

        String filename = null;
        String descriptionType = null;
        String smlFile = null;

        String query = "SELECT " + PGDAOConstants.descUrlCn + " , " + PGDAOConstants.descTypeCn
                + " , " + PGDAOConstants.smlFileCn + " FROM " + PGDAOConstants.procTn + " WHERE "
                + PGDAOConstants.procIDCn + " = '" + request.getProcedure() + "'";

        Connection con = null;
        try {
            con = cpool.getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                filename = rs.getString(PGDAOConstants.descUrlCn);
                descriptionType = rs.getString(PGDAOConstants.descTypeCn);
                smlFile = rs.getString(PGDAOConstants.smlFileCn);

                log.debug("FILENAME: " + filename);
                log.debug("DESCTYPE: " + descriptionType);
                log.debug("SMLFILE: " + smlFile);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the procedure from the SOS database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }

        // check whether SMLFile or Url is set
        if (filename == null && smlFile == null) {

            // throw ex
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.DescribeSensorParams.procedure.toString(),
                                 "No sensorML file was found for the requested procedure "
                                         + request.getProcedure());
            log.error("No sensorML file was found for the requested procedure "
                    + request.getProcedure());
            throw se;

        }
        else {

            if (filename != null && descriptionType != null && smlFile == null) {
                // return sensorML from folder

                if ( !descriptionType.equalsIgnoreCase(request.getOutputFormat())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.outputFormat.toString(),
                                         "The value of the output format is wrong and has to be "
                                                 + descriptionType + " for procedure "
                                                 + request.getProcedure());
                    log.error("The value of the output format is wrong and has to be "
                            + descriptionType + " for procedure " + request.getProcedure());
                    throw se;
                }

                try {

                    File sensorFile = null;
                    log.info(filename);

                    // read in the description file
                    if (filename.startsWith("standard/")) {
                        filename = filename.replace("standard/", "");
                        sensorFile = new File(SosConfigurator.getInstance().getSensorDir(),
                                              filename);
                    }
                    else {
                        sensorFile = new File(filename);
                    }

                    FileInputStream fis = new FileInputStream(sensorFile);

                    // parse the file into an org.w3c.dom.Document
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(fis);

                    // close inpuStream of description file
                    fis.close();

                    // set result
                    result = new SensorDocument(doc);

                }
                catch (FileNotFoundException fnfe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              fnfe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, fnfe);
                    throw se;
                }
                catch (IOException ioe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              ioe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, ioe);
                    throw se;
                }
                catch (ParserConfigurationException pce) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              pce);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, pce);
                    throw se;
                }
                catch (SAXException saxe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              saxe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, saxe);
                    throw se;
                }
            }
            else {
                // return sensorML from DB

                try {

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(smlFile)));

                    result = new SensorDocument(doc);

                }
                catch (SAXException saxe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              saxe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, saxe);
                    throw se;
                }
                catch (IOException ioe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              ioe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, ioe);
                    throw se;
                }
                catch (ParserConfigurationException pce) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              pce);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, pce);
                    throw se;
                }
            }
        }

        return result;
    }

    /**
     * @return Returns the sensor description as result of the describeSensor operation
     */
    public String getSensorDescription(SosDescribeSensorRequest request) throws OwsExceptionReport {

        // sensorDocument which should be returned
        String result = null;

        String filename = null;
        String descriptionType = null;
        String smlFile = null;

        String query = "SELECT " + PGDAOConstants.descUrlCn + " , " + PGDAOConstants.descTypeCn
                + " , " + PGDAOConstants.smlFileCn + " FROM " + PGDAOConstants.procTn + " WHERE "
                + PGDAOConstants.procIDCn + " = '" + request.getProcedure() + "'";

        Connection con = null;
        try {
            con = cpool.getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                filename = rs.getString(PGDAOConstants.descUrlCn);
                descriptionType = rs.getString(PGDAOConstants.descTypeCn);
                smlFile = rs.getString(PGDAOConstants.smlFileCn);

                log.debug("FILENAME: " + filename);
                log.debug("DESCTYPE: " + descriptionType);
                log.debug("SMLFILE: " + smlFile);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the procedure from the SOS database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }

        // check whether SMLFile or Url is set
        if (filename == null && smlFile == null) {

            // throw ex
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.DescribeSensorParams.procedure.toString(),
                                 "No sensorML file was found for the requested procedure "
                                         + request.getProcedure());
            log.error("No sensorML file was found for the requested procedure "
                    + request.getProcedure());
            throw se;

        }
        else {

            if (filename != null && descriptionType != null && smlFile == null) {
                // return sensorML from folder

                if (descriptionType.equalsIgnoreCase(request.getOutputFormat())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.outputFormat.toString(),
                                         "The value of the output format is wrong and has to be "
                                                 + descriptionType + " for procedure "
                                                 + request.getProcedure());
                    log.error("The value of the output format is wrong and has to be "
                            + descriptionType + " for procedure " + request.getProcedure());
                    throw se;
                }

                try {

                    File sensorFile = null;
                    log.info(filename);

                    // read in the description file
                    if (filename.startsWith("standard/")) {
                        filename = filename.replace("standard/", "");
                        sensorFile = new File(SosConfigurator.getInstance().getSensorDir(),
                                              filename);
                    }
                    else {
                        sensorFile = new File(filename);
                    }

                    FileInputStream fis = new FileInputStream(sensorFile);

                    byte[] b = new byte[fis.available()];
                    fis.read(b);
                    fis.close();

                    // set result
                    result = new String(b);

                }
                catch (FileNotFoundException fnfe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              fnfe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, fnfe);
                    throw se;
                }
                catch (IOException ioe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    log.error("An error occured while parsing the sensor description document!",
                              ioe);
                    se.addCodedException(ExceptionCode.NoApplicableCode, null, ioe);
                    throw se;
                }
            }
            else {
                // return sensorML from DB
                result = smlFile;
            }
        }

        return result;

    }

    /**
     * @return Returns the actual position of a sensor
     */
    public Point getActualPosition(SosDescribeSensorRequest request) throws OwsExceptionReport {

        // actualPosition which should be returned
        Point actualPosition = null;

        String query = "SELECT AsText(" + PGDAOConstants.actualPositionCn + ") AS geomText , SRID("
                + PGDAOConstants.actualPositionCn + ") AS srid " + " FROM " + PGDAOConstants.procTn
                + " WHERE " + PGDAOConstants.procIDCn + " = '" + request.getProcedure() + "'";

        WKTReader wktReader = new WKTReader();
        Connection con = null;
        try {
            con = cpool.getConnection();

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                // read position
                String geomWKT = rs.getString("geomText");
                int srid = Integer.MIN_VALUE;
                try {
                    Geometry geom = wktReader.read(geomWKT);
                    srid = rs.getInt("srid");
                    geom.setSRID(srid);
                    actualPosition = (Point) geom;
                }
                catch (Exception pe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.NoApplicableCode,
                                         null,
                                         "Error while parsing geometry of procedure from SOS DB: "
                                                 + pe.getMessage());
                    log.warn(se.getMessage());
                    throw se;
                }

            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the procedure from the SOS database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }

        return actualPosition;

    }

    /**
     * 
     * queries dynamic sensor parameters from SOSmobile DB and returns history containing the dynamic
     * parameters
     * 
     * @param request
     *        describeSensor request
     * @return Returns history of dynamic sensor parameters
     */
    public ProcedureHistory getProcedureHistory(SosDescribeSensorRequest request) throws OwsExceptionReport {

        ProcedureHistory history = null;

        // build query
        StringBuilder query = new StringBuilder();

        query.append("SELECT " + PGDAOConstants.procHistProcedureIdCn + " , "
                + PGDAOConstants.procHistActiveCn + " , " + PGDAOConstants.mobileCn + " , AsText("
                + PGDAOConstants.procHistPositionCn + ") AS geomText " + " , SRID("
                + PGDAOConstants.procHistPositionCn + ") AS srid , "
                + "iso_timestamp(" + PGDAOConstants.procHistTimeStampCn + ") AS " + PGDAOConstants.procHistTimeStampCn + " FROM " + PGDAOConstants.procHistTn);

        if (request.getTime() != null && request.getTime().length > 0) {
            query.append(getWhereClause4Time(request.getTime(), request.getProcedure()));
        }

        Connection con = null;
        WKTReader wktReader = new WKTReader();
        Collection<ProcedureHistoryEvent> events = new ArrayList<ProcedureHistoryEvent>();
        try {
            con = cpool.getConnection();

            Statement stmt = con.createStatement();
            log.debug("PROCEDURE HISTORY QUERY[DescribeSensor Operation]: " + query);
            ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                String procID = rs.getString(PGDAOConstants.procIDCn);

                // read position
                String geomWKT = rs.getString("geomText");
                int srid = Integer.MIN_VALUE;
                Point position = null;
                try {
                    Geometry geom = wktReader.read(geomWKT);
                    srid = rs.getInt("srid");
                    geom.setSRID(srid);
                    position = (Point) geom;
                }
                catch (Exception pe) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.NoApplicableCode,
                                         null,
                                         "Error while parsing geometry of procedure history from SOSmobile DB: "
                                                 + pe.getMessage());
                    log.warn(se.getMessage());
                    throw se;
                }

                // read time stamp
                String dateString = rs.getString(PGDAOConstants.timestampCn);
                DateTime timeStamp = null;
                timeStamp = SosDateTimeUtilities.parseIsoString2DateTime(dateString);
                    

                // read active and mobile
                boolean mobile = rs.getBoolean(PGDAOConstants.mobileCn);
                boolean active = rs.getBoolean(PGDAOConstants.activeCn);

                ProcedureHistoryEvent event = new ProcedureHistoryEvent(procID,
                                                                        position,
                                                                        active,
                                                                        mobile,
                                                                        timeStamp);
                events.add(event);

            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the procedure from the SOS database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }
        history = new ProcedureHistory(events);
        return history;
    }

    /**
     * returns the where clause for the Time parameters of the describeSensor request
     * 
     * @param times
     *        Time[] which contains the TimeInstants or TimePeriods for which the where clause should be
     *        constructed
     * @return String containing the where clause for this Time[]
     * @throws XmlException
     *         if construction of the where clause failed
     * @throws OwsExceptionReport
     *         if construction of the where clause failed
     */
    private String getWhereClause4Time(TemporalFilter[] times, String procedure_id) throws OwsExceptionReport {

        // string which contains the where clause
        StringBuffer query = new StringBuffer();

        query.append(" WHERE " + PGDAOConstants.procHistProcedureIdCn + " = '" + procedure_id
                + "' ");

        TemporalFilter temporalFilter = times[0];

        ISosTime time = temporalFilter.getTime();
        if (time instanceof TimeInstant) {
            query.append(createWhereClause4timeInstant((TimeInstant) time,
                                                       temporalFilter.getOperator(),
                                                       procedure_id));
        }

        if (time instanceof TimePeriod) {
            query.append(createWhereClause4timePeriod((TimePeriod) time,
                                                      temporalFilter.getOperator(),
                                                      procedure_id));
        }

        // get TimeNodes
        for (int j = 1; j < times.length; j++) {

            temporalFilter = times[j];

            time = temporalFilter.getTime();
            if (time instanceof TimeInstant) {
                query.append(createWhereClause4timeInstant((TimeInstant) time,
                                                           temporalFilter.getOperator(),
                                                           procedure_id));
            }

            if (time instanceof TimePeriod) {
                query.append(createWhereClause4timePeriod((TimePeriod) time,
                                                          temporalFilter.getOperator(),
                                                          procedure_id));
            }

        }

        return query.toString();
    } // end getWhereClause4Time

    /**
     * creates and returns the where clause for time period parameter
     * 
     * @param timePeriod
     *        TimePeriod object for which the where clause should be created
     * @param operator
     *        name of timeObs element (e.g. ogc:after or ogc:TEquals)
     * @return String containing the where clause for time period parameter
     * @throws ParseException
     *         if parsing begin or end date failed
     * @throws OwsExceptionReport
     *         if one parameter is incorrect or a required paramter is missing
     */
    private String createWhereClause4timePeriod(TimePeriod timePeriod,
                                                TimeOperator operator,
                                                String procedure_id) throws OwsExceptionReport {

        DateTime start = timePeriod.getStart();
        String indetStartTime = timePeriod.getStartIndet();
        DateTime end = timePeriod.getEnd();
        String indetEndTime = timePeriod.getEndIndet();
        Period duration = timePeriod.getDuration();

        String result = "";

        switch (operator) {

        // before
        case TM_Before:
            // //if indetPosition of endPosition = After throw exception!
            if (indetEndTime != null) {
                if (indetEndTime.equals(GMLConstants.IndetTimeValues.after.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         DescribeSensorParams.time.toString(),
                                         "The value of indeterminatePosition = '"
                                                 + indetEndTime
                                                 + "'  of EndPosition in TimePeriod"
                                                 + " is in conflict with the temporal operator of time = '"
                                                 + operator);
                    throw se;
                }
            }

            result += " AND (" + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end) + "'"
                    + ")";
            break; // end before

        // after
        case TM_After:
            // if indetPosition of beginPosition = Before throw exception!
            if (indetEndTime != null) {
                if (indetEndTime.equals(GMLConstants.IndetTimeValues.before.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         DescribeSensorParams.time.toString(),
                                         "The value of indeterminatePosition = '"
                                                 + indetStartTime
                                                 + "'  of StartPosition in TimePeriod"
                                                 + " is in conflict with the temporal operator of time = '"
                                                 + operator);
                    throw se;
                }
            }

            result += " AND (" + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start)
                    + "'" + ")";
            break; // end after

        // during
        case TM_During:

            /*
             * if duration not null, values between a duration should be returned. Only the endPosition
             * PGConfigurator.timestampCn or indeterminateTime of endPosition is read in. The begin Position
             * is not received attention. A timePeriod element which works may look like: 
             * <ogc:During>
             * <gml:TimePeriod> 
             * <gml:beginPosition indeterminatePosition="unknown"></gml:beginPosition>
             * <gml:endPosition indeterminatePosition="now">2005-10-05T10:17:00</gml:endPosition>
             * <gml:duration>P5M</gml:duration>
             * </gml:TimePeriod> 
             * </ogc:During>
             */
            if (duration != null) {

                if (indetEndTime != null
                        && indetEndTime.equals(GMLConstants.IndetTimeValues.now.name())) {
                    end = new DateTime();
                }

                else if (end == null) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         DescribeSensorParams.time.toString(),
                                         "If duration is set, either timePosition of endPosition in timePeriod"
                                                 + " must be set or indeterminatePosition of endPosition must be 'now' or 'unknown', where"
                                                 + "'unknown' accords to 'now'.");
                    throw se;
                }
                DateTime begin = end.minus(duration);

                result += " AND (" + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(begin)
                        + "'" + " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end)
                        + "'" + ")";

            }

            else {
                if ( (indetStartTime != null && indetStartTime.equals(GMLConstants.IndetTimeValues.before.name()))
                        && (indetEndTime != null && indetEndTime.equals(GMLConstants.IndetTimeValues.after.name()))) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         DescribeSensorParams.time.toString(),
                                         "The value of indeterminatePosition of StartPosition ='"
                                                 + indetStartTime
                                                 + "'  and of indeterminatePosition of EndPosition ='"
                                                 + indetEndTime
                                                 + "' in TimePeriod"
                                                 + " is in conflict with the temporal operator of time = '"
                                                 + operator);
                    throw se;
                }

                result += " AND (" + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start)
                        + "'" + " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end)
                        + "'" + ")";

            }
            break; // end during

        // after
        case TM_Equals:

            if (indetStartTime != null && indetEndTime != null
                    && indetStartTime.equals(FilterConstants.TimeOperator.TM_Before)
                    && indetEndTime.equals(FilterConstants.TimeOperator.TM_After)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     DescribeSensorParams.time.toString(),
                                     "The value of indeterminatePosition of StartPosition ='"
                                             + indetStartTime
                                             + "'  and of indeterminatePosition of EndPosition ='"
                                             + indetEndTime
                                             + "' in TimePeriod"
                                             + " is in conflict with the temporal operator of time = '"
                                             + operator);
                throw se;
            }
            result += " AND (" + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " >= " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start)
                    + "'" + "AND " + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " <= " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end)
                    + "'" + ")";
            break; // end after
        }

        return result.toString();
    } // end getWhereClause4timeInstant

    /**
     * creates the where clause for timeInstant parameter
     * 
     * @param timeInstant
     *        the timeInstant for which the where clause should be created
     * @param operator
     *        String containing the timeObs value (e.g. "after" or "before"...)
     * @return String representing the where clause for time instant
     * 
     * @throws ParseException
     *         if parsing the time parameter failed
     * @throws OwsExceptionReport
     *         if value for operator is incorrect
     */
    private String createWhereClause4timeInstant(TimeInstant timeInstant,
                                                 TimeOperator operator,
                                                 String procedure_id) throws OwsExceptionReport {

        DateTime timeValue = timeInstant.getValue();
        String result = "";

        // now build strings thru switching between operators
        switch (operator) {

        // before
        case TM_Before:
            result += " AND " + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " < '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                    + "' ";
            break; // end before

        // after
        case TM_After:
            result += " AND " + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " > '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                    + "' ";
            break; // end after

        // during
        case TM_During:
            result += createTimeInstantString4Tequals(timeInstant);
            break;// end during

        // equals
        case TM_Equals:
            result += createTimeInstantString4Tequals(timeInstant);
            break; // end equals

        default:
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 DescribeSensorParams.time.toString(),
                                 operator.name());
            throw se;
        }

        return result;
    } // end createWhereClause4TimeInstant

    /**
     * creates condition string for time instant with operator T_Equals or T_During
     * 
     * @param time
     *        time instant for which the condition string should be created
     * @return Returns condition string created from passed time instant
     */
    private String createTimeInstantString4Tequals(TimeInstant time) {
        String result = "";
        String indeterminateValue = time.getIndeterminateValue();
        DateTime timeValue = time.getValue();
        if (indeterminateValue != null) {
            // indeterminateTime = "after"
            if (indeterminateValue.equals(GMLConstants.IndetTimeValues.after.name())) {
                result += " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " > '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                        + "' ";
            }

            // indeterminateTime = "before"
            else if (indeterminateValue.equals(GMLConstants.IndetTimeValues.before.name())) {
                result += " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " < '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                        + "' ";
            }

            // indeterminateTime = "now"
            else if (indeterminateValue.equals(GMLConstants.IndetTimeValues.now.name())) {
                result += " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " = '" + SosDateTimeUtilities.formatDateTime2IsoString(new DateTime())
                        + "' ";
            }

            // indeterminateTime = "unknown"
            else if (indeterminateValue.equals(GMLConstants.IndetTimeValues.unknown.name())) {
                result += " AND " + PGDAOConstants.procHistTn + "."
                        + PGDAOConstants.procHistTimeStampCn + " = '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                        + "' ";
            }
        }

        // indeterminateTime is incorrect or not set, the during clause
        // without indeterminatTime param is created!!
        else {
            result += " AND " + PGDAOConstants.procHistTn + "."
                    + PGDAOConstants.procHistTimeStampCn + " = '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue)
                    + "' ";
        }
        return result;
    } // end createTimeInstantString4Tequals

    /**
     * creates where clause for time period parameter
     * 
     * @param period
     *        time period, for which where clause should be created
     * @param procID
     *        id of procedure
     * @return Returns where clause for query
     */
    @SuppressWarnings("unused")
    private String createWhereClause4TimePeriod(TimePeriod period, String procID) {
        DateTime begin = period.getStart();
        DateTime end = period.getEnd();
        if (begin == null || end == null) {
            // TODO throw exception
        }
        String query = " WHERE (" + PGDAOConstants.procHistTimeStampCn + " <= '"
                + SosDateTimeUtilities.formatDateTime2IsoString(end) + "' AND " + PGDAOConstants.procHistTimeStampCn + ">= '"
                + SosDateTimeUtilities.formatDateTime2IsoString(begin) + "') AND " + PGDAOConstants.procHistProcedureIdCn + " ='"
                + procID + "';";
        return query;
    }

    /**
     * creates the where clause for getProcedureHistory operation, if time parameter of describeSensor request
     * is TimeInstant; if it is timeInstant, last actual parameters are returned
     * 
     * @param instant
     *        time instant of describeSensor request
     * @param procID
     *        id of procedure
     * @return Returns where clause
     * @throws OwsExceptionReport
     *         if time instant is incorrect
     */
    @SuppressWarnings("unused")
    private String createWhereClause4TimeInstant(TimeInstant instant, String procID) throws OwsExceptionReport {

        String query = " INNER JOIN (SELECT MAX(" + PGDAOConstants.procHistTimeStampCn
                + ") as time  FROM " + PGDAOConstants.procHistTn + " WHERE "
                + PGDAOConstants.procHistTimeStampCn + " < '";

        if (instant.getIndeterminateValue() != null) {

            if (instant.getIndeterminateValue().equals(GMLConstants.IndetTimeValues.now.name())) {
                query += SosDateTimeUtilities.formatDateTime2IsoString(new DateTime());
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                log.error("Only 'now' is supported as indeterminate time value in timeInstant parameter of DescribeSensor request!");
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     SosConstants.DescribeSensorParams.time.name(),
                                     "Only 'now' is supported as indeterminate time value in timeInstant parameter of DescribeSensor request!");
                throw se;
            }
        }

        else if (instant.getValue() != null) {
            query += SosDateTimeUtilities.formatDateTime2IsoString(instant.getValue());
        }

        query += "' AND " + PGDAOConstants.procHistProcedureIdCn + " = '" + procID + "'";
        query += ") AS temp ON " + PGDAOConstants.procHistTn + "."
                + PGDAOConstants.procHistTimeStampCn + " = temp.time ";
        query += " WHERE " + PGDAOConstants.procHistProcedureIdCn + " = '" + procID + "';";

        return query;
    }

}
