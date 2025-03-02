package com.cg.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cg.entity.SysFile;
import com.cg.entity.User;
import com.cg.entity.view.VUser;
import com.cg.service.SysFileService;
import com.cg.service.UserService;
import com.cg.service.VUserService;
import com.cg.utils.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author 
 * @since 2024-10-13
 */
@RestController
@RequestMapping("/sys-user")
public class UserController {


    @Autowired
    private UserService userService;
    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private VUserService vuserService;

    @PostMapping(value = "/login")
    public SaResult login(@RequestParam String account, @RequestParam String password, HttpServletRequest request) {
       return userService.login(account, password, request.getHeader("Sec-Ch-Ua-Platform"));

    }
    @GetMapping(value = "/logout")
        public SaResult logout(@RequestHeader String satoken,@RequestParam Integer uid) {
                return userService.logout(satoken,uid);
        }
    @GetMapping
    public SaResult list(@RequestParam(required = false) Integer current,
                         @RequestParam(required = false) Integer pageSize,
                         @RequestParam(required = false) String name) {
        LambdaQueryWrapper<VUser> wrapper = new LambdaQueryWrapper<>();
        if (name == null) {
            wrapper=null;
        }else {
            wrapper.like(VUser::getName, name);
        }
        Page<VUser> page;
        if (current == null || pageSize == null) {
            page = new Page<>();
        }else {
            page = new Page<>(current, pageSize);
        }
        Page<VUser> aPage = vuserService.page(page,wrapper);
        return  SaResult.data(aPage);
    }

    @GetMapping(value = "/{id}")
    public SaResult getById(@PathVariable("id") Long id) {
        return SaResult.data(userService.getById(id));
    }

    @GetMapping("fetchAmount")
    public SaResult fetchAmount(@RequestParam(required = false) Integer uid) {
        return SaResult.data(userService.getById(uid).getAmount());
    }
    @GetMapping("getCollectorList")
    public SaResult getByAccount(@RequestParam(required = false) String account) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(account!=null,User::getAccount, account).eq(User::getRoleId, 4);
        return SaResult.data(userService.list(wrapper));
    }

    /**
     * 上传头像
     */
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${preview.url}")
    private String previewUrl;
    @PostMapping(value = "/uploadAvatar")
    public SaResult uploadAvatar( MultipartFile avatar) {
//        String avatarUrl;
        // 获取之前的旧头像

        String preAvatar = userService.getById(StpUtil.getLoginIdAsLong()).getAvatarUrl();
        LambdaQueryWrapper<SysFile> dwrapper = new LambdaQueryWrapper<>();
        if (preAvatar!=null) {
            dwrapper.eq( SysFile::getFileUrl, preAvatar);
            // 删除之前的旧头像
            sysFileService.remove(dwrapper);
            String realPath = uploadPath+StringUtils.topath(preAvatar);
            //从磁盘里面移除就头像
            sysFileService.delFromDisk(realPath);
        }
        // 上传新的头像
        String avatarUrl = sysFileService.upload(avatar);
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(User::getAvatarUrl, previewUrl + avatarUrl).eq(User::getId, StpUtil.getLoginIdAsLong());
            userService.update(wrapper);
        return SaResult.ok(previewUrl + avatarUrl);
    }


    @PostMapping(value = "/updatePassWord")
    public SaResult updatePassWord(@RequestParam String oldPassword
            ,@RequestParam String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Long loggedInUserId = StpUtil.getLoginIdAsLong();
        User one = userService.getById(loggedInUserId);
        if (!passwordEncoder.matches(oldPassword, one.getPassword())) {
            return SaResult.error("旧密码错误");
        }else if (passwordEncoder.matches(newPassword, one.getPassword())) {
                return SaResult.error("新密码不能与旧密码相同");
        }
        one.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(one);
        // 退出登录
        StpUtil.kickout(one.getId());
        StpUtil.logout(one.getId());
        return SaResult.ok("修改成功");

    }

    @PostMapping(value = "/create")
    public SaResult create(@RequestBody User params) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        params.setPassword(passwordEncoder.encode(params.getRePassword()));
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, params.getEmail());
        User one = userService.getOne(wrapper);
        if (one != null) {
            return SaResult.error("邮箱已存在");
        }
        LambdaQueryWrapper<User> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(User::getMobile, params.getMobile());
        one = userService.getOne(wrapper1);
        if (one != null) {
            return SaResult.error("手机号已存在");
        }
        try {
            userService.save(params);
        } catch (Exception e) {
            return SaResult.error("用户名已存在");
        }

        return SaResult.ok("注册成功");
    }

    @PostMapping(value = "/delete/{id}")
    public SaResult delete(@PathVariable("id") String id) {
        userService.removeById(id);
        return SaResult.ok("deleted successfully");
    }

    @PostMapping(value = "/update")
    public SaResult update(@RequestBody User params) {
        //如果id为空说明是用户本人操作更新自己的信息
        if (params.getId() == null) {
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getId, StpUtil.getLoginIdAsLong())
                    .set(params.getMobile() != null, User::getMobile, params.getMobile())
                    .set(params.getEmail() != null, User::getEmail, params.getEmail())
                    .set(params.getName() != null, User::getName, params.getName());
            userService.update(wrapper);
        }
        //如果id不为空说明是管理员操作更新用户信息
        userService.updateById(params);
        if (params.getStatus() == 0){
            //如果用户被禁用则踢出登录
            StpUtil.kickout(params.getId());
        return SaResult.ok("updated successfully");
        }
        return SaResult.ok("updated successfully");
    }
}
