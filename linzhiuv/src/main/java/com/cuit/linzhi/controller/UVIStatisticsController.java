package com.cuit.linzhi.controller;

import com.cuit.linzhi.service.UVIStatisticsService;
import com.cuit.linzhi.service.imp.UVIMonthStatisticsServiceImp;
import com.cuit.linzhi.service.imp.UVIYearStatisticsServiceImp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;



@Controller
@RequestMapping("statistics")
public class UVIStatisticsController {

    @Resource(name="year")
    UVIStatisticsService yearStatisticsService;
    @Resource(name="month")
    UVIStatisticsService monthStatisticsService;

    /**
     * 计算每年各月的均值
     */
    @GetMapping("year")
    @ResponseBody
    public String statisticsYear() {
        //统计均值
        int ref = yearStatisticsService.statisticsYearMean();

        if (ref != 0){
            return "生成成功！";
        }
        return "生成失败！";
    }


    /**
     * 计算各月的yita
     */
    @GetMapping("month")
    @ResponseBody
    public String statisticsMonth() {
        //统计均值
        int ref = monthStatisticsService.statisticsYearMean();

        if (ref != 0){
            return "生成成功！";
        }
        return "生成失败！";
    }


}
