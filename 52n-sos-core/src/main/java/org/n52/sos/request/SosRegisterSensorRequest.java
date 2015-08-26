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

package org.n52.sos.request;

import java.util.Collection;

import org.n52.sos.ogc.om.SosPhenomenon;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * class represents a registerSensor request
 * 
 * @author Christoph Stasch
 * 
 */
public class SosRegisterSensorRequest extends AbstractSosRequest {

    /** SensorML system, which should be registered */
    protected SensorSystem system;

    /** phenomena, for which the sensor, which should be registered, offers values for */
    protected Collection<SosPhenomenon> phenomena;

    /** phenomena, for which the sensor, which should be registered, offers values for */
    protected Collection<SosAbstractFeature> domainFeatures;

    /** String representing xml sensor description */
    protected String sensorDescription;

    /** mobileEnabled parameter */
    protected boolean mobileEnabled;

    /**
     * constructor
     * 
     * @param systemp
     *        sensor system, which should be registered
     * @param sosComponents
     *        offering ID, for which the sensor, which should be registered, offers values for
     * @param sensorDescriptionp
     *        offering ID, for which the sensor, which should be registered, offers values for
     */
    public SosRegisterSensorRequest(SensorSystem systemp,
                                    Collection<SosPhenomenon> sosComponents,
                                    String sensorDescriptionp,
                                    Collection<SosAbstractFeature> domainFeatures,
                                    boolean mobileEnabled) {
        this.system = systemp;
        this.phenomena = sosComponents;
        this.sensorDescription = sensorDescriptionp;
        this.domainFeatures = domainFeatures;
        this.mobileEnabled = mobileEnabled;
    }

    /**
     * @return the phenomena
     */
    public Collection<SosPhenomenon> getPhenomena() {
        return phenomena;
    }

    /**
     * @param phenomena
     *        the phenomena to set
     */
    public void setPhenomena(Collection<SosPhenomenon> phenomena) {
        this.phenomena = phenomena;
    }

    /**
     * @return the sensorDescription
     */
    public String getSensorDescription() {
        return sensorDescription;
    }

    /**
     * @param sensorDescription
     *        the sensorDescription to set
     */
    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    /**
     * @return the system
     */
    public SensorSystem getSystem() {
        return system;
    }

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SensorSystem system) {
        this.system = system;
    }

    /**
     * 
     * @return mobileEnabled
     */
    public boolean isMobileEnabled() {
        return mobileEnabled;
    }

    /**
     * 
     * @param mobileEnabled
     */
    public void setMobileEnabled(boolean mobileEnabled) {
        this.mobileEnabled = mobileEnabled;
    }

    public Collection<SosAbstractFeature> getDomainFeatures() {
        return domainFeatures;
    }

    public void setDomainFeatures(Collection<SosAbstractFeature> domainFeatures) {
        this.domainFeatures = domainFeatures;
    }

}
