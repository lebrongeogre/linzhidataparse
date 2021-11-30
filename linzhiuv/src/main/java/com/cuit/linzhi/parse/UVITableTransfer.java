package com.cuit.linzhi.parse;


import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新旧表转换
 *      新表：UVParsedBody（t_radiation_parsed）
 *      旧表：StatisticsUVIBody（t_sta_uvi）
 */
@Component
public class UVITableTransfer implements ITableTransfer<UVParsedBody, StatisticsUVIBody> {

    private UVParsedBody uvParsedBody;

    @Override
    public List<UVParsedBody> creatNewTableList(List<StatisticsUVIBody> statisticsUVIBodies) {

        List<UVParsedBody> uvParsedBodies;
        uvParsedBodies = new ArrayList<>();

        if (statisticsUVIBodies.size() != 0) {
            //遍历集合
            for (StatisticsUVIBody statisticsUVIBody : statisticsUVIBodies) {
                uvParsedBody = new UVParsedBody();
                uvParsedBody = createUVParseBody(statisticsUVIBody);
                uvParsedBodies.add(uvParsedBody);
            }
            return uvParsedBodies;
        }
        return null;
    }

    @Override
    public Map<String, String> parseFiles(Object dataTime) {

        Map<String, String> dateMap = new HashMap<>();
        //将日期格式转换为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String dateStr = dateFormat.format(dataTime);

        dateMap.put("年",dateStr.split("-")[0]);
        //去0
        int month = Integer.valueOf(dateStr.split("-")[1]);
        String monthStr = String.valueOf(month);
        dateMap.put("月",monthStr);
        dateMap.put("日",dateStr.split("-")[2]);
        dateMap.put("时",dateStr.split("-")[3]);
        return dateMap;
    }


    private UVParsedBody createUVParseBody(StatisticsUVIBody statisticsUVIBody) {
        UVParsedBody uvParsedBody = new UVParsedBody();
        Map<String, String> timeTrans = parseFiles(statisticsUVIBody.getDataTime());
        return getUvParsedBody(statisticsUVIBody, uvParsedBody, timeTrans);
    }

    static UVParsedBody getUvParsedBody(StatisticsUVIBody statisticsUVIBody, UVParsedBody uvParsedBody, Map<String, String> timeTrans) {
        uvParsedBody.setUviHourMax(statisticsUVIBody.getUviHourMax());
        uvParsedBody.setStation(statisticsUVIBody.getStation());
        uvParsedBody.setYear(timeTrans.get("年"));
        uvParsedBody.setMonth(timeTrans.get("月"));
        uvParsedBody.setDay(timeTrans.get("日"));
        uvParsedBody.setHour(timeTrans.get("时"));
        return uvParsedBody;
    }
}
