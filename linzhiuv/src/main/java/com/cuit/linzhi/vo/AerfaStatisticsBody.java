package com.cuit.linzhi.vo;

public class AerfaStatisticsBody {
    private String station;

    private Integer year;

    private Integer month;

    private Double aerfa;

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

    public Double getAerfa() {
        return aerfa;
    }

    public void setAerfa(Double aerfa) {
        this.aerfa = aerfa;
    }
}