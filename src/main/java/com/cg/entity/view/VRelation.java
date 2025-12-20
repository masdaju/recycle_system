package com.cg.entity.view;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serial;
import java.io.Serializable;

@TableName(value ="v_relation")
@Data
public class VRelation implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "friend_user_id")
    private Long friendUserId;
    @TableField(value = "status")
    private Integer status;
    @TableField(value = "name")
    private String name;
    @TableField(value = "avatar_url")
    private String avatarUrl;
    @TableField(value = "account")
    private String account;
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
