package com.cg.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 文件表
 * </p>
 *
 * @author 海カ布
 * @since 2024-10-22
 */
@Data
@Accessors(chain = true)
@TableName("sys_file")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 逻辑删除，0 - 正常 1 - 已删除
     */
    @TableField("status")
    private Byte status;

    /**
     * 文件名字
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件存放地址
     */
    @TableField("real_path")
    private String realPath;

    /**
     * 文件预览地址
     */
    @TableField("file_url")
    private String fileUrl;
}
