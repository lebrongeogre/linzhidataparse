package com.cuit.mete.jobs.grib.omijob;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.ConstantUtil;
import com.cuit.mete.jobs.grib.sta.job.RasterCalSave;
import com.cuit.mete.jobs.grib.sta.utils.imp.YearMonthFileUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.Map;

public class OMIYearMonthRasterStatisticsProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(OMIYearMonthRasterStatisticsProcess.class);
    private Map<String, Object> config;
    private RasterCalSave rasterCalSave;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }

    public void startJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (jobExecutionContext != null) {
            //这是从上下文件获取从jobService中传过来的配置
            config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");
        } else {
            config = (Map<String, Object>) ConstantUtil.get("OMIYearMonthRasterStatisticsProcess");
        }
        rasterCalSave = new RasterCalSave(new YearMonthFileUtils());
        Map<String, Object> uviStaMap = (Map<String, Object>) config.get("UVI_RASTER");
        //获取配置文件中文件根目录
        String allFilesPath = (String) uviStaMap.get("rasterFiles");
        //从配置文件中读取过滤条件
        List<String> monthFilter = (List<String>) (uviStaMap.get("filterMonth"));
        List<String> yearFilter = (List<String>) (uviStaMap.get("filterYear"));
        //从配置文件中读取文件保存路径
        String outFilePath = (String) uviStaMap.get("outFilePath");
        //按月份过滤文件列表，依次计算均值
        for (String yearIndex : yearFilter) {
            for (String monthIndex : monthFilter) {
                //按月份过滤文件列表
                List<String> pathFiltered = null;
                try {
                    //统计每年月均值
                    pathFiltered = rasterCalSave.getFileUtil().fileFilter(allFilesPath, yearIndex+monthIndex);
                } catch (Exception e) {
                    logger.debug("文件列表过滤失败！！");
                }
                //根据文件名列表统计栅格并生成统计结果文件
                logger.info("开始处理：["+yearIndex+monthIndex+"]uvi文件，共有["+pathFiltered.size()+"]个文件......");
                try{
                    String fileName = yearIndex+monthIndex;
                    rasterCalSave.calSaveRaster(pathFiltered,outFilePath,fileName);
                }catch (Exception e){
                    logger.info("文件组["+yearIndex+monthIndex+"]处理失败！");
                    e.printStackTrace();
                    continue;
                }
                logger.info("文件组["+yearIndex+monthIndex+"]处理成功.......");
            }
        }


    }


}
