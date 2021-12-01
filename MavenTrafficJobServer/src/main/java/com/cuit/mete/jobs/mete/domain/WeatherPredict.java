package com.cuit.mete.jobs.mete.domain;

import java.util.Date;

public class WeatherPredict {
	private String station;
	private Date publish_time;
	private Date pt;
	private Date pre_date;
	private String phase;
	private String weather_info;
	private int weather_img;
	private double weather_temperature;
	private String wind_direct;
	private String wind_power;
	
	public WeatherPredict(){}
	
	public WeatherPredict(String station, Date publish_time, Date pt, Date pre_data, String phase,
			String weather_info, int weather_img, double weather_temperature,
			String wind_direct, String wind_power){
		this.station = station;
		this.publish_time = publish_time;
		this.pt = pt;
		this.pre_date = pre_data;
		this.phase = phase;
		this.weather_info = weather_info;
		this.weather_img = weather_img;
		this.weather_temperature = weather_temperature;
		this.wind_direct = wind_direct;
		this.wind_power = wind_power;		
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
	public Date getPt() {
		return pt;
	}
	public void setPt(Date pt) {
		this.pt = pt;
	}
	public Date getPre_date() {
		return pre_date;
	}
	public void setPre_date(Date pre_date) {
		this.pre_date = pre_date;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getWeather_info() {
		return weather_info;
	}
	public void setWeather_info(String weather_info) {
		this.weather_info = weather_info;
	}
	public int getWeather_img() {
		return weather_img;
	}
	public void setWeather_img(int weather_img) {
		this.weather_img = weather_img;
	}
	public double getWeather_temperature() {
		return weather_temperature;
	}
	public void setWeather_temperature(double weather_temperature) {
		this.weather_temperature = weather_temperature;
	}
	public String getWind_direct() {
		return wind_direct;
	}
	public void setWind_direct(String wind_direct) {
		this.wind_direct = wind_direct;
	}
	public String getWind_power() {
		return wind_power;
	}
	public void setWind_power(String wind_power) {
		this.wind_power = wind_power;
	}
	
	public String getTableName() {
        return "tb_da_weather_predict";
    }
}
