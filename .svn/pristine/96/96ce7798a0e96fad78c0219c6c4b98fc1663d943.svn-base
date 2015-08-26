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

package org.n52.sos.decode.impl;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.SosConstants;
import org.n52.sos.decode.IHttpGetRequestDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetCapabilitiesRequest;

/**
 * class offers parsing method to create a SOSOperationRequest, which encapsulates the request parameters,
 * from a String. The different SosOperationRequest classes are useful, because XmlBeans generates no useful
 * documentation and handling of substitution groups is not simple. So it may be easier for foreign developers
 * to implement the DAO implementation classes for other data sources then PGSQL databases.
 * 
 * @author Stephan Knster
 * 
 */
public class HttpGetRequestDecoderMobile implements IHttpGetRequestDecoder {

    /**
     * method receives request and returns internal SOS representation of request
     * 
     * @param request
     *        HttpServletRequest, which contains the request parameters
     * @return AbstractSosRequest The internal SOS representation of request
     * @throws OwsExceptionReport
     *         if parsing of request fails
     */
    @SuppressWarnings("unchecked")
    public AbstractSosRequest receiveRequest(HttpServletRequest request) throws OwsExceptionReport {

        AbstractSosRequest response = null;

        String requestString = "";

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {

            String paramName = (String) paramNames.nextElement();

            if (paramName.equalsIgnoreCase(SosConstants.REQUEST)) {

                requestString = paramName;

                // getCapabilities request
                if (request.getParameter(paramName) != null
                        && request.getParameter(paramName).equalsIgnoreCase(SosConstants.Operations.getCapabilities.toString())) {
                    response = parseGetCapabilitiesRequest(request.getQueryString());
                }
            }
        }

        if ( ! (response instanceof SosGetCapabilitiesRequest)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 SosConstants.REQUEST,
                                 "The GET request " + request.getParameter(requestString)
                                         + " is not supported by this SOS.");
            throw se;
        }

        return response;
    }

    /**
     * parses the String representing the getCapabilities request and creates a SosGetCapabilities request
     * 
     * @param capString
     *        String with getCapabilities parameters
     * @return Returns SosGetCapabilitiesRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the String failed
     */
    public SosGetCapabilitiesRequest parseGetCapabilitiesRequest(String capString) throws OwsExceptionReport {

        SosGetCapabilitiesRequest request = new SosGetCapabilitiesRequest();

        // check length of queryString
        if (capString != null && capString.length() != 0) {

            // split queryString into the different parameterNames and values
            String[] params = capString.split("&");

            // if less than 2 parameters, throw exception!
            if (params.length >= 2) {

                // parse the values of the parameters
                for (String param : params) {
                    String[] nameAndValue = param.split("=");
                    if (nameAndValue.length > 1) {
                        if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapabilitiesParams.service.name())) {
                            request.setService(nameAndValue[1]);
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapabilitiesParams.AcceptVersions.name())) {
                            request.setAcceptVersions(nameAndValue[1].split(","));
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapGetParams.REQUEST.name())) {
                            request.setRequest(nameAndValue[1]);
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapGetParams.ACCEPTFORMATS.name())) {
                            request.setAcceptFormats(nameAndValue[1].split(","));
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapGetParams.UPDATESEQUENCE.name())) {
                            request.setUpdateSequence(nameAndValue[1]);
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapGetParams.SECTIONS.name())) {
                            request.setSections(nameAndValue[1].split(","));
                        }
                        else if (nameAndValue[0].equalsIgnoreCase(SosConstants.GetCapGetParams.MOBILEENABLED.name())) {
                            if (nameAndValue[1].equalsIgnoreCase("true")) {
                                request.setMobileEnabled(true);
                            }
                        }
                    }
                    else {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                             SosConstants.GetCapabilitiesParams.service.name(),
                                             "The value of parameter " + nameAndValue[0]
                                                     + " was missing.");
                        throw se;
                    }
                }

            }

            else {

                boolean foundService = false;

                for (String param : params) {
                    if (param.contains(SosConstants.GetCapGetParams.SERVICE.name())) {
                        foundService = true;
                    }
                }

                if ( !foundService) {
                    OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
                    se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                         SosConstants.GetCapabilitiesParams.service.name(),
                                         "Your request was invalid! The parameter SERVICE must be contained in your request!");
                    throw se;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
                    se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                         SosConstants.GetCapGetParams.REQUEST.name(),
                                         "Your request was invalid! The parameter REQUEST must be contained in your request!");
                    throw se;
                }
            }

        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                 null,
                                 "Your request was invalid!");
            throw se;
        }

        return request;

    } // end parseGetCapabilitiesRequest
}
