package com.cg.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.Feedbacks;

import java.util.List;

/**
 * <p>
 * 反馈表 Mapper 接口
 * </p>

 */
public interface FeedbacksMapper extends BaseMapper<Feedbacks> {


    Page<Feedbacks> getPage(Page<Object> objectPage, List<Integer> ratingList, Integer status);
}
