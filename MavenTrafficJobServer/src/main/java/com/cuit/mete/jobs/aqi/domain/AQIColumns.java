package com.cuit.mete.jobs.aqi.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AQIColumns {
    private Object columns;

    public Object getColumns() {
        return columns;
    }

    public void setColumns(Object columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "Columns{" +
                "columns=" + columns +
                '}';
    }
}
