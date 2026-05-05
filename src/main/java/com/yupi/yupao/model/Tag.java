package com.yupi.yupao.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签
 * @TableName tag
 */
@TableName(value ="tag")
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagname;

    /**
     * 用户id
     */
    private Long useid;

    /**
     * 父标签Id
     */
    private Long parentid;

    /**
     * 0-不是，1-父标签
     */
    private Integer idparent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 标签名称
     */
    public String getTagname() {
        return tagname;
    }

    /**
     * 标签名称
     */
    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    /**
     * 用户id
     */
    public Long getUseid() {
        return useid;
    }

    /**
     * 用户id
     */
    public void setUseid(Long useid) {
        this.useid = useid;
    }

    /**
     * 父标签Id
     */
    public Long getParentid() {
        return parentid;
    }

    /**
     * 父标签Id
     */
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    /**
     * 0-不是，1-父标签
     */
    public Integer getIdparent() {
        return idparent;
    }

    /**
     * 0-不是，1-父标签
     */
    public void setIdparent(Integer idparent) {
        this.idparent = idparent;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * 
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

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
        Tag other = (Tag) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTagname() == null ? other.getTagname() == null : this.getTagname().equals(other.getTagname()))
            && (this.getUseid() == null ? other.getUseid() == null : this.getUseid().equals(other.getUseid()))
            && (this.getParentid() == null ? other.getParentid() == null : this.getParentid().equals(other.getParentid()))
            && (this.getIdparent() == null ? other.getIdparent() == null : this.getIdparent().equals(other.getIdparent()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTagname() == null) ? 0 : getTagname().hashCode());
        result = prime * result + ((getUseid() == null) ? 0 : getUseid().hashCode());
        result = prime * result + ((getParentid() == null) ? 0 : getParentid().hashCode());
        result = prime * result + ((getIdparent() == null) ? 0 : getIdparent().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", tagname=").append(tagname);
        sb.append(", useid=").append(useid);
        sb.append(", parentid=").append(parentid);
        sb.append(", idparent=").append(idparent);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}