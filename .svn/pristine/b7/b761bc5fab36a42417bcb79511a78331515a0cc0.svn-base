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

package org.n52.sos.encode.impl;

import net.opengis.sos.x10.GetResultResponseDocument;
import net.opengis.sos.x10.InsertObservationResponseDocument;
import net.opengis.sos.x10.RegisterSensorResponseDocument;
import net.opengis.sos.x10.UpdateSensorResponseDocument;
import net.opengis.sos.x10.UpdateSensorResponseType;
import net.opengis.sos.x10.GetResultResponseDocument.GetResultResponse;
import net.opengis.sos.x10.GetResultResponseDocument.GetResultResponse.Result;
import net.opengis.sos.x10.InsertObservationResponseDocument.InsertObservationResponse;
import net.opengis.sos.x10.RegisterSensorResponseDocument.RegisterSensorResponse;
import net.opengis.sos.x10.UpdateSensorResponseDocument.UpdateSensorResponse;

import org.n52.sos.SosConstants;
import org.n52.sos.encode.ISosResponseEncoder;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * class encapsulates operations for creating response documents for SOSmobile operations
 * 
 * @author Christoph Stasch
 * 
 */
public class SosResponseEncoder implements ISosResponseEncoder {

    /**
     * creates UpdateSensorResponse
     * 
     * @return Returns XMLBeans representation of UpdateSensorResponse
     */
    public UpdateSensorResponseDocument createUpdateSensorResponse() {
        UpdateSensorResponseDocument xb_respDoc = UpdateSensorResponseDocument.Factory.newInstance();
        UpdateSensorResponse xb_resp = xb_respDoc.addNewUpdateSensorResponse();
        xb_resp.setStatus(UpdateSensorResponseType.SENSOR_UPDATED);
        return xb_respDoc;
    }

    /**
     * creates RegisterSensorResponse
     * 
     * @param assignedSensorID
     *        id of new registered sensor
     * @return Returns XMLBeans representation of RegisterSensorResponse
     */
    public RegisterSensorResponseDocument createRegisterSensorResponse(String assignedSensorID) {
        RegisterSensorResponseDocument xb_respDoc = RegisterSensorResponseDocument.Factory.newInstance();
        RegisterSensorResponse xb_resp = xb_respDoc.addNewRegisterSensorResponse();
        xb_resp.setAssignedSensorId(assignedSensorID);
        return xb_respDoc;
    }

    /**
     * creates InsertObservationRespones XMLBean
     * 
     * @param observation_id
     *        assigned id of observation
     * @return Returns assigned id of observation
     */
    public InsertObservationResponseDocument createInsertObservationResponse(int observation_id) {
        InsertObservationResponseDocument xb_insObsDoc = InsertObservationResponseDocument.Factory.newInstance();
        InsertObservationResponse xb_insObsResp = xb_insObsDoc.addNewInsertObservationResponse();
        xb_insObsResp.setAssignedObservationId(SosConstants.OBS_ID_PREFIX + observation_id);
        return xb_insObsDoc;
    }

    /**
     * creates an XmlBeans document representing the GetResultResponse document from the passed generic
     * observation
     * 
     * @param sosGenObs
     * @return XmlBeans representation of the GetResultResponse document
     * @throws OwsExceptionReport 
     */
    public GetResultResponseDocument createResultRespDoc(SosGenericObservation sosGenObs) throws OwsExceptionReport {
        GetResultResponseDocument xb_resRespDoc = GetResultResponseDocument.Factory.newInstance();
        GetResultResponse xb_resResp = xb_resRespDoc.addNewGetResultResponse();
        Result xb_result = xb_resResp.addNewResult();
        xb_result.setRS(SosConstants.OBS_TEMP_ID_PREFIX + sosGenObs.getObservationID());
        xb_result.setStringValue(sosGenObs.createResultString());
        return xb_resRespDoc;
    }
}
