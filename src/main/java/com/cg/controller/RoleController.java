package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Role;
import com.cg.entity.RoleResources;
import com.cg.service.RoleResourcesService;
import com.cg.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author 
 * @since 2024-10-13
 */
@RestController
@Slf4j
@RequestMapping("/sys-role")
public class RoleController {


    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleResourcesService roleResourcesService;
    @GetMapping
    @Cacheable(value = "rolePageCache", key = "#current + '::' + #pageSize", sync = true,condition = "#name==null")
    public SaResult list(@RequestParam(required = false) Integer current,
                         @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String name) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Role::getName, name);
        Page<Role> aPage;
        if (current == null || pageSize == null) {
            aPage =roleService.getPage(new Page<>(), wrapper);
        } else
            aPage =roleService.getPage(new Page<>(current, pageSize), wrapper);
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        return SaResult.data(roleService.getById(id));
    }
    @PostMapping(value = "/create")
    @CacheEvict(value = "rolePageCache", allEntries = true)
    public SaResult create(@RequestBody Role params) {
        roleService.save(params);

//        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Role::getId,params.getId());
//        Role one = roleService.getOne(lambdaQueryWrapper);

        if (roleResourcesService.refresh(params.getId(), params.getResId())) {
            return SaResult.error("创建成功");
        }
        return SaResult.ok("创建失败");
    }
    @PostMapping(value = "/delete/{id}")
    @CacheEvict(value = "rolePageCache", allEntries = true)
    public SaResult delete(@PathVariable("id") String id) {
        LambdaQueryWrapper<RoleResources> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(RoleResources::getRoleId,id);
        roleResourcesService.remove(wrapper);
        roleService.removeById(id);
        return SaResult.ok("deleted successfully");
    }
    @PostMapping(value = "/update")
    @CacheEvict(value = "rolePageCache", allEntries = true)
    public SaResult update(@RequestBody Role params) {
        if (roleResourcesService.refresh(params.getId(), params.getResId())) {
            return SaResult.error("update failed");
        }
        roleService.updateById(params);
        return SaResult.ok("updated successfully");
    }
}
