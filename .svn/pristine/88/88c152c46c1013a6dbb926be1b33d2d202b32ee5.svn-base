package org.n52.sos.decode;

import net.opengis.gml.FeaturePropertyType;
import net.opengis.sampling.x10.SamplingPointType;
import net.opengis.sampling.x10.SamplingSurfaceType;
import net.opengis.sos.x10.GenericDomainFeatureDocument;

import org.n52.sos.ogc.om.features.domainFeatures.SosGenericDomainFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingPoint;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingSurface;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public interface IFeatureDecoder {

   /**
    * parses sa:SamplingPoint feature and returns SOS representation of sa:SamplingPoint
    * 
    * @param xb_stationType
    *        XMLBeans representation of sa:SamplingPoint
    * @return SOS representation of sa:SamplingPoint
    * @throws OwsExceptionReport
    * 			if parsing sa:SamplingPoint failed
    */
   public SosSamplingPoint parseSamplingPoint(SamplingPointType xb_spType) throws OwsExceptionReport;
   
   /**
    * parses sa:SamplingSurface feature and returns SOS representation of sa:SamplingSurface
    * 
    * @param xb_spType
    *        XMLBeans representation of sa:SamplingSurface
    * @return SOS representation of sa:SamplingSurface
    * @throws OwsExceptionReport
    * 			if parsing sa:SamplingSurface failed
    */
   public SosSamplingSurface parseSamplingSurface(SamplingSurfaceType xb_spType) throws OwsExceptionReport;

   /**
    * parses generic domain feature and returns SOS representation of generic domain feature;
    * 
    * @param xb_fpType
    *        XMLBeans representation of generic domain feature
    * @return SOS representation of generic domain feature
    * @throws OwsExceptionReport
    */
   public SosGenericDomainFeature parseGenericDomainFeature(GenericDomainFeatureDocument xb_gdfDoc) throws OwsExceptionReport;

   /**
    * parses generic domain feature and returns SOS representation of generic domain feature;
    * 
    * @param xb_fpType
    *        XMLBeans representation of generic domain feature
    * @return SOS representation of generic domain feature
    * @throws OwsExceptionReport
    */
   public SosGenericDomainFeature parseGenericDomainFeature(FeaturePropertyType xb_fpType) throws OwsExceptionReport;
}
