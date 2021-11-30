package com.cuit.linzhi.vo;

import org.springframework.stereotype.Component;

@Component
public class TrueSolarTimeBody {
    private Integer id;

    private String date;

    private Long trueSolarTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getTrueSolarTime() {
        return trueSolarTime;
    }

    public void setTrueSolarTime(Long trueSolarTime) {
        this.trueSolarTime = trueSolarTime;
    }
}