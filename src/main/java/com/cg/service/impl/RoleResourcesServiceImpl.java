package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.RoleResources;
import com.cg.mapper.RoleResourcesMapper;
import com.cg.service.RoleResourcesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cg.utils.ListUtils.compare;

/**
 * @author MIZUGI
 * @description 针对表【sys_role_resources】的数据库操作Service实现
 * 该类实现了 RoleResourcesService 接口，用于处理角色资源关联表的相关业务逻辑，
 * 主要功能是刷新角色对应的资源列表。
 * @createDate 2024-10-10 10:27:35
 */
@Service
public class RoleResourcesServiceImpl extends ServiceImpl<RoleResourcesMapper, RoleResources>
        implements RoleResourcesService {

    /**
     * 刷新指定角色的资源列表。
     * 该方法会比较角色原有的资源列表和新的资源列表，找出需要删除和新增的资源，
     * 并在数据库中进行相应的删除和新增操作。
     *
     * @param id    角色的ID
     * @param resId 新的资源ID列表
     * @return 操作是否成功，这里固定返回 true
     */
    @Override
    @Transactional
    public boolean refresh(Long id, List<Long> resId) {
        // 创建 LambdaQueryWrapper 对象，用于构建查询条件
        LambdaQueryWrapper<RoleResources> wrapper = new LambdaQueryWrapper<>();
        // 添加查询条件：根据角色ID筛选记录
        wrapper.eq(RoleResources::getRoleId, id);
        // 调用 list 方法，根据查询条件获取角色原有的资源关联列表
        List<RoleResources> list = list(wrapper);
        // 使用流操作，从资源关联列表中提取出资源ID列表
        List<Long> resourceList = list.stream().map(RoleResources::getResourcesId).toList();
        // 调用 ListUtils 类的 compare 方法，比较原有的资源ID列表和新的资源ID列表，
        // 得到需要删除和新增的资源ID列表
        List<List<Long>> resultLists = compare(resourceList, resId);

        // 打印需要删除的资源ID列表
        System.out.println(resultLists.get(0));
        // 打印需要新增的资源ID列表
        System.out.println(resultLists.get(1));

        // 如果需要删除的资源ID列表不为空
        if (!resultLists.get(0).isEmpty()) {
            // 创建 LambdaQueryWrapper 对象，用于构建删除条件
            LambdaQueryWrapper<RoleResources> deleteWrapper = new LambdaQueryWrapper<>();
            // 添加删除条件：根据角色ID筛选记录
            deleteWrapper.eq(RoleResources::getRoleId, id);
            // 添加删除条件：根据需要删除的资源ID列表进行筛选
            deleteWrapper.in(RoleResources::getResourcesId, resultLists.get(0));
            // 调用 remove 方法，根据删除条件删除相应的资源关联记录
            remove(deleteWrapper);
        }

        // 如果需要新增的资源ID列表不为空
        if (!resultLists.get(1).isEmpty()) {
            // 遍历需要新增的资源ID列表
            for (Long l : resultLists.get(1)) {
                // 创建 RoleResources 对象，用于封装新的资源关联信息
                RoleResources roleResources = new RoleResources();
                // 设置角色ID
                roleResources.setRoleId(id);
                // 设置资源ID
                roleResources.setResourcesId(l);
                // 调用 save 方法，将新的资源关联信息保存到数据库中
                save(roleResources);
            }
        }
        // 返回操作结果，这里固定返回 true
        return false;
    }
}