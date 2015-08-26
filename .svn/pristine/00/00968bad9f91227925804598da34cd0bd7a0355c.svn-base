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
import java.sql.SQLException;
import java.sql.Statement;

import net.opengis.gml.TimePositionType;

import org.apache.log4j.Logger;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.ds.IUpdateSensorDAO;
import org.n52.sos.ds.insert.IInsertDomainFeatureDAO;
import org.n52.sos.ds.insert.IInsertRelationshipsDAO;
import org.n52.sos.ds.insert.pgsql.PGSQLInsertDAOFactory;
import org.n52.sos.ds.update.IUpdateProcedureDAO;
import org.n52.sos.ds.update.pgsql.PGSQLUpdateDAOFactory;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosUpdateSensorRequest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * DAO of PostgreSQL DB for UpdateSensor Operation
 * 
 * @author Stephan Knster
 */
public class PGSQLUpdateSensorDAO implements IUpdateSensorDAO {

    /** logger */
    protected static Logger log = Logger.getLogger(PGSQLUpdateSensorDAO.class);

    /** connection pool which contains the connections to the DB */
    protected PGConnectionPool cpool;


    /**
     * Constructor
     * 
     * @param cpool
     */
    public PGSQLUpdateSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
//        this.dbSdf = new SimpleDateFormat(PGDAOConstants.dbDateFormat);
    }

    /**
     * updates a sensor and creates an entry in procedure history table
     */
    public void updateSensor(SosUpdateSensorRequest request) throws OwsExceptionReport {

        Connection trCon = null;

        try {

            trCon = cpool.getConnection();
            trCon.setAutoCommit(false);

            PGSQLUpdateDAOFactory daoFactory = new PGSQLUpdateDAOFactory(cpool);
            IUpdateProcedureDAO updateProcedureDAO = daoFactory.getUpdateProcedureDAO();

            PGSQLInsertDAOFactory insertDaoFactory = new PGSQLInsertDAOFactory(cpool);
            IInsertDomainFeatureDAO insertDomFeatDAO = insertDaoFactory.getInsertDomainFeatureDAO();
            IInsertRelationshipsDAO insertRelDAO = insertDaoFactory.getInsertRelationshipsDAO();

            CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();

//            ResultSet procedureResultSet;
//
//            String actual_position = "";
//            boolean active;
//            boolean mobile;

            try {

                // if domain feature in request != null insert relationship and domain feature, if necessary
                if (request.getDomFeat() != null) {

                    // if only id is set, insert relationship, if necessary
                    if (request.getDomFeat().getDescription().equalsIgnoreCase("PARAMETER_"
                            + SosConstants.PARAMETER_NOT_SET)
                            || request.getDomFeat().getName().equalsIgnoreCase("PARAMETER_"
                                    + SosConstants.PARAMETER_NOT_SET)
                            || request.getDomFeat().getGeom() == null) {

                        // if df is not contained in db, throw exception
                        if (capsCache.getDomainFeatures().contains(request.getDomFeat().getId())) {
                            // if no procs for df are stored
                            if (capsCache.getProcs4DomainFeature(request.getDomFeat().getId()) == null) {
                                // insert relationship
                                insertRelDAO.insertProcDfRelationsship(request.getSensorID(),
                                                                       request.getDomFeat().getId(),
                                                                       trCon);
                                capsCache.updateFois(); //.refreshFOIs();
                            }
                            // if some procs are contained for df, insert relationship, if procID from request
                            // is
                            // not contained
                            else if ( !capsCache.getProcs4DomainFeature(request.getDomFeat().getId()).contains(request.getSensorID())) {
                                // insert relationship
                                insertRelDAO.insertProcDfRelationsship(request.getSensorID(),
                                                                       request.getDomFeat().getId(),
                                                                       trCon);
                                capsCache.updateFois(); //.refreshFOIs();
                            }
                        }
                        else {
                            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                            log.error("Error: domainFeature '"
                                    + request.getDomFeat().getId()
                                    + "' is not contained in database. Please add a domainFeature to your request.");
                            se.addCodedException(ExceptionCode.NoApplicableCode,
                                                 null,
                                                 "Error: domainFeature '"
                                                         + request.getDomFeat().getId()
                                                         + "' is not contained in database. Please add a domainFeature to your request.");
                            throw se;
                        }
                    }
                    else {
                        if ( !capsCache.getDomainFeatures().contains(request.getDomFeat().getId())) {

                            // insert domain feature, if not contained in DB
                            insertDomFeatDAO.insertDomainFeature(request.getDomFeat().getId(),
                                                                 request.getDomFeat().getName(),
                                                                 request.getDomFeat().getDescription(),
                                                                 request.getDomFeat().getGeom(),
                                                                 request.getDomFeat().getFeatureType(),
                                                                 request.getDomFeat().getSchemaLink(),
                                                                 trCon);

                            // insert relationship
                            insertRelDAO.insertProcDfRelationsship(request.getSensorID(),
                                                                   request.getDomFeat().getId(),
                                                                   trCon);
                            capsCache.updateFois(); //.refreshFOIs();
                        }
                        else {

                            // if no procs for df are stored
                            if (capsCache.getProcs4DomainFeature(request.getDomFeat().getId()) == null) {
                                // insert relationship
                                insertRelDAO.insertProcDfRelationsship(request.getSensorID(),
                                                                       request.getDomFeat().getId(),
                                                                       trCon);
                                capsCache.updateFois(); //.refreshFOIs();
                            }
                            // if some procs are contained for df, insert relationship, if procID from request
                            // is
                            // not contained
                            else if ( !capsCache.getProcs4DomainFeature(request.getDomFeat().getId()).contains(request.getSensorID())) {
                                // insert relationship
                                insertRelDAO.insertProcDfRelationsship(request.getSensorID(),
                                                                       request.getDomFeat().getId(),
                                                                       trCon);
                                capsCache.updateFois(); //.refreshFOIs();
                            }
                        }
                    }
                }

                // TODO: use insert proc hist DAO
                Statement stmt;
                
                // update procedure
                WKTReader wktReader = new WKTReader();
                Geometry geometry;

                geometry = wktReader.read("POINT(" + request.getPosition().getCoordinate().x + " "
                        + request.getPosition().getCoordinate().y + ")");
                geometry.setSRID(request.getPosition().getSRID());

                stmt = trCon.createStatement();
                stmt.execute(createInsertStatement(request.getSensorID(),
                								   request.getTime(),
                                                   geometry,
                                                   request.isActive(),
                                                   request.isMobile()));
                
                updateProcedureDAO.updateProcedure(request.getSensorID(),
                                                   geometry,
                                                   request.isActive(),
                                                   request.isMobile(),
                                                   trCon);
            }
            catch (ParseException e) {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                log.error("An error occured while reading the geometry: " + e.toString());
                se.addCodedException(ExceptionCode.NoApplicableCode, null, e);
                throw se;
            }
            catch (SQLException sqle) {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                log.error("An error occured while query the data from the database: "
                        + sqle.toString());
                se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
                throw se;
            }

            trCon.commit();
            trCon.setAutoCommit(true);
            capsCache.updateFois(); //.refreshFOIs();
        }

        catch (SQLException e) {
            String message = "Error while executing updateSensor operation. Values could not be stored in database: "
                    + e.getMessage();
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, message);
            log.error(message);
            throw se;
        }
        finally {
            if (trCon != null) {
                cpool.returnConnection(trCon);
            }
        }
    }

    /**
     * creates insert statement for procedure history table
     * 
     * @param procedure_id
     * @param actual_position
     * @param active
     * @param mobile
     * @return insert statement
     */
    public String createInsertStatement(String procedure_id,
    									TimePositionType time,
                                        Geometry geometry,
                                        boolean active,
                                        boolean mobile) {

        // current_date
    	
        String current_date = time.getStringValue();
        
      WKTWriter wktWriter = new WKTWriter();
      String geomWKT = wktWriter.write(geometry);
      int EPSGid = geometry.getSRID();

        // insert statement
        String query = "INSERT INTO " + PGDAOConstants.procHistTn + "("
                + PGDAOConstants.procHistProcedureIdCn + ", " + PGDAOConstants.procHistTimeStampCn
                + ", " + PGDAOConstants.procHistPositionCn + ", " + PGDAOConstants.procHistActiveCn
                + ", " + PGDAOConstants.procHistMobileCn + ") VALUES " + "('" + procedure_id
                + "', '" + current_date + "', GeometryFromText('" + geomWKT + "'," + EPSGid + "), " + active + ", " + mobile
                + ");";
//        current_date + "', '" + actual_position + "', " + active
        return query;
    }

}
