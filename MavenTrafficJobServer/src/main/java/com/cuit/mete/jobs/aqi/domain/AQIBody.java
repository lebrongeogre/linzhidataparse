package com.cuit.mete.jobs.aqi.domain;

import java.util.List;

public class AQIBody {
    private List<AQIColumns> data;
    private Long timePoint;

    public List<AQIColumns> getData() {
        return data;
    }

    public void setData(List<AQIColumns> data) {
        this.data = data;
    }

    public Long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(Long timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public String toString() {
        return "AQIBody{" +
                "data=" + data +
                ", timePoint=" + timePoint +
                '}';
    }
}
