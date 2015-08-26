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

package org.n52.sos.resp;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.TransformerException;

import org.n52.sos.SosConstants;

/**
 * Implementation of the SosResponse for the response to a DescribeSensor request.
 * 
 * @author Alexander C. Walkowski
 * @version 1.0
 */
public class SensorResponse implements ISosResponse {

    /** document which should be returned in the response */
    private SensorDocument sensorDoc;

    /** flag which indicated, whether zip compression should be applied */
    private boolean applyZipCompression;

    /**
     * creates a new SosResponse for a describe sensor request
     * 
     * @param sensorDoc
     *        the SensorDoc returned by this SensorResp
     * @param applyZipCompression
     *        true if zip compression should be applied to the response
     */
    public SensorResponse(SensorDocument sensorDoc, boolean applyZipCompression) {
        this.sensorDoc = sensorDoc;
        this.applyZipCompression = applyZipCompression;
    }

    /**
     * @return Returns the content type of this response
     */
    public String getContentType() {
        if (applyZipCompression) {
            return SosConstants.CONTENT_TYPE_ZIP;
        }
        return SosConstants.CONTENT_TYPE_XML;
    }

    /**
     * @return Returns the the length of the content in bytes
     * @throws TransformerException
     *         if getting the content length failed
     */
    public int getContentLength() throws TransformerException {
        return getByteArray().length;
    }

    /**
     * @return Returns the response as byte[]
     * @throws TransformerException
     *         if getting the content length failed
     */
    public byte[] getByteArray() throws TransformerException {
        ByteArrayOutputStream baos = sensorDoc.getOutputStream();
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return Returns true if the response should compressed using zip, otherwise false
     */
    public boolean getApplyGzipCompression() {
        return applyZipCompression;
    }
}