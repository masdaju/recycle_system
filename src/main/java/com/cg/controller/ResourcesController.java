package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Resources;
import com.cg.service.ResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统资源 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/sys-resources")
public class ResourcesController {

    @Autowired
    private ResourcesService resourcesService;

    @GetMapping(value = "/")
    public SaResult list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<Resources> aPage = resourcesService.page(new Page<>(current, pageSize));
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        return SaResult.data(resourcesService.getById(id));
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody Resources params) {

        resourcesService.save(params);
        return SaResult.ok("created successfully");
    }

    @PostMapping(value = "/delete/{id}")
    public SaResult delete(@PathVariable("id") Long id) {
        return resourcesService.removeResources(id);

    }

    @GetMapping(value = "tree")
    public SaResult tree(@RequestParam(required = false) Integer current
            , @RequestParam(required = false) Integer pageSize
            , @RequestParam(required = false) String name) {
        return resourcesService.tree(current, pageSize,name);

    }
    @GetMapping(value = "fullTree")
    public SaResult tree() {
        return resourcesService.fullTree();

    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody Resources params) {
        resourcesService.updateById(params);
        return SaResult.ok("updated successfully");
    }
}