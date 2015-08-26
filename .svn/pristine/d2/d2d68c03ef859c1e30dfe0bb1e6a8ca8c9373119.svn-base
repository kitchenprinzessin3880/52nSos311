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

import org.n52.sos.SosConstants;

/**
 * class represents a GetCapabilities request and encapsulates the parameters
 * 
 * @author Stephan Knster
 * 
 */
public class SosGetCapabilitiesRequest extends AbstractSosRequest {

    /** request parameter */
    protected String request;

    /** updateSequence parameter */
    protected String updateSequence;

    /** array containing the accepted versions */
    protected String[] acceptVersions;

    /** array containing the sections */
    protected String[] sections;

    /** array containing the accepted formats */
    protected String[] acceptFormats;

    /** mobileEnabled parameter */
    protected boolean mobileEnabled;

    /**
     * constructor; sets the not mandatory values either on "NOT_SET" if these are Strings or on MIN_VALUE if
     * these are Integers or on null, if these are Arrays.
     * 
     */
    public SosGetCapabilitiesRequest() {
        String notSet = SosConstants.PARAMETER_NOT_SET;
        service = notSet;
        updateSequence = notSet;
        acceptVersions = null;
        sections = null;
        acceptFormats = null;
        mobileEnabled = false;
    }

    /**
     * @return the accepted formats
     */
    public String[] getAcceptFormats() {
        return acceptFormats;
    }

    /**
     * @param acceptFormats
     *        the accepted formats to set
     */
    public void setAcceptFormats(String[] acceptFormats) {
        this.acceptFormats = acceptFormats;
    }

    /**
     * @return the accepted versions
     */
    public String[] getAcceptVersions() {
        return acceptVersions;
    }

    /**
     * @param acceptVersions
     *        the accepted versions to set
     */
    public void setAcceptVersions(String[] acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @return the sections
     */
    public String[] getSections() {
        return sections;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(String[] sections) {
        this.sections = sections;
    }

    /**
     * @return the update sequence
     */
    public String getUpdateSequence() {
        return updateSequence;
    }

    /**
     * @param updateSequence
     *        the update sequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

    /**
     * @return the request String
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request
     *        the request String
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * 
     * @return mobileEnabled
     */
    public boolean isMobileEnabled() {
        return mobileEnabled;
    }

    /**
     * 
     * @param mobileEnabled
     */
    public void setMobileEnabled(boolean mobileEnabled) {
        this.mobileEnabled = mobileEnabled;
    }
}
