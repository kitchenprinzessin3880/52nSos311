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

import javax.xml.transform.TransformerException;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.ogc.om.OMConstants;

/**
 * Implementation of the ISosResponse interface for a response to a DescribeFeatureType request.
 * 
 * @author Carsten Hollmann
 *
 */
public class DescribeFeatureTypeResponse implements ISosResponse {

	/** The RegisterSensorResponse XMLBeans document. */
	private XmlObject descFeatTypeDoc;

	/** indicator for compression usage */
	private boolean applyZipCompression;

	/**
	 * Constructor 
	 * 
	 * @param descFeatTypeDoc 
	 * @param applyZipCompression
	 */
	public DescribeFeatureTypeResponse(XmlObject descFeatTypeDoc,
			boolean applyZipCompression) {
		super();
		this.descFeatTypeDoc = descFeatTypeDoc;
		this.applyZipCompression = applyZipCompression;
	}

	/* (non-Javadoc)
	 * @see org.n52.sos.resp.ISosResponse#getApplyGzipCompression()
	 */
	public boolean getApplyGzipCompression() {
		return applyZipCompression;
	}

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     */
	public byte[] getByteArray() throws IOException, TransformerException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XmlOptions options = new XmlOptions();
		// options.setSaveNamespacesFirst();
		options.setCharacterEncoding(SosConfigurator.getInstance()
				.getCharacterEncoding());

		/*
		 * workaround for xmlns:xlink prefix, cause in any schema the name of
		 * the xlink prefix is wrong, so set this on xlink!
		 */
		HashMap<String, String> suggestedPrefixes = new HashMap<String, String>();
		suggestedPrefixes.put("http://www.w3.org/1999/xlink", "xlink");
		suggestedPrefixes.put("http://www.opengis.net/sos/1.0", "sos");
		suggestedPrefixes.put(OMConstants.NS_OM, OMConstants.NS_OM_PREFIX);
		suggestedPrefixes.put(OMConstants.NS_OGC, OMConstants.NS_OGC_PREFIX);
		suggestedPrefixes.put(OMConstants.NS_SWE, OMConstants.NS_SWE_PREFIX);
		options.setSaveSuggestedPrefixes(suggestedPrefixes);
		options.setSaveImplicitNamespaces(suggestedPrefixes);
		options.setSavePrettyPrint();
		options.setSaveAggressiveNamespaces();

		descFeatTypeDoc.save(baos, options);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}

	 /**
     * @return Returns the the length of the content in bytes.
     * @throws IOException
     *         if the transformation of the OM document into a byte[] failed
     */
	public int getContentLength() throws IOException, TransformerException {
		return getByteArray().length;
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

}
