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

import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the operations
 * (e.g. GetCapabilitiesDAO)
 * 
 * @author Christoph Stasch
 * 
 */
public interface IDAOFactory {

    /**
     * 
     * @return Returns the GetCapabilitiesDAO
     * @throws OwsExceptionReport
     */
    public IGetCapabilitiesDAO getCapabilitiesDAO() throws OwsExceptionReport;

    /**
     * 
     * @return Returns the DescribeObservationTypeDAO
     */
    public IDescribeObservationTypeDAO getDescribeObservationTypeDAO();

    /**
     * 
     * @return Returns the DescribeSensorDAO
     */
    public IDescribeSensorDAO getDescribeSensorDAO();
    
    /**
     * 
     * @return Returns the DescribeTargetFeatureTypeDAO
     */
    public IDescribeFeatureTypeDAO getDescribeFeatureTypeDAO();

    /**
     * 
     * @return Returns the DescribeTargetFeatureDAO
     */
    public IDescribeFeatureOfInterestDAO getDescribeFeatureOfInterestDAO();

    /**
     * 
     * @return Returns the GetObservationDAO
     */
    public IGetObservationDAO getObservationDAO();

    /**
     * 
     * @return Returns the GetObservationByIdDAO
     */
    public IGetObservationByIdDAO getObservationByIdDAO();

    /**
     * 
     * @return Returns the GetResultDAO
     */
    public IGetResultDAO getResultDAO();

    /**
     * 
     * @return Returns the GetResultDAO
     */
    public IRegisterSensorDAO getRegisterSensorDAO();

    /**
     * 
     * @return Returns the GetResultDAO
     */
    public IUpdateSensorDAO getUpdateSensorDAO();

    /**
     * 
     * @return Returns the GetTargetFeatureDAO
     */
    public IGetFeatureOfInterestDAO getFeatureOfInterestDAO();

    /**
     * 
     * @return Returns the GetFeatureOfInterestTimeDAO
     */
    public IGetFeatureOfInterestTimeDAO getFeatureOfInterestTimeDAO();

    /**
     * 
     * @return Returns the GetDomainFeatureDAO
     */
    public IGetDomainFeatureDAO getDomainFeatureDAO();

    /**
     * Returns configDAO
     * 
     * @return Returns the ConfigDAO
     */
    public IConfigDAO getConfigDAO();

    /**
     * Returns DAO for insertObservation operation
     * 
     * @return Returns DAO for insertObservation operation
     */
    public IInsertObservationOperationDAO getInsertObservationOperationDAO();

    /**
     * method returns connection pool used by this DAO Factory
     * 
     * @return returns Connection Pool implementation used by this DAO factory
     */
    public AbstractConnectionPool getConnectionPool();

    /**
     * Cleanup when the DAO factory is no longer needed (e.g. servlet undeploy)
     */
    public void cleanup();

}
