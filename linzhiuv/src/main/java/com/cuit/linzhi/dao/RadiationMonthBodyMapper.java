package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;

import com.cuit.linzhi.vo.RadiationMonthBody;

import java.util.List;

public interface RadiationMonthBodyMapper extends BaseMapper<RadiationMonthBody,Integer> {
    List<RadiationMonthBody> queryAllRecords();
}