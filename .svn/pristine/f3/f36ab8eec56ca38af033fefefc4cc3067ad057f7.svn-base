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

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.ds.IConfigDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * class represents the configDAO for a PostgreSQL database and implements the IConfigDAO
 * 
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLConfigDAO implements IConfigDAO {

    /**
     * logger, used for logging while initializing the constants from config file
     */
    private static Logger log = Logger.getLogger(PGSQLConfigDAO.class);

    /** connection pool which offers connections to database */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param conPool
     *        ConnectionPool to DB
     */
    public PGSQLConfigDAO(PGConnectionPool conPool) {
        this.cpool = conPool;
    }

    /**
     * queries the offerings of this SOS from the DB
     * 
     * @return Returns ArrayList with Strings containing the offerings of this SOS
     * @throws OwsExceptionReport
     *         if query of the offerings from the DB failed
     */
    public List<String> queryOfferings() throws OwsExceptionReport {

        String query = "SELECT " + PGDAOConstants.offeringIDCn + " FROM " + PGDAOConstants.offTn;
        List<String> offerings = new ArrayList<String>();
        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no offerings are available give back empty String
            if (rs == null) {
                return offerings;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String offering = rs.getString(PGDAOConstants.offeringIDCn);
                offerings.add(offering);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query offerings from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query offerings from DB!", se);
            throw se;
        }
        finally {
            if (con != null)
                cpool.returnConnection(con);
        }

        return offerings;
    }

    /**
     * queries the ids of SRS, which are supported by this SOS from DB
     * 
     * @return Returns Collection with Integer containing the ids of SRS, which are supported by this SOS from
     *         DB
     * @throws OwsExceptionReport
     *         if query of the SRS ids from the DB failed
     */
    public Collection<Integer> querySrids() throws OwsExceptionReport {

        String query = "SELECT srid FROM spatial_ref_sys;";
        Collection<Integer> srids = new ArrayList<Integer>();
        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no offerings are available give back empty String
            if (rs == null) {
                return srids;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                Integer srid = rs.getInt("srid");
                srids.add(srid);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query srids from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query srids from DB!", se);
            throw se;
        }
        finally {
            if (con != null)
                cpool.returnConnection(con);
        }

        return srids;
    }

    /**
     * queries the observedProperties for each offering from the DB and puts the values into a HashMap
     * 
     * @return Returns HashMap<String,String[]> containing the observedProperties as value for each offering
     *         (key)
     * @throws OwsExceptionReport
     *         if query of the observedProperties failed
     */
    public Map<String, List<String>> queryPhenomenons4Offerings() throws OwsExceptionReport {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.phenOffTn + ";";
        String phenomenon;
        String offering;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each pair of offeringID and phenomenonID, if not contained in
            // HashMap, insert new key-value
            // pair
            // otherwise add phenomenonID to other phenomenonIDs
            while (rs.next()) {
                offering = rs.getString(PGDAOConstants.offeringIDCn);
                phenomenon = rs.getString(PGDAOConstants.phenIDCn);

                if ( !result.containsKey(offering)) {
                    List<String> phenomena = new ArrayList<String>();
                    phenomena.add(phenomenon);
                    result.put(offering, phenomena);
                }

                else {
                    result.get(offering).add(phenomenon);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query phenomenons from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query offerings from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the offering names from the DB and puts the values into a HashMap
     * 
     * @return Returns HashMap<String,String> containing the offering as key and the offering name as value
     * @throws OwsExceptionReport
     *         if query of offering names failed
     */
    public Map<String, String> queryOfferingNames() throws OwsExceptionReport {
        Map<String, String> names = new HashMap<String, String>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.offTn;
        String name;
        String offeringID;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty HashMap
            if (rs == null) {
                return names;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                name = rs.getString(PGDAOConstants.offNameCn);

                if (offeringID != null && name != null)
                    names.put(offeringID, name);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query offering names from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query offerings from DB!", se);
            throw se;
        }

        finally {
            if (con != null)
                cpool.returnConnection(con);
        }
        return names;
    } // end queryOfferingNames

    /**
     * returns the procedures for each offering stored in a HashMap
     * 
     * @return HashMap<String, String[]> hash map containing the procedures for each offering
     * @throws OwsExceptionReport
     *         if DB query failed
     */
    public Map<String, List<String>> queryProcedures4Offerings() throws OwsExceptionReport {

        Map<String, List<String>> procedures4Off = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ","
                + PGDAOConstants.procIDCn + " FROM " + PGDAOConstants.procOffTn + ";";
        log.debug("PROC-OFF-QUERY: " + query);
        String procedureID;
        String offeringID;

        try {
            // execute query for single phenomena of offerings
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs == null) {
                return procedures4Off;
            }

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                procedureID = rs.getString(PGDAOConstants.procIDCn);
                if ( !procedures4Off.containsKey(offeringID)) {
                    ArrayList<String> procedureIDs = new ArrayList<String>();
                    procedureIDs.add(procedureID);
                    procedures4Off.put(offeringID, procedureIDs);
                }

                else {
                    procedures4Off.get(offeringID).add(procedureID);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query procedures from DB!", se);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return procedures4Off;
    }

    /**
     * queries all procedures which are used by the offerings offered by this SOS;
     * +++++++++++++++++++++++++++++++++ ATTENTION, just ID, mobility and status are set in the queried
     * objects, because these are necessary for GetCapabilities operation!! ++++++++++++++++++++++++++++++++++
     * 
     * 
     * @return ArrayList<String> containing all procedures contained in the DB
     * @throws OwsExceptionReport
     *         if query of the procedures failed
     */
    public Map<String, SensorSystem> queryAllProcedures() throws OwsExceptionReport {

        Map<String, SensorSystem> procedures = new HashMap<String, SensorSystem>();

        String query = "SELECT *  FROM " + PGDAOConstants.procTn;
        Connection con = null;
        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty HashMap
            if (rs == null) {
                return procedures;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String procID = rs.getString(PGDAOConstants.procIDCn);
                boolean mobility = rs.getBoolean(PGDAOConstants.mobileCn);
                boolean active = rs.getBoolean(PGDAOConstants.activeCn);
                SensorSystem proc = new SensorSystem(procID,
                                                     null,
                                                     null,
                                                     null,
                                                     null,
                                                     active,
                                                     mobility,
                                                     null);
                procedures.put(procID, proc);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query procedures from DB!", se);
            throw se;
        }

        finally {
            if (con != null)
                cpool.returnConnection(con);
        }

        return procedures;
    }

    /**
     * queries the result models for each offerings and puts them into a HashMap
     * 
     * @return Returns HashMap<String,String[]> containing the offerings as keys and the corresponding result
     *         models as values
     * @throws OwsExceptionReport
     *         if query of the result models failed
     */
    public Map<String, List<QName>> queryOfferingResultModels() throws OwsExceptionReport {

        Map<String, List<QName>> result = new HashMap<String, List<QName>>();

        Connection con = null;
        String query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ", "
                + PGDAOConstants.valueTypeCn + " FROM " + PGDAOConstants.phenTn
                + " NATURAL INNER JOIN " + PGDAOConstants.phenOffTn + ";";
        String valueType;
        String offeringID;
        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            /*
             * if (rs == null) { return result; }
             */

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                valueType = rs.getString(PGDAOConstants.valueTypeCn);

                // if HashMap does not contain the offering, add new offering
                // and corresponding result models
                // to result hash map
                if ( !result.containsKey(offeringID)) {
                    List<QName> resultModels = new ArrayList<QName>();

                    // if numerical value, add measurement type to result models
                    if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                        resultModels.add(SosConstants.RESULT_MODEL_MEASUREMENT);
                        result.put(offeringID, resultModels);

                    }

                    // if textual value add add scopedNameType to result models
                    // (used in category observation)
                    else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {
                        resultModels.add(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION);
                        result.put(offeringID, resultModels);
                    }
                    
                    // if spatial value add add scopedNameType to result models
                    // (used in spatial observation)
                    else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.spatialType.name())) {
                    	resultModels.add(SosConstants.RESULT_MODEL_SPATIAL_OBSERVATION);
                        result.put(offeringID, resultModels);
                    }

                    // check, if offering ID was created successfully, than add
                    // common observation result
                    // model (swe:Data)
                    if (result.get(offeringID) != null) {
                        result.get(offeringID).add(SosConstants.RESULT_MODEL_OBSERVATION);
                    }

                    // if not, add to result models and put them as new pair
                    // into result hash map
                    else {
                        resultModels.add(SosConstants.RESULT_MODEL_OBSERVATION);
                        result.put(offeringID, resultModels);
                    }
                }

                // if offering already contained as key in hash map, add the new
                // resultModels to the models
                // already contained as values for the offering
                else {
                    if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                        if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_MEASUREMENT)) {
                            result.get(offeringID).add(SosConstants.RESULT_MODEL_MEASUREMENT);
                        }
                    }
                    else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {
                        if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)) {
                            result.get(offeringID).add(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION);
                        }
                    }

                    if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_OBSERVATION)) {
                        result.get(offeringID).add(SosConstants.RESULT_MODEL_OBSERVATION);
                    }
                }
            }

            // now repeat this for composite phenomena
            query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ", "
                    + PGDAOConstants.valueTypeCn + " FROM " + PGDAOConstants.phenTn
                    + " NATURAL INNER JOIN " + PGDAOConstants.compPhenTn + " NATURAL INNER JOIN "
                    + PGDAOConstants.compPhenOffTn + ";";

            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

            /*
             * if (rs == null) { return result; }
             */

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                valueType = rs.getString(PGDAOConstants.valueTypeCn);

                // if HashMap does not contain the offering, add new offering
                // and corresponding result models
                // to result hash map
                if ( !result.containsKey(offeringID)) {
                    List<QName> resultModels = new ArrayList<QName>();

                    // if numerical value, add measurement type to result models
                    if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                        resultModels.add(SosConstants.RESULT_MODEL_MEASUREMENT);
                        result.put(offeringID, resultModels);

                    }

                    // if textual value add add scopedNameType to result models
                    // (used in category observation)
                    else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {
                        resultModels.add(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION);
                        result.put(offeringID, resultModels);
                    }

                    // check, if offering ID was created successfully, than add
                    // common observation result
                    // model (swe:Data)
                    if (result.get(offeringID) != null) {
                        result.get(offeringID).add(SosConstants.RESULT_MODEL_OBSERVATION);
                    }

                    // if not, add to result models and put them as new pair
                    // into result hash map
                    else {
                        resultModels.add(SosConstants.RESULT_MODEL_OBSERVATION);
                        result.put(offeringID, resultModels);
                    }
                }

                // if offering already contained as key in hash map, add the new
                // resultModels to the models
                // already contained as values for the offering
                else {
                    if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                        if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_MEASUREMENT)) {
                            result.get(offeringID).add(SosConstants.RESULT_MODEL_MEASUREMENT);
                        }
                    }
                    else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {
                        if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)) {
                            result.get(offeringID).add(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION);
                        }
                    }

                    if ( !result.get(offeringID).contains(SosConstants.RESULT_MODEL_OBSERVATION)) {
                        result.get(offeringID).add(SosConstants.RESULT_MODEL_OBSERVATION);
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query result models from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query result models from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the procedure for each feature of interest
     * 
     * @return HashMap<String,ArrayList<String>> contains the feature of interest ID as key and the
     *         corresponding procedureIDs as values
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryProcedures4FOIs() throws OwsExceptionReport {

        // foiIds as key, ArrayList containing the procedure ids as values
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        // build query
        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.procFoiTn + ";";
        String foiID;
        String procID;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                foiID = rs.getString(PGDAOConstants.foiIDCn);
                procID = rs.getString(PGDAOConstants.procIDCn);

                if (foiID != null && !foiID.equalsIgnoreCase("")) {
                    if (procID != null && !procID.equalsIgnoreCase("")) {
                        if (result.containsKey(foiID)) {
                            result.get(foiID).add(procID);
                        }
                        else {
                            List<String> procs = new ArrayList<String>();
                            procs.add(procID);
                            result.put(foiID, procs);
                        }

                    }

                    else {
                        if ( !result.containsKey(foiID)) {
                            List<String> procs = new ArrayList<String>();
                            result.put(foiID, procs);
                        }
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query result models from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query result models from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;

    }

    /**
     * queries the procedure for each domain feature
     * 
     * @return HashMap<String,ArrayList<String>> contains the domain feature ID as key and the corresponding
     *         procedureIDs as values
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryProcedures4DomainFeatures() throws OwsExceptionReport {

        // foiIds as key, ArrayList containing the procedure ids as values
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        // build query
        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.procDfTn + ";";
        String dfID;
        String procID;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                dfID = rs.getString(PGDAOConstants.domainFeatureIDCn);
                procID = rs.getString(PGDAOConstants.procIDCn);

                if (dfID != null && !dfID.equalsIgnoreCase("")) {
                    if (procID != null && !procID.equalsIgnoreCase("")) {
                        if (result.containsKey(dfID)) {
                            result.get(dfID).add(procID);
                        }
                        else {
                            List<String> procs = new ArrayList<String>();
                            procs.add(procID);
                            result.put(dfID, procs);
                        }

                    }

                    else {
                        if ( !result.containsKey(dfID)) {
                            List<String> procs = new ArrayList<String>();
                            result.put(dfID, procs);
                        }
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query result models from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query result models from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;

    }

    /**
     * queries the fois for each domain feature
     * 
     * @return HashMap<String,ArrayList<String>> contains the domain feature ID as key and the corresponding
     *         foiIDs as values
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryFois4DomainFeatures() throws OwsExceptionReport {

        // dfIds as key, ArrayList containing the foi ids as values
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        // build query
        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.foiDfTn + ";";
        String dfID;
        String foiID;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                dfID = rs.getString(PGDAOConstants.domainFeatureIDCn);
                foiID = rs.getString(PGDAOConstants.foiIDCn);

                if (dfID != null && !dfID.equalsIgnoreCase("")) {
                    if (foiID != null && !foiID.equalsIgnoreCase("")) {
                        if (result.containsKey(dfID)) {
                            result.get(dfID).add(foiID);
                        }
                        else {
                            List<String> fois = new ArrayList<String>();
                            fois.add(foiID);
                            result.put(dfID, fois);
                        }

                    }

                    else {
                        if ( !result.containsKey(dfID)) {
                            List<String> fois = new ArrayList<String>();
                            result.put(dfID, fois);
                        }
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query result models from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query result models from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;

    }

    /**
     * queries the units for each observedProperty and saves them in a HashMap
     * 
     * @return HashMap<String, String> with phenomenonID as key and the unit of the values for this
     *         phenomenon as values
     * @throws OwsExceptionReport
     *         if query of the units failed
     */
    public Map<String, ValueTypes> queryObsPropsValueTypes() throws OwsExceptionReport {
        String query = "SELECT " + PGDAOConstants.phenIDCn + ", " + PGDAOConstants.valueTypeCn
                + " FROM " + PGDAOConstants.phenTn;
        Map<String, ValueTypes> result = new HashMap<String, ValueTypes>();

        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String phenID = rs.getString(PGDAOConstants.phenIDCn);
                String valueTypeString = rs.getString(PGDAOConstants.valueTypeCn);

                if (valueTypeString != null && !valueTypeString.equalsIgnoreCase("")
                        && ValueTypes.contains(valueTypeString)) {
                    ValueTypes valueType = ValueTypes.valueOf(valueTypeString);
                    if (phenID != null && !phenID.equalsIgnoreCase("")) {

                        result.put(phenID, valueType);
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query units for ObservedProperties from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query units for ObservedProperties from DB!", se);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;

    }

    /**
     * @return Returns ArrayList with Strings containing the ids of all feature of interests contained in the
     *         database
     * @throws OwsExceptionReport
     *         if query failed
     */
    public List<String> queryAllFois() throws OwsExceptionReport {
        List<String> fois = new ArrayList<String>();

        String query = "SELECT " + PGDAOConstants.foiIDCn + " FROM " + PGDAOConstants.foiTn;

        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty HashMap
            if (rs == null) {
                return fois;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String foiID = rs.getString(PGDAOConstants.foiIDCn);
                fois.add(foiID);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query procedures from DB!", se);
            throw se;
        }

        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return fois;

    }

    /**
     * @return Returns ArrayList with Strings containing the ids of all domain features contained in the
     *         database
     * @throws OwsExceptionReport
     *         if query failed
     */
    public List<String> queryAllDomainFeatures() throws OwsExceptionReport {
        List<String> dfs = new ArrayList<String>();

        String query = "SELECT " + PGDAOConstants.domainFeatureIDCn + " FROM "
                + PGDAOConstants.dfTn;

        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty HashMap
            if (rs == null) {
                return dfs;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String dfID = rs.getString(PGDAOConstants.domainFeatureIDCn);
                dfs.add(dfID);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query procedures from DB!", se);
            throw se;
        }

        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        return dfs;

    }

    /**
     * queries the phenomenon components for each composite phenomenon
     * 
     * @return Returns HashMap<String,ArrayList<String>> containing the compositePhenomenonID as key and the
     *         phenomenon components as values
     * @throws OwsExceptionReport
     *         if an sql exception occured
     */
    public Map<String, List<String>> queryPhens4CompPhens() throws OwsExceptionReport {

        // Ids of composite phenomena as key, ArrayList containing the ids of
        // the phenomenon components as
        // values
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        // build query
        Connection con = null;
        String query = "SELECT " + PGDAOConstants.phenIDCn + " , " + PGDAOConstants.compPhenIDCn
                + " FROM " + PGDAOConstants.phenTn + " NATURAL JOIN " + PGDAOConstants.compPhenTn
                + ";";
        String compPhenID;
        String phenID;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                compPhenID = rs.getString(PGDAOConstants.compPhenIDCn);
                phenID = rs.getString(PGDAOConstants.phenIDCn);

                if (compPhenID != null && !compPhenID.equalsIgnoreCase("")) {
                    if (phenID != null && !phenID.equalsIgnoreCase("")) {
                        if (result.containsKey(compPhenID)) {
                            result.get(compPhenID).add(phenID);
                        }
                        else {
                            List<String> procs = new ArrayList<String>();
                            procs.add(phenID);
                            result.put(compPhenID, procs);
                        }

                    }

                    else {
                        if ( !result.containsKey(compPhenID)) {
                            List<String> procs = new ArrayList<String>();
                            result.put(compPhenID, procs);
                        }
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query result models from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query result models from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;
    }

    /**
     * queries the units for each observedProperty and saves them in a HashMap
     * 
     * @return HashMap<String, String> with offeringID as key and corresponding composite phenomenon ids as
     *         values
     * @throws OwsExceptionReport
     *         if query of the units failed
     */
    public Map<String, List<String>> queryOffCompPhens() throws OwsExceptionReport {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.compPhenOffTn + ";";
        String compositePhenomenon;
        String offering;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each pair of offeringID and phenomenonID, if not contained in
            // HashMap, insert new key-value
            // pair
            // otherwise add phenomenonID to other phenomenonIDs
            while (rs.next()) {
                offering = rs.getString(PGDAOConstants.offeringIDCn);
                compositePhenomenon = rs.getString(PGDAOConstants.compPhenIDCn);

                if ( !result.containsKey(offering)) {
                    List<String> phenomena = new ArrayList<String>();
                    phenomena.add(compositePhenomenon);
                    result.put(offering, phenomena);
                }

                else {
                    result.get(offering).add(compositePhenomenon);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query phenomenons from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query offerings from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the procedures for each phenomenon from the DB and puts the values into a HashMap
     * 
     * @return Returns HashMap<String,String[]> containing the procedures as value for each phenomenon (key)
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryProcedures4Phenomena() throws OwsExceptionReport {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.procPhenTn + ";";
        String phenomenon;
        String procedure;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each pair of offeringID and phenomenonID, if not contained in
            // HashMap, insert new key-value
            // pair
            // otherwise add phenomenonID to other phenomenonIDs
            while (rs.next()) {
                procedure = rs.getString(PGDAOConstants.procIDCn);
                phenomenon = rs.getString(PGDAOConstants.phenIDCn);

                if ( !result.containsKey(procedure)) {
                    List<String> phenomena = new ArrayList<String>();
                    phenomena.add(phenomenon);
                    result.put(procedure, phenomena);
                }

                else {
                    result.get(procedure).add(phenomenon);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query phenomena and procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query phenomena and procedures from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the phenomena for each procedure from the DB and puts the values into a HashMap
     * 
     * @return Returns HashMap<String,String[]> containing the phenomena as value for each procedure (key)
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryPhenomenons4Procedures() throws OwsExceptionReport {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.procPhenTn + ";";
        String phenomenon;
        String procedure;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each pair of offeringID and phenomenonID, if not contained in
            // HashMap, insert new key-value
            // pair
            // otherwise add phenomenonID to other phenomenonIDs
            while (rs.next()) {

                phenomenon = rs.getString(PGDAOConstants.phenIDCn);
                procedure = rs.getString(PGDAOConstants.procIDCn);

                if ( !result.containsKey(phenomenon)) {
                    List<String> procedures = new ArrayList<String>();
                    procedures.add(procedure);
                    result.put(phenomenon, procedures);
                }

                else {
                    result.get(phenomenon).add(procedure);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query phenomena and procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query phenomena and procedures from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the min and max time for each offering and returns a HashMap containing the offeringID as key
     * and the min_time max_time values as String[], where String[0] is min_time and String[1] is max_time
     * 
     * @return Returns HashMap containing the offeringID as key and the min_time max_time values as String[],
     *         where String[0] is min_time and String[1] is max_time; time is in PGSQL Date format (YYYY-MM-dd
     *         HH:mm:SSZ)
     * @throws OwsExceptionReport
     *         if query of time for offering failed
     */
    // TODO change this to return time period instead of string array!!!
    public Map<String, String[]> queryTime4Offerings() throws OwsExceptionReport {
        Map<String, String[]> result = new HashMap<String, String[]>();

        Connection con = null;
        String query = "SELECT " + PGDAOConstants.offeringIDCn + "," + PGDAOConstants.minTimeCn
                + "," + PGDAOConstants.maxTimeCn + " FROM " + PGDAOConstants.offTn + ";";
        log.debug(query);
        String offeringId;

        // String[] has length 2 and stores min and max value of time for each
        // offering
        String[] minMaxTime;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each triple of offeringID and minTime and maxTime
            // and add triple to result
            while (rs.next()) {
                offeringId = rs.getString(PGDAOConstants.offeringIDCn);

                // create String[] of length 2 with min and maxTime
                minMaxTime = new String[2];
                minMaxTime[0] = rs.getString(PGDAOConstants.minTimeCn);
                minMaxTime[1] = rs.getString(PGDAOConstants.maxTimeCn);

                // add to result
                result.put(offeringId, minMaxTime);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query phenomena and procedures from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query phenomena and procedures from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }
        return result;
    }

    /**
     * queries the unit for each phenomenon from the database
     * 
     * @return Returns HashMap containing the units(values) for the phenomena (keys)
     * @throws OwsExceptionReport
     *         if query failed
     */
    public Map<String, String> queryUnits4Phens() throws OwsExceptionReport {
        String query = "SELECT " + PGDAOConstants.phenIDCn + ", " + PGDAOConstants.unitCn
                + " FROM " + PGDAOConstants.phenTn;
        Map<String, String> result = new HashMap<String, String>();

        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String phenID = rs.getString(PGDAOConstants.phenIDCn);
                String unit = rs.getString(PGDAOConstants.unitCn);

                if ( !unit.equalsIgnoreCase("") && !phenID.equalsIgnoreCase("")) {
                    result.put(phenID, unit);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query units for ObservedProperties from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query units for ObservedProperties from DB!", se);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return result;

    }

    /**
     * queries the observation ids of this SOS from the DB
     * 
     * @return Returns ArrayList with Strings containing the observation ids of this SOS
     * @throws OwsExceptionReport
     *         if query of the observation ids from the DB failed
     */
    public List<String> queryObsIds() throws OwsExceptionReport {

        String query = "SELECT " + PGDAOConstants.obsIDCn + " FROM " + PGDAOConstants.obsTn;
        List<String> obsIds = new ArrayList<String>();
        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no offerings are available give back empty String
            if (rs == null) {
                return obsIds;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                String observation = rs.getString("observation_id");
                obsIds.add(observation);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query observation ids from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query observation ids from DB!", se);
            throw se;
        }
        finally {
            if (con != null)
                cpool.returnConnection(con);
        }

        return obsIds;
    }

    /**
     * queries EPSG code of coordinates contained in DB
     * 
     * @return Returns EPSG code of coordinates contained in DB
     * @throws OwsExceptionReport
     *             if query of EPSG code failed
     */
    public int queryEPSGcode() throws OwsExceptionReport {
        int result = 0;
        String query = "SELECT SRID(" + PGDAOConstants.geomCn + ") AS srid FROM "
                + PGDAOConstants.foiTn;
        Connection con = null;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no offerings are available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {
                result = rs.getInt("srid");
            }

        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport(e);
            log.error("error while query EPSG code from DB!", e);
            throw se;
        }
        finally {
            if (con != null)
                cpool.returnConnection(con);
        }
        return result;
    }

    /**
     * queries features of interest for each offering from DB
     * 
     * @return Returns Map containing the offeringIDs as keys and list with
     *         featureIDs as values
     * @throws OwsExceptionReport
     *             if DB query failed
     * 
     */
    public Map<String, List<String>> queryOffFeatures() throws OwsExceptionReport {
        Map<String, List<String>> features4Off = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ","
                + PGDAOConstants.foiIDCn + " FROM " + PGDAOConstants.foiOffTn + ";";
        String foiID;
        String offeringID;

        try {
            // execute query for single phenomena of offerings
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs == null) {
                return features4Off;
            }

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                foiID = rs.getString(PGDAOConstants.foiIDCn);
                if ( !features4Off.containsKey(offeringID)) {
                    ArrayList<String> foiIDs = new ArrayList<String>();
                    foiIDs.add(foiID);
                    features4Off.put(offeringID, foiIDs);
                }

                else {
                    features4Off.get(offeringID).add(foiID);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query features of interest for each offering from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query features of interest for each offering from DB!", se);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return features4Off;
    }// end queryOffFeatures

    /**
     * queries domain features for each offering from DB
     * 
     * @return Returns Map containing the offeringIDs as keys and list with
     *         domain_feature_ids as values
     * @throws OwsExceptionReport
     *             if DB query failed
     * 
     */
    public Map<String, List<String>> queryOffDomainFeatures() throws OwsExceptionReport {
        Map<String, List<String>> domainFeatures4Off = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ","
                + PGDAOConstants.domainFeatureIDCn + " FROM " + PGDAOConstants.dfOffTn + ";";
        String dfID;
        String offeringID;

        try {
            // execute query for single phenomena of offerings
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs == null) {
                return domainFeatures4Off;
            }

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                dfID = rs.getString(PGDAOConstants.domainFeatureIDCn);
                if ( !domainFeatures4Off.containsKey(offeringID)) {
                    ArrayList<String> dfIDs = new ArrayList<String>();
                    dfIDs.add(dfID);
                    domainFeatures4Off.put(offeringID, dfIDs);
                }

                else {
                    domainFeatures4Off.get(offeringID).add(dfID);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query domain features for each offering from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query domain features for each offering from DB!", se);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return domainFeatures4Off;
    }// end queryOffFeatures

    /**
     * returns the offerings for each procedure stored in a HashMap
     * 
     * @return HashMap<String, List<String>> hash map containing the offerings
     *         for each procedure
     * @throws OwsExceptionReport
     *             if DB query failed
     */
    public Map<String, List<String>> queryOfferings4Procedures() throws OwsExceptionReport {

        Map<String, List<String>> offerings4Proc = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT DISTINCT " + PGDAOConstants.offeringIDCn + ","
                + PGDAOConstants.procIDCn + " FROM " + PGDAOConstants.procOffTn + ";";
        log.debug("PROC-OFF-QUERY: " + query);
        String procedureID;
        String offeringID;

        try {
            // execute query for single phenomena of offerings
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs == null) {
                return offerings4Proc;
            }

            while (rs.next()) {
                offeringID = rs.getString(PGDAOConstants.offeringIDCn);
                procedureID = rs.getString(PGDAOConstants.procIDCn);
                if ( !offerings4Proc.containsKey(offeringID)) {
                    ArrayList<String> offeringIDs = new ArrayList<String>();
                    offeringIDs.add(offeringID);
                    offerings4Proc.put(procedureID, offeringIDs);
                }

                else {
                    offerings4Proc.get(procedureID).add(offeringID);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query offerings for procedures from DB!", sqle);
            throw se;
        }

        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
        }

        return offerings4Proc;
    }

    public Map<String, List<String>> queryPhenOffs() throws OwsExceptionReport {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        Connection con = null;
        String query = "SELECT * FROM " + PGDAOConstants.phenOffTn + ";";
        String phenomenon;
        String offering;

        try {
            // execute query
            con = cpool.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // if no result is available give back empty HashMap
            if (rs == null) {
                return result;
            }

            // get each pair of offeringID and phenomenonID, if not contained in
            // HashMap, insert new key-value
            // pair
            // otherwise add phenomenonID to other phenomenonIDs
            while (rs.next()) {
                offering = rs.getString(PGDAOConstants.offeringIDCn);
                phenomenon = rs.getString(PGDAOConstants.phenIDCn);

                if ( !result.containsKey(phenomenon)) {
                    List<String> offerings = new ArrayList<String>();
                    offerings.add(offering);
                    result.put(phenomenon, offerings);
                }

                else {
                    result.get(phenomenon).add(offering);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("error while query offerings for phenomena from DB!", sqle);
            throw se;
        }
        catch (OwsExceptionReport se) {
            log.error("error while query offerings for phenomena from DB!", se);
            throw se;
        }
        finally {
            if (con != null) {
                // return connection
                cpool.returnConnection(con);
            }
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

        DateTime result = null;

        Connection con = null;

        try {

            // get connection
            con = cpool.getConnection();

            // query max time
            Statement stmt = con.createStatement();
            String query = "SELECT iso_timestamp(MAX( " + PGDAOConstants.maxTimeCn + " )) AS MAXTIME from "
                    + PGDAOConstants.offTn;
            log.debug(">>>MAX DATE QUERY: " + query.toString());

            ResultSet rs = stmt.executeQuery(query);

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {

                String maxTime = rs.getString("MAXTIME");

                if (maxTime == null) {
                    return result;
                }
//              DELETE TIME
//                result = pgSdf.parse(maxTime.concat("00"));
                result = SosDateTimeUtilities.parseIsoString2DateTime(maxTime);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            if (sqle.getMessage().contains("function iso_timestamp(timestamp with time zone)")) {
                log.error("Function iso_timestamp(timestamp with time zone)does not exist! " +
                        "Run the update_iso_timestamp_function_postgres82.sql or " +
                        "update_iso_timestamp_function_postgres83.sql for the SOS database. " +
                        "Depending on your PostgreSQL version.", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle.getMessage() + 
                                     ". Run the update_iso_timestamp_function_postgres82.sql or " +
                                     "update_iso_timestamp_function_postgres83.sql for the SOS database. " +
                                     "Depending on your PostgreSQL version.");
            } else {
                log.error("Error while query max date of observation from database!", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            }
            throw se;
        }
        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }
        // return maxDate as String in gml format
        return result;
    }
    
    /**
     * returns the min date of an observation
     * 
     * @return Returns Date containing the min date of the observation in ISO8601 format
     * @throws OwsExceptionReport
     *         if query of the min date failed
     */
    public DateTime getMinDate4Observations() throws OwsExceptionReport {

        DateTime result = null;

        Connection con = null;

        try {
            // get connection
            con = cpool.getConnection();

            Statement stmt = con.createStatement();

            // query minDate
            String query = "SELECT iso_timestamp(MIN( " + PGDAOConstants.minTimeCn + " )) AS MINTIME from "
                    + PGDAOConstants.offTn;
            log.debug(">>>MIN DATE QUERY: " + query.toString());
            ResultSet rs = stmt.executeQuery(query);

            if (rs == null) {
                return result;
            }

            // get minDate as String from result set and parse min Date to java.util.date
            while (rs.next()) {

                String minTime = rs.getString("MINTIME");

                if (minTime == null) {
                    return result;
                }
                result = SosDateTimeUtilities.parseIsoString2DateTime(minTime);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            if (sqle.getMessage().contains("function iso_timestamp(timestamp with time zone)")) {
                log.error("Function iso_timestamp(timestamp with time zone)does not exist! " +
                        "Run the update_iso_timestamp_function_postgres82.sql or " +
                        "update_iso_timestamp_function_postgres83.sql for the SOS database. " +
                        "Depending on your PostgreSQL version.", sqle);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle.getMessage() + 
                                     ". Run the update_iso_timestamp_function_postgres82.sql or " +
                                     "update_iso_timestamp_function_postgres83.sql for the SOS database. " +
                                     "Depending on your PostgreSQL version.");
            } else {
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

        // return minDate as String in gml format
        return result;
    }
   
}// end of class PGSQLConfigDAO
