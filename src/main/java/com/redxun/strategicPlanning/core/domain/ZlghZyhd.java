package com.redxun.strategicPlanning.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-主要活动实体类
 * @author douhongli
 */
public class ZlghZyhd extends BaseDomain  {

    private static final long serialVersionUID = -8563282631071501608L;

    private String id;
    /**
     * 活动名称
     */
    private String moveName;
    /**
     * 总体目标
     */
    private String overallGoals;
    /**
     * 牵头人Id
     */
    private String initiatorId;
    /**
     * 牵头人Name
     */
    private String initiatorName;
    /**
     * 责任人Ids
     */
    private String respUserIds;
    /**
     * 责任人Names
     */
    private String respUserNames;
    /**
     * 责任部门Ids
     */
    private String respDeptIds;
    /**
     * 责任部门Names
     */
    private String respDeptNames;
    /**
     * 战略举措Id
     */
    private String zljcId;
    /**
     * 战略课题Id
     */
    private String zlktId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoveName() {
        return moveName;
    }

    public void setMoveName(String moveName) {
        this.moveName = moveName;
    }

    public String getOverallGoals() {
        return overallGoals;
    }

    public void setOverallGoals(String overallGoals) {
        this.overallGoals = overallGoals;
    }

    public String getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getRespUserIds() {
        return respUserIds;
    }

    public void setRespUserIds(String respUserIds) {
        this.respUserIds = respUserIds;
    }

    public String getRespUserNames() {
        return respUserNames;
    }

    public void setRespUserNames(String respUserNames) {
        this.respUserNames = respUserNames;
    }

    public String getRespDeptIds() {
        return respDeptIds;
    }

    public void setRespDeptIds(String respDeptIds) {
        this.respDeptIds = respDeptIds;
    }

    public String getRespDeptNames() {
        return respDeptNames;
    }

    public void setRespDeptNames(String respDeptNames) {
        this.respDeptNames = respDeptNames;
    }

    public String getZljcId() {
        return zljcId;
    }

    public void setZljcId(String zljcId) {
        this.zljcId = zljcId;
    }

    public String getZlktId() {
        return zlktId;
    }

    public void setZlktId(String zlktId) {
        this.zlktId = zlktId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghZyhd)) return false;

        ZlghZyhd zlghZyhd = (ZlghZyhd) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(moveName, zlghZyhd.moveName)
                .append(overallGoals, zlghZyhd.overallGoals)
                .append(initiatorId, zlghZyhd.initiatorId)
                .append(initiatorName, zlghZyhd.initiatorName)
                .append(respUserIds, zlghZyhd.respUserIds)
                .append(respUserNames, zlghZyhd.respUserNames)
                .append(respDeptIds, zlghZyhd.respDeptIds)
                .append(respDeptNames, zlghZyhd.respDeptNames)
                .append(zljcId, zlghZyhd.zljcId)
                .append(zlktId, zlghZyhd.zlktId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(moveName)
                .append(overallGoals)
                .append(initiatorId)
                .append(initiatorName)
                .append(respUserIds)
                .append(respUserNames)
                .append(respDeptIds)
                .append(respDeptNames)
                .append(zljcId)
                .append(zlktId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("moveName", moveName)
                .append("overallGoals", overallGoals)
                .append("initiatorId", initiatorId)
                .append("initiatorName", initiatorName)
                .append("respUserIds", respUserIds)
                .append("respUserNames", respUserNames)
                .append("respDeptIds", respDeptIds)
                .append("respDeptNames", respDeptNames)
                .append("zljcId", zljcId)
                .append("zlktId", zlktId)
                .toString();
    }
}
