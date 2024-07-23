package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.activiti.util.ProcessHandleHelper;
import com.redxun.bpm.core.entity.AbstractExecutionCmd;
import com.redxun.bpm.core.entity.IExecutionCmd;
import com.redxun.bpm.core.entity.TaskExecutor;
import com.redxun.core.script.GroovyScript;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.org.api.service.UserService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.CcbgDao;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CcbgScript implements GroovyScript {
    private Logger logger = LoggerFactory.getLogger(CcbgScript.class);
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CcbgDao ccbgDao;
    @Autowired
    private CcbgService ccbgService;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private UserService userService;


    //获取流程中使用的处理人为分管领导的信息
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

    //获取指定人的部门负责人的信息
    public Collection<TaskExecutor> getDepRespUser(String userid) {
        List<TaskExecutor> users = new ArrayList<>();
        if (StringUtils.isBlank(userid)) {
            userid = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(userid);
        if (deptResps != null && !deptResps.isEmpty()) {
            for (JSONObject depRespMan : deptResps) {
                users.add(new TaskExecutor(depRespMan.getString("USER_ID_"), depRespMan.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //获取指定人的部门分管主任的信息
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

    //获取指定人的部门内室主任的信息
    public Collection<TaskExecutor> getDepSzrUser(String userid) {
        List<TaskExecutor> users = new ArrayList<>();
        if (StringUtils.isBlank(userid)) {
            userid = ContextUtil.getCurrentUserId();
        }
        List<JSONObject> szrs = ccbgDao.querySzrByUserId(userid);
        if (szrs != null && !szrs.isEmpty()) {
            for (JSONObject szr : szrs) {
                users.add(new TaskExecutor(szr.getString("USER_ID_"), szr.getString("FULLNAME_")));
            }
        }
        return users;
    }

    //1.随行最大官是所长，则调用他的getDepFgzrUser
    //2.随行人员是室主任或产品主管,则调用他的getDepRespUser
    //3.随行人员是一般人员，则调用编制人的getDepSzrUser
    public Collection<TaskExecutor> getReviewingUsers() {
        String haveSz = "false";
        String szUserId = "";
        String haveSzrOrCpzg = "false";
        String szrOrCpzgUserId = "";
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        //以下根据随行人的职位大小来进行标记变量的初始化
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            } else if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "动力室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品技术支持室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "液压室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "电气室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "覆盖件室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "工作装置室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘传动室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品主管")) {
                haveSzrOrCpzg = "true";
                szrOrCpzgUserId = szrOrCpzgUserId == "" ? memberUserId : szrOrCpzgUserId;//取首位;
            }
        }
        //以下根据标记变量做判断返回合适的审核人
        if (haveSz.equals("true")) {
            return getDepFgzrUser(szUserId);
        } else if (haveSzrOrCpzg.equals("true")) {
            return getDepRespUser(szrOrCpzgUserId);
        } else {
            String mengFanJianShouXia = WebAppUtil.getProperty("mengFanJianShouXia");
            String[] mengFanJianShouXias = mengFanJianShouXia.split(":");
            for (String userno : mengFanJianShouXias) {
                String currentUserName = userService.getByUserId(formDataJson.get("CREATE_BY_").toString()).getUserNo();
                if (currentUserName.equalsIgnoreCase(userno)) {
                    List<TaskExecutor> users = new ArrayList<>();
                    users.add(new TaskExecutor("211326426918330401", "孟凡建"));
                    return users;
                }
            }
            return getDepSzrUser(formDataJson.get("CREATE_BY_").toString());
        }
    }

    //1.随行人员最大是副主任，则调用fgldUserInfos
    //2.随行人员最大是所长，则调用他的fgldUserInfos
    //3.随行人员是室主任或产品主管，则调用他的getDepFgzrUser
    //4.随行人员是一般人员，则调用编制人的getDepRespUser
    public Collection<TaskExecutor> getApprovingUsers() {
        String haveFgzr = "false";
        String fgzrUserId = "";
        String haveSz = "false";
        String szUserId = "";
        String haveSzrOrCpzg = "false";
        String szrOrCpzgUserId = "";
        IExecutionCmd cmd = ProcessHandleHelper.getProcessCmd();
        String formData = cmd.getJsonData();
        JSONObject formDataJson = JSONObject.parseObject(formData);
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        //以下根据随行人的职位大小来进行标记变量的初始化
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointRoleUser(memberUserId, RdmConst.JSZX_ZR)) {
                haveFgzr = "true";
                fgzrUserId = fgzrUserId == "" ? memberUserId : fgzrUserId;//取首位
            } else if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            } else if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "动力室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品技术支持室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "液压室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "电气室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "覆盖件室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "工作装置室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘传动室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品主管")) {
                haveSzrOrCpzg = "true";
                szrOrCpzgUserId = szrOrCpzgUserId == "" ? memberUserId : szrOrCpzgUserId;//取首位;
            }
        }
        //以下根据标记变量做判断返回合适的批准人
        if (haveFgzr.equals("true")) {
            return fgldUserInfos();
        } else if (haveSz.equals("true")) {
            return fgldUserInfos();
        } else if (haveSzrOrCpzg.equals("true")) {
            //后来改的，产品主管和室主任也是耿总审批
            return fgldUserInfos();
//            return getDepFgzrUser(szrOrCpzgUserId);
        } else {
            return getDepRespUser(formDataJson.get("CREATE_BY_").toString());
        }
    }

    //随行人有主任1
    public boolean haveZr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        //判断随行人员是否（有主任），一旦有为真
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointRoleUser(memberUserId, RdmConst.JSZX_ZR)) {
                return true;
            }
        }
        return false;
    }

    //随行人没有主任2
    public boolean notHaveZr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        //判断随行人员是否（有主任），一旦有为假
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointRoleUser(memberUserId, RdmConst.JSZX_ZR)) {
                return false;
            }
        }
        return true;
    }

    //随行人最大所长且分管主任为耿总1
    public boolean firstSzNotHaveZr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        String haveSz = "false";
        String szUserId = "";
        //来到这的肯定没有副主任，再判断下有没有所长
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            }
        }
        //如果有所长
        if (haveSz.equals("true")) {
            List<JSONObject> fgzrs = ccbgDao.queryFgzrByUserId(szUserId);
            //如果有分管主任
            if (fgzrs.size() > 0) {
                for (JSONObject fgzr : fgzrs) {
                    //如果分管主任是耿家文
                    if (fgzr.getString("USER_ID_").equals("185023978478100530")) {
                        return true;
                    }
                }
                return false;
            }
            //根本没有分管主任和耿家文是主任一个效果
            else {
                return true;
            }
        }
        //根本没有所长
        else {
            return false;
        }
    }

    //随行人最大所长且分管主任为耿总的否定2
    public boolean firstSzNotHaveZrNeg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        String haveSz = "false";
        String szUserId = "";
        //来到这的肯定没有副主任，再判断下有没有所长
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            }
        }
        //如果有所长
        if (haveSz.equals("true")) {
            List<JSONObject> fgzrs = ccbgDao.queryFgzrByUserId(szUserId);
            //如果有分管主任
            if (fgzrs.size() > 0) {
                for (JSONObject fgzr : fgzrs) {
                    //如果分管主任是耿家文
                    if (fgzr.getString("USER_ID_").equals("185023978478100530")) {
                        return false;
                    }
                }
                return true;
            }
            //根本没有分管主任和耿家文是主任一个效果
            else {
                return false;
            }
        }
        //根本没有所长
        else {
            return true;
        }
    }

    //随行人全是一般人员且编制人没有室主任2
    public boolean allNormalAndFirstNotHaveSzr(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        String haveSz = "false";
        String szUserId = "";
        String haveSzrOrCpzg = "false";
        String szrOrCpzgUserId = "";
        //来到这的肯定没有副主任，再判断下有没有所长,室主任
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            } else if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "动力室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品技术支持室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "液压室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "电气室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "覆盖件室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "工作装置室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘传动室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品主管")) {
                haveSzrOrCpzg = "true";
                szrOrCpzgUserId = szrOrCpzgUserId == "" ? memberUserId : szrOrCpzgUserId;//取首位;
            }
        }
        //如果有所长
        if (haveSz.equals("true")) {
            return false;
        }
        //如果有室主任
        else if (haveSzrOrCpzg.equals("true")) {
            return false;
        }
        //都是一般人员了,要通过编制者自己做判断了
        else {
            List<JSONObject> szrs = ccbgDao.querySzrByUserId(formDataJson.getString("CREATE_BY_"));
            //如果编制者有室主任
            if (szrs.size() > 0) {
                return false;
            }
            //满足最终条件，一般人员且没有室主任
            else {
                return true;
            }
        }
    }

    //随行人全是一般人员且编制人没有室主任的否定1
    public boolean allNormalAndFirstNotHaveSzrNeg(AbstractExecutionCmd cmd, Map<String, Object> vars) {
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
        List<String> memberUserIdsList =
                Arrays.asList(formDataJson.getString("memberUserIds").split(",", -1));
        String haveSz = "false";
        String szUserId = "";
        String haveSzrOrCpzg = "false";
        String szrOrCpzgUserId = "";
        //来到这的肯定没有副主任，再判断下有没有所长,室主任
        for (String memberUserId : memberUserIdsList) {
            if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "副所长") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "所长助理")) {
                haveSz = "true";
                szUserId = szUserId == "" ? memberUserId : szUserId;//取首位;
            } else if (rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "动力室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品技术支持室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "液压室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "电气室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "覆盖件室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "工作装置室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "底盘传动室主任") ||
                    rdmZhglUtil.judgeIsPointGwUser(memberUserId, "产品主管")) {
                haveSzrOrCpzg = "true";
                szrOrCpzgUserId = szrOrCpzgUserId == "" ? memberUserId : szrOrCpzgUserId;//取首位;
            }
        }
        //如果有所长
        if (haveSz.equals("true")) {
            return true;
        }
        //如果有室主任
        else if (haveSzrOrCpzg.equals("true")) {
            return true;
        }
        //都是一般人员了,要通过编制者自己做判断了
        else {
            List<JSONObject> szrs = ccbgDao.querySzrByUserId(formDataJson.getString("CREATE_BY_"));
            //如果编制者有室主任
            if (szrs.size() > 0) {
                return true;
            }
            //满足最终条件，一般人员且没有室主任
            else {
                return false;
            }
        }
    }

    //1.以驳回形式进入编制状态需要
    //2.以任何形式进入审核状态需要
    //3.以任何形式进入批准状态需要
    //其实没必要这么执着，直接利用act的status信息就行，节点编号和命名规范就可以了
    //但是我非要把他同步到表单里
    public void taskCreateScript(Map<String, Object> vars) {
        Map<String, Object> _vars = vars;
        JSONObject ccbgJsonObject = ccbgService.getCcbgDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        ccbgJsonObject.put("businessStatus", activitiId);
        //如果进入的是编制状态，要将后面两个业务处理人清空
        if (activitiId.equals("editing")) {
            ccbgJsonObject.put("reviewerUserId", "");
            ccbgJsonObject.put("approverUserId", "");
            ccbgJsonObject.put("reviewerUserName", "");
            ccbgJsonObject.put("approverUserName", "");
        }
        ccbgService.updateCcbg(ccbgJsonObject);
    }

    //1.以通过形式结束审核状态需要
    //2.以通过形式结束批准状态需要
    public void taskEndScript(Map<String, Object> vars) {
        JSONObject ccbgJsonObject = ccbgService.getCcbgDetail(vars.get("busKey").toString());
        String activitiId = vars.get("activityId").toString();
        //如果结束的是审核状态，记录审核人
        if (activitiId.equals("reviewing")) {
            ccbgJsonObject.put("reviewerUserId", ContextUtil.getCurrentUserId());
            ccbgJsonObject.put("reviewerUserName", ContextUtil.getCurrentUser().getFullname());
        }//如果结束的是批准状态，记录批准人和最终状态
        else if (activitiId.equals("approving")) {
            ccbgJsonObject.put("approverUserId", ContextUtil.getCurrentUserId());
            ccbgJsonObject.put("approverUserName", ContextUtil.getCurrentUser().getFullname());
            ccbgJsonObject.put("businessStatus", "close");

        }
        ccbgService.updateCcbg(ccbgJsonObject);
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【出差报告审批完成通知】: ");
        stringBuilder.append(ccbgJsonObject.getString("FULLNAME_"));
        stringBuilder.append(" 的出差报告审批完成").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(WebAppUtil.getProperty("cxyProjectLookUserNo", ""), noticeObj);
    }
}
