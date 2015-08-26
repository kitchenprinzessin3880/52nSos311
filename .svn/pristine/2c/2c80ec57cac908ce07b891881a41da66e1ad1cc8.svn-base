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

import org.joda.time.DateTime;

import com.vividsolutions.jts.geom.Point;

/**
 * represents a timestamp in a procedure history
 * 
 * @author Christoph Stasch
 * 
 */
public class ProcedureHistoryEvent {

    /** id of sensor */
    private String procID;

    /** position of sensor */
    private Point position;

    /** time of procedure event */
    private DateTime timeStamp;

    /** status of sensor (active or inactive) */
    private boolean isActive;

    /** mobility of sensor (mobile or fixed) */
    private boolean isMobile;

    /**
     * constructor
     * 
     * @param procIDp
     *        id of sensor
     * @param positionp
     *        position of sensor
     * @param isActivep
     *        status of sensor (active or inactive)
     * @param isMobilep
     *        mobility of sensor (mobile or fixed)
     * @param timeStamp
     *        time stamp of event
     */
    public ProcedureHistoryEvent(String procIDp,
                                 Point positionp,
                                 boolean isActivep,
                                 boolean isMobilep,
                                 DateTime timeStamp) {
        setProcID(procIDp);
        setPosition(positionp);
        setActive(isActivep);
        setMobile(isMobilep);
        setTimeStamp(timeStamp);
    }

    /**
     * Returns true, if sensor is active, false otherwise
     * 
     * @return Returns the isActive
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * sets the isActive property
     * 
     * @param isActive
     *        the isActive to set
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Returns true, if sensor is mobile, false otherwise
     * 
     * @return Returns the isMobile
     */
    public boolean isMobile() {
        return isMobile;
    }

    /**
     * sets the isMobile property to the passed value
     * 
     * @param isMobile
     *        the isMobile to set
     */
    public void setMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    /**
     * Returns the position of the sensor at this history event
     * 
     * @return Returns the position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * sets the position of this sensor at the history event
     * 
     * @param position
     *        the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Returns id of sensor of this history event
     * 
     * @return Returns the procID
     * 
     */
    public String getProcID() {
        return procID;
    }

    /**
     * sets id of sensor of this history event
     * 
     * @param procID
     *        the procID to set
     */
    public void setProcID(String procID) {
        this.procID = procID;
    }

    /**
     * Returns the timeStamp of this history event
     * 
     * @return Returns the timeStamp
     */
    public DateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * sets the timeStamp of this history event
     * 
     * @param timeStamp
     *        the timeStamp to set
     */
    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}
