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

import javax.xml.namespace.QName;

import org.n52.sos.ogc.om.OMConstants;

/**
 * SosConstants holds all important and often used constants of this SOS (e.g. name of the getCapabilities
 * operation)
 * 
 * @author Christoph Stasch
 * 
 */
public class SosConstants {

    /** the parameter name of REQUEST parameter of a HTTP GET request */
    public static final String REQUEST = "REQUEST";

    /**
     * the parameter value of REQUEST parameter of a HTTP GET request for actualizing metadata in the
     * Capabilities Cache
     */
    public static final String REFRESH_REQUEST = "RefreshMetadata";

    /**
     * the parameter value of REQUEST parameter of a HTTP GET request for actualizing fois in the
     * CapabilitiesCache
     */
    public static final String REFRESH_FOIS_REQUEST = "RefreshFois";
    
    /** Constant for the content type of the response */
    public static final String CONTENT_TYPE_XML = "text/xml";

    /** Constant for the content type of the response */
    public static final String CONTENT_TYPE_ZIP = "application/zip";

    /** Constant for the content type of the response */
    public static final String CONTENT_TYPE_OM = "text/xml;subtype=\"om/1.0.0\"";

    /**
     * Constant for the prefix of the SRS EPSG identifier used in this SOS
     * @deprecated SRS_NAME_PREFIX is now added to the sos.config
     */
    @Deprecated
    public static final String SRS_NAME_PREFIX = "urn:ogc:def:crs:EPSG:";

    /** Constant for prefixes of FOIs */
    public static final String FOI_PREFIX = "urn:ogc:def:object:feature:";

    /** Constant for prefixes of procedures */
    public static final String PROCEDURE_PREFIX = "urn:ogc:object:feature:Sensor:IFGI:";

    public static final String PROCEDURE_STANDARD_DESC_URL = "standardURL";

    /** Constant for prefixes of procedures */
    public static final String PHENOMENON_PREFIX = "urn:ogc:def:phenomenon:OGC:1.0.30:";

    /** Constant for the service name of the SOS */
    public static final String SOS = "SOS";

    /** String representing parameter value, if parameter is not set in an operation request */
    public static final String PARAMETER_NOT_SET = "NOT_SET";

    /** Constant for actual implementing version */
    public static final String SERVICEVERSION = "1.0.0";
    
    /**
     * request timeout in ms for split requests to SOS instances
     */
    public static final long UPDATE_TIMEOUT = 10000;

    /** Constant for actual implementing version 
     *	Measurement 
     */
    public static final String OBS_ID_PREFIX = "o_";
    
    /** Constant for actual implementing version 
     *	OvservationCollection 
     */
    public static final String OBS_COL_ID_PREFIX = "oc_";
    
    /** Constant for actual implementing version
     * 	ObservationTemplate
     */
    public static final String OBS_TEMP_ID_PREFIX = "ot_";
    
    /** Constant for actual implementing version
     * 	SpatialObservation
     */
    public static final String OBS_SPATIAL_ID_PREFIX = "so_";
    
    /** Constant for actual implementing version
     * 	CategoryObservation
     */
    public static final String OBS_CATEGORY_ID_PREFIX = "co_";

    /**
     * Constant 'out-of-bands' for response mode, which means that the results in an observation response
     * appear external to the observation element
     */
    public static final String RESPONSE_MODE_OUT_OF_BANDS = "out-of-bands";

    /**
     * Constant 'resultTemplate' for response mode, which means that the result is an ObservationTemplate for
     * subsequent calls to GetResult operation
     */
    public static final String RESPONSE_RESULT_TEMPLATE = "resultTemplate";

    /**
     * Constant 'inline' for response mode, which means that results are contained inline the Observation
     * elements of an observation response document
     */
    public static final String RESPONSE_MODE_INLINE = "inline";

    /**
     * Constant 'attached' for response mode, which means that result values of an observation response are
     * attached as MIME attachments
     */
    public static final String RESPONSE_MODE_ATTACHED = "attached";

    /**
     * output format for DescribeSensor operation; Currently, SOS only supports SensorML 1.0.1
     */
    public static final String SENSORML_OUTPUT_FORMAT = "text/xml;subtype=\"sensorML/1.0.1\"";

    // ////////////////////////////////////////////////////////
    // resultModel constants; not possible to use enum because of

    /** Constant for result model of a measurement as return type in observation collection */
    public static final QName RESULT_MODEL_MEASUREMENT = new QName(OMConstants.NS_OM,
                                                                   "Measurement",
                                                                   OMConstants.NS_OM_PREFIX);

    /**
     * Constant for spatial observations, which are returned if the value of an observation is a spatial value
     * value
     */
    public static final QName RESULT_MODEL_SPATIAL_OBSERVATION = new QName(OMConstants.NS_OM,
                                                                           "SpatialObservation",
                                                                           OMConstants.NS_OM_PREFIX);

    /**
     * Constant for category observations, which are returned if the value of an observation is a numerical
     * value
     */
    public static final QName RESULT_MODEL_CATEGORY_OBSERVATION = new QName(OMConstants.NS_OM,
                                                                            "CategoryObservation",
                                                                            OMConstants.NS_OM_PREFIX);

    /**
     * Constant for result model of basic observations, which are used for pointer observations, where the
     * result element is of an ExternalReferenceType
     */
    public static final QName RESULT_MODEL_OBSERVATION = new QName(OMConstants.NS_OM,
                                                                   "Observation",
                                                                   OMConstants.NS_OM_PREFIX);

    /** instance attribut, due to the singleton pattern */
    private static SosConstants instance = null;

    /** private constructor, to enforce use of instance instead of instantiation */
    private SosConstants() {
    }

    /** the names of the operations specified in the SOS specification */
    public enum Operations {
        getCapabilities, getObservation, getObservationById, describeSensor, insertObservation, getResult, getFeatureOfInterest, getFeatureOfInterestTime, getDomainFeature, describeFeatureOfInterest, describeObservtionType, describeResultModel, registerSensor, updateSensor
    }

    /** enum with names of Capabilities sections */
    public enum CapabilitiesSections {
        ServiceIdentification, ServiceProvider, OperationsMetadata, Filter_Capabilities, Contents, All;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(CapabilitiesSections.ServiceIdentification.name()))
                    || (s.equals(CapabilitiesSections.ServiceProvider.name()))
                    || (s.equals(CapabilitiesSections.OperationsMetadata.name()))
                    || (s.equals(CapabilitiesSections.Filter_Capabilities.name()))
                    || (s.equals(CapabilitiesSections.Contents.name()))
                    || (s.equals(CapabilitiesSections.All.name()));
            return contained;
        }
    }

    /** enum with parameter names for getCapabilities request */
    public enum GetCapabilitiesParams {
        Sections, AcceptVersions, updateSequence, AcceptFormats, service, mobileEnabled;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetCapabilitiesParams.Sections.name()))
                    || (s.equals(GetCapabilitiesParams.AcceptVersions.name()))
                    || (s.equals(GetCapabilitiesParams.updateSequence.name()))
                    || (s.equals(GetCapabilitiesParams.AcceptFormats.name()))
                    || (s.equals(GetCapabilitiesParams.service.name()))
                    || (s.equals(GetCapabilitiesParams.mobileEnabled.name()));
            return contained;
        }
    }

    /** enum with parameter names for getCapabilities HttpGet request */
    public enum GetCapGetParams {
        SECTIONS, UPDATESEQUENCE, VERSION, SERVICE, ACCEPTFORMATS, REQUEST, MOBILEENABLED;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetCapGetParams.SECTIONS.name()))
                    || (s.equals(GetCapGetParams.VERSION.name()))
                    || (s.equals(GetCapGetParams.UPDATESEQUENCE.name()))
                    || (s.equals(GetCapGetParams.ACCEPTFORMATS.name()))
                    || (s.equals(GetCapGetParams.SERVICE.name()))
                    || (s.equals(GetCapGetParams.REQUEST.name()))
                    || (s.equals(GetCapGetParams.MOBILEENABLED.name()));
            return contained;
        }
    }

    /** enum with parameter names for getObservation request */
    public enum GetObservationByIdParams {
        service, version, srsName, ObservationId, responseFormat, resultModel, responseMode, SortBy;
    }

    /** enum with parameter names for getObservation request */
    public enum GetObservationParams {
        service, version, srsName, resultType, startPosition, maxRecords, offering, eventTime, procedure, observedProperty, domainFeature, featureOfInterest, result, responseFormat, resultModel, responseMode, SortBy, BBOX;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetObservationParams.service.name()))
                    || (s.equals(GetObservationParams.version.name()))
                    || (s.equals(GetObservationParams.srsName.name()))
                    || (s.equals(GetObservationParams.resultType.name()))
                    || (s.equals(GetObservationParams.startPosition.name()))
                    || (s.equals(GetObservationParams.maxRecords.name()))
                    || (s.equals(GetObservationParams.offering.name()))
                    || (s.equals(GetObservationParams.eventTime.name()))
                    || (s.equals(GetObservationParams.procedure.name()))
                    || (s.equals(GetObservationParams.observedProperty.name()))
                    || (s.equals(GetObservationParams.domainFeature.name()))
                    || (s.equals(GetObservationParams.featureOfInterest.name()))
                    || (s.equals(GetObservationParams.result.name()))
                    || (s.equals(GetObservationParams.responseFormat.name()))
                    || (s.equals(GetObservationParams.resultModel.name()))
                    || (s.equals(GetObservationParams.responseMode.name()));
            return contained;
        }
    }

    /** enum with parameter names for registerSensor request */
    public enum RegisterSensorParams {
        service, version, SensorDescription, ObservationTemplate;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(RegisterSensorParams.service.name()))
                    || (s.equals(RegisterSensorParams.version.name()))
                    || (s.equals(RegisterSensorParams.SensorDescription.name()))
                    || (s.equals(RegisterSensorParams.ObservationTemplate.name()));
            return contained;
        }
    }

    /** enum with parameter names for insertObservation request */
    public enum InsertObservationParams {
        service, version, AssignedSensorId, Observation;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(InsertObservationParams.service.name()))
                    || (s.equals(InsertObservationParams.version.name()))
                    || (s.equals(InsertObservationParams.AssignedSensorId.name()))
                    || (s.equals(InsertObservationParams.Observation.name()));
            return contained;
        }
    }

    /** enum with parameter names for getFeatureOfInterest request */
    public enum GetFeatureOfInterestParams {
        service, version, featureOfInterestID, location;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetFeatureOfInterestParams.service.name()))
                    || (s.equals(GetFeatureOfInterestParams.version.name()))
                    || (s.equals(GetFeatureOfInterestParams.featureOfInterestID.name()))
                    || (s.equals(GetFeatureOfInterestParams.location.name()));
            return contained;
        }
    }

    /** enum with parameter names for getFeatureOfInterestTime request */
    public enum GetFeatureOfInterestTimeParams {
        service, version, featureOfInterestID, location, observedProperty, procedure, domainFeature;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetFeatureOfInterestTimeParams.service.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.version.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.featureOfInterestID.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.location.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.observedProperty.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.procedure.name()))
                    || (s.equals(GetFeatureOfInterestTimeParams.domainFeature.name()));
            return contained;
        }
    }

    /** enum with parameter names for getDomainFeature request */
    public enum GetDomainFeatureParams {
        service, version, domainFeatureID, location;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(GetDomainFeatureParams.service.name()))
                    || (s.equals(GetDomainFeatureParams.version.name()))
                    || (s.equals(GetDomainFeatureParams.domainFeatureID.name()))
                    || (s.equals(GetDomainFeatureParams.location.name()));
            return contained;
        }
    }

    /** enum with parameter names for getObservation request */
    public enum DescribeSensorParams {
        service, version, procedure, outputFormat, time;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(DescribeSensorParams.service.name()))
                    || (s.equals(DescribeSensorParams.version.name()))
                    || (s.equals(DescribeSensorParams.procedure.name()))
                    || (s.equals(DescribeSensorParams.outputFormat.name()))
                    || (s.equals(DescribeSensorParams.time.name()));
            return contained;
        }
    }

    /**
     * 
     * @author Christoph Stasch
     * 
     * @version 0.1
     */
    public enum ValueTypes {
        textType, numericType, spatialType, commonType, externalReferenceType, referenceValueTextType, referenceValueNumericType, referenceValueExternalReferenceType;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(ValueTypes.textType.name()))
                    || (s.equals(ValueTypes.numericType.name()))
                    || (s.equals(ValueTypes.spatialType.name()))
                    || (s.equals(ValueTypes.externalReferenceType.name()))
                    || (s.equals(ValueTypes.referenceValueTextType.name()))
                    || (s.equals(ValueTypes.referenceValueNumericType.name()))
                    || (s.equals(ValueTypes.referenceValueExternalReferenceType.name()));
            return contained;
        }
    }

    /**
     * 
     * @return SosConstants instance
     */
    public static synchronized SosConstants getInstance() {
        if (instance == null) {
            instance = new SosConstants();
        }

        return instance;
    }

    /**
     * possible resultTypes in getObservation request
     * 
     * @author Christoph Stasch
     * 
     */
    public static enum ResultType {
        results, hits;

        /**
         * method checks whether the string parameter is contained in this enumeration
         * 
         * @param s
         *        the name which should be checked
         * @return true if the name is contained in the enumeration
         */
        public static boolean contains(String s) {
            boolean contained = false;
            contained = (s.equals(ResultType.results.name())) || (s.equals(ResultType.hits.name()));
            return contained;
        }
    }

}
