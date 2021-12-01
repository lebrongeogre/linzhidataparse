package com.cuit.mete.jobs.aqi.db;


import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.aqi.domain.AqiCityData;

import java.util.Date;

public class AqiCityDayDao {
    private DruidDBOper dbOper = new DruidDBOper();

    public boolean isExist(String citycode, Date timepoint) {
        AqiCityData aqiCityData = dbOper.queryOneRow("SELECT code," +
                "       citycode," +
                "       cityname," +
                "       timepoint," +
                "       aqi," +
                "       primarypollutant," +
                "       index_mark," +
                "       co," +
                "       co_mark," +
                "       no2," +
                "       no2_mark," +
                "       so2," +
                "       so2_mark," +
                "       pm10," +
                "       pm10_mark," +
                "       pm2_5," +
                "       pm2_5_mark," +
                "       o3," +
                "       o3_mark," +
                "       state " +
                "FROM tb_da_aqicitydayrptdata where citycode = ? and timepoint=?", AqiCityData.class, citycode, timepoint);
        return (aqiCityData!=null);
    }

    public boolean saveData2DB(AqiCityData data) {
        return dbOper.Insert2DB(AqiCityData.class, data);
    }

}
