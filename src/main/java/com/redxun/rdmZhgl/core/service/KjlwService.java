package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.util.RdmCommonUtil;
import com.redxun.rdmZhgl.core.dao.KjlwDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class KjlwService {
    private Logger logger = LoggerFactory.getLogger(KjlwService.class);
    @Autowired
    private KjlwDao kjlwDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    public JsonPageResult<?> getKjlwList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "kjlwNum", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("CREATE_TIME_StartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("CREATE_TIME_EndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("fbTimeStartTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("fbTimeEndTime".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        // 增加角色过滤的条件
        RdmCommonUtil.addListAllQueryRoleExceptDraft(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> kjlwList = kjlwDao.queryKjlwList(params);
        for (Map<String, Object> kjlw : kjlwList) {
            if (kjlw.get("fbTime") != null) {
                kjlw.put("fbTime", DateUtil.formatDate((Date)kjlw.get("fbTime"), "yyyy-MM-dd"));
            }
            if (kjlw.get("CREATE_TIME_") != null) {
                kjlw.put("CREATE_TIME_", DateUtil.formatDate((Date)kjlw.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(kjlwList);
        //过滤条件
//        rjzzList =filterList(rjzzList);
        result.setData(kjlwList);
        int countkjlwDataList = kjlwDao.countKjlwList(params);
        result.setTotal(countkjlwDataList);
        return result;
    }

    public void createKjlw(JSONObject formData) {
        formData.put("kjlwId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        if (StringUtils.isBlank(formData.getString("fbTime"))) {
            formData.put("fbTime", null);
        }
        kjlwDao.insertKjlw(formData);
    }
    public void updateKjlw(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        if (StringUtils.isBlank(formData.getString("fbTime"))) {
            formData.put("fbTime", null);
        }
        formData.put("UPDATE_TIME_", new Date());
        kjlwDao.updateKjlw(formData);
    }
    public JSONObject getKjlwDetail(String kjlwId) {
        JSONObject detailObj = kjlwDao.queryKjlwById(kjlwId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if(detailObj.get("fbTime")!=null){
            detailObj.put("fbTime",DateUtil.formatDate((Date)detailObj.get("fbTime"), "yyyy-MM-dd"));
        }

        return detailObj;
    }
    public String toGetKjlwNum() {
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        JSONObject maxKjlw = kjlwDao.queryMaxKjlwNum(param);
        int existNumber = 0;
        if (maxKjlw != null) {
            existNumber = Integer.parseInt(maxKjlw.getString("kjlwNum").split("-", -1)[2]);
        }
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return "LW-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "LW-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }
    public List<JSONObject> getKjlwFileList(List<String> kjlwIdList) {
        List<JSONObject> kjlwFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("kjlwIds", kjlwIdList);
        kjlwFileList = kjlwDao.queryKjlwFileList(param);
        return kjlwFileList;
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
        String filePathBase = WebAppUtil.getProperty("kjlwFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find kjlwFilePathBase");
            return;
        }
        try {
            String kjlwId = toGetParamVal(parameters.get("kjlwId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + kjlwId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("kjlwId", kjlwId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            kjlwDao.addKjlwFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }
    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void deleteOneKjlwFile(String fileId, String fileName, String kjlwId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, kjlwId, WebAppUtil.getProperty("kjlwFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        kjlwDao.deleteKjlwFile(param);
    }
    public JsonResult deleteKjlw(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> kjlwIds = Arrays.asList(ids);
        List<JSONObject> files = getKjlwFileList(kjlwIds);
        String kjlwFilePathBase = WebAppUtil.getProperty("kjlwFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("kjlwId"), kjlwFilePathBase);
        }
        for (String oneKjlwId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneKjlwId, kjlwFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("kjlwIds", kjlwIds);
        kjlwDao.deleteKjlwFile(param);
        kjlwDao.deleteKjlw(param);
        return result;
    }
    public void exportKjlwList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getKjlwList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        for (Map<String, Object> oneMap : listData) {
            oneMap.put("statusName", convertStatusName(oneMap.get("status")));
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "科技论文列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"编号", "论文名称", "作者","期刊名称","期号","页码","发表时间", "申请人", "申请人部门", "申请时间", "认定状态","当前处理人","当前任务"};
        String[] fieldCodes = {"kjlwNum", "kjlwName", "writerName","qkmc","qNum","yema","fbTime", "applyUserName", "applyUserDeptName","CREATE_TIME_", "statusName","currentProcessUser","currentProcessTask"};
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
}
