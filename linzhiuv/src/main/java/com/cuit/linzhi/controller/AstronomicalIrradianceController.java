package com.cuit.linzhi.controller;

import com.cuit.linzhi.dao.AstronomicalIrradianceMapper;
import com.cuit.linzhi.service.AstronomicalIrradianceService;
import com.cuit.linzhi.vo.AstronomicalIrradianceBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("astronomicalIrradiance")
public class AstronomicalIrradianceController {

    @Resource
    AstronomicalIrradianceService astronomicalIrradianceService;

    @RequestMapping("insertList")
    @ResponseBody
    public String insertList() throws ParseException {
        // 数据库中添表
        int ref = astronomicalIrradianceService.calAstronomicalIrradiance();

        if (ref != 0){
            return "添加表成功！";
        }
        return "添加表失败！";
    }
}
