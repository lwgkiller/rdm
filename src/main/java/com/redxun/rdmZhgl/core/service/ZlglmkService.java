package com.redxun.rdmZhgl.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.cache.CacheUtil;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.portrait.core.dao.PortraitKnowledgeDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.ZlglmkDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgJsjl.core.manager.XcmgJsjlManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.xcmgjssjk.core.service.JssjkFileManager;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZlglmkService {
    private static Logger logger = LoggerFactory.getLogger(XcmgJsjlManager.class);
    @Autowired
    private ZlglmkDao zlglmkDao;
    @Autowired
    private JssjkFileManager jssjkFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Resource
    private PortraitKnowledgeDao portraitKnowledgeDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private ZljsjdsService zljsjdsService;

    public JsonPageResult<?> queryListData(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        addOrder(request, params, "gnztId,filingdate", "asc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                params.put(name, value);
            }
        }
        // 增加分页条件
        if (doPage) {
            addPage(request, params);
        }
        List<JSONObject> zgzlList = zlglmkDao.queryZgzlList(params);
        if (zgzlList != null && !zgzlList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (JSONObject oneObj : zgzlList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
                if (oneObj.getDate("examinationApproval") != null) {
                    oneObj.put("examinationApproval", simpleDateFormat.format(oneObj.getDate("examinationApproval")));
                }
            }
        }
        int countZgzlDataList = zlglmkDao.countZgzlList(params);
        result.setData(zgzlList);
        result.setTotal(countZgzlDataList);
        return result;
    }

    public void addOrder(HttpServletRequest request, Map<String, Object> params, String defaultSortField,
        String defaultSortOrder) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", defaultSortField);
            params.put("sortOrder", StringUtils.isBlank(defaultSortOrder) ? "asc" : defaultSortOrder);
        }
    }

    public void addPage(HttpServletRequest request, Map<String, Object> params) {
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    public List<JSONObject> enumList(String type) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("type", type);
        List<JSONObject> result = zlglmkDao.enumList(paramJson);
        for (JSONObject oneObj : result) {
            if (StringUtils.isNotBlank(oneObj.getString("CREATE_TIME_"))) {
                oneObj.put("CREATE_TIME_",
                    DateFormatUtil.format(oneObj.getDate("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
            if (StringUtils.isNotBlank(oneObj.getString("UPDATE_TIME_"))) {
                oneObj.put("UPDATE_TIME_",
                    DateFormatUtil.format(oneObj.getDate("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
            }
        }
        return result;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }

        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        try {
            String standardId = toGetParamVal(parameters.get("standardId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fjlx = toGetParamVal(parameters.get("fjlx"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("zlglmkFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
            if (StringUtils.isNotBlank(fjlx) && "jffj".equals(fjlx)) {
                Map<String, Object> params = new HashMap<>();
                params.put("fyId", standardId);
                List<JSONObject> fyzlId = zlglmkDao.queryFYId(params);
                JSONObject obj = fyzlId.get(0);
                // 向下载目录中写入文件
                String filePath = filePathBase + File.separator + obj.getString("zlId");
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                FileCopyUtils.copy(mf.getBytes(), file);
            } else {
                // 向下载目录中写入文件
                String filePath = filePathBase + File.separator + standardId;
                File pathFile = new File(filePath);
                if (!pathFile.exists()) {
                    pathFile.mkdirs();
                }
                String suffix = CommonFuns.toGetFileSuffix(fileName);
                String fileFullPath = filePath + File.separator + id + "." + suffix;
                File file = new File(fileFullPath);
                FileCopyUtils.copy(mf.getBytes(), file);
            }
            // 写入数据库-证书附件表
            if (StringUtils.isNotBlank(fjlx) && "zsfj".equals(fjlx)) {
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("id", id);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
                fileInfo.put("zlId", standardId);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("UPDATE_TIME_", new Date());
                zlglmkDao.addFileInfos(fileInfo);
            }
            if (StringUtils.isNotBlank(fjlx) && "jffj".equals(fjlx)) {
                JSONObject fileInfo = new JSONObject();
                fileInfo.put("id", id);
                fileInfo.put("fileName", fileName);
                fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
                fileInfo.put("costid", standardId);
                fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("CREATE_TIME_", new Date());
                fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                fileInfo.put("UPDATE_TIME_", new Date());
                zlglmkDao.addfyFileInfos(fileInfo);
            }
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONArray getFiles(HttpServletRequest request) {
        String standardId = RequestUtil.getString(request, "standardId");
        String fileName = RequestUtil.getString(request, "fileName");
        String fjlx = RequestUtil.getString(request, "fjlx");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(standardId)) {
            params.put("standardId", standardId);
        }
        if (StringUtils.isNotBlank(fileName)) {
            params.put("fileName", fileName);
        }
        JSONArray fileArray = null;
        if (StringUtils.isNotBlank(fjlx) && "zsfj".equals(fjlx)) {
            fileArray = zlglmkDao.getFiles(params);
        }
        if (StringUtils.isNotBlank(fjlx) && "jffj".equals(fjlx)) {
            fileArray = zlglmkDao.getfyFiles(params);
        }

        if (fileArray != null) {
            for (int index = 0; index < fileArray.size(); index++) {
                JSONObject oneFileObj = fileArray.getJSONObject(index);
                Date createTime = oneFileObj.getDate("CREATE_TIME_");
                if (createTime != null) {
                    oneFileObj.put("CREATE_TIME_", DateUtil.formatDate(createTime, null));
                }
            }
        }
        return fileArray;
    }

    public List<JSONObject> getJiaoFeiList(String meetingId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("meetingId", meetingId);
        List<JSONObject> jiaoFeiList = zlglmkDao.getJiaoFeiList(paramJson);
        return jiaoFeiList;

    }

    public void saveJiaoFei(JsonResult result, String jiaoFeiDataStr, String meetingId) {
        JSONArray jiaoFeiObjs = JSONObject.parseArray(jiaoFeiDataStr);
        if (jiaoFeiObjs == null || jiaoFeiObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        for (Object object : jiaoFeiObjs) {
            JSONObject jiaoFeiObj = (JSONObject)object;
            if (StringUtils.isBlank(jiaoFeiObj.getString("fyId"))) {
                jiaoFeiObj.put("fyId", IdUtil.getId());
                jiaoFeiObj.put("zlId", meetingId);
                jiaoFeiObj.put("jflbId", jiaoFeiObj.getString("jflbId"));
                jiaoFeiObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                jiaoFeiObj.put("CREATE_TIME_", new Date());
                zlglmkDao.insertJiaoFeiData(jiaoFeiObj);
                result.setData(jiaoFeiObj.getString("zlId"));
            } else {
                jiaoFeiObj.put("zlId", meetingId);
                jiaoFeiObj.put("jflbId", jiaoFeiObj.getString("jflbId"));
                jiaoFeiObj.put("CREATE_BY_", jiaoFeiObj.getString("CREATE_BY_"));
                jiaoFeiObj.put("CREATE_TIME_", jiaoFeiObj.getString("CREATE_TIME_"));
                jiaoFeiObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                jiaoFeiObj.put("UPDATE_TIME_", new Date());
                zlglmkDao.updateJiaoFeiData(jiaoFeiObj);
                result.setData(jiaoFeiObj.getString("zlId"));
            }
        }
    }

    public void saveOrCommitZgzlData(JsonResult result, String newZgzlDataStr) {
        JSONObject nseZgzlObj = JSONObject.parseObject(newZgzlDataStr);
        if (nseZgzlObj == null || nseZgzlObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(nseZgzlObj.getString("zgzlId"))) {
            nseZgzlObj.put("zgzlId", IdUtil.getId());
            nseZgzlObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            if (StringUtils.isBlank(nseZgzlObj.getString("filingdate"))) {
                nseZgzlObj.put("filingdate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("byCase"))) {
                nseZgzlObj.put("byCase", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("examinationApproval"))) {
                nseZgzlObj.put("examinationApproval", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authorizationDate"))) {
                nseZgzlObj.put("authorizationDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authionization"))) {
                nseZgzlObj.put("authionization", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("patentDate"))) {
                nseZgzlObj.put("patentDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("expiryDate"))) {
                nseZgzlObj.put("expiryDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authirizedTime"))) {
                nseZgzlObj.put("authirizedTime", null);
            }
            zlglmkDao.insertZgzlData(nseZgzlObj);
            result.setData(nseZgzlObj.getString("zgzlId"));
        } else {
            nseZgzlObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            if (StringUtils.isBlank(nseZgzlObj.getString("filingdate"))) {
                nseZgzlObj.put("filingdate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("byCase"))) {
                nseZgzlObj.put("byCase", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("examinationApproval"))) {
                nseZgzlObj.put("examinationApproval", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authorizationDate"))) {
                nseZgzlObj.put("authorizationDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authionization"))) {
                nseZgzlObj.put("authionization", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("patentDate"))) {
                nseZgzlObj.put("patentDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("expiryDate"))) {
                nseZgzlObj.put("expiryDate", null);
            }
            if (StringUtils.isBlank(nseZgzlObj.getString("authirizedTime"))) {
                nseZgzlObj.put("authirizedTime", null);
            }
            /** 判断员工画像人员是否改变，改变的话将员工画像删除，然后将同步状态位更新为0 */
            String zgzlId = nseZgzlObj.getString("zgzlId");
            JSONObject patentObj = zlglmkDao.getPatentById(zgzlId);
            String orgMyCompanyUserIds = patentObj.getString("myCompanyUserIds");
            String nowMyCompanyUserIds = nseZgzlObj.getString("myCompanyUserIds");
            if (!orgMyCompanyUserIds.equals(nowMyCompanyUserIds)) {
                // 删除已经同步的数据
                portraitKnowledgeDao.delPatentByOrgId(zgzlId);
                zlglmkDao.updatePatentAsyncStatus(zgzlId);
            }
            zlglmkDao.updateZgzlData(nseZgzlObj);
            result.setData(nseZgzlObj.getString("zgzlId"));
        }
        //更新项目成果计划和技术交底书
        if(StringUtils.isNotBlank(nseZgzlObj.getString("projectId"))
        &&StringUtils.isNotBlank(nseZgzlObj.getString("planId"))){
            zljsjdsService.saveOutList(nseZgzlObj);
            zlglmkDao.updateJdsProject(nseZgzlObj);
        }
    }

    public void saveOrCommitGjzlData(JsonResult result, String newGjzlDataStr) {
        JSONObject nseGjzlObj = JSONObject.parseObject(newGjzlDataStr);
        if (nseGjzlObj == null || nseGjzlObj.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(nseGjzlObj.getString("gjzlId"))) {
            nseGjzlObj.put("gjzlId", IdUtil.getId());
            nseGjzlObj.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            if (StringUtils.isBlank(nseGjzlObj.getString("applictonDay"))) {
                nseGjzlObj.put("applictonDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("openDay"))) {
                nseGjzlObj.put("openDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("nationOpenNumbei"))) {
                nseGjzlObj.put("nationOpenNumbei", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("authorizedDay"))) {
                nseGjzlObj.put("authorizedDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("rewardTime"))) {
                nseGjzlObj.put("rewardTime", null);
            }
            zlglmkDao.insertGjzlData(nseGjzlObj);
            result.setData(nseGjzlObj.getString("gjzlId"));
        } else {
            nseGjzlObj.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            if (StringUtils.isBlank(nseGjzlObj.getString("applictonDay"))) {
                nseGjzlObj.put("applictonDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("openDay"))) {
                nseGjzlObj.put("openDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("nationOpenNumbei"))) {
                nseGjzlObj.put("nationOpenNumbei", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("authorizedDay"))) {
                nseGjzlObj.put("authorizedDay", null);
            }
            if (StringUtils.isBlank(nseGjzlObj.getString("rewardTime"))) {
                nseGjzlObj.put("rewardTime", null);
            }
            zlglmkDao.updateGjzlData(nseGjzlObj);
            result.setData(nseGjzlObj.getString("gjzlId"));
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public JsonPageResult<?> queryGjListData(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        addOrder(request, params, "patentName", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                params.put(name, value);
            }
        }
        // 增加分页条件
        if (doPage) {
            addPage(request, params);
        }
        List<JSONObject> gjzlList = zlglmkDao.queryGjzlList(params);
        if (gjzlList != null && !gjzlList.isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (JSONObject oneObj : gjzlList) {
                if (oneObj.getDate("CREATE_TIME_") != null) {
                    oneObj.put("CREATE_TIME_", simpleDateFormat.format(oneObj.getDate("CREATE_TIME_")));
                }
            }
        }
        int countGjzlDataList = zlglmkDao.countGjzlList(params);
        result.setData(gjzlList);
        result.setTotal(countGjzlDataList);
        return result;
    }

    public JsonResult deleteZgzl(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> zgzlIds = Arrays.asList(ids);
        List<JSONObject> files = getZlglFileList(zgzlIds);
        String zlglmkFilePathBase = WebAppUtil.getProperty("zlglmkFilePathBase");
        for (JSONObject oneFile : files) {
            jssjkFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsmmId"), zlglmkFilePathBase);
        }
        for (String oneJsmmId : ids) {
            jssjkFileManager.deleteDirFromDisk(oneJsmmId, zlglmkFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("zgzlIds", zgzlIds);
        zlglmkDao.deleteZlglFile(param);
        zlglmkDao.deleteZgzl(param);
        return result;
    }

    public JsonResult deleteJiaoFei(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jiaoFeiId = Arrays.asList(id);
        List<JSONObject> files = getJiaoFeiFileList(jiaoFeiId);
        String zlglmkFilePathBase = WebAppUtil.getProperty("zlglmkFilePathBase");
        for (JSONObject oneFile : files) {
            jssjkFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsmmId"), zlglmkFilePathBase);
        }
        jssjkFileManager.deleteDirFromDisk(id, zlglmkFilePathBase);

        Map<String, Object> param = new HashMap<>();
        param.put("jiaoFeiId", jiaoFeiId);
        zlglmkDao.deleteJiaoFeiFile(param);
        zlglmkDao.deleteJiaoFei(param);
        return result;
    }

    public List<JSONObject> getZlglFileList(List<String> zlglIdList) {
        List<JSONObject> zlglFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlglIds", zlglIdList);
        zlglFileList = zlglmkDao.queryZlglFileList(param);
        return zlglFileList;
    }

    public List<JSONObject> getJiaoFeiFileList(List<String> jiaoFeiIdList) {
        List<JSONObject> jiaoFeiFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jiaoFeiIds", jiaoFeiIdList);
        jiaoFeiFileList = zlglmkDao.queryJiaoFeiFileList(param);
        return jiaoFeiFileList;
    }

    public JsonResult deleteGjzl(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> gjzlIds = Arrays.asList(ids);
        List<JSONObject> files = getZlglFileList(gjzlIds);
        String zlglmkFilePathBase = WebAppUtil.getProperty("zlglmkFilePathBase");
        for (JSONObject oneFile : files) {
            jssjkFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jsmmId"), zlglmkFilePathBase);
        }
        for (String oneJsmmId : ids) {
            jssjkFileManager.deleteDirFromDisk(oneJsmmId, zlglmkFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("zgzlIds", gjzlIds);
        zlglmkDao.deleteZlglFile(param);
        zlglmkDao.deleteGjzl(param);
        return result;
    }

    public void exportZgzlList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getZgzlList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "中国专利列表";
        String excelName = nowDate + title;
        String[] fieldNames =
            {"部门", "专业", "提案号", "提案名称", "专利名称", "专利类型", "专利申请号", "发明人", "专利权人/申请人","项目名称", "申请日", "委案日", "审批日", "授权通知发文日",
                "授权日", "被许可实用的单位或人员名称", "是否存在许可他人使用", "是否质押", "被转让公司名称", "权利要求项数", "专利权丧失发文日", "失效日期", "失效原因", "案件状态",
                "代理机构名称", "代理人","产出该专利的项目名称", "专利所属公司项目或产品所处阶段", "授权奖励金额", "授权奖励时间", "专利证书号", "奖励类型", "备注"};
        String[] fieldCodes =
            {"deptName", "zhuanYe", "billNo", "reportName", "patentName", "zllxId", "applicationNumber", "theInventors",
                "thepatentee","projectName", "filingdate", "byCase", "examinationApproval", "authorizationDate", "authionization",
                "personPermitted", "permissionOthers", "whetherPledge", "transferredCompany", "claimsNumber",
                "patentDate", "expiryDate", "failureReason", "gnztName", "agencyName", "agentThe", "projectName",
                "companyBelongs", "authorizedReward", "authirizedTime", "specifyCountry", "jllb", "beiZhu"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JsonPageResult<?> getZgzlList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        addOrder(request, params, "patentName", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                params.put(name, value);
            }
        }
        // 增加分页条件
        if (doPage) {
            addPage(request, params);
        }
        List<JSONObject> zgzlList = zlglmkDao.queryZgzlEXCList(params);
        result.setData(zgzlList);
        return result;
    }

    public void exportGjzlList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = getGjzlList(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "国际专利列表";
        String excelName = nowDate + title;
        String[] fieldNames =
            {"PCT名称", "英文名称", "国际申请号", "当前状态", "国际申请日", "国际公开日", "国际公开号", "指定国家", "国家局申请号", "国家局公开号", "国家局公开日", "国家授权号",
                "国家授权日", "发明人", "专利权人/申请人", "代理机构", "是否已申请国家专利", "对应的国内专利号", "专利名称", "是否已进国家阶段", "授权奖励金额", "授权奖励时间"};
        String[] fieldCodes = {"pctName", "englishName", "applictonNumber", "gjztName", "applictonDay", "openDay",
            "openNumber", "theCountry", "nationalNamber", "nationOpenDay", "nationOpenNumbei", "authorizedNumber",
            "authorizedDay", "theInventor", "thePatentee_theApplicane", "theAgency", "ifAppliedNational",
            "correspondingNumber", "patentName", "ifNationalStage", "rewardAmount", "rewardTime"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JsonPageResult<?> getGjzlList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        addOrder(request, params, "patentName", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                params.put(name, value);
            }
        }
        // 增加分页条件
        if (doPage) {
            addPage(request, params);
        }
        List<JSONObject> gjzlList = zlglmkDao.queryGjzlEXCList(params);
        result.setData(gjzlList);
        return result;
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "专利台账 （模板）.xlsx";
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
            Sheet sheet = wb.getSheet("台账");
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
            Row titleRow = sheet.getRow(1);
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

            JSONObject paramJson = new JSONObject();
            List<JSONObject> enumList = zlglmkDao.getEnumList(paramJson);
            Map<String, String> enumName2Id = new HashMap<>();
            if (enumList != null && !enumList.isEmpty()) {
                for (JSONObject oneEnum : enumList) {
                    enumName2Id.put(oneEnum.getString("enumName"), oneEnum.getString("id"));
                }
            }

            List<JSONObject> deptList = zlglmkDao.queryDeptInfos();
            Map<String, String> deptName2Id = new HashMap<>();
            if (deptList != null && !deptList.isEmpty()) {
                for (JSONObject oneDept : deptList) {
                    deptName2Id.put(oneDept.getString("NAME_"), oneDept.getString("GROUP_ID_"));
                }
            }
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i + 1);
                if (row == null) {
                    break;
                }
                JSONObject rowParse = generateDataFromRow(row, enumName2Id, itemList, titleList, deptName2Id);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < itemList.size(); i++) {
                zlglmkDao.insertZgzlDataExc(itemList.get(i));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, Map<String, String> enumName2Id, List<Map<String, Object>> itemList,
        List<String> titleList, Map<String, String> deptName2Id) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        for (int i = 0; i < titleList.size(); i++) {
            String title = titleList.get(i);
            title = title.replaceAll(" ", "");
            Cell cell = row.getCell(i);
            if (cell == null) {
                continue;
            }
            String cellValue = "";
            cellValue = ExcelUtil.getCellFormatValue(cell);
            switch (title) {
                case "序号":
                    itemRowMap.put("serialNumber", cellValue);
                    break;
                case "部门":
                    String deptId = "";
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!deptName2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "部门“" + cellValue + "”不存在！");
                            return oneRowCheck;
                        }
                        deptId = deptName2Id.get(cellValue);
                    }
                    itemRowMap.put("departmentId", deptId);
                    break;
                case "专业":
                    itemRowMap.put("zhuanYe", cellValue);
                    break;
                case "提案号":
                    itemRowMap.put("billNo", cellValue);
                    break;
                case "提案名称":
                    itemRowMap.put("reportName", cellValue);
                    break;
                case "专利名称":
                    itemRowMap.put("patentName", cellValue);
                    break;
                case "专利类型":
                    String zllxId = "";
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!enumName2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "专利类型“" + cellValue + "”不存在！");
                            return oneRowCheck;
                        }
                        zllxId = enumName2Id.get(cellValue);
                    }
                    itemRowMap.put("zllxId", zllxId);
                    break;
                case "专利（申请）号":
                    itemRowMap.put("applicationNumber", cellValue);
                    break;
                case "发明人":
                    // 根据人员姓名，如果人员在系统中存在重名，则返回失败；否则返回去掉领导之后的姓名和id列表
                    JSONObject userProcess = rdmZhglUtil.toGetUserInfosByNameStr(cellValue, true);
                    if (!userProcess.getBooleanValue("result")) {
                        oneRowCheck.put("message", userProcess.getString("message"));
                        return oneRowCheck;
                    } else {
                        itemRowMap.put("theInventors", userProcess.getString("userNameOriginal"));
                        itemRowMap.put("myCompanyUserIds", userProcess.getString("userIdFilter"));
                        itemRowMap.put("myCompanyUserNames", userProcess.getString("userNameFilter"));
                    }
                    break;
                case "专利权人/申请人名称":
                    itemRowMap.put("thepatentee", cellValue);
                    break;
                case "审批日":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("examinationApproval", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("examinationApproval", null);
                    }
                    break;
                case "委案日":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("byCase", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("byCase", null);
                    }
                    break;
                case "申请日":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("filingdate", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("filingdate", null);
                    }
                    break;
                case "授权通知发文日":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("authorizationDate", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("authorizationDate", null);
                    }
                    break;
                case "授权日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("authionization", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("authionization", null);
                    }
                    break;
                case "专利证书号":
                    // 处理后面加的.0
                    if (StringUtils.isNotBlank(cellValue) && cellValue.contains(".0")) {
                        cellValue = cellValue.split("\\.")[0];
                    }
                    itemRowMap.put("specifyCountry", cellValue);
                    break;
                case "是否存在许可他人使用":
                    String permissionOthersId = "";
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!enumName2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "是否存在许可他人使用“" + cellValue + "”不存在！");
                            return oneRowCheck;
                        }
                        permissionOthersId = enumName2Id.get(cellValue);
                    }
                    itemRowMap.put("permissionOthers", permissionOthersId);
                    break;
                case "被许可使用的单位或人员名称":
                    itemRowMap.put("personPermitted", cellValue);
                    break;
                case "是否质押":
                    String whetherPledgeId = "";
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!enumName2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "是否质押“" + cellValue + "”不存在！");
                            return oneRowCheck;
                        }
                        whetherPledgeId = enumName2Id.get(cellValue);
                    }
                    itemRowMap.put("whetherPledge", whetherPledgeId);
                    break;
                case "权利要求项数":
                    itemRowMap.put("claimsNumber", cellValue);
                    break;
                case "法律状态":
                case "案件状态":
                    String gnztId = "";
                    if (StringUtils.isNotBlank(cellValue)) {
                        if (!enumName2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "法律（案件）状态“" + cellValue + "”不存在！");
                            return oneRowCheck;
                        }
                        gnztId = enumName2Id.get(cellValue);
                    }
                    itemRowMap.put("gnztId", gnztId);
                    break;
                case "专利权丧失发文日":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("patentDate", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("patentDate", null);
                    }
                    break;
                case "失效日期":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("expiryDate", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("expiryDate", null);
                    }
                    break;
                case "失效原因":
                    itemRowMap.put("failureReason", cellValue);
                    break;
                case "代理机构名称":
                    itemRowMap.put("agencyName", cellValue);
                    break;
                case "代理人":
                    itemRowMap.put("agentThe", cellValue);
                    break;
                case "专利所属公司项目或产品名称":
                    itemRowMap.put("productName", cellValue);
                    break;
                case "专利所属公司项目或产品所处阶段":
                    itemRowMap.put("companyBelongs", cellValue);
                    break;
                case "奖励类别":
                    itemRowMap.put("jllb", cellValue);
                    break;
                case "奖励金额":
                    itemRowMap.put("rewardAmount", cellValue);
                    break;
                case "奖励时间":
                    if (StringUtils.isNotBlank(cellValue)) {
                        itemRowMap.put("rewardTime", RdmZhglUtil.toGetImportDateStr(cellValue));
                    } else {
                        itemRowMap.put("rewardTime", null);
                    }
                    break;
                case "技术分支":
                    itemRowMap.put("jsfz", cellValue);
                    break;
                case "备注":
                    itemRowMap.put("beiZhu", cellValue);
                    break;
            }
        }
        itemRowMap.put("zgzlId", IdUtil.getId());
        itemRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemList.add(itemRowMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public void saveTime(String value) {
        CacheUtil.delCache("property_" + "ZLTZ_LAST_TIME");
        zlglmkDao.updateLastUpdateTime(value);
        JSONObject noticeObj = new JSONObject();
        StringBuilder userIdBuilder = new StringBuilder();
        List<Map<String, String>> groupUsers =
            commonInfoManager.queryUserByGroupNameAndRelType(RdmConst.JSZX_LD, "GROUP-USER-BELONG");
        for (Map<String, String> groupUser : groupUsers) {
            userIdBuilder.append(groupUser.get("USER_ID_")).append(",");
        }
        String userIds = userIdBuilder.deleteCharAt(userIdBuilder.length() - 1).toString();
        noticeObj.put("content", "专利数据已更新,请在“RDM系统-综合管理-知识产权管理-中国专利管理”中查阅");
        sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
    }

}
