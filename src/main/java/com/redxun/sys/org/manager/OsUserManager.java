package com.redxun.sys.org.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import com.redxun.core.constants.MBoolean;
import com.redxun.core.dao.IDao;
import com.redxun.core.manager.MybatisBaseManager;
import com.redxun.core.query.IPage;
import com.redxun.core.query.QueryFilter;
import com.redxun.core.util.EncryptUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.model.IdentityInfo;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.model.StandardStatus;
import com.redxun.sys.core.entity.SysFile;
import com.redxun.sys.core.entity.SysInst;
import com.redxun.sys.core.manager.SysFileManager;
import com.redxun.sys.core.manager.SysInstManager;
import com.redxun.sys.org.dao.OsInstUsersDao;
import com.redxun.sys.org.dao.OsRelInstDao;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.*;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * <pre>
 *  
 * 描述：用户业务服务类
 * 构建组：miweb
 * 作者：keith
 * 邮箱: chshxuan@163.com
 * 日期:2014-2-1-上午12:52:41
 * 广州红迅软件有限公司（http://www.redxun.cn）
 * </pre>
 */
@Service
public class OsUserManager extends MybatisBaseManager<OsUser> {

    @Resource
    private OsRelInstDao osRelInstDao;
    @Resource
    private OsUserDao osUserDao;
    @Resource
    private OsRelTypeManager osRelTypeManager;
    @Resource
    private SysFileManager sysFileManager;
    @Resource
    private SysInstManager sysInstManager;
    @Resource
    private OsRelInstManager osRelInstManager;
    @Resource
    private OsInstUsersDao osInstUsersDao;
    @Resource
    private OsGroupManager osGroupManager;
    @Resource
    OsInstUsersManager osInstUsersManager;

    @SuppressWarnings("rawtypes")
    @Override
    protected IDao getDao() {
        return osUserDao;
    }

    public List<OsUser> getByDepIdGroupId(String depId, String groupId) {
        return osUserDao.getByDepIdGroupId(depId, groupId);
    }

    /**
     * 根据 SQL查询用户列表
     */
    public List<OsUser> getBySql(String sql) {
        return osUserDao.getBySql(sql);
    }

    /**
     * 查找某个用户组下某种关系的用户，并且可按用户编号、用户名进行过滤
     * 
     * @param groupId
     * @param relTypeId
     * @param userNo
     * @param fullname
     * @param page
     * @return
     */
    public List<OsUser> getByGroupIdRelTypeId(String groupId, String relTypeId, String userNo, String fullname,
        IPage page) {
        return osUserDao.getByGroupIdRelTypeId(groupId, relTypeId, userNo, fullname, page);
    }

    /**
     * 获得认证实例的用户Id及用户名
     * 
     * @param identityInfos
     * @return 返回数组格式如：String[]{'1,2','张三,李四'}
     */
    public String[] getUserInfoIdNames(Collection<IdentityInfo> identityInfos) {
        StringBuffer userNames = new StringBuffer();
        StringBuffer userIds = new StringBuffer();
        // 显示用户
        for (IdentityInfo info : identityInfos) {
            if (info instanceof OsUser) {
                OsUser user = (OsUser)info;
                userNames.append(user.getFullname()).append(",");
                userIds.append(user.getUserId()).append(",");
            } else if (info instanceof OsGroup) {
                OsGroup osGroup = (OsGroup)info;
                List<OsUser> osUsers = getBelongUsers(osGroup.getGroupId());
                for (OsUser user : osUsers) {
                    userNames.append(user.getFullname()).append(",");
                    userIds.append(user.getUserId()).append(",");
                }
            }
        }

        if (userNames.length() > 0) {
            userNames.deleteCharAt(userNames.length() - 1);
            userIds.deleteCharAt(userIds.length() - 1);
        }

        return new String[] {userIds.toString(), userNames.toString()};
    }

    @Override
    public void create(OsUser entity) {
        super.create(entity);
        createUserGroups(entity);
    }

    public void createUserGroups(OsUser entity) {
        String tenantId = StringUtil.isNotEmpty(entity.getEditTenantId()) ? entity.getEditTenantId()
            : ContextUtil.getCurrentTenantId();
        OsRelType osRelType = osRelTypeManager.get(OsRelType.REL_CAT_GROUP_USER_BELONG_ID);
        // 若主部门
        if (entity.getMainDep() != null) {
            OsRelInst inst = new OsRelInst();
            inst.setParty1(entity.getMainDep().getGroupId());
            inst.setParty2(entity.getUserId());
            inst.setRelTypeKey(osRelType.getKey());
            inst.setRelType(osRelType.getRelType());
            inst.setRelTypeId(osRelType.getId());
            inst.setDim1(OsDimension.DIM_ADMIN_ID);
            inst.setStatus(MBoolean.ENABLED.toString());
            inst.setInstId(IdUtil.getId());
            inst.setTenantId(tenantId);
            inst.setIsMain(MBoolean.YES.name());
            osRelInstDao.create(inst);
        }

        // 从部门
        if (entity.getCanDeps() != null) {
            for (OsGroup osGroup : entity.getCanDeps()) {
                OsRelInst inst = new OsRelInst();
                inst.setParty1(osGroup.getGroupId());
                inst.setParty2(entity.getUserId());
                inst.setRelTypeKey(osRelType.getKey());
                inst.setRelType(osRelType.getRelType());
                inst.setRelTypeId(osRelType.getId());
                inst.setDim1(OsDimension.DIM_ADMIN_ID);
                inst.setStatus(MBoolean.ENABLED.toString());
                inst.setInstId(IdUtil.getId());
                inst.setIsMain(MBoolean.NO.name());
                inst.setTenantId(tenantId);
                osRelInstDao.create(inst);
            }
        }

        // 其他用户组
        if (entity.getCanGroups() != null) {
            for (OsGroup osGroup : entity.getCanGroups()) {
                OsRelInst inst = new OsRelInst();
                inst.setParty1(osGroup.getGroupId());
                inst.setParty2(entity.getUserId());
                inst.setRelType(osRelType.getRelType());
                inst.setRelTypeKey(osRelType.getKey());
                inst.setRelTypeId(osRelType.getId());
                if (osGroup.getDimId() != null) {
                    inst.setDim1(osGroup.getDimId());
                }
                inst.setStatus(MBoolean.ENABLED.toString());
                inst.setInstId(IdUtil.getId());
                inst.setIsMain(MBoolean.NO.name());
                inst.setTenantId(tenantId);
                osRelInstDao.create(inst);
            }
        }
    }

    @Override
    public void update(OsUser entity) {
        super.update(entity);

        String tenantId = StringUtil.isNotEmpty(entity.getEditTenantId()) ? entity.getEditTenantId()
            : ContextUtil.getCurrentTenantId();
        updataToOsInstUsers(entity, tenantId);
        // 删除旧的关系
        osRelInstDao.delByParty2AndTenantId(entity.getUserId(), tenantId);
        // 创建新的关系用户
        createUserGroups(entity);
    }

    /**
     * 更新用户租户表信息
     */
    private void updataToOsInstUsers(OsUser entity, String tenantId) {
        OsInstUsers osInstUsers = osInstUsersDao.getByUserIdAndTenantId(entity.getUserId(), tenantId);
        osInstUsers.setStatus(entity.getStatus());
        osInstUsersDao.update(osInstUsers);
    }

    /**
     * 查找某个用户组下的用户
     * 
     * @param groupId
     * @param relTypeId
     * @return
     */
    public List<OsUser> getByGroupIdRelTypeId(String groupId, String relTypeId) {
        return osUserDao.getByGroupIdRelTypeId(groupId, relTypeId);
    }

    /**
     * 获得某个用户组下从属关系的用户
     * 
     * @param groupId
     * @return
     */
    public List<OsUser> getBelongUsers(String groupId) {
        return osUserDao.getByGroupIdRelTypeId(groupId, OsRelType.REL_CAT_GROUP_USER_BELONG_ID);
    }

    /**
     * 查找某个用户组下的用户,并且按条件过滤
     * 
     * @param filter
     * @return
     */
    public List<OsUser> getByGroupIdRelTypeId(QueryFilter filter) {
        return osUserDao.getByGroupIdRelTypeId(filter);
    }

    public List<OsUser> getByGroupPathRelTypeId(QueryFilter filter) {
        return osUserDao.getByGroupPathRelTypeId(filter);
    }

    @Override
    public void delete(String id) {
        logger.error("!!!delete inst, userId {}",id);
        osRelInstDao.delByParty2(id);
        // 同时删除该与该用户关联的账号
        super.delete(id);
    }

    /**
     * 初始化管理员
     */
    public OsUser initAdminUser(String instId, String userNo, String pwd, String fullname, String email, String mobile,
        Integer idSn) {

        String enPwd = EncryptUtil.encryptSha256(pwd);
        OsUser osUser = new OsUser();
        osUser.setUserId(IdUtil.getId());
        osUser.setFullname(fullname);
        osUser.setPwd(enPwd);
        osUser.setEmail(email);
        osUser.setUserNo(OsUser.TADMIN.equals(userNo) ? (Integer.toString(OsUser.IDSN + idSn)) : userNo);
        osUser.setTenantId(instId);
        osUser.setStatus(OsUser.STATUS_IN_JOB);
        osUser.setFrom(OsUser.FROM_SYS);
        osUser.setSex("Male");
        osUser.setIsAdmin(OsUser.ACC_TYPE_ADMIN);
        osUserDao.create(osUser);

        return osUser;
    }

    /**
     * 搜索用户。
     * 
     * @param filter
     * @return
     */
    public List<OsUser> getByFilter(QueryFilter filter) {
        return osUserDao.getByFilter(filter);
    }

    /**
     * 获取部门下用户。
     * 
     * @param groupId
     * @return
     */
    public List<OsUser> getByGroupId(String groupId) {
        return osUserDao.getByGroupId(groupId);
    }

    /**
     * 将用户头像暂时设置到photo里方便前端获取
     * 
     * @param user
     * @param request
     */
    public void virtualSetUserPhoto(OsUser user, HttpServletRequest request) {
        String photoId = user.getPhoto();
        String fullPath = "";
        if (StringUtils.isNotBlank(photoId)) {
            SysFile sysFile = sysFileManager.get(photoId);
            fullPath = request.getContextPath() + "/sys/core/file/download/" + sysFile.getFileId() + ".do";
        } else {
            fullPath = request.getContextPath() + "/styles/images/index/index_tap_06.png";
        }
        user.setPhoto(fullPath);
    }

    public OsUser getByEmail(String email) {
        return osUserDao.getByEmail(email);
    }

    /**
     * 判断邮件是否存在
     * 
     * @param email
     * @param pkId
     * @return
     */
    public boolean isEmailExist(String email, String pkId) {
        OsUser osUser = getByEmail(email);
        if (StringUtils.isEmpty(pkId) && osUser != null) {
            return true;
        } else if (StringUtils.isNotEmpty(pkId) && osUser != null && !(pkId.equals(osUser.getPkId()))) {
            return true;
        }

        return false;
    }

    /**
     * 根据用户ID和租户ID获取数据。
     * 
     * @param userId
     * @param tenantId
     * @return
     */
    public OsUser getByTenantId(String userId, String tenantId) {
        OsUser user = osUserDao.getByTenantId(userId, tenantId);
        return user;
    }

    /**
     * 根据用户名称获取用户对象。
     * 
     * @param userName
     * @return
     */
    public OsUser getByUserName(String userName) {
        String domain = "";
        String userNo = "";
        if (StringUtils.isEmpty(userName)) {
            return null;
        }
        String[] aryUser = userName.split("@");
        if (aryUser != null && aryUser.length == 2) {
            userNo = aryUser[0];
            domain = aryUser[1];
        } else {
            domain = WebAppUtil.getOrgMgrDomain();
            userNo = userName;
        }
        SysInst inst = sysInstManager.getByDomain(domain);
        OsUser osUser = null;
        if (inst != null) {
            osUser = osUserDao.getByUserName(userNo, inst.getTenantId());
        }
        if (osUser != null) {
            osUser.setTenantId(inst.getTenantId());
        }
        return osUser;
    }

    public OsUser getByUserName(String userNo, String tenantId) {
        OsUser user = osUserDao.getByUserName(userNo, tenantId);
        return user;
    }

    public void updPassword(String userId, String pwd) throws DecoderException {
      //  String password = EncryptUtil.encryptSha256(pwd);
        osUserDao.updPassword(userId, EncryptUtil.hexToBase64(pwd));
    }

    /**
     * 更新默认登陆机构
     */
    public void updateTenantIdFromDomain(String userId, String defaultDomain) {
        this.osUserDao.updateTenantIdFromDomain(userId, defaultDomain);
    }

    /**
     * 根据用户帐号获取用户数据。
     * 
     * @param userNo
     * @return
     */
    public List<OsUser> getByUserNo(String userNo) {
        return this.osUserDao.getByUserNo(userNo);
    }

    public String importUserTest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userImportPath = WebAppUtil.getProperty("userImportPath");
        if (StringUtils.isBlank(userImportPath)) {
            logger.error("找不到文件");
            return "数据导入失败，找不到文件！";
        }
        HSSFWorkbook hw = new HSSFWorkbook(new FileInputStream(userImportPath));
        HSSFSheet sheet = hw.getSheetAt(0);
        int rowNum = sheet.getPhysicalNumberOfRows();
        if (rowNum < 1) {
            logger.error("找不到标题行");
            return "数据导入失败，找不到标题行！";
        }

        // 解析标题部分
        HSSFRow titleRow = sheet.getRow(0);
        if (titleRow == null) {
            logger.error("找不到标题行");
            return "数据导入失败，找不到标题行！";
        }
        List<String> titleList = new ArrayList<>();
        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
            titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
        }

        if (rowNum < 2) {
            logger.info("数据行为空");
            return "数据导入完成，数据行为空！";
        }
        List<OsUser> userList = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < rowNum; rowIndex++) {
            HSSFRow row = sheet.getRow(rowIndex);
            OsUser osUser = new OsUser();
            osUser.setUserId(IdUtil.getId());
            String tenantId = ContextUtil.getCurrentTenantId();
            osUser.setTenantId(tenantId);
            osUser.setEditTenantId(tenantId);
            osUser.setFrom("ADDED");
            osUser.setSex("Male");
            osUser.setStatus("IN_JOB");
            for (int i = 0; i < titleList.size(); i++) {
                String title = titleList.get(i);
                HSSFCell cell = row.getCell(i);
                String cellValue = null;
                if (cell != null) {
                    cellValue = StringUtils.trim(cell.getStringCellValue());
                }
                switch (title) {
                    case "姓名":
                        osUser.setFullname(cellValue);
                        break;
                    case "账号":
                        osUser.setUserNo(cellValue);
                        break;
                    case "主部门ID":
                        OsGroup mainGroup = osGroupManager.get(cellValue);
                        osUser.setMainDep(mainGroup);
                        break;
                    case "职级ID":
                        List<OsGroup> canGroups = new ArrayList<OsGroup>();
                        OsGroup osGroup = osGroupManager.get(cellValue);
                        canGroups.add(osGroup);
                        osUser.setCanGroups(canGroups);
                        break;
                    case "密码":
                        osUser.setPwd(cellValue);
                        break;
                }
            }
            userList.add(osUser);
        }

        // 插入数据库
        for (OsUser osUser : userList) {
            create(osUser);
            OsInstUsers osInstUsers = new OsInstUsers();
            osInstUsers.setId(IdUtil.getId());
            osInstUsers.setApproveUser("1");
            osInstUsers.setUserId(osUser.getUserId());
            osInstUsers.setStatus(osUser.getStatus());
            osInstUsers.setIsAdmin(0);
            osInstUsers.setDomain("redxun.cn");
            osInstUsers.setTenantId(osUser.getTenantId());
            osInstUsers.setStatus(osUser.getStatus());
            osInstUsers.setApplyStatus("ENABLED");
            osInstUsers.setCreateType("CREATE");
            osInstUsersManager.create(osInstUsers);
        }
        return "成功";
    }
}