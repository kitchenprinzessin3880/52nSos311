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

import net.opengis.gml.FeatureDocument;

import org.apache.log4j.Logger;
import org.n52.sos.SosConstants.GetFeatureOfInterestParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetFeatureOfInterestDAO;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetFeatureOfInterestRequest;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.FeatureOfInterestResponse;
import org.n52.sos.resp.ISosResponse;

/**
 * class handles the GetFeatureOfInterest request
 * 
 * @author Stephan Knster
 * 
 */
public class GetFeatureOfInterestListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetFeatureOfInterestListener.class);

    /** the data access object for the GetFeatureOfInterest operation */
    private IGetFeatureOfInterestDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getFeatureOfInterest.name();

    /**
     * Constructor
     * 
     */
    public GetFeatureOfInterestListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getFeatureOfInterestDAO());
        log.info("GetFeatureOfInterestListener intialized successfully!!");
    }

    /**
     * @return Returns the factory.
     */
    public IGetFeatureOfInterestDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IGetFeatureOfInterestDAO the specific DAO for this operation
     */
    public void setDao(IGetFeatureOfInterestDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the FeatureOfInterest request and sends back a response
     * 
     * @param request
     *        the AbstractSosRequest request
     * 
     * @return Returns the FeatureOfInterest response
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;

        SosGetFeatureOfInterestRequest foiRequest = (SosGetFeatureOfInterestRequest) request;

        try {

            if (foiRequest.getFeatureIDs() != null && foiRequest.getFeatureIDs().length != 0) {
                // check featureOfInterestIDs
                for (String id : foiRequest.getFeatureIDs()) {
                    checkFeatureOfInterestID(id);
                }
            }

            // get abstract feature
            SosAbstractFeature absFoi = dao.getFeatureOfInterest(foiRequest);

            FeatureDocument xb_featDoc = SosConfigurator.getInstance().getGmlEncoder().createFeature(absFoi);

            // create feature response
            response = new FeatureOfInterestResponse(xb_featDoc);

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
                                 GetFeatureOfInterestParams.featureOfInterestID.toString(),
                                 "The value ("
                                         + featureOfInterestID
                                         + ") of the parameter '"
                                         + GetFeatureOfInterestParams.featureOfInterestID.toString()
                                         + "' is invalid");
            log.error("The value of the parameter 'featureOfInterestID' is invalid!", se);
            throw se;
        }
    }

    /**
     * 
     * @return Returns the name of the implemented operation
     */
    public String getOperationName() {
        return GetFeatureOfInterestListener.OPERATION_NAME;
    }

}
