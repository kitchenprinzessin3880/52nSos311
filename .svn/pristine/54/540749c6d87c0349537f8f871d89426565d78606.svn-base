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

package org.n52.sos.ogc.om.features;

import java.util.Collection;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.om.OMConstants;

/**
 * class represents a feature collection
 * 
 * @author Christoph Stasch
 *
 */
public class SosFeatureCollection extends SosAbstractFeature {

    /** members of this feature collection*/
    private Collection<SosAbstractFeature> members;

    /**
     * constructor
     * 
     * @param members
     *          collection with feature members of this collection
     */
    public SosFeatureCollection(Collection<SosAbstractFeature> members) {
        super("gml:FeatureCollection");
        setMembers(members);
    }

    /**
     * @return the members
     */
    public Collection<SosAbstractFeature> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(Collection<SosAbstractFeature> members) {
        this.members = members;
    }

    @Override
    public String getElementName() {
        return OMConstants.EN_FEATURE_COLLECTION;
    }

    @Override
    public String getNamespace() {
        // TODO Auto-generated method stub
        return OMConstants.NS_OM;
    }

    @Override
    public XmlObject toXmlBeans() {
        // TODO Auto-generated method stub
        return null;
    }

}
