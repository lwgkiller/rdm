package com.redxun.strategicPlanning.core.domain.dto;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-战略课题实体类
 * @author douhongli
 */
public class ZlghZlktDto extends BaseDomain  {

    private static final long serialVersionUID = -6995551047862786130L;

    private String id;
    private String zlktId;
    private String ktName;
    private String zljcName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZlktId() {
        return zlktId;
    }

    public void setZlktId(String zlktId) {
        this.zlktId = zlktId;
    }

    public String getKtName() {
        return ktName;
    }

    public void setKtName(String ktName) {
        this.ktName = ktName;
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

        if (!(o instanceof ZlghZlktDto)) return false;

        ZlghZlktDto zlghZlkt = (ZlghZlktDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, zlghZlkt.id)
                .append(zlktId, zlghZlkt.zlktId)
                .append(ktName, zlghZlkt.ktName)
                .append(zljcName, zlghZlkt.zljcName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(id)
                .append(zlktId)
                .append(ktName)
                .append(zljcName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("zljcId", zlktId)
                .append("ktName", ktName)
                .append("zljcName", zljcName)
                .toString();
    }
}
