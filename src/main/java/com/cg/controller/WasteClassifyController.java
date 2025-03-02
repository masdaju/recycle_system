package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.WasteClassify;
import com.cg.service.WasteClassifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 废品分类表 前端控制器
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/waste-classify")
public class WasteClassifyController {

    @Autowired
    private WasteClassifyService wasteClassifyService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current
            , @RequestParam(required = false) Integer pageSize
        , @RequestParam(required = false) String name) {
        ValueOperations<String, String> classify = stringRedisTemplate.opsForValue();
        String s = classify.get("classify");
        if (s == null||name != null||current != null||pageSize != null) {
        Page<WasteClassify> aPage =wasteClassifyService.getClassify(name, current, pageSize);
            classify.set("classify", JSON.toJSONString(aPage), 30,TimeUnit.SECONDS);
        return SaResult.data(aPage);
        }
        //刷新过期时间
        stringRedisTemplate.expire("classify", 30, TimeUnit.SECONDS);
        Page aPage = JSON.parseObject(s, Page.class);
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        WasteClassify wasteClassify = wasteClassifyService.getById(id);
        if (wasteClassify!= null) {
            return SaResult.data(wasteClassify);
        } else {
            return SaResult.error("未找到指定废品分类");
        }
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody WasteClassify params) {
        try {
            wasteClassifyService.save(params);
            stringRedisTemplate.delete("classify");
            return SaResult.ok("废品分类创建成功");
        } catch (Exception e) {
            return SaResult.error("废品分类创建失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/delete")
    public SaResult delete(@RequestBody List<Long> ids) {
        try {
            wasteClassifyService.removeByIds(ids);
            stringRedisTemplate.delete("classify");
            return SaResult.ok("废品分类删除成功");
        } catch (Exception e) {
            return SaResult.error("废品分类删除失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody WasteClassify params) {
        try {
            wasteClassifyService.updateById(params);
            return SaResult.ok("废品分类更新成功");
        } catch (Exception e) {
            return SaResult.error("废品分类更新失败: " + e.getMessage());
        }
    }
}