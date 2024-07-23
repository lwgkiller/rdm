package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 企业供应商-国外实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoGwgys extends BaseDomain {
    private static final long serialVersionUID = -4727018830861038088L;
    private String id;
    private String code;
    private String area;
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
                .append("area", area)
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

        if (!(o instanceof InfoGwgys)) return false;

        InfoGwgys infoGwgys = (InfoGwgys) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(code, infoGwgys.code)
                .append(area, infoGwgys.area)
                .append(is_hg, infoGwgys.is_hg)
                .append(gfName, infoGwgys.gfName)
                .append(ptCategory, infoGwgys.ptCategory)
                .append(wlName, infoGwgys.wlName)
                .append(wlCategory, infoGwgys.wlCategory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(code)
                .append(area)
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
