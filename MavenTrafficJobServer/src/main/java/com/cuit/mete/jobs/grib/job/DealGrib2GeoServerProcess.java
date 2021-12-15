package com.cuit.mete.jobs.grib.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.*;
import com.cuit.job.utils.metegrib2.DataGrid;
import com.cuit.job.utils.metegrib2.GridPoint;

import com.cuit.job.utils.metegrib2.NcUtil;
import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.DataSources.*;
import com.cuit.mete.Geometry.CoordinateTransformation;
import com.cuit.mete.Geometry.Envelope;
import com.cuit.mete.Geometry.Point;
import com.cuit.mete.Geometry.SpatialReference;


import com.cuit.mete.jobs.grib.job.util.NcUtilOMI;
import com.cuit.mete.jobs.grib.job.vo.NcBasicMeta;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DealGrib2GeoServerProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(DealGrib2GeoServerProcess.class);
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
        FileSendRecord fileSendRecord = new FileSendRecord();

        //获取配置文件中内容
        List<String> fileFlist = (List<String>) config.get("Files");
        Map<String,Object> typeConfig = (Map<String, Object>) config.get(fileFlist.get(0));

        String temptifpath = (String) typeConfig.get("temptifpath");
        if (StringUtils.isEmpty(temptifpath)) {
            temptifpath = "\\temptfidata";
        }
        if (!FileUtil.isExist(temptifpath)) {
            FileUtil.mkDir(temptifpath);
        }


        if (fileFlist != null && fileFlist.size() > 0) {
            for (int i = 0; i < fileFlist.size(); i++) {
                String fileFlag = fileFlist.get(i);

                Map<String, Object> dealFileConfig = (Map<String, Object>) config.get(fileFlag);
                if (dealFileConfig != null) {
                    String filePath = (String) dealFileConfig.get("path");
                    String fileNamePattern = (String) dealFileConfig.get("fileNamePattern");
                    String times = (String) dealFileConfig.get("times");
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
                    logger.debug("文件列表读取完成，共有" + allFiles.length + "个文件......");
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
                            logger.debug("==============================开始处理文件[" + file.getName() + "]==============================");
                            String sd = file.getName().substring(idatepos, idatepos + 9).replaceAll("[^0-9]", "");
                            //YYYYMMDD
                            Date dataDate = DateUtil.parseDateTimeSecond(sd + "000000");
                            logger.debug("当前日期为" + sd + "......");
                            //Date dataDate = new Date();
                            if (dataDate != null) {
                                Calendar calData = Calendar.getInstance();
                                calData.setTime(dataDate);
                                //获取年份
                                int year = calData.get(Calendar.YEAR);

                                //没有处理过的数据
                                if (!fileSendRecord.findSendFile(fileFlag, year, file.getName())) {
                                    //表示有数据
                                    //先读元数据
                                    NcBasicMeta ncBasicMeta = NcUtilOMI.getSPHDNcDataMetaData(file.getAbsolutePath(), element);
                                    //NcBasicMeta ncBasicMeta = NcUtilTemis.getSPHDNcDataMetaData(file.getAbsolutePath(), element);
                                    logger.debug("nc文件读取完成，准备写tiff文件......");
                                    DataGrid dataGrid = ncBasicMeta.getDataGrid();
                                    if (dataGrid != null) {
                                        //处理该文件
                                        if (dataGrid != null) {
                                            List<GridPoint> data = ncBasicMeta.getGridPoints();
                                            boolean flag = true;
                                            float[] dataFloat = new float[dataGrid.getRows() * dataGrid.getCols()];
                                            int pos = 0;
                                            for (int r = dataGrid.getRows() - 1; r >= 0; r--) {
                                                for (int c = 0; c < dataGrid.getCols(); c++) {
                                                    int p = r * dataGrid.getCols() + c;
                                                    if (p >= data.size()) {
                                                        logger.error(file.getAbsolutePath() + "取的数据个数不正确！");
                                                        flag = false;
                                                        break;
                                                    }
                                                    dataFloat[pos] = (float) data.get(p).value;
                                                    pos++;
                                                }
                                                if (!flag) {
                                                    break;
                                                }
                                            }
                                            if (flag) {
                                                //生成文件
                                                String tempath = temptifpath;
                                                String layername = FileUtil.getFileNameWithoutExt(file) + "_" + "uvi";
                                                String outtiffile = sd + ".tif";
                                                //获取开始时间
                                                startTime = System.currentTimeMillis();
                                                transArrar2Tif(dataFloat, new Point(dataGrid.getLon1(), dataGrid.getLat2()), dataGrid.getxCellSize(), dataGrid.getyCellSize(), dataGrid.getCols(), dataGrid.getRows(), tempath, outtiffile);

                                                //获取结束时间
                                                endTime = System.currentTimeMillis();
                                                logger.info("读取并处理GRIB2：" + file.getAbsolutePath() + "，生成TIF文件, 耗时：" + ((endTime - startTime) / 1000 + "s"));
                                            }

                                            //所有数据都处理了后，该文件不在处理
                                            if (retFlag) {
                                                fileSendRecord.addFile(fileFlag, year, file.getName());
                                            }
                                        } else {
                                            logger.error(file.getName() + "读数据结果长度不正确！有些时次未读出！");
                                            retFlag = false;
                                        }
                                    }
                                }
                            } else {
                                logger.error(file.getName() + "文件的日期部分" + sd + "转换出错！");
                                retFlag = false;
                            }
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


    public void transArrar2Tif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName) {
        boolean retFlag = true;
        GeneralRasterDataset newdataset2 = null;
        MemRasterDataset newdataset = null;
        try {
            //先启动许可，引入本地gdal-data
            LicenseEngine licenseEngine = new LicenseEngine();
            licenseEngine.StartUsing();


            SpatialReference psp = new SpatialReference("EPSG:4326");
            MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();

            MemRasterWorkspace work = pFac.CreateWorkspace();
            newdataset = work.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtFloat32, -32768, psp);
            RasterBand newband = newdataset.getRasterBand(0);
            newband.SaveBlockData(0, 0, cols, rows, data);
            SpatialReference pspto = new SpatialReference("EPSG:4326");

            Envelope env = newdataset.getExtent();
            CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
            pTrans.BeginTransform();
            env.Project(pTrans);

            double cellSize = 0.25;
            double right = env.getRight();
            double left = env.getLeft();
            double top = env.getTop();
            double bottom = env.getBottom();
            int cols2 = (int) ((right - left) / cellSize);
            int rows2 = (int) ((top - bottom) / cellSize);
            float[] data2 = new float[cols2 * rows2];
            Point leftTop2 = new Point(env.getLeft(), env.getTop());
            newband.GetBlockDataByCoord(leftTop2, cellSize, cols2, rows2, data2, pspto, -32768, true);

            RasterBand newband2 = null;
            GeneralRasterWorkspaceFactory pFac2 = new GeneralRasterWorkspaceFactory();
            GeneralRasterWorkspace work2 = pFac2.OpenWorkspace(path);
            newdataset2 = work2.CreateRasterDataset(tifFileName, leftTop2, cellSize, cellSize, cols2, rows2, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
            newband2 = newdataset2.getRasterBand(0);
            newband2.SaveBlockData(0, 0, cols2, rows2, data2);
            logger.debug("栅格文件生成成功，保存位置:" + path + ",文件名称：" + tifFileName);
        } catch (Exception e) {
            logger.error("栅格文件生成出错:" + e.getMessage());
            retFlag = false;
        } finally {
            if (newdataset2 != null) {
                newdataset2.Dispose();
            }
            if (newdataset != null) {
                newdataset.Dispose();
            }
        }
    }

    public void transArrar2IntTif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName) {
        //先启动许可，引入本地gdal-data
        LicenseEngine licenseEngine = new LicenseEngine();
        licenseEngine.StartUsing();

        SpatialReference psp = new SpatialReference("EPSG:4326");
        MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();

        MemRasterWorkspace work = pFac.CreateWorkspace();
        MemRasterDataset newdataset = work.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtFloat32, -32768, psp);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols, rows, data);
        SpatialReference pspto = new SpatialReference("EPSG:3857");

        Envelope env = newdataset.getExtent();
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);

        double cellSize = 10000;
        double right = env.getRight();
        double left = env.getLeft();
        double top = env.getTop();
        double bottom = env.getBottom();
        int cols2 = (int) ((right - left) / cellSize);
        int rows2 = (int) ((top - bottom) / cellSize);
        float[] data2 = new float[cols2 * rows2];
        Point leftTop2 = new Point(env.getLeft(), env.getTop());
        newband.GetBlockDataByCoord(leftTop2, cellSize, cols2, rows2, data2, pspto, -32768, true);

        RasterBand newband2 = null;
        GeneralRasterWorkspaceFactory pFac2 = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work2 = pFac2.OpenWorkspace(path);
        GeneralRasterDataset newdataset2 = work2.CreateRasterDataset(tifFileName, leftTop2, cellSize, cellSize, cols2, rows2, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
        newband2 = newdataset2.getRasterBand(0);
        newband2.SaveBlockData(0, 0, cols2, rows2, data2);
        newdataset2.Dispose();
        newdataset.Dispose();
        logger.info("栅格文件生成成功，保存位置:" + path + ",文件名称：" + tifFileName);
    }

}