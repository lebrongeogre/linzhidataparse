package com.cuit.linzhi.vo;

public class StatisticsUVIParsedBody {
    private String station;

    private Double uviMonthAccumulation;

    private Integer year;

    private Integer month;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public Double getUviMonthAccumulation() {
        return uviMonthAccumulation;
    }

    public void setUviMonthAccumulation(Double uviMonthAccumulation) {
        this.uviMonthAccumulation = uviMonthAccumulation;
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
}