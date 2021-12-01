package com.cuit.mete.jobs.aqi.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.DateUtil;
import com.cuit.mete.jobs.aqi.db.AqiConCityDayDao;
import com.cuit.mete.jobs.aqi.domain.AqiCityData;
import com.cuit.mete.jobs.aqi.domain.AqiDayData;
import com.cuit.mete.jobs.aqi.domain.IAQICalculator;
import com.cuit.mete.jobs.aqi.domain.ParseAQI;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCitysAQIDataFromWebProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(GetCitysAQIDataFromWebProcess.class);
    private Map<String, Object> config;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }
    public void startJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //这是从上下文件获取从jobService中传过来的配置
        config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");

        if (config != null) {
            logger.debug("开始从环境监测网站提取数据......");
            //http://www.scnewair.cn:6112/publish/getAllCityDayAQIC
            String url = (String) config.get("url");
            String repeat = (String) config.get("repeat");
            String sleep = (String) config.get("sleep");
            int repeatInt = 1;
            long sleepLong = 10000L;
            if (StringUtils.isEmpty(repeat)) {
                try {
                    repeatInt = Integer.parseInt(repeat);
                } catch (NumberFormatException e) {
                    repeatInt = 1;
                }
            }
            if (StringUtils.isEmpty(sleep)) {
                try {
                    sleepLong = Long.parseLong(sleep);
                } catch (NumberFormatException e) {
                    sleepLong = 10000L;
                }
            }
            //站点配置
            List<String> stationsList = (List<String>) config.get("stations");
            String[] stations = null;
            if (stationsList != null && stationsList.size() > 0) {
                stations = new String[stationsList.size()];
                for (int i = 0; i < stationsList.size(); i++) {
                    stations[i] = stationsList.get(i);
                }
            }

            readAQIDataFromWeb(url, stations, repeatInt, sleepLong);
        }
    }

    /**
     * 读取站点AQI
     *
     * @throws Exception
     */
    public int readAQIDataFromWeb(String url,  String[] stations, int repeatInt, long sleepLong) {
        int saverow = 0;
        GetAQIDataFromWebProcess getAQIDataFromWebProcess = new GetAQIDataFromWebProcess();
        IAQICalculator iaqiCalculator = new IAQICalculator();
        ParseAQI parseAQI = new ParseAQI();

        List<AqiCityData> aqiCityDataList = null;
        List<AqiCityData> aqiCityDataConList = null;
        for (int i = 0; i < repeatInt; i++) {
            try {
                //从网页取数据
                aqiCityDataList = getAQIDataFromWebProcess.readCityDayRptDataFromWeb(url);
                //检查是否取到数据
                if (aqiCityDataList != null) {
                    aqiCityDataConList = new ArrayList<AqiCityData>();
                    for (int j = 0; j < aqiCityDataList.size(); j++) {
                        AqiCityData aqiCityData = aqiCityDataList.get(j);
                        //检查是否是需要的数据
                        if (aqiCityData != null && aqiCityData.getCityname() != null && isContian(aqiCityData.getCityname(), stations)) {
                            //需要保存的
                            aqiCityDataConList.add(aqiCityData);
                        }
                    }
                    logger.debug("从环境监测网站提取AQI数据，需要保存的" + aqiCityDataConList.size() + "条城市空气质量数据！");
                    break;
                }
                Thread.sleep(sleepLong);
            } catch (InterruptedException e) {
            }

            //保存数据
            if (aqiCityDataConList != null && aqiCityDataConList.size() > 0) {
                AqiConCityDayDao aqiConCityDayDao = new AqiConCityDayDao();
                //不再保存Json文件了
                for (int j = 0; j < aqiCityDataConList.size(); j++) {
                    AqiCityData aqiCityData = aqiCityDataConList.get(j);
                    //检查是否已经存在
                    if (!aqiConCityDayDao.isExist(aqiCityData.getCitycode(), aqiCityData.getTimepoint())) {
                        AqiDayData aqiDayData = new AqiDayData();
                        iaqiCalculator.calDayIAQI(aqiCityData.getAqi(), aqiCityData.getPm2_5(), aqiCityData.getSo2(), aqiCityData.getNo2(), aqiCityData.getO3(), aqiCityData.getCo());
                        parseAQI.calLevel(aqiCityData.getAqi());
                        parseAQI.MaxIAQIAndPrimaryFactor(iaqiCalculator.getIAQIPM10(), iaqiCalculator.getIAQIPM25(), iaqiCalculator.getIAQISO2(), iaqiCalculator.getIAQINO2(), iaqiCalculator.getIAQIO3(), iaqiCalculator.getIAQICO());

                        aqiDayData.setCitycode(aqiCityData.getCitycode());
                        aqiDayData.setStaname(aqiCityData.getCityname());
                        aqiDayData.setDatatime(DateUtil.formatDateTime(aqiCityData.getTimepoint()));
                        aqiDayData.setLevel(parseAQI.getLevel());
                        aqiDayData.setLevelint(parseAQI.getIntlevel());
                        aqiDayData.setContaminants(aqiCityData.getPrimarypollutant());
                        aqiDayData.setPM25(aqiCityData.getPm2_5());
                        aqiDayData.setPM10(aqiCityData.getPm10());
                        aqiDayData.setSO2(aqiCityData.getSo2());
                        aqiDayData.setNO2(aqiCityData.getNo2());
                        aqiDayData.setCO(aqiCityData.getCo());
                        aqiDayData.setO3(aqiCityData.getO3());
                        aqiDayData.setAQI(aqiCityData.getAqi());
                        aqiDayData.setIPM25(iaqiCalculator.getIAQIPM25());
                        aqiDayData.setIPM10(iaqiCalculator.getIAQIPM10());
                        aqiDayData.setISO2(iaqiCalculator.getIAQISO2());
                        aqiDayData.setINO2(iaqiCalculator.getIAQINO2());
                        aqiDayData.setICO(iaqiCalculator.getIAQICO());
                        aqiDayData.setIO3(iaqiCalculator.getIAQIO3());
                        aqiDayData.setEtlid(9999L);
                        //不存在，则保存数据
                        aqiConCityDayDao.saveData2DB(aqiDayData);
                    }
                }
            }
        }
        return saverow;
    }

    private boolean isContian(String sta, String [] stations) {
        for (String station : stations) {
            if (station.equalsIgnoreCase(sta)) {
                return true;
            }
            //有可能包含
            if (sta.indexOf(station) >= 0) {
                return true;
            }
        }
        return false;
    }

}
