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
 Created: <CREATION DATE>
 Modified: 08/11/2008
 ***************************************************************/

package org.n52.sos.encode.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractFeatureCollectionType;
import net.opengis.gml.AbstractTimeGeometricPrimitiveType;
import net.opengis.gml.BoundingShapeType;
import net.opengis.gml.CodeType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.EnvelopeType;
import net.opengis.gml.FeatureCollectionDocument;
import net.opengis.gml.FeaturePropertyType;
import net.opengis.gml.LineStringDocument;
import net.opengis.gml.MeasureType;
import net.opengis.gml.MetaDataPropertyType;
import net.opengis.gml.PointDocument;
import net.opengis.gml.PolygonDocument;
import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodType;
import net.opengis.om.x10.AnyOrReferenceType;
import net.opengis.om.x10.CategoryObservationDocument;
import net.opengis.om.x10.GeometryObservationDocument;
import net.opengis.om.x10.MeasurementDocument;
import net.opengis.om.x10.MeasurementType;
import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.om.x10.ObservationCollectionType;
import net.opengis.om.x10.ObservationDocument;
import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.om.x10.ObservationType;
import net.opengis.om.x10.ProcessPropertyType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.BlockEncodingPropertyType;
import net.opengis.swe.x101.CodeSpacePropertyType;
import net.opengis.swe.x101.CompositePhenomenonDocument;
import net.opengis.swe.x101.CompositePhenomenonType;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.DataArrayType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataValuePropertyType;
import net.opengis.swe.x101.PhenomenonPropertyType;
import net.opengis.swe.x101.QualityPropertyType;
import net.opengis.swe.x101.ScopedNameType;
import net.opengis.swe.x101.SimpleDataRecordDocument;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.TimeObjectPropertyType;
import net.opengis.swe.x101.UomPropertyType;
import net.opengis.swe.x101.AbstractDataArrayType.ElementCount;
import net.opengis.swe.x101.CategoryDocument.Category;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.TextBlockDocument.TextBlock;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.TimeDocument.Time;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.SosConfigurator;
import org.n52.sos.SosConstants;
import org.n52.sos.SosConstants.ValueTypes;
import org.n52.sos.cache.CapabilitiesCacheController;
import org.n52.sos.encode.IOMEncoder;
import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.AbstractSosObservation;
import org.n52.sos.ogc.om.OMConstants;
import org.n52.sos.ogc.om.QualityAssessmentCache;
import org.n52.sos.ogc.om.SosCategoryObservation;
import org.n52.sos.ogc.om.SosGenericObservation;
import org.n52.sos.ogc.om.SosMeasurement;
import org.n52.sos.ogc.om.SosObservationCollection;
import org.n52.sos.ogc.om.SosQualifier;
import org.n52.sos.ogc.om.SosSpatialObservation;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.features.SosXmlFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingPoint;
import org.n52.sos.ogc.om.features.samplingFeatures.SosSamplingSurface;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Encoder for Observation and Measurements Documents / Elements, using XMLBeans, implemented as singleton
 * 
 * ATTENTION: This class needs the XmlBeans lib and the libs generated from the O&M schema files; the used
 * classes which are generated from XMLBeans are named with prefixed 'xb_'
 * 
 * @author Christoph Stasch
 * 
 */
public class OMEncoder implements IOMEncoder {

    /** logger */
    private static final Logger log = Logger.getLogger(OMEncoder.class);

    /** id for the composite phenomen, which describes the components in the O&M response */
    private final static String COMP_PHEN_ID = "cpid";

    private final static String PHEN_FEATURE = "urn:ogc:data:feature";

    /** schema name of the ObservationCollection schema */
    @SuppressWarnings("unused")
    private final static String OBS_SCHEMA_NAME = "sosCommon.xsd";

    // used for unique composite phenomenon IDs
    private static int compPhenIDcounter;

    /**
     * creates ObservationCollection document from the single observations
     * 
     * @param ootypes
     *        ArrayList<ObservationTypes> containing the observation elements
     * @return ReturnsObservationCollectionDocument representing O&M conform Observation Collection document
     * @throws OwsExceptionReport
     *         if the heap size is not sufficient for creating the observation collection
     */
    public ObservationCollectionDocument createObservationCollection(SosObservationCollection sosObsCol) throws OwsExceptionReport {

        // set compPhenIDcounter to zero;
        // TODO workaround cause compPhenID has to be unique in observation collection; implement
        // storing of composite phenomena and point to composite phenomenon the second time the same is used!!
        // e.g.: compPhen
        compPhenIDcounter = 0;

        // create ObservationCollectionDocument and add Collection
        ObservationCollectionDocument xb_obsColDoc = ObservationCollectionDocument.Factory.newInstance();
        ObservationCollectionType xb_obsCol = xb_obsColDoc.addNewObservationCollection();
        
        //modified by ASD
        this.encodeQualityAssessmentWithMetadataProperty(xb_obsCol);
        // set time
        /*
         * TimePeriod timePeriod = sosObsCol.getTime(); if (timePeriod != null) xb_obsCol.addNewEventTime();
         * xb_obsCol.setEventTime(createTime(timePeriod));
         */

        // setBoundedBy
        if (sosObsCol.getBoundedBy() != null) {
            xb_obsCol.addNewBoundedBy();
            xb_obsCol.setBoundedBy(createBoundedBy(sosObsCol.getBoundedBy(), sosObsCol.getSRID()));
        }

        if (sosObsCol.getObservationID() == null) {
            xb_obsCol.setId(SosConstants.OBS_COL_ID_PREFIX + "0");
        }
        else {
            xb_obsCol.setId(SosConstants.OBS_COL_ID_PREFIX + sosObsCol.getObservationID());
        }

        if (sosObsCol.getObservationMembers() != null) {
            Iterator<AbstractSosObservation> obsIter = sosObsCol.getObservationMembers().iterator();
            while (obsIter.hasNext()) {
                ObservationPropertyType xb_obs = xb_obsCol.addNewMember();
                AbstractSosObservation sosObs = obsIter.next();
                XmlObject xb_obsObject = createObservation(sosObs);
                xb_obs.set(xb_obsObject);

                XmlCursor cursor = xb_obs.newCursor();
                // rename elementName from sos:AbstractObservation to correct element name (Xml Beans
                // workaround 4 substitution groups)
                boolean isAO = cursor.toChild(new QName(OMConstants.NS_OM,
                                                        OMConstants.EN_ABSTRACT_OBSERVATION));
                if (isAO) {
                    cursor.setName(new QName(sosObs.getNamespace(), sosObs.getElementName(), "om"));
                }
            }
        }
        else {
            ObservationPropertyType xb_obs = xb_obsCol.addNewMember();
            xb_obs.setHref("urn:ogc:def:nil:OGC:inapplicable");
        }

        log.debug("Observation Collection encoded successfully!");
        return xb_obsColDoc;
    }

    /**
     * creates ObservationCollection document from the single observations
     * 
     * @param ootypes
     *        ArrayList<ObservationTypes> containing the observation elements
     * @return ReturnsObservationCollectionDocument representing O&M conform Observation Collection document
     * @throws OwsExceptionReport
     *         if the heap size is not sufficient for creating the observation collection
     */
    public ObservationCollectionDocument createObservationCollectionMobile(SosObservationCollection sosObsCol) throws OwsExceptionReport {
    	return createObservationCollection(sosObsCol);
    }

    /**
     * creates an XmlBeans ObservationDocument from the passed SosObservation
     * 
     * @param absObs
     *        sos representation of the observation, for which an ObservationDocument should be created
     * @return Returns XmlBeans representation of the sos observation
     * @throws OwsExceptionReport
     */
    public XmlObject createObservation(AbstractSosObservation absObs) throws OwsExceptionReport {
        XmlObject xb_obsDoc = null;

        if (absObs instanceof SosMeasurement) {
            xb_obsDoc = createMeasurement(absObs);
        }
        else if (absObs instanceof SosCategoryObservation) {
            xb_obsDoc = createCategoryObservation(absObs);
        }
        else if (absObs instanceof SosGenericObservation) {
            xb_obsDoc = createGenericObservation(absObs);
        }
        else if (absObs instanceof SosSpatialObservation) {
            xb_obsDoc = createSpatialObservation(absObs);

        }

        // TODO insert further observation types!!
        return xb_obsDoc;

    }

    /**
     * creates an XmlBeans Measurement from the passed SosObservation
     * 
     * @param absObs
     *        sos representation of the observation, for which an ObservationDocument should be created
     * @return Returns XmlBeans representation of the sos observation
     * @throws OwsExceptionReport
     *         if feature type of nested feature of interest is not supported by the GMLEncoder
     */
    public static MeasurementDocument createMeasurement(AbstractSosObservation absObs) throws OwsExceptionReport {
        SosMeasurement meas = (SosMeasurement) absObs;
        MeasurementDocument xb_measDoc = MeasurementDocument.Factory.newInstance();
        MeasurementType xb_measurement = xb_measDoc.addNewMeasurement();

        // add ID to integer primary key cause obsID is of type xs:ID
        xb_measurement.setId(SosConstants.OBS_ID_PREFIX + meas.getObservationID());

        // set eventTime
        xb_measurement.addNewSamplingTime();
        xb_measurement.setSamplingTime(createTimeObject(meas.getSamplingTime()));

        // set procedure
        ProcessPropertyType xb_procedure = xb_measurement.addNewProcedure();
        xb_procedure.setHref(meas.getProcedureID());

        // set observedProperty (phenomenon)
        PhenomenonPropertyType xb_observedProp = xb_measurement.addNewObservedProperty();
        xb_observedProp.setHref(meas.getPhenomenonID());

        // set domain feature
        if (meas.getDomainFeatureIDs() != null) {
        	FeaturePropertyType[] xb_dfs = new FeaturePropertyType[meas.getDomainFeatureIDs().size()];
            int df_count = 0;
            for (SosAbstractFeature dfID : meas.getDomainFeatureIDs()) {
                FeaturePropertyType df = FeaturePropertyType.Factory.newInstance();
                df.setHref(dfID.getId());
                xb_dfs[df_count] = df;
                df_count++;
            }
            xb_measurement.setDomainFeatureArray(xb_dfs);
        }
        
        // set feature
        FeaturePropertyType xb_foiType = xb_measurement.addNewFeatureOfInterest();
        if (SosConfigurator.getInstance().isFoiEncodedInObservation()) {
            SosAbstractFeature foi = meas.getFeatureOfInterest();
            xb_foiType.set(SosConfigurator.getInstance().getGmlEncoder().createFeature(foi));
        }
        else {
            xb_foiType.setHref(meas.getFeatureOfInterestID());
        }

        // add quality, if set
        if (meas.getQuality() != null) {
            AnyOrReferenceType xb_quality = xb_measurement.addNewResultQuality();
            xb_quality.set(createQualityProperty(meas.getQuality()));
        }

        XmlObject xb_measResult = xb_measurement.addNewResult();
        MeasureType xb_result = MeasureType.Factory.newInstance();
        xb_result.setUom(meas.getUnitsOfMeasurement());

        if ( !new Double(meas.getValue()).equals(Double.NaN)) {
            xb_result.setStringValue("" + meas.getValue());
        }

        else {
            xb_result.setNil();
        }
        xb_measResult.set(xb_result);

        return xb_measDoc;
    }

    
    /**
     * creates an XmlBeans GeometryObservationDocument from the passed SosObservation
     * 
     * @param absObs
     *        sos representation of the observation, for which an ObservationDocument should be created
     * @return Returns XmlBeans representation of the sos observation
     * @throws OwsExceptionReport
     *         if feature type of nested feature of interest is not supported by the GMLEncoder
     */
    public static GeometryObservationDocument createSpatialObservation(AbstractSosObservation absObs) throws OwsExceptionReport {

        SosSpatialObservation spobs = (SosSpatialObservation) absObs;
        GeometryObservationDocument xb_spobsDoc = GeometryObservationDocument.Factory.newInstance();
        ObservationType xb_spobs = xb_spobsDoc.addNewGeometryObservation();

        // add ID to integer primary key cause obsID is of type xs:ID
        xb_spobs.setId(SosConstants.OBS_SPATIAL_ID_PREFIX + spobs.getObservationID());

        // set eventTime
        xb_spobs.addNewSamplingTime();
        xb_spobs.setSamplingTime(createTimeObject(spobs.getSamplingTime()));

        // set procedure
        ProcessPropertyType xb_procedure = xb_spobs.addNewProcedure();
        xb_procedure.setHref(spobs.getProcedureID());

        // set observedProperty (phenomenon)
        PhenomenonPropertyType xb_observedProp = xb_spobs.addNewObservedProperty();
        xb_observedProp.setHref(spobs.getPhenomenonID());

     // set domain feature
        if (spobs.getDomainFeatureIDs() != null) {
        	FeaturePropertyType[] xb_dfs = new FeaturePropertyType[spobs.getDomainFeatureIDs().size()];
            int df_count = 0;
            for (SosAbstractFeature dfID : spobs.getDomainFeatureIDs()) {
                FeaturePropertyType df = FeaturePropertyType.Factory.newInstance();
                df.setHref(dfID.getId());
                xb_dfs[df_count] = df;
                df_count++;
            }
            xb_spobs.setDomainFeatureArray(xb_dfs);
        }
        
        // set feature
        FeaturePropertyType xb_foiType = xb_spobs.addNewFeatureOfInterest();
        if (SosConfigurator.getInstance().isFoiEncodedInObservation()) {
            SosAbstractFeature foi = spobs.getFeatureOfInterest();
                xb_foiType.set(SosConfigurator.getInstance().getGmlEncoder().createFeature(foi));
        }
        else {
            xb_foiType.setHref(spobs.getFeatureOfInterestID());
        }

        // add quality, if set
        if (spobs.getQuality() != null) {
            AnyOrReferenceType xb_quality = xb_spobs.addNewResultQuality();
            xb_quality.set(createQualityProperty(spobs.getQuality()));
        }

        // result
        XmlObject xb_result = xb_spobs.addNewResult();
        Geometry geom = spobs.getResult();

        GMLEncoder gmlEncoder = new GMLEncoder();
        if (geom instanceof Point) {

            // add point to result
            Point jts_point = (Point) geom;
            PointDocument xb_pd = PointDocument.Factory.newInstance();
            xb_pd.setPoint(gmlEncoder.encodePoint(jts_point));
            xb_result.set(xb_pd);

        }
        else if (geom instanceof LineString) {

            // add linestring to result
            LineString jts_lineString = (LineString) geom;
            LineStringDocument xb_lsd = LineStringDocument.Factory.newInstance();
            xb_lsd.setLineString(gmlEncoder.encodeLineString(jts_lineString));

            xb_result.set(xb_lsd);

        }
        else if (geom instanceof Polygon) {

            // add polygon to result
        	Polygon jts_polygon = (Polygon) geom;
            PolygonDocument xb_polDoc = PolygonDocument.Factory.newInstance();
            xb_polDoc.setPolygon(gmlEncoder.encodePolygon(jts_polygon));
            xb_result.set(xb_polDoc);
        }

        return xb_spobsDoc;
    }

    /**
     * creates an XmlBeans CategoryObservationDocument from the passed SosObservation
     * 
     * @param absObs
     *        sos representation of the observation, for which an ObservationDocument should be created
     * @return Returns XmlBeans representation of the sos observation
     * @throws OwsExceptionReport
     *         if feature type of nested feature of interest is not supported by the GMLEncoder
     */
    private CategoryObservationDocument createCategoryObservation(AbstractSosObservation absObs) throws OwsExceptionReport {

        SosCategoryObservation cobs = (SosCategoryObservation) absObs;
        CategoryObservationDocument xb_cobsDoc = CategoryObservationDocument.Factory.newInstance();
        ObservationType xb_cobs = xb_cobsDoc.addNewCategoryObservation();

        // add ID to integer primary key cause obsID is of type xs:ID
        xb_cobs.setId(SosConstants.OBS_CATEGORY_ID_PREFIX + cobs.getObservationID());

        // set eventTime
        xb_cobs.addNewSamplingTime();
        xb_cobs.setSamplingTime(createTimeObject(cobs.getSamplingTime()));

        // set procedure
        ProcessPropertyType xb_procedure = xb_cobs.addNewProcedure();
        xb_procedure.setHref(cobs.getProcedureID());

        // set observedProperty (phenomenon)
        PhenomenonPropertyType xb_observedProp = xb_cobs.addNewObservedProperty();
        xb_observedProp.setHref(cobs.getPhenomenonID());
     
        // set domain feature
        if (cobs.getDomainFeatureIDs() != null) {
        	FeaturePropertyType[] xb_dfs = new FeaturePropertyType[cobs.getDomainFeatureIDs().size()];
            int df_count = 0;
            for (SosAbstractFeature dfID : cobs.getDomainFeatureIDs()) {
                FeaturePropertyType df = FeaturePropertyType.Factory.newInstance();
                df.setHref(dfID.getId());
                xb_dfs[df_count] = df;
                df_count++;
            }
            xb_cobs.setDomainFeatureArray(xb_dfs);
        }
        // set feature
        FeaturePropertyType xb_foiType = xb_cobs.addNewFeatureOfInterest();
        if (SosConfigurator.getInstance().isFoiEncodedInObservation()) {
            SosAbstractFeature foi = cobs.getFeatureOfInterest();
            if (foi instanceof SosSamplingPoint) {
                xb_foiType.set(SosConfigurator.getInstance().getGmlEncoder().createFeature(foi));

                // change name of abstract feature to gml:FeatureCollection
                /*
                 * QName absFeatName = new QName(OMConstants.NS_GML, OMConstants.EN_ABSTRACT_FEATURE); QName
                 * stationName = new QName(OMConstants.NS_SA, OMConstants.EN_STATION); XmlCursor foiCursor =
                 * xb_foiType.newCursor();
                 * 
                 * boolean hasAbsFeat = foiCursor.toChild(absFeatName); if (hasAbsFeat) {
                 * foiCursor.setName(stationName); }
                 */
            }
        }
        else {
            xb_foiType.setHref(cobs.getFeatureOfInterestID());
        }

        // add quality, if set
        if (cobs.getQuality() != null) {
            AnyOrReferenceType xb_quality = xb_cobs.addNewResultQuality();
            xb_quality.set(createQualityProperty(cobs.getQuality()));
        }

        XmlObject xb_result = xb_cobs.addNewResult();
        ScopedNameType xb_snType = ScopedNameType.Factory.newInstance();
        xb_snType.setCodeSpace(cobs.getUnit());
        xb_snType.setStringValue(cobs.getTextValue());
        xb_result.set(xb_snType);

        return xb_cobsDoc;
    }

    /**
     * creates an XmlBeans ObservationDocument from the passed SosObservation
     * 
     * @param absObs
     *        sos representation of the observation, for which an ObservationDocument should be created
     * @return Returns XmlBeans representation of the sos observation
     * @throws OwsExceptionReport
     */
    private ObservationDocument createGenericObservation(AbstractSosObservation absObs) throws OwsExceptionReport {

        SosGenericObservation genObs = (SosGenericObservation) absObs;
        ObservationDocument xb_obsDoc = ObservationDocument.Factory.newInstance();
        ObservationType xb_obsType = xb_obsDoc.addNewObservation();
        
        //this.encodeQualityAssessmentWithMetadataProperty(xb_obsType);

        // set id
        if (genObs.getObservationID() != null) {
        	if (genObs.getObservationID().startsWith(SosConstants.OBS_TEMP_ID_PREFIX)) {
                xb_obsType.setId(genObs.getObservationID());
            } else {
            	xb_obsType.setId(SosConstants.OBS_TEMP_ID_PREFIX + genObs.getObservationID());
            }
        }
        
        // set time
        xb_obsType.setSamplingTime(createTimeObject(genObs.getSamplingTime()));
        
        // set domain feature
        if (genObs.getDomainFeatureIDs() != null) {
        	FeaturePropertyType[] xb_dfs = new FeaturePropertyType[genObs.getDomainFeatureIDs().size()];
            int df_count = 0;
            for (SosAbstractFeature dfID : genObs.getDomainFeatureIDs()) {
                FeaturePropertyType df = FeaturePropertyType.Factory.newInstance();
                df.setHref(dfID.getId());
                xb_dfs[df_count] = df;
                df_count++;
            }
            xb_obsType.setDomainFeatureArray(xb_dfs);
        }
        
    	// set foi
        FeatureCollectionDocument xb_featColDoc = FeatureCollectionDocument.Factory.newInstance();
        AbstractFeatureCollectionType xb_featCol = xb_featColDoc.addNewFeatureCollection();
        ArrayList<SosAbstractFeature> fois = genObs.getFois();

        for (SosAbstractFeature foi : fois) {
        	if (foi != null) {
        		FeaturePropertyType xb_foiMember = xb_featCol.addNewFeatureMember();
                if (SosConfigurator.getInstance().isFoiEncodedInObservation()
                        && (foi instanceof SosSamplingPoint || foi instanceof SosSamplingSurface) || foi instanceof SosXmlFeature) {
                    xb_foiMember.set(SosConfigurator.getInstance().getGmlEncoder().createFeature(foi));
                }
                else {
                    xb_foiMember.setHref(foi.getId());
                }
        	}
        }
        FeaturePropertyType xb_foiType = xb_obsType.addNewFeatureOfInterest();
        xb_foiType.addNewFeature();
        xb_foiType.set(xb_featColDoc);

        // change name of AbstractFeatureCollection to FeatureCollection
        QName absFeatName = new QName(OMConstants.NS_GML,
                                      OMConstants.EN_ABSTRACT_FEATURE_COLLECTION);
        QName featureName = new QName(OMConstants.NS_GML, OMConstants.EN_FEATURE_COLLECTION);
        XmlCursor foiCursor = xb_foiType.newCursor();
        boolean hasAbsFeat = foiCursor.toChild(absFeatName);
        if (hasAbsFeat) {
            foiCursor.setName(featureName);
        }

        // add procedure
        if (genObs.getProcedureID() != null) {
            ProcessPropertyType xb_procedure = xb_obsType.addNewProcedure();
            xb_procedure.setHref(genObs.getProcedureID());
        }
        else {
            xb_obsType.addNewProcedure();
        }

        // phenomena of the common observation
        PhenomenonPropertyType xb_obsProp = xb_obsType.addNewObservedProperty();
        // PhenomenonType xb_phen = xb_obsProp.addNewPhenomenon();
        CompositePhenomenonDocument xb_cpDoc = CompositePhenomenonDocument.Factory.newInstance();
        CompositePhenomenonType xb_compPhen = xb_cpDoc.addNewCompositePhenomenon();
        xb_compPhen.setId(COMP_PHEN_ID + compPhenIDcounter);
        compPhenIDcounter++;
        xb_compPhen.addNewName().setStringValue("resultComponents");
        ArrayList<String> phenComponents = genObs.getPhenComponents();
        BigInteger dimension = BigInteger.valueOf(phenComponents.size());
        xb_compPhen.setDimension(dimension);

        // time component
        PhenomenonPropertyType component = xb_compPhen.addNewComponent();
        component.setHref(OMConstants.PHEN_ID_ISO8601);

        // then get the components for every composite phenomenon
        for (String phenComp : phenComponents) {
            component = xb_compPhen.addNewComponent();
            component.setHref(phenComp);
        }

        xb_obsProp.set(xb_cpDoc);

        // add resultDefinition
        DataArrayDocument xb_dataArrayDoc = createDataArrayResult(genObs.getPhenComponents(),
                                                                  genObs.createResultString(),
                                                                  genObs.getTupleCount());
        XmlObject xb_result = xb_obsType.addNewResult();
        xb_result.set(xb_dataArrayDoc);
        
        return xb_obsDoc;
    } // end createGenericObservation

    /**
     * creates the swe:DataArray, which is used for the result element of a generic om:Observation
     * 
     * @param phenComponents
     *        ids of the phenomena of the common observation
     * @return DataDefinitionType representing the DataDefinition element of a CommonObservation
     * @throws OwsExceptionReport
     */
    private DataArrayDocument createDataArrayResult(ArrayList<String> phenComponents,
                                                    String resultString,
                                                    int count) throws OwsExceptionReport {

        // create DataArray
        DataArrayDocument xb_dataArrayDoc = DataArrayDocument.Factory.newInstance();
        DataArrayType xb_dataArray = xb_dataArrayDoc.addNewDataArray1();

        // set element count
        ElementCount xb_elementCount = xb_dataArray.addNewElementCount();
        xb_elementCount.addNewCount().setValue(BigInteger.valueOf(count));

        // create data definition
        DataComponentPropertyType xb_elementType = xb_dataArray.addNewElementType();

        SimpleDataRecordDocument xb_simpleDataRecordDoc = SimpleDataRecordDocument.Factory.newInstance();
        SimpleDataRecordType xb_simpleDataRecord = xb_simpleDataRecordDoc.addNewSimpleDataRecord();

        // add time component
        AnyScalarPropertyType xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName("Time");
        Time xbTimeComponent = xb_field.addNewTime();
        xbTimeComponent.setDefinition(OMConstants.PHEN_ID_ISO8601);

        // add foi
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName("feature");
        Text xbFoiText = xb_field.addNewText();
        xbFoiText.setDefinition(PHEN_FEATURE);
        
        // add phenomenon components
        CapabilitiesCacheController capsCache = SosConfigurator.getInstance().getCapsCacheController();
        Map<String, ValueTypes> valueTypes4phens = capsCache.getValueTypes4ObsProps();
        for (String phenComponent : phenComponents) {

            ValueTypes valueType = valueTypes4phens.get(phenComponent);
            if (valueType != null) {
                switch (valueType) {
                case numericType: {
                    xb_field = xb_simpleDataRecord.addNewField();
                    xb_field.setName(phenComponent.replace(SosConstants.PHENOMENON_PREFIX, ""));
                    Quantity xbQuantity = xb_field.addNewQuantity();
                    xbQuantity.setDefinition(phenComponent);
                    UomPropertyType xb_uom = xbQuantity.addNewUom();
                    xb_uom.setCode(capsCache.getUnit4ObsProp(phenComponent));
                    break;
                }
                case textType: {
                    xb_field = xb_simpleDataRecord.addNewField();
                    xb_field.setName(phenComponent.replace(SosConstants.PHENOMENON_PREFIX, ""));
                    Category xbCategory = xb_field.addNewCategory();
                    xbCategory.setDefinition(phenComponent);
                    break;
                }
                default:
                    xb_field = xb_simpleDataRecord.addNewField();
                    xb_field.setName(phenComponent.replace(SosConstants.PHENOMENON_PREFIX, ""));
                    Text xbText = xb_field.addNewText();
                    xbText.setDefinition(phenComponent);
                    break;
                }
            }
            else {
                xb_field = xb_simpleDataRecord.addNewField();
                xb_field.setName(phenComponent.replace(SosConstants.PHENOMENON_PREFIX, ""));
                Category xbCategory = xb_field.addNewCategory();
                xbCategory.setDefinition(phenComponent);
            }

        }

        // set components to SimpleDataRecord
        xb_elementType.set(xb_simpleDataRecordDoc);
        xb_elementType.setName("Components");

        // add encoding element
        BlockEncodingPropertyType xb_encoding = xb_dataArray.addNewEncoding();
        TextBlock xb_textBlock = xb_encoding.addNewTextBlock();

        xb_textBlock.setDecimalSeparator(SosConfigurator.getInstance().getDecimalSeparator());
        xb_textBlock.setTokenSeparator(SosConfigurator.getInstance().getTokenSeperator());
        xb_textBlock.setBlockSeparator(SosConfigurator.getInstance().getTupleSeperator());

        DataValuePropertyType xb_values = xb_dataArray.addNewValues();
        xb_values.newCursor().setTextValue(resultString);

        return xb_dataArrayDoc;
    }

    /**
     * method creates a compositePhenomenon element
     * 
     * @param compPhenId
     *        id of the composite phenomenon
     * @param phenComponents
     *        components of the composite phenomenon
     * @return Returns PhenomenonPropertyType which represents the composite phenomenon element
     */
    public CompositePhenomenonDocument createCompositePhenomenon(String compPhenId,
                                                                 List<String> phenComponents) {
        CompositePhenomenonDocument xb_cpDoc = CompositePhenomenonDocument.Factory.newInstance();
        CompositePhenomenonType xb_cpType = xb_cpDoc.addNewCompositePhenomenon();
        xb_cpType.setId(compPhenId + compPhenIDcounter);
        compPhenIDcounter++;

        // then get the components for every composite phenomenon
        if (phenComponents != null) {
            for (String phenComponent : phenComponents) {
                PhenomenonPropertyType xb_component = xb_cpType.addNewComponent();
                xb_component.setHref(phenComponent);
            }
        }
        return xb_cpDoc;
    }

    /**
     * creates the bounded by element for the passed envelope
     * 
     * @param envelope
     *        envelope, which should the boundedBy element be created for
     * @param srid
     * 		  srid, of the envelope
     * @return Returns XmlBeans representation of the gml:boundedBy element
     */
    public static BoundingShapeType createBoundedBy(Envelope envelope, int srid) {
        BoundingShapeType xb_boundingShape = BoundingShapeType.Factory.newInstance();
        EnvelopeType xb_envelope = xb_boundingShape.addNewEnvelope();

        DirectPositionType xb_lowerCorner = xb_envelope.addNewLowerCorner();
        if (!SosConfigurator.getInstance().switchCoordinatesForEPSG(srid)) {
            xb_lowerCorner.setStringValue(envelope.getMinX() + " " + envelope.getMinY());
        }
        else {
            xb_lowerCorner.setStringValue(envelope.getMinY() + " " + envelope.getMinX());
        }

        DirectPositionType xb_upperCorner = xb_envelope.addNewUpperCorner();
        if (!SosConfigurator.getInstance().switchCoordinatesForEPSG(srid)) {
            xb_upperCorner.setStringValue(envelope.getMaxX() + " " + envelope.getMaxY());
        }
        else {
            xb_upperCorner.setStringValue(envelope.getMaxY() + " " + envelope.getMaxX());
        }
        xb_envelope.setSrsName(SosConfigurator.getInstance().getSrsNamePrefix() + srid);
        return xb_boundingShape;
    }

    /**
     * creates XmlBeans TimeObjectPropertyType from passed sos time object
     * 
     * @param time
     *        ISosTime implementation, which should be created an XmlBeans O&M representation from
     * @return Returns XmlBean representing the passed time in O&M format
     * @throws OwsExceptionReport 
     */
    public static TimeObjectPropertyType createTimeObject(ISosTime time) throws OwsExceptionReport {
        TimeObjectPropertyType xb_eventTime = TimeObjectPropertyType.Factory.newInstance();
        if (time == null) {
            return xb_eventTime;
        }
        AbstractTimeGeometricPrimitiveType xb_absTimePrim = SosConfigurator.getInstance().getGmlEncoder().createTime(time);
        xb_eventTime.setTimeObject(xb_absTimePrim);

        if (xb_absTimePrim instanceof TimeInstantType) {

            // change name of _TimeObject to TimeInstant
            XmlCursor timeCursor = xb_eventTime.newCursor();
            boolean isTo = timeCursor.toChild(new QName(OMConstants.NS_GML,
                                                        OMConstants.EN_ABSTRACT_TIME_OBJECT));
            if (isTo) {
                timeCursor.setName(new QName(OMConstants.NS_GML, OMConstants.EN_TIME_INSTANT));
            }
        }

        else if (xb_absTimePrim instanceof TimePeriodType) {
            // change name of _TimeObject to TimeInstant
            XmlCursor timeCursor = xb_eventTime.newCursor();
            boolean isTo = timeCursor.toChild(new QName(OMConstants.NS_GML,
                                                        OMConstants.EN_ABSTRACT_TIME_OBJECT));
            if (isTo) {
                timeCursor.setName(new QName(OMConstants.NS_GML, OMConstants.EN_TIME_PERIOD));
            }
        }

        return xb_eventTime;
    }
    
    /**
     * creates a QuantityProperty representing the quality element for observations
     * 
     * @param qualityColl
     * 			The Collection of qualities for the Observation.
     * @return Returns XMLBean representing the quality element for observations
     */
    public static QualityPropertyType createQualityProperty(Collection<SosQuality> qualityColl) {
    	Iterator<SosQuality> qualityIter = qualityColl.iterator();
    	QualityPropertyType qpt = QualityPropertyType.Factory.newInstance();
        while (qualityIter.hasNext()) {
            SosQuality sosQuality = qualityIter.next();
            switch (sosQuality.getQualityType()) {
            case quantity:
                Quantity quantity = qpt.addNewQuantity();
                quantity.set(createQualityQuantity(sosQuality));
                break;
            case category:
                Category category = qpt.addNewCategory();
                category.set(createQualityCategory(sosQuality));
                break;
            case text:
                Text text = qpt.addNewText();
                text.set(createQualityText(sosQuality));
                break;
            }
        }
        return qpt;
    }

    /**
     * creates a Quantity representing the quality element for observations in case of quality type equals
     * 'quantity'
     * 
     * @param qualityType
     *        the quality, for which the quality element should be created
     * @return Returns XMLBean representing the quality element for observations
     */
    public static Quantity createQualityQuantity(SosQuality sosQuality) {

        // new quantity
        Quantity xb_quantity = Quantity.Factory.newInstance();

        // quality name
        CodeType xb_name = xb_quantity.addNewName();
        xb_name.setStringValue(sosQuality.getResultName());

        // quality value
        xb_quantity.setValue(Double.parseDouble(sosQuality.getResultValue()));

        // quality unit
        UomPropertyType xb_oum = xb_quantity.addNewUom();
        xb_oum.setCode(sosQuality.getResultUnit());

        return xb_quantity;
    }

    /**
     * creates a Category representing the quality element for observations in case of quality type equals
     * 'category'
     * 
     * @param qualityType
     *        the quality, for which the quality element should be created
     * @return Returns XMLBean representing the quality element for observations
     */
    public static Category createQualityCategory(SosQuality sosQuality) {

        // new category
        Category xb_category = Category.Factory.newInstance();

        // quality name
        CodeType xb_name = xb_category.addNewName();
        xb_name.setStringValue(sosQuality.getResultName());

        // quality value
        xb_category.setValue(sosQuality.getResultValue());

        // quality unit
        CodeSpacePropertyType xb_cspt = xb_category.addNewCodeSpace();
        xb_cspt.setType(sosQuality.getResultUnit());

        return xb_category;
    }

    /**
     * creates a Text representing the quality element for observations in case of quality type equals 'text'
     * 
     * @param qualityType
     *        the quality, for which the quality element should be created
     * @return Returns XMLBean representing the quality element for observations
     */
    public static Text createQualityText(SosQuality sosQuality) {

        // new Text
        Text xb_text = Text.Factory.newInstance();

        // quality name
        CodeType xb_name = xb_text.addNewName();
        xb_name.setStringValue(sosQuality.getResultName());

        // quality value
        xb_text.setValue(sosQuality.getResultValue());

        return xb_text;
    }
    
    private void encodeQualityAssessmentWithMetadataProperty(ObservationCollectionType observationCollection){
    	QualityAssessmentCache qcache=QualityAssessmentCache.getInstance();
    	List<SosQualifier> qualifiers=qcache.getQualifiers();
    	
    	MetaDataPropertyType metadataPropertyType=observationCollection.addNewMetaDataProperty();
    	
    	// create DataArray
        DataArrayDocument xb_dataArrayDoc = DataArrayDocument.Factory.newInstance();
        DataArrayType xb_dataArray = xb_dataArrayDoc.addNewDataArray1();

        // set element count
        ElementCount xb_elementCount = xb_dataArray.addNewElementCount();
        xb_elementCount.addNewCount().setValue(BigInteger.valueOf(qualifiers.size()));

        // create data definition
        DataComponentPropertyType xb_elementType = xb_dataArray.addNewElementType();
        
        SimpleDataRecordDocument xb_simpleDataRecordDoc = SimpleDataRecordDocument.Factory.newInstance();
        SimpleDataRecordType xb_simpleDataRecord = xb_simpleDataRecordDoc.addNewSimpleDataRecord();

        AnyScalarPropertyType xb_field = xb_simpleDataRecord.addNewField();
        //modified by ASD change the order - data level followed by quality flags
        /*xb_field.setName(qcache.getGroupIdName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getGenericQualifierName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getSpecificQualifierName());*/
        
        //xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getProcessingStatusIdName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getProcessingStatusName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getProcessingStatiDescName());
        
        xb_elementType.set(xb_simpleDataRecordDoc);
        xb_elementType.setName("Components");

        BlockEncodingPropertyType xb_encoding = xb_dataArray.addNewEncoding();
        TextBlock xb_textBlock = xb_encoding.addNewTextBlock();

        String tokenSeperator=SosConfigurator.getInstance().getTokenSeperator();
        String tupelSeperator=SosConfigurator.getInstance().getTupleSeperator();
        xb_textBlock.setDecimalSeparator(SosConfigurator.getInstance().getDecimalSeparator());
        xb_textBlock.setTokenSeparator(SosConfigurator.getInstance().getTokenSeperator());
        xb_textBlock.setBlockSeparator(SosConfigurator.getInstance().getTupleSeperator());

        DataValuePropertyType xb_values = xb_dataArray.addNewValues();
        //xb_values.newCursor().setTextValue(qcache.getQualifierResultString(tokenSeperator, tupelSeperator));
        xb_values.newCursor().setTextValue(qcache.getProcessingStatusResultString(tokenSeperator, tupelSeperator));
        metadataPropertyType.set(xb_dataArrayDoc);
        
        
        //--------------------------------
        
        
        metadataPropertyType= observationCollection.addNewMetaDataProperty();
    	
    	// create DataArray
        xb_dataArrayDoc = DataArrayDocument.Factory.newInstance();
        xb_dataArray = xb_dataArrayDoc.addNewDataArray1();

        // set element count
        xb_elementCount = xb_dataArray.addNewElementCount();
        xb_elementCount.addNewCount().setValue(BigInteger.valueOf(qcache.getProcessingStati().size()));

        // create data definition
        xb_elementType = xb_dataArray.addNewElementType();
        
        xb_simpleDataRecordDoc = SimpleDataRecordDocument.Factory.newInstance();
        xb_simpleDataRecord = xb_simpleDataRecordDoc.addNewSimpleDataRecord();

        /*
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getProcessingStatusIdName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getProcessingStatusName());*/
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getGroupIdName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getGenericQualifierName());
        xb_field = xb_simpleDataRecord.addNewField();
        xb_field.setName(qcache.getSpecificQualifierName());
        
        
        xb_elementType.set(xb_simpleDataRecordDoc);
        xb_elementType.setName("Components");

        xb_encoding = xb_dataArray.addNewEncoding();
        xb_textBlock = xb_encoding.addNewTextBlock();

        xb_textBlock.setDecimalSeparator(SosConfigurator.getInstance().getDecimalSeparator());
        xb_textBlock.setTokenSeparator(SosConfigurator.getInstance().getTokenSeperator());
        xb_textBlock.setBlockSeparator(SosConfigurator.getInstance().getTupleSeperator());

        xb_values = xb_dataArray.addNewValues();
        xb_values.newCursor().setTextValue(qcache.getQualifierResultString(tokenSeperator, tupelSeperator));
        //xb_values.newCursor().setTextValue(qcache.getProcessingStatusResultString(tokenSeperator, tupelSeperator));
        
        metadataPropertyType.set(xb_dataArrayDoc);
    }
    
//    private void encodeQualityAssessmentWithMetadataProperty(ObservationType observationCollection){
//    	QualityAssessmentCache qcache=QualityAssessmentCache.getInstance();
//    	MetaDataPropertyType metadataPropertyType= observationCollection.addNewMetaDataProperty();
//    	
//    	// create DataArray
//        DataArrayDocument xb_dataArrayDoc = DataArrayDocument.Factory.newInstance();
//        DataArrayType xb_dataArray = xb_dataArrayDoc.addNewDataArray1();
//
//        // set element count
//        ElementCount xb_elementCount = xb_dataArray.addNewElementCount();
//        xb_elementCount.addNewCount().setValue(BigInteger.valueOf(qcache.getProcessingStati().size()));
//
//        // create data definition
//        DataComponentPropertyType xb_elementType = xb_dataArray.addNewElementType();
//        
//        SimpleDataRecordDocument xb_simpleDataRecordDoc = SimpleDataRecordDocument.Factory.newInstance();
//        SimpleDataRecordType xb_simpleDataRecord = xb_simpleDataRecordDoc.addNewSimpleDataRecord();
//
//        AnyScalarPropertyType xb_field = xb_simpleDataRecord.addNewField();
//        xb_field.setName(qcache.getProcessingStatusIdName());
//        xb_field = xb_simpleDataRecord.addNewField();
//        xb_field.setName(qcache.getProcessingStatusName());
//        
//        xb_elementType.set(xb_simpleDataRecordDoc);
//        xb_elementType.setName("Components");
//
//        BlockEncodingPropertyType xb_encoding = xb_dataArray.addNewEncoding();
//        TextBlock xb_textBlock = xb_encoding.addNewTextBlock();
//
//        String tokenSeperator=SosConfigurator.getInstance().getTokenSeperator();
//        String tupelSeperator=SosConfigurator.getInstance().getTupleSeperator();
//        xb_textBlock.setDecimalSeparator(SosConfigurator.getInstance().getDecimalSeparator());
//        xb_textBlock.setTokenSeparator(SosConfigurator.getInstance().getTokenSeperator());
//        xb_textBlock.setBlockSeparator(SosConfigurator.getInstance().getTupleSeperator());
//
//        DataValuePropertyType xb_values = xb_dataArray.addNewValues();
//        xb_values.newCursor().setTextValue(qcache.getProcessingStatusResultString(tokenSeperator, tupelSeperator));
//        
//        metadataPropertyType.set(xb_dataArrayDoc);
//    	
//    }

}