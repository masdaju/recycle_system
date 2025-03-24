package com.cg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cg.entity.WasteClassify;
import com.cg.entity.view.VWaste;

import java.math.BigDecimal;
import java.util.List;

public interface VWasteMapper extends BaseMapper<VWaste> {
    BigDecimal toamount(Long requestId);

    List<WasteClassify> getTotalByType();

    List<WasteClassify> getAllTotal();
    List<WasteClassify> getMessByType();

    List<WasteClassify> getTotalByReportDate(Integer year);

}
