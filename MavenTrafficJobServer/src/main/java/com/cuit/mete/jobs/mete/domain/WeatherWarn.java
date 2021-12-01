package com.cuit.mete.jobs.mete.domain;

import java.util.Date;

public class WeatherWarn {
	private String station;
	private Date publish_time;
	private String alert;
	private String province;
	private String city;
	private String fmeans;
	private String issuecontent;
	private String pic;
	private String pic2;
	private String signallevel;
	private String signaltype;
	private String url;
	
	public WeatherWarn(){}
	public WeatherWarn(String station,
			Date publish_time,
			String alert,
			String province,
			String city,
			String fmeans,
			String issuecontent,
			String pic,
			String pic2,
			String signallevel,
			String signaltype,
			String url){
		this.station = station;
		this.publish_time = publish_time;
		this.alert = alert;
		this.province = province;
		this.city = city;
		this.fmeans = fmeans;
		this.issuecontent = issuecontent;
		this.pic = pic;
		this.pic2 = pic2;
		this.signallevel = signallevel;
		this.signaltype = signaltype;
		this.url = url;
	}
	
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public Date getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(Date publish_time) {
		this.publish_time = publish_time;
	}
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getFmeans() {
		return fmeans;
	}
	public void setFmeans(String fmeans) {
		this.fmeans = fmeans;
	}
	public String getIssuecontent() {
		return issuecontent;
	}
	public void setIssuecontent(String issuecontent) {
		this.issuecontent = issuecontent;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getPic2() {
		return pic2;
	}
	public void setPic2(String pic2) {
		this.pic2 = pic2;
	}
	public String getSignallevel() {
		return signallevel;
	}
	public void setSignallevel(String signallevel) {
		this.signallevel = signallevel;
	}
	public String getSignaltype() {
		return signaltype;
	}
	public void setSignaltype(String signaltype) {
		this.signaltype = signaltype;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTableName() {
        return "tb_da_weacher_warn";
    }
}
