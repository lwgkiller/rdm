package com.redxun.fzsj.core.service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.core.entity.BpmTask;
import com.redxun.bpm.core.manager.BpmNodeJumpManager;
import com.redxun.bpm.core.manager.BpmTaskManager;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
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
import com.redxun.core.query.Page;
import com.redxun.fzsj.core.dao.FzsjDao;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

@Service
public class FzsjService {
    private static Logger logger = LoggerFactory.getLogger(FzsjService.class);
    @Autowired
    private FzsjDao fzsjDao;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private CommonInfoDao commonInfoDao;
    @Autowired
    private BpmTaskManager bpmTaskManager;
    @Autowired
    private BpmNodeJumpManager bpmNodeJumpManager;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;

    public boolean compareTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = format.format(new Date());
        try {
            long nowDateTime = format.parse(nowDate).getTime();
            if (nowDateTime > time) {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public JsonPageResult<?> getFzsjList(HttpServletRequest request, HttpServletResponse response, boolean doPage,
                                         String type) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
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
        List<JSONObject> fzsjList = fzsjDao.fzsjListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (JSONObject fzsj : fzsjList) {
            if (fzsj.get("applyTime") != null) {
                fzsj.put("applyTime", format.format(fzsj.get("applyTime")));
            }
            if (fzsj.get("ldjsspsj") != null) {
                fzsj.put("ldjsspsj", sdf.format(fzsj.get("ldjsspsj")));
            }
            if (fzsj.get("CREATE_TIME_") != null) {
                fzsj.put("CREATE_TIME_", sdf.format(fzsj.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(fzsjList, ContextUtil.getCurrentUserId());
        List<JSONObject> myList = new ArrayList<>();
        if (type.equals("grgzt")) {
            myList = fzsjList.stream()
                    .filter(jsonObject -> jsonObject.getString("CREATE_BY_").equals(ContextUtil.getCurrentUserId())
                            || StringUtils.isNotBlank(jsonObject.getString("myTaskId")))
                    .collect(Collectors.toList());
        } else if (type.equals("fzrwhz")) {
            List<JSONObject> myListTemp = fzsjList.stream().filter(jsonObject -> jsonObject.getString("sqlcsfjs").equals("yes"))
                    .collect(Collectors.toList());
            if (params.containsKey("taskName") && StringUtil.isNotEmpty(params.getString("taskName"))) {//有当前节点参数
                String taskName = params.getString("taskName");
                for (JSONObject jsonObject : myListTemp) {
                    if (jsonObject.containsKey("taskName") && jsonObject.getString("taskName").equalsIgnoreCase(taskName)) {
                        myList.add(jsonObject);
                    }
                }
            } else {
                myList = myListTemp;
            }
        } else {
            List<JSONObject> zxyqList =
                    fzsjList.stream().filter(jsonObject -> StringUtils.isNotBlank(jsonObject.getString("taskName"))
                            && jsonObject.getString("taskName").equals("仿真任务执行")).collect(Collectors.toList());
            if (zxyqList.size() > 0) {
                for (JSONObject jsonObject : zxyqList) {
                    JSONObject fzzx = fzsjDao.queryLastFzzx(jsonObject.getString("id"));
                    long timeNode = fzzx.getDate("timeNode").getTime();
                    if (!compareTime(timeNode)) {
                        jsonObject.put("ecsx", "执行延期");
                        myList.add(jsonObject);
                    }
                }
            }
            List<JSONObject> gjList =
                    fzsjList.stream().filter(jsonObject -> StringUtils.isNotBlank(jsonObject.getString("taskName"))
                            && jsonObject.getString("taskName").equals("分管领导批准")).collect(Collectors.toList());
            if (gjList.size() > 0) {
                for (JSONObject jsonObject : gjList) {
                    if (!jsonObject.getString("gjyj").equals("tygj")) {
                        if (jsonObject.getString("gjyj").equals("bfgj")) {
                            jsonObject.put("ecsx", "部分改进");
                        } else {
                            jsonObject.put("ecsx", "不同意改进");
                        }
                        myList.add(jsonObject);
                    }
                }
            }
            List<JSONObject> gjyqList =
                    fzsjList.stream().filter(jsonObject -> StringUtils.isNotBlank(jsonObject.getString("taskName"))
                            && jsonObject.getString("taskName").equals("发起人确认")).collect(Collectors.toList());
            if (gjyqList.size() > 0) {
                for (JSONObject jsonObject : gjyqList) {
                    long predictFinishTime = jsonObject.getDate("predictFinishTime").getTime();
                    if (!compareTime(predictFinishTime)) {
                        jsonObject.put("ecsx", "改进延期");
                        myList.add(jsonObject);
                    }
                }
            }
        }
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        // 根据分页进行subList截取
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < myList.size()) {
                finalSubList = myList.subList(startSubListIndex,
                        endSubListIndex < myList.size() ? endSubListIndex : myList.size());
            }
        } else {
            finalSubList = myList;
        }
        if (finalSubList.size() > 0 && type.equals("fzrwhz")) {
            for (JSONObject jsonObject : finalSubList) {
                List<JSONObject> fzzxList = fzsjDao.queryFzzx(jsonObject.getString("id"));
                List<String> allZxry = new ArrayList<>();
                if (fzzxList.size() > 0) {
                    for (JSONObject fzzx : fzzxList) {
                        List<String> zxry = Arrays.asList(fzzx.getString("zxry").split(","));
                        allZxry.addAll(zxry);
                    }
                }
                if (allZxry.size() > 0) {
                    List<String> collect = allZxry.stream().distinct().collect(Collectors.toList());
                    String join = StringUtils.join(collect.toArray(), "，");
                    jsonObject.put("zxry", join);
                }

            }
        }
        result.setData(finalSubList);
        int count = 0;
        if (type.equals("grgzt")) {
            count = fzsjDao.countFzsjQuery(params);
        } else {
            count = myList.size();
        }
        result.setTotal(count);
        return result;
    }

    // ..
    public JsonResult deleteFzsj(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("ids", businessIds);
        param.put("belongDetailIds", businessIds);
        List<JSONObject> files = fzsjDao.queryFzsjFileList(param);
        String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("belongDetailId"), filePath);
            }
            for (String id : ids) {
                rdmZhglFileManager.deleteDirFromDisk(id, filePath);
            }
            fzsjDao.delFzsjFileByBelongDetailId(param);
        }
        fzsjDao.deleteFzsj(param);
        return result;
    }

    public JSONObject getFzsjDetail(String fzsjId) {
        JSONObject detailObj = fzsjDao.queryFzsjById(fzsjId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public void createFzsj(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_TIME_", new Date());
        fzsjDao.insertFzsj(formData);
    }

    public void updateFzsj(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        fzsjDao.updateFzsj(formData);
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String fzsjFilePathBase = WebAppUtil.getProperty("fzsjFilePathBase");
        if (StringUtils.isBlank(fzsjFilePathBase)) {
            logger.error("can't find fzsjFilePathBase");
            return;
        }
        try {
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String belongDetailId = toGetParamVal(parameters.get("belongDetailId"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = fzsjFilePathBase + File.separator + belongDetailId;
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
            fileInfo.put("belongDetailId", belongDetailId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fzsjDao.addFileInfos(fileInfo);
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

    public List<JSONObject> queryFzsjFileList(JSONObject params) {
        List<JSONObject> fileList = fzsjDao.queryFzsjFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }

    public void deleteFzsjFileById(String id, String fileName, String belongDetailId) {
        String filePath = WebAppUtil.getProperty("fzsjFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, belongDetailId, filePath);
        fzsjDao.deleteFzsjFileById(id);
    }

    public List<JSONObject> queryFzzx(String fzsjId) {
        List<JSONObject> list = fzsjDao.queryFzzx(fzsjId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject fzzx : list) {
            if (fzzx.get("CREATE_TIME_") != null) {
                fzzx.put("CREATE_TIME_", sdf.format(fzzx.get("CREATE_TIME_")));
            }
        }
        return list;
    }

    public JSONObject getFzzxDetail(String zxclId) {
        JSONObject fzzxDetail = fzsjDao.getFzzxDetail(zxclId);
        return fzzxDetail;
    }

    public void createFzzx(JSONObject formDataJson) {
        String id = IdUtil.getId();
        formDataJson.put("id", id);
        formDataJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("CREATE_TIME_", new Date());
        fzsjDao.createFzzx(formDataJson);
    }

    public void updateFzzx(JSONObject formDataJson) {
        formDataJson.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formDataJson.put("UPDATE_TIME_", new Date());
        fzsjDao.updateFzzx(formDataJson);
    }

    public JsonResult fzzxAddValid(String fzsjId, String fzzxAction, String step) {
        JsonResult result = new JsonResult(true);
        JSONObject fzzx = fzsjDao.fzzxAddValid(fzsjId);
        if (fzzx != null) {
            result.setSuccess(false);
        }
        return result;
    }

    public JsonResult deleteFzzx(String fzzxId) {
        JsonResult result = new JsonResult(true);
        fzsjDao.deleteFzzx(fzzxId);
        return result;
    }

    // 室主任审批流程校验
    public JsonResult fzszrshValid(String fzsjId) {
        JsonResult result = new JsonResult(true);
        JSONObject fzzx = fzsjDao.fzzxAddValid(fzsjId);
        if (fzzx == null) {
            result.setSuccess(false);
        }
        return result;
    }

    // 明细文件下载权限
    public boolean downloadPermission(String fzsjId) {
        if (StringUtils.isNotBlank(fzsjId)) {
            List<String> userIds = new ArrayList<>();
            String currentUserId = ContextUtil.getCurrentUserId();
            JSONObject fzsj = fzsjDao.queryFzsjById(fzsjId);
            // 创建人
            String createBy = fzsj.getString("CREATE_BY_");
            if (StringUtils.isNotBlank(createBy)) {
                userIds.add(createBy);
                // 申请人部门负责人
                List<JSONObject> deptResps = commonInfoDao.queryDeptRespByUserId(createBy);
                for (JSONObject deptResp : deptResps) {
                    userIds.add(deptResp.getString("USER_ID_"));
                }
            }
            // 产品主管或室主任
            String cpzgOrSzrId = fzsj.getString("cpzgOrSzrId");
            if (StringUtils.isNotBlank(cpzgOrSzrId)) {
                List<String> cpzgOrSzrIds = Arrays.asList(cpzgOrSzrId.split(","));
                userIds.addAll(cpzgOrSzrIds);
            }
            // 仿真室主任
            String fzszrId = fzsj.getString("fzszrId");
            if (StringUtils.isNotBlank(fzszrId)) {
                userIds.add(fzszrId);
            }
            // 仿真所长
            Map<String, Object> fzszParams = new HashMap<>();
            fzszParams.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            fzszParams.put("groupName", "仿真技术研究所");
            List<Map<String, String>> fzsz = xcmgProjectOtherDao.getDepRespManById(fzszParams);
            for (Map<String, String> map : fzsz) {
                userIds.add(map.get("PARTY2_"));
            }
            // 执行人员
            List<JSONObject> fzzxList = fzsjDao.queryFzzx(fzsjId);
            if (fzzxList.size() > 0) {
                for (JSONObject fzzx : fzzxList) {
                    String zxryId = fzzx.getString("zxryId");
                    if (StringUtils.isNotBlank(zxryId)) {
                        List<String> zxryIds = Arrays.asList(zxryId.split(","));
                        userIds.addAll(zxryIds);
                    }
                }
            }
            // 分管领导
            List<Map<String, String>> maps = rdmZhglUtil.queryFgld();
            for (Map<String, String> fgld : maps) {
                userIds.add(fgld.get("USER_ID_"));
            }
            if (userIds.contains(currentUserId)) {
                return true;
            }
        }
        return false;
    }

    public List<JSONObject> getYearList() {
        return fzsjDao.getYearList();
    }

    public List<JSONObject> getNdtjList(String startYear, String endYear) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startYear", startYear);
        jsonObject.put("endYear", endYear);
        return fzsjDao.getNdtjList(jsonObject);
    }

    //..作废完给历史审批者发个通知
    public void sendMessageAfterDiscard(JSONObject requestDataJson) throws Exception {
        String businessId = requestDataJson.getString("businessId");
        JSONObject fzsj = fzsjDao.queryFzsjById(businessId);
        String taskId = requestDataJson.getString("taskId");
        BpmTask bpmTask = bpmTaskManager.get(taskId);
        if (fzsj != null && bpmTask != null) {
            String fzNumber = fzsj.containsKey("fzNumber") ? fzsj.getString("fzNumber") : "";
            String questName = fzsj.getString("questName");
            Set<String> nodeHandleUserIds = bpmNodeJumpManager.getNodeHandleUserIds(bpmTask.getProcInstId());//获取当前任务的历史处理人员ids
            if (nodeHandleUserIds != null && !nodeHandleUserIds.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String userId : nodeHandleUserIds) {
                    stringBuilder.append(userId).append(",");
                }
                String userIds = stringBuilder.substring(0, stringBuilder.length() - 1);
                JSONObject noticeObj = new JSONObject();
                noticeObj.put("content", "编号为：‘" + fzNumber + "’，任务名称为：‘" + questName + "’的仿真任务已作废，请关注！");
                sendDDNoticeManager.sendNoticeForCommon(userIds, noticeObj);
            }
        }
    }
}
