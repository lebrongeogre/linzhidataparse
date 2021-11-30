package com.cuit.linzhi.parse.imp;

import com.cuit.linzhi.parse.ITableTransfer;
import com.cuit.linzhi.vo.RadiationMonthBody;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新旧表转换
 *  新表： StatisticsMonthAccumulationBody（t_sta_monthaccumulation）
 *  旧表： RadiationMonthBody (t_radiation_monthaccumulation)
 */
@Component
public class AccumulationTableTransfer implements ITableTransfer<StatisticsMonthAccumulationBody, RadiationMonthBody> {



    StatisticsMonthAccumulationBody statisticsMonthAccumulationBody;

    /**
     * 获取新表对象数据列表
     * @param radiationMonthBodies 旧表对象列表
     * @return
     */
    @Override
    public List<StatisticsMonthAccumulationBody> creatNewTableList(List<RadiationMonthBody> radiationMonthBodies) {
        //新建新表数据列表
        List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = new ArrayList<>();

        if (radiationMonthBodies.size() > 0) {
            //遍历旧表对象数据列表
            for (RadiationMonthBody ra : radiationMonthBodies) {
                //每次循环都生成一个新的新表对象
                statisticsMonthAccumulationBody = new StatisticsMonthAccumulationBody();
                //调用方法返回新表对象
                statisticsMonthAccumulationBody = createAccumulationBody(ra);
                //每次循环添加一个新表对象
                statisticsMonthAccumulationBodies.add(statisticsMonthAccumulationBody);
            }
            return statisticsMonthAccumulationBodies;
        }
        return null;
    }

    /**
     * 根据传来的旧表对象，生成一个新表对象
     * @param ra
     * @return
     */
    private StatisticsMonthAccumulationBody createAccumulationBody(RadiationMonthBody ra) {
        //新建一个新表对象
        StatisticsMonthAccumulationBody statisticsMonthAccumulationBody = new StatisticsMonthAccumulationBody();
        //调用方法，处理日期字段，返回日期map
        Map<String,String> dateMap = parseFiles(ra.getDataTime());
        //调用方法，返回新表对象
        return getAccumulationBody(statisticsMonthAccumulationBody,dateMap,ra);
    }

    private StatisticsMonthAccumulationBody getAccumulationBody(StatisticsMonthAccumulationBody statisticsMonthAccumulationBody, Map<String, String> dateMap, RadiationMonthBody ra) {

        statisticsMonthAccumulationBody.setMidmoonValue(ra.getMidmoonValue());
        statisticsMonthAccumulationBody.setMonthValue(ra.getMonthValue());
        statisticsMonthAccumulationBody.setYear(Integer.valueOf(dateMap.get("年")));
        statisticsMonthAccumulationBody.setMonth(Integer.valueOf(dateMap.get("月")));
        statisticsMonthAccumulationBody.setStation(ra.getStation());
        return statisticsMonthAccumulationBody;
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
        String time = (String)dateTime;
        dateMap.put("年",time.substring(0,4));
        dateMap.put("月",time.substring(4));
        return dateMap;
    }
}
