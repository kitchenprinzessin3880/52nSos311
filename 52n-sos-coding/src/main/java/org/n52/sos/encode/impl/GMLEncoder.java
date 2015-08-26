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

package org.n52.sos.encode.impl;

import java.text.ParseException;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractFeatureCollectionType;
import net.opengis.gml.AbstractFeatureType;
import net.opengis.gml.AbstractGeometryType;
import net.opengis.gml.AbstractRingPropertyType;
import net.opengis.gml.AbstractRingType;
import net.opengis.gml.AbstractSurfaceType;
import net.opengis.gml.AbstractTimeGeometricPrimitiveType;
import net.opengis.gml.CodeType;
import net.opengis.gml.CoordinatesType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.FeatureCollectionDocument;
import net.opengis.gml.FeatureCollectionDocument2;
import net.opengis.gml.FeatureDocument;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.LineStringType;
import net.opengis.gml.LinearRingType;
import net.opengis.gml.LocationPropertyType;
import net.opengis.gml.PointPropertyType;
import net.opengis.gml.PointType;
import net.opengis.gml.PolygonType;
import net.opengis.gml.StringOrRefType;
import net.opengis.gml.SurfacePropertyType;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.sampling.x10.SamplingPointDocument;
import net.opengis.sampling.x10.SamplingPointType;
import net.opengis.sampling.x10.SamplingSurfaceDocument;
import net.opengis.sampling.x10.SamplingSurfaceType;
import net.opengis.sos.x10.DomainFeatureType;
import net.opengis.sos.x10.GenericDomainFeatureDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.encode.IGMLEncoder;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.SosFeatureCollection;
import org.n52.sos.ogc.om.features.SosXmlFeature;
import org.n52.sos.ogc.om.features.domainFeatures.SosDomainArea;
import org.n52.sos.ogc.om.features.domainFeatures.SosGenericDomainFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosGenericSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingPoint;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingSurface;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.PolygonExtracter;

/**
 * class encapsulates encoding methods for GML elements as features or geometries
 * 
 * @author Christoph Stasch
 * 
 */
public class GMLEncoder implements IGMLEncoder {

    /**
     * creates FeatureDocument depending on feature type
     * 
     * @param absFeature
     *        the SosAbstractFeature
     * @return FeatureDocument XMLBeans representation of feature
     * @throws OwsExceptionReport
     *         if feature type is not supported by this encoder
     */
    public FeatureDocument createFeature(SosAbstractFeature absFeature) throws OwsExceptionReport {

        // check feature type and create FeatureDocument depending on feature type
        if (absFeature instanceof SosSamplingPoint) {
            return createSamplingPoint(absFeature);
        }

        else if (absFeature instanceof SosFeatureCollection) {
            return createFeatureCollection((SosFeatureCollection) absFeature);
        }

        // mobile request
        else if (absFeature instanceof SosGenericSamplingFeature) {
            return createSamplingPoint(absFeature);
        }

        else if (absFeature instanceof SosGenericDomainFeature) {
            return createGenericDomainFeature(absFeature);
        }

        else if (absFeature instanceof SosDomainArea) {
            return createGenericDomainFeature(absFeature);
        }
        
        else if (absFeature instanceof SosSamplingSurface) {
            return createSamplingSurface(absFeature);
        }
        
        else if (absFeature instanceof SosXmlFeature) {
        	return createXmlFeature(absFeature);
        }


        OwsExceptionReport se = new OwsExceptionReport();
        se.addCodedException(ExceptionCode.NoApplicableCode, null, "The feature type "
                + absFeature.getClass() + " is not supported for encoding!!");
        throw se;
    }

    /**
     * creates XmlBeans TimeObjectPropertyType from passed sos time object
     * 
     * @param time
     *        ISosTime implementation, which should be created an XmlBeans O&M representation from
     * @return Returns XmlBean representing the passed time in O&M format
     * @throws OwsExceptionReport 
     */
    public AbstractTimeGeometricPrimitiveType createTime(ISosTime time) throws OwsExceptionReport {
        AbstractTimeGeometricPrimitiveType xb_eventTime = null;

        if (time == null) {
            return xb_eventTime;
        }

        else if (time instanceof TimeInstant) {
            TimeInstantType xb_timeInstant = createTimeInstant((TimeInstant) time);
            xb_eventTime = xb_timeInstant;
        }

        else if (time instanceof TimePeriod) {
            TimePeriodType xb_timePeriod = createTimePeriod((TimePeriod) time);
            xb_eventTime = xb_timePeriod;
            // change name of _TimeObject to TimeInstant

        }

        return xb_eventTime;
    }

    /**
     * creates an OM event time instant element
     * 
     * @param dbTimePosition
     *        String containing the timestamp in db time format (example: 2005-10-08 10:15:00+02)
     * @return TimeObjectPropertyType representing the time element
     * @throws OwsExceptionReport 
     * 
     * @throws ParseException
     *         if parsing the db time string into a java.util.Date object
     */
    private TimeInstantType createTimeInstant(TimeInstant timeInstant) throws OwsExceptionReport {

        // create time instant
        TimeInstantType xb_timeInstant = TimeInstantType.Factory.newInstance();
        TimePositionType xb_posType = xb_timeInstant.addNewTimePosition();

        // parse db date string and format into GML format
        DateTime date = timeInstant.getValue();
        String timeString = SosDateTimeUtilities.formatDateTime2ResponseString(date);

        // concat minutes for timeZone offset, because gml requires xs:dateTime, which needs minutes in
        // timezone offset
        // TODO enable really
        xb_posType.setStringValue(timeString);

        return xb_timeInstant;
    }

    /**
     * creates a timePeriod element
     * 
     * @param begin
     *        timeString of the begin of the period in ISO8601 format
     * @param end
     *        timestring of the end of the period in ISO8601 format
     * @return Returns TimePeriodType representing the timePeriod in gml format
     * @throws OwsExceptionReport 
     */
    private TimePeriodType createTimePeriod(TimePeriod timePeriod) throws OwsExceptionReport {

        TimePeriodType xb_timePeriod = TimePeriodType.Factory.newInstance();

        if (timePeriod != null) {
            // beginPosition
            TimePositionType xb_timePositionBegin = TimePositionType.Factory.newInstance();
            String beginString  = SosDateTimeUtilities.formatDateTime2ResponseString(timePeriod.getStart());

            // concat minutes for timeZone offset, because gml requires xs:dateTime, which needs minutes in
            // timezone offset
            // TODO enable really
            xb_timePositionBegin.setStringValue(beginString);

            // endPosition
            TimePositionType xb_timePositionEnd = TimePositionType.Factory.newInstance();
            String endString = SosDateTimeUtilities.formatDateTime2ResponseString(timePeriod.getEnd());

            // concat minutes for timeZone offset, because gml requires xs:dateTime, which needs minutes in
            // timezone offset
            // TODO enable really
            xb_timePositionEnd.setStringValue(endString);

            xb_timePeriod.setBeginPosition(xb_timePositionBegin);
            xb_timePeriod.setEndPosition(xb_timePositionEnd);
        }

        return xb_timePeriod;
    }

    /**
     * creates SamplingPointDocument
     * 
     * @param absFoi
     *        SosAbstractFeature of type SosStation
     * @return SamplingPointDocument
     */
    private SamplingPointDocument createSamplingPoint(SosAbstractFeature absFoi) {

        // get and build srsName
        String srsName = SosConfigurator.getInstance().getSrsNamePrefix() + absFoi.getEpsgCode();

        // new StationDocument
        SamplingPointDocument xb_spointDoc = SamplingPointDocument.Factory.newInstance();

        // new Station, set ID
        SamplingPointType xb_spointType = xb_spointDoc.addNewSamplingPoint();
        xb_spointType.setId(absFoi.getId().replace(SosConstants.FOI_PREFIX, ""));

        // set Station name
        CodeType xb_name = xb_spointType.addNewName();
        xb_name.setStringValue(absFoi.getName());

        // add sampled Feature (if mobile request)
        if (absFoi instanceof SosGenericSamplingFeature) {
            SosGenericSamplingFeature sgsf = (SosGenericSamplingFeature) absFoi;
            for (SosAbstractFeature dfid : sgsf.getDomainFeatureIDs()) {
                FeaturePropertyType xb_featurePropType = xb_spointType.addNewSampledFeature();
                xb_featurePropType.setHref(dfid.getId());
            }
        }

        // add and set Position
        Point jts_point = (Point) absFoi.getJts_geom();
        PointPropertyType xb_position = xb_spointType.addNewPosition();
        PointType xb_point = xb_position.addNewPoint();
        DirectPositionType xb_pos = xb_point.addNewPos();
        xb_pos.setSrsName(srsName);
        String coords;
        if (SosConfigurator.getInstance().switchCoordinatesForEPSG(absFoi.getEpsgCode())) {
//            coords = "" + jts_point.getCoordinate().x + " " + jts_point.getCoordinate().y;
        	coords = switchCoordinates4String(jts_point);
        }
        else {
//            coords = "" + jts_point.getCoordinate().y + " " + jts_point.getCoordinate().x;
        	coords = getCoordinates4String(jts_point);
        }
        xb_pos.setStringValue(coords);

        return xb_spointDoc;
    }
    
    /**
     * creates SamplingSurfaceDocument
     * 
     * @param absFoi
     *        SosAbstractFeature of type SosStation
     * @return SamplingSurfaceDocument
     */
    private SamplingSurfaceDocument createSamplingSurface(SosAbstractFeature absFoi) {

        // get and build srsName
        String srsName = SosConfigurator.getInstance().getSrsNamePrefix() + absFoi.getEpsgCode();

        // new StationDocument
        SamplingSurfaceDocument xb_ssurfaceDoc = SamplingSurfaceDocument.Factory.newInstance();

        // new Station, set ID
        SamplingSurfaceType xb_ssurfaceType = xb_ssurfaceDoc.addNewSamplingSurface();
        xb_ssurfaceType.setId(absFoi.getId().replace(SosConstants.FOI_PREFIX, ""));

        // set Station name
        CodeType xb_name = xb_ssurfaceType.addNewName();
        xb_name.setStringValue(absFoi.getName());

        // add sampled Feature (if mobile request)
        if (absFoi instanceof SosGenericSamplingFeature) {
            SosGenericSamplingFeature sgsf = (SosGenericSamplingFeature) absFoi;
            for (SosAbstractFeature dfid : sgsf.getDomainFeatureIDs()) {
                FeaturePropertyType xb_featurePropType = xb_ssurfaceType.addNewSampledFeature();
                xb_featurePropType.setHref(dfid.getId());
            }
        }

        // add and set Position
        Polygon jts_polygon = (Polygon) absFoi.getJts_geom();
        SurfacePropertyType xb_surface = xb_ssurfaceType.addNewShape();
        AbstractSurfaceType xb_surf = xb_surface.addNewSurface();
        PolygonType xb_polyType = encodePolygon(jts_polygon);
        xb_polyType.setSrsName(srsName);
        xb_surf.set(xb_polyType);
        
        //correct polygon element
        XmlCursor cursor = xb_surface.newCursor();
        if (cursor.toChild(new QName(OMConstants.NS_GML,OMConstants.EN_ABSTRACT_SURFACE))){
        	cursor.setName(new QName(OMConstants.NS_GML,OMConstants.EN_POLYGON));
        }
       
        return xb_ssurfaceDoc;
    }

    /**
     * creates DomainAreaDocument
     * 
     * @param absFoi
     *        SosAbstractFeature
     * @return DomainAreaDocument
     */
    private GenericDomainFeatureDocument createGenericDomainFeature(SosAbstractFeature absFoi) {

        GenericDomainFeatureDocument xb_domFeatDoc = GenericDomainFeatureDocument.Factory.newInstance();

        DomainFeatureType xb_domFeat = xb_domFeatDoc.addNewGenericDomainFeature();

        // set ID
        xb_domFeat.setId(absFoi.getId());

        // set description
        StringOrRefType xb_srType = xb_domFeat.addNewDescription();
        xb_srType.setStringValue(absFoi.getDescription());

        // set name
        CodeType xb_ct = xb_domFeat.addNewName();
        xb_ct.setStringValue(absFoi.getName());

        // set geometry
        LocationPropertyType xb_lpt = xb_domFeat.addNewLocation();
        AbstractGeometryType xb_agt = xb_lpt.addNewGeometry();

        Geometry geom = absFoi.getJts_geom();

        if (geom instanceof Polygon) {
        	PolygonType xb_polType = encodePolygon((Polygon)geom);
            xb_agt.set(xb_polType);
            // xb_lpt.setGeometry(xb_polType);

            XmlCursor cursor = xb_lpt.newCursor();
            boolean hasChild = cursor.toChild(new QName(OMConstants.NS_GML,
                                                        OMConstants.EN_ABSTRACT_GEOMETRY));
            if (hasChild) {
                cursor.setName(new QName(OMConstants.NS_GML, OMConstants.EN_POLYGON));
            }
        }
        else {
            // TODO: implement further geometries
        }

        return xb_domFeatDoc;
    }

    /**
     * creates a FeatureCollection from the passed features of interest
     * 
     * @param fois
     *        SOS representation of the feature collection for which the collection should be created
     * @return Returns XMLBeans representation of gml:FeatureCollectionType containing the single
     *         GeoreferenceableFeatures created from the passed fois
     * @throws OwsExceptionReport
     *         if feature type of feature member is not supported
     */
    private FeatureCollectionDocument createFeatureCollection(SosFeatureCollection sosFeatCol) throws OwsExceptionReport {

        // create FeatureCollectionDocument
        FeatureCollectionDocument xb_featColDoc = FeatureCollectionDocument.Factory.newInstance();
        AbstractFeatureCollectionType xb_collection = xb_featColDoc.addNewFeatureCollection();

        for (SosAbstractFeature feature : sosFeatCol.getMembers()) {

            // check if foiId and geometry not empty
            if ( ! (feature.getId().equals("") && feature.getGeom().equals(""))) {

                FeaturePropertyType xb_member = xb_collection.addNewFeatureMember();
                FeatureDocument xb_feature = createFeature(feature);
                xb_member.set(xb_feature);

                XmlCursor cursor = xb_member.newCursor();
                boolean isAF = cursor.toChild(new QName(OMConstants.NS_GML,
                                                        OMConstants.EN_ABSTRACT_FEATURE));

                // rename elementName from gml:_Feature to sa:Station
                if (isAF && feature instanceof SosSamplingPoint) {
                    cursor.setName(new QName(OMConstants.NS_SA, OMConstants.EN_SAMPLINGPOINT));
                }
            }
        }

        XmlCursor cursor = xb_featColDoc.newCursor();
        boolean isAFC = cursor.toChild(new QName(OMConstants.NS_GML,
                                                 OMConstants.EN_ABSTRACT_FEATURE_COLLECTION));

        // rename elementName from gml:_FeatureCollection to gml:FeatureCollection
        if (isAFC) {
            cursor.setName(new QName(OMConstants.NS_GML, OMConstants.EN_FEATURE_COLLECTION));
        }

        return xb_featColDoc;
    }
    
    /**
     * creates a FeatureDocument from the passed feature or feature collection
     * 
     * @param absFeature SosAbstractFeature
     * @return FeatureDocument
     */
    private  FeatureDocument createXmlFeature(SosAbstractFeature absFeature) {
    	SosXmlFeature xml = (SosXmlFeature) absFeature;
		try {
			if (xml.getXmlFeatureObject() instanceof FeatureCollectionDocument2) {
				return FeatureCollectionDocument2.Factory.parse(xml.getXmlFeatureObject().toString());
			} 
			else {
				FeatureDocument fd = FeatureDocument.Factory.newInstance();
				fd.setFeature(AbstractFeatureType.Factory.parse(xml.getXmlFeatureObject().toString()));
				return fd;
			}
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public PointType encodePoint(Point jts_point) {
    	
    	PointType xb_pointType  = PointType.Factory.newInstance();
    	xb_pointType.setSrsName(SosConfigurator.getInstance().getSrsNamePrefix() + Integer.toString(jts_point.getSRID()));
        CoordinatesType xb_ct = xb_pointType.addNewCoordinates();
        xb_ct.setStringValue(jts_point.getX() + " " + jts_point.getY());
        
        return xb_pointType;
    }
    
    public LineStringType encodeLineString(LineString jts_lineString) {
    	
    	LineStringType xb_lst = LineStringType.Factory.newInstance();
        xb_lst.setSrsName(SosConfigurator.getInstance().getSrsNamePrefix()
                + Integer.toString(jts_lineString.getSRID()));
        CoordinatesType xb_coord = xb_lst.addNewCoordinates();
        Coordinate[] coords = jts_lineString.getCoordinates();

        StringBuilder coordsSB = new StringBuilder();

        for (int j = 0; j < coords.length; j++) {

            coordsSB.append(coords[j].x + " " + coords[j].y + ", ");
        }

        if (coordsSB.toString().endsWith(", ")) {
            coordsSB.delete(coordsSB.toString().length() - 2, coordsSB.toString().length());
        }

        xb_coord.setStringValue(coordsSB.toString());
        
        return xb_lst;
    }
    /**
     * Encodes a jts_Polygon to xml PolygonType.
     * 
     * @param geom
     * 			Geometry to be encoded. 
     * @return Returns XMLBeans representation of gml:PolygonType.
     */
    public PolygonType encodePolygon(Polygon geom){
    	 List< ? > jts_polygons = PolygonExtracter.getPolygons(geom);
    	 PolygonType xb_polType = PolygonType.Factory.newInstance();
         for (int i = 0; i < jts_polygons.size(); i++) {

             // PolygonDocument xb_polDoc = PolygonDocument.Factory.newInstance();
            
             AbstractRingPropertyType xb_arpt = xb_polType.addNewExterior();
             AbstractRingType xb_art = xb_arpt.addNewRing();

             LinearRingType xb_rt = LinearRingType.Factory.newInstance();
             CoordinatesType xb_ctype = xb_rt.addNewCoordinates();
             xb_polType.setSrsName("" + geom.getSRID());

//             Polygon pol = (Polygon) jts_polygons.get(i);
             String coords;
             
             if(SosConfigurator.getInstance().switchCoordinatesForEPSG(geom.getSRID())) {
            	 coords = switchCoordinates4String((Polygon) jts_polygons.get(i));
             } else {
            	 coords = getCoordinates4String((Polygon) jts_polygons.get(i));
             }
             xb_ctype.setStringValue(coords);

             xb_art.set(xb_rt);

             XmlCursor cursor2 = xb_arpt.newCursor();
             boolean hasChild2 = cursor2.toChild(new QName(OMConstants.NS_GML,
                                                           OMConstants.EN_ABSTRACT_RING));
             if (hasChild2) {
                 cursor2.setName(new QName(OMConstants.NS_GML, OMConstants.EN_LINEAR_RING));
             }



         }
		return xb_polType;
    }
    
    /**
     * Builds a String from jts_Geometry coordinates.
     * 
     * @param sourceGeom
     * 			jts_Geometry to get the coordinates.
     * @return
     * 			String with coordinates.
     */	
    private String getCoordinates4String(Geometry sourceGeom) {
    	StringBuffer stringCoords = new StringBuffer();
    	Coordinate[] sourceCoords = sourceGeom.getCoordinates();
    	
    	for (Coordinate coordinate : sourceCoords) {
			stringCoords.append(coordinate.x + " " + coordinate.y + ", ");
		}
    	
    	if (stringCoords.toString().endsWith(", ")) {
    		stringCoords.delete(stringCoords.toString().length() - 2, stringCoords.toString().length());
        }
    	return stringCoords.toString();
    }

    /**
     * Builds a String from jts_Geometry coordinates and switches the xy.
     * 
     * @param sourceGeom
     * 			jts_Geometry to get the coordinates.
     * @return
     * 			String with coordinates.
     */	
    private String switchCoordinates4String(Geometry sourceGeom) {
    	StringBuffer switchedCoords = new StringBuffer();
    	Coordinate[] sourceCoords = sourceGeom.getCoordinates();
    	
    	for (Coordinate coordinate : sourceCoords) {
			switchedCoords.append(coordinate.y + " " + coordinate.x + ", ");
		}
    	
    	if (switchedCoords.toString().endsWith(", ")) {
    		switchedCoords.delete(switchedCoords.toString().length() - 2, switchedCoords.toString().length());
        }
    	return switchedCoords.toString();
    }
}
