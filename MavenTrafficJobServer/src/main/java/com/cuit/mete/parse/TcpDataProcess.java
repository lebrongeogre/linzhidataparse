package com.cuit.mete.parse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Map;

public abstract class TcpDataProcess {
    private static final Logger logger = Logger.getLogger(TcpDataProcess.class);
    /**
     * 处理内容（数据）， 如Json串等
     */
    protected String  content;
    /**
     * 文件类型（数据类型）
     */
    protected String dataType;

    protected Map<String, Object> outConfig = null;

    public TcpDataProcess(String content, String dataType, Map<String, Object> outConfig) {
        this.content = content;
        this.dataType = dataType;
        this.outConfig = outConfig;
    }

    /**
     * 执行处理数据
     * @return 返回处理的条件 -1为失败
     */
    public abstract int execute();


    protected JSONArray conentToJson() {
        JSONObject obj = null;
        JSONArray arr = null;
        try {
            if (content != null) {
                obj = JSONObject.parseObject(content);
            }

            if (obj != null) {
                arr = obj.getJSONArray("DS");
            }
        }catch (Exception e) {
            logger.error("文本转Json时出错:" +  content);
        }

        return arr;
    }


    /**
     * 根据数据观测时间，计算存储数据的观测时间 北京时与UTC
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param mi
     * @param dataUTCFlag 数据观测时间UTC？
     * @param outUTCFlag  输出时间UTC true / false
     * @return
     */
    protected Calendar getOutDataTime(int year, int month, int day, int hour, int mi, boolean dataUTCFlag, boolean outUTCFlag) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, mi);

        if (dataUTCFlag) {
            if (outUTCFlag) {
                //保持原来时间
            } else {
                //转化为北京时
                calendar.add(Calendar.HOUR_OF_DAY, 8);
            }
        } else {
            if (outUTCFlag) {
                //转化为UTC时间
                calendar.add(Calendar.HOUR_OF_DAY, -8);
            } else {
                //保持原来时间
            }
        }
        return calendar;
    }

    protected Calendar getOutDataTime(Calendar calendar, boolean dataUTCFlag, boolean outUTCFlag) {
        if (dataUTCFlag) {
            if (outUTCFlag) {
                //保持原来时间
            } else {
                //转化为北京时
                calendar.add(Calendar.HOUR_OF_DAY, 8);
            }
        } else {
            if (outUTCFlag) {
                //转化为UTC时间
                calendar.add(Calendar.HOUR_OF_DAY, -8);
            } else {
                //保持原来时间
            }
        }
        return calendar;
    }
}
