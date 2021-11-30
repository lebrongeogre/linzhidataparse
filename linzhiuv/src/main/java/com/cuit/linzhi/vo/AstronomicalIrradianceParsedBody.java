package com.cuit.linzhi.vo;

public class AstronomicalIrradianceParsedBody {
    private String station;

    private Integer year;

    private Integer month;

    private Double uvMonthAccumulation;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getUvMonthAccumulation() {
        return uvMonthAccumulation;
    }

    public void setUvMonthAccumulation(Double uvMonthAccumulation) {
        this.uvMonthAccumulation = uvMonthAccumulation;
    }
}