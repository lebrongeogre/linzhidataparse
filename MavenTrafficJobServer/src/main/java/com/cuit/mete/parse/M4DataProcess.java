package com.cuit.mete.parse;

import cn.gov.cma.cimiss.gds.service.M4DataInterface;
import com.cuit.job.basejob.BaseJob;
import com.cuit.job.thread.SendMsg2MonitorThread;
import com.cuit.job.utils.FileUtil;
import com.cuit.job.utils.FtpUtil;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 从M4上取数据，并进行存储
 */
@DisallowConcurrentExecution
public class M4DataProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(M4DataProcess.class);
    private static final String FTPDOWNLOADTMP = "ftpdownload";
    private static final String M4DOWNLOADTMP = "M4download";
    private Map<String, Object> config;
    int upcount;
    int failcount;
    int totalcount;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }

    public void startJob(JobExecutionContext context) throws JobExecutionException {
        //这是从上下文件获取从jobService中传过来的配置
    	upcount=0;
        config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
        Calendar calendar = Calendar.getInstance();

        //另外也通过jobName来获取配置
        //String jobName = context.getJobDetail().getKey().getName();
        //config = (Map<String, Object>)ConstantUtil.get(jobName);

        //获取目录类型
        String type = config.get("type") + "";
        try {
            if ("ftp".equalsIgnoreCase(type)) {
                receiveFromFtp();
            } else if ("dir".equalsIgnoreCase(type)) {
                receiveByDirectory();
            } else if ("M4".equalsIgnoreCase(type)) {
                //从M4服务器中取数据
                receiveByM4(calendar);
            }
        } catch (Exception e) {
            logger.error("接收文件出错：" + e.getMessage(), e);
        }
    }

    public void executeHis(Map<String, Object> dataconfig, Calendar calendar) {
        //这是从上下文件获取从jobService中传过来的配置
        config = (Map<String, Object>) dataconfig;

        //另外也通过jobName来获取配置
        //String jobName = context.getJobDetail().getKey().getName();
        //config = (Map<String, Object>)ConstantUtil.get(jobName);

        //获取目录类型
        String type = config.get("type") + "";
        try {
            if ("ftp".equalsIgnoreCase(type)) {
                receiveFromFtp();
            } else if ("dir".equalsIgnoreCase(type)) {
                receiveByDirectory();
            } else if ("M4".equalsIgnoreCase(type)) {
                //从M4服务器中取数据
                receiveByM4(calendar);
            }
        } catch (Exception e) {
            logger.error("接收文件出错：" + e.getMessage(), e);
        }
    }

    private void receiveByM4( Calendar calendar) throws Exception {
        //创建下载临时目录
        File tmppath = new File(M4DOWNLOADTMP);
        if (!tmppath.exists()) {
            tmppath.mkdirs();
        }
        logger.info("通过M4服务器，提取数据文件....");
        Map<String, Object> dataMapM4 = (Map<String, Object>) config.get("data");
        String ipM4 = (String) dataMapM4.get("ip");
        String portM4 = (String) dataMapM4.get("Port");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(calendar.getTime());
        Calendar calendar2 =  Calendar.getInstance();
        calendar2.setTime(calendar.getTime());

        calendar1.add(Calendar.HOUR_OF_DAY, -24);
        String sYear = calendar1.get(Calendar.YEAR) + "";
        String sMonth = (calendar1.get(Calendar.MONTH) + 1)+"";
        String sDay = calendar1.get(Calendar.DAY_OF_MONTH) + "";
        String sHour = calendar1.get(Calendar.HOUR_OF_DAY) + "";
        String eYear = calendar2.get(Calendar.YEAR) + "";
        String eMonth = (calendar2.get(Calendar.MONTH) + 1)+"";
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

        Map<String, Object>  m4dataList = (Map<String, Object>) dataMapM4.get("M4Type");

        Iterator<String> it = m4dataList.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Map<String, Object> m4dataMap = (Map<String, Object>) m4dataList.get(key);
            String m4dataType = (String) m4dataMap.get("datasetname");

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
            logger.info("通过M4服务器，提取数据文件"+dt+"....");
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
                    totalcount=fileList.size();
                    //下载的临时文件列表
                    List<File> tmpFileList = new ArrayList<File>();

                    for (int i = 0; i < fileList.size(); i++) {
                        File tmpFile = new File(tmppath + "/" + fileList.get(i));
                        if (tmpFile.exists()) {
                            tmpFileList.add(tmpFile);
                        }
                    }
                    if (tmpFileList.size() > 0) {
                        saveFile(m4dataMap, tmpFileList, false);
                    }
                }

            }
        }
        String workInfo="本次M4数据处理结果:共有"+totalcount+"个数据提取,有" + upcount + "个数据提取成功,有"+failcount+"提取失败";
        new SendMsg2MonitorThread(workInfo).start();
        logger.info("本次数据处理完成.......");
    }

    private void receiveByDirectory() throws Exception {
        logger.info("通过文件夹方式获取文件....");
        String remotePath = config.get("remotePath") + "";
        remotePath = changeFilePathDatePart(remotePath);
        File dirFile = new File(remotePath);
        if (dirFile.exists()) {
            List<File> fileList = new ArrayList<File>(Arrays.asList(dirFile.listFiles()));
            saveFile(config, fileList, false);
        } else {
            logger.error("接收文件夹" + remotePath + "不存在");
        }
    }

    /**
     * 按FTP方式接收文件
     *
     * @throws Exception
     */
    private void receiveFromFtp() throws Exception {
        logger.info("通过FTP方式获取文件....");

        Map<String, Object> ftpMap = (Map<String, Object>) config.get("receiveFtp");
        String remotePath = (String) ftpMap.get("ftpPath");
        remotePath = changeFilePathDatePart(remotePath);

        //创建FTP工具
        FtpUtil ftpUtil = new FtpUtil(ftpMap.get("ftpServer") + "",
                Integer.parseInt(ftpMap.get("ftpPort") + ""),
                ftpMap.get("ftpUser") + "",
                ftpMap.get("ftpPwd") + "",
                remotePath);
        //登录FTP站点
        ftpUtil.connectServer();

        if (ftpUtil.isConnect()) {
            //获取目录文件
            String ftpPath = config.get("ftpPath") + "";
            FTPFile[] FtpFiles = ftpUtil.getFtpFiles(ftpPath);
            if (FtpFiles == null) {
                logger.error("获取FTP目录：" + config.get("ftpPath") + "中的文件失败");
                return;
            } else if (FtpFiles.length == 0) {
                logger.info("FTP目录：" + config.get("ftpPath") + "中没有文件");
                return;
            }

            logger.info("获取FTP目录列表成功，共有文件" + FtpFiles.length);


            //创建下载临时目录
            File tmppath = new File(FTPDOWNLOADTMP);
            if (!tmppath.exists()) {
                tmppath.mkdirs();
            }

            //下载的临时文件列表
            List<File> tmpFileList = new ArrayList<File>();
            for (FTPFile file : FtpFiles) {
                if (file.getType() == 0) {
                    //下载文件到临时目录
                    File tmpFile = new File(tmppath, file.getName());
                    ftpUtil.download(file.getName(), tmpFile);
                    logger.info("下载文件" + file.getName() + "成功");
                    tmpFileList.add(tmpFile);
                }
            }
            //关闭FTP连接
            ftpUtil.closeFtp();

            //将下载的临时文件保存到目录目录
            if (tmpFileList.size() > 0) {
                saveFile(config, tmpFileList, true);
            }
        } else {
            logger.error("无法登陆到FTP服务器：" + config.get("ftpServer"));
        }
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
    private boolean saveFile(Map<String, Object> saveConfig, List<File> fileList, boolean isdelete) throws Exception {
        boolean saveFlag  = false;
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
        boolean saveFlag  = true;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        for (File f : fileList) {
            try {
                FileUtil.copyFile(f, new File(dirFile, f.getName()));
                logger.info("文件保存成功：" + dirFile + "/"+f.getName());
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
        boolean saveFlag  = true;
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
                    if(f.exists() && f.isFile()) {
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
}
