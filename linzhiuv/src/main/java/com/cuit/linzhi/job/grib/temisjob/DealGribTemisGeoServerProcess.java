package com.cuit.linzhi.job.grib.temisjob;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.ConstantUtil;
import com.cuit.job.utils.DateUtil;
import com.cuit.job.utils.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DealGribTemisGeoServerProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(DealGribTemisGeoServerProcess.class);
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
            config = (Map<String, Object>) ConstantUtil.get("DealGrib2GeoServerProcess");
        }

        //获取开始时间
        long startTime = 0L;
        //获取结束时间
        long endTime = 0L;


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        //获取配置文件中内容
        List<String> fileFlist = (List<String>) config.get("Files");

        String temptifpath = (String) config.get("temptifpath");
        if (StringUtils.isEmpty(temptifpath)) {
            temptifpath = "\\temptfidata";
        }
        if (!FileUtil.isExist(temptifpath)) {
            FileUtil.mkDir(temptifpath);
        }


        if (fileFlist != null && fileFlist.size() > 0) {
            GribGeoserverPublisher gribGeoserverPublisher = new GribGeoserverPublisher();
            for (int i = 0; i < fileFlist.size(); i++) {
                String fileFlag = fileFlist.get(i);
                Map<String, Object> dealFileConfig = (Map<String, Object>) config.get(fileFlag);
                if (dealFileConfig != null) {
                    String filePath = (String) dealFileConfig.get("path");
                    String fileNamePattern = (String) dealFileConfig.get("fileNamePattern");
                    String times = getTimes();

                    String element = (String) dealFileConfig.get("element");
                    String datepos = (String) dealFileConfig.get("datepos");
                    String elementname = (String) dealFileConfig.get("elementname");
                    String Datatype = (String) dealFileConfig.get("Datatype");

                    String Range = (String) dealFileConfig.get("Range");
                    if (StringUtils.isEmpty(Range)) {
                        Range = "LAST";
                    }
                    Map<String, Object> geoserverconfig = (Map<String, Object>) dealFileConfig.get("geoserverconfig");
                    Map<String, Object> gridsetsconfig = (Map<String, Object>) dealFileConfig.get("gridsets");
                    Map<String, Object> warnconfig = (Map<String, Object>) dealFileConfig.get("warn");

                    int idatepos = Integer.parseInt(datepos);

                    File[] allFiles = FileUtil.getAllFiles(new File(filePath), fileNamePattern);
                    if (allFiles != null && allFiles.length >= 1) {

                        File[] dealllFiles = null;

                        if (Range.equalsIgnoreCase("ALL")) {
                            dealllFiles = allFiles;
                        } else {
                            //智能网格预报只取最后一个文件
                            int maxindex = 0;
                            File fileMax = allFiles[0];
                            String sd1 = fileMax.getName().substring(idatepos, idatepos + 10);
                            //YYYYMMDDHH
                            Date dataDateMax = DateUtil.parseDateTimeSecond(sd1 + "0000");
                            Calendar calDataMax = null;
                            if (dataDateMax != null) {
                                calDataMax = Calendar.getInstance();
                                calDataMax.setTime(dataDateMax);
                            }

                            for (int j = 1; j < allFiles.length; j++) {
                                File file = allFiles[j];
                                String sdbj = file.getName().substring(idatepos, idatepos + 10);
                                //YYYYMMDDHH
                                Date dataDatebj = DateUtil.parseDateTimeSecond(sdbj + "0000");
                                if (dataDatebj != null) {
                                    Calendar calDatabj = Calendar.getInstance();
                                    calDatabj.setTime(dataDatebj);
                                    if (calDataMax.before(calDatabj)) {
                                        calDataMax.setTime(calDatabj.getTime());
                                        maxindex = j;
                                    }
                                }
                            }

                            dealllFiles = new File[1];
                            dealllFiles[0] = allFiles[maxindex];
                        }


                        for (int j = 0; j < dealllFiles.length; j++) {
                            File file = allFiles[j];
                            retFlag = gribGeoserverPublisher.DealGribFile2GeoServer(temptifpath, fileFlag, times, element, elementname, Datatype, geoserverconfig, warnconfig, idatepos, file);
                        }
                    } else {
                        logger.error(filePath + "目录未找到需要处理的文件！");
                        retFlag = false;
                    }
                } else {
                    logger.error(fileFlag + "配置未找到！");
                    retFlag = false;
                }
            }
        }
    }

    private String getTimes() {
        String times = "";

        for (int i = 1; i < 366; i++) {
            if (i < 365) {
                times += String.valueOf(i) + ",";
            } else {
                times += String.valueOf(i);
            }
        }
        return times;
    }
}