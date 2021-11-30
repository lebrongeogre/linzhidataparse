package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsAllRadiationBody;

import java.util.List;

public interface StatisticsAllRadiationBodyMapper extends BaseMapper<StatisticsAllRadiationBody,Integer> {

    List<StatisticsAllRadiationBody> queryAllRecords();

}