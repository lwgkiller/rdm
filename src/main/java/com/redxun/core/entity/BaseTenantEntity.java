package com.redxun.core.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import com.redxun.core.annotion.table.FieldDefine;

/**
 * 
 * <pre> 
 * 描述：带租用ID实体的对象基础类
 * 构建组：ent-base-core
 * 作者：csx
 * 邮箱:chensx@jee-soft.cn
 * 日期:2014年12月25日-上午10:02:22
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@MappedSuperclass
public abstract class BaseTenantEntity extends BaseEntity{
	private static final long serialVersionUID = 1L;
	
	//承租人ID
    //@FieldDefine(title="租用机构ID",group="操作信息")
    @Column(name="TENANT_ID_")
    @Size(max=64)
    protected String tenantId=null;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
