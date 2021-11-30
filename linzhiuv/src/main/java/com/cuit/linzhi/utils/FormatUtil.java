package com.cuit.linzhi.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class FormatUtil {

    /**
     * 根据要求保留小数位数
     * @param data
     * @return
     */
    public static Double reshapeDecimalPlaces(Double data,int places){

        BigDecimal bg = new BigDecimal(data);
        return bg.setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
