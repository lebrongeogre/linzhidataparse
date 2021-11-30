package com.cuit.linzhi.vo;

import java.util.Date;

public class StatisticsUVIBody {
    private Integer id;

    private Date dataTime;

    private String uviHourMax;

    private String station;

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

    public String getUviHourMax() {
        return uviHourMax;
    }

    public void setUviHourMax(String uviHourMax) {
        this.uviHourMax = uviHourMax == null ? null : uviHourMax.trim();
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
    }

    @Override
    public String toString() {
        return "StatisticsUVIBody{" +
                "id=" + id +
                ", dataTime=" + dataTime +
                ", uviHourMax='" + uviHourMax + '\'' +
                ", station='" + station + '\'' +
                '}';
    }
}