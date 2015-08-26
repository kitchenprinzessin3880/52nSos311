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

/**
 * class represents an offering in the SOS database
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class SosOffering {

    /** if of this offering */
    private String offeringID;

    /** name of this offering */
    private String offeringName;

    /** min time of observations belonging to this offering in DB DateFormat (YYYY-mm-DD hh:MM:ssZ) */
    private String minTime;

    /** min time of observations belonging to this offering in DB DateFormat (YYYY-mm-DD hh:MM:ssZ) */
    private String maxTime;

    /**
     * constructor
     * 
     * @param offID
     *        offering id
     * @param offName
     *        offering name
     * @param minTimep
     *        min time of observations belonging to this offering in DB DateFormat (YYYY-mm-DD hh:MM:ssZ)
     * @param maxTimep
     *        max time of observations belonging to this offering in DB DateFormat (YYYY-mm-DD hh:MM:ssZ)
     */
    public SosOffering(String offID, String offName, String minTimep, String maxTimep) {
        offeringID = offID;
        offeringName = offName;
        minTime = minTimep;
        maxTime = maxTimep;
    }

    /**
     * @return Returns the offeringID.
     */
    public String getOfferingID() {
        return offeringID;
    }

    /**
     * @param offeringID
     *        The offeringID to set.
     */
    public void setOfferingID(String offeringID) {
        this.offeringID = offeringID;
    }

    /**
     * @return Returns the offeringName.
     */
    public String getOfferingName() {
        return offeringName;
    }

    /**
     * @param offeringName
     *        The offeringName to set.
     */
    public void setOfferingName(String offeringName) {
        this.offeringName = offeringName;
    }

    /**
     * @return the maxTime
     */
    public String getMaxTime() {
        return maxTime;
    }

    /**
     * @param maxTime
     *        the maxTime to set
     */
    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * @return the minTime
     */
    public String getMinTime() {
        return minTime;
    }

    /**
     * @param minTime the minTime to set
     */
    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

}
