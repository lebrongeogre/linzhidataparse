package com.cuit.mete.jobs.grib.sta.job;

import com.cuit.mete.BaseObjects.LicenseEngine;
import com.cuit.mete.DataSources.*;
import com.cuit.mete.Geometry.CoordinateTransformation;
import com.cuit.mete.Geometry.Envelope;
import com.cuit.mete.Geometry.Point;
import com.cuit.mete.Geometry.SpatialReference;
import com.cuit.mete.jobs.grib.job.BaseMethod;
import com.cuit.mete.jobs.grib.job.vo.GridPoint;
import com.cuit.mete.jobs.grib.sta.utils.Calculator;
import com.cuit.mete.jobs.grib.sta.utils.IFileUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.cuit.mete.jobs.grib.sta.utils.Calculator.getRasterArray;

public class RasterCalSave {
    private static final Logger logger = Logger.getLogger(RasterCalSave.class);
    private Calculator calculator = new Calculator();
    private IFileUtil fileUtil;

    public RasterCalSave() {
    }

    public RasterCalSave(IFileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public IFileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(IFileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    /**
     * 计算均值
     * @param pathFiltered 文件目录（绝对路径，包含文件名）
     * @param filePath
     * @param outFilePath 输出目录
     * @return
     */
    public void calSaveRaster(List<String> pathFiltered, String outFilePath,String filePath) {
        BaseMethod baseMethod = new BaseMethod();
        List<Float[]> dataArray = new ArrayList<>();
        //遍历文件目录，读取栅格数组集合
        for (String path : pathFiltered) {
           Float[] tempData = parseFloat(baseMethod.readRasterData(path));
           dataArray.add(tempData);
        }
        //计算均值
        float[] refRaster = calculator.getRasterArray(dataArray);
        //生成文件
        RasterBand rasterBand = baseMethod.readRaster(pathFiltered.get(0));
        transArrar2Tif(refRaster,rasterBand.getLeftTop(),rasterBand.getXCellSize(),rasterBand.getYCellSize(),rasterBand.getCols(),rasterBand.getRows(),outFilePath,filePath);
    }

    private Float[] parseFloat(float[] dataArray) {
        Float[] refArray = new Float[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            refArray[i] = Float.valueOf(dataArray[i]);
        }
        return refArray;
    }

    public void transArrar2Tif(float[] data, Point leftTop, double xCellSize, double yCellSize, int cols, int rows, String path, String tifFileName) {

        GeneralRasterDataset newdataset2 = null;
        MemRasterDataset newdataset = null;
        try {
            SpatialReference pspto = new SpatialReference("EPSG:4326");
            RasterBand newband2 = null;
            GeneralRasterWorkspaceFactory pFac2 = new GeneralRasterWorkspaceFactory();
            GeneralRasterWorkspace work2 = pFac2.OpenWorkspace(path);
            newdataset2 = work2.CreateRasterDataset(tifFileName, leftTop, xCellSize, yCellSize, cols, rows, 1, RasterDataType.rdtFloat32, RasterCreateFileType.rcftTiff, -32768, pspto);
            newband2 = newdataset2.getRasterBand(0);
            newband2.SaveBlockData(0, 0, cols, rows, data);
            logger.debug("栅格文件生成成功，保存位置:" + path + ",文件名称：" + tifFileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("栅格文件生成出错:" + e.getMessage());
        } finally {
            if (newdataset2 != null) {
                newdataset2.Dispose();
            }
            if (newdataset != null) {
                newdataset.Dispose();
            }
        }
    }

}
