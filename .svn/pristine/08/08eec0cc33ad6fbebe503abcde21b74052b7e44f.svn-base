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

import net.opengis.om.x10.ObservationCollectionDocument;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetObservationDAO;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetObservationRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.n52.sos.resp.ObservationResponse;

/**
 * class parses and validates the GetObservation requests and forwards them to the GetObservationDAO; after
 * query of Database, class encodes the ObservationResponse (thru using the OMEncoder)
 * 
 * @author Christoph Stasch
 * 
 */
public class GetObservationListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetObservationListener.class.getName());

    /** the data access object for the GetObservation operation */
    private IGetObservationDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getObservation.name();

    /**
     * Constructor
     * 
     */
    public GetObservationListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getObservationDAO());
        log.info("GetObservationListener initialized successfully!");
    }

    /**
     * @return Returns the factory.
     */
    public IGetObservationDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IGetObservationDAO the specific DAO for this operation (e.g. PostgreSQLGetObservationDAO)
     */
    public void setDao(IGetObservationDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the GetObservation request and sends back a repsonse
     * 
     * @param request
     *        the XMLObject request (which should be a GetObservationDocument)
     * 
     * @return Returns the GetObservation response
     * 
     */
    public synchronized ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;

        try {

            SosGetObservationRequest sosRequest = (SosGetObservationRequest) request;

            // check parameters with variable content
            Util4Listeners.checkServiceParameter(sosRequest.getService());
            Util4Listeners.checkSingleVersionParameter(sosRequest.getVersion());
            checkOfferingId(sosRequest.getOffering());
            checkObservedProperties(sosRequest.getObservedProperty(), sosRequest.getOffering());
            checkSrsName(sosRequest.getSrsName());

            boolean zipCompression = checkResponseFormat(sosRequest.getResponseFormat());

            boolean mobileEnabled = sosRequest.isMobileEnabled();

            SosObservationCollection obsCollection = new SosObservationCollection();
            ObservationCollectionDocument xb_obsCol;

            if (mobileEnabled) {
                obsCollection = this.dao.getObservationMobile(sosRequest);
                xb_obsCol = SosConfigurator.getInstance().getOmEncoder().createObservationCollectionMobile(obsCollection);
            }
            else {
                obsCollection = this.dao.getObservation(sosRequest);
                xb_obsCol = SosConfigurator.getInstance().getOmEncoder().createObservationCollection(obsCollection);
            }

            response = new ObservationResponse(xb_obsCol, zipCompression);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }

        return response;
    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return GetObservationListener.OPERATION_NAME;
    }

    /**
     * help method to check the result format parameter. If the application/zip result format is set, true is
     * returned. If not and the value is text/xml; subtype="OM" false is returned. If neither zip nor OM is
     * set, a ServiceException with InvalidParameterValue as its code is thrown.
     * 
     * @param responseFormat
     *        String containing the value of the result format parameter
     * @return boolean true if application/zip is the resultFormat value, false if its value is
     *         text/xml;subtype="OM"
     * @throws OwsExceptionReport
     *         if the parameter value is incorrect
     */
    private boolean checkResponseFormat(String responseFormat) throws OwsExceptionReport {
        boolean isZipCompr = false;
        if (responseFormat.equalsIgnoreCase(SosConstants.CONTENT_TYPE_OM)) {
            return isZipCompr;
        }

        else if (responseFormat.equalsIgnoreCase(SosConstants.CONTENT_TYPE_ZIP)) {
            isZipCompr = true;
            return isZipCompr;
        }

        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.responseFormat.toString(),
                                 "The value of the parameter '"
                                         + GetObservationParams.responseFormat.toString() + "'"
                                         + "must be '" + SosConstants.CONTENT_TYPE_OM + " or "
                                         + SosConstants.CONTENT_TYPE_ZIP
                                         + "'. Delivered value was: " + responseFormat);
            log.error("The responseFormat parameter is incorrect!", se);
            throw se;
        }
    }

    /**
     * checks if mandatory parameter observed property is correct
     * 
     * @param properties
     *        String[] containing the observed properties of the request
     * @param offering
     *        the requested offeringID
     * @throws OwsExceptionReport
     *         if the parameter does not containing any matching observedProperty for the requested offering
     */
    private void checkObservedProperties(String[] properties, String offering) throws OwsExceptionReport {
        List<String> obsProps = SosConfigurator.getInstance().getCapsCacheController().getAllPhenomenons4Offering(offering);

        // check single phenomena
        if (obsProps != null) {
            for (int i = 0; i < obsProps.size(); i++) {
                for (int j = 0; j < properties.length; j++) {
                    if (obsProps.get(i).equals(properties[j])) {
                        return;
                    }
                }
            }
        }

        // check composite phenomena
        obsProps = SosConfigurator.getInstance().getCapsCacheController().getOffCompPhens().get(offering);
        if (obsProps != null) {
            for (int i = 0; i < obsProps.size(); i++) {
                for (int j = 0; j < properties.length; j++) {
                    if (obsProps.get(i).equals(properties[j])) {
                        return;
                    }
                }
            }
        }

        OwsExceptionReport se = new OwsExceptionReport();
        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                             SosConstants.GetObservationParams.observedProperty.name(),
                             "No matching observedProperty was found in the request for the requested offering!");
        log.error("The observedProperty parameter is incorrect!", se);
        throw se;
    }

    /**
     * chechs if the passed offeringId is supported
     * 
     * @param offeringId
     *        the offeringId to be checked
     * @throws OwsExceptionReport
     *         if the passed offeringId is not supported
     */
    private void checkOfferingId(String offeringId) throws OwsExceptionReport {
        List<String> offerings = SosConfigurator.getInstance().getCapsCacheController().getOfferings();
        if ( !offerings.contains(offeringId)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.offering.toString(),
                                 "The value (" + offeringId + ") of the parameter '"
                                         + GetObservationParams.offering.toString()
                                         + "' is invalid");
            log.error("The Offering ID parameter is incorrect!", se);
            throw se;
        }
    }

    /**
     * 
     * 
     * 
     * @throws OwsExceptionReport
     */
    private void checkSrsName(String srsName) throws OwsExceptionReport {
        Integer srid = Integer.MIN_VALUE;
        String srsNamePrefix = SosConfigurator.getInstance().getSrsNamePrefix();
        if ( !srsName.equalsIgnoreCase("NOT_SET")) {
            try {
                srid = new Integer(srsName.replace(srsNamePrefix, ""));
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     SosConstants.GetObservationParams.srsName.name(),
                                     "Error while parsing srsName parameter! Parameter has to match pattern '"
                                             + srsNamePrefix
                                             + "' with appended EPSGcode number");
                throw se;
            }
            if ( !SosConfigurator.getInstance().getCapsCacheController().getSrids().contains(srid)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     SosConstants.GetObservationParams.srsName.name(),
                                     "The epsgCode '" + srid + "' for parameter "
                                             + SosConstants.GetObservationParams.srsName.toString()
                                             + "'is not supported by the SOS");
                throw se;
            }
        }
    }
}
