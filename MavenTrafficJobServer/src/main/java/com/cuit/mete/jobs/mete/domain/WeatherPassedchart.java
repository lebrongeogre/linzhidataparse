package com.cuit.mete.jobs.mete.domain;

import java.util.Date;

public class WeatherPassedchart {
	private String station;
	private Date dt_time;
	private Double humidity;
	private Double pressure;
	private Double rain1h;
	private Double rain6h;
	private Double rain12h;
	private Double rain24h;
	private String temp_diff;
	private Double temperature;
	private Double wind_derection;
	private Double wind_speed;
	
	public WeatherPassedchart(){}
	public WeatherPassedchart(String station,
			Date dt_time,
			Double humidity,
			Double pressure,
			Double rain1h,
			Double rain6h,
			Double rain12h,
			Double rain24h,
			String temp_diff,
			Double temperature,
			Double wind_derection,
			Double wind_speed){
		this.station = station;
		this.dt_time = dt_time;
		this.humidity = humidity;
		this.pressure = pressure;
		this.rain1h = rain1h;
		this.rain6h = rain6h;
		this.rain12h = rain12h;
		this.rain24h = rain24h;
		this.temp_diff = temp_diff;
		this.temperature = temperature;
		this.wind_derection = wind_derection;
		this.wind_speed = wind_speed;
	}
	
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public Date getDt_time() {
		return dt_time;
	}
	public void setDt_time(Date dt_time) {
		this.dt_time = dt_time;
	}
	public Double getHumidity() {
		return humidity;
	}
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	public Double getPressure() {
		return pressure;
	}
	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}
	public Double getRain1h() {
		return rain1h;
	}
	public void setRain1h(Double rain1h) {
		this.rain1h = rain1h;
	}
	public Double getRain6h() {
		return rain6h;
	}
	public void setRain6h(Double rain6h) {
		this.rain6h = rain6h;
	}
	public Double getRain12h() {
		return rain12h;
	}
	public void setRain12h(Double rain12h) {
		this.rain12h = rain12h;
	}
	public Double getRain24h() {
		return rain24h;
	}
	public void setRain24h(Double rain24h) {
		this.rain24h = rain24h;
	}
	public String getTemp_diff() {
		return temp_diff;
	}
	public void setTemp_diff(String temp_diff) {
		this.temp_diff = temp_diff;
	}
	public Double getTemperature() {
		return temperature;
	}
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}
	public Double getWind_derection() {
		return wind_derection;
	}
	public void setWind_derection(Double wind_derection) {
		this.wind_derection = wind_derection;
	}
	public Double getWind_speed() {
		return wind_speed;
	}
	public void setWind_speed(Double wind_speed) {
		this.wind_speed = wind_speed;
	}
	
	public String getTableName() {
        return "tb_da_weather_passedchart";
    }
}
