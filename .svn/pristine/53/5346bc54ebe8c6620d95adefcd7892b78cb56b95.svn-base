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
import org.n52.sos.ds.insert.IInsertOfferingDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.om.SosOffering;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class implements the IInsertObservationDAO interface and the methods for inserting observations into the
 * SOSDB which are defined in this interface.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertOfferingDAO implements IInsertOfferingDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertOfferingDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     * 
     */
    public InsertOfferingDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for inserting new offerings.
     * 
     * @param offering_id
     *        String id of the offering
     * @param offering_name
     *        String the name of the offering
     * @param min_time
     *        min_time for observations which belong to the offering; if no observations are contained yet,
     *        insert null!
     * @param max_time
     *        max_time for observations which belong to the offering; if no observations are contained yet,
     *        insert null!
     * @throws SQLException
     *         if insertion of an offering failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertOffering(String offering_id,
                               String offering_name,
                               String min_time,
                               String max_time,
                               Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {
            StringBuffer insertStmt = new StringBuffer();
            // insert statement for the offering table
            insertStmt.append("INSERT INTO offering VALUES ('" + offering_id + "','"
                    + offering_name + "', ");

            // check if min_time/max_time is null and append corresponding
            // values
            if (min_time == null) {
                insertStmt.append(" null, ");
            }
            else {
                insertStmt.append("'" + min_time + "',");
            }
            if (max_time == null) {
                insertStmt.append(" null);");
            }
            else {
                insertStmt.append("'" + max_time + "');");
            }
            insertStmt.append("ANALYZE;");
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt.toString());


            // TODO refresh offering metadata of capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a new offering
     * 
     * @param offering
     *        Offering which should be inserted
     * @throws SQLException
     *         if insertion of an offering failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertOffering(SosOffering offering, Connection trCon) throws SQLException,
            OwsExceptionReport {
        insertOffering(offering.getOfferingID(),
                       offering.getOfferingName(),
                       offering.getMinTime(),
                       offering.getMaxTime(),
                       trCon);
    }

    /**
     * method for inserting new offerings
     * 
     * @param offerings
     *        ArrayList containing the offerings which should be inserted
     * @throws SQLException
     *         if insertion of an offering failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertOfferings(ArrayList<SosOffering> offerings, Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {

            StringBuffer insertBuffer = new StringBuffer();
            for (SosOffering offering : offerings) {
                insertBuffer.append(createInsertStatement(offering.getOfferingID(),
                                                          offering.getOfferingName(),
                                                          offering.getMinTime(),
                                                          offering.getMaxTime()));
            }

            insertBuffer.append("ANALYZE;");
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertBuffer.toString());

        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * creates the insert statement
     * 
     * @param offering_id
     *        if of the offering
     * @param offering_name
     *        name of the offering
     * @param min_time
     *        min_time for observations which belong to the offering; if no observations are contained yet,
     *        insert null!
     * @param max_time
     *        max_time for observations which belong to the offering; if no observations are contained yet,
     *        insert null!
     * @return Returns insert statement
     */
    private String createInsertStatement(String offering_id,
                                         String offering_name,
                                         String min_time,
                                         String max_time) {

        StringBuffer insertStmt = new StringBuffer();
        // insert statement for the offering table
        insertStmt.append("INSERT INTO offering (" + PGDAOConstants.offeringIDCn + ", "
                + PGDAOConstants.offNameCn + ", " + PGDAOConstants.minTimeCn + ", "
                + PGDAOConstants.maxTimeCn + ") " + " VALUES ('" + offering_id + "','"
                + offering_name + "', ");

        if (min_time == null) {
            insertStmt.append(" null, ");
        }
        else {
            insertStmt.append("'" + min_time + "',");
        }
        if (max_time == null) {
            insertStmt.append(" null);");
        }
        else {
            insertStmt.append("'" + max_time + "');");
        }

        return insertStmt.toString();
    }
}