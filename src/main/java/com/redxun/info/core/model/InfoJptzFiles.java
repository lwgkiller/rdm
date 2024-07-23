package com.redxun.info.core.model;

import com.redxun.strategicPlanning.core.domain.BaseDomain;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 竞品图纸附件实体类
 * @author douhongli
 * @date 2021年5月31日10:02:34
 */
public class InfoJptzFiles extends BaseDomain {
    private static final long serialVersionUID = -5485305166657403066L;
    private String id;
    private String fileName;
    private String fileSize;
    private String belongId;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("fileName", fileName)
                .append("fileSize", fileSize)
                .append("belongId", belongId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof InfoJptzFiles)) return false;

        InfoJptzFiles that = (InfoJptzFiles) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(fileName, that.fileName)
                .append(fileSize, that.fileSize)
                .append(belongId, that.belongId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(fileName)
                .append(fileSize)
                .append(belongId)
                .toHashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBelongId() {
        return belongId;
    }

    public void setBelongId(String belongId) {
        this.belongId = belongId;
    }
}
