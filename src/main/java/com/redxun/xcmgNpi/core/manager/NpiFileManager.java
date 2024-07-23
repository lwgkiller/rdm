package com.redxun.xcmgNpi.core.manager;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgNpi.core.dao.NpiFileDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class NpiFileManager {
    private static Logger logger = LoggerFactory.getLogger(NpiFileManager.class);
    @Autowired
    private NpiFileDao npiFileDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    public JsonPageResult<?> queryNpiFileList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            JSONObject queryParam = new JSONObject();
            rdmZhglUtil.addOrderJSON(request, queryParam, "npi_file_system.sn,sys_dic.SN_", "asc");
            String filterParams = request.getParameter("filter");
            if (StringUtils.isNotBlank(filterParams)) {
                JSONArray jsonArray = JSONArray.parseArray(filterParams);
                for (int i = 0; i < jsonArray.size(); i++) {
                    String name = jsonArray.getJSONObject(i).getString("name");
                    if ("systemIds".equalsIgnoreCase(name)) {
                        JSONArray systemIds = jsonArray.getJSONObject(i).getJSONArray("value");
                        if (systemIds != null && !systemIds.isEmpty()) {
                            queryParam.put(name, systemIds);
                        }
                    } else {
                        String value = jsonArray.getJSONObject(i).getString("value");
                        if (StringUtils.isNotBlank(value)) {
                            queryParam.put(name, value);
                        }
                    }
                }
            }
            // 增加分页条件
            if (doPage) {
                rdmZhglUtil.addPageJSON(request, queryParam);
            }
            List<JSONObject> queryPageResult = npiFileDao.queryNpiFileList(queryParam);
            for (JSONObject oneData : queryPageResult) {
                oneData.put("createTime", DateFormatUtil.format(oneData.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            result.setData(queryPageResult);
            if (doPage) {
                // 查询总数
                queryParam.remove("startIndex");
                queryParam.remove("pageSize");
                int totalResult = npiFileDao.countNpiFileList(queryParam);
                result.setTotal(totalResult);
            }
        } catch (Exception e) {
            logger.error("Exception in queryNpiFileList", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }

    // 查询体系
    public List<JSONObject> systemQuery(JSONObject param) {
        try {
            List<JSONObject> result = npiFileDao.querySystem(param);
            return result;
        } catch (Exception e) {
            logger.error("Exception in systemQuery", e);
            return Collections.emptyList();
        }
    }

    public JSONObject queryNpiFileById(String id) {
        JSONObject queryParam = new JSONObject();
        queryParam.put("id", id);
        JSONObject result = npiFileDao.queryNpiFileById(queryParam);
        if (result != null && !result.isEmpty()) {
            result.put("systemName", result.getString("systemName") + "【" + result.getString("systemNameEn") + "】");
        }
        return result;
    }

    // 保存（包括新增保存、编辑保存）
    public void saveNpiFile(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            JSONObject objBody = new JSONObject();
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length != 0
                    && StringUtils.isNotBlank(entry.getValue()[0])) {
                    objBody.put(entry.getKey(), entry.getValue()[0]);
                }
            }

            MultipartFile fileObj = multipartRequest.getFile("npiFile");
            if (fileObj == null && StringUtils.isBlank(objBody.getString("fileObjName"))) {
                result.put("message", "操作失败，附件为空！");
                result.put("success", false);
                return;
            }
            // 新增或者更新本体标准
            addOrUpdateNpiFile(objBody, fileObj, result);
            result.put("id", objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in saveNpiFile", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }

    // 保存或者更新
    private void addOrUpdateNpiFile(JSONObject objBody, MultipartFile fileObj, JSONObject result) throws IOException {
        String id = objBody.getString("id");
        if (StringUtils.isBlank(id)) {
            // 新增文件
            String newId = IdUtil.getId();
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", new Date());
            npiFileDao.insertNpiFile(objBody);
            if (fileObj != null) {
                updateNpiFile2Disk(newId, fileObj, result);
            }
        } else {
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", new Date());
            npiFileDao.updateNpiFile(objBody);
            if (fileObj != null) {
                // 更新文件
                updateNpiFile2Disk(id, fileObj, result);
            }
        }
    }

    private void updateNpiFile2Disk(String npiFileId, MultipartFile fileObj, JSONObject result) throws IOException {
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("npiFileDir");
        if (StringUtils.isBlank(fileBasePath)) {
            result.put("message", "找不到文件路径！");
            result.put("success", false);
            return;
        }

        // 处理下载目录的更新
        File pathFile = new File(fileBasePath);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = fileBasePath + File.separator + npiFileId + ".pdf";
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            logger.warn("File " + fileFullPath + " will be deleted");
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    // 删除
    public void deleteNpiFile(JSONObject result, String npiFileIds) {
        try {
            // 删除本体标准
            JSONObject param = new JSONObject();
            String[] idArr = npiFileIds.split(",", -1);
            List<String> idList = new ArrayList<String>(Arrays.asList(idArr));
            param.put("ids", idList);
            npiFileDao.deleteNpiFileByIds(param);
            for (int i = 0; i < idList.size(); i++) {
                deleteNpiFileFromDisk(idList.get(i));
            }
            result.put("message", "删除成功！");
        } catch (Exception e) {
            logger.error("Exception in deleteNpiFile", e);
            result.put("message", "系统异常！");
        }
    }

    private void deleteNpiFileFromDisk(String npiFileId) {
        if (StringUtils.isBlank(npiFileId)) {
            logger.warn("npiFileId is blank");
            return;
        }
        String fileBasePath = SysPropertiesUtil.getGlobalProperty("npiFileDir");
        if (StringUtils.isBlank(fileBasePath)) {
            logger.error("can't find npiFileDir");
            return;
        }
        // 处理下载目录的删除
        String fileFullPath = fileBasePath + File.separator + npiFileId + ".pdf";
        File file = new File(fileFullPath);
        file.delete();
    }

    // 导出标准excel
    public void exportNpiFileExcel(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryNpiFileList(request, false);
        List<JSONObject> pageDataList = null;
        if (result != null && result.getData() != null && !result.getData().isEmpty()) {
            pageDataList = result.getData();
            for (JSONObject oneData : pageDataList) {
                if (StringUtils.isNotBlank(oneData.getString("fileNameEn"))) {
                    oneData.put("fileName",
                        oneData.getString("fileName") + "【" + oneData.getString("fileNameEn") + "】");
                }
                if (StringUtils.isNotBlank(oneData.getString("stageNameEn"))) {
                    oneData.put("stageName",
                        oneData.getString("stageName") + "【" + oneData.getString("stageNameEn") + "】");
                }
                if (StringUtils.isNotBlank(oneData.getString("systemNameEn"))) {
                    oneData.put("systemName",
                        oneData.getString("systemName") + "【" + oneData.getString("systemNameEn") + "】");
                }
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "NPI资料列表";
        String[] fieldNames = {"活动名称", "流程阶段", "活动类型", "创建人", "创建时间"};
        String[] fieldCodes = {"fileName", "stageName", "systemName", "creator", "createTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(pageDataList, fieldNames, fieldCodes, "NPI资料列表");
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
