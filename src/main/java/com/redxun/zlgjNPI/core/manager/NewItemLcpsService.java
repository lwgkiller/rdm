package com.redxun.zlgjNPI.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import com.redxun.zlgjNPI.core.dao.NewItemLcpsDao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class NewItemLcpsService {
    private Logger logger = LoggerFactory.getLogger(NewItemLcpsService.class);
    @Autowired
    private NewItemLcpsDao newItemLcpsDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public JsonPageResult<?> getXplcList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> param = new HashMap<>();
            String filterParams = request.getParameter("filter");
            if (StringUtils.isNotBlank(filterParams)) {
                JSONArray jsonArray = JSONArray.parseArray(filterParams);
                for (int i = 0; i < jsonArray.size(); i++) {
                    String name = jsonArray.getJSONObject(i).getString("name");
                    String value = jsonArray.getJSONObject(i).getString("value");
                    if (StringUtils.isNotBlank(value)) {
                        param.put(name, value);
                    }
                }
            }
            rdmZhglUtil.addOrder(request, param, "CREATE_TIME_", "desc");
            List<JSONObject> xplcList = newItemLcpsDao.queryXplcList(param);
            for (JSONObject oneObject : xplcList) {
                if (oneObject.getDate("CREATE_TIME_") != null){
                    oneObject.put("CREATE_TIME_", DateFormatUtil.format(oneObject.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
            // 向业务数据中写入任务相关的信息
            rdmZhglUtil.setTaskInfo2Data(xplcList, ContextUtil.getCurrentUserId());
            // 根据角色过滤
            xplcList = filterListByDepRole(xplcList);
            List<JSONObject> finalSubList = new ArrayList<JSONObject>();
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < xplcList.size()) {
                finalSubList = xplcList.subList(startSubListIndex,
                        endSubListIndex < xplcList.size() ? endSubListIndex : xplcList.size());
            }
            result.setData(finalSubList);
            result.setTotal(xplcList.size());
        } catch (Exception e) {
            logger.error("Exception in queryCxDevList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }
    private List<JSONObject> filterListByDepRole(List<JSONObject> xplcList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (xplcList == null || xplcList.isEmpty()) {
            return result;
        }
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            return xplcList;
        }
        // 过滤
        for (JSONObject oneProject : xplcList) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneProject.getString("myTaskId"))) {
                result.add(oneProject);
            } else if (StringUtils.isNotBlank(oneProject.getString("instStatus"))
                    && "DRAFTED".equals(oneProject.getString("instStatus"))) {
                if (oneProject.getString("CREATE_BY_").equals(ContextUtil.getCurrentUserId())) {
                    result.add(oneProject);
                }
            } else {
                result.add(oneProject);
            }
        }
        return result;
    }
    public void createLcps(JSONObject formData) {
        formData.put("xplcId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        newItemLcpsDao.insertLcps(formData);
    }
    public void updateLcps(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        newItemLcpsDao.updateLcps(formData);
    }
    public JSONObject getXplcDetail(String xplcId) {
        JSONObject detailObj = newItemLcpsDao.queryXplcById(xplcId);
        return detailObj;
    }
    public List<JSONObject> getXplcFileList(List<String> xplcIdList) {
        List<JSONObject> xplcFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("xplcIds", xplcIdList);
        xplcFileList = newItemLcpsDao.queryXplcFileList(param);
        return xplcFileList;
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
        String filePathBase = WebAppUtil.getProperty("xplcFilePathBase");
        String filePathBase_view = WebAppUtil.getProperty("xplcFilePathBase_preview");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find zljsjdsFilePathBase");
            return;
        }
        try {
            String xplcId = toGetParamVal(parameters.get("xplcId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + xplcId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);
            //写入预览目录
            String filePath_view = filePathBase_view + File.separator + xplcId;
            File pathFile_view = new File(filePath_view);
            if (!pathFile_view.exists()) {
                pathFile_view.mkdirs();
            }
            String fileFullPath_view = filePath_view + File.separator + id + "." + suffix;
            File file_view = new File(fileFullPath_view);
            FileCopyUtils.copy(mf.getBytes(), file_view);
            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("xplcId", xplcId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            newItemLcpsDao.addXplcFileInfos(fileInfo);
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
    public void deleteOneXplcFile(String fileId, String fileName, String xplcId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, xplcId,
                WebAppUtil.getProperty("xplcFilePathBase"));
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, xplcId,
                WebAppUtil.getProperty("xplcFilePathBase_preview"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        newItemLcpsDao.deleteXplcFile(param);
    }
    // 保存（包括新增保存、编辑保存）
    public void saveXplcxx(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameters = request.getParameterMap();
            String xplcId = RequestUtil.getString(request, "xplcId");
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                result.put("message", "操作失败，表单内容为空！");
                result.put("success", false);
                return;
            }
            String id = parameters.get("id")[0];

            if ("".equals(id)) {

                //用户信息添加
                JSONObject userInfo = new JSONObject();
                userInfo.put("id", IdUtil.getId());
                userInfo.put("xplcId", xplcId);
                userInfo.put("product", parameters.get("product")[0]);
                userInfo.put("jixin", parameters.get("jixin")[0]);
                userInfo.put("lbj", parameters.get("lbj")[0]);
                userInfo.put("wtqd", parameters.get("wtqd")[0]);
                userInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                userInfo.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                newItemLcpsDao.saveXplcxx(userInfo);
            } else {
                JSONObject userInfo = new JSONObject();
                userInfo.put("id", id);
                userInfo.put("product", parameters.get("product")[0]);
                userInfo.put("jixin", parameters.get("jixin")[0]);
                userInfo.put("lbj", parameters.get("lbj")[0]);
                userInfo.put("wtqd", parameters.get("wtqd")[0]);
                userInfo.put("yhcs", parameters.get("yhcs")[0]);
                userInfo.put("bmId", parameters.get("bmId")[0]);
                userInfo.put("bmName", parameters.get("bmName")[0]);
                userInfo.put("zrrId", parameters.get("zrrId")[0]);
                userInfo.put("zrrName", parameters.get("zrrName")[0]);
                String wcTime= parameters.get("wcTime")[0];
                if (StringUtils.isBlank(wcTime)){
                    userInfo.put("wcTime",null);
                }else {
                    try {
                        String dateStr = wcTime.split(Pattern.quote("(中国标准时间)"))[0].replace("GMT+0800", "GMT+08:00");
                        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z", Locale.US);
                        Date date = sdf.parse(dateStr);
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        userInfo.put("wcTime",sdf1.format(date) );
                    } catch (Exception e) {
                        logger.error("时间格式转化异常");
                    }
                }
                userInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                userInfo.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                newItemLcpsDao.updateXplcxx(userInfo);
            }

        } catch (Exception e) {
            logger.error("Exception in savePublicStandard", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }
    public List<JSONObject> getXplcxxList(String xplcId) {

        List<JSONObject> xplcxxList = new ArrayList<>();
        if (xplcId != null && !"".equals(xplcId)) {
            xplcxxList = newItemLcpsDao.getXplcxxList(xplcId);
            for (JSONObject xplcxx:xplcxxList){
                if (xplcxx.getDate("wcTime") != null){
                    xplcxx.put("wcTime", DateFormatUtil.format(xplcxx.getDate("wcTime"), "yyyy-MM-dd"));
                }
            }
        }
        return xplcxxList;
    }
    public JsonResult deleteXplc(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> xplcIds = Arrays.asList(ids);
        List<JSONObject> files = getXplcFileList(xplcIds);
        String xplcFilePathBase = WebAppUtil.getProperty("xplcFilePathBase");
        String xplcFilePathBase_view = WebAppUtil.getProperty("xplcFilePathBase_preview");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("xplcId"), xplcFilePathBase);
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("xplcId"), xplcFilePathBase_view);
        }
        for (String oneXplcId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneXplcId, xplcFilePathBase);
            rdmZhglFileManager.deleteDirFromDisk(oneXplcId, xplcFilePathBase_view);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("xplcIds", xplcIds);
        newItemLcpsDao.deleteXplcFile(param);
        newItemLcpsDao.delXplcxxList(param);
        newItemLcpsDao.deleteXplc(param);
        return result;
    }
}
