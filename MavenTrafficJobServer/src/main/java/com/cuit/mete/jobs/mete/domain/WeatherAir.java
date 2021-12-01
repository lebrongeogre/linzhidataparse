package com.cuit.mete.jobs.mete.domain;

import java.util.Date;

public class WeatherAir {
	private String station;
	private Date forecasttime;
	private Integer aq;
	private Integer aqi;
	private String aqi_code;
	private String aqi_text;
	
	public WeatherAir(){}
	public WeatherAir(String station, Date forecasttime, Integer aq,
			Integer aqi, String aqi_code, String aqi_text){
		this.station = station;
		this.forecasttime = forecasttime;
		this.aq = aq;
		this.aqi = aqi;
		this.aqi_code = aqi_code;
		this.aqi_text = aqi_text;
	}
	
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public Date getForecasttime() {
		return forecasttime;
	}
	public void setForecasttime(Date forecasttime) {
		this.forecasttime = forecasttime;
	}
	public Integer getAq() {
		return aq;
	}
	public void setAq(Integer aq) {
		this.aq = aq;
	}
	public Integer getAqi() {
		return aqi;
	}
	public void setAqi(Integer aqi) {
		this.aqi = aqi;
	}
	public String getAqi_code() {
		return aqi_code;
	}
	public void setAqi_code(String aqi_code) {
		this.aqi_code = aqi_code;
	}
	public String getAqi_text() {
		return aqi_text;
	}
	public void setAqi_text(String aqi_text) {
		this.aqi_text = aqi_text;
	}
	public String getTableName() {
        return "tb_da_weather_air";
    }
}
