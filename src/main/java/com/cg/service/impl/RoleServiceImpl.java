package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.Role;
import com.cg.entity.RoleResources;
import com.cg.mapper.RoleMapper;
import com.cg.service.RoleResourcesService;
import com.cg.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author MIZUGI
 * @description 针对表【sys_role(角色表)】的数据库操作Service实现
 * 该类负责处理角色表相关的业务逻辑，主要实现了分页查询角色并关联角色对应的资源ID列表的功能。
 * @createDate 2024-10-10 10:27:35
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
        implements RoleService {

    // 自动注入 RoleResourcesService 实例，用于获取角色资源关联信息
    @Autowired
    private RoleResourcesService roleResourcesService;

    /**
     * 分页查询角色列表，并为每个角色设置对应的资源ID列表。
     *
     * @param page    分页信息，包含当前页码、每页记录数等
     * @param wrapper 查询条件包装器，用于构建查询角色的条件
     * @return 包含角色列表的分页对象，每个角色对象中包含其对应的资源ID列表
     */
    @Override
    public Page<Role> getPage(Page<Role> page, LambdaQueryWrapper<Role> wrapper) {
        // 调用父类的 page 方法，根据分页信息和查询条件进行分页查询，获取角色分页对象
        Page<Role> aPage = page(page, wrapper);
        // 调用 RoleResourcesService 的 list 方法，获取所有的角色资源关联信息列表
        List<RoleResources> roleResourcesList = roleResourcesService.list();
        // 根据角色ID进行分组，并将每个角色对应的资源ID收集到一个列表中
        // 最终得到一个以角色ID为键，资源ID列表为值的 Map
        Map<Long, List<Long>> roleIdToResIdsMap = roleResourcesList.stream()
                .collect(Collectors.groupingBy(
                        RoleResources::getRoleId, // 按角色ID分组
                        Collectors.mapping(RoleResources::getResourcesId, Collectors.toList()) // 将资源ID收集到列表中
                ));

        // 从分页对象中获取角色列表
        List<Role> roles = aPage.getRecords();
        // 遍历角色列表，为每个角色设置对应的资源ID列表
        // 如果该角色没有对应的资源ID列表，则使用一个空的 ArrayList 作为默认值
        roles.forEach(role ->
                role.setResId(roleIdToResIdsMap.getOrDefault(role.getId(), new ArrayList<>()))
        );
        // 返回包含角色列表的分页对象，此时每个角色对象中已包含其对应的资源ID列表
        return aPage;
    }
}