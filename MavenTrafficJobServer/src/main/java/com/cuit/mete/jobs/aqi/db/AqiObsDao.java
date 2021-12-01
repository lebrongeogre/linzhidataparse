package com.cuit.mete.jobs.aqi.db;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.aqi.domain.AqiObsData;

import java.util.Date;

public class AqiObsDao {
    private DruidDBOper dbOper = new DruidDBOper();

    public boolean isExist(String citycode, String stacode, Date timepoint) {
        AqiObsData aqiObsData = dbOper.queryOneRow("SELECT countyname," +
                "       countyid," +
                "       stationcode," +
                "       stationname," +
                "       stationtypeid," +
                "       lat," +
                "       lng," +
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
                "       ico," +
                "       ino2," +
                "       iso2," +
                "       ipm10," +
                "       ipm2_5," +
                "       io3," +
                "       state " +
                " FROM tb_da_aqiobsrealtimedata where countyid = ? and stationcode=? and timepoint=?", AqiObsData.class, citycode, stacode, timepoint);
        return (aqiObsData != null);
    }

    public boolean saveData2DB(AqiObsData data) {
        return dbOper.Insert2DB(AqiObsData.class, data);
    }
}
