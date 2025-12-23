package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 运输调度表
 * </p>
 *
 * @author MIZUGI
 * @since 2024-12-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("transport_schedules")
public class TransportSchedules implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "schedule_id", type = IdType.AUTO)
    private Long scheduleId;
    @TableField("collector_id")
    private Long collectorId;
    @TableField("user_id")
    private Long userId;
    @TableField("request_id")
    private Long requestId;
    @TableField("vehicle_id")
    private Long vehicleId;
    @TableField("route")
    private String route;

    /**
     *0待处理1已处理(安排回收员和车辆--在运输调度表里添加)2已完成(在回收员回收完成将状态改为2)
     */
    @TableField("status")
    private Integer status;

    public TransportSchedules(Long collectorId, Long userId, Long vehicleId, String route) {
        this.collectorId = collectorId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.route = route;

    }
}
