package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.RoleResources;
import com.cg.mapper.RoleResourcesMapper;
import com.cg.service.RoleResourcesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cg.utils.ListUtils.compare;

@Service
public class RoleResourcesServiceImpl extends ServiceImpl<RoleResourcesMapper, RoleResources>
        implements RoleResourcesService {

    @Autowired
    private AsyncHelper asyncHelper;

    @Override
    @Transactional // 主事务：管理删除+等待异步任务
    public boolean refresh(Long id, List<Long> resId) {
        // 1. 查询原有资源关联
        LambdaQueryWrapper<RoleResources> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleResources::getRoleId, id);
        List<RoleResources> oldList = list(wrapper);
        List<Long> oldResIds = oldList.stream().map(RoleResources::getResourcesId).toList();

        // 2. 对比新旧资源，得到需删除和新增的ID
        List<List<Long>> compareResult = compare(oldResIds, resId);
        List<Long> toDelete = compareResult.get(0);
        List<Long> toAdd = compareResult.get(1);

        // 3. 执行删除操作（主事务内）
        if (!toDelete.isEmpty()) {
            LambdaQueryWrapper<RoleResources> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(RoleResources::getRoleId, id).in(RoleResources::getResourcesId, toDelete);
            remove(deleteWrapper);
        }

        // 4. 异步新增资源（解决数据覆盖+等待结果）
        if (!toAdd.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            toAdd.forEach(res -> {
                // 每次循环创建新对象，避免数据覆盖
                RoleResources roleRes = new RoleResources();
                roleRes.setRoleId(id);
                roleRes.setResourcesId(res);
                // 收集异步任务
                futures.add(asyncHelper.saveResourceAsync(roleRes));
            });

            try {
                // 等待所有异步任务完成（同步等待）
                CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
            } catch (Exception e) {
                // 任何异步任务失败，触发主事务回滚
                throw new RuntimeException("新增资源失败", e);
            }
        }

        return true; // 全部成功返回true
    }

    // 静态内部类：处理异步保存
    @Service
    @RequiredArgsConstructor
    static
    class AsyncHelper {
        private final RoleResourcesMapper mapper;
        @Async("asyncExecutor")
        @Transactional(propagation = Propagation.REQUIRES_NEW) // 独立事务
        public CompletableFuture<Void> saveResourceAsync(RoleResources roleResources) {
            try {
                mapper.insert(roleResources); // 直接用mapper插入
                return CompletableFuture.completedFuture(null); // 成功
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e); // 失败（传递异常）
            }
        }
    }
}