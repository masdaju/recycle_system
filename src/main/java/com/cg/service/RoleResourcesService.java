package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.RoleResources;

import java.util.List;

/**
* @author MIZUGI
* @description 针对表【sys_role_resources】的数据库操作Service
* @createDate 2024-10-10 10:27:35
*/
public interface RoleResourcesService extends IService<RoleResources> {

    boolean refresh(Long id, List<Long> resId);
}
