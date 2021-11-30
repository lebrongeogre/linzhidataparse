package com.cuit.linzhi.service.imp;


import com.cuit.linzhi.dao.StatisticsUVIBodyMapper;
import com.cuit.linzhi.dao.UVParsedBodyMapper;

import com.cuit.linzhi.parse.AccumulationTableTransfer;
import com.cuit.linzhi.parse.UVITableTransfer;
import com.cuit.linzhi.service.UVParsedService;

import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UVParsedServiceImp implements UVParsedService {

    @Resource
    StatisticsUVIBodyMapper statisticsUVIBodyMapper;
    @Resource
    UVParsedBodyMapper uvParsedBodyMapper;
    @Resource
    UVITableTransfer uviTableTransfer;


    public Integer insertBatch(){

        //获取处理前数据集
        List<StatisticsUVIBody> statisticsUVIBodies = statisticsUVIBodyMapper.queryAllRecords();
        //调用方法，生成新的数据集
        List<UVParsedBody> uvParsedBodies = uviTableTransfer.creatNewTableList(statisticsUVIBodies);
        //插入数据库
        int ref = 0;
        for (UVParsedBody uvParsedBody : uvParsedBodies) {
             ref = uvParsedBodyMapper.insertSingleTerm(uvParsedBody);
            ref = ref*ref;
        }
        return ref;
    }

}
