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

package org.n52.sos.encode;

import net.opengis.gml.AbstractTimeGeometricPrimitiveType;
import net.opengis.gml.FeatureDocument;

import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * offers methods for encoding gml elements and gml:Feature extensions;
 * implementation must be defined in conf.sos.gmlEncoder of config.properties file
 * 
 * ATTENTION: class needs XMLBeans generated jars of GML schema and schema extensions!!
 * 
 * @author Christoph Stasch
 *
 */
public interface IGMLEncoder {

    /**
     * creates an XMLBeans representation of passed SOS representation of feature; this could be also a feature collection; the type of the features
     * depend on the implementation of this method
     * 
     * @param absFeature
     *        SOS representation of the feature, which should be encoded
     * @return FeatureDocument
     *        XMLBeans representation of feature
     * @throws OwsExceptionReport 
     *          if feature type is not supported by the implementation of the IGMLEncoder
     *        
     */
    public FeatureDocument createFeature(SosAbstractFeature absFeature) throws OwsExceptionReport;

    /**
     * creates XmlBeans representation of gml:TimeObjectPropertyType from passed sos time object
     * 
     * @param time
     *        ISosTime implementation, which should be created an XmlBeans O&M representation from
     * @return Returns XmlBean representing the passed time in GML format
     * @throws OwsExceptionReport 
     */
    public AbstractTimeGeometricPrimitiveType createTime(ISosTime time) throws OwsExceptionReport;

}
