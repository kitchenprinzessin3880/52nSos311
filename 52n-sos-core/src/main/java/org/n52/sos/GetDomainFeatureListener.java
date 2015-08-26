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
import org.n52.sos.SosConstants.GetDomainFeatureParams;
import org.n52.sos.ds.IDAOFactory;
import org.n52.sos.ds.IGetDomainFeatureDAO;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;
import org.n52.sos.request.SosGetDomainFeatureRequest;
import org.n52.sos.resp.DomainFeatureResponse;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;

/**
 * class handles the GetDomainFeature request
 * 
 * @author Stephan Knster
 * 
 */
public class GetDomainFeatureListener implements ISosRequestListener {

    /** logger */
    private static final Logger log = Logger.getLogger(GetDomainFeatureListener.class);

    /** the data access object for the GetFeatureOfInterest operation */
    private IGetDomainFeatureDAO dao;

    /** Name of the operation the listener implements */
    private static final String OPERATION_NAME = SosConstants.Operations.getDomainFeature.name();

    /**
     * Constructor
     * 
     */
    public GetDomainFeatureListener() {
        SosConfigurator configurator = SosConfigurator.getInstance();
        IDAOFactory factory = configurator.getFactory();
        setDao(factory.getDomainFeatureDAO());
        log.info("GetDomainFeatureListener intialized successfully!!");
    }

    /**
     * @return Returns the factory.
     */
    public IGetDomainFeatureDAO getDao() {
        return dao;
    }

    /**
     * @param dao
     *        IGetDomainFeatureDAO the specific DAO for this operation
     */
    public void setDao(IGetDomainFeatureDAO dao) {
        this.dao = dao;
    }

    /**
     * method receives the DomainFeature request and sends back a response
     * 
     * @param request
     *        the AbstractSosRequest request
     * 
     * @return Returns the DomainFeature response
     * 
     */
    public ISosResponse receiveRequest(AbstractSosRequest request) {

        ISosResponse response = null;

        SosGetDomainFeatureRequest dfRequest = (SosGetDomainFeatureRequest) request;

        try {

            if (dfRequest.getDomainFeatureIDs() != null
                    && dfRequest.getDomainFeatureIDs().length != 0) {
                // check domainFeatureIDs
                for (String id : dfRequest.getDomainFeatureIDs()) {
                    checkDomainFeatureID(id);
                }
            }

            // get abstract feature
            SosAbstractFeature absDf = dao.getDomainFeature(dfRequest);

            // create document
            FeatureDocument xb_df = SosConfigurator.getInstance().getGmlEncoder().createFeature(absDf);

            // create feature response
            response = new DomainFeatureResponse(xb_df);

        }
        catch (OwsExceptionReport se) {
            return new ExceptionResp(se.getDocument());
        }
        return response;
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
        return GetDomainFeatureListener.OPERATION_NAME;
    }

}
