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

import org.n52.sos.ogc.om.AbstractSosObservation;

/**
 * class represents an InsertObservation request
 * 
 * @author Christoph Stasch
 * 
 */
public class SosInsertObservationRequest extends AbstractSosRequest {

    /** id of sensor, which has produced the observation */
    protected String assignedSensorId;

    /** observation, which should be inserted */
    protected Collection<AbstractSosObservation> observations;

    /** mobileEnabled parameter */
    protected boolean mobileEnabled;

    /**
     * constructor
     * 
     * @param sensorID
     *        id of sensor, which has produced the observation
     * @param obs
     *        observation, which should be inserted
     */
    public SosInsertObservationRequest(String sensorID,
                                       Collection<AbstractSosObservation> obs,
                                       boolean mobileEnabled) {
        this.assignedSensorId = sensorID;
        this.observations = obs;
        this.mobileEnabled = mobileEnabled;
    }

    /**
     * @return the assignedSensorId
     */
    public String getAssignedSensorId() {
        return assignedSensorId;
    }

    /**
     * @param assignedSensorId
     *        the assignedSensorId to set
     */
    public void setAssignedSensorId(String assignedSensorId) {
        this.assignedSensorId = assignedSensorId;
    }

    /**
     * @return the observation
     */
    public Collection<AbstractSosObservation> getObservation() {
        return observations;
    }

    /**
     * @param observation
     *        the observation to set
     */
    public void setObservation(Collection<AbstractSosObservation> observation) {
        this.observations = observation;
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

}
