package com.cuit.mete.parse;

import com.cuit.job.basejob.BaseJob;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

@DisallowConcurrentExecution
public class SeqProcessSample extends BaseJob {
	
	private static final Logger logger = Logger.getLogger(SeqProcessSample.class);
	
	private Map<String, Object> config;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		startJob(jobExecutionContext);
	}
	public void startJob(JobExecutionContext context) throws JobExecutionException {
		config = (Map<String, Object>) context.getJobDetail().getJobDataMap().get("config");
		try {
			//实例化预处理类
			PreProcessBase preProcess = (PreProcessBase) Class.forName(config.get("preProcessClass") + "").newInstance();
			
			//获取处理后的文件
			String fileName = preProcess.getDataFile();
			
			//处理数据
			
			//实例化数据输出类
			OutputProcessBase outputProcess = (OutputProcessBase) Class.forName(config.get("outputProcessClass") + "").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
