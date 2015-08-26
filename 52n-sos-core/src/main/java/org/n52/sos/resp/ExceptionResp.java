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

import net.opengis.ows.x11.ExceptionReportDocument;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.ogc.om.OMConstants;

/**
 * Implementation of the <code>ISosResponse</code> interface for OGC service exceptions.
 * 
 * @author Alexander C. Walkowski
 * @version 0.1
 */
public class ExceptionResp implements ISosResponse {

    /** indicator for compression usage */
    private boolean applyZipCompression;

    /** the exception report Document */
    private ExceptionReportDocument erd;

    /**
     * constructor
     * 
     * @param erd
     *        the exception report document for which the exception response should be created
     * @param applyZipCompression
     *        indicates whether zip compression should be applied (true) or not (false)
     */
    public ExceptionResp(ExceptionReportDocument erd, boolean applyZipCompression) {
        this.erd = erd;
        this.applyZipCompression = applyZipCompression;
    }

    /**
     * constructor without boolean for zip application
     * 
     * @param erd
     *        the exception report document for which the exception response should be created
     */
    public ExceptionResp(ExceptionReportDocument erd) {
        this.erd = erd;
        XmlCursor cursor = this.erd.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance",
                                              "schemaLocation"), OMConstants.SCHEMA_LOCATION_OWS);
        }
        this.applyZipCompression = false;
    }

    /**
     * @return Returns the content type of this response. The returned value is the constant
     *         ScsConstant.CONTENT_TYPE.
     */
    public String getContentType() {
        if (applyZipCompression) {
            return SosConstants.CONTENT_TYPE_ZIP;
        }
        return SosConstants.CONTENT_TYPE_XML;
    }

    /**
     * @return Returns the the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     */
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
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
        options.setCharacterEncoding(SosConfigurator.getInstance().getCharacterEncoding());

        HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
        suggestedPrefixes.put(OMConstants.NS_OWS, OMConstants.NS_OWS_PREFIX);
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        erd.save(baos, options);
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
