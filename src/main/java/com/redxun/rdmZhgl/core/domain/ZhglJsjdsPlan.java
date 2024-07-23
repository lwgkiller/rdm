package com.redxun.rdmZhgl.core.domain;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

/**
 * 综合管理-技术交底书计划数 实体类
 * @author douhongli
 * @since 2021年6月9日16:04:10
 */
public class ZhglJsjdsPlan extends BaseDomain {

    private static final long serialVersionUID = -4119077157807828028L;
    private String id;
    @NotNull(message = "部门id不能为空")
    private String deptId;
    private String year;
    @NotNull(message = "月份不能为空")
    private String month;
    @NotNull(message = "技术交底书总数不能为空")
    private String total;

    private String inventTotal;

    public String getInventTotal() {
        return inventTotal;
    }

    public void setInventTotal(String inventTotal) {
        this.inventTotal = inventTotal;
    }

    private String deptName;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZhglJsjdsPlan)) return false;

        ZhglJsjdsPlan that = (ZhglJsjdsPlan) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(deptId, that.deptId)
                .append(month, that.month)
                .append(total, that.total)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(deptId)
                .append(month)
                .append(total)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("deptId", deptId)
                .append("month", month)
                .append("total", total)
                .toString();
    }
}
