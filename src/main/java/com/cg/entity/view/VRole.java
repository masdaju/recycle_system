package com.cg.entity.view;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName v_role
 */
@TableName(value ="v_role")
@Data
public class VRole implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 角色id
     */

    private Long roleId;
    /**
     * 资源名称
     */
    private String name;

    /**
     * 父id
     */
    private Long pid;

    /**
     * 资源类型，menu-菜单，button-按钮
     */
    private String type;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 图标
     */
    private String icon;

    /**
     * 资源值
     */
    private String resValue;

    /**
     * 排序，从小到大
     */
    private Integer sortIndex;

    @TableField(exist = false)
    List<VRole> children;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        VRole other = (VRole) that;
        return (this.getRoleId() == null ? other.getRoleId() == null : this.getRoleId().equals(other.getRoleId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getPid() == null ? other.getPid() == null : this.getPid().equals(other.getPid()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getPath() == null ? other.getPath() == null : this.getPath().equals(other.getPath()))
            && (this.getIcon() == null ? other.getIcon() == null : this.getIcon().equals(other.getIcon()))
            && (this.getResValue() == null ? other.getResValue() == null : this.getResValue().equals(other.getResValue()))
            && (this.getSortIndex() == null ? other.getSortIndex() == null : this.getSortIndex().equals(other.getSortIndex()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoleId() == null) ? 0 : getRoleId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getPid() == null) ? 0 : getPid().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
        result = prime * result + ((getIcon() == null) ? 0 : getIcon().hashCode());
        result = prime * result + ((getResValue() == null) ? 0 : getResValue().hashCode());
        result = prime * result + ((getSortIndex() == null) ? 0 : getSortIndex().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleId=").append(roleId);
        sb.append(", name=").append(name);
        sb.append(", pid=").append(pid);
        sb.append(", type=").append(type);
        sb.append(", path=").append(path);
        sb.append(", icon=").append(icon);
        sb.append(", resValue=").append(resValue);
        sb.append(", sortIndex=").append(sortIndex);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }



}