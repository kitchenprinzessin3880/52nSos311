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
import java.util.Collection;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.TimePeriod;

import com.vividsolutions.jts.geom.Envelope;

/**
 * class represents an observation collection
 * 
 * @author Christoph Stasch
 * 
 */
public class SosObservationCollection extends AbstractSosObservation {

    /*********************************************************************************************************
     * number of records available in the service that matched the request criteria private int
     * numberOfRecordsMatched;
     * 
     * /** number of records actually returned to client * private int numberOfRecordsReturned;
     * 
     * /** date indicating when the result set will expire; if this date is not specified (null) the result
     * set expires immediately
     */
    private DateTime expiresDate;

    /** list containing the members of this observation collection */
    private Collection<AbstractSosObservation> observationMembers;

    /** two element multipoint in WKT format, which represents the BoundingBox for which values are returned */
    private Envelope boundedBy;
    
    /** the srid id of the BoundingBox **/
    private int srid;

    /**
     * @param id
     *        id of this observation collection
     * @param numberOfRecordsMatched
     *        number of records available in the service that matched the request criteria
     * @param numberOfRecordsReturned
     *        number of records actually returned to client
     * @param expiresDate
     *        date indicating when the result set will expire; if this date is not specified (null) the result
     *        set expires immediately
     * @param nextRecord
     *        start position of next record; value of 0 means all records have been returned
     * @param observationMembers
     *        list containing the members of this observation collection
     * @param time
     *        time period, for which values are returned in this observation collection
     * @param boundedByWKT
     *        two element multipoint in WKT format, which represents the BoundingBox for which values are
     *        returned
     */
    public SosObservationCollection(String id,
                                    int numberOfRecordsMatched,
                                    int numberOfRecordsReturned,
                                    DateTime expiresDate,
                                    int nextRecord,
                                    ArrayList<AbstractSosObservation> observationMembers,
                                    TimePeriod time,
                                    Envelope boundedBy) {
        super(time,
              id,
              OMConstants.PARAMETER_NOT_SET,
              null,
              OMConstants.PARAMETER_NOT_SET,
              null,
              OMConstants.PARAMETER_NOT_SET,
              OMConstants.PARAMETER_NOT_SET,
              null);

        this.expiresDate = expiresDate;
        this.observationMembers = observationMembers;
        // this.time = time;
        this.boundedBy = boundedBy;
    }

    /**
     * parameterless constructor
     * 
     */
    public SosObservationCollection() {
    }

    /**
     * @return the boundedByWKT
     */
    public Envelope getBoundedBy() {
        return boundedBy;
    }

    /**
     * @param boundedByWKT
     *        the boundedByWKT to set
     */
    public void setBoundedBy(Envelope boundedBy) {
        this.boundedBy = boundedBy;
    }
    
    /**
     * @param srid
     * 		  the srid of boundedByWKT to set
     */
    public void setSRID(int srid) {
    	this.srid = srid;
    }
    
    /**
     * @return the srid of boundedByWKT
     */
    public int getSRID() {
    	return srid;
    }

    /**
     * @return the expiresDate
     */
    public DateTime getExpiresDate() {
        return expiresDate;
    }

    /**
     * @param expiresDate
     *        the expiresDate to set
     */
    public void setExpiresDate(DateTime expiresDate) {
        this.expiresDate = expiresDate;
    }

    /**
     * @return the observationMembers
     */
    public Collection<AbstractSosObservation> getObservationMembers() {
        return observationMembers;
    }

    /**
     * @param observationMembers
     *        the observationMembers to set
     */
    public void setObservationMembers(Collection<AbstractSosObservation> observationMembers) {
        this.observationMembers = observationMembers;
    }

    @Override
    public String getElementName() {
        return OMConstants.EN_OBSERVATION_COLLECTION;
    }

    @Override
    public String getNamespace() {
        return OMConstants.NS_OM;
    }

    /**
     * adds passed ObservationCollection to observation collection
     * 
     * @param obsColToAdd
     *        observation collection, which should be added
     */
    public void addColllection(SosObservationCollection obsColToAdd) {

        // initialize, if necessary
        if (this.observationMembers == null) {
            this.observationMembers = new ArrayList<AbstractSosObservation>();
        }

        // initialize, if necessary
        if (this.boundedBy == null) {
            this.boundedBy = new Envelope();
        }
        if (obsColToAdd.getObservationMembers()!=null){
        	this.observationMembers.addAll(obsColToAdd.getObservationMembers());
        	this.boundedBy.expandToInclude(obsColToAdd.getBoundedBy());
        }
    }

    @Override
    public String getStringValue() {
        // TODO Auto-generated method stub
        return null;
    }

}
