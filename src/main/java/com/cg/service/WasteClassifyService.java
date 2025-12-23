package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.WasteClassify;

/**
 * <p>
 * 废品分类表 服务类
 * </p>
 */
public interface WasteClassifyService extends IService<WasteClassify> {

    Page<WasteClassify> getClassify(String name, Integer current, Integer pageSize);
}
