package com.cuit.mete.jobs.aqi.db;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.aqi.domain.AqiCityObsData;

import java.util.Date;

public class AqiCityObsDao {
    private DruidDBOper dbOper = new DruidDBOper();

    public boolean isExist(String citycode, Date timepoint) {
        AqiCityObsData aqiCityData = dbOper.queryOneRow("SELECT code," +
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
                "FROM tb_da_aqicityrealtimedata where citycode = ? and timepoint=?", AqiCityObsData.class, citycode, timepoint);
        return (aqiCityData!=null);
    }

    public boolean saveData2DB(AqiCityObsData data) {
        return dbOper.Insert2DB(AqiCityObsData.class, data);
    }

}
