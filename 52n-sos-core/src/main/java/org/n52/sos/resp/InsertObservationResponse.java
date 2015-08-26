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
import java.io.IOException;
import java.util.HashMap;

import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConstants;
import org.n52.sos.ogc.om.OMConstants;

import net.opengis.sos.x10.InsertObservationResponseDocument;

/**
 * class represents a InsertObservationResponse
 * 
 * @author Christoph Stasch
 * 
 */
public class InsertObservationResponse implements ISosResponse {

    /** XmlBeans representation of InsertObservation response */
    private InsertObservationResponseDocument xb_respDoc;

    /**
     * constructor
     * 
     * @param xb_respDocp
     *        XmlBeans representation of InsertObservation response
     */
    public InsertObservationResponse(InsertObservationResponseDocument xb_respDocp) {
        this.xb_respDoc = xb_respDocp;
    }

    /**
     * @return Returns true if the response should compressed using zip, otherwise false
     */
    public boolean getApplyGzipCompression() {
        return false;
    }

    /**
     * Returns the response as byte[]
     * 
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     */
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlOptions options = new XmlOptions();
        options.setSaveNamespacesFirst();
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();

        /*
         * workaround for xmlns:xlink prefix, cause in any schema the name of the xlink prefix is wrong, so
         * set this on xlink!
         */
        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put("http://www.w3.org/1999/xlink", "xlink");
        suggestedPrefixes.put(OMConstants.NS_SOS, OMConstants.NS_SOS_PREFIX);
        options.setSaveSuggestedPrefixes(suggestedPrefixes);
        options.setSavePrettyPrint();

        xb_respDoc.save(baos, options);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * Returns the the length of the content in bytes
     * 
     * @return Returns the the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     */
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return Returns the content type of this response
     */
    public String getContentType() {
        return SosConstants.CONTENT_TYPE_XML;
    }

}
