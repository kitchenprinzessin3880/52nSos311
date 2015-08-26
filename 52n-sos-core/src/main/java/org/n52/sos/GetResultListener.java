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
import org.n52.sos.ds.IGetResultDAO;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetResultRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.ResultResponse;

/**
 * class handles the GetResult request
 * 
 * @author Christoph Stasch
 * 
 */
public class GetResultListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetResultListener.class);

    /** the data access object for the GetResult operation */
    private IGetResultDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getResult.toString();
    
    /**
     * Constructor
     * 
     */
    public GetResultListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getResultDAO());
        log.debug("GetResultListener initialized successfully!");
    }

    /**
     * @return Returns the factory.
     */
    public IGetResultDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IGetResultDAO the specific DAO for this operation (e.g. PostgreSQLGetResultDAO)
     */
    public void setDao(IGetResultDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the GetResult request and sends back a response
     * 
     * @param request
     *        the GetResult request
     * 
     * @return Returns the GetResult response
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {
        ISosResponse response = null;

        // check if document is valid, otherwise return ExceptionResponse
        SosGetResultRequest sosRequest = (SosGetResultRequest) request;
        SosGenericObservation sosGenObs;
        String obsTempID = sosRequest.getObservationTemplateId();
        obsTempID = obsTempID.replace(SosConstants.OBS_TEMP_ID_PREFIX, "");
        try {
            sosGenObs = this.dao.getResult(sosRequest);
            sosGenObs.setObservationID(obsTempID);
            response = new ResultResponse(SosConfigurator.getInstance().getResponseEncoder().createResultRespDoc(sosGenObs));
        }
        catch (OwsExceptionReport se) {
        	return new ExceptionResp(se.getDocument());
        }

        return response;
    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return GetResultListener.OPERATION_NAME;
    }
}
