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

 Author: Christoph Stasch, Stephan Kuenster
 Created: 2006
 Modified: 12/15/2008
 ***************************************************************/
package org.n52.sos.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosDateTimeUtilities;
import org.n52.sos.SosConstants.GetObservationParams;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.ds.IGetObservationDAO;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.GMLConstants;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.SosCategoryObservation;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.om.SosMeasurement;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.om.SosSpatialObservation;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.domainFeatures.SosGenericDomainFeature;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.quality.SosQuality.QualityType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sos.request.SosGetObservationRequest;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
/**
 * DAO of PostgreSQL DB for GetObservation Operation. Central method is getObservation() which creates the
 * query and returns an ObservationCollection XmlBean. The method is abstract, This class is abstract, because
 * the different PostGIS versions for the different PGSQL versions return the geometries of the FOIs in
 * different ways.
 * 
 * @author Christoph Stasch
 * 
 */
public class PGSQLGetObservationDAO implements IGetObservationDAO {
	
	/** logger */
    private static Logger log = Logger.getLogger(PGSQLGetObservationDAO.class);

    /** connection pool which contains the connections to the DB */
    private PGConnectionPool cpool;
    
    /**
     * indicates, whether quality information should be queried from database or not
     */
    private boolean supportsQuality;
    /**
     * change 
     * author: juergen sorg
     * date: 2013-02-01
     */
    private boolean supportsOdmQuality;
    /** end change **/

    //
    private String tokenSeperator;
    private String tupleSeperator;
    private String noDataValue;
    private int requestSrid = -1;
    private Envelope boundedBy = null;
    private DateTime now = null;
    
    /**
     * constructor
     * 
     * @param cpool
     *        PGConnectionPool which contains the connections to the DB
     */
    public PGSQLGetObservationDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
        this.supportsQuality = SosConfigurator.getInstance().isSupportsQuality();
        /**
         * change 
         * author: juergen sorg
         * date: 2013-02-01
         */
        this.supportsOdmQuality=SosConfigurator.getInstance().isSupportOdmQuality();
        /** end change **/
        this.tokenSeperator = SosConfigurator.getInstance().getTokenSeperator();
        this.tupleSeperator = SosConfigurator.getInstance().getTupleSeperator();
        this.noDataValue = SosConfigurator.getInstance().getNoDataValue();

    }
    
	public SosObservationCollection getObservation(
			SosGetObservationRequest request) throws OwsExceptionReport {
		// setting a global "now" for this request
        now = new DateTime();
        
        // ObservationCollection object which will be returned
        SosObservationCollection response = new SosObservationCollection();
        Connection con = null;
        requestSrid = -1;
        boundedBy = null;
        try {
	        if (request.getObservedProperty().length > 0) {
	        	if(!(request.getSrsName() == null || request.getSrsName().equals("") || !request.getSrsName().startsWith(SosConfigurator.getInstance().getSrsNamePrefix()))) {
	        		requestSrid = ResultSetUtilities.parseSrsName(request.getSrsName());
	        	}
	            checkResultModel(request.getResultModel(), request.getObservedProperty());
	            
	        	List<ResultSet> resultSetList = new ArrayList<ResultSet>(); 
	        	
	        	con = cpool.getConnection();
        	
        		// if timeInstant contains "latest", return the last observation for
                // each phen/proc/foi/df
                if (request.getEventTime() != null && request.getEventTime().length > 0) {
                    for (TemporalFilter tf : request.getEventTime()) {
                        if (tf.getTime() instanceof TimeInstant) {
                            TimeInstant ti = (TimeInstant) tf.getTime();
                            if (ti.getIndeterminateValue() != null && ti.getIndeterminateValue().equals("latest")) {
                            	resultSetList.add(queryLatestObservations(request, con));
                            } else {
                            	resultSetList.add(queryObservation(request, con));
                            }
                        } else {
                        	resultSetList.add(queryObservation(request, con));
                        }
                    }
                } else {
                	resultSetList.add(queryObservation(request, con));
                }
                // end get ResultSets
            	for (ResultSet resultSet : resultSetList) {
            		int resultSetSize = getResultSetSize(resultSet);

                    // if resultModel parameter is set in the request, check,
                    // whether it is correct and then
                    // return
                    // request observations
                    QName resultModel = request.getResultModel();
                    // check ResponseMode
                    if (request.getResponseMode() != null
                            && !request.getResponseMode().equalsIgnoreCase(SosConstants.PARAMETER_NOT_SET)) {
                        if (request.getResponseMode() == SosConstants.RESPONSE_RESULT_TEMPLATE) {
                            return getResultTemplate(resultSet, request);
                        }

                    }
                    // check ResultModel
                    if (resultModel == null
	                    		|| resultModel.equals(SosConstants.RESULT_MODEL_MEASUREMENT)
	                            || resultModel.equals(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)
	                            || resultModel.equals(SosConstants.RESULT_MODEL_OBSERVATION)
	                           	|| resultModel.equals(SosConstants.RESULT_MODEL_SPATIAL_OBSERVATION)) {
                    	response.addColllection(getSingleObservationsFromResultSet(resultSet,
                              resultSetSize,
                              request,
                              resultModel));

                    }
                    else {
                        OwsExceptionReport se = new OwsExceptionReport();
                        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                             GetObservationParams.resultModel.toString(),
                                             "The value ("
                                                     + resultModel
                                                     + ") of the parameter '"
                                                     + GetObservationParams.resultModel.toString()
                                                     + "' is not supported by this SOS!");
                        throw se;
                    }
    			}
            }
            else {
            	OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.observedProperty.toString(),
                                     "The request contains no observed Properties!");
                throw se;
            }
            response.setBoundedBy(boundedBy);
            response.setSRID(requestSrid);
		} 
    	catch (OwsExceptionReport se) {
    			throw se;
		}
    	catch (Exception e) {
    		OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 "PGSQLGetObservationDAO",
                                 "Error during GetObservation: " + e.getStackTrace());
            throw se;
    	}
		finally {
            if (con != null) {
                cpool.returnConnection(con);
            }
        }
        return response;
	}// end getObservation

	public SosObservationCollection getObservationMobile(
			SosGetObservationRequest getObsRequest) throws OwsExceptionReport {
		// TODO Auto-generated method stub
		return getObservation(getObsRequest);
	}// end getObservationMobile
	
    /**
     * builds and executes the query to get the observations from the database; this method is also used from
     * the GetResultDAO
     * 
     * @param request
     *        getObservation request
     * @return Returns ResultSet containing the results of the query
     * @throws OwsExceptionReport
     *         if query failed
     */
    private ResultSet queryObservation(SosGetObservationRequest request, Connection con) throws OwsExceptionReport {

        ResultSet resultSet = null;

        // ////////////////////////////////////////////
        // get parameters from request
        String offering = request.getOffering();
        TemporalFilter[] temporalFilter = request.getEventTime();
        String[] procedures = request.getProcedure();
        String[] phenomena = request.getObservedProperty();
        boolean hasSpatialPhens = false;
        // int maxRecords = request.getMaxRecords();
        String srsName = request.getSrsName();
        if ( !srsName.equals(SosConstants.PARAMETER_NOT_SET)) {
            requestSrid = ResultSetUtilities.parseSrsName(srsName);
        }
        if (filterSpatialPhenomena(phenomena).length > 0) {
        	hasSpatialPhens = true;
        }

        // ///////////////////////////////////////////////
        // build query
        StringBuilder query = new StringBuilder();
        
        // select clause 
        query.append(getSelectClause(request.isMobileEnabled(), supportsQuality));

        // add geometry column to list, if srsName parameter is set, transform
        // coordinates into request system
        query.append(getGeometriesAsTextClause(srsName));

        // natural join of tables
        query.append(getFromClause(request.isMobileEnabled(), supportsQuality));

        query.append(" WHERE ");

        // append mandatory observedProperty parameters
        query.append(getWhereClause4ObsProps(phenomena));

        // append mandatory offering parameter
        query.append(getWhereClause4Offering(offering));

        // append feature of interest parameter
        if (request.getFeatureOfInterest() != null) {
            query.append(getWhereClause4Foi(request.getFeatureOfInterest(), hasSpatialPhens));
        }

        // append domain feature parameter
        if (request.getDomainFeature() != null) {
            query.append(getWhereClause4DomainFeature(request.getDomainFeature()));
        }

        // append optional parameters
        if (procedures != null && procedures.length > 0) {
            query.append(getWhereClause4Procedures(procedures, offering));
        }
        // append temporal filter parameter
        if (temporalFilter != null && temporalFilter.length > 0) {
            query.append(getWhereClause4Time(temporalFilter));
        }

        // append parameter for Result
        if (request.getResult() != null) {
            query.append(getWhereClause4Result(request.getResult(), offering, phenomena));
        }
        
        // append spatial filter parameter
        if (request.getResultSpatialFilter() != null) {
        	 query.append(getWhereClause4ResultSpatialFilter(request.getResultSpatialFilter()));
        }

        log.info("<<<QUERY>>>: " + query.toString());

        // //////////////////////////////////////////////////
        // execute query
        try {
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                 ResultSet.CONCUR_READ_ONLY);
            resultSet = stmt.executeQuery(query.toString());
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the data from the database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return resultSet;
    } // end queryObservation

    /**
     * creates an ObservationCollection from the db ResultSet.
     * 
     * @param resultSet
     * 			ResultSet with the queried information
     * @param resultSetSize
     * 			Size of the ResultSet
     * @param request
     * 			getObservation request
     * @param resultModel
     * 			QName of the result model
     * @return
     * 			SosObservationCollection
     * @throws OwsExceptionReport
     */
    private SosObservationCollection getSingleObservationsFromResultSet(ResultSet resultSet,
																		    int resultSetSize,
																		    SosGetObservationRequest request,
																		    QName resultModel) throws OwsExceptionReport {
    	 SosObservationCollection obsCol = new SosObservationCollection();
    	 HashMap<String, AbstractSosObservation> obs4obsIDs = new HashMap<String, AbstractSosObservation>();
    	 HashMap<String, AbstractSosObservation> obs4Procs = new HashMap<String, AbstractSosObservation>();

         /*
          * int startPosition = request.getStartPosition(); int numberOfRecordsReturned = 0;
          * 
          * DateTime startDateTime = null; DateTime endDateTime = null;
          */
         boundedBy = null;
         int srid = 0;
         try {

             // now iterate over resultset and create Measurement for each row
             while (resultSet.next()) {

                 // check remaining heap size
                 checkFreeMemory();

                 String obsID = resultSet.getString(PGDAOConstants.obsIDCn);

                 if (obs4obsIDs.containsKey(obsID)) {
                	 // is mobile
                	 if (request.isMobileEnabled()) {
                		 AbstractSosObservation obs = obs4obsIDs.get(obsID);
                         String dfID = resultSet.getString(PGDAOConstants.domainFeatureIDCn);
                         obs.addDomainFeatureID(new SosGenericDomainFeature(dfID));
                	 }
                	 // supports quality
                	 if (supportsQuality) {
                		 String qualityTypeString = resultSet.getString(PGDAOConstants.qualTypeCn);
                         String qualityUnit = resultSet.getString(PGDAOConstants.qualUnitCn);
                         String qualityName = resultSet.getString(PGDAOConstants.qualNameCn);
                         String qualityValue = resultSet.getString(PGDAOConstants.qualValueCn);
                         QualityType qualityType = QualityType.valueOf(qualityTypeString);
                         SosQuality quality = new SosQuality(qualityName,
                                                             qualityUnit,
                                                             qualityValue,
                                                             qualityType);
                         obs4obsIDs.get(obsID).addSingleQuality(quality);
                	 }
                     
                 }
                 else {
                	 String offeringID = resultSet.getString(PGDAOConstants.offeringIDCn);
                	 String mimeType = SosConstants.PARAMETER_NOT_SET;

                     // create time element
                     String timeString = resultSet.getString(PGDAOConstants.timestampCn);
                     DateTime timeDateTime = SosDateTimeUtilities.parseIsoString2DateTime(timeString);
                     TimeInstant time = new TimeInstant(timeDateTime, "");

                     String phenID = resultSet.getString(PGDAOConstants.phenIDCn);
                     String valueType = resultSet.getString(PGDAOConstants.valueTypeCn);
                     String procID = resultSet.getString(PGDAOConstants.procIDCn);
                     
                     String unit = resultSet.getString(PGDAOConstants.unitCn);

                     // domain feature
                     String domainFeatID = null;
                     ArrayList<SosAbstractFeature> domainFeatIDs = null;
                     if (request.isMobileEnabled()) {
                    	 domainFeatID = resultSet.getString(PGDAOConstants.domainFeatureIDCn);
                         domainFeatIDs = new ArrayList<SosAbstractFeature>();
                         domainFeatIDs.add(new SosGenericDomainFeature(domainFeatID));
                     }
                     
                     // feature of interest
                     String foiID = resultSet.getString(PGDAOConstants.foiIDCn);
                     String foiName = resultSet.getString(PGDAOConstants.foiNameCn);
                     String foiType = resultSet.getString(PGDAOConstants.featureTypeCn);

                     // foi geometry
                     String foiGeomWKT = resultSet.getString(PGDAOConstants.foiGeometry);
                     srid = checkRequestSridQuerySrid(resultSet.getInt(PGDAOConstants.foiSrid));
                     SosAbstractFeature foi = ResultSetUtilities.getAbstractFeatureFromValues(foiID, SosConstants.PARAMETER_NOT_SET, foiName, foiGeomWKT, srid, foiType, SosConstants.PARAMETER_NOT_SET);
                     boundedBy = checkEnvelope(boundedBy, foi.getGeom());
                    
                     // create quality
                     ArrayList<SosQuality> qualityList = null;
                     
                     if (supportsQuality) {
                    	 String qualityTypeString = resultSet.getString(PGDAOConstants.qualTypeCn);
                         String qualityUnit = resultSet.getString(PGDAOConstants.qualUnitCn);
                         String qualityName = resultSet.getString(PGDAOConstants.qualNameCn);
                         String qualityValue = resultSet.getString(PGDAOConstants.qualValueCn);
                         qualityList = new ArrayList<SosQuality>();
                         if (qualityValue != null) {
                             QualityType qualityType = QualityType.valueOf(qualityTypeString);
                             SosQuality quality = new SosQuality(qualityName,
                                                                 qualityUnit,
                                                                 qualityValue,
                                                                 qualityType);
                             qualityList.add(quality);
                         }
                     }
                     /**
                      * change 
                      * author: juergen sorg
                      * date: 2013-02-01
                      */
                     else if (this.supportsOdmQuality){
                    	 String qvalue=resultSet.getString(PGDAOConstants.textValueCn);
                    	 log.debug(new StringBuffer("odm quality flag: ").append(qvalue).toString());
                    	 //modified by ASD SosQuality quality = new SosQuality("qualityName","qualityUnit",qvalue,QualityType.text);
                    	 SosQuality quality = new SosQuality("QualityFlag","qualityUnit",qvalue,QualityType.text);
                    	 qualityList = new ArrayList<SosQuality>();
                    	 qualityList.add(quality);
                     }
                     
                     if (request.getResponseMode() != null
                             && !request.getResponseMode().equals(SosConstants.PARAMETER_NOT_SET)) {
                         // if responseMode is resultTemplate, then create observation template and return it
                         if (request.getResponseMode() == SosConstants.RESPONSE_RESULT_TEMPLATE) {
                             return getResultTemplate(resultSet, request);
                         }
                         else {
                             checkResponseModeInline(request.getResponseMode());
                         }
                     }
                     // if (Measurement, CategoryObs, Spatial, Observ)
                     if (resultModel == null || resultModel.equals(SosConstants.RESULT_MODEL_OBSERVATION)) {
                    	 String value;
                         if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.name())) {
                             value = resultSet.getString(PGDAOConstants.numericValueCn);
                         } 
                         else if (valueType.equalsIgnoreCase(SosConstants.ValueTypes.textType.name())) {
                        	 value = resultSet.getString(PGDAOConstants.textValueCn);
                         }
                         else {
                        	 OwsExceptionReport se = new OwsExceptionReport();
                 	         se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                 	        		 valueType,
                 	                 "The valueType '" + valueType + "' is not supported for om:Observation or no resultModel!");
                 	        log.error("The valueType '" + valueType + "' is not supported for om:Observation or no resultModel!",
                 	                  se);
                 	        throw se;
                         }
                         //change juergen
                         SosQuality quality=null;
                         if(qualityList!=null && qualityList.size()>0){
                        	 quality=qualityList.get(0);
                         }
                         //change end
                         if (obs4Procs.containsKey(procID)) {
                             SosGenericObservation sosGenObs = (SosGenericObservation) obs4Procs.get(procID);
                             sosGenObs.addFeature(foi);
                            
                             sosGenObs.addValue(timeDateTime, foiID, phenID, value
                                     /**change
                                      * author: juergen sorg
                                      * date: 2013-01-28
                                      */
                                     ,quality
                                     /** change end **/);
                         }
                         else {
                             SosGenericObservation observation = new SosGenericObservation(new ArrayList<String>(),
                                                                                           procID,
                                                                                           offeringID,
                                                                                           tokenSeperator,
                                                                                           tupleSeperator,
                                                                                           noDataValue);
                             observation.addFeature(foi);
                             observation.addValue(timeDateTime, foiID, phenID, value
                                     /**change
                                      * author: juergen sorg
                                      * date: 2013-01-28
                                      */
                                     ,quality
                                     /** change end **/);
                             observation.setObservationID(obsID);
                             obs4Procs.put(procID, observation);
                         }
                     }
                     else if (resultModel.equals(SosConstants.RESULT_MODEL_MEASUREMENT)
                             || valueType.equalsIgnoreCase(SosConstants.ValueTypes.numericType.toString())) {

                         // if responseMode is resultTemplate, then create observation template and return it
                         if (request.getResponseMode() == SosConstants.RESPONSE_RESULT_TEMPLATE) {
                             return getResultTemplate(resultSet, request);
                         }
                         else {
                             checkResponseModeInline(request.getResponseMode());
                         }

                         double value = Double.NaN;
                         if (resultSet.getString(PGDAOConstants.numericValueCn) != null)
                             value = resultSet.getDouble(PGDAOConstants.numericValueCn);

                         SosMeasurement measurement = new SosMeasurement(time,
                                                                         obsID,
                                                                         procID,
                                                                         domainFeatIDs,
                                                                         phenID,
                                                                         foi,
                                                                         offeringID,
                                                                         mimeType,
                                                                         value,
                                                                         unit,
                                                                         qualityList);
                         obs4obsIDs.put(obsID, measurement);
                     }
                     else if (resultModel.equals(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)) {

                         checkResponseModeInline(request.getResponseMode());

                         String value = resultSet.getString(PGDAOConstants.textValueCn);

                         SosCategoryObservation categoryObservation = new SosCategoryObservation(time,
                                                                                                 obsID,
                                                                                                 procID,
                                                                                                 foi,
                                                                                                 domainFeatIDs,
                                                                                                 phenID,
                                                                                                 offeringID,
                                                                                                 mimeType,
                                                                                                 value,
                                                                                                 unit,
                                                                                                 qualityList);
                         obs4obsIDs.put(obsID, categoryObservation);
                     }
                     else if (resultModel.equals(SosConstants.RESULT_MODEL_SPATIAL_OBSERVATION)) {
                    	 String value_geomWKT = resultSet.getString(PGDAOConstants.valueGeometry);
                    	 srid = checkRequestSridQuerySrid(resultSet.getInt(PGDAOConstants.valueSrid));
                         Geometry jts_value_geometry = ResultSetUtilities.createJTSGeom(value_geomWKT, srid);
                         boundedBy = checkEnvelope(boundedBy, jts_value_geometry);

                         SosSpatialObservation spatialObs = new SosSpatialObservation(time,
                                                                                      obsID,
                                                                                      procID,
                                                                                      domainFeatIDs,
                                                                                      phenID,
                                                                                      foi,
                                                                                      offeringID,
                                                                                      jts_value_geometry,
                                                                                      qualityList);
                         obs4obsIDs.put(obsID, spatialObs);
                     }
                 }
             }
         }
         catch (SQLException sqle) {
             OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
             se.addCodedException(ExceptionCode.NoApplicableCode,
                                  null,
                                  "Error while creating observations from database query result set: "
                                          + sqle.getMessage());
             log.error(se.getMessage());
             throw se;
         }

         if (obs4obsIDs.size() == 0 ) {
             if (obs4Procs.size() == 0) {
                 return obsCol;
             } else {
            	 obsCol.setObservationMembers(obs4Procs.values());
             }
         } else {
        	 obsCol.setObservationMembers(obs4obsIDs.values());
         }

         /*
          * TimePeriod obsColTime = new TimePeriod(startDateTime, endDateTime); obsCol.setTime(obsColTime);
          */
         obsCol.setBoundedBy(boundedBy);
         obsCol.setSRID(srid);
    	
    	return obsCol;
    }// end getSingleObservationFromResultSet
    
    
    /**
     * Returns an Observation as ResultTemplate.
     * 
     * @param getObsReq
     * @return
     * @throws OwsExceptionReport
     */
    private SosObservationCollection getResultTemplate(ResultSet resultSet,
                                                       SosGetObservationRequest request) throws OwsExceptionReport {

        if (request.getProcedure().length > 1) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "The request contains more than one procedure!");
            log.error(se.getMessage());
            throw se;
        }

        DateTime begin = now;
        DateTime end = SosDateTimeUtilities.calculateExpiresDateTime(begin);
        TimePeriod timePeriod = new TimePeriod(begin, end);

        SosObservationCollection obsCol = new SosObservationCollection();

        String[] reqPhenArray = request.getObservedProperty();
        ArrayList<String> requestedPhenomena = new ArrayList<String>(reqPhenArray.length);
        for (int i = 0; i < reqPhenArray.length; i++) {
            requestedPhenomena.add(reqPhenArray[i]);
        }

        String offeringID = request.getOffering();
        List<String> procIDs = new ArrayList<String>();
        Collection<AbstractSosObservation> observationMembers = new ArrayList<AbstractSosObservation>(1);
        try {
            while (resultSet.next()) {

                // check remaining heap size
                checkFreeMemory();

                String procID = resultSet.getString(PGDAOConstants.procIDCn);
                if ( !procIDs.contains(procID)) {
                    SosGenericObservation sosGenObs = new SosGenericObservation(requestedPhenomena,
                                                                                offeringID,
                                                                                tokenSeperator,
                                                                                tupleSeperator,
                                                                                noDataValue);

                    String foiID;
                    foiID = resultSet.getString(PGDAOConstants.foiIDCn);
                    sosGenObs.addFoiId(foiID);
                    sosGenObs.setProcedureID(procID);

                    // insert request into database for getResult requests
                    String obsTempId = insertGetObsRequest(request, begin, end, procID);
                    sosGenObs.setObservationID(obsTempId);
                    sosGenObs.setSamplingTime(timePeriod);
                    obsCol.setSamplingTime(timePeriod);
                    observationMembers.add(sosGenObs);

                    procIDs.add(procID);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while creating observations from database query result set: "
                                         + sqle.getMessage());
            log.error(se.getMessage());
            throw se;
        }

        obsCol.setObservationMembers(observationMembers);

        return obsCol;
    } // end getResultTemplate
    
    /**
     * builds the where clause condition for the result parameter
     * 
     * @param comparisonFilter
     *        The comparisonFilter, which represents the result parameter of the request
     * @param offering
     *        offering parameter of the getObservation request
     * @return Returns where clause condition of the request
     * @throws OwsExceptionReport
     *         if the result request parameter is incorrect
     */
    private String getWhereClause4Result(ComparisonFilter comparisonFilter,
                                         String offering,
                                         String[] phenomena) throws OwsExceptionReport {
        String result = "";
        String property = comparisonFilter.getPropertyName();

        CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();

        //modified by ASD
        if(property.equalsIgnoreCase("QualityFlag"))
        {
            	log.debug("Filter based on QualityFlag..");
                result = getWhereClause4ResultTextModified(comparisonFilter);
        }
        else
        {
        // if property is not contained
        if ( !capsCache.getValueTypes4ObsProps().containsKey(property)) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.result.name(),
                                 "Value of propertyName is not set or not supported!");
            throw se;
        }

        // if property is not specified as observedProperty
        List<String> phenomena_ = new ArrayList<String>(Arrays.asList(phenomena));
        if ( !phenomena_.contains(property)) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.result.name(),
                                 "Value of propertyName (" + property
                                         + ") is not specified as observedProperty!");
            throw se;
        }

        // check if comparisonFilter contains composite phenomenon as property;
        // If so, throw Exception,
        // because only single phenomena are supported
        if (capsCache.getOffCompPhens().containsValue(offering)) {
            if (capsCache.getOffCompPhens().get(offering).contains(property)) {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.result.name(),
                                     "The property of a result parameter has to be a single phenomenon and must not be a composite phenomenon. If you want to use a composite phenomenon, check the components and use one of them!");
                throw se;
            }
        }

        else {
            ValueTypes valueType = capsCache.getValueTypes4ObsProps().get(property);

            switch (valueType) {
            case textType:
                result = getWhereClause4ResultText(comparisonFilter);
                break;
            default:
                result = getWhereClause4ResultMeasure(comparisonFilter);
                break;
            }
        }
        }
        return result;
    } // end getWhereClause4Result

    /**
     * creates where clause for the result parameter of the getObservation request
     * 
     * @param result
     *        Result which represents the result element of the getObservation request
     * @return String containing the where clause for result parameter
     * @throws XmlException
     *         if parsing the node into an XmlObject failed
     * @throws OwsExceptionReport
     *         if required elements are missed
     */
    private String getWhereClause4ResultMeasure(ComparisonFilter result) throws OwsExceptionReport {

        ComparisonOperator operator = result.getOperator();
        String value = result.getValue();

        StringBuilder resultClause = new StringBuilder(" AND ");
        switch (operator) {
        case PropertyIsBetween:
            String upperValue = result.getValueUpper();
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn
                    + " > " + value + " AND " + PGDAOConstants.obsTn + "."
                    + PGDAOConstants.numericValueCn + " < " + upperValue + ")");
            break;
        case PropertyIsEqualTo:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " = "
                    + value);
            break;
        case PropertyIsGreaterThan:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " > "
                    + value);
            break;
        case PropertyIsGreaterThanOrEqualTo:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " >= "
                    + value);
            break;
        case PropertyIsLessThan:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " < "
                    + value);
            break;
        case PropertyIsLessThanOrEqualTo:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " <= "
                    + value);
            break;
        case PropertyIsNotEqualTo:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " <> "
                    + value);
            break;
        // case PropertyIsLike:
        // resultClause += "to_char(" + PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn +
        // ",'FM999999999D09') LIKE '"
        // + value + "' ESCAPE '" + escapeString + "'";
        // break;
        case PropertyIsNull:
            resultClause.append(PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + " is null");
            break;

        default:
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.result.name(),
                                 "The (" + operator.name() + ") is not supported by this SOS!");
            throw se;
        }

        return resultClause.toString();
    } // end getWhereClause4ResultMeasure


    /**
     * creates where clause for the result parameter of the getObservation request
     * 
     * @param result
     *        Result which represents the result element of the getObservation request
     * @return String containing the where clause for result parameter
     * @throws XmlException
     *         if parsing the node into an XmlObject failed
     * @throws OwsExceptionReport
     *         if required elements are missed
     */
    //modified by ASD
    private String getWhereClause4ResultTextModified(ComparisonFilter result) throws OwsExceptionReport {

        ComparisonOperator operator = result.getOperator();
        String value = result.getValue();
        String escapeString = result.getEscapeString();
        String wildCard = result.getWildCard();
        String singleChar = result.getSingleChar();

        if (value != null) {
        	value = value.replace(escapeString + wildCard, PGDAOConstants.ESCAPECHAR + wildCard);
            value = value.replace(escapeString + singleChar, PGDAOConstants.ESCAPECHAR + singleChar);

            if (value.contains(PGDAOConstants.ESCAPECHAR)) {
                String[] value_ = value.split(PGDAOConstants.ESCAPECHAR);
                String valueNew = "";
                for (int i = 0; i < value_.length; i++) {
                    String s = value_[i];
                    if (s.length() > 1) {
                        if (i > 0) {
                            String s_ = s.substring(1);
                            s_ = s_.replace(wildCard, PGDAOConstants.WILDCARD);
                            s_ = s_.replace(singleChar, PGDAOConstants.SINGLECHAR);
                            s = s.substring(0, 1) + s_;
                        }
                        else {
                            s = s.replace(wildCard, PGDAOConstants.WILDCARD);
                            s = s.replace(singleChar, PGDAOConstants.SINGLECHAR);
                        }
                    }
                    valueNew = valueNew + s;
                    if (valueNew.length() < value.length())
                        valueNew = valueNew + PGDAOConstants.ESCAPECHAR;
                    log.info("valueNew: " + valueNew);
                }
                value = valueNew;
            }
            else if (wildCard != null && singleChar != null){
                value = value.replace(wildCard, PGDAOConstants.WILDCARD);
                value = value.replace(singleChar, PGDAOConstants.SINGLECHAR);
            }
        }

        // String resultClause = " AND CASE " + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn
        // + "='" + property + "' THEN ";
        // String resultClause = " AND " + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn
        // + "='" + property + "' ";
        StringBuilder resultClause = new StringBuilder(" AND ");

        switch (operator) {
        case PropertyIsEqualTo:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn + " = '"
                    + value + "')");
            break;
        case PropertyIsLike:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " LIKE '" + value + "' ESCAPE '" + PGDAOConstants.ESCAPECHAR + "')");
            break;
        case PropertyIsNotEqualTo:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " <> '" + value + "')");
            break;
        case PropertyIsNull:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " is null)");
            break;
        default:
            String message = "The filter property (" + operator.name()
                    + ") is not supported by this SOS for text result filter!";
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationParams.result.name(),
                                 message);
            log.error(message);
            throw se;
        }
        
        return resultClause.toString();
    } // end getWhereClause4ResultText
    
    /**
     * creates where clause for the result parameter of the getObservation request
     * 
     * @param result
     *        Result which represents the result element of the getObservation request
     * @return String containing the where clause for result parameter
     * @throws XmlException
     *         if parsing the node into an XmlObject failed
     * @throws OwsExceptionReport
     *         if required elements are missed
     */
    private String getWhereClause4ResultText(ComparisonFilter result) throws OwsExceptionReport {

        ComparisonOperator operator = result.getOperator();
        String value = result.getValue();
        String escapeString = result.getEscapeString();
        String wildCard = result.getWildCard();
        String singleChar = result.getSingleChar();

        if (value != null) {
        	value = value.replace(escapeString + wildCard, PGDAOConstants.ESCAPECHAR + wildCard);
            value = value.replace(escapeString + singleChar, PGDAOConstants.ESCAPECHAR + singleChar);

            if (value.contains(PGDAOConstants.ESCAPECHAR)) {
                String[] value_ = value.split(PGDAOConstants.ESCAPECHAR);
                String valueNew = "";
                for (int i = 0; i < value_.length; i++) {
                    String s = value_[i];
                    if (s.length() > 1) {
                        if (i > 0) {
                            String s_ = s.substring(1);
                            s_ = s_.replace(wildCard, PGDAOConstants.WILDCARD);
                            s_ = s_.replace(singleChar, PGDAOConstants.SINGLECHAR);
                            s = s.substring(0, 1) + s_;
                        }
                        else {
                            s = s.replace(wildCard, PGDAOConstants.WILDCARD);
                            s = s.replace(singleChar, PGDAOConstants.SINGLECHAR);
                        }
                    }
                    valueNew = valueNew + s;
                    if (valueNew.length() < value.length())
                        valueNew = valueNew + PGDAOConstants.ESCAPECHAR;
                    log.info("valueNew: " + valueNew);
                }
                value = valueNew;
            }
            else if (wildCard != null && singleChar != null){
                value = value.replace(wildCard, PGDAOConstants.WILDCARD);
                value = value.replace(singleChar, PGDAOConstants.SINGLECHAR);
            }
        }

        // String resultClause = " AND CASE " + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn
        // + "='" + property + "' THEN ";
        // String resultClause = " AND " + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn
        // + "='" + property + "' ";
        StringBuilder resultClause = new StringBuilder(" AND ");

        switch (operator) {
        case PropertyIsEqualTo:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn + " = '"
                    + value + "')");
            break;
        case PropertyIsLike:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " LIKE '" + value + "' ESCAPE '" + PGDAOConstants.ESCAPECHAR + "')");
            break;
        case PropertyIsNotEqualTo:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " <> '" + value + "')");
            break;
        case PropertyIsNull:
            resultClause.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn
                    + " is null)");
            break;
        default:
            String message = "The filter property (" + operator.name()
                    + ") is not supported by this SOS for text result filter!";
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationParams.result.name(),
                                 message);
            log.error(message);
            throw se;
        }
        return resultClause.toString();
    } // end getWhereClause4ResultText

    /**
	 * creates where clause for the result parameter of the getObservation request
	 * @param spFil
	 * 			Result which represents the result element of the getObservation request
	 * @return String containing the where clause for result parameter
	 * @throws OwsExceptionReport
	 * 			if required elements are missed
	 */
	private String getWhereClause4ResultSpatialFilter(SpatialFilter spFil) throws OwsExceptionReport {
	    StringBuilder query = new StringBuilder();
	    int srid = spFil.getSrid();
	    if (requestSrid == -1) {
	        requestSrid = srid;
	    }
	
	    query.append(" AND " + PGSQLQueryUtilities.getWhereClause4SpatialFilter(spFil, PGDAOConstants.obsTn, PGDAOConstants.spatialValueCn, srid));
	
	    return query.toString();
	}// end getWhereClause4ResultSpatialFilter

	/**
     * constructs the where clause if a feature of interest is contained in the observation request
     * 
     * @param spFil
     *        the FeatureOfInterest for which the where clause should be returned
     * @return where clause of the FeatureOfInterest for the observation query
     * @throws OwsExceptionReport
     *         if construction of the where clause failed
     * @throws XmlException
     *         if construction of the where clause failed
     */
    private String getWhereClause4Foi(SpatialFilter spFil, boolean hasSpatialPhens) throws OwsExceptionReport {

        // query String
        StringBuilder query = new StringBuilder();

        // get ObjectIDs
        String[] objectIds = spFil.getFoiIDs();

        // if objectIDs not null, create where clause for objectIds an return
        // the where clause
        if (objectIds != null && objectIds.length != 0) {

            query.append(" AND (" + PGDAOConstants.foiTn + "." + PGDAOConstants.foiIDCn + " = '"
                    + objectIds[0] + "'");

            if (objectIds.length > 1) {
                for (String objectId : objectIds) {
                    query.append(" OR " + PGDAOConstants.foiTn + "." + PGDAOConstants.foiIDCn + " = '"
                            + objectId + "'");
                }
            }

            query.append(")");

            return query.toString();
            
        } else if (!hasSpatialPhens && spFil.getOperator() != null) {
        	// else if no objectIds are set, a spatialFilter is set, so parse this
            // and create therefor
            // a where clause

            int srid = spFil.getSrid();
            if (requestSrid == -1) {
                requestSrid = srid;
            }

            if ( !SosConfigurator.getInstance().getCapsCacheController().getSrids().contains(srid)) {
                String message = "The epsgCode '"
                        + srid
                        + "' of coordinates in featureOfInterest parameter is not supported by this SOS!!";
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     SosConstants.GetObservationParams.featureOfInterest.name(),
                                     message);
                log.error(message);
                throw se;
            }

            query.append(" AND " + PGSQLQueryUtilities.getWhereClause4SpatialFilter(spFil, PGDAOConstants.foiTn, PGDAOConstants.geomCn, requestSrid));
            query.toString();
        } 
        else {
        	OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationParams.featureOfInterest.toString(),
                                 "The spatial filter for observation request of spatial phenomena should be contained in result element and not in sampling feature filter!");
            throw se;
        }

        
        return query.toString();
    } // end getWhereClause4Foi

    /**
     * constructs the where clause if a domain feature is contained in the observation request
     * 
     * @param domainFeature
     * 			the DomainFeature for which the where clause should be returned
     * @return where clause of the DomainFeature for the observation query
     * @throws OwsExceptionReport
     * 			if construction of the where clause failed
     */
    private String getWhereClause4DomainFeature(SpatialFilter spFil) throws OwsExceptionReport {
        // query String
        StringBuilder query = new StringBuilder();

        // get ObjectIDs 
        String[] objectIds = spFil.getFoiIDs();

        // if objectIDs not null, create where clause for objectIds an return
        // the where clause
        if (objectIds != null && objectIds.length != 0) {

            query.append(" AND (" + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn
                    + " = '" + objectIds[0] + "'");

            if (objectIds.length > 1) {
            	for (String objectId : objectIds) {
            		query.append(" OR " + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn
                            + " = '" + objectId + "'");
				}
            }

            query.append(")");

            return query.toString();
        }

        // else if no objectIds are set, a spatialFilter is set, so parse this
        // and create therefor
        // a where clause

        int srid = spFil.getSrid();
        if (requestSrid == -1) {
            requestSrid = srid;
        }

        String postfix = " OR " + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureGeomCn + " IS NULL ";
        query.append(" AND " + PGSQLQueryUtilities.getWhereClause4SpatialFilter(spFil, PGDAOConstants.dfTn, PGDAOConstants.domainFeatureGeomCn, srid) + postfix);

        return query.toString();
    }// end getWhereClause4DomainFeature

    /**
     * method creates where clause section for observedProperties
     * 
     * @param observedProperties
     * 
     * @return String containing the where clause for observedProperties
     * 
     */
    private String getWhereClause4ObsProps(String[] observedProperties) {

        StringBuffer result = new StringBuffer();

        if (observedProperties != null) {
            result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn + " = " + "'"
                    + observedProperties[0] + "'");

            for (int i = 1; i < observedProperties.length; i++) {
                result.append(" OR " + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn + " = "
                        + "'" + observedProperties[i] + "' ");
            }

            result.append(")");
        }

        return result.toString();
    } // end getWhereClause4ObsProps

    /**
     * method creates where clause section for offering
     * 
     * @param offeringID
     * 
     * @return String containing the where clause for offering
     * 
     */
    private String getWhereClause4Offering(String offeringID) {
        StringBuilder result = new StringBuilder();
        result.append(" AND (");
        result.append(PGDAOConstants.offeringIDCn + " = '" + offeringID);
        result.append("')");
        return result.toString();
    }// end getWhereClause4Offering

    /**
     * returns the where clause for the EventTime parameters of the observation request
     * 
     * @param times
     *        EventTime[] which contains the PGConfigurator.timestampCns or TimePeriods for which the where
     *        clause should be constructed
     * @return String containing the where clause for this EventTime[]
     * @throws XmlException
     *         if construction of the where clause failed
     * @throws OwsExceptionReport
     *         if construction of the where clause failed
     */
    private String getWhereClause4Time(TemporalFilter[] times) throws OwsExceptionReport {

        // string which contains the where clause
        StringBuffer query = new StringBuffer();

        // "latest" not relevant here
        ArrayList<TemporalFilter> times_ = new ArrayList<TemporalFilter>();
        for (TemporalFilter tf : times) {
            ISosTime time = tf.getTime();
            if (time instanceof TimeInstant) {
                if (((TimeInstant) time).getIndeterminateValue() == null) {
                	times_.add(tf);
                } else {
                	if (!((TimeInstant) time).getIndeterminateValue().equalsIgnoreCase("latest")) {
                		times_.add(tf);
                	}
                }
            }
            else
                times_.add(tf);
        }

        if (times_.size() == 0)
            return "";

        query.append(" AND ( ");

        TemporalFilter temporalFilter = times_.get(0);

        ISosTime time = temporalFilter.getTime();
        if (time instanceof TimeInstant) {
            query.append(getWhereClause4timeInstant((TimeInstant) time,
                                                       temporalFilter.getOperator()));
        }

        if (time instanceof TimePeriod) {
            query.append(getWhereClause4timePeriod((TimePeriod) time,
                                                      temporalFilter.getOperator()));
        }

        // get TimeNodes
        for (int j = 1; j < times_.size(); j++) {

            query.append(" OR ( ");

            temporalFilter = times_.get(j);

            time = temporalFilter.getTime();
            if (time instanceof TimeInstant) {
                query.append(getWhereClause4timeInstant((TimeInstant) time,
                                                           temporalFilter.getOperator()));
            }

            if (time instanceof TimePeriod) {
                query.append(getWhereClause4timePeriod((TimePeriod) time,
                                                          temporalFilter.getOperator()));
            }

            query.append(" )");

        }

        query.append(" )");

        return query.toString();
    } // end getWhereClause4Time

    /**
     * creates and returns the where clause for time period parameter
     * 
     * @param timePeriod
     *        TimePeriod object for which the where clause should be created
     * @param operator
     *        name of timeObs element (e.g. ogc:after or ogc:TEquals)
     * @param offeringID
     *        tableName of the PGConfigurator.obsTn
     * @return String containing the where clause for time period parameter
     * @throws ParseException
     *         if parsing begin or end DateTime failed
     * @throws OwsExceptionReport
     *         if one parameter is incorrect or a required paramter is missing
     */
    private String getWhereClause4timePeriod(TimePeriod timePeriod, TimeOperator operator) throws OwsExceptionReport {

        DateTime start = timePeriod.getStart();
        String indetStartTime = timePeriod.getStartIndet();
        DateTime end = timePeriod.getEnd();
        String indetEndTime = timePeriod.getEndIndet();
        Period duration = timePeriod.getDuration();

        // setting start to now if indetStartTime is "now"
        if (indetStartTime != null
                && indetStartTime.equalsIgnoreCase(GMLConstants.IndetTimeValues.now.name())) {
            start = now;
        }
        // setting end to now if indetEndTime is "now"
        if (indetEndTime != null
                && indetEndTime.equalsIgnoreCase(GMLConstants.IndetTimeValues.now.name())) {
            end = now;
        }
        // setting end to max tst in database
        if (indetEndTime != null
                && indetEndTime.equals(GMLConstants.IndetTimeValues.unknown.name())) {
            end = SosConfigurator.getInstance().getFactory().getConfigDAO().getMaxDate4Observations().plusMillis(1);
        }
        // setting start to min tst in database
        if (indetStartTime != null
                && indetStartTime.equals(GMLConstants.IndetTimeValues.unknown.name())) {
            start = SosConfigurator.getInstance().getFactory().getConfigDAO().getMinDate4Observations().minusMillis(1);
        }

        // is tst specified for 'after' or 'before' in beginPosition
        if (indetStartTime != null
                && (indetStartTime.equals(GMLConstants.IndetTimeValues.after.name()) || indetStartTime.equals(GMLConstants.IndetTimeValues.before.name()))
                && start == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.eventTime.toString(),
                                 "The value of indeterminatePosition = '" + indetStartTime
                                         + "' of beginPosition in TimePeriod"
                                         + " requires a specified timestamp");
            throw se;
        }

        // is tst specified for 'after' or 'before' in endPosition
        if (indetEndTime != null
                && (indetEndTime.equals(GMLConstants.IndetTimeValues.after.name()) || indetEndTime.equals(GMLConstants.IndetTimeValues.before.name()))
                && end == null) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                 GetObservationParams.eventTime.toString(),
                                 "The value of indeterminatePosition = '" + indetEndTime
                                         + "' of endPosition in TimePeriod"
                                         + " requires a specified timestamp");
            throw se;
        }

        StringBuilder result = new StringBuilder();

        switch (operator) {

        // before
        case TM_Before:
            // //if indetPosition of endPosition = After throw exception!
            if (indetEndTime != null) {
                if (indetEndTime.equals(GMLConstants.IndetTimeValues.after.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "The value of indeterminatePosition = '"
                                                 + indetEndTime
                                                 + "'  of EndPosition in TimePeriod"
                                                 + " is in conflict with the temporal operator of eventTime = '"
                                                 + operator);
                    throw se;
                }
            }
            result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
            		+ " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end) + "'" + ")");
            break; // end before

        // after
        case TM_After:
            // if indetPosition of beginPosition = Before throw exception!
            if (indetStartTime != null) {
                if (indetStartTime.equals(GMLConstants.IndetTimeValues.before.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "The value of indeterminatePosition = '"
                                                 + indetStartTime
                                                 + "'  of StartPosition in TimePeriod"
                                                 + " is in conflict with the temporal operator of eventTime = '"
                                                 + operator);
                    throw se;
                }
            }
            result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
            		+ " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start) + "'" + ")");
            break; // end after

        // during
        case TM_During:

            /*
             * if duration not null, values between a duration should be returned. Only the endPosition
             * PGConfigurator.timestampCn or indeterminateTime of endPosition is read in. The begin Position
             * is not received attention. A timePeriod element which works may look like: <ogc:During>
             * <gml:TimePeriod> <gml:beginPosition indeterminatePosition="unknown"></gml:beginPosition>
             * <gml:endPosition indeterminatePosition="now">2005-10-05T10:17:00</gml:endPosition>
             * <gml:duration>P5M</gml:duration> </gml:TimePeriod> </ogc:During>
             */
            if (duration != null) {

                if (indetEndTime != null
                        && (indetEndTime.equals(GMLConstants.IndetTimeValues.now.name()) 
                                || indetEndTime.equals(GMLConstants.IndetTimeValues.unknown.name()))) {
                    end = now;
                }
                else if (indetEndTime != null
                        && (indetEndTime.equals(GMLConstants.IndetTimeValues.after.name()))
                        || indetEndTime.equals(GMLConstants.IndetTimeValues.before.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "If duration is set indeterminatePositions 'after' and 'before' are not allowed.");
                    throw se;
                }

                else if (end == null) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "If duration is set, either timePosition of endPosition in timePeriod"
                                                 + " must be set or indeterminatePosition of endPosition must be 'now' or 'unknown', where"
                                                 + "'unknown' accords to 'now'.");
                    throw se;
                }
                DateTime begin = end.minus(duration);

                result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                		+ " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(begin) + "'"
                        + " AND " + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                        + " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end) + "'" + ")");

            }

            else {
                if (indetStartTime != null
                        && indetStartTime.equals(GMLConstants.IndetTimeValues.before.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "The value of indeterminatePosition of StartPosition ='"
                                                 + indetStartTime
                                                 + "' in TimePeriod"
                                                 + " is in conflict with the temporal operator of eventTime = '"
                                                 + operator);
                    throw se;
                }
                else if (indetEndTime != null
                        && indetEndTime.equals(GMLConstants.IndetTimeValues.after.name())) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                         GetObservationParams.eventTime.toString(),
                                         "The value of indeterminatePosition of EndPosition ='"
                                                 + indetEndTime
                                                 + "' in TimePeriod"
                                                 + " is in conflict with the temporal operator of eventTime = '"
                                                 + operator);
                    throw se;
                }

                result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                		+ " > " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start) + "'"
                        + " AND " + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                        + " < " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end) + "'" + ")");

            }
            break; // end during

        // after
        case TM_Equals:

            if (indetStartTime != null
                    && indetStartTime.equals(GMLConstants.IndetTimeValues.before.name())) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.eventTime.toString(),
                                     "The value of indeterminatePosition of StartPosition ='"
                                             + indetStartTime
                                             + "' in TimePeriod"
                                             + " is in conflict with the temporal operator of eventTime = '"
                                             + operator);
                throw se;
            }
            else if (indetEndTime != null
                    && indetEndTime.equals(GMLConstants.IndetTimeValues.after.name())) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     GetObservationParams.eventTime.toString(),
                                     "The value of indeterminatePosition of EndPosition ='"
                                             + indetEndTime
                                             + "' in TimePeriod"
                                             + " is in conflict with the temporal operator of eventTime = '"
                                             + operator);
                throw se;
            }

            result.append(" (" + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
            		+ " >= " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(start) + "'"
                    + "AND " + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                    + " <= " + "'" + SosDateTimeUtilities.formatDateTime2IsoString(end) + "'" + ")");
            break; // end after
        }

        return result.toString();
    } // end getWhereClause4timeInstant

    /**
     * creates the where clause for timeInstant parameter
     * 
     * @param timeInstant
     *        the timeInstant for which the where clause should be created
     * @param operator
     *        String containing the timeObs value (e.g. "after" or "before"...)
     * @param offeringID
     *        tableName of the PGConfigurator.obsTn
     * @return String representing the where clause for time instant
     * 
     * @throws ParseException
     *         if parsing the time parameter failed
     * @throws OwsExceptionReport
     *         if value for operator is incorrect
     */
    private String getWhereClause4timeInstant(TimeInstant timeInstant, TimeOperator operator) throws OwsExceptionReport {
        DateTime timeValue = timeInstant.getValue();
        if (timeValue == null && timeInstant.getIndeterminateValue().equalsIgnoreCase("now")) {
            timeValue = now;
        }
        StringBuilder result = new StringBuilder();

        // now build strings thru switching between operators
        switch (operator) {

        // before
        case TM_Before:

            // setting timeValue to max tst in database
            if (timeInstant .getIndeterminateValue() == null || timeInstant.getIndeterminateValue().equals(GMLConstants.IndetTimeValues.unknown.name())) {
                timeValue = SosConfigurator.getInstance().getFactory().getConfigDAO().getMaxDate4Observations().plusMillis(1);
            }

            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
            		+ " < '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue) + "' ");
            break; // end before

        // after
        case TM_After:

            // setting timeValue to min tst in database
            if (timeInstant .getIndeterminateValue() == null || timeInstant.getIndeterminateValue().equals(GMLConstants.IndetTimeValues.unknown.name())) {
                timeValue = SosConfigurator.getInstance().getFactory().getConfigDAO().getMinDate4Observations().minusMillis(1);
            }

            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
            		+ " > '" + SosDateTimeUtilities.formatDateTime2IsoString(timeValue) + "' ");
            break; // end after

        // during
        case TM_During:
            result.append(createTimeInstantString4Tequals(timeInstant));
            break;// end during

        // equals
        case TM_Equals:
            result.append(createTimeInstantString4Tequals(timeInstant));
            break; // end equals

        default:
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue,
                                 SosConstants.GetObservationParams.eventTime.toString(),
                                 operator.name());
            throw se;
        }

        return result.toString();
    } // end createWhereClause4TimeInstant

    /**
	 * creates the where clause part for requested procedures
	 * 
	 * @param procedures
	 *        String[] containing the values of the procedure parameter
	 * @param offeringID
	 *        the offering ID of the getObservation request
	 * @return String representing the where clause part for the requested procedures
	 * @throws OwsExceptionReport
	 *         if no matching procedure is contained
	 */
	private String getWhereClause4Procedures(String[] procedures, String offeringID) throws OwsExceptionReport {
	
	    String query = "";
	    List<String> procsOffered = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(offeringID);
	
	    // indicates whether one procedure is valid
	    boolean isOffered = false;
	
	    // test, whether a valid procedure is contained in the request and then
	    // build, the where clause part
	    for (int i = 0; i < procedures.length; i++) {
	        for (int j = 0; j < procsOffered.size(); j++) {
	            if (procedures[i].equals(procsOffered.get(j))) {
	                isOffered = true;
	                query += "" + PGDAOConstants.obsTn + "." + PGDAOConstants.procIDCn + " = '"
	                        + procedures[i] + "' OR ";
	            }
	        }
	    }
	
	    // delete last 'or'
	    if ( !query.equals("")) {
	        int maxLength = query.length() - 3;
	        query = query.substring(0, maxLength);
	        query = " AND ( " + query + " ) ";
	    }
	
	    // if no valid procedure is contained, throw exception!
	    if (isOffered == false) {
	        OwsExceptionReport se = new OwsExceptionReport();
	        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                             GetObservationParams.procedure.toString(),
	                             "The requested procedures are invalid for the requested offering. Please recheck the Capabilities response document to ensure,"
	                                     + "that the requested procedure is valid!");
	        throw se;
	    }
	
	    return query;
	} // end getWhereClause4Procedures

	/**
     * queries latest measured observation for each sensor and phenomenon from SOS database; sends query for
     * each procedure to SOS
     * 
     * 
     * @param request
     *        internal SOS representation of GetObservation request
     * @return internal SOS representation of observation collection
     * @throws OwsExceptionReport
     *         if query of observations fails
     */
    private ResultSet queryLatestObservations(SosGetObservationRequest request, Connection con) throws OwsExceptionReport {

//        SosObservationCollection obsCol = new SosObservationCollection();
        ResultSet resultSet = null;
        try {

            List<String> procIDs = Arrays.asList(request.getProcedure());
            if (procIDs.size() == 0) {
                procIDs = SosConfigurator.getInstance().getCapsCacheController().getProcedures4Offering(request.getOffering());
            }
            List<String> phenIDs = Arrays.asList(request.getObservedProperty());

            Iterator<String> procIter = procIDs.iterator();
            while (procIter.hasNext()) {
                String procID = procIter.next();

                if (SosConfigurator.getInstance().getCapsCacheController().getProcPhens().get(procID) != null) {

                    Iterator<String> phenIter = SosConfigurator.getInstance().getCapsCacheController().getProcPhens().get(procID).iterator();

                    while (phenIter.hasNext()) {
                        String phenID = phenIter.next();

                        if (phenIDs.contains(phenID)) {

                            // ///////////////////////////////////////////////
                            // build query
                            StringBuilder query = new StringBuilder();
                            String srsName = request.getSrsName();
                            
                            String havingString4Procs = "";
                            String havingString4Fois = "";
                            String havingString4DFs = "";
                            if (request.isMobileEnabled()) {
                                  havingString4Procs = "AND " + PGDAOConstants.obsTn + "."
                                          + PGDAOConstants.procIDCn + " IN('" + procID + "') ";
                            	// Fois
                                if (request.getFeatureOfInterest() != null) {
                                    String foiSelect = getFoiIds4SpatialFilter(request.getFeatureOfInterest());
                                    havingString4Fois = "AND " + PGDAOConstants.obsTn + "."
                                            + PGDAOConstants.foiIDCn + " IN(" + foiSelect + ") ";
                                }
                                // Domain features
                                if (request.getDomainFeature() != null) {
                                    String dfSelect = getDFIds4SpatialFilter(request.getDomainFeature());
                                    havingString4DFs = "AND " + PGDAOConstants.dfTn + "."
                                            + PGDAOConstants.domainFeatureIDCn + " IN(" + dfSelect
                                            + ") ";
                                }
                            }

                            // SELECT clause
                            query.append(getSelectClause(request.isMobileEnabled(), supportsQuality));

                            // add geometry column to list, if srsName parameter is
                            // set, transform coordinates into request system
                            query.append(getGeometriesAsTextClause(srsName));

                            // FROM Clause
                            query.append(getFromClause(request.isMobileEnabled(), supportsQuality));
                            
                            if (request.isMobileEnabled()) {
                            	query.append(" WHERE " + PGDAOConstants.obsTn + "."
                                        + PGDAOConstants.timestampCn + " IN ( " + "SELECT Max("
                                        + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
                                        + ") " + "FROM (" + PGDAOConstants.obsTn
                                        + " NATURAL INNER JOIN " + PGDAOConstants.phenTn
                                        + " NATURAL INNER JOIN " + PGDAOConstants.foiTn
                                        + " INNER JOIN " + PGDAOConstants.obsDfTn + " ON "
                                        + PGDAOConstants.obsDfTn + "." + PGDAOConstants.obsIDCn + " = "
                                        + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn
                                        + " INNER JOIN " + PGDAOConstants.dfTn + " ON "
                                        + PGDAOConstants.obsDfTn + "."
                                        + PGDAOConstants.domainFeatureIDCn + " = "
                                        + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn
                                        + ") " + "GROUP BY " + PGDAOConstants.obsTn + "."
                                        + PGDAOConstants.phenIDCn + ", " + PGDAOConstants.obsTn + "."
                                        + PGDAOConstants.procIDCn + ", " + PGDAOConstants.dfTn + "."
                                        + PGDAOConstants.domainFeatureIDCn + ", "
                                        + PGDAOConstants.obsTn + "." + PGDAOConstants.foiIDCn + " "
                                        + "HAVING " + PGDAOConstants.obsTn + "."
                                        + PGDAOConstants.phenIDCn + " IN('" + phenID + "') "
                                        + havingString4Procs + havingString4Fois + havingString4DFs
                                        + "ORDER BY " + PGDAOConstants.phenIDCn + ", "
                                        + PGDAOConstants.procIDCn + ", " + PGDAOConstants.foiIDCn
                                        + ", " + PGDAOConstants.dfTn + "."
                                        + PGDAOConstants.domainFeatureIDCn + ") " + "AND " + ""
                                        + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn
                                        + " IN('" + phenID + "') " + havingString4Procs
                                        + havingString4Fois + havingString4DFs
                                        + getWhereClause4Offering(request.getOffering()));
                            }
                            else {
                            	String whereClauseProcPhen = PGDAOConstants.obsTn + "."
	                                + PGDAOConstants.procIDCn + " = '" + procID + "' AND "
	                                + PGDAOConstants.obsTn + "." + PGDAOConstants.phenIDCn + " ='"
	                                + phenID + "' "
	                                + getWhereClause4Offering(request.getOffering());
                            	query.append(" WHERE " + PGDAOConstants.obsTn + "."
	                                + PGDAOConstants.timestampCn + " IN (SELECT Max("
	                                + PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn
	                                + ") FROM " + PGDAOConstants.obsTn + " WHERE "
	                                + whereClauseProcPhen + ")");
                        		query.append(" AND " + whereClauseProcPhen);
                            }
                            

                            Statement stmt = con.createStatement();
                            resultSet = stmt.executeQuery(query.toString());
                        }
                    }
                }
                else {
                    OwsExceptionReport se = new OwsExceptionReport();
                    log.error("No phenomena for procedure '" + procID + "' were found!");
                    se.addCodedException(ExceptionCode.InvalidParameterValue,
                                         "procedureID",
                                         "No phenomena for procedure '" + procID + "' were found!");
                    throw se;
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("An error occured while query the data from the database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
        return resultSet;
//        return obsCol;
    }// end queryLatestObservations

    /**
	 * constructs a select statement which returns foi ids for a spatial filter
	 * 
	 * @param foi
	 *        the FeatureOfInterest for which the where clause should be returned
	 * @return select statement
	 * @throws OwsExceptionReport
	 *         if construction of the select statement failed
	 * @throws XmlException
	 *         if construction of the select statement failed
	 */
	private String getFoiIds4SpatialFilter(SpatialFilter foi) throws OwsExceptionReport {
	
	    // query String
	    StringBuilder query = new StringBuilder();
	    query.append("SELECT " + PGDAOConstants.foiIDCn + " FROM " + PGDAOConstants.foiTn + " WHERE ");
	
	    // get ObjectIDs of foi
	    String[] objectIds = foi.getFoiIDs();
	
	    // if objectIDs not null, create where clause for objectIds an return
	    // the where clause
	    if (objectIds != null && objectIds.length != 0) {
	
	        int oidsLength = objectIds.length;
	
	        query.append(PGDAOConstants.foiTn + "." + PGDAOConstants.foiIDCn + " = '" + objectIds[0]
	                + "'");
	
	        if (objectIds.length > 1) {
	            for (int i = 1; i < oidsLength; i++) {
	            	query.append(" OR " + PGDAOConstants.foiTn + "." + PGDAOConstants.foiIDCn + " = '"
	                        + objectIds[i] + "'");
	            }
	        }
	
	        return query.toString();
	    }
	
	    // else if no objectIds are set, a spatialFilter is set, so parse this
	    // and create therefor
	    // a where clause
	
	    int srid = foi.getSrid();
	    if (requestSrid == -1) {
	        requestSrid = srid;
	    }
	    
	    if ( !SosConfigurator.getInstance().getCapsCacheController().getSrids().contains(srid)) {
	        String message = "The epsgCode '"
	                + srid
	                + "' of coordinates in featureOfInterest parameter is not supported by this SOS!!";
	        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
	        se.addCodedException(ExceptionCode.InvalidParameterValue,
	                             SosConstants.GetObservationParams.featureOfInterest.name(),
	                             message);
	        log.error(message);
	        throw se;
	    }
	    query.append(PGSQLQueryUtilities.getWhereClause4SpatialFilter(foi, PGDAOConstants.foiTn, PGDAOConstants.geomCn, srid));
	    
	    return query.toString();
	} // end getFoiIds4SpatialFilter

	/**
	 * constructs the where clause if a feature of interest is contained in the observation request
	 * 
	 * @param foi
	 *        the FeatureOfInterest for which the where clause should be returned
	 * @return where clause of the FeatureOfInterest for the observation query
	 * @throws OwsExceptionReport
	 *         if construction of the where clause failed
	 * @throws XmlException
	 *         if construction of the where clause failed
	 */
	private String getDFIds4SpatialFilter(SpatialFilter df) throws OwsExceptionReport {
	
	    // query String
	    StringBuilder query = new StringBuilder();
	    query.append("SELECT " + PGDAOConstants.domainFeatureIDCn + " FROM " + PGDAOConstants.dfTn + " WHERE ");
	
	    // get ObjectIDs of df
	    String[] objectIds = df.getFoiIDs();
	
	    // if objectIDs not null, create where clause for objectIds an return
	    // the where clause
	    if (objectIds != null && objectIds.length != 0) {
	
	        int oidsLength = objectIds.length;
	
	        query.append(PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn + " = '"
	                + objectIds[0] + "'");
	
	        if (objectIds.length > 1) {
	            for (int i = 1; i < oidsLength; i++) {
	            	query.append(" OR " + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn
	                        + " = '" + objectIds[i] + "'");
	            }
	        }
	
	        return query.toString();
	    }
	
	    // else if no objectIds are set, a spatialFilter is set, so parse this
	    // and create therefor
	    // a where clause
	    int srid = df.getSrid();
	    if (requestSrid == -1) {
	        requestSrid = srid;
	    }
	    query.append(PGSQLQueryUtilities.getWhereClause4SpatialFilter(df, PGDAOConstants.dfTn, PGDAOConstants.domainFeatureGeomCn, srid));

	    return query.toString();
	} // end getDFIds4SpatialFilter

	/**
     * creates a select clause 4
     * 
     * @return String containing the select clause
     */
    private String getSelectClause(boolean mobile, boolean quality) {
    	StringBuilder select = new StringBuilder();
        select.append("SELECT iso_timestamp(" 
        		+ PGDAOConstants.obsTn + "."+ PGDAOConstants.timestampCn 
        		+ ") AS " + PGDAOConstants.timestampCn + ", "
                + PGDAOConstants.obsTn + "." + PGDAOConstants.textValueCn + ", "
                + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn + ", " 
                + PGDAOConstants.obsTn + "." + PGDAOConstants.numericValueCn + ", " 
                + PGDAOConstants.obsTn + "." + PGDAOConstants.spatialValueCn + ", " 
                + PGDAOConstants.obsTn + "." + PGDAOConstants.mimeTypeCn + ", " 
                + PGDAOConstants.obsTn + "." + PGDAOConstants.offeringIDCn + ", " 
                + PGDAOConstants.obsTn + "." + PGDAOConstants.procIDCn + ", " 
                + PGDAOConstants.phenTn + "." + PGDAOConstants.phenIDCn + ", " 
                + PGDAOConstants.phenTn + "." + PGDAOConstants.phenDescCn + ", " 
                + PGDAOConstants.phenTn + "." + PGDAOConstants.unitCn + "," 
                + PGDAOConstants.phenTn + "." + PGDAOConstants.valueTypeCn + "," 
                + PGDAOConstants.foiTn + "." + PGDAOConstants.foiNameCn + ", " 
                + PGDAOConstants.foiTn + "." + PGDAOConstants.foiIDCn + ", " 
                + PGDAOConstants.foiTn + "." + PGDAOConstants.featureTypeCn + ", " 
                + "SRID(" + PGDAOConstants.foiTn + "." + PGDAOConstants.geomCn 
                + ") AS " + PGDAOConstants.foiSrid + ", "
                + "SRID(" + PGDAOConstants.obsTn + "." + PGDAOConstants.spatialValueCn 
                + ") AS " + PGDAOConstants.valueSrid + ", ");
        if (mobile) {
        	select.append(PGDAOConstants.obsDfTn + "." + PGDAOConstants.domainFeatureIDCn + ", " );
        }
        if (quality) {
        	select.append(PGDAOConstants.qualityTn + "." + PGDAOConstants.qualTypeCn + ", "
                    + PGDAOConstants.qualityTn + "." + PGDAOConstants.qualNameCn + ", "
                    + PGDAOConstants.qualityTn + "." + PGDAOConstants.qualUnitCn + ", "
                    + PGDAOConstants.qualityTn + "." + PGDAOConstants.qualValueCn + ", ");
        }

        return select.toString();
    }// end getSelectClause

    
    /**
     * creates a AsText clause 4 geometries
     * 
	 * @param srsName
	 * 			requested srsName
	 * @return	clause as String
	 * @throws OwsExceptionReport
	 * 			if srsName parsing failed
	 */
	private String getGeometriesAsTextClause(String srsName) throws OwsExceptionReport {
		StringBuilder asText = new StringBuilder();
		if (srsName != null && !srsName.equals(SosConstants.PARAMETER_NOT_SET)
	            && !srsName.equals("")) {
			asText.append(" AsText(TRANSFORM(" + PGDAOConstants.obsTn + "."
	                + PGDAOConstants.spatialValueCn + "," + ResultSetUtilities.parseSrsName(srsName)
	                + ")) AS " + PGDAOConstants.valueGeometry + ", ");
			asText.append(" AsText(TRANSFORM(" + PGDAOConstants.foiTn + "." + PGDAOConstants.geomCn
	                + "," + ResultSetUtilities.parseSrsName(srsName) + ")) AS " + PGDAOConstants.foiGeometry);
	    }
	    else {
	    	asText.append(" AsText(" + PGDAOConstants.obsTn + "." + PGDAOConstants.spatialValueCn
	                + ") AS " + PGDAOConstants.valueGeometry + ", ");
	    	asText.append(" AsText(" + PGDAOConstants.foiTn + "." + PGDAOConstants.geomCn
	                + ") AS " + PGDAOConstants.foiGeometry);
	    }
		return asText.toString();
	}// end getGeometriesAsTextClause

	/**
     * creates a from and join clause 4 non spatial and not mobile
     * 
     * @return String containing the from clause
     */
    private String getFromClause(boolean mobile, boolean quality) {
    	StringBuilder from = new StringBuilder();
    	from.append(" FROM (" + PGDAOConstants.obsTn 
        		+ " NATURAL INNER JOIN " + PGDAOConstants.phenTn 
        		+ " NATURAL INNER JOIN " + PGDAOConstants.foiTn);
    	if (mobile) {
    		from.append(" INNER JOIN " + PGDAOConstants.obsDfTn + " ON " 
    			      + PGDAOConstants.obsDfTn + "." + PGDAOConstants.obsIDCn + " = " 
    			      + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn 
    			      + " INNER JOIN " + PGDAOConstants.dfTn + " ON "
    			      + PGDAOConstants.obsDfTn + "." + PGDAOConstants.domainFeatureIDCn + " = "
    			      + PGDAOConstants.dfTn + "." + PGDAOConstants.domainFeatureIDCn);
        }
        if (quality) {
        	from.append(" LEFT JOIN " + PGDAOConstants.qualityTn + " ON "
                    + PGDAOConstants.qualityTn + "." + PGDAOConstants.obsIDCn + " = "
                    + PGDAOConstants.obsTn + "." + PGDAOConstants.obsIDCn);
        }
        from.append(")");
        return from.toString();
    }// end getFromClause
    
    /**
     * Checks if the Request srid is the same as the query result srid. Important for the Response srid.
     * 
     * @param querySrid
     * @return
     */
    private int checkRequestSridQuerySrid(int querySrid) {
        if (querySrid == requestSrid || requestSrid == -1) {
        	requestSrid = querySrid;
            return querySrid;
        }
        else {
            return requestSrid;
        }
    }// end checkRequestSridQuerySrid
    
    /**
     * checks whether the passed responseMode parameter is 'inline', otherwise throws ServiceException
     * 
     * @param responseMode
     *        responseMode parameter of the getObservation request
     * @throws OwsExceptionReport
     *         if responseMode parameter is not 'inline'
     */
    private void checkResponseModeInline(String responseMode) throws OwsExceptionReport {
        if (responseMode != null && !responseMode.equals(SosConstants.PARAMETER_NOT_SET)
                && !responseMode.equalsIgnoreCase(SosConstants.RESPONSE_MODE_INLINE)) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.InvalidParameterValue, null, "The responseMode: "
                    + responseMode + " is not valid for the request resultModel!");
            throw se;
        }
    }// end checkResponseModeInline

    /**
     * checks the remaining heapsize and throws exception, if it is smaller than 8 kB
     * 
     * @throws OwsExceptionReport
     *         if remaining heapsize is smaller than 8kb
     */
    private void checkFreeMemory() throws OwsExceptionReport {
        long freeMem;
        // check remaining free memory on heap
        // if too small, throw exception to avoid an OutOfMemoryError
        freeMem = Runtime.getRuntime().freeMemory();
        log.debug("Remaining Heap Size: " + freeMem);
        if (Runtime.getRuntime().totalMemory() == Runtime.getRuntime().maxMemory()
                && freeMem < 256000) { // 256000 accords to 256 kB
            // create service exception
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "The observation response is to big for the maximal heap size = "
                                         + Runtime.getRuntime().maxMemory()
                                         + " Byte of the virtual machine! "
                                         + "Please either refine your getObservation request to reduce the number of observations in the response or ask the administrator of this SOS to increase the maximum heap size of the virtual machine!");
            throw se;
        }
    }// end checkFreeMemory 
    
	/**
	 * checks whether the passed geometry is contained in the passed envelope; if not, methods expands the
	 * envelope
	 * 
	 * @param envelope
	 *        envelope, in which the geometry should be contained
	 * @param geometry
	 *        geometry, which should be contained in the passed envelope
	 * @return Returns an envelope, which contains the passed geometry
	 */
	private Envelope checkEnvelope(Envelope envelope, Geometry geometry) {
	    if (envelope == null) {
	        envelope = geometry.getEnvelopeInternal();
	    }
	    else if ( !envelope.contains(geometry.getEnvelopeInternal())) {
	        envelope.expandToInclude(geometry.getEnvelopeInternal());
	    }
	    return envelope;
	}// end checkEnvelope

	/**
	 * method checks, whether the resultModel parameter is valid for the observed properties in the request.
	 * If f.e. the request contains resultModel=om:CategoryObservation and the request also contains the
	 * phenomenon waterCategorie, which is a categorical value, then a service exception is thrown!
	 * 
	 * @param resultModel
	 *        resultModel parameter which should be checked
	 * @param observedProperties
	 *        string array containing the observed property parameters of the request
	 * @throws OwsExceptionReport
	 *         if the resultModel parameter is incorrect in combination with the observedProperty parameters
	 */
	private void checkResultModel(QName resultModel, String[] observedProperties) throws OwsExceptionReport {
	    Map<String, ValueTypes> valueTypes4ObsProps = SosConfigurator.getInstance().getCapsCacheController().getValueTypes4ObsProps();
	    ValueTypes valueType;
	
	    // if measurement; check, if phenomenon is contained in request, which
	    // values are not numeric
	    if (resultModel != null) {
		    if (resultModel.equals(SosConstants.RESULT_MODEL_MEASUREMENT)) {
		        for (int i = 0; i < observedProperties.length; i++) {
		            valueType = valueTypes4ObsProps.get(observedProperties[i]);
		            if (valueType != ValueTypes.numericType) {
		                OwsExceptionReport se = new OwsExceptionReport();
		                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
		                                     GetObservationParams.resultModel.toString(),
		                                     "The value ("
		                                             + resultModel
		                                             + ") of the parameter '"
		                                             + GetObservationParams.resultModel.toString()
		                                             + "' is invalid, because the request contains phenomena, which values are not numericValues!");
		                log.error("The resultModel="
		                                  + resultModel
		                                  + " parameter is incorrect, because request contains phenomena, which values are not numericValues!",
		                          se);
		                throw se;
		            }
		        }
		    }
		
		    // if categoryObservation, check, if request contains phenomenon, which
		    // values are not categorical
		    else if (resultModel.equals(SosConstants.RESULT_MODEL_CATEGORY_OBSERVATION)) {
		        for (int i = 0; i < observedProperties.length; i++) {
		            valueType = valueTypes4ObsProps.get(observedProperties[i]);
		            if (valueType != ValueTypes.textType) {
		                OwsExceptionReport se = new OwsExceptionReport();
		                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
		                                     GetObservationParams.resultModel.toString(),
		                                     "The value ("
		                                             + resultModel
		                                             + ") of the parameter '"
		                                             + GetObservationParams.resultModel.toString()
		                                             + "' is invalid, because the request contains phenomena, which values are not textValues!");
		                log.error("The resultModel="
		                                  + resultModel
		                                  + " parameter is incorrect, because request contains phenomena, which values are not textValues!",
		                          se);
		                throw se;
		            }
		        }
		    }
		
		    // if spatialObservation, check, if request contains phenomenon, which
		    // values are not spatial
		    else if (resultModel.equals(SosConstants.RESULT_MODEL_SPATIAL_OBSERVATION)) {
		        for (int i = 0; i < observedProperties.length; i++) {
		            valueType = valueTypes4ObsProps.get(observedProperties[i]);
		            if (valueType != ValueTypes.spatialType) {
		                OwsExceptionReport se = new OwsExceptionReport();
		                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
		                                     GetObservationParams.resultModel.toString(),
		                                     "The value ("
		                                             + resultModel
		                                             + ") of the parameter '"
		                                             + GetObservationParams.resultModel.toString()
		                                             + "' is invalid, because the request contains phenomena, which values are not spatialValues!");
		                log.error("The resultModel="
		                                  + resultModel
		                                  + " parameter is incorrect, because request contains phenomena, which values are not spatialValues!",
		                          se);
		                throw se;
		            }
		        }
		    }
		
		    else if ( !resultModel.equals(SosConstants.RESULT_MODEL_OBSERVATION)) {
		        OwsExceptionReport se = new OwsExceptionReport();
		        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
		                             GetObservationParams.resultModel.toString(),
		                             "The value ("
		                                     + resultModel
		                                     + ") of the parameter '"
		                                     + GetObservationParams.resultModel.toString()
		                                     + "' is invalid! Valid are 'om:Observation' or 'om:Measurement' for numerical observations, 'om:CategoryObservation' for categorical observations, or 'om:SpatialObservation' for spatial observations! ");
		        log.error("The resultModel="
		                          + resultModel
		                          + " parameter is incorrect! Valid are 'om:Observation' or 'om:Measurement' for numerical observations, 'om:CategoryObservation' for categorical observations, or 'om:SpatialObservation' for spatial observations! ",
		                  se);
		        throw se;
		    }
	    }

	
	    // if neither Measurement nor CategoryObservation, it could be only
	    // generic observation, which fits
	    // for all types of values
	}// end checkResultModel

	/**
	 * filters spatial phenomena from
	 * 
	 * @param observedProperties
	 * @return
	 * @throws OwsExceptionReport
	 */
	private String[] filterSpatialPhenomena(String[] observedProperties) throws OwsExceptionReport {
	    ArrayList<String> spatialPhens = new ArrayList<String>();
	    for (int i = 0; i < observedProperties.length; i++) {
	        String phen = observedProperties[i];
	        Map<String, ValueTypes> valueTypes4Phens = SosConfigurator.getInstance().getCapsCacheController().getValueTypes4ObsProps();
	        if (valueTypes4Phens.containsKey(phen)) {
	            ValueTypes valueType = valueTypes4Phens.get(phen);
	            if (valueType == ValueTypes.spatialType) {
	                spatialPhens.add(phen);
	            }
	        }
	    }
	    String[] result = new String[spatialPhens.size()];
	    result = spatialPhens.toArray(result);
	    return result;
	}// end filterSpatialPhenomena

	/**
	 * method returs the number of rows of the passed resultSet
	 * 
	 * @param resultSet
	 *        resultSet for which number of rows should be returned
	 * @return Returns number of rows of passed resultSet
	 */
	private int getResultSetSize(ResultSet resultSet) {
	    int size = -1;
	
	    try {
	        resultSet.last();
	        size = resultSet.getRow();
	        resultSet.beforeFirst();
	    }
	    catch (SQLException e) {
	        return size;
	    }
	
	    return size;
	}// end getResultSize

	/**
	 * creates condition string for time instant with operator T_Equals or T_During
	 * 
	 * @param time
	 *        time instant for which the condition string should be created
	 * @return Returns condition string created from passed time instant
	 * @throws OwsExceptionReport
	 */
	private String createTimeInstantString4Tequals(TimeInstant time) throws OwsExceptionReport {
	    StringBuilder result = new StringBuilder();
	    String indeterminateValue = time.getIndeterminateValue();
	    DateTime timeValue = time.getValue();
	    if (indeterminateValue != null && !indeterminateValue.equals("")) {
	        
	        // is tst specified for 'after' or 'before' in beginPosition
	        if ((indeterminateValue.equals(GMLConstants.IndetTimeValues.after.name()) || indeterminateValue.equals(GMLConstants.IndetTimeValues.before.name()))
	                && timeValue == null) {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 GetObservationParams.eventTime.toString(),
	                                 "The value of indeterminatePosition = '" + indeterminateValue
	                                         + "' of TimePosition in TimeInstant"
	                                         + " requires a specified timestamp");
	            throw se;
	        }
	        
	        // indeterminateTime = "after"
	        if (timeValue != null
	                && indeterminateValue.equals(GMLConstants.IndetTimeValues.after.name())) {
	            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + " > '"
	                    + SosDateTimeUtilities.formatDateTime2IsoString(timeValue) + "' ");
	        }
	
	        // indeterminateTime = "before"
	        else if (timeValue != null
	                && indeterminateValue.equals(GMLConstants.IndetTimeValues.before.name())) {
	            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + " < '"
	                    + SosDateTimeUtilities.formatDateTime2IsoString(timeValue) + "' ");
	        }
	
	        // indeterminateTime = "now"
	        else if (indeterminateValue.equals(GMLConstants.IndetTimeValues.now.name())) {
	            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + " = '"
	                    + SosDateTimeUtilities.formatDateTime2IsoString(now) + "' ");
	        }
	
	        // indeterminateTime = "unknown"
	        else if (indeterminateValue.equals(GMLConstants.IndetTimeValues.unknown.name())) {
	            result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + " = '"
	                    + SosDateTimeUtilities.formatDateTime2IsoString(now) + "' ");
	        }
	
	        else {
	            OwsExceptionReport se = new OwsExceptionReport();
	            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
	                                 GetObservationParams.eventTime.toString(),
	                                 "The value of indeterminatePosition of timePosition ='"
	                                         + indeterminateValue
	                                         + "' is in conflict with the temporal operator of eventTime = 'TM_During' or 'TM_Equals'");
	            throw se;
	        }
	    }
	
	    // indeterminateTime is incorrect or not set, the during clause
	    // without indeterminatTime param is created!!
	    else {
	        result.append(PGDAOConstants.obsTn + "." + PGDAOConstants.timestampCn + " = '"
	                + SosDateTimeUtilities.formatDateTime2IsoString(timeValue) + "' ");
	    }
	    return result.toString();
	} // end createTimeInstantString4Tequals

	/**
	 * inserts the getObservation request into the database for later getResult or getObservationById
	 * requests; deletes the expired requests before the insertion
	 * 
	 * @param getObsDoc
	 *        the XmlBean which represents the getObservation request
	 * @return Returns ID of the request table, which is then used for id of the observation collections
	 * @throws OwsExceptionReport
	 *         if inserting the request failed
	 */
	// protected String insertGetObsRequest(SosGetObservationRequest getObsDoc, DateTime start, DateTime end)
	// throws OwsExceptionReport {
	private String insertGetObsRequest(SosGetObservationRequest getObsDoc,
	                                     DateTime start,
	                                     DateTime end,
	                                     String procID) throws OwsExceptionReport {
	    String requestID = "";
	    String obsTempID = "";
	    StringBuffer insertStatement = new StringBuffer();
	
	    // Data elements for request table
	    String dbEnd = null;
	    String dbStart = null;
	
	    // get request as string from xml bean; the request is stored as text
	    // (which accords to CLOB in SQL)
	    // in the postgreSQL database
	    String requestString = getObsDoc.getRequestString();
	
	    String offering = getObsDoc.getOffering();
	
	    // data elements for request_phenomenon
	    String[] obsProps = getObsDoc.getObservedProperty();
	
	    // first create insert statement for request table
	
	    dbEnd = SosDateTimeUtilities.formatDateTime2IsoString(end);
	    if (start != null) {
	        dbStart = "'" + SosDateTimeUtilities.formatDateTime2IsoString(start) + "'";
	    }
	
	    // append delete statement for deleting the expired requests
	    String deleteStatement = "DELETE FROM " + PGDAOConstants.reqTn + " WHERE "
	            + PGDAOConstants.endLeaseCn + " < '"
	            + SosDateTimeUtilities.formatDateTime2IsoString(now) + "';";
	    insertStatement.append(deleteStatement);
	
	    // append insert statement for storing the request
	    String insert4Request = "INSERT INTO " + PGDAOConstants.reqTn + "("
	            + PGDAOConstants.requestCn + "," + PGDAOConstants.beginLeaseCn + ","
	            + PGDAOConstants.endLeaseCn + "," + PGDAOConstants.offeringIDCn + ") VALUES ('"
	            + requestString + "'," + dbStart + ",'" + dbEnd + "','" + offering + "');";
	    insertStatement.append(insert4Request);
	
	    // then insert values of phenomena into the table request_phenomenon;
	    // check if phenomenon is
	    // composite phenomenon
	    List<String> compPhens = SosConfigurator.getInstance().getCapsCacheController().getOffCompPhens().get(offering);
	    String insert4ReqPhen = "";
	    for (String phenomenon : obsProps) {
	
	        // if phenomenon is composite phenomenon, insert values into request
	        if (compPhens != null && compPhens.contains(phenomenon)) {
	            insert4ReqPhen = "INSERT INTO " + PGDAOConstants.reqCompPhenTn + " VALUES ('"
	                    + phenomenon + "',currval(pg_get_serial_sequence('" + PGDAOConstants.reqTn
	                    + "','" + PGDAOConstants.requestIDCn + "')));";
	            insertStatement.append(insert4ReqPhen);
	        }
	
	        // if not composite phenomenon, insert into table request_phenomenon
	        else {
	            insert4ReqPhen = "INSERT INTO " + PGDAOConstants.reqPhenTn + " VALUES ('"
	                    + phenomenon + "',currval(pg_get_serial_sequence('" + PGDAOConstants.reqTn
	                    + "','" + PGDAOConstants.requestIDCn + "')));";
	            insertStatement.append(insert4ReqPhen);
	        }
	    }
	    // execute insert statements
	    Connection con = null;
	    try {
	        con = cpool.getConnection();
	
	        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
	                                             ResultSet.CONCUR_READ_ONLY);
	        // insert request
	        stmt.execute(insertStatement.toString());
	
	        // get request id
	        ResultSet rsRequest = stmt.executeQuery("SELECT currval(pg_get_serial_sequence('"
	                + PGDAOConstants.reqTn + "','" + PGDAOConstants.requestIDCn
	                + "')) AS obsColId FROM " + PGDAOConstants.reqTn);
	
	        while (rsRequest.next()) {
	            requestID = rsRequest.getString("obsColId");
	        }
	
	        // insert observation_template
	        stmt.execute("INSERT INTO " + PGDAOConstants.obsTempTn + "(" + PGDAOConstants.procIDCn
	                + ", " + PGDAOConstants.requestIDCn + ", " + PGDAOConstants.obsTempCn + ")"
	                + " VALUES( '" + procID + "'," + requestID + ", null);");
	
	        // get observation_template id
	        ResultSet rsObsTemp = stmt.executeQuery("SELECT currval(pg_get_serial_sequence('"
	                + PGDAOConstants.obsTempTn + "','" + PGDAOConstants.obsTempIDCn + "'))"
	                + " AS obsTempId FROM " + PGDAOConstants.obsTempTn);
	
	        while (rsObsTemp.next()) {
	            obsTempID = rsObsTemp.getString("obsTempId");
	        }
	    }
	    catch (SQLException sqle) {
	        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
	        log.error("An error occured while query the data from the database!", sqle);
	        se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
	        throw se;
	    }
	
	    // return connection to connection pool
	    finally {
	        if (con != null) {
	            cpool.returnConnection(con);
	        }
	    }
	
	    return obsTempID;
	} // end insertGetObsRequest
    
}
