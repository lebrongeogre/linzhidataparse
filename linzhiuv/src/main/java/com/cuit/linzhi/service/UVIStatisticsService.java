package com.cuit.linzhi.service;

import com.cuit.linzhi.vo.RadiationBody;
import com.cuit.linzhi.vo.StatisticsUVIBody;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UVIStatisticsService {

    /**
     * 统计年均值数据
     * @return
     */
    int statisticsYearMean();

}
