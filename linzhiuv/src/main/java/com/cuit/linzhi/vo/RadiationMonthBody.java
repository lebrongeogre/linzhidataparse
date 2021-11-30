package com.cuit.linzhi.vo;

public class RadiationMonthBody {
    private String station;

    private String dataTime;

    private Double monthValue;

    private Double midmoonValue;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime == null ? null : dataTime.trim();
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
}