package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 产品样本附件实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoCpybFiles extends BaseDomain {
    private static final long serialVersionUID = 288329349306520695L;
    private String id;
    private String belongId;
    private String fileName;
    private String fileSize;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("belongId", belongId)
                .append("fileName", fileName)
                .append("fileSize", fileSize)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InfoCpybFiles)) return false;

        InfoCpybFiles that = (InfoCpybFiles) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(belongId, that.belongId)
                .append(fileName, that.fileName)
                .append(fileSize, that.fileSize)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(belongId)
                .append(fileName)
                .append(fileSize)
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
