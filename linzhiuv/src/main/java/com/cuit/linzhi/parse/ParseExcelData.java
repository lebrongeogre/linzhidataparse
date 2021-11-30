package com.cuit.linzhi.parse;


import com.cuit.linzhi.utils.CalculatorUtil;
import com.cuit.linzhi.utils.ExcelReadUtil;
import com.cuit.linzhi.utils.FormatUtil;
import com.cuit.linzhi.utils.StringUtil;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class ParseExcelData {
    private static final Logger logger = Logger.getLogger(ParseExcelData.class);

    /**
     * 从excel文件中获取数据库表对象（StatisticsMonthAccumulationBody）
     * @param fileList 文件绝对路径列表
     * @param configMap excel文件配置
     * @return
     */
    public List<StatisticsMonthAccumulationBody> parseRadiationExcelData(LinkedList<String> fileList, Map<String, Map<String, String>> configMap) {


        /**
         * 1、新建StatisticsMonthAccumulationBody list
         * 2、遍历文件名列表
         *      （1）截取文件名中的站号、日期部分，处理后给StatisticsMonthAccumulationBody对象字段赋值
         *      （2）获取终止行号
         *      （2）将文件名、行列号map传递给ExcelReadUtil类进行处理，一次获得一个StatisticsMonthAccumulationBody类
         *      （3）将此类添加到StatisticsMonthAccumulationBody类列表里
         * 3、返回集合
         */
        List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = new ArrayList<>();
        Map<String, String> codeMap = configMap.get("codeMap");
        Map<String, String> rowColMap = configMap.get("rowColMap");
        StatisticsMonthAccumulationBody statisticsMonthAccumulationBody = null;

        for (String filePath :fileList) {

            logger.info("开始处理[" + filePath +"]文件......");
            File tempFle = new File(filePath);
            //获取目标对象字段名-值map
            Map<String, String> fieldMap = getStationFromFileName(tempFle.getName(),codeMap);
            try {
                String str = getDayCountByMonth(fieldMap.get("year"),fieldMap.get("month"));
                rowColMap.put("endRow",getDayCountByMonth(fieldMap.get("year"),fieldMap.get("month")));
            }catch (Exception e){
                logger.info("月份获取错误！");
            }
            try {
                Map<Integer, List<String>> excelContents = ExcelReadUtil.readContentByTerm(filePath,Integer.parseInt(rowColMap.get("startRow")),
                        Integer.parseInt(rowColMap.get("endRow")),Integer.parseInt(rowColMap.get("startCol")),
                        Integer.parseInt(rowColMap.get("endCol")));

                List<String> midMoonList = excelContents.get(Integer.parseInt(rowColMap.get("startCol")));
                List<String> dayAccumList = excelContents.get(Integer.parseInt(rowColMap.get("endCol")));

                statisticsMonthAccumulationBody = createBeanBody(fieldMap.get("station"),fieldMap.get("year"),fieldMap.get("month"),midMoonList,dayAccumList);
                statisticsMonthAccumulationBodies.add(statisticsMonthAccumulationBody);
                logger.info("文件[" + filePath +"]处理完毕，当前共有" +statisticsMonthAccumulationBodies.size() +"个目标对象......");
            }catch (Exception e){
                logger.info("Excel 文件读取错误！") ;
                e.printStackTrace();
            }

        }
        logger.info("文件全部处理完成，共" +statisticsMonthAccumulationBodies.size() +"个目标对象......");
        return statisticsMonthAccumulationBodies;
    }

    /**
     * 根据参数生成目标对象
     * @param station
     * @param year
     * @param month
     * @param midMoonList
     * @param dayAccumList
     * @return
     */
    private StatisticsMonthAccumulationBody createBeanBody(String station, String year, String month, List<String> midMoonList, List<String> dayAccumList) {

        StatisticsMonthAccumulationBody statisticsMonthAccumulationBody = new StatisticsMonthAccumulationBody();
        //设置年月站点字段
        statisticsMonthAccumulationBody.setStation(station);
        statisticsMonthAccumulationBody.setMonth(Integer.parseInt(month));
        statisticsMonthAccumulationBody.setYear(Integer.parseInt(year));
        //处理两个列表的参数值
        Map<String,List<Double>> doubleData = getDateField(midMoonList,dayAccumList);
        //处理并统计midList和monthAccumList累计值

        Double midAccum = CalculatorUtil.calMeanForDoubleList(doubleData.get("midData"));
        Double monthAccum = CalculatorUtil.calMeanForDoubleList(doubleData.get("dayData"));
        //设置mid和monthAccum字段
        statisticsMonthAccumulationBody.setMidmoonValue(midAccum);
        statisticsMonthAccumulationBody.setMonthValue(monthAccum);
        //返回对象
        return statisticsMonthAccumulationBody;
    }

    /**
     * 转换两个列表里字段的单位
     * @param midMoonList
     * @param dayAccumList
     * @return
     */
    private Map<String,List<Double>> getDateField(List<String> midMoonList, List<String> dayAccumList) {

        Map<String,List<Double>> doubleData = new HashMap<>();
        doubleData.put("midData",parseDataUnit(midMoonList));
        doubleData.put("dayData",parseDataUnit(dayAccumList));

        return doubleData;
    }

    /**
     * 转换单位
     * @param dataList
     * @return
     */
    private List<Double> parseDataUnit(List<String> dataList) {
        List<Double> doubleDataList = new ArrayList<>();
        if (dataList != null && dataList.size() > 0){
            for (String data :dataList) {
                //如果当前字符串可以转换为数字，则计算，不能转换则continue
                if (StringUtil.isNumber(data)){
                    double doubleData = Double.parseDouble(data) / 100 * 277.78;
                    //保留两位小数
                    doubleDataList.add(FormatUtil.reshapeDecimalPlaces(doubleData,2));
                }
            }
        }
        return doubleDataList;
    }

    /**
     * 获取当前月份的天数
     * @param year
     * @param month
     * @return
     */
    private String getDayCountByMonth(String year, String month) {
        switch (month) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                return "31";
            case "04":
            case "06":
            case "09":
            case "11":
                return "30";
            case "02":
                return judgeYear(year)?"29":"28";
        }
        return null;
    }

    /**
     * 判断是否为闰年
     * @param year
     * @return
     */
    private boolean judgeYear(String year) {
        int yearInt = Integer.parseInt(year);
        if(yearInt % 4 == 0 && yearInt % 100!=0){
            return true;
        }else if(yearInt % 400 == 0){
            return true;
        }
        return false;
    }

    /**
     * 获取站点、年、月名字符串
     * @param name 文件名字符串
     * @param codeMap 站点编号-站点名map
     * @return
     */
    private Map<String,String> getStationFromFileName(String name, Map<String, String> codeMap) {
        Map<String,String> fieldMap = new HashMap<>();

        fieldMap.put("station",codeMap.get(name.split("-")[0]));
        String date = name.split("-")[1];
        fieldMap.put("year",date.substring(0,4));
        fieldMap.put("month",date.substring(4,6));

        return fieldMap;
    }
}
