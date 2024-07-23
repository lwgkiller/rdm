package com.redxun.rdmCommon.core.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.PropertiesUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import org.apache.commons.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.EncryptUtil;
import com.redxun.rdmCommon.core.dao.RdmDao;
import com.redxun.saweb.util.IdUtil;

import groovy.util.logging.Slf4j;

/**
 * @author zhangzhen
 */
@Service
@Slf4j
public class RdmManager {
    private static Logger logger = LoggerFactory.getLogger(RdmManager.class);
    @Resource
    private RdmDao rdmDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private ImportUserManager importUserManager;

    public JSONObject newUserSave(JSONObject requestObject) throws DecoderException {
        JSONObject result = new JSONObject();
        result.put("success", true);
        // 检查账号是否重复
        boolean checkResult = checkUserNoNew(requestObject.getString("userNo"));
        if (!checkResult) {
            result.put("success", false);
            result.put("message", "账号已存在，提交失败！");
            return result;
        }
        requestObject.put("id", IdUtil.getId());
        requestObject.put("pwd", EncryptUtil.hexToBase64(requestObject.getString("pwd")));
        requestObject.put("confirmStatus", "no");
        rdmDao.insertNewUser(requestObject);
        // 发通知
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【RDM新用户注册通知】:");
        stringBuilder.append(requestObject.getString("mainDepName")).append("，")
                .append(requestObject.getString("fullname"));
        stringBuilder.append("，请确认！");
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon("1,161416982793249480", noticeObj);
        return result;
    }

    public JSONObject newUserConfirm(JSONObject requestObject) {
        JSONObject result = new JSONObject();
        result.put("success", true);
        // 创建账号
        JSONArray ids = requestObject.getJSONArray("ids");
        if (ids == null || ids.isEmpty()) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("ids", ids);
        params.put("confirmStatus", "no");
        List<JSONObject> userInfos = rdmDao.queryNewUser(params);
        if (userInfos.size() == 0) {
            return result;
        }
        // 查询岗位和职级的维度
        params.clear();
        List<JSONObject> dimensionInfos = rdmDao.queryOsDimension(params);
        String gwDimensionId = "";
        String zjDimensionId = "";
        for (JSONObject oneDimension : dimensionInfos) {
            if (oneDimension.getString("NAME_").equalsIgnoreCase("岗位")) {
                gwDimensionId = oneDimension.getString("DIM_ID_");
            }
            if (oneDimension.getString("NAME_").equalsIgnoreCase("职级")) {
                zjDimensionId = oneDimension.getString("DIM_ID_");
            }
        }

        for (int index = 0; index < userInfos.size(); index++) {
            JSONObject oneUser = userInfos.get(index);
            Map<String, Object> objBody = new HashMap<>();
            objBody.put("userName", oneUser.getString("fullname"));
            objBody.put("userNo", oneUser.getString("userNo"));
            objBody.put("pwd", oneUser.getString("pwd"));
            objBody.put("deptName", oneUser.getString("mainDepName"));
            objBody.put("groupId", oneUser.getString("mainDepId"));
            objBody.put("telephone", oneUser.getString("mobile"));
            objBody.put("CERT_NO_", oneUser.getString("certNo"));
            objBody.put("pdmUserNo", oneUser.getString("windchillPDM"));
            objBody.put("postId", oneUser.getString("gwId"));
            objBody.put("postDimId", gwDimensionId);
            objBody.put("dutyId", StringUtil.isNotEmpty(oneUser.getString("zjId")) ? oneUser.getString("zjId") : "87212403321741351");
            objBody.put("dutyDimId", zjDimensionId);
            objBody.put("szrUserId", oneUser.getString("szrUserId"));
            objBody.put("szrUserName", oneUser.getString("szrUserName"));
            importUserManager.saveOtherInfo(objBody);
        }
        // 更新状态
        params.put("ids", ids);
        rdmDao.updateConfirmStatus(params);
        return result;
    }

    public JsonResult deleteUser(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> userIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("userIds", userIds);
        rdmDao.deleteUser(param);
        return result;
    }

    private boolean checkUserNoNew(String userNo) {
        JSONObject param = new JSONObject();
        param.put("userNo", userNo);
        List<JSONObject> usersExist = rdmDao.queryUserByNo(param);
        List<JSONObject> usersNew = rdmDao.queryNewUser(param);
        if ((usersExist == null || usersExist.isEmpty()) && (usersNew == null || usersNew.isEmpty())) {
            return true;
        }
        return false;
    }

    public List<JSONObject> newUserList(HttpServletRequest request) {
        JSONObject params = new JSONObject();
        return rdmDao.queryNewUser(params);
    }

    //..获取表行数
    public JsonPageResult<?> getCountTableRows(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        List<JSONObject> demandList = rdmDao.selectCountTableRows(params);
        result.setData(demandList);
        return result;
    }

    //..刷新表行数
    public JsonResult doRefreshCountTableRows() throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        rdmDao.deleteCountTableRows();
        String dataBaseName = SysPropertiesUtil.getGlobalProperty("dataBaseName");
        rdmDao.callCountTableRows(dataBaseName);
        return result;
    }
}
