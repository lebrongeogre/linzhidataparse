package com.cuit.linzhi.dao;

import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TrueMeanTimeDifferenceMapper {

    int insert(TrueMeanTimeDifferenceBody record);

    int insertSelective(TrueMeanTimeDifferenceBody record);

    // 查询时差表全部记录
    List<TrueMeanTimeDifferenceBody> queryAllRecords();

}