package com.cuit.linzhi.dao;

import com.cuit.linzhi.vo.TrueSolarTimeBody;

public interface TrueSolarTimeBodyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TrueSolarTimeBody record);

    int insertSelective(TrueSolarTimeBody record);

    TrueSolarTimeBody selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrueSolarTimeBody record);

    int updateByPrimaryKey(TrueSolarTimeBody record);
}