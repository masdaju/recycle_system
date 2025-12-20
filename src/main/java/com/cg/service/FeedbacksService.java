package com.cg.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Feedbacks;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 反馈表 服务类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface FeedbacksService extends IService<Feedbacks> {


    Page<Feedbacks> getPage(Integer current, Integer pageSize, String rating, Integer status);
}
