package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("chat_message")
public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送用户id
     */
    @TableField("send_user_account")
    private String sendUserAccount;

    /**
     * 接手用户id
     */
    @TableField("accept_user_account")
    private String acceptUserAccount;

    /**

     * 发送内容
     */
    @TableField("content")
    private String content;

    @TableField("send_time")
    private Date sendTime;

}
