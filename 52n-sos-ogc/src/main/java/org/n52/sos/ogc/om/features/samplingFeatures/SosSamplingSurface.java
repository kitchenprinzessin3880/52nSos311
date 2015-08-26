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

 Author: Christoph Stasch
 Created: <CREATION DATE>
 Modified: 08/11/2008
***************************************************************/
package org.n52.sos.ogc.om.features.samplingFeatures;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.om.OMConstants;

import com.vividsolutions.jts.geom.Polygon;


/**
 * class represents sa:SamplingSurface; 
 * could be used in featureOfInterest of observation to represent features with Polygon as geometry
 * 
 * @author staschc
 *
 */
public class SosSamplingSurface extends SosAbstractSamplingFeature{
	
	/**
     * constructor with all parameters
     * 
     * @param idp
     * @param namep
     * @param descp
     * @param geomp
     * @param epsgCodep
     * @param schemaLinkp
     */
    public SosSamplingSurface(String idp,
                            String namep,
                            String descp,
                            Polygon geomp,
                            String featureType,
                            String schemaLinkp) {
        super(idp, namep, descp, geomp, featureType, schemaLinkp, null);
    }

    /**
     * constructor
     * 
     * @param featureID
     *        id of the feature
     */
    public SosSamplingSurface(String featureID) {
        super(featureID);
    }

    /**
     * @return the elementName used in the O&M response
     */
    public String getElementName() {
        return OMConstants.EN_SAMPLINGSURFACE;
    }

    /**
     * @return the namespace for the element name used in the O&M response
     */
    public String getNamespace() {
        return OMConstants.NS_SA;
    }

	/**
	 * NOT YET IMPLEMENTED!!
	 * 
	 */
	public XmlObject toXmlBeans() {
		// TODO Auto-generated method stub
		return null;
	}

}
