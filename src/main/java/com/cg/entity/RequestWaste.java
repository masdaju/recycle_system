package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 每个申请Id对应的废品
 * </p>
 */
@Data
@Accessors(chain = true)
@TableName("request_waste")
public class RequestWaste implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请Id(一个requestid有多个wasteId)
     */
    @TableField("request_id")
    private Long requestId;

    /**
     * 废品id
     */
    @TableField("waste_id")
    private Long wasteId;

    /**
     * 废品质量
     */
    @TableField("quantity")
    private BigDecimal quantity;
}
