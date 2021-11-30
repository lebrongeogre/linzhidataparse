package com.cuit.linzhi.dao;

import com.cuit.linzhi.vo.AstronomicalIrradianceBody;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper // 表示本类是一个 MyBatis 的 Mapper
@Repository
public interface AstronomicalIrradianceMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(AstronomicalIrradianceBody record);

    int insertSelective(AstronomicalIrradianceBody record);

    AstronomicalIrradianceBody selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AstronomicalIrradianceBody record);

    int updateByPrimaryKey(AstronomicalIrradianceBody record);


    int insertList(List<AstronomicalIrradianceBody> recordList);
}