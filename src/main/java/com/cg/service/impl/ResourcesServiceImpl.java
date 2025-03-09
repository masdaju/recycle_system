package com.cg.service.impl;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.Resources;
import com.cg.mapper.ResourcesMapper;
import com.cg.service.ResourcesService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cg.utils.ListUtils.findAllChildren;

/**
* @author MIZUGI
* @description 针对表【sys_resources(系统资源)】的数据库操作Service实现
* @createDate 2024-10-10 10:27:35
*/
@Service
public class ResourcesServiceImpl extends ServiceImpl<ResourcesMapper, Resources>
    implements ResourcesService{
    private static final Long ROOT_PID = 0L;
    
    @Override
    public SaResult tree(Integer CurrentPage,Integer PageSize,String name) {

        //通过分页方式查询pid为0的父节点
        LambdaQueryWrapper<Resources> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Resources::getPid,ROOT_PID)
                .like(name != null,Resources::getName,name);
        Page<Resources> iPage = new Page<>(CurrentPage,PageSize);
        this.page(iPage,lqw);
        List<Resources> voList =  iPage.getRecords();
        //获取全量数据
        List<Resources> dbList = list();
        //通过父节点构建树形结构
        voList.forEach(parent -> {
            List<Resources> children = buildTreeNode(dbList,parent);
            parent.setChildren(children);
        });
        iPage.setRecords(voList);
        return  SaResult.data(iPage);
    }
    // 重写fullTree方法，用于构建完整的树结构

    // 构建树结构的私有方法，根据数据库列表和父节点构建子节点列表

    @Override
    public SaResult fullTree() {
        // 从数据库中获取资源列表
        List<Resources> dbList = list();
        // 使用流式处理将数据库资源转换为VO资源
        // 创建一个新的SysResourcesVO对象
        // 返回VO对象
        List<Resources> voList = dbList.stream().filter(item -> ROOT_PID.equals(item.getPid())).collect(Collectors.toList());
        // 遍历VO资源列表，构建树结构
        voList.forEach(parent -> {
            // 构建子节点列表
            List<Resources> children = buildTreeNode(dbList,parent);
            // 将子节点列表设置到父节点中
            parent.setChildren(children);
        });
        // 返回构建好的树结构
        return SaResult.data(voList);
    }

    @Override
    public SaResult removeResources(Long id) {
        // 获取所有子节点
        removeByIds(findAllChildren(list(), id));
        return SaResult.ok("删除成功");
    }

    private List<Resources> buildTreeNode(List<Resources> dbList,Resources parent){
        // 定义子节点列表
        List<Resources> children = new ArrayList<>();
        // 遍历数据库列表，找到与父节点匹配的子节点
        dbList.forEach(item -> {
            // 如果子节点的父ID与父节点的ID匹配
            if(item.getPid().equals(parent.getId())){
                List<Resources> childList = buildTreeNode(dbList,item);
                item.setChildren(childList);
                // 将VO对象添加到子节点列表中
                children.add(item);
            }
        });
        // 返回子节点列表
        return children;
    }
}




