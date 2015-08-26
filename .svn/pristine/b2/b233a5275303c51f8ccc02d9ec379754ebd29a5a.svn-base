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

package org.n52.sos.decode.impl.utilities;


import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractGeometryType;
import net.opengis.gml.AbstractRingPropertyType;
import net.opengis.gml.AbstractRingType;
import net.opengis.gml.CoordinatesType;
import net.opengis.gml.DirectPositionListType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.LineStringType;
import net.opengis.gml.LinearRingType;
import net.opengis.gml.PointType;
import net.opengis.gml.PolygonType;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.gml.impl.LineStringTypeImpl;
import net.opengis.gml.impl.PointTypeImpl;
import net.opengis.gml.impl.PolygonTypeImpl;
import net.opengis.ogc.BBOXType;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.ogc.gml.GMLConstants;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.w3c.dom.Node;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * class offers methods for parsing GML elements of requests
 * 
 * @author Christoph Stasch
 * 
 */
public class GMLDecoder {

    /** logger */
    private static Logger log = Logger.getLogger(GMLDecoder.class);
    
    private static String cs = ",";
    private static String decimal = ".";
    private static String ts = " ";
    
    /**
     * parses TimeInstant
     * 
     * @param tp
     *        XmlBean representation of TimeInstant
     * @return
     * 		  Returns a TimeInstant created from the TimeInstantType
     * @throws java.text.ParseException
     * @throws java.text.ParseException
     *         if parsing the datestring into java.util.Date failed
     * @throws OwsExceptionReport 
     */
    public static TimeInstant parseTimeInstant(TimeInstantType tp) throws java.text.ParseException, OwsExceptionReport {
        TimeInstant ti = new TimeInstant();
        TimePositionType tpt = tp.getTimePosition();
        String timeString = tpt.getStringValue();
        if (timeString != null && !timeString.equals("")) {
            ti.setValue(SosDateTimeUtilities.parseIsoString2DateTime(timeString));
        }
        if (tpt.getIndeterminatePosition() != null) {
            ti.setIndeterminateValue(tpt.getIndeterminatePosition().toString());
        }
        return ti;
    }

    /**
     * help method, which creates an TimeInstant object from the DOM-node of the TimeInstantType. This
     * constructor is necessary cause XMLBeans does not full support substitution groups. So one has to do a
     * workaround with XmlCursor and the DomNodes of the elements.
     * 
     * @param timeInstant
     *        DOM Node of timeInstant element
     * @return 
     * 		  Returns a TimeInstant created from the DOM-Node
     * @throws OwsExceptionReport
     *         if no timePosition element is cotained in the timeInstant element
     * @throws XmlException
     *         if parsing the DomNode to an XMLBeans XmlObject failed
     */
    public static TimeInstant parseTimeInstantNode(Node timeInstant) throws OwsExceptionReport,
            XmlException {

        TimeInstant ti = new TimeInstant();

        /*
         * Parse node to XmlObject and then getValue of timePosition through navigation with XmlCursor.
         * Necessary cause parsing the node into TimeInstantType object does not work! TODO Maybe change if it
         * will work in future
         */
        XmlObject timeObject = XmlObject.Factory.parse(timeInstant);
        XmlCursor cursor = timeObject.newCursor();

        // nodename of timePosition element
        QName qname = new QName(GMLConstants.NS_GML, GMLConstants.EN_TIME_POSITION);

        cursor.toNextToken();

        // move cursor to timePosition element
        boolean isTP = cursor.toChild(qname);

        // if cursor is at timePosition element, est time on textValue
        if (isTP) {
            String positionString = cursor.getTextValue();
            if (positionString != null && !positionString.equals("")) {
            	if (positionString.equals("latest")) {
            		ti.setIndeterminateValue(positionString);
				}
				else {
					ti.setValue(SosDateTimeUtilities.parseIsoString2DateTime(positionString));
				}
                
            }

            // get indeterminateTime value
            boolean hasAttr = cursor.toFirstAttribute();

            // if intdeterminateTime attribute is set, set string value
            if (hasAttr) {
                ti.setIndeterminateValue(cursor.getTextValue());
            }
        }

        // else no timePosition element is found --> Exception!
        else {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.MissingParameterValue,
                                 "gml:timePosition",
                                 "No timePosition element is contained in the gml:timeInstant element");
            throw se;
        }

        return ti;

    }

    /**
	     * parse methode, which creates an TimePeriod object from the DOM-node of the TimePeriodType. This
	     * constructor is necessary cause XMLBeans does not fully support substitution groups. So one has to do a
	     * workaround with XmlCursor and the DomNodes of the elements.
	     * 
	     * 
	     * @param timePeriod
	     *        the DomNode of the timePeriod element
	     * @return Returns a TimePeriod created from the DOM-Node
	     * @throws XmlException
	     *         if the Node could not be parsed into a XmlBean
	     * @throws OwsExceptionReport
	     *         if required elements of the timePeriod are missed
	     */
	    public static TimePeriod parseTimePeriodNode(Node timePeriod) throws XmlException,
	            OwsExceptionReport {
	
	        TimePeriod tp = new TimePeriod();
	        /*
	         * Parse node to XmlObject and then getValue of timePositions through navigation with XmlCursor.
	         * Necessary cause parsing the node into TimePeriodType object does not work! TODO Maybe change if it
	         * will work in future
	         */
	        XmlObject timeObject = XmlObject.Factory.parse(timePeriod);
	        XmlCursor cursor = timeObject.newCursor();
	
	        // nodename of timePosition element
	        QName qnameBeginPos = new QName("http://www.opengis.net/gml", "beginPosition");
	        QName qnameBegin = new QName("http://www.opengis.net/gml", "begin");
	
	        QName qnameEndPos = new QName("http://www.opengis.net/gml", "endPosition");
	        QName qnameEnd = new QName("http://www.opengis.net/gml", "end");
	        QName qnameDuration = new QName("http://www.opengis.net/gml", "duration");
	        QName qnameIntervall = new QName("http://www.opengis.net/gml", "intervall");
	
	        cursor.toNextToken();
	
	        // move cursor to beginPosition element
	        boolean isBegin = cursor.toChild(qnameBegin);
	        boolean isBeginPos = cursor.toChild(qnameBeginPos);
	
	        // if correct, set startTime
	        // element is gml:begin
	        if (isBegin) {
	
	            if (cursor.getTextValue().equals("")) {
	                tp.setStart(null);
	            }
	            else {
	                String startString = cursor.getTextValue();
	                tp.setStart(SosDateTimeUtilities.parseIsoString2DateTime(startString));
	            }
	
	            // check startIndetTime
	            boolean isIndet = cursor.toFirstAttribute();
	            if (isIndet) {
	                tp.setStartIndet(cursor.getTextValue());
	                cursor.toParent();
	            }
	        }
	
	        // element is gml:beginPosition
	        else if (isBeginPos) {
	
	            if (cursor.getTextValue().equals("")) {
	                tp.setStart(null);
	            }
	            else {
	                String startString = cursor.getTextValue();
	                tp.setStart(SosDateTimeUtilities.parseIsoString2DateTime(startString));
	            }
	
	            // check startIndetTime
	            boolean isIndet = cursor.toFirstAttribute();
	            if (isIndet) {
	                tp.setStartIndet(cursor.getTextValue());
	                cursor.toParent();
	            }
	        }
	
	        // else no beginPosition -> throw exception!!
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 GetObservationParams.eventTime.toString(),
	                                 "The start time is missed in the timePeriod element of time parameter!");
	            throw se;
	        }
	
	        // move cursor back to timePeriod element
	        cursor.toParent();
	
	        // move cursor to endPosition element
	        boolean isEnd = cursor.toChild(qnameEnd);
	        boolean isEndPos = cursor.toChild(qnameEndPos);
	
	        // if correct, set endTime
	        // element is gml:end
	        if (isEnd) {
	
	            if (cursor.getTextValue().equals("")) {
	                tp.setEnd(null);
	            }
	
	            else {
	                String endString = cursor.getTextValue();
	                tp.setEnd(SosDateTimeUtilities.parseIsoString2DateTime(endString));
	            }
	
	            boolean isIndet = cursor.toFirstAttribute();
	
	            if (isIndet) {
	                tp.setEndIndet(cursor.getTextValue());
	                cursor.toParent();
	            }
	        }
	
	        // element is gml:endPosition
	        else if (isEndPos) {
	
	            if (cursor.getTextValue().equals("") || cursor.getTextValue() == null) {
	                tp.setEnd(null);
	            }
	            else {
	                String endString = cursor.getTextValue();
	                tp.setEnd(SosDateTimeUtilities.parseIsoString2DateTime(endString));
	            }
	
	            boolean isIndet = cursor.toFirstAttribute();
	
	            if (isIndet) {
	                tp.setEndIndet(cursor.getTextValue());
	                cursor.toParent();
	            }
	        }
	
	        // else no endPosition -> throw exception!!
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 GetObservationParams.eventTime.toString(),
	                                 "The end time is missed in the timePeriod element of time parameter!");
	            throw se;
	        }
	
	        // move cursor back to timePeriod element
	        cursor.toParent();
	
	        boolean isDuration = cursor.toChild(qnameDuration);
	        if (isDuration) {
	//            TODO: JODA TIME 
	            tp.setDuration(SosDateTimeUtilities.parseDuration(cursor.getTextValue()));
	        }
	
	        boolean isIntervall = cursor.toChild(qnameIntervall);
	        if (isIntervall) {
	            tp.setIntervall(cursor.getTextValue());
	        }
	
	        return tp;
	
	    }

	/**
	     * creates SOS representation of time period from XMLBeans representation of time period
	     * 
	     * @param xb_timePeriod
	     *        XMLBeans representation of time period
	     * @return Returns SOS representation of time period
	     * @throws OwsExceptionReport
	     */
	    public static TimePeriod parseTimePeriod(TimePeriodType xb_timePeriod) throws OwsExceptionReport {
	
	//        SimpleDateFormat gmlSdf = new SimpleDateFormat(SosConfigurator.getInstance().getGmlDateFormat());
	        TimePositionType xb_beginPos = xb_timePeriod.getBeginPosition();
	        DateTime begin = null;
	        if (xb_beginPos != null) {
	            String beginString = xb_beginPos.getStringValue();
	            try {
	                begin = SosDateTimeUtilities.parseIsoString2DateTime(beginString);
	            }
	            catch (Exception e) {
	                OwsExceptionReport se = new OwsExceptionReport();
	                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                     null,
	                                     "Error while parsing timestring '" + beginString
	                                             + "' from TimePeriod parameter:" + e.getMessage());
	                log.error(se.getMessage());
	                throw se;
	            }
	        }
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 null,
	                                 "gml:TimePeriod! must contain beginPos Element with valid ISO:8601 String!!");
	            log.error(se.getMessage());
	            throw se;
	        }
	
	        DateTime end = null;
	        TimePositionType xb_endPos = xb_timePeriod.getEndPosition();
	        if (xb_endPos != null) {
	            String endString = xb_endPos.getStringValue();
	            try {
	                end = SosDateTimeUtilities.parseIsoString2DateTime(endString);
	            }
	            catch (Exception e) {
	                OwsExceptionReport se = new OwsExceptionReport();
	                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                     null,
	                                     "Error while parsing timestring '" + endString
	                                             + "' from TimePeriod parameter:" + e.getMessage());
	                log.error(se.getMessage());
	                throw se;
	            }
	        }
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 null,
	                                 "gml:TimePeriod! must contain endPos Element with valid ISO:8601 String!!");
	            log.error(se.getMessage());
	            throw se;
	        }
	
	        TimePeriod tp = new TimePeriod(begin, end);
	        return tp;
	    }

	/**
	 * parses the XmlBeans AbstractGeometryType and returns a String representing the geometry in
	 * Well-Known-Text (WKT) format
	 * 
	 * @param xb_geometry
	 *        XmlBean representing the geometry
	 * @return Returns jts_geometry
	 * @throws OwsExceptionReport
	 *         if parsing the geometry failed
	 */
	public static Geometry getGeometry4XmlGeometry(AbstractGeometryType xb_geometry) throws OwsExceptionReport {
	
	    Geometry geometry = null;
	    // parse srid; if not set, throw exception!
	
	    // point
	    if (xb_geometry instanceof PointTypeImpl) {
	        PointType xb_point = (PointType) xb_geometry;
	        geometry = getGeometry4Point(xb_point);
	    }
	    
	    // LineString
	    else if (xb_geometry instanceof LineStringTypeImpl) {
	        LineStringType xb_lineString = (LineStringType) xb_geometry;
	        geometry = getGeometry4LineString(xb_lineString);
	    }
	    
	    // polygon
	    else if (xb_geometry instanceof PolygonTypeImpl) {
	        PolygonType xb_polygon = (PolygonType) xb_geometry;
	        geometry = getGeometry4Polygon(xb_polygon);
	    }
	    
	    else {
	    	OwsExceptionReport se = new OwsExceptionReport();
	        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                             null,
	                             "The FeatureType: "+ xb_geometry + " is not supportted! Only PointType and PolygonType");
	        log.error("Error while parsing FeatureType: "
	                + se.getMessage());
	        throw se;
	    }
	    
	    
	    if (SosConfigurator.getInstance().switchCoordinatesForEPSG(geometry.getSRID())) {
	    	geometry = switchCoordinate4Geometry(geometry);
	    }
	    
	    return geometry;
	}// end getGeometry4XmlGeometry

	/**
	 * parses the XmlBean which represents a Point and returns an JTS Geometry (which is a point)
	 * 
	 * @param xb_pointType
	 * 		XmlBean representing the point
	 * @return
	 * 		 Returns JTS Geometry which represents the passe point
	 * @throws OwsExceptionReport
	 * 		if parsing of point failed
	 */
	private static Geometry getGeometry4Point(PointType xb_pointType) throws OwsExceptionReport {
		
		Geometry geom = null;
		String geomWKT = null;
		int srid = -1;
		if (xb_pointType.getSrsName() != null) {
			srid = parseSrsName(xb_pointType.getSrsName());
		}
        
        if (xb_pointType.getPos() != null) {
        	DirectPositionType xb_pos = xb_pointType.getPos();
        	if (srid == -1 && xb_pos.getSrsName() != null) {
        		srid = parseSrsName(xb_pos.getSrsName());
        	}
            String directPosition = getString4Pos(xb_pos);
            geomWKT = "POINT(" + directPosition + ")";
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "For geometry type 'gml:Point' only element "
                                         + "'gml:pos' is allowed "
                                         + "in the feature of interest parameter!");
            throw se;
        }
        
        if (srid == -1) {
			OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No SrsName ist specified!");
            throw se;
		}
        geom = createGeometryFromWKT(geomWKT);
        geom.setSRID(srid);
    	
        return geom;
	}
	
	/**
	 * parses the XmlBean which represents a LineString and returns an JTS Geometry (which is a LineString)
	 * 
	 * @param xb_lineSringType
	 * 		XmlBean representing the LineString
	 * @return
	 * 		Returns JTS Geometry which represents the passe LineString
	 * @throws OwsExceptionReport
	 * 		if parsing of LineString failed
	 */
	private static Geometry getGeometry4LineString(LineStringType xb_lineSringType) throws OwsExceptionReport {
		
		Geometry geom = null;
		String geomWKT = null;
		int srid = -1;
		if (xb_lineSringType.getSrsName() != null) {
			srid = parseSrsName(xb_lineSringType.getSrsName());
		}
		
        DirectPositionType[] xb_positions = xb_lineSringType.getPosArray();

        StringBuffer positions = new StringBuffer();
        if(xb_positions != null && xb_positions.length > 0) {
        	if (srid == -1 && xb_positions[0].getSrsName() != null && !(xb_positions[0].getSrsName().equals(""))) {
    			srid = parseSrsName(xb_positions[0].getSrsName());
    		}
            positions.append(getString4PosArray(xb_lineSringType.getPosArray()));
        }
        geomWKT = "LINESTRING" + positions.toString() + "";
		
        if (srid == -1) {
        	OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No SrsName ist specified!");
            throw se;
        }
        
		geom = createGeometryFromWKT(geomWKT);
        geom.setSRID(srid);
        
        return geom;
	}
	
	/**
     * parses the XmlBean which represents a Polygon and returns an JTS Geometry (which is a polygon)
     * 
     * @param xb_polygonType
     *        XmlBean which represents the Polygon
     * @return Returns JTS Geometry which represents the passe polygon
     * @throws OwsExceptionReport
     */
    private static Geometry getGeometry4Polygon(PolygonType xb_polygonType) throws OwsExceptionReport {
        Geometry geom = null;
        int srid = -1;
        if (xb_polygonType.getSrsName() != null) {
			srid = parseSrsName(xb_polygonType.getSrsName());
		}
        String exteriorCoordString = null;
        StringBuilder geomWKT = new StringBuilder();
        StringBuilder interiorCoordString = new StringBuilder();

        AbstractRingPropertyType xb_exterior = xb_polygonType.getExterior();

        if (xb_exterior != null) {
            AbstractRingType xb_exteriorRing = xb_exterior.getRing();
            if (xb_exteriorRing instanceof LinearRingType) {
                LinearRingType xb_linearRing = (LinearRingType) xb_exteriorRing;
                if (srid == -1 && xb_linearRing.getSrsName() != null) {
                	srid = parseSrsName(xb_linearRing.getSrsName());
                }
                exteriorCoordString = getCoordString4LinearRing(xb_linearRing, srid);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "The Polygon must contain the following elements <gml:exterior><gml:LinearRing><gml:posList>!");
                log.error("Error while parsing polygon of featureOfInterest parameter: "
                        + se.getMessage());
                throw se;
            }
        }

        AbstractRingPropertyType[] xb_interior = xb_polygonType.getInteriorArray();
        AbstractRingPropertyType xb_interiorRing;
        if (xb_interior != null && xb_interior.length != 0) {
            for (int i = 0; i < xb_interior.length; i++) {
                xb_interiorRing = xb_interior[i];
                if (xb_interiorRing != null && xb_interiorRing instanceof LinearRingType) {
                    interiorCoordString.append(", " + getCoordString4LinearRing((LinearRingType) xb_interiorRing, srid));
                }
            }
        }

        geomWKT.append("POLYGON("); 
        geomWKT.append(exteriorCoordString);
        if (interiorCoordString != null) {
            geomWKT.append(interiorCoordString);
        }
        geomWKT.append(")");
        
        if (srid == 0) {
			OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No SrsName ist specified!");
            throw se;
		}
        geom = createGeometryFromWKT(geomWKT.toString());
        geom.setSRID(srid);

        return geom;
    }

    /**
     * creates a JTS Geometry from the passed WKT string representation of the geometry
     * 
     * @param wktString
     *        string representation in Well-Known-Text format of the geometry
     * @return Returns JTS Geometry
     * @throws OwsExceptionReport
     *         if creating the JTS Geometry failed
     */
    private static Geometry createGeometryFromWKT(String wktString) throws OwsExceptionReport {
        WKTReader wktReader = new WKTReader();
        Geometry geom = null;
        try {
            log.debug("FOI Geometry: " + wktString);
            geom = wktReader.read(wktString);
        }
        catch (ParseException pe) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "An error occurred, while parsing the foi parameter: "
                                         + pe.getMessage());
            log.error("Error while parsing the geometry of featureOfInterest parameter: "
                    + se.getMessage());
            throw se;
        }

        return geom;
    }// end createGeometryFromWKT

    /**
     * parses the BBOX element of the featureOfInterest element contained in the GetObservation request and
     * returns a String representing the BOX in Well-Known-Text format
     * 
     * @param xb_bbox
     *        XmlBean representing the BBOX-element in the request
     * @return Returns WKT-String representing the BBOX as Multipoint with two elements
     * @throws OwsExceptionReport
     *         if parsing the BBOX element failed
     */
    public static Geometry getGeometry4BBOX(BBOXType xb_bbox) throws OwsExceptionReport {
        Geometry result = null;

        EnvelopeType xb_envelope = xb_bbox.getEnvelope();

        // parse srid; if not set, throw exception!
        int srid = GMLDecoder.parseSrsName(xb_envelope.getSrsName());
        
        DirectPositionType xb_lowerCorner = xb_envelope.getLowerCorner();
        DirectPositionType xb_upperCorner = xb_envelope.getUpperCorner();

        String lower = xb_lowerCorner.getStringValue();
        String upper = xb_upperCorner.getStringValue();

        String geomWKT = null;

        if (SosConfigurator.getInstance().switchCoordinatesForEPSG(srid)) {
            lower = switchCoordinatesInString(lower);
            upper = switchCoordinatesInString(upper);
        }

        geomWKT = "MULTIPOINT(" + lower + ", " + upper + ")";

        result = createGeometryFromWKT(geomWKT);
        result.setSRID(srid);
        return result;
    }// end getGeometry4BBOX

    /**
     * parses the passed XmlBeans EnvelopeType and returns a MultiPoint with lowerLeft and upperRight of this
     * BBOX in WKT-format
     * 
     * @param xb_envelope
     *        XmlBean representing the envelope
     * @return Returns MultiPoint in WKT-format, which contains the lowerLeft and upperRight of the BBOX
     * @throws OwsExceptionReport
     */
    public static Geometry getWKT4Envelope(EnvelopeType xb_envelope) throws OwsExceptionReport {

        int srid = parseSrsName(xb_envelope.getSrsName());
                
        DirectPositionType xb_lowerCorner = xb_envelope.getLowerCorner();
        DirectPositionType xb_upperCorner = xb_envelope.getUpperCorner();

        String lower = xb_lowerCorner.getStringValue();
        String upper = xb_upperCorner.getStringValue();

        if (SosConfigurator.getInstance().switchCoordinatesForEPSG(srid)) {
            lower = switchCoordinatesInString(lower);
            upper = switchCoordinatesInString(upper);
        }

        String geomWKT = "MULTIPOINT(" + lower + ", " + upper + ")";
        Geometry geometry = createGeometryFromWKT(geomWKT);
        geometry.setSRID(srid);
        return geometry;
    }// end getWKT4Envelope
    
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
	public static int parseSrsName(String srsName) throws OwsExceptionReport {
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
	                                 null,
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
	                             null,
	                             "For geometry of the feature of interest parameter has to have a srsName attribute, which contains the Srs Name as EPSGcode following the following schema:"
	                                     + srsNamePrefix + "number!");
	        log.error("Error while parsing srsName of featureOfInterest parameter: "
	                + se.getMessage());
	        throw se;
	    }
	    return srid;
	}// end parseSrsName

	/**
	 * method parses the passed linearRing(generated thru XmlBEans) and returns a string containing the
	 * coordinate values of the passed ring
	 * 
	 * @param xb_linearRing
	 *        linearRing(generated thru XmlBEans)
	 * @return Returns a string containing the coordinate values of the passed ring
	 * @throws OwsExceptionReport
	 *         if parsing the linear Ring failed
	 */
	private static String getCoordString4LinearRing(LinearRingType xb_linearRing, int srid) throws OwsExceptionReport {
	    
	    String result = "";
	    DirectPositionListType xb_posList = xb_linearRing.getPosList();
	    CoordinatesType xb_coordinates = xb_linearRing.getCoordinates();
	    DirectPositionType[] xb_posArray = xb_linearRing.getPosArray();
	    if (xb_posList != null &&! (xb_posList.getStringValue().equals(""))) {
	    	result = getString4PosList(xb_posList);
	    } 
	    else if (xb_coordinates != null && !(xb_coordinates.getStringValue().equals(""))) {
	    	result = getString4Coordinates(xb_coordinates);
	    } 
	    else if (xb_posArray != null && xb_posArray.length > 0) {
	    	result = getString4PosArray(xb_posArray);
	    }
	    else {
	    	OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "The Polygon must contain the following elements <gml:exterior><gml:LinearRing><gml:posList>, <gml:exterior><gml:LinearRing><gml:coordinates> or <gml:exterior><gml:LinearRing><gml:pos>{<gml:pos>}!");
            log.error("Error while parsing polygon of featureOfInterest parameter: "
                    + se.getMessage());
            throw se;
	    }
	    
	    return result;
	}// end getCoordStrig4LinearRing

	/**
	 * parses XmlBeans DirectPosition to a String with coordinates for WKT.
	 * 
	 * @param xb_pos
	 * 		XmlBeans generated DirectPosition.
	 * @return
	 * 		Returns String with coordinates for WKT.
	 */
	private static String getString4Pos(DirectPositionType xb_pos) {
		StringBuffer coordinateString = new StringBuffer();
		
		coordinateString.append(xb_pos.getStringValue());
		
		return coordinateString.toString();
	}
	
	/**
	 * parses XmlBeans DirectPosition[] to a String with coordinates for WKT.
	 * 
	 * @param xb_pos
	 * 		XmlBeans generated DirectPosition[].
	 * @return
	 * 		Returns String with coordinates for WKT.
	 */
	private static String getString4PosArray(DirectPositionType[] xb_posArray) {
		StringBuffer coordinateString = new StringBuffer();
		coordinateString.append("(");
		for (DirectPositionType directPositionType : xb_posArray) {
			coordinateString.append(directPositionType.getStringValue());
			coordinateString.append(", ");
		}
		coordinateString.append(xb_posArray[0].getStringValue());
		coordinateString.append(")");
		
		return coordinateString.toString();
	}

	/**
	 * parses XmlBeans DirectPositionList to a String with coordinates for WKT.
	 * 
	 * @param xb_posList
	 * 		XmlBeans generated DirectPositionList.
	 * @return
	 * 		Returns String with coordinates for WKT.
	 * @throws OwsExceptionReport 
	 */
	private static String getString4PosList(DirectPositionListType xb_posList) throws OwsExceptionReport {
		StringBuffer coordinateString = new StringBuffer("(");
		List values = xb_posList.getListValue();
		if ((values.size() % 2) != 0) {
			OwsExceptionReport se = new OwsExceptionReport();
	        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                             null,
	                             "The Polygons posList must contain pairs of coordinates!");
	        log.error("Error while parsing polygon:  "
	                + se.getMessage());
	        throw se;
		} else {
			for (int i = 0; i < values.size(); i++) {
				coordinateString.append(values.get(i));
				if ((i % 2) != 0) {
					coordinateString.append(", ");
				} else {
	                   coordinateString.append(" "); 
	            }
			}
		}
		int length = coordinateString.length();
		coordinateString.delete(length-2, length);
		coordinateString.append(")");
		
		return coordinateString.toString();
	}
	
	/**
	 * parses XmlBeans Coordinates to a String with coordinates for WKT.
	 * Replaces cs, decimal and ts if different from default.
	 * 
	 * @param xb_coordinates
	 * 		XmlBeans generated Coordinates.
	 * @return
	 * 		Returns String with coordinates for WKT.
	 */
	private static String getString4Coordinates(CoordinatesType xb_coordinates) {
		String coordinateString = "";
		
		coordinateString = "(" + xb_coordinates.getStringValue() + ")";
		
		// replace cs, decimal and ts if different from default.
		if (!xb_coordinates.getCs().equals(cs)) {
			coordinateString = coordinateString.replace(xb_coordinates.getCs(), cs);
		}
		if (!xb_coordinates.getDecimal().equals(decimal)) {
			coordinateString = coordinateString.replace(xb_coordinates.getDecimal(), decimal);
		}
		if (!xb_coordinates.getTs().equals(ts)) {
			coordinateString = coordinateString.replace(xb_coordinates.getTs(), ts);
		}
		
		return coordinateString.toString();
	}
	
	/**
	 * switches the order of coordinates contained in a string, e.g. from String '3.5 4.4' to '4.4 3.5'
	 * 
	 * NOTE: ACTUALLY checks, whether dimension is 2D, othewise throws Exception!!
	 * 
	 * @param coordsString
	 *        contains coordinates, which should be switched
	 * @return Returns String contained coordinates in switched order
	 */
	public static String switchCoordinatesInString(String coordsString) {
	    String switchedCoordString = null;
	    String[] coordsArray = coordsString.split(" ");
	    if (coordsArray.length != 2) {
	        // TODO throw ServiceException
	    }
	    else {
	        switchedCoordString = coordsArray[1] + " " + coordsArray[0];
	    }
	    return switchedCoordString;
	}

	/**
	 * Switch coordinates of a geometry. 
	 * 
	 * @param sourceGeom 
	 * 		Geometry with coordinates to be switched. 
	 * @return
	 * 		Returns Geometry with switched coordinates.
	 */
	public static Geometry switchCoordinate4Geometry(Geometry sourceGeom) {
    	
    	GeometryFactory geomFac = new GeometryFactory();
    	Geometry switchedGeom = null;
    	
    	// switch coordinates
    	Coordinate[] coordArraySource = sourceGeom.getCoordinates();
    	List<Coordinate> coordList = new ArrayList<Coordinate>(); 
    	for (Coordinate coordinate : coordArraySource) {
    		if (Double.doubleToLongBits(coordinate.z) == Double.doubleToLongBits(Double.NaN)) {
    			coordList.add(new Coordinate(coordinate.y, coordinate.x, coordinate.z));
    		}
//    		else if(Double.doubleToLongBits(coordinate.z) == Double.doubleToLongBits(Double.NaN)) {
//    			coordList.add(new Coordinate(coordinate.y, coordinate.x, coordinate.z));
//    		} 
    		else {
    			coordList.add(new Coordinate(coordinate.y, coordinate.x));
    		}
		}
    	
    	Coordinate[] coordArraySwitched = coordList.toArray(coordArraySource);
        CoordinateArraySequence coordSeqArray = new CoordinateArraySequence(coordArraySwitched);
        
        // create new geometry with switched coordinates.
    	if (sourceGeom instanceof Point) {
    		Point point = new Point(coordSeqArray, geomFac);
    		switchedGeom = point;
    	}
    	else if (sourceGeom instanceof LineString) {
    		LineString line = new LineString(coordSeqArray, geomFac);
    		switchedGeom = line;
    	}
    	else if (sourceGeom instanceof Polygon) {
    		Polygon polygon = new Polygon(new LinearRing(coordSeqArray,geomFac), null, geomFac);
    		switchedGeom = polygon;
    	} 
    	else if (sourceGeom instanceof MultiPoint) {
    		MultiPoint multiPoint = geomFac.createMultiPoint(coordArraySource);
    		switchedGeom = multiPoint;
    	}
    	switchedGeom.setSRID(sourceGeom.getSRID());
    	
    	return switchedGeom;
    }

}
