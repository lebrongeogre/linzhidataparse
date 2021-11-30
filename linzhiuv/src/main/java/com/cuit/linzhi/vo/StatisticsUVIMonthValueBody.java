package com.cuit.linzhi.vo;

public class StatisticsUVIMonthValueBody {
    private String station;

    private Integer uviMonthaccumulation;

    private Integer year;

    private Integer month;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public Integer getUviMonthaccumulation() {
        return uviMonthaccumulation;
    }

    public void setUviMonthaccumulation(Integer uviMonthaccumulation) {
        this.uviMonthaccumulation = uviMonthaccumulation;
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