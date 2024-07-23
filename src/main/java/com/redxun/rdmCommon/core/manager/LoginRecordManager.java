package com.redxun.rdmCommon.core.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonResult;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.StandardManagerUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2020 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2020/7/10 16:43
 */
@Service
public class LoginRecordManager {
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    // 如果用户对应的部门是徐工挖机、信息部，不做限制。其他的按照职级
    public JsonResult judgeNetAllowLogin(OsUser user, HttpServletRequest request) {
        JsonResult result = new JsonResult(true, "");
        if (user == null || StringUtils.isBlank(user.getUserId())) {
            result.setSuccess(false);
            result.setMessage("该账号不存在！");
            return result;
        }
        // 查找用户的部门、职级信息
        Map<String, Object> userInfos = new HashMap<>();
        xcmgProjectManager.getUserInfoById(user.getUserId(), userInfos);
        if (userInfos == null || userInfos.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("该账号不存在！");
            return result;
        }
        // 对于标准管理平台sim，不做登录限制
        String webappName = WebAppUtil.getProperty("webappName", "rdm");
        if (!"rdm".equalsIgnoreCase(webappName)) {
            return result;
        }
        String openOfficeConfig = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("produce".equals(openOfficeConfig)) {
            // rdm系统白名单
            String crossNetWhiteUser = SysPropertiesUtil.getGlobalProperty("crossNetWhiteUser");
            if (StringUtils.isNotBlank(crossNetWhiteUser)) {
                List<String> crossNetWhiteUserList = Arrays.asList(crossNetWhiteUser.split(",", -1));
                if (crossNetWhiteUserList.contains(user.getUserNo())) {
                    return result;
                }
            }
            // 特殊部门
            String deptId = userInfos.get("mainDepId").toString();
            String deptName = userInfos.get("mainDepName").toString();
            if (deptName.equalsIgnoreCase(RdmConst.XXHGLB_NAME) || deptName.equalsIgnoreCase("徐工挖机")) {
                return result;
            }
            // IP白名单
            String crossNetWhiteIp = SysPropertiesUtil.getGlobalProperty("crossNetWhiteIp");
            if (StringUtils.isNotBlank(crossNetWhiteIp)) {
                String clientIp = StandardManagerUtil.getIpAddr(request);
                List<String> crossNetWhiteIpList = Arrays.asList(crossNetWhiteIp.split(",", -1));
                if (crossNetWhiteIpList.contains(clientIp)) {
                    return result;
                }
            }
            String zjName = "管理";

            if (userInfos.get("ZJ") != null && StringUtils.isNotBlank(userInfos.get("ZJ").toString())) {
                zjName = userInfos.get("ZJ").toString();
            }
            if (!zjName.contains("管理")) {
                // 技术人员只允许在技术网登陆
                if (StandardManagerUtil.judgeGLNetwork(request)) {
                    result.setSuccess(false);
                    result.setMessage("该账号不允许在管理网环境下登陆，请使用技术网！");
                }
            } else {
                // 管理人员只允许在管理网登录
                if (!StandardManagerUtil.judgeGLNetwork(request)) {
                    result.setSuccess(false);
                    result.setMessage("该账号不允许在技术网环境下登陆，请使用管理网！");
                }
            }
        }
        return result;
    }

    // 判断当前部门是否是挖掘机械研究院下的部门，并返回部门Id和name;包括挖掘机械研究院本身，2021-8-25
    public JSONObject judgeIsJSZX(String deptId, String deptName) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(deptId)) {
            deptId = ContextUtil.getCurrentUser().getMainGroupId();
        }
        if (StringUtils.isBlank(deptName)) {
            deptName = ContextUtil.getCurrentUser().getMainGroupName();
        }

        if (deptName.contains(RdmConst.JSZX_NAME)) {
            result.put("isJSZX", true);
            result.put("depId", deptId);
            result.put("depName", deptName);
            return result;
        }

        Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
        boolean isJSZX = false;
        if (deptId2Name != null && deptId2Name.containsKey(deptId)) {
            isJSZX = true;
        }
        result.put("isJSZX", isJSZX);
        result.put("depId", deptId);
        result.put("depName", deptName);
        return result;
    }

    // 判断当前部门是否是标准认证所，并返回部门Id和name
    public JSONObject isBzrzs(String deptId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(deptId)) {
            deptId = ContextUtil.getCurrentUser().getMainGroupId();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupId", deptId);
        List<Map<String, String>> depParentInfos = xcmgProjectOtherDao.queryDepParentById(params);
        result.put("isBzrzs", depParentInfos.get(0).get("NAME_").equalsIgnoreCase(RdmConst.BZJSS_NAME));
        return result;
    }

    // 判断当前部门是否是营销公司市场部，并返回部门Id和name
    public JSONObject judgeIsYXSCB(String deptId) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(deptId)) {
            deptId = ContextUtil.getCurrentUser().getMainGroupId();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupId", deptId);
        List<Map<String, String>> depParentInfos = xcmgProjectOtherDao.queryDepParentById(params);
        String parentName = depParentInfos.get(0).get("parentName");
        result.put("isYXSCB", parentName.contains("营销公司"));
        result.put("depId", deptId);
        result.put("depName", depParentInfos.get(0).get("NAME_").contains("市场部"));
        return result;
    }
}
