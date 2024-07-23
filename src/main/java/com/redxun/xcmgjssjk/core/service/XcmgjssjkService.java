package com.redxun.xcmgjssjk.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.HyglInternalService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgjssjk.core.dao.XcmgjssjkDao;
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
import java.util.*;

@Service
public class XcmgjssjkService {
    private static Logger logger = LoggerFactory.getLogger(HyglInternalService.class);
    @Autowired
    private XcmgjssjkDao xcmgjssjkDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;

    public JsonPageResult<?> getSpList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "CREATE_TIME_", "desc");
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
        // 增加分页条件
        // if (doPage) {
        // rdmZhglUtil.addPage(request, params);
        // }
        // 增加角色过滤的条件
        // addJssjkRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<Map<String, Object>> spList = xcmgjssjkDao.querySpList(params);
        for (Map<String, Object> jssjk : spList) {
            if (jssjk.get("jdTime") != null) {
                jssjk.put("jdTime", DateUtil.formatDate((Date)jssjk.get("jdTime"), "yyyy-MM-dd"));
            }
            if (jssjk.get("CREATE_TIME_") != null) {
                jssjk.put("CREATE_TIME_", DateUtil.formatDate((Date)jssjk.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 查询当前处理人
        xcmgProjectManager.setTaskCurrentUser(spList);
        // 过滤条件
        List<Map<String, Object>> finalAllApplyList = null;
        finalAllApplyList = filterList(spList);
        // 根据分页进行subList截取
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
        if (startSubListIndex < finalAllApplyList.size()) {
            finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
        }
        // result.setData(spList);
        // int countSpDataList = xcmgjssjkDao.countSpList(params);
        // result.setTotal(countSpDataList);
        // 返回结果
        result.setData(finalSubApplyList);
        result.setTotal(finalAllApplyList.size());
        return result;
    }

    public JSONObject getJssjkDetail(String jssjkId) {
        JSONObject detailObj = xcmgjssjkDao.queryJssjkById(jssjkId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.get("jdTime") != null) {
            detailObj.put("jdTime", DateUtil.formatDate((Date)detailObj.get("jdTime"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    public JsonResult createJssjk(JSONObject formData) {
        JsonResult result = new JsonResult(true, "操作成功！");
        formData.put("jssjkId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        if (StringUtils.isBlank(formData.getString("jdTime"))) {
            formData.put("jdTime", null);
        }

        formData.put("CREATE_TIME_", new Date());
        xcmgjssjkDao.insertJssjk(formData);
        if (StringUtils.isNotBlank(formData.getString("changeData"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("changeData"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String tdmcId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(tdmcId)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("jssjkId", formData.getString("jssjkId"));
                    oneObject.put("wbName", oneObject.getString("wbName"));
                    oneObject.put("nbIds", oneObject.getString("nbIds"));
                    oneObject.put("nbNames", oneObject.getString("nbNames"));
                    oneObject.put("gznr", oneObject.getString("gznr"));
                    oneObject.put("lxfs", oneObject.getString("lxfs"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    xcmgjssjkDao.insertJssjktdmc(oneObject);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("wbChangeData"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("wbChangeData"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String tdmcId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(tdmcId)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("jssjkId", formData.getString("jssjkId"));
                    oneObject.put("wbName", oneObject.getString("wbName"));
                    oneObject.put("wbdw", oneObject.getString("wbdw"));
                    oneObject.put("ifwb", "yes");
                    oneObject.put("gznr", oneObject.getString("gznr"));
                    oneObject.put("lxfs", oneObject.getString("lxfs"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    xcmgjssjkDao.insertJssjktdmcwb(oneObject);
                }
            }
        }

        result.setData(formData.getString("jssjkId"));
        return  result;
    }

    public JsonResult updateJssjk(JSONObject formData) {
        JsonResult result = new JsonResult(true, "操作成功！");
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        if (StringUtils.isBlank(formData.getString("jdTime"))) {
            formData.put("jdTime", null);
        }
        formData.put("UPDATE_TIME_", new Date());
        xcmgjssjkDao.updateJssjk(formData);
        logger.info("updateJssjk, jssjkId = "+formData.getString("jssjkId"));
        if (StringUtils.isNotBlank(formData.getString("changeData"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("changeData"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String tdmcId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(tdmcId)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("jssjkId", formData.getString("jssjkId"));
                    oneObject.put("wbName", oneObject.getString("wbName"));
                    oneObject.put("nbIds", oneObject.getString("nbIds"));
                    oneObject.put("nbNames", oneObject.getString("nbNames"));
                    oneObject.put("gznr", oneObject.getString("gznr"));
                    oneObject.put("lxfs", oneObject.getString("lxfs"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    xcmgjssjkDao.insertJssjktdmc(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    xcmgjssjkDao.updateJssjktdmc(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    xcmgjssjkDao.deleteJssjktdmc(param);
                }
            }
        }
        if (StringUtils.isNotBlank(formData.getString("wbChangeData"))) {
            JSONArray tdmcDataJson = JSONObject.parseArray(formData.getString("wbChangeData"));
            for (int i = 0; i < tdmcDataJson.size(); i++) {
                JSONObject oneObject = tdmcDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String tdmcId = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(tdmcId)) {
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("jssjkId", formData.getString("jssjkId"));
                    oneObject.put("wbName", oneObject.getString("wbName"));
                    oneObject.put("wbdw", oneObject.getString("wbdw"));
                    oneObject.put("ifwb", "yes");
                    oneObject.put("gznr", oneObject.getString("gznr"));
                    oneObject.put("lxfs", oneObject.getString("lxfs"));
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    xcmgjssjkDao.insertJssjktdmcwb(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    xcmgjssjkDao.updateJssjktdmcwb(oneObject);
                } else if ("removed".equals(state)) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("id", oneObject.getString("id"));
                    xcmgjssjkDao.deleteJssjktdmcwb(param);
                }
            }
        }

        result.setData(formData.getString("jssjkId"));
        return  result;
    }

    public JsonPageResult<?> getJssjkList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "CREATE_TIME_", "desc");
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
            rdmZhglUtil.addPage(request, params);
        }
        String type = RequestUtil.getString(request, "type", "");
        if (StringUtils.isNotBlank(type)) {
            params.put("type", type);
            params.put("userId", ContextUtil.getCurrentUserId());
        }
        List<Map<String, Object>> jssjkList = xcmgjssjkDao.queryJssjkList(params);
        for (Map<String, Object> jssjk : jssjkList) {
            if (jssjk.get("jdTime") != null) {
                jssjk.put("jdTime", DateUtil.formatDate((Date)jssjk.get("jdTime"), "yyyy-MM-dd"));
            }
            if (jssjk.get("CREATE_TIME_") != null) {
                jssjk.put("CREATE_TIME_", DateUtil.formatDate((Date)jssjk.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        result.setData(jssjkList);
        int countJssjkDataList = xcmgjssjkDao.countJssjkList(params);
        result.setTotal(countJssjkDataList);
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
        String filePathBase = WebAppUtil.getProperty("jssjkFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jssjkFilePathBase");
            return;
        }
        try {
            String jssjkId = toGetParamVal(parameters.get("jssjkId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fjlx = toGetParamVal(parameters.get("fjlx"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + jssjkId;
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
            fileInfo.put("jssjkId", jssjkId);
            fileInfo.put("fjlx", fjlx);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            xcmgjssjkDao.addFileInfos(fileInfo);
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


    public JsonResult deleteJssjk(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jssjkIds = Arrays.asList(ids);
        List<JSONObject> files = getJssjkFileList(jssjkIds);
        String jssjkFilePathBase = WebAppUtil.getProperty("jssjkFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                oneFile.getString("jssjkId"), jssjkFilePathBase);
        }
        for (String oneJsmmId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJsmmId, jssjkFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jssjkIds", jssjkIds);
        xcmgjssjkDao.deleteJssjkFile(param);
        xcmgjssjkDao.deleteJssjk(param);
        xcmgjssjkDao.deleteJssjktdmc(param);
        return result;
    }

    public List<JSONObject> getJssjkFileList(List<String> jssjkIdList) {
        List<JSONObject> jssjkFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jssjkIds", jssjkIdList);
        jssjkFileList = xcmgjssjkDao.queryJssjkFileList(param);
        return jssjkFileList;
    }

    public void deleteOneJssjkFile(String fileId, String fileName, String jssjkId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jssjkId,
            WebAppUtil.getProperty("jssjkFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        xcmgjssjkDao.deleteJssjkFile(param);
    }

    public JSONObject getJssjkCp(String jssjkId) {
        if (StringUtils.isBlank(jssjkId)) {
            return null;
        }
        List<JSONObject> detailObj = xcmgjssjkDao.queryJssjkByIdCp(jssjkId);
        if (!detailObj.isEmpty()) {
            return detailObj.get(0);
        }
        return null;
    }

    public List<JSONObject> getJssjkCpFile(String jssjkId) {
        List<JSONObject> detailObj = xcmgjssjkDao.queryJssjkByIdCpFile(jssjkId);
        return detailObj;
    }

    public List<JSONObject> getTdmcList(String jssjkId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("jssjkId", jssjkId);
        List<JSONObject> reasonList = xcmgjssjkDao.getTdmcList(paramJson);
        return reasonList;
    }

    private List<Map<String, Object>> filterList(List<Map<String, Object>> rjzzList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (rjzzList == null || rjzzList.isEmpty()) {
            return result;
        }
        if ("admin".equalsIgnoreCase(ContextUtil.getCurrentUser().getUserNo())) {
            return rjzzList;
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        boolean isJssjkzy = rdmZhglUtil.judgeIsPointRoleUser(currentUserId, "技术数据库专员");

        for (Map<String, Object> oneProject : rjzzList) {
            // 自己是当前流程处理人
            if (oneProject.get("status") != null && !"DRAFTED".equals(oneProject.get("status").toString())) {
                if (isJssjkzy) {
                    result.add(oneProject);
                } else if (oneProject.get("currentProcessUserId") != null
                    && oneProject.get("currentProcessUserId").toString().contains(currentUserId)) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 自己的查看自己的草稿
                if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                    result.add(oneProject);
                }
            }

        }
        return result;
    }

    public String toGetJssjkNumber(String jsly, String jsfx) {
        String nowYearMonth = DateFormatUtil.getNowByString("yyyyMM");
        String finalNowYearMonth = nowYearMonth.substring(2);
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", finalNowYearMonth);
        JSONObject maxJssjk = xcmgjssjkDao.queryMaxJssjkNum(param);
        int existNumber = 0;
        if (maxJssjk != null) {
            existNumber = Integer.parseInt(maxJssjk.getString("jsNum").split("-", -1)[3]);
        }
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return jsly + "-" + jsfx + "-" + finalNowYearMonth + "-00" + thisNumber;
        } else if (thisNumber < 100) {
            return jsly + "-" + jsfx + "-" + finalNowYearMonth + "-0" + thisNumber;
        } else {
            return jsly + "-" + jsfx + "-" + finalNowYearMonth + "-" + thisNumber;
        }
    }

    public List<JSONObject> getTdmcwbList(String jssjkId) {
        JSONObject paramJson = new JSONObject();
        paramJson.put("jssjkId", jssjkId);
        List<JSONObject> reasonList = xcmgjssjkDao.getTdmcwbList(paramJson);
        return reasonList;
    }
}
