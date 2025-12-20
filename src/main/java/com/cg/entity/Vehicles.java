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
 * 载具表
 * </p>
 *
 * @author 海カ布
 * @since 2024-12-26
 */
@Data
@Accessors(chain = true)
@TableName("vehicles")
public class Vehicles implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "vehicleId", type = IdType.AUTO)
    private Long vehicleId;

    /**
     * 车辆类型
     */
    @TableField("vehicleType")
    private String vehicleType;

    /**
     * 车牌
     */
    @TableField("licensePlate")
    private String licensePlate;
    /**
    *1:可用 2:不可用默认为1
    */
    @TableField("status")
    private String status;
}
