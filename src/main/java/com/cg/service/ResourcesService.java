package com.cg.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.Resources;

/**
* @author MIZUGI
* @description 针对表【sys_resources(系统资源)】的数据库操作Service
* @createDate 2024-10-10 10:27:35
*/
public interface ResourcesService extends IService<Resources> {

    SaResult tree(Integer CurrentPage, Integer PageSize,String name);

    // 重写fullTree方法，用于构建完整的树结构
    SaResult fullTree();

    SaResult removeResources(Long id);
}
