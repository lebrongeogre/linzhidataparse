package com.cuit.mete.thread;

import com.cuit.job.thread.SendMsg2MonitorThread;
import com.cuit.job.utils.ConstantUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class SendDataToTcpServer {

	private static final Logger logger = Logger.getLogger(SendDataToTcpServer.class);
	private static final String charEncode = "UTF-8";

	/**
	 * 发送数据
	 * @param content 文本串
	 * @param tcpServer 配置
	 */
	public static int sendString(String content, Map<String, Object> tcpServer, String saveFileName, boolean jsonFormat) {
		int ret = 0;
		//连接TCP服务
		try {
			Socket socket = new Socket(tcpServer.get("ip") + "", Integer.parseInt(tcpServer.get("port") + ""));

			//获取输出流
			OutputStream out = socket.getOutputStream();

			//加密方式
			String encryptionType = fillSpace((String) tcpServer.get("encryption"), TcpServerConstant.encryptionTypeLen);
			out.write(encryptionType.getBytes(charEncode));

			//加密方式
			String dataType =fillSpace((String) tcpServer.get("dataType"), TcpServerConstant.dataTypeLen);
			out.write(dataType.getBytes(charEncode));

			//存储文件名
			String fileName = fillSpace(saveFileName, TcpServerConstant.fileNameLen);
			out.write(fileName.getBytes(charEncode));

			String jsonFormatString = "";
			//是否用jsonFormat
			if (jsonFormat) {
				jsonFormatString = fillSpace("JSON", TcpServerConstant.stringFormatLen);
			} else {
				jsonFormatString = fillSpace("NO", TcpServerConstant.stringFormatLen);
			}
			out.write(jsonFormatString.getBytes(charEncode));

			//Crc校验
			String crcString = fillSpace("", TcpServerConstant.crcLen);
			out.write(crcString.getBytes(charEncode));


			//将内容转为字符串
			byte[] data = content.getBytes(charEncode);
			out.write(data);

			//关闭输出流
			out.flush();
			socket.shutdownOutput();

			//接收返回
			InputStream in = socket.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buff = new byte[512];
			int len = in.read(buff);
			while (len > 0) {
				bos.write(buff, 0, len);
				len = in.read(buff);
			}

			//返回结果
			//System.out.println("result:" + new String(bos.toByteArray(), charEncode));

			String workInfo = "往"+tcpServer.get("ip") + ":"+tcpServer.get("port")  + "发送数据, 返回结果" + new String(bos.toByteArray(), charEncode);
			new SendMsg2MonitorThread(workInfo).start();
			logger.info(workInfo);
			System.out.println(workInfo);

			//关闭输入
			socket.shutdownInput();
			//关闭连接
			socket.close();

		} catch (Exception e) {
			logger.error("通过TCP服务发送数据失败:" + e.getMessage(), e);
			ret = -1;
		}
		return ret;
	}

	public static int sendFile(File file, Map<String, Object> tcpServer) {
		int ret = 0;
		//连接TCP服务
		try {
			Socket socket = new Socket(tcpServer.get("ip") + "", Integer.parseInt(tcpServer.get("port") + ""));
			//获取输出流
			OutputStream out = socket.getOutputStream();

			//加密方式
			String encryptionType = fillSpace((String) tcpServer.get("encryption"), TcpServerConstant.encryptionTypeLen);
			out.write(encryptionType.getBytes(charEncode));

			//加密方式
			String dataType =fillSpace((String) tcpServer.get("dataType"), TcpServerConstant.dataTypeLen);
			out.write(dataType.getBytes(charEncode));


			//存储文件名
			String fileName = fillSpace(file.getName(), TcpServerConstant.fileNameLen);
			out.write(fileName.getBytes(charEncode));

			//文件不格式化
			String jsonFormatString = fillSpace("NO", TcpServerConstant.stringFormatLen);
			out.write(jsonFormatString.getBytes(charEncode));


			//Crc校验
			String crcString = fillSpace("", TcpServerConstant.crcLen);
			out.write(crcString.getBytes(charEncode));

			byte[] buff;
			//读取文件并写出
			FileInputStream fis = new FileInputStream(file);
			buff = new byte[512];
			int len = fis.read(buff);
			while (len > 0) {
				out.write(buff, 0, len);
				len = fis.read(buff);
			}
			fis.close();

			//关闭输出流
			out.flush();
			socket.shutdownOutput();

			//接收返回
			InputStream in = socket.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			len = in.read(buff);
			while (len > 0) {
				bos.write(buff, 0, len);
				len = in.read(buff);
			}

			//返回结果
			//System.out.println("result:" + new String(bos.toByteArray(), charEncode));
			String workInfo = "往"+tcpServer.get("ip") + ":"+tcpServer.get("port")  + "发送文件"+file.getName()+", 返回结果" + new String(bos.toByteArray(), charEncode);
			new SendMsg2MonitorThread(workInfo).start();
			logger.info(workInfo);
			System.out.println(workInfo);

			//关闭输入
			socket.shutdownInput();
			//关闭连接
			socket.close();

		} catch (Exception e) {
			logger.error("通过TCP服务发送数据失败:" + e.getMessage(), e);
			ret = -1;
		}
		return ret;
	}

	private static String fillSpace(String str, int len) {
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		for (int i = str.length() - 1; i < len; i++) {
			sb.append(" ");
		}
		return sb.substring(0, len);
	}


	public static void main(String[] args) {
		File f = new File("D:\\Temp\\Cimiss-SurfEle-0001-20201022230000.json");
		Map<String, Object> tcpServer1 = (Map<String, Object>) ConstantUtil.get("ReadSendCimssDataProcess");
		Map<String, Object> cimissDataList = (Map<String, Object>) tcpServer1.get("data");
		Map<String, Object> cimissdataMap = (Map<String, Object>) cimissDataList.get("dataset1");
		Map<String, Object> outPutInfo = (Map<String, Object>) cimissdataMap.get("output");
		Map<String, Object> outPutInfo1 = (Map<String, Object>) outPutInfo.get("out1");
		Map<String, Object> tcpServer = (Map<String, Object>) outPutInfo1.get("tcpServer");

		sendFile(f, tcpServer);
	}
}