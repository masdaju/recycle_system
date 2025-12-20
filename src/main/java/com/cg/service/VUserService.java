package com.cg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cg.entity.view.VUser;

/**
* @author MIZUGI
* @description 针对表【v_user】的数据库操作Service
* @createDate 2024-10-13 20:33:04
*/
public interface VUserService extends IService<VUser> {

    VUser getUser(String account);

}
