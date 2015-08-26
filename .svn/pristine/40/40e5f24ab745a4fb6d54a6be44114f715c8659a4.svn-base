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
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetObservationByIdDAO;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetObservationByIdRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.ObservationByIdResponse;

/**
 * class receives GetObservationById requests, queries the observation from the database and returns the
 * requested observation
 * 
 * @author Christoph Stasch
 * 
 */
public class GetObservationByIdListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetObservationByIdListener.class.getName());

    /** the data access object for the GetObservation operation */
    private IGetObservationByIdDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getObservationById.name();

    /**
     * Constructor
     * 
     */
    public GetObservationByIdListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getObservationByIdDAO());
        log.info("GetObservationByIdListener initialized successfully!");
    } // end constructor

    /**
     * @return the dao
     */
    public IGetObservationByIdDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        the dao to set
     */
    public void setDao(IGetObservationByIdDAO dao) {
        this.dao = dao;
    }

    /**
     * 
     * @return Returns the name of the operation, this listeners handles
     */
    public String getOperationName() {
        return OPERATION_NAME;
    }

    /**
     * receives request and returns the requested observation
     * 
     * @param request
     * @return ObservationByIdResponse
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {
        ISosResponse response = null;

        try {
            SosGetObservationByIdRequest sosRequest = (SosGetObservationByIdRequest) request;

            SosObservationCollection obsCollection = this.dao.getObservationById(sosRequest);
            XmlObject xb_obs = SosConfigurator.getInstance().getOmEncoder().createObservationCollection(obsCollection);

            boolean zipCompression = false;
            if (sosRequest.getResponseFormat() != null) {
                zipCompression = Util4Listeners.checkResponseFormat(sosRequest.getResponseFormat());
            }

            response = new ObservationByIdResponse(xb_obs, zipCompression);

        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }

        return response;
    }// end receiveRequest
}
