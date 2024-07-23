package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.ZlgjWTDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ZlgjWTScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(ZlgjHandler.class);
    @Autowired
    private ZlgjWTDao zlgjWTDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Autowired
    private ZlgjWTService zlgjWTService;

    // 质量改进 分管主任
    public Collection<TaskExecutor> getDeptFgzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String deptId = formDataJson.getString("ssbmId");
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIRECTOR");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }

    // 所属部门负责人
    public Collection<TaskExecutor> getDeptFzr() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String deptId = formDataJson.getString("ssbmId");
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

    // 获取流程中质量改进工程师
    public Collection<TaskExecutor> getGjgcsUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String wtlx = formDataJson.getString("wtlx");
        if (StringUtils.isNotBlank(wtlx)) {
            // 市场问题（根据机型类别定人）
            if (wtlx.equalsIgnoreCase(ZlgjConstant.SCWT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("jiXing", formDataJson.getString("jiXing"));
                List<JSONObject> userInfos = zlgjWTDao.querySCWTSHRYEnum(params);
                if (userInfos != null && !userInfos.isEmpty()) {
                    JSONObject userInfo = userInfos.get(0);
                    String[] userIdArr = userInfo.getString("userId").split(",", -1);
                    String[] userNameArr = userInfo.getString("userName").split(",", -1);
                    for (int index = 0; index < userIdArr.length; index++) {
                        users.add(new TaskExecutor(userIdArr[index], userNameArr[index]));
                    }
                }
            } else if (wtlx.equalsIgnoreCase(ZlgjConstant.HWWT)) {
                // 海外问题类，查找角色
                List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName("海外问题需求审核人");
                if (userInfos != null && !userInfos.isEmpty()) {
                    for (Map<String, String> oneUser : userInfos) {
                        users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                    }
                }
            } else if (wtlx.equalsIgnoreCase(ZlgjConstant.CNWT)) {
                // 厂内问题
                String userId = "";
                userId = formDataJson.getString("CREATE_BY_");
                if (StringUtils.isBlank(userId)) {
                    userId = ContextUtil.getCurrentUserId();
                }
                String deptId = commonInfoDao.getDeptIdByUserId(userId);
                Map<String, Object> params = new HashMap<>();
                params.put("deptId", deptId);
                params.put("REL_TYPE_KEY_", "GROUP-USER-ZLJZRY");
                List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
                if (userInfos != null && !userInfos.isEmpty()) {
                    for (Map<String, String> oneLeader : userInfos) {
                        users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
                    }
                }
            } else {
                // 新品类，查找角色
                List<Map<String, String>> userInfos = rdmZhglUtil.queryUserByGroupName("新品需求审核人员");
                if (userInfos != null && !userInfos.isEmpty()) {
                    for (Map<String, String> oneUser : userInfos) {
                        users.add(new TaskExecutor(oneUser.get("USER_ID_"), oneUser.get("FULLNAME_")));
                    }
                }
            }
        }
        return users;
    }

    // 只有字典项中配置的部门才需要走质量员审核
    public boolean needZlySh(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = "";
        userId = formDataJson.getString("CREATE_BY_");
        if (StringUtils.isBlank(userId)) {
            userId = ContextUtil.getCurrentUserId();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(userId));
        List<JSONObject> deptInfos = commonInfoDao.getDeptInfoByUserIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return false;
        }
        String deptName = deptInfos.get(0).getString("deptName");
        Map<String, Object> deptNameMap = commonInfoManager.genMap("ZLYSHDBM");
        if (deptNameMap != null && deptNameMap.containsKey(deptName)) {
            return true;
        }
        return false;
    }

    // 只有字典项中配置的部门才需要走质量员审核
    public boolean notNeedZlySh(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = "";
        userId = formDataJson.getString("CREATE_BY_");
        if (StringUtils.isBlank(userId)) {
            userId = ContextUtil.getCurrentUserId();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", Arrays.asList(userId));
        List<JSONObject> deptInfos = commonInfoDao.getDeptInfoByUserIds(params);
        if (deptInfos == null || deptInfos.isEmpty()) {
            return true;
        }
        String deptName = deptInfos.get(0).getString("deptName");
        Map<String, Object> deptNameMap = commonInfoManager.genMap("ZLYSHDBM");
        if (deptNameMap != null && deptNameMap.containsKey(deptName)) {
            return false;
        }
        return true;
    }

    // 获取创建人部门的质量兼职人员
    public Collection<TaskExecutor> getZljzryUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String userId = "";
        userId = formDataJson.getString("CREATE_BY_");
        if (StringUtils.isBlank(userId)) {
            userId = ContextUtil.getCurrentUserId();
        }
        String deptId = commonInfoDao.getDeptIdByUserId(userId);
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("REL_TYPE_KEY_", "GROUP-USER-ZLJZRY");
        List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }

        return users;
    }

    // 责任人
    public Collection<TaskExecutor> getZrrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrrId = formDataJson.getString("zrrId");
        String zrrName = formDataJson.getString("zrrName");
        if (StringUtils.isNotBlank(zrrId) && StringUtils.isNotBlank(zrrName)) {
            users.add(new TaskExecutor(zrrId, zrrName));
        }
        return users;
    }

    // 变更流程 分管领导批准
    public boolean leaderApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if ("YES".equals(yesOrno)) {
            return true;
        }
        return false;
    }

    // 变更流程 技术管理部批准
    public boolean tecGJXGApprove(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String yesOrno = formDataJson.getString("sffgsp");
        if ("NO".equals(yesOrno)) {
            return true;
        }
        return false;
    }

    // 项目责任人会签
    public Collection<TaskExecutor> getJsjdshqUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String hqrId = formDataJson.getString("hqrId");
        String hqrName = formDataJson.getString("hqrName");
        if (StringUtils.isNotBlank(hqrId) && StringUtils.isNotBlank(hqrName)) {
            List<String> hqrIdList = Arrays.asList(hqrId.split(",", -1));
            List<String> hqrNameList = Arrays.asList(hqrName.split(",", -1));
            for (int index = 0; index < hqrIdList.size(); index++) {
                users.add(new TaskExecutor(hqrIdList.get(index), hqrNameList.get(index)));
            }
        }
        return users;
    }

    // 质量改进工程师
    /*public Collection<TaskExecutor> getZlgjgcs() {
        List<TaskExecutor> users = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("REL_TYPE_KEY_", "GROUP-USER-DIREGJGCS");
        List<Map<String, String>> userInfos = zlgjWTDao.queryUserByZlgjgcs(params);
        if (userInfos != null && !userInfos.isEmpty()) {
            for (Map<String, String> oneLeader : userInfos) {
                users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
            }
        }
        return users;
    }*/

    // 产品主管
    public Collection<TaskExecutor> getCpzgUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String zrcpzgId = formDataJson.getString("zrcpzgId");
        String zrcpzgName = formDataJson.getString("zrcpzgName");
        if (StringUtils.isNotBlank(zrcpzgId) && StringUtils.isNotBlank(zrcpzgName)) {
            users.add(new TaskExecutor(zrcpzgId, zrcpzgName));
        }
        return users;
    }

    // 变更流程 是新品不跳过改进方案审核
    public boolean NewProduct(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String wtlx = formDataJson.getString("wtlx");
        String applyUserDeptName = formDataJson.getString("applyUserDeptName");
        if (wtlx.equalsIgnoreCase(ZlgjConstant.XPSZ) || wtlx.equalsIgnoreCase(ZlgjConstant.XPZDSY)
            || wtlx.equalsIgnoreCase(ZlgjConstant.XPLS) || wtlx.equalsIgnoreCase(ZlgjConstant.SCWT)
            || wtlx.equalsIgnoreCase(ZlgjConstant.WXBLX) || wtlx.equalsIgnoreCase(ZlgjConstant.HWWT)
                || wtlx.equalsIgnoreCase(ZlgjConstant.LBJSY)
                || (wtlx.equalsIgnoreCase(ZlgjConstant.CNWT) && !RdmConst.ZLBZB_NAME.equals(applyUserDeptName))) {
            return true;
        }
        return false;
    }

    // 变更流程 不是新品跳过改进方案审核
    public boolean moNewProduct(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String wtlx = formDataJson.getString("wtlx");
        String applyUserDeptName = formDataJson.getString("applyUserDeptName");
        if (wtlx.equalsIgnoreCase(ZlgjConstant.CNWT) && RdmConst.ZLBZB_NAME.equals(applyUserDeptName)) {
            return true;
        }
        return false;
    }

    // 变更流程 并案且严重问题
    public boolean yanzhongba(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifba = formDataJson.getString("ifba");
        String yzcd = formDataJson.getString("yzcd");
        if (ifba.equals("yes") && (yzcd.equals("A") || yzcd.equals("B"))) {
            return true;
        }
        return false;
    }

    // 变更流程 不改进且一般问题
    public boolean yibanba(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifba = formDataJson.getString("ifba");
        String yzcd = formDataJson.getString("yzcd");
        if (ifba.equals("yes") && (yzcd.equals("C") || yzcd.equals("W"))) {
            return true;
        }
        return false;
    }

    // 变更流程 改进
    public boolean gaijinba(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifba = formDataJson.getString("ifba");
        if (ifba.equals("no")) {
            return true;
        }
        return false;
    }

    // 变更流程 不改进且严重问题
    public boolean yanzhong(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifgj = formDataJson.getString("ifgj");
        String yzcd = formDataJson.getString("yzcd");
        if (ifgj.equals("no") && (yzcd.equals("A") || yzcd.equals("B"))) {
            return true;
        }
        return false;
    }

    // 变更流程 不改进且一般问题
    public boolean yiban(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifgj = formDataJson.getString("ifgj");
        String yzcd = formDataJson.getString("yzcd");
        if (ifgj.equals("no") && (yzcd.equals("C") || yzcd.equals("W"))) {
            return true;
        }
        return false;
    }

    // 变更流程 改进
    public boolean gaijin(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String ifgj = formDataJson.getString("ifgj");
        if (ifgj.equals("yes")) {
            return true;
        }
        return false;
    }

    // 最终有效
    public boolean isValid(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfyx = formDataJson.getString("sfyx");
        if (sfyx.equalsIgnoreCase("YES")) {
            return true;
        }
        return false;
    }

    // 最终无效
    public boolean isNotValid(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfyx = formDataJson.getString("sfyx");
        if (sfyx.equalsIgnoreCase("NO")) {
            return true;
        }
        return false;
    }

    // 流程最后验证
    /*public Collection<TaskExecutor> getZhqrUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String wtlx = formDataJson.getString("wtlx");
        if (StringUtils.isNotBlank(wtlx)) {
            // 问题改进类（根据部门取对应的）
            if (wtlx.equalsIgnoreCase(ZlgjConstant.CNWT) || wtlx.equalsIgnoreCase(ZlgjConstant.SCWT)
                || wtlx.equalsIgnoreCase(ZlgjConstant.HWWT)) {
                String deptId = formDataJson.getString("ssbmId");
                Map<String, Object> params = new HashMap<>();
                params.put("deptId", deptId);
                params.put("REL_TYPE_KEY_", "GROUP-USER-DIREGJGCS");
                List<Map<String, String>> userInfos = commonInfoDao.queryUserByGroupId(params);
                if (userInfos != null && !userInfos.isEmpty()) {
                    for (Map<String, String> oneLeader : userInfos) {
                        users.add(new TaskExecutor(oneLeader.get("USER_ID_"), oneLeader.get("FULLNAME_")));
                    }
                }
            } else {
                // 新品由发起人进行验证
                String userId = formDataJson.getString("CREATE_BY_");
                String userNmae = formDataJson.getString("applyUserName");
                users.add(new TaskExecutor(userId, userNmae));
            }
        }
        return users;
    }*/

    // 更新责任部门审核时间(节点通过)
    public void updateZrbmshTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("zrbmshTime", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        logger.error("updateZrbmshTime, wtId {}", formDataJson.getString("wtId"));
        zlgjWTDao.updateZrbmshTime(formDataJson);
    }

    // 更新责任部门审核时间(节点创建)
    public void updateZrbmshTimeStart(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("zrbmshTime", null);
        logger.error("updateZrbmshTime, wtId {}", formDataJson.getString("wtId"));
        zlgjWTDao.updateZrbmshTime(formDataJson);
    }

    // 清空责任部门审核时间
    public void clearZrbmshTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("zrbmshTime", null);
        logger.error("clearZrbmshTime, wtId {}", formDataJson.getString("wtId"));
        zlgjWTDao.updateZrbmshTime(formDataJson);
    }

    // 清空责任部门审核时间和zrrEnd（清除6,7 共两个时间）
    public void rejectZrbmshTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        formDataJson.put("zrbmshTime", null);
        logger.error("clearZrbmshTime, wtId {}", formDataJson.getString("wtId"));
        zlgjWTDao.updateZrbmshTime(formDataJson);
        formDataJson.put("zrrEnd", null);
        zlgjWTDao.updateZrrEnd(formDataJson);
        logger.error("clearzrrEnd, wtId {}", formDataJson.getString("wtId"));
    }

    // 更新产品主管最近一次的开始时间zrcpzgStart,计划结束时间zrcpzgEndPlan，清除后续五个时间
    public void updateZrcpzgStart(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        // 计算开始时间和计划结束时间
        String nowUTCDateStr = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        param.put("zrcpzgStart", nowUTCDateStr);
        Date zrcpzgStart = DateUtil.parseDate(nowUTCDateStr, DateUtil.DATE_FORMAT_FULL);
        Date zrcpzgEndPlan = null;
        String jjcd = formDataJson.getString("jjcd");// 紧急程度
        if ("特急".equals(jjcd)) {
            zrcpzgEndPlan = DateUtil.addHour(zrcpzgStart, 12);
        } else if ("紧急".equals(jjcd)) {
            zrcpzgEndPlan = DateUtil.addDay(zrcpzgStart, 1);
        } else if ("一般".equals(jjcd)) {
            zrcpzgEndPlan = DateUtil.addDay(zrcpzgStart, 1);
        }
        param.put("zrcpzgEndPlan", DateUtil.formatDate(zrcpzgEndPlan, DateUtil.DATE_FORMAT_FULL));
        // ..
        zlgjWTDao.updateZrcpzgStart(param);
        // 后续时间全部置空3-7,五个时间
        param.put("zrcpzgEnd", null);
        zlgjWTDao.updateZrcpzgEnd(param);
        param.put("zrrStart", null);
        param.put("zrrEndPlan", null);
        zlgjWTDao.updateZrrStart(param);
        param.put("zrrEnd", null);
        zlgjWTDao.updateZrrEnd(param);
        param.put("zrbmshTime", null);
        zlgjWTDao.updateZrbmshTime(new JSONObject(param));
    }

    // 更新产品主管最近一次的结束时间
    public void updateZrcpzgEnd(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        param.put("zrcpzgEnd", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        zlgjWTDao.updateZrcpzgEnd(param);
    }

    // 更新处理人最近一次的开始时间zrrStart,计划结束时间zrrEndPlan（写入4,5，清除6,7）
    public void updateZrrStart(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        // 计算开始时间和计划结束时间
        String nowUTCDateStr = XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss");
        param.put("zrrStart", nowUTCDateStr);
        Date zrrStart = DateUtil.parseDate(nowUTCDateStr, DateUtil.DATE_FORMAT_FULL);
        Date zrrEndPlan = null;
        String jjcd = formDataJson.getString("jjcd");// 紧急程度
        if ("特急".equals(jjcd)) {
            zrrEndPlan = DateUtil.addDay(zrrStart, 3);
        } else if ("紧急".equals(jjcd)) {
            zrrEndPlan = DateUtil.addDay(zrrStart, 5);
        } else if ("一般".equals(jjcd)) {
            zrrEndPlan = DateUtil.addDay(zrrStart, 10);
        }
        param.put("zrrEndPlan", DateUtil.formatDate(zrrEndPlan, DateUtil.DATE_FORMAT_FULL));
        // 写入4,5
        zlgjWTDao.updateZrrStart(param);
        // 后续时间全部置空6,7
        param.put("zrrEnd", null);
        zlgjWTDao.updateZrrEnd(param);
        // @lwgkiller:2022-10-31,现在有改进无效重回责任部门的情况了，那么也应该在此刻清除下个节点zrbmshTime责任部门审核的时间
        param.put("zrbmshTime", null);
        zlgjWTDao.updateZrbmshTime(new JSONObject(param));
    }

    // 更新处理人最近一次的结束时间
    public void updateZrrEnd(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        param.put("zrrEnd", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        zlgjWTDao.updateZrrEnd(param);
    }

    // 最后任务结束更新轮次信息
    public void updateRounds(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String sfyx = formDataJson.getString("sfyx");
        if (sfyx.equalsIgnoreCase("NO")) {
            JSONObject zlgj = zlgjWTDao.queryZlgjById(formDataJson.getString("wtId"));
            if (zlgj.containsKey("rounds") && StringUtil.isNotEmpty(zlgj.getString("rounds"))) {
                int rounds = zlgj.getIntValue("rounds");
                rounds++;
                zlgj.put("rounds", rounds);
                zlgjWTDao.updateZlgj(zlgj);
            } else {
                zlgj.put("rounds", 2);
                zlgjWTDao.updateZlgj(zlgj);
            }
        }
    }

    // 获取流程中风险排查中的改进工程师审核人员【从审批历史中查找】
    public Collection<TaskExecutor> getCheckUser() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        String instId = formDataJson.getString("instId");
        String wtlx = formDataJson.getString("wtlx");
        String applyUserDeptName = formDataJson.getString("applyUserDeptName");
        String applyUserName = formDataJson.getString("applyUserName");
        String creator = formDataJson.getString("CREATE_BY_");
        JSONObject param = new JSONObject();
        param.put("instId", instId);
        param.put("nodeName", "改进工程师需求审核");
        List<JSONObject> handlers = zlgjWTDao.queryNodeHandler(param);
        if (handlers != null && !handlers.isEmpty()) {
            for (JSONObject oneData : handlers) {
                users.add(new TaskExecutor(oneData.getString("HANDLER_ID_"), oneData.getString("FULLNAME_")));
            }
        } else if ((RdmConst.ZLBZB_NAME.equals(applyUserDeptName) && wtlx.equalsIgnoreCase(ZlgjConstant.CNWT))) {
            users.add(new TaskExecutor(creator, applyUserName));
        }
        return users;
    }

    public Collection<TaskExecutor> getLeader() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            if (StringUtils.isNotBlank(oneObject.getString("leaderId"))) {
                users.add(new TaskExecutor(oneObject.getString("leaderId"), oneObject.getString("leaderName")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getRes() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            if (StringUtils.isNotBlank(oneObject.getString("resId"))) {
                users.add(new TaskExecutor(oneObject.getString("resId"), oneObject.getString("resName")));
            }
        }
        return users;
    }

    public Collection<TaskExecutor> getRecheck() {
        List<TaskExecutor> users = new ArrayList<>();
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
        for (int i = 0; i < tdmcDataJson.size(); i++) {
            JSONObject oneObject = tdmcDataJson.getJSONObject(i);
            if ("不通过".equals(oneObject.getString("qualified"))) {
                users.add(new TaskExecutor(oneObject.getString("resId"), oneObject.getString("resName")));
            }
        }
        return users;
    }

    public boolean pass(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (StringUtils.isNotBlank(formDataJson.getString("SUB_zlgj_riskUser"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                if ("不通过".equals(oneObject.getString("qualified"))) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean unpass(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        if (StringUtils.isNotBlank(formDataJson.getString("SUB_zlgj_riskUser"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                if ("不通过".equals(oneObject.getString("qualified"))) {
                    return true;
                }
            }
        }
        return false;
    }

    // 不需要风险排查
    public boolean isNotRisk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
        if (tdmcDataJson.size() == 0) {
            return true;
        }
        return false;
    }

    // 需要风险排查
    public boolean istRisk(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String formData = cmd.getJsonData();
        if (StringUtils.isBlank(formData)) {
            logger.warn("formData is blank");
            return false;
        }
        JSONObject formDataJson = JSONObject.parseObject(formData);
        if (formDataJson.isEmpty()) {
            logger.warn("formData is blank");
            return false;
        }
        JSONArray tdmcDataJson = JSONObject.parseArray(formDataJson.getString("SUB_zlgj_riskUser"));
        if (tdmcDataJson.size() > 0) {
            return true;
        }
        return false;
    }

    // 问题处理人驳回时，需要清空3-7 共五个时间
    public void zrrRejectClearTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        param.put("wtId", formDataJson.getString("wtId"));
        // 3
        param.put("zrcpzgEnd", null);
        zlgjWTDao.updateZrcpzgEnd(param);
        // 4,5
        param.put("zrrStart", null);
        param.put("zrrEndPlan", null);
        zlgjWTDao.updateZrrStart(param);
        // 6
        param.put("zrrEnd", null);
        zlgjWTDao.updateZrrEnd(param);
        // 7
        param.put("zrbmshTime", null);
        zlgjWTDao.updateZrbmshTime(new JSONObject(param));
    }

    // 产品主管驳回时，需要清空所有的七个时间
    public void zrcpzgRejectClearTime(Map<String, Object> vars) {
        AbstractExecutionCmd cmd = (AbstractExecutionCmd)vars.get("cmd");
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        Map<String, Object> param = new HashMap<>();
        // 1,2
        param.put("wtId", formDataJson.getString("wtId"));
        param.put("zrcpzgStart", null);
        param.put("zrcpzgEndPlan", null);
        zlgjWTDao.updateZrcpzgStart(param);
        // 3
        param.put("zrcpzgEnd", null);
        zlgjWTDao.updateZrcpzgEnd(param);
        // 4,5
        param.put("zrrStart", null);
        param.put("zrrEndPlan", null);
        zlgjWTDao.updateZrrStart(param);
        // 6
        param.put("zrrEnd", null);
        zlgjWTDao.updateZrrEnd(param);
        // 7
        param.put("zrbmshTime", null);
        zlgjWTDao.updateZrbmshTime(new JSONObject(param));
    }

    // 部门负责人
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

    // 不需要质量员审核
    public boolean isNotZl(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String applyUserDeptName = toGetApplyUserDeptName(cmd, vars);
        if (RdmConst.ZLBZB_NAME.equals(applyUserDeptName)) {
            return true;
        }
        return false;
    }

    // 需要质量员审核
    public boolean isZl(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String applyUserDeptName = toGetApplyUserDeptName(cmd, vars);
        if (StringUtils.isNotBlank(applyUserDeptName) && !RdmConst.ZLBZB_NAME.equals(applyUserDeptName)) {
            return true;
        }
        return false;
    }

    private String toGetApplyUserDeptName(AbstractExecutionCmd cmd, Map<String, Object> vars) {
        String applyUserDeptName = "";
        String formData = cmd.getJsonData();
        if (StringUtils.isNotBlank(formData)) {
            JSONObject formDataJson = JSONObject.parseObject(formData);
            if (!formDataJson.isEmpty()) {
                applyUserDeptName = formDataJson.getString("applyUserDeptName");
                return applyUserDeptName;
            }
        }
        Object busKey = vars.get("busKey");
        if (busKey != null) {
            JSONObject formBaseInfo = zlgjWTService.getZlgjDetail(busKey.toString());
            if (formBaseInfo != null) {
                applyUserDeptName = formBaseInfo.getString("applyUserDeptName");
            }
        }
        return applyUserDeptName;
    }
}
