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

import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;

/**
 * class represents an GML comparisonOps Filter element. The standard value is value. If the operator is
 * 'PropertyIsBetween' there are two values in the result element: lowerValue and upperValue of the value
 * intervall. The lowerValue is the standard value, the upperValue then is valueUpper. If any other operator
 * is used in the filter element, the value of valueUpper should be 'null'! Value elements should be
 * ogc:Measure Elements.
 * 
 * ATTENTION: class needs the XmlBeans libs!!
 * 
 * @author Christoph Stasch
 * 
 */
public class ComparisonFilter {

    /** property name */
    private String propertyName;

    /** name of the operator */
    private ComparisonOperator operator;

    /**
     * value of the filter expression, if operator is "PropertyIsBetween" this is the lowerValue
     */
    private String value;

    /**
     * upperValue if operator is "PropertyIsBetween"; if any other operator is used, the value of this
     * attribute should be 'null'!
     */
    private String valueUpper;
    
    /**
     * User defined escape String
     */
    private String escapeString;
    
    /**
     * User defined wild card
     */
    private String wildCard;
    
    /**
     * User defined single Char
     */
    private String singleChar;

    /**
     * standard constructor
     * 
     * @param operator
     *        the filter operator (e.g. PropertyIsEqualTo)
     * @param value
     *        the value of the filter expression (lowerValue, if operator is "PropertyIsBetween")
     * @param propertyName
     *        name of the property for which this filter should be used
     * 
     */
    public ComparisonFilter(ComparisonOperator operatorp, String propertyNamep, String valuep) {
        this.operator = operatorp;
        this.value = valuep;
        this.propertyName = propertyNamep;
    }

    /**
     * constructor only for operator "Property Is Between"
     * 
     * @param operator
     *        the filter operator "PropertyIsEqualTo"
     * @param value
     *        the lower value
     * @param valueUpper
     *        the upper value
     * @throws Exception
     */
    public ComparisonFilter(ComparisonOperator operatorp,
                            String propertyNamep,
                            String valuep,
                            String valueUpperp) throws Exception {
        if (operator == ComparisonOperator.PropertyIsBetween) {
            this.operator = operatorp;
            this.value = valuep;
            this.valueUpper = valueUpperp;
            this.propertyName = propertyNamep;
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Use other constructor for ComparisonFilter! This constructor could only"
                                         + "be used for operator 'PropertyIsBetween'");
            throw se;
        }
    }
    
    public ComparisonFilter(ComparisonOperator operatorp,
            String propertyNamep,
            String valuep,
            String valueUpperp,
            String escapeStringp) throws Exception {
		if (operator == ComparisonOperator.PropertyIsLike) {
			this.operator = operatorp;
			this.value = valuep;
			this.valueUpper = valueUpperp;
			this.propertyName = propertyNamep;
			this.escapeString = escapeStringp;
		}
		else {
			OwsExceptionReport se = new OwsExceptionReport();
			se.addCodedException(ExceptionCode.NoApplicableCode,
			                 null,
			                 "Use other constructor for ComparisonFilter! This constructor could only"
			                         + "be used for operator 'PropertyIsLike'");
			throw se;
		}
    }

    /**
     * standard constructor
     * 
     */
    public ComparisonFilter() {
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @return Returns the valueUpper.
     */
    public String getValueUpper() {
        return valueUpper;
    }

    /**
     * @param value
     *        The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @param valueUpper
     *        The valueUpper to set.
     */
    public void setValueUpper(String valueUpper) {
        this.valueUpper = valueUpper;
    }

    /**
     * 
     * @return Returns String representation with values of this object
     */
    public String toString() {
        String result = "Spatial filter: ";
        if (valueUpper != null) {
            return result + propertyName + " " + value + " " + operator.name() + " " + valueUpper;
        }
        else {
            return result + propertyName + " " + operator.name() + " " + value;
        }
    }

    /**
     * @return the operator
     */
    public ComparisonOperator getOperator() {
        return operator;
    }

    /**
     * @param operator
     *        the operator to set
     */
    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName
     *        the propertyName to set
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

	/**
	 * @return the escapeString
	 */
	public String getEscapeString() {
		return escapeString;
	}

	/**
	 * @param escapeString
	 * 		  the escapeString to set
	 */
	public void setEscapeString(String escapeString) {
		this.escapeString = escapeString;
	}

    public String getWildCard() {
        return wildCard;
    }

    public void setWildCard(String wildCard) {
        this.wildCard = wildCard;
    }

    public String getSingleChar() {
        return singleChar;
    }

    public void setSingleChar(String singleChar) {
        this.singleChar = singleChar;
    }
    
}
