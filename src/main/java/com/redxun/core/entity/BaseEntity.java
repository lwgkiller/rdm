package com.redxun.core.entity;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.redxun.core.annotion.table.FieldDefine;
import com.redxun.core.constants.MBoolean;
import com.redxun.core.json.JsonDateSerializer;
import com.redxun.core.xstream.convert.DateConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;
/**
 * 
 * <pre> 
 * 描述：实体基础类
 * 构建组：mibase
 * 作者：keith
 * 邮箱:keith@mitom.cn
 * 日期:2014-1-31-下午2:21:51
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable{

	protected static final long serialVersionUID = 1L;

	public BaseEntity() {
        
    }
    /**
     * 获取实体的标识字符串，一般为标题或多个字段的组合，如学生的学号+姓名
     * @return 
     */
    public abstract String getIdentifyLabel();
    /**
     * 获取实体主键
     * @return 
     */
    public abstract Serializable getPkId();
    /**
     * 设置主键值
     * @param pkId 
     */
    public abstract void setPkId(Serializable pkId);
    
    //创建时间
    @FieldDefine(title="创建时间",group="操作信息",sortable=MBoolean.TRUE,defaultCol=MBoolean.TRUE)
    @Column(name="CREATE_TIME_")
    @XStreamConverter(value=DateConverter.class)
    protected Date createTime;
    //更新时间
    @FieldDefine(title="更新时间",group="操作信息",sortable=MBoolean.TRUE,defaultCol=MBoolean.TRUE)
    @Column(name="UPDATE_TIME_")
    @XStreamConverter(value=DateConverter.class)
    protected Date updateTime;
    
    //创建人ID或标识
    @FieldDefine(title="创建人",group="操作信息",sortable=MBoolean.TRUE,defaultCol=MBoolean.TRUE)
    @Column(name="CREATE_BY_")
    @Size(max=64)
    protected String createBy;
  
    //更新人ID或标识
    @FieldDefine(title="更新人",group="操作信息",sortable=MBoolean.TRUE,defaultCol=MBoolean.TRUE)
    @Column(name="UPDATE_BY_")
    @Size(max=64)
    protected String updateBy;

    @JsonSerialize(using=JsonDateSerializer.class)
    public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

	public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

}
