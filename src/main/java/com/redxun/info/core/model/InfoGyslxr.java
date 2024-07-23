package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 企业供应商联系人实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoGyslxr extends BaseDomain {
    private static final long serialVersionUID = -8079034621122641142L;
    private String id;
    private String belongId;
    private String contactName;
    private String post;
    private String phone;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("belongId", belongId)
                .append("contactName", contactName)
                .append("post", post)
                .append("phone", phone)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InfoGyslxr)) return false;

        InfoGyslxr that = (InfoGyslxr) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(belongId, that.belongId)
                .append(contactName, that.contactName)
                .append(post, that.post)
                .append(phone, that.phone)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(belongId)
                .append(contactName)
                .append(post)
                .append(phone)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
