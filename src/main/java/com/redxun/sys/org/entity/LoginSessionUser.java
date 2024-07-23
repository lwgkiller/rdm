package com.redxun.sys.org.entity;

import com.redxun.core.annotion.table.FieldDefine;
import com.redxun.core.annotion.table.TableDefine;
import com.redxun.core.entity.BaseTenantEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * <pre>
 *  
 * 描述：LOGIN_SESSION_USER实体类定义
 * 作者：szw
 * 邮箱: ray@redxun.com
 * 日期:2019-06-11 11:30:37
 * 版权：广州红迅软件
 * </pre>
 */
@TableDefine(title = "LOGIN_SESSION_USER")
public class LoginSessionUser extends BaseTenantEntity {
	public static final String USERNO_MAP_SESSION="USERNO_MAP_SESSION";

	public static final String USERNO_LOGIN="USERNO_LOGIN";

	public static final String LOGIN_TYPE="LOGIN";

	public static final String OULOGIN_TYPE="OULOGIN";

	public static final int OULOGIN_NUM=0;

	@FieldDefine(title = "主键")
	@Id
	@Column(name = "ID_")
	protected String id;

	@FieldDefine(title = "用户ID")
	@Column(name = "USER_ID_")
	protected String userId; 
	@FieldDefine(title = "登陆SESSION")
	@Column(name = "SESSION_ID_")
	protected String sessionId; 
	@FieldDefine(title = "客户端ip地址")
	@Column(name = "SESSION_IP_")
	protected String sessionIp; 
	@FieldDefine(title = "客户登陆时间")
	@Column(name = "LOGIN_FIRST_TIME")
	protected String loginFirstTime; 
	@FieldDefine(title = "创建人ID")
	@Column(name = "CREATE_BY_")
	protected String createBy;
	@FieldDefine(title = "session状态：LOGIN(在线)、OULOGIN(下线)")
	@Column(name = "SESSION_TYPE_")
	protected String sessionType;
	@FieldDefine(title = "客户登出时间")
	@Column(name = "OULOGIN_FIRST_TIME_")
	protected String ouLoginFirstTime;

	protected String userNo;
	protected String fullname;
	protected String orgGroup;
	protected String userOrgGroup;


	protected String controlType="并发模式";
	protected int controlNum;
	protected int occupyNum;
	protected int remainderNum;
	protected String occupancyRate;

	public LoginSessionUser() {
	}

	public String getControlType() {
		return controlType;
	}

	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	public int getControlNum() {
		return controlNum;
	}

	public void setControlNum(int controlNum) {
		this.controlNum = controlNum;
	}

	public int getOccupyNum() {
		return occupyNum;
	}

	public void setOccupyNum(int occupyNum) {
		this.occupyNum = occupyNum;
	}

	public int getRemainderNum() {
		return remainderNum;
	}

	public void setRemainderNum(int remainderNum) {
		this.remainderNum = remainderNum;
	}

	public String getOccupancyRate() {
		return occupancyRate;
	}

	public void setOccupancyRate(String occupancyRate) {
		this.occupancyRate = occupancyRate;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getOrgGroup() {
		return orgGroup;
	}

	public void setOrgGroup(String orgGroup) {
		this.orgGroup = orgGroup;
	}

	public String getUserOrgGroup() {
		return userOrgGroup;
	}

	public void setUserOrgGroup(String userOrgGroup) {
		this.userOrgGroup = userOrgGroup;
	}

	public String getOuLoginFirstTime() {
		return ouLoginFirstTime;
	}

	public void setOuLoginFirstTime(String ouLoginFirstTime) {
		this.ouLoginFirstTime = ouLoginFirstTime;
	}

	/**
	 * Default Key Fields Constructor for class Orders
	 */
	public LoginSessionUser(String in_id) {
		this.setPkId(in_id);
	}
	
	@Override
	public String getIdentifyLabel() {
		return this.id;
	}

	@Override
	public Serializable getPkId() {
		return this.id;
	}

	@Override
	public void setPkId(Serializable pkId) {
		this.id = (String) pkId;
	}
	
	public String getId() {
		return this.id;
	}

	
	public void setId(String aValue) {
		this.id = aValue;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * 返回 用户ID
	 * @return
	 */
	public String getUserId() {
		return this.userId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * 返回 登陆SESSION
	 * @return
	 */
	public String getSessionId() {
		return this.sessionId;
	}
	public void setSessionIp(String sessionIp) {
		this.sessionIp = sessionIp;
	}
	
	/**
	 * 返回 客户端ip地址
	 * @return
	 */
	public String getSessionIp() {
		return this.sessionIp;
	}
	public void setLoginFirstTime(String loginFirstTime) {
		this.loginFirstTime = loginFirstTime;
	}
	
	/**
	 * 返回 客户登陆时间
	 * @return
	 */
	public String getLoginFirstTime() {
		return this.loginFirstTime;
	}
	public void setCreatedBy(String createdBy) {
		this.createBy = createdBy;
	}
	
	/**
	 * 返回 创建人ID
	 * @return
	 */
	public String getCreatedBy() {
		return this.createBy;
	}
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	
	/**
	 * 返回 session状态：LOGIN(在线)、OULOGIN(下线)
	 * @return
	 */
	public String getSessionType() {
		return this.sessionType;
	}
	
	
	
	
		

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof LoginSessionUser)) {
			return false;
		}
		LoginSessionUser rhs = (LoginSessionUser) object;
		return new EqualsBuilder()
		.append(this.id, rhs.id)
		.append(this.userId, rhs.userId)
		.append(this.sessionId, rhs.sessionId)
		.append(this.sessionIp, rhs.sessionIp)
		.append(this.loginFirstTime, rhs.loginFirstTime)
		.append(this.createBy, rhs.createBy)
		.append(this.sessionType, rhs.sessionType)
		.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-82280557, -700257973)
		.append(this.id)
		.append(this.userId)
		.append(this.sessionId)
		.append(this.sessionIp)
		.append(this.loginFirstTime)
		.append(this.createBy)
		.append(this.sessionType)
		.toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this)
		.append("id", this.id) 
				.append("userId", this.userId) 
				.append("sessionId", this.sessionId) 
				.append("sessionIp", this.sessionIp) 
				.append("loginFirstTime", this.loginFirstTime) 
				.append("createdBy", this.createBy)
										.append("sessionType", this.sessionType) 
		.toString();
	}

}



