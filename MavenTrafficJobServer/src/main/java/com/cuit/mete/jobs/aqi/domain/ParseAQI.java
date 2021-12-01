package com.cuit.mete.jobs.aqi.domain;

import java.util.ArrayList;

/**
 * 用于计算各类因子日/小时AQI值以及首要污染物类型
 */
public class ParseAQI {
    //定义变量AQI
    private double AQI = 0;
    //定义变量首要污染物
    String primaryFactor = "";
    //定义变量AQI数字等级
    private static int intlevel;
    //定义变量AQI文字等级
    private static String level;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setAQI(double AQI) {
        this.AQI = AQI;
    }

    public double getAQI() {
        return AQI;
    }

    public String getPrimaryFactor() {
        return primaryFactor;
    }

    public void setPrimaryFactor(ArrayList<String> primary, int maxsum) {
        if (maxsum >= 1) {
            for (int i = 0; i < primary.size(); i++) {
                this.primaryFactor += primary.get(i) + ",";
            }
            if (this.primaryFactor.endsWith(",")) {
                this.primaryFactor = this.primaryFactor.substring(0, this.primaryFactor.length() - 1);
            }
        }
    }

    public int getIntlevel() {
        return intlevel;
    }

    public void setIntlevel(int intlevel) {
        this.intlevel = intlevel;
    }

    /**
     * 用于获得日/小时数据AQI并判断AQI等级
     *
     * @param IAQIpm10
     * @param IAQIpm25
     * @param IAQISO2
     * @param IAQINO2
     * @param IAQIO3
     * @param IAQICO
     * @return
     */
    public void calAQIAndLevel(double IAQIpm10, double IAQIpm25, double IAQISO2, double IAQINO2, double IAQIO3, double IAQICO) {

        MaxIAQIAndPrimaryFactor(IAQIpm10, IAQIpm25, IAQISO2, IAQINO2, IAQIO3, IAQICO);
        calLevel(AQI);
        if (intlevel == 0) {
            this.primaryFactor = "-";
        }
    }

    /**
     * 通过AQI确定空气污染等级
     *
     * @param
     */
    public void calLevel(double AQI) {
        //等级标准是 1-6，但为了和后面的单项等级保持从0开始，所以变为 0-5
        if (AQI > 0 && AQI <= 50) {
            setIntlevel(0);
            setLevel("优");
        } else if (AQI > 51 && AQI <= 100) {
            setIntlevel(1);
            setLevel("良");
        } else if (AQI > 100 && AQI <= 150) {
            setIntlevel(2);
            setLevel("轻度污染");
        } else if (AQI > 150 && AQI <= 200) {
            setIntlevel(3);
            setLevel("中度污染");
        } else if (AQI > 200 && AQI <= 300) {
            setIntlevel(4);
            setLevel("重度污染");
        } else if (AQI > 300) {
            setIntlevel(5);
            setLevel("严重污染");
        }
    }
    public Integer calLevelint(double AQI) {
        int level = 0;
        //等级标准是 1-6，但为了和后面的单项等级保持从0开始，所以变为 0-5
        if (AQI > 0 && AQI <= 50) {
            level= 0;
        } else if (AQI > 51 && AQI <= 100) {
            level= 1;
        } else if (AQI > 100 && AQI <= 150) {
            level= 2;
        } else if (AQI > 150 && AQI <= 200) {
            level= 3;
        } else if (AQI > 200 && AQI <= 300) {
            level= 4;
        } else if (AQI > 300) {
            level= 5;
        }
        return level;
    }
    public String calLevelString(double AQI) {
        String level = null;
        //等级标准是 1-6，但为了和后面的单项等级保持从0开始，所以变为 0-5
        if (AQI > 0 && AQI <= 50) {

            level = "优";
        } else if (AQI > 51 && AQI <= 100) {

            level = "良";
        } else if (AQI > 100 && AQI <= 150) {

            level = "轻度污染";
        } else if (AQI > 150 && AQI <= 200) {

            level = "中度污染";
        } else if (AQI > 200 && AQI <= 300) {

            level = "重度污染";
        } else if (AQI > 300) {

            level = "严重污染";
        }
        return level ;
    }
    /**
     * 用于计算日/小时数据AQI并获取首要污染物类型
     *
     * @param iaqipm10
     * @param iaqipm25
     * @param iaqiso2
     * @param iaqino2
     * @param iaqio3
     * @param iaqico
     */
    public void MaxIAQIAndPrimaryFactor(Double iaqipm10, Double iaqipm25, Double iaqiso2, Double iaqino2, Double iaqio3, Double iaqico) {
        Double[] arr = {iaqipm10, iaqipm25, iaqiso2, iaqino2, iaqio3, iaqico};
        //定义字符串型数组用来存放污染物名称
        String[] factorName = {"PM10", "PM2.5", "SO2", "NO2", "O3", "CO"};
        //定义变量首要污染物，用来当IAQI最大值重复时，将所对应污染物名称拼接起来
        String primaryfactor = "";
        //定义首要污染物数组
        ArrayList<String> primary = new ArrayList<String>();
        //max为数组中最大值，设其初始值为数组第一个元素
        double max = arr[0];
        //用来标记IAQI最大值是否存在相同
        boolean repeatflag = false;
        int maxsum = 1;
        //用来记录最大值索引
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            //将temp索引所对数组值与数组中其余值进行比较，若小于某一个值，则将该值对应的索引赋值给temp
            //即保证temp始终是数组中最大值所对应索引
            if (max < arr[i]) {
                max = arr[i];
                index = i;
                maxsum = 1;
                repeatflag = false;
            } else if (max == arr[i]) {
                maxsum++;
                repeatflag = true;
            }
        }
        //如果IAQI最大值有重复，则遍历arr，找到所有重复的污染物因子位置以及名称
        //其结果是一个String类型的数组，数组元素是IAQI最大值所对应的污染物因子名称
        if (repeatflag) {
            int temp = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == max) {
                    primary.add(factorName[i]);
                    temp++;
                }
            }
        } else {
            //如果IAQI最大值唯一，将唯一位置对应的污染物名称存入
            primary.add(factorName[index]);
        }
        //遍历污染物因子名称数组，将首要污染物拼接
        for (int i = 0; i < maxsum; i++) {
            primaryfactor += factorName[i];
        }
        //max即为数组中最大值--AQI
        setAQI(max);
        //设置首要污染物名称
        setPrimaryFactor(primary, maxsum);
    }
}
