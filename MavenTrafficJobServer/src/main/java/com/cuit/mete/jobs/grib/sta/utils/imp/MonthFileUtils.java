package com.cuit.mete.jobs.grib.sta.utils.imp;

import com.cuit.job.utils.DateUtil;
import com.cuit.mete.jobs.grib.sta.utils.IFileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthFileUtils implements IFileUtil {

    /**
     * 根据传入路径获取当前路径下所有文件
     *
     * @param filePath
     * @return
     */
    public List<String> getFileNameList(String filePath) {
        //获取路径  未处理文件目录
        File f = new File(filePath);
        List<String> files = new ArrayList<String>();
        try {
            File file = new File(filePath);
            File[] tempLists = file.listFiles();

            assert tempLists != null;
            for (File tempList : tempLists) {
                if (tempList.isFile()) {
                    files.add(tempList.toString());
                }
            }
        } catch (Exception e) {
        }
        return files;
    }


    /**
     * 根据传入路径及月份获取当前路径下所有满足过滤条件的文件
     *
     * @param filePath
     * @return
     */
    public List<String> fileFilter(String filePath, String filter) {
        //获取路径  未处理文件目录
        File file1 = new File(filePath);
        List<String> files = new ArrayList<>();
        try {
            File file = new File(filePath);
            File[] tempLists = file.listFiles();

            assert tempLists != null;
            for (File tempPath : tempLists) {
                String tempFilePath = tempPath.toString();
                String dateStr = numExtraction(tempFilePath);

                if (tempPath.isFile() && (dateStr.substring(4,6)).equals(filter)) {
                    files.add(tempPath.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    /**
     * 根据传入路径获取路径字符串中数字部分
     *
     * @param dateStr
     * @return
     */
    public String numExtraction(String dateStr) {
        String dest = "";
        if (dateStr != null) {
            dest = dateStr.replaceAll("[^0-9]", "");
        }
        return dest;
    }
}
