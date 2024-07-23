package com.redxun.rdmCommon.core.manager;

import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.rdmCommon.core.dao.PdmApiDao;
import com.redxun.rdmCommon.core.util.StringProcessUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class PdmApiManager {
    private static Logger logger = LoggerFactory.getLogger(PdmApiManager.class);
    @Resource
    private PdmApiDao pdmApiDao;
    @Autowired
    private XcmgProjectOtherDao xcmgProjectOtherDao;

    /**
     * 根据pdm用户账号查询该用户负责的或参与的，当前在运行中的科技项目、阶段、交付物
     * 
     * @param pdmUserNo
     * @return
     */
    public void pdmGetProjectList(String pdmUserNo, JSONObject result) {
        Map<String, Object> param = new HashMap<>();
        param.put("pdmUserNo", pdmUserNo);
        List<JSONObject> rdmUser = pdmApiDao.queryRdmUserByPdmUser(param);
        if (rdmUser == null || rdmUser.isEmpty()) {
            result.put("code", HttpStatus.SC_BAD_REQUEST);
            result.put("message", "在XE-RDM中无对应的账号，请联系RDM管理员");
            return;
        }
        if (rdmUser.size() != 1) {
            result.put("code", HttpStatus.SC_BAD_REQUEST);
            result.put("message", "在XE-RDM中找到多个对应的账号，请联系RDM管理员");
            return;
        }

        try {
            String rdmUserId = rdmUser.get(0).getString("USER_ID_");
            param.clear();
            param.put("userId", rdmUserId);
            param.put("status", "RUNNING");
            List<JSONObject> projectList = pdmApiDao.pdmQueryProjectList(param);
            if (projectList != null && !projectList.isEmpty()) {
                Map<String, List<JSONObject>> categoryId2StageList = queryProjectStageDeliverys(projectList);
                for (JSONObject oneProject : projectList) {
                    String categoryId = oneProject.getString("categoryId");
                    List<JSONObject> stages = categoryId2StageList.get(categoryId);
                    if (stages == null) {
                        stages = new ArrayList<>();
                    }
                    oneProject.put("stage", stages);
                    oneProject.remove("categoryId");
                }
            }
            result.put("data", projectList);
            result.put("code", HttpStatus.SC_OK);
        } catch (Exception e) {
            logger.error("Exception in pdmGetProjectList", e);
            result.put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            result.put("message", "XE-RDM系统异常，请联系RDM管理员");
        }
    }

    // 根据种类查询项目的阶段和阶段交付物
    public Map<String, List<JSONObject>> queryProjectStageDeliverys(List<JSONObject> projectList) {
        Map<String, Map<String, JSONObject>> categoryId2Stages = new HashMap<>();
        Set<String> categoryIds = new HashSet<>();
        for (JSONObject oneProject : projectList) {
            categoryIds.add(oneProject.getString("categoryId"));
        }
        Map<String, Object> param = new HashMap<>();
        param.put("categoryIds", new ArrayList<String>(categoryIds));
        List<JSONObject> deliveryInfos = pdmApiDao.queryStageDeliveryByCategoryIds(param);
        for (JSONObject oneDelivery : deliveryInfos) {
            String categoryId = oneDelivery.getString("categoryId");
            if (!categoryId2Stages.containsKey(categoryId)) {
                categoryId2Stages.put(categoryId, new HashMap<String, JSONObject>());
            }
            Map<String, JSONObject> stageId2Obj = categoryId2Stages.get(categoryId);
            String stageId = oneDelivery.getString("stageId");
            if (!stageId2Obj.containsKey(stageId)) {
                JSONObject oneStageObj = new JSONObject();
                oneStageObj.put("stageId", stageId);
                oneStageObj.put("stageName", oneDelivery.getString("stageName"));
                oneStageObj.put("delivery", new JSONArray());
                stageId2Obj.put(stageId, oneStageObj);
            }
            JSONArray deliveryArr = stageId2Obj.get(stageId).getJSONArray("delivery");
            JSONObject oneDeliveryObj = new JSONObject();
            oneDeliveryObj.put("deliveryId", oneDelivery.getString("deliveryId"));
            oneDeliveryObj.put("deliveryName", oneDelivery.getString("deliveryName"));
            deliveryArr.add(oneDeliveryObj);
        }

        Map<String, List<JSONObject>> result = new HashMap<>();
        // 将每一类中的阶段排序
        for (Map.Entry<String, Map<String, JSONObject>> entry : categoryId2Stages.entrySet()) {
            Map<String, JSONObject> stageId2Obj = entry.getValue();
            String categoryId = entry.getKey();
            List<JSONObject> stageList = new ArrayList<>(stageId2Obj.values());
            Collections.sort(stageList, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    return o1.getString("stageId").compareTo(o2.getString("stageId"));
                }
            });

            result.put(categoryId, stageList);
        }
        return result;
    }

    // 调用pdm接口获取交付物信息developOrProduce
    public List<Map<String, Object>> getPdmProjectFiles(Map<String, Object> params) {
        String developOrProduce = SysPropertiesUtil.getGlobalProperty("developOrProduce");
        if ("develop".equals(developOrProduce)) {
            return Collections.emptyList();
        }
        String pdmProjectFileUrl = SysPropertiesUtil.getGlobalProperty("pdmProjectFileUrl");
        if (StringUtils.isBlank(pdmProjectFileUrl)) {
            return Collections.emptyList();
        }
        if (params == null || params.get("projectId") == null
            || StringUtils.isBlank(params.get("projectId").toString())) {
            return Collections.emptyList();
        }
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, String> reqHeaders = new HashMap<>();
            reqHeaders.put("Content-Type", "application/json;charset=utf-8");
            String user = "CN_WJ_RDM";
            String pwd = "IMktH72CTgFt";
            reqHeaders.put("Authorization",
                    "Basic " + Base64.getUrlEncoder().encodeToString((user + ":" + pwd).getBytes()));
            JSONObject requestBody = new JSONObject();
            requestBody.put("projectId", params.get("projectId").toString());
            requestBody.put("stageId", params.get("stageId") == null ? "" : params.get("stageId").toString());
            requestBody.put("deliveryIds", Arrays.asList());
            requestBody.put("approvalStatus", "");
            String rtnContent = HttpClientUtil.postJson(pdmProjectFileUrl, requestBody.toJSONString(), reqHeaders);
            logger.info("response from pdm,return message:" + rtnContent);
            if (StringUtils.isNotBlank(rtnContent)) {
                JSONObject returnObj = JSONObject.parseObject(rtnContent);
                if (returnObj.getIntValue("code") >= 400) {
                    return Collections.emptyList();
                }
                String projectFilesStr = returnObj.getString("projectFiles");
                if (StringUtils.isNotBlank(projectFilesStr)) {
                    JSONArray projectFilesArr = JSONArray.parseArray(projectFilesStr);
                    if (projectFilesArr != null && !projectFilesArr.isEmpty()) {
                        Set<String> deliveryIds = new HashSet<>();
                        for (int index = 0; index < projectFilesArr.size(); index++) {
                            Map<String, Object> onePdmFile = new HashMap<>();
                            JSONObject oneFileObj = projectFilesArr.getJSONObject(index);
                            onePdmFile.put("fileSource", "pdm");
                            onePdmFile.put("creator",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("createBy")));
                            String createTime = "";
                            if (StringUtils.isNotBlank(oneFileObj.getString("createTime"))) {
                                createTime = oneFileObj.getString("createTime").split(" ", -1)[0];
                            }
                            onePdmFile.put("CREATE_TIME_", createTime);
                            onePdmFile.put("fileName",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("fileName")));
                            onePdmFile.put("fileSize",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("fileSize") + " KB"));
                            onePdmFile.put("deliveryId",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("deliveryId")));
                            if (StringUtils.isNotBlank(oneFileObj.getString("deliveryId"))) {
                                deliveryIds.add(oneFileObj.getString("deliveryId"));
                            }
                            onePdmFile.put("approvalStatus",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("approvalStatus")));
                            onePdmFile.put("filePath",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("filePath")));
                            onePdmFile.put("id", oneFileObj.getString("fileNumber"));
                            onePdmFile.put("isFolder", "0");
                            onePdmFile.put("isPDMFile", "1");
                            onePdmFile.put("projectId", params.get("projectId").toString());
                            onePdmFile.put("pid",
                                StringProcessUtil.toGetStringNotNull(oneFileObj.getString("stageId")));
                            result.add(onePdmFile);
                        }
                        if (!deliveryIds.isEmpty()) {
                            Map<String, String> deliveryId2Name = queryDeliveryId2Name(new ArrayList<>(deliveryIds));
                            for (Map<String, Object> oneData : result) {
                                String deliveryId = oneData.get("deliveryId").toString();
                                String deliveryName = "";
                                if (deliveryId2Name.containsKey(deliveryId)) {
                                    deliveryName = deliveryId2Name.get(deliveryId);
                                }
                                oneData.put("deliveryName", deliveryName);
                            }
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception in getPdmProjectFiles", e);
            return Collections.emptyList();
        }
    }

    public Map<String, String> queryDeliveryId2Name(List<String> deliveryIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("deliveryIds", deliveryIds);
        List<JSONObject> infos = xcmgProjectOtherDao.queryDeliveryByIds(params);
        if (infos == null || infos.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> id2Name = new HashMap<>();
        for (JSONObject oneData : infos) {
            id2Name.put(oneData.getString("deliveryId"), oneData.getString("deliveryName"));
        }
        return id2Name;
    }

}
