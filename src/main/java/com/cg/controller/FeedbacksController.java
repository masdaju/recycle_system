package com.cg.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Feedbacks;
import com.cg.service.FeedbacksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 反馈表 前端控制器
 * </p>

 */
@RestController
@RequestMapping("/feedbacks")
public class FeedbacksController {

    @Autowired
    private FeedbacksService feedbacksService;
//获取反馈列表
    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current
            , @RequestParam(required = false) Integer pageSize
            ,@RequestParam(required = false) String rating
            ,@RequestParam(required = false) Integer status) {

        Page<Feedbacks> aPage = feedbacksService.getPage(current, pageSize,rating,status);
        return SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") String id) {
        Feedbacks feedback = feedbacksService.getById(id);
        if (feedback!= null) {
            return SaResult.data(feedback);
        } else {
            return SaResult.error("未找到对应反馈信息");
        }
    }

    @PostMapping(value = "/create")
    //提交反馈
    public SaResult create(@RequestBody Feedbacks params) {
        params.setUserId(StpUtil.getLoginIdAsLong());
        try {
            feedbacksService.save(params);
            return SaResult.ok("创建成功");
        } catch (Exception e) {
            return SaResult.error("你已经提交过反馈了");
        }
    }

    @GetMapping("checked")
    //前端传递的参数是feedbackId后端通过RequestParam指定参数名
    public SaResult checked(@RequestParam(name = "feedbackId") Long id) {
        LambdaUpdateWrapper <Feedbacks> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Feedbacks::getStatus,1).eq(Feedbacks::getFeedbackId,id);
        try {
            feedbacksService.update(wrapper);
        } catch (Exception e) {
            SaResult.error("修改失败: " + e.getMessage());
        }
        return SaResult.ok("修改成功");
    }


    @PostMapping(value = "deleteByIds")
    public SaResult getById(@RequestBody List<Long> ids) {
        System.out.println(ids);
        try {
            feedbacksService.removeBatchByIds(ids);
        } catch (Exception e) {
            SaResult.error("删除失败: " + e.getMessage());
        }
        return SaResult.ok("删除成功");
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody Feedbacks params) {
        try {
            feedbacksService.updateById(params);
            return SaResult.ok("更新成功");
        } catch (Exception e) {
            return SaResult.error("更新失败：" + e.getMessage());
        }
    }
}