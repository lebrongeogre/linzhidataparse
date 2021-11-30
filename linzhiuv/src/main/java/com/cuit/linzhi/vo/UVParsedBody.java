package com.cuit.linzhi.vo;

public class UVParsedBody {

    private Integer id;

    private String station;

    private String year;

    private String month;

    private String day;

    private String hour;

    private String uviHourMax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station == null ? null : station.trim();
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day == null ? null : day.trim();
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour == null ? null : hour.trim();
    }

    public String getUviHourMax() {
        return uviHourMax;
    }

    public void setUviHourMax(String uviHourMax) {
        this.uviHourMax = uviHourMax == null ? null : uviHourMax.trim();
    }
}