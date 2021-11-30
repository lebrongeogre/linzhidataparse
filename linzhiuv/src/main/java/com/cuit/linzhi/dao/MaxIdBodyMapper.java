package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.MaxIdBody;
import org.springframework.stereotype.Repository;

public interface MaxIdBodyMapper extends BaseMapper<MaxIdBody,Integer> {

    Integer queryMaxID();

    Integer InsertMaxId();

}