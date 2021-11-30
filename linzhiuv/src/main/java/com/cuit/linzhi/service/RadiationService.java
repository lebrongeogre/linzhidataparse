package com.cuit.linzhi.service;



import com.cuit.linzhi.vo.RadiationBody;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RadiationService {

    List<RadiationBody> queryAllRecords();

    void caUVPercent();
}
