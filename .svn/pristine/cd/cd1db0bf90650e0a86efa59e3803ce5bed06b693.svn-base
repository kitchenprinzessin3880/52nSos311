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


import org.joda.time.DateTime;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class represents a GML conform timeInstant element; constructor is able to parse either a DOM Node or an
 * XmlBeans generated TimeInstantType element and create therefor then a TimeInstant object. This class is
 * necessary cause XmlBeans supports not fully substitution groups yet. If it will, this class would be
 * unnecessary
 * 
 * ATTENTION: class needs the XmlBeans libs generated from GML schemata!!
 * 
 * @author Christoph Stasch
 * 
 */
public class TimeInstant implements ISosTime {

    /** date for this timeInstant */
    private DateTime value; // timestamp in GML format

    /** indeterminate position of timeInstant */
    private String indeterminateValue;

    /**
     * ingconstructor with date and indeterminateValue strn
     * 
     * @param value
     *        date of the timeInstante
     * @param indeterminateValue
     */
    public TimeInstant(DateTime dateValue, String indeterminateValue) {
        this.value = dateValue;
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * constructor
     * 
     */
    public TimeInstant() {
    }

    /**
     * @return Returns the indeterminateValue.
     */
    public String getIndeterminateValue() {
        return indeterminateValue;
    }

    /**
     * @return Returns the value.
     */
    public DateTime getValue() {
        return value;
    }

    /**
     * @param indeterminateValue
     *        The indeterminateValue to set.
     */
    public void setIndeterminateValue(String indeterminateValue) {
        this.indeterminateValue = indeterminateValue;
    }

    /**
     * @param value
     *        The value to set.
     */
    public void setValue(DateTime value) {
        this.value = value;
    }

    public String toString() {
        if (value != null) {
            String dateString;
            try {
                dateString = SosDateTimeUtilities.formatDateTime2ResponseString(value);  
            }
            catch (OwsExceptionReport e) {
                return e.getMessage();
            }
            return "Time instant: " + dateString + "," + indeterminateValue;
        }
        else
            return "Time instant: " + indeterminateValue;
    }

}
