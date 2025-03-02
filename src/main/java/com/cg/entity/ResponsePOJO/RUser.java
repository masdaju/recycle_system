package com.cg.entity.ResponsePOJO;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data

public class RUser implements Serializable {
    /**
     * 主键
     */

    private Long id;


    /**
     * 登录账号
     */

    private String account;

    /**
     * 登录密码
     */
    @JsonIgnore
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 联系电话
     */
    private String mobile;
    /**
    * 头像url
    * */
    private String avatarUrl;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 角色id
     */
    private BigDecimal amount;
    private Long roleId;
    private String roleName;
    private String roleValue;
    private Date createTime;
    private Integer status;
    /**
     *可以访问的资源
     */
    private List<String> resource;
    private SaTokenInfo saTokenInfo;
}