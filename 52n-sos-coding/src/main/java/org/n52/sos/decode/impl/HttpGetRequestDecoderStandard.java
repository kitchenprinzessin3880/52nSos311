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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.decode.IHttpGetRequestDecoder;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosDescribeSensorRequest;
import org.n52.sos.request.SosGetCapabilitiesRequest;
import org.n52.sos.request.SosGetObservationRequest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

/**
 * class offers parsing method to create a SOSOperationRequest, which encapsulates the request parameters,
 * from a String. The different SosOperationRequest classes are useful, because XmlBeans generates no useful
 * documentation and handling of substitution groups is not simple. So it may be easier for foreign developers
 * to implement the DAO implementation classes for other data sources then PGSQL databases.
 * 
 * @author Stephan Knster
 * 
 */
public class HttpGetRequestDecoderStandard implements IHttpGetRequestDecoder {

    /**
     * logger
     */
    private static Logger log = Logger.getLogger(HttpGetRequestDecoderStandard.class);

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
                else if (request.getParameter(paramName) != null
                        && request.getParameter(paramName).equalsIgnoreCase(SosConstants.Operations.describeSensor.toString())) {
                    response = parseDescribeSensorRequest(request);
                }
                else if (request.getParameter(paramName) != null
                        && request.getParameter(paramName).equalsIgnoreCase(SosConstants.Operations.getObservation.toString())) {
                    response = parseGetObservationRequest(request);
                }
            }
        }

        if ( ! (response instanceof SosGetCapabilitiesRequest)
                && ! (response instanceof SosDescribeSensorRequest)
                && ! (response instanceof SosGetObservationRequest)) {
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
     * parses the HttpServletRequest representing the describeSensor request and creates a SosDescribeSensor
     * request
     * 
     * @param request
     *        HttpServletRequest, which contains the request parameters
     * @return SosDescribeSensorRequest
     * @throws OwsExceptionReport
     *         if parsing of request fails
     */
    @SuppressWarnings("unchecked")
    private SosDescribeSensorRequest parseDescribeSensorRequest(HttpServletRequest request) throws OwsExceptionReport {

        SosDescribeSensorRequest sdsRequest = new SosDescribeSensorRequest();

        boolean foundProcedure = false;
        boolean foundOutputFormat = false;
        boolean foundService = false;
        boolean foundVersion = false;

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            // procedure
            if (paramName.equalsIgnoreCase(SosConstants.DescribeSensorParams.procedure.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sdsRequest.setProcedures(paramValues[0]);
                    foundProcedure = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.procedure.name(),
                                         "The value of parameter "
                                                 + SosConstants.DescribeSensorParams.procedure.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // outputFormat
            else if (paramName.equalsIgnoreCase(SosConstants.DescribeSensorParams.outputFormat.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sdsRequest.setOutputFormat(paramValues[0]);
                    foundOutputFormat = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.outputFormat.name(),
                                         "The value of parameter "
                                                 + SosConstants.DescribeSensorParams.outputFormat.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // service
            else if (paramName.equalsIgnoreCase(SosConstants.DescribeSensorParams.service.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1
                        && paramValues[0].equalsIgnoreCase(SosConstants.SOS)) {
                    sdsRequest.setService(paramValues[0]);
                    foundService = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.service.name(),
                                         "The value of parameter "
                                                 + SosConstants.DescribeSensorParams.service.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // version
            else if (paramName.equalsIgnoreCase(SosConstants.DescribeSensorParams.version.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1
                        && paramValues[0].equalsIgnoreCase(SosConstants.SERVICEVERSION)) {
                    sdsRequest.setVersion(paramValues[0]);
                    foundVersion = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.DescribeSensorParams.version.name(),
                                         "The value of parameter "
                                                 + SosConstants.DescribeSensorParams.version.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            else if ( !paramName.equalsIgnoreCase(SosConstants.REQUEST)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     paramName,
                                     "The parameter " + paramName
                                             + " is not supported by this SOS.");
                throw se;
            }
        }

        if ( !foundProcedure) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.DescribeSensorParams.procedure.name(),
                                 "Your request was invalid! The parameter PROCEDURE must be contained in your request!");
            throw se;
        }

        if ( !foundOutputFormat) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.DescribeSensorParams.outputFormat.name(),
                                 "Your request was invalid! The parameter OUTPUTFORMAT must be contained in your request!");
            throw se;
        }

        if ( !foundService) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.DescribeSensorParams.service.name(),
                                 "Your request was invalid! The parameter SERVICE must be contained in your request!");
            throw se;
        }

        if ( !foundVersion) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.DescribeSensorParams.version.name(),
                                 "Your request was invalid! The parameter VERSION must be contained in your request!");
            throw se;
        }

        return sdsRequest;
    }

    /**
     * parses the HttpServletRequest representing the getObservation request and creates a SosGetObservation
     * request
     * 
     * @param request
     *        HttpServletRequest, which contains the request parameters
     * @return SosGetObservationRequest
     * @throws OwsExceptionReport
     *         if parsing of request fails
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    private SosGetObservationRequest parseGetObservationRequest(HttpServletRequest request) throws OwsExceptionReport {

        SosGetObservationRequest sgoRequest = new SosGetObservationRequest();

        boolean foundOffering = false;
        boolean foundObservedProperty = false;
        boolean foundResponseFormat = false;
        boolean foundService = false;
        boolean foundVersion = false;

        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);

            // offering
            if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.offering.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sgoRequest.setOffering(paramValues[0]);
                    foundOffering = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.offering.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.offering.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // observedProperty
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.observedProperty.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()) {
                    sgoRequest.setObservedProperty(paramValues[0].split(","));
                    foundObservedProperty = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.observedProperty.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.observedProperty.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // service
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.service.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1
                        && paramValues[0].equalsIgnoreCase(SosConstants.SOS)) {
                    sgoRequest.setService(paramValues[0]);
                    foundService = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.service.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.service.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // version
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.version.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1
                        && paramValues[0].equalsIgnoreCase(SosConstants.SERVICEVERSION)) {
                    sgoRequest.setVersion(paramValues[0]);
                    foundVersion = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.version.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.version.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // srsName
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.srsName.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sgoRequest.setSrsName(paramValues[0]);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.srsName.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.srsName.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // eventTime
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.eventTime.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()) {

                    String[] tFilter = paramValues[0].replace(" ", "+").split(",");

                    TemporalFilter[] filter = new TemporalFilter[tFilter.length];
                    List<TemporalFilter> filterList = new ArrayList<TemporalFilter>();

                    for (String eventTime : tFilter) {

                        String[] times = eventTime.split("/");

                        if (times.length == 1) {

                            DateTime instant = SosDateTimeUtilities.parseIsoString2DateTime(times[0]);
                            TimeInstant ti = new TimeInstant();
                            ti.setValue(instant);

                            TemporalFilter tf = new TemporalFilter(TimeOperator.TM_Equals, ti);
                            filterList.add(tf);

                        }
                        else if (times.length == 2) {

                            DateTime start = SosDateTimeUtilities.parseIsoString2DateTime(times[0]);
                            DateTime end = SosDateTimeUtilities.parseIsoString2DateTime(times[1]);
                            TimePeriod tp = new TimePeriod();
                            tp.setStart(start);
                            tp.setEnd(end);

                            TemporalFilter tf = new TemporalFilter(TimeOperator.TM_Equals, tp);
                            filterList.add(tf);

                        }
                        else {
                            OwsExceptionReport se = new OwsExceptionReport();
                            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                                 SosConstants.GetObservationParams.eventTime.name(),
                                                 "The value of parameter "
                                                         + SosConstants.GetObservationParams.eventTime.name()
                                                         + " (" + request.getParameter(paramName)
                                                         + ") is invalid.");
                            throw se;
                        }

                    }

                    filter = filterList.toArray(new TemporalFilter[0]);

                    sgoRequest.setEventTime(filter);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.eventTime.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.eventTime.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // BBOX
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.BBOX.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length >= 4) {

                    SpatialFilter sf = new SpatialFilter();

                    WKTReader wktReader = new WKTReader();
                    Geometry geom = null;
                    try {
                        geom = wktReader.read("MULTIPOINT(" + paramValues[0].split(",")[0] + " "
                                + paramValues[0].split(",")[1] + ", "
                                + paramValues[0].split(",")[2] + " " + paramValues[0].split(",")[3]
                                + ")");
                    }
                    catch (com.vividsolutions.jts.io.ParseException pe) {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             GetObservationParams.featureOfInterest.toString(),
                                             "An error occurred, while parsing the BBOX parameter: "
                                                     + pe.getMessage());
                        log.error("Error while parsing the geometry of BBOX parameter: "
                                + se.getMessage());
                        throw se;
                    }

                    if (paramValues[0].split(",").length == 5) {
                        geom.setSRID(parseSrsName(paramValues[0].split(",")[4]));
                    }
                    else {
                        geom.setSRID(4326);
                    }

                    sf.setGeometry(geom);
                    sf.setOperator(SpatialOperator.BBOX);
                    sgoRequest.setFeatureOfInterest(sf);

                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.BBOX.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.BBOX.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // procedure
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.procedure.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()) {
                    sgoRequest.setProcedure(paramValues[0].split(","));
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.procedure.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.procedure.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // featureOfInterest
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.featureOfInterest.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()) {
                    SpatialFilter sf = new SpatialFilter();
                    sf.setFoiIDs(paramValues[0].split(","));
                    sgoRequest.setFeatureOfInterest(sf);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.featureOfInterest.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.featureOfInterest.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // responseMode
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.responseMode.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sgoRequest.setResponseMode(paramValues[0]);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.responseMode.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.responseMode.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // responseFormat
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.responseFormat.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sgoRequest.setResponseFormat(paramValues[0]);
                    foundResponseFormat = true;
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.responseFormat.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.responseFormat.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            // resultModel
            else if (paramName.equalsIgnoreCase(SosConstants.GetObservationParams.resultModel.toString())) {
                if (paramValues.length == 1 && !paramValues[0].isEmpty()
                        && paramValues[0].split(",").length == 1) {
                    sgoRequest.setResponseFormat(paramValues[0]);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         SosConstants.GetObservationParams.resultModel.name(),
                                         "The value of parameter "
                                                 + SosConstants.GetObservationParams.resultModel.name()
                                                 + " (" + request.getParameter(paramName)
                                                 + ") is invalid.");
                    throw se;
                }
            }

            else if ( !paramName.equalsIgnoreCase(SosConstants.REQUEST)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     paramName,
                                     "The parameter " + paramName
                                             + " is not supported by this SOS.");
                throw se;
            }
        }

        if ( !foundOffering) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.GetObservationParams.offering.name(),
                                 "Your request was invalid! The parameter OFFERING must be contained in your request!");
            throw se;
        }

        if ( !foundObservedProperty) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.GetObservationParams.observedProperty.name(),
                                 "Your request was invalid! The parameter OBSERVEDPROPERTY must be contained in your request!");
            throw se;
        }

        if ( !foundService) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.GetObservationParams.service.name(),
                                 "Your request was invalid! The parameter SERVICE must be contained in your request!");
            throw se;
        }

        if ( !foundVersion) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.GetObservationParams.version.name(),
                                 "Your request was invalid! The parameter VERSION must be contained in your request!");
            throw se;
        }

        if ( !foundResponseFormat) {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 SosConstants.GetObservationParams.responseFormat.name(),
                                 "Your request was invalid! The parameter RESPONSEFORMAT must be contained in your request!");
            throw se;
        }

        return sgoRequest;
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
    private SosGetCapabilitiesRequest parseGetCapabilitiesRequest(String capString) throws OwsExceptionReport {

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

    /**
     * parses the srsName and returns an integer representing the number of the EPSG-code of the passed
     * srsName
     * 
     * @param srsName
     *        name of the spatial reference system in EPSG-format (withn urn identifier for EPSG)
     * @return Returns an integer representing the number of the EPSG-code of the passed srsName
     * @throws OwsExceptionReport
     *         if parsing the srsName failed
     */
    private int parseSrsName(String srsName) throws OwsExceptionReport {
        int srid = Integer.MIN_VALUE;
        String srsNamePrefix = SosConfigurator.getInstance().getSrsNamePrefix();
        if ( ! (srsName == null || srsName.equals(""))) {
            srsName = srsName.replace(srsNamePrefix, "");
            try {
                srid = new Integer(srsName).intValue();
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.featureOfInterest.toString(),
                                     "For geometry of the feature of interest parameter has to have a srsName attribute, which contains the Srs Name as EPSGcode following the following schema:"
                                             + srsNamePrefix + "number!");
                log.error("Error while parsing srsName of featureOfInterest parameter: "
                        + se.getMessage());
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.featureOfInterest.toString(),
                                 "For geometry of the feature of interest parameter has to have a srsName attribute, which contains the Srs Name as EPSGcode following the following schema:"
                                         + srsNamePrefix + "number!");
            log.error("Error while parsing srsName of featureOfInterest parameter: "
                    + se.getMessage());
            throw se;
        }
        return srid;
    }// end parseSrsName
}
