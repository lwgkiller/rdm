package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.LoginRecordManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.JstbDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class JstbService {
    private Logger logger = LogManager.getLogger(JstbService.class);
    @Autowired
    private JstbDao jstbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private LoginRecordManager loginRecordManager;

    public JsonPageResult<?> queryJstb(HttpServletRequest request, boolean doPage) {
        String currentUserId = ContextUtil.getCurrentUserId();
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
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeStart".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeEnd".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    params.put(name, value);
                }
            }
        }

        List<JSONObject> jstbList = jstbDao.queryJstb(params);
        for (JSONObject jstb : jstbList) {
            if (jstb.getDate("CREATE_TIME_") != null) {
                jstb.put("CREATE_TIME_", DateUtil.formatDate(jstb.getDate("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(jstbList, ContextUtil.getCurrentUserId());
        // 根据角色过滤
        jstbList = filterListByDepRole(jstbList);
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        // 根据分页进行subList截取
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < jstbList.size()) {
                finalSubList = jstbList.subList(startSubListIndex,
                    endSubListIndex < jstbList.size() ? endSubListIndex : jstbList.size());
            }
        } else {
            finalSubList = jstbList;
        }
        result.setData(finalSubList);
        result.setTotal(jstbList.size());
        return result;
    }

    public List<JSONObject> queryNoticeForRdmHome(String isRead) {
        List<JSONObject> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("sortField", "CREATE_TIME_");
        params.put("sortOrder", "desc");
        params.put("status", "发布");
        List<JSONObject> jstbList = jstbDao.queryJstb(params);
        List<JSONObject> resultMap = filterListByDepRole(jstbList);
        if (resultMap == null || resultMap.isEmpty()) {
            return result;
        }
        params.clear();
        params.put("readUserId", ContextUtil.getCurrentUserId());
        Set<String> noticeIds = new HashSet<>();
        for (Map<String, Object> oneNotice : resultMap) {
            noticeIds.add(oneNotice.get("jstbId").toString());
        }
        params.put("jstbIds", new ArrayList<String>(noticeIds));
        List<Map<String, Object>> readInfos = jstbDao.queryReaders(params);
        Set<String> readJstbIds = new HashSet<>();
        if (readInfos != null && !readInfos.isEmpty()) {
            for (Map<String, Object> oneMap : readInfos) {
                readJstbIds.add(oneMap.get("belongTbId").toString());
            }
        }

        for (Map<String, Object> jstb : resultMap) {
            String jstbId = jstb.get("jstbId").toString();
            String msgIsRead = "0";
            if (readJstbIds.contains(jstbId)) {
                msgIsRead = "1";
            }
            if (StringUtils.isNotBlank(isRead) && "0".equalsIgnoreCase(isRead) && !"0".equalsIgnoreCase(msgIsRead)) {
                continue;
            }
            JSONObject oneObj = new JSONObject();
            oneObj.put("title", jstb.get("jstbTitle"));
            oneObj.put("creator", jstb.get("creator"));
            oneObj.put("CREATE_TIME_", DateUtil.formatDate((Date)jstb.get("CREATE_TIME_"), "yyyy-MM-dd"));
            oneObj.put("typeName", "技术管理");
            oneObj.put("isRead", msgIsRead);
            oneObj.put("url", "/rdmZhgl/Jstb/see.do?jstbId=" + jstbId);
            result.add(oneObj);
        }
        // 未读的在最前面，然后时间倒序
        Collections.sort(result, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                if (!o1.getString("isRead").equalsIgnoreCase(o2.getString("isRead"))) {
                    return o1.getString("isRead").compareTo(o2.getString("isRead"));
                } else {
                    return o2.getString("CREATE_TIME_").compareTo(o1.getString("CREATE_TIME_"));
                }
            }
        });
        return result;
    }

    public void createJstb(JSONObject formData) {
        formData.put("jstbId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());

        jstbDao.insertJstb(formData);
    }

    public void updateJstb(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());

        jstbDao.updateJstb(formData);
    }

    public void saveJstbUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("jstbFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jstbFilePathBase");
            return;
        }
        try {
            String belongTbId = toGetParamVal(parameters.get("jstbId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongTbId;
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
            fileInfo.put("belongTbId", belongTbId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jstbDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getJstbDetail(String jstbId) {
        JSONObject detailObj = jstbDao.queryJstbById(jstbId);
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

    public String toGetJstbNumber() {
        String nowYearStart = DateFormatUtil.getNowByString("yyyy") + "-01-01";
        String nowYearStartUTC = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(nowYearStart), -8));
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStartUTC);
        param.put("countnumInfo", "yes");
        int existNumber = jstbDao.countJsglNumber(param);
        int thisNumber = existNumber + 1;
        if (thisNumber < 10) {
            return "JSTB-" + DateFormatUtil.getNowByString("yyyy") + "-0" + thisNumber;
        } else {
            return "JSTB-" + DateFormatUtil.getNowByString("yyyy") + "-" + thisNumber;
        }
    }

    public List<JSONObject> getJstbFileList(List<String> jstbIdList) {
        List<JSONObject> jstbFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jstbIds", jstbIdList);
        jstbFileList = jstbDao.queryJstbFileList(param);
        return jstbFileList;
    }

    public void deleteOneJstbFile(String fileId, String fileName, String jstbId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jstbId, WebAppUtil.getProperty("jstbFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        param.put("belongTbId", jstbId);
        jstbDao.deleteJstbFile(param);
    }

    public JsonResult deleteJstb(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jstbIds = Arrays.asList(ids);
        List<JSONObject> files = getJstbFileList(jstbIds);
        String jstbFilePathBase = WebAppUtil.getProperty("jstbFilePathBase");
        for (JSONObject oneFile : files) {
            rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                oneFile.getString("jstbId"), jstbFilePathBase);
        }
        for (String oneJstbId : ids) {
            rdmZhglFileManager.deleteDirFromDisk(oneJstbId, jstbFilePathBase);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("jstbIds", jstbIds);
        jstbDao.deleteJstbFile(param);
        jstbDao.deleteJstb(param);
        return result;
    }

    public JsonResult cancelJstb(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> jstbIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("jstbIds", jstbIds);
        jstbDao.cancelJstb(param);
        return result;
    }

    private List<JSONObject> filterListByDepRole(List<JSONObject> JstbList) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        if (JstbList == null || JstbList.isEmpty()) {
            return result;
        }
        Map<String, Object> params = new HashMap<>();
        boolean isJSGLBUser = false;
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        List<JSONObject> isJSGLB = jstbDao.isJSGLB(params);
        for (JSONObject jstb : isJSGLB) {
            if (jstb.getString("deptname").equals(RdmConst.JSGLB_NAME)) {
                isJSGLBUser = true;
                break;
            }
        }
        // 技术管理通报专员
        boolean isJSGLTBZYUser = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "技术管理通报专员");
        // 管理员查看所有的包括草稿数据
        if ("admin".equals(ContextUtil.getCurrentUser().getUserNo()) || isJSGLBUser || isJSGLTBZYUser) {
            return JstbList;
        }

        boolean isZbsj = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), "技术支部书记");
        boolean isAllQuery = rdmZhglUtil.judgeIsPointRoleUser(ContextUtil.getCurrentUserId(), RdmConst.AllDATA_QUERY_NAME);

        // 分管领导的查看权限
        boolean showAll = false;
        if (rdmZhglUtil.judgeUserIsFgld(ContextUtil.getCurrentUserId()) || isZbsj || isAllQuery) {
            showAll = true;
        }

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!"success".equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        // 是否挖掘机械研究院下的部门
        JSONObject isJSZX = loginRecordManager.judgeIsJSZX(currentUserMainDepId, currentUserMainDepName);
        boolean isJSZXUser = false;
        if (isJSZX.getBooleanValue("isJSZX")) {
            isJSZXUser = true;
        }
        // 如果是，则查询出挖掘机械研究院这个部门的id
        // 过滤
        for (JSONObject oneProject : JstbList) {
            // 自己是当前流程处理人
            if (StringUtils.isNotBlank(oneProject.getString("myTaskId"))) {
                result.add(oneProject);
            } else if (showAll) {
                // 对于非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneProject.get("status") != null && !"草稿".equals(oneProject.get("status").toString())) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else if (isDepRespMan) {
                // 审批中：自己部门员工或者自己提交的
                if (oneProject.get("status") != null && "审批中".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                    // 发布：自己部门员工或者自己提交或者涉及部门有自己部门
                } else if (oneProject.get("status") != null && "发布".equals(oneProject.get("status").toString())) {
                    if ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser && (oneProject.get("relatedDeptIds") != null
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))//前台直接选择的“挖掘机械研究院”
                        || oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                    // 草稿：自己的
                } else if (oneProject.get("status") != null && "草稿".equals(oneProject.get("status").toString())) {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                } else if (oneProject.get("status") != null && "作废".equals(oneProject.get("status").toString())) {
                    if ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser && (oneProject.get("relatedDeptIds") != null
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))
                        || oneProject.get("creatorDepId").toString().equals(currentUserMainDepId)
                        || oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            } else {
                // 其他人对于非草稿的且项目成员中包含自己的，或者草稿但是创建人CREATE_BY_是自己的
                if ((oneProject.get("status") != null && "发布".equals(oneProject.get("status").toString()))
                    && oneProject.get("relatedDeptIds") != null
                    && (oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1 || (isJSZXUser
                        && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))) {
                    result.add(oneProject);
                } else if ((oneProject.get("status") != null && "作废".equals(oneProject.get("status").toString()))
                    && ((oneProject.get("relatedDeptIds") != null
                        && oneProject.get("relatedDeptIds").toString().indexOf(currentUserMainDepId) != -1)
                        || (isJSZXUser
                            && oneProject.get("relatedDeptIds").toString().indexOf("87212403321741318") != -1))) {
                    result.add(oneProject);
                } else {
                    if (oneProject.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneProject);
                    }
                }
            }
        }
        return result;
    }
}
