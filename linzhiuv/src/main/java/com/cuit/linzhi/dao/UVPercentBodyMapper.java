package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.UVPercentBody;
import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UVPercentBodyMapper extends BaseMapper<UVPercentBody,Integer> {

    /**
     * 批量添加
     * @param uvPercentBodies
     * @return
     */
    Integer insertBatch(List<UVPercentBody> uvPercentBodies) throws DataAccessException;
}