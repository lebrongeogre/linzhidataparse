package com.cuit.linzhi.utils;

import java.util.regex.Pattern;

public class StringUtil {


    /**
     * 判断字符串是否支持转换为数字
     * 正则表达式
     *
     * @param string 字符串
     * @return
     */
    public static boolean isNumber(String string) {
        if (string == null)
            return false;
        Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
        return pattern.matcher(string).matches();
    }

}
