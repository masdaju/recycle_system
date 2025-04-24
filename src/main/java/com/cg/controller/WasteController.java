package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Waste;
import com.cg.service.WasteService;
import com.cg.utils.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 废品表 前端控制器
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/waste")
public class WasteController {

    @Autowired
    private WasteService wasteService;
@Autowired
private StringRedisTemplate stringRedisTemplate;
    @GetMapping
    //数据删除后大量的请求，会导致数据库压力过大，sync=true，表示同步执行缓存操作，避免数据库压力过大
    //condition="#name==null||#classifyId==null"表示只有当name和classifyId都为空时，才执行缓存操作
    @Cacheable(value = "wasteList", key = "#current + '::' + #pageSize",sync = true ,condition = "#name==null||#classifyId==null")
    public SaResult list(@RequestParam(required = false) Integer current,
                         @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String name,
                         @RequestParam(required = false) String classifyId) {
        List<Long> typeId = null;
        if (classifyId != null) {
            typeId = ListUtils.convertToList(classifyId);
        }
        LambdaQueryWrapper<Waste> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Waste::getName, name).in(classifyId != null, Waste::getCid, typeId);
        if (current == null || pageSize == null) {
            current = 1;
            pageSize = 10;
        }
        Page<Waste> aPage = wasteService.page(new Page<>(current, pageSize), wrapper);
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        Waste waste = wasteService.getById(id);
        if (waste!= null) {
            return SaResult.data(waste);
        } else {
            return SaResult.error("未找到指定废品");
        }
    }
    //废品创建
    @PostMapping(value = "/create")
    @CacheEvict(value = "wasteList", allEntries = true)
    public SaResult create(String params, @RequestParam MultipartFile file) {
        // 将 JSON 字符串转换为 Waste 对象
        Waste waste = JSON.parseObject(params, Waste.class);
        stringRedisTemplate.delete("wasteList");
        if (wasteService.saveWaste(waste, file)) {
            return SaResult.ok("废品创建成功");
        }
        return SaResult.error("废品创建失败");
    }


        @PostMapping(value = "/deleteByIds")
        @CacheEvict(value = "wasteList", allEntries = true)
    public SaResult delete(@RequestBody List<Long> ids) {
        try {
            wasteService.removeByIds(ids);
            stringRedisTemplate.delete("wasteList");
            return SaResult.ok("废品删除成功");
        } catch (Exception e) {
            return SaResult.error("废品删除失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update")
    @CacheEvict(value = "wasteList", allEntries = true)
    public SaResult update(String params, @RequestParam(required = false) MultipartFile file) {
        // 将 JSON 字符串转换为 Waste 对象
        Waste waste = JSON.parseObject(params, Waste.class);

        try {
            wasteService.updateWaste(waste,file);
            return SaResult.ok("废品更新成功");
        } catch (Exception e) {
            return SaResult.error("废品更新失败: " + e.getMessage());
        }
    }
}