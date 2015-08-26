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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.ds.IGetResultDAO;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetObservationRequest;
import org.n52.sos.request.SosGetResultRequest;

/**
 * DAO of PostgreSQL DB for GetResult Operation
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLGetResultDAO implements IGetResultDAO {

    /** logger */
    protected static final Logger log = Logger.getLogger(PGSQLGetResultDAO.class);

    /** connection pool which contains the connections to the DB */
    private PGConnectionPool cpool;

    /** the getObservation DAO is used to query the result data from the database */
    private PGSQLGetObservationDAO getObsDAO;

    /**
     * constructor
     * 
     * @param cpool
     *        PGConnectionPool which contains the connections to the DB
     */
    public PGSQLGetResultDAO(PGConnectionPool cpool, PGSQLGetObservationDAO getObsDAO) {
        this.cpool = cpool;
        this.getObsDAO = getObsDAO;
    }

    /**
     * method creates a query string from the getResult request to retrieve data from the database
     * 
     * @param getRes
     *        the getResult request
     * @return Returns query string to query the data for the response from the database
     * @throws OwsExceptionReport
     *         if query of the stored request or of data failed
     * 
     */
    public SosGenericObservation getResult(SosGetResultRequest getResRequest) throws OwsExceptionReport {

        SosGenericObservation sosGenObs = null;
        SosObservationCollection obsCol = null;

        // query stored request from database
        SosGetObservationRequest getObsRequest = queryRequest(getResRequest);

        // if no getObservation could be created, throw exception!
        if (getObsRequest == null) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoDataAvailable,
                                 null,
                                 "No observation template is available for the getResult request!");
            throw se;
        }

        // set EventTime on eventTime of getResultRequest
        if (getResRequest.getEventTimes() != null) {
            getObsRequest.setEventTime(getResRequest.getEventTimes());
        }
        
        // forward request to getObservation DAO to execute the data query and receive the resulting ResultSet
        obsCol = getObsDAO.getObservation(getObsRequest);
        if (obsCol.getObservationMembers() == null || obsCol.getObservationMembers().size() != 1) {

        }

        Iterator<AbstractSosObservation> obsMembers = obsCol.getObservationMembers().iterator();
        while (obsMembers.hasNext()) {
            sosGenObs = (SosGenericObservation) obsMembers.next();
        }

        return sosGenObs;
    }

    /**
     * queries the stored getObservation request for the observation id parameter of the getResult request
     * from the database and creates an GetObservation XMLBean representing the getObservation request,
     * changes time and procedure values, if necessary and returns the xml bean
     * 
     * @param getRes
     *        the getResult request
     * @return Returns XMLBean representing the former getObservation request
     * @throws OwsExceptionReport
     *         if query of the request failed
     */
    private SosGetObservationRequest queryRequest(SosGetResultRequest getRes) throws OwsExceptionReport {
        SosGetObservationRequest request = null;
        String observationID = getRes.getObservationTemplateId();
        List<String> procIDs = new ArrayList<String>();
        observationID = observationID.replace(SosConstants.OBS_TEMP_ID_PREFIX, "");

        String query = "SELECT " + PGDAOConstants.reqTn + ".*, "
        		+ PGDAOConstants.obsTempTn + "." + PGDAOConstants.procIDCn
        		+ " FROM " + PGDAOConstants.reqTn + " NATURAL INNER JOIN "
                + PGDAOConstants.obsTempTn + " WHERE " + PGDAOConstants.obsTempTn + "."
                + PGDAOConstants.obsTempIDCn + " = '" + observationID + "';";
        
        Connection con = null;
        try {
            con = cpool.getConnection();
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                 ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(query);
            if ( !rs.next()) {
                // do nothing
            }
            else {
                rs.beforeFirst();
            }
            // get id element
            while (rs.next()) {
            	procIDs.add(rs.getString(PGDAOConstants.procIDCn));
                String docString = rs.getString(PGDAOConstants.reqTn);
                AbstractSosRequest sosAbsRequest = SosConfigurator.getInstance().getHttpPostDecoder().receiveRequest(docString);
                if (sosAbsRequest instanceof SosGetObservationRequest) {
                    request = (SosGetObservationRequest) sosAbsRequest;

                }
                request.setProcedure((String[]) procIDs.toArray(new String[0]));
                request.setEventTime(getRes.getEventTimes());
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while query of result from DB:" + sqle.getMessage());
            log.error(se.getMessage());
            throw se;
        }
        finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }
        return request;
    }
}
