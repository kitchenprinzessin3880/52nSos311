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

package org.n52.sos;

import java.util.List;

import org.apache.log4j.Logger;
import org.n52.sos.ds.IInsertObservationOperationDAO;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosInsertObservationRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.InsertObservationResponse;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Listener for InsertObservation operation
 * 
 * @author Christoph Stasch
 * 
 */
public class InsertObservationListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(InsertObservationListener.class.getName());

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.insertObservation.name();

    /** DAO for inserting observation into db and getting necessary information from DB */
    private IInsertObservationOperationDAO insertObsOperationDAO;

    /**
     * constructor
     * 
     */
    public InsertObservationListener() {
        this.insertObsOperationDAO = SosConfigurator.getInstance().getFactory().getInsertObservationOperationDAO();
        log.info("InsertObservationListener initialized successfully!!");
    }

    /**
     * @return Returns name of InsertObservation operation
     */
    public String getOperationName() {
        return OPERATION_NAME;
    }

    /**
     * checks, wether insertObservation request is correct, then inserts observation and returns assigned id
     * of observation contained in InsertObservationResponse
     * 
     * @param request
     *        InsertObservation request
     * @return Returns InsertObservationResponse containing the assigned id for observation; if an error
     *         occurrs an ExceptionResponse is returned containing the error message
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;
        SosInsertObservationRequest insertObsReq = (SosInsertObservationRequest) request;
        try {
            
            SosObservationCollection obsCol = new SosObservationCollection();
            obsCol.setObservationMembers(insertObsReq.getObservation());
            obsCol.setBoundedBy(new Envelope());

            // check, whether sensor, which has produced the observation is
            // already registered at SOS,
            // otherwise return exception response
            for (AbstractSosObservation aso : obsCol.getObservationMembers()) {
                if ( !SosConfigurator.getInstance().getCapsCacheController().getProcedures().containsKey(aso.getProcedureID())) {
                    OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         SosConstants.RegisterSensorParams.SensorDescription.name(),
                                         "Sensor must be registered at SOS at first before inserting observations into SOS!");
                    log.warn(se.getMessage());
                    return new ExceptionResp(se.getDocument());
                }
            }
            
            int observationID = this.insertObsOperationDAO.insertObservation(obsCol, insertObsReq.isMobileEnabled());
 
            // if (insertObsReq.getObservation().size() != 1) {
            // OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            // se.addCodedException(ExceptionCode.InvalidParameterValue,
            // SosConstants.RegisterSensorParams.SensorDescription.name(),
            // "At the moment, just one observation could be inserted!!");
            // log.warn(se.getMessage());
            // return new ExceptionResp(se.getDocument());
            // }
//            int observationID = Integer.MIN_VALUE;
//            for (AbstractSosObservation obs : insertObsReq.getObservation()) {
//                observationID = this.insertObsOperationDAO.insertObservation(obs,
//                                                                             insertObsReq.isMobileEnabled());
//            }
            
            response = new InsertObservationResponse(SosConfigurator.getInstance().getResponseEncoder().createInsertObservationResponse(observationID));
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResp(e.getDocument());
        }
        return response;
    }
}