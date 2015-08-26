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
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.quality.SosQuality;

/**
 * class represents a category observation
 * 
 * @author Christoph Stasch
 * 
 */
public class SosCategoryObservation extends AbstractSosObservation {

    /** text value of the CategoryObservation */
    private String textValue;

    /** unit for the observation value */
    private String unit;

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
    public SosCategoryObservation(ISosTime time,
                                  String obsID,
                                  String procID,
                                  SosAbstractFeature foi,
                                  Collection<SosAbstractFeature> domainFeatureIDs,
                                  String phenID,
                                  String offeringID,
                                  String mimeType,
                                  String value,
                                  String unit,
                                  Collection<SosQuality> quality) {
        super(time, obsID, procID, domainFeatureIDs, phenID, foi, offeringID, mimeType, quality);
        this.textValue = value;
        this.unit = unit;
    }

    /**
     * @return the textValue
     */
    public String getTextValue() {
        return textValue;
    }

    /**
     * @param textValue
     *        the textValue to set
     */
    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *        the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNamespace() {
        return OMConstants.NS_OM;
    }

    public String getElementName() {
        return OMConstants.EN_CATEGORY_OBSERVATION;
    }

    @Override
    public String getStringValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
