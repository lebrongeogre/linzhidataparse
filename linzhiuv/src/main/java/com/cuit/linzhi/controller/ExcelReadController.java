package com.cuit.linzhi.controller;

import com.cuit.linzhi.job.excel.ReadRadiationExcelProcess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
@Controller
@RequestMapping("read")
public class ExcelReadController {

    @Resource
    ReadRadiationExcelProcess readRadiationExcelProcess;
    @RequestMapping("excel")
    @ResponseBody
    public void uviTableTrans() {
        readRadiationExcelProcess.parseExcelDataToDB();
    }



}
