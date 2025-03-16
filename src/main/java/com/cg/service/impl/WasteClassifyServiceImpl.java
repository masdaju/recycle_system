package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.WasteClassify;
import com.cg.mapper.WasteClassifyMapper;
import com.cg.service.WasteClassifyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 废品分类表 服务实现类
 * 该类负责处理与废品分类表相关的业务逻辑，主要实现了根据条件分页查询废品分类信息的功能。
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Service
public class WasteClassifyServiceImpl extends ServiceImpl<WasteClassifyMapper, WasteClassify> implements WasteClassifyService {

    /**
     * 根据废品分类名称进行模糊查询，并支持分页功能。
     *
     * @param name      废品分类名称，用于模糊查询，可为 null
     * @param current   当前页码，可为 null
     * @param pageSize  每页显示的记录数，可为 null
     * @return 包含废品分类信息的分页对象
     */
    @Override
    public Page<WasteClassify> getClassify(String name, Integer current, Integer pageSize) {
        // 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<WasteClassify> wrapper = new LambdaQueryWrapper<>();
        // 如果传入的废品分类名称不为 null，则添加模糊查询条件
        wrapper.like(name != null, WasteClassify::getName, name);
        // 创建一个空的分页对象
        Page<WasteClassify> aPage = new Page<>();

        // 检查当前页码和每页记录数是否为 null
        if (current == null || pageSize == null) {
            // 如果当前页码或每页记录数为 null，则查询所有符合条件的记录，并将结果设置到分页对象的记录列表中
            aPage.setRecords(list(wrapper));
        } else {
            // 如果当前页码和每页记录数都不为 null，则进行分页查询
            aPage = page(new Page<>(current, pageSize), wrapper);
        }
        // 返回包含废品分类信息的分页对象
        return aPage;
    }
}