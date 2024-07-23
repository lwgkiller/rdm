package com.redxun.xcmgProjectManager.report.entity;

public class XcmgProjectReportYwph {
    private String projectDepName;
    private int processOk;
    private int processDelay;
    private double delayRate;

    public XcmgProjectReportYwph(String projectDepName) {
        this.projectDepName=projectDepName;
    }

    public String getProjectDepName() {
        return projectDepName;
    }

    public void setProjectDepName(String projectDepName) {
        this.projectDepName = projectDepName;
    }

    public int getProcessOk() {
        return processOk;
    }

    public void setProcessOk(int processOk) {
        this.processOk = processOk;
    }

    public int getProcessDelay() {
        return processDelay;
    }

    public void setProcessDelay(int processDelay) {
        this.processDelay = processDelay;
    }

    public double getDelayRate() {
        return delayRate;
    }

    public void setDelayRate(double delayRate) {
        this.delayRate = delayRate;
    }
}



