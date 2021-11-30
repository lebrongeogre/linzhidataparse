package com.cuit.linzhi.service.imp;



import com.cuit.linzhi.dao.RadiationMonthBodyMapper;
import com.cuit.linzhi.dao.StatisticsMonthAccumulationBodyMapper;
import com.cuit.linzhi.parse.imp.AccumulationTableTransfer;
import com.cuit.linzhi.service.AccumulationParsedService;


import com.cuit.linzhi.vo.RadiationMonthBody;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 将月累积量（t_radiation_monthaccumulation）转换格式存储
 */
@Service
public class AccumulationParsedServiceImp implements AccumulationParsedService {

    @Resource
    RadiationMonthBodyMapper radiationMonthBodyMapper;
    @Resource
    AccumulationTableTransfer accumulationTableTransfer;
    @Resource
    StatisticsMonthAccumulationBodyMapper statisticsMonthAccumulationBodyMapper;


    public Integer insertBatch(){
        //获取处理前数据集
        List<RadiationMonthBody> radiationMonthBodies = radiationMonthBodyMapper.queryAllRecords();
        //调用方法，生成新的数据集
        List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies = accumulationTableTransfer.creatNewTableList(radiationMonthBodies);
        //插入数据库
        int ref = 0;
        for (StatisticsMonthAccumulationBody sta : statisticsMonthAccumulationBodies) {
            ref = statisticsMonthAccumulationBodyMapper.insertSingleTerm(sta);
            ref = ref*ref;
        }
        return ref;
    }

}
