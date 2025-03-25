package com.cg.controller;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Reports;
import com.cg.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 报告表 前端控制器
 * 该控制器负责处理与报告表相关的HTTP请求，包括报告的分页查询、根据ID查询、创建、批量删除和更新操作。
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@RestController
@RequestMapping("/reports")
public class ReportsController {

    // 自动注入 ReportsService 实例，用于调用报告相关的业务逻辑
    @Autowired
    private ReportsService reportsService;

    /**
     * 分页查询报告列表
     *
     * @param current  当前页码，若未提供则默认为1
     * @param pageSize 每页显示的记录数，若未提供则默认为10
     * @return 包含分页报告列表的 SaResult 对象
     */
    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current, @RequestParam(required = false) Integer pageSize) {
        // 若当前页码未提供，则设置默认值为1
        if (current == null) {
            current = 1;
        }
        // 若每页记录数未提供，则设置默认值为10
        if (pageSize == null) {
            pageSize = 10;
        }
        // 调用服务层的分页查询方法，获取分页报告列表
        Page<Reports> aPage = reportsService.page(new Page<>(current, pageSize));
        // 将分页报告列表封装到 SaResult 对象中返回
        return SaResult.data(aPage);
    }

    /**
     * 根据报告ID查询单个报告
     *
     * @param id 报告的ID
     * @return 包含报告信息的 SaResult 对象，若未找到则返回错误信息
     */
    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        // 调用服务层的根据ID查询方法，获取报告对象
        Reports report = reportsService.getById(id);
        // 若查询到报告，则将其封装到 SaResult 对象中返回
        if (report != null) {
            return SaResult.data(report);
        } else {
            // 若未查询到报告，则返回错误信息
            return SaResult.error("未找到对应报告");
        }
    }

    /**
     * 创建新的报告
     *
     * @param params 包含报告信息的 Reports 对象
     * @return 包含创建结果的 SaResult 对象
     */
    @PostMapping(value = "/create")
    public SaResult create(@RequestBody Reports params) {
        // 设置报告的日期为当前时间
        params.setReportDate(new Date());
        try {
            // 调用服务层的保存方法，将报告保存到数据库
            reportsService.save(params);
            // 返回创建成功的信息
            return SaResult.ok("创建成功");
        } catch (Exception e) {
            // 若创建过程中出现异常，则返回错误信息
            return SaResult.error("创建失败：" + e.getMessage());
        }
    }

    /**
     * 根据报告ID列表批量删除报告
     *
     * @param ids 要删除的报告的ID列表
     * @return 包含删除结果的 SaResult 对象
     */
    @PostMapping(value = "deleteByIds")
    public SaResult getById(@RequestBody List<Long> ids) {
        try {
            // 调用服务层的批量删除方法，根据ID列表删除报告
            reportsService.removeBatchByIds(ids);
        } catch (Exception e) {
            // 若删除过程中出现异常，则返回错误信息
            return SaResult.error("删除失败: " + e.getMessage());
        }
        // 返回删除成功的信息
        return SaResult.ok("删除成功");
    }

    /**
     * 根据报告对象更新报告信息
     *
     * @param params 包含要更新信息的 Reports 对象
     * @return 包含更新结果的 SaResult 对象
     */
    @PostMapping(value = "/update")
    public SaResult update(@RequestBody Reports params) {
        try {
            // 调用服务层的更新方法，根据报告对象更新数据库中的记录
            reportsService.updateById(params);
            // 返回更新成功的信息
            return SaResult.ok("更新成功");
        } catch (Exception e) {
            // 若更新过程中出现异常，则返回错误信息
            return SaResult.error("更新失败：" + e.getMessage());
        }
    }
}