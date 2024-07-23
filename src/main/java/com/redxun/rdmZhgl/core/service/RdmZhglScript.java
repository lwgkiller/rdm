package com.redxun.rdmZhgl.core.service;

import java.util.*;

import javax.annotation.Resource;

import com.redxun.core.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.*;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.sys.core.dao.SubsystemDao;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.org.dao.OsGroupDao;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Service
public class RdmZhglScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(RdmZhglScript.class);

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private ProductDao productDao;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private OsUserDao osUserDao;
    @Resource
    private SubsystemDao subsystemDao;
    @Resource
    private CcbgDao ccbgDao;
    @Resource
    private NdkfjhPlanDetailDao ndkfjhPlanDetailDao;
    @Resource
    private YfjbBaseInfoDao yfjbBaseInfoDao;
    @Autowired
    private RdmZhglDao rdmZhglDao;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private KjlwService kjlwService;


    // 获取流程中使用的处理人为部门负责人的信息
    public Collection<TaskExecutor> getDepRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyUserId = formDataJson.getString("CREATE_BY_");
        if (StringUtils.isBlank(applyUserId)) {
            applyUserId = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(applyUserId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    // 根据部门id获取部门负责人的信息
    public Collection<TaskExecutor> getDeptRespByDeptId() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String deptId = formDataJson.getString("zbbmId");
        if (StringUtils.isBlank(deptId)) {
            deptId = ContextUtil.getCurrentUser().getMainGroupId();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，技术管理部负责人
     */
    public Collection<TaskExecutor> getJsglbRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryJsglbRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，智能控制研究所负责人
     */
    public Collection<TaskExecutor> getZksRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryZksRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，智能控制研究所分管主任
     */
    public Collection<TaskExecutor> getZksFgzr() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("deptName", "智能控制研究所");
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIRECTOR");
        List<JSONObject> fgzrList = rdmZhglDao.queryUserByDeptNameAndRel(params);
        if (fgzrList != null && !fgzrList.isEmpty()) {
            for (JSONObject fgzr : fgzrList) {
                users.add(new TaskExecutor(fgzr.getString("USER_ID_"), fgzr.getString("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，营销公司市场部负责人
     */
    public Collection<TaskExecutor> getScbRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryScbRespUser();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，分管领导
     *
     * @return
     */
    public Collection<TaskExecutor> fgldUserInfos() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> leaderInfos = rdmZhglUtil.queryFgld();
        if (leaderInfos != null && !leaderInfos.isEmpty()) {
            for (Map<String, String> oneLeader : leaderInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 获取新品项目负责人
    public Collection<TaskExecutor> getXpszResponse() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainIds");
        JSONObject jsonObject = productDao.getObjectById(mainId);
        if (StringUtils.isBlank(jsonObject.getString("responseMan"))) {
            return users;
        }
        users.add(new TaskExecutor(jsonObject.getString("responseMan"), jsonObject.getString("responseManName")));
        return users;
    }

    // 获取新品项目管理员
    public Collection<TaskExecutor> getXpszAdmin() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainId");
        JSONObject jsonObject = productDao.getObjectById(mainId);
        if (StringUtils.isBlank(jsonObject.getString("planAdmin"))) {
            return users;
        }
        users.add(new TaskExecutor(jsonObject.getString("planAdmin"), jsonObject.getString("planAdminName")));
        return users;
    }

    /**
     * @tty 查询标准制修订牵头人 2021/3/26 脚本中只是用来查用户的id和用户名称name
     * @return
     */
    public Collection<TaskExecutor> getBzzxdResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String standardLeaderId = formDataJson.getString("standardLeaderId");
        String standardLeaderName = formDataJson.getString("standardLeaderName");
        Map<String, Object> param = new HashMap<>();

        param.put("standardLeaderId", standardLeaderId);// 标准牵头人ID
        param.put("standardLeaderName", standardLeaderName);// 标准牵头人name

        // JSONObject user=standardCreateDao.queryUserById(param);
        users.add(new TaskExecutor(standardLeaderId, standardLeaderName));
        return users;
    }

    /**
     * @tty 征求意见人员(征求意见人可以是多个)
     * @return
     */
    public Collection<TaskExecutor> getZqyjResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String yjUserIds = formDataJson.getString("yjUserIds");
        String yjUserNames = formDataJson.getString("yjUserNames");
        Map<String, Object> param = new HashMap<>();

        // 征求意见人可能是多个
        param.put("yjUserIds", yjUserIds);// 征求意见人员ID
        param.put("yjUserNames", yjUserNames);// 征求意见人员name

        // JSONObject user=standardCreateDao.queryYjUserIds(param);
        users.add(new TaskExecutor(yjUserIds, yjUserNames));
        return users;
    }

    /**
     * 标准对接人
     *
     * @return
     */
    public Collection<TaskExecutor> getDjrResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String standardPeopleId = formDataJson.getString("standardPeopleId");
        String standardPeopleName = formDataJson.getString("standardPeopleName");

        Map<String, Object> param = new HashMap<>();
        param.put("standardPeopleId", standardPeopleId);// 标准对接人ID
        param.put("standardPeopleName", standardPeopleName);// 标准对接人name

        // JSONObject user=standardCreateDao.queryPeopleById(param);
        users.add(new TaskExecutor(standardPeopleId, standardPeopleName));
        return users;
    }

    /**
     * 技术室主任
     *
     * @return
     */
    public Collection<TaskExecutor> getJsszr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String productSupervisorId = formDataJson.getString("productSupervisorId");
        String productSupervisorName = formDataJson.getString("productSupervisorName");

        Map<String, Object> param = new HashMap<>();
        param.put("productSupervisorId", productSupervisorId);// 技术室主任ID
        param.put("productSupervisorName", productSupervisorName);// 技术室主任name

        // JSONObject user=standardCreateDao.queryPeopleById(param);
        users.add(new TaskExecutor(productSupervisorId, productSupervisorName));
        return users;
    }

    /**
     * 工艺整机参数人员
     *
     * @return
     */
    public Collection<TaskExecutor> getGyzjcsry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String tractorParameterId = formDataJson.getString("tractorParameterId");
        String tractorParameterName = formDataJson.getString("tractorParameterName");

        Map<String, Object> param = new HashMap<>();
        param.put("tractorParameterId", tractorParameterId);// 工艺整机参数人员ID
        param.put("tractorParameterName", tractorParameterName);// 工艺整机参数人员name

        users.add(new TaskExecutor(tractorParameterId, tractorParameterName));
        return users;
    }

    /**
     * 工艺零部件参数人员
     *
     * @return
     */
    public Collection<TaskExecutor> getGylbjcsry() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String craftAssemblyId = formDataJson.getString("craftAssemblyId");
        String craftAssemblyName = formDataJson.getString("craftAssemblyName");

        Map<String, Object> param = new HashMap<>();
        param.put("craftAssemblyId", craftAssemblyId);// 工艺零部件参数人员ID
        param.put("craftAssemblyName", craftAssemblyName);// 工艺零部件参数人员name

        users.add(new TaskExecutor(craftAssemblyId, craftAssemblyName));
        return users;
    }

    /**
     * 零部件技术参数人员
     *
     * @return
     */
    public Collection<TaskExecutor> getLbjjscs() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String partsTechnologyId = formDataJson.getString("partsTechnologyId");
        String partsTechnologyName = formDataJson.getString("partsTechnologyName");

        Map<String, Object> param = new HashMap<>();
        param.put("partsTechnologyId", partsTechnologyId);// 零部件技术参数人员ID
        param.put("partsTechnologyName", partsTechnologyName);// 零部件技术参数人员name

        users.add(new TaskExecutor(partsTechnologyId, partsTechnologyName));
        return users;
    }

    /**
     * 流程中，质量保证部负责人
     */
    public Collection<TaskExecutor> getZlbzbfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryZlbzbfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，查询工艺部门负责人
     */
    public Collection<TaskExecutor> getGybmfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryGybmfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 流程中，查询数字化部负责人
     */
    public Collection<TaskExecutor> queryXXHGLBfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        List<Map<String, String>> depRespMans = rdmZhglUtil.queryXXHGLBfzr();
        if (depRespMans != null && !depRespMans.isEmpty()) {
            for (Map<String, String> depRespMan : depRespMans) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 标准审查人员(标准审查人可以是多个)
     */
    public Collection<TaskExecutor> getBzscUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String bzscIds = formDataJson.getString("bzscIds");
        String bzscNmaes = formDataJson.getString("bzscNmaes");
        if (StringUtils.isNotBlank(bzscIds) && StringUtils.isNotBlank(bzscNmaes)) {
            List<String> bzscIdsList = Arrays.asList(bzscIds.split(",", -1));
            List<String> bzscNmaesList = Arrays.asList(bzscNmaes.split(",", -1));
            for (int index = 0; index < bzscIdsList.size(); index++) {
                users.add(new TaskExecutor(bzscIdsList.get(index), bzscNmaesList.get(index)));
            }
        }
        return users;
    }

    /**
     * 实施确认人员(实施确认人可以是多个)
     */
    public Collection<TaskExecutor> getSsqrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ssqrIds = formDataJson.getString("ssqrIds");
        String ssqrNames = formDataJson.getString("ssqrNames");
        if (StringUtils.isNotBlank(ssqrIds) && StringUtils.isNotBlank(ssqrNames)) {
            List<String> ssqrIdsList = Arrays.asList(ssqrIds.split(",", -1));
            List<String> ssqrNmaesList = Arrays.asList(ssqrNames.split(",", -1));
            for (int index = 0; index < ssqrIdsList.size(); index++) {
                users.add(new TaskExecutor(ssqrIdsList.get(index), ssqrNmaesList.get(index)));
            }
        }
        return users;
    }

    /**
     * 实施确认人员(实施确认人可以是多个)
     */
    public Collection<TaskExecutor> getModelUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String modelPeopleId = formDataJson.getString("modelPeopleId");
        String modelPeopleName = formDataJson.getString("modelPeopleName");
        if (StringUtils.isNotBlank(modelPeopleId) && StringUtils.isNotBlank(modelPeopleName)) {
            List<String> idsList = Arrays.asList(modelPeopleId.split(",", -1));
            List<String> namesList = Arrays.asList(modelPeopleName.split(",", -1));
            for (int index = 0; index < idsList.size(); index++) {
                users.add(new TaskExecutor(idsList.get(index), namesList.get(index)));
            }
        }
        return users;
    }

    /**
     * 流程中，查询标准认证所负责人
     */
    public Collection<TaskExecutor> getBzrzsfzr() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", RdmConst.BZJSS_NAME);
        List<Map<String, String>> bzrzyjsfzrs = xcmgProjectOtherDao.getDepRespManById(params);
        if (bzrzyjsfzrs != null && !bzrzyjsfzrs.isEmpty()) {
            for (Map<String, String> depRespMan : bzrzyjsfzrs) {
                users.add(new TaskExecutor(depRespMan.get("PARTY2_"), depRespMan.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 牵头人部门负责人
     */
    public Collection<TaskExecutor> getQtrbmfzrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String standardLeaderId = formDataJson.getString("standardLeaderId");
        Map<String, Object> queryUserParams = new HashMap<>();
        queryUserParams.put("USER_ID_", standardLeaderId);
        List<Map<String, Object>> userInfos = xcmgProjectOtherDao.getUserInfoById(queryUserParams);
        String groupId = "";
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, Object> oneMap : userInfos) {
                if (StringUtils.isNotBlank(oneMap.get("dimKey").toString()) && oneMap.get("dimKey").equals("_ADMIN")) {
                    groupId = oneMap.get("groupId").toString();
                    break;
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", groupId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-LEADER");
        List<Map<String, String>> user = commonInfoDao.queryUserByGroupId(params);
        if (user != null && !user.isEmpty()) {
            for (Map<String, String> oneLeader : user) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * @tty 售前文件 获取产品主管
     * @return
     */
    public Collection<TaskExecutor> getSaleDirector() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("director");
        String userName = formDataJson.getString("directorName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    /**
     * 查询产品主管的部门负责人
     *
     * @return
     */
    public Collection<TaskExecutor> getCpzgDeptResper() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("director");
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(userId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * 查询编制人员的部门负责人， 如果是产品亮点介绍，则查询产品负责人，字典里面配置项目
     *
     * @return
     */
    public Collection<TaskExecutor> getSaleDeptResper() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("editorUserId");
        String fileType = formDataJson.getString("fileType");
        if ("cpldjs".equals(fileType)) {
            String applyType = formDataJson.getString("applyType");
            JSONObject paramJson = new JSONObject();
            paramJson.put("dicType", "JX-SPR");
            paramJson.put("dicKey", applyType);
            JSONObject dicObj = commonInfoDao.getDicInfo(paramJson);
            if (dicObj != null) {
                String auditUsers = dicObj.getString("VALUE_");
                String[] userIdArray = auditUsers.split(",");
                for (String tempUserId : userIdArray) {
                    OsUser osUser = osUserDao.get(tempUserId);
                    users.add(new TaskExecutor(tempUserId, osUser.getFullname()));
                }
            }
            return users;
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(userId);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * @tty 售前文件产品样本类型中“营销公司审核产品样本”节点，根据编制部门是国内还是海外，查询相应的角色
     * @return
     */
    public Collection<TaskExecutor> getSaleAuditLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String editorUserDeptId = formDataJson.getString("editorUserDeptId");
        if (commonInfoManager.queryWhetherUnderDept(editorUserDeptId, RdmConst.GNYXGS_NAME)) {
            List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName(RdmConst.GNCPYBSH);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> oneUser : userInfos) {
                    users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                }
            }
        } else if (commonInfoManager.queryWhetherUnderDept(editorUserDeptId, RdmConst.HWYXGS_NAME)) {
            List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName(RdmConst.HWCPYBSH);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> oneUser : userInfos) {
                    users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                }
            }
        }
        return users;
    }


    /**
     * 售前文件变更时，产品样本类型“营销公司审核产品样本变更”节点
     * 
     * @return
     */
    public Collection<TaskExecutor> getSaleAuditLeaderChange() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        // 查找编制人的主部门
        JSONObject creatorDeptInfo = commonInfoManager.queryDeptByUserId(formDataJson.getString("CREATE_BY_"));
        String editorUserDeptId = creatorDeptInfo.getString("deptId");
        if (commonInfoManager.queryWhetherUnderDept(editorUserDeptId, RdmConst.GNYXGS_NAME)) {
            List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName(RdmConst.GNCPYBSH);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> oneUser : userInfos) {
                    users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                }
            }
        } else if (commonInfoManager.queryWhetherUnderDept(editorUserDeptId, RdmConst.HWYXGS_NAME)) {
            List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName(RdmConst.HWCPYBSH);
            if (userInfos != null && !userInfos.isEmpty()) {
                for (Map<String, String> oneUser : userInfos) {
                    users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                }
            }
        }

        return users;
    }

    // 网关判断(售前文件 根据文件类型走不同的审批) 除了产品样本和市场推广文件之外的类型
    public boolean saleFlowToApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if (!"cpyb".equals(fileType) && !"sctgwj".equals(fileType)) {
            return true;
        }
        return false;
    }

    // 网关判断(售前文件 根据文件类型走不同的审批) 市场推广文件
    public boolean saleFlowToAudit(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("sctgwj".equals(fileType)) {
            return true;
        }
        return false;
    }

    // 网关判断(售前文件 根据文件类型走不同的审批) 产品样本
    public boolean saleFlowToCheck(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("cpyb".equals(fileType)) {
            return true;
        }
        return false;
    }

    // 网关判断(欧美澳售前文件 根据文件类型走不同的审批) 技术规格书,产品标选配表,多功能机具系统压力流量范围标准值表
    public boolean saleFileOMACheckjsggs(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("jsggs".equals(fileType) ||"cpbxpb".equals(fileType) || "dgnjjxtylllfwbzzb".equals(fileType)) {
            return true;
        }
        return false;
    }


    // 网关判断(欧美澳售前文件 根据文件类型走不同的审批) 产品亮点及竞争力分析
    public boolean saleFileOMACheckcpldjs(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("cpldjs".equals(fileType)) {
            return true;
        }
        return false;
    }
    // 网关判断(欧美澳售前文件 根据文件类型走不同的审批) 产品服务培训材料
    public boolean saleFileOMACheckcpfwpxcl(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("cpfwpxcl".equals(fileType)) {
            return true;
        }
        return false;
    }
    // 网关判断(欧美澳售前文件 根据文件类型走不同的审批) 产品导购手册
    public boolean saleFileOMACheckcpdgsc(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("cpdgsc".equals(fileType)) {
            return true;
        }
        return false;
    }

    // 网关判断(欧美澳售前文件 根据文件类型走不同的审批) 全生命周期成本清单
    public boolean saleFileOMACheckqsmzqcbqd(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fileType = formDataJson.getString("fileType");
        if ("qsmzqcbqd".equals(fileType)) {
            return true;
        }
        return false;
    }
    // 网关判断(欧美澳售前文件 是否类中国内销)
    public boolean saleFileOMAChecklzgnx(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String region = formDataJson.getString("region");
        if ("类中国".equals(region) || "内销".equals(region) ) {
            return true;
        }
        return false;
    }
    public boolean saleFileOMAChecklzgnxNO(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String region = formDataJson.getString("region");
        if ("类中国".equals(region) || "内销".equals(region) ) {
            return  false;
        }
        return true;
    }

    /**
     * @tty 征求人员(征求意见人可以是多个)
     * @return
     */
    public Collection<TaskExecutor> saleFileOMAgetZqyjResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String yjUserIds = formDataJson.getString("yjUserIds");
        String yjUserNames = formDataJson.getString("yjUserNames");

        users.add(new TaskExecutor(yjUserIds, yjUserNames));
        return users;
    }
    /**
     * @tty 征求人员的领导(征求意见人的部门负责人)
     * @return
     */
    public Collection<TaskExecutor> saleFileOMAgetZqyjRespDept() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String yjUserIds = formDataJson.getString("yjUserIds");
        String yjUserNames = formDataJson.getString("yjUserNames");

        if (StringUtils.isNotBlank(yjUserIds) && StringUtils.isNotBlank(yjUserNames)) {
            List<String> bzscIdsList = Arrays.asList(yjUserIds.split(",", -1));
            List<String> bzscNmaesList = Arrays.asList(yjUserNames.split(",", -1));
            for (int index = 0; index < bzscIdsList.size(); index++) {
//                users.add(new TaskExecutor(bzscIdsList.get(index), bzscNmaesList.get(index)));
                List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(bzscIdsList.get(index));
                if (deptResps != null && !deptResps.isEmpty()){
                    for (JSONObject depRespMan : deptResps) {
                        users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
                    }
                }
            }
        }
        return users;
    }
    /**
     * @tty 新品试制，获取计划管理员
     * @return
     */
    public Collection<TaskExecutor> getXpszPlanAdmin() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainIds");
        JSONObject xpszObj = productDao.getObjectById(mainId);
        String userId = xpszObj.getString("planAdmin");
        String userName = xpszObj.getString("planAdminName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    // 标准发布及打印归档
    public Collection<TaskExecutor> getBzfbDygd() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String standardPeopleId = formDataJson.getString("standardPeopleId");
        String standardPeopleName = formDataJson.getString("standardPeopleName");
        users.add(new TaskExecutor(standardPeopleId, standardPeopleName));
        // 增加打印人员
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "标准打印");
        List<Map<String, String>> leaderInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        if (leaderInfos != null && !leaderInfos.isEmpty()) {
            for (Map<String, String> oneLeader : leaderInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }

        return users;
    }

    /**
     * @tty 研发将本 获取审批组长
     * @return
     */
    public Collection<TaskExecutor> getYfjbGroupLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = formDataJson.getString("auditUserId");
        String userName = formDataJson.getString("auditUserName");
        users.add(new TaskExecutor(userId, userName));
        return users;
    }

    /**
     * @tty 研发将本 根据专业自动匹配审批组长
     * @return
     */
    public Collection<TaskExecutor> getYfjbMajorLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainId");
        JSONObject obj = yfjbBaseInfoDao.getObjectById(mainId);
        String major = obj.getString("major");
        JSONObject auditObj = yfjbBaseInfoDao.getAuditRelation(major);
        String replyUserId = auditObj.getString("userIds");
        String replyUserName = auditObj.getString("userName");
        String[] userIdArray = replyUserId.split(",");
        String[] userNameArray = replyUserName.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
            users.add(new TaskExecutor(userIdArray[i], userNameArray[i]));
        }
        return users;
    }

    /**
     * @tty 论坛 改进提案 获取回复人信息 1.先取表单中回复人员 2.如果未选择，则选择板块负责人
     */
    public Collection<TaskExecutor> getReplyUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String replyUserId = formDataJson.getString("replyUserId");
        String replyUserName = formDataJson.getString("replyUserName");
        String[] userIdArray = replyUserId.split(",");
        String[] userNameArray = replyUserName.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
            users.add(new TaskExecutor(userIdArray[i], userNameArray[i]));
        }
        return users;
    }

    /**
     * @tty 论坛 改进提案 获取板块负责人 1.如果未选择，则选择板块负责人
     */
    public Collection<TaskExecutor> getPlateResponsor() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String plate = formDataJson.getString("plate");
        Subsystem subsystem = subsystemDao.getByKey(plate);
        String principalUserId = subsystem.getPrincipalId();
        String principalUserName = subsystem.getPrincipal();
        if (StringUtil.isEmpty(principalUserId)) {
            return users;
        }
        String[] userIdArray = principalUserId.split(",");
        String[] userNameArray = principalUserName.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
            users.add(new TaskExecutor(userIdArray[i], userNameArray[i]));
        }
        return users;
    }

    public Collection<TaskExecutor> getTraceUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String plate = formDataJson.getString("plate");
        Subsystem subsystem = subsystemDao.getByKey(plate);
        String traceUserId = subsystem.getTraceUserId();
        String traceUserName = subsystem.getTraceUserName();
        if (StringUtil.isEmpty(traceUserId)) {
            return users;
        }
        String[] userIdArray = traceUserId.split(",");
        String[] userNameArray = traceUserName.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
            users.add(new TaskExecutor(userIdArray[i], userNameArray[i]));
        }
        return users;
    }

    /**
     * 查询分管主任
     */
    public Collection<TaskExecutor> getDepFgzrUser(String userid) {
        List<TaskExecutor> users = new ArrayList<>();
        if (StringUtils.isBlank(userid)) {
            userid = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> fgzrs = ccbgDao.queryFgzrByUserId(userid);
        if (fgzrs != null && !fgzrs.isEmpty()) {
            for (JSONObject fgzr : fgzrs) {
                users.add(new TaskExecutor(fgzr.getString("USER_ID_"), fgzr.getString("FULLNAME_")));
            }
        }
        return users;
    }

    /**
     * @tty 年度开发计划-获取项目责任所长
     */
    public Collection<TaskExecutor> getProjectRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainId");
        JSONObject detailObj = ndkfjhPlanDetailDao.getObjectById(mainId);
        String responsor = detailObj.getString("responsor");
        if (StringUtil.isEmpty(responsor)) {
            return users;
        }
        String responsorName = detailObj.getString("responsorName");
        users.add(new TaskExecutor(responsor, responsorName));
        return users;
    }

    /**
     * @tty 年度开发计划-获取项目计划管理员
     */
    public Collection<TaskExecutor> getProjectMngUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String mainId = formDataJson.getString("mainId");
        JSONObject detailObj = ndkfjhPlanDetailDao.getObjectById(mainId);
        String manager = detailObj.getString("manager");
        if (StringUtil.isEmpty(manager)) {
            return users;
        }
        String managerName = detailObj.getString("managerName");
        users.add(new TaskExecutor(manager, managerName));
        return users;
    }

    /**
     * @tty 年度开发计划新增流程-获取责任所在
     */
    public Collection<TaskExecutor> getAddProjectRespUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String responsor = formDataJson.getString("responsor");
        if (StringUtil.isEmpty(responsor)) {
            return users;
        }
        String responsorName = formDataJson.getString("responsorName");
        users.add(new TaskExecutor(responsor, responsorName));
        return users;
    }

    public boolean researchMaterial(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyType = formDataJson.getString("applyType");
        if (applyType.startsWith("Z53") || applyType.startsWith("Z55")) {
            applyType = applyType.substring(0, 3);
        }
        if ("Z53".equalsIgnoreCase(applyType) || "Z55".equalsIgnoreCase(applyType)
            || "Z56".equalsIgnoreCase(applyType)) {
            return true;
        }
        return false;
    }

    public boolean otherMaterial(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String applyType = formDataJson.getString("applyType");
        if (applyType.startsWith("Z53") || applyType.startsWith("Z55")) {
            applyType = applyType.substring(0, 3);
        }
        if ("Z27".equalsIgnoreCase(applyType) || "311".equalsIgnoreCase(applyType)) {
            return true;
        }
        return false;
    }

    /**
     * 获取物料预留号 财务订单对应项目负责人
     */
    public Collection<TaskExecutor> getMaterialLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String orderCode = formDataJson.getString("orderCode");
        if (StringUtil.isEmpty(orderCode)) {
            return users;
        }
        JSONObject userObj = rdmZhglDao.getProjectManager(orderCode);
        if (userObj == null) {
            return users;
        } else {
            users.add(new TaskExecutor(userObj.getString("userId"), userObj.getString("userName")));
        }
        return users;
    }

    // 动态管理立项审核是否需要复议
    public boolean needFy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sffy = formDataJson.getString("sffy");
        if ("yes".equalsIgnoreCase(sffy)) {
            return true;
        }
        return false;
    }

    public boolean notNeedFy(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sffy = formDataJson.getString("sffy");
        if (("no").equalsIgnoreCase(sffy)) {
            return true;
        }
        return false;
    }

    public boolean rejectEnd(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sffy = formDataJson.getString("sffy");
        if (("reject").equalsIgnoreCase(sffy)) {
            return true;
        }
        return false;
    }

    // 动态管理复议是否通过
    public boolean fyApproval(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fyjg = formDataJson.getString("fyjg");
        if ("yes".equalsIgnoreCase(fyjg)) {
            return true;
        }
        return false;
    }

    public boolean fyReject(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String fyjg = formDataJson.getString("fyjg");
        if ("no".equalsIgnoreCase(fyjg)) {
            return true;
        }
        return false;
    }

    /**
     * @mh 团队成员(可以是多个)
     * @return
     */
    public Collection<TaskExecutor> getTeamMember() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String mainDraftIds = formDataJson.getString("mainDraftIds");
        String mainDraftNames = formDataJson.getString("mainDraftNames");
        users.add(new TaskExecutor(mainDraftIds, mainDraftNames));
        return users;
    }

    /**
     * RDM功能开发流程中，开发负责人
     */
    public Collection<TaskExecutor> queryRdmDevResp() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);

        String devRespUserId = formDataJson.getString("devRespUserId");
        String devRespUserName = formDataJson.getString("devRespUserName");
        users.add(new TaskExecutor(devRespUserId, devRespUserName));
        return users;
    }

    /*
    *科技论文管理，在部长执行完更新表单
     */
    //..
    public void taskEndScript(Map<String, Object> vars) {
        try {
            JSONObject jsonObject = ProcessHandleHelper.getProcessCmd().getJsonDataObject();

            if (jsonObject.isEmpty()) {
                logger.warn("formData is blank");
                return ;
            }

            kjlwService.updateKjlw(jsonObject);

        } catch (Exception e) {
            logger.error("Exception in taskEndScript", e);
        }
    }
}
