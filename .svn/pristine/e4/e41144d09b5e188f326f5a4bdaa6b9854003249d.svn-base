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
import org.n52.sos.ds.insert.IInsertPhenomenonDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.om.SosCompositePhenomenon;
import org.n52.sos.ogc.om.SosPhenomenon;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class implements the IInsertObservationDAO interface and the methods for inserting observations into the
 * SOSDB which are defined in this interface.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertPhenomenonDAO implements IInsertPhenomenonDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertPhenomenonDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     */
    public InsertPhenomenonDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for inserting new phenomena into the SOS DB.
     * 
     * @param phenomenon_id
     *        String id of the phenomenon
     * @param phenomenon_description
     *        String description of the phenomenon
     * @param unit
     *        String unit of the phenomenon (e.g. 'cm' or 'degree')
     * @param applicationLink
     *        String application link of the phenomenon
     * @param valueType
     *        String the type of the values which belong to the new phenomenon
     * @param compPhenId
     *        String the id of the composite phenomenon, this phenomenon is part of
     * @throws SQLException
     *         if insertion of a phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertPhenomenon(String phenomenon_id,
                                 String phenomenon_description,
                                 String unit,
                                 String applicationLink,
                                 String valueType,
                                 String compPhenId,
                                 Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {
            // insert statement
            String insertStmt = createInsertStatement4Phenomenon(phenomenon_id,
                                                                 phenomenon_description,
                                                                 unit,
                                                                 applicationLink,
                                                                 valueType,
                                                                 compPhenId);
            log.trace(insertStmt);

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
     * method for inserting phenomena into the DB
     * 
     * @param phenomena
     *        Phenomenon[] which contains the phenomena which should be inserted
     * @throws SQLException
     *         if insertion of a phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertPhenomena(ArrayList<SosPhenomenon> phenomena, Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {

            StringBuffer insertStmt = new StringBuffer();
            String singleInsert = null;

            for (SosPhenomenon phenomenon : phenomena) {
                singleInsert = createInsertStatement4Phenomenon(phenomenon.getPhenomenonID(),
                                                                phenomenon.getPhenomenonDescription(),
                                                                phenomenon.getUnit(),
                                                                phenomenon.getApplicationSchemaLink(),
                                                                phenomenon.getValueType(),
                                                                phenomenon.getCompPhenId());
                insertStmt.append(singleInsert);
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt.toString());

            // TODO refresh metadata of phenomena in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * method for inserting new phenomenon into the SOS DB
     * 
     * @param phen
     *        Phenomenon which should be inserted
     * @throws SQLException
     *         if insertion of a phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertPhenomenon(SosPhenomenon phen, Connection trCon) throws SQLException,
            OwsExceptionReport {
        insertPhenomenon(phen.getPhenomenonID(),
                         phen.getPhenomenonDescription(),
                         phen.getUnit(),
                         phen.getApplicationSchemaLink(),
                         phen.getValueType(),
                         phen.getCompPhenId(),
                         trCon);
    }

    /**
     * method for inserting a composite phenomenon into the database
     * 
     * @param compPhenId
     *        id of the composite phenomenon
     * @param compPhenDesc
     *        description of the composite phenomenon
     * @param phenomenonComponents
     *        phenomenon components of the composite phenomenon
     * @throws SQLException
     *         if insertion of a composite phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertCompositePhenomenon(String compPhenId,
                                          String compPhenDesc,
                                          ArrayList<SosPhenomenon> phenomenonComponents,
                                          Connection trCon) throws SQLException, OwsExceptionReport {
        insertCompositePhenomenon(compPhenId, compPhenDesc, trCon);
        insertPhenomena(phenomenonComponents, trCon);
    }

    /**
     * method for inserting a composite phenomenon into database
     * 
     * @param compPhenId
     *        id of the composite phenomenon which should be inserted
     * @param compPhenDesc
     *        description of the composite phenomenon which should be inserted
     * @throws SQLException
     *         if insertion of a composite phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertCompositePhenomenon(String compPhenId, String compPhenDesc, Connection trCon) throws SQLException,
            OwsExceptionReport {
        Connection con = null;
        try {
            // insert statement
            String insertStmt = "INSERT INTO " + PGDAOConstants.compPhenTn + " VALUES ('"
                    + compPhenId + "','" + compPhenDesc + "');";
            log.trace(insertStmt);
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(insertStmt);

            // TODO refresh metadata of composite phenomenon in capabilities cache
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }
    }

    /**
     * method for inserting a composite phenomenon into the SOS database
     * 
     * @param compPhen
     *        The composite phenomenon which should be inserted
     * @throws SQLException
     *         if insertion of a composite phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertCompositePhenomenon(SosCompositePhenomenon compPhen, Connection trCon) throws SQLException,
            OwsExceptionReport {

        insertCompositePhenomenon(compPhen.getCompPhenId(), compPhen.getCompPhenDesc(), trCon);
        if (compPhen.getPhenomenonComponents() != null) {
            insertPhenomena(compPhen.getPhenomenonComponents(), trCon);
        }
    }

    /**
     * method for inserting composite phenomena into the database
     * 
     * @param compPhens
     *        collection of the composite phenomena, which should be inserted
     * @throws SQLException
     *         if insertion of a composite phenomenon failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertCompositePhenomena(ArrayList<SosCompositePhenomenon> compPhens,
                                         Connection trCon) throws SQLException, OwsExceptionReport {
        for (SosCompositePhenomenon compPhen : compPhens) {
            insertCompositePhenomenon(compPhen, trCon);
        }
    }

    /**
     * creates insert statement
     * 
     * @param phenomenon_id
     *        id of the phenomenon
     * @param phenomenon_description
     *        description of the phenomenon
     * @param unit
     *        unit of the phenomenon
     * @param applicationLink
     *        link of the phenomenon
     * @param valueType
     *        value type of the phenomenon
     * @param compPhenId
     *        id of the composite phenomenon the phenomenon accords to
     * @return Returns insertStatement
     */
    private String createInsertStatement4Phenomenon(String phenomenon_id,
                                                    String phenomenon_description,
                                                    String unit,
                                                    String applicationLink,
                                                    String valueType,
                                                    String compPhenId) {
        String insertStmt = "INSERT INTO " + PGDAOConstants.phenTn + " VALUES ('" + phenomenon_id
                + "','" + phenomenon_description + "','" + unit + "','" + valueType;
        if (compPhenId != null && !compPhenId.equals("")) {
            insertStmt += "','" + compPhenId;
        }

        insertStmt += "');";
        return insertStmt;
    }

}
