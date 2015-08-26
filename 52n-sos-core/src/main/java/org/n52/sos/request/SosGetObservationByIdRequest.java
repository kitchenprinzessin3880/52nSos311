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

package org.n52.sos.request;

import javax.xml.namespace.QName;

/**
 * class represents a getObservationById request
 * 
 * @author Christoph Stasch
 * 
 */
public class SosGetObservationByIdRequest extends AbstractSosRequest {

    /** version parameter */
    private String version;

    /**
     * srsName parameter defines the spatial reference system that should be used for any geometries that are
     * returned in the response.
     */
    private String srsName;

    /** the Id of the requested observation */
    private String observationID;

    /**
     * specifies the desired resultFormat MIME content type for transport of the results (e.g. TML, O&M native
     * format, or MPEG stream out-of-band)
     */
    private String responseFormat;

    /**
     * specifies the QName of the root element of an O&M Observation or element in the appropriate
     * substitution group (e.g. om:Measurement)
     */
    private QName resultModel;

    /**
     * specifies whether results are requested in-line, out-of-band, as an attachment, or if this is a request
     * for an observation template that will be used for subsequent calls to GetResult
     */
    private String responseMode;

    /**
     * @return the observationID
     */
    public String getObservationID() {
        return observationID;
    }

    /**
     * @param observationID
     *        the observationID to set
     */
    public void setObservationID(String observationID) {
        this.observationID = observationID;
    }

    /**
     * @return the responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * @param responseFormat
     *        the responseFormat to set
     */
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    /**
     * @return the responseMode
     */
    public String getResponseMode() {
        return responseMode;
    }

    /**
     * @param responseMode
     *        the responseMode to set
     */
    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    /**
     * @return the resultModel
     */
    public QName getResultModel() {
        return resultModel;
    }

    /**
     * @param resultModel
     *        the resultModel to set
     */
    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    /**
     * @return the srsName
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * @param srsName
     *        the srsName to set
     */
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *        the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
