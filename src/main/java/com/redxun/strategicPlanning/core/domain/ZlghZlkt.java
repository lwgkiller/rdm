package com.redxun.strategicPlanning.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-战略课题实体类
 * @author douhongli
 */
public class ZlghZlkt extends BaseDomain  {

    private static final long serialVersionUID = -6995551047862786130L;

    private String id;
    private String zljcId;
    private String ktName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZljcId() {
        return zljcId;
    }

    public void setZljcId(String zljcId) {
        this.zljcId = zljcId;
    }

    public String getKtName() {
        return ktName;
    }

    public void setKtName(String ktName) {
        this.ktName = ktName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghZlkt)) return false;

        ZlghZlkt zlghZlkt = (ZlghZlkt) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, zlghZlkt.id)
                .append(zljcId, zlghZlkt.zljcId)
                .append(ktName, zlghZlkt.ktName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(id)
                .append(zljcId)
                .append(ktName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("zljcId", zljcId)
                .append("ktName", ktName)
                .toString();
    }
}
