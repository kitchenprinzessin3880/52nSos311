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

import org.apache.log4j.Logger;
import org.n52.sos.ds.AbstractConnectionPool;
import org.n52.sos.ds.insert.IInsertDAOFactory;
import org.n52.sos.ds.insert.IInsertDomainFeatureDAO;
import org.n52.sos.ds.insert.IInsertFoiDAO;
import org.n52.sos.ds.insert.IInsertObservationDAO;
import org.n52.sos.ds.insert.IInsertOfferingDAO;
import org.n52.sos.ds.insert.IInsertPhenomenonDAO;
import org.n52.sos.ds.insert.IInsertProcedureDAO;
import org.n52.sos.ds.insert.IInsertRelationshipsDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;

/**
 * class implements the IInsertDAOFactory interface and the factory methods for creating the InsertDAOs.
 * InsertDAOs could be used for inserting data into SOS database.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class PGSQLInsertDAOFactory implements IInsertDAOFactory {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    private static final Logger log = Logger.getLogger(PGSQLInsertDAOFactory.class);

    /**
     * constructor which initializes the connectionPool. The ConnectionPool is used by every InsertDAO to get
     * connections to the SOS DB.
     * 
     * @param handlerp
     *        MemoryHandler for the logger
     * @param logLevelp
     *        Level for logging
     */
    public PGSQLInsertDAOFactory(AbstractConnectionPool cpool) {
        if (cpool instanceof PGConnectionPool) {
            this.cpool = (PGConnectionPool) cpool;
            log.info("InsertDAOFactory initialized successfully!!");
        }
        else {
            log.error("InsertDAOFactory could not be initialized for PostgreSQL, because PGSQL Connection Pool is for another DBMS!");
        }
    }

    /**
     * 
     * @return Returns an IInsertFoiDAO
     */
    public IInsertFoiDAO getInsertFoiDAO() {
        return new InsertFoiDAO(cpool);
    }

    /**
     * 
     * @return Returns an IInsertObservationDAO
     */
    public IInsertObservationDAO getInsertObservationDAO() {
        return new InsertObservationDAO(cpool);
    }

    /**
     * 
     * @return Returns an IInsertOfferingDAO
     */
    public IInsertOfferingDAO getInsertOfferingDAO() {
        return new InsertOfferingDAO(cpool);
    }

    /**
     * 
     * @return Returns an IInsertPhenomenonDAO
     */
    public IInsertPhenomenonDAO getInsertPhenomenonDAO() {
        return new InsertPhenomenonDAO(cpool);
    }

    /**
     * 
     * @return Returns an IInsertProcedureDAO
     */
    public IInsertProcedureDAO getInsertProcedureDAO() {
        return new InsertProcedureDAO(cpool);
    }

    /**
     * 
     * @return Returns an IInsertProcPhenDAO
     */
    public IInsertRelationshipsDAO getInsertRelationshipsDAO() {
        return new InsertRelationshipsDAO(cpool);
    }

    /**
     * 
     * @return Returns the ConnectionPool
     */
    public PGConnectionPool getConnectionPool() {
        return cpool;
    }

    /**
     * 
     * @return Returns an IInsertDomainFeatureDAO
     */
    public IInsertDomainFeatureDAO getInsertDomainFeatureDAO() {
        return new InsertDomainFeatureDAO(cpool);
    }
}
