package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.RoleResources;
import com.cg.service.RoleResourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 */
@RestController
@RequestMapping("/sys-role-resources")
public class RoleResourcesController {


    @Autowired
    private RoleResourcesService roleResourcesService;

    @GetMapping(value = "/")
    public SaResult list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        if (current == null) {
            current = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Page<RoleResources> aPage = roleResourcesService.page(new Page<>(current, pageSize));
        return SaResult.data(aPage);
    }


    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        return SaResult.data(roleResourcesService.getById(id));
    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody RoleResources params) {
        roleResourcesService.save(params);
        return SaResult.ok("created successfully");
    }

    @PostMapping(value = "/delete/{id}")
    public SaResult delete(@PathVariable("id") String id) {
        roleResourcesService.removeById(id);
        return SaResult.ok("deleted successfully");
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody RoleResources params) {
        roleResourcesService.updateById(params);
        return SaResult.ok("updated successfully");
    }
}
