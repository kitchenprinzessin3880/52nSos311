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

package org.n52.sos.ds.insert.pgsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.n52.sos.ds.insert.IInsertFoiDAO;
import org.n52.sos.ds.pgsql.PGConnectionPool;
import org.n52.sos.ds.pgsql.PGDAOConstants;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * class implements the IInsertObservationDAO interface and the methods for inserting observations into the
 * SOSDB which are defined in this interface.
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class InsertFoiDAO implements IInsertFoiDAO {

    /** connection pool */
    private PGConnectionPool cpool;

    /** logger */
    protected static final Logger log = Logger.getLogger(InsertFoiDAO.class);

    /**
     * constructor
     * 
     * @param cpoolp
     *        PGConnectionPool which offers a pool of open connections to the db
     */
    public InsertFoiDAO(PGConnectionPool cpoolp) {
        cpool = cpoolp;
    }

    /**
     * method for inserting a new feature of interest into the SOS DB. The geometry which should be inserted
     * has to have the textual form of the geometries the used database system offers.
     * 
     * @param feature_of_interest_id
     *        String the id of this feature of interest
     * @param feature_of_interest_name
     *        String name of this feature of interest
     * @param feature_of_interest_description
     *        String decription of the feature of interest
     * @param geom
     *        geometry of the feature of interest
     * @param featureType
     *        type of the feature of interest (e.g. 'sa:Station')
     * @param schemalink
     *        String containing the schema link
     * 
     */
    public void insertFOI(String feature_of_interest_id,
                          String feature_of_interest_name,
                          String feature_of_interest_description,
                          Geometry geom,
                          String featureType,
                          String schemalink,
                          Connection trCon) throws SQLException, OwsExceptionReport {

        Connection con = null;
        try {

            // insert statement
            String query = createInsertStatement(feature_of_interest_id,
                                                 feature_of_interest_name,
                                                 feature_of_interest_description,
                                                 geom,
                                                 featureType,
                                                 schemalink);

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            log.debug("FOI Insert Statement: " + query);
            stmt.execute(query);
            log.debug("FOI inserted successfully!");
        }
        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * methods inserts the feature of interest into the SOS database
     * 
     * @param foi
     *        FeatureOfInterest which should be inserted
     * @throws OwsExceptionReport
     */
    public void insertFOI(SosAbstractFeature foi, Connection trCon) throws SQLException,
            OwsExceptionReport {
        insertFOI(foi.getId(),
                  foi.getName(),
                  foi.getDescription(),
                  foi.getGeom(),
                  foi.getFeatureType(),
                  foi.getSchemaLink(),
                  trCon);
    }

    /**
     * methods inserts the multiple feature of interests into the SOS database
     * 
     * @param fois
     *        ArrayList with FeatureOfInterest containing the fois which should be inserted
     * @throws SQLException
     * @throws OwsExceptionReport
     */
    public void insertFOIs(ArrayList<SosAbstractFeature> fois, Connection trCon) throws SQLException,
            OwsExceptionReport {

        Connection con = null;

        try {
            StringBuffer queryBuffer = new StringBuffer();
            String query = null;

            // build insert string
            for (SosAbstractFeature foi : fois) {
                query = createInsertStatement(foi.getId(),
                                              foi.getName(),
                                              foi.getDescription(),
                                              foi.getGeom(),
                                              foi.getFeatureType(),
                                              foi.getSchemaLink());
                queryBuffer.append(query);
            }

            if (trCon == null) {
                con = cpool.getConnection();
            }
            else {
                con = trCon;
            }
            Statement stmt = con.createStatement();
            stmt.execute(queryBuffer.toString());

            // TODO refresh FOI metadata
        }

        finally {
            if (con != null && trCon == null) {
                cpool.returnConnection(con);
            }
        }

    }

    /**
     * creates the insert statement
     * 
     * @param feature_of_interest_id
     *        id of the foi which should be inserted
     * @param feature_of_interest_name
     *        name of the foi which should be inserted
     * @param feature_of_interest_description
     *        description of the foi which should be inserted
     * @param geom
     *        geometry of the feature of interest
     * @param featureType
     *        type of the feature of interest in schema document (e.g. 'sa:StationType')
     * @param schemalink
     *        schema link of the foi which should be inserted
     * @return Returns insert statement
     */
    public String createInsertStatement(String feature_of_interest_id,
                                        String feature_of_interest_name,
                                        String feature_of_interest_description,
                                        Geometry geom,
                                        String featureType,
                                        String schemalink) {

        WKTWriter wktWriter = new WKTWriter();
        String geomWKT = wktWriter.write(geom);
        int EPSGid = geom.getSRID();
        // insert statement
        String query = "INSERT INTO " + PGDAOConstants.foiTn + "(" + PGDAOConstants.foiIDCn + ", "
                + PGDAOConstants.foiNameCn + ", " + PGDAOConstants.foiDescCn + ", "
                + PGDAOConstants.geomCn + ", " + PGDAOConstants.featureTypeCn + ", "
                + PGDAOConstants.schemaLinkCn + ")" + " VALUES " + "('" + feature_of_interest_id
                + "','" + feature_of_interest_name + "','" + feature_of_interest_description + "',"
                + "GeometryFromText('" + geomWKT + "'," + EPSGid + "),'" + featureType + "','"
                + schemalink + "');";
        return query;
    }

}
