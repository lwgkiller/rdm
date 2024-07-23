package com.redxun.strategicPlanning.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.strategicPlanning.core.dao.CpxpghDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import org.apache.commons.lang.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CpxpghService {
    private static Logger logger = LoggerFactory.getLogger(CpxpghService.class);
    @Autowired
    private CpxpghDao cpxpghDao;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    //..
    public JsonPageResult<?> cpxpghListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addCpxpghRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List <JSONObject> cpxpghList = cpxpghDao.cpxpghListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject cpxpgh : cpxpghList) {
            if (cpxpgh.get("CREATE_TIME_") != null) {
                cpxpgh.put( "CREATE_TIME_", sdf.format(cpxpgh.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(cpxpghList, ContextUtil.getCurrentUserId());
        result.setData(cpxpghList);
        int count = cpxpghDao.countCpxpghQuery(params);
        result.setTotal(count);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、其他人看所有提交的，自己的；
    private void addCpxpghRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isAllDataQuery = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
        if (isAllDataQuery) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    public String insertCpxpgh(JSONObject formData) {
        String id = IdUtil.getId();
        formData.put("id", id);
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        cpxpghDao.insertCpxpgh(formData);
        return id;
    }

    public void updateCpxpgh(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        cpxpghDao.updateCpxpgh(formDataJson);
    }

    public JSONObject getCpxpghDetail(String cpxpghId) {
        JSONObject cpxpghDetail = cpxpghDao.getCpxpghDetail(cpxpghId);
        if (cpxpghDetail == null) {
            return new JSONObject();
        }
        return cpxpghDetail;
    }

    public List<JSONObject> queryChilds (JSONObject jsonObject) {
        return cpxpghDao.queryChilds(jsonObject);
    }

    public void insertChilds(Map<String, Object> params) {
        cpxpghDao.insertChilds(params);
    }

    public JsonResult delChildsById(String id) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject param = new JSONObject();
        param.put("id", id);
        cpxpghDao.delChildsById(param);
        return result;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map <String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map <String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = WebAppUtil.getProperty("cpxpghFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find cpxpghFilePathBase");
            return;
        }
        try {
            String cpxpghId = toGetParamVal(parameters.get("cpxpghId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + cpxpghId;
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
            fileInfo.put("cpxpghId", cpxpghId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            cpxpghDao.addFileInfos(fileInfo);
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

    public List <JSONObject> queryCpxpghFileList(Map <String, Object> params) {
        List <JSONObject> fileList = cpxpghDao.queryCpxpghFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put( "CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }

    public void delCpxpghFiles(String id, String fileName, String cpxpghId) {
        String filePath = WebAppUtil.getProperty("cpxpghFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id,fileName,cpxpghId,filePath);
        cpxpghDao.delCpxpghFiles(id);
    }

    public JsonResult deleteCpxpgh(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", businessIds);
        param.put("cpxpghIds", businessIds);
        cpxpghDao.delChildsByCpxpghId(param);
        cpxpghDao.deleteCpxpgh(param);
        return result;
    }


    public String insertCpxpghFinal(JSONObject formData) {
        String id = IdUtil.getId();
        formData.put("id", id);
        formData.put("CREATE_TIME_", new Date());
        cpxpghDao.insertCpxpgh(formData);
        return id;
    }

    public JsonPageResult <?> cpxpghFinalListQuery(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        List <JSONObject> cpxpghFinalList = cpxpghDao.cpxpghFinalListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject cpxpghFinal : cpxpghFinalList) {
            if (cpxpghFinal.get("CREATE_TIME_") != null) {
                cpxpghFinal.put( "CREATE_TIME_", sdf.format(cpxpghFinal.get("CREATE_TIME_")));
            }
        }
        result.setData(cpxpghFinalList);
        int count = cpxpghDao.countCpxpghFinalQuery(params);
        result.setTotal(count);
        return result;
    }

    // 是否技术状态确认人员
    public String isJsztqrUsers() {
        String userNo = ContextUtil.getCurrentUser().getUserNo();
        if (userNo.equals("admin")) {
            return "";
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("groupName", "产品型谱技术管理部产品主管");
        roleParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> cpzgUserInfos = xcmgProjectOtherDao.queryUserByGroupName(roleParams);
        if (cpzgUserInfos != null && !cpzgUserInfos.isEmpty()) {
            for (Map<String, String> user : cpzgUserInfos) {
                if (user.get("USER_ID_").equals(currentUserId)) {
                    return "cpxpjsglbcpzg";
                }
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", RdmConst.BZJSS_NAME);
        List<Map<String, String>> bzrzyjsfzrs = xcmgProjectOtherDao.getDepRespManById(params);
        if (bzrzyjsfzrs != null  && !cpzgUserInfos.isEmpty()) {
            for (Map<String, String> user : bzrzyjsfzrs) {
                if (user.get("PARTY2_").equals(currentUserId)) {
                    return "bzrzyjsfzr";
                }
            }
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "服务工程技术研究所");
        List<Map<String, String>> fwgcfzrs = xcmgProjectOtherDao.getDepRespManById(params);
        if (bzrzyjsfzrs != null  && !cpzgUserInfos.isEmpty()) {
            for (Map<String, String> user : fwgcfzrs) {
                if (user.get("PARTY2_").equals(currentUserId)) {
                    return "fwgcfzr";
                }
            }
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "智能控制研究所");
        List<Map<String, String>> znkzyjsfzrs = xcmgProjectOtherDao.getDepRespManById(params);
        if (bzrzyjsfzrs != null  && !cpzgUserInfos.isEmpty()) {
            for (Map<String, String> user : znkzyjsfzrs) {
                if (user.get("PARTY2_").equals(currentUserId)) {
                    return "znkzyjsfzr";
                }
            }
        }
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        params.put("groupName", "测试研究所");
        List<Map<String, String>> csyjsfzrs = xcmgProjectOtherDao.getDepRespManById(params);
        if (bzrzyjsfzrs != null  && !cpzgUserInfos.isEmpty()) {
            for (Map<String, String> user : csyjsfzrs) {
                if (user.get("PARTY2_").equals(currentUserId)) {
                    return "csyjsfzr";
                }
            }
        }
        return "";
    }

    public List<JSONObject> queryChildsBycpxpghId(String cpxpghId) {
        return cpxpghDao.queryChildsBycpxpghId(cpxpghId);
    }
    public void delChildsByCpxpghId(Map<String, Object> params) {
        cpxpghDao.delChildsByCpxpghId(params);
    }

    public Integer wheterRunning(String finalId){
        int i = cpxpghDao.wheterRunning(finalId);
        return cpxpghDao.wheterRunning(finalId);
    }

    public List <JSONObject> queryHistoryData(String finalId) {
        Map<String, Object> params = new HashMap <>();
        params.put("sortField", "CREATE_TIME_");
        params.put("sortOrder", "asc");
        params.put("finalId", finalId);
        params.put("taskStatus", "SUCCESS_END");
        List <JSONObject> cpxpghList = cpxpghDao.cpxpghListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject cpxpgh : cpxpghList) {
            if (cpxpgh.get("CREATE_TIME_") != null) {
                cpxpgh.put( "CREATE_TIME_", sdf.format(cpxpgh.get("CREATE_TIME_")));
            }
        }
        return cpxpghList;
    }

    public JsonResult discardCpxpghInst(String finalId) {
        JsonResult result = new JsonResult(true, "");
        JSONObject cpxpghDetail = cpxpghDao.getCpxpghDetail(finalId);
        cpxpghDetail.put("changeStatus","wbg");
        Map<String, Object> params = new HashMap <>();
        params.put("finalId", finalId);
        params.put("taskStatus", "SUCCESS_END");
        Integer count  = cpxpghDao.countCpxpghQuery(params);
        if (count > 1) {
            cpxpghDetail.put("changeStatus","bgwc");
        }
        cpxpghDetail.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        cpxpghDetail.put("UPDATE_TIME_", new Date());
        cpxpghDao.updateCpxpgh(cpxpghDetail);
        return result;
    }

    //是否战略规划专员
    public boolean isZlghzy() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupName", "战略规划专员");
        List<Map<String, String>> userInfos = xcmgProjectOtherDao.queryUserByGroupName(params);
        String currentUserId = ContextUtil.getCurrentUserId();
        if (userInfos.size() > 0) {
            for (Map<String,String> userInfo : userInfos) {
                if (currentUserId.equals(userInfo.get("USER_ID_"))) {
                    return true;
                }
            }
        }
        return  false;
    }
}
