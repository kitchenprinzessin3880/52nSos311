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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.n52.sos.ds.insert.IInsertRelationshipsDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Data access object for inserting the m:n relationship into the SOS database. You have to make sure that all
 * foreign key constraints are hold!
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertRelationshipsDAO implements IInsertRelationshipsDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertRelationshipsDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     */
    public InsertRelationshipsDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for inserting a new relationship between procedure and phenomenon into the SOS DB.
     * 
     * ATTENTION: Please make sure that the phenomenon, the phenomenon_id references on, and the procedure,
     * the procedure_id references on, are already contained in the DB!!
     * 
     * @param procedure_id
     *        String the procedure id (id of a sensor or sensor group)
     * @param phenomenon_id
     *        String id of the phenomenon
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcPhenRelationship(String procedure_id,
                                           String phenomenon_id,
                                           Connection trCon) throws SQLException,
            OwsExceptionReport {

        Connection con = null;
        try {
            // insert statement for procedure table
            String insertStmt = "INSERT INTO " + PGDAOConstants.procPhenTn + " VALUES ('"
                    + procedure_id + "','" + phenomenon_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * insert the proc phen relationships into the SOS DB
     * 
     * @param procPhenRel
     *        the proc phen relationships which should be inserted
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcPhenRelationships(ArrayList<String[]> procPhenRel, Connection trCon) throws SQLException,
            OwsExceptionReport {

        Connection con = null;
        try {
            StringBuffer insertStatements = new StringBuffer();
            for (String[] procFoi : procPhenRel) {
                if (procFoi.length == 2) {
                    insertStatements.append(createInsertStmt4ProcPhen(procFoi[0], procFoi[1]));
                }
            }
            con = null;
            String insertStatement = null;
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            for (String[] procPhen : procPhenRel) {
                if (procPhen.length == 2) {
                    insertStatement = createInsertStmt4ProcPhen(procPhen[0], procPhen[1]);
                    stmt.execute(insertStatement);
                }
            }

            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between procedure and feature_of_interest into the SOS DB.
     * 
     * ATTENTION: Please make sure that the feature of interest, the feature_of_interest_id references on, and
     * the procedure, the procedure_id references on, are already contained in the DB!!
     * 
     * @param procedure_id
     *        String the procedure id (id of a sensor or sensor group)
     * @param feature_of_interest_id
     *        String id of the phenomenon
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcFoiRelationship(String procedure_id,
                                          String feature_of_interest_id,
                                          Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.procFoiTn + " VALUES ('"
                    + procedure_id + "','" + feature_of_interest_id + "');";
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);
            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between offering and feature_of_interest into the SOS DB.
     * 
     * ATTENTION: Please make sure that the feature of interest, the feature_of_interest_id references on, and
     * the offering, the offering_id references on, are already contained in the DB!!
     * 
     * @param feature_of_interest_id
     *        String id of the phenomenon
     * @param offering_id
     *        String the offering id
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertFoiOffRelationship(String feature_of_interest_id,
                                         String offering_id,
                                         Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.foiOffTn + " VALUES ('"
                    + feature_of_interest_id + "','" + offering_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);
            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * insert the proc foi relationships into the SOS DB
     * 
     * @param procFoiRel
     *        the proc foi relationships which should be inserted
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcFoiRelationships(ArrayList<String[]> procFoiRel, Connection trCon) throws SQLException,
            OwsExceptionReport {

        Connection con = null;
        try {
            StringBuffer insertStatements = new StringBuffer();
            for (String[] procFoi : procFoiRel) {
                if (procFoi.length == 2) {
                    insertStatements.append(createInsertStmt4ProcFoi(procFoi[0], procFoi[1]));
                }
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStatements.toString());

            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between offering and feature_of_interest into the SOS DB.
     * 
     * @param foiOffs
     * 		  Features of interest for the offerings
     * @param trCon
     * 		  Connection to the database
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertFoiOffRelationships(ArrayList<String[]> foiOffs, Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {
            StringBuffer insertStatements = new StringBuffer();
            for (String[] foiOff : foiOffs) {
                if (foiOff.length == 2) {
                    insertStatements.append(createInsertStmt4FoiOff(foiOff[0], foiOff[1]));
                }
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStatements.toString());
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between phenomenon and offering into the SOS DB.
     * 
     * ATTENTION: Please make sure that the phenomenon, the phenomenon_id references on, and the offering, the
     * offering_id references on, are already contained in the DB!!
     * 
     * @param phenomenon_id
     *        String id of the phenomenon which is related to the passed offering (also through its id)
     * @param offering_id
     *        String id of the offering which is related to the passed phenomenon (also through its id)
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertPhenOffRelationship(String phenomenon_id, String offering_id, Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {
            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.phenOffTn + " VALUES ('"
                    + phenomenon_id + "','" + offering_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);
            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between composite phenomenon and offering into the SOS DB.
     * 
     * ATTENTION: Please make sure that the composite phenomenon, the composite_phenomenon_id references on,
     * and the offering, the offering_id references on, are already contained in the DB!!
     * 
     * @param composite_phenomenon_id
     *        String id of the composite phenomenon which is related to the passed offering (also through its
     *        id)
     * @param offering_id
     *        String id of the offering which is related to the passed phenomenon (also through its id)
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertCompPhenOffRelationship(String composite_phenomenon_id,
                                              String offering_id,
                                              Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {
            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.compPhenOffTn + " VALUES ('"
                    + composite_phenomenon_id + "','" + offering_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);
            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new relationship between procedure and offering into the SOS DB.
     * 
     * ATTENTION: Please make sure that the procedure, the procedure_id references on, and the offering, the
     * offering_id references on, are already contained in the DB!!
     * 
     * @param procedure_id
     *        String the procedure id (id of a sensor or sensor group)
     * @param offering_id
     *        String id of the offering which is related to the passed phenomenon (also through its id)
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcOffRelationship(String procedure_id, String offering_id, Connection trCon) throws SQLException,
            OwsExceptionReport {

        Connection con = null;
        try {
            // insert statement for procedure table
            String insertStmt = "INSERT INTO " + PGDAOConstants.procOffTn + " VALUES ('"
                    + procedure_id + "','" + offering_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);
            // TODO refresh metadata of relationships in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * creates insert statement for relationship between procedure and phenomenon
     * 
     * @param procID
     *        id of the procedure
     * @param offID
     *        id of the phenomenon
     * @return String representing the where clause
     */
    private String createInsertStmt4ProcPhen(String procID, String phenID) {
        String insertStmt = "INSERT INTO " + PGDAOConstants.procPhenTn + "( "
                + PGDAOConstants.procIDCn + ", " + PGDAOConstants.phenIDCn + ") " + " VALUES ('"
                + procID + "','" + phenID + "');";
        return insertStmt;
    }

    /**
     * creates insert statement for relationship between procedure and feature of interest
     * 
     * @param procID
     *        id of the feature of interest
     * @param foiID
     *        id of the offering
     * @return String representing the where clause
     */
    private String createInsertStmt4ProcFoi(String procID, String foiID) {
        String insertStmt = "INSERT INTO " + PGDAOConstants.procFoiTn + "( "
                + PGDAOConstants.procIDCn + ", " + PGDAOConstants.foiIDCn + ") " + " VALUES ('"
                + procID + "','" + foiID + "');";
        return insertStmt;
    }

    /**
     * creates insert statement for relationship between foi and offerings
     * 
     * @param foiID
     *        id of the feature of interest
     * @param offID
     *        id of the offering
     * @return String representing the where clause
     */
    private String createInsertStmt4FoiOff(String foiID, String offID) {
        String insertStmt = "INSERT INTO " + PGDAOConstants.foiOffTn + "( "
                + PGDAOConstants.foiIDCn + ", " + PGDAOConstants.offeringIDCn + ") " + " VALUES ('"
                + foiID + "','" + offID + "');";
        return insertStmt;
    }

    /**
     * method for inserting a new relationship between domain feature and offering into the SOS DB.
     * 
     * ATTENTION: Please make sure that the domain feature, the domain feature id references on, and the
     * offering, the offering_id references on, are already contained in the DB!!
     * 
     * @param domain_feature_id
     *        String of the domain feature id
     * @param offering_id
     *        String id of the offering which is related to the passed phenomenon (also through its id)
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertDfOffRelationsip(String domain_feature_id,
                                       String offering_id,
                                       Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.dfOffTn + " VALUES ('"
                    + domain_feature_id + "','" + offering_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * method for inserting a new relationship between feature of interest and domain feature into the SOS DB.
     * 
     * Please make sure that the feature of interest, the feature_of_interest_id references on and the domain
     * feature, the domain_feature_id references on, are already contained in the DB!!
     *  *
     * @param feature_of_interest_id
     *        String id of the phenomenon
     * @param domain_feature_id
     *        String of the domain feature id
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertFoiDfRelationship(String feature_of_interest_id,
                                        String domain_feature_id,
                                        Connection trCon) throws SQLException, OwsExceptionReport {
        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.foiDfTn + " VALUES ('"
                    + feature_of_interest_id + "','" + domain_feature_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * method for inserting a new relationship between observation and domain feature into the SOS DB.
     * 
     * ATTENTION: Please make sure that the observation, the observation_id references on, and the domain
     * feature, the domain_feature_id references on, are already contained in the DB!!
     * 
     * @param observation_id
     *        String the observation id
     * @param domain_feature_id
     *        String of the domain feature id
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertObsDfRelationship(String domain_feature_id,
                                        String observation_id,
                                        Connection trCon) throws SQLException, OwsExceptionReport {
        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.obsDfTn + " VALUES ('"
                    + observation_id + "','" + domain_feature_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * method for inserting a new relationship between procedure and domain feature into the SOS DB.
     * 
     * ATTENTION: Please make sure that the procedure, the procedure_id references on, and the domain feature,
     * the domain_feature_id references on, are already contained in the DB!!
     * 
     * @param procedure_id
     *        String the procedure id (id of a sensor or sensor group)
     * @param domain_feature_id
     *        String of the domain feature id
     * @throws SQLException
     *         if insertion of a relationship failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcDfRelationsship(String procedure_id,
                                          String domain_feature_id,
                                          Connection trCon) throws SQLException, OwsExceptionReport {
        Connection con = null;
        try {

            // insert statement for proc_foi table
            String insertStmt = "INSERT INTO " + PGDAOConstants.procDfTn + " VALUES ('"
                    + procedure_id + "','" + domain_feature_id + "');";

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

}
