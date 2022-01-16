package com.cuit.mete.jobs.grib.job.vo;

public class GridPoint {
    public double longitude;
    public double latitude;
    public double value;

    public GridPoint() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String toString() {
        return this.longitude + "," + this.latitude + "," + this.value;
    }
}
