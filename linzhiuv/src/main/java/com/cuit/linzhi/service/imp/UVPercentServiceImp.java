package com.cuit.linzhi.service.imp;

import com.cuit.linzhi.dao.RadiationBodyMapper;
import com.cuit.linzhi.dao.UVPercentBodyMapper;
import com.cuit.linzhi.parse.Calculator;
import com.cuit.linzhi.service.UVPercentService;
import com.cuit.linzhi.vo.RadiationBody;
import com.cuit.linzhi.vo.UVPercentBody;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UVPercentServiceImp implements UVPercentService {

    @Resource
    UVPercentBodyMapper uvPercentBodyMapper;
    @Resource
    RadiationBodyMapper radiationBodyMapper;
    @Resource
    Calculator calculateMean;

    @Override
    public Integer insertBatch() throws DataAccessException {

        List<RadiationBody> radiationBodies = radiationBodyMapper.queryAllRecords();
        List<UVPercentBody>  uvPercentBodies =  calculateMean.calYiTaMean(radiationBodies);
         return uvPercentBodyMapper.insertBatch(uvPercentBodies);
    }
}
