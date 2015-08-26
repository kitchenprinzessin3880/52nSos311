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

import net.opengis.ows.x11.CapabilitiesBaseType;
import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.CapabilitiesDocument.Capabilities;
import net.opengis.sos.x10.ContentsDocument.Contents;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.CapabilitiesSections;
import org.n52.sos.SosConstants.GetCapabilitiesParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetCapabilitiesDAO;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetCapabilitiesRequest;
import org.n52.sos.resp.CapabilitiesResponse;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;

/**
 * class handles the GetCapabilities request
 * 
 * @author Christoph Stasch
 * 
 */
public class GetCapabilitiesListener implements ISosRequestListener {

    /** the data access object for the getCapabilities operation */
    private IGetCapabilitiesDAO capdao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getCapabilities.name();


    /** the logger, used to log exceptions and additonaly information */
    private static Logger log = Logger.getLogger(GetCapabilitiesListener.class);

    /**
     * Constructor
     * 
     * @throws OwsExceptionReport
     * 
     */
    public GetCapabilitiesListener() throws OwsExceptionReport {

        // get sos configurator
        SosConfigurator configurator = SosConfigurator.getInstance();

        // setting up DAOFactory
        IDAOFactory factory = configurator.getFactory();
        IGetCapabilitiesDAO capDao = null;

        try {
            capDao = factory.getCapabilitiesDAO();
        }

        catch (OwsExceptionReport se) {
            log.error("Error while creating the getCapabilitiesDAO", se);
            throw se;
        }
        setDao(capDao);
        log.info("GetCapabilitiesListener initialized successfully!");
    }

    /**
     * method receives the getCapabilities request and sends back a repsonse
     * 
     * @param request
     *        the getCapabilities request
     * 
     * @return Returns a capabilities response or an exception response if the getCapabilities operation
     *         failed
     */
    public synchronized ISosResponse receiveRequest(AbstractSosRequest request) {

        try {

            // getting SosGetCapabilitiesRequest which contains the elements of the request
            SosGetCapabilitiesRequest sosRequest = (SosGetCapabilitiesRequest) request;

            String service = sosRequest.getService();
            String[] acceptVersions = sosRequest.getAcceptVersions();

            Util4Listeners.checkServiceParameter(service);
            if (acceptVersions != null) {
                Util4Listeners.checkAcceptedVersionsParameter(acceptVersions);
            }

            boolean mobileEnabled = sosRequest.isMobileEnabled();

            /*
             * getting parameter acceptFormats (optional) boolean zipCompr shows whether the response format
             * should be zip (true) or xml (false)
             */
            boolean zipCompr = false;
            String[] acceptFormats = sosRequest.getAcceptFormats();
            if (acceptFormats != null) {
                try {
                    zipCompr = checkAcceptFormats(acceptFormats);
                }
                catch (OwsExceptionReport se) {
                    return new ExceptionResp(se.getDocument());
                }
            }

         // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
            /*
             * checking parameter updateSequence (optional) the updateSequence value should be a TimeStamp in
             * GML format and is for this capabilities stored in the capabilities skeleton file
             */
//            String updateSequenceString = sosRequest.getUpdateSequence();

            // boolean indicates if the updateSequence parameter value equals the
            boolean updateSequence = false;
//            if (sosRequest.getUpdateSequence() != null
//                    && !sosRequest.getUpdateSequence().equals("")
//                    && !sosRequest.getUpdateSequence().equals("NOT_SET")) {
//                try {
//                    updateSequence = checkUpdateSequence(updateSequenceString);
//                }
//                catch (OwsExceptionReport se) {
//                    return new ExceptionResp(se.getDocument());
//                }
//            }
//
//            // if updateSequence parameter set and correct, set updateSequence in
//            // capabilities document
//            if (updateSequence == true) {
//                CapabilitiesDocument capsd = CapabilitiesDocument.Factory.newInstance();
//                Capabilities caps = capsd.addNewCapabilities();
//
//                // get Elements from skeleton file
//                try {
//                    CapabilitiesDocument capDoc;
//                    if (mobileEnabled) {
//                        capDoc = this.capdao.loadCapabilitiesSkeletonMobile();
//                    }
//                    else {
//                        capDoc = this.capdao.loadCapabilitiesSkeleton();
//                    }
//
//                    CapabilitiesBaseType capBaseType = capDoc.getCapabilities();
//                    caps.setVersion(capBaseType.getVersion());
//
//                    DateTime updateSequenceDate = this.capdao.getUpdateSequence(capDoc);
//                    String skelUpdateSequenceString = SosDateTimeUtilities.formatDateTime2ResponseString(updateSequenceDate);
////                                                                                  skelUpdateSequenceString.length() - 2);
//                    log.info("UpdateSequence: " + skelUpdateSequenceString);
//                    caps.setUpdateSequence(skelUpdateSequenceString);
//
//                    // if updateSequence equals updateSequence in skeleton file, return document with
//                    // only updateSequence and version
//                    if (skelUpdateSequenceString.equals(updateSequenceString)) {
//                        return new CapabilitiesResponse(capsd, zipCompr);
//                    }
//
//                }
//                catch (OwsExceptionReport se) {
//                    return new ExceptionResp(se.getDocument());
//                }
//            }

            // checking parameter sections (optional)
            String[] secType = sosRequest.getSections();
            if (secType != null) {
                return getCapabilities4Sections(secType, zipCompr, mobileEnabled);
            }

            // if sections=null return complete capabilities document
            // return zip compressed response if requested
            if (zipCompr) {
                try {
                    if (mobileEnabled) {
                        return new CapabilitiesResponse(this.capdao.getCompleteCapabilitiesMobile(),
                                                        true);
                    }
                    else {
                        return new CapabilitiesResponse(this.capdao.getCompleteCapabilities(), true);
                    }
                }
                catch (OwsExceptionReport se) {
                    log.error("Error while getting complete capabilities!", se);
                    return new ExceptionResp(se.getDocument());
                }
            }
            try {
                if (mobileEnabled) {
                    return new CapabilitiesResponse(this.capdao.getCompleteCapabilitiesMobile());
                }
                else {
                    return new CapabilitiesResponse(this.capdao.getCompleteCapabilities());
                }
            }
            catch (OwsExceptionReport se) {
                log.error("Error while getting complete capabilities!", se);
                return new ExceptionResp(se.getDocument());
            }

        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }
    }

    /**
     * handles the HttpGetRequest and returns the complete capabilities
     * 
     * @return Returns ISosResponse which is either the CapabilitiesResponse if the operation was successfull
     *         or an ExceptionReponse if an exception occured
     */
    public synchronized ISosResponse receiveGetRequest(SosGetCapabilitiesRequest sosRequest) {

        ISosResponse response = null;

        // getting SosGetCapabilitiesRequest which contains the elements of the request
        try {

            // check mandatory service and accepted version parameter parameter
            Util4Listeners.checkServiceParameter(sosRequest.getService());
            if (sosRequest.getAcceptVersions() != null
                    && !sosRequest.getAcceptVersions().equals("")) {
                Util4Listeners.checkAcceptedVersionsParameter(sosRequest.getAcceptVersions());
            }

            // param request wrong, return exception response!
            if (sosRequest.getRequest() == null
                    || !sosRequest.getRequest().equalsIgnoreCase(SosConstants.Operations.getCapabilities.name())) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     SosConstants.GetCapGetParams.REQUEST.name(),
                                     "The value of mandatory parameter "
                                             + SosConstants.GetCapGetParams.REQUEST.name()
                                             + " has to be: "
                                             + SosConstants.Operations.getCapabilities.name()
                                             + "! Your value was: " + sosRequest.getRequest());
                return new ExceptionResp(se.getDocument());
            }

            boolean mobileEnabled = sosRequest.isMobileEnabled();

            /*
             * getting parameter acceptFormats (optional) boolean zipCompr shows whether the response format
             * should be zip (true) or xml (false)
             */
            boolean zipCompr = false;
            if (sosRequest.getAcceptFormats() != null && !sosRequest.getAcceptFormats().equals("")) {
                try {
                    zipCompr = checkAcceptFormats(sosRequest.getAcceptFormats());
                }
                catch (OwsExceptionReport se) {
                    return new ExceptionResp(se.getDocument());
                }
            }

         // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
            // boolean indicates if the updateSequence parameter value
            // equals the
            boolean updateSequence = false;
//            if (sosRequest.getUpdateSequence() != null
//                    && !sosRequest.getUpdateSequence().equals("")
//                    && !sosRequest.getUpdateSequence().equals("NOT_SET")) {
//                try {
//                    updateSequence = checkUpdateSequence(sosRequest.getUpdateSequence());
//                }
//                catch (OwsExceptionReport se) {
//                    return new ExceptionResp(se.getDocument());
//                }
//            }

            // if updateSequence parameter set and correct, set
            // updateSequence in capabilities document
            if (updateSequence == true) {
                Capabilities caps;

                // get Elements from skeleton file
                CapabilitiesDocument capDoc;
                try {
                    CapabilitiesDocument skeletonCapDoc;
                    if (mobileEnabled) {
                        skeletonCapDoc = this.capdao.loadCapabilitiesSkeletonMobile();
                    }
                    else {
                        skeletonCapDoc = this.capdao.loadCapabilitiesSkeleton();
                    }

                    CapabilitiesBaseType capBaseType = skeletonCapDoc.getCapabilities();

                 // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
//                    DateTime updateSequenceDate = this.capdao.getUpdateSequence(skeletonCapDoc);
//                    String skelUpdateSequenceString = SosDateTimeUtilities.formatDateTime2ResponseString(updateSequenceDate);
//                    // if requested updateSequence equals updateSequence in CapsDoc, return document with
//                    // only updateSequence and version
//                    if (sosRequest.getUpdateSequence().equals(skelUpdateSequenceString)) {
//                        capDoc = CapabilitiesDocument.Factory.newInstance();
//                        caps = capDoc.addNewCapabilities();
//                        caps.setVersion(capBaseType.getVersion());
//                        caps.setUpdateSequence(skelUpdateSequenceString);
//                        return new CapabilitiesResponse(capDoc, zipCompr);
//                    }

                }
                catch (OwsExceptionReport se) {
                    return new ExceptionResp(se.getDocument());
                }
            }

            if (sosRequest.getSections() != null && !sosRequest.getSections().equals("")) {
                return getCapabilities4Sections(sosRequest.getSections(), zipCompr, mobileEnabled);
            }

            // if sections=null return complete capabilities document
            // return zip compressed response if requested
            if (zipCompr) {
                try {
                    if (mobileEnabled) {
                        response = new CapabilitiesResponse(this.capdao.getCompleteCapabilitiesMobile(),
                                                            true);
                    }
                    else {
                        response = new CapabilitiesResponse(this.capdao.getCompleteCapabilities(),
                                                            true);
                    }
                }
                catch (OwsExceptionReport se) {
                    log.error("Error while getting complete capabilities!", se);
                    return new ExceptionResp(se.getDocument());
                }
            }
            else
                try {
                    if (mobileEnabled) {
                        response = new CapabilitiesResponse(this.capdao.getCompleteCapabilitiesMobile());
                    }
                    else {
                        response = new CapabilitiesResponse(this.capdao.getCompleteCapabilities());
                    }
                }
                catch (OwsExceptionReport se) {
                    log.error("Error while getting complete capabilities!", se);
                    return new ExceptionResp(se.getDocument());
                }

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
        return GetCapabilitiesListener.OPERATION_NAME;
    }

    /**
     * method returns a capabilities response with the requested sections
     * 
     * @param sections
     *        String[] containing the requested section names
     * @param zipCompr
     *        boolean indicating, whether the result format should be application zip (true) or text/xml
     *        (false)
     * @return Capabilities response or exception response, if operation failed
     */
    private ISosResponse getCapabilities4Sections(String[] sections,
                                                  boolean zipCompr,
                                                  boolean mobileEnabled) {

        // booleans for sections (true if section is selected
        // explicitly)
        boolean serviceIdentificationSection = false;
        boolean serviceProviderSection = false;
        boolean operationsMetadataSection = false;
        boolean filter_CapabilitiesSection = false;
        boolean contentsSection = false;
        boolean all = false;
        boolean nothing = true;

        // handle sections array and set requested sections 'true'
        for (String s : sections) {

            /*
             * if name of requested section is incorrect (e.g. conten), throw Exception! (Case sensitive!!)
             */
            if ( !CapabilitiesSections.contains(s)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetCapabilitiesParams.Sections.name(),
                                     "The requested section does not exist! "
                                             + " Delivered value was: " + s);
                log.error("The requested section: " + s
                        + " is not part of the capabilities document.", se);
                return new ExceptionResp(se.getDocument());
            }

            // if name is correct, check which section is requested and
            // set boolean on true
            if (s.equals(CapabilitiesSections.All.toString())) {
                all = true;
                nothing = false;
                break;
            }

            else if (s.equals(CapabilitiesSections.ServiceIdentification.toString())) {
                nothing = false;
                serviceIdentificationSection = true;
            }

            else if (s.equals(CapabilitiesSections.ServiceProvider.toString())) {
                nothing = false;
                serviceProviderSection = true;
            }

            else if (s.equals(CapabilitiesSections.OperationsMetadata.toString())) {
                nothing = false;
                operationsMetadataSection = true;
            }

            else if (s.equals(CapabilitiesSections.Filter_Capabilities.toString())) {
                nothing = false;
                filter_CapabilitiesSection = true;
            }

            else if (s.equals(CapabilitiesSections.Contents.toString())) {
                nothing = false;
                contentsSection = true;
            }

        }

        // response with all sections should be created
        if (nothing || all) {
            CapabilitiesDocument capDocResp = null;
            try {
                if (mobileEnabled) {
                    capDocResp = this.capdao.getCompleteCapabilitiesMobile();
                }
                else {
                    capDocResp = this.capdao.getCompleteCapabilities();
                }
            }
            catch (OwsExceptionReport se) {
                return new ExceptionResp(se.getDocument());
            }

            // return zip compressed response if requested
            if (zipCompr) {
                return new CapabilitiesResponse(capDocResp, true);
            }

            // return xml response
            return new CapabilitiesResponse(capDocResp);
        }

        // create CapabilitiesResponse with requested sections
        // create NEW capabilitiesDocument
        CapabilitiesDocument capsd = CapabilitiesDocument.Factory.newInstance();
        Capabilities caps = capsd.addNewCapabilities();

        // get Elements from skeleton file
        CapabilitiesDocument capDoc;
        CapabilitiesBaseType capBaseType;
        try {
            if (mobileEnabled) {
                capDoc = this.capdao.loadCapabilitiesSkeletonMobile();
            }
            else {
                capDoc = this.capdao.loadCapabilitiesSkeleton();
            }

            capBaseType = capDoc.getCapabilities();

            caps.setVersion(capBaseType.getVersion());

         // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
//            DateTime updateSequence = this.capdao.getUpdateSequence(capDoc);
//            caps.setUpdateSequence(SosDateTimeUtilities.formatDateTime2ResponseString(updateSequence));

            if (serviceIdentificationSection) {
                caps.setServiceIdentification(capBaseType.getServiceIdentification());
            }
            if (serviceProviderSection) {
                caps.setServiceProvider(capBaseType.getServiceProvider());
            }
            if (operationsMetadataSection) {
                try {
                    OperationsMetadata xb_meta = caps.addNewOperationsMetadata();

                    if (mobileEnabled) {
                        xb_meta.set(this.capdao.getOperationsMetadataMobile(capDoc));
                    }
                    else {
                        xb_meta.set(this.capdao.getOperationsMetadata(capDoc));
                    }

                }
                catch (OwsExceptionReport se) {
                    log.error("Error while getting operations metadata section!", se);
                    return new ExceptionResp(se.getDocument());
                }
            }

            if (filter_CapabilitiesSection) {
                caps.setFilterCapabilities(capDoc.getCapabilities().getFilterCapabilities());
            }

            if (contentsSection) {
                // getting contents section from capabilitiesDAO
                try {
                    Contents xb_cont = caps.addNewContents();

                    if (mobileEnabled) {
                        xb_cont.set(this.capdao.getContentsMobile());
                    }
                    else {
                        xb_cont.set(this.capdao.getContents());
                    }
                }
                catch (OwsExceptionReport se) {
                    log.error("Error while getting the content section! ", se);
                    return new ExceptionResp(se.getDocument());
                }
            }

            // return zip compressed response if requested
            if (zipCompr) {
                return new CapabilitiesResponse(capsd, true);
            }

            // return xml response
            return new CapabilitiesResponse(capsd);
        }

        catch (OwsExceptionReport se) {
            log.error("Error while getting operations metadata section!", se);
            return new ExceptionResp(se.getDocument());
        }

    }

    /**
     * @return Returns the factory.
     */
    public IGetCapabilitiesDAO getDao() {
        return capdao;
    }

    /**
     * @param dao
     *        IGetCapabilitiesDAO the specific DAO for this operation
     */
    public void setDao(IGetCapabilitiesDAO dao) {
        this.capdao = dao;
    }

    /**
     * sets the requested OutputFormat (only xml and zip are supported)
     * 
     * @param formats
     *        String[] containing the requested formats
     * @return true, if 'application/zip' is the requested responseFormat
     * @throws OwsExceptionReport
     */
    private boolean checkAcceptFormats(String[] formats) throws OwsExceptionReport {
        boolean zipCompr = false;

        // ints are necessary for getting the priority of the ouptuformats
        int xml = -1;
        int zip = -1;
        for (int i = 0; i < formats.length; ++i) {
            if (formats[i].equals(SosConstants.CONTENT_TYPE_XML)) {
                xml = i;
            }
            else if (formats[i].equals(SosConstants.CONTENT_TYPE_ZIP)) {
                zip = i;
            }
        }
        if (zip == -1 && xml == -1) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 null,
                                 "The parameter '" + GetCapabilitiesParams.AcceptFormats.name()
                                         + "'"
                                         + " is invalid. The following values are supported: "
                                         + SosConstants.CONTENT_TYPE_XML + ", "
                                         + SosConstants.CONTENT_TYPE_ZIP);
            log.error("Requested accepted format is not supported by the 52n SOS!", se);
            throw se;
        }

        // if zip is requested testing, whether the priority is bigger than xml
        if (zip != -1 && (zip <= xml || xml == -1)) {
            zipCompr = true;
        }

        return zipCompr;
    }
    
 // TODO not implemented, see org.n52.sos.ds.pgsql.PGSQLGetCapabilitiesDAO.getUpdateSequence(CapabilitiesDocument)
//    /**
//     * checks the update sequence parameter of the request.
//     * 
//     * @param updateSequence
//     *        String containing the updateSequence date in GML format
//     * @return Returns true, if the parameters equals to the updateSequence in the capabilities document
//     * @throws OwsExceptionReport
//     */
//    private boolean checkUpdateSequence(String updateSequence) throws OwsExceptionReport {
//
//        boolean result = false;
//        
//            DateTime usDate = SosDateTimeUtilities.parseIsoString2DateTime(updateSequence);
//            DateTime upDateSequence = this.capdao.getUpdateSequence(this.capdao.loadCapabilitiesSkeleton());
//            if (usDate.isBefore(upDateSequence) || usDate.equals(upDateSequence)) {
//                result = true;
//            }
//
//            else if (usDate.isAfter(upDateSequence)) {
//                OwsExceptionReport se = new OwsExceptionReport();
//                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidUpdateSequence,
//                                     null,
//                                     "The parameter '"
//                                             + GetCapabilitiesParams.updateSequence.name()
//                                             + "'"
//                                             + " is wrong. The Value should be a date in gml-format and could not be after '"
//                                             + SosDateTimeUtilities.formatDateTime2ResponseString(upDateSequence));
//                log.error("The update Sequence parameter is wrong!", se);
//                throw se;
//            }
//        return result;
//    }

}
