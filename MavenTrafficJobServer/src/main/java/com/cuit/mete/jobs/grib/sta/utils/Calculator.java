package com.cuit.mete.jobs.grib.sta.utils;

import com.cuit.mete.jobs.grib.job.vo.GridPoint;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    public static float[] getRasterArray(List<Float[]> dataList) {

        int length = dataList.get(0).length;
        float[] data = new float[length];
        //表示栅格数据个点索引
        for (int index = 0;index < length;index++){
            List<Float> tempData = new ArrayList<>();
            for (Float[] dayArray : dataList){
                tempData.add(dayArray[index]);
            }
            data[index] = calMeanBy(tempData);
        }
        return data;
    }

    private static Float calMeanBy(List<Float> dataList) {

        double total = 0.0;
        double num = 0;
        for (Float data : dataList){
            if (data > 0){
                total += data;
                num ++;
            }
        }
        if (num > 0){
            return Float.valueOf(FormatUtil.reshapeDecimalPlaces( (total/num),8));
        }
        return new Float(0);
    }


}
