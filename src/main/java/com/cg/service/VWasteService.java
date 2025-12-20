package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.WasteClassify;
import com.cg.entity.view.VWaste;

import java.math.BigDecimal;
import java.util.List;

public interface VWasteService extends IService<VWaste> {
    BigDecimal toamount(Long requestId);

    List<WasteClassify> getTotalByType();

    List<WasteClassify> getAllTotal();
    List<WasteClassify> getMessByType();

    List<WasteClassify> getTotalByReportDate(Integer year);
}
