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

package org.n52.sos.ds;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Pool for DB connections. To get a connection, use the getConnection() method. Instead of invoking the
 * connection.close() method, call the returnConnection(connection) method of this pool, to deblock the
 * connection for other queries. The pool is initialized with the INITCON number of the sosconfig file. If
 * this number is arrived a new connection is created. The maximal number of active connections in the pool is
 * also defined in the sosconfig file. If the max number is arrived, an exception is thrown and the client has
 * to iterate its request a short time later.
 * 
 * @author Stephan Kuenster
 * 
 */
public abstract class AbstractConnectionPool {

    /** the logger, used to log exceptions and additonaly information */
    protected static Logger log = Logger.getLogger(AbstractConnectionPool.class);

    /** data source */
    protected BasicDataSource dataSource = new BasicDataSource();

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
    public AbstractConnectionPool(String connection,
                                  String user,
                                  String password,
                                  String driverName,
                                  int initConnections,
                                  int maxConnections) {

        dataSource.setDriverClassName(driverName);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setUrl(connection);

        // max connections active
        dataSource.setMaxActive(maxConnections);
        dataSource.setMaxIdle(maxConnections);

        // initial size of connection pool
        dataSource.setInitialSize(initConnections);

        dataSource.setMaxWait(5000);

        // TODO: further configuration
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);
        
        // important! allow access to underlying connection
        dataSource.setAccessToUnderlyingConnectionAllowed(true);

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
    public abstract Connection getConnection() throws OwsExceptionReport;

    /**
     * Invoke this method after executing the query with this connection, so that the connection is already
     * available in the pool
     * 
     * @param conn
     *        the connection which was used and now is available again
     * 
     * @throws OwsExceptionReport
     *         If closing the connection failed
     */
    public void returnConnection(Connection conn) throws OwsExceptionReport {
        try {
            conn.close();
            // log.debug("Connections active after close: " + dataSource.getNumActive());
            // log.debug("Connections idle after close: " + dataSource.getNumIdle());
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.fatal("Error closing active connection: " + sqle.toString());
            throw se;
        }
    }
}
