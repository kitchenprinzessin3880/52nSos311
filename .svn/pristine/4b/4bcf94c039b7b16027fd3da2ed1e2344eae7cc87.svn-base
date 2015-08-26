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

package org.n52.sos.ogc.om.features.samplingFeatures;

import java.util.Collection;

import org.n52.sos.ogc.om.features.SosAbstractFeature;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract super class for all sampling features; currently has additional db_id value
 * 
 * @author Christoph Stasch
 * 
 */
public abstract class SosAbstractSamplingFeature extends SosAbstractFeature {

    /** id of the domain features, which must spatially contain this sampling feature */
    private Collection<SosAbstractFeature> domainFeatureIDs;

    /**
     * constructor with id and GML encoded feature string
     * 
     * @param featID
     *        id of sampling feature
     * @param gmlFeature
     *        GML encoded feature string
     */
    public SosAbstractSamplingFeature(String featID) {
        super(featID);
    }

    /**
     * constructor with all parameters
     * 
     * @param db_idp
     *        id of samplingFeature
     * @param gmlIDp
     *        gml:id of the samplingFeature
     * @param namep
     *        name of samplingFeature
     * @param descp
     *        description of samplingFeature
     * @param geomp
     *        geometry of samplingFeature
     * @param gmlFeaturep
     *        GML encoded representation of feature
     * @param featureTypep
     *        type of feature
     * @param schemaLinkp
     *        link to application schema
     * @param domainFeaturesp
     *        ids of the domain features, this sampling feature belongs to
     * 
     */
    public SosAbstractSamplingFeature(String gmlIDp,
                                      String namep,
                                      String descp,
                                      Geometry geomp,
                                      String featureTypep,
                                      String schemaLinkp,
                                      Collection<SosAbstractFeature> domainFeaturesp) {
        super(gmlIDp, namep, descp, geomp, featureTypep, schemaLinkp);
        this.domainFeatureIDs = domainFeaturesp;
    }

    /**
     * @return the domainFeatureIDs
     */
    public Collection<SosAbstractFeature> getDomainFeatureIDs() {
        return domainFeatureIDs;
    }

    /**
     * @param domainFeatureIDs
     *        the domainFeatureIDs to set
     */
    public void setDomainFeatureIDs(Collection<SosAbstractFeature> domainFeatureIDs) {
        this.domainFeatureIDs = domainFeatureIDs;
    }

    /**
     * adds a domain feature to this sampling feature
     * 
     * @param domainFeatureID
     *        id of the domain feature, which should be added
     */
    public void addDomainFeature(SosAbstractFeature domainFeatureID) {
        this.domainFeatureIDs.add(domainFeatureID);
    }

}
