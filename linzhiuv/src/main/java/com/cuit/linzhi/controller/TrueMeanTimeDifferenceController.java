package com.cuit.linzhi.controller;

import com.cuit.linzhi.dao.TrueMeanTimeDifferenceMapper;
import com.cuit.linzhi.service.TrueMeanTimeDifferenceService;
import com.cuit.linzhi.vo.TrueMeanTimeDifferenceBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/trueMeanTimeDifference")
public class TrueMeanTimeDifferenceController {

    @Resource
    TrueMeanTimeDifferenceService trueMeanTimeDifferenceService;

    // 查询时差表全部记录
    @RequestMapping("/queryList")
    @ResponseBody
    public List<TrueMeanTimeDifferenceBody> queryList(){
       return trueMeanTimeDifferenceService.queryAllRecords();
    }
}
