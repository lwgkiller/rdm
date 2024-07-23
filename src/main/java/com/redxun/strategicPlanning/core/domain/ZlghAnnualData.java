package com.redxun.strategicPlanning.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ZlghAnnualData extends BaseDomain{

    private String id;

    private String kpiId;

    private String folk;

    private String target;

    private String reality;

    private ZlghKpi zlghKpi;

    private Boolean canInsert;

    public Boolean getCanInsert() {
        return canInsert;
    }

    public void setCanInsert(Boolean canInsert) {
        this.canInsert = canInsert;
    }

    public ZlghKpi getZlghKpi() {
        return zlghKpi;
    }

    public void setZlghKpi(ZlghKpi zlghKpi) {
        this.zlghKpi = zlghKpi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getFolk() {
        return folk;
    }

    public void setFolk(String folk) {
        this.folk = folk;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getReality() {
        return reality;
    }

    public void setReality(String reality) {
        this.reality = reality;
    }

    public ZlghAnnualData() {
    }

    public ZlghAnnualData(String id, String kpiId, String folk, String target, String reality, Boolean canInsert) {
        this.id = id;
        this.kpiId = kpiId;
        this.folk = folk;
        this.target = target;
        this.reality = reality;
        this.canInsert = canInsert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ZlghAnnualData that = (ZlghAnnualData) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(kpiId, that.kpiId)
                .append(folk, that.folk)
                .append(target, that.target)
                .append(reality, that.reality)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(kpiId)
                .append(folk)
                .append(target)
                .append(reality)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("kpiId", kpiId)
                .append("folk", folk)
                .append("target", target)
                .append("reality", reality)
                .append("zlghKpi", zlghKpi)
                .toString();
    }
}
