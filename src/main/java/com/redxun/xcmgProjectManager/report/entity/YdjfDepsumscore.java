package com.redxun.xcmgProjectManager.report.entity;

import com.redxun.core.entity.BaseTenantEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-8-11.
 */
public class YdjfDepsumscore extends BaseTenantEntity {
    protected String id;
    protected String depid;
    protected String depname;
    protected Double depsumscore;
    protected String time;

    public YdjfDepsumscore() {
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
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepid() {
        return depid;
    }

    public void setDepid(String depid) {
        this.depid = depid;
    }

    public String getDepname() {
        return depname;
    }

    public void setDepname(String depname) {
        this.depname = depname;
    }

    public Double getDepsumscore() {
        return depsumscore;
    }

    public void setDepsumscore(Double depsumscore) {
        this.depsumscore = depsumscore;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
