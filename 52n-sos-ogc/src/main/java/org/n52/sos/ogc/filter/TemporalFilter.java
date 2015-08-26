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

package org.n52.sos.ogc.filter;

import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.ISosTime;

/**
 * class represents a temporal filter
 * 
 * @author Christoph Stasch
 * 
 */
public class TemporalFilter {

    /** temporal operator (e.g. 'TEquals') */
    private TimeOperator operator;

    /** temporal operand */
    private ISosTime time;

    /**
     * standard constructor
     * 
     * @param operatorp
     *        temporal operator
     * @param timep
     *        temporal operand (e.g. TimePeriod)
     */
    public TemporalFilter(TimeOperator operatorp, ISosTime timep) {
        this.operator = operatorp;
        this.time = timep;
    }

    /**
     * standard constructor
     * 
     * @param operatorp
     *        temporal operator
     * @param timep
     *        temporal operand (e.g. TimePeriod)
     */
    public TemporalFilter(String operatorNamep, ISosTime timep) {
        this.operator = TimeOperator.valueOf(operatorNamep);
        this.time = timep;
    }

    /**
     * @return the operator
     */
    public TimeOperator getOperator() {
        return operator;
    }

    /**
     * @param operator
     *        the operator to set
     */
    public void setOperator(TimeOperator operator) {
        this.operator = operator;
    }

    /**
     * @return the time
     */
    public ISosTime getTime() {
        return time;
    }

    /**
     * @param time
     *        the time to set
     */
    public void setTime(ISosTime time) {
        this.time = time;
    }

    /**
     * 
     * @return Returns String representation with values of this object
     */
    public String toString() {
        return "Temporal filter: " + operator + time.toString();
    }

}
