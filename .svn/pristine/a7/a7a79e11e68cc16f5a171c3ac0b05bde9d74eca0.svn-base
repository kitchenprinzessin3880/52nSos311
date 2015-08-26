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

package org.n52.sos.ds;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;

/**
 * interface for the ConfigDAOs. Offers methods to query offerings, observedProperties for each offering,
 * offering names and foi's of offerings from the DB. The DAO is used to fill the Contents section for
 * Capabilities response in the getCapabilities operation.
 * 
 * @author staschc
 * 
 */
public interface IConfigDAO {

    /**
     * queries the observations ids of this SOS from the DB
     * 
     * @return Returns List with Strings containing the observations ids of this SOS
     * @throws OwsExceptionReport
     *         if query of the observations ids from the DB failed
     */
    public List<String> queryObsIds() throws OwsExceptionReport;

    /**
     * queries the ids of SRS, which are supported by this SOS from DB
     * 
     * @return Returns Collection with Integer containing the ids of SRS, which are supported by this SOS from
     *         DB
     * @throws OwsExceptionReport
     *         if query of the SRS ids from the DB failed
     */
    public Collection<Integer> querySrids() throws OwsExceptionReport;

    /**
     * queries the offerings of this SOS from the DB
     * 
     * @return Returns List with Strings containing the offerings of this SOS
     * @throws OwsExceptionReport
     *         if query of the offerings from the DB failed
     */
    public List<String> queryOfferings() throws OwsExceptionReport;

    /**
     * queries the observedProperties for each offering from the DB and puts the values into a Map
     * 
     * @return Map<String,List<String>> containing the observedProperties as value for each offering (key)
     * @throws OwsExceptionReport
     *         if query of the observedProperties failed
     */
    public Map<String, List<String>> queryPhenomenons4Offerings() throws OwsExceptionReport;

    /**
     * queries the offering names from the DB and puts the values into a Map
     * 
     * @return Returns Map<String,String> containing the offering as key and the offering name as value
     * @throws OwsExceptionReport
     */
    public Map<String, String> queryOfferingNames() throws OwsExceptionReport;

    /**
     * queries all procedures which are used by the offerings offered by this SOS
     * 
     * @return Map<String, SosProcedure> containing all procedures contained in the DB
     * @throws OwsExceptionReport
     *         if query of the procedures failed
     */
    public Map<String, SensorSystem> queryAllProcedures() throws OwsExceptionReport;

    /**
     * queries all feature of interests which are used by the offerings offered by this SOS
     * 
     * @return List<String> containing all feature of interests contained in the DB
     * @throws OwsExceptionReport
     *         if query of the procedures failed
     */
    public List<String> queryAllFois() throws OwsExceptionReport;

    /**
     * queries all domain features which are used by the offerings offered by this SOS
     * 
     * @return List<String> containing all domain features contained in the DB
     * @throws OwsExceptionReport
     *         if query of the domain features failed
     */
    public List<String> queryAllDomainFeatures() throws OwsExceptionReport;

    /**
     * queries the result models for each offerings and puts them into a Map
     * 
     * @return Returns Map<String,> containing the offerings as keys and the corresponding result models as
     *         values
     * @throws OwsExceptionReport
     *         if query of the result models failed
     */
    public Map<String, List<QName>> queryOfferingResultModels() throws OwsExceptionReport;

    /**
     * returns the procedures for each offering stored in a Map
     * 
     * @return Map<String,String[]> containing the procedures for each offering
     * @throws OwsExceptionReport
     *         if query of the procedures failed
     */
    public Map<String, List<String>> queryProcedures4Offerings() throws OwsExceptionReport;

    /**
     * returns the offerings for each phenomenon stored in a Map
     * 
     * @return contains the offerings for each phenomenon
     * @throws OwsExceptionReport
     *         if query of the procedures failed
     */
    public Map<String, List<String>> queryPhenOffs() throws OwsExceptionReport;

    /**
     * queries the procedure for each feature of interest
     * 
     * @return Returns Map<String,String> containing the feature of interest ID as key and the procedureID as
     *         value
     * @throws OwsExceptionReport
     *         if an sql exception occured
     */
    public Map<String, List<String>> queryProcedures4FOIs() throws OwsExceptionReport;

    /**
     * queries the procedure for each domain feature
     * 
     * @return Returns Map<String,String> containing the domain feature ID as key and the procedureID as
     *         value
     * @throws OwsExceptionReport
     *         if an sql exception occured
     */
    public Map<String, List<String>> queryProcedures4DomainFeatures() throws OwsExceptionReport;

    /**
     * queries the fois for each domain feature
     * 
     * @return Returns Map<String,String> containing the domain feature ID as key and the foiID as
     *         value
     * @throws OwsExceptionReport
     *         if an sql exception occured
     */
    public Map<String, List<String>> queryFois4DomainFeatures() throws OwsExceptionReport;

    /**
     * queries the phenomenon components for each composite phenomenon
     * 
     * @return Returns Map<String,List<String>> containing the compositePhenomenonID as key and the
     *         phenomenon components as values
     * @throws OwsExceptionReport
     *         if an sql exception occured
     */
    public Map<String, List<String>> queryPhens4CompPhens() throws OwsExceptionReport;

    /**
     * queries the units for each observedProperty and saves them in a Map
     * 
     * @return Map<String, String> with phenomenonID as key and the unit of the values for this phenomenon as
     *         values
     * @throws OwsExceptionReport
     *         if query of the units failed
     */
    public Map<String, ValueTypes> queryObsPropsValueTypes() throws OwsExceptionReport;

    /**
     * queries the units for each observedProperty and saves them in a Map
     * 
     * @return Map<String, String> with offeringID as key and corresponding composite phenomenon ids as
     *         values
     * @throws OwsExceptionReport
     *         if query of the units failed
     */
    public Map<String, List<String>> queryOffCompPhens() throws OwsExceptionReport;

    /**
     * queries the phenomena for each procedure from the DB and puts the values into a Map
     * 
     * @return Returns Map<String,String[]> containing the phenomena as value for each procedure (key)
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryPhenomenons4Procedures() throws OwsExceptionReport;

    /**
     * queries the procedures for each phenomenon from the DB and puts the values into a Map
     * 
     * @return Returns Map<String,String[]> containing the procedures as value for each phenomenon (key)
     * @throws OwsExceptionReport
     *         if the query failed
     */
    public Map<String, List<String>> queryProcedures4Phenomena() throws OwsExceptionReport;

    /**
     * queries the min and max time for each offering and returns a Map containing the offeringID as key and
     * the min_time max_time values as String[], where String[0] is min_time and String[1] is max_time
     * 
     * @return Returns Map containing the offeringID as key and the min_time max_time values as String[],
     *         where String[0] is min_time and String[1] is max_time
     * @throws OwsExceptionReport
     *         if query of time for offering failed
     */
    public Map<String, String[]> queryTime4Offerings() throws OwsExceptionReport;

    /**
     * queries the unit for each phenomenon from the database
     * 
     * @return Returns Map containing the units(values) for the phenomena (keys)
     * @throws OwsExceptionReport
     *         if query failed
     */
    public Map<String, String> queryUnits4Phens() throws OwsExceptionReport;

    /**
     * queries EPSG code of coordinates contained in DB
     * 
     * @return Returns EPSG code of coordinates contained in DB
     * @throws OwsExceptionReport
     *         if query of EPSG code failed
     */
    public int queryEPSGcode() throws OwsExceptionReport;

    /**
     * returns the offerings for each procedure stored in a Map
     * 
     * @return Map<String,String[]> containing the offerings for each procedure
     * @throws OwsExceptionReport
     *         if query of the offerings procedures failed
     */
    public Map<String, List<String>> queryOfferings4Procedures() throws OwsExceptionReport;

    /**
     * queries the features for each offering from DB
     * 
     * @return Returns
     */
    public Map<String, List<String>> queryOffFeatures() throws OwsExceptionReport;

    /**
     * queries the domain features for each offering from DB
     * 
     * @return Returns
     */
    public Map<String, List<String>> queryOffDomainFeatures() throws OwsExceptionReport;
    
    /**
     * returns the max date of all observation
     * 
     * @return Returns Date containing the max date of the observations in ISO8601 format
     * @throws OwsExceptionReport
     *         if query of the max date failed
     */
    public DateTime getMaxDate4Observations() throws OwsExceptionReport;
    
    /**
     * returns the min date of an observation
     * 
     * @return Returns Date containing the min date of the observation in ISO8601 format
     * @throws OwsExceptionReport
     *         if query of the min date failed
     */
    public DateTime getMinDate4Observations() throws OwsExceptionReport;

}
