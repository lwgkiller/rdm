package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.MonthUnPlanTaskDao;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.MonthUnProjectPlanDao;
import com.redxun.rdmZhgl.core.dao.MonthWorkApplyDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class MonthUnPlanTaskService {
    private static final Logger logger = LoggerFactory.getLogger(MonthUnPlanTaskService.class);
    @Resource
    MonthUnPlanTaskDao monthUnPlanTaskDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Resource
    MonthWorkApplyDao monthWorkApplyDao;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                }
            }
        }
    }

    public List<Map<String, Object>> getPlanList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            String deptId = CommonFuns.nullToString(params.get("deptId"));
            String[] searchdeptId=deptId.split(",");
            List<String> searchdeptIds = Arrays.asList(searchdeptId);
            JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-GLY");
            if (resultJson.getBoolean("YDGZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                if (StringUtil.isNotEmpty(deptId)) {
                    params.put("deptId", searchdeptIds);
                }
            } else if (resultJson.getBoolean("FGZR")) {
                // 分管主任看分管所的数据，和自己参与的项目
                JSONObject paramJson = new JSONObject();
                paramJson.put("userId", ContextUtil.getCurrentUserId());
                paramJson.put("typeKey", "GROUP-USER-DIRECTOR");
                List<String> deptIds = commonInfoManager.getGroupIds(paramJson);
                if (StringUtil.isNotEmpty(deptId)) {
                    deptIds = new ArrayList<>();
                    deptIds.addAll(searchdeptIds);
                    params.put("deptId", deptIds);
                } else {
                    params.put("deptId", deptIds);
                }
                params.put("userId", ContextUtil.getCurrentUserId());
            } else if (resultJson.getBoolean("isLeader")) {
                // 组负责人 看本部门所有人参与的项目
                List<String> deptIds = new ArrayList<>();
                deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("deptId", deptIds);
                params.put("userId", ContextUtil.getCurrentUserId());
                List<String> userIds = commonInfoDao.getUsersByDeptId(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("userIds", userIds);
            } else {
                // 普通人看本部门所有数据，以及别的部门有自己参与的项目
                List<String> deptIds = new ArrayList<>();
                deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
                params.put("deptId", deptIds);
                params.put("userId", ContextUtil.getCurrentUserId());
            }
            String ids = CommonFuns.nullToString(params.get("unPlanTaskIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            }
            result = monthUnPlanTaskDao.getPlanLst(params);
            // 如果是审批的话，先将归口部门不为空，且不为自己的过滤掉
            if (params.get("apply") != null) {
                String FGLD = CommonFuns.nullToString(params.get("FGLD"));
                Boolean isFGLD = "true".equals(FGLD) ? true : false;
                Iterator<Map<String, Object>> iterator = result.iterator();
                while (iterator.hasNext()) {
                    String mainDeptId = ContextUtil.getCurrentUser().getMainGroupId();
                    Map<String, Object> map = iterator.next();
                    // 如果审批流程的话，到分管领导处，将内控级的去掉
                    if (isFGLD && "3".equals(CommonFuns.nullToString(map.get("isCompanyLevel")))) {
                        iterator.remove();
                    }
                    if (map.get("reportDeptId") == null || "".equals(map.get("reportDeptId"))) {
                        continue;
                    }
                    if (!mainDeptId.equals(map.get("reportDeptId").toString())) {
                        iterator.remove();
                    }
                }
            }
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
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
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                if (mapKey.equals("startDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("endDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("asyncStatus", "0");
            resultJson.put("id", id);
            String deptId = CommonFuns.nullToString(objBody.get("deptId"));
            String yearMonth = CommonFuns.nullToString(objBody.get("yearMonth"));
            String code = genProjectCode(deptId, yearMonth);
            objBody.put("projectCode", code);
            Boolean flag = getActTaskById(objBody);
            if (flag) {
                monthUnPlanTaskDao.addObject(objBody);
            } else {
                return ResultUtil.result(false, "计划已审批，不允许新增计划！", "");
            }
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public String genProjectCode(String deptId, String yearMonth) {
        String code = "";
        String codeYear = yearMonth.split("-")[0];
        String codeMonth = yearMonth.split("-")[1];
        JSONObject paramObj = new JSONObject();
        paramObj.put("codeYear", codeYear);
        paramObj.put("codeMonth", codeMonth);
        paramObj.put("deptId", deptId);
        JSONObject codeObj = monthUnPlanTaskDao.getProjectCode(paramObj);
        if (codeObj == null) {
            paramObj.put("code", "01");
            paramObj.put("id", IdUtil.getId());
            monthUnPlanTaskDao.addCode(paramObj);
            JSONObject deptObj = monthUnPlanTaskDao.getDeptShortName(deptId);
            if (deptObj != null) {
                code = deptObj.getString("shortName") + "-" + codeYear.substring(2, 4) + codeMonth + "-01";
            }
        } else {
            String codeNum = CommonFuns.genProjectCode(codeObj.getString("code"), 2);
            code = codeObj.getString("shortName") + "-" + codeYear.substring(2, 4) + codeMonth + "-" + codeNum;
            paramObj.put("code", codeNum);
            monthUnPlanTaskDao.updateProjectCode(paramObj);
        }
        return code;
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                if (mapKey.equals("yearMonth")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM");
                    }
                }
                if (mapKey.equals("startDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                if (mapKey.equals("endDate")) {
                    if (mapValue != null && !"".equals(mapValue)) {
                        mapValue = CommonFuns.convertDateToStr(new Date(mapValue), "yyyy-MM-dd");
                    }
                }
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            monthUnPlanTaskDao.updateObject(objBody);
            resultJson.put("id", objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public void saveOrUpdateItem(String changeGridDataStr) {
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
                    // 根据负责人id 获取对应部门
                    // String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("responseUserId"));
                    // oneObject.put("responseDeptId", deptId);
                    monthUnPlanTaskDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    // String deptId = commonInfoDao.getDeptIdByUserId(oneObject.getString("responseUserId"));
                    // oneObject.put("responseDeptId", deptId);
                    monthUnPlanTaskDao.updateItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    monthUnPlanTaskDao.delItem(oneObject.getString("id"));
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
            monthUnPlanTaskDao.batchDelete(params);
            monthUnPlanTaskDao.batchDeleteItems(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除信息", e);
            return ResultUtil.result(false, "删除信息异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject removeItem(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            monthUnPlanTaskDao.batchDeleteItemsById(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除科技项目", e);
            return ResultUtil.result(false, "删除科技项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = monthUnPlanTaskDao.getObjectById(id);
        return jsonObject;
    }

    public List<JSONObject> getItemList(HttpServletRequest request) {
        List<JSONObject> resultArray = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", mainId);
            resultArray = monthUnPlanTaskDao.getItemList(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getItemList", e);
        }
        return resultArray;
    }

    public void exportUnProjectPlanExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "计划外任务";
        Map<String, Object> params = new HashMap<>(16);
        params = CommonFuns.getSearchParam(params, request, false);
        String deptId = CommonFuns.nullToString(params.get("deptId"));
        String[] searchdeptId=deptId.split(",");
        List<String> searchdeptIds = Arrays.asList(searchdeptId);
        JSONObject resultJson = commonInfoManager.hasPermission("YDGZ-GLY");
        if (resultJson.getBoolean("YDGZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            if (StringUtil.isNotEmpty(deptId)) {
                params.put("deptId", searchdeptIds);
            }
        } else if (resultJson.getBoolean("FGZR")) {
            // 分管主任看分管所的数据，和自己参与的项目
            JSONObject paramJson = new JSONObject();
            paramJson.put("userId", ContextUtil.getCurrentUserId());
            paramJson.put("typeKey", "GROUP-USER-DIRECTOR");
            List<String> deptIds = commonInfoManager.getGroupIds(paramJson);
            if (StringUtil.isNotEmpty(deptId)) {
                deptIds = new ArrayList<>();
                deptIds.addAll(searchdeptIds);
                params.put("deptId", deptIds);
            } else {
                params.put("deptId", deptIds);
            }
            params.put("userId", ContextUtil.getCurrentUserId());
        } else if (resultJson.getBoolean("isLeader")) {
            // 组负责人 看本部门所有人参与的项目
            List<String> deptIds = new ArrayList<>();
            deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("deptId", deptIds);
            params.put("userId", ContextUtil.getCurrentUserId());
            List<String> userIds = commonInfoDao.getUsersByDeptId(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("userIds", userIds);
        } else {
            // 组负责人和普通人看本部门所有数据，以及别的部门有自己参与的项目
            List<String> deptIds = new ArrayList<>();
            deptIds.add(ContextUtil.getCurrentUser().getMainGroupId());
            params.put("deptId", deptIds);
            params.put("userId", ContextUtil.getCurrentUserId());
        }
        List<Map<String, Object>> list = monthUnPlanTaskDao.getPlanLst(params);
        convertDate(list);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> levelMap = commonInfoManager.genMap("YDJH-JHJB");
        for (Map<String, Object> map : list) {
            if (map.get("isDelayApply") != null) {
                map.put("delayApplyText", sfMap.get(map.get("isDelayApply")));
            }
            if (map.get("isCompanyLevel") != null) {
                map.put("levelText", levelMap.get(map.get("isCompanyLevel")));
            }
        }
        String title = "计划外任务";
        String[] fieldNames = {"工作任务名称", "任务来源", "年月", "任务起止日期", "任务负责人", "负责部门","本月工作内容", "本月工作完成标志", "责任人", "配合部门", "计划级别",
            "完成情况", "备注", "是否提交延期申请"};
        String[] fieldCodes =
            {"taskName", "taskFrom", "yearMonth", "startEndDate", "responseMan", "deptName","workContent", "finishFlag",
                "planResponseMan", "responseDeptId", "levelText", "finishStatusText", "remark", "delayApplyText"};
        HSSFWorkbook wbObj = ExcelUtils.exportMonthWorkExcel(list, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JSONObject copyPlan(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String yearMonth = request.getParameter("yearMonth");
            String[] idArr = ids.split(",", -1);
            Map<String, Object> map;
            String newId = "";
            for (String id : idArr) {
                // 1.先复制主表信息
                map = monthUnPlanTaskDao.getObjMap(id);
                newId = IdUtil.getId();
                map.put("id", newId);
                map.put("yearMonth", yearMonth);
                map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("finishStatus", null);
                map.put("remark", null);
                monthUnPlanTaskDao.addObject(map);
                // 2.复制子表信息
                List<Map<String, Object>> list = monthUnPlanTaskDao.getItemMapList(id);
                for (Map<String, Object> tempMap : list) {
                    tempMap.put("id", IdUtil.getId());
                    tempMap.put("mainId", newId);
                    tempMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    tempMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    tempMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    tempMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    monthUnPlanTaskDao.addItem(tempMap);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in update 复制计划异常", e);
            return ResultUtil.result(false, "复制计划异常！", "");
        }
        return ResultUtil.result(true, "复制成功", resultJson);
    }

    public Boolean getActTaskById(Map<String, Object> objBody) {
        // 如果是月度工作管理员，则直接 返回true
        JSONObject adminJson = commonInfoManager.hasPermission("YDGZ-GLY");
        if (adminJson.getBoolean("YDGZ-GLY")) {
            return true;
        }
        List<JSONObject> list = new ArrayList<>();
        list = monthWorkApplyDao.getUnDeptMonthApply(objBody);
        boolean flag = true;
        if (list != null && list.size() == 0) {
            flag = true;
        } else {
            for (JSONObject temp : list) {
                String applyId = temp.getString("id");
                JSONObject jsonObject = monthWorkApplyDao.getActTaskById(applyId);
                if (!"调度员发起审批".equals(jsonObject.getString("actName"))
                    && !"SUCCESS_END".equals(jsonObject.getString("processStatus"))) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 预算模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> budgetTemplateDownload() {
        try {
            String fileName = "预算类项目导入模板.xlsx";
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
            logger.error("Exception in budgetTemplateDownload", e);
            return null;
        }
    }

    public void importBudgetExcel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            String yearMonth = request.getParameter("yearMonth");
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("预算项目");
            if (sheet == null) {
                logger.error("找不到预算项目导入模板");
                result.put("message", "数据导入失败，找不到预算项目导入页！");
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
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, dataInsert, itemList, titleList, yearMonth);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                monthUnPlanTaskDao.addObject(dataInsert.get(i));
            }
            for (int i = 0; i < itemList.size(); i++) {
                monthUnPlanTaskDao.addItem(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importBudgetExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> dataInsert,
        List<Map<String, Object>> itemList, List<String> titleList, String yearMonth) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "任务名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "任务名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("taskName", cellValue);
                    break;
                case "编号":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "编号为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("projectCode", cellValue);
                    break;
                case "项目开始结束时间":
                    oneRowMap.put("startEndDate", cellValue);
                    break;
                case "负责人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "负责人为空");
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
                        oneRowMap.put("userId", userId);
                    } else {
                        oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                        return oneRowCheck;
                    }
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        String userId = CommonFuns.nullToString(oneRowMap.get("userId"));
        oneRowMap.put("CREATE_BY_", userId);
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", userId);
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        String id = IdUtil.getId();
        oneRowMap.put("id", id);
        oneRowMap.put("taskFrom", "预算项目");
        oneRowMap.put("responseMan", userId);
        oneRowMap.put("isCompanyLevel", "2");
        String deptId = commonInfoDao.getDeptIdByUserId(userId);
        oneRowMap.put("deptId", deptId);
        oneRowMap.put("yearMonth", yearMonth);
        String startEndDate = CommonFuns.nullToString(oneRowMap.get("startEndDate"));
        if (StringUtil.isNotEmpty(startEndDate)) {
            String dateArry[] = startEndDate.split("-");
            if (dateArry.length == 2) {
                String startDate = dateArry[0].replace("/", "-") + "-01";
                String endDate = dateArry[1].replace("/", "-") + "-01";
                oneRowMap.put("startDate", startDate);
                oneRowMap.put("endDate", endDate);
            } else {
                oneRowCheck.put("message", "项目开始结束时间格式不对");
                return oneRowCheck;
            }

        }
        oneRowMap.put("asyncStatus", "1");
        dataInsert.add(oneRowMap);
        // 构造子表数据
        itemRowMap.put("id", IdUtil.getId());
        itemRowMap.put("mainId", id);
        itemRowMap.put("CREATE_BY_", userId);
        itemRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("UPDATE_BY_", userId);
        itemRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("responseUserId", userId);
        itemRowMap.put("workContent", "未填写");
        itemRowMap.put("finishFlag", "未填写");
        itemRowMap.put("sortIndex", "1");
        itemList.add(itemRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    /**
     * 预算模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> strategyTemplateDownload() {
        try {
            String fileName = "战略类项目导入模板.xlsx";
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
            logger.error("Exception in strategyTemplateDownload", e);
            return null;
        }
    }

    public void importStrategyExcel(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importStrategyFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            String yearMonth = request.getParameter("yearMonth");
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("战略项目");
            if (sheet == null) {
                logger.error("找不到战略项目导入模板");
                result.put("message", "数据导入失败，找不到战略项目导入页！");
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
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateStrategyDataFromRow(row, dataInsert, itemList, titleList, yearMonth);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            List<Map<String, Object>> tempInsert = new ArrayList<>();
            for (int i = 0; i < dataInsert.size(); i++) {
                monthUnPlanTaskDao.addObject(dataInsert.get(i));
            }
            for (int i = 0; i < itemList.size(); i++) {
                monthUnPlanTaskDao.addItem(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importStrategyExcel", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateStrategyDataFromRow(Row row, List<Map<String, Object>> dataInsert,
        List<Map<String, Object>> itemList, List<String> titleList, String yearMonth) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            String cellValue = null;
            if (cell != null) {
                cellValue = ExcelUtil.getCellFormatValue(cell);
            }
            switch (title) {
                case "序号":
                    break;
                case "任务名称":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "任务名称为空");
                        return oneRowCheck;
                    }
                    oneRowMap.put("taskName", cellValue);
                    break;
                case "项目开始结束时间":
                    oneRowMap.put("startEndDate", cellValue);
                    break;
                case "负责人":
                    if (StringUtils.isBlank(cellValue)) {
                        oneRowCheck.put("message", "负责人为空");
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
                        oneRowMap.put("userId", userId);
                    } else {
                        oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                        return oneRowCheck;
                    }
                    break;
                default:
                    oneRowCheck.put("message", "列“" + title + "”不存在");
                    return oneRowCheck;
            }
        }
        String userId = CommonFuns.nullToString(oneRowMap.get("userId"));
        oneRowMap.put("CREATE_BY_", userId);
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", userId);
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        String id = IdUtil.getId();
        oneRowMap.put("id", id);
        oneRowMap.put("taskFrom", "预算项目");
        oneRowMap.put("responseMan", userId);
        oneRowMap.put("isCompanyLevel", "2");
        String deptId = commonInfoDao.getDeptIdByUserId(userId);
        oneRowMap.put("deptId", deptId);
        oneRowMap.put("yearMonth", yearMonth);
        String startEndDate = CommonFuns.nullToString(oneRowMap.get("startEndDate"));
        if (StringUtil.isNotEmpty(startEndDate)) {
            String dateArry[] = startEndDate.split("-");
            if (dateArry.length == 2) {
                String startDate = dateArry[0].replace("/", "-") + "-01";
                String endDate = dateArry[1].replace("/", "-") + "-01";
                oneRowMap.put("startDate", startDate);
                oneRowMap.put("endDate", endDate);
            } else {
                oneRowCheck.put("message", "项目开始结束时间格式不对");
                return oneRowCheck;
            }

        }
        String code = genProjectCode(deptId, yearMonth);
        oneRowMap.put("projectCode", code);
        oneRowMap.put("asyncStatus", "1");
        dataInsert.add(oneRowMap);
        // 构造子表数据
        itemRowMap.put("id", IdUtil.getId());
        itemRowMap.put("mainId", id);
        itemRowMap.put("CREATE_BY_", userId);
        itemRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("UPDATE_BY_", userId);
        itemRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("responseUserId", userId);
        itemRowMap.put("workContent", "未填写");
        itemRowMap.put("finishFlag", "未填写");
        itemRowMap.put("sortIndex", "1");
        itemList.add(itemRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public List<Map<String, Object>> getPersonUnPlanTask(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("userId", ContextUtil.getCurrentUserId());
            result = monthUnPlanTaskDao.getUnPlanTaskList(params);
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public List<Map<String, Object>> getDeptUnFinishList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            result = monthUnPlanTaskDao.getDeptUnFinishList(params);
            // 如果是审批的话，先将归口部门不为空，且不为自己的过滤掉
            int flag = 1;
            for (int i = 0; i < result.size(); i++) {
                Map<String, Object> map = result.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(result.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", result.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

}
