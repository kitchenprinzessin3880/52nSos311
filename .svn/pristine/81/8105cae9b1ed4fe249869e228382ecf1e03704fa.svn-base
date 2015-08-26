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

import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IDescribeObservationTypeDAO;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.resp.ISosResponse;

/**
 * class handles the DescribeObservationType request
 * 
 * @author Christoph Stasch
 * 
 */
public class DescribeObservationTypeListener implements ISosRequestListener {

    /** the data access object factory, used to build the specific dao */
    private IDescribeObservationTypeDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = "DescribeObservationType";

    /**
     * Constructor
     * 
     */
    public DescribeObservationTypeListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getDescribeObservationTypeDAO());
    }

    /**
     * @return Returns the factory.
     */
    public IDescribeObservationTypeDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IDescribeObservationTypeDAO
     */
    public void setDao(IDescribeObservationTypeDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the DescribeObservationType request and sends back a repsonse
     * 
     * @param request
     *        the DescribeObservationType request
     * 
     * @return Returns the DescribeObservationType response
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {
        return null;
        // TODO implement in order to support the DescribeObservationType operation. this will be done in a
        // future release

    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return DescribeObservationTypeListener.OPERATION_NAME;
    }

}
