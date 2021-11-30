package com.cuit.linzhi.vo;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrueMeanTimeDifferenceBody {
    private Integer id;

    private String date;

    private String timeDifferenceValue;

    private String station;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

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

    public String getTimeDifferenceValue() {
        return timeDifferenceValue;
    }

    public void setTimeDifferenceValue(String timeDifferenceValue) {
        this.timeDifferenceValue = timeDifferenceValue == null ? null : timeDifferenceValue.trim();
    }

}