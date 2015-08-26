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

import net.opengis.gml.MetaDataPropertyType;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.PositionDocument.Position;
import net.opengis.sensorML.x101.PositionsDocument.Positions;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType;
import net.opengis.swe.x101.BooleanDocument.Boolean;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.VectorType.Coordinate;

import org.apache.xmlbeans.XmlCursor;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.ogc.om.SosOffering;
import org.n52.sos.ogc.om.SosPhenomenon;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

/**
 * class encapsulates method for mapping XmlBeans representation of SensorML elements to SOS objects.
 * Currently this class includes only one method to parse a System, as it is contained in the System template
 * for registering mobile sensorSystems to the SOSmobile
 * 
 * @author Christoph Stasch
 * 
 */
public class SensorMLDecoder {

    /** attribute name of status contained in capabilities element of sml:System */
    private static final String STATUS_NAME = "status";

    /** attribute name of mobile contained in capabilities element of sml:System */
    private static final String MOBILE_NAME = "mobile";

    /**
     * parses System, which should accords to SensorML System template for registerSensor requests to
     * SOSmobile
     * 
     * @param xb_system
     *        XMLBeans representation of SensorML System, whcih should be mapped to SOS's SensorSystem
     * @return Returns SensorSystem, which represents system contained in registerSensor request
     * @throws OwsExceptionReport
     *         if parsing of system failed
     */
    public static SensorSystem parseSystem(SystemType xb_system, String smlFile) throws OwsExceptionReport {
        SensorSystem system = null;
        String id = null;
        String descriptionURL = SosConstants.PROCEDURE_STANDARD_DESC_URL;
        String descriptionType = SosConstants.SENSORML_OUTPUT_FORMAT;

        boolean active = true;
        boolean mobile = false;

        // Get ID for Feature of Interest
        Identification[] xb_identifications = xb_system.getIdentificationArray();

        for (Identification identification : xb_identifications) {
            Identifier[] identifiers = identification.getIdentifierList().getIdentifierArray();
            for (Identifier identifier : identifiers) {
                if (identifier.getTerm().getDefinition().equals("urn:ogc:def:identifier:OGC:uniqueID")) {
                    id = identifier.getTerm().getValue();
                }
            }

        }
        if (id == null) {
            String message = "Error reading sensorML description: Please make sure that the identifier of the sml:System is contained! Have a look at the RegisterSensor example request or ask the admin of this SOS!";
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "SensorDescription",
                                 message);
            throw se;
        }
        Capabilities[] xb_capsArray = xb_system.getCapabilitiesArray();
        if (xb_capsArray.length == 1) {
            Capabilities xb_caps = xb_capsArray[0];
            AbstractDataRecordType xb_absDataRec = xb_caps.getAbstractDataRecord();
            if (xb_absDataRec instanceof SimpleDataRecordType) {
                SimpleDataRecordType xb_sdr = (SimpleDataRecordType) xb_absDataRec;
                AnyScalarPropertyType[] xb_fieldArray = xb_sdr.getFieldArray();
                for (AnyScalarPropertyType xb_propType : xb_fieldArray) {
                    String name = xb_propType.getName();
                    if (name.equals(MOBILE_NAME)) {
                        Boolean xb_bool = xb_propType.getBoolean();
                        if (xb_bool != null) {
                            mobile = xb_bool.getValue();
                        }
                        else {
                            String message = "Error reading sensorML description: Please make sure that the mobile field of the sml:System is contained in an sml:capabilities element! Have a look at the RegisterSensor example request or ask the admin of this SOS!";
                            OwsExceptionReport se = new OwsExceptionReport();
                            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                                 "SensorDescription",
                                                 message);
                            throw se;
                        }
                    }
                    else if (name.equals(STATUS_NAME)) {
                        Boolean xb_bool = xb_propType.getBoolean();
                        if (xb_bool != null) {
                            active = xb_bool.getValue();
                        }
                        else {
                            String message = "Error reading sensorML description: Please make sure that the status field of the sml:System is contained in an sml:capabilities element! Have a look at the RegisterSensor example request or ask the admin of this SOS!";
                            OwsExceptionReport se = new OwsExceptionReport();
                            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                                 "SensorDescription",
                                                 message);
                            throw se;
                        }
                    }
                }
            }
        }
        else {
            String message = "Error reading sensorML description: exactly one 'sml:capabilities' element has to be contained in the sml:System of the SensorDescription parameter!";
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "SensorDescription",
                                 message);
            throw se;
        }

        Position xb_position = xb_system.getPosition();

        // If none Position is given try first element of PositionList
        // TODO: make this work like it should
        if (xb_position == null) {
            try {
                Positions xb_positions = xb_system.getPositions();
                xb_position = xb_positions.getPositionList().getPositionArray(0);
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     null,
                                     "Error reading position, please ensure that the position is included");
                throw se;
            }
        }
        PositionType xb_posType = xb_position.getPosition();

        Point actualPosition = parsePointPosition(xb_posType);

        Outputs xb_outputs = xb_system.getOutputs();
        if (xb_outputs == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "No outputs are set in the registerSensor system parameter!!");
            throw se;
        }

        Collection<SosPhenomenon> phenomena = parseOutputs(xb_outputs);

        if (id != null && !id.equals("")) {
            system = new SensorSystem(id,
                                      descriptionURL,
                                      descriptionType,
                                      smlFile,
                                      actualPosition,
                                      active,
                                      mobile,
                                      phenomena);
        }
        return system;
    }

    /**
     * parses the outputs of a SensorML system and returns SosPhenomena representing the observed outputs of
     * the sensor system; ATTENTION: currently only implemented for Quantities, Texts and Positions!
     * 
     * @param xb_outputs
     *        XMLBeans representation of SensorML System's output element
     * @return Returns collection containing SosPhenomena representing the outputs
     * @throws OwsExceptionReport
     */
    private static Collection<SosPhenomenon> parseOutputs(Outputs xb_outputs) throws OwsExceptionReport {
        OutputList xb_outputList = xb_outputs.getOutputList();
        // xb_outputList.getOutputArray(1);
        IoComponentPropertyType[] xb_outputArray = xb_outputList.getOutputArray();
        ArrayList<SosPhenomenon> phenomena = new ArrayList<SosPhenomenon>(xb_outputArray.length);
        for (int i = 0; i < xb_outputArray.length; i++) {
            ArrayList<SosOffering> offerings = null;
            IoComponentPropertyType xb_comPropType = xb_outputArray[i];
                        
            Quantity xb_quantity = null;
            Text xb_text = null;
            AbstractDataRecordType xb_obsProp = null;
            
            // determine if quantities or positions will be observed
            if (xb_comPropType.getQuantity() != null) {
            	xb_quantity = xb_comPropType.getQuantity();
            }
            else if (xb_comPropType.getText() != null) {
            	xb_text = xb_comPropType.getText();
            }
            else if (xb_comPropType.getAbstractDataRecord() != null) {
            	if (xb_comPropType.getName().equals("position")) {
            		xb_obsProp = xb_comPropType.getAbstractDataRecord();
            	}	
            }
            
            if (xb_quantity != null || xb_text != null || xb_obsProp != null) {
            	
            	MetaDataPropertyType[] metaDataArray = null;
                String phenID = null;
                String valueType = null;
            	
            	if (xb_quantity != null) {
            		metaDataArray = xb_quantity.getMetaDataPropertyArray();
                    phenID = xb_quantity.getDefinition();
                    valueType = SosConstants.ValueTypes.numericType.name();
            	}
            	else if (xb_text != null) {
            		metaDataArray = xb_text.getMetaDataPropertyArray();
                    phenID = xb_text.getDefinition();
                    valueType = SosConstants.ValueTypes.textType.name();
            	}
            	else if (xb_obsProp != null) {
            		metaDataArray = xb_obsProp.getMetaDataPropertyArray();
                    phenID = xb_obsProp.getDefinition();
                    valueType = SosConstants.ValueTypes.spatialType.name();
            	}
                
                checkPhenID(phenID);
                String uom = null;
                String phenDesc = null;

                // parse offering of phenomenon
                for (MetaDataPropertyType metaData : metaDataArray) {
                    String offeringID = null;
                    String offeringName = null;
                    try {
                        XmlCursor mdCursor = metaData.newCursor();
                        mdCursor.toFirstContentToken();
                        mdCursor.toFirstChild();

                        // read id and name of offering
                        if (mdCursor.getName().getLocalPart().equals("id")) {
                            offeringID = mdCursor.getTextValue();
                        }
                        else if (mdCursor.getName().getLocalPart().equals("name")) {
                            offeringName = mdCursor.getTextValue();
                        }
                        mdCursor.toNextSibling();
                        if (mdCursor.getName().getLocalPart().equals("id")) {
                            offeringID = mdCursor.getTextValue();
                        }
                        else if (mdCursor.getName().getLocalPart().equals("name")) {
                            offeringName = mdCursor.getTextValue();
                        }
                    }
                    catch (Exception e) {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             "Offering",
                                             "Error Reading Offering of the Sensor, please check if Output Property has an Child similar to:\n"
                                                     + "<gml:metaDataProperty>" + "	<offering>"
                                                     + "		<id>OfferingID1</id>"
                                                     + "		<name>OfferingName1</name>"
                                                     + "	</offering>" + "</gml:metaDataProperty>");
                        throw se;
                    }

                    if (offeringID == null) {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             "Offering",
                                             "Error Reading Offering of the Sensor, please check if Output Property has an Child similar to:\n"
                                                     + "<gml:metaDataProperty>" + "	<offering>"
                                                     + "		<id>OfferingID1</id>"
                                                     + "		<name>OfferingName1</name>"
                                                     + "	</offering>" + "</gml:metaDataProperty>");
                        throw se;
                    }

                    else {
                        offerings = new ArrayList<SosOffering>(1);
                        offerings.add(new SosOffering(offeringID, offeringName, null, null));
                    }

                }

                // set uom only for quantities
                String code = null;
                if (xb_quantity != null) {
                	code = xb_quantity.getUom().getCode();
                	
                	if (code != null && !code.equals("")) {
                        uom = code;
                    }
                }

                SosPhenomenon phen = new SosPhenomenon(phenID,
                                                       phenDesc,
                                                       uom,
                                                       null,
                                                       valueType,
                                                       null,
                                                       offerings);
                phenomena.add(phen);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "sml:outputs",
                                     "Currently, only quantities, texts and positions are supported for output phenomena of sensors!");
                throw se;
            }
        }
        return phenomena;
    }

    /**
     * parses point position of SensorML system
     * 
     * @param xb_positionType
     *        XMLBeans representation of position, which should represent point position of sensor system
     * @return Returns JTS Point created from XMLBean
     * @throws OwsExceptionReport
     *         if parsing of point position failed
     */
    public static Point parsePointPosition(PositionType xb_positionType) throws OwsExceptionReport {
        String srsString = xb_positionType.getReferenceFrame();
        srsString = srsString.toLowerCase();
        int epsgCode = 0;
        String srsNamePrefix = SosConfigurator.getInstance().getSrsNamePrefix();
        if ( !srsString.startsWith(srsNamePrefix.toLowerCase())) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
                                 "The 'referenceFrame' attribute of swe:positionType has to be set and must be epsg code with prefix '"
                                         + srsNamePrefix + "' !!");
            throw se;
        }
        try {
            epsgCode = new Integer(srsString.substring(srsString.lastIndexOf(":") + 1)).intValue();
            // epsgCode = new
            // Integer(srsString.replace(SosConstants.SRS_NAME_PREFIX,
            // "")).intValue();
        }
        catch (Exception e) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
                                 "The 'referenceFrame' attribute of swe:positionType has to be set and must be epsg code with prefix '"
                                         + srsNamePrefix + "' !!");
            throw se;
        }
        VectorPropertyType xb_location = xb_positionType.getLocation();
        VectorType xb_vector = xb_location.getVector();
        Coordinate[] xb_coordArray = xb_vector.getCoordinateArray();
        Double xCoord = Double.NaN;
        Double yCoord = Double.NaN;
        Double zCoord = Double.NaN;
        for (Coordinate coord : xb_coordArray) {
            // latitude/northing or longitude/easting
        	try {
	       		 if (coord.getName().equals("easting") || coord.getName().equals("longitude")) {
	    			xCoord = new Double(coord.getQuantity().getValue());
	             }
	             else if (coord.getName().equals("northing") || coord.getName().equals("latitude")) {
	            	yCoord = new Double(coord.getQuantity().getValue());
	             }
	             else if (coord.getName().equals("altitude")) {
	            	zCoord = new Double(coord.getQuantity().getValue());
	             }
        		
			} catch (Exception e) {
				OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
	                                 "The swe:coordinate name ist not valid! Valid are 'easting' or 'longitude', 'northing' or 'latitude' and 'altitude' !");
	            throw se;
			}
           
        }
        if (xb_coordArray.length < 2 || xb_coordArray.length > 3) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
                                 "There are 2 or 3 coordinates required");
            throw se;
        }
        if (xCoord.equals(Double.NaN) || yCoord.equals(Double.NaN) || (xb_coordArray.length == 3 && zCoord.equals(Double.NaN))) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 SosConstants.RegisterSensorParams.SensorDescription.name(),
                                 "If there are 2 Coordinates AxisID have to be x and y, if there are 3, axisID have to be x,y and z");
            throw se;
        }

        GeometryFactory geomFac = new GeometryFactory();
        com.vividsolutions.jts.geom.Coordinate coord;
        if (zCoord.equals(Double.NaN)) {
            coord = new com.vividsolutions.jts.geom.Coordinate(xCoord, yCoord);
        }
        else {
            coord = new com.vividsolutions.jts.geom.Coordinate(xCoord, yCoord, zCoord);
        }
        com.vividsolutions.jts.geom.Coordinate[] coordArray = {coord};
        CoordinateArraySequence coordSeqArray = new CoordinateArraySequence(coordArray);
        Point point = new Point(coordSeqArray, geomFac);
        point.setSRID(epsgCode);
        return point;
    }

    /**
     * throws service exception, if passed phenID is null or empty string
     * 
     * @param phenID
     *            phenID, which should be checked
     * @throws OwsExceptionReport
     *             throws service exception, if passed phenID is null or empty
     *             string
     */
    private static void checkPhenID(String phenID) throws OwsExceptionReport {
        if (phenID == null || phenID.equals("")) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "def attribute has to be set in quantities of outputList in sensor system, which should be registered !!");
            throw se;
        }
    }
}
