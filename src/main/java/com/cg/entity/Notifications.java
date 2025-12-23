package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息通知表
 * </p>

 */
@Data
@Accessors(chain = true)
@TableName("notifications")
public class Notifications implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知Id
     */
    @TableId(value = "notification_id", type = IdType.AUTO)
    private Long notificationId;

    /**
     * 用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 通知消息
     */
    @TableField("message")
    private String message;

    /**
     * 发送时间
     */
    @TableField("sent_at")
    private Date sentAt;
    /**
     * 是否已读
     * 1未读，2已读
     */
    @TableField("is_read")
    private Integer isRead;
}
