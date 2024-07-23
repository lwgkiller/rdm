package com.redxun.strategicPlanning.core.domain;

import java.util.List;

public class ZlghKpi extends BaseDomain {

    private String id;

    private String kpiName;

    /*前一年的kpi数据*/
    private String preId;

    private String preFolk;

    private String preTarget;

    private String preReality;

    /*当前年的kpi数据*/
    private String currentId;

    private String currentFolk;

    private String currentTarget;

    private String currentReality;

     /*后一年的kpi数据*/
    private String afterOneId;

    private String afterOneFolk;

    private String afterOneTarget;

    private String afterOneReality;


     /*后二年的kpi数据*/
    private String afterTwoId;

    private String afterTwoFolk;

    private String afterTwoTarget;

    private String afterTwoReality;


     /*后三年的kpi数据*/
    private String afterThreeId;

    private String afterThreeFolk;

    private String afterThreeTarget;

    private String afterThreeReality;


     /*后四年的kpi数据*/
    private String afterFourId;

    private String afterFourFolk;

    private String afterFourTarget;

    private String afterFourReality;

    public String getPreId() {
        return preId;
    }

    public void setPreId(String preId) {
        this.preId = preId;
    }

    public String getPreFolk() {
        return preFolk;
    }

    public void setPreFolk(String preFolk) {
        this.preFolk = preFolk;
    }

    public String getPreTarget() {
        return preTarget;
    }

    public void setPreTarget(String preTarget) {
        this.preTarget = preTarget;
    }

    public String getPreReality() {
        return preReality;
    }

    public void setPreReality(String preReality) {
        this.preReality = preReality;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getCurrentFolk() {
        return currentFolk;
    }

    public void setCurrentFolk(String currentFolk) {
        this.currentFolk = currentFolk;
    }

    public String getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(String currentTarget) {
        this.currentTarget = currentTarget;
    }

    public String getCurrentReality() {
        return currentReality;
    }

    public void setCurrentReality(String currentReality) {
        this.currentReality = currentReality;
    }

    public String getAfterOneId() {
        return afterOneId;
    }

    public void setAfterOneId(String afterOneId) {
        this.afterOneId = afterOneId;
    }

    public String getAfterOneFolk() {
        return afterOneFolk;
    }

    public void setAfterOneFolk(String afterOneFolk) {
        this.afterOneFolk = afterOneFolk;
    }

    public String getAfterOneTarget() {
        return afterOneTarget;
    }

    public void setAfterOneTarget(String afterOneTarget) {
        this.afterOneTarget = afterOneTarget;
    }

    public String getAfterOneReality() {
        return afterOneReality;
    }

    public void setAfterOneReality(String afterOneReality) {
        this.afterOneReality = afterOneReality;
    }

    public String getAfterTwoId() {
        return afterTwoId;
    }

    public void setAfterTwoId(String afterTwoId) {
        this.afterTwoId = afterTwoId;
    }

    public String getAfterTwoFolk() {
        return afterTwoFolk;
    }

    public void setAfterTwoFolk(String afterTwoFolk) {
        this.afterTwoFolk = afterTwoFolk;
    }

    public String getAfterTwoTarget() {
        return afterTwoTarget;
    }

    public void setAfterTwoTarget(String afterTwoTarget) {
        this.afterTwoTarget = afterTwoTarget;
    }

    public String getAfterTwoReality() {
        return afterTwoReality;
    }

    public void setAfterTwoReality(String afterTwoReality) {
        this.afterTwoReality = afterTwoReality;
    }

    public String getAfterThreeId() {
        return afterThreeId;
    }

    public void setAfterThreeId(String afterThreeId) {
        this.afterThreeId = afterThreeId;
    }

    public String getAfterThreeFolk() {
        return afterThreeFolk;
    }

    public void setAfterThreeFolk(String afterThreeFolk) {
        this.afterThreeFolk = afterThreeFolk;
    }

    public String getAfterThreeTarget() {
        return afterThreeTarget;
    }

    public void setAfterThreeTarget(String afterThreeTarget) {
        this.afterThreeTarget = afterThreeTarget;
    }

    public String getAfterThreeReality() {
        return afterThreeReality;
    }

    public void setAfterThreeReality(String afterThreeReality) {
        this.afterThreeReality = afterThreeReality;
    }

    public String getAfterFourId() {
        return afterFourId;
    }

    public void setAfterFourId(String afterFourId) {
        this.afterFourId = afterFourId;
    }

    public String getAfterFourFolk() {
        return afterFourFolk;
    }

    public void setAfterFourFolk(String afterFourFolk) {
        this.afterFourFolk = afterFourFolk;
    }

    public String getAfterFourTarget() {
        return afterFourTarget;
    }

    public void setAfterFourTarget(String afterFourTarget) {
        this.afterFourTarget = afterFourTarget;
    }

    public String getAfterFourReality() {
        return afterFourReality;
    }

    public void setAfterFourReality(String afterFourReality) {
        this.afterFourReality = afterFourReality;
    }

    private List<ZlghAnnualData> zlghAnnualDataList;

    public List<ZlghAnnualData> getZlghAnnualDataList() {
        return zlghAnnualDataList;
    }

    public void setZlghAnnualDataList(List<ZlghAnnualData> zlghAnnualDataList) {
        this.zlghAnnualDataList = zlghAnnualDataList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }

}
