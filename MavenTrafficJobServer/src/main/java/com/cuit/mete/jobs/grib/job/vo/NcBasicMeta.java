package com.cuit.mete.jobs.grib.job.vo;

import com.cuit.job.utils.metegrib2.DataGrid;
import com.cuit.job.utils.metegrib2.GridPoint;

import java.util.ArrayList;
import java.util.List;

public class NcBasicMeta {

    private DataGrid dataGrid;
    private float[] latLists;
    private float[] lngLists;
    private List<GridPoint> gridPoints = new ArrayList<GridPoint>();

    public List<GridPoint> getGridPoints() {
        return gridPoints;
    }

    public void setGridPoints(List<GridPoint> gridPoints) {
        this.gridPoints = gridPoints;
    }

    public DataGrid getDataGrid() {
        return dataGrid;
    }

    public void setDataGrid(DataGrid dataGrid) {
        this.dataGrid = dataGrid;
    }

    public float[] getLatLists() {
        return latLists;
    }

    public void setLatLists(float[] latLists) {
        this.latLists = latLists;
    }

    public float[] getLngLists() {
        return lngLists;
    }

    public void setLngLists(float[] lngLists) {
        this.lngLists = lngLists;
    }

}
