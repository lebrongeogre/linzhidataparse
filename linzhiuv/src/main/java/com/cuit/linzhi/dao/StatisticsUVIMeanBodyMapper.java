package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsUVIMeanBody;

import java.util.List;

public interface StatisticsUVIMeanBodyMapper extends BaseMapper<StatisticsUVIMeanBody,Integer> {

    int insertList(List<StatisticsUVIMeanBody> uviMeanBodies);
}