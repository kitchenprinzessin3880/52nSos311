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

package org.n52.sos.ds;

import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.SosGetObservationRequest;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the operations
 * (e.g. GetCapabilitiesDAO)
 * 
 * @author Christoph Stasch
 * 
 */
public interface IGetObservationDAO {

    /**
     * process the GetObservation query
     * 
     * @param getObs
     *          GetObservation object which represents the getObservation request
     *   
     * @return ObservationDocument 
     *        representing the requested values in an OGC conform O&M observation document
     *        
     * @throws OwsExceptionReport
     *         if query of the database or creating the O&M document failed
     */
    public SosObservationCollection getObservation(SosGetObservationRequest getObsRequest) throws OwsExceptionReport;

    /**
     * process the GetObservation mobile query
     * 
     * @param getObs
     *          GetObservation object which represents the getObservation request
     *   
     * @return ObservationDocument 
     *        representing the requested values in an OGC conform O&M observation document
     *        
     * @throws OwsExceptionReport
     *         if query of the database or creating the O&M document failed
     */
    public SosObservationCollection getObservationMobile(SosGetObservationRequest getObsRequest) throws OwsExceptionReport;
}
