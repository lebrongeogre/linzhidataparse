package com.cuit.mete.jobs.grib.job;

import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.jobs.grib.omijob.ClipRasterProcessMODIS;
import org.junit.Test;
import org.quartz.JobExecutionException;

public class MODISProcessTest {

    @Test
    public void testClipModis() throws JobExecutionException {
        LicenseEngine licenseEngine = new LicenseEngine();
        licenseEngine.StartUsing();
        new ClipRasterProcessMODIS().startJob(null);
    }


}
