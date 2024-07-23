package com.redxun.strategicPlanning.core.domain.dto;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 战略规划-数据传输类
 * @author douhongli
 * @date 2021年5月27日15:02:11
 */
public class ZlghDto extends BaseDomain {
    private static final long serialVersionUID = 6257123667249753466L;

    private String id;
    private String zljcName;
    private String zlktId;
    private String ktName;
    private String zyhdId;
    private String moveName;
    private String overallGoals;
    private String initiatorId;
    private String initiatorName;
    private String respUserIds;
    private String respUserNames;
    private String respDeptIds;
    private String respDeptNames;
    private String preHdnfId;
    private String preYear;
    private String preJhTarget;
    private String preNzXsjdl;
    private String preNzWczp;
    private String preNdXsjdl;
    private String preNdWczp;
    private String currentHdnfId;
    private String currentYear;
    private String currentJhTarget;
    private String currentNzXsjdl;
    private String currentNzWczp;
    private String currentNdXsjdl;
    private String currentNdWczp;
    private String aftOneHdnfId;
    private String aftOneYear;
    private String aftOneJhTarget;
    private String aftOneNzXsjdl;
    private String aftOneNzWczp;
    private String aftOneNdXsjdl;
    private String aftOneNdWczp;
    private String aftTwoHdnfId;
    private String aftTwoYear;
    private String aftTwoJhTarget;
    private String aftTwoNzXsjdl;
    private String aftTwoNzWczp;
    private String aftTwoNdXsjdl;
    private String aftTwoNdWczp;

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

    public String getZyhdId() {
        return zyhdId;
    }

    public void setZyhdId(String zyhdId) {
        this.zyhdId = zyhdId;
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

    public String getPreHdnfId() {
        return preHdnfId;
    }

    public void setPreHdnfId(String preHdnfId) {
        this.preHdnfId = preHdnfId;
    }

    public String getPreYear() {
        return preYear;
    }

    public void setPreYear(String preYear) {
        this.preYear = preYear;
    }

    public String getPreJhTarget() {
        return preJhTarget;
    }

    public void setPreJhTarget(String preJhTarget) {
        this.preJhTarget = preJhTarget;
    }

    public String getPreNzXsjdl() {
        return preNzXsjdl;
    }

    public void setPreNzXsjdl(String preNzXsjdl) {
        this.preNzXsjdl = preNzXsjdl;
    }

    public String getPreNzWczp() {
        return preNzWczp;
    }

    public void setPreNzWczp(String preNzWczp) {
        this.preNzWczp = preNzWczp;
    }

    public String getPreNdXsjdl() {
        return preNdXsjdl;
    }

    public void setPreNdXsjdl(String preNdXsjdl) {
        this.preNdXsjdl = preNdXsjdl;
    }

    public String getPreNdWczp() {
        return preNdWczp;
    }

    public void setPreNdWczp(String preNdWczp) {
        this.preNdWczp = preNdWczp;
    }

    public String getCurrentHdnfId() {
        return currentHdnfId;
    }

    public void setCurrentHdnfId(String currentHdnfId) {
        this.currentHdnfId = currentHdnfId;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }

    public String getCurrentJhTarget() {
        return currentJhTarget;
    }

    public void setCurrentJhTarget(String currentJhTarget) {
        this.currentJhTarget = currentJhTarget;
    }

    public String getCurrentNzXsjdl() {
        return currentNzXsjdl;
    }

    public void setCurrentNzXsjdl(String currentNzXsjdl) {
        this.currentNzXsjdl = currentNzXsjdl;
    }

    public String getCurrentNzWczp() {
        return currentNzWczp;
    }

    public void setCurrentNzWczp(String currentNzWczp) {
        this.currentNzWczp = currentNzWczp;
    }

    public String getCurrentNdXsjdl() {
        return currentNdXsjdl;
    }

    public void setCurrentNdXsjdl(String currentNdXsjdl) {
        this.currentNdXsjdl = currentNdXsjdl;
    }

    public String getCurrentNdWczp() {
        return currentNdWczp;
    }

    public void setCurrentNdWczp(String currentNdWczp) {
        this.currentNdWczp = currentNdWczp;
    }

    public String getAftOneHdnfId() {
        return aftOneHdnfId;
    }

    public void setAftOneHdnfId(String aftOneHdnfId) {
        this.aftOneHdnfId = aftOneHdnfId;
    }

    public String getAftOneYear() {
        return aftOneYear;
    }

    public void setAftOneYear(String aftOneYear) {
        this.aftOneYear = aftOneYear;
    }

    public String getAftOneJhTarget() {
        return aftOneJhTarget;
    }

    public void setAftOneJhTarget(String aftOneJhTarget) {
        this.aftOneJhTarget = aftOneJhTarget;
    }

    public String getAftOneNzXsjdl() {
        return aftOneNzXsjdl;
    }

    public void setAftOneNzXsjdl(String aftOneNzXsjdl) {
        this.aftOneNzXsjdl = aftOneNzXsjdl;
    }

    public String getAftOneNzWczp() {
        return aftOneNzWczp;
    }

    public void setAftOneNzWczp(String aftOneNzWczp) {
        this.aftOneNzWczp = aftOneNzWczp;
    }

    public String getAftOneNdXsjdl() {
        return aftOneNdXsjdl;
    }

    public void setAftOneNdXsjdl(String aftOneNdXsjdl) {
        this.aftOneNdXsjdl = aftOneNdXsjdl;
    }

    public String getAftOneNdWczp() {
        return aftOneNdWczp;
    }

    public void setAftOneNdWczp(String aftOneNdWczp) {
        this.aftOneNdWczp = aftOneNdWczp;
    }

    public String getAftTwoHdnfId() {
        return aftTwoHdnfId;
    }

    public void setAftTwoHdnfId(String aftTwoHdnfId) {
        this.aftTwoHdnfId = aftTwoHdnfId;
    }

    public String getAftTwoYear() {
        return aftTwoYear;
    }

    public void setAftTwoYear(String aftTwoYear) {
        this.aftTwoYear = aftTwoYear;
    }

    public String getAftTwoJhTarget() {
        return aftTwoJhTarget;
    }

    public void setAftTwoJhTarget(String aftTwoJhTarget) {
        this.aftTwoJhTarget = aftTwoJhTarget;
    }

    public String getAftTwoNzXsjdl() {
        return aftTwoNzXsjdl;
    }

    public void setAftTwoNzXsjdl(String aftTwoNzXsjdl) {
        this.aftTwoNzXsjdl = aftTwoNzXsjdl;
    }

    public String getAftTwoNzWczp() {
        return aftTwoNzWczp;
    }

    public void setAftTwoNzWczp(String aftTwoNzWczp) {
        this.aftTwoNzWczp = aftTwoNzWczp;
    }

    public String getAftTwoNdXsjdl() {
        return aftTwoNdXsjdl;
    }

    public void setAftTwoNdXsjdl(String aftTwoNdXsjdl) {
        this.aftTwoNdXsjdl = aftTwoNdXsjdl;
    }

    public String getAftTwoNdWczp() {
        return aftTwoNdWczp;
    }

    public void setAftTwoNdWczp(String aftTwoNdWczp) {
        this.aftTwoNdWczp = aftTwoNdWczp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghDto)) return false;

        ZlghDto zlghDto = (ZlghDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(zyhdId, zlghDto.zyhdId)
                .append(preHdnfId, zlghDto.preHdnfId)
                .append(preYear, zlghDto.preYear)
                .append(preJhTarget, zlghDto.preJhTarget)
                .append(preNzXsjdl, zlghDto.preNzXsjdl)
                .append(preNzWczp, zlghDto.preNzWczp)
                .append(preNdXsjdl, zlghDto.preNdXsjdl)
                .append(preNdWczp, zlghDto.preNdWczp)
                .append(currentHdnfId, zlghDto.currentHdnfId)
                .append(currentYear, zlghDto.currentYear)
                .append(currentJhTarget, zlghDto.currentJhTarget)
                .append(currentNzXsjdl, zlghDto.currentNzXsjdl)
                .append(currentNzWczp, zlghDto.currentNzWczp)
                .append(currentNdXsjdl, zlghDto.currentNdXsjdl)
                .append(currentNdWczp, zlghDto.currentNdWczp)
                .append(aftOneHdnfId, zlghDto.aftOneHdnfId)
                .append(aftOneYear, zlghDto.aftOneYear)
                .append(aftOneJhTarget, zlghDto.aftOneJhTarget)
                .append(aftOneNzXsjdl, zlghDto.aftOneNzXsjdl)
                .append(aftOneNzWczp, zlghDto.aftOneNzWczp)
                .append(aftOneNdXsjdl, zlghDto.aftOneNdXsjdl)
                .append(aftOneNdWczp, zlghDto.aftOneNdWczp)
                .append(aftTwoHdnfId, zlghDto.aftTwoHdnfId)
                .append(aftTwoYear, zlghDto.aftTwoYear)
                .append(aftTwoJhTarget, zlghDto.aftTwoJhTarget)
                .append(aftTwoNzXsjdl, zlghDto.aftTwoNzXsjdl)
                .append(aftTwoNzWczp, zlghDto.aftTwoNzWczp)
                .append(aftTwoNdXsjdl, zlghDto.aftTwoNdXsjdl)
                .append(aftTwoNdWczp, zlghDto.aftTwoNdWczp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(zyhdId)
                .append(preHdnfId)
                .append(preYear)
                .append(preJhTarget)
                .append(preNzXsjdl)
                .append(preNzWczp)
                .append(preNdXsjdl)
                .append(preNdWczp)
                .append(currentHdnfId)
                .append(currentYear)
                .append(currentJhTarget)
                .append(currentNzXsjdl)
                .append(currentNzWczp)
                .append(currentNdXsjdl)
                .append(currentNdWczp)
                .append(aftOneHdnfId)
                .append(aftOneYear)
                .append(aftOneJhTarget)
                .append(aftOneNzXsjdl)
                .append(aftOneNzWczp)
                .append(aftOneNdXsjdl)
                .append(aftOneNdWczp)
                .append(aftTwoHdnfId)
                .append(aftTwoYear)
                .append(aftTwoJhTarget)
                .append(aftTwoNzXsjdl)
                .append(aftTwoNzWczp)
                .append(aftTwoNdXsjdl)
                .append(aftTwoNdWczp)
                .toHashCode();
    }

    @Override
    public String toString() {
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
                .append("preHdnfId", preHdnfId)
                .append("preYear", preYear)
                .append("preJhTarget", preJhTarget)
                .append("preNzXsjdl", preNzXsjdl)
                .append("preNzWczp", preNzWczp)
                .append("preNdXsjdl", preNdXsjdl)
                .append("preNdWczp", preNdWczp)
                .append("currentHdnfId", currentHdnfId)
                .append("currentYear", currentYear)
                .append("currentJhTarget", currentJhTarget)
                .append("currentNzXsjdl", currentNzXsjdl)
                .append("currentNzWczp", currentNzWczp)
                .append("currentNdXsjdl", currentNdXsjdl)
                .append("currentNdWczp", currentNdWczp)
                .append("aftOneHdnfId", aftOneHdnfId)
                .append("aftOneYear", aftOneYear)
                .append("aftOneJhTarget", aftOneJhTarget)
                .append("aftOneNzXsjdl", aftOneNzXsjdl)
                .append("aftOneNzWczp", aftOneNzWczp)
                .append("aftOneNdXsjdl", aftOneNdXsjdl)
                .append("aftOneNdWczp", aftOneNdWczp)
                .append("aftTwoHdnfId", aftTwoHdnfId)
                .append("aftTwoYear", aftTwoYear)
                .append("aftTwoJhTarget", aftTwoJhTarget)
                .append("aftTwoNzXsjdl", aftTwoNzXsjdl)
                .append("aftTwoNzWczp", aftTwoNzWczp)
                .append("aftTwoNdXsjdl", aftTwoNdXsjdl)
                .append("aftTwoNdWczp", aftTwoNdWczp)
                .toString();
    }
}
