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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlbeans.XmlException;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.resp.ExceptionResp;
import org.n52.sos.resp.ISosResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The servlet of the SOS which receives the incoming HttpPost and HttpGet requests and sends the operation
 * result documents to the client
 * 
 * @author Christoph Stasch
 * 
 */
@SuppressWarnings("restriction")
public class SOS extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * the requestOperator which handles the requests and send them up to the specific requestListeners
     */
    private RequestOperator reqOp;

    /** the logger, used to log exceptions and additonaly information */
    private static Logger log;

    /** The init parameter of the configFile */
    private static final String INIT_PARAM_CONFIG_FILE = "configFile";

    /** The init parameter of the dbConfigFile */
    private static final String INIT_PARAM_DBCONFIG_FILE = "dbConfigFile";
    
    private MessageFactory soapMessageFactory_11 = null;
    private MessageFactory soapMessageFactory_12 = null;
    private DocumentBuilderFactory docBuildFactory = null;
    private DocumentBuilder docBuilder = null;

    /**
     * 
     * /** initializes the Servlet
     * 
     */
    public void init() throws ServletException {

        // get ServletContext
        ServletContext context = getServletContext();
        String basepath = context.getRealPath("/");

        // get configFile as InputStream
        InputStream configStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_CONFIG_FILE));

        // get dbconfigFile as InputStream
        InputStream dbConfigStream = context.getResourceAsStream(getInitParameter(INIT_PARAM_DBCONFIG_FILE));

        if (configStream == null) {
            throw new UnavailableException("could not open the config file");
        }
        // initialize Logger
        PropertyConfigurator.configureAndWatch("log4j.properties", 60 * 1000);
        log = Logger.getLogger(SOS.class);

        // initialize configurator
        SosConfigurator configurator;
        try {
            configurator = SosConfigurator.getInstance(configStream, dbConfigStream, basepath);
        }

        catch (OwsExceptionReport se) {
            throw new UnavailableException(se.getMessage());
        }

        try {
            this.reqOp = configurator.buildRequestOperator();
            log.info("\n******\nSOS initiated successfully!\n******\n");
        }

        catch (OwsExceptionReport se) {
            log.fatal("the instatiation of a RequestOperator failed");
            throw new UnavailableException(se.getMessage());
        }
        
        try {
			soapMessageFactory_11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			soapMessageFactory_12 = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
		} catch (SOAPException soape) {
			log.error("the instatiation of a SOAPMessageFactory failed!", soape);
		}
		

		try {
			docBuildFactory = DocumentBuilderFactory.newInstance();
			docBuildFactory.setNamespaceAware(true);
			docBuilder = docBuildFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			log.error("the instatiation of the DocumentBuilder failed!", pce);
		}
    }

    @Override
    public void destroy() {
        // cleanup SosConfigurator
        SosConfigurator configurator = SosConfigurator.getInstance();
        if (configurator != null) {
            configurator.cleanup();
        }
        super.destroy();
    }

    /**
     * handles all POST requests, the request will be passed to the requestOperator
     * 
     * @param req
     *        the incomming request
     * 
     * @param resp
     *        the response for the incoming request
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {

        log.debug("\n**********\n(POST) Connected from: " + req.getRemoteAddr() + " "
                + req.getRemoteHost());
        
        SOAPMessage soapMessageRequest = null;
        ISosResponse sosResp = null;
        SOAPFault fault = null;
        String inputString = "";
        try {
            log.debug(req.getCharacterEncoding());

            InputStream in = req.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuffer sb = new StringBuffer();
            while ( (line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            inputString = sb.toString();
            log.debug("New Post Request is:" + inputString);

            // discard "request="-Input String header
            if (inputString.startsWith("request=")) {
                inputString = inputString.substring(8, inputString.length());
                inputString = java.net.URLDecoder.decode(inputString, SosConfigurator.getInstance().getCharacterEncoding());
                log.debug("Decoded Post Request is: " + inputString);
            } 
            
            // parse request to Document
            /**
             * change 
             * author: juergen sorg
             * date: 2013-03-18
             * reason: xerces is not thread-safe (org.xml.sax.SAXException: FWK005 parse may not be called while parsing.)
             * old:
            Document fullRequestDoc = docBuilder.parse( new ByteArrayInputStream(inputString.getBytes()));
             * new:     */
            Document fullRequestDoc = null;
            try {
                synchronized (docBuildFactory) {
                    DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
                    fullRequestDoc = docBuilder.parse(new ByteArrayInputStream(inputString.getBytes()));
                }
            } catch (ParserConfigurationException pce) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, pce.getMessage());
                log.error("Error while parsing request!", pce);
                throw se;
            } catch (SAXException saxe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, saxe.getMessage());
                log.error("Error while parsing request!", saxe);
                throw se;
            } finally {
                if (br != null) {
                    br.close();
                }
            }
            /** change end **/
            fullRequestDoc.getDocumentElement().normalize();
            // checks if inputString is a SOAP envelope
            if(fullRequestDoc.getDocumentElement().getLocalName().equalsIgnoreCase("Envelope")) {
            	String soapAction = "";
            	// SOAP 1.2
            	if (fullRequestDoc.getDocumentElement().getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE)) {
            		soapMessageRequest = soapMessageFactory_12.createMessage(new MimeHeaders(), new ByteArrayInputStream(inputString.getBytes()));
            		// look for SOAPAction param in SOAPHeader
            		if (soapMessageRequest.getSOAPHeader() != null) {
            			NodeList nodeList = soapMessageRequest.getSOAPHeader().getElementsByTagNameNS("http://www.w3.org/2005/08/addressing", "Action");
                    	if(nodeList.getLength() == 1) {
        					soapAction = nodeList.item(0).getTextContent();
        				}
            		}
            	} 
            	// SOAP 1.1
            	else if (fullRequestDoc.getDocumentElement().getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
            		soapMessageRequest = soapMessageFactory_11.createMessage(new MimeHeaders(), new ByteArrayInputStream(inputString.getBytes()));
            		// look for SOAPAction param in HTTP-Header
            		Enumeration<?> headerNames = req.getHeaderNames();
                    while (headerNames.hasMoreElements())
                    {
                        String headerNameKey = (String) headerNames.nextElement();
                        if (headerNameKey.equalsIgnoreCase("SOAPAction")) {
                        	soapAction = req.getHeader(headerNameKey);
                        	break;
                        }
                    }
                    // if SOAPAction is not spec conform, create SOAPFault
                    if (soapAction.equals("") || !soapAction.startsWith("SOAPAction:")) {
                    	//TODO: Exception
                    	fault = soapMessageRequest.getSOAPBody().addFault();
                    	QName qname = new QName(soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI(), "Client", soapMessageRequest.getSOAPPart().getEnvelope().getPrefix());
						fault.setFaultCode(qname);
                    	fault.setFaultCode(qname);
                    	fault.setFaultString("The SOAPAction parameter in the HTTP-Header is missing or not valid!", Locale.ENGLISH);
                    } 
                    // trim SOAPAction value
                    else {
                    	 soapAction = soapAction.replace("\"", "");
                    	 soapAction = soapAction.replace(" ", "");
                    	 soapAction = soapAction.replace("SOAPAction:", "");
                         soapAction = soapAction.trim();
                    }
            	} 
            	// Not known SOAP version
            	else {
            		soapMessageRequest = soapMessageFactory_11.createMessage();
            		soapMessageRequest.getSOAPHeader().detachNode();
            		fault = soapMessageRequest.getSOAPBody().addFault();
            		QName qname = new QName(soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI(), SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.getLocalPart(), soapMessageRequest.getSOAPPart().getEnvelope().getPrefix());
                	fault.setFaultCode(qname);
                	fault.setFaultString("The SOAP version is unknown!", Locale.ENGLISH);
                	Detail detail = fault.addDetail();
                	detail.setTextContent("The SOAP versions with SOAPNamespace '" + fullRequestDoc.getDocumentElement().getNamespaceURI() + "' is unknown! Valid versions are SOAP 1.1 with SOAPNamespace '" + SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE + "' and SOAP 1.2 with SOAPNamespace '" + SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE + "'!");
            	}
            	// if no fault, get document from SOAPBody
            	if (fault == null) {
	            	soapAction = soapAction.replace("\"", "");
					String[] saArray = soapAction.split("#");
					String localNameRequestType = saArray[saArray.length-1].trim();
					if (soapMessageRequest.getSOAPBody() != null) {
						Document bodyRequestDoc = soapMessageRequest.getSOAPBody().extractContentAsDocument();
						Element rootRequestDoc = bodyRequestDoc.getDocumentElement();
						if(localNameRequestType != null && !localNameRequestType.equals("")) {
							// check if SOAPAction is valid with SOAPBodyElement, else create SOAPFault
							if(!rootRequestDoc.getLocalName().equalsIgnoreCase(localNameRequestType) || !saArray[0].equals(OMConstants.NS_SOS)) {
								// TODO: Exception, different params
								SOAPBody body = soapMessageRequest.getSOAPBody();
								fault = body.addFault();
								if (soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
									QName qname = new QName(soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI(), SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.getLocalPart(), soapMessageRequest.getSOAPPart().getEnvelope().getPrefix());
									fault.setFaultCode(qname);
								} else {
									fault.setFaultCode(SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT);
								}
			                	fault.setFaultString("The ActionURI value is not valid with the SOAPBodyElement!", Locale.ENGLISH);
			                	if (!rootRequestDoc.getLocalName().equalsIgnoreCase(localNameRequestType) && !saArray[0].equals(OMConstants.NS_SOS)) {
			                		fault.addDetail().setTextContent("The local names are different. LocalName is '" + rootRequestDoc.getLocalName() + "' and the SOAPAction value is '" + localNameRequestType + "'! And the NamespareURIs are different. The SOAPAction NamespaceURI is '" + saArray[0] + "' and not '" + OMConstants.NS_SOS + "'!");
			                	} else if (!saArray[0].equals(OMConstants.NS_SOS)) {
			                		fault.addDetail().setTextContent("The NamespareURIs are different. The SOAPAction NamespaceURI is '" + saArray[0] + "' and not '" + OMConstants.NS_SOS + "'!");
			                	} else {
			                		fault.addDetail().setTextContent("The local names are different. LocalName is '" + rootRequestDoc.getLocalName() + "' and the SOAPAction value is '" + localNameRequestType + "'!");
			                	}
							} 
						}
						// add missing param in SOS request
						if (!rootRequestDoc.hasAttribute("service")) {
							rootRequestDoc.setAttribute("service", "SOS");
						}
						inputString = nodeToXmlString(rootRequestDoc);
			            sosResp = reqOp.doPostOperation(inputString);
					} 
					// if mandatory SOAPBody is missing, create SOAPFault
					else {
						if (soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)){
							soapMessageRequest = soapMessageFactory_11.createMessage();
							fault = soapMessageRequest.getSOAPBody().addFault();
							QName qname = new QName(soapMessageRequest.getSOAPPart().getEnvelope().getNamespaceURI(), SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.getLocalPart(), soapMessageRequest.getSOAPPart().getEnvelope().getPrefix());
							fault.setFaultCode(qname);
						} else {
							soapMessageRequest = soapMessageFactory_12.createMessage();
							fault = soapMessageRequest.getSOAPBody().addFault();
							fault.setFaultCode(SOAPConstants.SOAP_VERSIONMISMATCH_FAULT);
							
						}
	                	fault.setFaultString("The Element 'SOAPBody' is missing in the request!", Locale.ENGLISH);
					}
            	}
            	doSoapResponse(resp, sosResp, soapMessageRequest, fault);
            } else {
            	
            	//modified by ASD
                sosResp = reqOp.doPostOperation(inputString);
                doResponse(resp, sosResp);
            }
        }
        catch (IOException ioe) {
            log.error("Could not open input stream from request!");
        } catch (SOAPException soape) {
        	 log.error("Error in SOAP request!", soape);
		}/**
		change
		author: juergen sorg
		date: 2013-03-18  
        catch (SAXException saxe) {
			log.error("Could not parse requested document!", saxe);
		}
		
		* end change **/
		 catch (OwsExceptionReport owse) {
			doSoapResponse(resp, new ExceptionResp(owse.getDocument()), soapMessageRequest, fault);
		}
    }

    /**
     * handles all GET requests, the request will be passed to the RequestOperator
     * 
     * @param req
     *        the incoming request
     * 
     * @param resp
     *        the response for the incomming request
     * 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {

        String request = null;
        request = req.getParameter(SosConstants.REQUEST);

        // ////////////////////////////////////////////
        // methods for refreshing cached data from database
        if (request != null && request.equals(SosConstants.REFRESH_REQUEST)) {
            try {
                SosConfigurator.getInstance().getCapsCacheController().update(false); // refreshMetadata();
            }
            catch (OwsExceptionReport se) {
                doResponse(resp, new ExceptionResp(se.getDocument()));
            }
        }

        // /////////////////////////////////////////////
        // method for refreshing cached fois from database
        else if (request != null && request.equals(SosConstants.REFRESH_FOIS_REQUEST)) {
            try {
                SosConfigurator.getInstance().getCapsCacheController().updateFois(); //refreshFOIs();
            }
            catch (OwsExceptionReport se) {
                doResponse(resp, new ExceptionResp(se.getDocument()));
            }
        }

        // ///////////////////////////////////////////////
        // forward GET-request to RequestOperator
        else {
            log.debug("\n**********\n(GET) Connected from: " + req.getRemoteAddr() + " "
                    + req.getRemoteHost());
            log.trace("Query String: " + req.getQueryString());
            ISosResponse sosResp = reqOp.doGetOperation(req);
            doResponse(resp, sosResp);
        }
    }

    /**
     * writes the content of the SosResponse to the outputStream of the HttpServletResponse
     * 
     * @param resp
     *        the HttpServletResponse to which the content will be written
     * 
     * @param sosResponse
     *        the SosResponse, whose content will be written to the outputStream of resp param
     * 
     */
    public void doResponse(HttpServletResponse resp, ISosResponse sosResponse) {
        try {

            String contentType = sosResponse.getContentType();
            int contentLength = sosResponse.getContentLength();
            byte[] bytes = sosResponse.getByteArray();
            resp.setContentLength(contentLength);
            OutputStream out = resp.getOutputStream();
	    	if (sosResponse.getApplyGzipCompression()) {
	            resp.setContentType(contentType);
	            GZIPOutputStream gzip = new GZIPOutputStream(out);
	            gzip.write(bytes);
	            gzip.finish();
	        }
	        else {
	            resp.setContentType(contentType);
	            out.write(bytes);
	        }
            
            out.close();
        }
        catch (IOException ioe) {
            log.error("doResponse", ioe);
        }
        catch (TransformerException te) {
            log.error("doResponse", te);
        } 
    }
    
    /**
     * writes the content of the SosResponse as SOAPMessage to the outputStream of the HttpServletResponse
     * 
     * @param resp 
     * 			the HttpServletResponse to which the content will be written
     * 
     * @param sosResponse 
     * 			the SosResponse, whose content will be written to the outputStream of resp param
     * 
     * @param soapRequestMessage 
     * 			the SOAP request message, used for response
     */
    public void doSoapResponse(HttpServletResponse resp, ISosResponse sosResponse, SOAPMessage soapRequestMessage, SOAPFault soapFault) {
        try {
			SOAPMessage soapResponseMessage = soapRequestMessage;
			// remove old SOAPBody contents
			soapResponseMessage.getSOAPBody().removeContents();
			String contentType = "text/xml";
			boolean gzipCompression = false;
			
			// parse response as Document and add it to SOAPBody
			// if SOAPFault, add Fault to SOAP response
			if (soapFault != null) {
				SOAPBody body = soapResponseMessage.getSOAPBody();
				body.addChildElement(soapFault);
			} 
			// add sosResponse to SOAPBody
			else {
				contentType = sosResponse.getContentType();
				gzipCompression = sosResponse.getApplyGzipCompression();
				// if sosResponse is an Exception
				if (sosResponse instanceof ExceptionResp) {
					ExceptionReportDocument erd = ExceptionReportDocument.Factory.parse(new ByteArrayInputStream(sosResponse.getByteArray()));
					SOAPBody body = soapResponseMessage.getSOAPBody();
					SOAPFault newFault = body.addFault();
					if (soapResponseMessage.getSOAPPart().getEnvelope().getNamespaceURI().equalsIgnoreCase(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE)) {
						QName qname = new QName(soapResponseMessage.getSOAPPart().getEnvelope().getNamespaceURI(), "Client", soapResponseMessage.getSOAPPart().getEnvelope().getPrefix());
						newFault.setFaultCode(qname);
					} else {
						newFault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
					}
					newFault.setFaultString(erd.getExceptionReport().getExceptionArray(0).getExceptionTextArray(0), Locale.ENGLISH);
					Detail detail = newFault.addDetail();
					SOAPElement exRep = detail.addChildElement(soapRequestMessage.getSOAPPart().getEnvelope().createName(erd.getExceptionReport().getDomNode().getLocalName(), OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS));
					exRep.addNamespaceDeclaration(OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS);
					exRep.setAttribute("version", erd.getExceptionReport().getVersion());
					if (erd.getExceptionReport().getLang() != null && !erd.getExceptionReport().getLang().equals("")) {
						exRep.setAttribute("xml:lang", erd.getExceptionReport().getLang());
					}
					ExceptionType[] exceptions = erd.getExceptionReport().getExceptionArray();
					for (ExceptionType exceptionType : exceptions) {
						String code  = exceptionType.getExceptionCode();
						String locator = exceptionType.getLocator();
						StringBuffer text = new StringBuffer();
						for (String textArrayText : exceptionType.getExceptionTextArray()) {
							text.append(textArrayText);
						}
						SOAPElement exec = exRep.addChildElement(soapRequestMessage.getSOAPPart().getEnvelope().createName(erd.getExceptionReport().getExceptionArray(0).getDomNode().getLocalName(), OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS));
						exec.addAttribute(soapRequestMessage.getSOAPPart().getEnvelope().createName("exceptionCode", OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS), code);
						if (locator != null && !locator.equals("")) {
							exec.addAttribute(soapRequestMessage.getSOAPPart().getEnvelope().createName("locator", OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS), locator);
						}
						if (!text.toString().equals("")) {
							SOAPElement execText = exec.addChildElement(soapRequestMessage.getSOAPPart().getEnvelope().createName("ExceptionText", OMConstants.NS_OWS_PREFIX, OMConstants.NS_OWS));
							execText.setTextContent(text.toString());
						}
					}
				} 
				// if sosResponse is a Response
				else {
					Document doc = docBuilder.parse(new ByteArrayInputStream(sosResponse.getByteArray()));
		            doc.getDocumentElement().normalize();
					soapResponseMessage.getSOAPBody().addDocument(doc);
				}
			}
			// remove SOAPAction from SOAPHeader
			if (soapResponseMessage.getSOAPHeader() != null) {
				NodeList nodes = soapResponseMessage.getSOAPHeader().getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if(nodes.item(i).getLocalName() != null && nodes.item(i).getLocalName().equalsIgnoreCase("Action")){
						soapResponseMessage.getSOAPHeader().removeChild(nodes.item(i));
					}
				}
			}
			
			// response to OutputStream
			ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
			soapResponseMessage.writeTo(byteOutStream);
			
            int contentLength = byteOutStream.size();
            byte[] bytes = byteOutStream.toByteArray();
            resp.setContentLength(contentLength);
            OutputStream out = resp.getOutputStream();
	    	if (gzipCompression) {
	            resp.setContentType(contentType);
	            GZIPOutputStream gzip = new GZIPOutputStream(out);
	            gzip.write(bytes);
	            gzip.finish();
	        }
	        else {
	            resp.setContentType(contentType);
	            out.write(bytes);
	        }
            
            out.close();
            byteOutStream.close();
        }
        catch (IOException ioe) {
            log.error("doSoapResponse", ioe);
        } catch (TransformerException te) {
            log.error("doSoapResponse", te);
        } catch (SOAPException soape) {
        	log.error("doSoapResponse", soape);
		} catch (SAXException saxe) {
			log.error("doSoapResponse", saxe);
		} catch (XmlException xmle) {
			log.error("doSoapResponse", xmle);
		} 
    }
    
    
	/**
	 * Parses w3c.Node to String
	 * 
	 * @param node 
	 * 		Node to parse.
	 * 
	 * @return Node as String.
	 * @throws OwsExceptionReport 
	 */
	private String nodeToXmlString(Node node) throws OwsExceptionReport {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 null,
                                 "The request was sent in an unknown format or is invalid! Please use the SOS version 1.0 schemata to build your request and validate the requests before sending them to the SOS!");
            log.error("nodeToString Transformer Exception", te);
            throw se;
		}
		return sw.toString();
	}
}
