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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.ds.IConfigDAO;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * CapabilitiesCacheController implements all methods to request all objects and relationships from a standard
 * datasource
 * 
 * @author Alexander Strotmann
 * 
 */
public class CapabilitiesCacheController extends TimerTask {

    /** logger */
    private static Logger log = Logger.getLogger(CapabilitiesCacheController.class);

    /** the dao used for query stored metadata from the DB */
    private IConfigDAO configDao;

    private CapabilitiesCache capabilitiesCache;

    /**
     * Lock management
     */
    protected final ReentrantLock updateLock = new ReentrantLock(true);
    protected final Condition updateFree = updateLock.newCondition();
    protected boolean updateIsFree = true;

    /**
     * constructor
     * 
     */
    public CapabilitiesCacheController() {
        this.configDao = SosConfigurator.getInstance().getFactory().getConfigDAO();
        this.capabilitiesCache = new CapabilitiesCache();
        try {
            update(false);
        }
        catch (OwsExceptionReport e) {
            log.fatal("Fatal error: Couldn't initialize capabilities cache!");
        }
    }

    /**
     * Implements TimerTask's abstract run method.
     */
    public void run() {
        try {
            if (update(true))
                log.info("Timertask: capabilities cache update successful!");
            else
                log.warn("Timertask: capabilities cache update not successful!");
        }
        catch (OwsExceptionReport e) {
            log.fatal("Fatal error: Timertask couldn't update capabilities cache!");
        }
    }

    /**
     * queries the service offerings, the observedProperties for each offering, and the offering names from
     * the DB and sets these values in this configurator
     * 
     * @throws OwsExceptionReport
     *         if the query of one of the values described upside failed
     * 
     */
    public boolean update(boolean checkLastUpdateTime) throws OwsExceptionReport {
        boolean timeNotElapsed = true;
        try {
            // thread safe updating of the cache map
            timeNotElapsed = updateLock.tryLock(SosConstants.UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);

            // has waiting for lock got a time out?
            if ( !timeNotElapsed) {
                log.warn("\n******\nCapabilities caches not updated "
                        + "because of time out while waiting for update lock." + "\nWaited "
                        + SosConstants.UPDATE_TIMEOUT + " milliseconds.\n******\n");
                return false;
            }

            while ( !updateIsFree) {

                updateFree.await();
            }
            updateIsFree = false;

            queryOfferings();
            queryOffPhenomena();
            queryOffNames();
            queryProcedures();
            queryOffResultModels();
            queryOffProcedures();
            queryOffFois();
            queryFois();
            queryFoiProcedures();
            queryObsPropValueTypes();
            queryPhens4CompPhens();
            queryOffCompPhens();
            queryPhenProcs();
            queryProcPhens();
            queryTimes4Offerings();
            queryUnits4Phens();
            queryPhenOffs();
            querySRIDs();
            if (SosConfigurator.getInstance().isMobileEnabled()) {
                queryOffDomainFeatures();
                queryDomainFeatures();
                queryDomainFeatureProcedures();
                queryDomainFeatureFois();
            }

        }
        catch (InterruptedException e) {
            log.error("Problem while threadsafe capabilities cache update", e);
            return false;
        }
        finally {
            if (timeNotElapsed) {
                updateLock.unlock();
                updateIsFree = true;
            }
        }

        return true;

        // queryObservationIds();
    }

    /**
     * method for refreshing the metadata of fois in the capabilities cache; is invoked when a new feature of
     * interest is inserted into the SOS database
     * 
     * @throws OwsExceptionReport
     *         if refreshing failed
     */
    public void updateFois() throws OwsExceptionReport {

        boolean timeNotElapsed = true;
        try {
            // thread safe updating of the cache map
            timeNotElapsed = updateLock.tryLock(SosConstants.UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);

            // has waiting for lock got a time out?
            if ( !timeNotElapsed) {
                log.warn("\n******\nupdateFois() not successful "
                        + "because of time out while waiting for update lock." + "\nWaited "
                        + SosConstants.UPDATE_TIMEOUT + " milliseconds.\n******\n");
                return;
            }
            while ( !updateIsFree) {

                updateFree.await();
            }
            updateIsFree = false;
            queryFois();
            queryFoiProcedures();
            queryOffFois();
            queryDomainFeatures();
            queryDomainFeatureProcedures();
            queryDomainFeatureFois();
            queryOffDomainFeatures();

        }
        catch (InterruptedException e) {
            log.error("Problem while threadsafe capabilities cache update", e);
        }
        finally {
            if (timeNotElapsed) {
                updateLock.unlock();
                updateIsFree = true;
            }
        }
    }

    /**
     * refreshes sensor metadata; used after registration of new sensor at SOS
     * 
     * @throws OwsExceptionReport
     * 
     */
    public void updateSensorMetadata() throws OwsExceptionReport {

        boolean timeNotElapsed = true;
        try {
            // thread safe updating of the cache map
            timeNotElapsed = updateLock.tryLock(SosConstants.UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);

            // has waiting for lock got a time out?
            if ( !timeNotElapsed) {
                log.warn("\n******\nupdateSensorMetadata() not successful "
                        + "because of time out while waiting for update lock." + "\nWaited "
                        + SosConstants.UPDATE_TIMEOUT + " milliseconds.\n******\n");
                return;
            }
            while ( !updateIsFree) {

                updateFree.await();
            }
            updateIsFree = false;
            queryPhenProcs();
            queryProcPhens();
            queryProcedures();
            queryOffProcedures();

        }
        catch (InterruptedException e) {
            log.error("Problem while threadsafe capabilities cache update", e);
        }
        finally {
            if (timeNotElapsed) {
                updateLock.unlock();
                updateIsFree = true;
            }
        }
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
    public void updateMetadata4newObservation(AbstractSosObservation observation,
                                              boolean mobileEnabled) {

        boolean timeNotElapsed = true;
        try {
            // thread safe updating of the cache map
            timeNotElapsed = updateLock.tryLock(SosConstants.UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);

            // has waiting for lock got a time out?
            if ( !timeNotElapsed) {
                log.warn("\n******\nupdateMetadata4newObservation() not successful "
                        + "because of time out while waiting for update lock." + "\nWaited "
                        + SosConstants.UPDATE_TIMEOUT + " milliseconds.\n******\n");
                return;
            }
            while ( !updateIsFree) {

                updateFree.await();
            }
            updateIsFree = false;

            // create local variables for better readable code
            String procID = observation.getProcedureID();
            ArrayList<String> procedures = new ArrayList<String>(1);
            procedures.add(procID);

            String foiID = observation.getFeatureOfInterestID();
            ArrayList<String> fois = new ArrayList<String>(1);
            fois.add(foiID);

            // if foi id is NOT contained add foi
            if ( !this.capabilitiesCache.getFois().contains(foiID)) {
                this.capabilitiesCache.getFois().add(foiID);
            }

            // get offerings for phenomenon of observation
            List<String> offerings = this.getOfferings4Phenomenon(observation.getPhenomenonID());

            // insert foi_off relationsship for each offering
            for (String offering_id : offerings) {

                // check whether offering foi relationship is already contained
                // in
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
            if (this.capabilitiesCache.getFoiProcedures().get(foiID) != null) {
                this.capabilitiesCache.getFoiProcedures().get(foiID).add(procID);
            }
            else {
                this.capabilitiesCache.getFoiProcedures().put(foiID, procedures);
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
                        if ( !this.capabilitiesCache.getDomainFeatures().contains(domainFeatureID)) {
                            this.capabilitiesCache.getDomainFeatures().add(domainFeatureID);
                        }

                        // add relationship between domainFeature and foi
                        if (!this.capabilitiesCache.getDomainFeatureFois().containsKey(domainFeatureID)) {
                            this.capabilitiesCache.getDomainFeatureFois().put(domainFeatureID, fois);
                        }
                        else {
                            this.capabilitiesCache.getDomainFeatureFois().get(domainFeatureID).add(foiID);
                        }

                        // add relationship between domain feature and procedure
                        if (this.capabilitiesCache.getDomainFeatureProcedures().get(domainFeatureID) == null) {
                            this.capabilitiesCache.getDomainFeatureProcedures().put(domainFeatureID,
                                                                                    procedures);
                        }
                        else {
                            this.capabilitiesCache.getDomainFeatureProcedures().get(domainFeatureID).add(procID);
                        }

                        // add relationship between domain feature and offering
                        if (this.capabilitiesCache.getOffDomainFeatures().get(observation.getOfferingID()) == null) {
                            ArrayList<String> domainFeatures = new ArrayList<String>(1);
                            domainFeatures.add(domainFeatureID);
                            this.capabilitiesCache.getOffDomainFeatures().put(observation.getOfferingID(),
                                                                              domainFeatures);
                        }
                        else if ( !this.capabilitiesCache.getOffDomainFeatures().get(observation.getOfferingID()).contains(domainFeatureID)) {
                            this.capabilitiesCache.getOffDomainFeatures().get(observation.getOfferingID()).add(domainFeatureID);
                        }
                    }
                }
            }

        }
        catch (InterruptedException e) {
            log.error("Problem while threadsafe capabilities cache update", e);
        }
        finally {
            if (timeNotElapsed) {
                updateLock.unlock();
                updateIsFree = true;
            }
            ;
        }
    }


    /**
     * queries the observation ids from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of observation ids failed
     */
    public void queryObservationIds() throws OwsExceptionReport {
        this.capabilitiesCache.setObsIds(configDao.queryObsIds());
    }

    /**
     * queries the ids of SRS, which are supported by this SOS
     * 
     * @throws OwsExceptionReport
     *         if query of SRS ids failed
     */
    public void querySRIDs() throws OwsExceptionReport {
        this.capabilitiesCache.setSrids(configDao.querySrids());
    }

    /**
     * queries the observation ids from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of observation ids failed
     */
    public void queryPhenOffs() throws OwsExceptionReport {
        this.capabilitiesCache.setPhenOffs(configDao.queryPhenOffs());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOfferings() throws OwsExceptionReport {
        this.capabilitiesCache.setOfferings(configDao.queryOfferings());
    }

    /**
     * queries the units for phenomena from database
     * 
     * @throws OwsExceptionReport
     *         if query failed
     * 
     */
    public void queryUnits4Phens() throws OwsExceptionReport {
        this.capabilitiesCache.setUnit4Phen(configDao.queryUnits4Phens());
    }

    /**
     * queries the features for each offering from database
     * 
     * @throws OwsExceptionReport
     */
    public void queryOffFois() throws OwsExceptionReport {
        this.capabilitiesCache.setOffFeatures(configDao.queryOffFeatures());
    }

    /**
     * queries the domain features for each offering from database
     * 
     * @throws OwsExceptionReport
     */
    public void queryOffDomainFeatures() throws OwsExceptionReport {
        this.capabilitiesCache.setOffDomainFeatures(configDao.queryOffDomainFeatures());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOffPhenomena() throws OwsExceptionReport {
        this.capabilitiesCache.setOffPhenomenons(configDao.queryPhenomenons4Offerings());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOffNames() throws OwsExceptionReport {
        this.capabilitiesCache.setOffName(configDao.queryOfferingNames());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryProcedures() throws OwsExceptionReport {
        this.capabilitiesCache.setProcedures(configDao.queryAllProcedures());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOffResultModels() throws OwsExceptionReport {
        this.capabilitiesCache.setOffResultModels(configDao.queryOfferingResultModels());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOffProcedures() throws OwsExceptionReport {
        this.capabilitiesCache.setOffProcedures(configDao.queryProcedures4Offerings());
        this.capabilitiesCache.setProcOffs(configDao.queryOfferings4Procedures());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryFois() throws OwsExceptionReport {
        this.capabilitiesCache.setFois(configDao.queryAllFois());
        this.capabilitiesCache.setSrid(configDao.queryEPSGcode());
    }

    /**
     * queries the domain features from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryDomainFeatures() throws OwsExceptionReport {
        this.capabilitiesCache.setDomainFeatures(configDao.queryAllDomainFeatures());
        this.capabilitiesCache.setSrid(configDao.queryEPSGcode());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryFoiProcedures() throws OwsExceptionReport {
        this.capabilitiesCache.setFoiProcedures(configDao.queryProcedures4FOIs());
    }

    /**
     * queries the procedures for domain features from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryDomainFeatureProcedures() throws OwsExceptionReport {
        this.capabilitiesCache.setDomainFeatureProcedures(configDao.queryProcedures4DomainFeatures());
    }

    /**
     * queries the fois for domain features from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryDomainFeatureFois() throws OwsExceptionReport {
        this.capabilitiesCache.setDomainFeatureFois(configDao.queryFois4DomainFeatures());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryObsPropValueTypes() throws OwsExceptionReport {
        this.capabilitiesCache.setObsPropsValueTypes(configDao.queryObsPropsValueTypes());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryPhens4CompPhens() throws OwsExceptionReport {
        this.capabilitiesCache.setPhens4CompPhens(configDao.queryPhens4CompPhens());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryOffCompPhens() throws OwsExceptionReport {
        this.capabilitiesCache.setOffCompPhens(configDao.queryOffCompPhens());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryPhenProcs() throws OwsExceptionReport {
        this.capabilitiesCache.setPhenProcs(configDao.queryPhenomenons4Procedures());
    }

    /**
     * queries the offerings from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of offerings failed
     */
    public void queryProcPhens() throws OwsExceptionReport {
        this.capabilitiesCache.setProcPhens(configDao.queryProcedures4Phenomena());
    }

    /**
     * queries the min and max time for each offering from the DB
     * 
     * @throws OwsExceptionReport
     *         if query of min and max time forofferings failed
     */
    public void queryTimes4Offerings() throws OwsExceptionReport {
        this.capabilitiesCache.setTimes4Offerings(configDao.queryTime4Offerings());
    }

    /**
     * Returns the observedProperties (phenomenons) for the requested offering
     * 
     * @param offering
     *        the offering for which observedProperties should be returned
     * @return Returns String[] containing the phenomenons of the requested offering
     */
    public List<String> getPhenomenons4Offering(String offering) {
        List<String> result = new ArrayList<String>(this.capabilitiesCache.getPhenomenons4Offering(offering));
        if (result == null)
            return new ArrayList<String>();
        return result;
    }

    /**
     * methods returns all phenomena (single or components of composite phenomena) which belong to the
     * requested offering; necessary for database queries
     * 
     * @param offering
     *        the id of the offering for which all phenomena should be returned
     * @return List<String> containing all phenomena which belong to the offering
     */
    public List<String> getAllPhenomenons4Offering(String offering) {
        List<String> result = new ArrayList<String>(this.capabilitiesCache.getAllPhenomenons4Offering(offering));
        if (result == null)
            return new ArrayList<String>();
        return result;
    }

    /**
     * Returns copy of the phenomenons of all offerings
     * 
     * @return List<String> containing the phenomenons of all offerings
     */
    public List<String> getAllPhenomenons() {
        return new ArrayList<String>(this.capabilitiesCache.getAllPhenomenons());
    }

    /**
     * returns the offerings of this SOS
     * 
     * @return List<String> containing the offerings of this SOS
     */
    public List<String> getOfferings() {
        return new ArrayList<String>(this.capabilitiesCache.getOfferings());
    }

    /**
     * returns the observation ids of this SOS
     * 
     * @return List<String> containing the observation ids of this SOS
     */
    public List<String> getObservationIds() {
        return new ArrayList<String>(this.capabilitiesCache.getObsIds());
    }

    /**
     * returns the observedProperties for each offering
     * 
     * @return Map<String, String[]> containing the offerings with its observedProperties
     */
    public Map<String, List<String>> getObsPhenomenons() {
        return new TreeMap<String, List<String>>(this.capabilitiesCache.getOffPhenomenons());
    }

    /**
     * returns the name of the requested offering
     * 
     * @param offering
     *        the offering for which the name should be returned
     * @return String containing the name of the offering
     */
    public String getOfferingName(String offering) {
        return this.capabilitiesCache.getOffName().get(offering);
    }

    /**
     * Returns TreeMap containing all procedures which are used by the Offerings offered in this SOS
     * 
     * @return TreeMap<String, SensorSystem> TreeMap containing all procedures which are used by the Offerings
     *         offered in this SOS
     */
    public Map<String, SensorSystem> getProcedures() {
        return new TreeMap<String, SensorSystem>(this.capabilitiesCache.getProcedures());
    }

    /**
     * return the result models for the requested offering
     * 
     * @param offering
     *        the offering for which the result models should be returned
     * @return String[] containing the result models for the requested offering
     */
    public List<QName> getResultModels4Offering(String offering) {
        if ( !this.capabilitiesCache.getOffResultModels().containsKey(offering))
            return new ArrayList<QName>();
        return this.capabilitiesCache.getOffResultModels().get(offering);
    }

    /**
     * returns the procedures for the requested offering
     * 
     * @param offering
     *        the offering for which the procedures should be returned
     * @return String[] containing the procedures for the requested offering
     */
    public List<String> getProcedures4Offering(String offering) {
        if ( !this.capabilitiesCache.getOffProcedures().containsKey(offering))
            return new ArrayList<String>();
        return new ArrayList<String>(this.capabilitiesCache.getOffProcedures().get(offering));
    }

    /**
     * returns the procedureID for the feature of interest (station)
     * 
     * @param foiID
     *        the foiID for which the procedureID should returned
     * @return String representing the procedureID
     */
    public List<String> getProc4FOI(String foiID) {
        if ( !this.capabilitiesCache.getFoiProcedures().containsKey(foiID))
            return new ArrayList<String>();
        return new ArrayList<String>(this.capabilitiesCache.getFoiProcedures().get(foiID));
    }

    /**
     * returns the procedureIDs for the domain feature
     * 
     * @param dfID
     *        the dfID for which the procedureID should returned
     * @return String representing the procedureID
     */
    public List<String> getProcs4DomainFeature(String dfID) {
        if ( !this.capabilitiesCache.getDomainFeatureProcedures().containsKey(dfID))
            return new ArrayList<String>();
        return this.capabilitiesCache.getDomainFeatureProcedures().get(dfID);
    }

    /**
     * returns the foiIDs for the domain feature
     * 
     * @param dfID
     *        the dfID for which the foiIDs should be returned
     * @return String representing the foiID
     */
    public List<String> getFois4DomainFeature(String dfID) {
        if (!this.capabilitiesCache.getDomainFeatureFois().containsKey(dfID))
            return new ArrayList<String>();
        return this.capabilitiesCache.getDomainFeatureFois().get(dfID);
    }

    /**
     * return the unit of the values for the observedProperty
     * 
     * @param observedProperty
     *        String observedProperty for which the type of the values should be returned
     * @return String representing the valueType of the values for the observedProperty
     */
    public String getUnit4ObsProp(String observedProperty) {
        return this.capabilitiesCache.getUnit4Phen().get(observedProperty);
    }

    /**
     * returns the FOIs
     * 
     * @return ArrayList<String> the FOIs
     */
    public List<String> getFois() {
        return new ArrayList<String>(this.capabilitiesCache.getFois());
    }

    /**
     * returns the phens4CompPhens
     * 
     * @return HashMap<String, List<String>> the phens4CompPhens
     */
    public Map<String, List<String>> getPhens4CompPhens() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getPhens4CompPhens());
    }

    /**
     * Returns the offCompPhens
     * 
     * @return HashMap<String, List<String>> the offCompPhens
     */
    public Map<String, List<String>> getOffCompPhens() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getOffCompPhens());
    }

    /**
     * returns the procedures for phenomena
     * 
     * @return HashMap<String, List<String>> the procedures for phenomena
     */
    public Map<String, List<String>> getPhenProcs() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getPhenProcs());
    }

    /**
     * returns the phenomena for procedures
     * 
     * @return HashMap<String, List<String>> the phenomena for procedures
     */
    public Map<String, List<String>> getProcPhens() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getProcPhens());
    }

    /**
     * returns the value type for the passed phenomenon
     * 
     * @param phenomenonID
     *        id of the phenomenon for which the value type should be returned
     * @return Returns the value type for the passed phenomenon
     */
    public ValueTypes getValueType4Phenomenon(String phenomenonID) {
        return this.capabilitiesCache.getObsPropsValueTypes().get(phenomenonID);
    }

    /**
     * returns the the times for offerings
     * 
     * @return Map<String, String[]> the times for offerings
     */
    public Map<String, String[]> getTimes4Offerings() {
        return new HashMap<String, String[]>(this.capabilitiesCache.getTimes4Offerings());
    }

    /**
     * Returns Srid of coordinates stored in SOS database
     * 
     * @return int Srid of coordinates stored in SOS database
     */
    public int getSrid() {
        return this.capabilitiesCache.getSrid();
    }

    /**
     * 
     * @return Returns Map containing offeringIDs as keys and list of corresponding features as values
     */
    public Map<String, List<String>> getOffFeatures() {
        return this.capabilitiesCache.getOffFeatures();
    }

    /**
     * returns the offerings for the passed procedure id
     * 
     * @param procID
     *        id of procedure, for which related offerings should be returned
     * @return Returns offerings, for which passed procedure produces data
     */
    public List<String> getOfferings4Procedure(String procID) {
        List<String> result = new ArrayList<String>(this.capabilitiesCache.getOfferings4Procedure(procID));
        if (result == null)
            return new ArrayList<String>();
        return result;
    }

    /**
     * returns the offerings for the passed phenomenon
     * 
     * @param phenID
     *        id of procedure, for which related offerings should be returned
     * @return Returns offerings, to which passed phenomenon belongs to
     */
    public List<String> getOfferings4Phenomenon(String phenID) {
        List<String> result = new ArrayList<String>(this.capabilitiesCache.getOfferings4Phenomenon(phenID));
        if (result == null)
            return new ArrayList<String>();
        return result;
    }

    /**
     * Returns srids, which are supported by this SOS
     * 
     * @return Returns srids, which are supported by this SOS
     */
    public Collection<Integer> getSrids() {
        return new ArrayList<Integer>(this.capabilitiesCache.getSrids());
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
    public boolean getMobility4Proc(String procID) throws OwsExceptionReport {
        return this.capabilitiesCache.getMobility4Proc(procID);
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
    public boolean getStatus4Proc(String procID) throws OwsExceptionReport {
        return this.capabilitiesCache.getStatus4Proc(procID);
    }

    /**
     * returns domain features
     * 
     * @return List<String> domain features
     */
    public List<String> getDomainFeatures() {
        return new ArrayList<String>(this.capabilitiesCache.getDomainFeatures());
    }

    /**
     * returns the FOIs for domainFeatures
     * 
     * @return HashMap<String, List<String>> the FOIs for domainFeatures
     */
    public Map<String, List<String>> getDomainFeatureFois() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getDomainFeatureFois());
    }

    /**
     * returns a map containing all offerings and their domain features
     * 
     * @return Map<String, List<String>> map containing all offerings and their domain features
     */
    public Map<String, List<String>> getOffDomainFeatures() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getOffDomainFeatures());
    }

    /**
     * returns the domainFeatureProcedures
     * 
     * @return HashMap<String, List<String>> the domainFeatureProcedures
     */
    public Map<String, List<String>> getDomainFeatureProcedures() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getDomainFeatureProcedures());
    }

    /**
     * returns valuetypes for obsProps
     * 
     * @return HashMap<String, ValueTypes> valuetypes for obsProps
     */
    public Map<String, ValueTypes> getValueTypes4ObsProps() {
        return new HashMap<String, ValueTypes>(this.capabilitiesCache.getValueTypes4ObsProps());
    }

    /**
     * returns procedures for offerings
     * 
     * @return Map<String, List<String>> procedures for offerings
     */
    public Map<String, List<String>> getOffProcedures() {
        return new HashMap<String, List<String>>(this.capabilitiesCache.getOffProcedures());
    }

}
