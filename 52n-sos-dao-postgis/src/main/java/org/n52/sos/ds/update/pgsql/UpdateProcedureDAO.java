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

package org.n52.sos.ds.update.pgsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ds.update.IUpdateProcedureDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * class implements the IUpdateProcedureDAO interface and the methods for updating procedures in
 * the SOSDB which are defined in this interface.
 * 
 * @author Stephan Kuenster
 * 
 * @version 0.1
 */
public class UpdateProcedureDAO implements IUpdateProcedureDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(UpdateProcedureDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     * @param sosURLp
     *        url of the SOS for which data should be inserted/updated (necessary for updating the capabilities cache)
     */
    public UpdateProcedureDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * 
     */
    public void updateProcedure(String procedure_id,
                                Geometry actual_position,
                                boolean active,
                                boolean mobile,
                                Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = trCon;
        try {

            // update statement
            String query = createUpdateStatement(procedure_id, actual_position, active, mobile);
            if (con == null) {
            	con = cpool.getConnection();
            }
            Statement stmt = con.createStatement();
            stmt.execute(query);

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while updating the data in the database: "
                    + sqle.toString());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
    }

    public String createUpdateStatement(String procedure_id,
                                        Geometry actual_position,
                                        boolean active,
                                        boolean mobile) {

        WKTWriter wktWriter = new WKTWriter();
        String geomWKT = wktWriter.write(actual_position);
        int EPSGid = actual_position.getSRID();

        // update statement
        String query = "UPDATE " + PGDAOConstants.procTn + " SET "
                + PGDAOConstants.actualPositionCn + " = GeometryFromText('" + geomWKT + "',"
                + EPSGid + "), " + PGDAOConstants.activeCn + " = " + active + ", "
                + PGDAOConstants.mobileCn + " = " + mobile + " WHERE " + PGDAOConstants.procIDCn
                + " = '" + procedure_id + "';";
        return query;
    }

}
