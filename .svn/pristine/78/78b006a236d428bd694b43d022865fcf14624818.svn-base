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

 Author: Christoph Stasch, Stephan Kuenster
 Created: <CREATION DATE>
 Modified: 08/11/2008
 ***************************************************************/

package org.n52.sos.decode.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractFeatureCollectionType;
import net.opengis.gml.AbstractFeatureType;
import net.opengis.gml.AbstractGeometryType;
import net.opengis.gml.AbstractRingPropertyType;
import net.opengis.gml.AbstractRingType;
import net.opengis.gml.AbstractTimeObjectType;
import net.opengis.gml.CoordinatesType;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.GeometryPropertyType;
import net.opengis.gml.LineStringType;
import net.opengis.gml.LinearRingType;
import net.opengis.gml.MeasureType;
import net.opengis.gml.PointType;
import net.opengis.gml.PolygonType;
import net.opengis.gml.TimeInstantType;
import net.opengis.om.x10.CategoryObservationType;
import net.opengis.om.x10.GeometryObservationType;
import net.opengis.om.x10.MeasurementType;
import net.opengis.om.x10.ObservationType;
import net.opengis.om.x10.ProcessPropertyType;
import net.opengis.sampling.x10.SamplingPointDocument;
import net.opengis.sampling.x10.SamplingSurfaceDocument;
import net.opengis.sos.x10.GenericDomainFeatureDocument;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.PhenomenonPropertyType;
import net.opengis.swe.x101.QualityPropertyType;
import net.opengis.swe.x101.ScopedNameType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.TimeObjectPropertyType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.decode.IOMDecoder;
import org.n52.sos.decode.impl.utilities.GMLDecoder;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.SosCategoryObservation;
import org.n52.sos.ogc.om.SosMeasurement;
import org.n52.sos.ogc.om.SosSpatialObservation;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.SosXmlFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosGenericSamplingFeature;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.quality.SosQuality.QualityType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * class encapsulates static operations for mapping XMLBeans representations of O&M xml documents to SOSmobile
 * representations; currently only parsing of XMLBeans representation of om:Measurement is supported
 * 
 * @author Christoph Stasch
 * 
 */
public class OMDecoder implements IOMDecoder{

    /**
     * logger, used for logging while initialising the constants from config file
     */
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(OMDecoder.class);

	/**
	 * Feature Decoder
	 */
	private FeatureDecoder featureDecoder = new FeatureDecoder();

    /**
     * maps XMLBeans om:Measurement representation to SosMeasurement; current implementation: 1. only
     * gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosMeasurements is returned, because there is a one-to-many relationship in the database
     * bet- ween offering and observation table. In reality, this is an m:n to relationship, but to avoid a
     * join with offering table for each GetObservation request, the measurement is inserted once for each
     * offering
     * 
     * @param xb_measType
     *        XMLBeans representation of om:Measurement, which should be parsed
     * @return Returns SosMeasurement
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseMeasurement(MeasurementType xb_measType,
                                                                      boolean mobileEnabled) throws OwsExceptionReport {

        // parse procedure
        ProcessPropertyType xb_proc = xb_measType.getProcedure();
        String procID = xb_proc.getHref();

        // parse phenomenon (observedProperty)
        PhenomenonPropertyType xb_phen = xb_measType.getObservedProperty();
        String phenID = xb_phen.getHref();

        // parse samplingTime
        ISosTime samplingTime = null;
        TimeObjectPropertyType xb_samplingTime = xb_measType.getSamplingTime();
        AbstractTimeObjectType xb_absTime = xb_samplingTime.getTimeObject();
        if (xb_absTime instanceof TimeInstantType) {
            try {
                samplingTime = GMLDecoder.parseTimeInstant((TimeInstantType) xb_absTime);
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Error while parsing samplingTime of Measurement in insertObservation request: "
                                             + pe.getMessage());
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Currently only TimeInstant is supported for samplingTime in Observation of insertObservation request");
            throw se;
        }

        // parse samplingFeature
        SosAbstractFeature foi = null;
        FeaturePropertyType featurePropType = xb_measType.getFeatureOfInterest();
        
        /*
         * workaround for parsing foi, because XmlBeans does not set Feature of FeaturePropertyType
         */
        XmlObject object = null;
        try {
			object = XmlObject.Factory.parse(featurePropType.toString());
		} catch (XmlException xmle) {
			OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Error while parsing feature of interest of Measurement in insertObservation request: "
                                         + xmle.getMessage());
            throw se;
		}
		
		if (object instanceof SamplingPointDocument){
			SamplingPointDocument xb_spDoc = (SamplingPointDocument) object;
			foi = featureDecoder.parseSamplingPoint(xb_spDoc.getSamplingPoint());
		}
		
		else if (object instanceof SamplingSurfaceDocument){
			SamplingSurfaceDocument xb_ssDoc = (SamplingSurfaceDocument)object;
			foi = featureDecoder.parseSamplingSurface(xb_ssDoc.getSamplingSurface());
		}
		
		else {
			SosXmlFeature xmlFoi = new SosXmlFeature("");
			xmlFoi.setXmlFeatureObject(object);
			foi = xmlFoi;
		}
		
		// domain features
		SosAbstractFeature domFeat = null;
        Collection<SosAbstractFeature> df_col = new ArrayList<SosAbstractFeature>();

        if (mobileEnabled) {
        	FeaturePropertyType[] xb_domainFeatures = xb_measType.getDomainFeatureArray();
            if (xb_domainFeatures.length > 0) {
                df_col = new ArrayList<SosAbstractFeature>();
                for (FeaturePropertyType xb_df : xb_domainFeatures) {
                    domFeat = featureDecoder.parseGenericDomainFeature(xb_df);
                    df_col.add(domFeat);
                }
            }
        }

        // result

        double value = Double.NaN;
        try {
            MeasureType xb_result = (MeasureType) xb_measType.getResult();
            String valueString = xb_result.getStringValue();
            value = Double.parseDouble(valueString);
        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null, 
                                 "Error while parsing result value of Measurement in insertObservation request: "
                                         + e.getMessage());
            throw se;
        }

        Collection<AbstractSosObservation> measurements = new ArrayList<AbstractSosObservation>();

        List<String> offeringIDs = SosConfigurator.getInstance().getCapsCacheController().getOfferings4Phenomenon(phenID);
        Iterator<String> offIter = offeringIDs.iterator();
        
        
        List<String> procIDs;
        
        if (offeringIDs.size() > 0) {
            while (offIter.hasNext()) {
                String offeringID = offIter.next();
                
                procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);
                if (procIDs.size() > 0){
					if ( procIDs.contains(procID) ){
		                SosMeasurement sosMeas = new SosMeasurement(samplingTime,
		                                                            null,
		                                                            procID,
		                                                            df_col,
		                                                            phenID,
		                                                            foi,
		                                                            offeringID,
		                                                            null,
		                                                            value,
		                                                            null,
		                                                            null);
		
		                measurements.add(sosMeas);
					}
                }
            }
            if (measurements.isEmpty()) {
            	OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "No procedures are contained in db for offerings'");
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No offering is contained in db for phenomenon '" + phenID + "'");
            throw se;
        }
        return measurements;
    }
    
    /**
     * maps XMLBeans om:CategoryObservation representation to SosCategoryObservation; current implementation:
     * 1. only gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for
     * samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosCategoryObservations is returned, because there is a one-to-many relationship in the
     * database between offering and observation table. In reality, this is an m:n to relationship, but to
     * avoid a join with offering table for each GetObservation request, the categoryObservation is inserted
     * once for each offering
     * 
     * @param xb_catObsType
     *        XMLBeans representation of om:CategoryObservation, which should be parsed
     * @return Returns SosCategoryObservation
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseCategoryObservation(CategoryObservationType xb_catObsType,
                                                                      boolean mobileEnabled) throws OwsExceptionReport {

        // parse procedure
        ProcessPropertyType xb_proc = xb_catObsType.getProcedure();
        String procID = xb_proc.getHref();

        // parse phenomenon (observedProperty)
        PhenomenonPropertyType xb_phen = xb_catObsType.getObservedProperty();
        String phenID = xb_phen.getHref();

        // parse samplingTime
        ISosTime samplingTime = null;
        TimeObjectPropertyType xb_samplingTime = xb_catObsType.getSamplingTime();
        AbstractTimeObjectType xb_absTime = xb_samplingTime.getTimeObject();
        if (xb_absTime instanceof TimeInstantType) {
            try {
                samplingTime = GMLDecoder.parseTimeInstant((TimeInstantType) xb_absTime);
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Error while parsing samplingTime of Measurement in insertObservation request: "
                                             + pe.getMessage());
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Currently only TimeInstant is supported for samplingTime in Observation of insertObservation request");
            throw se;
        }

        // parse samplingFeature
        SosAbstractFeature foi = null;
        FeaturePropertyType featurePropType = xb_catObsType.getFeatureOfInterest();
        
        /*
         * workaround for parsing foi, because XmlBeans does not set Feature of FeaturePropertyType
         */
        XmlObject object = null;
        try {
			object = XmlObject.Factory.parse(featurePropType.toString());
		} catch (XmlException xmle) {
			OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Error while parsing feature of interest of CategoryObservation in insertObservation request: "
                                         + xmle.getMessage());
            throw se;
		}
		
		if (object instanceof SamplingPointDocument){
			SamplingPointDocument xb_spDoc = (SamplingPointDocument) object;
			foi = featureDecoder.parseSamplingPoint(xb_spDoc.getSamplingPoint());
		}
		
		else if (object instanceof SamplingSurfaceDocument){
			SamplingSurfaceDocument xb_ssDoc = (SamplingSurfaceDocument)object;
			foi = featureDecoder.parseSamplingSurface(xb_ssDoc.getSamplingSurface());
		}
		
		// domain features
		SosAbstractFeature domFeat = null;
        Collection<SosAbstractFeature> df_col = new ArrayList<SosAbstractFeature>();

        if (mobileEnabled) {
        	FeaturePropertyType[] xb_domainFeatures = xb_catObsType.getDomainFeatureArray();
            if (xb_domainFeatures.length > 0) {
                df_col = new ArrayList<SosAbstractFeature>();
                for (FeaturePropertyType xb_df : xb_domainFeatures) {
                    domFeat = featureDecoder.parseGenericDomainFeature(xb_df);
                    df_col.add(domFeat);
                }
            }
        }

        // result
        ScopedNameType xb_result = (ScopedNameType) xb_catObsType.getResult();
        String value = xb_result.getStringValue();

        Collection<AbstractSosObservation> catObs = new ArrayList<AbstractSosObservation>();

        List<String> offeringIDs = SosConfigurator.getInstance().getCapsCacheController().getOfferings4Phenomenon(phenID);
        Iterator<String> offIter = offeringIDs.iterator();
        
        
        List<String> procIDs;
        
        if (offeringIDs.size() > 0) {
            while (offIter.hasNext()) {
                String offeringID = offIter.next();
                
                procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);
                if (procIDs.size() > 0){
					if ( procIDs.contains(procID) ){
		                SosCategoryObservation sosCatObs = new SosCategoryObservation(samplingTime,
		                                                            null,
		                                                            procID,
		                                                            foi,
		                                                            df_col,
		                                                            phenID,
		                                                            offeringID,
		                                                            null,
		                                                            value, //CO String, M double
		                                                            null,
		                                                            null);
		
		                catObs.add(sosCatObs);
					}
                }
                
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No offering is contained in db for phenomenon '" + phenID + "'");
            throw se;
        }
        return catObs;
    }

    /**
     * maps XMLBeans om:SpatialObservation representation to SosSpatialObservation; current implementation: 1.
     * only gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for
     * samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosSpatialObservations is returned, because there is a one-to-many relationship in the
     * database between offering and observation table. In reality, this is an m:n to relationship, but to
     * avoid a join with offering table for each GetObservation request, the spatialObservation is inserted
     * once for each offering
     * 
     * @param xb_geomObsType
     *        XMLBeans representation of om:SpatialObservation, which should be parsed
     * @return Returns om:SosSpatialObservation
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseSpatialObservation(GeometryObservationType xb_geomObsType,
                                                                             boolean mobileEnabled) throws OwsExceptionReport {
        // parse procedure
        ProcessPropertyType xb_proc = xb_geomObsType.getProcedure();
        String procID = xb_proc.getHref();

        // parse phenomenon (observedProperty)
        PhenomenonPropertyType xb_phen = xb_geomObsType.getObservedProperty();
        String phenID = xb_phen.getHref();

        // parse samplingTime
        ISosTime samplingTime = null;
        TimeObjectPropertyType xb_samplingTime = xb_geomObsType.getSamplingTime();
        AbstractTimeObjectType xb_absTime = xb_samplingTime.getTimeObject();
        if (xb_absTime instanceof TimeInstantType) {
            try {
                samplingTime = GMLDecoder.parseTimeInstant((TimeInstantType) xb_absTime);
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Error while parsing samplingTime of Measurement in insertObservation request: "
                                             + pe.getMessage());
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Currently only TimeInstant is supported for samplingTime in Observation of insertObservation request");
            throw se;
        }

	    // result (needed to parse samplingFeature movingObject)
	    Geometry value = null;
	
	    GeometryPropertyType xb_gpt = (GeometryPropertyType) xb_geomObsType.getResult();
	    AbstractGeometryType xb_agt = xb_gpt.getGeometry();
	
	    String srsNamePrefix = SosConfigurator.getInstance().getSrsNamePrefix();
	
	    if (xb_agt instanceof PointType) {
	
	        PointType xb_pt = (PointType) xb_agt;
	        CoordinatesType xb_ct = xb_pt.getCoordinates();
	        String coords = xb_ct.getStringValue();
	        String[] coordString = coords.split(" ");
	        Coordinate jts_coord = new Coordinate(Double.parseDouble(coordString[0]),
	                                              Double.parseDouble(coordString[1]));
	
	        GeometryFactory gf = new GeometryFactory();
	        value = gf.createPoint(jts_coord);
	        String srid = xb_pt.getSrsName();
	        srid = srid.replace(srsNamePrefix, "");
	        value.setSRID(Integer.parseInt(srid));
	
	    }
	    else if (xb_agt instanceof LineStringType) {
	
	        LineStringType xb_lst = (LineStringType) xb_agt;
	        CoordinatesType xb_ct = xb_lst.getCoordinates();
	        String coords = xb_ct.getStringValue();
	        String[] coordString = coords.split(",");
	
	        Coordinate[] lsCoords = new Coordinate[coordString.length];
	
	        for (int i = 0; i < coordString.length; i++) {
	            if (coordString[i].startsWith(" ")) {
	                coordString[i] = coordString[i].replaceFirst(" ", "");
	            }
	            String[] pointCoords = coordString[i].split(" ");
	            Coordinate jts_coord = new Coordinate(Double.parseDouble(pointCoords[0]),
	                                                  Double.parseDouble(pointCoords[1]));
	            lsCoords[i] = jts_coord;
	        }
	
	        GeometryFactory gf = new GeometryFactory();
	        value = gf.createLineString(lsCoords);
	        String srid = xb_lst.getSrsName();
	        srid = srid.replace(srsNamePrefix, "");
	        value.setSRID(Integer.parseInt(srid));
	
	    }
	    else if (xb_agt instanceof PolygonType) {
	
	        PolygonType xb_pgt = (PolygonType) xb_agt;
	        AbstractRingPropertyType xb_arpt = xb_pgt.getExterior();
	        AbstractRingType xb_art = xb_arpt.getRing();
	        if (xb_art instanceof LinearRingType) {
	            LinearRingType xb_lrt = (LinearRingType) xb_art;
	            CoordinatesType xb_ct = xb_lrt.getCoordinates();
	            String coords = xb_ct.getStringValue();
	            String[] coordString = coords.split(",");
	
	            Coordinate[] lsCoords = new Coordinate[coordString.length];
	
	            for (int i = 0; i < coordString.length; i++) {
	                if (coordString[i].startsWith(" ")) {
	                    coordString[i] = coordString[i].replaceFirst(" ", "");
	                }
	                String[] pointCoords = coordString[i].split(" ");
	                Coordinate jts_coord = new Coordinate(Double.parseDouble(pointCoords[0]),
	                                                      Double.parseDouble(pointCoords[1]));
	                lsCoords[i] = jts_coord;
	            }
	
	            GeometryFactory gf = new GeometryFactory();
	
	            value = gf.createPolygon(gf.createLinearRing(lsCoords), null);
	            String srid = xb_pgt.getSrsName();
	            srid = srid.replace(srsNamePrefix, "");
	            value.setSRID(Integer.parseInt(srid));
	        }
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
	                                 null,
	                                 "Error: only LinearRing is supported for polygon exterior by this SOS!");
	            throw se;
	        }
	
	    }
	    else {
	        OwsExceptionReport se = new OwsExceptionReport();
	        se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
	                             null,
	                             "Error: geometry type is not supported by this SOS!");
	        throw se;
	    }

        // parse samplingFeature
        SosAbstractFeature foi = null;
                
        if (xb_geomObsType.getFeatureOfInterest() != null) {
        	
        	FeaturePropertyType featurePropType = xb_geomObsType.getFeatureOfInterest();
        	
        	XmlCursor cursor = featurePropType.newCursor();
        	
        	if (cursor.toChild("http://www.opengis.net/sampling/1.0", "SamplingPoint")) {
        		
        		// parse SamplingPoint
        		SamplingPointDocument xb_spDoc = null;
    	        try {
    	        	xb_spDoc = SamplingPointDocument.Factory.parse(featurePropType.toString());       	
    	        }
    	        catch (XmlException xmle) {
    	            OwsExceptionReport se = new OwsExceptionReport();
    	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
    	                                 null,
    	                                 "Error while parsing feature of interest in insertObservation request: "
    	                                         + xmle.getMessage());
    	            throw se;
    	        }
    	        
    	        foi = featureDecoder.parseSamplingPoint(xb_spDoc.getSamplingPoint());
        		
        	} else if (cursor.toChild("http://www.opengis.net/sampling/1.0", "SamplingSurface")) {
        		
        		// parse SamplingPoint
        		SamplingSurfaceDocument xb_ssDoc = null;
    	        try {
    	        	xb_ssDoc = SamplingSurfaceDocument.Factory.parse(featurePropType.toString());       	
    	        }
    	        catch (XmlException xmle) {
    	            OwsExceptionReport se = new OwsExceptionReport();
    	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
    	                                 null,
    	                                 "Error while parsing feature of interest in insertObservation request: "
    	                                         + xmle.getMessage());
    	            throw se;
    	        }
    	        
    	        foi = featureDecoder.parseSamplingSurface(xb_ssDoc.getSamplingSurface());
    	        
        	} else if (cursor.toChild("http://www.52north.org/1.0", "movingObject")) {
        		
        		// build SamplingFeature from movingObject name
        		String featureID = cursor.getAttributeText(new QName("http://www.opengis.net/gml", "id"));
        		SosGenericSamplingFeature genericSampFeat = new SosGenericSamplingFeature(featureID);
        		
        		genericSampFeat.setFeatureType(cursor.getDomNode().getNamespaceURI() + cursor.getName());
        		genericSampFeat.setName(genericSampFeat.getElementName());
        		genericSampFeat.setApplicationSchemaType(cursor.getDomNode().getNamespaceURI());
        		
        		// use SRID and Geometry from result
        		genericSampFeat.setEpsgCode(value.getSRID());
        		genericSampFeat.setGeom(value);
        		        		
        		foi = genericSampFeat;
        	}

        }
        
		// domain features
		SosAbstractFeature domFeat = null;
        Collection<SosAbstractFeature> df_col = new ArrayList<SosAbstractFeature>();

        if (mobileEnabled) {
        	FeaturePropertyType[] xb_domainFeatures = xb_geomObsType.getDomainFeatureArray();
            if (xb_domainFeatures.length > 0) {
                df_col = new ArrayList<SosAbstractFeature>();
                for (FeaturePropertyType xb_df : xb_domainFeatures) {
                    domFeat = featureDecoder.parseGenericDomainFeature(xb_df);
                    df_col.add(domFeat);
                }
            }
        }

        Collection<AbstractSosObservation> spatialObs = new ArrayList<AbstractSosObservation>();

        List<String> offeringIDs = SosConfigurator.getInstance().getCapsCacheController().getOfferings4Phenomenon(phenID);
        Iterator<String> offIter = offeringIDs.iterator();
        
                
        List<String> procIDs;
        
        if (offeringIDs.size() > 0) {
            while (offIter.hasNext()) {
                String offeringID = offIter.next();
                
                procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);
				if (procIDs.size() > 0){
					if ( procIDs.contains(procID) ){
						SosSpatialObservation sosSpatialObs = new SosSpatialObservation(samplingTime,
                                                                                null,
                                                                                procID,
                                                                                df_col,
                                                                                phenID,
                                                                                foi,
                                                                                offeringID,
                                                                                value,
                                                                                null);

						spatialObs.add(sosSpatialObs);
					}
				} 
				
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No offering is contained in db for phenomenon '" + phenID + "'");
            throw se;
        }

        return spatialObs;
    }
    
    /**
     * maps XMLBeans om:Measurement representation to SosMeasurement or om:CategoryObservation to
     * SosCategoryObservation depending on value type in the generic observation; current implementation: 1.
     * only gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for
     * samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosMeasurements is returned, because there is a one-to-many relationship in the database
     * bet- ween offering and observation table. In reality, this is an m:n to relationship, but to avoid a
     * join with offering table for each GetObservation request, the measurement or category is inserted once
     * for each offering
     * 
     * @param xb_obsType
     *        XMLBeans representation of om:Observation, which should be parsed
     * @return Returns Collection of AbstractSosObservation
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseGenericObservation(ObservationType xb_obsType,
                                                                      boolean mobileEnabled) throws OwsExceptionReport {

        Collection<AbstractSosObservation> obs = new ArrayList<AbstractSosObservation>();

        // general observation attributes
        
        //procedure
        if (xb_obsType.getProcedure() == null 
                || !xb_obsType.getProcedure().isSetHref()
                || xb_obsType.getProcedure().getHref().equals("")){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Procedure is not specified.");
            throw se;            
        }                
            
        String procId = xb_obsType.getProcedure().getHref();
        
        if (!SosConfigurator.getInstance().getCapsCacheController().getProcedures().containsKey(procId)){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Procedure is not registered in this SOS: "
                                         + procId);
            throw se;
        }

        // composite phenomena
//        List<String> phenComponents = new ArrayList<String>();
//        PhenomenonPropertyType xb_obsProp = xb_obsType.getObservedProperty();
//        if (xb_obsProp.getPhenomenon() instanceof CompositePhenomenonType) {
//            CompositePhenomenonType xb_compPhen = (CompositePhenomenonType) xb_obsProp.getPhenomenon();
//            for (PhenomenonPropertyType c : xb_compPhen.getComponentArray()) {
//                // do not add the time phen, because it is added by
//                // OMEncoder
//                if ( !c.getHref().equalsIgnoreCase(OMConstants.PHEN_ID_ISO8601))
//                    phenComponents.add(c.getHref());
//            }
//        }

        // Sampling Time
//        TimeObjectPropertyType xb_topt = xb_obsType.getSamplingTime();
//        XmlCursor timeCursor = xb_topt.newCursor();
//        boolean isTimeInstant;
//        boolean isTimePeriod;
//        QName timeInstantName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT);
//        QName timePeriodName = new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD);
//        @SuppressWarnings("unused")
//        ISosTime sosTime = null;
//
//        try {
//            isTimeInstant = timeCursor.toChild(timeInstantName);
//            if (isTimeInstant) {
//
//                sosTime = GMLDecoder.parseTimeInstantNode(timeCursor.getDomNode());
//            }
//
//            isTimePeriod = timeCursor.toChild(timePeriodName);
//            if (isTimePeriod) {
//                sosTime = GMLDecoder.parseTimePeriod((TimePeriodType) xb_topt.getTimeObject());
//            }
//        }
//        catch (Exception e) {
//            log.error("Error while parsing eventTime of Observation response: " + e.getMessage());
//            throw new OwsExceptionReport(e.getCause());
//        }

        // FOI        
        if (xb_obsType.getFeatureOfInterest() == null 
                || xb_obsType.getFeatureOfInterest().getFeature() == null){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "FeatureOfInterest is not specified.");
            throw se;            
        } 
        
        FeaturePropertyType xb_fpt = xb_obsType.getFeatureOfInterest();
        AbstractFeatureType xb_aft = xb_obsType.getFeatureOfInterest().getFeature();
        List<SosAbstractFeature> saps = new ArrayList<SosAbstractFeature>();
        List<String> foiIds = new ArrayList<String>();

        XmlObject object = null;

        try {
            object = XmlObject.Factory.parse(xb_fpt.toString());
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "decode(ObservationCollectionDocument obsColDoc, String resultModel)",
                                 "Error while parsing feature of interest: " + xmle.getMessage());
            throw se;
        }

        // SamplingPoint
        if (object instanceof SamplingPointDocument) {
            SamplingPointDocument xb_spDoc = (SamplingPointDocument) object;
            saps.add(featureDecoder.parseSamplingPoint(xb_spDoc.getSamplingPoint()));

            foiIds.add(xb_spDoc.getSamplingPoint().getId());
        }

        // SamplingSurface
        else if (object instanceof SamplingSurfaceDocument) {
            SamplingSurfaceDocument xb_ssDoc = (SamplingSurfaceDocument) object;
            saps.add(featureDecoder.parseSamplingSurface(xb_ssDoc.getSamplingSurface()));

            foiIds.add(xb_ssDoc.getSamplingSurface().getId());
        }

        // FeatureCollection
        else if (xb_aft instanceof AbstractFeatureCollectionType) {
            AbstractFeatureCollectionType xb_afct = (AbstractFeatureCollectionType) xb_aft;
            for (FeaturePropertyType xb_m : xb_afct.getFeatureMemberArray()) {

                try {
                    object = XmlObject.Factory.parse(xb_m.toString());
                }
                catch (XmlException xmle) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         "decode(ObservationCollectionDocument obsColDoc, String resultModel)",
                                         "Error while parsing feature of interest: "
                                                 + xmle.getMessage());
                    throw se;
                }

                // SamplingPoint
                if (object instanceof SamplingPointDocument) {
                    SamplingPointDocument xb_spDoc = (SamplingPointDocument) object;
                    saps.add(featureDecoder.parseSamplingPoint(xb_spDoc.getSamplingPoint()));

                    foiIds.add(xb_spDoc.getSamplingPoint().getId());
                }

                // Sampling Surface
                else if (object instanceof SamplingSurfaceDocument) {
                    SamplingSurfaceDocument xb_ssDoc = (SamplingSurfaceDocument) object;
                    saps.add(featureDecoder.parseSamplingSurface(xb_ssDoc.getSamplingSurface()));

                    foiIds.add(xb_ssDoc.getSamplingSurface().getId());
                }
            }
        }

        // domain features
        Collection<SosAbstractFeature> df_col = new ArrayList<SosAbstractFeature>();

        if (mobileEnabled) {
            // TODO: dirty hack to get domain feature!! Check, whether this
            // could be implemented more nice in
            // future...
            XmlCursor cursor = xb_obsType.newCursor();
            boolean hasDf = cursor.toChild(new QName(OMConstants.NS_OM,
                                                     OMConstants.EN_DOMAIN_FEATURE));
            if (hasDf) {
                String completeDF = cursor.xmlText();

                String s[] = completeDF.split("</GenericDomainFeature>");
                if (s.length > 2) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         null,
                                         "Error while parsing domain feature in insertObservation request. Only one domain feature is supported by this SOS.");
                    throw se;
                }
                else {
                    completeDF = completeDF.replace("sos:", "");
                    int seperatorGen = completeDF.indexOf("<Generic");
                    int seperatorClosingTag = completeDF.indexOf(">");
                    String namespaces = completeDF.substring(17, seperatorClosingTag);

                    completeDF = completeDF.substring(seperatorGen);
                    completeDF = completeDF.replace("</om:" + OMConstants.EN_DOMAIN_FEATURE + ">",
                                                    "");
                    completeDF = completeDF.replace("<GenericDomainFeature ",
                                                    "<GenericDomainFeature " + namespaces + " ");

                    if (completeDF.contains("xmlns:sos")
                            && !completeDF.contains("xmlns=\"http://www.opengis.net/sos/1.0\"")) {
                        completeDF = completeDF.replace("xmlns:sos", "xmlns");
                    }
                    GenericDomainFeatureDocument xb_gdfDoc = null;
                    try {
                        xb_gdfDoc = GenericDomainFeatureDocument.Factory.parse(completeDF);
                    }
                    catch (XmlException xmle) {
                        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                        se.addCodedException(ExceptionCode.NoApplicableCode, null, xmle);
                        throw se;
                    }

                    df_col.add(featureDecoder.parseGenericDomainFeature(xb_gdfDoc));
                }
            }
        }

        // SosQuality
        Collection<SosQuality> sq = null;
        if (xb_obsType.isSetResultQuality()) {
            sq = new ArrayList<SosQuality>();
            QualityPropertyType qpt = (QualityPropertyType) xb_obsType.getResultQuality();
            sq.add(new SosQuality(qpt.getQuantity().getNameArray(0).getStringValue(),
                                  qpt.getQuantity().getUom().getCode(),
                                  String.valueOf(qpt.getQuantity().getValue()),
                                  QualityType.quantity));
            sq.add(new SosQuality(qpt.getCategory().getNameArray(0).getStringValue(),
                                  qpt.getCategory().getCodeSpace().getType(),
                                  String.valueOf(qpt.getCategory().getValue()),
                                  QualityType.category));
            sq.add(new SosQuality(qpt.getText().getNameArray(0).getStringValue(),
                                  null,
                                  String.valueOf(qpt.getText().getValue()),
                                  QualityType.text));
        }
        
        // result
        if (xb_obsType.getResult() == null ){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Result is not specified.");
            throw se;            
        } 

        // parsing the result
        // XMLBeans: Why in hell doesn't that work:
        // DataArrayType xb_dat = (DataArrayType) xb_ot.getResult();
        // SimpleDataRecordType xb_sdrt = (SimpleDataRecordType)
        // xb_dat.getElementType();

        // so we have to do this in that stupid way...
        XmlCursor cResult = xb_obsType.getResult().newCursor();
        if (!cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"))){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "DataArray in Result is not specified.");
            throw se;
        }
        DataArrayDocument xb_dataArrayDoc = DataArrayDocument.Factory.newInstance();

        try {
            xb_dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
        }
        catch (XmlException e) {
            throw new OwsExceptionReport(e.getCause());
        }

        SimpleDataRecordType dataRecord = (SimpleDataRecordType) xb_dataArrayDoc.getDataArray1().getElementType().getAbstractDataRecord();

        AnyScalarPropertyType[] fieldArray = dataRecord.getFieldArray();

        // evaluate the components
        int tstIdx = -1;
        int foiIdx = -1;

        int i = 0;
        Map<Integer, String> phens = new HashMap<Integer, String>();
        Map<String, String> units4Phens = new HashMap<String, String>();
        Map<String, ValueTypes> valueTypes4Phens = new HashMap<String, ValueTypes>();

        for (AnyScalarPropertyType s : fieldArray) {
            if (s.getName().equalsIgnoreCase("Time"))
                tstIdx = i;
            else if (s.getName().equalsIgnoreCase("feature"))
                foiIdx = i;
            else {
                // value types and units for phenomenon
                if (s.isSetQuantity()) {
                    phens.put(i, s.getQuantity().getDefinition());
                    if ( !valueTypes4Phens.keySet().contains(s.getQuantity().getDefinition())) {
                        valueTypes4Phens.put(s.getQuantity().getDefinition(),
                                             SosConstants.ValueTypes.numericType);
                        units4Phens.put(s.getQuantity().getDefinition(),
                                        s.getQuantity().getUom().getCode());
                    }
                }
                else if (s.isSetCategory()) {
                    phens.put(i, s.getCategory().getDefinition());
                    if ( !valueTypes4Phens.keySet().contains(s.getCategory().getDefinition()))
                        valueTypes4Phens.put(s.getCategory().getDefinition(),
                                             SosConstants.ValueTypes.textType);
                }
                else if (s.isSetText()) {
                    phens.put(i, s.getText().getDefinition());
                    // only numericType and textType important for
                    // current OMEncoder
                    if ( !valueTypes4Phens.keySet().contains(s.getText().getDefinition()))
                        valueTypes4Phens.put(s.getText().getDefinition(),
                                             SosConstants.ValueTypes.commonType);
                }
            }
            i++;
        }
        
        if (tstIdx < 0){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Time is not specified in SimpleDataRecord.");
            throw se;            
        } 
        
        if (foiIdx < 0){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Feature is not specified in SimpleDataRecord.");
            throw se;            
        } 

        // evaluate the separators of the SOS instance
        String tokenSepLocal = xb_dataArrayDoc.getDataArray1().getEncoding().getTextBlock().getTokenSeparator();
        String blockSepLocal = xb_dataArrayDoc.getDataArray1().getEncoding().getTextBlock().getBlockSeparator();

        String resultText = xb_dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().getNodeValue();

        String[] tupels = resultText.trim().split(blockSepLocal);
        
        if (tupels.length == 0){
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No values specified in DataRecord.");
            throw se;            
        }

        for (String t : tupels) {
            String[] v = t.split(tokenSepLocal);
            
            if (v.length != phens.size() + 2){
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Invalid number of data values in tuple: " + t);
                throw se;            
            }
            
            DateTime tst;            
            try {
                tst = new DateTime(v[tstIdx].trim());
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Invalid date format: " + v[tstIdx].trim());
                throw se;  
            }
            
            String foi = v[foiIdx];
            for (int j : phens.keySet()) {
                int foi_ = foiIds.indexOf(foi);
                
                if (foi_ < 0){
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         null,
                                         "The Feature Of Interest id is not valid: "
                                                 + foi);
                    throw se;
                }
                
                SosAbstractFeature sap = saps.get(foi_);

                AbstractSosObservation aso = null;

                // numeric --> Measurement
                if (valueTypes4Phens.get(phens.get(j)).name().equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                    double val = Double.NaN;
                    if (v[j] != null
                            && !v[j].equalsIgnoreCase(SosConfigurator.getInstance().getNoDataValue()))
                        val = Double.parseDouble(v[j]);

                    List<String> offeringIDs = SosConfigurator.getInstance().getCapsCacheController().getOfferings4Phenomenon(phens.get(j));
                    Iterator<String> offIter = offeringIDs.iterator();

                    List<String> procIDs;

                    if (offeringIDs.size() > 0) {

                        while (offIter.hasNext()) {
                            String offeringID = offIter.next();

                            procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);

                            if (procIDs.size() > 0) {

                                if (procIDs.contains(procId)) {

                                    aso = new SosMeasurement(new TimeInstant(tst, null),
                                                             null,
                                                             procId,
                                                             df_col,
                                                             phens.get(j),
                                                             sap,
                                                             offeringID,
                                                             null,
                                                             val,
                                                             units4Phens.get(phens.get(j)),
                                                             sq);

                                    obs.add(aso);
                                }
                            }

                        }

                    }
                    else {

                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             null,
                                             "No offering is contained in db for phenomenon '"
                                                     + phens.get(j) + "'");
                        throw se;
                    }
                }

                // text --> Category
                else if (valueTypes4Phens.get(phens.get(j)).name().equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {

                    List<String> offeringIDs = SosConfigurator.getInstance().getCapsCacheController().getOfferings4Phenomenon(phens.get(j));
                    Iterator<String> offIter = offeringIDs.iterator();

                    List<String> procIDs;

                    if (offeringIDs.size() > 0) {

                        while (offIter.hasNext()) {
                            String offeringID = offIter.next();

                            procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);

                            if (procIDs.size() > 0) {

                                if (procIDs.contains(procId)) {

                                    aso = new SosCategoryObservation(new TimeInstant(tst, null),
                                                                     null,
                                                                     procId,
                                                                     sap,
                                                                     df_col,
                                                                     phens.get(j),
                                                                     offeringID,
                                                                     null,
                                                                     v[j],
                                                                     null,
                                                                     sq);

                                    obs.add(aso);
                                }
                            }

                        }

                    }
                    else {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             null,
                                             "No offering is contained in db for phenomenon '"
                                                     + phens.get(j) + "'");
                        throw se;
                    }

                }

            }
        }

        return obs;
    }

}
