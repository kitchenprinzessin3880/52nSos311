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

package org.n52.sos.decode;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractSosRequest;

/**
 * interface offers parsing method to parse the HttpServletRequest
 * 
 * @author Stephan Knster
 * 
 */
public interface IHttpGetRequestDecoder {

    /**
     * parses the HttpServletRequest representing the request and creates an AbstractSosRequest
     * 
     * @param request
     *        HttpServletRequest including the request parameters
     * @return Returns AbstractSosRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing failed
     */
    public AbstractSosRequest receiveRequest(HttpServletRequest request) throws OwsExceptionReport;

}
