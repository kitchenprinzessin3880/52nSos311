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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class which represents a CommonObservation
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class SosGenericObservation extends AbstractSosObservation {

    /** token seperator for the value tuples contained in the result element of the generic observation */
    private String tokenSeperator;

    /** no data value for the values contained in the result element */
    private String noDataValue;

    /** seperator of value tuples, which are contained in the resulte element */
    private String tupleSeperator;

    /** the phenomenon components of the composite phenomenon */
    private ArrayList<String> phenComponents;

    /** contains the ids of the fois, which are values contained for in this observation */
    private ArrayList<String> foiIDs;

    /** contains the fois, for which this Observations contains values */
    private ArrayList<SosAbstractFeature> fois;

    /**
     * represents the values for the result element and uses a String with foiID and appended timeString as
     * key and an SosGenericObsValue as values
     */
    private HashMap<DateTime, HashMap<String, SosGenObsValueTuple>> values = null;

    /**
     * constructor for common observations with user defined composite phenomenon (if no composite phenomena
     * are contained in the request and the resultModel parameter is not changed to 'Measurement' or
     * 'CategoryObservation'
     * 
     * @param phenComps
     *        the phenomena for which the common observation should be added
     * 
     */
    public SosGenericObservation(ArrayList<String> phenComps,
                                 String procedureID,
                                 String offeringID,
                                 String tokenSeperator,
                                 String tupleSeperator,
                                 String noDataValue) {
        super();
        this.fois = new ArrayList<SosAbstractFeature>();
        phenComponents = phenComps;
        setProcedureID(procedureID);
        setOfferingID(offeringID);
        foiIDs = new ArrayList<String>();
        values = new HashMap<DateTime, HashMap<String, SosGenObsValueTuple>>(1000);
        this.tokenSeperator = tokenSeperator;
        this.tupleSeperator = tupleSeperator;
        this.noDataValue = noDataValue;
    }

    /**
     * constructor for common observations with user defined composite phenomenon (if no composite phenomena
     * are contained in the request and the resultModel parameter is not changed to 'Measurement' or
     * 'CategoryObservation'
     * 
     * @param phenComps
     *        the phenomena for which the common observation should be added
     * 
     */
    public SosGenericObservation(ArrayList<String> phenComps,
                                 String offeringID,
                                 String tokenSeperator,
                                 String tupleSeperator,
                                 String noDataValue) {
        super();
        this.fois = new ArrayList<SosAbstractFeature>();
        foiIDs = new ArrayList<String>();
        phenComponents = phenComps;
        setOfferingID(offeringID);
        values = new HashMap<DateTime, HashMap<String, SosGenObsValueTuple>>();
        this.tokenSeperator = tokenSeperator;
        this.tupleSeperator = tupleSeperator;
        this.noDataValue = noDataValue;
    }

    /**
     * 
     * @return Returns the ids of the fois for this generic observation
     */
    public ArrayList<String> getFoiIds() {
        return foiIDs;
    }

    /**
     * adds a value with corresponding phenomenon to the result element of the common observation at a
     * specific time
     * 
     * @param time
     * 
     * @param phenValue
     */
    public void addValue(DateTime timeDate, String foiID, String phenID, String value
            /**change
             * author: juergen sorg
             * date: 2013-01-28
             */
            , SosQuality quality
            /** change end **/) {

        addFoiId(foiID);
        addPhenomenon(phenID,quality);

        // check timePeriod of generic observation
        if (this.getSamplingTime() == null) {
            TimePeriod timePeriod = new TimePeriod(timeDate, timeDate);
            setSamplingTime(timePeriod);
        }
        else {
            if (this.getSamplingTime() instanceof TimePeriod) {
                TimePeriod timePeriod = (TimePeriod) this.getSamplingTime();
                if (timePeriod.getStart().isAfter(timeDate)){
                    timePeriod.setStart(timeDate);
                }
                if (timePeriod.getEnd().isBefore(timeDate)) {
                    timePeriod.setEnd(timeDate);
                }
            }
        }

        // if necessary, initialize values
        if (values == null) {
            values = new HashMap<DateTime, HashMap<String, SosGenObsValueTuple>>(1000);
        }

        // if time already contained in values, add the values to the map containing the values for the
        // different fois of a timestamp
        if (values.containsKey(timeDate)) {

            // check, valueObject is already contained for the foi
            HashMap<String, SosGenObsValueTuple> foiValues = values.get(timeDate);
            SosGenObsValueTuple genObsValue = null;
            if (foiValues.containsKey(foiID)) {
                genObsValue=foiValues.get(foiID);
            }
            // otherwise add new valueObject
            else {
                genObsValue = new SosGenObsValueTuple(timeDate, foiID);
                foiValues.put(foiID, genObsValue);
            }
            genObsValue.addPhenValue(phenID, value);
            if (quality!=null){
            	genObsValue.addPhenValue(new StringBuffer(phenID).append("QualityFlag").toString(), quality.getResultValue());
            }
        }

        // else create new ArrayList and put this together with time as key into the value hash map
        else {
            HashMap<String, SosGenObsValueTuple> foiValues = new HashMap<String, SosGenObsValueTuple>();
            SosGenObsValueTuple genObsValue = new SosGenObsValueTuple(timeDate, foiID);
            genObsValue.addPhenValue(phenID, value);
            //change juergen
            if(quality!=null){
            	genObsValue.addPhenValue(new StringBuffer(phenID).append("QualityFlag").toString(), quality.getResultValue());
            }
            //
            foiValues.put(foiID, genObsValue);
            values.put(timeDate, foiValues);
        }
    }

    /**
     * this method is created for inserting quality information as new phenomenon into generic observations
     * 
     * @param phenID
     *        id of the phenomenon, which should be added
     */
    //change juergen
    public void addPhenomenon(String phenID, SosQuality quality) {
        if ( !isPhenomenonOf(phenID)) {
            if (this.phenComponents == null) {
                this.phenComponents = new ArrayList<String>();
            }
            this.phenComponents.add(phenID);
            if(quality!=null){
            	this.phenComponents.add(new StringBuffer(phenID).append("QualityFlag").toString());
            }
        }
    }

    /**
     * adds the passed feature to the features of this observation
     * 
     * @param foi
     *        feature, which should be added
     */
    public void addFeature(SosAbstractFeature foi) {
        if ( !this.fois.contains(foi)) {
            this.fois.add(foi);
        }
    }

    /**
     * adds the foi id to the foi ids of this generic observation
     * 
     * @param foiId
     *        foi id, which should be added
     */
    public void addFoiId(String foiId) {
        if ( !this.foiIDs.contains(foiId)) {
            this.foiIDs.add(foiId);
        }
    }

    /**
     * method indicates, whether the value of the requested phenomenon is part of the common observation
     * 
     * @param phenomenonID
     *        the id of the phenomenon for which should be tested, whether the value of the requested
     *        phenomenon is part of the common observation
     * @return boolean true, if the values of the phenomenon are part of the common observation, false, if not
     */
    public boolean isPhenomenonOf(String phenomenonID) {
        boolean contained = false;
        if (phenComponents != null) {
            contained = this.phenComponents.contains(phenomenonID);
        }
        return contained;
    }

    /**
     * @return Returns the phenComponents.
     */
    public ArrayList<String> getPhenComponents() {
        return phenComponents;
    }

    /**
     * @param phenComponents
     *        The phenComponents to set.
     */
    public void setPhenComponents(ArrayList<String> phenComponents) {
        this.phenComponents = phenComponents;
    }

    /**
     * @return Returns the values.
     */
    public HashMap<DateTime, HashMap<String, SosGenObsValueTuple>> getValues() {
        return values;
    }

    /**
     * creates a result string representing the value matrix of the common observation or getResult response
     * document
     * 
     * @param phenComponents
     *        the phenomenon components of the values of the value matrix
     * @param values
     *        HashMap containing the time as key, and an ArrayList with pairs of phenomena and values at the
     *        key time as values
     * @return String representing the value matrix of the result element
     * @throws OwsExceptionReport 
     * @throws ServiceException
     */
    @SuppressWarnings( {"unchecked"})
    public String createResultString() throws OwsExceptionReport {

        // save the position for values of each phenomenon in a hash map with
        // the phen id as key
        // and the postion as value
        HashMap<String, Integer> phenIdsAndValueStringPos = new HashMap<String, Integer>();
        int i = 2;
        for (String phenComp : this.phenComponents) {
            phenIdsAndValueStringPos.put(phenComp, Integer.valueOf(i));
            i++;
        }

        // id of the phenomenon for a value which should be inserted into the
        // value matrix
        String phenId = null;

        // single value of one phenomenon
        String value = null;

        // value matrix which should be built
        StringBuffer valueMatrix = new StringBuffer();

        /*
         * now iterate over values of value hashMap; Remember: hash map contains timeString in ISO8601 format
         * as keys and ArrayList<String[]> as values; the String array has length 2 and the first component
         * is the phenomenon of the value, the second the value itself
         */
        Iterator iter = values.entrySet().iterator();

        // /////////////////////////////////////////////////////////////
        // parse time keys and put them into a Date array to sort them
        DateTime[] timeStamps = new DateTime[values.size()];
        int pos = 0;
        DateTime time = null;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            time = (DateTime) entry.getKey();
            DateTime date = time;
            timeStamps[pos] = date;
            pos++;

        }
        Arrays.sort(timeStamps);

        // ////////////////////////////////////////////////////////////////////////
        // now iterate on sorted timeStamps to build the value matrix
        // ATTENTION: Due to reverse sorting of elements, when adding to element list in XmlBeans
        // the elements are ordered reverse to the order in the phenComponents list!!
        String[] valueTuple = null;
        int valuesPerTuple = phenComponents.size() + 2;
        for (int j = 0; j < timeStamps.length; j++) {

            // get corresponding pairs of phenomenon and value for the timeStamp
            HashMap<String, SosGenObsValueTuple> foiValues = values.get(timeStamps[j]);

            String timeString = SosDateTimeUtilities.formatDateTime2ResponseString(timeStamps[j]);

            Set<Entry<String, SosGenObsValueTuple>> entrySet = foiValues.entrySet();
            iter = entrySet.iterator();

            while (iter.hasNext()) {

                Map.Entry entry = (Map.Entry) iter.next();
                SosGenObsValueTuple genObsValue = (SosGenObsValueTuple) entry.getValue();

                // build new tuple
                valueTuple = new String[valuesPerTuple];

                // add time to value tuple
                valueTuple[0] = timeString;

                valueTuple[1] = genObsValue.getFoiID();
                Iterator phenValueIter = genObsValue.getPhenValues().entrySet().iterator();

                while (phenValueIter.hasNext()) {
                    Map.Entry phenValueEntry = (Map.Entry) phenValueIter.next();
                    phenId = (String) phenValueEntry.getKey();
                    value = (String) phenValueEntry.getValue();
                    int arrayPosition = phenIdsAndValueStringPos.get(phenId);
                    valueTuple[arrayPosition] = value;
                }

                // append values to value matrix
                for (int k = 0; k < valuesPerTuple; k++) {
                    value = valueTuple[k];

                    // if value == null, set value on noDataValue, which is stated
                    // in config file
                    if (value == null) {
                        value = noDataValue;
                    }
                    valueMatrix.append(value);
                    valueMatrix.append(tokenSeperator);
                }

                // delete last TokenSeperator
                int tokenSepLength = tokenSeperator.length();
                valueMatrix.delete(valueMatrix.length() - tokenSepLength, valueMatrix.length());
                valueMatrix.append(tupleSeperator);
            }
        }
        return valueMatrix.toString();
    }

    /**
     * @return Returns the namespace of this observation
     */
    public String getNamespace() {
        return OMConstants.NS_OM;
    }

    /**
     * @return Returns the element name of this observation
     */
    public String getElementName() {
        return OMConstants.EN_OBSERVATION;
    }

    /**
     * @return the fois
     */
    public ArrayList<SosAbstractFeature> getFois() {
        return fois;
    }

    /**
     * returns the number of tuples contained in this observation
     * 
     * @return Returns the number of tuples contained in this observation
     */
    public int getTupleCount() {
        return this.values.size();
    }

    @Override
    public String getStringValue() {
        // TODO Auto-generated method stub
        return null;
    }
}
