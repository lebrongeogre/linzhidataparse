package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsYiTaBody;

import java.util.List;

public interface StatisticsYiTaBodyMapper extends BaseMapper<StatisticsYiTaBody,Integer> {


    /**
     * 插入列表
     * @param statisticsYiTaBodies
     * @return
     */
    Integer insertList(List<StatisticsYiTaBody>statisticsYiTaBodies);


}