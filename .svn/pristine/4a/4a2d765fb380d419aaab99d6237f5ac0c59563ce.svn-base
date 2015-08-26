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
 Modified: 8.11.2008
 ***************************************************************/

package org.n52.sos.decode.impl;

import net.opengis.gml.AbstractGeometryType;
import net.opengis.gml.AbstractSurfaceType;
import net.opengis.gml.CodeType;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.LocationPropertyType;
import net.opengis.gml.SurfacePropertyType;
import net.opengis.sampling.x10.SamplingPointType;
import net.opengis.sampling.x10.SamplingSurfaceType;
import net.opengis.sos.x10.DomainFeatureType;
import net.opengis.sos.x10.GenericDomainFeatureDocument;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.n52.sos.decode.IFeatureDecoder;
import org.n52.sos.decode.impl.utilities.GMLDecoder;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.features.domainFeatures.SosGenericDomainFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingPoint;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingSurface;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * class offers static methods for parsing features; currently only parsing of GenericDomainFeature and
 * GenericSamplingFeature is supported
 * 
 * @author Christoph Stasch
 * 
 */
public class FeatureDecoder implements IFeatureDecoder {
	
	 /** logger */
    private static Logger log = Logger.getLogger(FeatureDecoder.class);

    /**
     * parses sa:SamplingPoint feature and returns SOS representation of sa:SamplingPoint
     * 
     * @param xb_stationType
     *        XMLBeans representation of sa:SamplingPoint
     * @return SOS representation of sa:SamplingPoint
     * @throws OwsExceptionReport
     * 			if parsing sa:SamplingPoint failed
     */
    public SosSamplingPoint parseSamplingPoint(SamplingPointType xb_spType) throws OwsExceptionReport {
    	SosSamplingPoint samplingPoint = null;
        String id = xb_spType.getId();
        String name = "";
        String desc = "";
        try {

            // get name
            CodeType[] xb_nameArray = xb_spType.getNameArray();
            if (xb_nameArray.length == 1) {
                name = xb_nameArray[0].getStringValue();
            }

            // get description
            if (xb_spType.getDescription() != null) {
                desc = xb_spType.getDescription().getStringValue();
            }
            Point point = (Point)GMLDecoder.getGeometry4XmlGeometry(xb_spType.getPosition().getPoint());
            String fType = OMConstants.NS_SA_PREFIX + ":" + OMConstants.EN_SAMPLINGPOINT;
            String schemaLink = xb_spType.schemaType().getSourceName();
            samplingPoint = new SosSamplingPoint(id, name, desc, point, fType, schemaLink);
        }
        catch (OwsExceptionReport se) {
            throw se;
        }
        catch (Exception e) {
        	OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
          se.addCodedException(ExceptionCode.NoApplicableCode,
                               null,
                               "Missing or wrong elements for Feature of Interest: " + e.getStackTrace());
          log.error("Missing or wrong elements for Feature of Interest: " + e.getStackTrace());
		}
        return samplingPoint;
    }
    
    /**
     * parses sa:SamplingSurface feature and returns SOS representation of sa:SamplingSurface
     * 
     * @param xb_spType
     *        XMLBeans representation of sa:SamplingSurface
     * @return SOS representation of sa:SamplingSurface
     * @throws OwsExceptionReport
     * 			if parsing sa:SamplingSurface failed
     */
    public SosSamplingSurface parseSamplingSurface(SamplingSurfaceType xb_spType) throws OwsExceptionReport {
    	SosSamplingSurface samplingSurface;
    	String id = xb_spType.getId();
        String name = "";
        String desc = "";
        Geometry geom = null;
        try {

            // get name
            CodeType[] xb_nameArray = xb_spType.getNameArray();
            if (xb_nameArray.length == 1) {
                name = xb_nameArray[0].getStringValue();
            }

            // get description
            if (xb_spType.getDescription() != null) {
                desc = xb_spType.getDescription().getStringValue();
            }

            // get geometry
            if (xb_spType.getShape() != null) {
            	SurfacePropertyType xb_surfPropType = xb_spType.getShape();
                AbstractSurfaceType xb_agt = xb_surfPropType.getSurface();
                geom = GMLDecoder.getGeometry4XmlGeometry(xb_agt);
            }

            if (geom == null) {
            	samplingSurface = new SosSamplingSurface(id);
            }
            else {
            	samplingSurface = new SosSamplingSurface(id,
                                                 name,
                                                 desc,
                                                 (Polygon)geom,
                                                 OMConstants.NS_SA_PREFIX+":"+OMConstants.EN_SAMPLINGSURFACE,
                                                 null);
            }
        }
        catch (OwsExceptionReport se) {
            throw se;
        }
        catch (Exception e) {
        	   OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
             se.addCodedException(ExceptionCode.NoApplicableCode,
                                  null,
                                  "Missing or wrong elements for Feature of Interest: " + e.getStackTrace());
             log.error("Missing or wrong elements for Feature of Interest: " + e.getStackTrace());
             throw se;
		}
		return samplingSurface;
    }

    /**
     * parses generic domain feature and returns SOS representation of generic domain feature;
     * 
     * @param xb_fpType
     *        XMLBeans representation of generic domain feature
     * @return SOS representation of generic domain feature
     * @throws OwsExceptionReport
     */
    public SosGenericDomainFeature parseGenericDomainFeature(GenericDomainFeatureDocument xb_gdfDoc) throws OwsExceptionReport {

        DomainFeatureType xb_dft = null;
        SosGenericDomainFeature df = null;

        try {
            // TODO: parse domain feature directly using the FeaturePropertyType, enable validation of
            // document in PostRequestDecoderMobile
            // GenericDomainFeatureDocument xb_gfDoc =
            // GenericDomainFeatureDocument.Factory.parse(xb_fpType.xmlText());
			xb_dft = xb_gdfDoc.getGenericDomainFeature();


            String id = xb_dft.getId();
            String name = "";
            String desc = "";
            Geometry geom = null;

            // get name
            CodeType[] xb_nameArray = xb_dft.getNameArray();
            if (xb_nameArray.length == 1) {
                name = xb_nameArray[0].getStringValue();
            }

            // get description
            if (xb_dft.getDescription() != null) {
                desc = xb_dft.getDescription().getStringValue();
            }

            // get geometry
            if (xb_dft.getLocation() != null) {
                LocationPropertyType xb_lpt = xb_dft.getLocation();
                AbstractGeometryType xb_agt = xb_lpt.getGeometry();
                geom = GMLDecoder.getGeometry4XmlGeometry(xb_agt);
            }

            if (name.equalsIgnoreCase("") || desc.equalsIgnoreCase("") || geom == null) {
                df = new SosGenericDomainFeature(id);
            }
            else {
                df = new SosGenericDomainFeature(id,
                                                 name,
                                                 desc,
                                                 geom,
                                                 "sos:GenericDomainFeature",
                                                 null);
            }
        }
        catch (OwsExceptionReport se) {
			throw se;
		}
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Missing or wrong elements for Domain Feature: " + e.getStackTrace());
            log.error("Missing or wrong elements for Domain Feature: " + e.getStackTrace());
            throw se;
        }
        return df;
    }

    /**
     * parses generic domain feature and returns SOS representation of generic domain feature;
     * 
     * @param xb_fpType
     *        XMLBeans representation of generic domain feature
     * @return SOS representation of generic domain feature
     * @throws OwsExceptionReport
     */
    public SosGenericDomainFeature parseGenericDomainFeature(FeaturePropertyType xb_fpType) throws OwsExceptionReport {

        SosGenericDomainFeature df = null;
        
        	// TODO: parse domain feature directly using the FeaturePropertyType, enable validation of
            // document in PostRequestDecoderMobile
        	GenericDomainFeatureDocument xb_gfDoc = null;
			try {
				xb_gfDoc = GenericDomainFeatureDocument.Factory.parse(xb_fpType.xmlText());
			} catch (XmlException xmle) {
				OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
	            se.addCodedException(ExceptionCode.NoApplicableCode, null, xmle);
	            throw se;
			}
			
            df =  parseGenericDomainFeature(xb_gfDoc);
            return df;
    }
    
}
