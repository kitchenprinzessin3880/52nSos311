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
import java.util.List;
import java.util.Map;

import net.opengis.gml.TimePeriodDocument;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.GetDomainFeatureParams;
import org.n52.sos.SosConstants.GetFeatureOfInterestTimeParams;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetFeatureOfInterestTimeDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorSystem;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetFeatureOfInterestTimeRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.FeatureOfInterestTimeResponse;
import org.n52.sos.resp.ISosResponse;

/**
 * class receives 
 * 
 * @author Christoph Stasch
 *
 */
public class GetFeatureOfInterestTimeListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetFeatureOfInterestTimeListener.class);

    /** the data access object for the GetFeatureOfInterestTime operation */
    private IGetFeatureOfInterestTimeDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getFeatureOfInterestTime.name();

    /**
     * Constructor
     * 
     */
    public GetFeatureOfInterestTimeListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getFeatureOfInterestTimeDAO());
        log.info("GetFeatureOfInterestTimeListener intialized successfully!!");
    }

    /**
     * @return Returns the factory.
     */
    public IGetFeatureOfInterestTimeDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IGetFeatureOfInterestTimeDAO the specific DAO for this operation
     */
    public void setDao(IGetFeatureOfInterestTimeDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the FeatureOfInterestTime request and sends back a response
     * 
     * @param request
     *        the AbstractSosRequest request
     * 
     * @return Returns the FeatureOfInterest response
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;

        try {

            SosGetFeatureOfInterestTimeRequest sosRequest = (SosGetFeatureOfInterestTimeRequest) request;

            // check parameters with variable content
            Util4Listeners.checkServiceParameter(sosRequest.getService());
            Util4Listeners.checkSingleVersionParameter(sosRequest.getVersion());

            // check offeringID
            if (sosRequest.getOffering() != null && sosRequest.getOffering() != "") {
                checkOfferingId(sosRequest.getOffering());
            }

            // check domain feature ids
            if (sosRequest.getDomainFeature() != null) {
                if (sosRequest.getDomainFeature().getObjectIDArray() != null
                        && sosRequest.getDomainFeature().getObjectIDArray().length != 0) {
                    for (int i = 0; i < sosRequest.getDomainFeature().getObjectIDArray().length; i++) {
                        checkDomainFeatureID(sosRequest.getDomainFeature().getObjectIDArray(i));
                    }
                }
            }

            // check featureID
            if (sosRequest.getFeatureID() != null && sosRequest.getFeatureID() != "") {
                checkFeatureOfInterestID(sosRequest.getFeatureID());
            }

            // check procedures
            if (sosRequest.getProcedure() != null && sosRequest.getProcedure().length != 0) {
                checkProcedure(sosRequest.getProcedure());
            }

            // check observedProperties
            if (sosRequest.getPhenomenon() != null && sosRequest.getPhenomenon().length != 0) {
                checkOberservedProperty(sosRequest.getPhenomenon());
            }

            TimePeriodDocument timeDoc = this.dao.getFeatureOfInterestTime(sosRequest);

            response = new FeatureOfInterestTimeResponse(timeDoc);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }

        return response;
    }

    /**
     * checks if the passed checkFeatureOfInterestID is supported
     * 
     * @param featureOfInterestID
     *        the featureOfInterestID to be checked
     * @throws OwsExceptionReport
     *         if the passed featureOfInterestID is not supported
     */
    private void checkFeatureOfInterestID(String featureOfInterestID) throws OwsExceptionReport {
        List<String> fois = SosConfigurator.getInstance().getCapsCacheController().getFois();
        if ( !fois.contains(featureOfInterestID)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetFeatureOfInterestTimeParams.featureOfInterestID.toString(),
                                 "The value '"
                                         + featureOfInterestID
                                         + "' of the parameter '"
                                         + GetFeatureOfInterestTimeParams.featureOfInterestID.toString()
                                         + "' is invalid!");
            log.error("The value of the parameter 'featureOfInterestID' is invalid!", se);
            throw se;
        }
    }

    /**
     * checks if the passed oberservedProperty is supported
     * 
     * @param oberservedProperty
     *        the oberservedProperty to be checked
     * @throws OwsExceptionReport
     *         if the passed oberservedProperty is not supported
     */
    private void checkOberservedProperty(String[] phenomena) throws OwsExceptionReport {
        List<String> phens = SosConfigurator.getInstance().getCapsCacheController().getAllPhenomenons();
        for (int i = 0; i < phenomena.length; i++) {
            if ( !phens.contains(phenomena[i])) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetFeatureOfInterestTimeParams.observedProperty.toString(),
                                     "The value '"
                                             + phenomena[i]
                                             + "' of the parameter '"
                                             + GetFeatureOfInterestTimeParams.observedProperty.toString()
                                             + "' is invalid!");
                log.error("The value of the parameter 'observedProperty' is invalid!", se);
                throw se;
            }
        }
    }

    /**
     * checks if the passed Procedure is supported
     * 
     * @param Procedure
     *        the Procedure to be checked
     * @throws OwsExceptionReport
     *         if the passed Procedure is not supported
     */
    @SuppressWarnings("unchecked")
    private void checkProcedure(String[] procedures) throws OwsExceptionReport {
        Map<String, SensorSystem> procs = SosConfigurator.getInstance().getCapsCacheController().getProcedures();

        List<String> procIds = new ArrayList<String>();

        Iterator< ? > iterator = procs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            procIds.add((String) entry.getKey());
        }

        for (int i = 0; i < procedures.length; i++) {
            if ( !procIds.contains(procedures[i])) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetFeatureOfInterestTimeParams.procedure.toString(),
                                     "The value '" + procedures[i] + "' of the parameter '"
                                             + GetFeatureOfInterestTimeParams.procedure.toString()
                                             + "' is invalid!");
                log.error("The value of the parameter 'procedure' is invalid!", se);
                throw se;
            }
        }
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
                                 "The value '" + offeringId + "' of the parameter '"
                                         + GetObservationParams.offering.toString()
                                         + "' is invalid");
            log.error("The Offering ID parameter is incorrect!", se);
            throw se;
        }
    }

    /**
     * checks if the passed domainFeatureID is supported
     * 
     * @param domainFeatureID
     *        the domainFeatureID to be checked
     * @throws OwsExceptionReport
     *         if the passed domainFeatureID is not supported
     */
    private void checkDomainFeatureID(String domainFeatureID) throws OwsExceptionReport {
        List<String> dfs = SosConfigurator.getInstance().getCapsCacheController().getDomainFeatures();
        if ( !dfs.contains(domainFeatureID)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetDomainFeatureParams.domainFeatureID.toString(),
                                 "The value '" + domainFeatureID + "' of the parameter '"
                                         + GetDomainFeatureParams.domainFeatureID.toString()
                                         + "' is invalid");
            log.error("The value of the parameter 'domainFeatureID' is invalid!", se);
            throw se;
        }
    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return GetFeatureOfInterestTimeListener.OPERATION_NAME;
    }

}
