package com.redxun.strategicPlanning.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class BaseDomain implements Serializable {

    private static final long serialVersionUID = 101771541102019531L;
    /*miniUI 新增/更新/删除判断*/
    public static final String ADD = "added";
    public static final String UPDATE = "modified";
    public static final String REMOVE = "removed";

    private String createBy;

    private String creator;
    private String updator;

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createTime;

    private String tenantId;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date updateTime;

    private Integer _id = 0;

    private Integer _uid;

    private String _state;

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer get_uid() {
        return _uid;
    }

    public void set_uid(Integer _uid) {
        this._uid = _uid;
    }

    public String get_state() {
        return _state;
    }

    public void set_state(String _state) {
        this._state = _state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseDomain that = (BaseDomain) o;

        return new EqualsBuilder()
                .append(tenantId, that.tenantId)
                .append(updateBy, that.updateBy)
                .append(updateTime, that.updateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tenantId)
                .append(updateBy)
                .append(updateTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("createBy", createBy)
                .append("createTime", createTime)
                .append("tenantId", tenantId)
                .append("updateBy", updateBy)
                .append("updateTime", updateTime)
                .toString();
    }
}
