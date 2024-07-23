package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 竞品图纸实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoJptz extends BaseDomain {
    private static final long serialVersionUID = -4936678904644649450L;
    private String id;
    private String paperFactory;
    private String paperCategory;
    private String paperDesc;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("paperFactory", paperFactory)
                .append("paperCategory", paperCategory)
                .append("paperDesc", paperDesc)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InfoJptz)) return false;

        InfoJptz infoJptz = (InfoJptz) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(paperFactory, infoJptz.paperFactory)
                .append(paperCategory, infoJptz.paperCategory)
                .append(paperDesc, infoJptz.paperDesc)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(paperFactory)
                .append(paperCategory)
                .append(paperDesc)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaperFactory() {
        return paperFactory;
    }

    public void setPaperFactory(String paperFactory) {
        this.paperFactory = paperFactory;
    }

    public String getPaperCategory() {
        return paperCategory;
    }

    public void setPaperCategory(String paperCategory) {
        this.paperCategory = paperCategory;
    }

    public String getPaperDesc() {
        return paperDesc;
    }

    public void setPaperDesc(String paperDesc) {
        this.paperDesc = paperDesc;
    }
}
