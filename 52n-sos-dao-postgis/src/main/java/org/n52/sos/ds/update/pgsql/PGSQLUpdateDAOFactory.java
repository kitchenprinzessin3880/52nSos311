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

import org.apache.log4j.Logger;
import org.n52.sos.ds.AbstractConnectionPool;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.update.IUpdateDAOFactory;
import org.n52.sos.ds.update.IUpdateProcedureDAO;

/**
 * class implements the IUpdateDAOFactory interface and the factory methods for creating the UpdateDAOs.
 * UpdateDAOs could be used for updating data in SOS database.
 * 
 * @author Stephan Kuenster
 * 
 * @version 0.1
 */
public class PGSQLUpdateDAOFactory implements IUpdateDAOFactory {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    private static final Logger log = Logger.getLogger(PGSQLUpdateDAOFactory.class);

    /**
     * constructor which initializes the connectionPool. The ConnectionPool is used by every UpdateDAO to get
     * connections to the SOS DB.
     * 
     * @param handlerp
     *        MemoryHandler for the logger
     * @param logLevelp
     *        Level for logging
     */
    public PGSQLUpdateDAOFactory(AbstractConnectionPool cpool) {
        if (cpool instanceof PGConnectionPool) {
            this.cpool = (PGConnectionPool) cpool;
            log.info("UpdateDAOFactory initialized successfully!!");
        }
        else {
            log.error("UpdateDAOFactory could not be initialized for PostgreSQL, because PGSQL Connection Pool is for another DBMS!");
        }
    }

    /**
     * 
     * @return Returns an IUpdateProcedureDAO
     */
    public IUpdateProcedureDAO getUpdateProcedureDAO() {
        return new UpdateProcedureDAO(cpool);
    }

    /**
     * 
     * @return Returns the ConnectionPool
     */
    public PGConnectionPool getConnectionPool() {
        return cpool;
    }
}
