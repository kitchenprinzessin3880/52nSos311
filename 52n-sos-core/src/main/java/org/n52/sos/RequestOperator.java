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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.Operations;
import org.n52.sos.decode.IHttpGetRequestDecoder;
import org.n52.sos.decode.IHttpPostRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosDescribeFeatureTypeRequest;
import org.n52.sos.request.SosDescribeSensorRequest;
import org.n52.sos.request.SosGetCapabilitiesRequest;
import org.n52.sos.request.SosGetDomainFeatureRequest;
import org.n52.sos.request.SosGetFeatureOfInterestRequest;
import org.n52.sos.request.SosGetFeatureOfInterestTimeRequest;
import org.n52.sos.request.SosGetObservationByIdRequest;
import org.n52.sos.request.SosGetObservationRequest;
import org.n52.sos.request.SosGetResultRequest;
import org.n52.sos.request.SosInsertObservationRequest;
import org.n52.sos.request.SosRegisterSensorRequest;
import org.n52.sos.request.SosUpdateSensorRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;

/**
 * This class contains the different Listeners which are registered through the config file. After parsing the
 * request through the doOperation() method, the request is send up to the specific Listener (e.g.
 * GetCapabilitiesListener)
 * 
 * @author Christoph Stasch
 * 
 */
public class RequestOperator {

    /** container of the requestListener */
    private HashMap<String, ISosRequestListener> reqListener;

    /** the logger, used to log exceptions and additonaly information */
    private static Logger log = Logger.getLogger(RequestOperator.class);

    /**decoder for http post requests*/
    private IHttpPostRequestDecoder httpPostDecoder;

    /**decoder for http get requests*/
    private IHttpGetRequestDecoder httpGetDecoder;

    /**
     * constructor
     *
     */
    public RequestOperator() {
        this.httpPostDecoder = SosConfigurator.getInstance().getHttpPostDecoder();
        this.httpGetDecoder = SosConfigurator.getInstance().getHttpGetDecoder();
    }

    /**
     * adds a requestListener to the listener collection
     * 
     * @param listener
     *        the requestListener which should be added
     */
    public void addRequestListener(ISosRequestListener listener) {
        if (reqListener == null) {
            reqListener = new HashMap<String, ISosRequestListener>();
        }
        reqListener.put(listener.getOperationName(), listener);
    }

    /**
     * removes a requestListener from the listener collection
     * 
     * @param listener
     *        the requestListener which should be removed
     * 
     * @return ISosRequestListener the listener which is removed (null if erasure failed or listener is not
     *         contained)
     */
    public ISosRequestListener removeReqListener(ISosRequestListener listener) {
        if (reqListener == null) {
            return null;
        }
        reqListener.remove(listener.getOperationName());
        return listener;
    }

    /**
     * doOperation implementation for HttpGetRequest
     * 
     * @param queryString
     *        the queryString of the HttpGetRequest
     * 
     * @return Returns ISosResponse which contains either the Capabilities document of this SOS or an
     *         ExceptionReport if the request was invalid or the operation failed.
     */
    public ISosResponse doGetOperation(HttpServletRequest req) {
        
    	ISosResponse response = null;
        AbstractSosRequest request = null;
        
        try {
            request = httpGetDecoder.receiveRequest(req);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }
        
        ISosRequestListener requestListener = null;

        // getCapabilities request
        if (request instanceof SosGetCapabilitiesRequest) {
        	requestListener = (GetCapabilitiesListener) reqListener.get(Operations.getCapabilities.toString());
        }
        // describeSensor request
        else if (request instanceof SosDescribeSensorRequest) {
            requestListener = (DescribeSensorListener) reqListener.get(Operations.describeSensor.toString());
        }
        // getObservation request
        else if (request instanceof SosGetObservationRequest) {
            requestListener = (GetObservationListener) reqListener.get(Operations.getObservation.toString());
        }
        
        // receive request
        if (requestListener != null) {
	        response = requestListener.receiveRequest(request);
	        if ( ! (response instanceof ExceptionResp)) {
	            log.debug(requestListener.getOperationName() + " operation executed successfully!");
	        }	
        } else {
        	OwsExceptionReport se = new OwsExceptionReport();
        	se.addCodedException(OwsExceptionReport.ExceptionCode.OperationNotSupported, null,
        			"Please add the missing Listener into the pom.xml (new deploy) or into the sos.config (reload webapp), you find in your webapp/WEB-INF/conf.");
        	log.error("Listener is unknown!", se);
        	return new ExceptionResp(se.getDocument());
        }
        
        return response;
    }

    /**
     * parses the request, creates an IServiceRequest and sent it up to the specific listener
     * 
     * @param requestString
     *        the post request as String
     * 
     * @return the serviceResponse to the request
     * 
     * 
     */
    public ISosResponse doPostOperation(String requestString) {

        // ////////////////////////////////////////////////////////////
        // //Unmarshalling with XMLBeans (parsing request)
        // ////////////////////////////////////////////////////////////
        ISosResponse response = null;

        AbstractSosRequest request = null;

        try {
            request = httpPostDecoder.receiveRequest(requestString);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }
        
        ISosRequestListener requestListener = null;

        // getCapabilities request
        if (request instanceof SosGetCapabilitiesRequest) {
        	requestListener = (GetCapabilitiesListener) reqListener.get(Operations.getCapabilities.toString());
        }

        // getObservation request
        else if (request instanceof SosGetObservationRequest) {
        	requestListener = (GetObservationListener) reqListener.get(Operations.getObservation.toString());
        }

        //getObservationById request
        else if (request instanceof SosGetObservationByIdRequest) {
            requestListener = (GetObservationByIdListener) reqListener.get(Operations.getObservationById.toString());
        }

        // describeSensor request
        else if (request instanceof SosDescribeSensorRequest) {
            requestListener = (DescribeSensorListener) reqListener.get(Operations.describeSensor.toString());
        }

        // registerSensor request
        else if (request instanceof SosRegisterSensorRequest) {
            requestListener = (RegisterSensorListener) reqListener.get(Operations.registerSensor.toString());
        }

        // insertObservation request
        else if (request instanceof SosInsertObservationRequest) {
        	requestListener = (InsertObservationListener) reqListener.get(Operations.insertObservation.toString());
        }

        // getResult request
        else if (request instanceof SosGetResultRequest) {
        	requestListener = (GetResultListener) reqListener.get(Operations.getResult.toString());
        }

        // getFeatureOfInterest request
        else if (request instanceof SosGetFeatureOfInterestRequest) {
        	requestListener = (GetFeatureOfInterestListener) reqListener.get(Operations.getFeatureOfInterest.toString());
        }

        // getFeatureOfInterestTime request
        else if (request instanceof SosGetFeatureOfInterestTimeRequest) {
        	requestListener = (GetFeatureOfInterestTimeListener) reqListener.get(Operations.getFeatureOfInterestTime.toString());
        }

        // getDomainFeature request
        else if (request instanceof SosGetDomainFeatureRequest) {
        	requestListener = (GetDomainFeatureListener) reqListener.get(Operations.getDomainFeature.toString());
        }

        // updateSensor request
        else if (request instanceof SosUpdateSensorRequest) {
        	requestListener = (UpdateSensorListener) reqListener.get(Operations.updateSensor.toString());
        }
        
        // describeFeatureType
        else if (request instanceof SosDescribeFeatureTypeRequest) {
        	requestListener = (DescribeFeatureTypeListener) reqListener.get(Operations.describeFeatureOfInterest.toString());
		}

        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "The request was sent in an unknown format or is invalid! Please use the SOS version 1.0 schemata to build your request and validate the requests before sending them to the SOS!");
            log.error("Request is unknown! ", se);
            return new ExceptionResp(se.getDocument());
        }
        
        if (requestListener != null) {
	        response = requestListener.receiveRequest(request);
	        if ( ! (response instanceof ExceptionResp)) {
	            log.debug(requestListener.getOperationName() + " operation executed successfully!");
	        }	
        } else {
        	OwsExceptionReport se = new OwsExceptionReport();
        	se.addCodedException(OwsExceptionReport.ExceptionCode.OperationNotSupported, null,
        			"Please add the missing Listener into the pom.xml (new deploy) or into the sos.config (reload webapp), you find in your webapp/WEB-INF/conf.");
        	log.error("Listener is unknown!", se);
        	return new ExceptionResp(se.getDocument());
        }


        return response;
    }

    /**
     * @return Returns HashMap containing the requestListeners.
     */
    public HashMap<String, ISosRequestListener> getReqListener() {
        return reqListener;
    }

    /**
     * @param reqListener
     *        The reqListener to set.
     */
    public void setReqListener(HashMap<String, ISosRequestListener> reqListener) {
        this.reqListener = reqListener;
    }
}
