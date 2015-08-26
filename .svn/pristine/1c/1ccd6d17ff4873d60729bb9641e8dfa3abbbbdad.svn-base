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

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.DelegatingConnection;
import org.n52.sos.ds.AbstractConnectionPool;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Connection Pool for PostgreSQL databases. Class implements the abstract class _ConnectionPool and
 * implements the getNewConnection method.
 * 
 * @author Stephan Kuenster
 * 
 */
public class PGConnectionPool extends AbstractConnectionPool {

    /**
     * constructor with necessary parameters as strings
     * 
     * @param connection
     *        connection url
     * @param user
     *        db username
     * @param password
     *        db password
     * @param driverName
     *        classname of the db driver
     * @param initConnections
     *        number of initial connections
     * @param maxConnections
     *        maximal number of connections in the pool
     */
    public PGConnectionPool(String connection,
                            String user,
                            String password,
                            String driverName,
                            int initConnections,
                            int maxConnections) {

        super(connection, user, password, driverName, initConnections, maxConnections);

        log.info("\n******\nConfig File loaded successfully!\n******\n");
    }

    /**
     * Abstract method returns an available connection from the pool. After the query operation, you have to
     * "give back" the connection to the pool with the returnConnection method!
     * 
     * @return DB Connection to execute the query
     * 
     * @throws OwsExceptionReport
     *         If all connections are in use and no further connection could be established
     */
    public Connection getConnection() throws OwsExceptionReport {

        // pooled connection
        Connection conn;

        // inner connection (postgresql)
        Connection con;

        Class< ? > geomClass;
        Class< ? > boxClass;

        try {
            // get pooled connection
            conn = dataSource.getConnection();

            // int active = dataSource.getNumActive();
            // int idle = dataSource.getNumIdle();
            // log.debug("Connections active: " + active);
            // log.debug("Connections idle: " + idle);

            // get inner connection (postgresql) from pooled connection
            con = ((DelegatingConnection) conn).getInnermostDelegate();

            geomClass = Class.forName("org.postgis.PGgeometry");
            boxClass = Class.forName("org.postgis.PGbox3d");

            // add spatial data types to inner connection
            ((org.postgresql.PGConnection) con).addDataType("geometry", geomClass);
            ((org.postgresql.PGConnection) con).addDataType("box3d", boxClass);

        }
        catch (SQLException sqle) {
            if (dataSource.getNumActive() == dataSource.getMaxActive()) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     null,
                                     "All db connections are in use. Please try again later! "
                                             + sqle.toString());
                log.debug("All database connections are in use. Please retry later! "
                        + sqle.toString());
                throw se;
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                     null,
                                     "Could not get connection from connection pool. Please make sure that your database server is running and configured proper. "
                                             + sqle.toString());
                log.debug("Could not get connection from connection pool. Please make sure that your database server is running and configured proper. "
                        + sqle.toString());
                throw se;
            }
        }
        catch (ClassNotFoundException cnfe) {
            OwsExceptionReport se = new OwsExceptionReport(cnfe);
            log.fatal("Adding geometry types to the connection failed. Please make sure that the PostGIS extension of your PostgreSQLDB is activated: "
                    + cnfe.toString());
            throw se;
        }

        // return new pooled connection
        return conn;
    }

}
