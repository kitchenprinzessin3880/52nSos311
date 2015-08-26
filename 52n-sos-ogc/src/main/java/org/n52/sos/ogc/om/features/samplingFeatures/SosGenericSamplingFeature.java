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

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.features.SosAbstractFeature;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class represents a station feature
 * 
 * @author Christoph Stasch
 * 
 */
public class SosGenericSamplingFeature extends SosAbstractSamplingFeature {

    /** name of featuretype */
    public static final String FEATURE_TYPE = "sos:GenericSamplingFeature";

    /** name of feature */
    private static final String FEATURE_NAME = "GenericSamplingFeature";

    /** namespace of feature */
    private static final String NAMESPACE = OMConstants.NS_SOS;

    /**
     * constructor with id and GML encoded feature string
     * 
     * @param featID
     *        id of sampling feature
     * @param gmlFeature
     *        GML encoded feature string
     */
    public SosGenericSamplingFeature(String featID) {
        super(featID);
    }

    /**
     * constructor with all parameters
     * 
     * @param idp
     *        id of samplingFeature
     * @param namep
     *        name of samplingFeature
     * @param descp
     *        description of samplingFeature
     * @param geomp
     *        geometry of samplingFeature
     * @param epsgCodep
     *        epsgCode of geometry
     * @param schemaLinkp
     *        link to application schema
     */
    public SosGenericSamplingFeature(String gmlIDp,
                                     String namep,
                                     String descp,
                                     Geometry geomp,
                                     String schemaLinkp,
                                     Collection<SosAbstractFeature> domainFeatureIDs) {
        super(gmlIDp, namep, descp, geomp, FEATURE_TYPE, schemaLinkp, domainFeatureIDs);
    }

    /**
     * simple constructor with only geometry parameter; other attributes are set to 'null'
     * 
     * @param geomp
     *        geometry of sampling feature
     */
    public SosGenericSamplingFeature(String gmlIDp,
                                     Geometry geomp,
                                     Collection<SosAbstractFeature> domainFeatureIDs) {
        super(gmlIDp, null, null, geomp, FEATURE_TYPE, null, domainFeatureIDs);
    }

    /**
     * @return the elementName used in the O&M response
     */
    public String getElementName() {
        return FEATURE_NAME;
    }

    /**
     * @return the namespace for the element name used in the O&M response
     */
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public XmlObject toXmlBeans() {
        // TODO Auto-generated method stub
        return null;
    }
}
