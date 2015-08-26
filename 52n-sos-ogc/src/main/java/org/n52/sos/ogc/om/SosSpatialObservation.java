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

package org.n52.sos.ogc.om;

import java.util.Collection;

import org.n52.sos.ogc.gml.time.ISosTime;
import org.n52.sos.ogc.om.features.SosAbstractFeature;
import org.n52.sos.ogc.om.quality.SosQuality;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * class represents SpatialObservation, which has a geometry as result value
 * 
 * @author staschc
 * 
 */
public class SosSpatialObservation extends AbstractSosObservation {

    /** result value, which is a geometry */
    private Geometry result;

    /**
     * constructor
     * 
     * @param time
     *        time at which the observation event took place
     * @param obsID
     *        id of the observation
     * @param procID
     *        id of the procedure, by which the value was produced
     * @param domainFeatureIDs
     *        ids of the doamin features, to which the observation belongs
     * @param phenID
     *        id of the phenomenon, of which the value is
     * @param offeringID
     *        id of the offering to which this observation belongs
     * @param value
     *        geometry, which is the result of this spatial observation
     */
    public SosSpatialObservation(ISosTime time,
                                 String obsID,
                                 String procID,
                                 Collection<SosAbstractFeature> domainFeatureIDs,
                                 String phenID,
                                 SosAbstractFeature sf,
                                 String offeringID,
                                 Geometry value,
                                 Collection<SosQuality> quality) {
        super(time, obsID, procID, domainFeatureIDs, phenID, sf, offeringID, null, quality);
        this.result = value;
    }

    @Override
    public String getElementName() {
        return OMConstants.EN_SPATIAL_OBSERVATION;
    }

    @Override
    public String getNamespace() {
        return OMConstants.NS_OM;
    }

    @Override
    public String getStringValue() {
        WKTWriter wktWriter = new WKTWriter();
        return wktWriter.write(this.result);
    }

    /**
     * @return the result
     */
    public Geometry getResult() {
        return result;
    }

    /**
     * @param result
     *        the result to set
     */
    public void setResult(Geometry result) {
        this.result = result;
    }

}
