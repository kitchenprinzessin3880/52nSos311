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

import java.util.ArrayList;

/**
 * class represents a composite phenomenon
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public class SosCompositePhenomenon {

    /** the id of the composite phenomenon */
    private String compPhenId;

    /** the description of the composite phenomenon */
    private String compPhenDesc;

    /** the components of the composite phenomenon */
    private ArrayList<SosPhenomenon> phenomenonComponents;

    /**
     * standard constructor
     * 
     * @param compPhenId
     *        id of the composite phenomenon
     * @param compPhenDesc
     *        description of the composite phenomenon
     * @param phenomenonComponents
     *        components of the composite phenomenon
     */
    public SosCompositePhenomenon(String compPhenId,
                                  String compPhenDesc,
                                  ArrayList<SosPhenomenon> phenomenonComponents) {
        setCompPhenId(compPhenId);
        setCompPhenDesc(compPhenDesc);
        setPhenomenonComponents(phenomenonComponents);
    }

    /**
     * @return Returns the compPhenDesc.
     */
    public String getCompPhenDesc() {
        return compPhenDesc;
    }

    /**
     * @param compPhenDesc
     *        The compPhenDesc to set.
     */
    public void setCompPhenDesc(String compPhenDesc) {
        this.compPhenDesc = compPhenDesc;
    }

    /**
     * @return Returns the compPhenId.
     */
    public String getCompPhenId() {
        return compPhenId;
    }

    /**
     * @param compPhenId
     *        The compPhenId to set.
     */
    public void setCompPhenId(String compPhenId) {
        this.compPhenId = compPhenId;
    }

    /**
     * @return Returns the phenomenonComponents.
     */
    public ArrayList<SosPhenomenon> getPhenomenonComponents() {
        return phenomenonComponents;
    }

    /**
     * @param phenomenonComponents
     *        The phenomenonComponents to set.
     */
    public void setPhenomenonComponents(ArrayList<SosPhenomenon> phenomenonComponents) {
        this.phenomenonComponents = phenomenonComponents;
    }
}
