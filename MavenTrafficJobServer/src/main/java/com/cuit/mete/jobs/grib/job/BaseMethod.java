package com.cuit.mete.jobs.grib.job;


import com.cuit.mete.DataSources.*;
import com.cuit.mete.Geometry.*;
import com.cuit.mete.RasterUtils.ncPoint;
import com.cuit.mete.SA.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * 整理强哥封装好的方法
 */
public class BaseMethod {

    private static final Logger logger = Logger.getLogger(BaseMethod.class);

    public static void main(String[] args) {
        transformAndResample( "F:\\project_pyj\\bak\\ncjtwarnmodel_v2\\output.tif",  "F:\\project_pyj\\bak\\ncjtwarnmodel_v2",  "output_trans3857",  800, "EPSG:3857");
    }
    /**
     * 读写文件栅格
     * @param pathFrom 读取栅格文件绝对路径
     * @param pathTo   存储栅格文件路径
     * @param fileName 存储栅格文件名称
     */
    public static void readAndWriteRaster(String pathFrom, String pathTo, String fileName) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        int cols = band.getCols();
        int rows = band.getRows();
        float[] data = new float[cols * rows];
        band.GetBlockData(0, 0, band.getCols(), band.getRows(), cols, rows, data, true);
        GeneralRasterWorkspaceFactory pFac = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work = pFac.OpenWorkspace(pathTo);
        GeneralRasterDataset newdataset = work.CreateRasterDatasetEx(fileName, band, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols, rows, data);
        newdataset.Dispose();
    }
    /**
     * 读文件栅格,返回波段
     * @param pathFrom 读取栅格文件绝对路径
     */
    public RasterBand readRaster(String pathFrom) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        int cols = band.getCols();
        int rows = band.getRows();
        float[] data = new float[cols * rows];
        band.GetBlockData(0, 0, band.getCols(), band.getRows(), cols, rows, data, true);
        return band;
    }
    /**
     * 读文件栅格,返回数组
     * @param pathFrom 读取栅格文件绝对路径
     */
    public float[] readRasterData(String pathFrom) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        int cols = band.getCols();
        int rows = band.getRows();
        float[] data = new float[cols * rows];
        band.GetBlockData(0, 0, band.getCols(), band.getRows(), cols, rows, data, true);
        return data;
    }
    /**
     * 投影转换、重采样生成新栅格文件
     * @param pathFrom 读取栅格文件绝对路径
     * @param pathTo   存储栅格文件路径
     * @param fileName 存储栅格文件名称
     * @param cellSize 像元大小
     */
    public static void transformAndResample(String pathFrom, String pathTo, String fileName, double cellSize,String projcode) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        Envelope env = band.getExtent();
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference(projcode);
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        int cols = (int) (env.getWidth() / cellSize);
        int rows = (int) (env.getHeight() / cellSize);
        float[] data = new float[cols * rows];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols, rows, data, pspto, -32768, true);
        GeneralRasterWorkspaceFactory pFac = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work = pFac.OpenWorkspace(pathTo);
        GeneralRasterDataset newdataset = work.CreateRasterDataset(fileName, leftTop, cellSize, cellSize, cols, rows, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols, rows, data);
        newdataset.Dispose();
    }
    /**
     * 投影转换
     * @param pathFrom 读取栅格文件绝对路径
     * @param pathTo   存储栅格文件路径
     * @param fileName 存储栅格文件名称
     */
    public static void transformProj(String pathFrom, String pathTo, String fileName, String projcode) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        int cols1 = band.getCols();
        int rows1 = band.getRows();
        double nodata = band.getNoData();
        Envelope env = band.getExtent();
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference(projcode);
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        int cellSize = (int)((env.getRight()-env.getLeft())/rows1);
        float[] data = new float[cols1 * rows1];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols1, rows1, data, pspto, -32768, true);
        GeneralRasterWorkspaceFactory pFac = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work = pFac.OpenWorkspace(pathTo);
        GeneralRasterDataset newdataset = work.CreateRasterDataset(fileName, leftTop, cellSize, cellSize, cols1, rows1, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols1, rows1, data);
        newdataset.Dispose();
    }
    /**
     * 投影转换、重采样、范围与指定环境一致
     * @param pathFrom 读取栅格文件绝对路径
     * @param pathTo   存储栅格文件路径
     * @param fileName 存储栅格文件名称
     * @param env 指定环境
     */
    public static void transformAndResample(String pathFrom, String pathTo, String fileName, double cellSize,Envelope env) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference("EPSG:4610");
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        int cols = (int) (env.getWidth() / cellSize);
        int rows = (int) (env.getHeight() / cellSize);
        float[] data = new float[cols * rows];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols, rows, data, pspto, -32768, true);
        GeneralRasterWorkspaceFactory pFac = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work = pFac.OpenWorkspace(pathTo);
        GeneralRasterDataset newdataset = work.CreateRasterDataset(fileName, leftTop, cellSize, cellSize, cols, rows, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols, rows, data);
        newdataset.Dispose();
    }
    /**
     * 投影转换、重采样、并返回值的数组
     * @param pathFrom 读取的tif文件的位置
     * @param cellSize  重采样像元大小
     * @param env 指定环境
     * @return
     */
    public static float[] transformAndResample(String pathFrom, double cellSize,Envelope env) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference("EPSG:4610");
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        double right = env.getRight();
        double left = env.getLeft();
        double top = env.getTop();
        double bottom = env.getBottom();
        int cols = (int) ((right-left) / cellSize);
        int rows = (int) ((top-bottom) / cellSize);
        float[] data = new float[cols * rows];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols, rows, data, pspto, -32768, true);
        return data;
    }
    /**
     * 读取并重采样
     * @param pathFrom 读取栅格文件绝对路径
     * @param cellSize 像元大小
     * @return
     */
    public RasterBand readAndResample(String pathFrom, double cellSize) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        Envelope env = band.getExtent();
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference("EPSG:4610");
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        double cellSizeX = env.getWidth() / band.getRows();
        double cellSizeY = env.getWidth() / band.getCols();
        int cols = (int) (env.getWidth() / cellSize);
        int rows = (int) (env.getHeight() / cellSize);
        float[] data = new float[cols * rows];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols, rows, data, pspto, -32768, true);
        return band;
    }
    /**
     * 创建内存栅格
     * @param pathFrom 栅格文件路径
     * @param fileName 内存栅格文件名称
     */
    public void createMemRaster(String pathFrom, String fileName) {
        GeneralRasterDataset dataset = new GeneralRasterDataset();
        dataset.OpenFromFile(pathFrom);
        RasterBand band = dataset.getRasterBand(0);
        double nodata = band.getNoData();
        Envelope env = band.getExtent();
        SpatialReference psp = band.getSpatialReference();
        SpatialReference pspto = new SpatialReference("EPSG:4610");
        CoordinateTransformation pTrans = new CoordinateTransformation(psp, pspto);
        pTrans.BeginTransform();
        env.Project(pTrans);
        double cellSizeX = env.getWidth() / band.getRows();
        double cellSizeY = env.getWidth() / band.getCols();
        double cellSize = Math.max(cellSizeX, cellSizeY);
        int cols = (int) (env.getWidth() / cellSize);
        int rows = (int) (env.getHeight() / cellSize);
        float[] data = new float[cols * rows];
        Point leftTop = new Point(env.getLeft(), env.getTop());
        band.GetBlockDataByCoord(leftTop, cellSize, cols, rows, data, pspto, -32768);
        MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();
        MemRasterWorkspace work = pFac.CreateWorkspace();
        MemRasterDataset newdataset = work.CreateRasterDataset(fileName, leftTop, cellSize, cellSize, cols, rows, 1, RasterDataType.rdtFloat32, -32768, pspto);
        RasterBand newband = newdataset.getRasterBand(0);
        newband.SaveBlockData(0, 0, cols, rows, data);
        newdataset.Dispose();
    }

    /**
     * 读取矢量
     * @param pathFrom 矢量文件路径
     */
    public void readShapefile(String pathFrom) {
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        pfc.OpenFromFile(pathFrom);
        int featureCount = pfc.getFeatureCount();
        int fieldIndex = pfc.getTableDesc().getFieldIndex("ID");
        String sValue = "";
        for (int k = 0; k < featureCount; k++) {
            Geometry geo = pfc.GetFeature(k);
            sValue = pfc.GetFieldValueAsString(k, fieldIndex);
        }
    }

    /**
     * 创建内存矢量
     * @param pathFrom 矢量文件路径
     * @param filename 内存文件名称
     */
    public void createMemFeature(String pathFrom, String filename) {
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        pfc.OpenFromFile(pathFrom);
        MemFeatureWorkspaceFactory pFac = new MemFeatureWorkspaceFactory();
        MemFeatureWorkspace workspace = pFac.CreateWorkspace();
        MemFeatureClass newfc = workspace.CreateFeatureClass(filename, pfc.getShapeType(), pfc.getTableDesc(), pfc.getSpatialReference());
        int featureCount = pfc.getFeatureCount();
        int fieldCount = pfc.getTableDesc().getFieldCount();
        String[] sValues = new String[fieldCount];
        for (int k = 0; k < featureCount; k++) {
            Geometry geo = pfc.GetFeature(k);
            for (int j = 0; j < fieldCount; j++) {
                sValues[j] = pfc.GetFieldValueAsString(k, j);
            }
            newfc.AddFeatureEx(geo, sValues);
        }
    }

    /**
     * 读写矢量文件
     * @param pathFrom
     * @param pathTo
     * @param fileName
     */
    public void writeShapefile(String pathFrom, String pathTo, String fileName) {
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        pfc.OpenFromFile(pathFrom);
        ShapefileWorkspaceFactory pFac = new ShapefileWorkspaceFactory();
        ShapefileWorkspace workspace = pFac.OpenWorkspace(pathTo);
        ShapefileFeatureClass newfc = workspace.CreateFeatureClass(fileName, pfc.getShapeType(), pfc.getTableDesc(), pfc.getSpatialReference());
        int featureCount = pfc.getFeatureCount();
        int fieldCount = pfc.getTableDesc().getFieldCount();
        String[] sValues = new String[fieldCount];
        for (int k = 0; k < featureCount; k++) {
            Geometry geo = pfc.GetFeature(k);
            for (int j = 0; j < fieldCount; j++) {
                sValues[j] = pfc.GetFieldValueAsString(k, j);
            }
            newfc.AddFeatureEx(geo, sValues);
        }
    }

    /**
     * 矢量转栅格
     * @param shapefliePath
     * @param rasterfilePath
     */
    public void transRasterToShapefile(String shapefliePath, String rasterfilePath) {
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        pfc.OpenFromFile(shapefliePath);
        SAEnvironment envi = SAEnvironment.CreateFromFeatureClass(pfc, 10000);
        FeatureToRaster ftr = new FeatureToRaster();
        ftr.setEnvironment(envi);
        FileRasterTarget target = new FileRasterTarget(rasterfilePath, RasterCreateFileType.rcftTiff);
        ftr.Convert(pfc, "fid", target, -32768);
    }

    /**
     * IDW方法插值，将点对象列表插值生成内存栅格
     * @param points     传入多个点对象
     * @param clipsource 用来裁剪的图层（边界）
     * @param cellSize   像元大小
     * @return 内存栅格
     */
    public RasterBand IDW_pointsToMemRaster(ArrayList<ncPoint> points, String clipsource, Double cellSize) {
        TableDescCreator creator = new TableDescCreator();
        MemFeatureWorkspaceFactory mFac = new MemFeatureWorkspaceFactory();
        logger.info("正在处理插值数据... ...");
        creator.AddField(FieldDesp.CreateNumeicField("VALUE", 0, 0));
        creator.AddField(FieldDesp.CreateNumeicField("X", 0, 0));
        creator.AddField(FieldDesp.CreateNumeicField("Y", 0, 0));
        TableDesc desc = creator.CreateTableDesc();
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        logger.info("读取矢量数据,路径为" + clipsource);
        pfc.OpenFromFile(clipsource);
        SAEnvironment envi = SAEnvironment.CreateFromFeatureClass(pfc, cellSize);
        Envelope extent = envi.getExtent();
//        double r = extent.getRight();
//        double l = extent.getLeft();
//        double t = extent.getTop();
//        double b = extent.getBottom();
        FeatureToRaster ftr = new FeatureToRaster();
        ftr.setEnvironment(envi);
        MemRasterTarget target = new MemRasterTarget();
        RasterBand band1 = ftr.Convert(pfc, "FID", target, -32768);
        double nodata1 = band1.getNoData();
        MemFeatureWorkspace mWorkspace = mFac.CreateWorkspace();
        MemFeatureClass mNewfc = mWorkspace.CreateFeatureClass("temp" + new Date() + Math.random() * 10000, VectorShapeType.vstPoint, desc, band1.getSpatialReference());
        SpatialReference re = new SpatialReference("EPSG:4326");
        CoordinateTransformation p = new CoordinateTransformation(re, band1.getSpatialReference());
        p.BeginTransform();
        Envelope env1 = band1.getExtent();
        double right1 = env1.getRight();
        double left1 = env1.getLeft();
        double top1 = env1.getTop();
        double bottom1 = env1.getBottom();
        for (int i = 0; i < points.size(); i++) {
            String[] sValues = new String[3];
            Geometry geo = new Point(points.get(i).getLog(), points.get(i).getLat());
            geo.Project(p);
            sValues[0] = String.valueOf(points.get(i).getValue());
            sValues[1] = String.valueOf(Double.parseDouble(String.format("%.2f", points.get(i).getLog())));
            sValues[2] = String.valueOf(Double.parseDouble(String.format("%.2f", points.get(i).getLat())));
            mNewfc.AddFeatureEx(geo, sValues);
        }
        IDWInterpolate iftr = new IDWInterpolate();
        int power = 4;
        int point = 5;
        target = new MemRasterTarget();
        envi = SAEnvironment.CreateFromFeatureClass(pfc, cellSize);
        iftr.setEnvironment(envi);
        RasterBand band = iftr.IDWByPointNum(mNewfc, "VALUE", point, power, target, -32768);
        if (band == null) {
            logger.info("插值失败");
            return band;
        }
        logger.info("插值成功，生成内存文件....");
        double cellSize1 = band.getXCellSize();
        Envelope env = band.getExtent();
        double left = env.getLeft();
        double top = env.getTop();
        double bottom = env.getBottom();
        double right = env.getRight();
        double clipright;
        double clipleft;
        double cliptop;
        double clipbottom;
        //裁剪算法
        if (right > right1) {
            clipright = right1;
        } else {
            clipright = right;
        }
        if (left < left1) {
            clipleft = left1;
        } else {
            clipleft = left;
        }
        if (top > top1) {
            cliptop = top1;
        } else {
            cliptop = top;
        }
        if (bottom < bottom1) {
            clipbottom = bottom1;
        } else {
            clipbottom = bottom;
        }
        logger.info("开始裁剪... ...");
        Point leftTop = new Point(clipleft, cliptop);
        int cols = (int) ((clipright - clipleft) / cellSize1);
        int rows = (int) ((cliptop - clipbottom) / cellSize1);
        logger.info("cellSize1:" + cellSize1 + "   clipright-clipleft:" + (clipright - clipleft) + "   cliptop-clipbottom:" + (cliptop - clipbottom));
        float[] data = new float[cols];
        float[] data1 = new float[cols];
        MemRasterWorkspaceFactory pFac = new MemRasterWorkspaceFactory();
        MemRasterWorkspace work = pFac.CreateWorkspace();
        MemRasterDataset newdataset = work.CreateRasterDataset("temp" + new Date() + Math.random() * 10000, leftTop, cellSize1, cellSize1, cols, rows, 1, RasterDataType.rdtFloat32, -32768, re);
        RasterBand newband = newdataset.getRasterBand(0);
        for (int i = 0; i < rows; i++) {
            if (top1 >= top && left1 <= left) {
                band.GetBlockData(0, i, cols, 1, cols, 1, data);
                band1.GetBlockData((int) ((left - left1) / cellSize1), (int) ((top1 - top) / cellSize1) + i, cols, 1, cols, 1, data1);
            } else if (top1 >= top && left1 >= left) {
                band.GetBlockData((int) ((left1 - left) / cellSize1), i, cols, 1, cols, 1, data);
                band1.GetBlockData(0, (int) ((top1 - top) / cellSize1) + i, cols, 1, cols, 1, data1);
            } else if (top1 <= top && left1 <= left) {
                band.GetBlockData(0, (int) ((top - top1) / cellSize1) + i, cols, 1, cols, 1, data);
                band1.GetBlockData((int) ((left - left1) / cellSize1), i, cols, 1, cols, 1, data1);
            } else if (top1 <= top && left1 >= left) {
                band.GetBlockData((int) ((left1 - left) / cellSize1), (int) ((top - top1) / cellSize1) + i, cols, 1, cols, 1, data);
                band1.GetBlockData(0, i, cols, 1, cols, 1, data1);
            }
            for (int j = 0; j < cols; j++) {
                if (data1[j] == nodata1)
                    data[j] = (float) nodata1;
            }
            newband.SaveBlockData(0, i, cols, 1, data);
        }
        band.ClearStatistics();
        band1.ClearStatistics();
        pfc.Dispose();
        logger.info("裁剪成功!!!");
        return newband;
    }

    /**
     * 矢量裁剪栅格
     * @param clipsource 裁剪边界图层
     * @param rasterPath 要裁剪的栅格数据
     * @param saveTo 保存路径
     * @param saveName 保存名称
     */
    public void clipRaster(String clipsource, String rasterPath,String saveTo,String saveName) {
        //读栅格
        RasterBand rasterBand = readRaster(rasterPath);
        //获取像元大小
        double xCellSize = rasterBand.getXCellSize();
        //读边界图层
        logger.info("读取矢量数据,路径为" + clipsource);
        ShapefileFeatureClass pfc = new ShapefileFeatureClass();
        double nodata1 = rasterBand.getNoData();
        pfc.OpenFromFile(clipsource);
        SAEnvironment envi = SAEnvironment.CreateFromFeatureClass(pfc, xCellSize);
        //把矢量图层转成栅格
        FeatureToRaster ftr = new FeatureToRaster();
        ftr.setEnvironment(envi);
        MemRasterTarget target = new MemRasterTarget();
        RasterBand band1 = ftr.Convert(pfc, "FID", target, -32768);

        //获取裁剪边界范围
        Envelope env1 = band1.getExtent();
        double right1 = env1.getRight();
        double left1 = env1.getLeft();
        double top1 = env1.getTop();
        double bottom1 = env1.getBottom();
        //栅格数据边界范围
        Envelope env = rasterBand.getExtent();
        double left = env.getLeft();
        double top = env.getTop();
        double bottom = env.getBottom();
        double right = env.getRight();

        //裁剪算法
        double clipright;
        double clipleft;
        double cliptop;
        double clipbottom;
        if (right > right1) {
            clipright = right1;
        } else {
            clipright = right;
        }
        if (left < left1) {
            clipleft = left1;
        } else {
            clipleft = left;
        }
        if (top > top1) {
            cliptop = top1;
        } else {
            cliptop = top;
        }
        if (bottom < bottom1) {
            clipbottom = bottom1;
        } else {
            clipbottom = bottom;
        }
        logger.info("开始裁剪... ...");
        Point leftTop = new Point(clipleft, cliptop);
        int cols = (int) ((clipright - clipleft) / xCellSize);
        int rows = (int) ((cliptop - clipbottom) / xCellSize);
        logger.info("cellSize1:" + xCellSize + "   clipright-clipleft:" + (clipright - clipleft) + "   cliptop-clipbottom:" + (cliptop - clipbottom));
        float[] data = new float[cols];
        float[] data1 = new float[cols];
        GeneralRasterWorkspaceFactory pFac = new GeneralRasterWorkspaceFactory();
        GeneralRasterWorkspace work = pFac.OpenWorkspace(saveTo);
        GeneralRasterDataset newdataset = work.CreateRasterDatasetEx(saveName, band1, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768);
        RasterBand newband = newdataset.getRasterBand(0);
        for (int i = 0; i < rows; i++) {
            if (top1 >= top && left1 <= left) {
                rasterBand.GetBlockData(0, i, cols, 1, cols, 1, data);
                band1.GetBlockData((int) ((left - left1) / xCellSize), (int) ((top1 - top) / xCellSize) + i, cols, 1, cols, 1, data1);
            } else if (top1 >= top && left1 >= left) {
                rasterBand.GetBlockData((int) ((left1 - left) / xCellSize), i, cols, 1, cols, 1, data);
                band1.GetBlockData(0, (int) ((top1 - top) / xCellSize) + i, cols, 1, cols, 1, data1);
            } else if (top1 <= top && left1 <= left) {
                rasterBand.GetBlockData(0, (int) ((top - top1) / xCellSize) + i, cols, 1, cols, 1, data);
                band1.GetBlockData((int) ((left - left1) / xCellSize), i, cols, 1, cols, 1, data1);
            } else if (top1 <= top && left1 >= left) {
                rasterBand.GetBlockData((int) ((left1 - left) / xCellSize), (int) ((top - top1) / xCellSize) + i, cols, 1, cols, 1, data);
                band1.GetBlockData(0, i, cols, 1, cols, 1, data1);
            }
            for (int j = 0; j < cols; j++) {
                if (data1[j] == nodata1)
                    data[j] = (float) nodata1;
            }
            newband.SaveBlockData(0, i, cols, 1, data);
        }
        rasterBand.ClearStatistics();
        band1.ClearStatistics();
        pfc.Dispose();
        logger.info("裁剪成功!!!");
    }
}
