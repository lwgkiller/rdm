package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.rdmZhgl.core.dao.BbsBaseInfoDao;
import com.redxun.rdmZhgl.core.dao.BbsDiscussDao;
import com.redxun.rdmZhgl.core.dao.BbsNoticeDao;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.sys.core.dao.SubsystemDao;
import com.redxun.sys.core.entity.Subsystem;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.dao.OsUserDao;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.util.CommonExcelUtils;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangzhen
 */
@Service
public class BbsBaseInfoService {
    private static final Logger logger = LoggerFactory.getLogger(BbsBaseInfoService.class);
    @Resource
    BbsBaseInfoDao bbsBaseInfoDao;
    @Resource
    BbsDiscussDao bbsDiscussDao;
    @Resource
    private BbsNoticeDao bbsNoticeDao;
    @Resource
    private SubsystemDao subsystemDao;
    @Resource
    private CommonInfoDao commonInfoDao;
    @Resource
    private SendDDNoticeManager sendDDNoticeManager;
    @Resource
    private OsUserDao osUserDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Resource
    private BpmInstManager bpmInstManager;
    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd  HH:mm:ss"));
                        }
                    }
                    if ("closeDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd HH:mm:ss"));
                        }
                    }
                    if ("planFinishDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                    if ("conformDate".equals(key)) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public JsonPageResult<?> query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            String plate = request.getParameter("plate");
            String model = request.getParameter("model");
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("plate", plate);
            params.put("model", model);
            list = bbsBaseInfoDao.query(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("plate", plate);
            params.put("model", model);
            totalList = bbsBaseInfoDao.query(params);
            CommonFuns.convertDate(list);
            setTaskCurrentUser(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    // 对于一个任务有多个候选执行人的场景，act_ru_task中没有执行人，需要从act_ru_identitylink查询。因此统一在此处查询
    public void setTaskCurrentUser(List<Map<String, Object>> objList) {
        Map<String, Map<String, Object>> taskId2Pro = new HashMap<>();
        for (Map<String, Object> onePro : objList) {
            if (onePro.get("taskId") != null && StringUtils.isNotBlank(onePro.get("taskId").toString())) {
                taskId2Pro.put(onePro.get("taskId").toString(), onePro);
            }
        }
        if (taskId2Pro.isEmpty()) {
            return;
        }
        Map<String, Object> queryTaskExecutors = new HashMap<>();
        queryTaskExecutors.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        queryTaskExecutors.put("taskIds", new ArrayList<String>(taskId2Pro.keySet()));
        List<Map<String, String>> task2Executors = xcmgProjectOtherDao.queryTaskExecutorsByTaskIds(queryTaskExecutors);
        if (task2Executors == null || task2Executors.isEmpty()) {
            logger.warn("can't find task executors");
            return;
        }
        for (Map<String, String> oneTaskExecutor : task2Executors) {
            String taskId = oneTaskExecutor.get("taskId");
            String executorName = oneTaskExecutor.get("currentProcessUser");
            String executorId = oneTaskExecutor.get("currentProcessUserId");
            Map<String, Object> needPutPro = taskId2Pro.get(taskId);
            if (needPutPro.get("currentProcessUser") != null
                    && StringUtils.isNotBlank(needPutPro.get("currentProcessUser").toString())) {
                executorName += "," + needPutPro.get("currentProcessUser").toString();
                executorId += "," + needPutPro.get("currentProcessUserId").toString();
            }
            needPutPro.put("currentProcessUser", executorName);
            needPutPro.put("currentProcessUserId", executorId);
        }
        String currentUserId = ContextUtil.getCurrentUserId();
        for (Map<String, Object> oneProject : objList) {
            if (oneProject.get("currentProcessUserId") != null) {
                String[] currentProcessUserIdArray = oneProject.get("currentProcessUserId").toString().split(",");
                for(String userId:currentProcessUserIdArray){
                    if(currentUserId.equals(userId)){
                        oneProject.put("processTask", true);
                    }
                }
            }
        }
    }
    public JSONObject add(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            if (parameters == null || parameters.isEmpty()) {
                logger.error("表单内容为空！");
                return ResultUtil.result(false, "操作失败，表单内容为空！", "");
            }
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            resultJson.put("id", id);
            bbsBaseInfoDao.add(objBody);
            sendNotice(objBody);
            savePic(id, CommonFuns.nullToString(objBody.get("picName")));
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public void sendNotice(Map<String, Object> paramMap) {
        try {
            String plate = CommonFuns.nullToString(paramMap.get("plate"));
            String recordId = CommonFuns.nullToString(paramMap.get("id"));
            //标准的要给专业领域负责人发
            if ("standardManager".equals(plate)) {
                dealBzNotice(recordId,paramMap);
            }
            //给子系统责任人发钉钉消息
            sendDingMsg(paramMap);
            //给子系统责任人发系统消息+耿总
            sendSystemNotice(plate,recordId);
        } catch (Exception e) {
            logger.error("发送论坛通知异常", e);
        }
    }
    public void sendDingMsg(Map<String, Object> paramMap){
        try {
            JSONObject noticeObj = new JSONObject();
            String plate = CommonFuns.nullToString(paramMap.get("plate"));
            Subsystem subsystem = subsystemDao.getByKey(plate);
            String principalId = subsystem.getPrincipalId();
            if(StringUtil.isNotEmpty(principalId)){
                principalId += ",1";
            }else{
                principalId = "1";
            }
            OsUser osUser = osUserDao.get(CommonFuns.nullToString(paramMap.get("CREATE_BY_")));
            StringBuilder stringBuilder = new StringBuilder("【RDM论坛:");
            stringBuilder.append(subsystem.getName()).append("板块】");
            stringBuilder.append("：").append(osUser.getFullname());
            stringBuilder.append("发布标题为：“").append(paramMap.get("title")).append("”的新帖");
            stringBuilder.append("，请及时查看");
            stringBuilder.append("，通知时间：").append(XcmgProjectUtil.getNowLocalDateStr(DateUtil.DATE_FORMAT_FULL));
            noticeObj.put("content", stringBuilder.toString());
            sendDDNoticeManager.sendNoticeForCommon(principalId, noticeObj);
        }catch (Exception e){
            logger.error("发送钉钉消息异常", e);
        }
    }
    public void sendSystemNotice(String plate,String recordId){
        try {
            Subsystem subsystem = subsystemDao.getByKey(plate);
            String principalId = subsystem.getPrincipalId();
            String userIdArray[] = principalId.split(",");
            for (String userId : userIdArray) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", IdUtil.getId());
                map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("mainId", recordId);
                map.put("userId", userId);
                map.put("isRead", "0");
                bbsNoticeDao.add(map);
            }
            //给分管领导角色发通知
            JSONObject paramJson = new JSONObject();
            paramJson = new JSONObject();
            paramJson.put("dim", "2");
            paramJson.put("key", "FGLD");
            List<JSONObject> groupUserList = commonInfoDao.getGroupUsers(paramJson);
            for (JSONObject userObj : groupUserList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", IdUtil.getId());
                map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("mainId", recordId);
                map.put("userId", userObj.getString("PARTY2_"));
                map.put("isRead", "0");
                bbsNoticeDao.add(map);
            }
            //给管理员发送
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", IdUtil.getId());
            map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            map.put("mainId", recordId);
            map.put("userId", "1");
            map.put("isRead", "0");
            bbsNoticeDao.add(map);
        }catch (Exception e){
            logger.error("发送论坛系统通知异常", e);
        }
    }
    public void dealBzNotice(String recordId,Map<String, Object> paramMap){
        if (StringUtil.isNotEmpty(recordId)) {
            LinkedHashSet<String> userList = new LinkedHashSet<>();
            // 获取专业负责人和业务负责人
            List<JSONObject> list = bbsNoticeDao.getStandardUsers(recordId);
            for(JSONObject temp : list){
                String respUserId = temp.getString("respUserId");
                String ywRespUserId = temp.getString("ywRespUserId");
                if(StringUtil.isNotEmpty(respUserId)){
                    userList.add(respUserId);
                }
                if(StringUtil.isNotEmpty(ywRespUserId)){
                    userList.add(ywRespUserId);
                }
            }
            for (String userId : userList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", IdUtil.getId());
                map.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                map.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                map.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                map.put("mainId", paramMap.get("id"));
                map.put("userId", userId);
                map.put("isRead", "0");
                bbsNoticeDao.add(map);
            }
        }
    }
    public void savePic(String mainId, String picNames) {
        if (StringUtil.isNotEmpty(picNames)) {
            if (picNames.length() > 1) {
                picNames = picNames.substring(0, picNames.length() - 1);
            }
            String[] nameArray = picNames.split(",", -1);
            Map<String, Object> objBody = new HashMap<>(16);
            objBody.put("mainId", mainId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            for (String temp : nameArray) {
                objBody.put("id", IdUtil.getId());
                objBody.put("fileName", temp);
                bbsBaseInfoDao.addPic(objBody);
            }
        }
    }

    public JSONObject update(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.updateObject(objBody);
            String id = CommonFuns.nullToString(objBody.get("id"));
            resultJson.put("id", id);
            savePic(id, CommonFuns.nullToString(objBody.get("picName")));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            bbsBaseInfoDao.batchDelete(params);
            bbsDiscussDao.batchDeleteByMainId(params);
            bbsNoticeDao.batchDeleteByMainId(params);
            for (String id : idArr) {
                deleteFileOnDisk(id);
                // 删除实例
                bpmInstManager.deleteCascade(id, "");
            }
            bbsBaseInfoDao.batchDeletePic(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除", e);
            return ResultUtil.result(false, "删除异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public void deleteFileOnDisk(String mainId) {
        List<JSONObject> list = bbsBaseInfoDao.getPicList(mainId);
        String picUrl = SysPropertiesUtil.getGlobalProperty("BBS_PIC_URL");
        for (JSONObject jsonObject : list) {
            String fileName = jsonObject.getString("fileName");
            String fileBasePath = picUrl + File.separator + "image" + File.separator + fileName;
            File file = new File(fileBasePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    public JsonPageResult<?> getBbsList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            list = bbsBaseInfoDao.getBbsList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = bbsBaseInfoDao.getBbsList(params);
            convertDate(list);
            setTaskCurrentUser(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }
    public void exportBaseInfoExcel(HttpServletRequest request, HttpServletResponse response) {
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String excelName = nowDate + "论坛汇总表";
        Map<String, Object> params = new HashMap<>(16);
        params =CommonFuns.getSearchParam(params, request, false);
        List<Map<String,Object>> list = bbsBaseInfoDao.getBbsList(params);
        convertDate(list);
        Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
        Map<String, Object> satisfactionMap = commonInfoManager.genMap("satisfaction");
        Map<String, Object> typeMap = commonInfoManager.genMap("BBSType");
        Map<String, Object> urgencyMap = commonInfoManager.genMap("BBSUrgency");
        for(Map<String,Object> map:list){
            if(map.get("closed")!=null){
                map.put("closedText",sfMap.get(map.get("closed")));
            }
            if(map.get("satisfaction")!=null){
                map.put("satisfactionText",satisfactionMap.get(map.get("satisfaction")));
            }
            if(map.get("bbsType")!=null){
                map.put("bbsTypeText",typeMap.get(map.get("bbsType")));
            }
            if(map.get("urgency")!=null){
                map.put("urgencyText",urgencyMap.get(map.get("urgency")));
            }
            if(map.get("isAdopt")!=null){
                map.put("isAdoptText",sfMap.get(map.get("isAdopt")));
            }
        }
        String title = "论坛帖子汇总";
        String[] fieldNames = {"标题", "主题", "板块","帖子分类","紧急程度","是否采纳","计划完成时间","计划内容","不采纳原因","结果确认时间","技术管理部意见", "是否关帖", "满意度", "意见", "回帖数量", "发帖人", "发帖时间", "关帖时间"};
        String[] fieldCodes = {"title", "direction", "plateName", "bbsTypeText","urgencyText","isAdoptText","planFinishDate","planContent","unAdoptReason","conformDate","techOpinion",
                "closedText", "satisfactionText", "opinion", "totalDiscussNum", "publisher","CREATE_TIME_","closeDate"};
        HSSFWorkbook wbObj = CommonExcelUtils.exportStyleExcel(list, fieldNames, fieldCodes, title);
        // 输出
        CommonExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
    public JSONObject openPost(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> objBody = new HashMap<>(16);
            objBody.put("id",request.getParameter("id"));
            objBody.put("closed","0");
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.updateObject(objBody);
            String id = CommonFuns.nullToString(objBody.get("id"));
            resultJson.put("id", id);
            savePic(id, CommonFuns.nullToString(objBody.get("picName")));
        } catch (Exception e) {
            logger.error("Exception in update 开贴异常", e);
            return ResultUtil.result(false, "开贴异常！", "");
        }
        return ResultUtil.result(true, "开贴成功", resultJson);
    }
    public JSONObject closePost(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            objBody.put("closed","1");
            objBody.put("closeDate",XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.updateObject(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 关帖异常", e);
            return ResultUtil.result(false, "关帖异常！", "");
        }
        return ResultUtil.result(true, "关帖成功", resultJson);
    }
    public JSONObject removePost(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, String[]> parameters = request.getParameterMap();
            Map<String, Object> objBody = new HashMap<>(16);
            for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
                String mapKey = entry.getKey();
                String mapValue = entry.getValue()[0];
                objBody.put(mapKey, mapValue);
            }
            String id = CommonFuns.nullToString(objBody.get("postId"));
            objBody.put("id",id);
            JSONObject bbsObj = bbsBaseInfoDao.getObject(id);
            //删除原来的通知信息
            bbsNoticeDao.delByMainId(id);
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            bbsBaseInfoDao.removePost(objBody);
            objBody.put("CREATE_BY_", bbsObj.getString("CREATE_BY_"));
            objBody.put("title", bbsObj.getString("title"));
            sendNotice(objBody);
        } catch (Exception e) {
            logger.error("Exception in update 移帖异常", e);
            return ResultUtil.result(false, "移帖异常！", "");
        }
        return ResultUtil.result(true, "移帖成功", resultJson);
    }
}
