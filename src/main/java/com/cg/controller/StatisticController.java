package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.cg.entity.WasteClassify;
import com.cg.service.VWasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/statistic")
@RestController
public class StatisticController {

    @Autowired
    private VWasteService vWasteService;
    //完成总单数
    @GetMapping("/totalByType")
    public SaResult getTotal(){

        List<WasteClassify> list =vWasteService.getTotalByType();
        return SaResult.data(list);
    }
    //接收总单数单数
    @GetMapping("/total")
    public SaResult getALLTotal(){
        List<WasteClassify> list =vWasteService.getAllTotal();
        return SaResult.data(list);
    }
    //
    //按月统计单数（已完成）
        @GetMapping("/totalByMonth")
    public SaResult getTotalByMonth(Integer year){
        List<WasteClassify> list =vWasteService.getTotalByReportDate(year);
        return SaResult.data(list);
    }

    @GetMapping("/getMessByType")
    public SaResult getTotalByMonthAndType(){
        List<WasteClassify> list =vWasteService.getMessByType();
        return SaResult.data(list);
    }

}
