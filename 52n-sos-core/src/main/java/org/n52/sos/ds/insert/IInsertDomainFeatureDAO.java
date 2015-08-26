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

package org.n52.sos.ds.insert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Interface for inserting new domain features into SOS database
 * 
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public interface IInsertDomainFeatureDAO {

    /**
     * method for inserting a new domain feature into the SOS DB. The geometry which should be inserted has to
     * have the textual form of the geometries the used database system offers.
     * 
     * @param domain_feature_id
     *        String the id of this domain feature
     * @param domain_feature_name
     *        String name of this domain feature
     * @param domain_feature_description
     *        String decription of the domain feature
     * @param geom
     *        geometry of the domain feature
     * @param featureType
     *        type of the domain feature (e.g. 'sa:Station')
     * @param schemalink
     *        String containing the schema link
     * @throws SQLException
     *         if insertion of a domain feature failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertDomainFeature(String domain_feature_id,
                                    String domain_feature_name,
                                    String domain_feature_description,
                                    Geometry geomWKT,
                                    String featureType,
                                    String schemalink,
                                    Connection trCon) throws SQLException, OwsExceptionReport;

    /**
     * method inserts the domain feature into the SOS database
     * 
     * @param foi
     *        SosFeatureOfInterest which should be inserted
     * @throws SQLException
     *         if insertion of a domain feature failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertDomainFeature(SosAbstractFeature foi, Connection trCon) throws SQLException,
            OwsExceptionReport;

    /**
     * method inserts the multiple domain features into the SOS database
     * 
     * @param fois
     *        ArrayList with FeatureOfInterest containing the fois which should be inserted
     * @throws SQLException
     *         if insertion of a domain feature failed
     * @throws OwsExceptionReport
     *         if getting the connection to the database from connection pool failed
     */
    public void insertDomainFeatures(ArrayList<SosAbstractFeature> fois, Connection trCon) throws SQLException,
            OwsExceptionReport;
}
