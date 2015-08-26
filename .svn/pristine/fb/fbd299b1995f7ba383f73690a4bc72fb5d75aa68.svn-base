package org.n52.sos.encode;

import net.opengis.sos.x10.GetResultResponseDocument;
import net.opengis.sos.x10.InsertObservationResponseDocument;
import net.opengis.sos.x10.RegisterSensorResponseDocument;
import net.opengis.sos.x10.UpdateSensorResponseDocument;

import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public interface ISosResponseEncoder {
	
    /**
     * creates UpdateSensorResponse
     * 
     * @return Returns XMLBeans representation of UpdateSensorResponse
     */
    public UpdateSensorResponseDocument createUpdateSensorResponse();
    /**
     * creates RegisterSensorResponse
     * 
     * @param assignedSensorID
     *        id of new registered sensor
     * @return Returns XMLBeans representation of RegisterSensorResponse
     */
    public RegisterSensorResponseDocument createRegisterSensorResponse(String assignedSensorID);

    /**
     * creates InsertObservationRespones XMLBean
     * 
     * @param observation_id
     *        assigned id of observation
     * @return Returns assigned id of observation
     */
    public InsertObservationResponseDocument createInsertObservationResponse(int observation_id);

    /**
     * creates an XmlBeans document representing the GetResultResponse document from the passed generic
     * observation
     * 
     * @param sosGenObs
     * @return XMLBeans representation of GetResultResponse
     * @throws OwsExceptionReport 
     */
    public GetResultResponseDocument createResultRespDoc(SosGenericObservation sosGenObs) throws OwsExceptionReport;

}
