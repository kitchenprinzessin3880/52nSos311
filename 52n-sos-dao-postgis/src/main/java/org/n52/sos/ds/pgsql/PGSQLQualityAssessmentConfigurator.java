package org.n52.sos.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.sos.ogc.om.QualityAssessmentCache;
import org.n52.sos.ogc.om.SosProcessingStatus;
import org.n52.sos.ogc.om.SosQualifier;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class PGSQLQualityAssessmentConfigurator {

	private static PGSQLQualityAssessmentConfigurator _this;
	private static Logger LOG = Logger
			.getLogger(PGSQLQualityAssessmentConfigurator.class);
	private PGConnectionPool cpool;
	
	public static PGSQLQualityAssessmentConfigurator getInstance(
			PGConnectionPool cpool, PGDAOConstants constants)
			throws OwsExceptionReport, SQLException {
		if (_this == null) {
			_this = new PGSQLQualityAssessmentConfigurator(cpool, constants);
		}
		return _this;
	}

	private PGSQLQualityAssessmentConfigurator(PGConnectionPool cpool,
			PGDAOConstants constants) throws OwsExceptionReport, SQLException {
		this.cpool = cpool;
		String sqlQualifiers = new StringBuffer("select q1.")
				.append(constants.getQualityAssessmentQualifierCodeCn())
				.append(" as generic, q2.")
				.append(constants.getQualityAssessmentQualifierCodeCn())
				.append(" as specific, q0.")
				.append(constants.getQualityAssessmentGroupIdCn())
				.append(" as groupid, q1.")
				.append(constants.getQualityAssessmentQualifierIdCn())
				.append(" as genericid, q2.")
				.append(constants.getQualityAssessmentQualifierIdCn())
				.append(" as specificid FROM ")
				.append(constants
						.getQualityAssessmentSpecificQualifierGroupTablename())
				.append(" as q0 LEFT OUTER JOIN ")
				.append(constants.getQualityAssessmentQualifierTablename())
				.append(" as q1 ON q0.")
				.append(constants
						.getQualityAssessmentQualifierGroupTableGroupIdCn())
				.append(" = q1.")
				.append(constants.getQualityAssessmentQualifierIdCn())
				.append(" LEFT OUTER JOIN ")
				.append(constants.getQualityAssessmentQualifierTablename())
				.append(" as q2 ON q0.")
				.append(constants
						.getQualityAssessmentQualifierGroupsTableQualifierIdCn())
				.append(" = q2.")
				.append(constants.getQualityAssessmentQualifierIdCn())
				.append(" ORDER BY generic,specific ").toString();
		String sqlProcessingStati = new StringBuffer("SELECT ")
				//.append(constants.getProcessingStatiId()).append(",") (FZJ)
				.append(constants.getProcessingStatiId()).append(",").append(constants.getProcessingStatiCode()).append(",")
				.append(constants.getProcessingStatiDesc()).append(" FROM ")
				.append(constants.getProcessingStatiTn()).toString();
		LOG.debug("Quality Assessment: qualifiers query: " + sqlQualifiers);
		LOG.debug("Quality Assessment: processing status query: " + sqlProcessingStati);
		List<SosQualifier> qualifiers = new ArrayList<SosQualifier>();
		List<SosProcessingStatus> processingStati = new ArrayList<SosProcessingStatus>();
		Connection con = this.cpool.getConnection();
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlQualifiers);
			while (rs.next()) {
				qualifiers.add(new SosQualifier(rs.getInt(3),
						rs.getInt(4), rs.getInt(5), rs.getString(1), rs
								.getString(2)));
			}
			rs = stmt.executeQuery(sqlProcessingStati);
			while (rs.next()) {
				//modified by ASD
				//processingStati.add(new SosProcessingStatus(rs.getInt(1),rs.getString(2)));
				processingStati.add(new SosProcessingStatus(rs.getInt(1),rs.getString(2),rs.getString(3)));
			}
		} finally {
			this.cpool.returnConnection(con);
		}
		//modified by ASD
		QualityAssessmentCache.getInstance(qualifiers, processingStati, constants.getQualifierIdName(),
				constants.getGenericQualifierName(),constants.getSpecificQualifierName(),
				constants.getProcessingStatiIdName(), constants.getProcessingStatiName(), constants.getProcessingStatiDescName());
	}

}
