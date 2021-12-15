package com.cuit.linzhi.utils;

import com.cuit.linzhi.utils.pattern.BasicUtils;

import java.util.ArrayList;
import java.util.List;

public class CalculatorUtil extends BasicUtils {

    public CalculatorUtil(){
          utilFunction = new MeanCalculator();
    }

    //计算传入集合的均值
    public static Double calMeanForStrList(List<String> dataList) {
        try {
            List<Double> doubleData = transListToDouble(dataList);
            double mean = calMeanForDoubleList(doubleData);
            return mean;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Double calMeanForDoubleList(List<Double> dataList) {
        double total = 0.0;
        for (Double dataTerm :dataList) {
            total+=dataTerm;
        }
        return FormatUtil.reshapeDecimalPlaces(total/dataList.size(),2);
    }

    public static List<Double> transListToDouble(List<String> dataList) {
        List<Double> doubleData = new ArrayList<>();
        for (String dataTerm :dataList) {
            doubleData.add(Double.parseDouble(dataTerm));
        }
        return doubleData;
    }

}
