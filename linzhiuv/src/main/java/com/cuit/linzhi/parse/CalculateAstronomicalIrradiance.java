package com.cuit.linzhi.parse;

import com.cuit.linzhi.vo.AstronomicalIrradianceBody;
import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import com.cuit.linzhi.vo.TrueSolarTimeBody;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

@Component
public class CalculateAstronomicalIrradiance {

    @Resource
    CalculateTrueSolarTime calculateTrueSolarTime;  // 计算真太阳时类

    private TrueMeanTimeDifferenceBody trueMeanTimeDifferenceBody;
    private AstronomicalIrradianceBody astronomicalIrradianceBody;  // 天文辐照度类

    private List<AstronomicalIrradianceBody> astronomicalIrradianceBodies;
    private List<TrueSolarTimeBody> trueSolarTimeBodies;

    private List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies;

    public List<TrueMeanTimeDifferenceBody> getTrueMeanTimeDifferenceBodies() {
        return trueMeanTimeDifferenceBodies;
    }

    public void setTrueMeanTimeDifferenceBodies(List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies) {
        this.trueMeanTimeDifferenceBodies = trueMeanTimeDifferenceBodies;
    }

    /**
     * 得到天文辐照度list
     *
     * @param strLongitude
     * @param strNoonTime
     * @param
     * @return 天文辐照度list
     * @throws ParseException
     */
    public List<AstronomicalIrradianceBody> calculateAstronomicalIrradiance(String station, String strLongitude, String strNoonTime) throws ParseException {
        //转换经纬度格式
        String transLongitude = transToDegree(strLongitude);

        // 1、计算得出真太阳时
        trueSolarTimeBodies = calculateTrueSolarTime.calculateTrueSolarTime(transLongitude, strNoonTime, trueMeanTimeDifferenceBodies);

        // 2、遍历得出的一年真太阳时列表,计算天文辐照度
        traversalTrueSolarTimeMillSecondsList(station, trueSolarTimeBodies);

        return astronomicalIrradianceBodies;
    }

    /**
     * 将度分秒形式转换为度,以字符串形式返回
     *
     * @param strLongitude
     * @return
     */
    private String transToDegree(String strLongitude) {
        Double degree = null;
        /* 拆分字符串，拆分度分秒*/
        if (strLongitude.contains("秒")) {
            String[] longitudeSplitArray = strLongitude.split("度|分|秒");
            degree = Double.valueOf(longitudeSplitArray[0]) + Double.valueOf(longitudeSplitArray[1]) / 60 + Double.valueOf(longitudeSplitArray[2]) / 3600;
        } else if (strLongitude.contains("分")) {
            String[] longitudeSplitArray = strLongitude.split("度|分");
            degree = Double.valueOf(longitudeSplitArray[0]) + Double.valueOf(longitudeSplitArray[1]) / 60;
        }else if (strLongitude.contains("度")){
            return strLongitude;
        }
        return String.valueOf(degree);
    }

    /**
     * 遍历时差表，并计算天文辐照度list
     *
     * @param station
     * @param trueSolarTimeBodies
     */
    private void traversalTrueSolarTimeMillSecondsList(String station, List<TrueSolarTimeBody> trueSolarTimeBodies) {

        for (int i = 0; i < trueSolarTimeBodies.size(); i++) {

            /* 按顺序取一年中每天的真太阳时 */
            TrueSolarTimeBody trueSolarTimeBody = trueSolarTimeBodies.get(i);
            Long trueSolarTimeMillSeconds = trueSolarTimeBody.getTrueSolarTime();
            Integer trueSolarTime = Math.round(trueSolarTimeMillSeconds / (1000 * 60));    // 以24小时计,毫秒数转为小时

            // 1、计算日地距离订正值
            Double sunEarthDistanceAmendedValuation = calSunEarthDistanceAmendedValuation(i);
            // 2、计算太阳高度角的正弦
            Double sinSolarAltitude = calSolarDeclination(i, trueSolarTime);
            // 3、计算天文辐照度的结果
            calAstronomicalIrradianceList(station, i, sunEarthDistanceAmendedValuation, sinSolarAltitude);
        }
    }

    /**
     * 利用总公式计算天文辐照度的最终结果
     *
     * @param station
     * @param i
     * @param sunEarthDistanceAmendedValuation
     * @param sinSolarAltitude
     */
    private void calAstronomicalIrradianceList(String station, int i, Double sunEarthDistanceAmendedValuation, Double sinSolarAltitude) {
        // 1、计算最终结果
        Double result = 1377.0 * sinSolarAltitude * sunEarthDistanceAmendedValuation;

        // 得到分割后的日期
        String[] dateSplitArray = splitDate();
        String strMonth = dateSplitArray[0];
        String strDay = dateSplitArray[1];

        /* 给天文辐照度 字段赋值 */
        AstronomicalIrradianceBody astronomicalIrradianceBody = new AstronomicalIrradianceBody();
        astronomicalIrradianceBody.setStation(station); // 站点
        astronomicalIrradianceBody.setMonth(String.valueOf(strMonth));  // 月
        astronomicalIrradianceBody.setDay(String.valueOf((strDay)));    // 日
        astronomicalIrradianceBody.setAstronomicalIrradiance(result);   //天文辐照度
        /* 加到天文辐照度list中 */
        astronomicalIrradianceBodies.add(astronomicalIrradianceBody);
    }

    /**
     * 拆分月日 (几月几日)
     *
     * @return
     */
    private String[] splitDate() {
        String strDate = trueMeanTimeDifferenceBody.getDate();
        return strDate.split("月|日");
    }

    /**
     * 借传来的一年中的第几天(i)参数，用子公式计算日地距离订正值
     *
     * @param i 日
     * @return
     */
    private Double calSunEarthDistanceAmendedValuation(int i) {
        return 1.00011 + 0.034221 * Math.cos((2 * Math.PI * (i + 1)) / 365)
                + 0.00128 * Math.sin((2 * Math.PI * (i + 1)) / 365)
                + 0.000719 * Math.cos((4 * Math.PI) / 365)
                + 0.000077 * Math.sin((4 * Math.PI * (i + 1)) / 365);
    }

    /**
     * 借传来的一年中的第几天(i)参数和那一天的真太阳时，用子公式计算那一天的太阳高度角的正弦
     *
     * @param i
     * @param trueSolarTime
     * @return
     */
    private Double calSolarDeclination(int i, Integer trueSolarTime) {
        /* 1、计算太阳赤纬 */
        Double solarDeclination = 0.006918 - 0.339912 * Math.cos((2 * Math.PI * (i + 1)) / 365)
                + 0.070257 * Math.sin((2 * Math.PI * (i + 1)) / 365)
                + 0.006758 * Math.cos((4 * Math.PI * (i + 1)) / 365)
                + 0.000907 * Math.sin((4 * Math.PI * (i + 1)) / 365)
                + 0.002697 * Math.cos((6 * Math.PI * (i + 1)) / 365)
                + 0.00148 * Math.sin((6 * Math.PI * (i + 1)) / 365);
        /* 2、计算太阳时角 */
        Double solarHourAngle = (12 - trueSolarTime) * (-(Math.PI) / 12);    // trueSolarTime，以24小时计

        return Math.cos(solarDeclination) * Math.cos(solarHourAngle);
    }
}