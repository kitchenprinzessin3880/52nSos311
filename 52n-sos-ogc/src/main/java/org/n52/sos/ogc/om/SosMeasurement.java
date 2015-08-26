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

package org.n52.sos.ogc.om;

import java.util.Collection;

import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.quality.SosQuality;

/**
 * class represents a measurement observation
 * 
 * @author Christoph Stasch
 * 
 */
public class SosMeasurement extends AbstractSosObservation {

    /** numerical value of the observation */
    private double value;

    /** units of the measurement value */
    private String unitsOfMeasurement;

    /**
     * constructor
     * 
     * @param time
     *        time at which the observation event took place
     * @param obsID
     *        id of the observation
     * @param procID
     *        id of the procedure, by which the value was produced
     * @param foiID
     *        id of the feature of interest, to which the observation belongs
     * @param phenID
     *        id of the phenomenon, of which the value is
     * @param offeringID
     *        id of the offering to which this observation belongs
     * @param mimeType
     *        mimeType of the observation result
     * @param value
     *        result value
     */
    public SosMeasurement(ISosTime time,
                          String obsID,
                          String procID,
                          Collection<SosAbstractFeature> domainFeatureIDs,
                          String phenID,
                          SosAbstractFeature foi,
                          String offeringID,
                          String mimeType,
                          double value,
                          String unitsOfMeasurement,
                          Collection<SosQuality> quality) {
        super(time, obsID, procID, domainFeatureIDs, phenID, foi, offeringID, mimeType, quality);
        this.unitsOfMeasurement = unitsOfMeasurement;
        this.value = value;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value
     *        the value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * @return the unitsOfMeasurement
     */
    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    /**
     * @param unitsOfMeasurement
     *        the unitsOfMeasurement to set
     */
    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    /**
     * returns the namespace of this measurement
     */
    public String getNamespace() {
        return OMConstants.NS_OM;
    }

    /**
     * returns the element name of this measurement
     * 
     */
    public String getElementName() {
        return OMConstants.EN_MEASUREMENT;
    }

    /**
     * sets the sampling time of this measurement
     * 
     * @param timeInstant
     */
    public void setSamplingTime(TimeInstant timeInstant) {
        setSamplingTime(timeInstant);
    }

    @Override
    public String getStringValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
