package com.cg.entity.view;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("v_waste")
public class VWaste {
    private Long id;
    private Long cid;
    private Long requestId;
    private Long wasteId;
    private Long userId;
    /**
     * 分类名称
     */
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
    private Integer status;
//    private String username;
    private String wasteName;
    private String time;
    private String reportDate;


}
