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

package org.n52.sos.ogc.gml.time;

import java.text.ParseException;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class represents a GML conform timePeriod element; constructor is able to parse either a DOM Node or a
 * XmlBeans generated TimePeriodType element and create therefor a TimeInstant object. This class is necessary
 * cause XmlBeans supports not fully substitution groups yet. If it will, this class would be unnecessary.
 * 
 * ATTENTION: class needs the XmlBeans libs generated from GML schemata!!
 * 
 * @author Christoph Stasch
 * 
 */
public class TimePeriod implements ISosTime {

    /** start Date of timePeriod */
    private DateTime start;

    /** indeterminate position of startPosition */
    private String startIndet;

    /** end Date of timePeriod */
    private DateTime end;

    /** indeterminate position of endPosition */
    private String endIndet;

    /** duration value */
    private Period duration = null; // ISO8601 format

    /** intervall value */
    private String intervall = null; // ISO8601 format

    /**
     * standard constructor
     * 
     * @param start
     *        timeString of startposition in ISO8601 format
     * @param startIndet
     *        indeterminate time position of start
     * @param end
     *        timeString of endposition in ISO8601 format
     * @param endIndet
     *        indeterminate time value of end position
     * @param duration
     *        duration in ISO8601 format
     * @throws ParseException
     *         if parsing the time strings of start or end into java.util.Date failed
     * @throws OwsExceptionReport 
     */
    public TimePeriod(DateTime start, String startIndet, DateTime end, String endIndet, String duration) throws ParseException, OwsExceptionReport {
        this.start = start;
        this.startIndet = startIndet;
        this.end = end;
        this.endIndet = endIndet;
        this.duration = SosDateTimeUtilities.parseDuration(duration);
    }
    
    /**
     * constructor with start and end date as parameters
     * 
     * @param start
     *        start date of the time period
     * @param end
     *        end date of the timeperiod
     */
    public TimePeriod(DateTime start, DateTime end) {
        this.start = start;
        this.end = end;
    }

    /**
     * empty constructor
     * 
     */
    public TimePeriod() {
    }

    /**
     * @return Returns the duration.
     */
    public Period getDuration() {
        return duration;
    }

    /**
     * @return Returns the end.
     */
    public DateTime getEnd() {
        return end;
    }

    /**
     * @return Returns the endIndet.
     */
    public String getEndIndet() {
        return endIndet;
    }

    /**
     * @return Returns the start.
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * @return Returns the startIndet.
     */
    public String getStartIndet() {
        return startIndet;
    }

    /**
     * @param duration
     *        The duration to set.
     */
    public void setDuration(Period duration) {
        this.duration = duration;
    }

    /**
     * @param end
     *        The end to set.
     */
    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * @param endIndet
     *        The endIndet to set.
     */
    public void setEndIndet(String endIndet) {
        this.endIndet = endIndet;
    }

    /**
     * @param start
     *        The start to set.
     */
    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * @param startIndet
     *        The startIndet to set.
     */
    public void setStartIndet(String startIndet) {
        this.startIndet = startIndet;
    }

    public void setIntervall(String intervall) {
        this.intervall = intervall;
    }

    public String getIntervall() {
        return this.intervall;
    }
    

    public String toString() {
        String result = "Time period: ";
        try {
            if (start != null) {
                result += SosDateTimeUtilities.formatDateTime2ResponseString(start) + ", ";
            }
            result += startIndet + ", ";
            if (end != null) {
                result += SosDateTimeUtilities.formatDateTime2ResponseString(end) + ", ";
            }
        }
        catch (OwsExceptionReport e) {
            return e.getMessage();
        }
        result += endIndet;
        return result;
    }

}
