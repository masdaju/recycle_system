package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 反馈表
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Data
@Accessors(chain = true)
@TableName("feedbacks")
public class Feedbacks implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈表Id
     */
    @TableId(value = "feedback_id", type = IdType.AUTO)
    private Long feedbackId;

    /**
     * 客户Id/用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 反馈内容
     */
    @TableField("content")
    private String content;

    /**
     * 打分1-5
     */
    @TableField("rating")
    private Integer rating;

    /**
     * 0未读，1已读
     */
    @TableField("status")
    private Integer status;

    @TableField("request_id")
    private Long requestId;
    @TableField(exist = false)
    private String account;
    @TableField(exist = false)
    private String name;
}
