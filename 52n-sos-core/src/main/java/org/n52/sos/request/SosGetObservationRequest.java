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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;

/**
 * class represents a GetObservation request and encapsulates the parameters
 * 
 * @author Christoph Stasch
 * 
 */
public class SosGetObservationRequest extends AbstractSosRequest {

    /** version parameter */
    protected String version;

    /**represents the request document as string*/
    protected String requestString;

    /**indicates, wether DomainFeature spatial filter is set*/
    private boolean setDomainFeatureSpatialFilter;

    /**indicates, wether FeatureOfInterest spatial filter is set*/
    private boolean setFeatureOfInterestSpatialFilter;

    /**
     * srsName parameter (name of the spatial reference system); indicates, in which SRS the coordinates of
     * the response should be returned
     */
    protected String srsName;

    /**
     * resultType parameter; specifies, whether the response should include just a summary of the result set
     * or actual results
     */
    //private ResultType resultType;
    /** startPosition parameter; indicates at which record position the SOS should start generating output */
    //private int startPosition;
    /** maxRecords parameter; defines the maximum number of records, which should be returned */
    //private int maxRecords;
    /** offering parameter; id of the offering, for which observations are requested */
    protected String offering;

    /** array containing the eventTime parameters, that means the time for which observations are requested */
    protected TemporalFilter[] eventTimes;

    /** array containing the ids of the sensors, for which observations are requested */
    protected String[] procedure;

    /** array containing the ids of the phenomena, for which observations are requested */
    private String[] observedProperty;

    /** feature of interest parameter; this could be either a list of feature ids or a spatial filter */
    protected SpatialFilter featureOfInterest;

    /** domain feature parameter; this could be either a list of feature ids or a spatial filter */
    private SpatialFilter domainFeature;

    /** result parameter; spatial filter */
    private SpatialFilter resultSpatialFilter;

    /** result parameter; specifies a filter on the result values (e.g. result>45) */
    protected ComparisonFilter result;

    /**
     * responseFormat parameter; specifies the desired resultFormat MIME content type for transport of the
     * results (E.g. O&M)
     */
    protected String responseFormat;

    /**
     * resultModel parameter; specifies the QName of the rootElement of an O&M Observation (e.g.
     * om:Measurement)
     */
    protected QName resultModel;

    /**
     * responseMode parameter; specifies whether results are requested in-line, out-of-band, as an attachment,
     * or if this is a request for an observation template that will be used for subsequent calls to
     * GetResult.
     */
    protected String responseMode;

    /** sortBy parameter; used to specify the sort order for the results */
    // private SosSortBy sortBy;
    /** mobileEnabled parameter */
    protected boolean mobileEnabled;

    /**
     * constructor; sets the not mandatory values either on "NOT_SET" if these are Strings or on MIN_VALUE if
     * these are Integers or on null, if these are Arrays.
     * 
     */
    public SosGetObservationRequest() {
        String notSet = SosConstants.PARAMETER_NOT_SET;
        srsName = notSet;
        /*resultType = null;
        startPosition = Integer.MIN_VALUE;
        maxRecords = Integer.MIN_VALUE;*/
        eventTimes = null;
        procedure = null;
        featureOfInterest = null;
        domainFeature = null;
        result = null;
        resultModel = null;
        responseMode = notSet;
        //sortBy = null;
        mobileEnabled = false;
    }

    /**
     * @return the eventTime
     */
    public TemporalFilter[] getEventTime() {
        return eventTimes;
    }

    /**
     * @param eventTime
     *        the eventTime to set
     */
    public void setEventTime(TemporalFilter[] eventTime) {
        this.eventTimes = eventTime;
    }

    /**
     * @return the featureOfInterest
     */
    public SpatialFilter getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest
     *        the featureOfInterest to set
     */
    public void setFeatureOfInterest(SpatialFilter featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * @return the featureOfInterest
     */
    public SpatialFilter getDomainFeature() {
        return domainFeature;
    }

    /**
     * @param featureOfInterest
     *        the featureOfInterest to set
     */
    public void setDomainFeature(SpatialFilter featureOfInterest) {
        this.domainFeature = featureOfInterest;
    }

    /**
     * @return the maxRecords
     *
    public int getMaxRecords() {
        return maxRecords;
    }
    */

    /**
     * @param maxRecords
     *        the maxRecords to set
     *
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }
    */

    /**
     * 
     * @return ids of the phenomena
     */
    public String[] getObservedProperty() {
        return observedProperty;
    }

    /**
     * @param observedProperty
     *        the observedProperty to set
     */
    public void setObservedProperty(String[] observedProperty) {
        CapabilitiesCacheController cache = SosConfigurator.getInstance().getCapsCacheController();
        
        List<String> phens = new ArrayList<String>();
        Map<String, List<String>> compPhens = cache.getPhens4CompPhens();
        
        // find phens for comp phens
        for(String phen : observedProperty){                
            if (compPhens.keySet().contains(phen))      
                phens.addAll(compPhens.get(phen));
            else
                phens.add(phen);
        }
        
        this.observedProperty = (String[]) phens.toArray(new String[0]);
    }

    /**
     * @return the offering
     */
    public String getOffering() {
        return offering;
    }

    /**
     * @param offering
     *        the offering to set
     */
    public void setOffering(String offering) {
        this.offering = offering;
    }

    /**
     * @return the procedure
     */
    public String[] getProcedure() {
        return procedure;
    }

    /**
     * @param procedure
     *        the procedure to set
     */
    public void setProcedure(String[] procedure) {
        this.procedure = procedure;
    }

    /**
     * @return the responseFormat
     */
    public String getResponseFormat() {
        return responseFormat;
    }

    /**
     * @param responseFormat
     *        the responseFormat to set
     */
    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }

    /**
     * @return the responseMode
     */
    public String getResponseMode() {
        return responseMode;
    }

    /**
     * @param responseMode
     *        the responseMode to set
     */
    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    /**
     * @return the result
     */
    public ComparisonFilter getResult() {
        return result;
    }

    /**
     * @param result
     *        the result to set
     */
    public void setResult(ComparisonFilter result) {
        this.result = result;
    }

    /**
     * @return the resultModel
     */
    public QName getResultModel() {
        return resultModel;
    }

    /**
     * @param resultModel
     *        the resultModel to set
     */
    public void setResultModel(QName resultModel) {
        this.resultModel = resultModel;
    }

    /**
     * @return the resultType
     *
    public ResultType getResultType() {
        return resultType;
    }
    */

    /**
     * @param resultType
     *        the resultType to set
     *
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }
    */

    /**
     * @return the sortBy
     *
    public SosSortBy getSortBy() {
        return sortBy;
    }
    */

    /**
     * @param sortBy
     *        the sortBy to set
     *
    public void setSortBy(SosSortBy sortBy) {
        this.sortBy = sortBy;
    }
    */

    /**
     * @return name of the spatial reference system
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * @param srsName
     *        the srsName to set
     */
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

    /**
     * @return the startPosition
     *
    public int getStartPosition() {
        return startPosition;
    }
    */

    /**
     * @param startPosition the startPosition to set
     *
    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }
    */

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
     * @return the requestString
     */
    public String getRequestString() {
        return requestString;
    }

    /**
     * @param requestString the requestString to set
     */
    public void setRequestString(String requestString) {
        this.requestString = requestString;
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
     * @return the setDomainFeatureSpatialFilter
     */
    public boolean isSetDomainFeatureSpatialFilter() {
        return setDomainFeatureSpatialFilter;
    }

    /**
     * @param setDomainFeatureSpatialFilter the setDomainFeatureSpatialFilter to set
     */
    public void setSetDomainFeatureSpatialFilter(boolean setDomainFeatureSpatialFilter) {
        this.setDomainFeatureSpatialFilter = setDomainFeatureSpatialFilter;
    }

    /**
     * @return the setFeatureOfInterestSpatialFilter
     */
    public boolean isSetFeatureOfInterestSpatialFilter() {
        return setFeatureOfInterestSpatialFilter;
    }

    /**
     * @param setFeatureOfInterestSpatialFilter the setFeatureOfInterestSpatialFilter to set
     */
    public void setSetFeatureOfInterestSpatialFilter(boolean setFeatureOfInterestSpatialFilter) {
        this.setFeatureOfInterestSpatialFilter = setFeatureOfInterestSpatialFilter;
    }

    /**
     * @return the resultSpatialFilter
     */
    public SpatialFilter getResultSpatialFilter() {
        return resultSpatialFilter;
    }

    /**
     * @param resultSpatialFilter the resultSpatialFilter to set
     */
    public void setResultSpatialFilter(SpatialFilter resultSpatialFilter) {
        this.resultSpatialFilter = resultSpatialFilter;
    }

    /**
     * returns copy of this request with passed observedProperty parameters
     * 
     * @param obsProps
     *          contains the observed property parameters
     * @return Returns copy of this request with passed observedProperty parameters
     */
    public SosGetObservationRequest copyOf(String[] obsProps) {
        SosGetObservationRequest result = new SosGetObservationRequest();
        result.setDomainFeature(this.domainFeature);
        result.setEventTime(this.eventTimes);
        result.setObservedProperty(obsProps);
        result.setOffering(this.offering);
        result.setProcedure(this.procedure);
        result.setResponseFormat(this.responseFormat);
        result.setResponseMode(this.responseMode);
        result.setResultSpatialFilter(this.resultSpatialFilter);
        result.setResult(this.result);
        result.setResultModel(this.resultModel);
        result.setFeatureOfInterest(this.featureOfInterest);
        result.setService(this.service);
        result.setSetDomainFeatureSpatialFilter(this.isSetDomainFeatureSpatialFilter());
        result.setSetFeatureOfInterestSpatialFilter(this.isSetFeatureOfInterestSpatialFilter());
        result.setSrsName(this.srsName);
        result.setRequestString(this.requestString);
        return result;

    }

}
