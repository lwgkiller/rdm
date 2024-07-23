package com.redxun.rdmCommon.core.manager;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.MaintainabilityEvaluationFormDao;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.zlgjNPI.core.dao.ProductManageDao;

import groovy.util.logging.Slf4j;

/**
 * @author zhangzhen
 */
@Service
@Slf4j
public class CommonInfoManager {
    private static Logger logger = LoggerFactory.getLogger(CommonInfoManager.class);
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private MaintainabilityEvaluationFormDao maintainabilityEvaluationFormDao;
    @Resource
    private ProductManageDao productManageDao;
    @Autowired
    private OsGroupManager osGroupManager;
    @Resource
    private OsGroupDao osGroupDao;

    public List<Map<String, Object>> getDicValues(String dicType) {
        Map<String, Object> params = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            params.put("key", dicType);
            list = commonInfoDao.getDicValues(params);
        } catch (Exception e) {
            logger.error("Exception in getDicValues", e);
        }
        return list;
    }

    /**
     * 产品技术管控需求下拉框
     *
     * @param dicType
     * @return
     */
    public List<Map<String, Object>> getMessage(String dicType, String nodeName) {
        Map<String, Object> params = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            params.put("key", dicType);
            params.put("nodeName", nodeName);
            list = productManageDao.getProductExploitRelation(params);
        } catch (Exception e) {
            logger.error("Exception in getDicValues", e);
        }
        return list;
    }

    /**
     * 将字典项转化为map对象
     */
    public Map<String, Object> genMap(String dicType) {
        List<Map<String, Object>> list = getDicValues(dicType);
        Map<String, Object> resultMap = new HashMap<>(16);
        for (Map<String, Object> map : list) {
            resultMap.put(map.get("key_").toString(), map.get("text"));
        }
        return resultMap;
    }

    public void getUserCert2Id(Map<String, String> userCertNo2UserId) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userIdAndCertNoList = commonInfoDao.getUserIdAndCerNo();
        if (userIdAndCertNoList != null && !userIdAndCertNoList.isEmpty()) {
            for (Map<String, String> oneMap : userIdAndCertNoList) {
                if (StringUtils.isNotBlank(oneMap.get("USER_ID_")) && StringUtils.isNotBlank(oneMap.get("CERT_NO_"))) {
                    userCertNo2UserId.put(oneMap.get("CERT_NO_"), oneMap.get("USER_ID_"));
                }
            }
        }
    }

    public List<JSONObject> getUserIdByUserName(String userName) {
        Map<String, String> deptMap = queryDeptUnderJSZX();
        List<String> idList = new ArrayList<>(deptMap.keySet());
        Map<String, Object> params = new HashMap<>(16);
        params.put("ids", idList);
        params.put("userName", userName);
        List<JSONObject> list = commonInfoDao.getUserInfoByUserName(params);
        return list;
    }

    /**
     * 获取人员最高权限
     */
    public JSONObject hasPermission(String roleKey) {
        String userID = ContextUtil.getCurrentUserId();
        String userDeptId = ContextUtil.getCurrentUser().getMainGroupId();
        JSONObject resultJson = new JSONObject();
        resultJson.put("isLeader", false);
        resultJson.put(roleKey, false);
        resultJson.put("FGLD", false);
        resultJson.put("FGZR", false);
        resultJson.put("HX-GLY", false);
        resultJson.put(RdmConst.ALLDATA_QUERY_KEY, false);
        // 获取用户所属组织
        List<JSONObject> resultList = commonInfoDao.getUserOrg(userID);
        for (JSONObject temp : resultList) {
            //组与用户的关系是否为LEADER且部门编号对应则是领导
            if ("GROUP-USER-LEADER".equals(temp.getString("REL_TYPE_KEY_"))
                && userDeptId.equalsIgnoreCase(temp.getString("PARTY1_"))) {
                resultJson.put("isLeader", true);
            }
            //判断是否为分管领导
            if ("FGLD".equals(temp.getString("KEY_"))) {
                resultJson.put("FGLD", true);
            }
            //所有数据访问权限
            if (RdmConst.ALLDATA_QUERY_KEY.equals(temp.getString("KEY_"))) {
                resultJson.put(RdmConst.ALLDATA_QUERY_KEY, true);
            }
            //根据os_rel_inst中REL_TYPE_KEY_字段判断是否为分管主任
            if ("GROUP-USER-DIRECTOR".equals(temp.getString("REL_TYPE_KEY_"))) {
                resultJson.put("FGZR", true);
            }
            //画像-管理员
            if ("HX-GLY".equals(temp.getString("KEY_"))) {
                resultJson.put("HX-GLY", true);
            }
            //月度工作-计划调度员
            if (roleKey.equals(temp.getString("KEY_"))) {
                resultJson.put(roleKey, true);
            }
        }
        return resultJson;
    }

    public List<String> getGroupIds(JSONObject jsonObject) {
        List<JSONObject> list = commonInfoDao.getBelongGroups(jsonObject);
        StringBuffer groupIds = new StringBuffer();
        List<String> idList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            idList.add(map.get("PARTY1_").toString());
        }
        return idList;
    }

    // 根据部门id获取是否是挖掘机械研究院下的人员
    public Boolean judgeIsTechDept(String deptId) {
        Map<String, String> deptId2Name = queryDeptUnderJSZX();
        boolean isJSZX = false;
        if (deptId2Name != null && deptId2Name.containsKey(deptId)) {
            isJSZX = true;
        }
        return isJSZX;
    }

    // 查询挖掘机械研究院下所有的部门(只返回等级是“部门”的组)
    public Map<String, String> queryDeptUnderJSZX() {
        List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(RdmConst.JSZX_NAME);
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        String jszxId = list.get(0).getString("GROUP_ID_");
        List<JSONObject> deptInfos = commonInfoDao.queryDeptUnderJSZX(jszxId);
        Map<String, String> deptId2Name = new HashMap<>();
        for (JSONObject oneDept : deptInfos) {
            deptId2Name.put(oneDept.getString("GROUP_ID_"), oneDept.getString("NAME_"));
        }
        return deptId2Name;
    }

    // 封装，获取挖掘机械研究院下所有的部门负责人
    public List<Map<String, String>> queryJszxAllSuoZhang() {
        Map<String, Object> params = new HashMap<>();
        List<JSONObject> list = commonInfoDao.getDeptInfoByDeptName(RdmConst.JSZX_NAME);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        String jszxId = list.get(0).getString("GROUP_ID_");
        params.put("jszxId", jszxId);
        List<Map<String, String>> jszxDeptResps = maintainabilityEvaluationFormDao.getAllSuozhang(params);
        return jszxDeptResps;
    }

    public boolean judgeUserIsPointRole(String roleName, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        boolean result = false;
        for (int i = 0; i < currentUserRoles.size(); i++) {
            Map<String, Object> map = currentUserRoles.get(i);
            if (roleName.equals(map.get("NAME_"))) {
                result = true;
                break;
            }
        }
        return result;
    }

    // 文件的下载或者是pdf文件的预览
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId,
        String fileBasePath, String tableName) {
        try {
            if (StringUtils.isBlank(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            if (StringUtils.isBlank(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + (StringUtils.isBlank(tableName) ? "" : (File.separator + tableName))
                + (StringUtils.isBlank(formId) ? "" : (File.separator + formId)) + File.separator + fileId + "."
                + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    public void officeFilePreview(String fileName, String fileId, String formId, String fileBasePath,
        HttpServletResponse response, String tableName) {
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtils.isBlank(tableName) ? "" : (File.separator + tableName))
            + (StringUtils.isBlank(formId) ? "" : (File.separator + formId)) + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + (StringUtils.isBlank(tableName) ? "" : (File.separator + tableName))
            + (StringUtils.isBlank(formId) ? "" : (File.separator + formId)) + File.separator + convertPdfDir
            + File.separator + fileId + ".pdf";;
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    public void imageFilePreview(String fileName, String fileId, String formId, String fileBasePath,
        HttpServletResponse response, String tableName) {
        if (StringUtils.isBlank(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtils.isBlank(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtils.isBlank(tableName) ? "" : (File.separator + tableName))
            + (StringUtils.isBlank(formId) ? "" : (File.separator + formId)) + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    public static void deleteFileOnDisk(String mainId, String fileId, String suffix, String fileUrl) {
        try {
            String fullFileName = fileId + "." + suffix;
            StringBuilder fileBasePath = new StringBuilder(SysPropertiesUtil.getGlobalProperty(fileUrl));
            String fullPath = fileBasePath.append(File.separator).append(mainId).append(File.separator)
                .append(fullFileName).toString();
            File file = new File(fullPath);
            if (file.exists()) {
                file.delete();
            }
            // 删除目录
            String filePathDir = SysPropertiesUtil.getGlobalProperty(fileUrl) + File.separator + mainId;
            File pathFile = new File(filePathDir);
            pathFile.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteDirFromDisk", e);
        }
    }

    /**
     * 判断用户是否是指定部门的负责人
     *
     * @param userId
     * @return
     */
    public boolean judgeUserIsDeptRespor(String userId, String deptName) {
        List<Map<String, String>> userInfos = queryDeptRespUser(deptName);
        for (Map<String, String> userInfo : userInfos) {
            if (userId.equalsIgnoreCase(userInfo.get("PARTY2_"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询部门负责人信息
     *
     * @return
     */
    public List<Map<String, String>> queryDeptRespUser(String deptName) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", deptName);
        List<Map<String, String>> depRespMans = xcmgProjectOtherDao.getDepRespManById(params);
        return depRespMans;
    }

    // 根据groupName和REL_TYPE_KEY_查询USER_ID_和FULLNAME_
    public List<Map<String, String>> queryUserByGroupNameAndRelType(String groupName, String relTypeKey) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", groupName);
        params.put("REL_TYPE_KEY_", relTypeKey);
        List<Map<String, String>> depRespMans = commonInfoDao.queryUserByGroupName(params);
        return depRespMans;
    }

    public JSONObject queryDeptNameById(String deptId) {
        Map<String, Object> param = new HashMap<>();
        param.put("deptId", deptId);
        JSONObject result = commonInfoDao.queryDept(param);
        if (result == null) {
            result = new JSONObject();
        }
        return result;
    }

    public JSONObject queryDeptByUserId(String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        JSONObject deptInfo = commonInfoDao.queryDeptByUserId(param);
        return deptInfo;
    }

    public List<JSONObject> batchDeptByUserId(Set<String> userIds) {
        Map<String, Object> param = new HashMap<>();
        param.put("userIds", userIds);
        List<JSONObject> deptInfo = commonInfoDao.batchDeptByUserId(param);
        return deptInfo;
    }

    // ..添加自己最近的记录，重复的不添加，已添加的保留X条
    public void addMyMenuRecent(String menuIdSysId) {
        try {
            String menuId = menuIdSysId.split("_")[0];
            String sysId = menuIdSysId.split("_")[1];
            JSONObject params = new JSONObject();
            params.put("clickUserId", ContextUtil.getCurrentUserId());
            params.put("menuId", menuId);
            List<JSONObject> myMenuRecentList = commonInfoDao.getMyMenuRecent(params);
            // 如果我的近期记录里没有某菜单，就添加
            if (myMenuRecentList.isEmpty()) {
                JSONObject menuRecent = new JSONObject();
                menuRecent.put("id", IdUtil.getId());
                menuRecent.put("menuId", menuId);
                menuRecent.put("sysId", sysId);
                menuRecent.put("sysId", sysId);
                menuRecent.put("clickUserId", ContextUtil.getCurrentUserId());
                menuRecent.put("CREATE_TIME_", new Date());
                menuRecent.put("UPDATE_TIME_", new Date());
                commonInfoDao.insertMyMenuRecent(menuRecent);
            } else {
                myMenuRecentList.get(0).put("UPDATE_TIME_", new Date());
                commonInfoDao.updateMyMenuRecent(myMenuRecentList.get(0));
            }
            // 处理菜单，保留近期访问的X条记录
            params.put("howManyRecent", 10);
            commonInfoDao.processMyMenuRecent(params);
        } catch (Exception e) {
            logger.error("添加自己最近的记录失败，");
        }
    }

    // ..找自己的访问记录
    public List<JSONObject> getMyMenuRecent(JSONObject params) {
        return commonInfoDao.getMyMenuRecent(params);
    }

    // ..根据groupKey，维度，级别，用户id，机构id判断某用户是否属于指定的group
    public boolean judgeUserIsPointGroup(String groupKey, String dimId, String rankLevel, String userId,
        String tenantId) {
        List<OsGroup> groups = osGroupManager.getByDimAndLevelBelongs(dimId, rankLevel, userId, tenantId);
        if (groups != null && !groups.isEmpty()) {
            boolean isok = false;
            for (OsGroup group : groups) {
                if (group.getKey().equalsIgnoreCase(groupKey)) {
                    isok = true;
                    break;
                }
            }
            return isok;
        } else {
            return false;
        }
    }

    /**
     * 查询某个部门是否是另一个部门的子部门
     * 
     * @param sonDeptId，查询的子部门
     * @param parentDeptName，查询的父部门
     * @return
     */
    public boolean queryWhetherUnderDept(String sonDeptId, String parentDeptName) {
        if (StringUtils.isBlank(sonDeptId) || StringUtils.isBlank(parentDeptName)) {
            return false;
        }
        OsGroup sonGroup = osGroupDao.get(sonDeptId);
        if (sonGroup == null) {
            return false;
        }
        List<OsGroup> parentGroupList = osGroupDao.getByDepName(parentDeptName);
        if (parentGroupList == null || parentGroupList.isEmpty() || parentGroupList.size() > 1) {
            return false;
        }
        String parentGroupId = parentGroupList.get(0).getGroupId();
        String path = sonGroup.getPath();
        // 判断son的path中是否包含parentId
        if (path.contains(parentGroupId)) {
            return true;
        }
        return false;
    }
}
