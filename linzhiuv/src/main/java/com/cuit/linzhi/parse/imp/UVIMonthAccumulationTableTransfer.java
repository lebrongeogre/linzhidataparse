package com.cuit.linzhi.parse.imp;

import com.cuit.linzhi.dao.UVParsedBodyMapper;
import com.cuit.linzhi.parse.ITableTransfer;
import com.cuit.linzhi.vo.StatisticsUVIMonthValueBody;

import java.util.List;

public class UVIMonthAccumulationTableTransfer implements ITableTransfer<StatisticsUVIMonthValueBody, UVParsedBodyMapper> {
    @Override
    public List<StatisticsUVIMonthValueBody> creatNewTableList(List<UVParsedBodyMapper> uvParsedBodyMappers) {
        return null;
    }

    @Override
    public Object parseFiles(Object o) {
        return null;
    }
}
