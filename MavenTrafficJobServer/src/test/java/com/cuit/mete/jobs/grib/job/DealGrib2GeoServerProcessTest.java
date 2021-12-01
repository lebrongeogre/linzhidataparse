package com.cuit.mete.jobs.grib.job;

import org.junit.Test;
import org.quartz.JobExecutionException;

import static org.junit.Assert.*;

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
}