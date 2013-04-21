package com.medisa.myspacecal;

public class SatelliteIntegral {
	String dataStart = "";
	String dataEnd = "";
	String raj2000 = "";
	String decj2000 = "";
	String target = "";
	
	
	
	public SatelliteIntegral(String dataStart, String dataEnd, String raj2000,
			String decj2000, String target) {
		super();
		this.dataStart = dataStart;
		this.dataEnd = dataEnd;
		this.raj2000 = raj2000;
		this.decj2000 = decj2000;
		this.target = target;
	}
	public String getDataStart() {
		return dataStart;
	}
	public void setDataStart(String dataStart) {
		this.dataStart = dataStart;
	}
	public String getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(String dataEnd) {
		this.dataEnd = dataEnd;
	}
	public String getRaj2000() {
		return raj2000;
	}
	public void setRaj2000(String raj2000) {
		this.raj2000 = raj2000;
	}
	public String getDecj2000() {
		return decj2000;
	}
	public void setDecj2000(String decj2000) {
		this.decj2000 = decj2000;
	}
	public String getTarget() {
		return target;
	}
	public void setStrumento(String target) {
		this.target = target;
	}
	
	
}
