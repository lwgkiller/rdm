package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.dao.ImportUserDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.YfjbBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.YfjbMonthDataDao;
import com.redxun.rdmZhgl.core.dao.YfjbMonthProductionsDao;
import com.redxun.rdmZhgl.core.dao.YfjbProductCostDao;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.util.CommonExcelUtils;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import javafx.scene.Parent;
import org.activiti.engine.runtime.Execution;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author zhangzhen
 */
@Service
public class YfjbBaseInfoService {
    private static final Logger logger = LoggerFactory.getLogger(YfjbBaseInfoService.class);
    @Resource
    YfjbBaseInfoDao yfjbBaseInfoDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private YfjbProductCostDao yfjbProductCostDao;
    @Resource
    private ImportUserDao importUserDao;
    @Resource
    private YfjbMonthProductionsDao yfjbMonthProductionsDao;
    @Resource
    private YfjbMonthDataDao yfjbMonthDataDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("noticeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }

            }
        }
    }

    public static Map<String, Object> getSearchParam(Map<String, Object> params, HttpServletRequest request,
        boolean doPage) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (doPage) {
            params.put("currentIndex", Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("pageSize", pageSize);
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }

        String filterParams = request.getParameter("filterSearch");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if (name.endsWith("_startTime")) {
                        value = DateUtil.formatDate(DateUtil.parseDate(value));
                    }
                    if (name.endsWith("_endTime")) {
                        value = DateUtil.formatDate(DateUtil.parseDate(value));
                    }
                    params.put(name, value);
                }
            }
        }
        return params;
    }

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
            JSONObject leaderJson = commonInfoManager.hasPermission("YFJB-FGZR");
            JSONObject manageGroup = commonInfoManager.hasPermission("YFJB-GLXZ");
            JSONObject jbzyJson = commonInfoManager.hasPermission("JBZY");
            if (StringUtil.isEmpty(deptId)) {
                if (resultJson.getBoolean("YFJB-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || manageGroup.getBoolean("YFJB-GLXZ") || jbzyJson.getBoolean("JBZY")
                    || leaderJson.getBoolean("YFJB-FGZR") || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                } else {
                    String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    params.put("deptId", currentDeptId);
                    params.put("userId", ContextUtil.getCurrentUserId());
                }
            }
            list = yfjbBaseInfoDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            if (StringUtil.isEmpty(deptId)) {
                if (resultJson.getBoolean("YFJB-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || manageGroup.getBoolean("YFJB-GLXZ") || jbzyJson.getBoolean("JBZY")
                    || leaderJson.getBoolean("YFJB-FGZR") || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                } else {
                    String currentDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    params.put("deptId", currentDeptId);
                    params.put("userId", ContextUtil.getCurrentUserId());
                }
            }
            totalList = yfjbBaseInfoDao.query(params);
            convertDate(list);
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "研发降本项目";
        Map<String, Object> params = new HashMap<>(16);
        params = getSearchParam(params, request, false);
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        if (StringUtil.isNotEmpty(deptId)) {
            params.put("deptId", deptId);
        }
        List<Map<String, Object>> list = yfjbBaseInfoDao.query(params);
        convertDate(list);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> statusMap = commonInfoManager.genMap("YFJB-XMZT");
        Map<String, Object> costTypeMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> processMap = commonInfoManager.genMap("YFJB-JDZT");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        Map<String, Object> processTypeMap = commonInfoManager.genMap("YFJB-JDLB");
        for (Map<String, Object> map : list) {
            if (map.get("infoStatus") != null) {
                map.put("infoStatusText", statusMap.get(map.get("infoStatus")));
            }
            if (map.get("isNewProcess") != null) {
                map.put("isNewProcessText", sfMap.get(map.get("isNewProcess")));
            }
            if (map.get("costType") != null) {
                map.put("costTypeText", costTypeMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("processStatus") != null) {
                map.put("processStatusText", processMap.get(map.get("processStatus")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
            if (map.get("type") != null) {
                map.put("typeText", processTypeMap.get(map.get("type")));
            }
        }
        String title = "研发降本项目";
        String[] fieldNames = {"销售型号", "设计型号", "项目状态", "是否填写最新进度", "原物料编码", "原物料名称", "原供应商", "降本方式", "降本措施", "替代物料编码",
            "替代物料名称", "新供应商", "差额", "单台用量", "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "计划试制时间", "实际试制时间",
            "计划下发切换通知单时间", "实际下发切换通知单时间", "计划切换时间", "实际切换时间", "所属部门", "所属专业", "责任人", "进度年月", "进度类型", "进度状态", "进度内容"};
        String[] fieldCodes = {"saleModel", "designModel", "infoStatusText", "isNewProcessText", "orgItemCode",
            "orgItemName", "orgSupplier", "costTypeText", "costMeasure", "newItemCode", "newItemName", "newSupplier",
            "differentPrice", "perSum", "replaceRate", "perCost", "achieveCost", "risk", "isReplaceText", "jhsz_date",
            "sjsz_date", "jhxfqh_date", "sjxfqh_date", "jhqh_date", "sjqh_date", "deptName", "majorText", "responseMan",
            "yearMonth", "typeText", "processStatusText", "processContent"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public List<Map<String, Object>> getInfoListByMainId(HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            list = yfjbBaseInfoDao.getInfoListByMainId(mainId);
            convertDate(list);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return list;
    }

    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.endsWith("_date")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            if("0".equals(CommonFuns.nullToString(objBody.get("isSz")))){
                objBody.put("jhsz_date", "无");
            }
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id", id);
            yfjbBaseInfoDao.addObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            // 如果是已提交状态，则判断哪些字段做了调整，然后发送通知
            String id = request.getParameter("id");
            JSONObject infoObj = yfjbBaseInfoDao.getObjectById(id);
            Boolean flag = false;
            if ("2".equals(infoObj.getString("infoStatus"))) {
                flag = true;
            }
            List<String> changeList = new ArrayList<>();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.endsWith("_date")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                if (flag) {
                    if (!CommonFuns.nullToString(infoObj.getString(mapKey)).equals(CommonFuns.nullToString(mapValue))) {
                        changeList.add(mapKey);
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            if (changeList.size() > 0) {
                sendDingMsg(objBody.get("id").toString(), changeList);
            }
            if("0".equals(CommonFuns.nullToString(objBody.get("isSz")))){
                objBody.put("jhsz_date", "无");
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            yfjbBaseInfoDao.updateObject(objBody);
            resultJson.put("id", objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JsonPageResult<?> processList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            String mainId = request.getParameter("mainId");
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("mainId", mainId);
            list = yfjbBaseInfoDao.processList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("mainId", mainId);
            totalList = yfjbBaseInfoDao.processList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public JsonPageResult<?> getMemberList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            String mainId = request.getParameter("mainId");
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("mainId", mainId);
            list = yfjbBaseInfoDao.getMemberList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("mainId", mainId);
            totalList = yfjbBaseInfoDao.getMemberList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public void saveOrUpdateItem(String changeGridDataStr, String mainId) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if (oneObject.get("yearMonth") != null) {
                    String yearMonth = oneObject.get("yearMonth").toString().substring(0, 7);
                    oneObject.put("yearMonth", yearMonth);
                }
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    oneObject.put("mainId", mainId);
                    // 根据负责人id 获取对应部门
                    yfjbBaseInfoDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    yfjbBaseInfoDao.updateItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    yfjbBaseInfoDao.delItem(oneObject.getString("id"));
                }
                dealProcessStatus(oneObject, state);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    public void dealProcessStatus(JSONObject obj, String action) {
        try {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DATE);
            String yearMonth = CommonFuns.genYearMonth("cur");
            String type = "";
            if (day < 20) {
                type = "1";
            } else {
                type = "2";
            }
            JSONObject paramJson = new JSONObject();
            paramJson.put("id", obj.getString("mainId"));
            if ("added".equals(action) || "modified".equals(action)) {
                if (yearMonth.equals(obj.getString("yearMonth")) && type.equals(obj.getString("type"))) {
                    // 更新状态
                    paramJson.put("isNewProcess", "1");
                } else {
                    paramJson.put("isNewProcess", "0");
                }
            }
            yfjbBaseInfoDao.updateIsNewProcess(paramJson);
        } catch (Exception e) {
            logger.error("dealProcessStatus", e);
        }
    }

    public void saveOrUpdateMember(String changeGridDataStr, String mainId) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    oneObject.put("mainId", mainId);
                    // 根据负责人id 获取对应部门
                    yfjbBaseInfoDao.addMember(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    yfjbBaseInfoDao.updateMember(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    yfjbBaseInfoDao.delMember(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            yfjbBaseInfoDao.batchDelete(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除降本项目", e);
            return ResultUtil.result(false, "删除降本项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = yfjbBaseInfoDao.getObjectById(id);
        return jsonObject;
    }

    public JSONObject copy(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            List<Map<String, Object>> list = yfjbBaseInfoDao.getBaseInfoListByIds(params);
            for (Map<String, Object> map : list) {
                map.put("id", IdUtil.getId());
                yfjbBaseInfoDao.addObject(map);
            }
        } catch (Exception e) {
            logger.error("Exception in update 复制项目信息", e);
            return ResultUtil.result(false, "复制项目信息异常！", "");
        }
        return ResultUtil.result(true, "复制成功", resultJson);
    }

    public JSONObject submit(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            yfjbBaseInfoDao.infoSubmit(params);
        } catch (Exception e) {
            logger.error("Exception in update 提交项目信息", e);
            return ResultUtil.result(false, "复提交项目信息异常！", "");
        }
        return ResultUtil.result(true, "提交成功", resultJson);
    }

    /** 过滤掉废止和草稿状态的数据 */
    public void filterData(List<Map<String, Object>> list) {
        String userId = ContextUtil.getCurrentUserId();
        Iterator<Map<String, Object>> it = list.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            String creator = CommonFuns.nullToString(map.get("CREATE_BY_"));
            if (!userId.equals(creator)) {
                if ("3".equals(CommonFuns.nullToString(map.get("infoStatus")))
                    || "1".equals(CommonFuns.nullToString(map.get("infoStatus")))) {
                    it.remove();
                }
            }
        }
    }

    /** 发送钉钉消息 */
    public void sendDingMsg(String mainId, List<String> list) {
        // 发送钉钉通知
        // 获取创建人所在部门id
        // 获取本部门降本专员信息
        JSONObject yfjbObj = yfjbBaseInfoDao.getObjectById(mainId);
        JSONObject paramJson = new JSONObject();
        paramJson.put("deptId", yfjbObj.getString("deptId"));
        paramJson.put("roleKey", "JBZY");
        List<JSONObject> userList = commonInfoDao.getDeptRoleUsers(paramJson);
        StringBuffer userIds = new StringBuffer();
        for (JSONObject temp : userList) {
            userIds.append(temp.getString("PARTY2_")).append(",");
        }
        paramJson = new JSONObject();
        paramJson.put("dim", "2");
        paramJson.put("key", "YFJB-GLY");
        List<JSONObject> groupUserList = commonInfoDao.getGroupUsers(paramJson);
        for (JSONObject temp : groupUserList) {
            userIds.append(temp.getString("PARTY2_")).append(",");
        }
        // 拼接改变字符串
        Map<String, Object> baseInfoMap = commonInfoManager.genMap("YFJB-JBXX");
        StringBuffer changeStr = new StringBuffer();
        for (String temp : list) {
            changeStr.append(baseInfoMap.get(temp)).append(",");
        }
        JSONObject noticeObj = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("【研发降本】");
        stringBuilder.append("，销售型号：").append(yfjbObj.getString("saleModel")).append("的降本项目，字段：").append(changeStr)
            .append("有修改，请知悉");
        stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
        noticeObj.put("content", stringBuilder.toString());
        sendDDNoticeManager.sendNoticeForCommon(userIds.toString(), noticeObj);
    }

    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        String reportType = CommonFuns.nullToString(params.get("reportType"));
        switch (reportType) {
            case "jxwdbb":
                String modelType = CommonFuns.nullToString(params.get("modelType"));
                if ("1".equals(modelType)) {
                    exportModelExcel(response, params);
                } else if ("2".equals(modelType)) {
                    exportModelTotalExcel(response, params);
                }
                break;
            case "wlwdbb":
                String costType = CommonFuns.nullToString(params.get("costCategory"));
                String reportRule = CommonFuns.nullToString(params.get("reportRule"));
                if ("zjjb".equals(costType)) {
                    // 直接降本
                    if ("old".equals(reportRule)) {
                        exportZjjbExcel(response, params);
                    } else if ("new".equals(reportRule)) {
                        exportNewZjjbExcel(response, params);
                    }
                } else if ("hzb".equals(costType)) {
                    // 汇总表
                    if ("old".equals(reportRule)) {
                        exportCostTotalExcel(response, params);
                    } else if ("new".equals(reportRule)) {
                        exportNewCostTotalExcel(response, params);
                    }
                } else {
                    // 物料替代、物料取消表
                    if ("old".equals(reportRule)) {
                        exportCostCategoryExcel(response, params);
                    } else if ("new".equals(reportRule)) {
                        exportNewCostCategoryExcel(response, params);
                    }
                }
                break;
            case "jhqhxm":
                exportPlanChangeExcel(response, params);
                break;
            case "sjqhxm":
                exportActualChangeExcel(response, params);
                break;
            case "jhxfqhtzdxm":
                exportPlanChangeNoticeExcel(response, params);
                break;
            case "sjxfqhtzdxm":
                exportActualChangeNoticeExcel(response, params);
                break;
            case "jhxfsztzdxm":
                exportPlanProduceNoticeExcel(response, params);
                break;
            case "sjxfsztzdxm":
                exportActualProduceNoticeExcel(response, params);
                break;
            case "jbxmxfqhtzdsjwqhtj":
                exportUnChangeExcel(response, params);
                break;
            case "jbzxlbjszjhb":
                exportJbzxExcel(response, params);
                break;
            case "qnjbycysjjbdb":
                exportPlanActualExcel(response, params);
                break;
        }
    }

    /**
     * 导出项目维度表格-物料代替，物料取消
     */
    public void exportCostCategoryExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度表格";
            String costCategory = CommonFuns.nullToString(params.get("costCategory"));
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            //调整
            String reportStartYearMonth = reportYearInt - 1+"-12";
            String reportEndYearMonth = reportYearInt+"-10";

            params.put("reportStartYearMonth", reportStartYearMonth);
            params.put("reportEndYearMonth", reportEndYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "年项目维度表格-" + getCostCategoryText(costCategory);
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            convertDate(list);
            Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
            String switchDate = "";
            String designModel = "";
            Float perCost;
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                if (map.get("isReplace") != null) {
                    map.put("isReplaceText", sfMap.get(map.get("isReplace")));
                }
                designModel = CommonFuns.nullToString(map.get("designModel"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                perCost = Float.parseFloat(CommonFuns.nullToZeroString(map.get("perCost")));
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    boolean isCurrentYear = false;
                    boolean isLastYear = false;
                    boolean isPreYear = false;
                    if (year == reportYearInt) {
                        isCurrentYear = true;
                    } else if (year == reportYearInt - 1) {
                        isLastYear = true;
                    } else if (year == reportYearInt - 2) {
                        isPreYear = true;
                    }
                    if (obj == null) {
                        continue;
                    }
                    for (int i = 1; i <= 12; i++) {
                        map.put(getMonth(i) + "Production",
                            CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i))));
                        if ((isCurrentYear)) {
                            if (i >= month) {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), totalMoney);
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else if (isLastYear) {
                            if (i < month) {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), totalMoney);
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else if (isPreYear) {
                            if(i==1){
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), totalMoney);
                            }
                        }
                    }
                }
            }
            String[] fieldNames = {"原物料编码", "原物料名称", "原物料价格", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量", "代替比例(%)",
                "单台降本", "整机物料号", "整机型号", "生产是否切换", "切换日期", "责任人", "备注", "1月产量", "2月产量", "3月产量", "4月产量", "5月产量", "6月产量",
                "7月产量", "8月产量", "9月产量", "10月产量", "11月产量", "12月产量", "一月预计降本额", "二月预计降本额", "三月预计降本额", "四月预计降本额",
                "五月预计降本额", "六月预计降本额", "七月预计降本额", "八月预计降本额", "九月预计降本额", "十月预计降本额", "十一月预计降本额", "十二月预计降本额"};
            String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "newItemCode", "newItemName",
                "newItemPrice", "differentPrice_float", "perSum", "replaceRate", "perCost_float", "", "designModel",
                "isReplaceText", "sjqh_date", "responseMan", "remark", "januaryProduction_float",
                "februaryProduction_float", "marchProduction_float", "aprilProduction_float", "mayProduction_float",
                "juneProduction_float", "julyProduction_float", "augustProduction_float", "septemberProduction_float",
                "octoberProduction_float", "novemberProduction_float", "decemberProduction_float", "january_float",
                "february_float", "march_float", "april_float", "may_float", "june_float", "july_float", "august_float",
                "september_float", "october_float", "november_float", "december_float"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格", e);
        }
    }

    /**
     * 导出项目维度表格-物料代替，物料取消
     */
    public void exportNewCostCategoryExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度表格";
            String costCategory = CommonFuns.nullToString(params.get("costCategory"));
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            int lastYear = reportYearInt - 1;
            String reportYearMonth = reportYearInt + "-01";
            params.put("reportYearMonth", reportYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "年项目维度表格-" + getCostCategoryText(costCategory);
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            convertDate(list);
            Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
            String switchDate = "";
            String designModel = "";
            Float perCost;
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                if (map.get("isReplace") != null) {
                    map.put("isReplaceText", sfMap.get(map.get("isReplace")));
                }
                designModel = CommonFuns.nullToString(map.get("designModel"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                float basePrice = CommonFuns.nullToStringToZero(map.get("basePrice"));
                float newItemPrice = CommonFuns.nullToStringToZero(map.get("newItemPrice"));
                perCost = basePrice - newItemPrice;
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    boolean isReportYear = true;
                    if (year < reportYearInt) {
                        isReportYear = false;
                    }
                    if (obj == null) {
                        continue;
                    }
                    for (int i = 1; i <= 12; i++) {
                        map.put(getMonth(i) + "Production", CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i))));
                        if (isReportYear) {
                            if (i >= month) {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), totalMoney);
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else {
                            Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                            map.put(getMonth(i), totalMoney);
                        }
                    }
                }
            }
            String[] fieldNames = {"原物料编码", "原物料名称", "原物料价格", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量", "代替比例(%)",
                "单台降本", "整机物料号", "整机型号", "生产是否切换", "切换日期", "责任人", "备注", "单台降本", "1月产量", "2月产量", "3月产量", "4月产量", "5月产量",
                "6月产量", "7月产量", "8月产量", "9月产量", "10月产量", "11月产量", "12月产量", "一月预计降本额", "二月预计降本额", "三月预计降本额", "四月预计降本额",
                "五月预计降本额", "六月预计降本额", "七月预计降本额", "八月预计降本额", "九月预计降本额", "十月预计降本额", "十一月预计降本额", "十二月预计降本额"};
            String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "newItemCode", "newItemName",
                "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost", "", "designModel",
                "isReplaceText", "sjqh_date", "responseMan", "remark", "perCost", "januaryProduction_float",
                "februaryProduction_float", "marchProduction_float", "aprilProduction_float", "mayProduction_float",
                "juneProduction_float", "julyProduction_float", "augustProduction_float", "septemberProduction_float",
                "octoberProduction_float", "novemberProduction_float", "decemberProduction_float", "january_float",
                "february_float", "march_float", "april_float", "may_float", "june_float", "july_float", "august_float",
                "september_float", "october_float", "november_float", "december_float"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格", e);
        }
    }

    /**
     * 导出降本增效零部件变更汇总表
     */
    public void exportJbzxExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "降本增效零部件变更汇总表";
            String reportYearMonth = CommonFuns.nullToString(params.get("reportYearMonth"));
            String title = reportYearMonth.replace("-", "年") + "月降本增效零部件变更汇总表";
            List<Map<String, Object>> list = yfjbBaseInfoDao.getJbzxList(params);
            convertDate(list);
            Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
            for (Map<String, Object> map : list) {
                if (map.get("major") != null) {
                    map.put("majorText", majorMap.get(map.get("major")));
                }
                String noticeNoAndDate =
                    CommonFuns.nullToString(map.get("noticeNo")) + "/" + CommonFuns.nullToString(map.get("noticeDate"));
                map.put("noticeNoAndDate", noticeNoAndDate);
            }
            String[] fieldNames1 =
                {"机型", "负责部门", "分类", "更改前", "更改前", "更改前", "更改后", "更改后", "更改后", "互换性", "主要差异性、试制要求、竞品使用情况（以此依据进行三级评价）",
                    "通知单单号/通知单预计下发时间", "评价方法", "切换条件", "质量评估报告回复时间", "质量评估报告回复时间", "质量评估报告回复时间"};
            String[] fieldNames2 = {"机型", "负责部门", "分类", "物料号", "图号+规格型号+零件名", "供应商", "物料号", "图号+规格型号+零件名", "供应商", "互换性",
                "主要差异性、试制要求、竞品使用情况（以此依据进行三级评价）", "通知单单号/通知单预计下发时间", "评价方法", "切换条件", "技术中心", "供方发展部", "装配厂"};
            String[] fieldCodes = {"saleModel", "deptName", "majorText", "orgItemCode", "orgItemName", "orgSupplier",
                "newItemCode", "newItemName", "newSupplier", "changeable", "assessment", "noticeNoAndDate", "pjff",
                "qhtj", "jszx", "fgfzb", "zpc",};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportJbzxExcel(list, fieldNames1, fieldNames2, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格", e);
        }
    }

    /**
     * 导出项目维度-直接降本 统计规则： 1.如统计2021年的降本 2020年7月份切换的项目，2021年从1月统计到6月 2.2021年切换的项目 7月份切换的，要算9月份的降本额，延期两个月
     */
    public void exportZjjbExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度表格";
            String costCategory = CommonFuns.nullToString(params.get("costCategory"));
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            String reportStartYearMonth = reportYearInt - 1 + "-12";
            String reportEndYearMonth = reportYearInt + "-10";
            params.put("reportStartYearMonth", reportStartYearMonth);
            params.put("reportEndYearMonth", reportEndYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "年项目维度表格-" + getCostCategoryText(costCategory);
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            List<Map<String, Object>> list = yfjbBaseInfoDao.getZjjbList(params);
            Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
            convertDate(list);
            String switchDate = "";
            String designModel = "";
            Float perCost;
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                if (map.get("isReplace") != null) {
                    map.put("isReplaceText", sfMap.get(map.get("isReplace")));
                }
                designModel = CommonFuns.nullToString(map.get("designModel"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                perCost = Float.parseFloat(CommonFuns.nullToZeroString(map.get("perCost")));
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    boolean isCurrentYear = false;
                    boolean isLastYear = false;
                    boolean isPreYear = false;
                    if (year == reportYearInt) {
                        isCurrentYear = true;
                    } else if (year == reportYearInt - 1) {
                        isLastYear = true;
                    } else if (year == reportYearInt - 2) {
                        isPreYear = true;
                    }
                    if (obj == null) {
                        continue;
                    }
                    for (int i = 1; i <= 12; i++) {
                        map.put(getMonth(i) + "Production",
                            CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i))));
                        if (isCurrentYear) {
                            if (i >= month + 2) {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), new DecimalFormat("#.##").format(totalMoney));
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else if (isLastYear) {
                            if (i < month + 2) {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), new DecimalFormat("#.##").format(totalMoney));
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else if (isPreYear) {
                            if (i == 1) {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), new DecimalFormat("#.##").format(totalMoney));
                            }
                        }
                    }
                }
            }
            String[] fieldNames = {"供应商代码", "供应商名称", "物料号", "物料描述", "评估类", "物料加供应商", "财务审核原行价格", "财务审核现行价格", "财务核算差异",
                "整机物料号", "整机型号", "单台用量", "备注", "执行日期", "负责人", "通知单下发日期", "现在状态", "1月产量", "2月产量", "3月产量", "4月产量", "5月产量",
                "6月产量", "7月产量", "8月产量", "9月产量", "10月产量", "11月产量", "12月产量", "一月预计降本额", "二月预计降本额", "三月预计降本额", "四月预计降本额",
                "五月预计降本额", "六月预计降本额", "七月预计降本额", "八月预计降本额", "九月预计降本额", "十月预计降本额", "十一月预计降本额", "十二月预计降本额"};
            String[] fieldCodes = {"supperCode", "supperName", "orgItemCode", "orgItemName", "evaluate", "itemSupper",
                "orgItemPrice", "newItemPrice", "differentPrice_float", "itemCode", "designModel", "perSum", "remark",
                "jbfazsss_date", "responseMan", "sjxfqh_date", "isReplaceText", "januaryProduction_float",
                "februaryProduction_float", "marchProduction_float", "aprilProduction_float", "mayProduction_float",
                "juneProduction_float", "julyProduction_float", "augustProduction_float", "septemberProduction_float",
                "octoberProduction_float", "novemberProduction_float", "decemberProduction_float", "january_float",
                "february_float", "march_float", "april_float", "may_float", "june_float", "july_float", "august_float",
                "september_float", "october_float", "november_float", "december_float"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格-直接降本", e);
        }
    }

    /**
     * 导出项目维度-直接降本 统计规则： 2020年1月份切换项目2021年报表降本额应该为“0”，2020年2-12月份切换项目，原物料价格应为“基准价格”并统计全年；基准价格在基本信息维护时填写。
     */
    public void exportNewZjjbExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度表格";
            String costCategory = CommonFuns.nullToString(params.get("costCategory"));
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            int lastYear = reportYearInt - 1;
            //2023-03-29 徐鹏改造 要求只算今年的
            String reportYearMonth = reportYearInt + "-01";
            params.put("reportYearMonth", reportYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "年项目维度表格-" + getCostCategoryText(costCategory);
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            List<Map<String, Object>> list = yfjbBaseInfoDao.getZjjbList(params);
            Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
            convertDate(list);
            String switchDate = "";
            String designModel = "";
            Double perCost;
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                if (map.get("isReplace") != null) {
                    map.put("isReplaceText", sfMap.get(map.get("isReplace")));
                }
                designModel = CommonFuns.nullToString(map.get("designModel"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                Double basePrice = CommonFuns.nullToStringToDoubleZero(map.get("basePrice"));
                Double newItemPrice = CommonFuns.nullToStringToDoubleZero(map.get("newItemPrice"));
                perCost = basePrice - newItemPrice;
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    boolean isReportYear = true;
                    if (year < reportYearInt) {
                        isReportYear = false;
                    }
                    if (obj == null) {
                        continue;
                    }
                    for (int i = 1; i <= 12; i++) {
                        map.put(getMonth(i) + "Production", CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i))));
                        if (isReportYear) {
                            if (i >= month) {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                map.put(getMonth(i), totalMoney);
                            } else {
                                map.put(getMonth(i), 0);
                            }
                        } else {
                            Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                            map.put(getMonth(i), totalMoney);
                        }
                    }
                }
            }
            String[] fieldNames = {"供应商代码", "供应商名称", "物料号", "物料描述", "评估类", "物料加供应商", "财务审核原行价格", "财务审核现行价格", "财务核算差异",
                "整机物料号", "整机型号", "单台用量", "备注", "执行日期", "负责人", "通知单下发日期", "现在状态", "1月产量", "2月产量", "3月产量", "4月产量", "5月产量",
                "6月产量", "7月产量", "8月产量", "9月产量", "10月产量", "11月产量", "12月产量", "一月预计降本额", "二月预计降本额", "三月预计降本额", "四月预计降本额",
                "五月预计降本额", "六月预计降本额", "七月预计降本额", "八月预计降本额", "九月预计降本额", "十月预计降本额", "十一月预计降本额", "十二月预计降本额"};
            String[] fieldCodes = {"supperCode", "supperName", "orgItemCode", "orgItemName", "evaluate", "itemSupper",
                "orgItemPrice", "newItemPrice", "differentPrice_float", "itemCode", "designModel", "perSum", "remark",
                "jbfazsss_date", "responseMan", "sjxfqh_date", "isReplaceText", "januaryProduction_float",
                "februaryProduction_float", "marchProduction_float", "aprilProduction_float", "mayProduction_float",
                "juneProduction_float", "julyProduction_float", "augustProduction_float", "septemberProduction_float",
                "octoberProduction_float", "novemberProduction_float", "decemberProduction_float", "january_float",
                "february_float", "march_float", "april_float", "may_float", "june_float", "july_float", "august_float",
                "september_float", "october_float", "november_float", "december_float"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格-直接降本", e);
        }
    }

    /**
     * 导出项目维度-汇总
     */
    public void exportCostTotalExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度汇总表";
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            int lastYear = reportYearInt - 1;
            String reportYearMonth = reportYearInt + "-01";
            params.put("reportYearMonth", reportYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "全年降本预测";
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }

            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            String switchDate = "";
            String designModel = "";
            String costType = "";
            Float perCost;
            JSONObject itemReplaceJson = new JSONObject();
            JSONObject itemCancelJson = new JSONObject();
            JSONObject itemDirectJson = new JSONObject();
            JSONObject itemOtherJson = new JSONObject();
            JSONObject itemTotalJson = new JSONObject();
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                designModel = CommonFuns.nullToString(map.get("designModel"));
                costType = CommonFuns.nullToString(map.get("costType"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                if (obj == null) {
                    continue;
                }
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                perCost = Float.parseFloat(CommonFuns.nullToZeroString(map.get("perCost")));
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if(switchDate.indexOf(".")>-1){
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    }else if(switchDate.indexOf("-")>-1){
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    }else{
                        continue;
                    }
                    boolean isCurrentYear = false;
                    boolean isLastYear = false;
                    boolean isPreYear = false;
                    if (year == reportYearInt) {
                        isCurrentYear = true;
                    }else if(year == reportYearInt-1){
                        isLastYear = true;
                    }else if(year == reportYearInt-2){
                        isPreYear = true;
                    }
                    if ("1".equals(costType) || "2".equals(costType) || "3".equals(costType) || "6".equals(costType) || "8".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    } else if ("4".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    } else if ("5".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    }
                }
            }
            Double totalChange = 0d;
            Double totalCancel = 0d;
            Double totalDirect = 0d;
            Double totalOther = 0d;
            for (int i = 1; i <= 12; i++) {
                String key = getMonth(i);
                totalChange += itemReplaceJson.getDoubleValue(key);
                totalCancel += itemCancelJson.getDoubleValue(key);
                totalDirect += itemDirectJson.getDoubleValue(key);
                totalOther += itemOtherJson.getDoubleValue(key);
                Double total = itemReplaceJson.getDoubleValue(key) + itemCancelJson.getDoubleValue(key)
                    + itemDirectJson.getDoubleValue(key) + itemOtherJson.getDoubleValue(key);
                itemTotalJson.put(key, total);
            }
            itemReplaceJson.put("title", "物料替代");
            itemCancelJson.put("title", "物料取消");
            itemDirectJson.put("title", "直接降本");
            itemOtherJson.put("title", "其他");
            itemTotalJson.put("title", "总计");
            itemReplaceJson.put("total", totalChange);
            itemCancelJson.put("total", totalCancel);
            itemDirectJson.put("total", totalDirect);
            itemOtherJson.put("total", totalOther);
            itemTotalJson.put("total", totalChange + totalCancel + totalDirect + totalOther);
            String[] fieldNames =
                {"降本方式", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月", "总计"};
            String[] fieldCodes = {"title", "january_float", "february_float", "march_float", "april_float",
                "may_float", "june_float", "july_float", "august_float", "september_float", "october_float",
                "november_float", "december_float", "total_float"};
            List<JSONObject> dataList = new ArrayList<>();
            dataList.add(itemReplaceJson);
            dataList.add(itemCancelJson);
            dataList.add(itemDirectJson);
            dataList.add(itemOtherJson);
            dataList.add(itemTotalJson);
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(dataList, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度-汇总", e);
        }
    }

    /**
     * 导出项目维度-汇总
     */
    public void exportNewCostTotalExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "项目维度汇总表";
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            int reportYearInt = Integer.parseInt(reportYear);
            int lastYear = reportYearInt - 1;
            String reportYearMonth = reportYearInt + "-01";
            params.put("reportYearMonth", reportYearMonth);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String title = reportYear + "全年降本预测";
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            String switchDate = "";
            String designModel = "";
            String costType = "";
            Double perCost;
            JSONObject itemReplaceJson = new JSONObject();
            JSONObject itemCancelJson = new JSONObject();
            JSONObject itemDirectJson = new JSONObject();
            JSONObject itemOtherJson = new JSONObject();
            JSONObject itemTotalJson = new JSONObject();
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                designModel = CommonFuns.nullToString(map.get("designModel"));
                costType = CommonFuns.nullToString(map.get("costType"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                if (obj == null) {
                    obj = new JSONObject();
                }
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if(switchDate.indexOf(".")>-1){
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    }else if(switchDate.indexOf("-")>-1){
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    }else{
                        continue;
                    }
                    boolean isReportYear = true;
                    if (year < reportYearInt) {
                        isReportYear = false;
                    }
                    Double basePrice = CommonFuns.nullToStringToDoubleZero(map.get("basePrice"));
                    Double newItemPrice = CommonFuns.nullToStringToDoubleZero(map.get("newItemPrice"));
                    perCost = basePrice - newItemPrice;
                    if ("1".equals(costType) || "2".equals(costType) || "3".equals(costType) || "6".equals(costType)
                        || "8".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current = CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                itemReplaceJson.put(getMonth(i), totalMoney + current);

                            }
                        }
                    } else if ("4".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemCancelJson.getDoubleValue(getMonth(i)));
                                    itemCancelJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current = CommonFuns.nullToDoubleZero(itemCancelJson.getDoubleValue(getMonth(i)));
                                itemCancelJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    } else if ("5".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemDirectJson.getDoubleValue(getMonth(i)));
                                    itemDirectJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current = CommonFuns.nullToDoubleZero(itemDirectJson.getDoubleValue(getMonth(i)));
                                itemDirectJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    } else {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current = CommonFuns.nullToDoubleZero(itemOtherJson.getDoubleValue(getMonth(i)));
                                    itemOtherJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney = perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current = CommonFuns.nullToDoubleZero(itemOtherJson.getDoubleValue(getMonth(i)));
                                itemOtherJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    }

                }
            }
            Double totalChange = 0d;
            Double totalCancel = 0d;
            Double totalDirect = 0d;
            Double totalOther = 0d;
            for (int i = 1; i <= 12; i++) {
                String key = getMonth(i);
                totalChange += itemReplaceJson.getDoubleValue(key);
                totalCancel += itemCancelJson.getDoubleValue(key);
                totalDirect += itemDirectJson.getDoubleValue(key);
                totalOther += itemOtherJson.getDoubleValue(key);
                Double total = itemReplaceJson.getDoubleValue(key) + itemCancelJson.getDoubleValue(key)
                    + itemDirectJson.getDoubleValue(key) + itemOtherJson.getDoubleValue(key);
                itemTotalJson.put(key, total);
            }
            itemReplaceJson.put("title", "物料替代");
            itemCancelJson.put("title", "物料取消");
            itemDirectJson.put("title", "直接降本");
            itemOtherJson.put("title", "其他");
            itemTotalJson.put("title", "总计");
            itemReplaceJson.put("total", totalChange);
            itemCancelJson.put("total", totalCancel);
            itemDirectJson.put("total", totalDirect);
            itemOtherJson.put("total", totalOther);
            itemTotalJson.put("total", totalChange + totalCancel + totalDirect + totalOther);
            String[] fieldNames =
                {"降本方式", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月", "总计"};
            String[] fieldCodes = {"title", "january_float", "february_float", "march_float", "april_float",
                "may_float", "june_float", "july_float", "august_float", "september_float", "october_float",
                "november_float", "december_float", "total_float"};
            List<JSONObject> dataList = new ArrayList<>();
            dataList.add(itemReplaceJson);
            dataList.add(itemCancelJson);
            dataList.add(itemDirectJson);
            dataList.add(itemOtherJson);
            dataList.add(itemTotalJson);
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(dataList, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度-汇总", e);
        }
    }

    public String getCostCategoryText(String category) {
        String categoryText = "";
        switch (category) {
            case "wltd":
                categoryText = "物料替代";
                break;
            case "wlqx":
                categoryText = "物料取消";
                break;
            case "zjjb":
                categoryText = "直接降本";
                break;
            case "hzb":
                categoryText = "汇总表";
        }
        return categoryText;
    }

    /**
     * 导出计划切换项目列表
     */
    public void exportPlanChangeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "计划切换项目列表";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月计划切换项目列表";
        List<Map<String, Object>> list = yfjbBaseInfoDao.getPlanChangeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost",
            "achieveCost", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出实际切换项目列表
     */
    public void exportActualChangeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "实际切换项目列表";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月实际切换项目列表";
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject leaderJson = commonInfoManager.hasPermission("YFJB-FGZR");
        List<Map<String, Object>> list = yfjbBaseInfoDao.getActualChangeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost",
            "achieveCost", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出计划下发切换通知单项目
     */
    public void exportPlanChangeNoticeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "计划下发切换通知单项目列表";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月计划下发切换通知单项目列表";
        List<Map<String, Object>> list = yfjbBaseInfoDao.getPlanChangeNoticeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型", "切换通知号", "通知单下发时间", "备注"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost",
            "achieveCost", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel", "noticeNo",
            "noticeDate", "remark"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出实际下发切换通知单项目
     */
    public void exportActualChangeNoticeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "实际下发试制通知单项目";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月实际下发试制通知单项目列表";
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject leaderJson = commonInfoManager.hasPermission("YFJB-FGZR");
        List<Map<String, Object>> list = yfjbBaseInfoDao.getActualChangeNoticeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型", "切换通知号", "通知单下发时间", "备注"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost",
            "achieveCost", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel", "noticeNo",
            "noticeDate", "remark"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出计划下发试制通知单项目
     */
    public void exportPlanProduceNoticeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "计划下发试制通知单项目列表";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月计划下发试制通知单项目列表";
        if(StringUtil.isNotEmpty(yearMonth)){
            params.put("yearMonthSlot",yearMonth.replace("-","."));
        }
        List<Map<String, Object>> list = yfjbBaseInfoDao.getPlanProduceNoticeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型", "试制通知号", "通知单下发时间", "备注"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice_float", "perSum", "replaceRate", "perCost_float",
            "achieveCost_float", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel",
            "productionNo", "noticeDate", "remark"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出实际下发试制通知单项目
     */
    public void exportActualProduceNoticeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "实际下发试制通知单项目列表";
        String yearMonth = CommonFuns.nullToString(params.get("yearMonth"));
        String title = yearMonth.replace("-", "年") + "月实际下发试制通知单项目列表";
        if(StringUtil.isNotEmpty(yearMonth)){
            params.put("yearMonthSlot",yearMonth.replace("-","."));
        }
        List<Map<String, Object>> list = yfjbBaseInfoDao.getActualProduceNoticeList(params);
        convertDate(list);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        for (Map<String, Object> map : list) {
            if (map.get("costType") != null) {
                map.put("costTypeText", jbfsMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
        }
        String[] fieldNames = {"物料编码", "物料名称", "原物料价格", "降本方式", "降本措施", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "切换时间", "所属专业", "责任人", "涉及机型", "试制通知号", "通知单下发时间", "备注"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgItemPrice", "costTypeText", "costMeasure",
            "newItemCode", "newItemName", "newItemPrice", "differentPrice_float", "perSum", "replaceRate", "perCost_float",
            "achieveCost_float", "risk", "isReplaceText", "sjqh_date", "majorText", "responseMan", "saleModel",
            "productionNo", "noticeDate", "remark"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出实际下发试制通知单项目
     */
    public void exportUnChangeExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "切换通知单已下发但未切换项目列表";
        String reportYearMonth = CommonFuns.nullToString(params.get("reportYearMonth"));
        String title = reportYearMonth.replace("-", "年") + "月切换通知单已下发但未切换项目列表";
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject leaderJson = commonInfoManager.hasPermission("YFJB-FGZR");
        List<Map<String, Object>> list = yfjbBaseInfoDao.getUnChangeList(params);
        convertDate(list);
        String[] fieldNames = {"原物料编码", "原物料名称", "原供应商", "替代物料编码", "替代物料名称", "替代供应商", "替代比例(%)", "单台降本", "切换通知下发时间",
            "切换通知单号", "通知单中库存及切换车号", "责任人", "涉及机型", "备注", "未切换原因", "剩余库存量", "预计切换车号及时间"};
        String[] fieldCodes = {"orgItemCode", "orgItemName", "orgSupplier", "newItemCode", "newItemName", "newSupplier",
            "replaceRate", "perCost", "noticeDate", "noticeNo", "storageAndCar", "responseMan", "saleModel", "remark"};
        HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出机型维度报表
     */
    public void exportModelExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "机型维度报表";
        String saleModel = CommonFuns.nullToString(params.get("saleModel")).trim();
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        /**
         * 规则说明： （1）如果销售机型为空，统计部门为空，则统计所有数据 （2）如果销售机型为空，统计部门不为空，则统计所选择部门数据 （3）如果销售机型不为空，统计部门为空，则统计所有部门的本销售机型
         * （4）如果销售机型不为空，统计部门不为空，则统计本部门下的本销售机型
         */
        JSONObject paramJson = new JSONObject();
        paramJson.put("saleModel", saleModel.trim());
        JSONObject resultJson = commonInfoManager.hasPermission("YFJB-GLY");
        JSONObject leaderJson = commonInfoManager.hasPermission("YFJB-FGZR");
        if (StringUtil.isNotEmpty(deptId)) {
            params.put("deptId", deptId);
        }
        List<JSONObject> dataList = yfjbBaseInfoDao.getInfoListByModel(paramJson);
        Map<String, Object> jbfsMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        Map<String, Object> paramMap;
        List<String> saleModelList = new ArrayList<>();
        for (JSONObject temp : dataList) {
            temp.put("costType", jbfsMap.get(temp.getString("costType")));
            temp.put("isReplace", sfMap.get(temp.getString("isReplace")));
            temp.put("major", majorMap.get(temp.getString("major")));
            String mainId = temp.getString("id");
            paramMap = new HashMap<>(16);
            paramMap.put("mainId", mainId);
            List<Map<String, Object>> processList = yfjbBaseInfoDao.processList(paramMap);
            temp.put("processList", processList);
            saleModelList.add(temp.getString("saleModel"));
        }
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int lastYear = currentYear - 1;
        JSONObject yearReportJson = new JSONObject();
        yearReportJson.put("currentYear", currentYear);
        yearReportJson.put("lastYear", lastYear);
        paramJson = new JSONObject();
        paramJson.put("reportYear", currentYear);
        paramJson.put("saleModelList", saleModelList);
        if (StringUtil.isNotEmpty(deptId)) {
            params.put("deptId", deptId);
        }
        /** 策划成本 */
        Float currentPerCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getPerCost(paramJson));
        paramJson.put("reportYear", lastYear);
        Float lastYearPerCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getPerCost(paramJson));
        Float totalPerCost = currentPerCost + lastYearPerCost;
        yearReportJson.put("currentPerCost", currentPerCost);
        yearReportJson.put("lastYearPerCost", lastYearPerCost);
        yearReportJson.put("totalPerCost", totalPerCost);
        /** 实现降本 */
        paramJson.put("reportYear", currentYear);
        Float currentAchieveCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getAchieveCost(paramJson));
        paramJson.put("reportYear", lastYear);
        Float lastAchieveCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getAchieveCost(paramJson));
        Float totalAchieveCost = currentAchieveCost + lastAchieveCost;
        yearReportJson.put("currentAchieveCost", currentAchieveCost);
        yearReportJson.put("lastAchieveCost", lastAchieveCost);
        yearReportJson.put("totalAchieveCost", totalAchieveCost);
        /** 获取整机成本 */
        paramJson.put("costYear", currentYear - 1);
        paramJson.put("saleModelList", saleModelList);
        JSONObject productCostInfo = yfjbProductCostDao.getCostByInfo(paramJson);
        String currentProductCost = "";
        if (productCostInfo != null) {
            currentProductCost = productCostInfo.getString("cost");
        }
        paramJson.put("costYear", lastYear - 1);
        productCostInfo = yfjbProductCostDao.getCostByInfo(paramJson);
        String lastProductCost = "";
        if (productCostInfo != null) {
            lastProductCost = productCostInfo.getString("cost");
        }
        yearReportJson.put("currentProductCost", currentProductCost);
        yearReportJson.put("lastProductCost", lastProductCost);

        /** 按月度统计报表 */
        JSONObject monthReport = new JSONObject();
        monthReport.put("currentTitle", currentYear + "年月度单台降本统计");
        monthReport.put("lastTitle", lastYear + "年月度单台降本统计");
        JSONArray monthReportArray = new JSONArray();
        Float yearTotal = 0f;
        for (int i = 1; i <= 3; i++) {
            paramJson = new JSONObject();
            paramJson.put("saleModelList", saleModelList);
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", deptId);
            }
            JSONObject object = new JSONObject();
            Float total = 0f;
            for (int j = 1; j <= 13; j++) {
                if (i == 1) {
                    if (j == 13) {
                        object.put(String.valueOf(j), "合计");
                    } else {
                        object.put(String.valueOf(j), j + "月");
                    }
                } else if (i == 2) {
                    if (j < 10) {
                        paramJson.put("yearMonth", lastYear + "-0" + j);
                        Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                        total += value;
                        object.put(String.valueOf(j), value);
                    } else if (j < 13) {
                        paramJson.put("yearMonth", lastYear + "-" + j);
                        Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                        total += value;
                        object.put(String.valueOf(j), value);
                    } else if (j == 13) {
                        object.put(String.valueOf(j), total);
                        yearTotal += total;
                        total = 0f;
                    }
                } else if (i == 3) {
                    if (j < 10) {
                        paramJson.put("yearMonth", currentYear + "-0" + j);
                        Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                        total += value;
                        object.put(String.valueOf(j), value);
                    } else if (j < 13) {
                        paramJson.put("yearMonth", currentYear + "-" + j);
                        Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                        total += value;
                        object.put(String.valueOf(j), value);
                    } else if (j == 13) {
                        object.put(String.valueOf(j), total);
                        yearTotal += total;
                        total = 0f;
                    }
                }
            }
            monthReportArray.add(object);
        }
        monthReport.put("list", monthReportArray);
        monthReport.put("total", yearTotal);
        String title = saleModel;
        String[] fieldNames = {"销售型号", "原物料编码", "原物料名称", "原物料价格", "降本方式", "替代物料编码", "替代物料名称", "替代物料价格", "差额", "单台用量",
            "替代比例（%）", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "计划试制时间", "时间试制时间", "计划下发切换通知单时间", "实际下发切换通知单时间", "计划切换时间",
            "时间切换时间", "所属专业", "责任人"};
        String[] fieldCodes = {"saleModel", "orgItemCode", "orgItemName", "orgItemPrice", "costType", "newItemCode",
            "newItemName", "newItemPrice", "differentPrice", "perSum", "replaceRate", "perCost", "achieveCost", "risk",
            "isReplace", "jhsz_date", "sjsz_date", "jhxfqh_date", "sjxfqh_date", "jhqh_date", "sjqh_date", "major",
            "responseMan"};
        HSSFWorkbook wbObj =
            YfjbExcelUtils.exportModelExcel(dataList, fieldNames, fieldCodes, title, yearReportJson, monthReport);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /** 机型汇总表 */
    public void exportModelTotalExcel(HttpServletResponse response, Map<String, Object> params) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "机型维度汇总报表";
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        JSONObject paramJson = new JSONObject();
        if (StringUtil.isNotEmpty(deptId)) {
            params.put("deptId", deptId);
        }
        /** 1.先按年份查询 降本机型数据 */
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        paramJson.put("reportYear", currentYear);
        // 当前年报表以计划切换时间为准
        paramJson.put("yearType", "current");
        List<JSONObject> dataList = yfjbBaseInfoDao.getModelsByYear(paramJson);
        // 整机成本要取前一年的数据
        paramJson.put("costYear", currentYear - 1);
        for (JSONObject temp : dataList) {
            /** 2.查询机型+年份对应的整机成本 */
            String saleModel = temp.getString("saleModel");
            paramJson.put("model", saleModel);
            JSONObject costObj = yfjbProductCostDao.getObjByInfo(paramJson);
            Float productCost = 0f;
            if (costObj != null) {
                productCost = costObj.getFloatValue("cost");
            }
            temp.put("productCost", productCost);
            /** 3.统计机型+年份单台策划降本额 */
            paramJson.put("saleModel", saleModel);
            Float currentPerCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getPerCost(paramJson));
            temp.put("planPerCost", currentPerCost);
            /** 4.计算策划占比 */
            if (productCost == 0f) {
                temp.put("planRate", "0");
            } else {
                float rate = currentPerCost / productCost;
                temp.put("planRate", new DecimalFormat("#.###%").format(rate));
            }
            /** 5.按月份统计单台降本额 */
            /** 6.已实现单台降本 */
            /** 7.已实现单台降本 */
            calculateMonthCost(temp, paramJson, currentYear, productCost);
        }
        paramJson.put("reportYear", currentYear - 1);
        // 当前年报表以计划切换时间为准
        paramJson.put("yearType", "lastYear");
        List<JSONObject> dataLastList = yfjbBaseInfoDao.getModelsByYear(paramJson);
        // 整机成本要取前一年的数据
        paramJson.put("costYear", currentYear - 2);
        for (JSONObject temp : dataLastList) {
            /** 2.查询机型+年份对应的整机成本 */
            String saleModel = temp.getString("saleModel");
            paramJson.put("model", saleModel);
            JSONObject costObj = yfjbProductCostDao.getObjByInfo(paramJson);
            Float productCost = 0f;
            if (costObj != null) {
                productCost = costObj.getFloatValue("cost");
            }
            temp.put("productCost", productCost);
            /** 3.统计机型+年份单台策划降本额 */
            paramJson.put("saleModel", saleModel);
            Float currentPerCost = CommonFuns.nullToZero(yfjbBaseInfoDao.getPerCost(paramJson));
            temp.put("planPerCost", new DecimalFormat("#.##").format(currentPerCost));
            /** 4.计算策划占比 */
            if (productCost == 0f) {
                temp.put("planRate", "0");
            } else {
                float rate = currentPerCost / productCost;
                temp.put("planRate", new DecimalFormat("#.###%").format(rate));
            }
            /** 5.按月份统计单台降本额 */
            /** 6.已实现单台降本 */
            /** 7.已实现单台降本 */
            calculateMonthCost(temp, paramJson, currentYear, productCost);
        }
        String title1 = currentYear + "年年各机型降本统计";
        String title2 = currentYear - 1 + "年年各机型降本统计";
        String[] fieldNames =
            {"机型", "整机成本", "单台策划降本额", "占比", "1月份单台降本额", "2月份单台降本额", "3月份单台降本额", "4月份单台降本额", "5月份单台降本额", "6月份单台降本额",
                "7月份单台降本额", "8月份单台降本额", "9月份单台降本额", "10月份单台降本额", "11月份单台降本额", "12月份单台降本额", "已实现单台降本", "降本占比"};
        String[] fieldCodes = {"saleModel", "productCost_float", "planPerCost_float", "planRate", "january_float",
            "february_float", "march_float", "april_float", "may_float", "june_float", "july_float", "august_float",
            "september_float", "october_float", "november_float", "december_float", "actualCost_float", "actualRate"};
        HSSFWorkbook wbObj =
            YfjbExcelUtils.exportTotalModel(dataList, dataLastList, fieldNames, fieldCodes, title1, title2);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    /**
     * 导出全年降本预测与实际降本对比-qnjbycysjjbdb
     */
    public void exportPlanActualExcel(HttpServletResponse response, Map<String, Object> params) {
        try {
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "全年降本预测与实际降本对比";
            String reportYear = CommonFuns.nullToString(params.get("reportYear"));
            String reportRule = CommonFuns.nullToString(params.get("reportRule"));
            String title = reportYear + "全年降本预测与实际降本对比";
            List<JSONObject> list = new ArrayList<>();
            genAllYearData(list, reportRule, reportYear);
            String[] fieldNames = {"", "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月", "总计"};
            String[] fieldCodes = {"title", "january", "february", "march", "april", "may", "june", "july", "august",
                "september", "october", "november", "december", "total"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出全年降本预测与实际降本对比", e);
        }
    }

    public void genAllYearData(List<JSONObject> list, String reportRule, String reportYear) {
        // 1.拼接可预测降本
        JSONObject ycjbObj = new JSONObject();
        if ("old".equals(reportRule)) {
            ycjbObj = calculateOldPlanCost(reportYear);
        } else if ("new".equals(reportRule)) {
            ycjbObj = calculateNewPlanCost(reportYear);
        }
        ycjbObj.put("title", "预测降本/元");
        // 2.拼接实际降本
        JSONObject sjjbObj = new JSONObject();
        sjjbObj.put("title", "实际降本/元");
        // 3.拼接产量预算
        JSONObject clysObj = new JSONObject();
        clysObj.put("title", "产量预算/台");
        JSONObject monthProduceObj = yfjbMonthProductionsDao.getProductByYear(reportYear);
        if (monthProduceObj != null) {
            clysObj.put("january", monthProduceObj.get("january"));
            clysObj.put("february", monthProduceObj.get("february"));
            clysObj.put("march", monthProduceObj.get("march"));
            clysObj.put("april", monthProduceObj.get("april"));
            clysObj.put("may", monthProduceObj.get("may"));
            clysObj.put("june", monthProduceObj.get("june"));
            clysObj.put("july", monthProduceObj.get("july"));
            clysObj.put("august", monthProduceObj.get("august"));
            clysObj.put("september", monthProduceObj.get("september"));
            clysObj.put("october", monthProduceObj.get("october"));
            clysObj.put("november", monthProduceObj.get("november"));
            clysObj.put("december", monthProduceObj.get("december"));
            clysObj.put("total", monthProduceObj.get("total"));
        }

        // 4.拼接实际产量
        JSONObject sjclObj = new JSONObject();
        sjclObj.put("title", "实际产量/台");
        // 5.拼接预计材料成本
        JSONObject yjclcbObj = new JSONObject();
        yjclcbObj.put("title", "预计材料成本/元");
        // 6.拼接实际材料成本
        JSONObject sjclcbObj = new JSONObject();
        sjclcbObj.put("title", "实际材料成本/元");
        // 7.拼接预计单台材料成本
        JSONObject yjdtclcbObj = new JSONObject();
        yjdtclcbObj.put("title", "预计单台材料成本/元");
        // 8.拼接实际单台材料成本
        JSONObject sjdtclcbObj = new JSONObject();
        sjdtclcbObj.put("title", "实际单台材料成本/元");
        // 9.拼接预测月度完成占比
        JSONObject ycydwczbObj = new JSONObject();
        ycydwczbObj.put("title", "预测月度完成占比");
        // 10.拼接实际月度完成占比
        JSONObject sjydwczbObj = new JSONObject();
        sjydwczbObj.put("title", "实际月度完成占比");
        // 11.拼接年度降本实时进度
        JSONObject ndjbssjdObj = new JSONObject();
        ndjbssjdObj.put("title", "年度降本实时进度");
        List<JSONObject> monthDataList = yfjbMonthDataDao.getMonthDataByYear(reportYear);
        Double totalRealCost = 0d;
        Integer totalRealProduce = 0;
        Double totalPlanMatCost = 0d;
        Double totalRealMatCost = 0d;
        Double totalPlanPerMatCost = 0d;
        Double totalRealPerMatCost = 0d;
        Double totalPlanMonthFinish = 0d;
        Double totalRealMonthFinish = 0d;
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        Double totalJb = 0d;
        Double totalCb = 0d;
        /**
         * 计算年度降本实时进度的总和时，本月前的采用实际，后面的采用预测
         * */
        Double complexJb = 0d;
        Double complexClcb = 0d;
        for (int i = 1; i <= 12; i++) {
            for (JSONObject temp : monthDataList) {
                String yearMonth = temp.getString("yearMonth");
                String month = yearMonth.substring(5, 7);
                int monthValue = Integer.parseInt(month);
                if (monthValue == i) {
                    String monthEng = getMonth(i);
                    // 实际降本
                    Double realCost = 0d;
                    if ("old".equals(reportRule)) {
                        realCost = temp.getDoubleValue("realCostOld");
                        if (monthValue <= currentMonth) {
                            totalJb += realCost;
                            complexJb += realCost;
                        }
                    } else if ("new".equals(reportRule)) {
                        realCost = temp.getDoubleValue("realCostNew");
                        if (monthValue <= currentMonth) {
                            totalJb += realCost;
                            complexJb += realCost;
                        }
                    }
                    if (realCost != null) {
                        totalRealCost += realCost;
                    }
                    // 实际产量
                    Integer realProduce = temp.getInteger("realProduce");
                    if (realProduce != null) {
                        totalRealProduce += realProduce;
                    }
                    // 预计材料成本
                    Double planMatCost = temp.getDoubleValue("planMatCost");
                    if (planMatCost != null) {
                        totalPlanMatCost += planMatCost;
                        if (monthValue > currentMonth) {
                            totalCb += planMatCost;
                            complexClcb += planMatCost;
                        }
                    }
                    // 实际材料成本
                    Double realMatCost = temp.getDoubleValue("realMatCost");
                    if (realMatCost != null) {
                        totalRealMatCost += realMatCost;
                        if (monthValue <= currentMonth) {
                            totalCb += realMatCost;
                            complexClcb += realMatCost;
                        }
                    }
                    // 预计单台材料成本 = 预计材料成本/预计产量;
                    if (planMatCost != null && clysObj.getFloat(monthEng) != null && clysObj.getFloat(monthEng) != 0) {
                        Double planPerMatCost = planMatCost / clysObj.getDoubleValue(monthEng);
                        totalPlanPerMatCost += planPerMatCost;
                        yjdtclcbObj.put(monthEng, new DecimalFormat("#.##").format(planPerMatCost));
                    }

                    // 实际单台材料成本 = 实际材料成本/实际产量
                    if (realMatCost != null && realProduce != null && realProduce != 0) {
                        Double realPerMatCost = realMatCost / realProduce;
                        totalRealPerMatCost += realPerMatCost;
                        sjdtclcbObj.put(monthEng, new DecimalFormat("#.##").format(realPerMatCost));
                    }
                    // 预测月度完成占比 = 预测降本/预计材料成本
                    if (planMatCost != null && ycjbObj.getFloat(monthEng) != null && planMatCost != 0) {
                        Double planMonthFinish = ycjbObj.getFloat(monthEng) / planMatCost;
                        ycydwczbObj.put(monthEng, new DecimalFormat("#.##%").format(planMonthFinish));
                        if (monthValue > currentMonth) {
//                            ndjbssjdObj.put(monthEng, new DecimalFormat("#.##%").format(planMonthFinish));
                            complexJb += ycjbObj.getFloat(monthEng);
                        }
                    }

                    // 实际月度完成占比=实际降本/实际材料成本
                    if (realCost != null && realMatCost != null && realMatCost != 0) {
                        Double realMonthFinish = realCost / realMatCost;
                        totalRealMonthFinish += realMonthFinish;
                        sjydwczbObj.put(monthEng, new DecimalFormat("#.##%").format(realMonthFinish));
                        if (monthValue <= currentMonth) {
                            ndjbssjdObj.put(monthEng, new DecimalFormat("#.##%").format(totalJb/totalCb));
                        }
                    }
                    sjjbObj.put(monthEng, realCost);
                    sjclObj.put(monthEng, realProduce);
                    yjclcbObj.put(monthEng, planMatCost);
                    sjclcbObj.put(monthEng, realMatCost);
                }
            }
        }
        totalJb += ycjbObj.getDoubleValue("totalJb");
        sjjbObj.put("total", new DecimalFormat("#.##").format(totalRealCost));
        sjclObj.put("total", totalRealProduce);
        yjclcbObj.put("total", totalPlanMatCost);
        sjclcbObj.put("total", totalRealMatCost);
        yjdtclcbObj.put("total", new DecimalFormat("#.##").format(totalPlanMatCost/clysObj.getDoubleValue("total")));
        sjdtclcbObj.put("total", new DecimalFormat("#.##").format(totalRealMatCost/totalRealProduce));
        ycydwczbObj.put("total", new DecimalFormat("#.##%").format(ycjbObj.getDoubleValue("total") / totalPlanMatCost));
        sjydwczbObj.put("total", new DecimalFormat("#.##%").format(totalRealCost / totalRealMatCost));
        ndjbssjdObj.put("total", new DecimalFormat("#.##%").format(complexJb / complexClcb));

        list.add(ycjbObj);
        list.add(sjjbObj);
        list.add(clysObj);
        list.add(sjclObj);
        list.add(yjclcbObj);
        list.add(sjclcbObj);
        list.add(yjdtclcbObj);
        list.add(sjdtclcbObj);
        list.add(ycydwczbObj);
        list.add(sjydwczbObj);
        list.add(ndjbssjdObj);
    }

    /**
     * 计算预测降本-老规则 eg:4月份切换6月份算降本，计算12个月
     */
    public JSONObject calculateOldPlanCost(String reportYear) {
        JSONObject itemTotalJson = new JSONObject();
        try {
            int reportYearInt = Integer.parseInt(reportYear);
            String reportStartYearMonth = reportYearInt - 2 + "-12";
            String reportEndYearMonth = reportYearInt + "-10";
            Map<String, Object> params = new HashMap<>(16);
            params.put("reportStartYearMonth", reportStartYearMonth);
            params.put("reportEndYearMonth", reportEndYearMonth);
            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            String switchDate = "";
            String designModel = "";
            String costType = "";
            Double perCost;
            JSONObject itemReplaceJson = new JSONObject();
            JSONObject itemCancelJson = new JSONObject();
            JSONObject itemDirectJson = new JSONObject();
            JSONObject itemOtherJson = new JSONObject();
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                designModel = CommonFuns.nullToString(map.get("designModel"));
                costType = CommonFuns.nullToString(map.get("costType"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                if (obj == null) {
                    continue;
                }
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                if (switchDate.indexOf(".") == -1 && switchDate.indexOf("-") == -1) {
                    switchDate = CommonFuns.nullToString(map.get("jhqh_date"));
                }
                perCost = Double.parseDouble(CommonFuns.nullToZeroString(map.get("perCost")));
                int month;
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    /**
                     * 统计说明： （1）今年切换的，只能算是切换后延迟两个月的降本额 （2）去年切换的，切换时间+2+12达到的本年度月份 （3）前年的，只能算作今年一月份的统计额
                     */
                    boolean isCurrentYear = false;
                    boolean isLastYear = false;
                    boolean isPreYear = false;
                    if (year == reportYearInt) {
                        isCurrentYear = true;
                    } else if (year == reportYearInt - 1) {
                        isLastYear = true;
                    } else if (year == reportYearInt - 2) {
                        isPreYear = true;
                    }
                    if ("1".equals(costType) || "2".equals(costType) || "3".equals(costType) || "6".equals(costType)
                        || "8".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    } else if ("4".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    } else if ("5".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    } else {
                        for (int i = 1; i <= 12; i++) {
                            if (isCurrentYear) {
                                if (i >= month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isLastYear) {
                                if (i < month + 2) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else if (isPreYear) {
                                if (i == 1) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            }
                        }
                    }

                }
            }
            Double totalChange = 0d;
            Double totalCancel = 0d;
            Double totalDirect = 0d;
            Double totalOther = 0d;
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            Double totalJb = 0d;
            for (int i = 1; i <= 12; i++) {
                String key = getMonth(i);
                totalChange += itemReplaceJson.getDoubleValue(key);
                totalCancel += itemCancelJson.getDoubleValue(key);
                totalDirect += itemDirectJson.getDoubleValue(key);
                totalOther += itemOtherJson.getDoubleValue(key);
                Double total = itemReplaceJson.getDoubleValue(key) + itemCancelJson.getDoubleValue(key)
                    + itemDirectJson.getDoubleValue(key) + itemOtherJson.getDoubleValue(key);
                if (i > currentMonth) {
                    totalJb += total;
                }
                itemTotalJson.put(key, new DecimalFormat("#.##").format(total));
            }
            itemReplaceJson.put("title", "物料替代");
            itemCancelJson.put("title", "物料取消");
            itemDirectJson.put("title", "直接降本");
            itemOtherJson.put("title", "其他");
            itemTotalJson.put("title", "总计");
            itemReplaceJson.put("total", totalChange);
            itemCancelJson.put("total", totalCancel);
            itemDirectJson.put("total", totalDirect);
            itemOtherJson.put("total", totalOther);
            itemTotalJson.put("total",
                new DecimalFormat("#.##").format(totalChange + totalCancel + totalDirect + totalOther));
            itemTotalJson.put("totalJb", totalJb);
            // 输出
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  计算预测降本", e);
        }
        return itemTotalJson;
    }

    /**
     * 计算预测降本-新规则
     */
    public JSONObject calculateNewPlanCost(String reportYear) {
        JSONObject itemTotalJson = new JSONObject();
        try {
            int reportYearInt = Integer.parseInt(reportYear);
            int lastYear = reportYearInt - 1;
            String reportYearMonth = reportYearInt + "-01";
            Map<String, Object> params = new HashMap<>(16);
            params.put("reportYearMonth", reportYearMonth);
            List<Map<String, Object>> list = yfjbBaseInfoDao.getCostCategoryList(params);
            String switchDate = "";
            String designModel = "";
            String costType = "";
            Double perCost;
            JSONObject itemReplaceJson = new JSONObject();
            JSONObject itemCancelJson = new JSONObject();
            JSONObject itemDirectJson = new JSONObject();
            JSONObject itemOtherJson = new JSONObject();
            Map<String, Object> paramMap;
            for (Map<String, Object> map : list) {
                designModel = CommonFuns.nullToString(map.get("designModel"));
                costType = CommonFuns.nullToString(map.get("costType"));
                paramMap = new HashMap<>(16);
                paramMap.put("productYear", reportYear);
                paramMap.put("productModel", designModel.trim());
                JSONObject obj = yfjbMonthProductionsDao.getObjByInfo(paramMap);
                if (obj == null) {
                    obj = new JSONObject();
                    continue;
                }
                switchDate = CommonFuns.nullToString(map.get("sjqh_date"));
                if (switchDate.indexOf(".") == -1 && switchDate.indexOf("-") == -1) {
                    switchDate = CommonFuns.nullToString(map.get("jhqh_date"));
                }
                if (StringUtil.isNotEmpty(switchDate) && switchDate.length() > 5) {
                    int year = Integer.parseInt(switchDate.substring(0, 4));
                    int month = 0;
                    if (switchDate.indexOf(".") > -1) {
                        month = Integer.parseInt(switchDate.split("\\.")[1]);
                    } else if (switchDate.indexOf("-") > -1) {
                        month = Integer.parseInt(switchDate.split("-")[1]);
                    } else {
                        continue;
                    }
                    boolean isReportYear = true;
                    if (year < reportYearInt) {
                        isReportYear = false;
                    }
                    Double basePrice = CommonFuns.nullToStringToDoubleZero(map.get("basePrice"));
                    Double newItemPrice = CommonFuns.nullToStringToDoubleZero(map.get("newItemPrice"));
                    perCost = basePrice - newItemPrice;
                    if ("1".equals(costType) || "2".equals(costType) || "3".equals(costType) || "6".equals(costType)
                        || "8".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                    itemReplaceJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current =
                                    CommonFuns.nullToDoubleZero(itemReplaceJson.getDoubleValue(getMonth(i)));
                                itemReplaceJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    } else if ("4".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemCancelJson.getDoubleValue(getMonth(i)));
                                    itemCancelJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current =
                                    CommonFuns.nullToDoubleZero(itemCancelJson.getDoubleValue(getMonth(i)));
                                itemCancelJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    } else if ("5".equals(costType)) {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemDirectJson.getDoubleValue(getMonth(i)));
                                    itemDirectJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current =
                                    CommonFuns.nullToDoubleZero(itemDirectJson.getDoubleValue(getMonth(i)));
                                itemDirectJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    } else {
                        for (int i = 1; i <= 12; i++) {
                            if (isReportYear) {
                                if (i >= month) {
                                    Double totalMoney =
                                        perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                    Double current =
                                        CommonFuns.nullToDoubleZero(itemOtherJson.getDoubleValue(getMonth(i)));
                                    itemOtherJson.put(getMonth(i), totalMoney + current);
                                }
                            } else {
                                Double totalMoney =
                                    perCost * CommonFuns.nullToDoubleZero(obj.getDoubleValue(getMonth(i)));
                                Double current = CommonFuns.nullToDoubleZero(itemOtherJson.getDoubleValue(getMonth(i)));
                                itemOtherJson.put(getMonth(i), totalMoney + current);
                            }
                        }
                    }

                }
            }
            Double totalChange = 0d;
            Double totalCancel = 0d;
            Double totalDirect = 0d;
            Double totalOther = 0d;
            Calendar calendar = Calendar.getInstance();
            int currentMonth = calendar.get(Calendar.MONTH);
            Double totalJb = 0d;
            for (int i = 1; i <= 12; i++) {
                String key = getMonth(i);
                totalChange += itemReplaceJson.getDoubleValue(key);
                totalCancel += itemCancelJson.getDoubleValue(key);
                totalDirect += itemDirectJson.getDoubleValue(key);
                totalOther += itemOtherJson.getDoubleValue(key);
                Double total = itemReplaceJson.getDoubleValue(key) + itemCancelJson.getDoubleValue(key)
                    + itemDirectJson.getDoubleValue(key) + itemOtherJson.getDoubleValue(key);
                itemTotalJson.put(key, new DecimalFormat("#.##").format(total));
                if (i > currentMonth) {
                    totalJb += total;
                }
            }
            itemTotalJson.put("total",
                new DecimalFormat("#.##").format(totalChange + totalCancel + totalDirect + totalOther));
            itemTotalJson.put("totalJb", totalJb);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  计算预测降本-新规则", e);
        }
        return itemTotalJson;
    }

    public void calculateMonthCost(JSONObject temp, JSONObject paramJson, int year, float productCost) {
        Float total = 0f;
        for (int j = 1; j <= 13; j++) {
            if (j < 10) {
                paramJson.put("yearMonth", year + "-0" + j);
                Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                temp.put(getMonth(j), new DecimalFormat("#.##").format(value));
                total += value;
            } else if (j < 13) {
                paramJson.put("yearMonth", year + "-" + j);
                Float value = CommonFuns.nullToZero(yfjbBaseInfoDao.getMonthAchieveCost(paramJson));
                temp.put(getMonth(j), new DecimalFormat("#.##").format(value));
                total += value;
            }
        }
        temp.put("actualCost", new DecimalFormat("#.##").format(total));
        if (productCost == 0f) {
            temp.put("actualRate", "0");
        } else {
            float rate = total / productCost;
            temp.put("actualRate", new DecimalFormat("#.###%").format(rate));
        }
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "研发降本项目导入模板.xlsx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    public void importProduct(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            Map<String, String> userCertNo2UserId = new HashMap<>(16);
            commonInfoManager.getUserCert2Id(userCertNo2UserId);
            Map<String, Object> jbfs2Id = new HashMap<>(16);
            Map<String, Object> major2Id = new HashMap<>(16);
            Map<String, Object> SF2Id = new HashMap<>(16);
            Map<String, Object> important2Id = new HashMap<>(16);
            Map params = new HashMap<>(16);
            params.put("key", "YFJB-JBFS");
            CommonFuns.getCategoryName2Id(jbfs2Id, commonInfoDao.getDicValues(params));
            params = new HashMap<>(16);
            params.put("key", "YFJB-SSZY");
            CommonFuns.getCategoryName2Id(major2Id, commonInfoDao.getDicValues(params));
            params.put("key", "YESORNO");
            CommonFuns.getCategoryName2Id(SF2Id, commonInfoDao.getDicValues(params));
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, dataInsert, itemList, titleList, userCertNo2UserId,
                    jbfs2Id, major2Id, SF2Id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                yfjbBaseInfoDao.addObject(dataInsert.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> dataInsert,
        List<Map<String, Object>> itemList, List<String> titleList, Map<String, String> userNo2UserId,
        Map<String, Object> jbfs2Id, Map<String, Object> major2Id, Map<String, Object> SF2Id) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                if ("原物料编码".equals(title) || "替代物料编码".equals(title)) {
                    cellValue = StringUtils.trim(dataFormatter.formatCellValue(cell));
                } else {
                    cellValue = ExcelUtil.getCellFormatValue(cell);
                }
            }
            switch (title) {
                case "序号":
                    break;
                case "销售型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "销售型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("saleModel", cellValue);
                    break;
                case "设计型号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "设计型号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("designModel", cellValue);
                    break;
                case "原物料编码":
                    oneRowMap.put("orgItemCode", cellValue);
                    break;
                case "原物料名称":
                    oneRowMap.put("orgItemName", cellValue);
                    break;
                case "原供应商":
                    oneRowMap.put("orgSupplier", cellValue);
                    break;
                case "原物料价格":
                    oneRowMap.put("orgItemPrice", cellValue);
                    break;
                case "基准价格":
                    oneRowMap.put("basePrice", cellValue);
                    break;
                case "降本方式":
                    if (StringUtils.isNotBlank(cellValue) && !jbfs2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "降本方式为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("costType", jbfs2Id.get(cellValue));
                    break;
                case "降本措施":
                    oneRowMap.put("costMeasure", cellValue);
                    break;
                case "替代物料编码":
                    oneRowMap.put("newItemCode", cellValue);
                    break;
                case "替代物料名称":
                    oneRowMap.put("newItemName", cellValue);
                    break;
                case "新供应商":
                    oneRowMap.put("newSupplier", cellValue);
                    break;
                case "替代物料价格":
                    oneRowMap.put("newItemPrice", cellValue);
                    break;
                case "差额":
                    oneRowMap.put("differentPrice", cellValue);
                    break;
                case "单台用量":
                    oneRowMap.put("perSum", cellValue);
                    break;
                case "代替比例(%)":
                    oneRowMap.put("replaceRate", cellValue);
                    break;
                case "单台降本":
                    oneRowMap.put("perCost", cellValue);
                    break;
                case "已实现单台降本":
                    oneRowMap.put("achieveCost", cellValue);
                    break;
                case "风险评估":
                    oneRowMap.put("risk", cellValue);
                    break;
                case "生产是否切换":
                    if (StringUtils.isNotBlank(cellValue) && !SF2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "生产是否切换为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("isReplace", SF2Id.get(cellValue));
                    break;
                case "计划试制时间":
                    oneRowMap.put("jhsz_date", cellValue);
                    break;
                case "实际试制时间":
                    oneRowMap.put("sjsz_date", cellValue);
                    break;
                case "计划下发切换通知单时间":
                    oneRowMap.put("jhxfqh_date", cellValue);
                    break;
                case "实际下发切换通知单时间":
                    oneRowMap.put("sjxfqh_date", cellValue);
                    break;
                case "计划切换时间":
                    oneRowMap.put("jhqh_date", cellValue);
                    break;
                case "实际切换时间":
                    oneRowMap.put("sjqh_date", cellValue);
                    break;
                case "所属部门":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "所属部门为空或不存在");
                        return oneRowCheck;
                    }
                    JSONObject paramJson = new JSONObject();
                    paramJson.put("deptName", cellValue);
                    paramJson.put("DimId", "1");
                    JSONObject groupJson = importUserDao.getGroupId(paramJson);
                    if (groupJson == null) {
                        oneRowCheck.put("message", "系统中不存在此部门");
                        return oneRowCheck;
                    } else {
                        oneRowMap.put("deptId", groupJson.getString("groupId"));
                    }
                    break;
                case "所属专业":
                    if (StringUtils.isNotBlank(cellValue) && !major2Id.containsKey(cellValue)) {
                        oneRowCheck.put("message", "所属专业为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("major", major2Id.get(cellValue));
                    break;
                case "责任人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "责任人为空");
                        return oneRowCheck;
                    }
                    List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                    if (list != null && list.isEmpty()) {
                        oneRowCheck.put("message", "用户：" + cellValue + "在系统中找不到对应的账号！");
                        return oneRowCheck;
                    } else if (list != null && list.size() > 1) {
                        oneRowCheck.put("message", "用户：" + cellValue + "在系统中找到多个账号！");
                        return oneRowCheck;
                    } else if (list != null && list.size() == 1) {
                        String userId = list.get(0).getString("USER_ID_");
                        oneRowMap.put("response", userId);
                    } else {
                        oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                        return oneRowCheck;
                    }
                    break;
                case "技术方案策划完成":
                    oneRowMap.put("jsfachwc_date", cellValue);
                    break;
                case "试制通知下发完成":
                    oneRowMap.put("sztzxfwc_date", cellValue);
                    break;
                case "性能、可靠性验证完成":
                    oneRowMap.put("xnkkxyzwc_date", cellValue);
                    break;
                case "降本方案正式实施时间":
                    oneRowMap.put("jbfazsss_date", cellValue);
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String id = IdUtil.getId();
        oneRowMap.put("id", id);
        dataInsert.add(oneRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public String getMonth(int month) {
        String monthText = "";
        switch (month) {
            case 1:
                monthText = "january";
                break;
            case 2:
                monthText = "february";
                break;
            case 3:
                monthText = "march";
                break;
            case 4:
                monthText = "april";
                break;
            case 5:
                monthText = "may";
                break;
            case 6:
                monthText = "june";
                break;
            case 7:
                monthText = "july";
                break;
            case 8:
                monthText = "august";
                break;
            case 9:
                monthText = "september";
                break;
            case 10:
                monthText = "october";
                break;
            case 11:
                monthText = "november";
                break;
            case 12:
                monthText = "december";
                break;
        }
        return monthText;
    }

    public JSONObject getReportDeptProjectNum(){
        List<JSONObject> projectNumList = new ArrayList<>();
        try {
            projectNumList = yfjbBaseInfoDao.getDeptProjectNum();
            for(JSONObject temp:projectNumList){
                String deptId = temp.getString("deptId");
                int changeNum = yfjbBaseInfoDao.getDeptChangeNum(deptId);
                int totalSum = temp.getIntValue("totalSum");
                double ratio = (double)changeNum/totalSum*100;
                temp.put("changeNum",changeNum);
                temp.put("ratio",ratio);
            }
        }catch (Exception e){
            logger.error("Exception in  getReportDeptProjectNum", e);
        }
        return ResultUtil.result(true,"",projectNumList);
    }
    public JSONObject getMajorData(){
        List<JSONObject> majorNumList = new ArrayList<>();
        try {
            majorNumList = yfjbBaseInfoDao.getMajorData();
        }catch (Exception e){
            logger.error("Exception in  getMajorData", e);
        }
        return ResultUtil.result(true,"",majorNumList);
    }
    public JSONObject getDelayDataList(){
        List<JSONObject> delayList = new ArrayList<>();
        try {
            delayList = yfjbBaseInfoDao.getDelayDataList();
            for(JSONObject temp:delayList){
                int totalSum = temp.getIntValue("totalSum");
                double productDelayNumRatio = (double)temp.getInteger("productDelayNum")/totalSum*100;
                double changeNoticeDelayNumRatio = (double)temp.getInteger("changeNoticeDelayNum")/totalSum*100;
                double changeDelayNumRatio = (double)temp.getInteger("changeDelayNum")/totalSum*100;
                temp.put("productDelayNumRatio",productDelayNumRatio);
                temp.put("changeNoticeDelayNumRatio",changeNoticeDelayNumRatio);
                temp.put("changeDelayNumRatio",changeDelayNumRatio);
            }
        }catch (Exception e){
            logger.error("Exception in  getDelayDataList", e);
        }
        return ResultUtil.result(true,"",delayList);
    }
    public JSONObject getQuarterData(HttpServletRequest request){
        List<JSONObject> quarterList = new ArrayList<>();
        try {
            String year = RequestUtil.getString(request, "year", "");
            quarterList = yfjbBaseInfoDao.getQuarterDataList(year);
            for(JSONObject temp:quarterList){
                Double rateOne = 0.0;
                Double rateTwo = 0.0;
                Double rateThree = 0.0;
                Double rateFour = 0.0;
                if (temp.containsKey("realTimeRateOne") && !temp.getString("planTimeRateOne").equalsIgnoreCase("0") && !StringUtils.isBlank(temp.getString("planTimeRateOne"))){
                    rateOne = temp.getDouble("realTimeRateOne")/temp.getDouble("planTimeRateOne")*100;
                }
                if (temp.containsKey("planTimeRateTwo") && !temp.getString("planTimeRateTwo").equalsIgnoreCase("0") && !StringUtils.isBlank(temp.getString("planTimeRateTwo"))){
                    rateTwo = temp.getDouble("realTimeRateTwo")/temp.getDouble("planTimeRateTwo")*100;
                }
                if (temp.containsKey("planTimeRateThree") && !temp.getString("planTimeRateThree").equalsIgnoreCase("0") && !StringUtils.isBlank(temp.getString("planTimeRateThree"))){
                    rateThree = temp.getDouble("realTimeRateThree")/temp.getDouble("planTimeRateThree")*100;
                }
                if (temp.containsKey("planTimeRateFour") && !temp.getString("planTimeRateFour").equalsIgnoreCase("0") && !StringUtils.isBlank(temp.getString("planTimeRateFour"))){
                    rateFour = temp.getDouble("realTimeRateFour")/temp.getDouble("planTimeRateFour")*100;
                }
                DecimalFormat df = new DecimalFormat("0.00");
                temp.put("rateOne",df.format(rateOne));
                temp.put("rateTwo",df.format(rateTwo));
                temp.put("rateThree",df.format(rateThree));
                temp.put("rateFour",df.format(rateFour));

            }
        }catch (Exception e){
            logger.error("Exception in  quarterData", e);
        }
        return ResultUtil.result(true,"",quarterList);
    }

    public JsonResult getQuaData(HttpServletRequest request){
        List<JSONObject> quarterList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        JSONObject labelObject = new JSONObject();
        labelObject.put("show", "true");
        labelObject.put("position", "top");
        try {
            String year = RequestUtil.getString(request, "year", "");
            //总数
            quarterList = yfjbBaseInfoDao.getQuarterDataList(year);
            int countList = quarterList.size();

            // 图表数据
            List<JSONObject> seriesData1 = new ArrayList<>();
            List<JSONObject> seriesData2 = new ArrayList<>();
            List<JSONObject> seriesData3 = new ArrayList<>();
            List<JSONObject> seriesData4 = new ArrayList<>();

            List<JSONObject> seriesData1R = new ArrayList<>();
            List<JSONObject> seriesData2R = new ArrayList<>();
            List<JSONObject> seriesData3R = new ArrayList<>();
            List<JSONObject> seriesData4R = new ArrayList<>();

            List<JSONObject> seriesData1Rate = new ArrayList<>();
            List<JSONObject> seriesData2Rate = new ArrayList<>();
            List<JSONObject> seriesData3Rate = new ArrayList<>();
            List<JSONObject> seriesData4Rate = new ArrayList<>();

            // x轴数据
            List<String> xAxisData = new ArrayList<String>();
            // y轴数据（总）
            List<String> legendData = new ArrayList<>();

            List<Double> yAxisDataPlan1 = new ArrayList<>();
            List<Double> yAxisDataPlan2 = new ArrayList<>();
            List<Double> yAxisDataPlan3 = new ArrayList<>();
            List<Double> yAxisDataPlan4 = new ArrayList<>();

            List<Double> yAxisDataReal1 = new ArrayList<>();
            List<Double> yAxisDataReal2 = new ArrayList<>();
            List<Double> yAxisDataReal3 = new ArrayList<>();
            List<Double> yAxisDataReal4 = new ArrayList<>();

            List<Double> yAxisDataRate1 = new ArrayList<>();
            List<Double> yAxisDataRate2 = new ArrayList<>();
            List<Double> yAxisDataRate3 = new ArrayList<>();
            List<Double> yAxisDataRate4 = new ArrayList<>();

            for (JSONObject oneData:quarterList){
                xAxisData.add(oneData.getString("deptName"));
                //1p
                if(oneData.containsKey("planTimeRateOne")){
                    yAxisDataPlan1.add(oneData.getDouble("planTimeRateOne"));
                }else {
                    yAxisDataPlan1.add(0.00);
                }
                //2p
                if(oneData.containsKey("planTimeRateTwo")){
                    yAxisDataPlan2.add(oneData.getDouble("planTimeRateTwo"));
                }else {
                    yAxisDataPlan2.add(0.00);
                }
                //3p
                if(oneData.containsKey("planTimeRateThree")){
                    yAxisDataPlan3.add(oneData.getDouble("planTimeRateThree"));
                }else {
                    yAxisDataPlan3.add(0.00);
                }
                //4p
                if(oneData.containsKey("planTimeRateFour")){
                    yAxisDataPlan4.add(oneData.getDouble("planTimeRateFour"));
                }else {
                    yAxisDataPlan4.add(0.00);
                }
            }
            DecimalFormat df = new DecimalFormat("0.00");
            for (JSONObject oneData:quarterList){
                //1r
                if(oneData.containsKey("realTimeRateOne")){
                    yAxisDataReal1.add(oneData.getDouble("realTimeRateOne"));
                    String rate1String = df.format(oneData.getDouble("realTimeRateOne")/oneData.getDouble("planTimeRateOne")*100);
                    yAxisDataRate1.add(Double.parseDouble(rate1String));
                }else {
                    yAxisDataReal1.add(0.00);
                    yAxisDataRate1.add(0.00);
                }
                //2r
                if(oneData.containsKey("realTimeRateTwo")){
                    yAxisDataReal2.add(oneData.getDouble("realTimeRateTwo"));
                    String rate2String = df.format(oneData.getDouble("realTimeRateTwo")/oneData.getDouble("planTimeRateTwo")*100);
                    yAxisDataRate2.add(Double.parseDouble(rate2String));
                }else {
                    yAxisDataReal2.add(0.00);
                    yAxisDataRate2.add(0.00);
                }
                //3r
                if(oneData.containsKey("realTimeRateThree")){
                    yAxisDataReal3.add(oneData.getDouble("realTimeRateThree"));
                    String rate3String = df.format(oneData.getDouble("realTimeRateThree")/oneData.getDouble("planTimeRateThree")*100);
                    yAxisDataRate3.add(Double.parseDouble(rate3String));
                }else {
                    yAxisDataReal3.add(0.00);
                    yAxisDataRate3.add(0.00);
                }
                //4r
                if(oneData.containsKey("realTimeRateFour")){
                    yAxisDataReal4.add(oneData.getDouble("realTimeRateFour"));
                    String rate4String = df.format(oneData.getDouble("realTimeRateFour")/oneData.getDouble("planTimeRateFour")*100);
                    yAxisDataRate4.add(Double.parseDouble(rate4String));
                }else {
                    yAxisDataReal4.add(0.00);
                    yAxisDataRate4.add(0.00);
                }
            }

            JSONObject oneSerie = new JSONObject();
            oneSerie.put("name", "一季度预计");
            oneSerie.put("type", "bar");
            oneSerie.put("data", yAxisDataPlan1);
            oneSerie.put("stack", "一季度预计");
            oneSerie.put("barGap", "0");
            oneSerie.put("yAxisIndex", 0);
            legendData.add(oneSerie.getString("name"));
            seriesData1.add(oneSerie);

            JSONObject twoSerie = new JSONObject();
            twoSerie.put("name", "二季度预计");
            twoSerie.put("type", "bar");
            twoSerie.put("data", yAxisDataPlan2);
            twoSerie.put("stack", "二季度预计");
            twoSerie.put("barGap", "0");
            twoSerie.put("yAxisIndex", 0);
            legendData.add(twoSerie.getString("name"));
            seriesData2.add(twoSerie);

            JSONObject threeSerie = new JSONObject();
            threeSerie.put("name", "三季度预计");
            threeSerie.put("type", "bar");
            threeSerie.put("data", yAxisDataPlan3);
            threeSerie.put("stack", "三季度预计");
            threeSerie.put("barGap", "0");
            threeSerie.put("yAxisIndex", 0);
            legendData.add(threeSerie.getString("name"));
            seriesData3.add(threeSerie);

            JSONObject fourSerie = new JSONObject();
            fourSerie.put("name", "四季度预计");
            fourSerie.put("type", "bar");
            fourSerie.put("data", yAxisDataPlan3);
            fourSerie.put("stack", "四季度预计");
            fourSerie.put("barGap", "0");
            fourSerie.put("yAxisIndex", 0);
            legendData.add(fourSerie.getString("name"));
            seriesData4.add(fourSerie);

            JSONObject oneSerieR = new JSONObject();
            oneSerieR.put("name", "一季度实际");
            oneSerieR.put("type", "bar");
            oneSerieR.put("data", yAxisDataReal1);
            oneSerieR.put("stack", "一季度实际");
            oneSerieR.put("barGap", "0");
            oneSerieR.put("yAxisIndex", 0);
            legendData.add(oneSerieR.getString("name"));
            seriesData1R.add(oneSerieR);

            JSONObject twoSerieR = new JSONObject();
            twoSerieR.put("name", "二季度实际");
            twoSerieR.put("type", "bar");
            twoSerieR.put("data", yAxisDataReal2);
            twoSerieR.put("stack", "二季度实际");
            twoSerieR.put("barGap", "0");
            twoSerieR.put("yAxisIndex", 0);
            legendData.add(twoSerieR.getString("name"));
            seriesData2R.add(twoSerieR);

            JSONObject threeSerieR = new JSONObject();
            threeSerieR.put("name", "三季度实际");
            threeSerieR.put("type", "bar");
            threeSerieR.put("data", yAxisDataReal3);
            threeSerieR.put("stack", "三季度实际");
            threeSerieR.put("barGap", "0");
            threeSerieR.put("yAxisIndex", 0);
            legendData.add(threeSerieR.getString("name"));
            seriesData3R.add(threeSerieR);

            JSONObject fourSerieR = new JSONObject();
            fourSerieR.put("name", "四季度实际");
            fourSerieR.put("type", "bar");
            fourSerieR.put("data", yAxisDataReal4);
            fourSerieR.put("stack", "四季度实际");
            fourSerieR.put("barGap", "0");
            fourSerieR.put("yAxisIndex", 0);
            legendData.add(fourSerieR.getString("name"));
            seriesData4R.add(fourSerieR);

            JSONObject oneSerieRate1 = new JSONObject();
            oneSerieRate1.put("name", "一季度完成率");
            oneSerieRate1.put("type", "line");
            oneSerieRate1.put("yAxisIndex", 1);
            oneSerieRate1.put("label", new JSONObject() {
                {
                    put("show", true);
                    put("position", "top");
                    put("color", "black");
                    put("formatter", "{c}%");
                }
            });
            oneSerieRate1.put("data", yAxisDataRate1);
            oneSerieRate1.put("stack", "一季度完成率");
            legendData.add(oneSerieRate1.getString("name"));
            seriesData1Rate.add(oneSerieRate1);

            JSONObject twoSerieRate2 = new JSONObject();
            twoSerieRate2.put("name", "二季度完成率");
            twoSerieRate2.put("type", "line");
            twoSerieRate2.put("yAxisIndex", 1);
            twoSerieRate2.put("label", new JSONObject() {
                {
                    put("show", true);
                    put("position", "top");
                    put("color", "black");
                    put("formatter", "{c}%");
                }
            });
            twoSerieRate2.put("data", yAxisDataRate2);
            twoSerieRate2.put("stack", "二季度完成率");
            legendData.add(twoSerieRate2.getString("name"));
            seriesData2Rate.add(twoSerieRate2);

            JSONObject threeSerieRate3 = new JSONObject();
            threeSerieRate3.put("name", "三季度完成率");
            threeSerieRate3.put("type", "line");
            threeSerieRate3.put("yAxisIndex", 1);
            threeSerieRate3.put("label", new JSONObject() {
                {
                    put("show", true);
                    put("position", "top");
                    put("color", "black");
                    put("formatter", "{c}%");
                }
            });
            threeSerieRate3.put("data", yAxisDataRate3);
            threeSerieRate3.put("stack", "三季度完成率");
            legendData.add(threeSerieRate3.getString("name"));
            seriesData3Rate.add(threeSerieRate3);

            JSONObject fourSerieRate4 = new JSONObject();
            fourSerieRate4.put("name", "四季度完成率");
            fourSerieRate4.put("type", "line");
            fourSerieRate4.put("yAxisIndex", 1);
            fourSerieRate4.put("label", new JSONObject() {
                {
                    put("show", true);
                    put("position", "top");
                    put("color", "black");
                    put("formatter", "{c}%");
                }
            });
            fourSerieRate4.put("data", yAxisDataRate4);
            fourSerieRate4.put("stack", "四季度完成率");
            legendData.add(fourSerieRate4.getString("name"));
            seriesData4Rate.add(fourSerieRate4);

            resultMap.put("legendData", legendData);
            resultMap.put("seriesData1", seriesData1);
            resultMap.put("seriesData2", seriesData2);
            resultMap.put("seriesData3", seriesData3);
            resultMap.put("seriesData4", seriesData4);
            resultMap.put("seriesData1R", seriesData1R);
            resultMap.put("seriesData2R", seriesData2R);
            resultMap.put("seriesData3R", seriesData3R);
            resultMap.put("seriesData4R", seriesData4R);
            resultMap.put("seriesData1Rate", seriesData1Rate);
            resultMap.put("seriesData2Rate", seriesData2Rate);
            resultMap.put("seriesData3Rate", seriesData3Rate);
            resultMap.put("seriesData4Rate", seriesData4Rate);
            resultMap.put("xAxisData", xAxisData);

        }catch (Exception e){
            logger.error("Exception in  quarterData", e);
        }
        return JsonResultUtil.success(resultMap);
    }

    public List<JSONObject> getDelayProductList(HttpServletRequest request){
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONObject paramJson = new JSONObject();
            String deptId = request.getParameter("deptId");
            String delayType = request.getParameter("delayType");
            paramJson.put("delayType",delayType);
            paramJson.put("deptId",deptId);
            list = yfjbBaseInfoDao.getDelayProductList(paramJson);
            CommonFuns.convertDateJSON(list);
        }catch (Exception e){
            logger.error("Exception in  getDelayProductList", e);
        }
        return list;
    }

    public void exportDelayListExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "研发降本项目";
        String deptId = request.getParameter("deptId");
        String delayType = request.getParameter("delayType");
        JSONObject paramJson = new JSONObject();
        paramJson.put("delayType",delayType);
        paramJson.put("deptId",deptId);
        List<JSONObject> list  = yfjbBaseInfoDao.getDelayProductList(paramJson);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> statusMap = commonInfoManager.genMap("YFJB-XMZT");
        Map<String, Object> costTypeMap = commonInfoManager.genMap("YFJB-JBFS");
        Map<String, Object> processMap = commonInfoManager.genMap("YFJB-JDZT");
        Map<String, Object> majorMap = commonInfoManager.genMap("YFJB-SSZY");
        Map<String, Object> processTypeMap = commonInfoManager.genMap("YFJB-JDLB");
        for (Map<String, Object> map : list) {
            if (map.get("infoStatus") != null) {
                map.put("infoStatusText", statusMap.get(map.get("infoStatus")));
            }
            if (map.get("isNewProcess") != null) {
                map.put("isNewProcessText", sfMap.get(map.get("isNewProcess")));
            }
            if (map.get("costType") != null) {
                map.put("costTypeText", costTypeMap.get(map.get("costType")));
            }
            if (map.get("isReplace") != null) {
                map.put("isReplaceText", sfMap.get(map.get("isReplace")));
            }
            if (map.get("processStatus") != null) {
                map.put("processStatusText", processMap.get(map.get("processStatus")));
            }
            if (map.get("major") != null) {
                map.put("majorText", majorMap.get(map.get("major")));
            }
            if (map.get("type") != null) {
                map.put("typeText", processTypeMap.get(map.get("type")));
            }
        }
        String title = "研发降本项目";
        String[] fieldNames = {"销售型号", "设计型号", "项目状态", "是否填写最新进度", "原物料编码", "原物料名称", "原供应商", "降本方式", "降本措施", "替代物料编码",
                "替代物料名称", "新供应商", "差额", "单台用量", "代替比例(%)", "单台降本", "已实现单台降本", "风险评估", "生产是否切换", "计划试制时间", "实际试制时间",
                "计划下发切换通知单时间", "实际下发切换通知单时间", "计划切换时间", "实际切换时间", "所属部门", "所属专业", "责任人", "进度年月", "进度类型", "进度状态", "进度内容"};
        String[] fieldCodes = {"saleModel", "designModel", "infoStatusText", "isNewProcessText", "orgItemCode",
                "orgItemName", "orgSupplier", "costTypeText", "costMeasure", "newItemCode", "newItemName", "newSupplier",
                "differentPrice", "perSum", "replaceRate", "perCost", "achieveCost", "risk", "isReplaceText", "jhsz_date",
                "sjsz_date", "jhxfqh_date", "sjxfqh_date", "jhqh_date", "sjqh_date", "deptName", "majorText", "responseMan",
                "yearMonth", "typeText", "processStatusText", "processContent"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
