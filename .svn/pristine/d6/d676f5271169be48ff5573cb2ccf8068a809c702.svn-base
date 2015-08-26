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

 Author: Carsten Hollmann
 Created: 2009-04-23
 Modified: 2009-04-23
 ***************************************************************/

package org.n52.sos;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

/**
 * Utility class for time.
 * Parser and Formatter for time.
 * 
 * 
 * @author Carsten Hollmann
 * 
 */
public class SosDateTimeUtilities {

    /** the logger, used to log exceptions and additonaly information */
    private static Logger log = Logger.getLogger(SosDateTimeUtilities.class);
    
    private static String responseFormat;
    private static int lease;

    /**
     * parses an iso8601 time String to a DateTime Object.
     * 
     * @param timeString the time String
     * @return Returns a DateTime Object.
     * @throws OwsExceptionReport 
     */
    public static DateTime parseIsoString2DateTime(String timeString) throws OwsExceptionReport {
        if (timeString == null || timeString.equals("")) {
            return null;
        }
        try {
            return ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(timeString);
        }
        catch (IllegalArgumentException iae) {
          OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
          log.error("Error while parse time String to DateTime!", iae);
          se.addCodedException(ExceptionCode.InvalidParameterValue, null, iae);
          throw se;
        } 
        catch (UnsupportedOperationException uoe) {
            OwsExceptionReport owser = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while parse time String to DateTime!", uoe);
            owser.addCodedException(ExceptionCode.InvalidParameterValue, null, uoe);
            throw owser;
          }
    }

    /**
     * formats a DateTime to an iso8601 String.
     * 
     * @param dateTime The DateTime.
     * @return Returns formatted time String.
     */
    public static String formatDateTime2IsoString(DateTime dateTime) {
        if(dateTime == null) {
            dateTime = new DateTime(0000,01,01,00,00,00,000,DateTimeZone.UTC);
            return dateTime.toString().replace("Z", "+00:00");
        }
        return dateTime.toString();
    }

    /**
     * formats a DateTime to a String. If format is set in the properties, time will be in this format. Else the format is iso8601.
     * 
     * @param dateTime The DateTime.
     * @return formatted time String.
     * @throws OwsExceptionReport 
     */
    public static String formatDateTime2ResponseString(DateTime dateTime) throws OwsExceptionReport {
        try {
            if (responseFormat == null || responseFormat.equals("")) {
                return formatDateTime2IsoString(dateTime);
            } else {
                if(dateTime == null) {
                    dateTime = new DateTime(0000,01,01,00,00,00,000,DateTimeZone.UTC);
                }
                return dateTime.toString(DateTimeFormat.forPattern(responseFormat));
            }
        }
        catch (IllegalArgumentException e) {
            OwsExceptionReport owser = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while format DateTime to time String!", e);
            owser.addCodedException(ExceptionCode.InvalidParameterValue, null, e);
            throw owser;
        }
    }
    
    /**
     * parses the duration string from a timePeriod to a Period.
     * 
     * @param stringDuration iso8601 formatted string for duration.
     * @return Returns a Period parsed from the string.
     */
    public static Period parseDuration(String stringDuration) {
        return ISOPeriodFormat.standard().parsePeriod(stringDuration);
    }
    
    /**
     * calculates the expires DateTime of the observationCollection from the start DateTime of the request and the
     * lease property of the config file
     * 
     * @param start
     *        start DateTime for the lease
     * @return Returns the expires DateTime
     */
    public static DateTime calculateExpiresDateTime(DateTime start) {
        DateTime end = null;
        end = start.plusMinutes(lease);
        return end;
    }

    public static void setResponseFormat(String responseFormat) {
        SosDateTimeUtilities.responseFormat = responseFormat;
    }
    
    public static void setLease(int lease) {
        SosDateTimeUtilities.lease = lease;
    }
}
