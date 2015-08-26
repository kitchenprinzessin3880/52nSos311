package org.n52.sos.ogc.om;

public class SosProcessingStatus {

	@Override
	public String toString() {
		return "SosProcessingStatus [id=" + id + ", code=" + code + "]";
	}

	private int id;
	private String code;
	private String desc;;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public SosProcessingStatus() {
		super();

	}

	public SosProcessingStatus(int id, String code) {
		super();
		this.id = id;
		this.code = code;
	}
	
	//modified by ASD
	public SosProcessingStatus(int id, String code, String des) {
		super();
		this.id = id;
		this.code = code;
		this.desc = des;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String toString(String seperator) {
		//modified by ASD
		return new StringBuffer().append(this.id).append(seperator).append(this.code).append(seperator).append(this.desc).toString();
	}

}
