package com.cuit.linzhi.parse;

import com.cuit.linzhi.vo.StatisticsUVIBody;
import com.cuit.linzhi.vo.UVParsedBody;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.List;

@Component
public class TableTransferProcess {

    /**
     * 将tb_sta_uvi表中数据按格式转为t_sta_parsed数据
     * @param statisticsUVIBodies
     * @return
     */
    public List<UVParsedBody> transOrigToParses(List<StatisticsUVIBody> statisticsUVIBodies) {

        if (statisticsUVIBodies.size() != 0){


        }
        return null;
    }
}
