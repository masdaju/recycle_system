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
* @createDate 2024-10-10 10:27:35
*/
@Service
public class RoleResourcesServiceImpl extends ServiceImpl<RoleResourcesMapper, RoleResources>
    implements RoleResourcesService{

    @Override
    @Transactional
    public boolean refresh(Long id, List<Long> resId) {
        LambdaQueryWrapper<RoleResources> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleResources::getRoleId, id);
        List<RoleResources> list = list(wrapper);
        List<Long> resourceList =list.stream().map(RoleResources::getResourcesId).toList();
        List<List<Long>> resultLists = compare(resourceList, resId);
        System.out.println(resultLists.get(0));//删除的资源id
        System.out.println(resultLists.get(1));//新增的资源id
        if (!resultLists.get(0).isEmpty()) {
            LambdaQueryWrapper<RoleResources> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(RoleResources::getRoleId, id);
            deleteWrapper.in(RoleResources::getResourcesId, resultLists.get(0));
            remove(deleteWrapper);
        }
        if (!resultLists.get(1).isEmpty()) {
            for (Long l : resultLists.get(1)) {
                RoleResources roleResources = new RoleResources();
                roleResources.setRoleId(id);
                roleResources.setResourcesId(l);
                save(roleResources);
            }

        }
        return true;
    }
}




