package com.cuit.mete.jobs.aqi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AQIData {
    private Double AQI;
    private String CITYCODE;
    private String CITYNAME;
    private Double CO;
    private String CODE;
    private Boolean CO_MARK;
    private Boolean INDEX_MARK;
    private Double NO2;
    private Boolean NO2_MARK;
    private Double O3;
    private Boolean O3_MARK;
    private Double PM10;
    private Boolean PM10_MARK;
    private Double PM2_5;
    private Boolean PM2_5_MARK;
    private String PRIMARYPOLLUTANT;
    private Double SO2;
    private Boolean SO2_MARK;
    private Integer STATE;
    private Long TIMEPOINT;

    public Double getAQI() {
        return AQI;
    }

    public void setAQI(Double AQI) {
        this.AQI = AQI;
    }

    public String getCITYCODE() {
        return CITYCODE;
    }

    public void setCITYCODE(String CITYCODE) {
        this.CITYCODE = CITYCODE;
    }

    public String getCITYNAME() {
        return CITYNAME;
    }

    public void setCITYNAME(String CITYNAME) {
        this.CITYNAME = CITYNAME;
    }

    public Double getCO() {
        return CO;
    }

    public void setCO(Double CO) {
        this.CO = CO;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public Boolean getCO_MARK() {
        return CO_MARK;
    }

    public void setCO_MARK(Boolean CO_MARK) {
        this.CO_MARK = CO_MARK;
    }

    public Boolean getINDEX_MARK() {
        return INDEX_MARK;
    }

    public void setINDEX_MARK(Boolean INDEX_MARK) {
        this.INDEX_MARK = INDEX_MARK;
    }

    public Double getNO2() {
        return NO2;
    }

    public void setNO2(Double NO2) {
        this.NO2 = NO2;
    }

    public Boolean getNO2_MARK() {
        return NO2_MARK;
    }

    public void setNO2_MARK(Boolean NO2_MARK) {
        this.NO2_MARK = NO2_MARK;
    }

    public Double getO3() {
        return O3;
    }

    public void setO3(Double o3) {
        O3 = o3;
    }

    public Boolean getO3_MARK() {
        return O3_MARK;
    }

    public void setO3_MARK(Boolean o3_MARK) {
        O3_MARK = o3_MARK;
    }

    public Double getPM10() {
        return PM10;
    }

    public void setPM10(Double PM10) {
        this.PM10 = PM10;
    }

    public Boolean getPM10_MARK() {
        return PM10_MARK;
    }

    public void setPM10_MARK(Boolean PM10_MARK) {
        this.PM10_MARK = PM10_MARK;
    }

    public Double getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(Double PM2_5) {
        this.PM2_5 = PM2_5;
    }

    public Boolean getPM2_5_MARK() {
        return PM2_5_MARK;
    }

    public void setPM2_5_MARK(Boolean PM2_5_MARK) {
        this.PM2_5_MARK = PM2_5_MARK;
    }

    public String getPRIMARYPOLLUTANT() {
        return PRIMARYPOLLUTANT;
    }

    public void setPRIMARYPOLLUTANT(String PRIMARYPOLLUTANT) {
        this.PRIMARYPOLLUTANT = PRIMARYPOLLUTANT;
    }

    public Double getSO2() {
        return SO2;
    }

    public void setSO2(Double SO2) {
        this.SO2 = SO2;
    }

    public Boolean getSO2_MARK() {
        return SO2_MARK;
    }

    public void setSO2_MARK(Boolean SO2_MARK) {
        this.SO2_MARK = SO2_MARK;
    }

    public Integer getSTATE() {
        return STATE;
    }

    public void setSTATE(Integer STATE) {
        this.STATE = STATE;
    }

    public Long getTIMEPOINT() {
        return TIMEPOINT;
    }

    public void setTIMEPOINT(Long TIMEPOINT) {
        this.TIMEPOINT = TIMEPOINT;
    }

    @Override
    public String toString() {
        return "AQIData{" +
                "AQI=" + AQI +
                ", CITYCODE='" + CITYCODE + '\'' +
                ", CITYNAME='" + CITYNAME + '\'' +
                ", CO=" + CO +
                ", CODE='" + CODE + '\'' +
                ", CO_MARK=" + CO_MARK +
                ", INDEX_MARK=" + INDEX_MARK +
                ", NO2=" + NO2 +
                ", NO2_MARK=" + NO2_MARK +
                ", O3=" + O3 +
                ", O3_MARK=" + O3_MARK +
                ", PM10=" + PM10 +
                ", PM10_MARK=" + PM10_MARK +
                ", PM2_5=" + PM2_5 +
                ", PM2_5_MARK=" + PM2_5_MARK +
                ", PRIMARYPOLLUTANT='" + PRIMARYPOLLUTANT + '\'' +
                ", SO2=" + SO2 +
                ", SO2_MARK=" + SO2_MARK +
                ", STATE=" + STATE +
                ", TIMEPOINT=" + TIMEPOINT +
                '}';
    }
}
