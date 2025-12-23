package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 废品表
 * </p>
 */
@Data
@Accessors(chain = true)
@TableName("waste")
public class Waste implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "waste_id", type = IdType.AUTO)
    private Long wasteId;

    /**
     * 废品分类id
     */
    @TableField("cid")
    private Long cid;

    /**
     * 创建时间
     */
    @TableField("create_date")
    private Date createDate;

    /**
     * 废品名称
     */
    @TableField("name")
    private String name;

    /**
     * 废品描述
     */
    @TableField("description")
    private String description;
    /**
     * 图片地址
     */
    @TableField("img_url")
    private String imgUrl;
    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;
    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

}
