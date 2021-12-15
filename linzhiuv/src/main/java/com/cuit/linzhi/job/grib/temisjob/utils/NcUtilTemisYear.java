package com.cuit.linzhi.job.grib.temisjob.utils;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.cuit.job.utils.metegrib2.DataGrid;
import com.cuit.job.utils.metegrib2.GridPoint;
import com.cuit.job.utils.metegrib2.NcUtil;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import ucar.ma2.Array;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class NcUtilTemisYear {
    private static final Logger logger = Logger.getLogger(NcUtil.class);

    public NcUtilTemisYear() {
    }

    public static Map<String, List<GridPoint>> getNcData(String file, String paramName, String levels) {
        NetcdfFile ncfile = null;

        try {
            ncfile = NetcdfFile.open(file);
            if (ncfile != null) {
                Map var4 = getNcData(ncfile, paramName, levels);
                return var4;
            }
        } catch (Exception var15) {
            logger.error("打开文件" + file + "出错：" + var15.getMessage(), var15);
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var14) {
                    logger.error("关闭文件" + file + "出错：" + var14.getMessage(), var14);
                }
            }

        }

        return null;
    }

    public static Map<String, List<GridPoint>> getNcData(String location, byte[] data, String paramName, String levels) {
        NetcdfFile ncfile = null;

        try {
            ncfile = NetcdfFile.openInMemory(location, data);
            Map var5 = getNcData(ncfile, paramName, levels);
            return var5;
        } catch (Exception var15) {
            logger.error("打开文件" + location + "字节数组出错：" + var15.getMessage(), var15);
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var14) {
                    logger.error("关闭文件" + location + "字节数组出错：" + var14.getMessage(), var14);
                }
            }

        }

        return null;
    }

    private static Map<String, List<GridPoint>> getNcData(NetcdfFile ncfile, String paramName, String levels) {
        Map<String, List<GridPoint>> result = new HashMap();
        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");

        try {
            List<Variable> variables = ncfile.getVariables();
            Iterator var5 = variables.iterator();

            while(var5.hasNext()) {
                Variable v = (Variable)var5.next();
                String long_name = null;
                List<Attribute> attrList = v.getAttributes();
                Iterator var9 = attrList.iterator();

                while(var9.hasNext()) {
                    Attribute attr = (Attribute)var9.next();
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
                    float[] a_height = null;
                    boolean isLevel = false;
                    Iterator var14 = dimList.iterator();

                    while(var14.hasNext()) {
                        Dimension d = (Dimension)var14.next();
                        Variable v_height;
                        Array a_lon;
                        if (d.getShortName().toLowerCase().equals("lat")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_height.read();
                            latList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().equals("lon")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_height.read();
                            lngList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().startsWith("isobaric")) {
                            v_height = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_height = (float[])((float[])v_height.read().copyTo1DJavaArray());
                            isLevel = true;
                        }
                    }

                    if (isLevel) {
                        String[] levelItems = levels.split(",");
                        String[] var29 = levelItems;
                        int var31 = levelItems.length;

                        for(int var32 = 0; var32 < var31; ++var32) {
                            String level = var29[var32];
                            int heightIndex = -1;

                            for(int i = 0; i < a_height.length; ++i) {
                                if (a_height[i] == (float)Integer.parseInt(level)) {
                                    heightIndex = i;
                                    break;
                                }
                            }

                            if (heightIndex >= 0) {
                                int[] origin = new int[]{0, heightIndex, 0, 0};
                                int[] size = new int[]{1, 1, latList.length, lngList.length};
                                float[][] data = (float[][])((float[][])v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                                List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                                result.put(level, gplist);
                            } else {
                                logger.error("文件" + ncfile.getLocation() + "中未找到高度层" + level);
                            }
                        }

                        return result;
                    } else {
                        float[][] data = (float[][])((float[][])v.read().reduce().copyToNDJavaArray());
                        List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                        result.put("", gplist);
                        break;
                    }
                }
            }
        } catch (Exception var24) {
            logger.error("读文件时" + ncfile.getLocation() + "出错：" + var24.getMessage(), var24);
        }

        return result;
    }

    public static DataGrid getSPHDNcDataMetaData(String file, String paramName) {
        NetcdfFile ncfile = null;

        DataGrid var3;
        try {
            ncfile = NetcdfFile.open(file);
            var3 = getSPHDNcDataMetaData(ncfile, paramName);
        } catch (Exception var14) {
            logger.error("打开文件" + file + "出错：" + var14.getMessage(), var14);
            return null;
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var13) {
                    logger.error("关闭文件" + file + "出错：" + var13.getMessage(), var13);
                }
            }

        }

        return var3;
    }

    public static DataGrid getSPHDNcDataMetaData(String location, byte[] data, String paramName) {
        NetcdfFile ncfile = null;

        try {
            ncfile = NetcdfFile.openInMemory(location, data);
            DataGrid var4 = getSPHDNcDataMetaData(ncfile, paramName);
            return var4;
        } catch (Exception var14) {
            logger.error("打开文件" + location + "字节数组出错：" + var14.getMessage(), var14);
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var13) {
                    logger.error("关闭文件" + location + "字节数组出错：" + var13.getMessage(), var13);
                }
            }

        }

        return null;
    }

    private static DataGrid getSPHDNcDataMetaData(NetcdfFile ncfile, String paramName) {
        DataGrid dataGrid = null;
        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");

        try {
            List<Variable> variables = ncfile.getVariables();
            Iterator var4 = variables.iterator();

            while(var4.hasNext()) {
                Variable v = (Variable)var4.next();
                String long_name = null;
                List<Attribute> attrList = v.getAttributes();
                Iterator var8 = attrList.iterator();

                while(var8.hasNext()) {
                    Attribute attr = (Attribute)var8.next();
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
                    //int[] timesArray = null;
                    boolean isTimes = false;
                    Iterator var13 = dimList.iterator();

                    while(var13.hasNext()) {
                        Dimension d = (Dimension)var13.next();
                        Variable v_times;
                        Array a_lon;
                        if (d.getShortName().toLowerCase().equals("latitude")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            latList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().equals("longitude")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            lngList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().startsWith("time")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            int[] timesArray = (int[])((int[])v_times.read().copyTo1DJavaArray());
                            isTimes = true;
                        }
                    }

                    if (lngList != null && lngList.length >= 0 && latList != null && latList.length > 0) {
                        dataGrid = new DataGrid();
                        dataGrid.setLons(lngList);
                        dataGrid.setLats(latList);
                        dataGrid.setRows(latList.length);
                        dataGrid.setCols(lngList.length);
                        dataGrid.setLat1(latList[0]);
                        dataGrid.setLat2(latList[latList.length - 1]);
                        dataGrid.setLon1(lngList[0]);
                        dataGrid.setLon2(lngList[lngList.length - 1]);
                        dataGrid.setxCellSize((float)(1.0D * (double)dataGrid.getLon2() - (double)dataGrid.getLon1()) / (float)dataGrid.getCols());
                        dataGrid.setyCellSize((float)(1.0D * (double)dataGrid.getLat2() - (double)dataGrid.getLat1()) / (float)dataGrid.getRows());
                    }
                    break;
                }
            }
        } catch (Exception var17) {
            logger.error("读文件时" + ncfile.getLocation() + "出错：" + var17.getMessage(), var17);
        }

        return dataGrid;
    }

    public static Map<String, List<GridPoint>> getSPHDNcData(String file, String paramName, String times) {
        NetcdfFile ncfile = null;

        try {
            ncfile = NetcdfFile.open(file);
            if (ncfile != null) {
                Map var4 = getSPHDNcData(ncfile, paramName, times);
                return var4;
            }
        } catch (Exception var15) {
            logger.error("打开文件" + file + "出错：" + var15.getMessage(), var15);
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var14) {
                    logger.error("关闭文件" + file + "出错：" + var14.getMessage(), var14);
                }
            }

        }

        return null;
    }

    public static Map<String, List<GridPoint>> getSPHDNcData(String location, byte[] data, String paramName, String times) {
        NetcdfFile ncfile = null;

        try {
            ncfile = NetcdfFile.openInMemory(location, data);
            Map var5 = getSPHDNcData(ncfile, paramName, times);
            return var5;
        } catch (Exception var15) {
            logger.error("打开文件" + location + "字节数组出错：" + var15.getMessage(), var15);
        } finally {
            if (ncfile != null) {
                try {
                    ncfile.close();
                } catch (IOException var14) {
                    logger.error("关闭文件" + location + "字节数组出错：" + var14.getMessage(), var14);
                }
            }

        }

        return null;
    }

    private static Map<String, List<GridPoint>> getSPHDNcData(NetcdfFile ncfile, String paramName, String times) {
        Map<String, List<GridPoint>> result = new HashMap();
        paramName = paramName.replace(" @ ", "_");
        paramName = paramName.replaceAll(" ", "_");

        try {
            List<Variable> variables = ncfile.getVariables();
            Iterator var5 = variables.iterator();

            label88:
            while(var5.hasNext()) {
                Variable v = (Variable)var5.next();
                String long_name = null;
                List<Attribute> attrList = v.getAttributes();
                Iterator var9 = attrList.iterator();

                while(var9.hasNext()) {
                    Attribute attr = (Attribute)var9.next();
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
                    Iterator var14 = dimList.iterator();

                    while(var14.hasNext()) {
                        Dimension d = (Dimension)var14.next();
                        Variable v_times;
                        Array a_lon;
                        if (d.getShortName().toLowerCase().equals("latitude")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            latList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().equals("longitude")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            a_lon = v_times.read();
                            lngList = (float[])((float[])a_lon.copyTo1DJavaArray());
                        } else if (d.getShortName().toLowerCase().startsWith("days")) {
                            v_times = ncfile.findVariable(d.getGroup().getShortName() + "/" + d.getShortName());
                            timesArray = (int[])((int[])v_times.read().copyTo1DJavaArray());
                            isTimes = true;
                        }
                    }

                    if (!isTimes) {
                        float[][] data = (float[][])((float[][])v.read().reduce().copyToNDJavaArray());
                        List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                        result.put("", gplist);
                        break;
                    }

                    String[] timesItems = times.split(",");
                    String[] var28 = timesItems;
                    int var30 = timesItems.length;
                    int var31 = 0;

                    while(true) {
                        if (var31 >= var30) {
                            break label88;
                        }

                        String time = var28[var31];
                        int timeIndex = -1;

                        for(int i = 1; i <= timesArray.length; i++) {
                            if (timesArray[i - 1] == Integer.parseInt(time)) {
                                timeIndex = i-1;
                                break;
                            }
                        }
                        logger.debug("开始处理第[" + timeIndex + "]天数据........");
                        if (timeIndex >= 0) {
                            float[][] data = (float[][])null;
                            int[] origin;
                            int[] size;
                            if (dimList.size() == 3) {
                                origin = new int[]{timeIndex, 0, 0};
                                size = new int[]{1, latList.length, lngList.length};
                                data = (float[][])((float[][])v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                            } else if (dimList.size() == 4) {
                                origin = new int[]{timeIndex, 0, 0, 0};
                                size = new int[]{1, 1, latList.length, lngList.length};
                                data = (float[][])((float[][])v.read(origin, size).reduce().reduce().copyToNDJavaArray());
                            }

                            List<GridPoint> gplist = getGridPointList(data, latList, lngList);
                            result.put(time, gplist);
                            logger.debug("第[" + timeIndex + "]天数据处理完成，当前共有[" + result.size()+"]条数据........");
                        } else {
                            logger.error("文件" + ncfile.getLocation() + "中未找到时间" + time);
                        }

                        ++var31;
                    }
                }
            }

            ncfile.close();
        } catch (Exception var23) {
            logger.error("读文件时" + ncfile.getLocation() + "出错：" + var23.getMessage(), var23);
        }

        return result;
    }

    private static List<GridPoint> getGridPointList(float[][] data, float[] latList, float[] lngList) {
        List<GridPoint> gplist = new ArrayList();

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                GridPoint gp = new GridPoint();
                gp.latitude = formatNumber((double)latList[i], 3);
                gp.longitude = formatNumber((double)lngList[j], 3);
                gp.value = formatNumber((double)data[i][j], 3);
                gplist.add(gp);
            }
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
