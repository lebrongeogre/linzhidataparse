package com.cuit.linzhi.controller;

import com.cuit.linzhi.service.RadiationService;
import com.cuit.linzhi.service.UVPercentService;
import com.cuit.linzhi.service.imp.RadiationServiceImp;
import com.cuit.linzhi.vo.RadiationBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("radiation")
public class RadiationController {

    @Autowired
    RadiationService radiationService;


    @RequestMapping("querylist")
    @ResponseBody
    public List<RadiationBody> queryList() {
        return radiationService.queryAllRecords();
    }



}
