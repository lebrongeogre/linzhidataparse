package com.cuit.mete.jobs.grib.job;

import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.jobs.grib.omijob.*;
import com.cuit.mete.jobs.grib.sta.job.MonthRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.SeasonRasterStatisticsProcess;
import com.cuit.mete.jobs.grib.sta.job.YearRasterStatisticsProcess;
import org.junit.Test;
import org.quartz.JobExecutionException;

public class OMIProcessTest {
    /**
     * 读取omi nc文件
     */
    @Test
    public void testStartJob() {
        try {
            new DealGrib2GeoServerProcess().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按范围裁剪omi栅格影像
     */
    @Test
    public void testStartOMIClipJob() {
        try {
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new ClipRasterProcessOMI().startJob(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计omi每年每月均值
     */
    @Test
    public void testReadRasterYear() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new OMIYearMonthRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计moi年均值
     */
    @Test
    public void testReadOMIRasterYear() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new OMIYearRasterStatisticsProcess().startJob(null);
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
            new OMISeasonRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计omi月均值
     */
    @Test
    public void testReadRasterMonth() {
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();
            new OMIMonthRasterStatisticsProcess().startJob(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSplit() {
        String s = "011";
        int i = Integer.parseInt(s);
        System.out.println(i);

    }

}
