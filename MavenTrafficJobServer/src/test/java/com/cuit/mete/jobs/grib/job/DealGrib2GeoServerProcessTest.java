package com.cuit.mete.jobs.grib.job;

import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.jobs.grib.temisjob.ClipRasterProcess;
import com.cuit.mete.jobs.grib.temisjob.DealGribTemisGeoServerProcess;
import com.cuit.mete.jobs.grib.job.DealGrib2GeoServerProcess;
import org.junit.Test;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DealGrib2GeoServerProcessTest {

    @Test
    public void startJob() {
        try {
            new DealGrib2GeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStartJob() {
        try {
            new DealGrib2GeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStartTemisJob() {
        try {
            new DealGribTemisGeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNumExtract() {
        Date dt = new Date();
        System.out.println(dt.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    }

    @Test
    public void testStartTemisClipJob() {
        try {
            new ClipRasterProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadRaster() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new BaseMethod().readRaster("D:\\temp\\ncjtwarn_HIGHTEM_20210929134645_1632894405844.tif");
            new BaseMethod().readRaster("D:\\temp\\ncjtwarn_HIGHTEM_20210929134645_1632894405844.tif");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}