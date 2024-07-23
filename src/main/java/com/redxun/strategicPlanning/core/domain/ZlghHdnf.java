package com.redxun.strategicPlanning.core.domain;

import com.redxun.saweb.util.IdUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * 战略规划-活动年份实体类
 * @author douhongli
 * @date 2021年5月27日16:38:51
 */
public class ZlghHdnf extends BaseDomain  {

    private static final long serialVersionUID = -3511503082889717244L;

    private String id;
    private String zyhdId;
    private String year;
    private String jhTarget;
    private String nzXsjdl;
    private String nzWczp;
    private String ndXsjdl;
    private String ndWczp;

    public ZlghHdnf() {
    }

    public ZlghHdnf(String zyhdId, String year, String jhTarget, String nzXsjdl, String nzWczp, String ndXsjdl, String ndWczp, String createBy) {
        this.id = IdUtil.getId();
        this.zyhdId = zyhdId;
        this.year = year;
        this.jhTarget = jhTarget;
        this.nzXsjdl = nzXsjdl;
        this.nzWczp = nzWczp;
        this.ndXsjdl = ndXsjdl;
        this.ndWczp = ndWczp;
        super.setCreateBy(createBy);
    }


    public ZlghHdnf(String id, String zyhdId, String year, String jhTarget, String nzXsjdl, String nzWczp, String ndXsjdl, String ndWczp, String updateBy) {
        this.id = id;
        this.zyhdId = zyhdId;
        this.year = year;
        this.jhTarget = jhTarget;
        this.nzXsjdl = nzXsjdl;
        this.nzWczp = nzWczp;
        this.ndXsjdl = ndXsjdl;
        this.ndWczp = ndWczp;
        super.setUpdateBy(updateBy);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZyhdId() {
        return zyhdId;
    }

    public void setZyhdId(String zyhdId) {
        this.zyhdId = zyhdId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getJhTarget() {
        return jhTarget;
    }

    public void setJhTarget(String jhTarget) {
        this.jhTarget = jhTarget;
    }

    public String getNzXsjdl() {
        return nzXsjdl;
    }

    public void setNzXsjdl(String nzXsjdl) {
        this.nzXsjdl = nzXsjdl;
    }

    public String getNzWczp() {
        return nzWczp;
    }

    public void setNzWczp(String nzWczp) {
        this.nzWczp = nzWczp;
    }

    public String getNdXsjdl() {
        return ndXsjdl;
    }

    public void setNdXsjdl(String ndXsjdl) {
        this.ndXsjdl = ndXsjdl;
    }

    public String getNdWczp() {
        return ndWczp;
    }

    public void setNdWczp(String ndWczp) {
        this.ndWczp = ndWczp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ZlghHdnf)) return false;

        ZlghHdnf zlghHdnf = (ZlghHdnf) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(zyhdId, zlghHdnf.zyhdId)
                .append(year, zlghHdnf.year)
                .append(jhTarget, zlghHdnf.jhTarget)
                .append(nzXsjdl, zlghHdnf.nzXsjdl)
                .append(nzWczp, zlghHdnf.nzWczp)
                .append(ndXsjdl, zlghHdnf.ndXsjdl)
                .append(ndWczp, zlghHdnf.ndWczp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(zyhdId)
                .append(year)
                .append(jhTarget)
                .append(nzXsjdl)
                .append(nzWczp)
                .append(ndXsjdl)
                .append(ndWczp)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("zyhdId", zyhdId)
                .append("year", year)
                .append("jhTarget", jhTarget)
                .append("nzXsjdl", nzXsjdl)
                .append("nzWczp", nzWczp)
                .append("ndXsjdl", ndXsjdl)
                .append("ndWczp", ndWczp)
                .toString();
    }
}
