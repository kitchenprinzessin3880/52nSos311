package org.n52.sos.ogc.om;

public class SosQualifier {
	
	@Override
	public String toString() {
		return "SosQualifier [id=" + id + ", genericId=" + genericId
				+ ", specificId=" + specificId + ", genericCode=" + genericCode
				+ ", specificCode=" + specificCode + "]";
	}

	public SosQualifier(int id, int genericId, int specificId,
			String genericCode, String specificCode) {
		super();
		this.id = id;
		this.genericId = genericId;
		this.specificId = specificId;
		this.genericCode = genericCode;
		this.specificCode = specificCode;
	}
	
	public SosQualifier() {}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGenericId() {
		return genericId;
	}
	public void setGenericId(int genericId) {
		this.genericId = genericId;
	}
	public int getSpecificId() {
		return specificId;
	}
	public void setSpecificId(int specificId) {
		this.specificId = specificId;
	}
	public String getGenericCode() {
		return genericCode;
	}
	public void setGenericCode(String genericCode) {
		this.genericCode = genericCode;
	}
	public String getSpecificCode() {
		return specificCode;
	}
	public void setSpecificCode(String specificCode) {
		this.specificCode = specificCode;
	}
	private int id;
	private int genericId;
	private int specificId;
	private String genericCode;
	private String specificCode;
	
	public String toString(String seperator){
		return new StringBuffer().append(this.id).append(seperator)
				.append(this.genericCode).append(seperator)
				.append(this.specificCode).toString();
	}
	
	
	
	
}
