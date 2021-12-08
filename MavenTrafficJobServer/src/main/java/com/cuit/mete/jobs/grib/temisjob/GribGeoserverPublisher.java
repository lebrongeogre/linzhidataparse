package com.cuit.mete.jobs.grib.temisjob;

import com.cuit.job.utils.DateUtil;
import com.cuit.job.utils.FileSendRecord;
import com.cuit.job.utils.FileUtil;
import com.cuit.job.utils.metegrib2.DataGrid;
import com.cuit.job.utils.metegrib2.GridPoint;
import com.cuit.job.utils.metegrib2.NcUtil;
import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.DButils.Sequences;
import com.cuit.mete.DataSources.*;
import com.cuit.mete.Geometry.CoordinateTransformation;
import com.cuit.mete.Geometry.Envelope;
import com.cuit.mete.Geometry.Point;
import com.cuit.mete.Geometry.SpatialReference;
import com.cuit.mete.geoserver.GeoServerConfig;
import com.cuit.mete.geoserver.GeoServerPublishUtil;
import com.cuit.mete.jobs.grib.db.ModelGeoServer;
import com.cuit.mete.jobs.grib.domain.ModelGeoServerDao;
import com.cuit.mete.jobs.grib.temisjob.utils.NcUtilTemisYear;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Grib2GeoServer发布
 */
public class GribGeoserverPublisher {
    private static final Logger logger = Logger.getLogger(GribGeoserverPublisher.class);

    public boolean DealGribFile2GeoServer(String filename, byte[] grib2data, Map<String, Object> config) {
        boolean retFlag = true;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        logger.debug("开始读取处理" + filename + "文件的配置文件......");
        String temppath = (String) config.get("temppath");
        if (StringUtils.isEmpty(temppath)) {
            temppath = "\\temptfidata";
        }
        if (!FileUtil.isExist(temppath)) {
            FileUtil.mkDir(temppath);
        }

        String times = (String) config.get("times");
        String element = (String) config.get("element");
        String datepos = (String) config.get("datepos");
        String elementname = (String) config.get("elementname");
        String Datatype = (String) config.get("Datatype");

        Map<String, Object> geoserverconfig = (Map<String, Object>) config.get("geoserverconfig");
        Map<String, Object> gridsetsconfig = (Map<String, Object>) config.get("gridsets");
        Map<String, Object> warnconfig = (Map<String, Object>) config.get("warn");

        int idatepos = Integer.parseInt(datepos);
        FileSendRecord fileSendRecord = new FileSendRecord();
        String sd = filename.substring(idatepos, idatepos + 10);
        //YYYYMMDDHH
        Date dataDate = DateUtil.parseDateTimeSecond(sd + "0000");
        if (dataDate != null) {
            Calendar calData = Calendar.getInstance();
            calData.setTime(dataDate);
            //获取年份
            int year = calData.get(Calendar.YEAR);

            //没有处理过的数据
            if (!fileSendRecord.findSendFile(Datatype, year, filename)) {
                boolean retFileFlag = Byte2File(grib2data, temppath, filename);
                String pathfilename = temppath + "\\" + filename;

                if (retFileFlag) {
                    //先读元数据
                    logger.debug("开始读文件" + filename + "的元数据......");
                    DataGrid dataGrid = NcUtil.getSPHDNcDataMetaData(pathfilename, element);
                    if (dataGrid != null) {
                        //处理该文件
                        logger.debug("开始读文件" + filename + "的" + element + "数据......");
                        Map<String, List<GridPoint>> mapPoint = NcUtil.getSPHDNcData(pathfilename, element, times);
                        String[] timesList = times.split(",");
                        if (mapPoint != null & mapPoint.size() == timesList.length) {
                            logger.debug("开始发布" + filename + "及存储相关数据......");
                            retFlag = DealDataAndPuhlish(filename, Datatype, temppath, elementname, geoserverconfig, warnconfig, calData, dataGrid, mapPoint, timesList);
                            //所有数据都处理了后，该文件不再处理（一个文件有多个预报时次）
                            if (retFlag) {
                                fileSendRecord.addFile(Datatype, year, filename);
                            }
                        } else {
                            logger.error(filename + "读数据结果长度不正确！有些时次未读出！");
                            retFlag = false;
                        }
                    }
                } else {
                    logger.error(filename + "字节生成文件时出错" + temppath + "\\" + filename + "转换出错！");
                }
            } else {
                logger.info(filename + "文件已处理过，不再处理！");
            }
        } else {
            logger.error(filename + "文件的日期部分" + sd + "转换出错！");
            retFlag = false;
        }
        return retFlag;
    }

    public boolean DealGribFile2GeoServer(String pathfilename, Map<String, Object> config) {
        boolean retFlag = true;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());


        String temptifpath = (String) config.get("temppath");
        if (StringUtils.isEmpty(temptifpath)) {
            temptifpath = "\\temptfidata";
        }
        if (!FileUtil.isExist(temptifpath)) {
            FileUtil.mkDir(temptifpath);
        }
        Map<String, Object> dealFileConfig = config;

        String times = (String) config.get("times");
        String element = (String) config.get("element");
        String datepos = (String) config.get("datepos");
        String elementname = (String) config.get("elementname");
        String Datatype = (String) config.get("Datatype");

        Map<String, Object> geoserverconfig = (Map<String, Object>) dealFileConfig.get("geoserverconfig");
        Map<String, Object> gridsetsconfig = (Map<String, Object>) dealFileConfig.get("gridsets");
        Map<String, Object> warnconfig = (Map<String, Object>) dealFileConfig.get("warn");

        int idatepos = Integer.parseInt(datepos);
        FileSendRecord fileSendRecord = new FileSendRecord();
        String filename = FileUtil.getFileNameWithFullPath(pathfilename);
        String sd = filename.substring(idatepos, idatepos + 10);
        //YYYYMMDDHH
        Date dataDate = DateUtil.parseDateTimeSecond(sd + "0000");
        if (dataDate != null) {
            Calendar calData = Calendar.getInstance();
            calData.setTime(dataDate);
            //获取年份
            int year = calData.get(Calendar.YEAR);

            //没有处理过的数据
            if (!fileSendRecord.findSendFile(Datatype, year, filename)) {
                //表示有数据
                //先读元数据
                DataGrid dataGrid = NcUtil.getSPHDNcDataMetaData(pathfilename, element);
                if (dataGrid != null) {
                    //处理该文件
                    Map<String, List<GridPoint>> mapPoint = NcUtil.getSPHDNcData(pathfilename, element, times);
                    String[] timesList = times.split(",");
                    if (mapPoint != null & mapPoint.size() == timesList.length) {
                        retFlag = DealDataAndPuhlish(filename, Datatype, temptifpath, elementname, geoserverconfig, warnconfig, calData, dataGrid, mapPoint, timesList);
                        //所有数据都处理了后，该文件不再处理（一个文件有多个预报时次）
                        if (retFlag) {
                            fileSendRecord.addFile(Datatype, year, filename);
                        }
                    } else {
                        logger.error(filename + "读数据结果长度不正确！有些时次未读出！");
                        retFlag = false;
                    }
                }
            }
        } else {
            logger.error(filename + "文件的日期部分" + sd + "转换出错！");
            retFlag = false;
        }
        return retFlag;
    }

    public boolean DealGribFile2GeoServer(String temptifpath, String fileFlag, String times, String element, String elementname, String datatype, Map<String, Object> geoserverconfig, Map<String, Object> warnconfig, int idatepos, File file) {
        boolean retFlag = true;

        FileSendRecord fileSendRecord = new FileSendRecord();

        String sd = file.getName().substring(idatepos, idatepos + 10);
        //YYYYMMDDHH
        //Date dataDate = DateUtil.parseDateTimeSecond(sd + "0000");
        Date dataDate = new Date();
        if (dataDate != null) {
            Calendar calData = Calendar.getInstance();
            calData.setTime(dataDate);
            //获取年份
            int year = calData.get(Calendar.YEAR);

            //没有处理过的数据
            if (!fileSendRecord.findSendFile(fileFlag, year, file.getName())) {
                //表示有数据
                //先读元数据
                DataGrid dataGrid = NcUtilTemisYear.getSPHDNcDataMetaData(file.getAbsolutePath(), element);
                if (dataGrid != null) {
                    //处理该文件
                    //timeInterval用于存放时间索引  十个为一组
                    List<String> timeInterval = createTimeInterval(times);
                    //一次处理十个数据
                    for (String interval : timeInterval) {
                        Map<String, List<GridPoint>> mapPoint = NcUtilTemisYear.getSPHDNcData(file.getAbsolutePath(), element, interval);
                        String[] intervalList = interval.split(",");
                        if (mapPoint != null & mapPoint.size() == intervalList.length) {
                            retFlag = DealDataAndPuhlish(file.getAbsolutePath(), datatype, temptifpath, elementname, geoserverconfig, warnconfig, calData, dataGrid, mapPoint, intervalList);
                            //所有数据都处理了后，该文件不再处理（一个文件有多个预报时次）
                            if (retFlag) {
                                fileSendRecord.addFile(fileFlag, year, file.getName());
                            }
                        } else {
                            logger.error(file.getName() + "读数据结果长度不正确！有些时次未读出！");
                            retFlag = false;
                        }
                    }
                }
            }
        } else {
            logger.error(file.getName() + "文件的日期部分" + sd + "转换出错！");
            retFlag = false;
        }
        return retFlag;
    }

    /**
     * 根据传入字符串获取时间间隔数组
     *
     * @param times
     * @return
     */
    private List<String> createTimeInterval(String times) {

        List<String> timeInterval = new ArrayList<>();
        int interval = 10;
        String[] timeSplit = times.split(",");
        for (int timeIndex = 0; timeIndex < timeSplit.length; ) {
            String[] tempTime;
            if (timeSplit.length - timeIndex < interval) {
                tempTime = new String[timeSplit.length - timeIndex];
            } else {
                tempTime = new String[interval];
            }
            for (int j = 0; j < tempTime.length; j++) {
                tempTime[j] = timeSplit[timeIndex + j];
            }
            timeInterval.add(parseArray(tempTime));
            timeIndex += interval;
        }
        return timeInterval;
    }

    private String parseArray(String[] tempTime) {

        String str = "";

        for (int i = 0; i < tempTime.length; i++) {
            if (i == tempTime.length - 1) {
                str = str + tempTime[i];
                break;
            }
            str = str + tempTime[i] + ",";
        }
        return str;
    }

    private boolean DealDataAndPuhlish(String filename, String datatype, String temptifpath, String elementname, Map<String, Object> geoserverconfig, Map<String, Object> warnconfig, Calendar calData, DataGrid dataGrid, Map<String, List<GridPoint>> mapPoint, String[] timesList) {
        boolean retFlag = true;
        for (int k = 0; k < timesList.length; k++) {
            String curtime = timesList[k];

            List<GridPoint> data = mapPoint.get(curtime);
            logger.debug("开始生成第[" + curtime + "]天数据，并开始生成tiff........");
            boolean flag = true;
            float[] dataFloat = new float[dataGrid.getRows() * dataGrid.getCols()];
            int pos = 0;
            for (int r = dataGrid.getRows() - 1; r >= 0; r--) {
                for (int c = 0; c < dataGrid.getCols(); c++) {
                    int p = r * dataGrid.getCols() + c;
                    if (p >= data.size()) {
                        logger.error(filename + "取的数据个数不正确！");
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
                    //发布并保存数据
                    retFlag = PublishTifGeoserver(temptifpath, filename, calData, dataGrid, curtime, dataFloat);

            } else {
                logger.error(filename + "取的数据个数不正确！");
            }
        }
        return retFlag;
    }

    private boolean PublishTifGeoserver(String temptifpath, String fileName, Calendar calData, DataGrid dataGrid, String curtime, float[] dataFloat) {
        boolean retFlag = true;
        long startTime;
        long endTime;//生成文件
        String sd = calData.get(Calendar.YEAR) + "-" + (calData.get(Calendar.MONTH) + 1) + "-" + calData.get(Calendar.DAY_OF_MONTH) + " " + calData.get(Calendar.HOUR_OF_DAY) + ":00:00";
        String tempath = temptifpath;
        String layername = FileUtil.getFileNameWithoutExt(new File(temptifpath + "\\" + fileName)) + "_" + curtime;
        String outtiffile = layername + ".tif";

        //获取开始时间
        startTime = System.currentTimeMillis();
        try{
            transArrar2Tif(dataFloat, new Point(dataGrid.getLon1(), dataGrid.getLat2()), dataGrid.getxCellSize(), dataGrid.getyCellSize(), dataGrid.getCols(), dataGrid.getRows(), tempath, outtiffile);
        }catch(Exception e){
            logger.error("转换tif文件时出错！");
            retFlag = false;
        }
        endTime = System.currentTimeMillis();
        logger.info("读取并处理GRIB2：" + fileName + "，生成TIF文件, 耗时：" + ((endTime - startTime) / 1000 + "s"));
        return retFlag;
    }

    /**
     * 存储发布信息
     *
     * @param elementname
     * @param datatype
     * @param sd
     * @param calData
     * @param curtime
     * @param tempath
     * @param layername
     * @param outtiffile
     * @param geoServerConfig
     * @return
     */
    private boolean saveGeoServerMsg2DB(String elementname, String datatype, String sd, Calendar calData, String curtime, String tempath, String layername, String outtiffile, GeoServerConfig geoServerConfig) {
        //存记录
        Sequences sequences = new Sequences("GeoServerPubid");
        Long geoserverDbId = sequences.nextVal();
        ModelGeoServer modelGeoServer = new ModelGeoServer();
        modelGeoServer.setDatatype(datatype);
        modelGeoServer.setName(elementname);

        int pre = Integer.parseInt(curtime);

        modelGeoServer.setId(geoserverDbId);
        modelGeoServer.setGeoserverworkspaces(geoServerConfig.getWorkspaces());
        modelGeoServer.setGeoserverstyles(geoServerConfig.getStyles());
        modelGeoServer.setGeoserverlayers(layername);
        modelGeoServer.setDataclass("栅格");
        modelGeoServer.setPublishdate(calData.get(Calendar.YEAR) + "-" + (calData.get(Calendar.MONTH) + 1) + "-" + calData.get(Calendar.DAY_OF_MONTH) + " " + calData.get(Calendar.HOUR_OF_DAY) + ":00:00");
        modelGeoServer.setFilesource(tempath + File.separator + outtiffile);
        modelGeoServer.setSrs("EPSG:3857");
        modelGeoServer.setDownLoadFile(outtiffile);

        modelGeoServer.setPretime(pre);
        Calendar calData2 = Calendar.getInstance();
        calData2.setTime(calData.getTime());

        calData2.add(Calendar.HOUR, pre);
        modelGeoServer.setPredatetime(calData2.get(Calendar.YEAR) + "-" + (calData2.get(Calendar.MONTH) + 1) + "-" + calData2.get(Calendar.DAY_OF_MONTH) + " " + calData2.get(Calendar.HOUR_OF_DAY) + ":00:00");

        if (pre > 0) {
            modelGeoServer.setDatadesc(DateUtil.formatDateMM(calData2.getTime()) + "（" + DateUtil.formatDateMM(calData.getTime()) + "时发布）" + elementname);
        } else {
            modelGeoServer.setDatadesc(sd + elementname);
        }

        //保存入数据库
        ModelGeoServerDao modelGeoServerDao = new ModelGeoServerDao();
        return modelGeoServerDao.insertData(modelGeoServer);
    }


    private boolean saveGeoServerMsg2DB(String elementname, String datatype, Calendar calData, String curtime, String tempath, String layername, String outtiffile, GeoServerConfig geoServerConfig) {
        String sd = "";   //日期部分
        //存记录
        Sequences sequences = new Sequences("GeoServerPubid");
        Long geoserverDbId = sequences.nextVal();
        ModelGeoServer modelGeoServer = new ModelGeoServer();
        modelGeoServer.setDatatype(datatype);
        modelGeoServer.setName(elementname);

        int pre = Integer.parseInt(curtime);

        modelGeoServer.setId(geoserverDbId);
        modelGeoServer.setGeoserverworkspaces(geoServerConfig.getWorkspaces());
        modelGeoServer.setGeoserverstyles(geoServerConfig.getStyles());
        modelGeoServer.setGeoserverlayers(layername);
        modelGeoServer.setDataclass("栅格");
        sd = calData.get(Calendar.YEAR) + "-" + (calData.get(Calendar.MONTH) + 1) + "-" + calData.get(Calendar.DAY_OF_MONTH) + " " + calData.get(Calendar.HOUR_OF_DAY) + ":00:00";
        modelGeoServer.setPublishdate(sd);
        modelGeoServer.setFilesource(tempath + File.separator + outtiffile);
        modelGeoServer.setSrs("EPSG:3857");
        modelGeoServer.setDownLoadFile(outtiffile);

        modelGeoServer.setPretime(pre);
        Calendar calData2 = Calendar.getInstance();
        calData2.setTime(calData.getTime());

        calData2.add(Calendar.HOUR, pre);
        modelGeoServer.setPredatetime(calData2.get(Calendar.YEAR) + "-" + (calData2.get(Calendar.MONTH) + 1) + "-" + calData2.get(Calendar.DAY_OF_MONTH) + " " + calData2.get(Calendar.HOUR_OF_DAY) + ":00:00");

        if (pre > 0) {
            modelGeoServer.setDatadesc(DateUtil.formatDateMM(calData2.getTime()) + "（" + DateUtil.formatDateMM(calData.getTime()) + "时发布）" + elementname);
        } else {
            modelGeoServer.setDatadesc(sd + elementname);
        }

        //保存入数据库
        ModelGeoServerDao modelGeoServerDao = new ModelGeoServerDao();
        return modelGeoServerDao.insertData(modelGeoServer);
    }

    /**
     * 转换数组到tif文件 （浮点）
     *
     * @param data
     * @param leftTop
     * @param xCellSize
     * @param yCellSize
     * @param cols
     * @param rows
     * @param path
     * @param tifFileName
     * @return
     */
    public boolean transArrar2Tif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName) {
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
        return retFlag;
    }

    /**
     * 转换数组到tif文件 （整型）
     *
     * @param data
     * @param leftTop
     * @param xCellSize
     * @param yCellSize
     * @param cols
     * @param rows
     * @param path
     * @param tifFileName
     * @return
     */
    public boolean transArrar2IntTif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName) {
        boolean retFlag = true;
        //先启动许可，引入本地gdal-data
        LicenseEngine licenseEngine = new LicenseEngine();
        licenseEngine.StartUsing();
        GeneralRasterDataset newdataset2 = null;
        MemRasterDataset newdataset = null;
        try {
            SpatialReference psp = new SpatialReference("EPSG:4610");
            MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();

            MemRasterWorkspace work = pFac.CreateWorkspace();
            newdataset = work.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtFloat32, -32768, psp);
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
            newdataset2 = work2.CreateRasterDataset(tifFileName, leftTop2, cellSize, cellSize, cols2, rows2, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
            newband2 = newdataset2.getRasterBand(0);
            newband2.SaveBlockData(0, 0, cols2, rows2, data2);
            logger.debug("栅格文件生成成功，保存位置:" + path + ",文件名称：" + tifFileName);
        } catch (Exception e) {
            logger.debug("栅格文件生成出错:" + e.getMessage());
            retFlag = false;
        } finally {
            if (newdataset2 != null) {
                newdataset2.Dispose();
            }
            if (newdataset != null) {
                newdataset.Dispose();
            }
        }
        return retFlag;
    }

    /**
     * 转换数组到tif文件 （整型）
     *
     * @param data
     * @param leftTop
     * @param xCellSize
     * @param yCellSize
     * @param cols
     * @param rows
     * @param path
     * @param tifFileName
     * @param psp
     * @return
     */
    public boolean transArrar2IntTif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName, SpatialReference psp) {
        boolean retFlag = true;
        //先启动许可，引入本地gdal-data
        LicenseEngine licenseEngine = new LicenseEngine();
        licenseEngine.StartUsing();

        GeneralRasterDataset newdataset2 = null;

        try {
            //SpatialReference pspto = new SpatialReference("EPSG:3857");
//        SpatialReference psp = new SpatialReference("EPSG:4610");
//        MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();
//
//        MemRasterWorkspace work = pFac.CreateWorkspace();
//        MemRasterDataset newdataset = work.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtInt16, -32768, psp);
//        RasterBand newband = newdataset.getRasterBand(0);
//        newband.SaveBlockData(0, 0, cols, rows, data);
//
//
//        Envelope env = newdataset.getExtent();
//        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
//        pTrans.BeginTransform();
//        env.Project(pTrans);
//
//        double cellSize = 10000;
//        double right = env.getRight();
//        double left = env.getLeft();
//        double top = env.getTop();
//        double bottom = env.getBottom();
//        int cols2 = (int) ((right-left) / cellSize);
//        int rows2 = (int) ((top-bottom) / cellSize);
//        int []data2 = new int[cols2 * rows2];
//        Point leftTop2 = new Point(env.getLeft(), env.getTop());
//        newband.GetBlockDataByCoord(leftTop2, cellSize, cols2, rows2, data2, pspto, -32768, true);
//        newdataset.Dispose();

            RasterBand newband2 = null;
            //SpatialReference pspto = new SpatialReference("EPSG:3857");
            GeneralRasterWorkspaceFactory pFac2 = new GeneralRasterWorkspaceFactory();
            GeneralRasterWorkspace work2 = pFac2.OpenWorkspace(path);
            newdataset2 = work2.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtInt16, RasterCreateFileType.rcftTiff, -32768, psp);
            newband2 = newdataset2.getRasterBand(0);
            newband2.SaveBlockData(0, 0, cols, rows, data);
            newdataset2.Dispose();

            logger.info("栅格文件生成成功，保存位置:" + path + ",文件名称：" + tifFileName);
        } catch (Exception e) {
            logger.debug("栅格文件生成出错:" + e.getMessage());
            retFlag = false;
        } finally {
            if (newdataset2 != null) {
                newdataset2.Dispose();
            }
        }
        return retFlag;
    }

    /**
     * 判断预警，发布geoServer, 并保存在数据库中
     *
     * @param datatype
     * @param warnconfig
     * @param dataData
     * @param dataGrid
     * @param dataFloat
     * @param fileName
     * @param curtime
     * @param tempath
     * @param geoServerConfig
     * @return
     */
    private boolean WarnMsg2DB(String datatype, Map<String, Object> warnconfig, Calendar dataData, DataGrid dataGrid, float[] dataFloat, String fileName, String curtime, String tempath, GeoServerConfig geoServerConfig) {
        boolean retFlag = true;
        boolean pubWarnFlag = false;
        long startTime;
        long endTime;

        //进行单要素预警判断,如果未定义，则不判断
        if (warnconfig != null) {
            List<String> WarnLevelList = (List<String>) warnconfig.get("WarnLevel");
            for (int m = 0; m < dataFloat.length; m++) {
                for (int l = 0; l < WarnLevelList.size(); l++) {
                    String slvl = WarnLevelList.get(l);
                    Map<String, Object> lvlConfig = (Map<String, Object>) warnconfig.get(slvl);
                    String svalue = (String) lvlConfig.get("value");
                    String sInterruptvalue = (String) lvlConfig.get("Interruptvalue");
                    String warnFlag = (String) lvlConfig.get("warn");
                    if (StringUtils.isEmpty(warnFlag)) {
                        warnFlag = "false";
                    }
                    int value = Integer.parseInt(svalue);
                    int Interruptvalue = Integer.parseInt(sInterruptvalue);
                    if (dataFloat[m] < Interruptvalue) {
                        dataFloat[m] = value;
                        if ("true".equalsIgnoreCase(warnFlag)) {
                            pubWarnFlag = true;
                        }
                        break;
                    }
                }
            }

            if (pubWarnFlag) {
                String warnOutTiffile = fileName + "_" + curtime + "_warn.tif";
                String warnlayername = fileName + "_" + curtime + "_warn";

                String warnstyles = (String) warnconfig.get("warnstyles");
                String warnelementname = (String) warnconfig.get("elementname");
                String warnDatatype = (String) warnconfig.get("Datatype");

                //获取开始时间
                startTime = System.currentTimeMillis();
                transArrar2IntTif(dataFloat, new Point(dataGrid.getLon1(), dataGrid.getLat2()), dataGrid.getxCellSize(), dataGrid.getyCellSize(), dataGrid.getCols(), dataGrid.getRows(), tempath, warnOutTiffile);
                //获取结束时间
                endTime = System.currentTimeMillis();
                logger.info("生成预警信息：" + fileName + "，生成TIF文件, 耗时：" + ((endTime - startTime) / 1000 + "s"));
                geoServerConfig.setLayer(warnlayername);
                geoServerConfig.setStyles(warnstyles);

                //发布 GeoServer
                try {
                    logger.info("正在发布GeoServer：" + tempath + "--------文件名称：" + warnOutTiffile);
                    //获取开始时间
                    startTime = System.currentTimeMillis();

                    boolean ret = GeoServerPublishUtil.publishGeoTIFF(geoServerConfig, tempath + File.separator + warnOutTiffile);
                    //获取结束时间
                    endTime = System.currentTimeMillis();
                    if (ret) {
                        logger.info("发布GeoServer：" + tempath + "--------文件名称：" + warnOutTiffile + "  成功！" + "  耗时：" + ((endTime - startTime) / 1000 + "s"));
                        boolean saveDBFlag = saveGeoServerMsg2DB(warnelementname, warnDatatype, dataData, curtime, tempath, warnlayername, warnOutTiffile, geoServerConfig);
                        if (saveDBFlag) {
                            logger.debug("保存" + datatype + "预敬信息GeoServer发布信息成功！");
                        } else {
                            logger.error("保存" + datatype + "预敬信息GeoServer发布信息失败！");
                            retFlag = false;
                        }
                    } else {
                        logger.error("发布" + datatype + "GeoServer预警数据" + warnOutTiffile + "时出错!");
                    }
                } catch (Exception e) {
                    logger.error("发布" + datatype + "GeoServer预警数据时，出错：" + e.getMessage(), e);
                    retFlag = false;
                }
            }
        }
        return retFlag;
    }

    private boolean Byte2File(byte[] bfile, String filePath, String fileName) {
        boolean retFlag = true;
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            logger.debug("由字节生成文件出错：" + e.getMessage());
            retFlag = false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    logger.debug("由字节生成文件出错(关闭文件）：" + e1.getMessage());
                    retFlag = false;
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    logger.debug("由字节生成文件出错(关闭文件）：" + e1.getMessage());
                    retFlag = false;
                }
            }
        }
        return retFlag;
    }
}
