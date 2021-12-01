package com.cuit.mete.jobs.mete.db;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.mete.domain.WeatherAir;
import com.cuit.mete.jobs.mete.domain.WeatherPassedchart;
import com.cuit.mete.jobs.mete.domain.WeatherPredict;
import com.cuit.mete.jobs.mete.domain.WeatherWarn;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class WeatherDao extends DruidDBOper {
	
	/**
	 * 批量插入预报数据
	 * @param list
	 * @return
	 */
	public int batchInsertPredict(List<WeatherPredict> list){
		Iterator<WeatherPredict> it = list.iterator();
		String sql = "select count(1) count from tb_da_weather_predict where station = ? and publish_time = ? and pt = ? and pre_date = ?";
		while(it.hasNext()){
			//判断记录是否存在
			WeatherPredict predict = it.next();
			Long count = queryOneRowByName(sql, Long.class, "count", predict.getStation(), predict.getPublish_time(), predict.getPt(), predict.getPre_date());
			if(count > 0){
				it.remove();
			}
		}
		//批量插入
		InsertList2DB(WeatherPredict.class, list);
		
		return list.size();
	}
	
	/**
	 * 批量插入实况数据
	 * @param list
	 * @return
	 */
	public int batchInsertPassedchart(List<WeatherPassedchart> list){
		Iterator<WeatherPassedchart> it = list.iterator();
		List<WeatherPassedchart> saveDBList = new ArrayList<WeatherPassedchart>();
		String sql = "select count(1) count from tb_da_weather_passedchart where station = ? and dt_time = ?";
		while(it.hasNext()){
			//判断记录是否存在
			WeatherPassedchart parsschart = it.next();
			Long count = queryOneRowByName(sql, Long.class, "count", parsschart.getStation(), parsschart.getDt_time());
			if(count <= 0){
				saveDBList.add(parsschart);
			}
		}
		//批量插入
		InsertList2DB(WeatherPassedchart.class, saveDBList);
		
		return saveDBList.size();
	}
	
	/**
	 * 判断预警数据是否存在
	 * @param station
	 * @param publish_time
	 * @return
	 */
	public boolean existsWeatherWarn(String station, Date publish_time){
		String sql = "select count(1) count from tb_da_weacher_warn where station = ? and publish_time = ?";
		Long count = queryOneRowByName(sql, Long.class, "count", station, publish_time);
		return count > 0;
	}
	
	/**
	 * 插入预警数据
	 * @param warn
	 * @return
	 */
	public boolean insertWeatherWarn(WeatherWarn warn){
		return Insert2DB(WeatherWarn.class, warn);
	}
	
	/**
	 * 空气质量数据是否存在
	 * @param station
	 * @param forecasttime
	 * @return
	 */
	public boolean existsWeatherAir(String station, Date forecasttime){
		String sql = "select count(1) count from tb_da_weather_air where station = ? and forecasttime = ?";
		Long count = queryOneRowByName(sql, Long.class, "count", station, forecasttime);
		return count > 0;
	}
	
	/**
	 * 插入空气质量数据
	 * @param air
	 * @return
	 */
	public boolean insertWeatherAir(WeatherAir air){
		return Insert2DB(WeatherAir.class, air);
	}
}
