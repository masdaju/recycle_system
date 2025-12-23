package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.Feedbacks;

/**
 * <p>
 * 反馈表 服务类
 * </p>
 */
public interface FeedbacksService extends IService<Feedbacks> {


    Page<Feedbacks> getPage(Integer current, Integer pageSize, String rating, Integer status);
}
