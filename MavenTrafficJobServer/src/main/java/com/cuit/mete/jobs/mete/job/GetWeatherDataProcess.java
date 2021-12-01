package com.cuit.mete.jobs.mete.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.HttpUtil;
import com.cuit.job.utils.JsonUtil;
import com.cuit.mete.jobs.mete.db.WeatherDao;
import com.cuit.mete.jobs.mete.domain.WeatherAir;
import com.cuit.mete.jobs.mete.domain.WeatherPassedchart;
import com.cuit.mete.jobs.mete.domain.WeatherPredict;
import com.cuit.mete.jobs.mete.domain.WeatherWarn;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GetWeatherDataProcess extends BaseJob {
	private static final Logger logger = Logger.getLogger(GetWeatherDataProcess.class);
	private Map<String, Object> config;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		startJob(jobExecutionContext);
	}

	public void startJob(JobExecutionContext context) throws JobExecutionException {
		config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
		String url = (String) config.get("url");
		
		//获取站点列表
		List<String> stationList = (List<String>) config.get("stations");
		//获取时效
		List<String> agingList = (List<String>) config.get("agings");
		int[] agings = null;
		if(agingList != null && agingList.size() > 0){
			agings = new int[agingList.size()];
			for(int i = 0; i < agingList.size(); i++){
				agings[i] = Integer.parseInt(agingList.get(i)) / 24;
			}
		}
		if(stationList != null && stationList.size() > 0){
			for(String stationId : stationList){
				try{
					String reqUrl = url + "?stationid=" + stationId + "&_=" + System.currentTimeMillis();
					logger.info("从" + reqUrl + "获取信息");
					
					
					//获取数据
					String data = HttpUtil.getRequest(reqUrl, "UTF-8");
					if(data == null){
						logger.error(stationId + "获取数据失败");
					}else{
						logger.debug("数据：" + data);
						
						//解析数据
						JSONObject jsonData = JsonUtil.getObjectFromJson(data, JSONObject.class);
						if(jsonData == null){
							logger.error(stationId + "解析数据失败:" + data);
						}else{
							if("0".equals(jsonData.getString("code"))){
								
								WeatherDao dao = new WeatherDao();
								
								JSONObject dataObj = jsonData.getJSONObject("data");
								
								//获取预报数据
								JSONObject predictObj = dataObj.getJSONObject("predict");
								JSONArray preDetails = predictObj.getJSONArray("detail");
								String publish_time = predictObj.getString("publish_time");
								List<WeatherPredict> predictList = new ArrayList<WeatherPredict>();
								if(agings == null){
									//未配置时效时，取全部内容
									for(int i = 0; i < preDetails.size(); i++){
										List<WeatherPredict> list =parseJson2WeatherPredict(stationId, parseString2Date(publish_time), preDetails.getJSONObject(i));
										predictList.addAll(list);
									}
								}else{
									//按配置的时效提取
									for(int i = 0; i < agings.length; i++){
										List<WeatherPredict> list =parseJson2WeatherPredict(stationId, parseString2Date(publish_time), preDetails.getJSONObject(agings[i]));
										predictList.addAll(list);
									}
								}
								
								logger.info(stationId + "预报数据解析成功，共有" + predictList.size() + "条数据");
								//插入数据
								if(predictList.size() > 0){
									int count = dao.batchInsertPredict(predictList);
									logger.info(stationId + "预报数据，成功插入" + count + "条"); 
								}
								
								
								//获取实况数据
								JSONArray passedchartObj = dataObj.getJSONArray("passedchart");
								List<WeatherPassedchart> passedchartList = new ArrayList<WeatherPassedchart>();
								for(int i = 0; i < passedchartObj.size(); i++){
									WeatherPassedchart passedchart = new WeatherPassedchart(stationId,
											parseString2Date(passedchartObj.getJSONObject(i).getString("time")),
											passedchartObj.getJSONObject(i).getDouble("humidity"),
											passedchartObj.getJSONObject(i).getDouble("pressure"),
											passedchartObj.getJSONObject(i).getDouble("rain1h"),
											passedchartObj.getJSONObject(i).getDouble("rain6h"),
											passedchartObj.getJSONObject(i).getDouble("rain12h"),
											passedchartObj.getJSONObject(i).getDouble("rain24h"),
											passedchartObj.getJSONObject(i).getString("tempDiff"),
											passedchartObj.getJSONObject(i).getDouble("temperature"),
											passedchartObj.getJSONObject(i).getDouble("windDirection"),
											passedchartObj.getJSONObject(i).getDouble("windSpeed")
									);
									passedchartList.add(passedchart);
								}
								logger.info(stationId + "实况数据解析成功，共有" + passedchartList.size() + "条数据"); 
								if(passedchartList.size() > 0){
									int count = dao.batchInsertPassedchart(passedchartList);
									logger.info(stationId + "实况数据，成功插入" + count + "条"); 
								}
								
								
								//获取预警数据
								JSONObject realObj = dataObj.getJSONObject("real");
								publish_time = realObj.getString("publish_time");
								WeatherWarn warn = new WeatherWarn(stationId, parseString2Date(publish_time),
										realObj.getJSONObject("warn").getString("alert"), 
										realObj.getJSONObject("warn").getString("province"),
										realObj.getJSONObject("warn").getString("city"),
										realObj.getJSONObject("warn").getString("fmeans"),
										realObj.getJSONObject("warn").getString("issuecontent"),
										realObj.getJSONObject("warn").getString("pic"),
										realObj.getJSONObject("warn").getString("pic2"),
										realObj.getJSONObject("warn").getString("signallevel"),
										realObj.getJSONObject("warn").getString("signaltype"), 
										realObj.getJSONObject("warn").getString("url")
								);

								//全为空的不入库
								if (!(warn.getAlert().indexOf("9999") >= 0 && warn.getSignallevel().indexOf("9999") >=0 && warn.getIssuecontent().indexOf("9999") >= 0)) {
									//如果预警数据不存在插入预警数据
									if (!dao.existsWeatherWarn(warn.getStation(), warn.getPublish_time())) {
										boolean flag = dao.insertWeatherWarn(warn);
										if (flag) {
											logger.info(stationId + "预警数据插入成功");
										} else {
											logger.info(stationId + "预警数据插入失败");
										}
									}
								}

								//获取空气质量
								JSONObject airObj = dataObj.getJSONObject("air");
								String forecasttime = airObj.getString("forecasttime");
								if(StringUtils.isNotEmpty(forecasttime)){
									WeatherAir air = new WeatherAir(stationId,
										parseString2Date(airObj.getString("forecasttime")), 
										airObj.getInt("aq"),
										airObj.getInt("aqi"),
										airObj.getString("aqiCode"),
										airObj.getString("text")
									);
									
									//如果空气质量数据不存在插入空气质量数据
									if(!dao.existsWeatherAir(air.getStation(), air.getForecasttime())){
										boolean flag = dao.insertWeatherAir(air);
										if(flag){
											logger.info(stationId + "空气质量数据插入成功");
										}else{
											logger.error(stationId + "空气质量数据插入失败"); 
										}
									}
								}
							}else{
								logger.error(stationId + "获取数据失败：code=" + jsonData.get("code") + ",msg=" + jsonData.getDouble("msg"));
							}
						}
					}
					
				}catch(Exception e){
					logger.error("获取站点" + stationId + "数据失败：" + e.getMessage(), e);
				}
			}
		}else{
			logger.error("未找到配置的站点数据");
		}
		
	}
	
	private List<WeatherPredict> parseJson2WeatherPredict(String stationId, Date publish_time, JSONObject detailObj){
		List<WeatherPredict> predictList = new ArrayList<WeatherPredict>();
		
		Date pt = parseString2Date(detailObj.getString("pt"));
		Date date = parseString2Date(detailObj.getString("date"));
		JSONObject dayObj = detailObj.getJSONObject("day");
		JSONObject nightObj = detailObj.getJSONObject("night");
		
		WeatherPredict weatherPredictDay = new WeatherPredict(stationId, 
				publish_time,pt,date,"day", 
				dayObj.getJSONObject("weather").getString("info"),
				dayObj.getJSONObject("weather").getInt("img"),
				dayObj.getJSONObject("weather").getDouble("temperature"),
				dayObj.getJSONObject("wind").getString("direct"),
				dayObj.getJSONObject("wind").getString("power")
		);
		predictList.add(weatherPredictDay);
		
		WeatherPredict weatherPredictNight = new WeatherPredict(stationId, 
				publish_time,pt,date,"night", 
				nightObj.getJSONObject("weather").getString("info"),
				nightObj.getJSONObject("weather").getInt("img"),
				nightObj.getJSONObject("weather").getDouble("temperature"),
				nightObj.getJSONObject("wind").getString("direct"),
				nightObj.getJSONObject("wind").getString("power")
		);
		predictList.add(weatherPredictNight);
		
		return predictList;
	}
}
