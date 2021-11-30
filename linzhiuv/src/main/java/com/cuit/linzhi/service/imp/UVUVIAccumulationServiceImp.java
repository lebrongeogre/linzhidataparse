package com.cuit.linzhi.service.imp;


import com.cuit.linzhi.dao.StatisticsAllRadiationBodyMapper;
import com.cuit.linzhi.dao.StatisticsUVIParsedBodyMapper;
import com.cuit.linzhi.parse.imp.UVUVIAccumulationTableTransfer;
import com.cuit.linzhi.service.UVUVIAccumulationService;
import com.cuit.linzhi.vo.StatisticsAllRadiationBody;
import com.cuit.linzhi.vo.StatisticsUVIParsedBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UVUVIAccumulationServiceImp implements UVUVIAccumulationService {


    @Resource
    UVUVIAccumulationTableTransfer transfer;
    @Resource
    StatisticsAllRadiationBodyMapper statisticsAllRadiationBodyMapper;
    @Resource
    StatisticsUVIParsedBodyMapper statisticsUVIParsedBodyMapper;


    @Override
    public Integer insertList() {


        //按当前年月站点为条件查询t_sta_uvi表中紫外辐射辐照度数据
        List<StatisticsAllRadiationBody> statisticsAllRadiationBodies = statisticsAllRadiationBodyMapper.queryAllRecords();
        List<StatisticsUVIParsedBody> statisticsUVIParsedBodies = transfer.creatNewTableList(statisticsAllRadiationBodies);
        Integer ref = statisticsUVIParsedBodyMapper.insertBatch(statisticsUVIParsedBodies);

        return ref;
    }
}
