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

package org.n52.sos.ogc.sensorML;

import java.util.Collection;

import org.n52.sos.ogc.om.SosPhenomenon;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class represents a SensorML System
 * 
 * @author staschc
 * 
 */
public class SensorSystem {

    /** id of sensor system */
    private String id;

    /** URL to sensor description */
    private String descriptionURL;

    /**
     * description type (either 'text/xml;subtype="SensorML"' or 'text/xml;subtype="TML"'
     */
    private String descriptionType;

    /** string representation of complete sensorML file */
    private String sml_file;

    /** last measured position of sensor */
    private Geometry actualPosition;

    /** indicates, whether system is mobile (true) or not (false) */
    private boolean mobile;

    /**
     * indicates, whether system is active in producing data for SOS (true) or not (false)
     */
    private boolean active;

    /** output phenomena of the sensors, which belong to this system */
    private Collection<SosPhenomenon> outputs;

    // /** constructor with position and mobile flag
    // *
    // *
    // * @param positionp
    // * JTS Point representing position of system
    // * @param mobilep
    // * indicates, whether system is mobile (true) or fixed (false)
    // * @param outputsp
    // * phenomena, for which sensor system produces observations
    // */
    // public SensorSystem(Point positionp, boolean mobilep,
    // Collection<SosPhenomenon> outputsp) {
    // this.actual_position = positionp;
    // this.mobile = mobilep;
    // this.outputs = outputsp;
    // }
    //
    // /**
    // * constructor with position and mobile flag
    // *
    // * @param positionp
    // * JTS Point representing position of system
    // * @param mobilep
    // * indicates, whether system is mobile (true) or fixed (false)
    // * @param components
    // * procedures with phenomena, for which sensor system produces
    // observations
    // */
    // public SensorSystem(String idp, Point positionp, boolean mobilep,
    // Collection<SosPhenomenon> components) {
    // this.id = idp;
    // this.actual_position = positionp;
    // this.mobile = mobilep;
    // this.outputs = components;
    // }

    /**
     * constructor with position and mobile flag
     * 
     * @param positionp
     *        JTS Point representing position of system
     * @param mobilep
     *        indicates, whether system is mobile (true) or fixed (false)
     * @param components
     *        procedures with phenomena, for which sensor system produces observations
     */
    public SensorSystem(String idp,
                        String descriptionURLp,
                        String descriptionTypep,
                        String smlFilep,
                        Geometry positionp,
                        boolean activep,
                        boolean mobilep,
                        Collection<SosPhenomenon> phens) {
        this.id = idp;
        this.descriptionURL = descriptionURLp;
        this.descriptionType = descriptionTypep;
        this.sml_file = smlFilep;
        this.actualPosition = positionp;
        this.active = activep;
        this.mobile = mobilep;
        this.outputs = phens;
    }

    /**
     * constructor
     * 
     * @param idp
     *        id of sensor system
     * @param positionp
     *        position of sensor system
     * @param mobilep
     *        indicates, whether system is mobile (true) or stationary (false)
     * @param ativep
     *        indicates, whether system is active in producing data for SOS (true) or not (false)
     */
    public SensorSystem(String idp, Geometry positionp, boolean mobilep, boolean ativep) {
        this.id = idp;
        this.actualPosition = positionp;
        this.mobile = mobilep;
        this.active = ativep;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *        the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the mobile
     */
    public boolean isMobile() {
        return mobile;
    }

    /**
     * @param mobile
     *        the mobile to set
     */
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *        the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the outputs
     */
    public Collection<SosPhenomenon> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs
     *        the outputs to set
     */
    public void setOutputs(Collection<SosPhenomenon> outputs) {
        this.outputs = outputs;
    }

    /**
     * @return the descriptionURL
     */
    public String getDescriptionURL() {
        return descriptionURL;
    }

    /**
     * @param descriptionURL
     *        the descriptionURL to set
     */
    public void setDescriptionURL(String descriptionURL) {
        this.descriptionURL = descriptionURL;
    }

    /**
     * @return the descriptionType
     */
    public String getDescriptionType() {
        return descriptionType;
    }

    /**
     * @param descriptionType
     *            the descriptionType to set
     */
    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    /**
     * @return the actualPosition
     */
    public Geometry getActualPosition() {
        return actualPosition;
    }

    /**
     * @param actualPosition
     *            the actualPosition to set
     */
    public void setActualPosition(Geometry actualPosition) {
        this.actualPosition = actualPosition;
    }

    /**
     * @return the sml_file
     */
    public String getSml_file() {
        return sml_file;
    }

    /**
     * @param sml_file
     *            the sml_file to set
     */
    public void setSml_file(String sml_file) {
        this.sml_file = sml_file;
    }
}
