package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 废品请求表
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-27
 */
@Data
@Accessors(chain = true)
@TableName("waste_requests")
public class WasteRequests implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请I表d
     */
    @TableId(value = "request_id", type = IdType.AUTO)
    private Long requestId;

    /**
     * 客户Id/用户id
     */
    @TableField("user_id")
    private Long userId;
    @TableField("appointmentTime")
    private Date appointmentTime;
    /**
     * 0待处理1已接收2已完成
     */
    @TableField("status")
    private Integer status;
    @TableField("address")
    private String address;
    @TableField("remark")
    private String remark;
    @TableField(exist = false)
    private List<Long> wid;
}
