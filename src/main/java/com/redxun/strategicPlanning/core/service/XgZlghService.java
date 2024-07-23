package com.redxun.strategicPlanning.core.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.strategicPlanning.core.dao.XgZlghDao;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class XgZlghService {
    private static Logger logger = LoggerFactory.getLogger(XgZlghService.class);
    @Autowired
    private XgZlghDao xgZlghDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getZlghList(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String type= RequestUtil.getString(request,"type","");
        params.put("type",type);
        rdmZhglUtil.addOrder(request, params, "prizeTime", "desc");
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
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }

        List<Map<String, Object>> zlghList = xgZlghDao.queryZlghList(params);
        for (Map<String, Object> zlgh : zlghList) {
            if (zlgh.get("ghwcTime") != null) {
                zlgh.put("ghwcTime", DateUtil.formatDate((Date)zlgh.get("ghwcTime"), "yyyy-MM-dd"));
            }
            if (zlgh.get("ghyxqs") != null) {
                zlgh.put("ghyxqs", DateUtil.formatDate((Date)zlgh.get("ghyxqs"), "yyyy-MM-dd"));
            }
            if (zlgh.get("ghyxqe") != null) {
                zlgh.put("ghyxqe", DateUtil.formatDate((Date)zlgh.get("ghyxqe"), "yyyy-MM-dd"));
            }
        }

        result.setData(zlghList);
        int countZlghList = xgZlghDao.countZlghList(params);
        result.setTotal(countZlghList);
        return result;
    }
    public Map<String, Object> queryZlghById(String zlghId) {
        Map<String, Object> param = new HashMap<>();
        param.put("zlghId", zlghId);
        Map<String, Object> oneInfo = xgZlghDao.queryZlghById(param);
        return oneInfo;
    }
    public void saveZlgh(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            Map<String, String[]> parameters = request.getParameterMap();
            String zlghId = RequestUtil.getString(request, "zlghId");
            String type = RequestUtil.getString(request, "type");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            Map<String, Object> objBody = new HashMap<>();
            addOrUpdateZlgh(parameters, objBody, zlghId,type);
            result.put("message", "保存成功！");
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveLbj", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }
    private void addOrUpdateZlgh(Map<String, String[]> parameters, Map<String, Object> objBody,String zlghId
                                ,String type) throws IOException {

        if (StringUtils.isBlank(zlghId)) {
            // 新增
            String newId = IdUtil.getId();
            JSONObject zlghInfo = new JSONObject();
            zlghInfo.put("zlghId", newId);
            zlghInfo.put("zlghName", parameters.get("zlghName")[0]);
            zlghInfo.put("ghnr", parameters.get("ghnr")[0]);
            zlghInfo.put("ghzgbmId", parameters.get("ghzgbmId")[0]);
            zlghInfo.put("ghzgbmName", parameters.get("ghzgbmName")[0]);
            zlghInfo.put("ghfzrId", parameters.get("ghfzrId")[0]);
            zlghInfo.put("ghfzrName", parameters.get("ghfzrName")[0]);
            zlghInfo.put("ghnf", parameters.get("ghnf")[0]);
            String ghwcTime = parameters.get("ghwcTime")[0];
            try {
                String dateStr = ghwcTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghwcTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            zlghInfo.put("ghbb", parameters.get("ghbb")[0]);
            String ghyxqs = parameters.get("ghyxqs")[0];
            try {
                String dateStr = ghyxqs.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghyxqs", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            String ghyxqe = parameters.get("ghyxqe")[0];
            try {
                String dateStr = ghyxqe.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghyxqe", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            zlghInfo.put("xbbmId", parameters.get("xbbmId")[0]);
            zlghInfo.put("xbbmName", parameters.get("xbbmName")[0]);
            zlghInfo.put("xbbmfzrId", parameters.get("xbbmfzrId")[0]);
            zlghInfo.put("xbbmfzrName", parameters.get("xbbmfzrName")[0]);
            zlghInfo.put("remark", parameters.get("remark")[0]);
            zlghInfo.put("type", type);
            zlghInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            zlghInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xgZlghDao.saveZlghList(zlghInfo);

        } else {
            JSONObject zlghInfo = new JSONObject();
            zlghInfo.put("zlghId", zlghId);
            zlghInfo.put("zlghName", parameters.get("zlghName")[0]);
            zlghInfo.put("ghnr", parameters.get("ghnr")[0]);
            zlghInfo.put("ghzgbmId", parameters.get("ghzgbmId")[0]);
            zlghInfo.put("ghzgbmName", parameters.get("ghzgbmName")[0]);
            zlghInfo.put("ghfzrId", parameters.get("ghfzrId")[0]);
            zlghInfo.put("ghfzrName", parameters.get("ghfzrName")[0]);
            zlghInfo.put("ghnf", parameters.get("ghnf")[0]);
            String ghwcTime = parameters.get("ghwcTime")[0];
            try {
                String dateStr = ghwcTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghwcTime", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            zlghInfo.put("ghbb", parameters.get("ghbb")[0]);
            String ghyxqs = parameters.get("ghyxqs")[0];
            try {
                String dateStr = ghyxqs.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghyxqs", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            String ghyxqe = parameters.get("ghyxqe")[0];
            try {
                String dateStr = ghyxqe.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                Date date = sdf.parse(dateStr);
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                zlghInfo.put("ghyxqe", sdf1.format(date));
            } catch (Exception e) {
                logger.error("时间格式转化异常");
            }
            zlghInfo.put("xbbmId", parameters.get("xbbmId")[0]);
            zlghInfo.put("xbbmName", parameters.get("xbbmName")[0]);
            zlghInfo.put("xbbmfzrId", parameters.get("xbbmfzrId")[0]);
            zlghInfo.put("xbbmfzrName", parameters.get("xbbmfzrName")[0]);
            zlghInfo.put("remark", parameters.get("remark")[0]);
            zlghInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            zlghInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            xgZlghDao.updateZlghList(zlghInfo);
        }
    }
    public JSONArray getFiles(HttpServletRequest request) {
        String standardId = RequestUtil.getString(request, "standardId");
        String fileName = RequestUtil.getString(request, "fileName");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(standardId)) {
            params.put("standardId", standardId);
        }
        if (StringUtils.isNotBlank(fileName)) {
            params.put("fileName", fileName);
        }
        JSONArray fileArray = xgZlghDao.getFiles(params);
        return fileArray;
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

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("zlghFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
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
            // 写入数据库-证书附件表
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("zlghId", standardId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            xgZlghDao.addFileInfos(fileInfo);
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
    public JsonResult deleteZlgh(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> zlghIds = Arrays.asList(ids);
        List<JSONObject> files = getZlghFileList(zlghIds);
        String zlghFilePathBase = WebAppUtil.getProperty("zlghFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("zlghId"), zlghFilePathBase);
        }
        for (String oneZlghId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneZlghId, zlghFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("zlghIds", zlghIds);
        xgZlghDao.deleteZlghFile(param);
        xgZlghDao.deleteZlgh(param);
        return result;
    }
    public List<JSONObject> getZlghFileList(List<String> zlghIdList) {
        List<JSONObject> zlghFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("zlghIds", zlghIdList);
        zlghFileList = xgZlghDao.queryZlghFileList(param);
        return zlghFileList;
    }
}
