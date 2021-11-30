package com.cuit.linzhi.vo;

import org.springframework.stereotype.Component;


public class StatisticsMonthAccumulationBody {
    private String station;

    private Double monthValue;

    private Double midmoonValue;

    private Integer year;

    private Integer month;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public Double getMonthValue() {
        return monthValue;
    }

    public void setMonthValue(Double monthValue) {
        this.monthValue = monthValue;
    }

    public Double getMidmoonValue() {
        return midmoonValue;
    }

    public void setMidmoonValue(Double midmoonValue) {
        this.midmoonValue = midmoonValue;
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