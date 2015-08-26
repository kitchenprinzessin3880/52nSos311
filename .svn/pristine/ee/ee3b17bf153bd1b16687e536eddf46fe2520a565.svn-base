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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.sos.SosConfigurator;
import org.w3c.dom.Document;

/**
 * represents a SensorML document used within the describeSensor operation
 * 
 * @author Alexander C. Walkowski
 * 
 * @version 1.0
 */
public class SensorDocument {

    /** the sensor document */
    private Document sensorDoc;

    /**
     * creates a SensorDoc from the passed Document object
     * 
     * @param sensorDoc
     *        the document containing all information of the SensorML document.
     */
    public SensorDocument(Document sensorDoc) {
        this.sensorDoc = sensorDoc;
    }

    /**
     * @return Returns the document of the capabilitiesDoc
     */
    public Document getDocument() {
        return sensorDoc;
    }

    /**
     * @return Returns the ByteArrayOutputStream for this document.
     * 
     * @throws TransformerException
     *         if transforming the sensor description document into a ByteArrayOutputStream failed
     */
    public ByteArrayOutputStream getOutputStream() throws TransformerException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        // set encoding
        transformer.setOutputProperty(OutputKeys.ENCODING,
                                      SosConfigurator.getInstance().getCharacterEncoding());

        // create DOMSource from sensorDoc
        DOMSource source = new DOMSource(sensorDoc);

        // write sensorDoc intoOutputStream
        StreamResult result = new StreamResult(outStream);
        transformer.transform(source, result);

        return outStream;
    }
}
