package com.cg.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cg.entity.ResponsePOJO.RUser;
import com.cg.entity.User;
import com.cg.entity.view.VRole;
import com.cg.entity.view.VUser;
import com.cg.mapper.ChatMessageMapper;
import com.cg.mapper.UserMapper;
import com.cg.service.UserService;
import com.cg.service.VRoleService;
import com.cg.service.VUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author MIZUGI
 * @description 针对表【sys_user(用户表)】的数据库操作Service实现
 * 该类实现了 UserService 接口，负责处理用户表相关的业务逻辑，主要包括用户登录和退出登录的功能。
 * @createDate 2024-10-10 10:27:35
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    @Autowired
    private VUserService vUserService;
    @Autowired
    private VRoleService vRoleService;
    @Autowired
   private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户登录方法
     *
     * @param account  用户账号
     * @param password 用户密码
     * @param header   登录设备信息
     * @return 包含登录结果的 SaResult 对象
     */
    @Override

    public SaResult login(String account, String password, String header) {
//        LambdaUpdateWrapper<VUser> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(VUser::getAccount, account);
//        VUser vUser = vUserService.getOne(wrapper);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    //AOP不会代理Cacheable注解
       // VUser vUser = getUser(account);
        //
        VUser vUser = vUserService.getUser(account);
        // 检查用户是否存在
        if (ObjectUtils.isEmpty(vUser)) {
            return SaResult.error("账号不存在");

        } else {
            //用户存在的情况检查用户账号状态
            if (vUser.getStatus() == 0) {
                return SaResult.error("账号已被禁用");
            }
            //检查密码输错次数
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("login_error:" + account))&&Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("login_error:" + account))) >= 5){
                stringRedisTemplate.expire("login_error:" + account, 5, TimeUnit.MINUTES);
                return SaResult.error("密码错误次数过多，账号已被锁定，请5分钟后再试");
            }
            // 验证用户输入的密码是否正确
            if (encoder.matches(password, vUser.getPassword())) {
                // 创建 QueryWrapper 对象，用于构建查询 VRole 的条件
                QueryWrapper<VRole> wrapper1 = new QueryWrapper<>();
                // 添加查询条件：根据角色 ID、角色状态和状态进行筛选
                wrapper1.eq("role_id", vUser.getRoleId()).eq("status", 1).eq("role_status", 1);
                // 调用 vRoleService 的 list 方法，根据查询条件获取 VRole 列表
                List<VRole> list = vRoleService.list(wrapper1);
                // 使用流操作，从 VRole 列表中提取资源值，并收集到一个列表中
                List<String> resourceList = list.stream().map(VRole::getResValue).toList();
                // 使用 Sa-Token 进行用户登录，传入用户 ID
                StpUtil.login(vUser.getId());
                // 创建 RUser 对象，用于封装返回给前端的用户信息
                RUser rUser = new RUser();
                // 使用 BeanUtils 复制 VUser 对象的属性到 RUser 对象中
                BeanUtils.copyProperties(vUser, rUser);
                // 设置 RUser 对象的资源列表
                rUser.setResource(resourceList);
                // 获取 Sa-Token 的信息
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                // 设置登录设备信息
                tokenInfo.setLoginDevice(header);
                // 设置 RUser 对象的 Sa-Token 信息
                rUser.setSaTokenInfo(tokenInfo);

                //登录成功，删除登录错误次数
                stringRedisTemplate.delete("login_error:" + account);
                // 返回包含用户信息的 SaResult 对象
                return SaResult.data(rUser);
            }
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("login_error:" + account))){

                stringRedisTemplate.opsForValue().increment("login_error:" + account,1);
            }else {
                //设置登录错误次数
                stringRedisTemplate.opsForValue().set("login_error:" + account, "1", 5, TimeUnit.MINUTES);
            }
            // 密码验证失败，返回错误信息
            return SaResult.error("密码错误");
        }
    }

    @Override
    public SaResult loginByEmail(String email, String code) {
        boolean equals = Boolean.TRUE.equals(stringRedisTemplate.hasKey("email_code:" + email));
        if (equals&&code.equals(stringRedisTemplate.opsForValue().get("email_code:" + email))){
            QueryWrapper<VUser> wrapper = new QueryWrapper<>();
            wrapper.eq("email", email);
            VUser vUser = vUserService.getOne(wrapper);
            if (ObjectUtils.isEmpty(vUser)) {
                return SaResult.error("用户不存在");
            }
            if (vUser.getStatus()==0) {
                return SaResult.error("账号已被禁用");
            }
            RUser rUser = new RUser();
            BeanUtils.copyProperties(vUser, rUser);
            QueryWrapper<VRole> wrapper1 = new QueryWrapper<>();
            // 添加查询条件：根据角色 ID、角色状态和状态进行筛选
            wrapper1.eq("role_id", vUser.getRoleId()).eq("status", 1).eq("role_status", 1);
            StpUtil.login(vUser.getId());
            List<VRole> list = vRoleService.list(wrapper1);
            // 使用流操作，从 VRole 列表中提取资源值，并收集到一个列表中
            List<String> resourceList = list.stream().map(VRole::getResValue).toList();
            rUser.setResource(resourceList);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            // 设置登录设备信息
            // 设置 RUser 对象的 Sa-Token 信息
            rUser.setSaTokenInfo(tokenInfo);
            //登录成功把验证码移除
            stringRedisTemplate.delete("email_code:" + email);
            return SaResult.data(rUser);
        }
        return SaResult.error("邮箱验证码错误或者过期");

    }

//    @Cacheable(value = "users", key = "#account")
//    public VUser getUser(String account) {
//        LambdaUpdateWrapper<VUser> wrapper = new LambdaUpdateWrapper<>();
//        // 添加查询条件：根据用户账号进行筛选
//        wrapper.eq(VUser::getAccount, account);
//        // 调用 vUserService 的 getOne 方法，根据查询条件获取单个 VUser 对象
//        return vUserService.getOne(wrapper);
//    }
    /**
     * 用户退出登录方法
     *
     * @param satoken 用户的 Sa-Token 值
     * @param userId  用户 ID
     * @return 包含退出登录结果的 SaResult 对象
     */
    @Override
//    @CacheEvict(value = "users", key = "#userId")
    public SaResult logout(String satoken, Object userId) {
        VUser user = vUserService.getOne(new QueryWrapper<VUser>().eq("id", userId));
        String account = user.getAccount();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey("users::" + account))){
            stringRedisTemplate.delete("users::" + account);
        }
        // 根据 Sa-Token 值进行用户退出登录操作
        StpUtil.logoutByTokenValue(satoken);

        return SaResult.ok("退出成功");
    }

    @Override
    public String getCollectorName(Long requestId) {
        return chatMessageMapper.getCollectorName(requestId);
    }

    @Override
    public SaResult loginByScan(String Token) {

        Object loginId = StpUtil.getLoginIdByToken(Token);
        QueryWrapper<VUser> wrapper = new QueryWrapper<>();
        wrapper.eq("id", loginId);
        VUser vUser = vUserService.getOne(wrapper);
        if (ObjectUtils.isEmpty(vUser)) {
            return SaResult.error("用户不存在");
        }
        if (vUser.getStatus() == 0) {
            return SaResult.error("账号已被禁用");
        }
        RUser rUser = new RUser();
        BeanUtils.copyProperties(vUser, rUser);
        QueryWrapper<VRole> wrapper1 = new QueryWrapper<>();
        // 添加查询条件：根据角色 ID、角色状态和状态进行筛选
        wrapper1.eq("role_id", vUser.getRoleId()).eq("status", 1).eq("role_status", 1);
        StpUtil.login(vUser.getId());
        List<VRole> list = vRoleService.list(wrapper1);
        // 使用流操作，从 VRole 列表中提取资源值，并收集到一个列表中
        List<String> resourceList = list.stream().map(VRole::getResValue).toList();
        rUser.setResource(resourceList);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        rUser.setSaTokenInfo(tokenInfo);
        return SaResult.data(rUser);
    }

}