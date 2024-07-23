package com.redxun.restApi.orguser.entity;

/**
 * 模块：jsaas
 * 包名：com.redxun.restApi.orguser.entity
 * 功能描述：[TODO]
 *
 * @author：think
 * @date:2019/7/12
 */
public class TenantEntity {
    private String instId;
    /**
     * 机构中文名，必填
     */
    private String nameCn;
    private String nameEn;
    /**
     * 机构编码，必填
     */
    private String instNo;
    /**
     * 机构域名，必填
     */
    private String domain;
    /**
     * 法人
     */
    private String legalMan;
    private String address;
    private String phone;
    private String mail;
    private String fax;
    private String contractor;
    private String cmpId;

    public String getCmpId() {
        return cmpId;
    }

    public void setCmpId(String cmpId) {
        this.cmpId = cmpId;
    }

    public String getInstId() {
        return instId;
    }

    public void setInstId(String instId) {
        this.instId = instId;
    }

    /**
     * 机构类型编码，来自sys_inst_type表
     */
    private String instType;

    public TenantEntity() {
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getInstNo() {
        return instNo;
    }

    public void setInstNo(String instNo) {
        this.instNo = instNo;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLegalMan() {
        return legalMan;
    }

    public void setLegalMan(String legalMan) {
        this.legalMan = legalMan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getContractor() {
        return contractor;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getInstType() {
        return instType;
    }

    public void setInstType(String instType) {
        this.instType = instType;
    }
}
