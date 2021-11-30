package com.cuit.linzhi.service.imp;

import com.cuit.linzhi.config.StatisticsConfig;
import com.cuit.linzhi.dao.StatisticsUVIMeanBodyMapper;
import com.cuit.linzhi.dao.UVParsedBodyMapper;
import com.cuit.linzhi.parse.Calculator;
import com.cuit.linzhi.service.UVIStatisticsService;

import com.cuit.linzhi.vo.StatisticsUVIMeanBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UVIStatisticsServiceImp implements UVIStatisticsService {


    @Resource
    UVParsedBodyMapper uvParsedBodyMapper;
    @Resource
    StatisticsUVIMeanBodyMapper statisticsUVIMeanBodyMapper;
    @Resource
    Calculator calculator;
    @Resource
    StatisticsConfig statisticsConfig;

    @Override
    /**
     *  统计月均值，并入库
     *      1. 查询语句需要三个参数，分三层循环
     *         第一层：循环年份
     *         第二层：循环站点
     *         第三层：循环月份
     *              查询结果最小项：某一年某个站点所有月份的list  2017年林芝1（1-12月）月数据
     *              查询次小项：某一年所有站点所有月份数据        2017年林芝、拉萨（1-12）月数据
     *              查询最外层项：所有年所有站点所有月份数据      2017-2020林芝、拉萨（1-12）月数据
     *     2、最内层循环：循环月份。
     *                   需要一个方法，参数是list（某一年某个站点某个月份的list），返回值是该月均值。
     *                   需要一个map，将当前月份及当前月份均值存入map、
     *                   循环十二次结束后，得到一个map<月份，均值>
     *     3、中间层循环：循环站点。
     *                   需要一个方法，参数是当前年、当前站点、最内层list，返回值是meanBody
     *                   需要一个方法，将meanBody插入数据库
     *     4、最外层循环：循环年份
     *
     */
    public int statisticsYearMean() {

        //从配置文件中获取年、站点list
        List<String> yearList = statisticsConfig.getYears();
        List<String> stationList = statisticsConfig.getStations();
        List<String> months = statisticsConfig.getMonths();


        //第一层循环，循环年份
        List<StatisticsUVIMeanBody> statisticsUVIMeanBodies = new ArrayList<>();
        for (String yearIndex : yearList) {
            //第一层循环，循环站点(这个map包含某一年所有站点、所有月份的均值)
            Map<String, Map<String, Double>> stationMonthValue = new HashMap<>();
            for (String stationIndex : stationList) {

                Map<String, Double> monthValue = new HashMap<>();
                //第一层循环，循环月份
                for (String monthIndex : months) {
                    //根据当前年份、站点、月份查询数据list
                    List<UVParsedBody> uvParsedBodies = uvParsedBodyMapper.queryByTerm(yearIndex, stationIndex, monthIndex);
                    //计算当前list均值
                    if (uvParsedBodies.size() == 0) {
                        continue;
                    }
                    Double meanValue = calculator.singleMeanCal(uvParsedBodies);
                    //key:月份 value：对应均值
                    monthValue.put(monthIndex, meanValue);
                }
                //key:站点  value：当前站点对应所有月份的均值map
                stationMonthValue.put(stationIndex, monthValue);
            }
            //获取当前年份所对应的均值对象
            List<StatisticsUVIMeanBody> staBodies = createMeanBody(yearIndex, stationMonthValue);
            //将当前年份对应均值对象添加到均值集合
            for (StatisticsUVIMeanBody sta:staBodies) {
                statisticsUVIMeanBodies.add(sta);
            }
        }
        //获取插入结果返回的行数 0：失败
        int ref = statisticsUVIMeanBodyMapper.insertList(statisticsUVIMeanBodies);
        if (ref != 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 生成均值表里一条记录
     *
     * @param year
     * @param stationMonthValue
     * @return
     */
    private List<StatisticsUVIMeanBody> createMeanBody(String year, Map<String, Map<String, Double>> stationMonthValue) {

        List<StatisticsUVIMeanBody> statisticsUVIMeanBodies = new ArrayList<>();
        StatisticsUVIMeanBody statisticsUVIMeanBody = null;
        //内嵌map
        Map<String, Double> monthValue = new HashMap<>();
        String station = "";

        //遍历第一层map key:站点名  value： 内层map  key：月份  value：值
        Set outKeySet = stationMonthValue.keySet();
        Iterator outIterator = outKeySet.iterator();
        while (outIterator.hasNext()) {
            station = (String) outIterator.next();
            monthValue = stationMonthValue.get(station);
            statisticsUVIMeanBody = getStatisticsUVIMeanBody(year, station, monthValue);
            statisticsUVIMeanBodies.add(statisticsUVIMeanBody);
        }
        return statisticsUVIMeanBodies;
    }

    private StatisticsUVIMeanBody getStatisticsUVIMeanBody(String year, String station, Map<String, Double> monthValue) {

        StatisticsUVIMeanBody statisticsUVIMeanBody = new StatisticsUVIMeanBody();

        statisticsUVIMeanBody.setYear(year);
        statisticsUVIMeanBody.setStation(station);
        statisticsUVIMeanBody.setMean1(monthValue.get("1"));
        statisticsUVIMeanBody.setMean2(monthValue.get("2"));
        statisticsUVIMeanBody.setMean3(monthValue.get("3"));
        statisticsUVIMeanBody.setMean4(monthValue.get("4"));
        statisticsUVIMeanBody.setMean5(monthValue.get("5"));
        statisticsUVIMeanBody.setMean6(monthValue.get("6"));
        statisticsUVIMeanBody.setMean7(monthValue.get("7"));
        statisticsUVIMeanBody.setMean8(monthValue.get("8"));
        statisticsUVIMeanBody.setMean9(monthValue.get("9"));
        statisticsUVIMeanBody.setMean10(monthValue.get("10"));
        statisticsUVIMeanBody.setMean11(monthValue.get("11"));
        statisticsUVIMeanBody.setMean12(monthValue.get("12"));

        return statisticsUVIMeanBody;
    }
}
