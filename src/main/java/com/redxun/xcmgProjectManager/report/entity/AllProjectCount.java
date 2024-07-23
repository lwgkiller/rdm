package com.redxun.xcmgProjectManager.report.entity;

import com.redxun.core.entity.BaseTenantEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-8-11.
 */
public class AllProjectCount extends BaseTenantEntity {
    protected String id;
    protected String categoryname;
    protected String projectcount;

    public AllProjectCount() {
    }

    public String getCategoryName() {
        return categoryname;
    }

    public void setCategoryName(String categoryName) {
        this.categoryname = categoryname;
    }

    public String getProjectCount() {
        return projectcount;
    }

    public void setProjectCount(String projectCount) {
        this.projectcount = projectcount;
    }

    @Override
    public String getIdentifyLabel() {
        return this.id;
    }

    @Override
    public Serializable getPkId() {
        return this.id;
    }

    @Override
    public void setPkId(Serializable pkId) {
        this.id = (String) pkId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String aValue) {
        this.id = aValue;
    }
}
