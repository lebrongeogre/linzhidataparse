package com.cuit.linzhi.parse;

import com.cuit.linzhi.vo.RadiationMonthBody;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccumulationTableTransfer implements ITableTransfer<StatisticsMonthAccumulationBody, RadiationMonthBody> {


    @Resource
    StatisticsMonthAccumulationBody statisticsMonthAccumulationBody;

    @Override
    public List<StatisticsMonthAccumulationBody> creatNewTableList(List<RadiationMonthBody> radiationMonthBodies) {

        List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = new ArrayList<>();

        if (radiationMonthBodies.size() > 0) {

            for (RadiationMonthBody ra : radiationMonthBodies) {
                statisticsMonthAccumulationBody = new StatisticsMonthAccumulationBody();
                statisticsMonthAccumulationBody = createAccumulationBody(ra);
                statisticsMonthAccumulationBodies.add(statisticsMonthAccumulationBody);
            }
            return statisticsMonthAccumulationBodies;
        }
        return null;
    }

    private StatisticsMonthAccumulationBody createAccumulationBody(RadiationMonthBody ra) {

        StatisticsMonthAccumulationBody statisticsMonthAccumulationBody = new StatisticsMonthAccumulationBody();
        Map<String,String> dateMap = parseFiles(ra.getDataTime());
        return getAccumulationBody(statisticsMonthAccumulationBody,dateMap,ra);

    }

    private StatisticsMonthAccumulationBody getAccumulationBody(StatisticsMonthAccumulationBody statisticsMonthAccumulationBody, Map<String, String> dateMap, RadiationMonthBody ra) {

        statisticsMonthAccumulationBody.setMidmoonValue(ra.getMidmoonValue());
        statisticsMonthAccumulationBody.setMonthValue(ra.getMonthValue());
        statisticsMonthAccumulationBody.setYear(Integer.valueOf(dateMap.get("???")));
        statisticsMonthAccumulationBody.setMonth(Integer.valueOf(dateMap.get("???")));
        statisticsMonthAccumulationBody.setStation(ra.getStation());
        return statisticsMonthAccumulationBody;
    }

    @Override
    public Map<String,String> parseFiles(Object dateTime) {

        Map<String,String> dateMap = new HashMap<>();
        String time = (String)dateTime;
        dateMap.put("???",time.substring(0,3));
        dateMap.put("???",time.substring(4));
        return dateMap;
    }

    @Component
    public static class UVITableTransfer implements ITableTransfer<UVParsedBody, StatisticsUVIBody> {

        private UVParsedBody uvParsedBody;

        @Override
        public List<UVParsedBody> creatNewTableList(List<StatisticsUVIBody> statisticsUVIBodies) {

            List<UVParsedBody> uvParsedBodies;
            uvParsedBodies = new ArrayList<>();

            if (statisticsUVIBodies.size() != 0) {
                //????????????
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
            //?????????????????????????????????
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String dateStr = dateFormat.format(dataTime);

            dateMap.put("???",dateStr.split("-")[0]);
            //???0
            int month = Integer.valueOf(dateStr.split("-")[1]);
            String monthStr = String.valueOf(month);
            dateMap.put("???",monthStr);
            dateMap.put("???",dateStr.split("-")[2]);
            dateMap.put("???",dateStr.split("-")[3]);
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
            uvParsedBody.setYear(timeTrans.get("???"));
            uvParsedBody.setMonth(timeTrans.get("???"));
            uvParsedBody.setDay(timeTrans.get("???"));
            uvParsedBody.setHour(timeTrans.get("???"));
            return uvParsedBody;
        }
    }
}
