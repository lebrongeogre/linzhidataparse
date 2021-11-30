package com.cuit.linzhi.parse;

import com.cuit.linzhi.vo.*;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.*;

@Component
public class Calculator {


    private UVPercentBody uvPercentBody;




    /**
     * 统计η
     * 10年，12个月
     *
     * @param radiationBodyList
     * @return
     */

    public List<UVPercentBody> calYiTaMean(List<RadiationBody> radiationBodyList) {

        List<UVPercentBody> uvPercentBodies = new LinkedList<>();

        for (int j = 1; j <= 12; j++) {
            Double mean2 = 0.0;
            Double mean9 = 0.0;
            Double sum = 0.0;
            for (RadiationBody radiationBody : radiationBodyList) {
                int month = Integer.parseInt(radiationBody.getMonth());
                Double allradiation = radiationBody.getAllradiation();
                Double uvradiation = radiationBody.getUvradiation();
                if (month == j) {
                    mean2 = uvradiation / allradiation;
                    sum += mean2;
                }
            }
            uvPercentBody = new UVPercentBody();
            DecimalFormat df = new DecimalFormat("0.000000");
            mean9 = Double.valueOf(df.format((Double) sum / 9));
            uvPercentBody.setMonth(j);
            uvPercentBody.setYita(mean9);
            uvPercentBodies.add(uvPercentBody);
        }
        return uvPercentBodies;
    }

    /**
     * 计算传入集合的均值
     * @param uvParsedBodies
     * @return
     */
    public Double singleMeanCal(List<UVParsedBody> uvParsedBodies) {

        Double uviMean = 0.0;
        double uviTotal = 0.0;

        if (uvParsedBodies.size()>0) {
            //遍历集合,获取uvi总和
            for (UVParsedBody uvParsedBody:uvParsedBodies) {
                uviTotal += Double.valueOf(uvParsedBody.getUviHourMax());
            }
            //计算均值
            uviMean = uviTotal/(uvParsedBodies.size());
            //保留小数，转换为字符串
            uviMean = Double.valueOf(String.format("%2f",uviTotal/(uvParsedBodies.size())));
            return uviMean;
        }
        return null;
    }

    /**
     * 计算新版本yita
     *
     *
     * @param
     * @param uvParsedBodies
     * @param statisticsMonthAccumulationBodies
     * @return
     */
    public UVPercentBody calYiTaMean( String station, String month, List<UVParsedBody> uvParsedBodies, List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies) {

        if (uvParsedBodies.size() > 0 && statisticsMonthAccumulationBodies.size() > 0){
            Double uviMonthAccumulation = singleMeanCal(uvParsedBodies);
            Double uvMonthAccumulation = statisticsMonthAccumulationBodies(statisticsMonthAccumulationBodies);
            if (!uviMonthAccumulation.equals(0) && !uvMonthAccumulation.equals(0)){
                Double yita = uviMonthAccumulation/uvMonthAccumulation*100;
                UVPercentBody uvPercentBody = getUVPercentBody(station,month,yita);
                return uvPercentBody;
            }
        }
        return null;
    }

    private UVPercentBody getUVPercentBody(String station, String month, Double yita) {
        return new UVPercentBody(Integer.valueOf(month),yita,station);
    }


    private Double statisticsMonthAccumulationBodies(List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies) {

        Double uviMean = 0.0;
        double uviTotal = 0.0;

        if (statisticsMonthAccumulationBodies.size()>0) {
            //遍历集合,获取uvi总和
            for (StatisticsMonthAccumulationBody sta:statisticsMonthAccumulationBodies) {
                uviTotal += Double.valueOf(sta.getMonthValue());
            }
            //计算均值
            uviMean = uviTotal/(statisticsMonthAccumulationBodies.size());
            //保留小数，转换为字符串
            uviMean = Double.valueOf(String.format("%2f",uviTotal/(statisticsMonthAccumulationBodies.size())));
            return uviMean;
        }
        return null;

    }

}
