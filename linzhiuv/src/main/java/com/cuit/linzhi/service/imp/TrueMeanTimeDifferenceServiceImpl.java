package com.cuit.linzhi.service.imp;

import com.cuit.linzhi.dao.TrueMeanTimeDifferenceMapper;
import com.cuit.linzhi.service.TrueMeanTimeDifferenceService;
import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TrueMeanTimeDifferenceServiceImpl implements TrueMeanTimeDifferenceService {

    @Resource
    TrueMeanTimeDifferenceMapper trueMeanTimeDifferenceMapper;

    @Override
    public List<TrueMeanTimeDifferenceBody> queryAllRecords() {

        List<TrueMeanTimeDifferenceBody> trueMeanTimeDifferenceBodies = trueMeanTimeDifferenceMapper.queryAllRecords();

        return trueMeanTimeDifferenceBodies;
    }
}
