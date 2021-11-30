package com.cuit.linzhi.service.imp;


import com.cuit.linzhi.config.StatisticsConfig;
import com.cuit.linzhi.dao.UVParsedBodyMapper;
import com.cuit.linzhi.service.UVIMonthValueService;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import com.cuit.linzhi.vo.StatisticsUVIMonthValueBody;
import com.cuit.linzhi.vo.UVParsedBody;
import com.cuit.linzhi.vo.UVPercentBody;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class UVIMonthValueServiceImp implements UVIMonthValueService {

    @Resource
    UVParsedBodyMapper uvParsedBodyMapper;
    @Resource
    StatisticsUVIMonthValueBody statisticsUVIMonthValueBody;
    @Resource
    StatisticsConfig statisticsConfig;



    public Integer insertBatch(){
        //读取站点、月份列表
        List<String> stations = statisticsConfig.getStations();
        List<String> months = statisticsConfig.getMonths();
        List<String> years = statisticsConfig.getYears();
        List<UVPercentBody> uvPercentBodies = new ArrayList<>();
        //最外层循环，遍历站点列表
        if (stations.size() > 0 && months.size() > 0) {
            for (String stationIndex : stations) {
                //第二层循环，遍历月份列表
                for (String monthIndex : months) {
                    //将当前站点、月份作为参数，查询对应集合
                    for (String yearIndex : years) {

                    }

                }
            }

        }
        return 0;

    }
}
