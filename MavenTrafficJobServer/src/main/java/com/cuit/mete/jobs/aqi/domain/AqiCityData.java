package com.cuit.mete.jobs.aqi.domain;

import java.util.Date;

public class AqiCityData {
    private String code;
    private String citycode;
    private String cityname;
    private Date timepoint;
    private Double aqi;
    private String primarypollutant;
    private Integer index_mark;
    private Double co;
    private Integer co_mark;
    private Double no2;
    private Integer no2_mark;
    private Double so2;
    private Integer so2_mark;
    private Double pm10;
    private Integer pm10_mark;
    private Double pm2_5;
    private Integer pm2_5_mark;
    private Double o3;
    private Integer o3_mark;
    private String state;

    public AqiCityData() {
    }

    public AqiCityData(String code, String citycode, String cityname, Date timepoint, Double aqi, String primarypollutant, Integer index_mark, Double co, Integer co_mark, Double no2, Integer no2_mark, Double so2, Integer so2_mark, Double pm10, Integer pm10_mark, Double pm2_5, Integer pm2_5_mark, Double o3, Integer o3_mark, String state) {
        this.code = code;
        this.citycode = citycode;
        this.cityname = cityname;
        this.timepoint = timepoint;
        this.aqi = aqi;
        this.primarypollutant = primarypollutant;
        this.index_mark = index_mark;
        this.co = co;
        this.co_mark = co_mark;
        this.no2 = no2;
        this.no2_mark = no2_mark;
        this.so2 = so2;
        this.so2_mark = so2_mark;
        this.pm10 = pm10;
        this.pm10_mark = pm10_mark;
        this.pm2_5 = pm2_5;
        this.pm2_5_mark = pm2_5_mark;
        this.o3 = o3;
        this.o3_mark = o3_mark;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public Date getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(Date timepoint) {
        this.timepoint = timepoint;
    }

    public Double getAqi() {
        return aqi;
    }

    public void setAqi(Double aqi) {
        this.aqi = aqi;
    }

    public String getPrimarypollutant() {
        return primarypollutant;
    }

    public void setPrimarypollutant(String primarypollutant) {
        this.primarypollutant = primarypollutant;
    }

    public Integer getIndex_mark() {
        return index_mark;
    }

    public void setIndex_mark(Integer index_mark) {
        this.index_mark = index_mark;
    }

    public Double getCo() {
        return co;
    }

    public void setCo(Double co) {
        this.co = co;
    }

    public Integer getCo_mark() {
        return co_mark;
    }

    public void setCo_mark(Integer co_mark) {
        this.co_mark = co_mark;
    }

    public Double getNo2() {
        return no2;
    }

    public void setNo2(Double no2) {
        this.no2 = no2;
    }

    public Integer getNo2_mark() {
        return no2_mark;
    }

    public void setNo2_mark(Integer no2_mark) {
        this.no2_mark = no2_mark;
    }

    public Double getSo2() {
        return so2;
    }

    public void setSo2(Double so2) {
        this.so2 = so2;
    }

    public Integer getSo2_mark() {
        return so2_mark;
    }

    public void setSo2_mark(Integer so2_mark) {
        this.so2_mark = so2_mark;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Integer getPm10_mark() {
        return pm10_mark;
    }

    public void setPm10_mark(Integer pm10_mark) {
        this.pm10_mark = pm10_mark;
    }

    public Double getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(Double pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public Integer getPm2_5_mark() {
        return pm2_5_mark;
    }

    public void setPm2_5_mark(Integer pm2_5_mark) {
        this.pm2_5_mark = pm2_5_mark;
    }

    public Double getO3() {
        return o3;
    }

    public void setO3(Double o3) {
        this.o3 = o3;
    }

    public Integer getO3_mark() {
        return o3_mark;
    }

    public void setO3_mark(Integer o3_mark) {
        this.o3_mark = o3_mark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTableName() {
        return "tb_da_aqicitydayrptdata";
    }

    @Override
    public String toString() {
        return "AqiCityData{" +
                "code='" + code + '\'' +
                ", citycode='" + citycode + '\'' +
                ", cityname='" + cityname + '\'' +
                ", timepoint=" + timepoint +
                ", aqi=" + aqi +
                ", primarypollutant='" + primarypollutant + '\'' +
                ", index_mark=" + index_mark +
                ", co=" + co +
                ", co_mark=" + co_mark +
                ", no2=" + no2 +
                ", no2_mark=" + no2_mark +
                ", so2=" + so2 +
                ", so2_mark=" + so2_mark +
                ", pm10=" + pm10 +
                ", pm10_mark=" + pm10_mark +
                ", pm2_5=" + pm2_5 +
                ", pm2_5_mark=" + pm2_5_mark +
                ", o3=" + o3 +
                ", o3_mark=" + o3_mark +
                ", state='" + state + '\'' +
                '}';
    }
}
