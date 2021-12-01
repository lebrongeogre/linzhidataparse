package com.cuit.mete.jobs.mete.job;

import com.cuit.job.basejob.BaseJob;
import com.cuit.job.utils.HttpUtil;
import com.cuit.job.utils.JsonUtil;
import com.cuit.mete.jobs.mete.db.WarnInteDao;
import com.cuit.mete.jobs.mete.domain.WarningItem;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ReadWebWarnMsg2DBProcess extends BaseJob {
    private static final Logger logger = Logger.getLogger(ReadWebWarnMsg2DBProcess.class);
    private Map<String, Object> config;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        startJob(jobExecutionContext);
    }

    public void startJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        //这是从上下文件获取从jobService中传过来的配置
        config = (Map<String, Object>) jobExecutionContext.getJobDetail().getJobDataMap().get("config");
        //获取配置文件中datainfo标签下的内容
        Map<String, Object> apiConfig = (Map<String, Object>) config.get("ApiConfig");
        if (apiConfig != null) {
            //获取文件路径map
            String url = (String) apiConfig.get("url");
            String userid = (String) apiConfig.get("id");
            String key = (String) apiConfig.get("key");
            if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(userid) && !StringUtils.isEmpty(key)) {
                //            <!--预警类型，如暴雨事件、雷电事件、地质灾害、高温事件、暴雨事件、森林火险-->
                //            <entry key="etypechnFilter"></entry>
                //            <!--red/orange/yellow/unknown之一-->
                //            <entry key="lvlFilter"></entry>
                //            <!--内容关键字（字符）-->
                //            <entry key="DescFilter"></entry>
                //            <!--区域，如成都、南充、石棉-->
                //            <entry key="areaFilter">雅安</entry>
                String etypechnFilter = null;
                String lvlFilter = null;
                String DescFilter = null;
                String areaFilter = null;

                Map<String, Object> filterConfig = (Map<String, Object>) config.get("FilterConfig");
                if (filterConfig != null) {
                    etypechnFilter = (String) filterConfig.get("etypechnFilter");
                    lvlFilter = (String) filterConfig.get("lvlFilter");
                    DescFilter = (String) filterConfig.get("DescFilter");
                    areaFilter = (String) filterConfig.get("areaFilter");
                }
                List<WarningItem>  listWarnItem = readWarnFromWeb(url, userid,key, etypechnFilter, lvlFilter, DescFilter, areaFilter);
                if (listWarnItem!= null) {
                    int rowdb = writeWarn2DB(listWarnItem);
                    logger.info("国突平台预警信息提取程序提取到"+listWarnItem.size()+"条数据,写入数据库："+rowdb+"条");
                } else {
                    logger.error("国突平台预警信息提取程序未提取到符合条件的信息！");
                }
            } else {
                logger.error("国突平台预警信息提取程序URL等配置不正确！");
            }
        } else {
            logger.error("国突平台预警信息提取程序配置不正确！");
        }
    }

    private int writeWarn2DB(List<WarningItem> listWarnItem) {
        int rowdb = 0;
        for (int i = 0; i < listWarnItem.size(); i++) {
            WarningItem warningItem =  listWarnItem.get(i);
            WarnInteDao warnInteDao = new WarnInteDao();
            WarningItem wt = warnInteDao.selectOneDataByWID(warningItem.getWid());
            if (wt == null) {
                int row = warnInteDao.insert(warningItem);
                if (row > 0) {
                    rowdb ++;
                    logger.debug("已插入一条预警信息：" + warningItem.getTitle());
                }
            } else {
                //表示更新
                if ("Update".equalsIgnoreCase(warningItem.getMtype())) {
                    int row = warnInteDao.update(warningItem);
                    if (row > 0) {
                        rowdb++;
                        logger.debug("已更新了一条预警信息：" + warningItem.getTitle());
                    }
                }
            }
        }
        return rowdb;
    }

    private List<WarningItem> readWarnFromWeb(String url, String userid,String key, String etypechnFilter, String lvlFilter, String DescFilter, String areaFilter) {
        List<WarningItem> needcdataList = null;
        String jsonStr = HttpUtil.getRequest(url + "?id=" + userid + "&key=" + key, null);
        Map<String, Object> mapFromJson = JsonUtil.getMapFromJson(jsonStr);
        if (mapFromJson != null && mapFromJson.size() >0) {
            Integer resultInt =(Integer) mapFromJson.get("returnCode");
            if (resultInt == 0) {
                logger.debug("从地址："+url+"取预警数据，返回结果代码"+ mapFromJson.get("returnCode") + "，条数：" +  mapFromJson.get("rowCnt") );
                List<Map<String, String>> cdataList = (List<Map<String, String>>) mapFromJson.get("cdata");

                if (cdataList != null && cdataList.size() > 0) {
                    needcdataList = new ArrayList<WarningItem>();
                    for (int i = 0; i < cdataList.size(); i++) {
                        WarningItem warningItem = null;
                        Map<String, String> warningMap = (Map<String, String>) cdataList.get(i);
                        try {
                            warningItem = new WarningItem();
                            BeanUtils.populate(warningItem, warningMap);
                        } catch (IllegalAccessException e) {
                        } catch (InvocationTargetException e) {
                        }
                        if (warningItem != null) {
                            boolean flag = true;
                            if (!StringUtils.isEmpty(etypechnFilter)) {
                                flag = flag && (warningItem.getEtypechn().indexOf(etypechnFilter) >= 0);
                            }
                            if (flag && !StringUtils.isEmpty(lvlFilter)) {
                                flag = flag && getLvlCon(lvlFilter, warningItem.getLvl());
                            }
                            if (flag && !StringUtils.isEmpty(DescFilter)) {
                                flag = flag && (warningItem.getEtypechn().indexOf(DescFilter) >= 0);
                            }
                            if (flag && !StringUtils.isEmpty(areaFilter)) {
                                flag = flag && (warningItem.getP1().indexOf(areaFilter) >= 0 || warningItem.getP2().indexOf(areaFilter) >= 0 || warningItem.getP3().indexOf(areaFilter) >= 0);
                            }

                            if (flag) {
                                needcdataList.add(warningItem);
                            }
                        }
                    }
                }
            } else {
                logger.debug("从地址："+url+"取数据不正确，返回结果代码"+ mapFromJson.get("returnCode"));
            }
        }
        return needcdataList;
    }

    private boolean getLvlCon(String lvlFilter, String warnLvl) {
        int intLvlFilter = 0;
        if ("red".equalsIgnoreCase(lvlFilter)) intLvlFilter = 9;
        if ("orange".equalsIgnoreCase(lvlFilter)) intLvlFilter = 8;
        if ("yellow".equalsIgnoreCase(lvlFilter)) intLvlFilter = 7;
        if ("unknown".equalsIgnoreCase(lvlFilter)) intLvlFilter = 0;

        int intWarnLvl = 0;
        if ("red".equalsIgnoreCase(warnLvl)) intWarnLvl = 9;
        if ("orange".equalsIgnoreCase(warnLvl)) intWarnLvl = 8;
        if ("yellow".equalsIgnoreCase(warnLvl)) intWarnLvl = 7;
        if ("unknown".equalsIgnoreCase(warnLvl)) intWarnLvl = 0;

        return intLvlFilter <= intWarnLvl;
    }

    public static void main(String[] args) {
        String url = "http://int.scqx.net/";
        String id = "1295";
        String key = "07ad1677c5f87ae2";
        new ReadWebWarnMsg2DBProcess().readWarnFromWeb(url, id, key, "","","", "四川");
    }
}
