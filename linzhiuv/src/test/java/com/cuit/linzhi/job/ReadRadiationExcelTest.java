package com.cuit.linzhi.job;

import com.cuit.linzhi.job.excel.ReadRadiationExcelProcess;
import org.junit.Test;

public class ReadRadiationExcelTest {


    @Test
    public void testProcess() {
        new ReadRadiationExcelProcess().parseExcelDataToDB();
    }

}
