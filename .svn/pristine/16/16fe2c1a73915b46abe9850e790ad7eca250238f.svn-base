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
import org.n52.sos.ds.IRegisterSensorDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosRegisterSensorRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.RegisterSensorResponse;

/**
 * Listener for RegisterSensor Operation
 * 
 * @author Christoph Stasch
 * 
 */
public class RegisterSensorListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(RegisterSensorListener.class.getName());

    /** the data access object for the GetObservation operation */
    private IRegisterSensorDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.registerSensor.name();

    /**
     * constructor
     * 
     */
    public RegisterSensorListener() {
        // get sos configurator
        SosConfigurator configurator = SosConfigurator.getInstance();

        // setting up DAOFactory
        IDAOFactory factory = configurator.getFactory();
        IRegisterSensorDAO sensorDao = null;
        sensorDao = factory.getRegisterSensorDAO();
        setDao(sensorDao);
        log.info("RegisterSensorListener initialized successfully!");
    }

    /**
     * @return Returns OperationName
     */
    public String getOperationName() {
        return OPERATION_NAME;
    }

    /**
     * receive a request of a registerSensor request and returns the corresponding response
     * 
     * @param request
     *        registerSensor request
     * @return Returns RegisterSensorResponse or ExceptionReport, if registering of sensor failed
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {
        ISosResponse response = null;
        SosRegisterSensorRequest regSensorRequest = (SosRegisterSensorRequest) request;
        try {
            String assignedSensorId = this.dao.insertSensor(regSensorRequest);
            response = new RegisterSensorResponse(SosConfigurator.getInstance().getResponseEncoder().createRegisterSensorResponse(assignedSensorId));
        }
        catch (OwsExceptionReport owse) {
            return new ExceptionResp(owse.getDocument());
        }
        return response;
    }

    /**
     * sets dao to passed RegisterSensorDAO
     * 
     * @param sensorDAO
     *        RegisterSensorDAO
     */
    public void setDao(IRegisterSensorDAO sensorDAO) {
        this.dao = sensorDAO;
    }

}
