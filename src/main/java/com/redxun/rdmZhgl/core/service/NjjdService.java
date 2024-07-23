package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmZhgl.core.dao.JsmmDao;
import com.redxun.rdmZhgl.core.dao.NjjdDao;
import com.redxun.rdmZhgl.core.job.NjjdExcelUtils;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * 应用模块名称
 * <p>
 * 代码描述
 * <p>
 * Copyright: Copyright (C) 2021 XXX, Inc. All rights reserved.
 * <p>
 * Company: 徐工挖掘机械有限公司
 * <p>
 *
 * @author liangchuanjiang
 * @since 2021/2/23 10:43
 */
@Service
public class NjjdService {
    private Logger logger = LoggerFactory.getLogger(NjjdService.class);

    @Autowired
    private NjjdDao njjdDao;

    @Autowired
    private JsmmDao jsmmDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> getNjjdList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "id", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("rdTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("rdTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        String scene = RequestUtil.getString(request, "scene", "");
        if (StringUtils.isNotEmpty(scene)) {
            params.put("scene", scene);
        }

        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(),
            ContextUtil.getCurrentUser().getUserNo());

        List<Map<String, Object>> njjdList = njjdDao.queryNjjdListByStatus(params);

        for (Map<String, Object> jsmm : njjdList) {
            if (jsmm.get("jlTime") != null) {
                jsmm.put("jlTime", DateUtil.formatDate((Date)jsmm.get("jlTime"), "yyyy-MM-dd"));
            }

            if (jsmm.get("CREATE_TIME_") != null) {
                jsmm.put("CREATE_TIME_", DateUtil.formatDate((Date)jsmm.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(njjdList);
        result.setData(njjdList);
        int countJsmmDataList = njjdDao.countJsmmLists(params);
        result.setTotal(countJsmmDataList);
        return result;
    }

    public void exportNjjdList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getNjjdList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("statusName", convertStatusName(oneMap.get("STATUS")));
            // oneMap.put("whetherJLName", convertJLStatusName(oneMap.get("whetherJL")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "农机鉴定列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"申请编号", "项目编号", "产品型号", "主销区域", "农机产量", "农机销量", "项目名称", "认定状态", "当前处理人", "创建时间"};
        String[] fieldCodes = {"applyNumber", "projectNumber", "productType", "salesArea", "njcl", "njxs",
            "projectName", "statusName", "currentProcessTask", "CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private String convertStatusName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "DRAFTED":
                return "草稿";
            case "RUNNING":
                return "认定中";
            case "SUCCESS_END":
                return "通过认定";
            case "DISCARD_END":
                return "未通过认定";
        }
        return "";
    }

    private String convertJLStatusName(Object statusKey) {
        if (statusKey == null) {
            return "未奖励";
        }
        String statusKeyStr = statusKey.toString();
        if (statusKeyStr.equalsIgnoreCase("yes")) {
            return "已奖励";
        }
        return "未奖励";
    }

    public JSONObject getNjjdDetail(String njjdId) {
        JSONObject detailObj = njjdDao.queryNjjdById(njjdId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    /**
     * 创建表单
     * 
     * @param formData
     */
    public void createNjjd(JSONObject formData) {

        // String jsmmNumber = toGetNjjdNumber();
        formData.put("njjdId", IdUtil.getId());
        formData.put("bdId", "");
        formData.put("ggId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        // if (StringUtils.isBlank(formData.getString("whetherJL"))) {
        // formData.put("whetherJL", "no");
        // }

        njjdDao.insertNjjd(formData);

        // 规格参数添加
        njjdDao.saveParamInfo(formData);
        if (StringUtils.isNotBlank(formData.getString("changeXzpzData"))) {
            JSONArray xzpzDataJson = JSONObject.parseArray(formData.getString("changeXzpzData"));
            for (int i = 0; i < xzpzDataJson.size(); i++) {
                JSONObject oneObject = xzpzDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String xzpzId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(xzpzId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("njBaseId", formData.getString("njjdId"));
                    oneObject.put("pzmc", oneObject.getString("pzmc"));
                    oneObject.put("pzdw", oneObject.getString("pzdw"));
                    oneObject.put("pzsjz", oneObject.getString("pzsjz"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    njjdDao.insertNjjdxzpz(oneObject);
                }
            }
        }
    }

    /**
     * 修改表单
     * 
     * @param formData
     */
    public void updateNjjd(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        njjdDao.updateNjjd(formData);

        // 规格参数修改
        njjdDao.updateParamInfo(formData);
        if (StringUtils.isNotBlank(formData.getString("changeXzpzData"))) {
            JSONArray xzpzDataJson = JSONObject.parseArray(formData.getString("changeXzpzData"));
            for (int i = 0; i < xzpzDataJson.size(); i++) {
                JSONObject oneObject = xzpzDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String xzpzId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(xzpzId)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("njBaseId", formData.getString("njjdId"));
                    oneObject.put("pzmc", oneObject.getString("pzmc"));
                    oneObject.put("pzdw", oneObject.getString("pzdw"));
                    oneObject.put("pzsjz", oneObject.getString("pzsjz"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    njjdDao.insertNjjdxzpz(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    njjdDao.updateNjjdxzpz(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    njjdDao.deleteNjjdxzpz(param);
                }
            }
        }
    }

    public List<JSONObject> getNjjdFileList(List<String> njjdIdList, String njfjDl) {
        List<JSONObject> njjdFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("njjdIds", njjdIdList);
        param.put("njfjDl", njfjDl);
        njjdFileList = njjdDao.queryNjjdFileList(param);
        return njjdFileList;
    }

    public void saveUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("njjdFilePathBase");
        String njjdFilePathBase_preview = WebAppUtil.getProperty("njjdFilePathBase_preview");
        if (StringUtils.isBlank(filePathBase) || StringUtils.isBlank(njjdFilePathBase_preview)) {
            logger.error("can't find njjdFilePathBase or njjdFilePathBase_preview");
            return;
        }
        try {
            String njjdId = toGetParamVal(parameters.get("njjdId"));
            String njfjDl = toGetParamVal(parameters.get("njfjDl"));
            // String njfjDl = RequestUtil.getString(request, "njfjDl", "");
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String typeId = toGetParamVal(parameters.get("typeId"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + njjdId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            // 处理预览目录中的文件
            String filePath_preview = njjdFilePathBase_preview;
            String fileFullPath_preview = filePath_preview + File.separator + njjdId;
            File file_preview = new File(fileFullPath_preview);
            // 目录不存在则创建
            if (!file_preview.exists()) {
                file_preview.mkdirs();
            }
            String suffixy = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath_pre = file_preview + File.separator + id + "." + suffixy;
            File file_pre = new File(fileFullPath_pre);
            // 文件存在则更新掉
            if (file_preview.exists()) {
                logger.warn("File " + fileFullPath_preview + " will be deleted");
                // file_preview.delete();
            }
            // 删除预览文件夹中生成的临时pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String convertPdfPath = fileFullPath_preview + File.separator + convertPdfDir + File.separator
                + OfficeDocPreview.generateConvertPdfFileName(fileName);
            File tmpPdfFile = new File(convertPdfPath);
            tmpPdfFile.delete();
            FileCopyUtils.copy(mf.getBytes(), file_pre);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("njBaseId", njjdId);
            fileInfo.put("njfjXlId", typeId);
            fileInfo.put("njfjDl", njfjDl);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            njjdDao.addNjjdFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void deleteOneJsmmFile(String fileId, String fileName, String jsmmId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jsmmId, WebAppUtil.getProperty("jsmmFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        jsmmDao.deleteJsmmFile(param);
    }

    public String toGetNjjdNumber() {
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        param.put("countJsmmNumber", "yes");
        String existNumber = njjdDao.countJsmmList(param);
        if (existNumber == null) {
            existNumber = "0";
        }
        int thisNumber = Integer.valueOf(existNumber) + 1;
        if (thisNumber < 10) {
            return "NJ-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "NJ-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }

    // 查询附件类型
    public List<JSONObject> queryNjjdFileTypes(String njfjDl) {
        return njjdDao.queryNjjdFileTypes(njfjDl);
    }

    // 更新奖励状态
    public void updateJlStatus(JSONObject result, JSONObject postObj) {
        jsmmDao.updateJlInfo(postObj);
        result.put("result", true);
        result.put("message", "操作成功");
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    /**
     * 删除技术秘密和磁盘中的文件
     * 
     * @param ids
     * @return
     */
    public JsonResult deleteNjjd(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jsmmIds = Arrays.asList(ids);
        List<JSONObject> files = getNjjdFileList(jsmmIds, "");
        String jsmmFilePathBase = WebAppUtil.getProperty("njjdFilePathBase");
        String ylFilePathBase = WebAppUtil.getProperty("njjdFilePathBase_preview");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("Id"), oneFile.getString("fileName"),
                oneFile.getString("njBaseId"), jsmmFilePathBase);
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("Id"), oneFile.getString("fileName"),
                oneFile.getString("njBaseId"), ylFilePathBase);
        }
        for (String oneJsmmId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, jsmmFilePathBase);
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, ylFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("njjdIds", jsmmIds);
        njjdDao.deleteNjjdFile(param);
        njjdDao.deleteNjjd(param);
        njjdDao.deleteNjjdparam(param);
        njjdDao.deleteNjjduser(param);
        njjdDao.deleteNjjdxzpz(param);
        return result;
    }

    /**
     * 查询农机鉴定用户信息
     * 
     * @param njjdId
     * @return
     */
    public List<JSONObject> getUserList(String njjdId) {

        List<JSONObject> userList = new ArrayList<>();

        if (njjdId != null && !"".equals(njjdId)) {
            userList = njjdDao.getUserList(njjdId);
        }
        return userList;
    }

    public void saveUserList(JSONObject result, String changeGridDataStr, String njBaseId) {
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            if (changeGridDataJson == null || changeGridDataJson.isEmpty()) {
                logger.warn("gridDataArray is blank");
                result.put("message", "保存失败，数据为空！");
                return;
            }

            Set<String> needDelId = new HashSet<>();
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("njBaseId", njBaseId);
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    njjdDao.saveUserList(oneObject);
                } else if ("modified".equals(state)) {
                    // 修改
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                    njjdDao.updateUserList(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除时需要先判断是否有关联的标准，有的话不能删除
                    needDelId.add(id);
                }
            }
            if (!needDelId.isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("ids", needDelId);
                if (!needDelId.isEmpty()) {
                    njjdDao.delUserListByIds(params);
                }
            }

        } catch (Exception e) {
            logger.error("Exception in saveDimension");
            result.put("message", "保存失败，系统异常！");
            return;
        }
    }

    // 保存（包括新增保存、编辑保存）
    public void savePublicStandard(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = request.getParameterMap();
            MultipartFile fileObj = multipartRequest.getFile("standardFile");
            String njjdId = RequestUtil.getString(request, "njjdId");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }

            Map<String, Object> objBody = new HashMap<>();
            // constructParam(parameters, objBody);
            // // 新增或者更新本体标准

            addOrUpdatePublicStandard(parameters, objBody, fileObj, njjdId);

            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    private void addOrUpdatePublicStandard(Map<String, String[]> parameters, Map<String, Object> objBody,
        MultipartFile fileObj, String njjdId) throws IOException {
        // String standardId = parameters.get("id")[0] == null ? "" : parameters.get("id")[0].toString();
        String fjId = njjdDao.selectFjId(parameters.get("id")[0]);
        if (StringUtils.isBlank(fjId)) {
            // 新增文件
            String newStandardId = IdUtil.getId();
            if (fileObj != null) {
                updatePublicStandardFile2Disk(newStandardId, fileObj, parameters, njjdId);
            }
            // 文件添加
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", newStandardId);
            fileInfo.put("fileName", parameters.get("fileName")[0]);
            // fileInfo.put("fileSize",parameters.get("fileSize")[0]);
            fileInfo.put("njBaseId", njjdId);
            fileInfo.put("njfjXlId", '1');
            fileInfo.put("njfjDl", "yh");
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            njjdDao.addNjjdFileInfos(fileInfo);

            // 用户信息添加
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", IdUtil.getId());
            userInfo.put("njBaseId", njjdId);
            userInfo.put("userName", parameters.get("userName")[0]);
            userInfo.put("address", parameters.get("address")[0]);
            userInfo.put("phone", parameters.get("phone")[0]);
            String buyTime = parameters.get("buyTime")[0];
            try {
                String dateStr = buyTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                userInfo.put("buyTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }

            userInfo.put("fjId", newStandardId);
            userInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            userInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            njjdDao.saveUserList(userInfo);

        } else {
            if (fileObj != null) {
                // 更新文件
                updatePublicStandardFile2Disk(fjId, fileObj, parameters, njjdId);
            } else {
                // fileObj为空，有可能是真的没有附件，也有可能是编辑场景（有fileName）
                // 如无fileName则用户前台希望删除该标准的文件，否则说明用户没处理
                String fileName = parameters.get("fileName")[0] == null ? "" : parameters.get("fileName")[0].toString();
                if (StringUtils.isBlank(fileName)) {
                    deletePublicStandardFileFromDisk(fjId, parameters);
                }
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            // standardDao.updatePublicStandard(objBody);

            // 文件修改
            JSONObject info = new JSONObject();
            info.put("id", fjId);
            info.put("fileName", parameters.get("fileName")[0]);
            // fileInfo.put("fileSize",parameters.get("fileSize")[0]);
            info.put("njBaseId", njjdId);
            info.put("njfjXlId", '1');
            info.put("njfjDl", "yh");
            info.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            info.put("CREATE_TIME_", new Date());
            njjdDao.updateNjjdFileInfos(info);

            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", parameters.get("id")[0]);
            fileInfo.put("njBaseId", njjdId);
            fileInfo.put("userName", parameters.get("userName")[0]);
            fileInfo.put("address", parameters.get("address")[0]);
            fileInfo.put("phone", parameters.get("phone")[0]);
            fileInfo.put("buyTime", parameters.get("buyTime")[0]);
            String buyTime = parameters.get("buyTime")[0];
            try {
                String dateStr = buyTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                fileInfo.put("buyTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }

            fileInfo.put("fjId", fjId);
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            njjdDao.updateUserList(fileInfo);
        }
    }

    private void updatePublicStandardFile2Disk(String standardId, MultipartFile fileObj,
        Map<String, String[]> parameters, String njjdId) throws IOException {
        if (StringUtils.isBlank(standardId) || fileObj == null) {
            logger.warn("no standardId or fileObj");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("njjdFilePathBase");
        String njjdFilePathBase_preview = WebAppUtil.getProperty("njjdFilePathBase_preview");
        if (StringUtils.isBlank(standardFilePathBase)) {
            logger.error("can't find njjdFilePathBase");
            return;
        }

        // 向下载目录中写入文件
        String filePath = standardFilePathBase + File.separator + njjdId;
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = filePath + File.separator + standardId + "." + suffix;
        File file = new File(fileFullPath);
        // 处理预览目录中的文件
        String filePath_preview = njjdFilePathBase_preview;
        String fileFullPath_preview = filePath_preview + File.separator + njjdId;
        // 修改时先删
        JSONObject detailObj = njjdDao.queryNjjdFileById(standardId);
        if (detailObj != null) {
            String suffix_de = CommonFuns.toGetFileSuffix(detailObj.getString("fileName"));
            String fileFullPath_de = filePath + File.separator + standardId + "." + suffix_de;
            File file_de = new File(fileFullPath_de);
            file_de.delete();
            String fileFullPath_pre_de = fileFullPath_preview + File.separator + standardId + "." + suffix_de;
            File file_pre_de = new File(fileFullPath_pre_de);
            file_pre_de.delete();
        }
        File file_preview = new File(fileFullPath_preview);
        // 目录不存在则创建
        if (!file_preview.exists()) {
            file_preview.mkdirs();
        }
        String fileFullPath_pre = file_preview + File.separator + standardId + "." + suffix;
        File file_pre = new File(fileFullPath_pre);
        FileCopyUtils.copy(fileObj.getBytes(), file_pre);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        // 预览删除
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileFullPath_preview + File.separator + File.separator + convertPdfDir + File.separator
            + standardId + ".pdf";
        File pdffile = new File(convertPdfPath);
        pdffile.delete();

        FileCopyUtils.copy(fileObj.getBytes(), file);

        // // 处理下载目录的更新
        // File pathFile = new File(standardFilePathBase);
        // // 目录不存在则创建
        // if (!pathFile.exists()) {
        // pathFile.mkdirs();
        // }
        // String fileFullPath = standardFilePathBase + File.separator + standardId;
        // File file = new File(fileFullPath);
        // // 文件存在则更新掉
        // if (file.exists()) {
        // logger.warn("File " + fileFullPath + " will be deleted");
        // file.delete();
        // }
        // FileCopyUtils.copy(fileObj.getBytes(), file);

    }

    private void deletePublicStandardFileFromDisk(String standardId, Map<String, String[]> parameters) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return;
        }
        String standardFilePathBase = WebAppUtil.getProperty("njjdFilePathBase");
        String njjdFilePathBase_preview = WebAppUtil.getProperty("njjdFilePathBase_preview");
        // 处理下载目录的删除
        String suffix = CommonFuns.toGetFileSuffix(parameters.get("fileName")[0]);
        String fileFullPath = standardFilePathBase + File.separator + standardId + '.' + suffix;
        File file = new File(fileFullPath);
        file.delete();
        // 预览目录
        String fileFullPath_re = njjdFilePathBase_preview + File.separator + standardId + '.' + suffix;
        File file_re = new File(fileFullPath_re);
        file_re.delete();
    }

    /**
     * 知识产权模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "农机用户信息证明文件模板.docx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/portrait/" + fileName).toURI());
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

    // 判断预览或者申请的时候是否需要提交申请
    public void checkOperateApply(String requestBody, Map<String, String> result) {
        JSONObject requestObj = JSONObject.parseObject(requestBody);
        String fileId = requestObj.getString("fileId");
        String applyUserId = requestObj.getString("applyUserId");
        String njjdId = requestObj.getString("njjdId");
        if (StringUtils.isBlank(fileId) || StringUtils.isBlank(njjdId)) {
            logger.error("请求参数缺失！");
            result.put("result", "1");
            result.put("applyId", "");
            result.put("instId", "");
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fileId", fileId);
        params.put("njjdId", njjdId);
        params.put("applyUserId", applyUserId);
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> applyInfos = njjdDao.queryUserOperateApply(params);
        if (applyInfos == null || applyInfos.isEmpty()) {
            result.put("result", "1");
            result.put("applyId", "");
            result.put("instId", "");
            return;
        }
        for (Map<String, Object> oneApply : applyInfos) {
            String instStatus = oneApply.get("instStatus") == null ? "" : oneApply.get("instStatus").toString();
            if (StringUtils.isBlank(instStatus)) {
                continue;
            }
            String useStatus = oneApply.get("sfxz") == null ? "0" : oneApply.get("sfxz").toString();
            if ("SUCCESS_END".equalsIgnoreCase(instStatus) && "0".equalsIgnoreCase(useStatus)) {
                result.put("result", "0");
                result.put("applyId", oneApply.get("id").toString());
                result.put("instId", oneApply.get("instId").toString());
                return;
            }
            if ("DRAFTED".equalsIgnoreCase(instStatus)) {
                result.put("result", "1");
                result.put("applyId", oneApply.get("id").toString());
                result.put("instId", oneApply.get("instId").toString());
                return;
            }
            if ("RUNNING".equalsIgnoreCase(instStatus)) {
                result.put("result", "2");
                result.put("applyId", oneApply.get("id").toString());
                result.put("instId", oneApply.get("instId").toString());
                return;
            }
        }
        result.put("result", "1");
        result.put("instId", "");
    }

    public List<JSONObject> getXzpzList(String njjdId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("njjdId", njjdId);
        List<JSONObject> reasonList = njjdDao.getXzpzList(paramJson);
        return reasonList;
    }

    public void exportNjjdcpgg(HttpServletRequest request, HttpServletResponse response, String njjdId) {
        Map<String, Object> params = new HashMap<>();
        params.put("njjdId", njjdId);
        List<Map<String, Object>> listData = njjdDao.queryNjjdcpgg(params);
        List<Map<String, Object>> listData1 = njjdDao.queryNjjdxzpz(params);
        // listData.addAll(listData1);
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("jgxsName", convertJgxsName(oneMap.get("ztJgxs")));
            oneMap.put("ldczName", convertLdczName(oneMap.get("ztLdcz")));
            oneMap.put("jsxsName", convertJsxsName(oneMap.get("jssXs")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "产品规格表";
        String excelName = nowDate + title;
        // String[] fieldNames = {"整机参数型号", "整机参数结构型式", "整机参数工作质量/单位(kg)", "整机参数铲斗容量/单位(m3)", "整机参数外形尺寸和运输状态/单位(mm)",
        // "整机参数轴距/单位(mm)", "整机参数轮式轮距/单位(mm)", "整机参数轮式轮胎规格/单位(mm)", "整机参数轮式轮胎气压/单位(mm)", "整机参数履带式履带轨距/单位(mm)"
        // ,"整机参数履带接地长度/单位(mm)","整机参数履带板宽度/单位(mm)","整机参数履带式履带材质","整机参数后端回转半径/单位(mm)","整机参数离地间隙/单位(mm)","整机参数转台离地高度/单位(mm)","整机参数转台总宽度/单位(mm)","整机参数转台尾端长度/单位(mm)","整机参数回转中心至驱动轮中心距/单位(mm)","整机参数平均接地比压/单位(kPa)"
        // ,"整机参数档位数","整机参数理论速度前进/单位(km/h)","整机参数理论速度倒退/单位(km/h)","整机参数爬坡能力","发动机型号","发动机型式","发动机生产厂","发动机标定功率/单位(kw)","发动机额定净功率/单位(kw)","发动机标定转速/单位(r/min)","驾驶室型号","驾驶室型式","驾驶室生产厂","作业参数最大挖掘力铲斗/单位(kn)"
        // ,"作业参数最大挖掘力斗杆/单位(kn)","作业参数最大挖掘半径/单位(mm)","作业参数最大挖掘深度/单位(mm)","作业参数最大垂直挖掘深度/单位(mm)","作业参数最大挖掘高度/单位(mm)","作业参数最大卸载高度/单位(mm)","工作装置动臂长度/单位(mm)","工作装置斗杆长度/单位(mm)","液压系统主液压泵型号","液压系统主液压泵流量/单位(L/min)","液压系统设定工作压力/单位(MPa)"
        // ,"---","选装配置名称","选装配置单位","选装配置设计值"};
        String[] fieldNames = {"项目", "项目", "项目", "单位", "设计值"};
        String[] fieldCodes = {"ztXh", "jgxsName", "ztGzzl", "ztCdrl", "ztWxcc", "ztZj", "ztLsLj", "ztLsLtgg",
            "ztLsLtQy", "ztLdGj", "ztLdCd", "ztLdKd", "ldczName", "ztHdhzBj", "ztLdJx", "ztZtldGd", "ztZtKd",
            "ztZtwdCd", "ztZxj", "ztJdby", "ztDws", "ztSdQj", "ztSdDt", "ztPp", "fdjXh", "fdjXs", "fdjFactory",
            "fdjBdGl", "fdjEdGl", "fdjBdZs", "jssXh", "jsxsName", "jssFactory", "zyccZdWjlCd", "zycsZdwjlDg",
            "zycsZdwjBj", "zycsZdwjSd", "zycsZdCzwjSd", "zycsZdwjGd", "zycsZdxzGd", "gzzzDbCd", "gzzzDgCd", "yybXh",
            "yybLl", "gzyl", "---", "pzmc", "pzdw", "pzsjz"};
        // HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        HSSFWorkbook wbObj = NjjdExcelUtils.exportExcelWB(listData, listData1, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    private String convertJgxsName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "0":
                return "专用动力挖掘机";
            case "1":
                return "拖拉机挖掘机组";
        }
        return "";
    }

    private String convertLdczName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "0":
                return "金属";
            case "1":
                return "橡胶";
            case "2":
                return "其他";
        }
        return "";
    }

    private String convertJsxsName(Object statusKey) {
        if (statusKey == null) {
            return "";
        }
        String statusKeyStr = statusKey.toString();
        switch (statusKeyStr) {
            case "0":
                return "简易驾驶室";
            case "1":
                return "封闭驾驶室";
            case "2":
                return "安全框架";
        }
        return "";
    }

}
