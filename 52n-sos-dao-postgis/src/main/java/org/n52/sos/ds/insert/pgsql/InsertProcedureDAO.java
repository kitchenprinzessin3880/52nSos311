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
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ds.insert.IInsertProcedureDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * class implements the IInsertProcedureDAO interface and the methods for inserting procedures into the SOS DB
 * which are defined in this interface.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertProcedureDAO implements IInsertProcedureDAO {

    /** connection pool */
    private PGConnectionPool cpool;
    

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertProcedureDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     */
    public InsertProcedureDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for intserting a new procedure (sensor or sensor group) into the SOS DB.
     * 
     * @param procedure_id
     *        String the procedure id (id of a sensor or sensor group)
     * @param procedure_name
     *        String name of the procedure
     * @param procedure_description
     *        String description of the procedure
     * @param sml_file
     *        complete sensorML description in String representation
     * @param actual_position
     *        last measured position of sensor
     * @param isActive
     *        indicates, whether sensor is currently collecting data (true) or not (false)
     * @param isMobile
     *        indicates, whether sensor is mobile (true) or fixed (false)
     * @throws SQLException
     *         if insertion of a procedure failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcedure(String procedure_id,
                                String description_url,
                                String description_type,
                                String sml_file,
                                Geometry actual_position,
                                boolean isActive,
                                boolean isMobile,
                                Connection trCon) throws SQLException, OwsExceptionReport {

        WKTWriter wktWriter = new WKTWriter();
        String geomWKT = wktWriter.write(actual_position);
        int EPSGid = actual_position.getSRID();
        Connection con = null;
        try {
            // insert statement for procedure table
            String insertProcStmt = "INSERT INTO " + PGDAOConstants.procTn + " VALUES ('"
                    + procedure_id + "','" + description_url + "','" + description_type + "','"
                    + sml_file + "',GeometryFromText('" + geomWKT + "'," + EPSGid + ")," + isActive
                    + "," + isMobile + ");";
            log.debug("Insert procedure statement: " + insertProcStmt);
         
            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }

            Statement stmt = con.createStatement();
            stmt.execute(insertProcStmt);
            
            // insert statement for procedure history table
            String current_date = SosDateTimeUtilities.formatDateTime2IsoString(new DateTime());
            String insertProcHistStmt = "INSERT INTO " + PGDAOConstants.procHistTn + "("
            + PGDAOConstants.procHistProcedureIdCn + ", " + PGDAOConstants.procHistTimeStampCn
            + ", " + PGDAOConstants.procHistPositionCn + ", " + PGDAOConstants.procHistActiveCn
            + ", " + PGDAOConstants.procHistMobileCn + ") VALUES " + "('" + procedure_id
            + "', '" + current_date + "',GeometryFromText('" + geomWKT + "'," + EPSGid + "), " + isActive + ", " + isMobile
            + ");";
//            current_date + "', '" + actual_position + "', " + isActive
            stmt.execute(insertProcHistStmt);
            log.debug("Insert procedure statement: " + insertProcHistStmt);
            
//            CapabilitiesCache.getInstance().refreshSensorMetadata();
            SosConfigurator.getInstance().getCapsCacheController().update(false);
        }
        finally {

            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }

        }
        log.debug("Procedure inserted successfully!");
    }

    /**
     * method for inserting a procedure into the SOS database
     * 
     * @param procedure
     *        Procedure which should be inserted
     * @throws SQLException
     *         if insertion of a procedure failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcedure(SensorSystem procedure, Connection trCon) throws SQLException,
            OwsExceptionReport {
        insertProcedure(procedure.getId(),
                        procedure.getDescriptionURL(),
                        procedure.getDescriptionType(),
                        procedure.getSml_file(),
                        procedure.getActualPosition(),
                        procedure.isActive(),
                        procedure.isMobile(),
                        trCon);
    }

    /**
     * method for inserting multiple procedures into the SOS database
     * 
     * @param procedures
     *        ArrayList with Procedure containing the procedures which should be inserted
     * @throws SQLException
     *         if insertion of a procedure failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertProcedures(ArrayList<SensorSystem> procedures, Connection trCon) throws SQLException,
            OwsExceptionReport {

        // create insert statements
        for (SensorSystem proc : procedures) {
            insertProcedure(proc, trCon);
        }

    }

}
