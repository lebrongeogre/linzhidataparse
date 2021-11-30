package com.cuit.linzhi.dao;

import com.cuit.linzhi.base.BaseMapper;
import com.cuit.linzhi.vo.StatisticsMonthAccumulationBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface StatisticsMonthAccumulationBodyMapper extends BaseMapper<StatisticsMonthAccumulationBody,Integer> {

    /**
     * 单次插入
     * @param statisticsMonthAccumulationBody
     * @return
     */
    Integer insertSingleTerm(StatisticsMonthAccumulationBody statisticsMonthAccumulationBody) throws DataAccessException;

    /**
     * 批量插入
     * @param statisticsMonthAccumulationBodies
     * @return
     */
    Integer insertBatch(List<StatisticsMonthAccumulationBody> statisticsMonthAccumulationBodies);


    /**
     * 根据传入条件动态查询
     * @param stationIndex
     * @param monthIndex
     * @return
     */
    List<StatisticsMonthAccumulationBody> queryByTerm(String yearIndex,String stationIndex, String monthIndex);
}