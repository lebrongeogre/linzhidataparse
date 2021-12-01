package com.cuit.mete.jobs.waterquality.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.JsonUtil;
import com.cuit.mete.jobs.waterquality.db.WaterDataDao;
import com.cuit.mete.jobs.waterquality.domain.WaterData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.util.*;

/**
 * 获取 “四川省生态环境厅”四川省生态环境监测总站 水质自动监测数据
 * http://119.6.113.148:9090/scszjcsj/szjc_sj/DataPublish/preview.xhtml
 */
@DisallowConcurrentExecution
public class GetScjcWaterDataProcess extends BaseJob {
	private static final Logger logger = Logger.getLogger(GetScjcWaterDataProcess.class);
	private Map<String, Object> config;

	private void insertWaterPreview(WaterData waterData){
		WaterDataDao dao = new WaterDataDao();
		//查询是否有记录
		boolean isExists = dao.existsWaterPreview(waterData.getStationid(), waterData.getPublishtime());
		if(!isExists){
			//插入记录
			boolean flag = dao.insertWaterPreview(waterData);
			if(flag){
				logger.debug(waterData.getStationname() + "水质实况数据插入成功");
			}else{
				logger.error(waterData.getStationname() + "水质实况数据插入失败");
			}
			
		}
	}
	
	private void insertWaterDay(WaterData waterData){
		WaterDataDao dao = new WaterDataDao();
		//查询是否有记录
		boolean isExists = dao.existsWaterDay(waterData.getStationid(), waterData.getPublishtime());
		if(!isExists){
			//插入记录
			boolean flag = dao.insertWaterDay(waterData);
			if(flag){
				logger.info(waterData.getStationname() + "水质日报数据插入成功");
			}else{
				logger.error(waterData.getStationname() + "水质日报数据插入失败");
			}
		}
	}

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");
		int rows = 0;
		//获取URL
		String url = (String) config.get("url");

		//获取类别
		String type = (String) config.get("type");
		if(StringUtils.isEmpty(type)){
			//默认为日报
			type = "day";
		}

		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try{
			//创建HttpClient
			client = HttpClients.createDefault();

			//创建GET请求
			HttpGet get = new HttpGet(url);
			String content = null;
			//先通过GET请求获取页面
			response = client.execute(get);

			//获取sessionID和cookie中的token
			String jssession = "";
			if(response.getStatusLine().getStatusCode() == 200){
				content = EntityUtils.toString(response.getEntity(),"UTF-8");
				Header[] cookieHead = response.getHeaders("Set-Cookie");
				for(Header header : cookieHead){
					String value = header.getValue();
					jssession += value.substring(0, value.indexOf(";")) + "; ";
				}
			}else{
				logger.error("发送GET请求出错：code=" + response.getStatusLine().getStatusCode());
				return;
			}

			//关闭response
			response.close();

			jssession = jssession.substring(0, jssession.lastIndexOf(";"));

			//获取页面中的form部分
			int start = content.indexOf("<form id=\"j_id_3\"");
			int end = content.indexOf("</form>", start);
			String formStr = content.substring(start, end);

			//获取form中的token
			start = formStr.indexOf("name=\"javax.faces.ViewState\"");
			end = formStr.indexOf("/>", start);
			String token = formStr.substring(start, end);
			start = token.indexOf("value=") + "value=".length() + 1;
			end = token.indexOf("\"", start);
			token = token.substring(start, end);


			//再次创建POST请求
			HttpPost httpPost = new HttpPost(url);
			//添加SessionID和token
			httpPost.setHeader("Cookie", jssession);
			//添加Header属性
			httpPost.setHeader("Faces-Request", "partial/ajax");
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
			httpPost.setHeader("Referer", url);
			//伪装浏览器请求
			httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");

			//添加参数
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("javax.faces.partial.ajax", "true"));
			parameters.add(new BasicNameValuePair("javax.faces.source", "j_id_3:j_id_5"));
			parameters.add(new BasicNameValuePair("javax.faces.partial.execute", "@all"));
			parameters.add(new BasicNameValuePair("j_id_3:j_id_5", "j_id_3:j_id_5"));
			parameters.add(new BasicNameValuePair("j_id_3_SUBMIT", "1"));
			parameters.add(new BasicNameValuePair("autoScroll", ""));
			parameters.add(new BasicNameValuePair("javax.faces.ViewState", token));

			//构造一个form表单式的实体
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

			//提交请求
			response = client.execute(httpPost);

			if(response.getStatusLine().getStatusCode() == 200){
				content = EntityUtils.toString(response.getEntity(),"UTF-8");
			}else{
				logger.error("发送POST请求出错：code=" + response.getStatusLine().getStatusCode());
				return;
			}

			//获取数据部分
			start = content.indexOf("<extension ln=\"primefaces\" type=\"args\">") + "<extension ln=\"primefaces\" type=\"args\">".length();
			end = content.indexOf("</extension>");
			String data = content.substring(start, end);
			//logger.debug("content=" + data);

			//解析数据
			JSONObject json = JsonUtil.getObjectFromJson(data, JSONObject.class);

			//获取日期
			String publisData = json.getString("publisData");
			Date publishtime = parseString2Date(publisData, "yyyy年MM月dd日HH时mm分ss秒");

			//获取列名
			Map<String, String> colMap = new HashMap<String, String>();
			JSONArray tabFactors = json.getJSONArray("tabFactors");
			for(int i = 0; i < tabFactors.size(); i++){
				colMap.put(tabFactors.getJSONObject(i).getString("FACTORALIAS"), tabFactors.getJSONObject(i).getString("COL"));
			}

			//解析数据
			JSONArray tbdata = json.getJSONArray("data");
			for(int i = 0; i < tbdata.size(); i++){
				WaterData waterData = new WaterData();

				//设置站点信息
				waterData.setStationid(tbdata.getJSONObject(i).getString("STATIONID"));
				waterData.setStationname(tbdata.getJSONObject(i).getString("STATIONNAME"));
				waterData.setBasinname(tbdata.getJSONObject(i).getString("BASINNAME"));
				waterData.setCity(tbdata.getJSONObject(i).getString("CITY"));
				waterData.setRivername(tbdata.getJSONObject(i).getString("RIVERNAME"));
				waterData.setLatitude(tbdata.getJSONObject(i).getString("LATITUDE"));
				waterData.setLongitude(tbdata.getJSONObject(i).getString("LONGITUDE"));

				//设置时间
				if("preview".equalsIgnoreCase(type)){
					//实时报才有采集日期字段
					if(tbdata.getJSONObject(i).containsKey("COLLECTINGTIME")
							&& StringUtils.isNotEmpty(tbdata.getJSONObject(i).getString("COLLECTINGTIME"))){
						waterData.setCollectingtime(new Date(tbdata.getJSONObject(i).getLong("COLLECTINGTIME")));
					}
				}
				if(tbdata.getJSONObject(i).containsKey("PUBLISH_HOUR_TIME")
						&& StringUtils.isNotEmpty(tbdata.getJSONObject(i).getString("PUBLISH_HOUR_TIME"))){
					waterData.setPublishtime(parseString2Date(tbdata.getJSONObject(i).getString("PUBLISH_HOUR_TIME")));
				}else{
					waterData.setPublishtime(publishtime);
				}

				//设置数据值
				if(tbdata.getJSONObject(i).containsKey(colMap.get("高锰酸盐指数")))
					waterData.setKmno(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("高锰酸盐指数"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("氨氮")))
					waterData.setNh(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("氨氮"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("总磷")))
					waterData.setP(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("总磷"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("浊度")))
					waterData.setTurbid(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("浊度"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("溶解氧")))
					waterData.setO(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("溶解氧"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("pH")))
					waterData.setPh(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("pH"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("生物毒性")))
					waterData.setBiotoxicity(tbdata.getJSONObject(i).getString(colMap.get("生物毒性")));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("水温")))
					waterData.setTemperature(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("水温"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("电导率")))
					waterData.setElectric(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("电导率"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("总氮")))
					waterData.setN(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("总氮"))));
				if(tbdata.getJSONObject(i).containsKey(colMap.get("叶绿素")))
					waterData.setChlorophyll(changeStr2Number(tbdata.getJSONObject(i).getString(colMap.get("叶绿素"))));

				//设置质量数据
				waterData.setChecklevel(tbdata.getJSONObject(i).getString("CHECKLEVER"));
				waterData.setTargetwater(tbdata.getJSONObject(i).getString("TARGETWATER"));
				waterData.setNoreachfacts(tbdata.getJSONObject(i).getString("NOREACHFACTS"));

				//插入记录
				if("day".equals(type)){
					insertWaterDay(waterData);
				}else{
					insertWaterPreview(waterData);
				}
				rows ++;
			}
		}catch(Exception e){
			logger.error("获取水质报数据出错：" + e.getMessage(), e);
		}finally {
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭respnose失败：" + e.getMessage(), e);
				}
			}
			if(client != null){
				try {
					client.close();
				} catch (IOException e) {
					logger.error("关闭client失败：" + e.getMessage(), e);
				}
			}
		}
		sendMonitorMsg("已从环境监测站提取水质监测数据" + rows + "条!");
	}
}
