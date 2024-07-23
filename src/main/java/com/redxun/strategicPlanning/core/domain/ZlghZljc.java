package com.redxun.strategicPlanning.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-战略举措实体类
 * @author douhongli
 */
public class ZlghZljc extends BaseDomain  {
    private static final long serialVersionUID = -9084839207864696659L;

    private String id;
    private String zljcName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZljcName() {
        return zljcName;
    }

    public void setZljcName(String zljcName) {
        this.zljcName = zljcName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghZljc)) return false;

        ZlghZljc zlghZljc = (ZlghZljc) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(zljcName, zlghZljc.zljcName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(zljcName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("zljcName", zljcName)
                .toString();
    }
}
