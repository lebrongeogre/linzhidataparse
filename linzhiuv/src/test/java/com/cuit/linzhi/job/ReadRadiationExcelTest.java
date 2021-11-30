package com.cuit.linzhi.job;

import org.junit.Test;

public class ReadRadiationExcelTest {


    @Test
    public void testProcess() {
        new ReadRadiationExcelProcess().parseExcelDataToDB();
    }

}
