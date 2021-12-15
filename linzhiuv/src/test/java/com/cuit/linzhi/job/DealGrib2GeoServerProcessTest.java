package com.cuit.linzhi.job;


import com.cuit.linzhi.job.grib.job.DealGrib2GeoServerProcess;
import com.cuit.linzhi.job.grib.temisjob.DealGribTemisGeoServerProcess;
import org.junit.Test;
import org.quartz.JobExecutionException;


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


}