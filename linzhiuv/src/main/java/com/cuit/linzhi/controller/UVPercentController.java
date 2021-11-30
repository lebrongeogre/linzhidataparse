package com.cuit.linzhi.controller;

import com.cuit.linzhi.service.RadiationService;
import com.cuit.linzhi.service.UVPercentService;
import com.cuit.linzhi.vo.RadiationBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("uvpercent")
public class UVPercentController {

    @Resource
    UVPercentService uvPercentService;


    @RequestMapping("createuvpercent")
    @ResponseBody
    public String createUVPercent() {

        Integer res = uvPercentService.insertBatch();
        if (res.equals(0)) {
            return "计算失败！";
        }
        return "计算成功！";
    }


}
