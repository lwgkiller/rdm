package com.redxun.strategicPlanning.core.domain.dto;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-主要活动数据传输类
 * @author douhongli
 */
public class ZlghZyhdDto extends BaseDomain {

    private static final long serialVersionUID = 6133056448328140710L;

    /**
     * 战略举措id
     */
    private String id;
    /**
     * 战略举措名称
     */
    private String zljcName;

    /**
     * 战略课题Id
     */
    private String zlktId;

    /**
     * 战略课题名称
     */
    private String ktName;

    /**
     * 主要活动id
     */
    private String zyhdId;

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

    public String getZlktId() {
        return zlktId;
    }

    public void setZlktId(String zlktId) {
        this.zlktId = zlktId;
    }

    public String getZljcName() {
        return zljcName;
    }

    public void setZljcName(String zljcName) {
        this.zljcName = zljcName;
    }

    public String getKtName() {
        return ktName;
    }

    public void setKtName(String ktName) {
        this.ktName = ktName;
    }

    public String getZyhdId() {
        return zyhdId;
    }

    public void setZyhdId(String zyhdId) {
        this.zyhdId = zyhdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghZyhdDto)) return false;

        ZlghZyhdDto that = (ZlghZyhdDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(zlktId, that.zlktId)
                .append(zyhdId, that.zyhdId)
                .append(moveName, that.moveName)
                .append(overallGoals, that.overallGoals)
                .append(initiatorId, that.initiatorId)
                .append(initiatorName, that.initiatorName)
                .append(respUserIds, that.respUserIds)
                .append(respUserNames, that.respUserNames)
                .append(respDeptIds, that.respDeptIds)
                .append(respDeptNames, that.respDeptNames)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(zlktId)
                .append(zyhdId)
                .append(moveName)
                .append(overallGoals)
                .append(initiatorId)
                .append(initiatorName)
                .append(respUserIds)
                .append(respUserNames)
                .append(respDeptIds)
                .append(respDeptNames)
                .toHashCode();
    }

    @Override
    public String
    toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("zljcName", zljcName)
                .append("zlktId", zlktId)
                .append("ktName", ktName)
                .append("zyhdId", zyhdId)
                .append("moveName", moveName)
                .append("overallGoals", overallGoals)
                .append("initiatorId", initiatorId)
                .append("initiatorName", initiatorName)
                .append("respUserIds", respUserIds)
                .append("respUserNames", respUserNames)
                .append("respDeptIds", respDeptIds)
                .append("respDeptNames", respDeptNames)
                .toString();
    }
}
