package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.WasteClassify;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 废品分类表 服务类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface WasteClassifyService extends IService<WasteClassify> {

    Page<WasteClassify> getClassify(String name, Integer current, Integer pageSize);
}
