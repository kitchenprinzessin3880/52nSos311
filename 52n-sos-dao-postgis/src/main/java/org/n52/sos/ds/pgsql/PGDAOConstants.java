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

package org.n52.sos.ds.pgsql;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * class pgconfigurator loads the dao constants like tablenames and columnnames from the dssos.config file and
 * saves them into variables
 * 
 * @author staschc
 * 
 */
public class PGDAOConstants {

	/** change **
	 * author: juergen sorg
	 * date: 2013-06-17
	 */
	private static String PN_QUALITY_ASSESSMENT_GENERIC_QUALIFIER_NAME="QUALITY_ASSESSMENT_GENERIC_QUALIFIER_NAME";
	private static String PN_QUALITY_ASSESSMENT_SPECIFIC_QUALIFIER_NAME="QUALITY_ASSESSMENT_SPECIFIC_QUALIFIER_NAME";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIER_ID_NAME="QUALITY_ASSESSMENT_QUALIFIER_ID_NAME";

	private static String PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_NAME="QUALITY_ASSESSMENT_PROCESSINGSTATI_NAME";
	private static String PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_NAME="QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_NAME";
	
	private static String PN_QUALITY_ASSESSMENT_GROUP_ID="QUALITY_ASSESSMENT_GROUP_ID_CN";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIER_ID="QUALITY_ASSESSMENT_GENERIC_QUALIFIER_ID_CN";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIERGROUPS_TABLE_QUALIFIER_ID="QUALITY_ASSESSMENT_QUALIFIERGROUPS_TABLE_QUALIFIER_ID_CN";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIER_CODE="QUALITY_ASSESSMENT_QUALIFIER_CODE_CN";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIER_TN="QUALITY_ASSESSMENT_QUALIFIER_TN";
	private static String PN_QUALITY_ASSESSMENT_QUALIFIERGROUP_TN="QUALITY_ASSESSMENT_QUALIFIERGroup_TN";


	private static String PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_TN="QUALITY_ASSESSMENT_PROCESSINGSTATI_TN";
	private static String PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_CN="QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_CN";
	private static String PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_CODE_CN="QUALITY_ASSESSMENT_PROCESSINGSTATI_CODE_CN";
	
	private String qualityAssessmentGroupIdCn="objectid";
	private String qualityAssessmentQualifierGroupTableGroupIdCn="groupid";
	private String qualityAssessmentQualifierIdCn="objectid";
	private String qualityAssessmentQualifierGroupsTableQualifierIdCn="qualifierid";
	private String qualityAssessmentQualifierCodeCn="code";
	private String qualityAssessmentQualifierTablename="observationreferences.qualifiers";
	private String qualityAssessmentSpecificQualifierGroupTablename="observationreferences.qualifiergroups";
	
	private String processingStatiId="objectid";
	private String processingStatiCode="code";
	private String processingStatiDesc="shortdesc";
	private String processingStatiTn="observationreferences.processingstati";
	
	private String qualifierIdName="QualifierId";
	private String genericQualifierName="GenericQualifier";
	private String specificQualifierName="SpecificQualifier";
	
	private String processingStatiIdName="ProcessingStatusId";
	private String processingStatiName="ProcessingStatusCode";
	private String processingStatiDescName="ProcessingStatusDesc";
	
	
	
	//modified by ASD
	public String getProcessingStatiDescName() {
		return processingStatiDescName;
	}

	public void setProcessingStatiDescName(String processingStatiDescName) {
		this.processingStatiDescName = processingStatiDescName;
	}

	public String getQualifierIdName() {
		return qualifierIdName;
	}

	public String getGenericQualifierName() {
		return genericQualifierName;
	}

	public String getSpecificQualifierName() {
		return specificQualifierName;
	}

	public String getProcessingStatiIdName() {
		return processingStatiIdName;
	}

	public String getProcessingStatiName() {
		return processingStatiName;
	}

	
	public String getQualityAssessmentGroupIdCn() {
		return qualityAssessmentGroupIdCn;
	}

	public String getQualityAssessmentQualifierIdCn() {
		return qualityAssessmentQualifierIdCn;
	}

	public String getQualityAssessmentQualifierCodeCn() {
		return qualityAssessmentQualifierCodeCn;
	}

	public String getQualityAssessmentQualifierGroupsTableQualifierIdCn() {
		return qualityAssessmentQualifierGroupsTableQualifierIdCn;
	}

	public String getQualityAssessmentQualifierTablename() {
		return qualityAssessmentQualifierTablename;
	}

	public String getQualityAssessmentSpecificQualifierGroupTablename() {
		return qualityAssessmentSpecificQualifierGroupTablename;
	}
	
	public String getQualityAssessmentQualifierGroupTableGroupIdCn() {
		return qualityAssessmentQualifierGroupTableGroupIdCn;
	}
	
	public String getProcessingStatiId() {
		return processingStatiId;
	}

	public String getProcessingStatiCode() {
		return processingStatiCode;
	}
	
	public String getProcessingStatiTn() {
		return processingStatiTn;
	}

	public String getProcessingStatiDesc() {
		return processingStatiDesc;
	}
	
	/** end change **/
	
    /** instance attribute due to singleton pattern */
    private static PGDAOConstants instance = null;

    /** logger, used for logging while initializing the constants from config file */
    private static Logger log = Logger.getLogger(PGDAOConstants.class);

    // /////////////////////////////////////////////////////////////////////////////////
    // propertyNames of table names
    private final String TNFOI = "TNFOI";

    private final String TNOBS = "TNOBS";

    private final String TNPHEN = "TNPHEN";

    private final String TNCOMPPHEN = "TNCOMPPHEN";

    private final String TNPROC = "TNPROC";

    private final String TNPROCHIST = "TNPROCHIST";

    private final String TNOFF = "TNOFF";

    private final String TNREQPHEN = "TNREQPHEN";

    private final String TNREQCOMPPHEN = "TNREQCOMPPHEN";

    private final String TNREQ = "TNREQ";

    private final String TNOBSTEMP = "TNOBSTEMP";

    private final String TNPHENOFF = "TNPHENOFF";

    private final String TNCOMPPHENOFF = "TNCOMPPHENOFF";

    private final String TNPROCPHEN = "TNPROCPHEN";

    private final String TNPROCFOI = "TNPROCFOI";

    private final String TNPROCDF = "TNPROCDF";

    private final String TNPROCOFF = "TNPROCOFF";

    private final String TNFOIOFF = "TNFOIOFF";

    private final String TNDFOFF = "TNDFOFF";

    private final String TNFOIDF = "TNFOIDF";

    private final String TNDF = "TNDF";

    private final String TNOBSDF = "TNOBSDF";

    private final String TNQUALITY = "TNQUALITY";

    // /////////////////////////////////////////////////////////////////////////////////
    // property names of column names
    private final String FEATURETYPE = "FEATURETYPE";

    private final String FOIID = "FOIID";

    private final String FOINAME = "FOINAME";

    private final String FOIDESC = "FOIDESC";

    private final String GEOM = "GEOM";

    private final String SCHEMALINK = "SCHEMALINK";

    private final String DOMAINFEATURETYPE = "DOMAINFEATURETYPE";

    private final String DOMAINFEATUREID = "DOMAINFEATUREID";

    private final String DOMAINFEATURENAME = "DOMAINFEATURENAME";

    private final String DOMAINFEATUREDESC = "DOMAINFEATUREDESC";

    private final String DOMAINFEATUREGEOM = "DOMAINFEATUREGEOM";

    private final String TIMESTAMP = "TIMESTAMP";

    private final String OBSID = "OBSID";

    private final String TEXTVALUE = "TEXTVALUE";

    private final String NUMERICVALUE = "NUMERICVALUE";

    private final String SPATIALVALUE = "SPATIALVALUE";

    private final String MIMETYPE = "MIMETYPE";

    private final String PHENID = "PHENID";

    private final String PHENDESC = "PHENDESC";

    private final String UNIT = "UNIT";

    private final String OMASLINK = "OMASLINK";

    private final String VALUETYPE = "VALUETYPE";

    private final String COMPPHENID = "COMPPHENID";

    private final String COMPPHENDESC = "COMPPHENDESC";

    private final String PROCID = "PROCID";

    private final String DESCURL = "DESCURL";

    private final String DESCTYPE = "DESCTYPE";

    private final String SMLFILE = "SMLFILE";

    private final String ACTUALPOSITION = "ACTUALPOSITION";

    private final String ACTIVETN = "ACTIVETN";

    private final String MOBILETN = "MOBILETN";

    private final String PROCHISTID = "PROCHISTID";

    private final String PROCHISTTIMESTAMP = "PROCHISTTIMESTAMP";

    private final String PROCHISTPOSITION = "PROCHISTPOSITION";

    private final String PROCHISTACTIVE = "PROCHISTACTIVE";

    private final String PROCHISTMOBILE = "PROCHISTMOBILE";

    private final String OFFERINGID = "OFFERINGID";

    private final String OFFNAME = "OFFNAME";

    private final String REQID = "REQID";

    private final String REQUEST = "REQUEST";

    private final String BEGINLEASE = "BEGINLEASE";

    private final String ENDLEASE = "ENDLEASE";

    private final String OBSTEMPID = "OBSTEMPID";

    private final String OBSTEMP = "OBSTEMP";

    private final String MINTIME = "MINTIME";

    private final String MAXTIME = "MAXTIME";

    private final String QUALITYNAME = "QUALITYNAME";

    private final String QUALITYUNIT = "QUALITYUNIT";

    private final String QUALITYVALUE = "QUALITYVALUE";

    private final String QUALITYTYPE = "QUALITYTYPE";

    // other propertynames
    private final String DAOFACTORY = "DAOFactory";

    private final String CONNECTIONSTRING = "CONNECTIONSTRING";

    private final String DRIVER = "DRIVER";

    private final String USER = "user";

    private final String PASSWORD = "password";

    private final String INITCON = "INITCON";

    private final String MAXCON = "MAXCON";

    // /////////////////////////////////////////////////////////////////////////////////
    // table names
    public static String foiTn;

    public static String obsTn;

    public static String offTn;

    public static String procTn;

    public static String procHistTn;

    public static String phenTn;

    public static String compPhenTn;

    public static String phenOffTn;

    public static String compPhenOffTn;

    public static String procPhenTn;

    public static String procFoiTn;

    public static String procDfTn;

    public static String procOffTn;

    public static String foiOffTn;

    public static String dfOffTn;

    public static String reqTn;

    public static String reqPhenTn;

    public static String reqCompPhenTn;

    public static String obsTempTn;

    public static String qualityTn;

    public static String foiDfTn;

    public static String dfTn;

    public static String obsDfTn;

    // /////////////////////////////////////////////////////////////////////////////////
    // columnnames
    // column names of foi table
    public static String featureTypeCn;

    public static String foiIDCn;

    public static String foiNameCn;

    public static String foiDescCn;

    public static String geomCn;

    public static String schemaLinkCn;

    // column names of domain feature table
    public static String domainFeatureTypeCn;

    public static String domainFeatureIDCn;

    public static String domainFeatureNameCn;

    public static String domainFeatureDescCn;

    public static String domainFeatureGeomCn;

    // column names of procedure history table
    public static String procHistProcedureIdCn;

    public static String procHistTimeStampCn;

    public static String procHistPositionCn;

    public static String procHistActiveCn;

    public static String procHistMobileCn;

    // column names of observation table
    public static String timestampCn;

    public static String mimeTypeCn;

    public static String textValueCn;

    public static String numericValueCn;

    public static String spatialValueCn;

    public static String obsIDCn;

    // column names of quality table
    public static String qualNameCn;

    public static String qualUnitCn;

    public static String qualValueCn;

    public static String qualTypeCn;

    // column names of procedure table
    public static String procIDCn;

    public static String descUrlCn;

    public static String descTypeCn;

    public static String smlFileCn;

    public static String actualPositionCn;

    public static String mobileCn;

    public static String activeCn;

    // column names of offering table
    public static String offeringIDCn;

    public static String offNameCn;

    public static String minTimeCn;

    public static String maxTimeCn;

    // column names of phenomenon
    public static String phenIDCn;

    public static String phenDescCn;

    public static String unitCn;

    public static String omAppSchemaLinkCn;

    public static String valueTypeCn;

    // column names of composite phenomenon table
    public static String compPhenIDCn;

    public static String compPhenDescCn;

    // column names of request table
    public static String requestIDCn;

    public static String requestCn;

    public static String beginLeaseCn;

    public static String endLeaseCn;

    // column names of observation_template table
    public static String obsTempIDCn;

    public static String obsTempCn;

    // other constants
    public static String daoFactory;

    public static String connectionString;

    public static String driver;

    public static String user;

    public static String password;

    public static int initcon = 0;

    public static int maxcon = 0;
    
    
    // query response names
    public static String foiSrid = "foi_srid";
    
    public static String valueSrid = "value_srid";
    
    public static String foiGeometry = "foi_geom";
    
    public static String valueGeometry = "value_geom";
    
    public static String dfSrid = "df_srid";
    
    // sql constants
    public final static String WILDCARD = "%";
    public final static String SINGLECHAR = "_";
    public final static String ESCAPECHAR = "/";
    
    /**
     * constructor
     * 
     * @param is
     *        InputStream from dssos.config file
     * @param logLevel
     *        Level for logging
     * @param handler
     *        MemoryHandler for logger
     */
    private PGDAOConstants(Properties props) {

        beginLeaseCn = props.getProperty(BEGINLEASE);
        connectionString = props.getProperty(CONNECTIONSTRING);
        compPhenTn = props.getProperty(TNCOMPPHEN);
        compPhenIDCn = props.getProperty(COMPPHENID);
        compPhenDescCn = props.getProperty(COMPPHENDESC);
        compPhenOffTn = props.getProperty(TNCOMPPHENOFF);
        daoFactory = props.getProperty(DAOFACTORY);
        driver = props.getProperty(DRIVER);
        endLeaseCn = props.getProperty(ENDLEASE);
        featureTypeCn = props.getProperty(FEATURETYPE);
        foiDescCn = props.getProperty(FOIDESC);
        foiIDCn = props.getProperty(FOIID);
        foiNameCn = props.getProperty(FOINAME);
        foiTn = props.getProperty(TNFOI);
        dfTn = props.getProperty(TNDF);
        domainFeatureTypeCn = props.getProperty(DOMAINFEATURETYPE);
        domainFeatureIDCn = props.getProperty(DOMAINFEATUREID);
        domainFeatureDescCn = props.getProperty(DOMAINFEATUREDESC);
        domainFeatureNameCn = props.getProperty(DOMAINFEATURENAME);
        domainFeatureGeomCn = props.getProperty(DOMAINFEATUREGEOM);
        foiOffTn = props.getProperty(TNFOIOFF);
        dfOffTn = props.getProperty(TNDFOFF);
        foiDfTn = props.getProperty(TNFOIDF);
        geomCn = props.getProperty(GEOM);
        initcon = new Integer(props.getProperty(INITCON)).intValue();
        maxcon = new Integer(props.getProperty(MAXCON)).intValue();
        minTimeCn = props.getProperty(MINTIME);
        maxTimeCn = props.getProperty(MAXTIME);
        numericValueCn = props.getProperty(NUMERICVALUE);
        spatialValueCn = props.getProperty(SPATIALVALUE);
        obsTn = props.getProperty(TNOBS);
        obsDfTn = props.getProperty(TNOBSDF);
        obsIDCn = props.getProperty(OBSID);
        obsTempTn = props.getProperty(TNOBSTEMP);
        obsTempIDCn = props.getProperty(OBSTEMPID);
        obsTempCn = props.getProperty(OBSTEMP);
        offeringIDCn = props.getProperty(OFFERINGID);
        offNameCn = props.getProperty(OFFNAME);
        offTn = props.getProperty(TNOFF);
        omAppSchemaLinkCn = props.getProperty(OMASLINK);
        password = props.getProperty(PASSWORD);
        phenDescCn = props.getProperty(PHENDESC);
        phenIDCn = props.getProperty(PHENID);
        phenOffTn = props.getProperty(TNPHENOFF);
        phenTn = props.getProperty(TNPHEN);
        descUrlCn = props.getProperty(DESCURL);
        procIDCn = props.getProperty(PROCID);
        descTypeCn = props.getProperty(DESCTYPE);
        smlFileCn = props.getProperty(SMLFILE);
        actualPositionCn = props.getProperty(ACTUALPOSITION);
        mobileCn = props.getProperty(MOBILETN);
        activeCn = props.getProperty(ACTIVETN);
        procPhenTn = props.getProperty(TNPROCPHEN);
        procFoiTn = props.getProperty(TNPROCFOI);
        procDfTn = props.getProperty(TNPROCDF);
        procOffTn = props.getProperty(TNPROCOFF);
        procTn = props.getProperty(TNPROC);
        procHistTn = props.getProperty(TNPROCHIST);
        procHistProcedureIdCn = props.getProperty(PROCHISTID);
        procHistTimeStampCn = props.getProperty(PROCHISTTIMESTAMP);
        procHistPositionCn = props.getProperty(PROCHISTPOSITION);
        procHistActiveCn = props.getProperty(PROCHISTACTIVE);
        procHistMobileCn = props.getProperty(PROCHISTMOBILE);
        qualityTn = props.getProperty(TNQUALITY);
        qualNameCn = props.getProperty(QUALITYNAME);
        qualUnitCn = props.getProperty(QUALITYUNIT);
        qualValueCn = props.getProperty(QUALITYVALUE);
        qualTypeCn = props.getProperty(QUALITYTYPE);
        reqTn = props.getProperty(TNREQ);
        reqPhenTn = props.getProperty(TNREQPHEN);
        reqCompPhenTn = props.getProperty(TNREQCOMPPHEN);
        requestIDCn = props.getProperty(REQID);
        requestCn = props.getProperty(REQUEST);
        schemaLinkCn = props.getProperty(SCHEMALINK);
        textValueCn = props.getProperty(TEXTVALUE);
        timestampCn = props.getProperty(TIMESTAMP);
        unitCn = props.getProperty(UNIT);
        user = props.getProperty(USER);
        valueTypeCn = props.getProperty(VALUETYPE);
        mimeTypeCn = props.getProperty(MIMETYPE);        
        log.info("PGDAOConstants initialized successfully!!");
        this.qualityAssessmentQualifierGroupsTableQualifierIdCn=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIERGROUPS_TABLE_QUALIFIER_ID, 
        		this.qualityAssessmentQualifierGroupsTableQualifierIdCn);
        this.qualityAssessmentQualifierIdCn=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIER_ID, this.qualityAssessmentQualifierIdCn);
        this.qualityAssessmentGroupIdCn=props.getProperty(PN_QUALITY_ASSESSMENT_GROUP_ID, this.qualityAssessmentGroupIdCn);
        this.qualityAssessmentQualifierCodeCn=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIER_CODE, this.qualityAssessmentQualifierCodeCn);
        this.qualityAssessmentSpecificQualifierGroupTablename=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIERGROUP_TN, this.qualityAssessmentSpecificQualifierGroupTablename);
        this.qualityAssessmentQualifierGroupsTableQualifierIdCn=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIERGROUPS_TABLE_QUALIFIER_ID, this.qualityAssessmentQualifierGroupsTableQualifierIdCn);
        this.qualityAssessmentQualifierTablename=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIER_TN, this.qualityAssessmentQualifierTablename);
        
        this.processingStatiCode=props.getProperty(PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_CODE_CN,this.processingStatiCode);
        this.processingStatiId=props.getProperty(PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_CN, this.processingStatiId);
        this.processingStatiTn=props.getProperty(PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_TN, this.processingStatiTn);
        
        this.processingStatiIdName=props.getProperty(PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_ID_NAME, this.processingStatiIdName);
        this.processingStatiName=props.getProperty(PN_QUALITY_ASSESSMENT_PROCESSINGSTATI_NAME, this.processingStatiName);
        
        this.qualifierIdName=props.getProperty(PN_QUALITY_ASSESSMENT_QUALIFIER_ID_NAME, this.qualifierIdName);
        this.genericQualifierName=props.getProperty(PN_QUALITY_ASSESSMENT_GENERIC_QUALIFIER_NAME, this.genericQualifierName);
        this.specificQualifierName=props.getProperty(PN_QUALITY_ASSESSMENT_SPECIFIC_QUALIFIER_NAME, this.specificQualifierName);
    }

    /**
     * getInstance method due to singleton pattern
     * 
     * @param is
     *        InputStream of dssos.config file
     * @param logLevel
     *        Level for logging; must be parameter cause this method is invoked in the constructor of the
     *        SosConfigurator
     * @param handler
     *        MemoryHandler for logging; must be parameter cause this method is invoked in the constructor of
     *        the SosConfigurator
     * @return The only PGDAOConstants instance
     */
    public static synchronized PGDAOConstants getInstance(Properties daoProps) {

        if (instance == null) {
            instance = new PGDAOConstants(daoProps);
            return instance;
        }
        return instance;
    }
}