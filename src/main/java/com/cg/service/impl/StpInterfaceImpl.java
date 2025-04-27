package com.cg.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cg.entity.view.VRole;
import com.cg.entity.view.VUser;
import com.cg.service.VRoleService;
import com.cg.service.VUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所拥有的权限码集合 
     */
    @Resource
    private VRoleService vRoleService;
    @Resource
    private VUserService vUserService;
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LambdaQueryWrapper<VUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VUser::getId,loginId);
        VUser one = vUserService.getOne(wrapper);
        if (one==null||one.getStatus().equals(0)){
            return null;
        }
        LambdaQueryWrapper<VRole> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(VRole::getRoleId,one.getRoleId());
        List<VRole> list = vRoleService.list(wrapper1);
        return list.stream().map(VRole::getResValue).toList();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LambdaQueryWrapper<VUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VUser::getId,loginId);
        List<VUser> list = vUserService.list(wrapper);
        return list.stream().map(VUser::getRoleValue).toList();
    }

}
