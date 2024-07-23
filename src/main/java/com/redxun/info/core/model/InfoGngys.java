package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 企业供应商-国内实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoGngys extends BaseDomain {

    private static final long serialVersionUID = 2210150453858968822L;
    private String id;
    private String code;
    private String is_hg;
    private String gfName;
    private String ptCategory;
    private String wlName;
    private String wlCategory;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("code", code)
                .append("is_hg", is_hg)
                .append("gfName", gfName)
                .append("ptCategory", ptCategory)
                .append("wlName", wlName)
                .append("wlCategory", wlCategory)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InfoGngys)) return false;

        InfoGngys infoGngys = (InfoGngys) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(id, infoGngys.id)
                .append(code, infoGngys.code)
                .append(is_hg, infoGngys.is_hg)
                .append(gfName, infoGngys.gfName)
                .append(ptCategory, infoGngys.ptCategory)
                .append(wlName, infoGngys.wlName)
                .append(wlCategory, infoGngys.wlCategory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(id)
                .append(code)
                .append(is_hg)
                .append(gfName)
                .append(ptCategory)
                .append(wlName)
                .append(wlCategory)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIs_hg() {
        return is_hg;
    }

    public void setIs_hg(String is_hg) {
        this.is_hg = is_hg;
    }

    public String getGfName() {
        return gfName;
    }

    public void setGfName(String gfName) {
        this.gfName = gfName;
    }

    public String getPtCategory() {
        return ptCategory;
    }

    public void setPtCategory(String ptCategory) {
        this.ptCategory = ptCategory;
    }

    public String getWlName() {
        return wlName;
    }

    public void setWlName(String wlName) {
        this.wlName = wlName;
    }

    public String getWlCategory() {
        return wlCategory;
    }

    public void setWlCategory(String wlCategory) {
        this.wlCategory = wlCategory;
    }
}
