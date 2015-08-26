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

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.om.OMConstants;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface for encoding the feature of interest. Necessary because different feature types should be
 * supported. The SOS database or another feature source (e.g. WFS) should provide information about the
 * application schema.
 * 
 * @author Christoph Stasch
 * 
 */
public abstract class SosAbstractFeature {

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

    /**
     * constructor with feature ID
     *
     */
    public SosAbstractFeature(String featureID) {
        String notSet = OMConstants.PARAMETER_NOT_SET;
        setId(featureID);
        setFeatureType(notSet);
        setName(notSet);
        setDescription(notSet);
        setApplicationSchemaType(notSet);
    }

    /**
     * constructor
     * 
     * @param idp
     *        id
     * @param namep
     *        name of the feature of interest
     * @param descp
     *        description of the feature of interest
     * @param geomp
     *        geometry in OGC WKT format
     * @param schemaLinkp
     *        schemaLink of the feature of interest
     */
    public SosAbstractFeature(String idp,
                              String namep,
                              String descp,
                              Geometry geomp,
                              String featureType,
                              String schemaLinkp) {
        setId(idp);
        setName(namep);
        setDescription(descp);
        setGeom(geomp);
        setEpsgCode(geomp.getSRID());
        setFeatureType(featureType);
        setSchemaLink(schemaLinkp);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SosAbstractFeature) {
            SosAbstractFeature feature = (SosAbstractFeature) o;
            if (feature.getId().equals(this.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * encodes feature of interest into a XmlBeans XmlObject
     * 
     * @param feature
     *        the feature, which should be encoded
     * @return Returns XmlObject, which represents the passed feature as an XmlBean
     */
    public abstract XmlObject toXmlBeans();

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *        The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the geom.
     */
    public Geometry getGeom() {
        return jts_geom;
    }

    /**
     * @param geom
     *        The geom to set.
     */
    public void setGeom(Geometry geom) {
        this.jts_geom = geom;
    }

    /**
     * @return Returns the iD.
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *        The iD to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the schemaLink.
     */
    public String getSchemaLink() {
        return applicationSchemaType;
    }

    /**
     * @param schemaLink
     *        The schemaLink to set.
     */
    public void setSchemaLink(String schemaLink) {
        this.applicationSchemaType = schemaLink;
    }

    /**
     * @return Returns the epsgCode.
     */
    public int getEpsgCode() {
        return epsgCode;
    }

    /**
     * @param epsgCode
     *        The epsgCode to set.
     */
    public void setEpsgCode(int epsgCode) {
        this.epsgCode = epsgCode;
    }

    /**
     * @return the featureType
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * @param featureType
     *        the featureType to set
     */
    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    /**
     * @return the applicationSchemaType
     */
    public String getApplicationSchemaType() {
        return applicationSchemaType;
    }

    /**
     * @param applicationSchemaType
     *        the applicationSchemaType to set
     */
    public void setApplicationSchemaType(String applicationSchemaType) {
        this.applicationSchemaType = applicationSchemaType;
    }

    /**
     * @return the elementName
     */
    public abstract String getElementName();

    /**
     * @return the jts_geom
     */
    public Geometry getJts_geom() {
        return jts_geom;
    }

    /**
     * @param jts_geom
     *        the jts_geom to set
     */
    public void setJts_geom(Geometry jts_geom) {
        this.jts_geom = jts_geom;
    }

    /**
     * @return the namespace
     */
    public abstract String getNamespace();
}
