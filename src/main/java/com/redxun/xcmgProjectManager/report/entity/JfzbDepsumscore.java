package com.redxun.xcmgProjectManager.report.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-8-11.
 */
public class JfzbDepsumscore {
    protected String depname;
    protected int depsumscore;
    protected Map<String, JfzbStatics> levelName2Statics = new HashMap<>();
    protected List<JfzbStatics> jfzbStaticsList = new ArrayList<>();

    public List<JfzbStatics> getJfzbStaticsList() {
        return jfzbStaticsList;
    }

    public void setJfzbStaticsList(List<JfzbStatics> jfzbStaticsList) {
        jfzbStaticsList = jfzbStaticsList;
    }

    public JfzbDepsumscore(String depname) {
        this.depname = depname;
    }

    public String getDepname() {
        return depname;
    }

    public void setDepname(String depname) {
        this.depname = depname;
    }

    public Map<String, JfzbStatics> getLevelName2Statics() {
        return levelName2Statics;
    }

    public void setLevelName2Statics(Map<String, JfzbStatics> levelName2Statics) {
        this.levelName2Statics = levelName2Statics;
    }

    public int getDepsumscore() {
        return depsumscore;
    }

    public void setDepsumscore(int depsumscore) {
        this.depsumscore = depsumscore;
    }
}