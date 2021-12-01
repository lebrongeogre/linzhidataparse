package com.cuit.mete.thread;

import com.cuit.job.thread.JobThread;
import com.cuit.job.thread.SendMsg2MonitorThread;
import com.cuit.job.utils.ConstantUtil;
import com.cuit.job.utils.JsonFormatTool;
import com.cuit.mete.parse.TcpDataProcess;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

public class DataReceiveThread extends JobThread {
	private static final Logger logger = Logger.getLogger(DataReceiveThread.class);
	private Socket socket;
	private static final String charEncode = "UTF-8";
	private String tcpSavePath;
	private String encryptionType;

	/**
	 * TCPIP接收数据
	 * @param socket socket
	 * @param encryptionType 加密数据类型
	 * @param tcpSavePath 文件存放路径
	 */
	public DataReceiveThread(Socket socket, String encryptionType, String tcpSavePath){
		this.socket = socket;
		this.encryptionType = encryptionType;
		this.tcpSavePath = tcpSavePath;
	}
	
	public void run() {
		try {
			//获取输入流
			InputStream in = socket.getInputStream();
			//获取输出流
			OutputStream out = socket.getOutputStream();

			//读取数据
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			//先读数据头

			byte[] buff = new byte[512];
			int len = in.read(buff);
			while (len > 0) {
				bos.write(buff, 0, len);
				len = in.read(buff);
			}

			//读取完成关闭输入流
			socket.shutdownInput();
			//返回结果
			out.write("ok".getBytes());
			out.flush();
			socket.shutdownOutput();
			socket.close();

			//解析数据
			byte[] data = bos.toByteArray();

			int curPos = 0;

			if (data.length > TcpServerConstant.encryptionTypeLen + TcpServerConstant.dataTypeLen + TcpServerConstant.fileNameLen + TcpServerConstant.stringFormatLen + TcpServerConstant.crcLen) {

				//加密方式
				String encryptionType = new String(Arrays.copyOfRange(data, curPos, curPos + TcpServerConstant.encryptionTypeLen)).trim();
				curPos += TcpServerConstant.encryptionTypeLen;
				//System.out.println(encryptionType);

				//处理程序
				String dataType = new String(Arrays.copyOfRange(data, curPos, curPos + TcpServerConstant.dataTypeLen)).trim();
				curPos += TcpServerConstant.dataTypeLen;
				//System.out.println(dataType);

				//写出文件名
				String fileName = new String(Arrays.copyOfRange(data, curPos, curPos + TcpServerConstant.fileNameLen)).trim();
				curPos += TcpServerConstant.fileNameLen;
				//System.out.println(fileName);

				//写出格式
				String stringFromat = new String(Arrays.copyOfRange(data, curPos, curPos + TcpServerConstant.stringFormatLen)).trim();
				curPos += TcpServerConstant.stringFormatLen;


				//CRC
				String crcString = new String(Arrays.copyOfRange(data, curPos, curPos + TcpServerConstant.crcLen)).trim();
				curPos += TcpServerConstant.crcLen;
				//System.out.println(crcString);

				Map<String, Object> jobInfo = (Map<String, Object>) ConstantUtil.get("TcpDataProcess");
				Map<String, Object> dataConfigList = (Map<String, Object>) jobInfo.get("data");
				Map<String, Object> dataConfig = (Map<String, Object>) dataConfigList.get(dataType);
				if (dataConfig != null) {
					String processClass = (String) dataConfig.get("processClass");
					String processClassName = (String) dataConfig.get("datasetname");
					if (StringUtils.isEmpty(processClassName)) {
						processClassName = processClass;
					}
					Map<String, Object> outConfig = (Map<String, Object>) dataConfig.get("output");
					Iterator<String> itout = outConfig.keySet().iterator();
					//如果输出定义中，存在文件定义，则输出文件
					String saveFile = "";
					while (itout.hasNext()) {
						String outkey = itout.next();
						Map<String, Object> outPutInfo = (Map<String, Object>) outConfig.get(outkey);
						if ("dir".equalsIgnoreCase(outPutInfo.get("type") + "")) {
							String savePath = (String) outPutInfo.get("savePath");
							if (!savePath.isEmpty() && savePath != "" && !fileName.isEmpty() && fileName != "") {
								Calendar curdate = Calendar.getInstance();
								savePath = ConstantUtil.changeFilePathDatePart(savePath, curdate);

								//保存文件
								File path = new File(savePath);
								if (!path.exists()) {
									path.mkdirs();
								}
								File file = new File(path, fileName);
								FileOutputStream fos = new FileOutputStream(file);
								//是否需要对内容进行格式化
								if ("JSON".equalsIgnoreCase(stringFromat)) {
									String content = new String(Arrays.copyOfRange(data, curPos, data.length), charEncode);
									fos.write(JsonFormatTool.formatJson(content).getBytes(charEncode));
								} else {
									fos.write(Arrays.copyOfRange(data, curPos, data.length));
								}
								fos.close();
								saveFile = "保存文件" + savePath + "\\" + fileName + ",";
							}
						}
					}
					String saveDbRec = "";
					if (!processClass.isEmpty() && processClass != "") {
						try {
							//根据处理程序来处理
							String content = new String(Arrays.copyOfRange(data, curPos, data.length), charEncode);

							Constructor c = Class.forName(processClass).getConstructor(String.class, String.class, Map.class);
							TcpDataProcess dataProcess = (TcpDataProcess) c.newInstance(content, dataType, outConfig);
							int r = dataProcess.execute();

							if (r == -1) {
								logger.error(processClassName + "处理数据失败");
								logger.debug(processClassName + "处理数据失败:" + content);
							} else {
								logger.info(processClassName + "处理数据成功，条数：" + r);
								saveDbRec = "处理记录" + r + "条!";
							}
						} catch (Exception e) {
							logger.debug(processClassName + "处理数据异常:" + e.getMessage());
						}
					}

					bos.close();

					String workInfo = "接收到" + dataType + "数据，" + saveFile + saveDbRec;
					new SendMsg2MonitorThread(workInfo).start();
					logger.info(workInfo);
				} else {
					logger.error("接收到数据" + dataType + "，处理类型节点未定义");
				}
			} else {
				logger.error("接收到的数据，格式不对（长度不符）");
			}
		} catch (Exception e) {
			logger.error("接收到数据，处理出错：" + e.getMessage());
		}
	}
}
