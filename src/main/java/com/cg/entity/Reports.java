package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 报告表
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Data
@Accessors(chain = true)
@TableName("reports")
public class Reports implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 报告Id，在回收完成后添加
     */
    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    /**
     * 回收人Id/用户Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 报告时间
     */
    @TableField("report_date")
    private Date reportDate;
    @TableField("request_id")
    private Long requestId;
    @TableField(exist = false)
    private BigDecimal total;

}
