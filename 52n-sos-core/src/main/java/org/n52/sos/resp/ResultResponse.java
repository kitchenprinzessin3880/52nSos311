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

import javax.xml.namespace.QName;

import net.opengis.sos.x10.GetResultResponseDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.ogc.om.OMConstants;

/**
 * Implementation of the ISosResponse interface for a response to a getResult request.
 * 
 * @author Christoph Stasch
 * @version 1.0
 */
public class ResultResponse implements ISosResponse {

    /** The OM result response document */
    private GetResultResponseDocument getResRespDoc;

    /** indicator for compression usage */
    private boolean applyZipCompression;

    /**
     * constructor creates an ResultResponse from a passed GetResultResponseDocument.
     * 
     * @param resDoc
     *        the response document
     */
    public ResultResponse(GetResultResponseDocument xb_resDoc) {
        getResRespDoc = xb_resDoc;
        XmlCursor cursor = getResRespDoc.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance",
                                              "schemaLocation"), OMConstants.NS_SOS + " "
                    + OMConstants.SCHEMA_LOCATION_SOS);
        }
    }

    /**
     * @return Returns the content type of this response. The returned value is the constant
     *         SosConstants.CONTENT_TYPE.
     */
    public String getContentType() {
        if (applyZipCompression) {
            return SosConstants.CONTENT_TYPE_ZIP;
        }
        return SosConstants.CONTENT_TYPE_XML;
    }

    /**
     * @return Returns the the length of the content in bytes.
     * @throws IOException
     *         if the transformation of the OM document into a byte[] failed
     */
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if the transformation of the OM document into a byte[] failed
     */
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlOptions options = new XmlOptions();
        options.setSaveNamespacesFirst();
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();

        options.setCharacterEncoding(SosConfigurator.getInstance().getCharacterEncoding());

        /*
         * workaround for xmlns:xlink prefix, cause in any schema the name of the xlink prefix is wrong, so
         * set this on xlink!
         */
        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put("http://www.w3.org/1999/xlink", "xlink");
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        this.getResRespDoc.save(baos, options);
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
