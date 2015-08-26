package org.n52.sos.decode;

import java.util.Collection;

import net.opengis.om.x10.GeometryObservationType;
import net.opengis.om.x10.MeasurementType;

import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public interface IOMDecoder {
	
    /**
     * maps XMLBeans om:Measurement representation to SosMeasurement; current implementation: 1. only
     * gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosMeasurements is returned, because there is a one-to-many relationship in the database
     * bet- ween offering and observation table. In reality, this is an m:n to relationship, but to avoid a
     * join with offering table for each GetObservation request, the measurement is inserted once for each
     * offering
     * 
     * @param xb_measType
     *        XMLBeans representation of om:Measurement, which should be parsed
     * @return Returns SosMeasurement
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseMeasurement(MeasurementType xb_measType,
                                                                      boolean mobileEnabled) throws OwsExceptionReport;
    
    /**
     * maps XMLBeans om:SpatialObservation representation to SosSpatialObservation; current implementation: 1.
     * only gml:TimeInstant for samplingTime supported 2. only GenericSamplingFeature supported for
     * samplingFeature
     * 
     * Parses at first the procedure and then fetches offerings for procedures from CapabilitiesCache; an
     * collection of SosSpatialObservations is returned, because there is a one-to-many relationship in the
     * database between offering and observation table. In reality, this is an m:n to relationship, but to
     * avoid a join with offering table for each GetObservation request, the spatialObservation is inserted
     * once for each offering
     * 
     * @param xb_geomObsType
     *        XMLBeans representation of om:SpatialObservation, which should be parsed
     * @return Returns Sosom:SpatialObservation
     * @throws OwsExceptionReport
     */
    public Collection<AbstractSosObservation> parseSpatialObservation(GeometryObservationType xb_geomObsType,boolean mobileEnabled) throws OwsExceptionReport;
  
}
