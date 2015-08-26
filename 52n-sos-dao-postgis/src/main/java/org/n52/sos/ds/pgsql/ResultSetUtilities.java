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

 Author: Carsten Hollmann
 Created: 2009
 Modified: 12/10/2009
 ***************************************************************/
package org.n52.sos.ds.pgsql;

import org.apache.log4j.Logger;
import org.n52.sos.SosConfigurator;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.domainFeatures.SosDomainArea;
import org.n52.sos.ogc.om.features.domainFeatures.SosGenericDomainFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingPoint;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingSurface;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport.ExceptionLevel;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Utility class for DAOs. Parse and Create.
 * 
 * @author Carsten Hollmann
 *
 */
public class ResultSetUtilities {
	
	 /** logger */
    protected static Logger log = Logger.getLogger(PGSQLGetObservationDAO.class);

	/**
	 * Creates a SosAbstractFeature (SosSamplingPoint, SosSamplingSurface, SosDomainArea, SosGenericDomainFeature) from parameters.
	 * 
	 * @param id
	 * @param desc
	 * @param name
	 * @param geomWKT
	 * @param srid
	 * @param featureType
	 * @param schemaLink
	 * @return SosAbstractFeature
	 * @throws OwsExceptionReport
	 */
	public static SosAbstractFeature getAbstractFeatureFromValues(String id, String desc, String name, String geomWKT, int srid, String featureType, String schemaLink) throws OwsExceptionReport {
		
		SosAbstractFeature absFeat = null;
        Geometry geometry = createJTSGeom(geomWKT, srid);
		
		 // add new AbstractFeature to Collection
        // TODO: implement further AbstractFeatures
        if (featureType.equalsIgnoreCase(OMConstants.NS_SA_PREFIX+":"+OMConstants.EN_SAMPLINGPOINT)) {
        	if (geometry instanceof Point){
                absFeat = new SosSamplingPoint(id,
                                                         name,
                                                         desc,
                                                         (Point)geometry,
                                                         featureType,
                                                         schemaLink);
        	}
        	else {
        		OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "The geometry of feature type '"
                    + featureType + "' has to be Point!!");
            log.error("The feature type '" + featureType + "' is not supported!", se);
            throw se;
        	}
        }
        else if (featureType.equalsIgnoreCase(OMConstants.NS_SA_PREFIX+":"+OMConstants.EN_SAMPLINGSURFACE)) {
            if (geometry instanceof Polygon){
        	absFeat = new SosSamplingSurface(id,
                                                     name,
                                                     desc,
                                                     (Polygon)geometry,
                                                     featureType,
                                                     schemaLink);
            }else {
                OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, "The geometry of feature type '"
                        + featureType + "' has to be Polygon!!");
                log.error("The feature type '" + featureType + "' is not supported!", se);
                throw se;
            }
        }
        else if (featureType.equalsIgnoreCase(OMConstants.NS_SA_PREFIX + ":" + OMConstants.EN_DOMAINAREA)) {
        	if (geometry instanceof Polygon) {
        		absFeat = new SosDomainArea(id,
                      name,
                      desc,
                      geometry,
                      featureType,
                      schemaLink);
        	}
        	else {
        		OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, "The geometry of feature type '"
                        + featureType + "' has to be Polygon!!");
                log.error("The feature type '" + featureType + "' is not supported!", se);
                throw se;
        	}
        		
        }
        else if (featureType.equalsIgnoreCase(OMConstants.NS_SOS_PREFIX+ ":" + OMConstants.EN_GENERICDOMAINFEATURE)) {
        	if(geometry instanceof Polygon) {
        		absFeat = new SosGenericDomainFeature(id,
                      name,
                      desc,
                      geometry,
                      featureType,
                      schemaLink);
        	}
        	else {
        		OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
                se.addCodedException(ExceptionCode.NoApplicableCode, null, "The geometry of feature type '"
                        + featureType + "' has to be Polygon!!");
                log.error("The feature type '" + featureType + "' is not supported!", se);
                throw se;
        	}
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "The geometry or the Prefix of feature type '"
                    + featureType + "' is not support!!");
            log.error("The feature type '" + featureType + "' is not supported!", se);
            throw se;
        }
		return absFeat;
	}
	
    /**
     * parses the srsName and returns an integer representing the number of the EPSG-code of the passed
     * srsName
     * 
     * @param srsName
     *        name of the spatial reference system in EPSG-format (withn urn identifier for EPSG)
     * @return Returns an integer representing the number of the EPSG-code of the passed srsName
     * @throws OwsExceptionReport
     *         if parsing the srsName failed
     */
    public static int parseSrsName(String srsName) throws OwsExceptionReport {
        int srid = Integer.MIN_VALUE;
        String srsNamePrefix = SosConfigurator.getInstance().getSrsNamePrefix();
        if ( ! (srsName == null || srsName.equals("") || !srsName.startsWith(srsNamePrefix))) {
            srsName = srsName.replace(srsNamePrefix, "");
            try {
                srid = new Integer(srsName).intValue();
            }
            catch (Exception e) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "Parsing srsName",
                                     "The srsName attribute has to have the following schema:"
                                             + srsNamePrefix + "number!");
                log.error("Error while parsing srsName parameter: "
                        + se.getMessage());
                throw se;
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
            						"Parsing srsName",
                                 "The srsName attribute has to have the following schema:"
                                         + srsNamePrefix + "number!");
            log.error("Error while parsing srsName parameter: "
                    + se.getMessage());
            throw se;
        }
        return srid;
    }// end parseSrsName
    
	/**
	 * creates an JTS geometry from the passed geometry string in WKT format and the passed srid
	 * 
	 * @param geomWKT
	 *        WKT representation of the geometry
	 * @param srid
	 *        EPSG code number of the spatial reference system
	 * @return Returns JTS geometry, which is created from the passed parameters.
	 * @throws OwsExceptionReport
	 *         If parsing the WKTString failed
	 */
	public static Geometry createJTSGeom(String geomWKT, int srid) throws OwsExceptionReport {
	    Geometry geom = null;
	    WKTReader wktReader = new WKTReader();
	    try {
	        geom = wktReader.read(geomWKT);
	        geom.setSRID(srid);
	    }
	    catch (com.vividsolutions.jts.io.ParseException pe) {
	        OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
	        se.addCodedException(ExceptionCode.NoApplicableCode,
	                             null,
	                             "Error while creating geometry from database geometry object: "
	                                     + pe.getMessage());
	        log.error(se.getMessage());
	        throw se;
	    }
	    return geom;
	}// end createJTSgeom
    
}
