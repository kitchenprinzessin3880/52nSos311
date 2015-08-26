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

import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.n52.sos.ds.AbstractConnectionPool;
import org.n52.sos.ds.IConfigDAO;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IDescribeFeatureTypeDAO;
import org.n52.sos.ds.IGetDomainFeatureDAO;
import org.n52.sos.ds.IGetFeatureOfInterestTimeDAO;
import org.n52.sos.ds.IGetObservationByIdDAO;
import org.n52.sos.ds.IInsertObservationOperationDAO;
import org.n52.sos.ds.IRegisterSensorDAO;
import org.n52.sos.ds.IUpdateSensorDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * DAO factory for PostgreSQL 8.1
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLDAOFactory implements IDAOFactory {

    private static Logger log = Logger.getLogger(PGSQLDAOFactory.class);

    /** ConnectionPool, which contains connections to the DB */
    private PGConnectionPool cpool;

    /**
     * constructor
     * 
     * @param connection
     *        String containing the connection URL
     * @param user
     *        String username for the DB
     * @param password
     *        String password for the DB
     * @param driver
     *        String classname of the DB driver (with packages in front)
     * @param initcon
     *        int number of initial connections contained in the PGConnectionPool
     * @param maxcon
     *        int max number of connections contained in the PGConnectionPool
     */
    public PGSQLDAOFactory(Properties daoProps) throws OwsExceptionReport {

        initializeDAOConstants(daoProps);
        String connection = PGDAOConstants.connectionString;
        String user = PGDAOConstants.user;
        String password = PGDAOConstants.password;
        String driver = PGDAOConstants.driver;
        int initcon = PGDAOConstants.initcon;
        int maxcon = PGDAOConstants.maxcon;
        // initialize PGConnectionPool
        this.cpool = new PGConnectionPool(connection, user, password, driver, initcon, maxcon);
        /**change
    	 * author: juergen sorg
    	 * date: 2013-06-17
    	 * **/
        this.initQualityAssessment(PGDAOConstants.getInstance(daoProps));
        /** change end **/
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     * @throws OwsExceptionReport
     */
    public PGSQLGetCapabilitiesDAO getCapabilitiesDAO() throws OwsExceptionReport {
        PGSQLGetCapabilitiesDAO capsDao = null;
        capsDao = new PGSQLGetCapabilitiesDAO(cpool);
        return capsDao;
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLDescribeObservationTypeDAO getDescribeObservationTypeDAO() {
        // TODO not yet implemented
        return null;
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLDescribeSensorDAO getDescribeSensorDAO() {
        PGSQLDescribeSensorDAO dsDao = new PGSQLDescribeSensorDAO(cpool);
        return dsDao;
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLDescribeFeatureOfInterestDAO getDescribeFeatureOfInterestDAO() {
        // TODO not yet implemented
        return null;
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLGetObservationDAO getObservationDAO() {
        return new PGSQLGetObservationDAO(cpool);
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLGetResultDAO getResultDAO() {
        return new PGSQLGetResultDAO(cpool, new PGSQLGetObservationDAO(cpool));
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public PGSQLGetFeatureOfInterestDAO getFeatureOfInterestDAO() {
        return new PGSQLGetFeatureOfInterestDAO(cpool);
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public IGetDomainFeatureDAO getDomainFeatureDAO() {
        return new PGSQLGetDomainFeatureDAO(cpool);
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public IGetFeatureOfInterestTimeDAO getFeatureOfInterestTimeDAO() {
        return new PGSQLGetFeatureOfInterestTimeDAO(cpool);
    }

    /**
     * method intitializes and returns a PostgreSQLGetTargetFeatureDAO
     * 
     * @param logLevel
     *        Level for logging, parameter is necessary cause the PGSQLConfigDAO is used in the
     *        SosConfigurator's constructor
     * @param handler
     *        MemoryHandler for logging, parameter is necessary cause the PGSQLConfigDAO is used in the
     *        SosConfigurator's constructor
     * @return PostgreSQLGetTargetFeatureDAO DAO for the GetTargetFeature operation
     */
    public IConfigDAO getConfigDAO() {
        return new PGSQLConfigDAO(cpool);
    }

    /**
     * 
     * @return Returns the GetObservationByIdDAO
     */
    public IGetObservationByIdDAO getObservationByIdDAO() {
        return new PGSQLGetObservationByIdDAO(cpool);
    }

    /**
     * initializes the DAOConstants
     * 
     * @param daoProps
     *        Properties created from the dssos.config file
     * @throws OwsExceptionReport
     *         if initializing the DAOConstants failed
     */
    private void initializeDAOConstants(Properties daoProps) throws OwsExceptionReport {
    	PGDAOConstants.getInstance(daoProps);
    }

    /**
     * returns the registerSensor DAO
     * 
     * @return returns the registerSensor DAO
     * 
     */
    public IRegisterSensorDAO getRegisterSensorDAO() {

        return new PGSQLRegisterSensorDAO(cpool);
    }

    /**
     * returns the updateSensor DAO
     * 
     * @return returns the updateSensor DAO
     * 
     */
    public IUpdateSensorDAO getUpdateSensorDAO() {
        return new PGSQLUpdateSensorDAO(cpool);
    }

    /**
     * 
     * @return Returns the insertObservation DAO
     */
    public IInsertObservationOperationDAO getInsertObservationOperationDAO() {
        return new PGSQLInsertObservationOperationDAO(cpool);
    }

    /**
     * method returns connection pool used by this DAO Factory
     * 
     * @return returns Connection Pool implementation used by this DAO factory
     */
    public AbstractConnectionPool getConnectionPool() {
        return this.cpool;
    }

    public void cleanup() {
        try {
            this.finalize();
        }
        catch (Throwable e) {
            log.error("Error while shutting down DAO Factory: " + e.getMessage());
        }
    }

    /**
     * method intitializes and returns a DescribeFeatureTypeDAO
     * 
     * @return DescribeFeatureTypeDAO DAO for the DescribeFeatureType operation
     */
	public IDescribeFeatureTypeDAO getDescribeFeatureTypeDAO() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * change
	 * author: juergen sorg
	 * date: 2013-06-17	 
	 * @throws OwsExceptionReport 
	 * */
	private void initQualityAssessment(PGDAOConstants constants) throws OwsExceptionReport{
		try {
			PGSQLQualityAssessmentConfigurator.getInstance(cpool, constants);
		}  catch (SQLException e) {
			throw new OwsExceptionReport("exception while initialize Quality Assessment cache",e);
		}
	}
}
