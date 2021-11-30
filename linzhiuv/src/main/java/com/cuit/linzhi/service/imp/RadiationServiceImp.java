package com.cuit.linzhi.service.imp;

import com.cuit.linzhi.dao.RadiationBodyMapper;
import com.cuit.linzhi.service.RadiationService;
import com.cuit.linzhi.vo.RadiationBody;
import com.cuit.linzhi.vo.UVPercentBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RadiationServiceImp implements RadiationService {

    @Resource
    RadiationBodyMapper radiationBodyMapper;




    @Override
    public List queryAllRecords() {
        return radiationBodyMapper.queryAllRecords();
    }

    @Override
    public void caUVPercent() {

    }
}
