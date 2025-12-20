package com.cg.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.Feedbacks;
import com.cg.mapper.FeedbacksMapper;
import com.cg.service.FeedbacksService;
import com.cg.utils.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 反馈表 服务实现类
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Service
public class FeedbacksServiceImpl extends ServiceImpl<FeedbacksMapper, Feedbacks> implements FeedbacksService {


    @Override
    public Page<Feedbacks> getPage(Integer current, Integer pageSize, String rating, Integer status) {
        List<Integer> ratingList = null;
        if (rating != null) {
            ratingList = ListUtils.convertToInteger(rating);
        }
        if (current == null || pageSize == null) {
            current = 1;
            pageSize = 10;
        }

        return getBaseMapper().getPage(new Page<>(current, pageSize),ratingList,status);
    }
}
