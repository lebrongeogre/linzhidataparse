package com.cuit.linzhi.parse;

import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import com.cuit.linzhi.vo.TrueSolarTimeBody;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CalculateTrueSolarTime {

    private List<TrueSolarTimeBody> trueSolarTimeBodies = new ArrayList<>();
    private Long meanSolarTimeMilliSeconds;
    private Long trueSolarTimeMilliSeconds;
    private Long trueMeanTimeDifferenceMilliSeconds; // 真平太阳时差

    TrueMeanTimeDifferenceBody trueMeanTimeDifferenceBody;
    TrueSolarTimeBody trueSolarTimeBody = new TrueSolarTimeBody();

    /**
     * 计算出真太阳时list（每一天的真太阳时不同）
     *
     * @param strLongitude                 当地经度字符串
     * @param strNoonTime                  正午时间
     * @param trueMeanTimeDifferenceBodies
     * @return 真太阳时list
     * @throws ParseException
     */
    public List<TrueSolarTimeBody> calculateTrueSolarTime(String strLongitude, String strNoonTime, List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies) throws ParseException {
        // 1、计算平太阳时
        meanSolarTimeMilliSeconds = calculateMeanSolarTime(strLongitude, strNoonTime);
        // 2、遍历一年的时差表，计算真太阳时
        trueSolarTimeBodies = traverseTimeDifferenceList(meanSolarTimeMilliSeconds, trueMeanTimeDifferenceBodies);
        return trueSolarTimeBodies;
    }

    /**
     * 计算出平太阳时
     *
     * @param strLongitude 当地经度字符串
     * @param strNoonTime  正午时间
     * @return 平太阳时毫秒数
     * @throws ParseException
     */
    public Long calculateMeanSolarTime(String strLongitude, String strNoonTime) throws ParseException {
        // 1、根据经度算站点时差值的毫秒数
        Long timeDifferenceMilliSeconds = calTimeDifferenceMilliSeconds(strLongitude);
        // 2、将正午时间转为毫秒数
        long noonTimeMilliSeconds = calNoonTimeMilliSeconds(strNoonTime);
        // 3、计算真太阳时的毫秒数
        meanSolarTimeMilliSeconds = noonTimeMilliSeconds + timeDifferenceMilliSeconds;
        return meanSolarTimeMilliSeconds;
    }

    /**
     * 根据公式计算时差的毫秒数
     *
     * @param strLongitude
     * @return
     */
    private Long calTimeDifferenceMilliSeconds(String strLongitude) {

        DecimalFormat df = new DecimalFormat("#");
        // 计算时差毫秒数
        String stationTimeDifferenceMinute = df.format((Double.valueOf(strLongitude) - 120.0) * 4 * 60 * 1000);

        return Long.valueOf(stationTimeDifferenceMinute);
    }

    /**
     * 将正午时间转为毫秒数
     *
     * @param strNoonTime
     * @return
     * @throws ParseException
     */
    private long calNoonTimeMilliSeconds(String strNoonTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date noonTime = sdf.parse(strNoonTime);
        return noonTime.getTime();
    }

    /**
     * 遍历时差表，计算真太阳时list
     *
     * @param meanSolarTimeMillSeconds
     * @param trueMeanTimeDifferenceBodies
     * @return
     */
    public List<TrueSolarTimeBody> traverseTimeDifferenceList(Long meanSolarTimeMillSeconds, List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies) {
        // 得到存入对象中的时差表值
        if (trueMeanTimeDifferenceBodies != null) {
            for (int i = 0; i < trueMeanTimeDifferenceBodies.size(); i++) {
                // 1、取第i个时差值记录
                trueMeanTimeDifferenceBody = trueMeanTimeDifferenceBodies.get(i);
                // 2、取第i个时差值
                String strTimeDifferenceValue = trueMeanTimeDifferenceBody.getTimeDifferenceValue();
                // 3、取一年中的日期(几月几日)
                String strDate = trueMeanTimeDifferenceBody.getDate();
                // 4、判断时差值的正负，并进行正负值处理
                trueMeanTimeDifferenceMilliSeconds = judgeTimeDifferenceValueSign(strTimeDifferenceValue);
                // 得到真太阳时的毫秒数
                trueSolarTimeMilliSeconds = calTrueSolarTimeMillSeconds(meanSolarTimeMillSeconds);

                // 设置真太阳时的字段值,并将一条记录加到list
                trueSolarTimeBody.setTrueSolarTime(trueSolarTimeMilliSeconds);
                trueSolarTimeBody.setDate(strDate);
                trueSolarTimeBodies.add(trueSolarTimeBody);
            }
        }
        return trueSolarTimeBodies;
    }

    /**
     * 判断时差值的正负，计算出时差的毫秒数
     *
     * @param timeDifferenceValue
     * @return
     */
    private Long judgeTimeDifferenceValueSign(String timeDifferenceValue) {
        boolean status = timeDifferenceValue.contains("-");
        if (status) {
            /* 时差值为负 */
            calMinusTimeDifferenceValue(timeDifferenceValue);
        } else {
            /* 时差值为正 */
            calPlusTimeDifferenceValue(timeDifferenceValue);
        }
        return trueMeanTimeDifferenceMilliSeconds;
    }

    /**
     * 当时差为正时，拆分正号、分、秒
     *
     * @param timeDifferenceValue
     * @return
     */
    private Long calPlusTimeDifferenceValue(String timeDifferenceValue) {
        String[] timeDifferenceValueSplitArray = timeDifferenceValue.split("\\+|分|秒");
        Integer timeDifferenceValueMinute = Integer.parseInt(timeDifferenceValueSplitArray[1]); // 时差值拆分出的分
        Integer timeDifferenceValueSecond = Integer.parseInt(timeDifferenceValueSplitArray[2]); // 时差值拆分出的秒
        /* 将真平太阳时时差转为毫秒数， */
        trueMeanTimeDifferenceMilliSeconds = (long) timeDifferenceValueMinute * 60 * 10000
                + timeDifferenceValueSecond * 1000;
        return trueMeanTimeDifferenceMilliSeconds;
    }

    /**
     * 当时差为负时，拆分负号、分 、秒
     *
     * @param timeDifferenceValue
     * @return
     */
    private Long calMinusTimeDifferenceValue(String timeDifferenceValue) {
        String[] timeDifferenceValueSplitArray = timeDifferenceValue.split("-|分|秒");
        Integer timeDifferenceValueMinute = Integer.parseInt(timeDifferenceValueSplitArray[1]); // 时差值拆分出的分
        Integer timeDifferenceValueSecond = Integer.parseInt(timeDifferenceValueSplitArray[2]); // 时差值拆分出的秒
        /* 将真平太阳时时差转为毫秒数，加上负号 */
        trueMeanTimeDifferenceMilliSeconds = (long) (-1) * (timeDifferenceValueMinute * 60 * 10000
                + timeDifferenceValueSecond * 1000);
        return trueMeanTimeDifferenceMilliSeconds;
    }

    /**
     * 计算真太阳时的毫秒数
     *
     * @param meanSolarTimeMillSeconds
     * @return
     */
    private Long calTrueSolarTimeMillSeconds(Long meanSolarTimeMillSeconds) {
        return trueMeanTimeDifferenceMilliSeconds + meanSolarTimeMillSeconds;
    }
}