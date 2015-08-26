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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractGeometryType;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.TimePositionType;
import net.opengis.ogc.BBOXType;
import net.opengis.ogc.BinaryComparisonOpType;
import net.opengis.ogc.BinarySpatialOpType;
import net.opengis.ogc.ComparisonOpsType;
import net.opengis.ogc.ExpressionType;
import net.opengis.ogc.LiteralType;
import net.opengis.ogc.LowerBoundaryType;
import net.opengis.ogc.PropertyIsBetweenType;
import net.opengis.ogc.PropertyIsLikeType;
import net.opengis.ogc.PropertyIsNullType;
import net.opengis.ogc.PropertyNameType;
import net.opengis.ogc.SpatialOpsType;
import net.opengis.ogc.UpperBoundaryType;
import net.opengis.om.x10.CategoryObservationDocument;
import net.opengis.om.x10.CategoryObservationType;
import net.opengis.om.x10.GeometryObservationDocument;
import net.opengis.om.x10.GeometryObservationType;
import net.opengis.om.x10.MeasurementDocument;
import net.opengis.om.x10.MeasurementType;
import net.opengis.om.x10.ObservationType;
import net.opengis.ows.x11.GetCapabilitiesType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sos.x10.DescribeFeatureTypeDocument;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetDomainFeatureDocument;
import net.opengis.sos.x10.GetFeatureOfInterestDocument;
import net.opengis.sos.x10.GetFeatureOfInterestTimeDocument;
import net.opengis.sos.x10.GetObservationByIdDocument;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetResultDocument;
import net.opengis.sos.x10.InsertObservationDocument;
import net.opengis.sos.x10.RegisterSensorDocument;
import net.opengis.sos.x10.UpdateSensorDocument;
import net.opengis.sos.x10.DescribeFeatureTypeDocument.DescribeFeatureType;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor.Time;
import net.opengis.sos.x10.GetDomainFeatureDocument.GetDomainFeature;
import net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest;
import net.opengis.sos.x10.GetFeatureOfInterestTimeDocument.GetFeatureOfInterestTime;
import net.opengis.sos.x10.GetObservationByIdDocument.GetObservationById;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.DomainFeature;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.EventTime;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.FeatureOfInterest;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.Result;
import net.opengis.sos.x10.GetResultDocument.GetResult;
import net.opengis.sos.x10.InsertObservationDocument.InsertObservation;
import net.opengis.sos.x10.ObservationTemplateDocument.ObservationTemplate;
import net.opengis.sos.x10.RegisterSensorDocument.RegisterSensor;
import net.opengis.sos.x10.RegisterSensorDocument.RegisterSensor.SensorDescription;
import net.opengis.sos.x10.UpdateSensorDocument.UpdateSensor;
import net.opengis.swe.x101.PositionType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.decode.IHttpPostRequestDecoder;
import org.n52.sos.decode.impl.utilities.GMLDecoder;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.sensorML.SensorSystem;
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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * class offers parsing method to create a SOSOperationRequest, which encapsulates the request parameters,
 * from a GetOperationDocument (XmlBeans generated Java class representing the request) The different
 * SosOperationRequest classes are useful, because XmlBeans generates no useful documentation and handling of
 * substitution groups is not simple. So it may be easier for foreign developers to implement the DAO
 * implementation classes for other data sources then PGSQL databases.
 * 
 * @author Christoph Stasch
 * 
 */
public class HttpPostRequestDecoderMobile implements IHttpPostRequestDecoder {

    /**
     * logger, used for logging while initializing the constants from config file
     */
    private static Logger log = Logger.getLogger(HttpPostRequestDecoderMobile.class);

    /**
     * Feature Decoder
     */
    private FeatureDecoder featureDecoder = new FeatureDecoder();
    
	/**
	 * O&M Decoder
	 */
	private OMDecoder omDecoder = new OMDecoder();
    
    /**
     * method receives request and returns internal SOS representation of request
     * 
     * @param docString
     *        string, which contains the request document
     * @return Returns internal SOS representation of request
     * @throws OwsExceptionReport
     *         if parsing of request fails
     */
    public AbstractSosRequest receiveRequest(String docString) throws OwsExceptionReport {
        AbstractSosRequest response = null;
        XmlObject doc;
        try {
            doc = XmlObject.Factory.parse(docString);
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while parsing xml request!", xmle);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "RequestOperator.doOperation()",
                                 "An xml error occured when parsing the request: XML Exception: "
                                         + xmle.getMessage());

            throw se;
        }

        log.info("REQUESTTYPE:" + doc.getClass());

        // getCapabilities request
        if (doc instanceof GetCapabilitiesDocument) {
            GetCapabilitiesDocument capsDoc = (GetCapabilitiesDocument) doc;
            response = parseGetCapabilitiesRequest(capsDoc);
        }

        // getObservation request
        else if (doc instanceof GetObservationDocument) {
            GetObservationDocument obsDoc = (GetObservationDocument) doc;
            response = parseGetObservationRequest(obsDoc);
        }

        // getObservationById request
        else if (doc instanceof GetObservationByIdDocument) {
            GetObservationByIdDocument obsDoc = (GetObservationByIdDocument) doc;
            response = parseGetObsByIdRequest(obsDoc);
        }

        // describeSensor request
        else if (doc instanceof DescribeSensorDocument) {
            DescribeSensorDocument obsDoc = (DescribeSensorDocument) doc;
            response = parseDescribeSensorRequest(obsDoc);
        }

        // getResult request
        else if (doc instanceof GetResultDocument) {
            GetResultDocument obsDoc = (GetResultDocument) doc;
            response = parseGetResultRequest(obsDoc);
        }

        // getFeatureOfInterest request
        else if (doc instanceof GetFeatureOfInterestDocument) {
            GetFeatureOfInterestDocument obsDoc = (GetFeatureOfInterestDocument) doc;
            response = parseGetFoiRequest(obsDoc);
        }

        // getFeatureOfInterestTime request
        else if (doc instanceof GetFeatureOfInterestTimeDocument) {
            GetFeatureOfInterestTimeDocument obsDoc = (GetFeatureOfInterestTimeDocument) doc;
            response = parseGetFoiTimeRequest(obsDoc);
        }

        // getDomainFeature request
        else if (doc instanceof GetDomainFeatureDocument) {
            GetDomainFeatureDocument obsDoc = (GetDomainFeatureDocument) doc;
            response = parseGetDomainFeatureRequest(obsDoc);
        }

        // insertObservation request
        else if (doc instanceof InsertObservationDocument) {
            InsertObservationDocument insertObsDoc = (InsertObservationDocument) doc;
            response = parseInsertObsRequest(insertObsDoc);
        }

        // describeFeatureOfInterest request
        else if (doc instanceof DescribeFeatureTypeDocument) {
        	DescribeFeatureTypeDocument descFeatTypeDoc = (DescribeFeatureTypeDocument) doc;
        	response = parseDescFeatTypeRequest(descFeatTypeDoc);
//            OwsExceptionReport se = new OwsExceptionReport();
//            se.addCodedException(OwsExceptionReport.ExceptionCode.OperationNotSupported,
//                                 SosConstants.Operations.describeFeatureOfInterest.name(),
//                                 "The requested Operation "
//                                         + SosConstants.Operations.describeFeatureOfInterest.name()
//                                         + "is not supported by this SOS.");
//            throw se;
        }

        // RegisterSensor request
        else if (doc instanceof RegisterSensorDocument) {
            RegisterSensorDocument regSensDoc = (RegisterSensorDocument) doc;
            response = parseRegisterSensorDocument(regSensDoc);
        }

        // UpdateSensor request
        else if (doc instanceof UpdateSensorDocument) {
            UpdateSensorDocument usDoc = (UpdateSensorDocument) doc;
            response = parseUpdateSensorDocument(usDoc);
        }

        return response;
    }

    /**
     * parses the passes XmlBeans document and creates a SOS InsertObservation request
     * 
     * @param xb_insertObsDoc
     *        XmlBeans document of InsertObservation request
     * @return Returns SOS representation of InsertObservation request
     * @throws OwsExceptionReport
     *         if parsing of request or parameter in Observation, which should be inserted, is incorrect
     */
    public AbstractSosRequest parseInsertObsRequest(InsertObservationDocument xb_insertObsDoc) throws OwsExceptionReport {
        InsertObservation xb_insertObs = xb_insertObsDoc.getInsertObservation();
        String assignedSensorID = xb_insertObs.getAssignedSensorId();

        boolean mobileEnabled = xb_insertObs.getMobileEnabled();

        ObservationType xb_obsType = xb_insertObs.getObservation();

        Collection<AbstractSosObservation> obsCol = null;
        if (xb_obsType instanceof MeasurementType) {
            obsCol = omDecoder.parseMeasurement((MeasurementType) xb_obsType, mobileEnabled);
        }
        else if (xb_obsType instanceof CategoryObservationType) {
        	obsCol = omDecoder.parseCategoryObservation((CategoryObservationType) xb_obsType,
        												mobileEnabled);
        }
        else if (xb_obsType instanceof GeometryObservationType) {
            obsCol = omDecoder.parseSpatialObservation((GeometryObservationType) xb_obsType,
                                                      	mobileEnabled);
        }
        else if (xb_obsType instanceof ObservationType) { // Generic Observation
            obsCol = omDecoder.parseGenericObservation(xb_obsType, mobileEnabled);
        }
        return new SosInsertObservationRequest(assignedSensorID, obsCol, mobileEnabled);
    }

    /**
     * parses the passes XmlBeans document and creates a SOS describeSensor request
     * 
     * @param xb_descSensDoc
     *        XmlBeans document representing the describeSensor request
     * @return Returns SOS describeSensor request
     * @throws OwsExceptionReport
     *         if validation of the request failed
     */
    public AbstractSosRequest parseDescribeSensorRequest(DescribeSensorDocument xb_descSensDoc) throws OwsExceptionReport {

        // validate document
        validateDocument(xb_descSensDoc);

        SosDescribeSensorRequest descSensorRequest = new SosDescribeSensorRequest();
        DescribeSensor xb_descSensor = xb_descSensDoc.getDescribeSensor();
        descSensorRequest.setService(xb_descSensor.getService());
        descSensorRequest.setVersion(xb_descSensor.getVersion());

        descSensorRequest.setMobileEnabled(xb_descSensor.getMobileEnabled());

        // check, whether Temporal Filter is set
        if (xb_descSensor.getTimeArray().length != 0) {
            descSensorRequest.setTime(parseTime(xb_descSensor.getTimeArray()));
        }

        descSensorRequest.setProcedures(xb_descSensor.getProcedure());

        if (xb_descSensor.getOutputFormat() != null && !xb_descSensor.getOutputFormat().equals("")) {
            descSensorRequest.setOutputFormat(xb_descSensor.getOutputFormat());
        }
        else {
            descSensorRequest.setOutputFormat(SosConstants.PARAMETER_NOT_SET);
        }
        return descSensorRequest;
    }

    /**
     * parses a RegisterSensorDocument and returns a SosRegisterSensorRequest
     * 
     * @param xb_regSensDoc
     *        the XMLBeans document of the RegisterSensor request
     * @return Returns SosRegisterSensorRequest
     * @throws OwsExceptionReport
     *         if request is incorrect or not valid
     */
    public SosRegisterSensorRequest parseRegisterSensorDocument(RegisterSensorDocument xb_regSensDoc) throws OwsExceptionReport {

        // validateDocument(xb_regSensDoc);

        SosRegisterSensorRequest request = null;

        RegisterSensor xb_regSens = xb_regSensDoc.getRegisterSensor();
        SensorDescription xb_sensDesc = xb_regSens.getSensorDescription();
        ObservationTemplate xb_obsTemplate = xb_regSens.getObservationTemplate();
        
        // check whether observation template is measurement
        try {
            MeasurementDocument.Factory.parse(xb_obsTemplate.toString());
        }
        catch (XmlException xmle) {

        	// check whether observation template is categoryObservation
        	try {
        		CategoryObservationDocument.Factory.parse(xb_obsTemplate.toString());
        	}
        	catch (XmlException xmle1) {
        	
	        	// check whether observation template is geometryObservation
	        	try {
	        		GeometryObservationDocument.Factory.parse(xb_obsTemplate.toString());
	        	}
	        	catch (XmlException xmle2) {
	        		OwsExceptionReport se = new OwsExceptionReport();
	                se.addCodedException(ExceptionCode.InvalidParameterValue,
	                                     null,
	                                     "52North SOS currently only allows measurements, category observations and geometry observations to be inserted!! Parsing of Measurement, CategoryObservation and GeometryObservation template failed because: "
	                                             + xmle2.getLocalizedMessage());
	                throw se;
	        	}
        	}
        	
        }

        // parse
        SensorMLDocument xb_sensorML = null;
        SystemType xb_system = null;
        String smlFile = "";
        try {
            xb_sensorML = SensorMLDocument.Factory.parse(xb_sensDesc.toString());
            smlFile = xb_sensorML.xmlText();
            Member[] xb_memberArray = xb_sensorML.getSensorML().getMemberArray();
            if (xb_memberArray.length == 1) {
                AbstractProcessType xb_proc = xb_memberArray[0].getProcess();
                if (xb_proc instanceof SystemType) {
                    xb_system = (SystemType) xb_proc;
                }
            }
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 null,
                                 "52North SOS currently only allows sml:Systems to be registered!! Parsing of sml:System failed because: "
                                         + xmle.getLocalizedMessage());
            throw se;
        }
        SensorSystem sensorSystem = SensorMLDecoder.parseSystem(xb_system, smlFile);
        String systemDescription = xb_sensDesc.toString();

        SosAbstractFeature domFeat = null;
        Collection<SosAbstractFeature> df_col = null;

        // parse domain features if mobile request
        if (xb_regSens.getMobileEnabled()) {
            FeaturePropertyType[] xb_domainFeatures = xb_regSens.getDomainFeatureArray();
            if (xb_domainFeatures.length > 0) {
                df_col = new ArrayList<SosAbstractFeature>();
                for (FeaturePropertyType xb_df : xb_domainFeatures) {
                    domFeat = featureDecoder.parseGenericDomainFeature(xb_df);
                    df_col.add(domFeat);
                }
            }
        }

        request = new SosRegisterSensorRequest(sensorSystem,
                                               sensorSystem.getOutputs(),
                                               systemDescription,
                                               df_col,
                                               xb_regSens.getMobileEnabled());
        return request;
    }

    /**
     * parses the XMLBeans representation of the UpdateSensorDocument and creates an SosUpdateSensorRequest
     * 
     * @param xb_usDoc
     *        XMLBeans representation of the UpdateSensor request
     * @return Returns SOSmobile representation of the UpdateSensor request
     * @throws OwsExceptionReport
     *         if validation of request failed
     */
    public AbstractSosRequest parseUpdateSensorDocument(UpdateSensorDocument xb_usDoc) throws OwsExceptionReport {

        // validateDocument(xb_usDoc);

        SosUpdateSensorRequest updateSensorRequest = null;
        UpdateSensor xb_us = xb_usDoc.getUpdateSensor();

        Point point = null;
        SosAbstractFeature domFeat = null;

        String procID = xb_us.getSensorID();
        TimePositionType time = xb_us.getTimeStamp().getTimePosition();
        boolean mobile = xb_us.getIsMobile();
        boolean active = xb_us.getIsActive();

        PositionType xb_pos = xb_us.getPosition();
        if (xb_pos != null) {
            point = SensorMLDecoder.parsePointPosition(xb_pos);
        }

        FeaturePropertyType xb_fpt = xb_us.getDomainFeature();
        if (xb_fpt != null) {
            domFeat = featureDecoder.parseGenericDomainFeature(xb_fpt);
        }

        updateSensorRequest = new SosUpdateSensorRequest(procID, time, point, mobile, active, domFeat);

        return updateSensorRequest;
    }

    /**
     * parses the passes XmlBeans document and creates a SOS getFeatureOfInterest request
     * 
     * @param xb_getFoiDoc
     *        XmlBeans document representing the getFeatureOfInterest request
     * @return Returns SOS getFeatureOfInterest request
     * @throws OwsExceptionReport
     *         if validation of the request failed
     */
    public AbstractSosRequest parseGetFoiRequest(GetFeatureOfInterestDocument xb_getFoiDoc) throws OwsExceptionReport {

        // validate request
        validateDocument(xb_getFoiDoc);

        SosGetFeatureOfInterestRequest getFoiRequest = new SosGetFeatureOfInterestRequest();
        GetFeatureOfInterest xb_getFoi = xb_getFoiDoc.getGetFeatureOfInterest();

        getFoiRequest.setVersion(xb_getFoi.getVersion());
        getFoiRequest.setService(xb_getFoi.getService());

        if (xb_getFoi.getFeatureOfInterestIdArray() != null
                && xb_getFoi.getFeatureOfInterestIdArray().length != 0) {
            getFoiRequest.setFeatureIDs(xb_getFoi.getFeatureOfInterestIdArray());
        }

        if (xb_getFoi.getLocation() != null) {
            getFoiRequest.setLocation(parseLocation(xb_getFoi.getLocation()));
        }

        if (xb_getFoi.getEventTimeArray() != null && xb_getFoi.getEventTimeArray().length != 0) {
            // TODO delete Exception and delete the comment sign if functionality is supported.
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "EventTime",
                                 "The eventTime parameter is not supported for GetFeatureOfInterest!");
            throw se;
//            getFoiRequest.setEventTimes(parseEventTime4GetFoi((net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest.EventTime[]) xb_getFoi.getEventTimeArray()));
        }

        return getFoiRequest;
    }

    /**
     * parses the passes XmlBeans document and creates a SOS getDomainFeature request
     * 
     * @param xb_getDomainFeatureDoc
     *        XmlBeans document representing the getFeatureOfInterest request
     * @return Returns SOS getFeatureOfInterest request
     * @throws OwsExceptionReport
     *         if validation of the request failed
     */
    public AbstractSosRequest parseGetDomainFeatureRequest(GetDomainFeatureDocument xb_getDomainFeatureDoc) throws OwsExceptionReport {

        // validate request
        validateDocument(xb_getDomainFeatureDoc);

        SosGetDomainFeatureRequest getDomainFeatureRequest = new SosGetDomainFeatureRequest();
        GetDomainFeature xb_getDomainFeature = xb_getDomainFeatureDoc.getGetDomainFeature();

        getDomainFeatureRequest.setVersion(xb_getDomainFeature.getVersion());
        getDomainFeatureRequest.setService(xb_getDomainFeature.getService());

        if (xb_getDomainFeature.getDomainFeatureIdArray() != null
                && xb_getDomainFeature.getDomainFeatureIdArray().length != 0) {
            getDomainFeatureRequest.setDomainFeatureIDs(xb_getDomainFeature.getDomainFeatureIdArray());
        }

        if (xb_getDomainFeature.getLocation() != null) {
            getDomainFeatureRequest.setLocation(parseDomainFeatureLocation(xb_getDomainFeature.getLocation()));
        }

        if (xb_getDomainFeature.getEventTimeArray() != null
                && xb_getDomainFeature.getEventTimeArray().length != 0) {
            // TODO delete Exception and delete the comment sign if functionality is supported.
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "EventTime",
                                 "The eventTime parameter is not supported for GetDomainFeature!");
            throw se;
//            getDomainFeatureRequest.setEventTimes(parseEventTime4GetDomainFeature((net.opengis.sos.x10.GetDomainFeatureDocument.GetDomainFeature.EventTime[]) xb_getDomainFeature.getEventTimeArray()));
        }

        return getDomainFeatureRequest;
    }

    /**
     * parses the passes XmlBeans document and creates a SOS getFeatureOfInterestTime request
     * 
     * @param xb_getFoiTimeDoc
     *        XmlBeans document representing the getFeatureOfInterestTime request
     * @return Returns SOS getFeatureOfInterestTime request
     * @throws OwsExceptionReport
     *         if validation of the request failed
     */
    public AbstractSosRequest parseGetFoiTimeRequest(GetFeatureOfInterestTimeDocument xb_getFoiTimeDoc) throws OwsExceptionReport {

        // validate request
        validateDocument(xb_getFoiTimeDoc);

        SosGetFeatureOfInterestTimeRequest getFoiTimeRequest = new SosGetFeatureOfInterestTimeRequest();
        GetFeatureOfInterestTime xb_getFoiTime = xb_getFoiTimeDoc.getGetFeatureOfInterestTime();

        getFoiTimeRequest.setVersion(xb_getFoiTime.getVersion());
        getFoiTimeRequest.setService(xb_getFoiTime.getService());

        getFoiTimeRequest.setMobileEnabled(xb_getFoiTime.getMobileEnabled());

        if (xb_getFoiTime.getOffering() != null && xb_getFoiTime.getOffering().length() != 0) {
            getFoiTimeRequest.setOffering(xb_getFoiTime.getOffering());
        }

        if (xb_getFoiTime.getDomainFeature() != null) {
            getFoiTimeRequest.setDomainFeature(xb_getFoiTime.getDomainFeature());
            if (xb_getFoiTime.getDomainFeature().getSpatialOps() != null) {
                getFoiTimeRequest.setDomainFeatureSpatialFilter(parseDomainFeature(xb_getFoiTime.getDomainFeature()));
            }
        }

        if (xb_getFoiTime.getFeatureOfInterestId() != null
                && xb_getFoiTime.getFeatureOfInterestId().length() != 0) {
            getFoiTimeRequest.setFeatureID(xb_getFoiTime.getFeatureOfInterestId());
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 SosConstants.Operations.getFeatureOfInterestTime.name(),
                                 "The request does not contain a feature of interest id!");
            throw se;
        }

        if (xb_getFoiTime.getSensorIDArray() != null
                && xb_getFoiTime.getSensorIDArray().length != 0) {
            getFoiTimeRequest.setProcedure(xb_getFoiTime.getSensorIDArray());
        }

        if (xb_getFoiTime.getObservedPropertyArray() != null
                && xb_getFoiTime.getObservedPropertyArray().length != 0) {
            getFoiTimeRequest.setPhenomenon(xb_getFoiTime.getObservedPropertyArray());
        }

        return getFoiTimeRequest;
    }

    /**
     * parses the getResult XmlBeans representation and creates a SOSGetResultRequest
     * 
     * @param xb_getResultDoc
     *        XmlBeans representation of the getResultRequest
     * @return Returns SOS representation of the getResult request
     * @throws OwsExceptionReport
     *         if validation of the getResult request failed
     */
    public AbstractSosRequest parseGetResultRequest(GetResultDocument xb_getResultDoc) throws OwsExceptionReport {
        SosGetResultRequest request = new SosGetResultRequest();
        // validateDocument(xb_getResultDoc);
        GetResult xb_getRes = xb_getResultDoc.getGetResult();
        request.setService(xb_getRes.getService());
        request.setVersion(xb_getRes.getVersion());
        request.setObservationTemplateId(xb_getRes.getObservationTemplateId());
        if (xb_getRes.getEventTimeArray() != null && xb_getRes.getEventTimeArray().length != 0) {
            // TODO does this work??
            net.opengis.sos.x10.GetResultDocument.GetResult.EventTime[] resultEventTimes = (net.opengis.sos.x10.GetResultDocument.GetResult.EventTime[]) xb_getRes.getEventTimeArray();
            request.setEventTimes(parseEventTime4GetResult(resultEventTimes));
            
        }

        return request;
    }

    /**
     * parses the XmlBean representing the getObservation request and creates a SoSGetObservation request
     * 
     * @param xb_obsDoc
     *        XmlBean created from the incoming request stream
     * @return Returns SosGetObservationRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the XmlBean failed
     */
    public AbstractSosRequest parseGetObservationRequest(GetObservationDocument xb_obsDoc) throws OwsExceptionReport {

        SosGetObservationRequest request = new SosGetObservationRequest();

        // validate document
        validateDocument(xb_obsDoc);

        // parse getObservation
        GetObservation xb_getObs = xb_obsDoc.getGetObservation();

        if (xb_getObs.getResponseMode() != null) {
            String responseMode = xb_getObs.getResponseMode().toString();
            checkResponseMode(responseMode);
            request.setResponseMode(responseMode);
            // resultTemplate
            if (responseMode.equals(SosConstants.RESPONSE_RESULT_TEMPLATE)) {
            	// ENUM ????
//            	xb_getObs.setResponseMode(SosConstants.RESPONSE_MODE_INLINE);
            	String requestString = xb_obsDoc.toString();
            	requestString = requestString.replace("<responseMode>resultTemplate</responseMode>", "");
                request.setRequestString(requestString);
            }
        }

        request.setService(xb_getObs.getService());
        request.setVersion(xb_getObs.getVersion());

        request.setMobileEnabled(xb_getObs.getMobileEnabled());

        String srsName = xb_getObs.getSrsName();
        if (srsName == null || srsName.equals("")) {
            // do nothing
        }
        else {
            request.setSrsName(srsName);
        }
        
        /* if (xb_getObs.getResultType() != null && !xb_getObs.getResultType().equals("")) { String
         * resultTypeString = xb_getObs.getResultType().toString(); if
         * (SosConstants.ResultType.contains(resultTypeString)) {
         * request.setResultType(SosConstants.ResultType.valueOf(resultTypeString)); } else { ServiceException
         * se = new ServiceException();
         * se.addCodedException(ServiceException.ExceptionCode.InvalidParameterValue,
         * GetObservationParams.resultType.toString(), "The resultType must either be:" +
         * SosConstants.ResultType.hits.toString() + " or " + SosConstants.ResultType.results.toString() +
         * "!"); log.error("Error while parsing resultType parameter: " + se.getMessage()); throw se; } }

         /* if (xb_getObs.getStartPosition() != null) {
         * request.setStartPosition(xb_getObs.getStartPosition().intValue()); } if (xb_getObs.getMaxRecords() !=
         * null) { request.setMaxRecords(xb_getObs.getMaxRecords().intValue()); }
         */

        request.setOffering(xb_getObs.getOffering());
        request.setEventTime(parseEventTime4GetObs(xb_getObs.getEventTimeArray()));

        if (xb_getObs.getProcedureArray() != null) {
            request.setProcedure(xb_getObs.getProcedureArray());
        }

        request.setObservedProperty(xb_getObs.getObservedPropertyArray());

        if (xb_getObs.getFeatureOfInterest() != null) {
            request.setFeatureOfInterest(parseFeatureOfInterest(xb_getObs.getFeatureOfInterest()));
            if (request.getFeatureOfInterest().getGeometry() != null) {
                request.setSrsName(SosConfigurator.getInstance().getSrsNamePrefix() + request.getFeatureOfInterest().getGeometry().getSRID());
            }
        }

        // if mobileEnabled check for spatialFilter
        if (request.isMobileEnabled()) {
            if (xb_getObs.getDomainFeature() != null) {
                request.setDomainFeature(parseDomainFeature(xb_getObs.getDomainFeature()));
            }

            // if spatial filter for domain feature AND feature of interest is set, throw exception
            if (request.getDomainFeature() != null && request.getFeatureOfInterest() != null) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     SosConstants.Operations.getObservation.name(),
                                     "The request may not contain both a spatial filter for feature of interests and a spatial filter for domain features.");
                throw se;
            }
        }

        if (xb_getObs.getResult() != null) {
            request.setResult(parseResult(xb_getObs.getResult()));
        }

        request.setResponseFormat(xb_getObs.getResponseFormat());

        if (xb_getObs.getResultModel() != null) {
            request.setResultModel(xb_getObs.getResultModel());
        }

        /*
         * if (xb_getObs.getSortBy() != null) { request.setSortBy(parseSortBy(xb_getObs.getSortBy())); }
         */

        return request;
    } // end parseGetObservationRequest

    /**
     * parses the XmlBean representing the getCapabilities request and creates a SosGetCapabilities request
     * 
     * @param xb_capsDoc
     *        XmlBean created from the incoming request stream
     * @return Returns SosGetCapabilitiesRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the XmlBean failed
     */
    public AbstractSosRequest parseGetCapabilitiesRequest(GetCapabilitiesDocument xb_capsDoc) throws OwsExceptionReport {

        SosGetCapabilitiesRequest request = new SosGetCapabilitiesRequest();

        // validate document
        validateDocument(xb_capsDoc);

        GetCapabilitiesType xb_getCaps = xb_capsDoc.getGetCapabilities();

        request.setService(xb_capsDoc.getGetCapabilities().getService());

        request.setMobileEnabled(xb_capsDoc.getGetCapabilities().getMobileEnabled());

        // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
//        if (xb_getCaps.getUpdateSequence() != null) {
//            request.setUpdateSequence(xb_getCaps.getUpdateSequence());
//        }

        if (xb_getCaps.getAcceptFormats() != null
                && xb_getCaps.getAcceptFormats().sizeOfOutputFormatArray() != 0) {
            request.setAcceptFormats(xb_getCaps.getAcceptFormats().getOutputFormatArray());
        }

        if (xb_getCaps.getAcceptVersions() != null
                && xb_getCaps.getAcceptVersions().sizeOfVersionArray() != 0) {
            request.setAcceptVersions(xb_getCaps.getAcceptVersions().getVersionArray());
        }

        if (xb_getCaps.getSections() != null
                && xb_getCaps.getSections().getSectionArray().length != 0) {
            request.setSections(xb_getCaps.getSections().getSectionArray());
        }

        return request;

    } // end parseGetCapabilitiesRequest

    /**
     * method parses the GetObservationById document and creates a SosGetObservationByIdRequest object from
     * the passed request
     * 
     * @param xb_getObsByIdDoc
     *        XmlBeans representation of GetObservationById request
     * @return Returns SosGetObservationByIdRequest object representing the request
     */
    public AbstractSosRequest parseGetObsByIdRequest(GetObservationByIdDocument xb_getObsByIdDoc) {
        SosGetObservationByIdRequest request = new SosGetObservationByIdRequest();
        // validateDocument(xb_getObsByIdDoc);
        GetObservationById xb_getObsById = xb_getObsByIdDoc.getGetObservationById();
        request.setService(xb_getObsById.getService());
        request.setVersion(xb_getObsById.getVersion());
        request.setObservationID(xb_getObsById.getObservationId());
        request.setSrsName(xb_getObsById.getSrsName());
        request.setResponseFormat(xb_getObsById.getResponseFormat());
        request.setResultModel(xb_getObsById.getResultModel());
        if (xb_getObsById.getResponseMode() != null) {
            request.setResponseMode(xb_getObsById.getResponseMode().toString());
        }
        return request;
    } // end parseGetObsByIdRequest

    /**
     * method parses the DescribeFeatureType document and creates a SosDescribeFeatureTypeRequest object from
     * the passed request
     * 
     * @param xb_descFeat XmlBeans representation of DescribeFeatureType request
     * @return Returns SosDescribeFeatureTypeRequest object representing the request
     * @throws OwsExceptionReport If parsing the XmlBean failed
     */
    public AbstractSosRequest parseDescFeatTypeRequest(
			DescribeFeatureTypeDocument xb_descFeat) throws OwsExceptionReport {
    	 // validate request
        validateDocument(xb_descFeat);

        SosDescribeFeatureTypeRequest descFestTypeRequest = new SosDescribeFeatureTypeRequest();
        DescribeFeatureType xb_DescFeat = xb_descFeat.getDescribeFeatureType();

        descFestTypeRequest.setVersion(xb_DescFeat.getVersion());
        descFestTypeRequest.setService(xb_DescFeat.getService());

        if (xb_DescFeat.getFeatureId() != null ) {
        	descFestTypeRequest.setFeatureIDs(xb_DescFeat.getFeatureId());
        }

        return descFestTypeRequest;
		
	}

	/**
     * parses the eventTime of the GetObservation requests and returns an array representing the temporal filters
     * 
     * @param xb_eventTimes
     *        array of XmlObjects representing the eventTime element in the getObservation request
     * @return Returns array representing the temporal filters
     * @throws OwsExceptionReport
     *         if parsing of the element failed
     */
    private TemporalFilter[] parseEventTime4GetObs(EventTime[] xb_eventTimes) throws OwsExceptionReport {
        if (xb_eventTimes == null || xb_eventTimes.length == 0) {
            return null;
        }
        TemporalFilter[] result = new TemporalFilter[xb_eventTimes.length];

        String opNodeName;
        XmlCursor timeCursor;
        boolean isTimeInstant;
        boolean isTimePeriod;
        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
        ISosTime sosTime;
        for (int i = 0; i < xb_eventTimes.length; i++) {
            timeCursor = xb_eventTimes[i].newCursor();
            
            timeCursor.toFirstChild();
            opNodeName = timeCursor.getName().getLocalPart();
            //getDomNode().getNodeName().replace("ogc:", "");
            timeCursor = xb_eventTimes[i].getTemporalOps().newCursor();

            try {
                isTimeInstant = timeCursor.toChild(timeInstantName);
                if (isTimeInstant) {
                    sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }

                isTimePeriod = timeCursor.toChild(timePeriodName);
                if (isTimePeriod) {
                    sosTime = GMLDecoder.parseTimePeriodNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error(se.getMessage() + e.getMessage());
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "EventTime",
                                     "Error while parsing eventTime of GetObservation request: "
                                             + e.getMessage());
                throw se;
            }
        }
        return result;

    } // end parseEventTime4GetObs
     
    /**
     * parses the eventTime of the GetResult requests and returns an array representing the temporal filters
     * 
     * @param xb_eventTimes
     *        array of XmlObjects representing the eventTime element in the getObservation request
     * @return Returns array representing the temporal filters
     * @throws OwsExceptionReport
     *         if parsing of the element failed
     */
    private TemporalFilter[] parseEventTime4GetResult(net.opengis.sos.x10.GetResultDocument.GetResult.EventTime[] xb_eventTimes) throws OwsExceptionReport {
        if (xb_eventTimes == null || xb_eventTimes.length == 0) {
            return null;
        }
        TemporalFilter[] result = new TemporalFilter[xb_eventTimes.length];
        
        String opNodeName;
        XmlCursor timeCursor;
        boolean isTimeInstant;
        boolean isTimePeriod;
        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
        ISosTime sosTime;
        for (int i = 0; i < xb_eventTimes.length; i++) {
            timeCursor = xb_eventTimes[i].newCursor();
            
            timeCursor.toFirstChild();
            opNodeName = timeCursor.getName().getLocalPart();
            //getDomNode().getNodeName().replace("ogc:", "");
            timeCursor = xb_eventTimes[i].getTemporalOps().newCursor();

            try {
                isTimeInstant = timeCursor.toChild(timeInstantName);
                if (isTimeInstant) {
                    sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }

                isTimePeriod = timeCursor.toChild(timePeriodName);
                if (isTimePeriod) {
                    sosTime = GMLDecoder.parseTimePeriodNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error(se.getMessage() + e.getMessage());
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "EventTime",
                                     "Error while parsing eventTime of GetResult request: "
                                             + e.getMessage());
                throw se;
            }
        }
        return result;
    } // end parseEventTime4GetResult
    
    /**
     * parses the eventTime of the GetFoi requests and returns an array representing the temporal filters
     * 
     * @param xb_eventTimes
     *        array of XmlObjects representing the eventTime element in the getObservation request
     * @return Returns array representing the temporal filters
     * @throws OwsExceptionReport
     *         if parsing of the element failed
     */
    private TemporalFilter[] parseEventTime4GetFoi(net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest.EventTime[] xb_eventTimes) throws OwsExceptionReport {
        if (xb_eventTimes == null || xb_eventTimes.length == 0) {
            return null;
        }
        TemporalFilter[] result = new TemporalFilter[xb_eventTimes.length];
        
        String opNodeName;
        XmlCursor timeCursor;
        boolean isTimeInstant;
        boolean isTimePeriod;
        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
        ISosTime sosTime;
        for (int i = 0; i < xb_eventTimes.length; i++) {
            timeCursor = xb_eventTimes[i].newCursor();
            
            timeCursor.toFirstChild();
            opNodeName = timeCursor.getName().getLocalPart();
            //getDomNode().getNodeName().replace("ogc:", "");
            timeCursor = xb_eventTimes[i].getTemporalOps().newCursor();

            try {
                isTimeInstant = timeCursor.toChild(timeInstantName);
                if (isTimeInstant) {
                    sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }

                isTimePeriod = timeCursor.toChild(timePeriodName);
                if (isTimePeriod) {
                    sosTime = GMLDecoder.parseTimePeriodNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error(se.getMessage() + e.getMessage());
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "EventTime",
                                     "Error while parsing eventTime of GetFeatureOfInterest request: "
                                             + e.getMessage());
                throw se;
            }
        }
        return result;
    } // end parseEventTime4GetFoi
    
    /**
     * parses the eventTime of the GetDomainFeature requests and returns an array representing the temporal filters
     * 
     * @param xb_eventTimes
     *        array of XmlObjects representing the eventTime element in the getObservation request
     * @return Returns array representing the temporal filters
     * @throws OwsExceptionReport
     *         if parsing of the element failed
     */
    private TemporalFilter[] parseEventTime4GetDomainFeature(net.opengis.sos.x10.GetDomainFeatureDocument.GetDomainFeature.EventTime[] xb_eventTimes) throws OwsExceptionReport {
        if (xb_eventTimes == null || xb_eventTimes.length == 0) {
            return null;
        }
        TemporalFilter[] result = new TemporalFilter[xb_eventTimes.length];
        
        String opNodeName;
        XmlCursor timeCursor;
        boolean isTimeInstant;
        boolean isTimePeriod;
        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
        ISosTime sosTime;
        for (int i = 0; i < xb_eventTimes.length; i++) {
            timeCursor = xb_eventTimes[i].newCursor();
            
            timeCursor.toFirstChild();
            opNodeName = timeCursor.getName().getLocalPart();
            //getDomNode().getNodeName().replace("ogc:", "");
            timeCursor = xb_eventTimes[i].getTemporalOps().newCursor();

            try {
                isTimeInstant = timeCursor.toChild(timeInstantName);
                if (isTimeInstant) {
                    sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }

                isTimePeriod = timeCursor.toChild(timePeriodName);
                if (isTimePeriod) {
                    sosTime = GMLDecoder.parseTimePeriodNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error(se.getMessage() + e.getMessage());
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "EventTime",
                                     "Error while parsing eventTime of GetDomainFeature request: "
                                             + e.getMessage());
                throw se;
            }
        }
        return result;
    } // end parseEventTime4GetDomainFeature

    /**
     * parses the Time of the requests and returns an array representing the temporal filters
     * 
     * @param xb_Times
     *        array of XmlObjects representing the Time element in the describeSensor request
     * @return Returns array representing the temporal filters
     * @throws OwsExceptionReport
     *         if parsing of the element failed
     */
    private TemporalFilter[] parseTime(Time[] xb_times) throws OwsExceptionReport {
        if (xb_times == null || xb_times.length == 0) {
            return null;
        }
        TemporalFilter[] result = new TemporalFilter[xb_times.length];

        String opNodeName;
        XmlCursor timeCursor;
        boolean isTimeInstant;
        boolean isTimePeriod;
        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
        ISosTime sosTime;
        for (int i = 0; i < xb_times.length; i++) {
            timeCursor = xb_times[i].newCursor();

            timeCursor.toFirstChild();
            opNodeName = timeCursor.getDomNode().getNodeName().replace("ogc:", "");

            try {
                isTimeInstant = timeCursor.toChild(timeInstantName);
                if (isTimeInstant) {
                    sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }

                isTimePeriod = timeCursor.toChild(timePeriodName);
                if (isTimePeriod) {
                    sosTime = GMLDecoder.parseTimePeriodNode(timeCursor.getDomNode());
                    result[i] = new TemporalFilter(opNodeName, sosTime);
                }
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error(se.getMessage() + e.getMessage());
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "Time",
                                     "Error while parsing time of describeSensor request: "
                                             + e.getMessage());
                throw se;
            }
        }
        return result;

    } // end parseTime

    /**
     * parses the featureOfInterest element of the GetObservation request
     * 
     * @param xb_foi
     *        XmlBean representing the feature of interest parameter of the request
     * @return Returns SpatialFilter created from the passed foi request parameter
     * @throws OwsExceptionReport
     *         if creation of the SpatialFilter failed
     */
    private SpatialFilter parseLocation(net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest.Location xb_location) throws OwsExceptionReport {

        if (xb_location == null) {
            return null;
        }

        SpatialFilter result = new SpatialFilter();
        SpatialOperator operator = null;
        Geometry geometry = null;

        SpatialOpsType xb_abstractSpatialOps = xb_location.getSpatialOps();

        // check whether spatial Operator is ogc:BBOX
        if (xb_abstractSpatialOps instanceof BBOXType) {
            operator = FilterConstants.SpatialOperator.BBOX;
            BBOXType xb_bbox = (BBOXType) (xb_abstractSpatialOps);
            geometry = GMLDecoder.getGeometry4BBOX(xb_bbox);
        }

        // if not ogc:BBOX
        else {
            // switch between node names because type is now BinarySpatialOpType (no use of instanceof
            // operator possible :-( )
            String operatorName = xb_abstractSpatialOps.getDomNode().getNodeName().replace("ogc:",
                                                                                           "");
            if (operatorName.equalsIgnoreCase(SpatialOperator.Contains.name())) {
                operator = FilterConstants.SpatialOperator.Contains;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }

            else if (operatorName.equalsIgnoreCase(SpatialOperator.Overlaps.name())) {
                operator = FilterConstants.SpatialOperator.Overlaps;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }
            else if (operatorName.equalsIgnoreCase(SpatialOperator.Intersects.name())) {
                operator = FilterConstants.SpatialOperator.Intersects;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     null,
                                     "SpatialOps operator '" + operatorName + "' not supported");
                throw se;
            }
        }

        result.setOperator(operator);

        result.setGeometry(geometry);
        return result;
    }// end parseFeatureOfInterest

    /**
     * parses the featureOfInterest element of the GetObservation request
     * 
     * @param xb_foi
     *        XmlBean representing the feature of interest parameter of the request
     * @return Returns SpatialFilter created from the passed foi request parameter
     * @throws OwsExceptionReport
     *         if creation of the SpatialFilter failed
     */
    private SpatialFilter parseDomainFeatureLocation(net.opengis.sos.x10.GetDomainFeatureDocument.GetDomainFeature.Location xb_location) throws OwsExceptionReport {

        if (xb_location == null) {
            return null;
        }

        SpatialFilter result = new SpatialFilter();
        SpatialOperator operator = null;
        Geometry geometry = null;

        SpatialOpsType xb_abstractSpatialOps = xb_location.getSpatialOps();

        // check whether spatial Operator is ogc:BBOX
        if (xb_abstractSpatialOps instanceof BBOXType) {
            operator = FilterConstants.SpatialOperator.BBOX;
            BBOXType xb_bbox = (BBOXType) (xb_abstractSpatialOps);
            geometry = GMLDecoder.getGeometry4BBOX(xb_bbox);
        }

        // if not ogc:BBOX
        else {
            // switch between node names because type is now BinarySpatialOpType (no use of instanceof
            // operator possible :-( )
            String operatorName = xb_abstractSpatialOps.getDomNode().getNodeName().replace("ogc:",
                                                                                           "");
            if (operatorName.equalsIgnoreCase(SpatialOperator.Contains.name())) {
                operator = FilterConstants.SpatialOperator.Contains;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }

            else if (operatorName.equalsIgnoreCase(SpatialOperator.Overlaps.name())) {
                operator = FilterConstants.SpatialOperator.Overlaps;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }
            else if (operatorName.equalsIgnoreCase(SpatialOperator.Intersects.name())) {
                operator = FilterConstants.SpatialOperator.Intersects;
                geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     null,
                                     "SpatialOps operator '" + operatorName + "' not supported");
                throw se;
            }
        }

        result.setOperator(operator);

        result.setGeometry(geometry);
        return result;
    }// end parseFeatureOfInterest

    /**
     * parses the featureOfInterest element of the GetObservation request
     * 
     * @param xb_foi
     *        XmlBean representing the feature of interest parameter of the request
     * @return Returns SpatialFilter created from the passed foi request parameter
     * @throws OwsExceptionReport
     *         if creation of the SpatialFilter failed
     */
    private SpatialFilter parseFeatureOfInterest(FeatureOfInterest xb_foi) throws OwsExceptionReport {

        SpatialFilter result = new SpatialFilter();

        CapabilitiesCacheController cache = SosConfigurator.getInstance().getCapsCacheController();
        List<String> fois_cache = cache.getFois();
        String[] fois_request = xb_foi.getObjectIDArray();

        StringBuffer invalidFois = new StringBuffer();

        if (xb_foi == null) {
            return null;
        }
        else if (xb_foi.getObjectIDArray() != null && xb_foi.getObjectIDArray().length != 0) {

            // check if foi in request is a valid foi
            for (int i = 0; i < fois_request.length; i++)
                if ( !fois_cache.contains(fois_request[i])) {
                    invalidFois.append("'" + fois_request[i] + "' ");
                }

            if (invalidFois.length() > 0) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error("The FeatureOfInterest ID(s) " + invalidFois.toString()
                        + " in the request is/are invalid!");
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "FeatureOfInterest",
                                     "The FeatureOfInterest ID(s) " + invalidFois.toString()
                                             + " in the request is/are invalid!");
                throw se;
            }

            result.setFoiIDs(xb_foi.getObjectIDArray());
            return result;
        }
        else if (xb_foi.getSpatialOps() != null) {
            SpatialOperator operator = null;
            Geometry geometry = null;

            SpatialOpsType xb_abstractSpatialOps = xb_foi.getSpatialOps();

            // check whether spatial Operator is ogc:BBOX
            if (xb_abstractSpatialOps instanceof BBOXType) {
                operator = FilterConstants.SpatialOperator.BBOX;
                BBOXType xb_bbox = (BBOXType) (xb_abstractSpatialOps);
                geometry = GMLDecoder.getGeometry4BBOX(xb_bbox);
            }           

            // if not ogc:BBOX
            else {
                // switch between node names because type is now BinarySpatialOpType - no use of instanceof
                // operator possible :-(
                String operatorName = xb_abstractSpatialOps.getDomNode().getLocalName();

                if (operatorName.equalsIgnoreCase(SpatialOperator.Contains.name())) {
                    operator = FilterConstants.SpatialOperator.Contains;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }

                else if (operatorName.equalsIgnoreCase(SpatialOperator.Overlaps.name())) {
                    operator = FilterConstants.SpatialOperator.Overlaps;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else if (operatorName.equalsIgnoreCase(SpatialOperator.Intersects.name())) {
                    operator = FilterConstants.SpatialOperator.Intersects;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         null,
                                         "SpatialOps operator '" + operatorName + "' not supported");
                    throw se;
                }
            }
            result.setOperator(operator);
            result.setGeometry(geometry);
            return result;
        }
        else {
            throw new IllegalArgumentException("Incorrect encoding of 'featureOfInterest' element");
        }
    }// end parseFeatureOfInterest

    /**
     * parses the domainFeature element of the GetObservation request
     * 
     * @param xb_foi
     *        XmlBean representing the domain feature parameter of the request
     * @return Returns SpatialFilter created from the passed foi request parameter
     * @throws OwsExceptionReport
     *         if creation of the SpatialFilter failed
     */
    private SpatialFilter parseDomainFeature(DomainFeature xb_df) throws OwsExceptionReport {

        SpatialFilter result = new SpatialFilter();

        CapabilitiesCacheController cache = SosConfigurator.getInstance().getCapsCacheController();
        List<String> df_cache = cache.getDomainFeatures();
        String[] df_request = xb_df.getObjectIDArray();

        StringBuffer invalidDFs = new StringBuffer();

        if (xb_df == null) {
            return null;
        }
        else if (xb_df.getObjectIDArray() != null && xb_df.getObjectIDArray().length != 0) {

            // check if foi in request is a valid foi
            for (int i = 0; i < df_request.length; i++)
                if ( !df_cache.contains(df_request[i])) {
                    invalidDFs.append("'" + df_request[i] + "' ");
                }

            if (invalidDFs.length() > 0) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error("The DomainFeature ID(s) " + invalidDFs.toString()
                        + " in the request is/are invalid!");
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "DomainFeature",
                                     "The DomainFeature ID(s) " + invalidDFs.toString()
                                             + " in the request is/are invalid!");
                throw se;
            }

            result.setFoiIDs(xb_df.getObjectIDArray());
            return result;
        }
        else if (xb_df.getSpatialOps() != null) {
            SpatialOperator operator = null;
            Geometry geometry = null;

            SpatialOpsType xb_abstractSpatialOps = xb_df.getSpatialOps();

            // check whether spatial Operator is ogc:BBOX
            if (xb_abstractSpatialOps instanceof BBOXType) {
                operator = FilterConstants.SpatialOperator.BBOX;
                BBOXType xb_bbox = (BBOXType) (xb_abstractSpatialOps);
                geometry = GMLDecoder.getGeometry4BBOX(xb_bbox);
            }

            // if not ogc:BBOX
            else {
                // switch between node names because type is now BinarySpatialOpType - no use of instanceof
                // operator possible :-(
                String operatorName = xb_abstractSpatialOps.getDomNode().getLocalName();

                if (operatorName.equalsIgnoreCase(SpatialOperator.Contains.name())) {
                    operator = FilterConstants.SpatialOperator.Contains;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }

                else if (operatorName.equalsIgnoreCase(SpatialOperator.Overlaps.name())) {
                    operator = FilterConstants.SpatialOperator.Overlaps;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else if (operatorName.equalsIgnoreCase(SpatialOperator.Intersects.name())) {
                    operator = FilterConstants.SpatialOperator.Intersects;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         null,
                                         "SpatialOps operator '" + operatorName + "' not supported");
                    throw se;
                }
            }
            result.setOperator(operator);
            result.setGeometry(geometry);
            return result;
        }
        else {
            throw new IllegalArgumentException("Incorrect encoding of 'domainFeature' element");
        }
    }// end parseDomainFeature

    /**
     * parses the domainFeature element of the GetObservation request
     * 
     * @param xb_foi
     *        XmlBean representing the domain feature parameter of the request
     * @return Returns SpatialFilter created from the passed foi request parameter
     * @throws OwsExceptionReport
     *         if creation of the SpatialFilter failed
     */
    private SpatialFilter parseDomainFeature(GetFeatureOfInterestTimeDocument.GetFeatureOfInterestTime.DomainFeature xb_df) throws OwsExceptionReport {

        SpatialFilter result = new SpatialFilter();

        CapabilitiesCacheController cache = SosConfigurator.getInstance().getCapsCacheController();
        List<String> df_cache = cache.getDomainFeatures();
        String[] df_request = xb_df.getObjectIDArray();

        StringBuffer invalidDFs = new StringBuffer();

        if (xb_df == null) {
            return null;
        }
        else if (xb_df.getObjectIDArray() != null && xb_df.getObjectIDArray().length != 0) {

            // check if foi in request is a valid foi
            for (int i = 0; i < df_request.length; i++)
                if ( !df_cache.contains(df_request[i])) {
                    invalidDFs.append("'" + df_request[i] + "' ");
                }

            if (invalidDFs.length() > 0) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error("The DomainFeature ID(s) " + invalidDFs.toString()
                        + " in the request is/are invalid!");
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "DomainFeature",
                                     "The DomainFeature ID(s) " + invalidDFs.toString()
                                             + " in the request is/are invalid!");
                throw se;
            }

            result.setFoiIDs(xb_df.getObjectIDArray());
            return result;
        }
        else if (xb_df.getSpatialOps() != null) {
            SpatialOperator operator = null;
            Geometry geometry = null;

            SpatialOpsType xb_abstractSpatialOps = xb_df.getSpatialOps();

            // check whether spatial Operator is ogc:BBOX
            if (xb_abstractSpatialOps instanceof BBOXType) {
                operator = FilterConstants.SpatialOperator.BBOX;
                BBOXType xb_bbox = (BBOXType) (xb_abstractSpatialOps);
                geometry = GMLDecoder.getGeometry4BBOX(xb_bbox);
            }

            // if not ogc:BBOX
            else {
                // switch between node names because type is now BinarySpatialOpType - no use of instanceof
                // operator possible :-(
                String operatorName = xb_abstractSpatialOps.getDomNode().getLocalName();

                if (operatorName.equalsIgnoreCase(SpatialOperator.Contains.name())) {
                    operator = FilterConstants.SpatialOperator.Contains;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }

                else if (operatorName.equalsIgnoreCase(SpatialOperator.Overlaps.name())) {
                    operator = FilterConstants.SpatialOperator.Overlaps;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else if (operatorName.equalsIgnoreCase(SpatialOperator.Intersects.name())) {
                    operator = FilterConstants.SpatialOperator.Intersects;
                    geometry = parseBinarySpatialOpTypeGeom((BinarySpatialOpType) xb_abstractSpatialOps);
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         null,
                                         "SpatialOps operator '" + operatorName + "' not supported");
                    throw se;
                }
            }
            result.setOperator(operator);
            result.setGeometry(geometry);
            return result;
        }
        else {
            throw new IllegalArgumentException("Incorrect encoding of 'domainFeature' element");
        }
    }// end parseDomainFeature

    /**
     * parses the geometry or envelope element of the spatial filter and returns a WKT-representation of this
     * geometry
     * 
     * @param xb_binSpatialOpType
     *        XmlBean representing either the envelope or the geometry
     * @return Returns Well-Known-Text (WKT) representation of the geometry or envelope
     * @throws OwsExceptionReport
     *         if parsing failed
     */
    private Geometry parseBinarySpatialOpTypeGeom(BinarySpatialOpType xb_binSpatialOpType) throws OwsExceptionReport {
        Geometry geometry = null;

        EnvelopeType xb_envelope = xb_binSpatialOpType.getEnvelope();
        AbstractGeometryType xb_geometry = xb_binSpatialOpType.getGeometry();
        if (xb_envelope != null) {
            //geometry = getWKT4Envelope(xb_envelope);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Geometry 'Envelope' is only supported for spatial operator 'BBOX'.");
            throw se;
        }
        else if (xb_geometry != null) {
            geometry = GMLDecoder.getGeometry4XmlGeometry(xb_geometry);
        }
        return geometry;
    } // end parseBinarySpatialOpTypeGeom

    /**
     * parses the XmlBean which represents the result filter and creates a ComparisonFilter representing the
     * result Filter
     * 
     * @param xb_result
     *        XmlBean representing the result filter
     * @return Returns a ComparisonFilter which represents the result filter
     * @throws OwsExceptionReport
     *         if parsing the result filter failed
     */
    public static ComparisonFilter parseResult(Result xb_result) throws OwsExceptionReport {
        ComparisonOpsType xb_compOpsType = xb_result.getComparisonOps();

        ComparisonFilter compFilter = new ComparisonFilter();

        ComparisonOperator operator = null;
        String propertyName = null;
        String value = null;
        String valueUpper = null;
        String escapeString = null;
        String wildCard = null;
        String singleChar = null;

        if (xb_compOpsType instanceof PropertyIsBetweenType) {
            operator = ComparisonOperator.PropertyIsBetween;
            PropertyIsBetweenType xb_propBetween = (PropertyIsBetweenType) xb_compOpsType;
            propertyName = xb_propBetween.getExpression().newCursor().getTextValue();
            LowerBoundaryType xb_lowerBoundary = xb_propBetween.getLowerBoundary();
            value = xb_lowerBoundary.getExpression().newCursor().getTextValue();
            UpperBoundaryType xb_upperBoundary = xb_propBetween.getUpperBoundary();
            valueUpper = xb_upperBoundary.getExpression().newCursor().getTextValue();
        }
        else if (xb_compOpsType instanceof PropertyIsLikeType) {
            operator = ComparisonOperator.PropertyIsLike;
            PropertyIsLikeType xb_propIsLikeType = (PropertyIsLikeType) xb_compOpsType;
            propertyName = xb_propIsLikeType.getPropertyName().newCursor().getTextValue();
            value = xb_propIsLikeType.getLiteral().newCursor().getTextValue();
    		
    		wildCard = xb_propIsLikeType.getWildCard();
    		singleChar = xb_propIsLikeType.getSingleChar();
    		escapeString = xb_propIsLikeType.getEscapeChar();
        }
        else if (xb_compOpsType instanceof PropertyIsNullType) {
            operator = ComparisonOperator.PropertyIsNull;
            PropertyIsNullType xb_propIsNullType = (PropertyIsNullType) xb_compOpsType;
            propertyName = xb_propIsNullType.getPropertyName().newCursor().getTextValue();
        }
        else {
            BinaryComparisonOpType xb_binCompOpType = (BinaryComparisonOpType) xb_compOpsType;
            String operatorName = xb_binCompOpType.getDomNode().getNodeName().replace("ogc:", "");

            if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsEqualTo.name())) {
                operator = ComparisonOperator.PropertyIsEqualTo;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }
            else if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsGreaterThan.name())) {
                operator = ComparisonOperator.PropertyIsGreaterThan;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }
            else if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsGreaterThanOrEqualTo.name())) {
                operator = ComparisonOperator.PropertyIsGreaterThanOrEqualTo;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }
            else if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsLessThan.name())) {
                operator = ComparisonOperator.PropertyIsLessThan;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }
            else if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsLessThanOrEqualTo.name())) {
                operator = ComparisonOperator.PropertyIsLessThanOrEqualTo;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }

            else if (operatorName.equalsIgnoreCase(ComparisonOperator.PropertyIsNotEqualTo.name())) {
                operator = ComparisonOperator.PropertyIsNotEqualTo;
                String[] propValue = getPropValue4ExpressionArray(xb_binCompOpType.getExpressionArray());
                propertyName = propValue[0];
                value = propValue[1];
            }

            else {
                // TODO throw Service Exception
            }
        }
        compFilter.setValue(value);
        compFilter.setPropertyName(propertyName);
        compFilter.setOperator(operator);
        compFilter.setValueUpper(valueUpper);
        compFilter.setEscapeString(escapeString);
        compFilter.setWildCard(wildCard);
        compFilter.setSingleChar(singleChar);
        return compFilter;
    } // end parseResult

	/**
     * Returns an array with propertyname and value form the passed expressionArray, which is xmlBeans
     * generated
     * 
     * @param xb_expressionArray
     *        XmlBean representing the expressionarray
     * @return Returns String[] of length=2 containing the property as first element and the value as second
     * @throws OwsExceptionReport
     *         if parsing the expression array failed
     */
    private static String[] getPropValue4ExpressionArray(ExpressionType[] xb_expressionArray) throws OwsExceptionReport {
        String returnValue = null;
        String propertyName = null;
        if (xb_expressionArray.length == 2) {
            // parse propertyname
            if (xb_expressionArray[0] instanceof PropertyNameType) {
                propertyName = xb_expressionArray[0].newCursor().getTextValue();
            }

            if (xb_expressionArray[1] instanceof LiteralType) {
                returnValue = xb_expressionArray[1].newCursor().getTextValue();
            }

        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.result.toString(),
                                 "The requested '"
                                         + GetObservationParams.result.toString()
                                         + "' filter is not supported by this service! PropertyIsEqualTo Filter can just have one Literal!");
            throw se;
        }
        String[] result = new String[] {propertyName, returnValue};
        return result;
    }// end getPropValue4ExpressionArray


    /**
     * checks whether the getObservationRequest XMLDocument is valid
     * 
     * @param getObsDoc
     *        the document which should be checked
     * 
     * @throws OwsExceptionReport
     *         if the Document is not valid
     */
    private void validateDocument(XmlObject xb_doc) throws OwsExceptionReport {

        // Create an XmlOptions instance and set the error listener.
        ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        // Validate the GetCapabilitiesRequest XML document
        boolean isValid = xb_doc.validate(validationOptions);

        // Create Exception with error message if the xml document is invalid
        if ( !isValid) {

            String message = null;
            String parameterName = null;

            // getValidation error and throw service exception for the first error
            Iterator<XmlError> iter = validationErrors.iterator();
            while (iter.hasNext()) {

                // ExceptionCode for Exception
                ExceptionCode exCode = null;

                // get name of the missing or invalid parameter
                XmlError error = iter.next();
                message = error.getMessage();
                if (message != null) {

                    // check, if parameter is missing or value of parameter is invalid to ensure, that correct
                    // exceptioncode in exception response is used

                    // invalid parameter value
                    if (message.startsWith("The value")) {
                        exCode = ExceptionCode.InvalidParameterValue;

                        // split message string to get attribute name
                        String[] messAndAttribute = message.split("attribute '");
                        if (messAndAttribute.length == 2) {
                            parameterName = messAndAttribute[1].replace("'", "");
                        }
                    }

                    // invalid enumeration value --> InvalidParameterValue
                    else if (message.contains("not a valid enumeration value")) {
                        exCode = ExceptionCode.InvalidParameterValue;

                        // get attribute name
                        String[] messAndAttribute = message.split(" ");
                        parameterName = messAndAttribute[10];
                    }

                    // mandatory attribute is missing --> missingParameterValue
                    else if (message.startsWith("Expected attribute")) {
                        exCode = ExceptionCode.MissingParameterValue;

                        // get attribute name
                        String[] messAndAttribute = message.split("attribute: ");
                        if (messAndAttribute.length == 2) {
                            String[] attrAndRest = messAndAttribute[1].split(" in");
                            if (attrAndRest.length == 2) {
                                parameterName = attrAndRest[0];
                            }
                        }
                    }

                    // mandatory element is missing --> missingParameterValue
                    else if (message.startsWith("Expected element")) {
                        exCode = ExceptionCode.MissingParameterValue;

                        // get element name
                        String[] messAndElements = message.split(" '");
                        if (messAndElements.length >= 2) {
                            String elements = messAndElements[1];
                            if (elements.contains("offering")) {
                                parameterName = "offering";
                            }
                            else if (elements.contains("observedProperty")) {
                                parameterName = "observedProperty";
                            }
                            else if (elements.contains("responseFormat")) {
                                parameterName = "responseFormat";
                            }
                            else if (elements.contains("procedure")) {
                                parameterName = "procedure";
                            }
                            else if (elements.contains("featureOfInterest")) {
                                parameterName = "featureOfInterest";
                            }
                            else {
                                // TODO check if other elements are invalid
                            }
                        }
                    }
                    // invalidParameterValue
                    else if (message.startsWith("Element")) {
                        exCode = ExceptionCode.InvalidParameterValue;

                        // get element name
                        String[] messAndElements = message.split(" '");
                        if (messAndElements.length >= 2) {
                            String elements = messAndElements[1];
                            if (elements.contains("offering")) {
                                parameterName = "offering";
                            }
                            else if (elements.contains("observedProperty")) {
                                parameterName = "observedProperty";
                            }
                            else if (elements.contains("responseFormat")) {
                                parameterName = "responseFormat";
                            }
                            else if (elements.contains("procedure")) {
                                parameterName = "procedure";
                            }
                            else if (elements.contains("featureOfInterest")) {
                                parameterName = "featureOfInterest";
                            }
                            else {
                                // TODO check if other elements are invalid
                            }
                        }
                    }
                    else {
                        // create service exception
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(ExceptionCode.InvalidRequest,
                                             null,
                                             "[XmlBeans validation error:] " + message);
                        log.error("The request is invalid!", se);
                        throw se;
                    }

                    // create service exception
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(exCode, parameterName, "[XmlBeans validation error:] "
                            + message);
                    log.error("The request is invalid!", se);
                    throw se;
                }

            }

        }
    }

    /**
     * help method to check the response mode parameter of GetObservation request. If the value is incorrect,
     * an exception report will be returned!
     * 
     * @param responseMode
     *        String containing the value of the response mode parameter
     * @throws OwsExceptionReport
     *         if the parameter value is incorrect
     */
    private void checkResponseMode(String responseMode) throws OwsExceptionReport {

        // TODO add further response modes, if supported!!
        if (responseMode != null) {
            if (// responseMode.equals(SosConstants.RESPONSE_MODE_ATTACHED)||
            !responseMode.equals(SosConstants.RESPONSE_MODE_INLINE) &&
            // responseMode.equals(SosConstants.RESPONSE_MODE_OUT_OF_BANDS)||
                    !responseMode.equals(SosConstants.RESPONSE_RESULT_TEMPLATE)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.responseMode.toString(),
                                     "The value of the parameter '"
                                             + GetObservationParams.responseMode.toString() + "'"
                                             + "must be '" + SosConstants.RESPONSE_MODE_INLINE
                                             + " or " + SosConstants.RESPONSE_RESULT_TEMPLATE
                                             + "'. Delivered value was: " + responseMode);
                log.error("The responseMode parameter of GetObservation request is incorrect!", se);
                throw se;
            }
        }
    }

}
