package com.cuit.linzhi.service;

import com.cuit.linzhi.vo.UVPercentBody;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UVPercentService {

    /**
     * 批量添加
     * @return
     */
    Integer insertBatch() throws DataAccessException;
}
