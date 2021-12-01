package com.cuit.mete.parse;

import cimiss.service.CimissDataInteface;
import cn.gov.cma.cimiss.gds.service.M4DataInterface;
import com.cuit.job.basejob.BaseJob;
import com.cuit.job.thread.SendMsg2MonitorThread;
import com.cuit.job.utils.FileUtil;
import com.cuit.job.utils.FtpUtil;
import com.cuit.mete.thread.SendDataToTcpServer;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.*;

@DisallowConcurrentExecution
public class SynStationProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(SeqProcessSample.class);
    private static final String CIMISSDOWNLOADTMP = "Cimissdownload";
    private Map<String, Object> config;
    
    int upcount;
    int failcount;
    int totalcount;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }
    public void startJob(JobExecutionContext context) throws JobExecutionException {
        config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
        logger.info("通过Cimiss接口，提取气象站点数据....");
        upcount=0;
        String cimissIP = (String) config.get("ip");
        Map<String, Object> apiUserconfig = (Map<String, Object>) config.get("ApiUserconfig");
        String cimissUser = (String) apiUserconfig.get("user");
        String cimissPassword = (String) apiUserconfig.get("password");

        Map<String, Object> cimissDataList = (Map<String, Object>) config.get("data");
        Iterator<String> it = cimissDataList.keySet().iterator();
        while (it.hasNext()) {
        	String key = it.next();
            Map<String, Object> cimissdataMap = (Map<String, Object>) cimissDataList.get(key);
            String cimissDataType = (String) cimissdataMap.get("datasetname");
            //处理地面资料
            if ("Station".equalsIgnoreCase(cimissDataType)) {
                CimissDataInteface dataInteface = null;
                //读出地面数据

                dataInteface = new CimissDataInteface(cimissIP, cimissUser, cimissPassword);
                List<String> fileList=null;
                try {
                	//GetStaInfoByRegionCode_JsonArray("540000");
                	fileList= dataInteface.GetStaInfoByRegionCode_OutToJsonFile("230000", CIMISSDOWNLOADTMP);
                	if (fileList != null && fileList.size() > 0) { 
                        //下载的临时文件列表
                        List<File> tmpFileList = new ArrayList<File>();

                        for (int i = 0; i < fileList.size(); i++) {
                            File tmpFile = new File(fileList.get(i));
          
                            logger.info("获取Cimiss" + tmpFile.exists());
                            if (tmpFile.exists()) {
                            	logger.info("当前文件为" + fileList.get(i));
                                tmpFileList.add(tmpFile);
                            }
                        }
                        if (tmpFileList.size() > 0) {
                        	totalcount=tmpFileList.size();
                            saveFile(cimissdataMap, tmpFileList, false);
                        }
                    }
                } catch (NullPointerException e) {
                    logger.error("Cimiss提取数据出错:" + e.getMessage());
                }
                if (fileList != null) {
                    logger.info("通过Cimiss接口，提取到" + fileList.size() + "个Station数据....");                                    
                }
            }
        }
        String workInfo="本次Cimiss的Station数据处理结果:共有"+totalcount+"个数据提取,有" + upcount + "个数据提取成功,有"+failcount+"提取失败";
        logger.info(workInfo);
        new SendMsg2MonitorThread(workInfo).start();
    }
    /**
     * 保存文件
     *
     * @throws
     * @Title: saveFile
     * @param: @param fileList
     * @param: @throws Exception
     * @return: void
     */

    private boolean saveFile(Map<String, Object> saveConfig, List<File> fileList, boolean isdelete) {
    	boolean saveFlag = false;
        //获取输出路径
        Map<String, Object> outConfig = (Map<String, Object>) saveConfig.get("output");
        Iterator<String> it = outConfig.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Map<String, Object> outPutInfo = (Map<String, Object>) outConfig.get(key);
            if ("dir".equals(outPutInfo.get("type") + "")) {
                String outputPath = (String) outPutInfo.get("savePath");
                outputPath = changeFilePathDatePart(outputPath);
                saveFlag = saveFiles2Directory(fileList, outputPath);
            } else if ("ftp".equals(outPutInfo.get("type") + "")) {           	
                saveFlag = saveFiles2Ftp(fileList, (Map<String, Object>) outPutInfo.get("outFtp"));
            } else {
                //其他方式
            }
        }
        //如果保存失败，将数据删除，重下重传
        if (!saveFlag || isdelete) {
            for (File f : fileList) {
                //删除临时文件
                if (isdelete) {
                    f.delete();
                }
            }
        }
        return saveFlag;
		
	}
    
    
    /**
     * 保存文件到本地目录
     *
     * @throws
     * @Title: saveFiles2Directory
     * @param: @param fileList
     * @param: @param dirPath
     * @param: @throws IOException
     * @return: void
     */
    private boolean saveFiles2Directory(List<File> fileList, String dirPath) {
        boolean saveFlag = true;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        for (File f : fileList) {
            try {
                FileUtil.copyFile(f, new File(dirFile, f.getName()));
                logger.info("文件保存成功：" + dirFile + "\\" + f.getName());
            } catch (IOException e) {
                saveFlag = false;
                e.printStackTrace();
            }
        }
        return saveFlag;
    }

    /**
     * 保存文件到FTP
     *
     * @throws
     * @Title: saveFiles2Ftp
     * @param: @param fileList
     * @param: @param ftpMap
     * @param: @throws Exception
     * @return: void
     */
    private boolean saveFiles2Ftp(List<File> fileList, Map<String, Object> ftpMap) {
        boolean saveFlag = true;
        //创建FTP工具
        logger.info("开始FTP上传");
        FtpUtil ftpUtil = null;
        String remotePath = (String) ftpMap.get("ftpPath");
        remotePath = changeFilePathDatePart(remotePath);

        try {
            ftpUtil = new FtpUtil(ftpMap.get("ftpServer") + "",
                    Integer.parseInt(ftpMap.get("ftpPort") + ""),
                    ftpMap.get("ftpUser") + "",
                    ftpMap.get("ftpPwd") + "",
                    remotePath);
            //登录FTP站点，如果目录不存在，则创建目录。
            ftpUtil.connectServerAndCreateDirecroty();

            if (ftpUtil.isConnect()) {
                for (File f : fileList) {
                    if (f.exists() && f.isFile()) {
                    	Updata(ftpUtil,f,0);
                    }
                }              
            }
        } catch (IOException e) {
            saveFlag = false;
            e.printStackTrace();
        } catch (Exception e) {
            saveFlag = false;
            e.printStackTrace();
        } finally {
            ftpUtil.closeServer();
        }
        return saveFlag;
    }
    
    private void Updata(FtpUtil ftpUtil, File f, int trancount){
    	if(trancount<5){
    		try {   	
			ftpUtil.upload(f.getAbsolutePath());
			logger.info("文件FTP上传成功：" +f.getName());
	        upcount++;
	        return;
    		} catch (Exception e) {
    			trancount++;
    			Updata(ftpUtil,f,trancount);
    		}
    	}else{
			failcount++;
			logger.info("文件FTP上传失败：" +f.getName());
			return;
		}

    }

    @DisallowConcurrentExecution
    public static class DownM4FileSendProcess extends BaseJob {
        private static final Logger logger = Logger.getLogger(DownM4FileSendProcess.class);
        private static final String FTPDOWNLOADTMP = "ftpdownload";
        private static final String M4DOWNLOADTMP = "M4download";
        private Map<String, Object> config;

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            startJob(jobExecutionContext);
        }

        public void startJob(JobExecutionContext context) throws JobExecutionException {
            //这是从上下文件获取从jobService中传过来的配置
            config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
            Calendar calendar = Calendar.getInstance();
            //获取目录类型
            String type = config.get("type") + "";
            try {
                downLoadM4(calendar);
            } catch (Exception e) {
                logger.error("接收文件出错：" + e.getMessage(), e);
            }
        }

        private void downLoadM4(Calendar calendar) throws Exception {
            //创建下载临时目录
            File tmppath = new File(M4DOWNLOADTMP);
            if (!tmppath.exists()) {
                tmppath.mkdirs();
            }
            logger.info("通过M4服务器，提取数据文件....");

            String ipM4 = (String) config.get("ip");
            String portM4 = (String) config.get("Port");

            Map<String, Object> m4dataList = (Map<String, Object>) config.get("data");

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(calendar.getTime());
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(calendar.getTime());

            calendar1.add(Calendar.HOUR_OF_DAY, -24);
            String sYear = calendar1.get(Calendar.YEAR) + "";
            String sMonth = (calendar1.get(Calendar.MONTH) + 1) + "";
            String sDay = calendar1.get(Calendar.DAY_OF_MONTH) + "";
            String sHour = calendar1.get(Calendar.HOUR_OF_DAY) + "";
            String eYear = calendar2.get(Calendar.YEAR) + "";
            String eMonth = (calendar2.get(Calendar.MONTH) + 1) + "";
            String eDay = calendar2.get(Calendar.DAY_OF_MONTH) + "";
            String eHour = calendar2.get(Calendar.HOUR_OF_DAY) + "";

            if (sMonth.length() < 2) sMonth = "0" + sMonth;
            if (eMonth.length() < 2) eMonth = "0" + eMonth;
            if (sDay.length() < 2) sDay = "0" + sDay;
            if (eDay.length() < 2) eDay = "0" + eDay;
            if (sHour.length() < 2) sHour = "0" + sHour;
            if (eHour.length() < 2) eHour = "0" + eHour;
            //提取数据，并转出文件

            M4DataInterface m4DataInterface = new M4DataInterface();
            m4DataInterface.setIP(ipM4);
            m4DataInterface.setPORT(portM4);

            Iterator<String> it = m4dataList.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Map<String, Object> m4dataMap = (Map<String, Object>) m4dataList.get(key);
                Map<String, Object> outConfigList = (Map<String, Object>) m4dataMap.get("output");

                String m4dataType = (String) m4dataMap.get("datasetID");
                String m4dataName = (String) m4dataMap.get("datasetname");

                M4DataInterface.DataName dt = null;
                if (m4dataType.equalsIgnoreCase("SURFACE_TMP")) {
                    dt = M4DataInterface.DataName.SURFACE_TMP;
                } else if (m4dataType.equalsIgnoreCase("SURFACE_RAIN01")) {
                    dt = M4DataInterface.DataName.SURFACE_RAIN01;
                } else if (m4dataType.equalsIgnoreCase("SURFACE_RH")) {
                    dt = M4DataInterface.DataName.SURFACE_RH;
                } else if (m4dataType.equalsIgnoreCase("SPCC_Ground_TMP")) {
                    dt = M4DataInterface.DataName.SPCC_Ground_TMP;
                } else if (m4dataType.equalsIgnoreCase("SPCC_RAIN03")) {
                    dt = M4DataInterface.DataName.SPCC_RAIN03;
                } else if (m4dataType.equalsIgnoreCase("SURFACE_PLOT")) {
                    dt = M4DataInterface.DataName.SURFACE_PLOT_3H;
                }
                logger.info("通过M4服务器，提取数据文件" + dt + "....");
                int numfiles = 0;
                if (dt != null) {
                    List<String> fileList = m4DataInterface.GetDataByTimerange(dt,
                            M4DOWNLOADTMP,
                            sYear,
                            sMonth,
                            sDay,
                            sHour,
                            eYear,
                            eMonth,
                            eDay,
                            eHour
                    );

                    if (fileList != null && fileList.size() > 0) {
                        logger.info("获取M4目录列表成功，共有文件" + fileList.size());
                        numfiles = fileList.size();

                        //下载的临时文件列表
                        List<File> tmpFileList = new ArrayList<File>();
                        for (int i = 0; i < fileList.size(); i++) {
                            File tmpFile = new File(tmppath + "/" + fileList.get(i));
                            if (tmpFile.exists()) {
                                tmpFileList.add(tmpFile);
                            }
                        }

                        Iterator<String> itout = outConfigList.keySet().iterator();
                        while (itout.hasNext()) {
                            String outkey = itout.next();
                            Map<String, Object> outConfig = (Map<String, Object>) outConfigList.get(outkey);
                            String outType = (String) outConfig.get("type");

                            Map<String, Object> tcpServer = (Map<String, Object>) outConfig.get("tcpServer");

                            if ("TCPIP".equalsIgnoreCase(outType)) {
                                for (int i = 0; i < tmpFileList.size(); i++) {
                                    if (SendDataToTcpServer.sendFile(tmpFileList.get(i), tcpServer) == -1) {
                                        //未传成功，则重新下载再传
                                        tmpFileList.get(i).delete();
                                    }
                                }
                            } else {
                                if (tmpFileList.size() > 0) {
                                    saveFile(outConfig, tmpFileList, false);
                                }
                            }
                        }
                        String workInfo = m4dataName + "共有" + numfiles + "个数据文" + "";
                        new SendMsg2MonitorThread(workInfo).start();
                        logger.info("本次数据处理完成.......");
                    } else {
                        String workInfo = m4dataName + "没有需要下载的文件！";
                        new SendMsg2MonitorThread(workInfo).start();
                        logger.info(workInfo);
                    }
                }
            }
        }


        private boolean saveFile(Map<String, Object> saveConfig, List<File> fileList, boolean isdelete) throws Exception {
            boolean saveFlag = false;

            String outType = (String) saveConfig.get("type");

            if ("dir".equalsIgnoreCase(outType)) {
                String outputPath = (String) saveConfig.get("savePath");
                outputPath = changeFilePathDatePart(outputPath);
                saveFlag = saveFiles2Directory(fileList, outputPath);
            } else if ("ftp".equalsIgnoreCase(outType)) {
                saveFlag = saveFiles2Ftp(fileList, (Map<String, Object>) saveConfig.get("outFtp"));
            } else {
                //其他方式
            }
            //如果保存失败，将数据删除，重下重传
            if (!saveFlag || isdelete) {
                for (File f : fileList) {
                    //删除临时文件
                    if (isdelete) {
                        f.delete();
                    }
                }
            }
            return saveFlag;
        }

        /**
         * 保存文件到本地目录
         *
         * @throws
         * @Title: saveFiles2Directory
         * @param: @param fileList
         * @param: @param dirPath
         * @param: @throws IOException
         * @return: void
         */
        private boolean saveFiles2Directory(List<File> fileList, String dirPath) {
            boolean saveFlag = true;
            File dirFile = new File(dirPath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            for (File f : fileList) {
                try {
                    FileUtil.copyFile(f, new File(dirFile, f.getName()));
                    logger.info("文件保存成功：" + dirFile + "/" + f.getName());
                } catch (IOException e) {
                    saveFlag = false;
                    e.printStackTrace();
                }
            }
            return saveFlag;
        }

        /**
         * 保存文件到FTP
         *
         * @throws
         * @Title: saveFiles2Ftp
         * @param: @param fileList
         * @param: @param ftpMap
         * @param: @throws Exception
         * @return: void
         */
        private boolean saveFiles2Ftp(List<File> fileList, Map<String, Object> ftpMap) {
            boolean saveFlag = true;
            //创建FTP工具
            FtpUtil ftpUtil = null;
            String remotePath = (String) ftpMap.get("ftpPath");
            remotePath = changeFilePathDatePart(remotePath);

            try {
                ftpUtil = new FtpUtil(ftpMap.get("ftpServer") + "",
                        Integer.parseInt(ftpMap.get("ftpPort") + ""),
                        ftpMap.get("ftpUser") + "",
                        ftpMap.get("ftpPwd") + "",
                        remotePath);
                //登录FTP站点，如果目录不存在，则创建目录。
                ftpUtil.connectServerAndCreateDirecroty();
                logger.info("开始FTP上传");
                if (ftpUtil.isConnect()) {
                    for (File f : fileList) {
                        if (f.exists() && f.isFile()) {
                            Updata(ftpUtil, f, 0);
                        }
                    }
                }
            } catch (IOException e) {
                saveFlag = false;
                e.printStackTrace();
            } catch (Exception e) {
                saveFlag = false;
                e.printStackTrace();
            } finally {
                ftpUtil.closeServer();
            }
            return saveFlag;
        }

        private int Updata(FtpUtil ftpUtil, File f, int trancount) {
            if (trancount < 5) {
                try {
                    ftpUtil.upload(f.getAbsolutePath());
                    logger.info("文件FTP上传成功：" + f.getName());
                    return 0;
                } catch (Exception e) {
                    trancount++;
                    Updata(ftpUtil, f, trancount);
                    return -1;
                }
            } else {
                logger.info("文件FTP上传失败：" + f.getName());
                return -1;
            }
        }
    }
}
