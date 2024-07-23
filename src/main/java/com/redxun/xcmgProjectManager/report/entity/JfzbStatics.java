package com.redxun.xcmgProjectManager.report.entity;

import com.redxun.core.entity.BaseTenantEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-8-11.
 */
public class JfzbStatics {
    protected String levelname;
    protected int levelsum;

    public JfzbStatics(String levelname) {
        this.levelname=levelname;
    }


    public String getLevelname() {
        return levelname;
    }

    public void setLevelname(String levelname) {
        this.levelname = levelname;
    }

    public int getLevelsum() {
        return levelsum;
    }

    public void setLevelsum(int levelsum) {
        this.levelsum = levelsum;
    }
}
