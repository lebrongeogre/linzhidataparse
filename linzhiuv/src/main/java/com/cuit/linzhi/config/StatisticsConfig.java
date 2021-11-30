package com.cuit.linzhi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.data")
public class StatisticsConfig {


    private List<String> years;
    private List<String> stations;
    private List<String> months;
    private Map<String,String> longitude;   // 地区的经度

    public Map<String, String> getLongitude() {
        return longitude;
    }

    public void setLongitude(Map<String, String> longitude) {
        this.longitude = longitude;
    }

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public List<String> getYears() {
        return years;
    }

    public void setYears(List<String> years) {
        this.years = years;
    }

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }



}
