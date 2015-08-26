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


import net.opengis.ows.x11.OperationsMetadataDocument.OperationsMetadata;
import net.opengis.sos.x10.CapabilitiesDocument;
import net.opengis.sos.x10.ContentsDocument.Contents;

import org.joda.time.DateTime;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the operations
 * (e.g. GetCapabilitiesDAO)
 * 
 * @author Christoph Stasch
 * 
 */
public interface IGetCapabilitiesDAO {

    /**
     * 
     * @return Returns the GetCapabilities response with all sections
     * 
     * @throws OwsExceptionReport
     *         if query of min and max date for observations failed
     */
    public CapabilitiesDocument getCompleteCapabilities() throws OwsExceptionReport;

    /**
     * 
     * @return Returns the GetCapabilities mobile response with all sections
     * 
     * @throws OwsExceptionReport
     *         if query of min and max date for observations failed
     */
    public CapabilitiesDocument getCompleteCapabilitiesMobile() throws OwsExceptionReport;

    /**
     * returns the max date of an observation
     * 
     * @return Date containing the min date of the observation
     */
    public DateTime getMaxDate4Observations() throws OwsExceptionReport;

    /**
     * returns the min date of an observation
     * 
     * @return Date containing the min date of the observation
     */
    public DateTime getMinDate4Observations() throws OwsExceptionReport;

    /**
     * returns the min date value for the requested offering
     * 
     * @param offering
     *        String which represents the offering for which the min date should be queried
     * @return Date which represents the min date for the offering
     * @throws OwsExceptionReport
     *         if query of the min date failed
     */
    public DateTime getMinDate4Offering(String offering) throws OwsExceptionReport;

    /**
     * returns the max date value for the requested offering
     * 
     * @param offering
     *        String which represents the offering for which the max date should be queried
     * @return Date which represents the max date for the offering
     * @throws OwsExceptionReport
     *         if query of the max date failed
     */
    public DateTime getMaxDate4Offering(String offering) throws OwsExceptionReport;

    /**
     * builds the contents section of the capabilities response. The data values for this section are all read
     * in from configfile or database
     * 
     * @return Contents xml bean representing the contents section of the capabilities response document
     * @throws OwsExceptionReport
     *         if building the contents section failed
     */
    public Contents getContents() throws OwsExceptionReport;

    /**
     * builds the contents section of the capabilities response. The data values for this section are all read
     * in from configfile or database
     * 
     * @return Contents xml bean representing the contents section of the capabilities response document
     * @throws OwsExceptionReport
     *         if building the contents section failed
     */
    public Contents getContentsMobile() throws OwsExceptionReport;

    /**
     * method returns the operationsMetadata section and queries therefore the min and max time to fill the
     * eventTime element of the getObservation operations metadata
     * 
     * @param capsDoc
     *        the CapabilitiesDocument bean resulting from parsing the capabilities_skeleton file
     * @return OperationsMetadata bean representing the operations metadata section of the capabilities
     *         response document
     * @throws OwsExceptionReport
     *         if the query of the min and max time for all observations failed
     */
    public OperationsMetadata getOperationsMetadata(CapabilitiesDocument capsDoc) throws OwsExceptionReport;

    /**
     * method returns the operationsMetadata section and queries therefore the min and max time to fill the
     * eventTime element of the getObservation operations metadata
     * 
     * @param capsDoc
     *        the CapabilitiesDocument bean resulting from parsing the capabilities_skeleton file
     * @return OperationsMetadata bean representing the operations metadata section of the capabilities
     *         response document
     * @throws OwsExceptionReport
     *         if the query of the min and max time for all observations failed
     */
    public OperationsMetadata getOperationsMetadataMobile(CapabilitiesDocument capsDoc) throws OwsExceptionReport;

    /**
     * (re-)loads the capabilities skeleton file and sets the private capabilitiesSkeleton variable
     * 
     * @throws OwsExceptionReport
     *         if binding of the skeletonFile with XMLBeans classes failed
     */
    public CapabilitiesDocument loadCapabilitiesSkeleton() throws OwsExceptionReport;

    /**
     * (re-)loads the capabilities skeleton mobile file and sets the private capabilitiesSkeleton variable
     * 
     * @throws OwsExceptionReport
     *         if binding of the skeletonFile with XMLBeans classes failed
     */
    public CapabilitiesDocument loadCapabilitiesSkeletonMobile() throws OwsExceptionReport;

    /**
     * The updateSequence parameter in this SOS is implemented as a Date in gml format. If the service
     * metadata are updated the updateSequence parameter is set on the actual date. The update Sequence
     * parameter is then saved in the capabilities skeleton file.
     * 
     * @return Date which represents the updateSequence value of this capabilities
     */
    public DateTime getUpdateSequence(CapabilitiesDocument capsDoc) throws OwsExceptionReport;

}
