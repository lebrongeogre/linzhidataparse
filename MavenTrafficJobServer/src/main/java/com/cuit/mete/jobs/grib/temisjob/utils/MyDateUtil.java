package com.cuit.mete.jobs.grib.temisjob.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理的一些工具
 */
public class MyDateUtil {

    /**
     * 根据传入的年份和数字获取日期
     *
     * @param year
     * @param num
     * @return yyyyMMdd
     */
    public static Date getDateOfYear(String year, String num) throws ParseException {

        int intMonth = 1;
        int count2 = 28;
        int countAll = 0;
        int intDay = 1;
        String month = "";
        String day = "";
        int intYear = Integer.parseInt(year);
        if ((intYear % 4 == 0 && intYear % 100 != 0) || (intYear % 400 == 0)) {
            count2 = 29;
        }
        //判断是否是闰年
        int intNum = Integer.parseInt(num);
        //判断当前天数索引的日期 格式yyyyMMdd
        for (int i = 1; i < 13; i++) {
            if (Integer.parseInt(num) <= getCountByMonth(i, count2)) {
                intMonth = i;
                intDay = Integer.parseInt(num) - countAll - getCountByMonth(i - 1, count2);
                break;
            }
            countAll += getCountByMonth(i, count2);
        }
        if (intDay < 10){
            day = "0"+intDay;
        }else{
            day = intDay+"";
        }
        if (intMonth < 10){
            month = "0"+intMonth;
        }else{
            month = intMonth+"";
        }
        String dateStr = intYear+""+month+day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.parse(dateStr);
    }

    private static int getCountByMonth(int month, int count2) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return count2;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return 0;
    }

    /**
     * 根据传入日期字符串格式格式化日期并返回
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDateToStr(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String a = sdf.format(date);
        return sdf.format(date);
    }

}
