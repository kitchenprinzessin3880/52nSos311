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

package org.n52.sos.ds.insert;

import org.n52.sos.ds.AbstractConnectionPool;

/**
 * Interface which offers factory methods to create the InsertDAOs
 * 
 * @author Christoph Stasch
 * 
 * @version 0.1
 */
public interface IInsertDAOFactory {

    /**
     * 
     * @return Returns an IInsertFoiDAO
     */
    public IInsertFoiDAO getInsertFoiDAO();

    /**
     * 
     * @return Returns an IInsertDomainFeatureDAO
     */
    public IInsertDomainFeatureDAO getInsertDomainFeatureDAO();

    /**
     * 
     * @return Returns an IInsertObservationDAO
     */
    public IInsertObservationDAO getInsertObservationDAO();

    /**
     * 
     * @return Returns an IInsertOfferingDAO
     */
    public IInsertOfferingDAO getInsertOfferingDAO();

    /**
     * 
     * @return Returns an IInsertPhenomenonDAO
     */
    public IInsertPhenomenonDAO getInsertPhenomenonDAO();

    /**
     * 
     * @return Returns an IInsertProcedureDAO
     */
    public IInsertProcedureDAO getInsertProcedureDAO();

    /**
     * 
     * @return Returns an IInsertProcPhenDAO
     */
    public IInsertRelationshipsDAO getInsertRelationshipsDAO();

    /**
     * 
     * @return Returns the ConnectionPool which offers connections to DB
     */
    public AbstractConnectionPool getConnectionPool();
}
