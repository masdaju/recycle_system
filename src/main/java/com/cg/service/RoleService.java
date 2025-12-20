package com.cg.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.Role;

/**
* @author MIZUGI
* @description 针对表【sys_role(角色表)】的数据库操作Service
* @createDate 2024-10-10 10:27:35
*/
public interface RoleService extends IService<Role> {

    Page<Role> getPage(Page<Role> page, LambdaQueryWrapper<Role> wrapper);
}
