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

import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;

import com.vividsolutions.jts.geom.Geometry;

/**
 * class represents a spatial filter
 * 
 * @author Christoph Stasch
 * 
 */
public class SpatialFilter {

    /** spatial operator (e.g. 'Contains') */
    private SpatialOperator operator;

    /** spatial operand (geometry) in Well-Known-Text (WKT) format */
    private Geometry geometry;

    /** array containing ids of feature of interest, if the filter is only for specific fois; otherwise 'null' */
    private String[] foiIDs;

    /**
     * standard constructor
     * 
     * @param operatorp
     *        spatial operator
     * @param geomWKTp
     *        spatial operand (geometry) in WKT format
     * @param foiIDsp
     *        array containing ids of fois
     */
    public SpatialFilter(SpatialOperator operatorp, Geometry geomWKTp, String[] foiIDsp) {
        this.operator = operatorp;
        this.geometry = geomWKTp;
        this.foiIDs = foiIDsp;
    }

    /**
     * parameterless constructor
     * 
     */
    public SpatialFilter() {
    }

    /**
     * @return the operator
     */
    public SpatialOperator getOperator() {
        return operator;
    }

    /**
     * @param operator
     *        the operator to set
     */
    public void setOperator(SpatialOperator operator) {
        this.operator = operator;
    }

    /**
     * 
     * @return Returns String representation with values of this object
     */
    public String toString() {
        return "Spatial filter: " + operator + " " + geometry;
    }

    /**
     * @return the foiIDs
     */
    public String[] getFoiIDs() {
        return foiIDs;
    }

    /**
     * @param foiIDs
     *        the foiIDs to set
     */
    public void setFoiIDs(String[] foiIDs) {
        this.foiIDs = foiIDs;
    }

    /**
     * @return the srid
     */
    public int getSrid() {
        return geometry.getSRID();
    }

    /**
     * @return the geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * @param geometry
     *        the geometry to set
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
