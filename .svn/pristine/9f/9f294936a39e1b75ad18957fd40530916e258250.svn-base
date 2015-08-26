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

package org.n52.sos;

import org.apache.log4j.Logger;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IDescribeFeatureTypeDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosDescribeFeatureTypeRequest;
import org.n52.sos.resp.DescribeFeatureTypeResponse;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;

/**
 * class handles the DescribeFeatureOfInterest request
 * 
 * @author Christoph Stasch
 * 
 */
public class DescribeFeatureTypeListener implements ISosRequestListener {

    /** the data access object for the DescribeFeatureOfInterest operation */
    private IDescribeFeatureTypeDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.describeFeatureOfInterest.name();

    /** logger */
    private static Logger log = Logger.getLogger(DescribeFeatureTypeListener.class);
    
    /**
     * Constructor
     * 
     * 
     */
    public DescribeFeatureTypeListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getDescribeFeatureTypeDAO());
    }

    /**
     * @return Returns the factory.
     */
    public IDescribeFeatureTypeDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IDescribeTargetFeatureDAO the dao for this operation
     */
    public void setDao(IDescribeFeatureTypeDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the DescribeTargetFeature request and sends back a repsonse
     * 
     * @param request
     *        the DescribeTargetFeature request
     * 
     * @return Returns the DescribeTargetFeature response
     * @throws OwsExceptionReport 
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {
    	DescribeFeatureTypeResponse response = null;
    	SosDescribeFeatureTypeRequest req = (SosDescribeFeatureTypeRequest) request;
    	// TODO: request is not defined !?!
    	try {
    		response = new DescribeFeatureTypeResponse(this.dao.getFeatureOfInterestDescription(req), false);
		} catch (OwsExceptionReport owse) {
			 return new ExceptionResp(owse.getDocument());
		}
        return response;

    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return DescribeFeatureTypeListener.OPERATION_NAME;
    }

}
