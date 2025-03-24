package com.cg.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.WasteClassify;
import com.cg.entity.view.VWaste;
import com.cg.mapper.VWasteMapper;
import com.cg.service.VWasteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VWasteServiceImpl extends ServiceImpl<VWasteMapper, VWaste> implements VWasteService {
    @Override
    public BigDecimal toamount(Long requestId) {
        return baseMapper.toamount(requestId);
    }

    @Override
    public List<WasteClassify> getTotalByType() {
        return getBaseMapper().getTotalByType();
    }

    @Override
    public List<WasteClassify> getAllTotal() {
        return getBaseMapper().getAllTotal();
    }

    @Override
    public List<WasteClassify> getMessByType() {
        return getBaseMapper().getMessByType();
    }


    @Override
    public List<WasteClassify> getTotalByReportDate(Integer year) {
        return baseMapper.getTotalByReportDate(year);
    }
}
//extends ServiceImpl<WasteClassifyMapper, WasteClassify> implements WasteClassifyService