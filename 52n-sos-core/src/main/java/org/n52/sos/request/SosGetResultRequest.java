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

import org.n52.sos.ogc.filter.TemporalFilter;

/**
 * class represents a getResult request
 * 
 * @author Christoph Stasch
 *
 */
public class SosGetResultRequest extends AbstractSosRequest {

    /**requested service version*/
    private String version;

    /**id of the observation template, which was received in the former */
    private String observationTemplateId;

    /**requested eventTimes*/
    private TemporalFilter[] eventTimes;

    /**
     * @return the eventTimes
     */
    public TemporalFilter[] getEventTimes() {
        return eventTimes;
    }

    /**
     * @param eventTimes the eventTimes to set
     */
    public void setEventTimes(TemporalFilter[] eventTimes) {
        this.eventTimes = eventTimes;
    }

    /**
     * @return the observationTemplateId
     */
    public String getObservationTemplateId() {
        return observationTemplateId;
    }

    /**
     * @param observationTemplateId the observationTemplateId to set
     */
    public void setObservationTemplateId(String observationTemplateId) {
        this.observationTemplateId = observationTemplateId;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
