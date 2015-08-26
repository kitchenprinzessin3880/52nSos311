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

import net.opengis.gml.TimePeriodDocument;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.sos.x10.GetFeatureOfInterestTimeDocument.GetFeatureOfInterestTime.DomainFeature;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.ds.IGetFeatureOfInterestTimeDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.SosGetFeatureOfInterestTimeRequest;

/**
 * DAO of PostgreSQL DB for GetFeatureOfInterestTime Operation
 * 
 * @author Stephan Knster
 */
public class PGSQLGetFeatureOfInterestTimeDAO implements IGetFeatureOfInterestTimeDAO {

    /** logger */
    protected static Logger log = Logger.getLogger(PGSQLGetObservationDAO.class);

    /** connection pool which contains the connections to the DB */
    protected PGConnectionPool cpool;


    /**
     * Constructor
     * 
     * @param cpool
     */
    public PGSQLGetFeatureOfInterestTimeDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    /**
     * 
     */
    public TimePeriodDocument getFeatureOfInterestTime(SosGetFeatureOfInterestTimeRequest request) throws OwsExceptionReport {

        TimePeriodDocument result = TimePeriodDocument.Factory.newInstance();

        DateTime begin = new DateTime();
        DateTime end = new DateTime();

        String minDateString = null;
        String maxDateString = null;

        // build query
        StringBuilder query = new StringBuilder();

        Connection con = null;

        try {

            // get connection
            con = cpool.getConnection();

            // query max time
            Statement stmt = con.createStatement();
            query.append("SELECT iso_timestamp(MIN( " + PGDAOConstants.timestampCn + " )) AS MINTIME, iso_timestamp(MAX( "
                    + PGDAOConstants.timestampCn + " )) AS MAXTIME FROM " + PGDAOConstants.obsTn);

            if (request.getMobileEnabled()) {
                query.append(" INNER JOIN " + PGDAOConstants.obsDfTn + " ON "
                        + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn + " = "
                        + PGDAOConstants.obsDfTn + "." + PGDAOConstants.obsIDCn + " INNER JOIN "
                        + PGDAOConstants.dfTn + " ON " + PGDAOConstants.obsDfTn + "."
                        + PGDAOConstants.domainFeatureIDCn + " = " + PGDAOConstants.dfTn + "."
                        + PGDAOConstants.domainFeatureIDCn);
            }
            query.append(" WHERE ");

            query.append(PGDAOConstants.foiIDCn + " = '" + request.getFeatureID() + "'");

            if (request.getMobileEnabled()) {
                //if request is mobile, consider all parameters

                //offering
                if (request.getOffering() != null && request.getOffering().length() != 0) {
                    query.append(" AND " + PGDAOConstants.offeringIDCn + " = '"
                            + request.getOffering() + "'");
                }

                //domain feature
                if (request.getDomainFeature() != null) {

                    // Object
                    if (request.getDomainFeature().getObjectIDArray() != null
                            && request.getDomainFeature().getObjectIDArray().length != 0) {

                        if ( !query.toString().endsWith(" AND ")
                                && !query.toString().endsWith(" AND (")) {
                            query.append(" AND (");
                        }

                        DomainFeature domFeat = request.getDomainFeature();
                        String[] domFeaturesArray = domFeat.getObjectIDArray();
                        for (int i = 0; i < domFeaturesArray.length; i++) {

                            query.append(PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn + " = '"
                                    + domFeaturesArray[i] + "' OR ");
                        }

                        if (query.toString().endsWith(" OR ")) {
                            query.delete(query.toString().length() - 4, query.toString().length());
                            query.append(")");
                        }
                    }
                    else if (request.getDomainFeatureSpatialFilter() != null) {

                        if ( !query.toString().endsWith(" AND ")
                                && !query.toString().endsWith(" AND (")) {
                            query.append(" AND ");
                        }

                        query.append(PGSQLQueryUtilities.getWhereClause4SpatialFilter(request.getDomainFeatureSpatialFilter(),
                                                                  PGDAOConstants.dfTn, PGDAOConstants.domainFeatureGeomCn, request.getDomainFeatureSpatialFilter().getSrid()));

                    }
                }

                //procedures
                if (request.getProcedure() != null) {

                    if ( !query.toString().endsWith(" AND ")
                            && !query.toString().endsWith(" AND (")) {
                        query.append(" AND (");
                    }

                    for (int i = 0; i < request.getProcedure().length; i++) {

                        query.append(PGDAOConstants.procIDCn + " = '" + request.getProcedure()[i]
                                + "' OR ");
                    }

                    if (query.toString().endsWith(" OR ")) {
                        query.delete(query.toString().length() - 4, query.toString().length());
                        query.append(")");
                    }
                }

                //phenomena
                if (request.getPhenomenon() != null) {

                    if ( !query.toString().endsWith(" AND ")
                            && !query.toString().endsWith(" AND (")) {
                        query.append(" AND (");
                    }

                    for (int i = 0; i < request.getPhenomenon().length; i++) {

                        query.append(PGDAOConstants.phenIDCn + " = '" + request.getPhenomenon()[i]
                                + "' OR ");
                    }

                    if (query.toString().endsWith(" OR ")) {
                        query.delete(query.toString().length() - 4, query.toString().length());
                        query.append(")");
                    }
                }
            }

            log.info(">>>MIN/MAX DATE QUERY: " + query.toString());

            ResultSet rs = stmt.executeQuery(query.toString());

            // if no maxDate is available give back empty String
            if (rs == null) {
                return result;
            }

            // get result as string and parse String to date
            while (rs.next()) {

                String maxTime = rs.getString("MAXTIME");
                String minTime = rs.getString("MINTIME");

                if (maxTime == null || minTime == null) {
                    TimePeriodType xb_time = result.addNewTimePeriod();
                    TimePositionType xb_begin = xb_time.addNewBeginPosition();
                    xb_begin.setNil();
                    TimePositionType xb_end = xb_time.addNewEndPosition();
                    xb_end.setNil();
                    return result;
                }
                
                begin = SosDateTimeUtilities.parseIsoString2DateTime(minTime);
                minDateString = SosDateTimeUtilities.formatDateTime2ResponseString(begin);

                end = SosDateTimeUtilities.parseIsoString2DateTime(maxTime);
                maxDateString = SosDateTimeUtilities.formatDateTime2ResponseString(end);
            }

        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(sqle);
            log.error("Error while query min/max date of observation from database!", sqle);
            throw se;
        }
        finally {
            // return connection
            if (con != null)
                cpool.returnConnection(con);
        }

        // return TimePeriodDocument

        TimePeriodType xb_time = result.addNewTimePeriod();
        TimePositionType xb_begin = xb_time.addNewBeginPosition();
        xb_begin.setStringValue(minDateString);
        TimePositionType xb_end = xb_time.addNewEndPosition();
        xb_end.setStringValue(maxDateString);

        return result;

    }
}
