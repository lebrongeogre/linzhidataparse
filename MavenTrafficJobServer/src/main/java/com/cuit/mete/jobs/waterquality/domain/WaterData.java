package com.cuit.mete.jobs.waterquality.domain;

import java.util.Date;

public class WaterData {
	private String stationid;
	private String city;
	private String basinname;
	private String stationname;
	private String rivername;
	private Date publishtime;
	private Double kmno;
	private Double nh;
	private Double p;
	private Double turbid;
	private Double o;
	private Double ph;
	private String biotoxicity;
	private Double temperature;
	private Double electric;
	private Double n;
	private Double chlorophyll;
	private String checklevel;
	private String targetwater;
	private String noreachfacts;
	private Date collectingtime;
	private String latitude;
	private String longitude;
	
	public Double getKmno() {
		return kmno;
	}
	public void setKmno(Double kmno) {
		this.kmno = kmno;
	}
	public Double getNh() {
		return nh;
	}
	public void setNh(Double nh) {
		this.nh = nh;
	}
	public Double getP() {
		return p;
	}
	public void setP(Double p) {
		this.p = p;
	}
	public Double getTurbid() {
		return turbid;
	}
	public void setTurbid(Double turbid) {
		this.turbid = turbid;
	}
	public Double getO() {
		return o;
	}
	public void setO(Double o) {
		this.o = o;
	}
	public Double getPh() {
		return ph;
	}
	public void setPh(Double ph) {
		this.ph = ph;
	}
	public String getBiotoxicity() {
		return biotoxicity;
	}
	public void setBiotoxicity(String biotoxicity) {
		this.biotoxicity = biotoxicity;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Double getElectric() {
		return electric;
	}
	public void setElectric(Double electric) {
		this.electric = electric;
	}
	public Double getN() {
		return n;
	}
	public void setN(Double n) {
		this.n = n;
	}
	public Double getChlorophyll() {
		return chlorophyll;
	}
	public void setChlorophyll(Double chlorophyll) {
		this.chlorophyll = chlorophyll;
	}
	public String getChecklevel() {
		return checklevel;
	}
	public void setChecklevel(String checklevel) {
		this.checklevel = checklevel;
	}
	public String getTargetwater() {
		return targetwater;
	}
	public void setTargetwater(String targetwater) {
		this.targetwater = targetwater;
	}
	
	public String getStationid() {
		return stationid;
	}
	public void setStationid(String stationid) {
		this.stationid = stationid;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getBasinname() {
		return basinname;
	}
	public void setBasinname(String basinname) {
		this.basinname = basinname;
	}
	public String getStationname() {
		return stationname;
	}
	public void setStationname(String stationname) {
		this.stationname = stationname;
	}
	public String getRivername() {
		return rivername;
	}
	public void setRivername(String rivername) {
		this.rivername = rivername;
	}
	public Date getPublishtime() {
		return publishtime;
	}
	public void setPublishtime(Date publishtime) {
		this.publishtime = publishtime;
	}
	public String getNoreachfacts() {
		return noreachfacts;
	}
	public void setNoreachfacts(String noreachfacts) {
		this.noreachfacts = noreachfacts;
	}
	public Date getCollectingtime() {
		return collectingtime;
	}
	public void setCollectingtime(Date collectingtime) {
		this.collectingtime = collectingtime;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String toString(){
		return stationname + "," + rivername + "," + publishtime + "," + kmno + ',' + nh + "," + p + ","
				+ turbid + "," + o + "," + ph + "," + biotoxicity + "," + temperature + ","
				+ electric + "," + n + "," + chlorophyll + "," + checklevel + ","
				+ targetwater + "," + noreachfacts;
	}
}
