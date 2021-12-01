package com.cuit.mete.jobs.mete.domain;

public class WeatherStation {
    private String station_id_c;
    private String station_name;
    private String admin_code_chn;
    private String town_code;
    private String country;
    private String province;
    private String city;
    private String cnty;
    private String town;
    private String station_levl;
    private Double lat;
    private Double lon;
    private Double alti;

    //不能缺少空构造函数
    public WeatherStation() {
    }

    public WeatherStation(String station_id_c, String station_name, String admin_code_chn, String town_code, String country, String province, String city, String cnty, String town, String station_levl, Double lat, Double lon, Double alti) {
        this.station_id_c = station_id_c;
        this.station_name = station_name;
        this.admin_code_chn = admin_code_chn;
        this.town_code = town_code;
        this.country = country;
        this.province = province;
        this.city = city;
        this.cnty = cnty;
        this.town = town;
        this.station_levl = station_levl;
        this.lat = lat;
        this.lon = lon;
        this.alti = alti;
    }

    public String getStation_id_c() {
        return station_id_c;
    }

    public void setStation_id_c(String station_id_c) {
        this.station_id_c = station_id_c;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getAdmin_code_chn() {
        return admin_code_chn;
    }

    public void setAdmin_code_chn(String admin_code_chn) {
        this.admin_code_chn = admin_code_chn;
    }

    public String getTown_code() {
        return town_code;
    }

    public void setTown_code(String town_code) {
        this.town_code = town_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStation_levl() {
        return station_levl;
    }

    public void setStation_levl(String station_levl) {
        this.station_levl = station_levl;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getAlti() {
        return alti;
    }

    public void setAlti(Double alti) {
        this.alti = alti;
    }

    @Override
    public String toString() {
        return "weatherStation{" +
                "station_id_c='" + station_id_c + '\'' +
                ", station_name='" + station_name + '\'' +
                ", admin_code_chn='" + admin_code_chn + '\'' +
                ", town_code='" + town_code + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", cnty='" + cnty + '\'' +
                ", town='" + town + '\'' +
                ", station_levl='" + station_levl + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", alti=" + alti +
                '}';
    }
}

