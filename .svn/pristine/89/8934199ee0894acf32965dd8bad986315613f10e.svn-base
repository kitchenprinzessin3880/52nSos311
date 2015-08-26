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

package org.n52.sos.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.n52.sos.ds.IGetFeatureOfInterestDAO;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.SosFeatureCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosGetFeatureOfInterestRequest;

/**
 * DAO of PostgreSQL DB for GetFeatureOfInterest Operation
 * 
 * @author Stephan Knster
 */
public class PGSQLGetFeatureOfInterestDAO implements IGetFeatureOfInterestDAO {

    /** logger */
    protected static Logger log = Logger.getLogger(PGSQLGetObservationDAO.class);

    /** connection pool which contains the connections to the DB */
    protected PGConnectionPool cpool;

    /**
     * Constructor
     * 
     * @param cpool
     */
    public PGSQLGetFeatureOfInterestDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * 
     */
    public SosAbstractFeature getFeatureOfInterest(SosGetFeatureOfInterestRequest request) throws OwsExceptionReport {

        SosAbstractFeature result = null;

        // FoiID
        String[] foiIdArray = request.getFeatureIDs();

        // Foi location
        SpatialFilter foiLocation = request.getLocation();

        // initialize local attributes
        String foi = "";
        String desc = "";
        String name = "";
        String geom = "";
        String featureType = "";
        int srid = 0;
        String schemaLink = "";

        Connection con = cpool.getConnection();
        ResultSet foiResultSet;
        Collection<SosAbstractFeature> sosAbstractFois = null;

        try {

            // initialize SosAbstractFeature Collection
            sosAbstractFois = new ArrayList<SosAbstractFeature>();

            // get Foi ResultSet
            if ((foiIdArray != null && foiIdArray.length != 0) || foiLocation != null) {
                foiResultSet = queryFoi(foiIdArray, foiLocation, con);
            }
            else {
                foiResultSet = null;
            }

            // get Foi data
            while (foiResultSet.next()) {

                foi = foiResultSet.getString(PGDAOConstants.foiIDCn);
                name = foiResultSet.getString(PGDAOConstants.foiNameCn);
                desc = foiResultSet.getString(PGDAOConstants.foiDescCn);
                geom = foiResultSet.getString(PGDAOConstants.foiGeometry);
                featureType = foiResultSet.getString(PGDAOConstants.featureTypeCn);
                srid = foiResultSet.getInt(PGDAOConstants.foiSrid);

                // add new AbstractFeature to Collection
                sosAbstractFois.add(ResultSetUtilities.getAbstractFeatureFromValues(foi, desc, name, geom, srid, featureType, schemaLink));

            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the data from the database: " + sqle.toString());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }
        SosFeatureCollection featCol = new SosFeatureCollection(sosAbstractFois);
        result = featCol;

        return result;
    }

    /**
     * builds and executes the query to get the FOI data from the database;
     * 
     * @param getFeatureOfInterestId
     * @return ResultSet
     * @throws OwsExceptionReport
     */
    private ResultSet queryFoi(String[] featureOfInterestIds, SpatialFilter foiLocation, Connection con) throws OwsExceptionReport {

        // initialize resultset
        ResultSet resultSet = null;

        try {

            Statement stmt;
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                       ResultSet.CONCUR_READ_ONLY);

            // buffer for query
            StringBuilder query = new StringBuilder();

            query.append("SELECT " +
            		PGDAOConstants.foiIDCn + ", " + PGDAOConstants.foiNameCn + ", " 
            		+ PGDAOConstants.foiDescCn 
            		+ ", " + "AsText(" + PGDAOConstants.geomCn + ") AS " + PGDAOConstants.foiGeometry + ", "
            		+ "SRID(" + PGDAOConstants.geomCn + ") AS " + PGDAOConstants.foiSrid + ", "
            		+ PGDAOConstants.featureTypeCn + ", " + PGDAOConstants.schemaLinkCn
            		+ " FROM " + PGDAOConstants.foiTn + " WHERE ");
            
            if (featureOfInterestIds != null && featureOfInterestIds.length != 0) {
            	int foiCount = featureOfInterestIds.length;
                for (int i = 0; i < foiCount; i++) {
                    query.append(PGDAOConstants.foiIDCn + " = '" + featureOfInterestIds[i] + "'");
                    if (i != foiCount - 1) {
                        query.append(" OR ");
                    }
                }
            }
            else if (foiLocation != null) {
            	query.append(PGSQLQueryUtilities.getWhereClause4SpatialFilter(foiLocation, PGDAOConstants.foiTn, PGDAOConstants.geomCn, foiLocation.getSrid()));
            }

            log.debug(">>>QUERY: " + query.toString());

            resultSet = stmt.executeQuery(query.toString());

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the data from the database: " + sqle.toString());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return resultSet;

    }

}
