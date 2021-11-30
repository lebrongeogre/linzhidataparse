package com.cuit.linzhi.vo;

import java.util.Date;

public class StatisticsAllRadiationBody {
    private Integer id;

    private Date dataTime;

    private Double uviHourMax;

    private String station;

    private Double uviHourAccumu;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public Double getUviHourMax() {
        return uviHourMax;
    }

    public void setUviHourMax(Double uviHourMax) {
        this.uviHourMax = uviHourMax;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    public Double getUviHourAccumu() {
        return uviHourAccumu;
    }

    public void setUviHourAccumu(Double uviHourAccumu) {
        this.uviHourAccumu = uviHourAccumu;
    }
}