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

package org.n52.sos.ogc.om;

import java.util.Collection;

/**
 * class represents a phenomenon of an observation
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class SosPhenomenon {

    /** id */
    private String phenomenonID;

    /** description of the phenomenon */
    private String phenomenonDescription;

    /** unit of the values of the phenomenons observations */
    private String unit;

    /** O&M application schema link of the phenomenon */
    private String applicationSchemaLink;

    /** valueType in the database of the phenomenons observation values */
    private String valueType;

    /** id of the composite phenomenon, this phenomenon is a component of */
    private String compPhenId;

    /** Offerings the Procedure should be added to */
    private Collection<SosOffering> offerings;

    /**
     * 
     * @param phenomenonID
     *        id of the phenomenon
     * @param phenomenonDescription
     *        description of the phenomenon
     * @param unit
     *        unit of the observation values according to this phenomenon
     * @param applicationSchemaLink
     *        OM application schema link of this phenomenon
     * @param valueType
     *        database valType of the observation values according to this phenomenon
     * @param compPhenId
     *        id of the composite phenomenon, this phenomenon is a component of
     * @param offerings
     *        Collection of Offerings the Phenomenon should be added to
     */
    public SosPhenomenon(String phenomenonID,
                         String phenomenonDescription,
                         String unit,
                         String applicationSchemaLink,
                         String valueType,
                         String compPhenId,
                         Collection<SosOffering> offerings) {
        super();
        this.phenomenonID = phenomenonID;
        this.phenomenonDescription = phenomenonDescription;
        this.unit = unit;
        this.applicationSchemaLink = applicationSchemaLink;
        this.valueType = valueType;
        this.compPhenId = compPhenId;
        this.offerings = offerings;
    }

    /**
     * constructor
     * 
     * @param phenID
     *        id of the phenomenon
     * @param phenDesc
     *        description of the phenomenon
     * @param unitp
     *        unit of the observation values according to this phenomenon
     * @param appSchemaLink
     *        OM application schema link of this phenomenon
     * @param valType
     *        database valType of the observation values according to this phenomenon
     * @param compPhenId
     *        id of the composite phenomenon, this phenomenon is a component of
     */
    public SosPhenomenon(String phenID,
                         String phenDesc,
                         String unitp,
                         String appSchemaLink,
                         String valType,
                         String compPhenId) {
        setPhenomenonID(phenID);
        setPhenomenonDescription(phenDesc);
        setUnit(unitp);
        setApplicationSchemaLink(appSchemaLink);
        setValueType(valType);
        setCompPhenId(compPhenId);
    }

    /**
     * @return Returns the applicationSchemaLink.
     */
    public String getApplicationSchemaLink() {
        return applicationSchemaLink;
    }

    /**
     * @param applicationSchemaLink
     *        The applicationSchemaLink to set.
     */
    public void setApplicationSchemaLink(String applicationSchemaLink) {
        this.applicationSchemaLink = applicationSchemaLink;
    }

    /**
     * @return Returns the phenomenonDescription.
     */
    public String getPhenomenonDescription() {
        return phenomenonDescription;
    }

    /**
     * @param phenomenonDescription
     *        The phenomenonDescription to set.
     */
    public void setPhenomenonDescription(String phenomenonDescription) {
        this.phenomenonDescription = phenomenonDescription;
    }

    /**
     * @return Returns the phenomenonID.
     */
    public String getPhenomenonID() {
        return phenomenonID;
    }

    /**
     * @param phenomenonID
     *        The phenomenonID to set.
     */
    public void setPhenomenonID(String phenomenonID) {
        this.phenomenonID = phenomenonID;
    }

    /**
     * @return Returns the unit.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit
     *        The unit to set.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @return Returns the valueType.
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * @param valueType
     *        The valueType to set.
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return Returns the compPhenId.
     */
    public String getCompPhenId() {
        return compPhenId;
    }

    /**
     * @param compPhenId
     *        The compPhenId to set.
     */
    public void setCompPhenId(String compPhenId) {
        this.compPhenId = compPhenId;
    }

    public Collection<SosOffering> getOfferings() {
        return offerings;
    }

    public void setOfferings(Collection<SosOffering> offerings) {
        this.offerings = offerings;
    }

}
