package com.cuit.linzhi.service.imp;


import com.cuit.linzhi.config.StatisticsConfig;
import com.cuit.linzhi.dao.StatisticsMonthAccumulationBodyMapper;
import com.cuit.linzhi.dao.UVParsedBodyMapper;
import com.cuit.linzhi.dao.UVPercentBodyMapper;
import com.cuit.linzhi.parse.Calculator;
import com.cuit.linzhi.service.UVIStatisticsService;

import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import com.cuit.linzhi.vo.UVParsedBody;
import com.cuit.linzhi.vo.UVPercentBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 统计yita
 * 1、按月份、站点查询所有辐射数据（t_radiation_parsed）
 * 2、按月份、站点查询所有月辐射总量数据(t_sta_monthaccumulation)
 * 3、调用方法返回yita列表(t_radiation_yita)
 * 4、插入数据库
 */
@Service("month")
public class UVIMonthStatisticsServiceImp implements UVIStatisticsService {

    @Resource
    UVParsedBodyMapper uvParsedBodyMapper;
    @Resource
    StatisticsMonthAccumulationBodyMapper statisticsMonthAccumulationBodyMapper;
    @Resource
    UVPercentBodyMapper uvPercentBodyMapper;
    @Resource
    StatisticsConfig statisticsConfig;
    @Resource
    Calculator calculator;


    public int statisticsYearMean() {

        //读取站点、月份列表
        List<String> stations = statisticsConfig.getStations();
        List<String> months = statisticsConfig.getMonths();
        List<String> years = statisticsConfig.getYears();
        List<UVPercentBody> uvPercentBodies = new ArrayList<>();
        //用于存储所有年份的某个月的数据列表
        List<UVParsedBody> tempUVParsedBodies = null;
        List<StatisticsMonthAccumulationBody> tempStatisticsMonthAccumulationBodies = null;
        //最外层循环，遍历站点列表
        if (stations.size() > 0 && months.size() > 0) {
            for (String stationIndex : stations) {
                //第二层循环，遍历月份列表
                for (String monthIndex : months) {
                    tempUVParsedBodies = new ArrayList<>();
                    tempStatisticsMonthAccumulationBodies = new ArrayList<>();
                    //将当前站点、月份作为参数，查询对应集合
                    for (String yearIndex : years) {
                        List<UVParsedBody> uvParsedBodies = uvParsedBodyMapper.queryByTerm(yearIndex, stationIndex, monthIndex);
                        tempUVParsedBodies.addAll(uvParsedBodies);
                        if (tempUVParsedBodies.size() != 0) {
                            List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = statisticsMonthAccumulationBodyMapper.queryByTerm(yearIndex, stationIndex, monthIndex);
                            tempStatisticsMonthAccumulationBodies.addAll(statisticsMonthAccumulationBodies);
                        }
                    }
                    UVPercentBody uvPercentBody = calculator.calYiTaMean(stationIndex, monthIndex, tempUVParsedBodies, tempStatisticsMonthAccumulationBodies);
                    uvPercentBodies.add(uvPercentBody);
                }
            }
            Integer ref = uvPercentBodyMapper.insertBatch(uvPercentBodies);
            return ref;
        }
        return 0;
    }
}