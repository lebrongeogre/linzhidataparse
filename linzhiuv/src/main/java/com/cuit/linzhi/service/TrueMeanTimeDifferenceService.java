package com.cuit.linzhi.service;


import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TrueMeanTimeDifferenceService {

    List<TrueMeanTimeDifferenceBody> queryAllRecords();
}
