package com.cuit.linzhi.vo;

import org.springframework.stereotype.Repository;


public class UVPercentBody {

    private Integer month;

    private Double yita;

    private String station;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Double getYita() {
        return yita;
    }

    public void setYita(Double yita) {
        this.yita = yita;
    }

    public UVPercentBody(Integer month, Double yita, String station) {
        this.month = month;
        this.yita = yita;
        this.station = station;
    }

    public UVPercentBody() {
    }

    @Override
    public String toString() {
        return "UVPercentBody{" +
                ", month=" + month +
                ", yita=" + yita +
                '}';
    }
}