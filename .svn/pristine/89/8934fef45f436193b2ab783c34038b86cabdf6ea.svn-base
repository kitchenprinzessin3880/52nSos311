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

package org.n52.sos.ogc.om.features;

import net.opengis.gml.FeatureArrayPropertyType;
import net.opengis.gml.FeatureCollectionDocument2;

import org.apache.xmlbeans.XmlObject;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class represents a feature as xml
 * 
 * @author Carsten Hollmann
 *
 */
public class SosXmlFeature extends SosAbstractFeature {

	
    /** id */
    protected String id;

    /** name */
    protected String name;

    /** description */
    protected String description;

    /** geometry in OGC WKT format */
    protected Geometry jts_geom;

    /** number of EPSG code of the geometry */
    protected int epsgCode;

    /** name of the feature type (e.g. sa:Station) */
    protected String featureType;

    /** URL to the application schema of this feature of interest */
    protected String applicationSchemaType;
	
    protected XmlObject xmlFeatureObject;
    
	public SosXmlFeature(String featureID) {
		super(featureID);
	}
	
	/**
	 * Constructor
	 * @param idp id
	 * @param namep name of the feature
	 * @param descp description of the feature
	 * @param geomp geometry in OGC WKT format
	 * @param featureType 
	 * @param schemaLinkp schemaLink of the feature
	 */
	public SosXmlFeature(String idp, String namep, String descp,
			Geometry geomp, String featureType, String schemaLinkp) {
		super(idp, namep, descp, geomp, featureType, schemaLinkp);
	}

	@Override
	public String getElementName() {
		return null;
	}

	@Override
	public String getNamespace() {
		return null;
	}

	@Override
	public XmlObject toXmlBeans() {
		return null;
	}

    /**
     * Set the xml representation of the feature.
     * 
     * @param xmlObject xml representation
     */
    public void setXmlFeatureObject(XmlObject xmlObject) {
    	xmlFeatureObject = xmlObject;
    }
    
    /**
     * Get the xml representation of the feature.
     * 
     * @return XmlObject
     */
    public XmlObject getXmlFeatureObject() {
    	return xmlFeatureObject;
    }
    
    /**
     * Adds a xml representation of a feature.
     * If representation is a FeatureCollection, it will be append.
     * 
     * @param xmlObject xml representation
     */
    public void addXmlFeatureObject(XmlObject xmlObject) {
    	if (xmlObject instanceof FeatureCollectionDocument2){
    		FeatureCollectionDocument2 fcd = (FeatureCollectionDocument2)xmlFeatureObject;
        	FeatureCollectionDocument2 fcdAdd = (FeatureCollectionDocument2)xmlObject;
        	FeatureArrayPropertyType fapt = fcd.getFeatureCollection().addNewFeatureMembers();
        	fapt = fcdAdd.getFeatureCollection().getFeatureMembers();
        	xmlFeatureObject = fcd;
    	} else {
    		xmlFeatureObject = xmlObject;
    	}
    	
    }

}
