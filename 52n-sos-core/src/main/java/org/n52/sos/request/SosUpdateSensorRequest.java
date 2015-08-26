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

import net.opengis.gml.TimePositionType;

import org.n52.sos.ogc.om.features.SosAbstractFeature;

import com.vividsolutions.jts.geom.Point;

/**
 * class represents a UpdateSensor request
 * 
 * @author Christoph Stasch
 * 
 */
public class SosUpdateSensorRequest extends AbstractSosRequest {

    /** id of the sensor, which should be updated */
    private String sensorID;

    /** time when the sensor was an the new position */
    private TimePositionType time;
    
    /** new position of sensor, which should be set to actual position */
    private Point position;

    /** indicates, whether sensor is active (true) or inactive (false) */
    private boolean active;

    /** indicates, whether sensor is mobile(true) or stationary(false) */
    private boolean mobile;

    /** domain feature */
    private SosAbstractFeature domFeat;

    /**
     * constructor
     * 
     * @param sensorID
     *        id of the sensor, which should be updated
     * @param position
     *        new position of sensor, which should be set to actual position
     * @param activep
     *        indicates, whether sensor is active (true) or inactive (false)
     * @param mobilep
     *        indicates, whether sensor is mobile(true) or stationary(false)
     */
    public SosUpdateSensorRequest(String sensorID,
    							  TimePositionType time,
                                  Point position,
                                  boolean activep,
                                  boolean mobilep,
                                  SosAbstractFeature domFeat) {
        this.sensorID = sensorID;
        this.time = time;
        this.position = position;
        this.mobile = mobilep;
        this.active = activep;
        this.domFeat = domFeat;
    }

    /**
     * 
     * @return Returns new position of sensor, which should be set to actual position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * 
     * @return Returns new id of sensor, which should be updated
     */
    public String getSensorID() {
        return sensorID;
    }

    /**
     * @return Returns the time when the sensor was on the new position
     */
    public TimePositionType getTime(){
    	return time;
    }
    
    /**
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return Returns the mobile
     */
    public boolean isMobile() {
        return mobile;
    }

    /**
     * 
     * @return Returns the domainFeature
     */
    public SosAbstractFeature getDomFeat() {
        return domFeat;
    }

}
