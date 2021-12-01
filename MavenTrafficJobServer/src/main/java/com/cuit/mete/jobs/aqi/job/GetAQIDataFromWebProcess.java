package com.cuit.mete.jobs.aqi.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.ConstantUtil;
import com.cuit.job.utils.HttpUtil;
import com.cuit.job.utils.JsonUtil;
import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.aqi.db.AqiCityDayDao;
import com.cuit.mete.jobs.aqi.db.AqiCityObsDao;
import com.cuit.mete.jobs.aqi.db.AqiObsDao;
import com.cuit.mete.jobs.aqi.domain.AqiCityData;
import com.cuit.mete.jobs.aqi.domain.AqiCityObsData;
import com.cuit.mete.jobs.aqi.domain.AqiObsData;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * http://www.scnewair.cn:6112/publish/index.html 空气质量发布系统中取数据，并保存到数据库中
 * 先取从（http://www.scnewair.cn:6112/publish/getAllCityDayAQIC）提取需城市质量日报
 * http://www.scnewair.cn:6112/publish/getAllCity24HRealTimeAQIC城市实时监测
 * 再从每个城市的监测点数据
 * http://www.scnewair.cn:6112/publish/getAllStation24HRealTimeAQIC  （5111为城市编号）
 * SO2、NO2、PM10、PM2.5、O3单位为ug/m3，CO单位为mg/m3
 */
public class GetAQIDataFromWebProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(GetCitysAQIDataFromWebProcess.class);
    private Map<String, Object> config;
    private DruidDBOper oper = new DruidDBOper();

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (jobExecutionContext == null) {
            //为了测试方便增加
            config = (Map<String, Object>) ConstantUtil.get("GetAQIDataFromWebProcess");
        } else {
            //这是从上下文件获取从jobService中传过来的配置
            config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");
        }

        if (config != null) {
            logger.debug("开始从环境监测网站提取AQI数据......");

            String url = (String) config.get("citydayrpturl");
            String statonobsurl = (String) config.get("statonobsurl");
            String cityobsurl = (String) config.get("cityobsurl");

            String repeat = (String) config.get("repeat");
            String sleep = (String) config.get("sleep");
            int repeatInt = 1;
            long sleepLong = 10000L;
            if (!StringUtils.isEmpty(repeat)) {
                try {
                    repeatInt = Integer.parseInt(repeat);
                } catch (NumberFormatException e) {
                    repeatInt = 1;
                }
            }
            if (!StringUtils.isEmpty(sleep)) {
                try {
                    sleepLong = Long.parseLong(sleep);
                } catch (NumberFormatException e) {
                    sleepLong = 10000L;
                }
            }
            if (!(StringUtils.isEmpty(url) || StringUtils.isEmpty(statonobsurl) || StringUtils.isEmpty(cityobsurl))) {
                readAQIDataFromWeb(url, cityobsurl, statonobsurl, repeatInt, sleepLong);
            } else {
                logger.error("环境监测网站提取AQI数据URL地址配置不正确！");
            }
        } else {
            logger.error("环境监测网站提取AQI数据配置不正确！");
        }
    }

    /**
     * 读取站点AQI
     *
     * @throws Exception
     */
    public void readAQIDataFromWeb(String cityrpturl, String cityobsurl, String stationobsurl, int repeatInt, long sleepLong) {
        int rows = 0;
        //取城市实况
        List<AqiCityObsData>  aqiCityobsDataList = readCityObsAqiDataAndSaveDB(cityobsurl, repeatInt, sleepLong);
        rows += aqiCityobsDataList.size();

        //取城市日报
        List<AqiCityData> aqiCityDataList = readCityAqiDataAndSaveDB(cityrpturl, repeatInt, sleepLong);
        rows += aqiCityDataList.size();

        //取站点实况
        if (aqiCityDataList!=null) {
            for (int i = 0; i < aqiCityDataList.size(); i++) {
                AqiCityData aqiCityData = aqiCityDataList.get(i);
                String weburl = stationobsurl + "?cityCode=" + aqiCityData.getCitycode();
                int row = readObsAqiDataAndSaveDB(weburl, repeatInt, sleepLong, aqiCityData.getCityname());
                rows += row;
            }
        }
        sendMonitorMsg("从环境监测站网站提取到数据：" + rows + "条");
    }

    private int readObsAqiDataAndSaveDB(String url, int repeatInt, long sleepLong, String cityName) {
        int saverow = 0;
        List<AqiObsData> aqiObsDataList = null;
        for (int i = 0; i < repeatInt; i++) {
            try {
                //从网页取数据
                aqiObsDataList = readObsDataFromWeb(url);
                if (aqiObsDataList != null) {
                    //保存到数据库
                    AqiObsDao aqiObsDao = new AqiObsDao();

                    for (int j = 0; j < aqiObsDataList.size(); j++) {
                        AqiObsData aqiData = aqiObsDataList.get(j);
                       if (!aqiObsDao.isExist(aqiData.getCountyid(),aqiData.getStationcode(), aqiData.getTimepoint())){
                            //新的，则保存
                            if (aqiObsDao.saveData2DB(aqiData)) {
                                saverow++;
                            }
                        }
                    }
                    logger.info("从环境监测网站提取AQI数据，保存了" + saverow + "条" + cityName + "空气质量数据！");
                    break;
                }
                Thread.sleep(sleepLong);
            } catch (InterruptedException e) {
            }
        }
        return saverow;
    }

    private List<AqiCityData> readCityAqiDataAndSaveDB(String url, int repeatInt, long sleepLong) {
        List<AqiCityData> aqiCityDataList = null;
        for (int i = 0; i < repeatInt; i++) {
            try {
                //从网页取数据
                aqiCityDataList = readCityDayRptDataFromWeb(url);

                if (aqiCityDataList != null) {
                    //保存到数据库
                    AqiCityDayDao aqiCityDao = new AqiCityDayDao();
                    int saverow = 0;
                    for (int j = 0; j < aqiCityDataList.size(); j++) {
                        AqiCityData aqiCityData = aqiCityDataList.get(j);
                        if (!aqiCityDao.isExist(aqiCityData.getCitycode(), aqiCityData.getTimepoint())) {
                            //新的，则保存
                            if (aqiCityDao.saveData2DB(aqiCityData)) {
                                saverow++;
                            }
                        }
                    }
                    logger.info("从环境监测网站提取AQI数据，保存了" + saverow + "条城市空气质量数据！");
                    break;
                }
                Thread.sleep(sleepLong);
            } catch (InterruptedException e) {
            }
        }
        return aqiCityDataList;
    }

    private List<AqiCityObsData> readCityObsAqiDataAndSaveDB(String url, int repeatInt, long sleepLong) {
        List<AqiCityObsData> aqiCityDataList = null;
        for (int i = 0; i < repeatInt; i++) {
            try {
                //从网页取数据
                aqiCityDataList = readCityObsDataFromWeb(url);

                if (aqiCityDataList != null) {
                    //保存到数据库
                    AqiCityObsDao aqiCityDao = new AqiCityObsDao();
                    int saverow = 0;
                    for (int j = 0; j < aqiCityDataList.size(); j++) {
                        AqiCityObsData aqiCityData = aqiCityDataList.get(j);
                        if (!aqiCityDao.isExist(aqiCityData.getCitycode(), aqiCityData.getTimepoint())) {
                            //新的，则保存
                            if (aqiCityDao.saveData2DB(aqiCityData)) {
                                saverow++;
                            }
                        }
                    }
                    logger.info("从环境监测网站提取AQI数据，保存了" + saverow + "条城市空气质量数据！");
                    break;
                }
                Thread.sleep(sleepLong);
            } catch (InterruptedException e) {
            }
        }
        return aqiCityDataList;
    }

    /**
     * 城市日报
     * @param url
     * @return
     */
    public List<AqiCityObsData> readCityObsDataFromWeb(String url) {
        List<AqiCityObsData> retList = null;
        try {
            String jsonStr = HttpUtil.getRequest(url, "UTF-8");
            if (jsonStr != null) {
                jsonStr = jsonStr.toLowerCase();     //全变为小写，为了转对象

                List<Map<String, Object>> listMapFromJson = null;
                try {
                    listMapFromJson = JsonUtil.getListFromJson(jsonStr);
                } catch (Exception e) {
                    logger.debug("地址：" + url + "取的结果数据转换时出错" + e.getMessage());
                }

                if (listMapFromJson != null && listMapFromJson.size() > 0) {
                    logger.debug("从地址：" + url + "取城市实测数据，取到" + listMapFromJson.size() + "小时数据！");
                    retList = new ArrayList<AqiCityObsData>();
                    for (int i = 0; i < listMapFromJson.size(); i++) {
                        Map<String, Object> mapData = listMapFromJson.get(i);
                        Long timepoint = (Long) mapData.get("timepoint");
                        List<Map<String, Object>> listMapData = (List<Map<String, Object>>) mapData.get("data");
                        if (listMapData != null && listMapData.size() > 0) {
                            Date datatime = new Date(timepoint);
                            for (int j = 0; j < listMapData.size(); j++) {
                                Map<String, Object> staMap = listMapData.get(j);
                                Map<String, Object> mapcolumns = (Map<String, Object>) staMap.get("columns");
                                AqiCityObsData aqiCityData = null;
                                try {
                                    aqiCityData = new AqiCityObsData();
                                    BeanUtils.populate(aqiCityData, mapcolumns);
                                    //部分数据的是间是空的！
                                    if (aqiCityData != null && aqiCityData.getTimepoint() == null) {
                                        aqiCityData.setTimepoint(datatime);
                                    }
                                } catch (IllegalAccessException e) {
                                } catch (InvocationTargetException e) {
                                }
                                if (aqiCityData != null) {
                                    //为空的数据不保存
                                    if (aqiCityData.getAqi() != null && aqiCityData.getPrimarypollutant() != null) {
                                        retList.add(aqiCityData);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.debug("从地址：" + url + "取数据不正确！");
                }
            }
        } catch (Exception e) {
            logger.debug("从地址：" + url + "取数据时出错" + e.getMessage());
        }
        return retList;
    }

    /**
     * 城市日报
     * @param url
     * @return
     */
    public List<AqiCityData> readCityDayRptDataFromWeb(String url) {
        List<AqiCityData> retList = null;
        try {
            String jsonStr = HttpUtil.getRequest(url, "UTF-8");
            if (jsonStr != null) {
                jsonStr = jsonStr.toLowerCase();     //全变为小写，为了转对象
                Map<String, Object> mapFromJson = JsonUtil.getMapFromJson(jsonStr);

                if (mapFromJson != null && mapFromJson.size() > 0) {
                    Long timepoint = (Long) mapFromJson.get("timepoint");
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) mapFromJson.get("data");

                    if (dataList != null && dataList.size() > 0) {
                        logger.debug("从地址：" + url + "取AQI数据，取到" + dataList.size() + "条数据！");

                        retList = new ArrayList<AqiCityData>();
                        for (int i = 0; i < dataList.size(); i++) {
                            AqiCityData aqiCityData = null;
                            Map<String, Object> dataMap = (Map<String, Object>) dataList.get(i);
                            Map<String, Object> objMap = (Map<String, Object>) dataMap.get("columns");

                            try {
                                aqiCityData = new AqiCityData();
                                BeanUtils.populate(aqiCityData, objMap);
                            } catch (IllegalAccessException e) {
                            } catch (InvocationTargetException e) {
                            }
                            if (aqiCityData != null) {
                                retList.add(aqiCityData);
                            }
                        }
                    } else {
                        logger.debug("从地址：" + url + "取数据不正确，返回结果代码" + mapFromJson.get("returnCode"));
                    }
                }
            }
        }catch (Exception e) {
            logger.debug("从地址：" + url + "取数据时出错" + e.getMessage());
        }
        return retList;
    }

    public List<AqiObsData> readObsDataFromWeb(String url) {
        List<AqiObsData> retList = null;
        try {
            String jsonStr = HttpUtil.getRequest(url, "UTF-8");
            if (jsonStr != null) {
                jsonStr = jsonStr.toLowerCase();     //全变为小写，为了转对象
                List<Map<String, Object>> listMapFromJson = null;
                try {
                    listMapFromJson = JsonUtil.getListFromJson(jsonStr);
                } catch (Exception e) {
                    logger.debug("地址：" + url + "取的结果数据转换时出错" + e.getMessage());
                }
                if (listMapFromJson != null && listMapFromJson.size() > 0) {
                    logger.debug("从地址：" + url + "取AQI站点实测数据，取到" + listMapFromJson.size() + "小时数据！");
                    retList = new ArrayList<AqiObsData>();
                    for (int i = 0; i < listMapFromJson.size(); i++) {
                        Map<String, Object> mapData = listMapFromJson.get(i);
                        Long timepoint = (Long)mapData.get("timepoint");
                        List<Map<String, Object>> listMapData = (List<Map<String, Object>>) mapData.get("data");
                        if (listMapData != null && listMapData.size() > 0) {
                            Date datatime = new Date(timepoint);
                            for (int j = 0; j < listMapData.size(); j++) {
                                Map<String, Object> staMap = listMapData.get(j);
                                Map<String, Object> mapcolumns = (Map<String, Object>) staMap.get("columns");
                                AqiObsData aqiObsData = null;
                                try {
                                    aqiObsData = new AqiObsData();
                                    BeanUtils.populate(aqiObsData, mapcolumns);
                                    //部分数据的是间是空的！
                                    if(aqiObsData != null && aqiObsData.getTimepoint() == null) {
                                        aqiObsData.setTimepoint(datatime);
                                    }
                                } catch (IllegalAccessException e) {
                                } catch (InvocationTargetException e) {
                                }
                                if (aqiObsData != null) {
                                    //为空的数据不保存
                                    if (aqiObsData.getAqi() != null && aqiObsData.getPrimarypollutant() != null) {
                                        retList.add(aqiObsData);
                                    }
                                }
                            }
                        } else {
                            logger.debug("从地址：" + url + "取AQI站点实测数据，无data数据！");
                        }
                    }
                } else {
                    logger.debug("从地址：" + url + "取AQI站点实测数据，转换后无数据！");
                }
            }
        }catch (Exception e) {
            logger.error("从地址：" + url + "取AQI站点实测数据处理时，出错：" + e.getMessage());
        }
        return retList;
    }

    /**
     * 读城市站点数据
     * @param url
     * @return
     */
    public List<AqiObsData> readCountyDataFromWeb(String url) {
        List<AqiObsData> retList = null;
        try {
            String jsonStr = HttpUtil.getRequest(url, "UTF-8");
            if (jsonStr != null) {
                jsonStr = jsonStr.toLowerCase();     //全变为小写，为了转对象
                List<Map<String, Object>> listFromJson = JsonUtil.getListFromJson(jsonStr);
                Map<String, Object> mapFromJson = JsonUtil.getMapFromJson(jsonStr);

                if (mapFromJson != null && mapFromJson.size() > 0) {
                    Map<String, Object> mapcolumns = (Map<String, Object>) mapFromJson.get("columns");
                    Map<String, Object> stationrealtimeaqi = (Map<String, Object>) mapcolumns.get("stationrealtimeaqi");
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) stationrealtimeaqi.get("data");

                    if (dataList != null && dataList.size() > 0) {
                        logger.debug("从地址：" + url + "取AQI站点实测数据，取到" + dataList.size() + "条数据！");

                        retList = new ArrayList<AqiObsData>();
                        for (int i = 0; i < dataList.size(); i++) {
                            AqiObsData aqiObsData = null;
                            Map<String, Object> dataMap = (Map<String, Object>) dataList.get(i);
                            Map<String, Object> objMap = (Map<String, Object>) dataMap.get("columns");

                            try {
                                aqiObsData = new AqiObsData();
                                BeanUtils.populate(aqiObsData, objMap);
                            } catch (IllegalAccessException e) {
                            } catch (InvocationTargetException e) {
                            }
                            if (aqiObsData != null) {
                                retList.add(aqiObsData);
                            }
                        }
                    } else {
                        logger.debug("从地址：" + url + "取数据不正确，data数据为空");
                    }
                }else {
                    logger.debug("从地址：" + url + "取数据不正确，转为Json为空");
                }
            }
        }catch (Exception e) {
            logger.debug("从地址：" + url + "取数据时，出错" + e.getMessage());
        }
        return retList;
    }
    
    public static void main(String[] args) {
        try {
            String log4jConf = "log4j.properties";
            PropertyConfigurator.configure(log4jConf);
        } catch (Exception var4) {
            logger.error("装入log4配置文件时出错：" + var4.getMessage(), var4);
        }
        try {
            new GetAQIDataFromWebProcess().execute(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }
}
