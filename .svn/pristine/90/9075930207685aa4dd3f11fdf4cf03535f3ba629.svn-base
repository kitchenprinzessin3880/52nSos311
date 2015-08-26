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

package org.n52.sos.encode.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.EventDocument.Event;
import net.opengis.sensorML.x101.EventListDocument.EventList;
import net.opengis.sensorML.x101.EventListDocument.EventList.Member;
import net.opengis.sensorML.x101.HistoryDocument.History;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.BooleanDocument.Boolean;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.encode.ISensorMLEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.ogc.sensorML.ProcedureHistory;
import org.n52.sos.ogc.sensorML.ProcedureHistoryEvent;
import org.n52.sos.resp.SensorDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Point;

/**
 * class offers static operations for encoding of SensorML documents/elements
 * 
 * @author Christoph Stasch
 *
 */
public class SensorMLEncoder implements ISensorMLEncoder {

    /** the logger, used to log exceptions and additonaly information */
    private static Logger log = Logger.getLogger(SensorMLEncoder.class);

    /**
     * creates sml:System
     * 
     * @param smlDescription
     *          SensorML encoded system description
     * @param history
     *          history of sensor parameters
     * @return Returns XMLBeans representation of sml:System
     * @throws OwsExceptionReport 
     */
    public SensorDocument createSensor(String sensorDesc,
                                       ProcedureHistory history,
                                       Point actualPosition) throws OwsExceptionReport {

        SensorDocument xb_sensorDoc = null;
        SystemDocument xb_systemDoc = null;
        SensorMLDocument xb_smlDoc = null;

        //get SystemDocument
        try {

            xb_smlDoc = SensorMLDocument.Factory.parse(sensorDesc);
            SensorML sml = xb_smlDoc.getSensorML();
            SensorMLDocument.SensorML.Member[] memb = sml.getMemberArray();

            xb_systemDoc = SystemDocument.Factory.parse(memb[0].toString());

        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "Error while encoding SensorML description from stored SensorML encoded sensor description with XMLBeans: "
                                         + xmle.getMessage());
            throw se;
        }

        SystemType xb_system = xb_systemDoc.getSystem();

        //if history is not null, set sensor history
//        if (history != null) {
        if (history != null && !history.getHistory().isEmpty()) {
            History xb_history = xb_system.addNewHistory();
            EventList xb_eventList = xb_history.addNewEventList();

            Collection<ProcedureHistoryEvent> events = history.getHistory();

            Iterator<ProcedureHistoryEvent> iter = events.iterator();
            ProcedureHistoryEvent lastEvent = null;
            int i = 1;
            while (iter.hasNext()) {
            	// check memory
            	checkFreeMemory();
            	
                lastEvent = iter.next();
                Member xb_member = xb_eventList.addNewMember();
                xb_member.setName("Position" + i);
                i++;
                Event xb_event = xb_member.addNewEvent();
                String timeString = SosDateTimeUtilities.formatDateTime2ResponseString(lastEvent.getTimeStamp());
                xb_event.setDate(timeString);
                DataComponentPropertyType xb_prop = xb_event.addNewProperty();
                xb_prop.set(createPosition(lastEvent.getPosition()));
                xb_prop.setName("position");

                DataComponentPropertyType xb_prop1 = xb_event.addNewProperty();
                Boolean bool = xb_prop1.addNewBoolean();
                bool.setValue(lastEvent.isActive());
                xb_prop1.setName("active");

                DataComponentPropertyType xb_prop2 = xb_event.addNewProperty();
                bool = xb_prop2.addNewBoolean();
                bool.setValue(lastEvent.isMobile());
                xb_prop2.setName("mobile");
            }

            if (lastEvent == null) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     SosConstants.DescribeSensorParams.time.name(),
                                     "No positions are contained for the sensor for the requested time period!!");
                throw se;
            }
        }

        //set actual position
        Position xb_pos = xb_system.getPosition();
        xb_pos.set(createPosition(actualPosition));
        xb_pos.setName("actualPosition");

        xb_smlDoc.getSensorML().getMemberArray(0).set(xb_systemDoc);

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xb_smlDoc.toString())));

            xb_sensorDoc = new SensorDocument(doc);

        }
        catch (SAXException saxe) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while parsing the sensor description document!", saxe);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, saxe);
            throw se;
        }
        catch (IOException ioe) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while parsing the sensor description document!", ioe);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, ioe);
            throw se;
        }
        catch (ParserConfigurationException pce) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while parsing the sensor description document!", pce);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, pce);
            throw se;
        }

        return xb_sensorDoc;
    }

    /**
     * creates swe:Position element from passed JTS Point
     * 
     * @param point
     *          JTS point containing the coords for swe:POsition
     * @return Returns XMLBeans representation of swe:Position
     */
//    public PositionDocument createPosition(Point point) {
    public Position createPosition(Point point) {

//        PositionDocument xb_posDoc = PositionDocument.Factory.newInstance();
//        Position xb_pos = xb_posDoc.addNewPosition();
//        PositionType xb_posType = xb_pos.addNewPosition();
        
        Position xb_pos = Position.Factory.newInstance();
        PositionType xb_posType = xb_pos.addNewPosition();


//        xb_posType.addNewName().setStringValue(SosConfigurator.getInstance().getSrsNamePrefix() + point.getSRID());
        xb_posType.setReferenceFrame(SosConfigurator.getInstance().getSrsNamePrefix() + point.getSRID());

        VectorPropertyType xb_location = xb_posType.addNewLocation();
        VectorType xb_vector = xb_location.addNewVector();

        Coordinate xb_coord = xb_vector.addNewCoordinate();
        xb_coord.setName("xcoord");
        Quantity xb_quantity = xb_coord.addNewQuantity();
        xb_quantity.setValue(point.getX());

        xb_coord = xb_vector.addNewCoordinate();
        xb_coord.setName("ycoord");
        xb_quantity = xb_coord.addNewQuantity();
        xb_quantity.setValue(point.getY());

//        return xb_posDoc;
        return xb_pos;
    }
    /**
     * checks the remaining heapsize and throws exception, if it is smaller than 8 kB
     * 
     * @throws OwsExceptionReport
     *         if remaining heapsize is smaller than 8kb
     */
    private void checkFreeMemory() throws OwsExceptionReport {
        long freeMem;
        // check remaining free memory on heap
        // if too small, throw exception to avoid an OutOfMemoryError
        freeMem = Runtime.getRuntime().freeMemory();
        log.debug("Remaining Heap Size: " + freeMem);
        if (Runtime.getRuntime().totalMemory() == Runtime.getRuntime().maxMemory() && freeMem < 256000) { // 256000 accords to 256 kB
            // create service exception
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "The describeSensor response is to big for the maximal heap size = "
                                         + Runtime.getRuntime().maxMemory()
                                         + " Byte of the virtual machine! "
                                         + "Please either refine your describeSensor request to reduce the number of history sensor positions in the response or ask the administrator of this SOS to increase the maximum heap size of the virtual machine!");
            throw se;
        }
    }
}
