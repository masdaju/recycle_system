package com.cg.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.view.VUser;
import com.cg.mapper.VUserMapper;
import com.cg.service.VUserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
* @author MIZUGI
* @description 针对表【v_user】的数据库操作Service实现
* @createDate 2024-10-13 20:33:04
*/
@Service
public class VUserServiceImpl extends ServiceImpl<VUserMapper, VUser>
    implements VUserService{
    @Override
    @Cacheable(value = "users", key = "#account")
    public VUser getUser(String account) {
        LambdaUpdateWrapper<VUser> wrapper = new LambdaUpdateWrapper<>();
        // 添加查询条件：根据用户账号进行筛选
        wrapper.eq(VUser::getAccount, account);
        // 调用 vUserService 的 getOne 方法，根据查询条件获取单个 VUser 对象
        return getOne(wrapper);
    }
}




