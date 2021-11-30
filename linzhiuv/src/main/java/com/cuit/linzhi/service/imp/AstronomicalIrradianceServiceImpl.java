package com.cuit.linzhi.service.imp;

import com.cuit.linzhi.config.StatisticsConfig;
import com.cuit.linzhi.dao.AstronomicalIrradianceMapper;
import com.cuit.linzhi.dao.TrueMeanTimeDifferenceMapper;
import com.cuit.linzhi.parse.CalculateAstronomicalIrradiance;
import com.cuit.linzhi.service.AstronomicalIrradianceService;
import com.cuit.linzhi.vo.AstronomicalIrradianceBody;
import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Service
public class AstronomicalIrradianceServiceImpl implements AstronomicalIrradianceService {

    @Resource
    TrueMeanTimeDifferenceMapper trueMeanTimeDifferenceMapper;
    @Resource
    AstronomicalIrradianceMapper astronomicalIrradianceMapper;
    @Resource
    CalculateAstronomicalIrradiance calculateAstronomicalIrradiance;
    @Resource
    TrueMeanTimeDifferenceBody trueMeanTimeDifferenceBody;
    @Resource
    StatisticsConfig statisticsConfig;

    private List<AstronomicalIrradianceBody> astronomicalIrradianceBodies;

    @Override
    public int calAstronomicalIrradiance() throws ParseException {

        // 获取站点list
        List<String> stations = statisticsConfig.getStations();
        // 获取站点经度
        Map<String,String> longitudeMap = statisticsConfig.getLongitude();

        // 从数据库中查出时差表
        List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies = trueMeanTimeDifferenceMapper.queryAllRecords();
        // 将时差表存入
        if (trueMeanTimeDifferenceBodies != null){
            calculateAstronomicalIrradiance.setTrueMeanTimeDifferenceBodies(trueMeanTimeDifferenceBodies);
        }

        // 遍历站点
        for (String station:stations){
            // 得到站点经度
            String strLongitude = longitudeMap.get(station);
            // 计算得出天文辐照度list
            astronomicalIrradianceBodies = calculateAstronomicalIrradiance.
                    calculateAstronomicalIrradiance(station,strLongitude, "12:00:00");
        }

        int ref = astronomicalIrradianceMapper.insertList(astronomicalIrradianceBodies);
        /* 返回入库结果*/
        if (ref == 1){
            return 1;
        }
        return 0;
    }


}
