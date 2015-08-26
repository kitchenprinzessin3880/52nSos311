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
 Created: 2009
 Modified: 12/10/2009
 ***************************************************************/
package org.n52.sos.ds.pgsql;

import org.apache.xmlbeans.XmlException;
import org.n52.sos.SosConfigurator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Utility class for DAOs to build SQL statements.
 * 
 * @author Carsten Hollmann
 *
 */
public class PGSQLQueryUtilities {
    /**
     * constructs the where clause if a feature of interest is contained in the observation request
     * 
     * @param spFil
     *        the FeatureOfInterest for which the where clause should be returned
     * @return where clause of the FeatureOfInterest for the observation query
     * @throws OwsExceptionReport
     *         if construction of the where clause failed
     * @throws XmlException
     *         if construction of the where clause failed
     */
    public static String getWhereClause4SpatialFilter(SpatialFilter spFil, String tableName, String columnName, int srid) throws OwsExceptionReport {

        // query String
        String query = "";

        SpatialOperator operator = spFil.getOperator();
        Geometry geom = spFil.getGeometry();
        WKTWriter wktWriter = new WKTWriter();
        String geomWKT = wktWriter.write(geom);

        switch (operator) {
        // filter operator is ogc:BBOX or ogc:Contains
        case BBOX:
            query = getWhereClause4BBOX(geomWKT, srid, tableName, columnName);
            break;
        case Contains:
            query = getWhereClause4Geometry(geomWKT, srid, operator.toString(), tableName, columnName);
            break;
        case Overlaps:
            query = getWhereClause4Geometry(geomWKT, srid, operator.toString(), tableName, columnName);
            break;
        case Intersects:
            query = getWhereClause4Geometry(geomWKT, srid, operator.toString(), tableName, columnName);
            break;
        default:
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "The spatial filter operator " + operator.name()
                                         + " is not supported");
            throw se;
        }

        return query;
    } // end getWhereClause4Foi
    
    /**
     * creates a where clause 4 the BBOX spatial filter
     * 
     * @param operator
     *        DOM Node of the ogc:BBOX element
     * @return Returns the where clause part for this bounding box filter as String
     * @throws XmlException
     *         if paring the Dom Node into a BBOXDocument (XmlBean) failed
     */
    private static String getWhereClause4BBOX(String geometryWKT, int srid, String tableName, String columnName) {
    	StringBuilder query = new StringBuilder();
        query.append("Intersects(ENVELOPE(GeometryFromText('" + geometryWKT + "', " + srid
                + ")), ");
        if (srid != SosConfigurator.getInstance().getCapsCacheController().getSrid()) {
            query.append("TRANSFORM(" + tableName + "." + columnName + "," + srid + "))");
        }
        else {
            query.append(tableName + "." + columnName + ")");
        }
        return query.toString();
    } // end getWhereClause4BBOX

    /**
     * builds the where clause part for the spatial filter which contains a geometry and not an envelope
     * 
     * @param xb_geometry
     *        the XmlBean geometry of the spatial filter
     * @param operatorName
     *        the name of the spatial filter as String
     * @return Returns a String representing the where clause part of the spatial filter
     * @throws XmlException
     *         if parsing the geometry bean into a concrete geometry bean (e.g. PointType) failed
     * @throws OwsExceptionReport
     */
    private static String getWhereClause4Geometry(String geometryWKT, int srid, String operatorName, String tableName, String columnName) {
    	StringBuilder query = new StringBuilder();
    	query.append(operatorName + "(GeometryFromText('" + geometryWKT + "', " + srid
                + "), ");
        if (srid != SosConfigurator.getInstance().getCapsCacheController().getSrid()) {
            query.append("TRANSFORM(" + tableName + "." + columnName + "," + srid + "))");
        }
        else {
            query.append(tableName + "." + columnName + ")");
        }
        return query.toString();
    } // end getWhereClause4Geometry
}
