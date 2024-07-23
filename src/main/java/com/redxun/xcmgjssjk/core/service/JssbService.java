package com.redxun.xcmgjssjk.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.util.RdmConst;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgjssjk.core.dao.JssbDao;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class JssbService {
    private Logger logger = LogManager.getLogger(JssbService.class);
    @Autowired
    private JssbDao jssbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> queryJssb(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("publishTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        addJssbRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> jssbList = jssbDao.queryJssb(params);
        for (JSONObject jssb : jssbList) {
            if (jssb.get("CREATE_TIME_") != null) {
                jssb.put("CREATE_TIME_", DateUtil.formatDate((Date)jssb.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (jssb.getDate("sbTime") != null) {
                jssb.put("sbTime", DateUtil.formatDate(jssb.getDate("sbTime"), "yyyy-MM-dd"));
            }
            if (jssb.getDate("startTime") != null) {
                jssb.put("startTime", DateUtil.formatDate(jssb.getDate("startTime"), "yyyy-MM"));
            }
            if (jssb.getDate("needTime") != null) {
                jssb.put("needTime", DateUtil.formatDate(jssb.getDate("needTime"), "yyyy-MM"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(jssbList, ContextUtil.getCurrentUserId());
        result.setData(jssbList);
        int countJssbDataList = jssbDao.countJssbList(params);
        result.setTotal(countJssbDataList);
        return result;
    }

    /**
     * 1、admin/分管领导/技术申报管理人员，查看所有 2、部门负责人查看本部门的 3、其他人查看自己创建的和本专业的
     * 
     * @param params
     * @param userId
     * @param userNo
     */
    private void addJssbRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        // 分管领导
        boolean isAllDataQuery = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
        if (isAllDataQuery) {
            params.put("roleName", "fgld");
            return;
        }
        // 技术申报管理员
        boolean isJssbgly = rdmZhglUtil.judgeIsPointRoleUser(userId, "技术申报管理员");
        if (isJssbgly) {
            params.put("roleName", "jssbgly");
            return;
        }
        // 部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (currentUserDepInfo.getString("result").equalsIgnoreCase("success")
            && currentUserDepInfo.getBooleanValue("isDepRespMan")) {
            params.put("roleName", "deptRespUser");
            params.put("deptId", currentUserDepInfo.getString("currentUserMainDepId"));
            return;
        }
        // 普通员工
        String zyStr=rdmZhglUtil.queryUserZyByUserId(userId);
        params.put("zyStr",zyStr);
        params.put("roleName", "ptyg");
    }



    public void createJssb(JSONObject formData) {
        formData.put("jssbId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jssbDao.createJssb(formData);
        if (StringUtils.isNotBlank(formData.getString("jszb"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("jszb"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String jszbId = oneObject.getString("jszbId");
                if ("added".equals(state) || StringUtils.isBlank(jszbId)) {
                    oneObject.put("jszbId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("jssbId"));
                    oneObject.put("item", oneObject.getString("item"));
                    oneObject.put("target", oneObject.getString("target"));
                    oneObject.put("company", oneObject.getString("company"));
                    oneObject.put("nowLevel", oneObject.getString("nowLevel"));
                    oneObject.put("bestLevel", oneObject.getString("bestLevel"));
                    oneObject.put("toLevel", oneObject.getString("toLevel"));
                    oneObject.put("note", oneObject.getString("note"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jssbDao.createJszb(oneObject);
                }
            }
        }
    }

    public void updateJssb(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jssbDao.updateJssb(formData);
        if (StringUtils.isNotBlank(formData.getString("jszb"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("jszb"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String jszbId = oneObject.getString("jszbId");
                if ("added".equals(state) || StringUtils.isBlank(jszbId)) {
                    oneObject.put("jszbId", IdUtil.getId());
                    oneObject.put("belongId", formData.getString("jssbId"));
                    oneObject.put("item", oneObject.getString("item"));
                    oneObject.put("target", oneObject.getString("target"));
                    oneObject.put("company", oneObject.getString("company"));
                    oneObject.put("nowLevel", oneObject.getString("nowLevel"));
                    oneObject.put("bestLevel", oneObject.getString("bestLevel"));
                    oneObject.put("toLevel", oneObject.getString("toLevel"));
                    oneObject.put("note", oneObject.getString("note"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    jssbDao.createJszb(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    jssbDao.updateJszb(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("jszbId", oneObject.getString("jszbId"));
                    jssbDao.deleteDetail(param);
                }
            }
        }
    }

    public void saveJssbUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("jssbFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jssbFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("jssbId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jssbDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getJssbDetail(String jssbId) {
        JSONObject detailObj = jssbDao.queryJssbById(jssbId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("sbTime") != null) {
            detailObj.put("sbTime", DateUtil.formatDate(detailObj.getDate("sbTime"), "yyyy-MM-dd"));
        }
        if (detailObj.getDate("startTime") != null) {
            detailObj.put("startTime", DateUtil.formatDate(detailObj.getDate("startTime"), "yyyy-MM"));
        }
        if (detailObj.getDate("needTime") != null) {
            detailObj.put("needTime", DateUtil.formatDate(detailObj.getDate("needTime"), "yyyy-MM"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getJssbFileList(String jssbId) {
        List<JSONObject> jssbFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jssbId", jssbId);
        jssbFileList = jssbDao.queryJssbFileList(param);
        return jssbFileList;
    }

    public List<JSONObject> getCnList(String jssbId) {
        List<JSONObject> jssbCnList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jssbId", jssbId);
        jssbCnList = jssbDao.queryJszbList(param);
        return jssbCnList;
    }

    public void deleteOneJssbFile(String fileId, String fileName, String jssbId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jssbId, WebAppUtil.getProperty("jssbFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        jssbDao.deleteJssbFile(param);
    }

    public JsonResult deleteJssb(String jssbId) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<JSONObject> files = getJssbFileList(jssbId);
        String wjFilePathBase = WebAppUtil.getProperty("jssbFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                oneFile.getString("belongId"), wjFilePathBase);
        }
        rdmZhglFileManager.deleteDirFromDisk(jssbId, wjFilePathBase);
        Map<String, Object> param = new HashMap<>();
        param.put("jssbId", jssbId);
        jssbDao.deleteJssbFile(param);
        jssbDao.deleteJssb(param);
        jssbDao.deleteDetail(param);
        return result;
    }

    public void insertCnsx(JSONObject formData) {
        formData.put("cnsxId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jssbDao.createJszb(formData);
    }

    public void updateCnsx(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jssbDao.updateJssb(formData);
    }

    public JsonResult deleteDetail(String[] YdjxIdsArr) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String jszbId : YdjxIdsArr) {
            // 删除基本信息
            param.clear();
            param.put("jszbId", jszbId);
            jssbDao.deleteDetail(param);
        }
        return result;
    }

    public void exportJssbList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryExport(request);
        List<Map<String, Object>> listData = result.getData();
        String title = "技术申报导出列表";
        String excelName = title;
        String[] fieldNames = {"技术名称", "申报时间", "申报部门", "申报人员", "专业方向", "预计开始时间", "预计完成时间", "技术简介", "技术描述", "创新点",
            "核心零部件", "产权分析", "项次", "指标", "单位", "现有水平", "标杆水平", "目标水平", "备注"};
        String[] fieldCodes =
            {"jsName", "sbTime", "sbDep", "sbRes", "direction", "startTime", "needTime", "intro", "jsms", "innovate",
                "corepart", "property", "item", "target", "company", "nowLevel", "bestLevel", "toLevel", "note"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        HSSFSheet sheet = wbObj.getSheetAt(0);
        if (listData != null) {
            String json1 = JSON.toJSONString(listData.get(0));
            JSONObject temp1 = JSON.parseObject(json1);
            String startId = temp1.getString("jssbId");
            int start = 2;
            int end = 2;
            for (int i = 1; i < listData.size(); i++) {
                String json2 = JSON.toJSONString(listData.get(i));
                JSONObject temp2 = JSON.parseObject(json2);
                if (!startId.equals(temp2.getString("jssbId"))) {
                    startId = temp2.getString("jssbId");
                    CellRangeAddress region1 = new CellRangeAddress(start, end, 0, 0);
                    CellRangeAddress region2 = new CellRangeAddress(start, end, 1, 1);
                    CellRangeAddress region3 = new CellRangeAddress(start, end, 2, 2);
                    CellRangeAddress region4 = new CellRangeAddress(start, end, 3, 3);
                    CellRangeAddress region5 = new CellRangeAddress(start, end, 4, 4);
                    CellRangeAddress region6 = new CellRangeAddress(start, end, 5, 5);
                    CellRangeAddress region7 = new CellRangeAddress(start, end, 6, 6);
                    CellRangeAddress region8 = new CellRangeAddress(start, end, 7, 7);
                    CellRangeAddress region9 = new CellRangeAddress(start, end, 8, 8);
                    CellRangeAddress region10 = new CellRangeAddress(start, end, 9, 9);
                    CellRangeAddress region11 = new CellRangeAddress(start, end, 10, 10);
                    CellRangeAddress region12 = new CellRangeAddress(start, end, 11, 11);
                    sheet.addMergedRegionUnsafe(region1);
                    sheet.addMergedRegionUnsafe(region2);
                    sheet.addMergedRegionUnsafe(region3);
                    sheet.addMergedRegionUnsafe(region4);
                    sheet.addMergedRegionUnsafe(region5);
                    sheet.addMergedRegionUnsafe(region6);
                    sheet.addMergedRegionUnsafe(region7);
                    sheet.addMergedRegionUnsafe(region8);
                    sheet.addMergedRegionUnsafe(region9);
                    sheet.addMergedRegionUnsafe(region10);
                    sheet.addMergedRegionUnsafe(region11);
                    sheet.addMergedRegionUnsafe(region12);
                    start = end + 1;
                }
                end = end + 1;
            }
            CellRangeAddress region1 = new CellRangeAddress(start, listData.size() + 1, 0, 0);
            CellRangeAddress region2 = new CellRangeAddress(start, listData.size() + 1, 1, 1);
            CellRangeAddress region3 = new CellRangeAddress(start, listData.size() + 1, 2, 2);
            CellRangeAddress region4 = new CellRangeAddress(start, listData.size() + 1, 3, 3);
            CellRangeAddress region5 = new CellRangeAddress(start, listData.size() + 1, 4, 4);
            CellRangeAddress region6 = new CellRangeAddress(start, listData.size() + 1, 5, 5);
            CellRangeAddress region7 = new CellRangeAddress(start, listData.size() + 1, 6, 6);
            CellRangeAddress region8 = new CellRangeAddress(start, listData.size() + 1, 7, 7);
            CellRangeAddress region9 = new CellRangeAddress(start, listData.size() + 1, 8, 8);
            CellRangeAddress region10 = new CellRangeAddress(start, listData.size() + 1, 9, 9);
            CellRangeAddress region11 = new CellRangeAddress(start, listData.size() + 1, 10, 10);
            CellRangeAddress region12 = new CellRangeAddress(start, listData.size() + 1, 11, 11);
            sheet.addMergedRegionUnsafe(region1);
            sheet.addMergedRegionUnsafe(region2);
            sheet.addMergedRegionUnsafe(region3);
            sheet.addMergedRegionUnsafe(region4);
            sheet.addMergedRegionUnsafe(region5);
            sheet.addMergedRegionUnsafe(region6);
            sheet.addMergedRegionUnsafe(region7);
            sheet.addMergedRegionUnsafe(region8);
            sheet.addMergedRegionUnsafe(region9);
            sheet.addMergedRegionUnsafe(region10);
            sheet.addMergedRegionUnsafe(region11);
            sheet.addMergedRegionUnsafe(region12);
        }
        // HSSFSheet sheet = wbObj.getSheetAt(0);
        // sheet.addMergedRegionUnsafe(new CellRangeAddress(2, 3, 0, 0));
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JsonPageResult<?> queryExport(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "jssbId");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    if ("publishTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }

        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> jssbList = jssbDao.queryExport(params);
        for (JSONObject jssb : jssbList) {
            if (jssb.get("CREATE_TIME_") != null) {
                jssb.put("CREATE_TIME_", DateUtil.formatDate((Date)jssb.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (jssb.getDate("sbTime") != null) {
                jssb.put("sbTime", DateUtil.formatDate(jssb.getDate("sbTime"), "yyyy-MM-dd"));
            }
            if (jssb.getDate("startTime") != null) {
                jssb.put("startTime", DateUtil.formatDate(jssb.getDate("startTime"), "yyyy-MM"));
            }
            if (jssb.getDate("needTime") != null) {
                jssb.put("needTime", DateUtil.formatDate(jssb.getDate("needTime"), "yyyy-MM"));
            }
        }
        result.setData(jssbList);
        return result;
    }
}
