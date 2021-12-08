package com.cuit.mete.jobs.grib.job;

import com.cuit.mete.jobs.grib.temisjob.DealGribTemisGeoServerProcess;
import com.cuit.mete.jobs.grib.job.DealGrib2GeoServerProcess;
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