package com.cuit.linzhi.controller;


import com.cuit.linzhi.parse.imp.AccumulationTableTransfer;
import com.cuit.linzhi.service.AccumulationParsedService;
import com.cuit.linzhi.service.UVParsedService;
import com.cuit.linzhi.service.UVUVIAccumulationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("trans")
public class UVIParsedController {


    @Resource
    UVParsedService uvParsedService;
    @Resource
    AccumulationParsedService accumulationParsedService;
    @Resource
    UVUVIAccumulationService uvuviAccumulationService;


    @RequestMapping("uvi")
    @ResponseBody
    public String uviTableTrans() {
        int ref = uvParsedService.insertBatch();
        if (ref != 0){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("accumulation")
    @ResponseBody
    public String accumulationTableTrans() {
        int ref = accumulationParsedService.insertBatch();
        if (ref != 0){
            return "成功";
        }
        return "失败";
    }

    @RequestMapping("uviplus")
    @ResponseBody
    public String uviPlusTableTrans() {
        int ref = uvuviAccumulationService.insertList();
        if (ref != 0){
            return "成功";
        }
        return "失败";
    }


}
