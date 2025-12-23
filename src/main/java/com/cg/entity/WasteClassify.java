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
 * 废品分类表
 * </p>
 */
@Data
@Accessors(chain = true)
@TableName("waste_classify")
public class WasteClassify implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "cid", type = IdType.AUTO)
    private Long cid;
    /**
     * 分类名称
     */
    @TableField("name")
    private String name;
    @TableField(exist = false)
    private Integer month;
    @TableField(exist = false)
    private Integer countNum;
    @TableField(exist = false)
    private BigDecimal quantity;
}
