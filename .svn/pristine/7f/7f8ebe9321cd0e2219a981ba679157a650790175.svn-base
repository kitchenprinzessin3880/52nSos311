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

package org.n52.sos.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * This singleton class encapsulates HashMaps, which store relationships between the different metadata
 * components of this SOS (e.g. fois 4 offerings). The intention is to achieve better performance in getting
 * this information from this cache than to query always the DB for this information. (Usually the
 * informations stored here do not often change)
 * 
 * @author Christoph Stasch
 * 
 */
public class CapabilitiesCache {

    /** logger */
    private static Logger log = Logger.getLogger(CapabilitiesCache.class);

    /**
     * contains the supported ids of SRS, which are supported by this SOS instance
     */
    private Collection<Integer> srids;

    /** contains the offering IDs offered in the database */
    private List<String> offerings;

    /** contains the procedure IDs offered in the database */
    private Map<String, SensorSystem> procedures;

    /** contains the procedure IDs offered in the database */
    private List<String> fois;

    /** contains the domain feature IDs offered in the database */
    private List<String> domainFeatures;

    /** contains the observation IDs offered in the database */
    private List<String> obsIds;

    /** hash map containing the phenomenons for each offering */
    private Map<String, List<String>> offPhenomenons;

    /** hash map containing the name for each offering */
    private Map<String, String> offName;

    /** hash map containing the name for each offering */
    private Map<String, List<QName>> offResultModels;

    /** hash map containing the procedures for each offering */
    private Map<String, List<String>> offProcedures;

    /** hash map containing the features of interest for each offering */
    private Map<String, List<String>> offFeatures;

    /** hash map containing the domain features for each offering */
    private Map<String, List<String>> offDomainFeatures;

    /** hash map containing the procedures for each feature of interest */
    private Map<String, List<String>> foiProcedures;

    /** hash map containing the procedure for each domain feature */
    private Map<String, List<String>> domainFeatureProcedures;

    /** hash map containing the fois for each domain feature */
    private Map<String, List<String>> domainFeatureFois;

    /** hash map containing the units of the values for each observedProperties */
    private Map<String, ValueTypes> obsPropsValueTypes;

    /**
     * hash map containing the phenomenon components of each compositePhenomenon
     */
    private Map<String, List<String>> phens4CompPhens;

    /**
     * hash map containing the offering IDs as keys and the corresponding composite phenomena ids as values
     */
    private Map<String, List<String>> offCompPhens;

    /** hash map containing the corresponding phenomena for each procedure */
    private Map<String, List<String>> procPhens;

    /** hash map containing the offerings(values) for each procedure (key) */
    private Map<String, List<String>> procOffs;

    /**
     * hash map containing the phenomenon IDs as keys and the corresponding procedure ids as values
     */
    private Map<String, List<String>> phenProcs;

    /** map contains the offerings for each phenomenon */
    private Map<String, List<String>> phenOffs;

    private Map<String, String[]> times4Offerings;

    /** contains the unit (value) for each phenomenon (key) */
    private Map<String, String> unit4Phen;

    /** EPSG code of coordinates contained in the database */
    private int srid;

    /**
     * constructor
     * @throws OwsExceptionReport 
     */
    protected CapabilitiesCache() {
    }

    /**
     * returns
     * 
     * @return
     */
    protected Map<String, ValueTypes> getValueTypes4ObsProps() {
        return this.obsPropsValueTypes;
    }

    /**
     * Returns the observedProperties (phenomenons) for the requested offering
     * 
     * @param offering
     *        the offering for which observedProperties should be returned
     * @return Returns String[] containing the phenomenons of the requested offering
     */
    protected List<String> getPhenomenons4Offering(String offering) {
        return this.offPhenomenons.get(offering);
    }

    /**
     * methods returns all phenomena (single or components of composite phenomena) which belong to the
     * requested offering; necessary for database queries
     * 
     * @param offering
     *        the id of the offering for which all phenomena should be returned
     * @return List<String> containing all phenomena which belong to the offering
     */
    protected List<String> getAllPhenomenons4Offering(String offering) {
        List<String> result = new ArrayList<String>();

        // single phenomena
        if (this.offPhenomenons.containsKey(offering)) {

            result.addAll(this.offPhenomenons.get(offering));

            // components of composite phenomena
            if (this.offCompPhens.containsKey(offering)) {
                List<String> compPhens = this.offCompPhens.get(offering);
                for (String cp : compPhens) {
                    if (this.phens4CompPhens.containsKey(cp))
                        result.addAll(this.phens4CompPhens.get(cp));
                }

            }
        }

        // only components of composite phenomena
        else {
            if (this.offCompPhens.containsKey(offering)) {
                List<String> compPhens = this.offCompPhens.get(offering);
                for (String cp : compPhens) {
                    if (this.phens4CompPhens.containsKey(cp))
                        result.addAll(this.phens4CompPhens.get(cp));
                }

            }
        }
        return result;
    }

    /**
     * Returns the phenomenons of all offerings
     * 
     * @return List<String> containing the phenomenons of all offerings
     */
    protected List<String> getAllPhenomenons() {
        List<String> phenomenons = new ArrayList<String>();
        for (String s : this.offerings) {

            // get single phenomena
            if (this.offPhenomenons.containsKey(s)) {
                List<String> phen = this.offPhenomenons.get(s);
                for (String p : phen) {
                    if ( !phenomenons.contains(p)) {
                        phenomenons.add(p);
                    }
                }
            }

            // get composite phenomena
            if (this.offCompPhens.containsKey(s)) {
                List<String> phen = this.offCompPhens.get(s);
                for (String p : phen) {

                    // add id of the composite phenomenon to the result
                    if ( !phenomenons.contains(p)) {
                        phenomenons.add(p);
                    }

                    // add components of composite phenomenon to the result
                    if (phens4CompPhens.containsKey(p)) {

                        List<String> components = phens4CompPhens.get(p);
                        for (String phenComp : components) {
                            if ( !phenomenons.contains(phenComp)) {
                                phenomenons.add(phenComp);
                            }
                        }

                    }
                }
            }
        }
        return phenomenons;
    }

    /**
     * returns the offerings of this SOS
     * 
     * @return List<String> containing the offerings of this SOS
     */
    protected List<String> getOfferings() {
        return this.offerings;
    }

    /**
     * returns the observation ids of this SOS
     * 
     * @return List<String> containing the observation ids of this SOS
     */
    protected List<String> getObservationIds() {
        return this.obsIds;
    }
    
    /**
     * returns the observation ids of this SOS
     * 
     * @return List<String> containing the observation ids of this SOS
     */
    protected List<String> getObsIds() {
        return obsIds;
    }
    
    /**
     * returns relationships between offerings and phenomena
     * 
     * @return
     */
    protected Map<String, List<String>> getOffPhenomenons() {
        return offPhenomenons;
    }
    
    /**
     * returns relationships between names and offerings
     * 
     * @return
     */
    protected Map<String, String> getOffName() {
        return offName;
    }
    
    /**
     * returns relationships between offerings and result models
     * 
     * @return
     */
    protected Map<String, List<QName>> getOffResultModels() {
        return offResultModels;
    }
    
    /**
     * returns relationships between offerings and procedures
     * 
     * @return
     */
    protected Map<String, List<String>> getOffProcedures() {
        return offProcedures;
    }
    
    /**
     * returns the units for phenomena
     * @return the units related to phenomenon
     */
    protected Map<String, String> getUnit4Phen() {
        return unit4Phen;
    }
    
    /**
     * returns the value types for phenomena
     * @return value types related to phenomena
     */
    protected Map<String, ValueTypes> getObsPropsValueTypes() {
        return obsPropsValueTypes;
    }

    /**
     * sets the offerings of this SOS
     * 
     * @param offerings
     *        List<String> containing the offerings
     */
    protected void setOfferings(List<String> offerings) {
        this.offerings = offerings;
    }

    /**
     * returns the observedProperties for each offering
     * 
     * @return Map<String, String[]> containing the offerings with its observedProperties
     */
    protected Map<String, List<String>> getObsPhenomenons() {
        return offPhenomenons;
    }

    /**
     * returns the name of the requested offering
     * 
     * @param offering
     *        the offering for which the name should be returned
     * @return String containing the name of the offering
     */
    protected String getOfferingName(String offering) {
        return this.offName.get(offering);
    }

    /**
     * 
     * 
     * @return Returns ListString containing all procedures which are used by the Offerings offered in this
     *         SOS
     */
    protected Map<String, SensorSystem> getProcedures() {
        return procedures;
    }

    /**
     * return the result models for the requested offering
     * 
     * @param offering
     *        the offering for which the result models should be returned
     * @return String[] containing the result models for the requested offering
     */
    protected List<QName> getResultModels4Offering(String offering) {
        return this.offResultModels.get(offering);
    }

    /**
     * returns the procedures for the requested offering
     * 
     * @param offering
     *        the offering for which the procedures should be returned
     * @return String[] containing the procedures for the requested offering
     */
    protected List<String> getProcedures4Offering(String offering) {
        return this.offProcedures.get(offering);
    }

    /**
     * returns the procedureID for the feature of interest (station)
     * 
     * @param foiID
     *        the foiID for which the procedureID should returned
     * @return String representing the procedureID
     */
    protected List<String> getProc4FOI(String foiID) {
        return this.foiProcedures.get(foiID);
    }

    /**
     * returns the procedureIDs for the domain feature
     * 
     * @param dfID
     *        the dfID for which the procedureID should returned
     * @return String representing the procedureID
     */
    protected List<String> getProcs4DomainFeature(String dfID) {
        return this.domainFeatureProcedures.get(dfID);
    }

    /**
     * returns the foiIDs for the domain feature
     * 
     * @param dfID
     *        the dfID for which the foiIDs should be returned
     * @return String representing the foiID
     */
    protected List<String> getFois4DomainFeature(String dfID) {
        return this.domainFeatureFois.get(dfID);
    }

    /**
     * returns the foiProcedures
     * 
     * @return Map<String, List<String>> foiProcedures
     */
    protected Map<String, List<String>> getFoiProcedures() {
        return foiProcedures;
    }

    /**
     * return the unit of the values for the observedProperty
     * 
     * @param observedProperty
     *        String observedProperty for which the type of the values should be returned
     * @return String representing the valueType of the values for the observedProperty
     */
    protected String getUnit4ObsProp(String observedProperty) {
        return this.unit4Phen.get(observedProperty);
    }

    /**
     * @return Returns the fois.
     */
    protected List<String> getFois() {
        return fois;
    }

    /**
     * @return Returns the phens4CompPhens.
     */
    protected Map<String, List<String>> getPhens4CompPhens() {
        return phens4CompPhens;
    }

    /**
     * @return Returns the offCompPhens.
     */
    protected Map<String, List<String>> getOffCompPhens() {
        return offCompPhens;
    }

    /**
     * @return Returns the phenProcs.
     */
    protected Map<String, List<String>> getPhenProcs() {
        return phenProcs;
    }

    /**
     * sets the observation ids
     * @param obsIds
     */
    protected void setObsIds(List<String> obsIds) {
        this.obsIds = obsIds;
    }
    
    /**
     * sets the SRIDs
     * @param srids
     */
    protected void setSrids(Collection<Integer> srids) {
        this.srids = srids;
    }
    
    /**
     * sets relationships between offerings and FOIs
     * 
     * @param offFeatures
     */
    protected void setOffFeatures(Map<String, List<String>> offFeatures) {
        this.offFeatures = offFeatures;
    }
    
    /**
     * sets the offering domain feature relations
     * @param offDomainFeatures
     */
    protected void setOffDomainFeatures(Map<String, List<String>> offDomainFeatures) {
        this.offDomainFeatures = offDomainFeatures;
    }
    
    /**
     * sets relationships between offerings and phenomena
     * 
     * @param offPhenomenons
     */
    protected void setOffPhenomenons(Map<String, List<String>> offPhenomenons) {
        this.offPhenomenons = offPhenomenons;
    }
    
    /**
     * sets relationships between names and offerings
     * 
     * @param offName
     */
    protected void setOffName(Map<String, String> offName) {
        this.offName = offName;
    }
    
    /**
     * set procedures with SensorML
     * @param procedures
     */
    protected void setProcedures(Map<String, SensorSystem> procedures) {
        this.procedures = procedures;
    }

    /**
     * sets relationships between offerings and result models
     * 
     * @param offResultModels
     */
    protected void setOffResultModels(Map<String, List<QName>> offResultModels) {
        this.offResultModels = offResultModels;
    }
    
    /**
     * sets relationships between offerings and procedures
     * 
     * @param offProcedures
     */
    protected void setOffProcedures(Map<String, List<String>> offProcedures) {
        this.offProcedures = offProcedures;
    }
    
    /**
     * sets relationships between procedures and offerings
     * 
     * @param procOffs
     */
    protected void setProcOffs(Map<String, List<String>> procOffs) {
        this.procOffs = procOffs;
    }
    
    /**
     * sets FOIs
     * 
     * @param fois
     */
    protected void setFois(List<String> fois) {
        this.fois = fois;
    }
    
    /**
     * sets the SRID
     * @param srid
     */
    protected void setSrid(int srid) {
        this.srid = srid;
    }
    
    /**
     * sets the feature of interest procedure relations
     * @param foiProcedures
     */
    protected void setFoiProcedures(Map<String, List<String>> foiProcedures) {
        this.foiProcedures = foiProcedures;
    }
    
    /**
     * sets the feature of interest domain feature relations
     * @param domainFeatureFois
     */
    protected void setDomainFeatureFois(Map<String, List<String>> domainFeatureFois) {
        this.domainFeatureFois = domainFeatureFois;
    }
    
    /**
     * sets relationships between phenomena and composite phenomena
     * 
     * @param phens4CompPhens
     */
    protected void setPhens4CompPhens(Map<String, List<String>> phens4CompPhens) {
        this.phens4CompPhens = phens4CompPhens;
    }
    
    /**
     * sets relationships between offerings and composite phenomena
     * 
     * @param offCompPhens
     */
    protected void setOffCompPhens(Map<String, List<String>> offCompPhens) {
        this.offCompPhens = offCompPhens;
    }
    
    /**
     * sets the unit phenomenon relations
     * @param unit4Phen
     */
    protected void setUnit4Phen(Map<String, String> unit4Phen) {
        this.unit4Phen = unit4Phen;
    }
    
    /**
     * sets relationships between phenomena and offerings
     * 
     * @param phenOffs
     */
    protected void setPhenOffs(Map<String, List<String>> phenOffs) {
        this.phenOffs = phenOffs;
    }

    /**
     * sets phenomenon procedure relations
     * @param phenProcs
     *        The phenProcs to set.
     */
    protected void setPhenProcs(Map<String, List<String>> phenProcs) {
        this.phenProcs = phenProcs;
    }

    /**
     * returns the procedure phenomenon relations
     * @return Returns the procPhens.
     */
    protected Map<String, List<String>> getProcPhens() {
        return procPhens;
    }

    /**
     * sets the procedure phenomenon relations
     * @param procPhens
     *        The procPhens to set.
     */
    protected void setProcPhens(Map<String, List<String>> procPhens) {
        this.procPhens = procPhens;
    }

    /**
     * returns the value type for the passed phenomenon
     * 
     * @param phenomenonID
     *        id of the phenomenon for which the value type should be returned
     * @return Returns the value type for the passed phenomenon
     */
    protected ValueTypes getValueType4Phenomenon(String phenomenonID) {
        return obsPropsValueTypes.get(phenomenonID);
    }

    /**
     * sets the phenomenon value type relations
     * @param obsPropsValueTypes
     */
    protected void setObsPropsValueTypes(Map<String, ValueTypes> obsPropsValueTypes) {
        this.obsPropsValueTypes = obsPropsValueTypes;
    }

    /**
     * returns the time offering relations
     * @return the times4Offerings
     */
    protected Map<String, String[]> getTimes4Offerings() {
        return times4Offerings;
    }

    /**
     * sets the time offering relations
     * @param times4Offerings
     *        the times4Offerings to set
     */
    protected void setTimes4Offerings(Map<String, String[]> times4Offerings) {
        this.times4Offerings = times4Offerings;
    }

    /**
     * returns the SRID
     * @return Returns Srid of coordinates stored in SOS database
     */
    protected int getSrid() {
        return srid;
    }

    /**
     * 
     * @return Returns Map containing offeringIDs as keys and list of corresponding features as values
     */
    protected Map<String, List<String>> getOffFeatures() {
        return offFeatures;
    }

    /**
     * returns the offerings for the passed procedure id
     * 
     * @param procID
     *        id of procedure, for which related offerings should be returned
     * @return Returns offerings, for which passed procedure produces data
     */
    protected List<String> getOfferings4Procedure(String procID) {
        List<String> result = new ArrayList<String>();
        if (this.procOffs.containsKey(procID)) {
            result.addAll(this.procOffs.get(procID));
        }
        return result;
    }

    /**
     * returns the offerings for the passed phenomenon
     * 
     * @param phenID
     *        id of procedure, for which related offerings should be returned
     * @return Returns offerings, to which passed phenomenon belongs to
     */
    protected List<String> getOfferings4Phenomenon(String phenID) {
        List<String> result = new ArrayList<String>();
        if (this.phenOffs.containsKey(phenID)) {
            result.addAll(this.phenOffs.get(phenID));
        }
        return result;
    }

    /**
     * Returns srids, which are supported by this SOS
     * 
     * @return Returns srids, which are supported by this SOS
     */
    protected Collection<Integer> getSrids() {
        return this.srids;
    }

    /**
     * returns mobility information about sensor with passed id
     * 
     * @param procID
     *        sensor, for which mobility information should be returned
     * @return Returns true, if sensor is mobile, otherwise false.
     * @throws OwsExceptionReport
     *         if requested procedure is not registered at SOSmobile
     */
    protected boolean getMobility4Proc(String procID) throws OwsExceptionReport {
        if (this.procedures.containsKey(procID)) {
            return this.procedures.get(procID).isMobile();
        }
        OwsExceptionReport se = new OwsExceptionReport();
        se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                             null,
                             "Error while getting mobility info for procedure. Procedure is not registered at SOS!");
        log.error(se);
        throw se;
    }

    /**
     * returns sensor status information about sensor with passed id
     * 
     * @param procID
     *        sensor, for which sensor status information should be returned
     * @return Returns true, if sensor is active, otherwise false.
     * @throws OwsExceptionReport
     *         if requested procedure is not registered at SOSmobile
     */
    protected boolean getStatus4Proc(String procID) throws OwsExceptionReport {
        if (this.procedures.containsKey(procID)) {
            return this.procedures.get(procID).isActive();
        }
        OwsExceptionReport se = new OwsExceptionReport();
        se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                             null,
                             "Error while getting status info for procedure. Procedure is not registered at SOS!");
        log.error(se);
        throw se;
    }

    /**
     * returns domain features
     * 
     * @return List<String> domain features
     */
    protected List<String> getDomainFeatures() {
        return domainFeatures;
    }

    /**
     * @return the domainFeatureFois
     */
    protected Map<String, List<String>> getDomainFeatureFois() {
        return domainFeatureFois;
    }

    /**
     * sets domain features
     * 
     * @param domainFeatures
     */
    protected void setDomainFeatures(List<String> domainFeatures) {
        this.domainFeatures = domainFeatures;
    }

    /**
     * returns a map containing all offerings and their domain features
     * 
     * @return Map<String, List<String>>
     */
    protected Map<String, List<String>> getOffDomainFeatures() {
        return offDomainFeatures;
    }

    /**
     * @return the domainFeatureProcedures
     */
    protected Map<String, List<String>> getDomainFeatureProcedures() {
        return domainFeatureProcedures;
    }

    /**
     * @param domainFeatureProcedures
     *        the domainFeatureProcedures to set
     */
    protected void setDomainFeatureProcedures(Map<String, List<String>> domainFeatureProcedures) {
        this.domainFeatureProcedures = domainFeatureProcedures;
    }

    /**
     * methods for adding relationships in Cache for recently received new observation
     * 
     * @param observation
     *        recently received observation which has been inserted into SOS db and whose relationships have
     *        to be maintained in cache
     * @param mobileEnabled
     *        indicates whether request containing the passed observation has been mobile enabled
     */
    protected void refreshMetadata4newObservation(AbstractSosObservation observation,
                                               boolean mobileEnabled) {

        // create local variables for better readable code
        String procID = observation.getProcedureID();
        ArrayList<String> procedures = new ArrayList<String>(1);
        procedures.add(procID);

        String foiID = observation.getFeatureOfInterestID();
        ArrayList<String> fois = new ArrayList<String>(1);
        fois.add(foiID);

        // if foi id is NOT contained add foi
        if ( !this.getFois().contains(foiID)) {
            this.fois.add(foiID);
        }

        // get offerings for phenomenon of observation
        List<String> offerings = this.getOfferings4Phenomenon(observation.getPhenomenonID());

        // insert foi_off relationsship for each offering
        for (String offering_id : offerings) {

            // check whether offering foi relationship is already contained in
            // DB
            if ( !this.getOffFeatures().containsKey(offering_id)) {

                // Case 1: offering is NOT contained in foi_off -> insert
                // relationsship
                this.getOffFeatures().put(offering_id, fois);
            }
            else if ( !this.getOffFeatures().get(offering_id).contains(foiID)) {

                // Case 2: offering is already stored in foi_off -> insert
                // relationsship if
                // offering NOT contains foi id
                this.getOffFeatures().get(offering_id).add(foiID);
            }

        }

        // insert proc_foi relationsship
        if (this.foiProcedures.get(foiID) != null) {
            this.foiProcedures.get(foiID).add(procID);
        }
        else {
            this.foiProcedures.put(foiID, procedures);
        }

        // if mobileEnabled = true
        if (mobileEnabled) {

            // if domain feature in request != null insert relationship and
            // domain feature, if necessary
            if (observation.getDomainFeatureIDs() != null
                    && observation.getDomainFeatureIDs().size() > 0) {

                // iterate over all domain features
                for (SosAbstractFeature sos_af : observation.getDomainFeatureIDs()) {

                    // add domainFeature
                    String domainFeatureID = sos_af.getId();
                    if ( !this.domainFeatures.contains(domainFeatureID)) {
                        this.domainFeatures.add(domainFeatureID);
                    }

                    // add relationship between domainFeature and foi
                    if (this.domainFeatureFois.containsKey(domainFeatureID)) {
                        this.domainFeatureFois.put(domainFeatureID, fois);
                    }
                    else {
                        this.domainFeatureFois.get(domainFeatureID).add(foiID);
                    }

                    // add relationship between domain feature and procedure
                    if (this.domainFeatureProcedures.get(domainFeatureID) == null) {
                        this.domainFeatureProcedures.put(domainFeatureID, procedures);
                    }
                    else {
                        this.domainFeatureProcedures.get(domainFeatureID).add(procID);
                    }

                    // add relationship between domain feature and offering
                    if (this.offDomainFeatures.get(observation.getOfferingID()) == null) {
                        ArrayList<String> domainFeatures = new ArrayList<String>(1);
                        domainFeatures.add(domainFeatureID);
                        this.offDomainFeatures.put(observation.getOfferingID(), domainFeatures);
                    }
                    else if ( !this.offDomainFeatures.get(observation.getOfferingID()).contains(domainFeatureID)) {
                        this.offDomainFeatures.get(observation.getOfferingID()).add(domainFeatureID);
                    }
                }
            }
        }
    }
}
