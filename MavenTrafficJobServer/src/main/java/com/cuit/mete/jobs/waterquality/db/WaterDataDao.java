package com.cuit.mete.jobs.waterquality.db;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.waterquality.domain.WaterData;

import java.util.Date;

public class WaterDataDao extends DruidDBOper {
	
	/**
	 * 查询水质实况记录是否存在
	 * @param station
	 * @param river
	 * @param dt_time
	 * @return
	 */
	public boolean existsWaterPreview(String stationid, Date publishtime){
		String sql = "select count(1) count from tb_da_scjc_preview where stationid = ? and publishtime = ?";
		Long count = queryOneRowByName(sql, Long.class, "count", stationid, publishtime);
		return count > 0;
	}
	
	/**
	 * 插入水质实况记录
	 * @param data
	 * @return
	 */
	public boolean insertWaterPreview(WaterData data){
		return Insert2DB(WaterData.class, data, "tb_da_scjc_preview");
	}
	
	/**
	 * 查询水质日报是否存在
	 * @param station
	 * @param river
	 * @param dt_time
	 * @return
	 */
	public boolean existsWaterDay(String stationid, Date dt_time){
		String sql = "select count(1) count from tb_da_scjc_day where stationid = ? and publishtime = ?";
		Long count = queryOneRowByName(sql, Long.class, "count", stationid, dt_time);
		return count > 0;
	}
	
	/**
	 * 插入水质日报
	 * @param data
	 * @return
	 */
	public boolean insertWaterDay(WaterData data){
		return Insert2DB(WaterData.class, data, "tb_da_scjc_day");
	}
	
}
