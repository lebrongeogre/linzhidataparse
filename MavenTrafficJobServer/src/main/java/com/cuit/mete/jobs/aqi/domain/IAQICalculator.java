package com.cuit.mete.jobs.aqi.domain;

/**
 * 用于计算各类因子的日/小时IAQI值
 */
public class IAQICalculator {
    public final static double NODATA = -9999;
    public final static int NOLEVEL = -9;
    //分别定义六个指标的空气质量分指数
    private double IAQIPM10 = NODATA;
    private double IAQIPM25 = NODATA;
    private double IAQISO2 = NODATA;
    private double IAQINO2 = NODATA;
    private double IAQIO3 = NODATA;
    private double IAQICO = NODATA;
    //定义六个指标IAQI对应的等级  0-6 共七个等级
    private int LAQIPM10 = NOLEVEL;
    private int LAQIPM25 = NOLEVEL;
    private int LAQISO2 = NOLEVEL;
    private int LAQINO2 = NOLEVEL;
    private int LAQIO3 = NOLEVEL;
    private int LAQICO = NOLEVEL;


    public void setIAQISO2(double IAQISO2) {
        this.IAQISO2 = IAQISO2;
    }

    public void setIAQIPM10(double IAQIPM10) {
        this.IAQIPM10 = IAQIPM10;
    }

    public void setIAQIPM25(double IAQIPM25) {
        this.IAQIPM25 = IAQIPM25;
    }

    public void setIAQINO2(double IAQINO2) {
        this.IAQINO2 = IAQINO2;
    }

    public Double getIAQIPM10() {
        return IAQIPM10;
    }

    public double getIAQIPM25() {
        return IAQIPM25;
    }

    public double getIAQISO2() {
        return IAQISO2;
    }

    public double getIAQINO2() {
        return IAQINO2;
    }

    public double getIAQIO3() {
        return IAQIO3;
    }

    public double getIAQICO() {
        return IAQICO;
    }

    public void setIAQIO3(double IAQIO3) {
        this.IAQIO3 = IAQIO3;
    }

    public void setIAQICO(double IAQICO) {
        this.IAQICO = IAQICO;
    }

    public int getLAQIPM10() {
        return LAQIPM10;
    }

    public void setLAQIPM10(int LAQIPM10) {
        this.LAQIPM10 = LAQIPM10;
    }

    public int getLAQIPM25() {
        return LAQIPM25;
    }

    public void setLAQIPM25(int LAQIPM25) {
        this.LAQIPM25 = LAQIPM25;
    }

    public int getLAQISO2() {
        return LAQISO2;
    }

    public void setLAQISO2(int LAQISO2) {
        this.LAQISO2 = LAQISO2;
    }

    public int getLAQINO2() {
        return LAQINO2;
    }

    public void setLAQINO2(int LAQINO2) {
        this.LAQINO2 = LAQINO2;
    }

    public int getLAQIO3() {
        return LAQIO3;
    }

    public void setLAQIO3(int LAQIO3) {
        this.LAQIO3 = LAQIO3;
    }

    public int getLAQICO() {
        return LAQICO;
    }

    public void setLAQICO(int LAQICO) {
        this.LAQICO = LAQICO;
    }

    /**
     * 用于计算各指标的日IAQI
     *
     * @param pm10
     * @param pm25
     * @param SO2
     * @param NO2
     * @param O3
     * @param CO
     */
    public void calDayIAQI(double pm10, double pm25, double SO2, double NO2, double O3, double CO) {
        if (pm10 != NODATA) {
            IAQI_PM10DayCal(pm10);
        }
        if (pm25 != NODATA) {
            IAQI_PM25DayCal(pm25);
        }
        if (SO2 != NODATA) {
            IAQI_SO2DayCal(SO2);
        }
        if (NO2 != NODATA) {
            IAQI_NO2DayCal(NO2);
        }
        if (O3 != NODATA) {
            IAQI_O3DayCal(O3);
        }
        if (CO != NODATA) {
            IAQI_CODayCal(CO);
        }
    }



    /**
     * 计算CO日空气质量分指数
     *
     * @param co
     */
    private void IAQI_CODayCal(double co) {
        if (co > 0 && co <= 2) {
            setIAQICO(Math.ceil((double) 50 / 2 * (co)));
            setLAQICO(0);
        } else if ((double) co > 2 && co <= 4) {
            setIAQICO(Math.ceil((double) 50 / 2 * (co - 2) + 50));
            setLAQICO(1);
        } else if (co > 4 && co <= 14) {
            setIAQICO(Math.ceil((double) 50 / 10 * (co - 4) + 100));
            setLAQICO(2);
        } else if (co > 14 && co <= 24) {
            setIAQICO(Math.ceil((double) 50 / 10 * (co - 14) + 150));
            setLAQICO(3);
        } else if (co > 24 && co <= 36) {
            setIAQICO(Math.ceil((double) 100 / 12 * (co - 24) + 200));
            setLAQICO(4);
        } else if (co > 36 && co <= 48) {
            setIAQICO(Math.ceil((double) 100 / 12 * (co - 36) + 300));
            setLAQICO(5);
        } else if (co > 48 && co <= 60) {
            setIAQICO(Math.ceil((double) 100 / 12 * (co - 48) + 400));
            setLAQICO(6);
        }
    }

    /**
     * 计算臭氧日空气质量分指数
     *
     * @param o3
     */
    private void IAQI_O3DayCal(double o3) {
        if (o3 > 0 && o3 <= 100) {
            setIAQIO3(Math.ceil((double) 50 / 100 * (o3)));
            setLAQIO3(0);
        } else if (o3 > 100 && o3 <= 160) {
            setIAQIO3(Math.ceil((double) 50 / 60 * (o3 - 100) + 50));
            setLAQIO3(1);
        } else if (o3 > 160 && o3 <= 215) {
            setIAQIO3(Math.ceil((double) 50 / 55 * (o3 - 160) + 100));
            setLAQIO3(2);
        } else if (o3 > 215 && o3 <= 265) {
            setIAQIO3(Math.ceil((double) 50 / 150 * (o3 - 215) + 150));
            setLAQIO3(3);
        } else if (o3 > 265 && o3 <= 800) {
            setIAQIO3(Math.ceil((double) 100 / 535 * (o3 - 265) + 200));
            setLAQIO3(4);
        } else if (o3 > 1600 && o3 <= 2100) {
            setIAQIO3(Math.ceil((double) 100 / 12 * (o3 - 800) + 300));
            setLAQIO3(5);
        } else if (o3 > 2100 && o3 <= 2620) {
            setIAQIO3(Math.ceil((double) 100 / 12 * (o3 - 48) + 400));
            setLAQIO3(6);
        }
    }


    /**
     * 计算二氧化氮日空气质量分指数
     *
     * @param no2
     */
    private void IAQI_NO2DayCal(double no2) {
        if (no2 > 0 && no2 <= 40) {
            setIAQINO2(Math.ceil((double) 50 / 40 * (no2)));
            setLAQINO2(0);
        } else if (no2 > 40 && no2 <= 80) {
            setIAQINO2(Math.ceil((double) 50 / 40 * (no2 - 40) + 50));
            setLAQINO2(1);
        } else if (no2 > 80 && no2 <= 180) {
            setIAQINO2(Math.ceil((double) 50 / 100 * (no2 - 80) + 100));
            setLAQINO2(2);
        } else if (no2 > 180 && no2 <= 280) {
            setIAQINO2(Math.ceil((double) 50 / 100 * (no2 - 180) + 150));
            setLAQINO2(3);
        } else if (no2 > 280 && no2 <= 565) {
            setIAQINO2(Math.ceil((double) 100 / 285 * (no2 - 280) + 200));
            setLAQINO2(4);
        } else if (no2 > 565 && no2 <= 750) {
            setIAQINO2(Math.ceil((double) 100 / 185 * (no2 - 565) + 300));
            setLAQINO2(5);
        } else if (no2 > 750 && no2 <= 940) {
            setIAQINO2(Math.ceil((double) 100 / 190 * (no2 - 750) + 400));
            setLAQINO2(6);
        }
    }

    /**
     * 计算二氧化硫日空气质量分指数
     *
     * @param so2
     */
    private void IAQI_SO2DayCal(double so2) {
        if (so2 > 0 && so2 <= 50) {
            setIAQISO2(Math.ceil((double) 50 / 50 * (so2)));
            setLAQISO2(0);
        } else if (so2 > 50 && so2 <= 150) {
            setIAQISO2(Math.ceil((double) 50 / 100 * (so2 - 50) + 50));
            setLAQISO2(1);
        } else if (so2 > 150 && so2 <= 475) {
            setIAQISO2(Math.ceil((double) 50 / 325 * (so2 - 150) + 100));
            setLAQISO2(2);
        } else if (so2 > 475 && so2 <= 800) {
            setIAQISO2(Math.ceil((double) 50 / 325 * (so2 - 475) + 150));
            setLAQISO2(3);
        } else if (so2 > 800 && so2 <= 1600) {
            setIAQISO2(Math.ceil((double) 100 / 800 * (so2 - 800) + 200));
            setLAQISO2(4);
        } else if (so2 > 1600 && so2 <= 2100) {
            setIAQISO2(Math.ceil((double) 100 / 500 * (so2 - 1600) + 300));
            setLAQISO2(5);
        } else if (so2 > 2100 && so2 <= 2620) {
            setIAQISO2(Math.ceil((double) 100 / 520 * (so2 - 2100) + 400));
            setLAQISO2(6);
        }
    }

    /**
     * 计算PM2.5日空气质量分指数
     *
     * @param pm25
     */
    private void IAQI_PM25DayCal(double pm25) {
        calIAQIPM25(pm25);
    }

    /**
     * 计算PM10日空气质量分指数
     *
     * @param pm10 24小时PM10平均浓度值
     */
    private void IAQI_PM10DayCal(double pm10) {
        calIAQIPM10(pm10);
    }


    /**
     * 计算各指标逐时空气质量分指数
     *
     * @param pm10
     * @param pm25
     * @param SO2
     * @param NO2
     * @param O3
     * @param CO
     */
    public void calHourIAQI(double pm10, double pm25, double SO2, double NO2, double O3, double CO) {

        if (pm10 != NODATA) {
            IAQI_PM10HourCal(pm10);
        }
        if (pm25 != NODATA) {
            IAQI_PM25HourCal(pm25);
        }
        if (SO2 != NODATA){
            IAQI_SO2HourCal(SO2);
        }
        if (NO2 != NODATA) {
            IAQI_NO2HourCal(NO2);
        }
        if (O3 != NODATA) {
            IAQI_O3HourCal(O3);
        }
        if (CO != NODATA) {
            IAQI_COHourCal(CO);
        }
    }

    /**
     * 计算PM10小时空气质量分指数
     *
     * @param pm10
     */
    private void IAQI_PM10HourCal(double pm10) {
        calIAQIPM10(pm10);
    }

    /**
     * PM10的日报、小时AQI算法一样，故抽取出一个方法
     *
     * @param pm10
     */
    private void calIAQIPM10(double pm10) {
        if (pm10 > 0 && pm10 <= 50) {
            setIAQIPM10(Math.ceil((double) 50 / 50 * (pm10)));
            setLAQIPM10(0);
        } else if (pm10 > 50 && pm10 <= 150) {
            setIAQIPM10(Math.ceil((double) 50 / 100 * (pm10 - 50) + 50));
            setLAQIPM10(1);
        } else if (pm10 > 150 && pm10 <= 250) {
            setIAQIPM10(Math.ceil((double) 50 / 100 * (pm10 - 150) + 100));
            setLAQIPM10(2);
        } else if (pm10 > 250 && pm10 <= 350) {
            setIAQIPM10(Math.ceil((double) 50 / 100 * (pm10 - 250) + 150));
            setLAQIPM10(3);
        } else if (pm10 > 350 && pm10 <= 420) {
            setIAQIPM10(Math.ceil((double) 100 / 70 * (pm10 - 350) + 200));
            setLAQIPM10(4);
        } else if (pm10 > 420 && pm10 <= 500) {
            setIAQIPM10(Math.ceil((double) 100 / 80 * (pm10 - 420) + 300));
            setLAQIPM10(5);
        } else if (pm10 > 500 && pm10 <= 600) {
            setIAQIPM10(Math.ceil((double) 100 / 100 * (pm10 - 500) + 400));
            setLAQIPM10(6);
        }
    }

    /**
     * 计算PM2.5小时空气质量分指数
     *
     * @param pm25
     */
    private void IAQI_PM25HourCal(double pm25) {
        calIAQIPM25(pm25);
    }

    /**
     * PM2.5的日报、小时AQI算法一样，故抽取出一个方法
     */
    private void calIAQIPM25(double pm25) {
        if (pm25 > 0 && pm25 <= 35) {
            setIAQIPM25(Math.ceil((double) 50 / 35 * (pm25)));
            setLAQIPM25(0);
        } else if (pm25 > 35 && pm25 <= 75) {
            setIAQIPM25(Math.ceil((double) 50 / 40 * (pm25 - 35) + 50));
            setLAQIPM25(1);
        } else if (pm25 > 75 && pm25 <= 115) {
            setIAQIPM25(Math.ceil((double) 50 / 40 * (pm25 - 75) + 100));
            setLAQIPM25(2);
        } else if (pm25 > 115 && pm25 <= 150) {
            setIAQIPM25(Math.ceil((double) 50 / 35 * (pm25 - 115) + 150));
            setLAQIPM25(3);
        } else if (pm25 > 150 && pm25 <= 250) {
            setIAQIPM25(Math.ceil((double) 100 / 100 * (pm25 - 150) + 200));
            setLAQIPM25(4);
        } else if (pm25 > 250 && pm25 <= 350) {
            setIAQIPM25(Math.ceil((double) 100 / 100 * (pm25 - 250) + 300));
            setLAQIPM25(5);
        } else if (pm25 > 350 && pm25 <= 500) {
            setIAQIPM25(Math.ceil((double) 100 / 150 * (pm25 - 350) + 400));
            setLAQIPM25(6);
        }
    }

    /**
     * 计算二氧化硫小时空气质量分指数
     *
     * @param so2
     */
    private void IAQI_SO2HourCal(double so2) {
        if (so2 > 0 && so2 <= 150) {
            setIAQISO2(Math.ceil((double) 50 / 150 * (so2)));
            setLAQISO2(0);
        } else if (so2 > 150 && so2 <= 500) {
            setIAQISO2(Math.ceil((double) 50 / 350 * (so2 - 150) + 50));
            setLAQISO2(1);
        } else if (so2 > 500 && so2 <= 650) {
            setIAQISO2(Math.ceil((double) 50 / 150 * (so2 - 500) + 100));
            setLAQISO2(2);
        } else if (so2 > 650 && so2 <= 800) {
            setIAQISO2(Math.ceil((double) 50 / 150 * (so2 - 650) + 150));
            setLAQISO2(3);
        }
    }

    /**
     * 计算二氧化氮小时空气质量分指数
     *
     * @param no2
     */
    private void IAQI_NO2HourCal(double no2) {
        if (no2 > 0 && no2 <= 100) {
            setIAQINO2(Math.ceil((double) 50 / 100 * (no2)));
            setLAQINO2(0);
        } else if (no2 > 100 && no2 <= 200) {
            setIAQINO2(Math.ceil((double) 50 / 100 * (no2 - 100) + 50));
            setLAQINO2(1);
        } else if (no2 > 200 && no2 <= 700) {
            setIAQINO2(Math.ceil((double) 50 / 500 * (no2 - 200) + 100));
            setLAQINO2(2);
        } else if (no2 > 700 && no2 <= 1200) {
            setIAQINO2(Math.ceil((double) 50 / 500 * (no2 - 700) + 150));
            setLAQINO2(3);
        } else if (no2 > 1200 && no2 <= 2340) {
            setIAQINO2(Math.ceil((double) 100 / 285 * (no2 - 1200) + 200));
            setLAQINO2(4);
        } else if (no2 > 2340 && no2 <= 3090) {
            setIAQINO2(Math.ceil((double) 100 / 185 * (no2 - 2340) + 300));
            setLAQINO2(5);
        } else if (no2 > 3090 && no2 <= 3840) {
            setIAQINO2(Math.ceil((double) 100 / 190 * (no2 - 3090) + 400));
            setLAQINO2(6);
        }
    }

    /**
     * 计算臭氧小时空气质量分指数
     *
     * @param o3
     */
    private void IAQI_O3HourCal(double o3) {
        if (o3 > 0 && o3 <= 160) {
            setIAQIO3(Math.ceil((double) 50 / 160 * (o3)));
            setLAQIO3(0);
        } else if (o3 > 160 && o3 <= 200) {
            setIAQIO3(Math.ceil((double) 50 / 40 * (o3 - 160) + 50));
            setLAQIO3(1);
        } else if (o3 > 200 && o3 <= 300) {
            setIAQIO3(Math.ceil((double) 50 / 100 * (o3 - 200) + 100));
            setLAQIO3(2);
        } else if (o3 > 300 && o3 <= 400) {
            setIAQIO3(Math.ceil((double) 50 / 100 * (o3 - 300) + 150));
            setLAQIO3(3);
        } else if (o3 > 400 && o3 <= 800) {
            setIAQIO3(Math.ceil((double) 100 / 400 * (o3 - 400) + 200));
            setLAQIO3(4);
        } else if (o3 > 800 && o3 <= 1000) {
            setIAQIO3(Math.ceil((double) 100 / 200 * (o3 - 800) + 300));
            setLAQIO3(5);
        } else if (o3 > 1000 && o3 <= 1200) {
            setIAQIO3(Math.ceil((double) 100 / 200 * (o3 - 1000) + 400));
            setLAQIO3(6);
        }
    }

    /**
     * 计算一氧化碳小时空气质量分指数
     *
     * @param co
     */
    private void IAQI_COHourCal(double co) {
        if (co > 0 && co <= 5) {
            setIAQICO(Math.ceil((double) 50 / 5 * (co)));
            setLAQICO(0);
        } else if (co > 5 && co <= 10) {
            setIAQICO(Math.ceil((double) 50 / 5 * (co - 5) + 50));
            setLAQICO(1);
        } else if (co > 10 && co <= 35) {
            setIAQICO(Math.ceil((double) 50 / 25 * (co - 10) + 100));
            setLAQICO(2);
        } else if (co > 35 && co <= 60) {
            setIAQICO(Math.ceil((double) 50 / 25 * (co - 35) + 150));
            setLAQICO(3);
        } else if (co > 60 && co <= 90) {
            setIAQICO(Math.ceil((double) 100 / 30 * (co - 60) + 200));
            setLAQICO(4);
        } else if (co > 90 && co <= 120) {
            setIAQICO(Math.ceil((double) 100 / 30 * (co - 90) + 300));
            setLAQICO(5);
        } else if (co > 120 && co <= 150) {
            setIAQICO(Math.ceil((double) 100 / 30 * (co - 120) + 400));
            setLAQICO(6);
        }
    }
}
