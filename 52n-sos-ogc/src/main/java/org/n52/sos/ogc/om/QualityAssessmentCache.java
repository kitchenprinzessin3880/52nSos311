package org.n52.sos.ogc.om;

import java.util.List;

import org.apache.log4j.Logger;
import org.n52.sos.ogc.om.SosProcessingStatus;
import org.n52.sos.ogc.om.SosQualifier;

public class QualityAssessmentCache {

	private static QualityAssessmentCache _this;
	private static Logger LOG = Logger.getLogger(QualityAssessmentCache.class);
	private List<SosQualifier> qualifiers;
	private List<SosProcessingStatus> processingStati;
	private String groupIdName;
	private String genericQualifierName;
	private String specificQualifierName;
	private String processingStatusIdName;
	private String processingStatusName;
	//modified by ASD
	private String processingStatiDescName;

	public String getProcessingStatiDescName() {
		return processingStatiDescName;
	}

	public void setProcessingStatiDescName(String processingStatiDescName) {
		this.processingStatiDescName = processingStatiDescName;
	}

	private String qualifiersResultString;
	private String processingStatiResultString;

	/**
	 * @precondition static singelton method getInstance/1 was invoked before
	 * @return
	 */
	public static QualityAssessmentCache getInstance() {
		return _this;
	}

	public static QualityAssessmentCache getInstance(
			List<SosQualifier> qualifiers,
			List<SosProcessingStatus> processingStati, String groupIdName,
			String genericQualifierName, String specificQualifierName,
			String processingStatusIdName, String processingStatusName, String processingStatiDescName) {
		if (_this == null) {
			_this = new QualityAssessmentCache(qualifiers, processingStati,
					groupIdName, genericQualifierName, specificQualifierName,
					processingStatusIdName, processingStatusName, processingStatiDescName);
		}
		return _this;
	}

	private QualityAssessmentCache(List<SosQualifier> qualifiers,
			List<SosProcessingStatus> processingStati, String groupIdName,
			String genericQualifierName, String specificQualifierName,
			String processingStatusIdName, String processingStatusName, String processingStatiDescName) {
		this.qualifiers = qualifiers;
		this.processingStati = processingStati;
		this.genericQualifierName = genericQualifierName;
		this.groupIdName = groupIdName;
		this.processingStatusIdName = processingStatusIdName;
		this.processingStatusName = processingStatusName;
		this.processingStatiDescName = processingStatiDescName;
		this.specificQualifierName = specificQualifierName;
		this.qualifiersResultString = null;
		this.processingStatiResultString = null;
		LOG.debug("Quality Assessment: qualifiers: "
				+ this.qualifiers.toString());
		LOG.debug("Quality Assessment: processingstati: "
				+ this.processingStati.toString());
	}

	public List<SosQualifier> getQualifiers() {
		return qualifiers;
	}

	public List<SosProcessingStatus> getProcessingStati() {
		return processingStati;
	}

	public String getGroupIdName() {
		return groupIdName;
	}

	public String getGenericQualifierName() {
		return genericQualifierName;
	}

	public String getSpecificQualifierName() {
		return specificQualifierName;
	}

	public String getProcessingStatusIdName() {
		return processingStatusIdName;
	}

	public String getProcessingStatusName() {
		return processingStatusName;
	}

	public String getQualifierResultString(String tokenSeperator,
			String tupelSeperator) {
		if (this.qualifiersResultString == null) {
			StringBuffer result = new StringBuffer();
			for (SosQualifier qualifier : this.qualifiers) {
				result.append(qualifier.toString(tokenSeperator)).append(
						tupelSeperator);
			}
			this.qualifiersResultString = result.substring(0,
					result.length() - 1);
		}
		return this.qualifiersResultString;
	}

	public String getProcessingStatusResultString(String tokenSeperator,
			String tupelSeperator) {
		if (this.processingStatiResultString == null) {
			StringBuffer result = new StringBuffer();

			for (SosProcessingStatus procStat : this.processingStati) {
				result.append(procStat.toString(tokenSeperator)).append(
						tupelSeperator);
			}
			
			this.processingStatiResultString = result.substring(0,result.length() - 1);
		}
		return this.processingStatiResultString;
	}
}