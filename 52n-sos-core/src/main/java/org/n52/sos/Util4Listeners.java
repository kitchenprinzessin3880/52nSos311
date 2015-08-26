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

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.GetCapabilitiesParams;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.Util4Exceptions;

/**
 * Class encapsulates operations for checking, whether mandatory parameters for all operations are corrected
 * 
 * @author Christoph Stasch
 * 
 */
public class Util4Listeners {

    /** logger */
    private static Logger log = Logger.getLogger(Util4Listeners.class);

    /**
     * method checks whether this SOS supports the requested versions
     * 
     * @param versions
     *        the requested versions of the SOS
     * 
     * @throws OwsExceptionReport
     *         if this SOS does not support the requested versions
     */
    public static void checkAcceptedVersionsParameter(String[] versions) throws OwsExceptionReport {

        if (versions != null) {
            String serviceVersion = SosConstants.SERVICEVERSION;

            for (int i = 0; i < versions.length; ++i) {
                if (versions[i].equals(serviceVersion)) {
                    return;
                }
            }

            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.VersionNegotiationFailed,
                                 GetCapabilitiesParams.AcceptVersions.name(),
                                 "The parameter '" + GetCapabilitiesParams.AcceptVersions.name()
                                         + "'" + " does not contain the version of this SOS: '"
                                         + serviceVersion + "'");
            log.error("The accepted versions parameter is incorrect.", se);
            throw se;
        }
        OwsExceptionReport se = Util4Exceptions.createMissingParameterException(GetCapabilitiesParams.AcceptVersions.name());
        log.error(se);
        throw se;
    }

    /**
     * method checks whether this SOS supports the single requested version
     * 
     * @param version
     *        the requested version of the SOS
     * @throws OwsExceptionReport
     *         if this SOS does not support the requested versions
     */
    public static void checkSingleVersionParameter(String version) throws OwsExceptionReport {

        // if version is incorrect, throw exception
        if (version == null || !version.equalsIgnoreCase(SosConstants.SERVICEVERSION)) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetCapabilitiesParams.AcceptVersions.name(),
                                 "The parameter '" + GetCapabilitiesParams.AcceptVersions.name()
                                         + "'" + " does not contain the version of this SOS: '"
                                         + SosConstants.SERVICEVERSION + "'");
            log.error("The accepted versions parameter is incorrect.", se);
            throw se;
        }
    }

    /**
     * method checks, whether the passed string containing the requested versions of the SOS contains the
     * versions, the 52n SOS supports
     * 
     * @param versionsString
     *        comma seperated list of requested service versions
     * @throws OwsExceptionReport
     *         if the versions list is empty or no matching version is contained
     */
    public static void checkAcceptedVersionsParameter(String versionsString) throws OwsExceptionReport {
        // check acceptVersions
        if (versionsString != null && !versionsString.equals("")) {
            String[] versionsArray = versionsString.split(",");
            checkAcceptedVersionsParameter(versionsArray);
        }

        else {
            OwsExceptionReport se = Util4Exceptions.createMissingParameterException(GetCapabilitiesParams.AcceptVersions.name());
            log.error(se);
            throw se;
        }
    }

    /**
     * checks whether the required service parameter is correct
     * 
     * @param service
     *        service parameter of the request
     * @throws OwsExceptionReport
     *         if service parameter is incorrect
     */
    public static void checkServiceParameter(String service) throws OwsExceptionReport {

        // if service==null, throw exception with missing parameter value code
        if (service == null || service.equalsIgnoreCase("NOT_SET")) {
            OwsExceptionReport se = Util4Exceptions.createMissingParameterException(SosConstants.GetCapabilitiesParams.service.name());
            log.error(se);
            throw se;
        }

        // if not null, but incorrect, throw also exception
        else if ( !service.equals(SosConstants.SOS)) {
            log.error("Service parameter is wrong!");
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetCapabilitiesParams.service.toString(),
                                 "The value of the mandatory parameter '"
                                         + GetCapabilitiesParams.service.toString() + "' "
                                         + "must be '" + SosConstants.SOS
                                         + "'. Delivered value was: " + service);
            throw se;
        }
    }

    /**
     * help method to check the result format parameter. If the application/zip result format is set, true is
     * returned. If not and the value is text/xml; subtype="OM" false is returned. If neither zip nor OM is
     * set, a ServiceException with InvalidParameterValue as its code is thrown.
     * 
     * @param resultFormat
     *        String containing the value of the result format parameter
     * @return boolean true if application/zip is the resultFormat value, false if its value is
     *         text/xml;subtype="OM"
     * @throws OwsExceptionReport
     *         if the parameter value is incorrect
     */
    public static boolean checkResponseFormat(String resultFormat) throws OwsExceptionReport {
        boolean isZipCompr = false;
        if (resultFormat.equalsIgnoreCase(SosConstants.CONTENT_TYPE_OM)) {
            return isZipCompr;
        }

        else if (resultFormat.equalsIgnoreCase(SosConstants.CONTENT_TYPE_ZIP)) {
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
                                         + "'. Delivered value was: " + resultFormat);
            log.error("The resultFormat parameter is incorrect!", se);
            throw se;
        }
    }

}