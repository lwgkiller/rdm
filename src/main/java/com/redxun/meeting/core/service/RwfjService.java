package com.redxun.meeting.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.meeting.core.dao.RwfjDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class RwfjService {
    private Logger logger = LogManager.getLogger(RwfjService.class);
    @Autowired
    private RwfjDao rwfjDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private HyglService hyglService;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    public JsonPageResult<?> queryRwfj(HttpServletRequest request, boolean doPage) {
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
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        // 对params进行处理，区别出权限
        addRwfjRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> rwfjList = rwfjDao.queryRwfj(params);
        for (JSONObject rwfj : rwfjList) {
            if (rwfj.get("CREATE_TIME_") != null) {
                rwfj.put("CREATE_TIME_", DateUtil.formatDate((Date)rwfj.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(rwfjList, ContextUtil.getCurrentUserId());
        result.setData(rwfjList);
        int countRwfjDataList = rwfjDao.countRwfjList(params);
        result.setTotal(countRwfjDataList);
        return result;
    }

    private void addRwfjRoleParam(Map<String, Object> params, String userId, String userNo) {
        // 如果是管理员直接返回，给予所有查看权限
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        // 如果是“所有数据查看权限”，分管领导、技术中心主任、会议管理员给与所有查看权限
        boolean allDataQuery =
            rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.AllDATA_QUERY_NAME);
        boolean isFgld = rdmZhglUtil.judgeUserIsFgld(userId);// 分管领导
        boolean isJSZR = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.JSZX_ZR);// 技术中心主任
        boolean hyglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.HYGLZY);// 会议管理专员
        if (allDataQuery || isFgld || isJSZR || hyglzy) {
            return;
        }
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (currentUserDepInfo.getString("result").equalsIgnoreCase("success")
            && currentUserDepInfo.getBooleanValue("isDepRespMan")) {
            params.put("roleName", "deptRespUser");
            params.put("currentUserMainDepId", currentUserDepInfo.getString("currentUserMainDepId"));
        } else {
            // 部门推进专员，跟部门负责人一样的权限
            boolean bmhyglzy = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.BMHYGLZY);
            if (bmhyglzy) {
                params.put("roleName", "deptRespUser");
                params.put("currentUserMainDepId", ContextUtil.getCurrentUser().getMainGroupId());
            } else {
                params.put("roleName", "ptyg");
            }
        }
    }

    public void createRwfj(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        rwfjDao.createRwfj(formData);
    }

    public void updateRwfj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        rwfjDao.updateRwfj(formData);
    }

    public void saveRwfjUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("rwfjFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find rwfjFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("id"));
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
            rwfjDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getRwfjDetail(String id) {
        JSONObject detailObj = rwfjDao.queryRwfjById(id);
        if (detailObj == null) {
            return new JSONObject();
        }
        Set<String> userIdSet = new HashSet<>();
        if (StringUtils.isNotBlank(detailObj.getString("meetingUserIds"))) {
            userIdSet.addAll(Arrays.asList(detailObj.getString("meetingUserIds").split(",", -1)));
        }
        if (!userIdSet.isEmpty()) {
            Map<String, String> userId2Name = hyglService.queryUserNameByIds(userIdSet);
            if (StringUtils.isNotBlank(detailObj.getString("meetingUserIds"))) {
                List<String> userIds = Arrays.asList(detailObj.getString("meetingUserIds").split(",", -1));
                String meetingUserNames = hyglService.toGetUserNamesByIds(userId2Name, userIds);
                detailObj.put("meetingUserNames", meetingUserNames);
            }
        }

        Set<String> otherIdSet = new HashSet<>();
        if (StringUtils.isNotBlank(detailObj.getString("otherPlanRespUserIds"))) {
            otherIdSet.addAll(Arrays.asList(detailObj.getString("otherPlanRespUserIds").split(",", -1)));
        }
        if (!otherIdSet.isEmpty()) {
            Map<String, String> userId2Name = hyglService.queryUserNameByIds(otherIdSet);
            if (StringUtils.isNotBlank(detailObj.getString("otherPlanRespUserIds"))) {
                List<String> userIds = Arrays.asList(detailObj.getString("otherPlanRespUserIds").split(",", -1));
                String otherPlanRespUserName = hyglService.toGetUserNamesByIds(userId2Name, userIds);
                detailObj.put("otherPlanRespUserName", otherPlanRespUserName);
            }
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getRwfjFileList(String belongId) {
        List<JSONObject> rwfjFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("id", belongId);
        rwfjFileList = rwfjDao.queryRwfjFileList(param);
        return rwfjFileList;
    }

    public void deleteOneRwfjFile(String fileId, String fileName, String id) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, id, WebAppUtil.getProperty("rwfjFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        rwfjDao.deleteRwfjFile(param);
    }

    public JsonResult deleteRwfj(String[] idsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String id : idsArr) {
            // 删除基本信息
            List<JSONObject> files = getRwfjFileList(id);
            String wjFilePathBase = WebAppUtil.getProperty("rwfjFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                    oneFile.getString("belongId"), wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(id, wjFilePathBase);
            param.put("id", id);
            rwfjDao.deleteRwfjFile(param);
            rwfjDao.deleteRwfj(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;

    }

    public ResponseEntity<byte[]> importDownload() {
        try {
            String fileName = "所别-科室-机型-供应商-物料-申请人.doc";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/rwfj/" + fileName).toURI());
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

}
