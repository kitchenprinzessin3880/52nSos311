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

import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.quality.SosQuality;

/**
 * Class represents an abstract observation
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public abstract class AbstractSosObservation {

    /**
     * ID of this observation; in the standard 52n SOS PostgreSQL database, this is implemented through a
     * sequence type.
     */
    private String observationID;

    /** ID of the procedure by which the observation is made */
    private String procedureID;

    /** ID of the phenomenon to which the observation accords to */
    private String phenomenonID;

    /** ID of the feature of interest (the place where the observation is made) */
    private SosAbstractFeature featureOfInterest;

    /** domain features, which this observation is produced for */
    private Collection<SosAbstractFeature> domainFeatureIDs;

    /** ID of the offering to which this observation belongs */
    private String offeringID;

    /** mime type of the value or the result the value points to */
    private String mimeType;

    /** time of the observation */
    private ISosTime samplingTime;

    /** quality of the observation */
    private Collection<SosQuality> quality;

    /**
     * parameterless constructor
     * 
     */
    public AbstractSosObservation() {

    }

    /**
     * constructor
     * 
     * @param obsID
     *        id of this observation
     * @param procID
     *        procedure ID of this observation
     * @param foiID
     *        feature of interest ID of this observation
     * @param phenID
     *        phenomenon ID of this observation
     * @param offeringID
     *        id of the offering the observation belongs to
     * @param mimeType
     *        mimeType of the observation result
     */
    public AbstractSosObservation(ISosTime time,
                                  String obsID,
                                  String procID,
                                  Collection<SosAbstractFeature> domainFeatureIDs,
                                  String phenID,
                                  SosAbstractFeature foi,
                                  String offeringID,
                                  String mimeType,
                                  Collection<SosQuality> quality) {
        setSamplingTime(time);
        setObservationID(obsID);
        setProcedureID(procID);
        setPhenomenonID(phenID);
        setFeatureOfInterest(foi);
        setDomainFeatures(domainFeatureIDs);
        setOfferingID(offeringID);
        setMimeType(mimeType);
        setQuality(quality);
    }

    /**
     * @return Returns the featureOfInterestID.
     */
    public String getFeatureOfInterestID() {
        return featureOfInterest.getId();
    }

    /**
     * @param featureOfInterest
     *        The featureOfInterest to set.
     */
    public void setFeatureOfInterest(SosAbstractFeature featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * @return Returns the featureOfInterest.
     */
    public SosAbstractFeature getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @return Returns the observationID.
     */
    public String getObservationID() {
        return observationID;
    }

    /**
     * @param observationID
     *        The observationID to set.
     */
    public void setObservationID(String observationID) {
        this.observationID = observationID;
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
     * @return Returns the procedureID.
     */
    public String getProcedureID() {
        return procedureID;
    }

    /**
     * @param procedureID
     *        The procedureID to set.
     */
    public void setProcedureID(String procedureID) {
        this.procedureID = procedureID;
    }

    /**
     * @return Returns the timeStamp.
     */
    public ISosTime getSamplingTime() {
        return samplingTime;
    }

    /**
     * @param time
     *        The time to set.
     */
    public void setSamplingTime(ISosTime time) {
        this.samplingTime = time;
    }

    /**
     * @return Returns the mimeType.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType
     *        The mimeType to set.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the offeringID
     */
    public String getOfferingID() {
        return offeringID;
    }

    /**
     * @param offeringID
     *        the offeringID to set
     */
    public void setOfferingID(String offeringID) {
        this.offeringID = offeringID;
    }

    /**
     * 
     * @return Returns namespace of the observation for O&M encoding
     */
    public abstract String getNamespace();

    /**
     * 
     * @return Returns element name of this observation for O&M encoding
     */
    public abstract String getElementName();

    /**
     * @return the quality
     */
    public Collection<SosQuality> getQuality() {
        return quality;
    }

    /**
     * @param quality
     *        the quality to set
     */
    public void setQuality(Collection<SosQuality> quality) {
        this.quality = quality;
    }

    /**
     * adds a single quality to the observation
     * 
     * @param qualitySingle
     */
    public void addSingleQuality(SosQuality qualitySingle) {
        this.quality.add(qualitySingle);
    }

    /**
     * Returns the value of the observation as string
     * 
     * @return Returns the value of the observation as string
     */
    public abstract String getStringValue();

    /**
     * @return the domainFeatures
     */
    public Collection<SosAbstractFeature> getDomainFeatureIDs() {
        return domainFeatureIDs;
    }

    /**
     * @param domainFeatureIDs
     *       the domainFeatures to set
     */
    public void setDomainFeatures(Collection<SosAbstractFeature> domainFeatureIDs) {
        this.domainFeatureIDs = domainFeatureIDs;
    }

    /**
     * adds a domain feature to this observation
     * 
     * @param domainFeatureID
     *          id of domain feature, which should be added
     */
    public void addDomainFeatureID(SosAbstractFeature domainFeatureID) {
        this.domainFeatureIDs.add(domainFeatureID);
    }

}
