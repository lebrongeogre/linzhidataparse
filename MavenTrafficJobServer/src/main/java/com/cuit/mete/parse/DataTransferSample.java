package com.cuit.mete.parse;

import com.cuit.job.basejob.BaseJob;
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

@DisallowConcurrentExecution
public class DataTransferSample extends BaseJob {
	
	private static final Logger logger = Logger.getLogger(DataTransferSample.class);
	private static final String FTPDOWNLOADTMP = "ftpdownload";
	private Map<String, Object> config;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		startJob(jobExecutionContext);
	}
	public void startJob(JobExecutionContext context) throws JobExecutionException {
		//这是从上下文件获取从jobService中传过来的配置
		config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
		//通过jobName来获取配置
		//String jobName = context.getJobDetail().getKey().getName();
		//config = (Map<String, Object>)ConstantUtil.get(jobName);
		
		//获取目录类型
		String type = config.get("type") + "";
		try{
			if("ftp".equalsIgnoreCase(type)){
				receiveFromFtp();
			}else if("dir".equalsIgnoreCase(type)){
				receiveByDirectory();
			}else{
				//其他接收方式
			}
		}catch(Exception e){
			logger.error("接收文件出错：" + e.getMessage(), e);
		}
	}
	
	private void receiveByDirectory() throws Exception{
		logger.info("通过文件夹方式获取文件....");
		String remotePath = config.get("remotePath") + "";
		File dirFile = new File(remotePath);
		if(dirFile.exists()){
			List<File> fileList = new ArrayList<File>(Arrays.asList(dirFile.listFiles()));
			saveFile(fileList, false);
		}else{
			logger.error("接收文件夹" + remotePath + "不存在");
		}
	}
	
	/**
	 * 按FTP方式接收文件
	 * @param config
	 * @throws Exception 
	 */
	private void receiveFromFtp() throws Exception{
		logger.info("通过FTP方式获取文件....");
		
		Map<String, Object> ftpMap = (Map<String, Object>) config.get("receiveFtp"); 
		//创建FTP工具
		FtpUtil ftpUtil = new FtpUtil(ftpMap.get("ftpServer") + "",
				Integer.parseInt(ftpMap.get("ftpPort") + ""), 
				ftpMap.get("ftpUser") + "", 
				ftpMap.get("ftpPwd") + "",
				ftpMap.get("ftpPath") + "");
		//登录FTP站点
		ftpUtil.connectServer();
		
		if(ftpUtil.isConnect()){
			//获取目录文件
			String ftpPath = config.get("ftpPath") + "";
			FTPFile[] FtpFiles = ftpUtil.getFtpFiles(ftpPath);
			if(FtpFiles == null){
				logger.error("获取FTP目录：" + config.get("ftpPath") + "中的文件失败");
				return;
			}else if(FtpFiles.length == 0){
				logger.info("FTP目录：" + config.get("ftpPath") + "中没有文件");
				return;
			}
			
			logger.info("获取FTP目录列表成功，共有文件" + FtpFiles.length);
			
			
			//创建下载临时目录
			File tmppath = new File(FTPDOWNLOADTMP);
			if(!tmppath.exists()){
				tmppath.mkdirs();
			}
			
			//下载的临时文件列表
			List<File> tmpFileList = new ArrayList<File>();
			for(FTPFile file : FtpFiles){
				if(file.getType() == 0){
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
			if(tmpFileList.size() > 0){
				saveFile(tmpFileList, true);
			}
		}else{
			logger.error("无法登陆到FTP服务器：" + config.get("ftpServer"));
		}
	}
	
	/**
	 * 保存文件
	 * @Title: saveFile   
	 * @param: @param fileList
	 * @param: @throws Exception      
	 * @return: void      
	 * @throws
	 */
	private void saveFile(List<File> fileList, boolean isdelete) throws Exception{
		//获取输出路径
		Map<String, Object> outConfig = (Map<String, Object>) config.get("output");
		Iterator<String> it = outConfig.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			Map<String, Object> outPutInfo = (Map<String, Object>) outConfig.get(key);
			if("dir".equals(outPutInfo.get("type") + "")){
				saveFiles2Directory(fileList, outPutInfo.get("savePath") + "", isdelete);
			}else if("ftp".equals(outPutInfo.get("type") + "")){
				saveFiles2Ftp(fileList, (Map<String, Object>)outPutInfo.get("outFtp"), isdelete);
			}else{
				//其他方式
			}
	
		}
	}
	
	/**
	 * 保存文件到本地目录
	 * @Title: saveFiles2Directory   
	 * @param: @param fileList
	 * @param: @param dirPath
	 * @param: @throws IOException      
	 * @return: void      
	 * @throws
	 */
	private void saveFiles2Directory(List<File> fileList, String dirPath, boolean isdelete) throws IOException{
		File dirFile = new File(dirPath);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		for(File f : fileList){
			FileUtil.copyFile(f, new File(dirFile, f.getName()));
			//删除临时文件
			if(isdelete){
				f.delete();
			}
		}
	}
	
	/**
	 * 保存文件到FTP
	 * @Title: saveFiles2Ftp   
	 * @param: @param fileList
	 * @param: @param ftpMap
	 * @param: @throws Exception      
	 * @return: void      
	 * @throws
	 */
	private void saveFiles2Ftp(List<File> fileList, Map<String, Object> ftpMap, boolean isdelete) throws Exception{
		//创建FTP工具
		FtpUtil ftpUtil = new FtpUtil(ftpMap.get("ftpServer") + "",
				Integer.parseInt(ftpMap.get("ftpPort") + ""), 
				ftpMap.get("ftpUser") + "", 
				ftpMap.get("ftpPwd") + "",
				ftpMap.get("ftpPath") + "");
		//登录FTP站点
		ftpUtil.connectServer();
		
		if(ftpUtil.isConnect()){
			for(File f : fileList){
				ftpUtil.upload(f.getAbsolutePath());
				//删除临时文件
				if(isdelete){
					f.delete();
				}
			}
		}
	}
}
