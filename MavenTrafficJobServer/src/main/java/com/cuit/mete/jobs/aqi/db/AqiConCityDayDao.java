package com.cuit.mete.jobs.aqi.db;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.DButils.Sequences;
import com.cuit.mete.jobs.aqi.domain.AqiDayData;

import java.util.Date;

public class AqiConCityDayDao {
    private DruidDBOper dbOper = new DruidDBOper();

    public boolean isExist(String citycode, Date timepoint) {

        AqiDayData aqiDayData = dbOper.queryOneRow("SELECT citycode, staname, datatime, level, levelint, Contaminants, PM25, PM10, SO2, NO2, CO, O3, AQI, IPM25, IPM10, ISO2, INO2, ICO, IO3, etlid  \n" +
                " FROM  tb_da_aqiday  where citycode = ? and datatime=?", AqiDayData.class, citycode, timepoint);
        return (aqiDayData!=null);
    }

    public boolean saveData2DB(AqiDayData data) {
        return dbOper.Insert2DB(AqiDayData.class, data);
    }


    public Long getDBjsonidListID() {
        Sequences sequences = new Sequences("datajsonid");
        Long id = sequences.nextVal();
        return id;
    }
}
