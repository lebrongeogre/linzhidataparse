package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsUVIParsedBody;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface StatisticsUVIParsedBodyMapper extends BaseMapper<StatisticsUVIParsedBody,Integer> {

    @Override
    Integer insertBatch(List<StatisticsUVIParsedBody> entities) throws DataAccessException;
}