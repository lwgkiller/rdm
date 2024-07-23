package com.redxun.info.core.manager;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.redxun.info.core.dao.QbgzDao;
import com.redxun.info.core.dao.QbsjDao;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class QbsjService {
    private Logger logger = LogManager.getLogger(QbsjService.class);
    @Autowired
    private QbgzDao qbgzDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private CommonInfoManager commonInfoManager;
    @Autowired
    private QbsjDao qbsjDao;

    public JsonPageResult<?> queryQbsj(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "qbsj_baseInfo.CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
//        addQbgzRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> qbgzList = qbsjDao.queryQbsj(params);
        for (Map<String, Object> qbgz : qbgzList) {
            if (qbgz.get("CREATE_TIME_") != null) {
                qbgz.put("CREATE_TIME_", DateUtil.formatDate((Date)qbgz.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }

        xcmgProjectManager.setTaskCurrentUser(qbgzList);
        result.setData(qbgzList);
        int countQbgzDataList = qbsjDao.countQbsj(params);
        result.setTotal(countQbgzDataList);
        return result;
    }

    private void addQbgzRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);
        if (isFgld) {
            params.put("roleName", "fgld");
            return;
        }
        boolean isZlgcs = rdmZhglUtil.judgeIsPointRoleUser(userId, "情报专员");
        if (isZlgcs) {
            params.put("roleName", "qbzy");
            return;
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (currentUserDepInfo.getString("result").equalsIgnoreCase("success")
            && currentUserDepInfo.getBooleanValue("isDepRespMan")) {
            params.put("roleName", "deptRespUser");
            params.put("deptId", currentUserDepInfo.getString("currentUserMainDepId"));
            return;
        }
        params.put("roleName", "ptyg");
    }

    public String toGetQbsjNumber() {
        String qbNum = null;
        String nowYearMonth = DateFormatUtil.getNowByString("yyyy-MM");
        String nowYearStart = nowYearMonth + "-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        JSONObject maxQbgz = qbsjDao.queryMaxQbsjNum(param);
        int existNumber = 0;
        if (maxQbgz != null) {
            existNumber = Integer.parseInt(maxQbgz.getString("qbNumber").substring(8));
        }
        int thisNumber = existNumber + 1;
        String temp=DateFormatUtil.getNowByString("yyyyMM");
        if (thisNumber < 10) {
            qbNum = "QB" + temp + "00" + thisNumber;
        } else if (thisNumber < 100 && thisNumber >= 10) {
            qbNum = "QB" + temp + "0" + thisNumber;
        } else if (thisNumber < 1000 && thisNumber >= 100) {
            qbNum = "QB" + temp + thisNumber;
        } else if (thisNumber < 10000 && thisNumber >= 1000) {
            qbNum = "QB" + temp + thisNumber;
        }
        return qbNum;
    }

    public void createQbsj(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        qbsjDao.createQbsj(formData);
    }

    public void updateQbsj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        qbsjDao.updateQbsj(formData);
    }

    public void saveQbsjUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("qbsjFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find qbsjFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("qbgzId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));

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
            fileInfo.put("id", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("qbBaseInfoId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            qbsjDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getQbsjDetail(String qbgzId) {
        JSONObject detailObj = qbsjDao.queryQbsjById(qbgzId);
        if (detailObj == null) {
            return new JSONObject();
        }

        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getQbsjFileList(List<String> qbgzIdList) {
        List<JSONObject> qbgzFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("qbgzIds", qbgzIdList);
        qbgzFileList = qbsjDao.queryQbsjFileList(param);
        return qbgzFileList;
    }

    public void deleteOneQbgzFile(String fileId, String fileName, String qbgzId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, qbgzId, WebAppUtil.getProperty("qbsjFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        param.put("qbgzIds", Arrays.asList(qbgzId));
        qbsjDao.deleteQbsjFile(param);
    }

    public JsonResult deleteQbsj(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> qbgzIds = Arrays.asList(ids);
        List<JSONObject> files = getQbsjFileList(qbgzIds);
        String qbsjFilePathBase = WebAppUtil.getProperty("qbsjFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("qbBaseInfoId"), qbsjFilePathBase);
        }
        for (String oneJstbId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJstbId, qbsjFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("qbgzIds", qbgzIds);
        qbsjDao.deleteQbsjFile(param);
        qbsjDao.deleteQbsj(param);
        return result;
    }

    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "情报报告.docx";
            // 创建文件实例
            File file = new File(QbsjService.class.getClassLoader().getResource("templates/qbgc/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers,
                org.springframework.http.HttpStatus.OK);
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject queryProvideChart(String timeFrom, String timeTo) {
        JSONObject result = new JSONObject();
        Map<String, String> deptId2Name = commonInfoManager.queryDeptUnderJSZX();
        List<String> depts = new ArrayList<>(deptId2Name.values());
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(timeTo)) {
            params.put("timeTo", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeTo), 16)));
        }
        if (StringUtils.isNotBlank(timeFrom)) {
            params.put("timeFrom", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeFrom), -8)));
        }
        List<JSONObject> resultMap = qbgzDao.queryProvide(params);
        if (resultMap == null) {
            resultMap = Collections.emptyList();
        }
        Map<String, Long> dept2Num = new HashMap<>();
        if (resultMap != null && !resultMap.isEmpty()) {
            for (Map<String, Object> oneMap : resultMap) {
                String dept = oneMap.get("deptName").toString();
                Long num = (Long)oneMap.get("countNumber");
                dept2Num.put(dept, num);
            }
        }
        List<Long> resultData = new ArrayList<>();
        for (int i = 0; i < depts.size(); i++) {
            if (dept2Num.containsKey(depts.get(i))) {
                resultData.add(dept2Num.get(depts.get(i)));
            } else {
                resultData.add(0L);
            }
        }
        result.put("data", resultData);
        result.put("depts", depts);
        return result;
    }

    public JSONObject queryType(String timeFrom, String timeTo) {
        JSONObject result = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        if (StringUtils.isNotBlank(timeTo)) {
            params.put("timeTo", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeTo), 16)));
        }
        if (StringUtils.isNotBlank(timeFrom)) {
            params.put("timeFrom", DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(timeFrom), -8)));
        }
        List<JSONObject> resultMap = qbgzDao.queryType(params);
        if (resultMap == null) {
            resultMap = Collections.emptyList();
        }
        result.put("data", resultMap);
        return result;
    }

    public List<JSONObject> querySmallTypeByBigType(String bigTypeName) {
        List<JSONObject> smallTypeList = new ArrayList<>();
        JSONObject params = new JSONObject();
        params.put("bigTypeName", bigTypeName);
        smallTypeList = qbsjDao.queryTypeList(params);
        return smallTypeList;
    }
}
