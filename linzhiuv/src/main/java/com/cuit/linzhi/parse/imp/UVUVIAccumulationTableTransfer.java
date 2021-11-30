package com.cuit.linzhi.parse.imp;

import com.cuit.linzhi.parse.ITableTransfer;
import com.cuit.linzhi.vo.*;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 新旧表转换
 *      新表：StatisticsUVIParsedBody （t_sta_uviparsed）
 *      旧表：StatisticsAllRadiationBody （t_sta_uviplus）
 */
@Component
public class UVUVIAccumulationTableTransfer implements ITableTransfer<StatisticsUVIParsedBody, StatisticsAllRadiationBody> {



    StatisticsUVIParsedBody statisticsUVIParsedBody;

    /**
     * 获取新表对象数据列表
     * @param statisticsAllRadiationBodies 旧表对象列表
     * @return
     */
    @Override
    public List<StatisticsUVIParsedBody> creatNewTableList(List<StatisticsAllRadiationBody> statisticsAllRadiationBodies) {
        //新建新表数据列表
        List<StatisticsUVIParsedBody> statisticsUVIParsedBodies = new ArrayList<>();

        if (statisticsAllRadiationBodies.size() > 0) {
            //遍历旧表对象数据列表
            for (StatisticsAllRadiationBody sta : statisticsAllRadiationBodies) {
                //每次循环都生成一个新的新表对象
                statisticsUVIParsedBody = new StatisticsUVIParsedBody();
                //调用方法返回新表对象
                statisticsUVIParsedBody = createAccumulationBody(sta);
                //每次循环添加一个新表对象
                statisticsUVIParsedBodies.add(statisticsUVIParsedBody);
            }
            return statisticsUVIParsedBodies;
        }
        return null;
    }

    /**
     * 根据传来的旧表对象，生成一个新表对象
     * @param sta
     * @return
     */
    private StatisticsUVIParsedBody createAccumulationBody(StatisticsAllRadiationBody sta) {
        //新建一个新表对象
        StatisticsUVIParsedBody statisticsUVIParsedBody = new StatisticsUVIParsedBody();
        //调用方法，处理日期字段，返回日期map
        Map<String,String> dateMap = parseFiles(sta.getDataTime());
        //调用方法，返回新表对象
        return getAccumulationBody(statisticsUVIParsedBody,dateMap,sta);
    }

    private StatisticsUVIParsedBody getAccumulationBody(StatisticsUVIParsedBody statisticsUVIParsedBody, Map<String, String> dateMap, StatisticsAllRadiationBody sta) {

        statisticsUVIParsedBody.setYear(Integer.valueOf(dateMap.get("年")));
        statisticsUVIParsedBody.setMonth(Integer.valueOf(dateMap.get("月")));
        statisticsUVIParsedBody.setStation(sta.getStation());
        statisticsUVIParsedBody.setUviMonthAccumulation(sta.getUviHourAccumu());
        return statisticsUVIParsedBody;

    }

    /**
     * 参数处理方法
     * @param dateTime
     * @return
     */
    @Override
    public Map<String,String> parseFiles(Object dateTime) {
        //新建日期map
        Map<String,String> dateMap = new HashMap<>();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = (Date)dateTime;
        try{
            String timeStr = format1.format(time);
            dateMap.put("年",timeStr.split("-")[0]);
            dateMap.put("月",timeStr.split("-")[1].split("-")[0]);
            return dateMap;
        }catch(Exception e){
            System.out.println("当前日期："+time);
        }
        return null;
    }
}
