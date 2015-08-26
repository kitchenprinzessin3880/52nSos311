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

import org.n52.sos.ogc.filter.TemporalFilter;

/**
 * class represents a describeSensor request
 * 
 * @author Christoph Stasch
 *
 */
public class SosDescribeSensorRequest extends AbstractSosRequest {

    /**procedure, which a description is requested for*/
    protected String procedure;

    /**requested output format for the response*/
    protected String outputFormat;

    /** service version*/
    protected String version;

    /** mobileEnabled parameter */
    protected boolean mobileEnabled;

    /** array containing the Time parameters, that means the time for which the sensor is requested */
    protected TemporalFilter[] Times;

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the procedures
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * @param procedures the procedures to set
     */
    public void setProcedures(String procedure) {
        this.procedure = procedure;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
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

    /**
     * @return the eventTime
     */
    public TemporalFilter[] getTime() {
        return Times;
    }

    /**
     * @param eventTime
     *        the eventTime to set
     */
    public void setTime(TemporalFilter[] Time) {
        this.Times = Time;
    }
}
