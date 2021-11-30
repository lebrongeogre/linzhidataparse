package com.cuit.linzhi.vo;

public class RadiationBody {
    private Integer id;

    private String stationid;

    private String year;

    private String month;

    private Double allradiation;

    private Double uvradiation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStationid() {
        return stationid;
    }

    public void setStationid(String stationid) {
        this.stationid = stationid == null ? null : stationid.trim();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year == null ? null : year.trim();
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month == null ? null : month.trim();
    }

    public Double getAllradiation() {
        return allradiation;
    }

    public void setAllradiation(Double allradiation) {
        this.allradiation = allradiation;
    }

    public Double getUvradiation() {
        return uvradiation;
    }

    public void setUvradiation(Double uvradiation) {
        this.uvradiation = uvradiation;
    }
}