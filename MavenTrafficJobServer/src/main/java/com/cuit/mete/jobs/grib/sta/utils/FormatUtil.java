package com.cuit.mete.jobs.grib.sta.utils;

import java.math.BigDecimal;

public class FormatUtil {

    /**
     * 根据要求保留小数位数
     * @param data
     * @return
     */
    public static String reshapeDecimalPlaces(Double data, int places){

        BigDecimal bg = new BigDecimal(data);
        return String.valueOf(bg.setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue());
    }


}
