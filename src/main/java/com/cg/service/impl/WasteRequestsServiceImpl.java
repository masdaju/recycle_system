package com.cg.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.RequestWaste;
import com.cg.entity.RoleResources;
import com.cg.entity.WasteRequests;
import com.cg.mapper.WasteRequestsMapper;
import com.cg.service.RequestWasteService;
import com.cg.service.WasteRequestsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cg.utils.ListUtils.compare;

/**
 * <p>
 * 废品请求表 服务实现类
 * 该类实现了WasteRequestsService接口，处理与废品请求表相关的业务逻辑，
 * 包括保存废品请求、更新废品请求、分页查询废品请求等功能。
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-27
 */
@Service
public class WasteRequestsServiceImpl extends ServiceImpl<WasteRequestsMapper, WasteRequests> implements WasteRequestsService {
    // 注入RequestWasteService实例，用于处理RequestWaste相关业务
    @Resource
    private RequestWasteService requestWasteService;

    /**
     * 保存废品请求及对应的废品信息
     *
     * @param requestId 废品请求ID
     * @param wid       废品ID列表
     * @return 保存成功返回true，否则返回false
     */
    @Override
        @Transactional
        public boolean saveWasteRequests(Long requestId, List<Long> wid) {
            // 如果废品ID列表不为空
            if (!wid.isEmpty()) {
                // 遍历废品ID列表
                for (Long l : wid) {
                    // 创建RequestWaste对象
                    RequestWaste requestWaste = new RequestWaste();
                    // 设置请求ID
                    requestWaste.setRequestId(requestId);
                    // 设置废品ID
                    requestWaste.setWasteId(l);
                    // 调用RequestWasteService的save方法保存
                    requestWasteService.save(requestWaste);
                }
                return true;
            }
            return false;
        }

    /**
     * 更新废品请求及对应的废品信息
     *
     * @param requestId 废品请求ID
     * @param wid       新的废品ID列表
     * @return 更新成功返回true
     */
    @Override
    @Transactional
    public boolean updateWasteRequests(Long requestId, List<Long> wid) {
        // 创建查询条件包装器，根据请求ID查询RequestWaste列表
        LambdaQueryWrapper<RequestWaste> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RequestWaste::getRequestId, requestId);
        List<RequestWaste> list = requestWasteService.list(wrapper);
        // 提取修改前的废品ID列表
        List<Long> resourceList = list.stream().map(RequestWaste::getWasteId).toList();
        // 调用compare方法比较修改前和新的废品ID列表，得到应删除和新增的废品ID列表
        List<List<Long>> resultLists = compare(resourceList, wid);
        System.out.println(resultLists.get(0));// 打印应删除的资源ID
        System.out.println(resultLists.get(1));// 打印应新增的资源ID

        // 删除操作
        if (!resultLists.get(0).isEmpty()) {
            // 创建删除条件包装器
            LambdaQueryWrapper<RequestWaste> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(RequestWaste::getRequestId, requestId);
            deleteWrapper.in(RequestWaste::getWasteId, resultLists.get(0));
            // 调用RequestWasteService的remove方法删除
            requestWasteService.remove(deleteWrapper);
        }

        // 新增操作
        if (!resultLists.get(1).isEmpty()) {
            //不要重复创建RequestWaste对象
            RequestWaste requestWaste = new RequestWaste();
            for (Long l : resultLists.get(1)) {
//                RequestWaste requestWaste = new RequestWaste();
                requestWaste.setRequestId(requestId);
                requestWaste.setWasteId(l);
                requestWasteService.save(requestWaste);
            }
        }
        return true;
    }

    /**
     * 分页查询当前登录用户的废品请求信息，并关联每个请求对应的废品ID
     *
     * @param current  当前页码
     * @param pageSize 每页记录数
     * @return 包含废品请求信息的分页对象
     */
    @Override
    public Page<WasteRequests> getPage(Integer current, Integer pageSize) {
        // 获取当前登录用户的ID
        Long loginId = StpUtil.getLoginIdAsLong();
        // 创建查询条件包装器，根据用户ID查询废品请求
        LambdaQueryWrapper<WasteRequests> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WasteRequests::getUserId, loginId);
        // 执行分页查询
        Page<WasteRequests> apage = page(new Page<>(current, pageSize), queryWrapper);
        // 遍历查询结果，为每个废品请求关联对应的废品ID
        apage.getRecords().forEach(item -> {
            LambdaQueryWrapper<RequestWaste> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RequestWaste::getRequestId, item.getRequestId());
            // 提取废品ID列表并设置到WasteRequests对象的wid属性中
            item.setWid(requestWasteService.list(wrapper).stream().map(RequestWaste::getWasteId).toList());
        });
        return apage;
    }

    /**
     * 根据状态分页查询废品请求信息，并按预约时间升序排序
     *
     * @param current  当前页码
     * @param pageSize 每页记录数
     * @param status   废品请求状态，可为null
     * @return 包含废品请求信息的分页对象
     */
    @Override
    public Page<WasteRequests> getRequestByStatuspage(Integer current, Integer pageSize, Integer status) {
        // 创建查询条件包装器
        LambdaQueryWrapper<WasteRequests> queryWrapper = new LambdaQueryWrapper<>();
        // 如果状态不为null，添加状态查询条件
        queryWrapper.eq(status != null, WasteRequests::getStatus, status)
                // 按预约时间升序排序
                .orderByAsc(WasteRequests::getAppointmentTime);
        // 执行分页查询
        return page(new Page<>(current, pageSize), queryWrapper);
    }
}