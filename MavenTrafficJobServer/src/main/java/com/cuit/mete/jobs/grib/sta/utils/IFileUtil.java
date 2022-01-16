package com.cuit.mete.jobs.grib.sta.utils;

import java.util.List;

public interface IFileUtil {

    /**
     * 根据传入路径及条件获取当前路径下所有满足过滤条件的文件
     *
     * @param filePath
     * @return
     */
     List<String> fileFilter(String filePath, String filter);


    /**
     * 根据传入路径获取当前路径下所有文件
     *
     * @param filePath
     * @return
     */
     List<String> getFileNameList(String filePath);


}
