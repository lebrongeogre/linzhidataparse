package com.cuit.linzhi.job.excel;

import com.cuit.linzhi.config.RadiationExcelReadConfig;


import com.cuit.linzhi.dao.StatisticsMonthAccumulationBodyMapper;
import com.cuit.linzhi.parse.ParseExcelData;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Component
public class ReadRadiationExcelProcess {
    private static final Logger logger = Logger.getLogger(ReadRadiationExcelProcess.class);

    @Resource
    private RadiationExcelReadConfig radiationExcelReadConfig;
    @Resource
    private ParseExcelData parseExcelData;
    @Resource
    StatisticsMonthAccumulationBodyMapper statisticsMonthAccumulationBodyMapper;
    /**
     * 1、按要求读取数据
     * 2、进行单位变幻处理
     * 3、生成数据库表对象
     * 4、入库
     */
    public int parseExcelDataToDB() {

        //从配置类中读取配置  文件路径、起始、终止行；起始、终止列
        List<String> filePath = radiationExcelReadConfig.getFilePath();
        Map<String, Map<String, String>> configMap = radiationExcelReadConfig.getConfigMap();
        List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = new ArrayList<>();

        //遍历文件路径列表，获取每一个文件路径下的文件名列表
        for (String filePathIndex : filePath) {
            //读取文件名列表
            LinkedList<String> fileList = new LinkedList<>();

            try {
                fileList = getFileNameListWithSuffix(filePathIndex, "XLS");
                logger.info("共读取到"+fileList.size() + "个数据文件");
            }catch (Exception e) {
                logger.info("路径读取错误！" + Arrays.toString(e.getStackTrace()));
                continue;
            }

            try {
                logger.info("------------开始处理文件集--------------");
                statisticsMonthAccumulationBodies = parseExcelData.parseRadiationExcelData(fileList,configMap);
                logger.info("------------开始入库--------------");
                try{
                    int ref = statisticsMonthAccumulationBodyMapper.insertBatch(statisticsMonthAccumulationBodies);
                        logger.info("入库结束，共插入"+statisticsMonthAccumulationBodies.size()+"条数据......");
                }catch(Exception e){
                    logger.info("入库失败......");
                    e.printStackTrace();
                }

            } catch (Exception e) {
                logger.info("路径读取错误！" + Arrays.toString(e.getStackTrace()));
            }
        }
        return 0;
    }

    /**
     * 根据传入文件路径获取该路径下的文件名列表
     * @param unParseFile
     * @return
     */
    private static LinkedList<String> getFileNameList(String unParseFile) {
        //获取路径  未处理文件目录
        File f = new File(unParseFile);
        LinkedList<String> files = new LinkedList<String>();
        try {
            File file = new File(unParseFile);
            File[] tempLists = file.listFiles();

            assert tempLists != null;
            for (File tempList : tempLists) {
                if (tempList.isFile()) {
                    files.add(tempList.toString());
                }
            }
            logger.debug("数据文件列表完成！");
        } catch (Exception e) {
            logger.error("数据文档文件列表失败！");
        }
        return files;
    }


    /**
     * 根据传入文件路径获取该路径下的文件名列表，过滤掉非兴趣文件
     * @param unParseFile
     * @return
     */
    private static LinkedList<String> getFileNameListWithSuffix(String unParseFile,String suffix) {
        //获取路径  未处理文件目录
        File f = new File(unParseFile);
        LinkedList<String> files = new LinkedList<String>();
        try {
            File file = new File(unParseFile);
            File[] tempLists = file.listFiles();

            assert tempLists != null;
            for (File tempList : tempLists) {
                String strName = tempList.getName();
                String str = strName.substring(strName.length()-3);
                if (tempList.isFile() && strName.substring(strName.length()-3).equalsIgnoreCase(suffix)) {
                    files.add(tempList.toString());
                }
            }
            logger.debug("数据文件列表读取完成！");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("数据文档文件列表失败！");
        }
        return files;
    }
}

