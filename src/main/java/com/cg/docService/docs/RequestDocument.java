package com.cg.docService.docs;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Author: MIZUGI
 * Date: 2025/11/28
 * Description:
 */
@Data
public class RequestDocument{
    @JsonProperty(value = "request_id")
    private Long requestId;
    @JsonProperty("user_id")
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date appointmentTime;
    private Integer status;
    private String address;
    private String remark;
    @TableField(exist = false)
    private List<Long> wid;
}
