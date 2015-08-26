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

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;

/**
 * class represents a getDomainFeature request
 * 
 * @author Stephan Kuenster
 *
 */
public class SosGetDomainFeatureRequest extends AbstractSosRequest {

    /**service version parameter*/
    private String version;

    /**represents the id of the feature, which should be returned*/
    private String[] domainFeatureIDs;

    /**represents the location parameter; this argument allows the request to be made based on a spatial filter expression rather than by using an actual feature ID.*/
    private SpatialFilter location;

    /**Specifies the time for which the feature of interest is to be queried. */
    private TemporalFilter[] eventTimes;

    /**
     * @return the eventTime
     */
    public TemporalFilter[] getEventTimes() {
        return eventTimes;
    }

    /**
     * @param eventTime the eventTime to set
     */
    public void setEventTimes(TemporalFilter[] eventTime) {
        this.eventTimes = eventTime;
    }

    /**
     * @return the location
     */
    public SpatialFilter getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(SpatialFilter location) {
        this.location = location;
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

    /**
     * @return the domainFeatureIDs
     */
    public String[] getDomainFeatureIDs() {
        return domainFeatureIDs;
    }

    /**
     * @param domainFeatureIDs the domainFeatureIDs to set
     */
    public void setDomainFeatureIDs(String[] domainFeatureIDs) {
        this.domainFeatureIDs = domainFeatureIDs;
    }

}
