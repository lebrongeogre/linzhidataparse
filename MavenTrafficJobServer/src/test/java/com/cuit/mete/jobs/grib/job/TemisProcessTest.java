package com.cuit.mete.jobs.grib.job;

import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.jobs.grib.omijob.ClipRasterProcessOMI;
import com.cuit.mete.jobs.grib.omijob.OMIYearMonthRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.MonthRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.SeasonRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.YearMonthRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.YearRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.temisjob.ClipRasterProcess;
import com.cuit.mete.jobs.grib.temisjob.DealGribTemisGeoServerProcess;
import org.junit.Test;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TemisProcessTest {

    @Test
    public void startJob() {
        try {
            new DealGrib2GeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }



    /**
     * 读取temis nc文件
     */
    @Test
    public void testStartTemisJob() {
        try {
            new DealGribTemisGeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * 按范围裁剪temis栅格影像
     */
    @Test
    public void testStartTemisClipJob() {
        try {
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new ClipRasterProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }



    /**
     * 统计temis每年每月的均值
     */
    @Test
    public void testReadRaster() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();

            new YearMonthRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计temis月均值
     */
    @Test
    public void testReadRasterMonth() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new MonthRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 统计temis年均值
     */
    @Test
    public void testReadOMIRasterYear() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new YearRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计temis季均值
     */
    @Test
    public void testReadRasterSeason() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new SeasonRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}