package com.cuit.mete.jobs.grib.job.util;

import com.cuit.job.utils.metegrib2.DataGrid;
import com.cuit.job.utils.metegrib2.GridPoint;
import com.cuit.mete.jobs.grib.job.vo.NcBasicMeta;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import ucar.ma2.Array;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class NcUtilOMI {
    private static final Logger logger = Logger.getLogger(com.cuit.job.utils.metegrib2.NcUtil.class);

    private static Double startLat = -89.875;
    private static Double endLat = 89.875;
    private static Double startLon = -179.875;
    private static Double endLon = 179.875;
    private static Double interval = 0.25;

    public NcUtilOMI() {
    }

    public static Map<String, List<GridPoint>> getNcData(String file, String paramName, String levels) {
        Map<String, List<GridPoint>> result = new HashMap();
        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");

        try {
            NetcdfFile ncfile = NetcdfFile.open(file);
            List<Variable> variables = ncfile.getVariables();
            Iterator var6 = variables.iterator();

            label81:
            while (var6.hasNext()) {
                Variable v = (Variable) var6.next();
                String long_name = null;
                List<Attribute> attrList = v.getAttributes();
                Iterator var10 = attrList.iterator();

                while (var10.hasNext()) {
                    Attribute attr = (Attribute) var10.next();
                    if (attr.getFullName().toLowerCase().equals("Title")) {
                        long_name = attr.getStringValue().replace(" @ ", "_");
                        long_name = long_name.replaceAll(" ", "_");
                        break;
                    }
                }

                Log.debug("要素名：" + long_name);
                if (long_name != null && long_name.toLowerCase().equals(paramName.toLowerCase())) {
                    List<Dimension> dimList = v.getDimensions();
                    float[] lngList = null;
                    float[] latList = null;
                    float[] a_height = null;
                    boolean isLevel = false;
                    Iterator var15 = dimList.iterator();

                    while (var15.hasNext()) {
                        Dimension d = (Dimension) var15.next();
                        Variable v_height;
                        Array a_lon;
                        if (d.getShortName().toLowerCase().equals("lat")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_height.read();
                            latList = (float[]) ((float[]) a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().equals("lon")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_height.read();
                            lngList = (float[]) ((float[]) a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().startsWith("isobaric")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_height = (float[]) ((float[]) v_height.read().copyTo1DJavaArray());
                            isLevel = true;
                        }
                    }

                    if (!isLevel) {
                        float[][] data = (float[][]) ((float[][]) v.read().reduce().copyToNDJavaArray());
                        List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                        result.put("", gplist);
                        break;
                    }

                    String[] levelItems = levels.split(",");
                    String[] var30 = levelItems;
                    int var32 = levelItems.length;
                    int var33 = 0;

                    while (true) {
                        if (var33 >= var32) {
                            break label81;
                        }

                        String level = var30[var33];
                        int heightIndex = -1;

                        for (int i = 0; i < a_height.length; ++i) {
                            if (a_height[i] == (float) Integer.parseInt(level)) {
                                heightIndex = i;
                                break;
                            }
                        }

                        if (heightIndex >= 0) {
                            int[] origin = new int[]{0, heightIndex, 0, 0};
                            int[] size = new int[]{1, 1, latList.length, lngList.length};
                            float[][] data = (float[][]) ((float[][]) v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                            List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                            result.put(level, gplist);
                        } else {
                            logger.error("文件" + file + "中未找到高度层" + level);
                        }

                        ++var33;
                    }
                }
            }

            ncfile.close();
        } catch (Exception var25) {
            var25.printStackTrace();
        }

        return result;
    }

    public static NcBasicMeta getSPHDNcDataMetaData(String file, String paramName) {
        NcBasicMeta ncBasicMeta = new NcBasicMeta();
        DataGrid dataGrid = null;
        NetcdfFile ncfile = null;

        try {
            //打开并读取nc文件
            ncfile = NetcdfFile.open(file);
            List<Variable> variables = ncfile.getVariables();

            //获取数据变量
            Variable paramVar = getVariableByName(variables, paramName);
            try {

                //构造经度、纬度数组
                float[] latList = createArrayByStaEndInterval(startLat, endLat, interval);
                float[] lonList = createArrayByStaEndInterval(startLon, endLon, interval);

                float[][] dataList = readVarToNArray(ncfile, paramVar);

                List<GridPoint> gridPoints = getGridPointList(dataList, latList, lonList);
                ncBasicMeta.setGridPoints(gridPoints);

                if (lonList != null && lonList.length >= 0 && latList != null && latList.length > 0) {
                    ncBasicMeta.setLatLists(latList);
                    ncBasicMeta.setLngLists(lonList);
                    dataGrid = new DataGrid();
                    dataGrid.setLons(lonList);
                    dataGrid.setLats(latList);
                    dataGrid.setRows(latList.length);
                    dataGrid.setCols(lonList.length);
                    dataGrid.setLat1(latList[0]);
                    dataGrid.setLat2(latList[latList.length - 1]);
                    dataGrid.setLon1(lonList[0]);
                    dataGrid.setLon2(lonList[lonList.length - 1]);
                    dataGrid.setxCellSize((float)(1.0D * (double)latList[latList.length-1] - (double)latList[0]) / (float)latList.length);
                    dataGrid.setyCellSize((float)(1.0D * (double)lonList[lonList.length-1] - (double)lonList[0]) / (float)lonList.length);
                    ncBasicMeta.setDataGrid(dataGrid);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("变量数组读取失败！");
            }
        } catch (Exception var26) {
            var26.printStackTrace();
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var25) {
                }
            }

        }
        return ncBasicMeta;
    }

    /**
     * 根据传入的始末位置及间隔，生成数组
     *
     * @param startIndex
     * @param endIndex
     * @param interval
     * @return
     */
    private static float[] createArrayByStaEndInterval(Double startIndex, Double endIndex, Double interval) {

        double lengthDouble = (endIndex - startIndex) / interval;
        int lengthInt = (int) lengthDouble;

        float[] array = new float[lengthInt + 1];

        for (int index = 0; index <= lengthInt; index++) {

            array[index] = (float) (startIndex + interval * index);

        }

        return array;
    }


    private static float[][] readVarToNArray(NetcdfFile ncFile, Variable variable) throws IOException, InvalidRangeException {


        float[][] data = null;
        int[] origin = new int[]{0, 0};
        int[] size;
        float[][] dataTrans = new float[variable.getShape(0)][variable.getShape(1)];

        size = new int[]{variable.getShape(0), variable.getShape(1)};
        Class type = variable.read(origin, size).getElementType();
        data = (float[][]) ((float[][]) variable.read(origin, size).reduce().copyToNDJavaArray());

        for (int i = 0; i < variable.getShape(0); i++) {
            for (int j = 0; j < variable.getShape(1); j++) {
                dataTrans[i][j] = Float.parseFloat(String.valueOf(data[i][j]));
                dataTrans[i][j] = dataTrans[i][j] ;
            }
        }
        return dataTrans;
    }

    /**
     * 读取传入变量内涵的一维数据数组
     *
     * @param ncfile
     * @param variable
     * @return
     */
    private static float[] readVarTo1Array(NetcdfFile ncfile, Variable variable) throws IOException {


        float[] varList = null;

        Array dataArray = variable.read();
        varList = (float[]) ((float[]) dataArray.copyTo1DJavaArray());

        return varList;
    }


    /**
     * 根据传入变量名获取变量
     *
     * @param variables
     * @param paramName
     * @return
     */
    private static Variable getVariableByName(List<Variable> variables, String paramName) {

        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");
        Variable var = null;
        Iterator var5 = variables.iterator();
        while (var5.hasNext()) {
            var = (Variable) var5.next();
            String long_name = null;
            List<Attribute> attrList = var.getAttributes();
            Iterator var9 = attrList.iterator();

            while (var9.hasNext()) {
                Attribute attr = (Attribute) var9.next();
//                    String s = attr.getFullName();  用来获得属性名称
                if (attr.getFullName().toLowerCase().equals("title")) {
                    long_name = attr.getStringValue().replace(" @ ", "_");
                    long_name = long_name.replaceAll(" ", "_");
                    break;
                }
            }
            if (long_name != null && long_name.toLowerCase().equals(paramName.toLowerCase())) {
                return var;
            }
        }
        return null;
    }


    public static Map<String, List<GridPoint>> getSPHDNcData(String file, String paramName, String times) {
        Map<String, List<GridPoint>> result = new HashMap();
        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");

        try {
            NetcdfFile ncfile = NetcdfFile.open(file);
            List<Variable> variables = ncfile.getVariables();
            Iterator var6 = variables.iterator();

            label88:
            while (var6.hasNext()) {
                Variable v = (Variable) var6.next();
                String long_name = null;
                List<Attribute> attrList = v.getAttributes();
                Iterator var10 = attrList.iterator();

                while (var10.hasNext()) {
                    Attribute attr = (Attribute) var10.next();
                    if (attr.getFullName().toLowerCase().equals("long_name")) {
                        long_name = attr.getStringValue().replace(" @ ", "_");
                        long_name = long_name.replaceAll(" ", "_");
                        break;
                    }
                }

                Log.debug("要素名：" + long_name);
                if (long_name != null && long_name.toLowerCase().equals(paramName.toLowerCase())) {
                    List<Dimension> dimList = v.getDimensions();
                    float[] lngList = null;
                    float[] latList = null;
                    int[] timesArray = null;
                    boolean isTimes = false;
                    Iterator var15 = dimList.iterator();

                    while (var15.hasNext()) {
                        Dimension d = (Dimension) var15.next();
                        Variable v_times;
                        Array a_lon;
                        if (d.getShortName().toLowerCase().equals("lat")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            latList = (float[]) ((float[]) a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().equals("lon")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            lngList = (float[]) ((float[]) a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().startsWith("time")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            timesArray = (int[]) ((int[]) v_times.read().copyTo1DJavaArray());
                            isTimes = true;
                        }
                    }

                    if (!isTimes) {
                        float[][] data = (float[][]) ((float[][]) v.read().reduce().copyToNDJavaArray());
                        List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                        result.put("", gplist);
                        break;
                    }

                    String[] timesItems = times.split(",");
                    String[] var29 = timesItems;
                    int var31 = timesItems.length;
                    int var32 = 0;

                    while (true) {
                        if (var32 >= var31) {
                            break label88;
                        }

                        String time = var29[var32];
                        int timeIndex = -1;

                        for (int i = 0; i < timesArray.length; ++i) {
                            if (timesArray[i] == Integer.parseInt(time)) {
                                timeIndex = i;
                                break;
                            }
                        }

                        if (timeIndex >= 0) {
                            float[][] data = (float[][]) null;
                            int[] origin;
                            int[] size;
                            if (dimList.size() == 3) {
                                origin = new int[]{timeIndex, 0, 0};
                                size = new int[]{1, latList.length, lngList.length};
                                data = (float[][]) ((float[][]) v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                            } else if (dimList.size() == 4) {
                                origin = new int[]{timeIndex, 0, 0, 0};
                                size = new int[]{1, 1, latList.length, lngList.length};
                                data = (float[][]) ((float[][]) v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                            }

                            List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                            result.put(time, gplist);
                        } else {
                            logger.error("文件" + file + "中未找到高度层" + time);
                        }

                        ++var32;
                    }
                }
            }

            ncfile.close();
        } catch (Exception var24) {
            var24.printStackTrace();
        }

        return result;
    }

    private static List<GridPoint> getGridPointList(float[][] data, float[] latList, float[] lngList) {
        List<GridPoint> gplist = new ArrayList();
        int i6 = latList.length;
        int j3 = lngList.length;
        int k = data.length;
        try {
            for (int i = 0; i < data.length; ++i) {
                for (int j = 0; j < data[i].length; ++j) {
                    GridPoint gp = new GridPoint();
                    gp.latitude = formatNumber((double) latList[i], 3);
                    gp.longitude = formatNumber((double) lngList[j], 3);
                    if (data[i][j] > 0){
                        gp.value = formatNumber((double) data[i][j] / 1000000000000.0, 3);
                    }else{
                        gp.value = formatNumber((double) data[i][j], 3);
                    }
                    gplist.add(gp);
                }
            }
        }catch (Exception e){

        }


        return gplist;
    }

    private static double formatNumber(double d, int dic) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(dic, 4).doubleValue();
    }

    public static void main(String[] args) throws IOException {
        String file = "D:\\Temp\\融合产品\\SPHC\\SPHC_ER01\\Z_NWGD_C_BCCD_20210925113024_P_RFFC_SPHC-ER01_20210925120000_02401.GRB2";
        Map<String, List<GridPoint>> result = getSPHDNcData(file, "Total precipitation (1_Hour Accumulation)_Ground or water surface", "1,2");
        System.out.println(result.size());
    }
}

