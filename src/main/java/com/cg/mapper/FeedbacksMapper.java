package com.cg.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Feedbacks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 反馈表 Mapper 接口
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
public interface FeedbacksMapper extends BaseMapper<Feedbacks> {


    Page<Feedbacks> getPage(Page<Object> objectPage, List<Integer> ratingList, Integer status);
}
