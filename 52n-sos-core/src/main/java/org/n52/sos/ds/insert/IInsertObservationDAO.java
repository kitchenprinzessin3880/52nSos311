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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.joda.time.DateTime;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Interface for inserting new observations into the 52n-SOS-v2-01-00 DB
 * 
 * 
 * @author Alexander C. Walkowski
 * 
 * @version 0.1
 */
public interface IInsertObservationDAO {

    /**
     * method for inserting an observation
     * 
     * @param time
     *        time of the observation
     * @param procedure_id
     *        String the id of the procedure the observation value belongs to
     * @param feature_of_interest_id
     *        String the id of the feature_of_interest(Station) the observation value belongs to
     * @param phenomenon_id
     *        String the id of the phenomenon the observation value belongs to
     * @param offering_id
     *        id of the offering, which the observation is inserted for
     * @param value
     *        String the value of this observation
     * @param mimeType
     *        mimeType, if value is reference on stream or other file
     * @param valueColumnName
     *        should be name of the column (either 'text_value' or numeric_value')
     * @param quality
     *        collection of quality informations for the observation, which should be inserted
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public int insertObservation(ISosTime time,
                                 String procedure_id,
                                 String feature_of_interest_id,
                                 String phenomenon_id,
                                 String offering_id,
                                 String value,
                                 String mimeType,
                                 String valueColumnName,
                                 Collection<SosQuality> quality,
                                 Connection trCon) throws SQLException, OwsExceptionReport;

    /**
     * method for inserting an observation into the SOS database
     * 
     * @param obs
     *        observation with text value which should be inserted
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public int insertObservation(AbstractSosObservation obs, Connection trCon) throws SQLException,
            IOException,
            OwsExceptionReport;

    /**
     * method for inserting multiple observations into the database
     * 
     * @param observations
     *        ArrayList with Observation containing the observations which should be inserted
     * @throws SQLException
     *         if inserting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public void insertObservations(ArrayList<AbstractSosObservation> observations, Connection trCon) throws SQLException,
            IOException,
            OwsExceptionReport;

    /**
     * deletes values in observation table which are before the passed date
     * 
     * @param lastValidDate
     *        Date before which the values in the observation table should be deleted
     * @return Returns true, if deleting was successful, false otherwise
     * @throws SQLException
     *         if deleting an observation failed
     * @throws OwsExceptionReport
     *         if getting a database connection from connection pool failed
     */
    public void deleteOldObservations(DateTime lastValidDate, Connection trCon) throws OwsExceptionReport,
            SQLException;

    /**
     * executes the ANALYZE statement to reanalyze metadata about the tables
     * 
     * @throws SQLException
     *         if executing analyze statement failed
     * @throws OwsExceptionReport
     *         if getting connection from connection pool failed
     * 
     */
    public void analyze() throws OwsExceptionReport, SQLException;
}
