package com.redxun.sys.org.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.redxun.core.annotion.table.FieldDefine;
import com.redxun.core.annotion.table.TableDefine;
import com.redxun.core.constants.MBoolean;
import com.redxun.core.entity.BaseTenantEntity;
import com.redxun.org.api.model.ITenant;
import com.redxun.org.api.model.IUser;
import com.redxun.sys.core.entity.SysInst;
import com.redxun.sys.core.util.SysPropertiesUtil;

/**
 * <pre>
 * 描述：OsUser实体类定义
 * 用户信息表
 * 构建组：miweb
 * 作者：keith
 * 邮箱: chshxuan@163.com
 * 日期:2014-2-1-上午12:52:41
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@Entity
@Table(name = "OS_USER")
@TableDefine(title = "用户信息表")
public class OsUser extends BaseTenantEntity implements UserDetails, IUser {

    public final static String ACCOUNT_PRE = "UC:";

    /**
     * 系统添加=ADDED
     */
    public final static String FROM_ADDED = "ADDED";
    /**
     * 系统缺省=SYSTEM
     */
    public final static String FROM_SYS = "SYSTEM";

    /**
     * 导入=IMPORTED
     */
    public final static String FROM_IMPORTED = "IMPORTED";
    /**
     * 员工状态-在职=IN_JOB
     */
    public final static String STATUS_IN_JOB = "IN_JOB";
    /**
     * 员工状态-离职=OUT_JOB
     */
    public final static String STATUS_OUT_JOB = "OUT_JOB";
    /**
     * 员工状态-删除=DEL_JOB
     */
    public final static String STATUS_DEL_JOB = "DEL_JOB";
    /**
     * 员工扩展属性=User
     */
    public final static String ATTR_USER = "User";

    /**
     * 管理员账号=1
     */
    public final static Integer ACC_TYPE_ADMIN = 1;
    /**
     * 普通用户账号=0
     */
    public final static Integer ACC_TYPE_COMMON = 0;

    /**
     * 租户管理员账号=tadmin
     */
    public static final String TADMIN = "tadmin";

    /**
     * 租户管理员账号初始值=100000
     */
    public static final Integer IDSN = 100000;

    @FieldDefine(title = "PKID")
    @Id
    @Column(name = "USER_ID_")
    protected String userId;
    /* 姓名 */
    @FieldDefine(title = "姓名", defaultCol = MBoolean.YES)
    @Column(name = "FULLNAME_")
    @Size(max = 64)
    @NotEmpty
    protected String fullname;

    /* 姓名 */
    @FieldDefine(title = "编号", defaultCol = MBoolean.YES)
    @Column(name = "USER_NO_")
    @Size(max = 64)
    @NotEmpty
    protected String userNo;

    @FieldDefine(title = "密码", defaultCol = MBoolean.YES)
    @Column(name = "PWD_")
    @Size(max = 64)
    @NotEmpty
    protected String pwd;

    /* 姓名 */
    @FieldDefine(title = "类型", defaultCol = MBoolean.YES)
    @Column(name = "USER_TYPE_")
    @Size(max = 50)
    protected String userType;

    /* 入职时间 */
    @FieldDefine(title = "入职时间", defaultCol = MBoolean.YES)
    @Column(name = "ENTRY_TIME_")
    protected java.util.Date entryTime;
    /* 离职时间 */
    @FieldDefine(title = "离职时间", defaultCol = MBoolean.YES)
    @Column(name = "QUIT_TIME_")
    protected java.util.Date quitTime;

    @FieldDefine(title = "性别", defaultCol = MBoolean.YES)
    @Column(name = "SEX_")
    protected String sex;

    /* 生日 */
    @FieldDefine(title = "生日", defaultCol = MBoolean.YES)
    @Column(name = "BIRTHDAY_")
    protected java.util.Date birthday;

    /* 状态 */
    @FieldDefine(title = "状态", defaultCol = MBoolean.YES)
    @Column(name = "STATUS_")
    @Size(max = 20)
    @NotEmpty
    protected String status;
    /* 来源 */
    @FieldDefine(title = "来源", defaultCol = MBoolean.YES)
    @Column(name = "FROM_")
    @Size(max = 20)
    protected String from;
    /* 手机 */
    @FieldDefine(title = "手机", defaultCol = MBoolean.YES)
    @Column(name = "MOBILE_")
    @Size(max = 20)
    protected String mobile;
    /* 邮件 */
    @FieldDefine(title = "邮件", defaultCol = MBoolean.YES)
    @Column(name = "EMAIL_")
    @Size(max = 100)
    protected String email;
    /* 地址 */
    @FieldDefine(title = "地址", defaultCol = MBoolean.YES)
    @Column(name = "ADDRESS_")
    @Size(max = 255)
    protected String address;
    /* 紧急联系人 */
    @FieldDefine(title = "紧急联系人", defaultCol = MBoolean.YES)
    @Column(name = "URGENT_")
    @Size(max = 64)
    protected String urgent;
    /* 紧急联系人手机 */
    @FieldDefine(title = "紧急联系人手机", defaultCol = MBoolean.YES)
    @Column(name = "URGENT_MOBILE_")
    @Size(max = 20)
    protected String urgentMobile;
    /* QQ号 */
    @FieldDefine(title = "QQ号", defaultCol = MBoolean.YES)
    @Column(name = "QQ_")
    @Size(max = 20)
    protected String qq;
    /* 身份证号 */
    @FieldDefine(title = "身份证号", defaultCol = MBoolean.YES)
    @Column(name = "CERT_NO_")
    @Size(max = 20)
    protected String certNo;

    /* PDM账号 */
    @FieldDefine(title = "PDM账号", defaultCol = MBoolean.YES)
    @Column(name = "pdmUserNo")
    @Size(max = 50)
    protected String pdmUserNo;

    /* QMS账号 */
    @FieldDefine(title = "QMS账号", defaultCol = MBoolean.YES)
    @Column(name = "qmsUserNo")
    @Size(max = 50)
    protected String qmsUserNo;

    @FieldDefine(title = "室主任id", defaultCol = MBoolean.YES)
    @Column(name = "szrUserId")
    @Size(max = 200)
    protected String szrUserId;

    @FieldDefine(title = "室主任name", defaultCol = MBoolean.YES)
    @Column(name = "szrUserName")
    @Size(max = 200)
    protected String szrUserName;

    /* 照片 */
    @FieldDefine(title = "照片", defaultCol = MBoolean.YES)
    @Column(name = "PHOTO_")
    @Size(max = 255)
    protected String photo;

    @FieldDefine(title = "同步微信", defaultCol = MBoolean.YES)
    @Column(name = "SYNC_WX_")
    protected Integer syncWx = 0;

    @FieldDefine(title = "是否管理员", defaultCol = MBoolean.YES)
    protected Integer isAdmin = 0;

    @FieldDefine(title = "域名", defaultCol = MBoolean.YES)
    protected String domain = "";

    @FieldDefine(title = "默认登陆机构", defaultCol = MBoolean.YES)
    @Column(name = "DEFAULT_DOMAIN_")
    protected String defaultDomain = "";

    @FieldDefine(title = "用户类型", defaultCol = MBoolean.YES)
    @Column(name = "CREATE_TYPE_")
    protected String createType = "";
    @Transient
    private List<OsRelInst> upLowRelInsts;

    /**
     * 主部门Id,用于临时的数据处理
     */
    @Transient
    private String depId;

    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @FieldDefine(title = "编辑机构ID", defaultCol = MBoolean.YES)
    protected String editTenantId = "";

    public List<OsRelInst> getUpLowRelInsts() {
        return upLowRelInsts;
    }

    public void setUpLowRelInsts(List<OsRelInst> upLowRelInsts) {
        this.upLowRelInsts = upLowRelInsts;
    }

    /**
     * 主部门
     */
    @Transient
    protected OsGroup mainDep;

    @Transient
    protected OsGroup company;
    /**
     * 候选部门
     */
    @Transient
    protected List<OsGroup> canDeps;
    /**
     * 其他用户组
     */
    @Transient
    protected List<OsGroup> canGroups;
    @Transient
    protected Set<String> groupIds = new HashSet<String>();

    @Transient
    protected ITenant tenant;

    // 部门路径名称
    @Transient
    protected String depPathNames;

    @Transient
    private Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    @FieldDefine(title = "密码强弱状态", defaultCol = MBoolean.YES)
    @Column(name = "pwdStatus")
    @Size(max = 50)
    private String pwdStatus;

    public String getPwdStatus() {
        return pwdStatus;
    }

    public void setPwdStatus(String pwdStatus) {
        this.pwdStatus = pwdStatus;
    }

    /**
     * 用户的上下级关系
     */
    @Transient
    private OsRelInst upLowRelInst;

    public String getEditTenantId() {
        return editTenantId;
    }

    public void setEditTenantId(String editTenantId) {
        this.editTenantId = editTenantId;
    }

    public String getDepId() {
        return depId;
    }

    public void setDepId(String depId) {
        this.depId = depId;
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDefaultDomain() {
        return defaultDomain;
    }

    public String getCreateType() {
        return createType;
    }

    public void setCreateType(String createType) {
        this.createType = createType;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    /**
     * Default Empty Constructor for class OsUser
     */
    public OsUser() {
        super();
    }

    public OsRelInst getUpLowRelInst() {
        return upLowRelInst;
    }

    public void setUpLowRelInst(OsRelInst upLowRelInst) {
        this.upLowRelInst = upLowRelInst;
    }

    /**
     * Default Key Fields Constructor for class OsUser
     */
    public OsUser(String in_userId) {
        this.setUserId(in_userId);
    }

    /**
     * 用户ID * @return String
     */
    public String getUserId() {
        return this.userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 设置 用户ID
     */
    public void setUserId(String aValue) {
        this.userId = aValue;
    }

    /**
     * 姓名 * @return String
     */
    public String getFullname() {
        return this.fullname;
    }

    /**
     * 设置 姓名
     */
    public void setFullname(String aValue) {
        this.fullname = aValue;
    }

    /**
     * 入职时间 * @return java.util.Date
     */
    public java.util.Date getEntryTime() {
        return this.entryTime;
    }

    /**
     * 设置 入职时间
     */
    public void setEntryTime(java.util.Date aValue) {
        this.entryTime = aValue;
    }

    /**
     * 离职时间 * @return java.util.Date
     */
    public java.util.Date getQuitTime() {
        return this.quitTime;
    }

    /**
     * 设置 离职时间
     */
    public void setQuitTime(java.util.Date aValue) {
        this.quitTime = aValue;
    }

    /**
     * 状态 * @return String
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 设置 状态
     */
    public void setStatus(String aValue) {
        this.status = aValue;
    }

    /**
     * 来源 * @return String
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * 设置 来源
     */
    public void setFrom(String aValue) {
        this.from = aValue;
    }

    /**
     * 手机 * @return String
     */
    @Override
    public String getMobile() {
        return this.mobile;
    }

    /**
     * 设置 手机
     */
    public void setMobile(String aValue) {
        this.mobile = aValue;
    }

    /**
     * 邮件 * @return String
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * 设置 邮件
     */
    public void setEmail(String aValue) {
        this.email = aValue;
    }

    /**
     * 地址 * @return String
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * 设置 地址
     */
    public void setAddress(String aValue) {
        this.address = aValue;
    }

    /**
     * 紧急联系人 * @return String
     */
    public String getUrgent() {
        return this.urgent;
    }

    /**
     * 设置 紧急联系人
     */
    public void setUrgent(String aValue) {
        this.urgent = aValue;
    }

    /**
     * 紧急联系人手机 * @return String
     */
    public String getUrgentMobile() {
        return this.urgentMobile;
    }

    /**
     * 设置 紧急联系人手机
     */
    public void setUrgentMobile(String aValue) {
        this.urgentMobile = aValue;
    }

    /**
     * QQ号 * @return String
     */
    public String getQq() {
        return this.qq;
    }

    /**
     * 设置 QQ号
     */
    public void setQq(String aValue) {
        this.qq = aValue;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getPdmUserNo() {
        return pdmUserNo;
    }

    public void setPdmUserNo(String pdmUserNo) {
        this.pdmUserNo = pdmUserNo;
    }

    public String getQmsUserNo() {
        return qmsUserNo;
    }

    public void setQmsUserNo(String qmsUserNo) {
        this.qmsUserNo = qmsUserNo;
    }

    /**
     * 照片 * @return String
     */
    public String getPhoto() {
        return this.photo;
    }

    /**
     * 设置 照片
     */
    public void setPhoto(String aValue) {
        this.photo = aValue;
    }

    @Override
    public String getIdentifyLabel() {
        return this.getFullname();
    }

    @Override
    public Serializable getPkId() {
        return this.userId;
    }

    @Override
    public void setPkId(Serializable pkId) {
        this.userId = (String)pkId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public java.util.Date getBirthday() {
        return birthday;
    }

    public void setBirthday(java.util.Date birthday) {
        this.birthday = birthday;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public OsGroup getMainDep() {
        return mainDep;
    }

    public void setMainDep(OsGroup mainDep) {
        this.mainDep = mainDep;
    }

    public List<OsGroup> getCanDeps() {
        return canDeps;
    }

    public void setCanDeps(List<OsGroup> canDeps) {
        this.canDeps = canDeps;
    }

    public List<OsGroup> getCanGroups() {
        return canGroups;
    }

    public void setCanGroups(List<OsGroup> canGroups) {
        this.canGroups = canGroups;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof OsUser)) {
            return false;
        }
        OsUser rhs = (OsUser)object;
        return new EqualsBuilder().append(this.userId, rhs.userId).append(this.tenantId, rhs.tenantId).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(-82280557, -700257973).append(this.userId).append(this.fullname)
            .append(this.entryTime).append(this.quitTime).append(this.status).append(this.from).append(this.mobile)
            .append(this.email).append(this.address).append(this.urgent).append(this.urgentMobile).append(this.qq)
            .append(this.photo).append(this.createBy).append(this.createTime).append(this.tenantId)
            .append(this.updateBy).append(this.updateTime).toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append("userId", this.userId).append("fullname", this.fullname)
            .append("entryTime", this.entryTime).append("quitTime", this.quitTime).append("status", this.status)
            .append("from", this.from).append("mobile", this.mobile).append("email", this.email)
            .append("address", this.address).append("urgent", this.urgent).append("urgentMobile", this.urgentMobile)
            .append("qq", this.qq).append("photo", this.photo).append("createBy", this.createBy)
            .append("createTime", this.createTime).append("tenantId", this.tenantId).append("updateBy", this.updateBy)
            .append("updateTime", this.updateTime).toString();
    }

    @Override
    public String getIdentityType() {
        return IDENTIFY_TYPE_USER;
    }

    @Override
    public String getIdentityName() {
        return fullname;
    }

    @Override
    public String getIdentityId() {
        return this.getUserId();
    }

    @Override
    public ITenant getTenant() {
        if (this.tenant != null) {
            return this.tenant;
        }
        return new SysInst();
    }

    @Override
    public void setTenant(ITenant tenant) {
        this.tenant = tenant;
    }

    @Override
    public String getUsername() {
        return this.getUserNo() + "@" + this.getDomain();
    }

    @Override
    public void setUsername(String userName) {}

    @Override
    @Transient
    public String getPwd() {
        return this.pwd;
    }

    @Override
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public boolean isSuperAdmin() {

        String adminAccount = SysPropertiesUtil.getAdminAccount();
        if (this.userNo.indexOf("@") == -1) {
            return userNo.equalsIgnoreCase(adminAccount) || ACC_TYPE_ADMIN == getIsAdmin();
        } else {
            int idx = userNo.indexOf("@");
            return userNo.substring(0, idx).equalsIgnoreCase(adminAccount) || ACC_TYPE_ADMIN == getIsAdmin();
        }
    }

    public static void main(String[] args) {
        String account = "admin@abc.com";
        System.out.println(account.substring(0, account.indexOf("@")));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return this.pwd;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "IN_JOB".equals(this.status);
    }

    @Override
    public String getMainGroupId() {
        if (mainDep != null) {
            return mainDep.getGroupId();
        }
        return null;
    }

    public int getSyncWx() {
        return syncWx;
    }

    public void setSyncWx(int syncWx) {
        this.syncWx = syncWx;
    }

    @Override
    public Set<String> getGroupIds() {
        return groupIds;
    }

    public OsGroup getCompany() {
        return company;
    }

    public void setCompany(OsGroup company) {
        this.company = company;
    }

    @Override
    public String getCompanyId() {
        if (company != null)
            return company.getGroupId();
        return null;
    }

    @Override
    public String getCompanyName() {
        if (company != null)
            return company.getName();
        return null;
    }

    @Override
    public String getMainGroupName() {
        if (mainDep != null) {
            return mainDep.getName();
        }
        return null;
    }

    public String getDepPathNames() {
        return depPathNames;
    }

    public void setDepPathNames(String depPathNames) {
        this.depPathNames = depPathNames;
    }

    public Integer getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Integer isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getSzrUserId() {
        return szrUserId;
    }

    public void setSzrUserId(String szrUserId) {
        this.szrUserId = szrUserId;
    }

    public String getSzrUserName() {
        return szrUserName;
    }

    public void setSzrUserName(String szrUserName) {
        this.szrUserName = szrUserName;
    }

    @Override
    public String getUserUpLowPath() {
        if (upLowRelInst == null) {
            return null;
        }
        return upLowRelInst.getPath();
    }

    @Override
    public boolean isSaaSAdmin() {
        return OsUser.ACC_TYPE_ADMIN == getIsAdmin() ? true : false;
    }
}
