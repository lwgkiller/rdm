package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.serviceEngineering.core.dao.DecorationManualTopicDao;
import com.redxun.serviceEngineering.core.dao.DecorationManualTopicPSDao;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class DecorationManualTopicPSService {
    private static Logger logger = LoggerFactory.getLogger(DecorationManualTopicPSService.class);
    @Autowired
    private DecorationManualTopicPSDao decorationManualTopicPSDao;
    @Autowired
    private DecorationManualTopicDao decorationManualTopicDao;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        getListParams(params, request);
        List<JSONObject> businessList = decorationManualTopicPSDao.dataListQuery(params);
        // 查询当前处理人--会签不用这个
        //xcmgProjectManager.setTaskCurrentUser(businessLi
        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = decorationManualTopicPSDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    private void getListParams(Map<String, Object> params, HttpServletRequest request) {
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "businessStatus");
            params.put("sortOrder", "asc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
//                    if ("communicateStartTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
//                    }
//                    if ("communicateEndTime".equalsIgnoreCase(name)) {
//                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
//                    }
                    params.put(name, value);
                }
            }
        }

        // 增加分页条件
        params.put("startIndex", 0);
        params.put("pageSize", 20);
        String pageIndex = request.getParameter("pageIndex");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
            params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
            params.put("pageSize", Integer.parseInt(pageSize));
        }
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject param = new JSONObject();
        param.put("businessIds", businessIds);
        decorationManualTopicPSDao.deleteBusiness(param);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JSONObject getDetail(String businessId) {
        JSONObject jsonObject = decorationManualTopicPSDao.queryDetailById(businessId);
        if (jsonObject == null) {
            return new JSONObject();
        }
        return jsonObject;
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
            formData.put("businessStatus", "A");
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processJsonItemInsert(formData);
            decorationManualTopicPSDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processJsonItemUpdate(formData);
            decorationManualTopicPSDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..@lwgkiller:无表化设计参考，复杂情况
    private void processJsonItemInsert(JSONObject formData) {
        //生成一对多自己的业务明细,通过assessorIds生成相应明细,没有就不生成
        JSONArray jsonArray = new JSONArray();
        String assessorIds = formData.getString("assessorIds");
        if (StringUtil.isNotEmpty(assessorIds)) {
            String[] assessorIdsArray = assessorIds.split(",");
            for (String assessorId : assessorIdsArray) {
                JSONObject item = new JSONObject();
                item.put("id", IdUtil.getId());
                item.put("assessorId_item", assessorId);
                OsUser assessor = osUserManager.get(assessorId);
                OsGroup belongDep = osGroupManager.getBelongDep(assessorId);
                item.put("assessor_item", assessor.getFullname());
                item.put("assessorDepId_item", belongDep.getGroupId());
                item.put("assessorDep_item", belongDep.getName());
                item.put("isPS_item", "");
                jsonArray.add(item);
            }
            formData.put("topicPSItems", jsonArray.toString());
        }
        //..生成一对多引用别的业务,没有就不生成
        if (StringUtil.isNotEmpty(formData.getString("topicChangeData"))) {
            String topicChangeDataString = formData.getString("topicChangeData");
            JSONArray jsonArrayTopicChange = JSONObject.parseArray(topicChangeDataString);
            StringBuilder topicIdBuilder = new StringBuilder();
            StringBuilder businessNoTopicBuilder = new StringBuilder();
            StringBuilder chapterBuilder = new StringBuilder();
            StringBuilder systemBuilder = new StringBuilder();
            StringBuilder topicTypeBuilder = new StringBuilder();
            StringBuilder productSerieBuilder = new StringBuilder();
            StringBuilder manualVersionBuilder = new StringBuilder();
            for (Object topicChange : jsonArrayTopicChange) {
                JSONObject jsonTopicChange = (JSONObject) topicChange;
                topicIdBuilder.append(jsonTopicChange.getString("id")).append(",");
                businessNoTopicBuilder.append(jsonTopicChange.getString("businessNo")).append(",");
                chapterBuilder.append(jsonTopicChange.getString("chapter")).append(",");
                systemBuilder.append(jsonTopicChange.getString("system")).append(",");
                topicTypeBuilder.append(jsonTopicChange.getString("topicType")).append(",");
                productSerieBuilder.append(jsonTopicChange.getString("productSerie")).append(",");
                manualVersionBuilder.append(jsonTopicChange.getString("manualVersion")).append(",");

            }
            formData.put("topicId", topicIdBuilder.substring(0, topicIdBuilder.length() - 1));
            formData.put("businessNoTopic", businessNoTopicBuilder.substring(0, businessNoTopicBuilder.length() - 1));
            formData.put("chapter", chapterBuilder.substring(0, chapterBuilder.length() - 1));
            formData.put("system", systemBuilder.substring(0, systemBuilder.length() - 1));
            formData.put("topicType", topicTypeBuilder.substring(0, topicTypeBuilder.length() - 1));
            formData.put("productSerie", productSerieBuilder.substring(0, productSerieBuilder.length() - 1));
            formData.put("manualVersion", manualVersionBuilder.substring(0, manualVersionBuilder.length() - 1));
        }
    }

    //..@lwgkiller:无表化设计参考，复杂情况
    private void processJsonItemUpdate(JSONObject formData) {
        //初始化来自现有明细的MAP
        Map<String, JSONObject> topicPSItemsByItemChangeData = new HashedMap();
        if (formData.containsKey("itemChangeData") && StringUtil.isNotEmpty(formData.getString("itemChangeData"))) {
            String itemChangeDataString = formData.getString("itemChangeData");
            JSONArray jsonArrayItemChange = JSONObject.parseArray(itemChangeDataString);
            for (Object itemChange : jsonArrayItemChange) {
                JSONObject jsonItemChange = (JSONObject) itemChange;
                jsonItemChange.remove("_state");
                jsonItemChange.remove("_id");
                jsonItemChange.remove("_uid");
                topicPSItemsByItemChangeData.put(jsonItemChange.getString("assessorId_item"), jsonItemChange);
            }
        }
        //初始化来自会签人的生成的MAP
        Map<String, JSONObject> topicPSItemsByAssessorIds = new HashedMap();
        String assessorIds = formData.getString("assessorIds");
        JSONArray jsonArrayFinal = new JSONArray();
        //更新一对多自己的业务明细,通过assessorIds生成相应明细,没有就置空
        if (StringUtil.isNotEmpty(assessorIds)) {
            String[] assessorIdsArray = assessorIds.split(",");
            for (String assessorId : assessorIdsArray) {
                JSONObject jsonItemAssessor = new JSONObject();
                jsonItemAssessor.put("id", IdUtil.getId());
                jsonItemAssessor.put("assessorId_item", assessorId);
                OsUser assessor = osUserManager.get(assessorId);
                OsGroup belongDep = osGroupManager.getBelongDep(assessorId);
                jsonItemAssessor.put("assessor_item", assessor.getFullname());
                jsonItemAssessor.put("assessorDepId_item", belongDep.getGroupId());
                jsonItemAssessor.put("assessorDep_item", belongDep.getName());
                jsonItemAssessor.put("isPS_item", "");
                topicPSItemsByAssessorIds.put(jsonItemAssessor.getString("assessorId_item"), jsonItemAssessor);
            }
            //遍历来自会签人的生成的MAP，如果在来自现有明细的MAP有对应的key，则用key对应的对象替换.(MAP遍历参考,我就用这个吧,用了好几种花样的都)
            topicPSItemsByAssessorIds.forEach((key, value) -> {
                if (topicPSItemsByItemChangeData.containsKey(key)) {
                    topicPSItemsByAssessorIds.put(key, topicPSItemsByItemChangeData.get(key));
                }
            });
            //替换完后，再遍历生成现有的明细
            topicPSItemsByAssessorIds.forEach((key, value) -> {
                jsonArrayFinal.add(value);
            });
            formData.put("topicPSItems", jsonArrayFinal.toString());
        } else {
            formData.put("topicPSItems", "");
        }
        //..更新一对多引用别的业务,没有就置空
        if (StringUtil.isNotEmpty(formData.getString("topicChangeData"))) {
            String topicChangeDataString = formData.getString("topicChangeData");
            JSONArray jsonArrayTopicChange = JSONObject.parseArray(topicChangeDataString);
            StringBuilder topicIdBuilder = new StringBuilder();
            StringBuilder businessNoTopicBuilder = new StringBuilder();
            StringBuilder chapterBuilder = new StringBuilder();
            StringBuilder systemBuilder = new StringBuilder();
            StringBuilder topicTypeBuilder = new StringBuilder();
            StringBuilder productSerieBuilder = new StringBuilder();
            StringBuilder manualVersionBuilder = new StringBuilder();
            for (Object topicChange : jsonArrayTopicChange) {
                JSONObject jsonTopicChange = (JSONObject) topicChange;
                topicIdBuilder.append(jsonTopicChange.getString("id")).append(",");
                businessNoTopicBuilder.append(jsonTopicChange.getString("businessNo")).append(",");
                chapterBuilder.append(jsonTopicChange.getString("chapter")).append(",");
                systemBuilder.append(jsonTopicChange.getString("system")).append(",");
                topicTypeBuilder.append(jsonTopicChange.getString("topicType")).append(",");
                productSerieBuilder.append(jsonTopicChange.getString("productSerie")).append(",");
                manualVersionBuilder.append(jsonTopicChange.getString("manualVersion")).append(",");

            }
            formData.put("topicId", topicIdBuilder.substring(0, topicIdBuilder.length() - 1));
            formData.put("businessNoTopic", businessNoTopicBuilder.substring(0, businessNoTopicBuilder.length() - 1));
            formData.put("chapter", chapterBuilder.substring(0, chapterBuilder.length() - 1));
            formData.put("system", systemBuilder.substring(0, systemBuilder.length() - 1));
            formData.put("topicType", topicTypeBuilder.substring(0, topicTypeBuilder.length() - 1));
            formData.put("productSerie", productSerieBuilder.substring(0, productSerieBuilder.length() - 1));
            formData.put("manualVersion", manualVersionBuilder.substring(0, manualVersionBuilder.length() - 1));
        } else {
            formData.put("topicId", "");
            formData.put("businessNoTopic", "");
            formData.put("chapter", "");
            formData.put("system", "");
            formData.put("topicType", "");
            formData.put("productSerie", "");
            formData.put("manualVersion", "");
        }
    }

    //..
    public List<JSONObject> getTopicToPS(String businessId) {
        JSONObject jsonObject = decorationManualTopicPSDao.queryDetailById(businessId);
        List<JSONObject> itemList = new ArrayList<>();
        if (jsonObject != null && StringUtil.isNotEmpty(jsonObject.getString("topicId"))) {
            List<String> ids = Arrays.asList(jsonObject.getString("topicId").split(","));
            JSONObject params = new JSONObject();
            params.put("ids", ids);
            itemList = decorationManualTopicDao.dataListQuery(params);
        }
        return itemList;
    }
}
