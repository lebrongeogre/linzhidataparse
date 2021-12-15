package com.cuit.mete.jobs.grib.temisjob;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.ConstantUtil;
import com.cuit.job.utils.FileSendRecord;
import com.cuit.job.utils.FileUtil;
import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.jobs.grib.job.BaseMethod;
import com.cuit.mete.jobs.grib.temisjob.utils.MyDateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.text.ParseException;
import java.util.*;

/**
 * 用于批量裁剪栅格、并输出文件
 */
public class ClipRasterProcess extends BaseJob {

    private static final Logger logger = Logger.getLogger(BaseMethod.class);
    public static BaseMethod baseMethod = new BaseMethod();
    private Map<String, Object> config;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }

    public void startJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        boolean retFlag = true;
        if (jobExecutionContext != null) {
            //这是从上下文件获取从jobService中传过来的配置
            config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");
        } else {
            config = (Map<String, Object>) ConstantUtil.get("ClipRasterProcess");
        }

        //获取开始时间
        long startTime = 0L;
        //获取结束时间
        long endTime = 0L;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        Map<String,String> uviClipMap = (Map<String, String>) config.get("UVI_INDEX_Clip");
        //获取配置文件中待裁剪栅格文件的路径
        String rasterFilePath = uviClipMap.get("rasterFiles");
        List<String> fileList = null;
        try{
            fileList = getFileNameList(rasterFilePath);
        }catch(Exception e){
            logger.info("文件列表读取失败......");
            return;
        }

        //获取配置文件中边界图层路径
        String slPath = uviClipMap.get("shiliang");
        //获取文件输出路径
        String outTifPath = uviClipMap.get("outFile");
        if (StringUtils.isEmpty(outTifPath)) {
            outTifPath = "\\temptfidata";
        }
        if (!FileUtil.isExist(outTifPath)) {
            FileUtil.mkDir(outTifPath);
        }

        //----------------------开始遍历栅格文件，并依次处理-------------------------
        if (fileList.size() != 0) {
            //表示不为空
            for (String tempFile : fileList) {
                //从文件中获取文件保存名称.0
                String dateStr = numExtraction(tempFile);
                //根据日期字符串获取日期
                try{
                    String fileName = getFileName(dateStr);
                    //先启动许可，引入本地gdal-data
                    LicenseEngine licenseEngine = new LicenseEngine();
                    licenseEngine.StartUsing();
                    logger.info("开始处理" + tempFile + "文件......");
                    int ref = baseMethod.clipRaster(slPath, tempFile, outTifPath,fileName);
                    logger.info("文件" + tempFile + "处理成功......");
                }catch(Exception e){
                    logger.info("文件处理失败......");
                }
            }
            logger.info("文件夹列表处理完成......");
        } else {
            logger.info("文件列表为空......");
        }

    }

    private String getFileName(String dateStr) {
        Date fileDate = null;
        try {
            fileDate = MyDateUtil.getDateOfYear(dateStr.substring(0, 4), dateStr.substring(4));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return MyDateUtil.formatDateToStr(fileDate, "yyyyMMdd");
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

    /**
     * 根据传入路径获取当前路径下所有文件
     * @param filePath
     * @return
     */
    private static LinkedList<String> getFileNameList(String filePath) {
        //获取路径  未处理文件目录
        File f = new File(filePath);
        LinkedList<String> files = new LinkedList<String>();
        try {
            File file = new File(filePath);
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

}
