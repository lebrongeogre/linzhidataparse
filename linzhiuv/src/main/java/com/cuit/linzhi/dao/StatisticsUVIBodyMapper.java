package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.RadiationBody;
import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVPercentBody;

import java.util.List;

public interface StatisticsUVIBodyMapper extends BaseMapper<StatisticsUVIBody,Integer> {



    /**
     * 查询所有所有数据
     * @return
     */
    List<StatisticsUVIBody> queryAllRecords();

}