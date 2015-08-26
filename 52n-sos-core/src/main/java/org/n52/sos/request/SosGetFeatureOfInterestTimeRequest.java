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

import net.opengis.sos.x10.GetFeatureOfInterestTimeDocument.GetFeatureOfInterestTime.DomainFeature;

/**
 * class represents a getFeatureOfInterestTime request
 * 
 * @author Christoph Stasch
 *
 */
public class SosGetFeatureOfInterestTimeRequest extends AbstractSosRequest {

    /**service version parameter*/
    protected String version;

    /**represents the offering, which should be returned*/
    protected String offering;

    /**represents the id of the domainFeature, which should be returned*/
    protected DomainFeature domainFeature;

    /**represents the location parameter; this argument allows the request to be made based on a spatial filter expression rather than by using an actual feature ID.*/
    private SpatialFilter domainFeatureSpatialFilter;

    /**represents the id of the feature, which should be returned*/
    protected String featureID;

    /**represents the id of the procedure, which should be returned*/
    protected String[] procedure = null;

    /**represents the id of the phenomenon, which should be returned*/
    protected String[] phenomenon = null;

    /**mobileEnabled parameter*/
    protected boolean mobileEnabled;

    /**
     * 
     * @return offering
     */
    public String getOffering() {
        return offering;
    }

    /**
     * 
     * @param offering
     */
    public void setOffering(String offering) {
        this.offering = offering;
    }

    /**
     * 
     * @return featureID
     */
    public String getFeatureID() {
        return featureID;
    }

    /**
     * 
     * @param featureID
     */
    public void setFeatureID(String featureID) {
        this.featureID = featureID;
    }

    /**
     * 
     * @return id of the domain feature
     */
    public DomainFeature getDomainFeature() {
        return domainFeature;
    }

    /**
     * 
     * @param domainFeature id of the domain feature
     */
    public void setDomainFeature(DomainFeature domainFeature) {
        this.domainFeature = domainFeature;
    }

    /**
     * 
     * @return service version
     */
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return mobileEnabled
     */
    public boolean getMobileEnabled() {
        return mobileEnabled;
    }

    /**
     * 
     * @param mobileEnabled
     */
    public void setMobileEnabled(boolean mobileEnabled) {
        this.mobileEnabled = mobileEnabled;
    }

    /**
     * 
     * @return id of the phenomenon
     */
    public String[] getPhenomenon() {
        return phenomenon;
    }

    /**
     * 
     * @param phenomenon
     */
    public void setPhenomenon(String[] phenomenon) {
        this.phenomenon = phenomenon;
    }

    /**
     * 
     * @return id of the procedure
     */
    public String[] getProcedure() {
        return procedure;
    }

    /**
     * 
     * @param procedure
     */
    public void setProcedure(String[] procedure) {
        this.procedure = procedure;
    }

    /**
     * 
     * @return a spatial filter as general location parameter
     */
    public SpatialFilter getDomainFeatureSpatialFilter() {
        return domainFeatureSpatialFilter;
    }

    /**
     * 
     * @param domainFeatureSpatialFilter
     */
    public void setDomainFeatureSpatialFilter(SpatialFilter domainFeatureSpatialFilter) {
        this.domainFeatureSpatialFilter = domainFeatureSpatialFilter;
    }

}
