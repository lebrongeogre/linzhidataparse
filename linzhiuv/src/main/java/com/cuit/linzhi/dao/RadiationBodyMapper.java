package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.MaxIdBody;
import com.cuit.linzhi.vo.RadiationBody;

import java.util.List;

public interface RadiationBodyMapper extends BaseMapper<RadiationBody,Integer> {

    List<RadiationBody> queryAllRecords();
}