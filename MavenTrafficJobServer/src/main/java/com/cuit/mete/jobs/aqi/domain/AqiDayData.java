package com.cuit.mete.jobs.aqi.domain;

public class AqiDayData {
    private String citycode;
    private String staname;
    private String datatime;
    private String level;
    private Integer levelint;
    private String Contaminants;
    private Double PM25;
    private Double PM10;
    private Double SO2;
    private Double NO2;
    private Double CO;
    private Double O3;
    private Double AQI;
    private Double IPM25;
    private Double IPM10;
    private Double ISO2;
    private Double INO2;
    private Double ICO;
    private Double IO3;
    private Long etlid;

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getDatatime() {
        return datatime;
    }

    public void setDatatime(String datatime) {
        this.datatime = datatime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getLevelint() {
        return levelint;
    }

    public void setLevelint(Integer levelint) {
        this.levelint = levelint;
    }

    public String getContaminants() {
        return Contaminants;
    }

    public void setContaminants(String contaminants) {
        Contaminants = contaminants;
    }

    public Double getPM25() {
        return PM25;
    }

    public void setPM25(Double PM25) {
        this.PM25 = PM25;
    }

    public Double getPM10() {
        return PM10;
    }

    public void setPM10(Double PM10) {
        this.PM10 = PM10;
    }

    public Double getSO2() {
        return SO2;
    }

    public void setSO2(Double SO2) {
        this.SO2 = SO2;
    }

    public Double getNO2() {
        return NO2;
    }

    public void setNO2(Double NO2) {
        this.NO2 = NO2;
    }

    public Double getCO() {
        return CO;
    }

    public void setCO(Double CO) {
        this.CO = CO;
    }

    public Double getO3() {
        return O3;
    }

    public void setO3(Double o3) {
        O3 = o3;
    }

    public Double getAQI() {
        return AQI;
    }

    public void setAQI(Double AQI) {
        this.AQI = AQI;
    }

    public Double getIPM25() {
        return IPM25;
    }

    public void setIPM25(Double IPM25) {
        this.IPM25 = IPM25;
    }

    public Double getIPM10() {
        return IPM10;
    }

    public void setIPM10(Double IPM10) {
        this.IPM10 = IPM10;
    }

    public Double getISO2() {
        return ISO2;
    }

    public void setISO2(Double ISO2) {
        this.ISO2 = ISO2;
    }

    public Double getINO2() {
        return INO2;
    }

    public void setINO2(Double INO2) {
        this.INO2 = INO2;
    }

    public Double getICO() {
        return ICO;
    }

    public void setICO(Double ICO) {
        this.ICO = ICO;
    }

    public Double getIO3() {
        return IO3;
    }

    public void setIO3(Double IO3) {
        this.IO3 = IO3;
    }


    public String getStaname() {
        return staname;
    }

    public void setStaname(String staname) {
        this.staname = staname;
    }

    public Long getEtlid() {
        return etlid;
    }

    public void setEtlid(Long etlid) {
        this.etlid = etlid;
    }

    public String getTableName(){
        return  "tb_da_aqiday";
    }

}
