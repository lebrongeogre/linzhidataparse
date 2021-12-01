package com.cuit.mete.thread;

import com.cuit.job.thread.JobThread;
import com.cuit.job.utils.ConstantUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class TCPServerThread extends JobThread {
	private static final Logger logger = Logger.getLogger(TCPServerThread.class);
	
	public void run(){
		Map<String, Object> tcpipServerConfig = (Map<String, Object>) ConstantUtil.get("tcpipServer");
		String tcpPort = (String) tcpipServerConfig.get("tcpPort");
		String tcpSavePath = (String) tcpipServerConfig.get("tcpSavePath");
		String encryptionType = (String) tcpipServerConfig.get("encryption");

		if(StringUtils.isEmpty(tcpPort)){
			logger.error("未配置TCP端口");
		}else{
			ServerSocket server = null;
			try {
				int port = Integer.parseInt(tcpPort);
				server = new ServerSocket(port);
				server.setSoTimeout(10000);
				while(!stopFlag){
					try {
						Socket sc = server.accept();
						new DataReceiveThread(sc, encryptionType, tcpSavePath).start();
					}catch (IOException e) {
						//超时
						//logger.debug(tcpPort + "端口监听"+e.getMessage());
					}
				}
				logger.info(tcpPort + "端口监听结束！");
			} catch (Exception e) {
				logger.error("启动TCP服务出错：" + e.getMessage(), e);
			}
			finally {
				if (server != null) {
					try {
						server.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}
}
