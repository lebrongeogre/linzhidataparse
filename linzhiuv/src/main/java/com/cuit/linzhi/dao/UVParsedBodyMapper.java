package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVParsedBody;
import com.cuit.linzhi.vo.UVPercentBody;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UVParsedBodyMapper extends BaseMapper<UVParsedBody,Integer> {

    /**
     * 单次插入
     * @param uvParsedBody
     * @return
     */
    Integer insertSingleTerm(UVParsedBody uvParsedBody) throws DataAccessException;



    /**
     * 根据传入条件动态查询
     * @param yearIndex
     * @param stationIndex
     * @param monthIndex
     * @return
     */
    List<UVParsedBody> queryByTerm(String yearIndex, String stationIndex, String monthIndex);

    /**
     * 根据传入条件动态查询
     * @param stationIndex
     * @param monthIndex
     * @return
     */
    List<UVParsedBody> queryByTwoTerm(String stationIndex, String monthIndex);

    Integer insertBatch(List<UVParsedBody> uvParsedBodies) throws DataAccessException;


}