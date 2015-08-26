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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import net.opengis.sos.x10.DescribeSensorDocument;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConstants.DescribeSensorParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IDescribeSensorDAO;
import org.n52.sos.encode.ISensorMLEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.sensorML.ProcedureHistory;
import org.n52.sos.ogc.sensorML.SensorSystem;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosDescribeSensorRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.SensorDocument;
import org.n52.sos.resp.SensorResponse;

import com.vividsolutions.jts.geom.Point;

/**
 * class handles the DescribeSensor request
 * 
 * @author Christoph Stasch
 * 
 */
public class DescribeSensorListener implements ISosRequestListener {

    /** the data access object for the DescribeSensor operation */
    private IDescribeSensorDAO dao;

    /** SensorML Encoder */
    private ISensorMLEncoder smlEncoder;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.describeSensor.name();

    /** logger */
    private static Logger log = Logger.getLogger(DescribeSensorListener.class);

    /**
     * Constructor
     * 
     */
    public DescribeSensorListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getDescribeSensorDAO());
        smlEncoder = SosConfigurator.getInstance().getSensorMLEncoder();
        log.info("DescribeSensorListener initialized successfully!");
    }

    /**
     * @return Returns the factory.
     */
    public IDescribeSensorDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IDescribeSensorDAO
     */
    public void setDao(IDescribeSensorDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the DescribeSensor request and sends back a repsonse
     * 
     * @param request
     *        the DescribeSensor request
     * 
     * @return Returns the DescribeSensor response
     * 
     */
    public synchronized ISosResponse receiveRequest(AbstractSosRequest request) {

        boolean applyZIPcomp = false;

        SosDescribeSensorRequest descSensRequest = (SosDescribeSensorRequest) request;
        String sensorID = descSensRequest.getProcedure();

        // null or an empty String
        if (sensorID == null || sensorID.equals("")) {
            log.error("The required sensor id parameter is missing!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.MissingParameterValue,
                                 DescribeSensorParams.procedure.toString(),
                                 "The value of the mandatory parameter '"
                                         + DescribeSensorParams.procedure.toString()
                                         + "' was not found in the request or is incorrect!");
            return new ExceptionResp(se.getDocument());
        }
        try {
            checkSensorID(sensorID);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }

        SensorDocument systemDoc = null;

        try {
            // checking optional attribute paramter outputFormat
            String outputFormat = descSensRequest.getOutputFormat();

            if (outputFormat == SosConstants.PARAMETER_NOT_SET) {
                log.error("The required outputFormat parameter is missing!");
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.MissingParameterValue,
                                     DescribeSensorParams.procedure.toString(),
                                     "The value of the mandatory parameter '"
                                             + DescribeSensorParams.outputFormat.toString()
                                             + "' was not found in the request or is incorrect!");
                return new ExceptionResp(se.getDocument());
            }

            try {
                checkOutputFormat(outputFormat);
            }
            catch (OwsExceptionReport se) {
                return new ExceptionResp(se.getDocument());
            }

            if (outputFormat.equals(SosConstants.CONTENT_TYPE_ZIP)) {
                applyZIPcomp = true;
            }

            // check whether request is mobile enabled
            if (descSensRequest.isMobileEnabled()) {

                // if no Temporal Filter is set
                if (descSensRequest.getTime() == null) {

                    // return SML Document without procedure history
                    String smlDesc = this.dao.getSensorDescription(descSensRequest);
                    Point actualPosition = this.dao.getActualPosition(descSensRequest);
                    systemDoc = smlEncoder.createSensor(smlDesc, null, actualPosition);

                }
                else {

                    // return SML Document with procedure history
                    String smlDesc = this.dao.getSensorDescription(descSensRequest);
                    ProcedureHistory procHistory = this.dao.getProcedureHistory(descSensRequest);
                    Point actualPosition = this.dao.getActualPosition(descSensRequest);
                    systemDoc = smlEncoder.createSensor(smlDesc, procHistory, actualPosition);
                }

            }
            else {
                if (descSensRequest.getTime() != null) {
                    log.error("A temporal filter is not allowed in a non-mobile describe sensor request!");
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         DescribeSensorParams.time.toString(),
                                         "A temporal filter is not allowed in a non-mobile describe sensor request!");
                    return new ExceptionResp(se.getDocument());
                }
                systemDoc = this.dao.getSensorDocument(descSensRequest);
            }

        }
        catch (OwsExceptionReport e) {
            return new ExceptionResp(e.getDocument());
        }

        return new SensorResponse(systemDoc, applyZIPcomp);

    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return DescribeSensorListener.OPERATION_NAME;
    }

    /**
     * checks whether the describeSensorRequest XML document is valid
     * 
     * @param doc
     *        the document which should be checked
     * 
     * @throws OwsExceptionReport
     *         if the Document is not valid
     */
    @SuppressWarnings("unused")
    private void validateDescSensorDoc(DescribeSensorDocument doc) throws OwsExceptionReport {

        // Create an XmlOptions instance and set the error listener.
        ArrayList<XmlError> validationErrors = new ArrayList<XmlError>();
        XmlOptions validationOptions = new XmlOptions();
        validationOptions.setErrorListener(validationErrors);

        // Validate the GetCapabilitiesRequest XML document
        boolean isValid = doc.validate(validationOptions);

        // Create Exception with error messages if the xml document is invalid
        if ( !isValid) {
            String message = null;
            String parameterName = null;

            // getValidation error and throw service exception for the first
            // error
            Iterator<XmlError> iter = validationErrors.iterator();
            while (iter.hasNext()) {

                // get name of the missing or invalid parameter
                message = iter.next().getMessage();
                if (message != null) {
                    String[] messageParts = message.split(" ");

                    if (messageParts.length > 3) {
                        parameterName = messageParts[2];
                    }

                    log.error("Request is not valid!");
                    // create service exception
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.MissingParameterValue,
                                         parameterName,
                                         "[XmlBeans validation error:] " + message);
                    throw se;
                }

            }
        }
    }

    /**
     * checks whether the requested sensor ID is valid
     * 
     * @param sensorID
     *        the sensor ID which should be checked
     * @throws OwsExceptionReport
     *         if the value of the sensor ID parameter is incorrect
     */
    private void checkSensorID(String sensorID) throws OwsExceptionReport {
        Map<String, SensorSystem> procs = SosConfigurator.getInstance().getCapsCacheController().getProcedures(); // CapabilitiesCache.getInstance().getProcedures();

        boolean isContained = false;

        if (procs.containsKey(sensorID)) {
            isContained = true;
        }

        if (isContained == false) {
            log.error("Requested sensor is not registered at this SOS!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.DescribeSensorParams.procedure.name(),
                                 "The value of the sensorID parameter is incorrect. Please check the capabilities response document for valid sensor Ids!");
            throw se;
        }

    }

    /**
     * checks whether the value of outputFormat parameter is valid
     * 
     * @param outputFormat
     *            the outputFormat parameter which should be checked
     * @throws OwsExceptionReport
     *             if the value of the outputFormat parameter is incorrect
     */
    private void checkOutputFormat(String outputFormat) throws OwsExceptionReport {
        if ( !outputFormat.equals(SosConstants.SENSORML_OUTPUT_FORMAT)) {
            log.error("outputFormat: '" + outputFormat + "' is not supported by this SOS!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.DescribeSensorParams.outputFormat.name(),
                                 "The value '"
                                         + outputFormat
                                         + "' of the outputFormat parameter is incorrect and has to be '"
                                         + SosConstants.SENSORML_OUTPUT_FORMAT
                                         + "' for the requested sensor!");
            throw se;
        }
    }
}
