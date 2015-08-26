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
import org.n52.sos.ds.IUpdateSensorDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosUpdateSensorRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.UpdateSensorResponse;

/**
 * Listener for UpdateSensor operation
 * 
 * @author Christoph Stasch
 * 
 */
public class UpdateSensorListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(UpdateSensorListener.class.getName());

    /** the data access object for the GetObservation operation */
    private IUpdateSensorDAO dao;

    /**
     * constructor
     * 
     */
    public UpdateSensorListener() {
        // get sos configurator
        SosConfigurator configurator = SosConfigurator.getInstance();

        // setting up DAOFactory
        IDAOFactory factory = configurator.getFactory();
        IUpdateSensorDAO sensorDao = null;
        sensorDao = factory.getUpdateSensorDAO();
        setDao(sensorDao);
        log.info("UpdateSensorListener initialized successfully!");
    }

    /**
     * @return Returns operation name
     * 
     */
    public String getOperationName() {
        return SosConstants.Operations.updateSensor.name();
    }

    /**
     * receives internal SOS representation of request for update sensor operation, checks the request
     * parameter and updates sensor parameters in SOS database
     * 
     * @param request
     *        updateSensor request
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;
        SosUpdateSensorRequest upSensorRequest = (SosUpdateSensorRequest) request;
        try {
            this.dao.updateSensor(upSensorRequest);
            response = new UpdateSensorResponse(SosConfigurator.getInstance().getResponseEncoder().createUpdateSensorResponse());
        }
        catch (OwsExceptionReport owse) {
            return new ExceptionResp(owse.getDocument());
        }
        return response;
    }

    /**
     * sets dao to passed UpdateSensorDAO
     * 
     * @param sensorDAO
     *        UpdateSensorDAO
     */
    public void setDao(IUpdateSensorDAO sensorDAO) {
        this.dao = sensorDAO;
    }

}
