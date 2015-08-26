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
import org.n52.sos.SosConstants;
import org.n52.sos.ds.IGetDomainFeatureDAO;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.SosFeatureCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosGetDomainFeatureRequest;

/**
 * DAO of PostgreSQL DB for GetDomainFeature Operation
 * 
 * @author Stephan Knster
 */
public class PGSQLGetDomainFeatureDAO implements IGetDomainFeatureDAO {

    /** logger */
    protected static Logger log = Logger.getLogger(PGSQLGetDomainFeatureDAO.class);

    /** connection pool which contains the connections to the DB */
    protected PGConnectionPool cpool;

    /**
     * Constructor
     * 
     * @param cpool
     */
    public PGSQLGetDomainFeatureDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * 
     */
    public SosAbstractFeature getDomainFeature(SosGetDomainFeatureRequest request) throws OwsExceptionReport {

        SosAbstractFeature result = null;

        // dfID
        String[] dfIdArray = request.getDomainFeatureIDs();

        // df location
        SpatialFilter dfLocation = request.getLocation();

        // initialize local attributes
        String df = "";
        String desc = "";
        String name = "";
        String geomWKT = "";
        String featureType = "";
        int srid = 0;

        Connection con = cpool.getConnection();
        ResultSet dfResultSet;
        Collection<SosAbstractFeature> sosAbstractDfs = null;

        try {

            // initialize SosAbstractFeature Collection
            sosAbstractDfs = new ArrayList<SosAbstractFeature>();

            // get Foi ResultSet
            if ((dfIdArray != null && dfIdArray.length != 0) || dfLocation != null) {
                dfResultSet = queryDf(dfIdArray, dfLocation, con);
            }
            else {
                dfResultSet = null;
            }

            // get Foi data
            while (dfResultSet.next()) {

                df = dfResultSet.getString(PGDAOConstants.domainFeatureIDCn);
                name = dfResultSet.getString(PGDAOConstants.domainFeatureNameCn);
                desc = dfResultSet.getString(PGDAOConstants.domainFeatureDescCn);
                geomWKT = dfResultSet.getString(PGDAOConstants.domainFeatureGeomCn);
                featureType = dfResultSet.getString(PGDAOConstants.domainFeatureTypeCn);
                srid = dfResultSet.getInt(PGDAOConstants.dfSrid );

//              // add new AbstractFeature to Collection
                sosAbstractDfs.add(ResultSetUtilities.getAbstractFeatureFromValues(df, desc, name, geomWKT, srid, featureType, SosConstants.PARAMETER_NOT_SET));
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
        SosFeatureCollection featCol = new SosFeatureCollection(sosAbstractDfs);
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
    private ResultSet queryDf(String[] domainFeatureIds, SpatialFilter dfLocation, Connection con) throws OwsExceptionReport {

        // initialize resultset
        ResultSet resultSet = null;

        try {

            Statement stmt;
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                       ResultSet.CONCUR_READ_ONLY);

            // buffer for query
            StringBuffer queryBuffer = new StringBuffer();

            queryBuffer.append("SELECT " + PGDAOConstants.domainFeatureIDCn + ", "
                    + PGDAOConstants.domainFeatureNameCn + ", "
                    + PGDAOConstants.domainFeatureDescCn + "," + " AsText("
                    + PGDAOConstants.domainFeatureGeomCn + ") AS " + PGDAOConstants.domainFeatureGeomCn + ", " 
                    + " SRID(" + PGDAOConstants.domainFeatureGeomCn + ") AS " + PGDAOConstants.dfSrid + ", "
                    + PGDAOConstants.domainFeatureTypeCn + "," + PGDAOConstants.schemaLinkCn
                    + " FROM " + PGDAOConstants.dfTn + " WHERE ");
            if (domainFeatureIds != null && domainFeatureIds.length > 0) {
            	int foiCount = domainFeatureIds.length;
                for (int i = 0; i < foiCount; i++) {
                    queryBuffer.append(PGDAOConstants.domainFeatureIDCn + " = '" + domainFeatureIds[i]
                            + "'");
                    if (i != foiCount - 1) {
                        queryBuffer.append(" OR ");
                    }
                }
            } else if (dfLocation != null) {
                queryBuffer.append(PGSQLQueryUtilities.getWhereClause4SpatialFilter(dfLocation, PGDAOConstants.dfTn, PGDAOConstants.domainFeatureGeomCn, dfLocation.getSrid()));
            }

            log.debug(">>>QUERY: " + queryBuffer.toString());

            resultSet = stmt.executeQuery(queryBuffer.toString());

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
