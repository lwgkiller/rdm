package com.redxun.standardManager.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardDao;
import com.redxun.standardManager.core.dao.StandardFieldManagerDao;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Service
@Slf4j
public class FzsjbzManager {
    private static Logger logger = LoggerFactory.getLogger(FzsjbzManager.class);
    @Autowired
    private StandardDao standardDao;
    @Autowired
    private StandardFieldManagerDao standardFieldManagerDao;
    // 检索标准
    public JsonPageResult<?> queryFzbzList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> queryParam = new HashMap<String, Object>();
            String currentUserId = ContextUtil.getCurrentUserId();
            getQueryStandardParams(request, queryParam);
            String userGroupIdStr = null;
            // 查询当前用户所属的group，并与标准体系中的group作比对，如果体系中的group不为空且有交集，则可见
            Set<String> userGroupIds = new HashSet<>();
            List<JSONObject> userBelongGroups = standardDao.queryUserBelongGroups(currentUserId);
            for (JSONObject oneGroup : userBelongGroups) {
                userGroupIds.add(oneGroup.getString("PARTY1_"));
            }
            StringBuilder groupIdSb = new StringBuilder();
            for (String groupId : userGroupIds) {
                groupIdSb.append(groupId).append(",");
            }
            if (groupIdSb.length() > 0) {
                userGroupIdStr = groupIdSb.substring(0, groupIdSb.length() - 1);
            }
            if (StringUtils.isNotBlank(userGroupIdStr)) {
                queryParam.put("userGroupIds", userGroupIdStr);
            }
            queryParam.put("currentUserId", ContextUtil.getCurrentUserId());
            List<Map<String, Object>> queryPageResult = standardDao.queryFzsjbzList(queryParam);
            Map<String, String> userId2UserName = new HashMap<>();
            getUserId2Name(userId2UserName);
            if (queryPageResult != null && !queryPageResult.isEmpty()) {
                List<String> standardIds = new ArrayList<>();
                Iterator iterator = queryPageResult.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> oneInfo = (Map<String, Object>)iterator.next();
                    // String status=oneInfo.get("standardStatus").toString();
                    // String create_by_=oneInfo.get("CREATE_BY_").toString();
                    // if("draft".equalsIgnoreCase(status)&&!currentUserId.equals(create_by_)) {
                    // iterator.remove();
                    // }
                    // 查看对应的标准全文是否存在
                    boolean existFile = checkStandardFileExist(oneInfo.get("id").toString(), false);
                    oneInfo.put("existFile", existFile);
                    if (oneInfo.get("CREATE_TIME_") != null) {
                        oneInfo.put("CREATE_TIME_",
                                DateUtil.formatDate((Date)oneInfo.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("UPDATE_TIME_") != null) {
                        oneInfo.put("UPDATE_TIME_",
                                DateUtil.formatDate((Date)oneInfo.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("publishTime") != null) {
                        oneInfo.put("publishTime",
                                DateUtil.formatDate((Date)oneInfo.get("publishTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneInfo.get("stopTime") != null) {
                        oneInfo.put("stopTime",
                                DateUtil.formatDate((Date)oneInfo.get("stopTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    // 拼接起草人的姓名
                    if (oneInfo.get("publisherId") != null) {
                        oneInfo.put("publisherName",
                                toGetUserNameByIds(oneInfo.get("publisherId").toString(), userId2UserName));
                    }
                    // 替换创建人和废止人的姓名
                    if (oneInfo.get("stoperId") != null) {
                        oneInfo.put("stoperName",
                                toGetUserNameByIds(oneInfo.get("stoperId").toString(), userId2UserName));
                    }
                    if (oneInfo.get("CREATE_BY_") != null) {
                        oneInfo.put("creator",
                                toGetUserNameByIds(oneInfo.get("CREATE_BY_").toString(), userId2UserName));
                    }
                    standardIds.add(oneInfo.get("id").toString());
                }
                // 批量查询标准对应的专业领域信息
                if (!standardIds.isEmpty()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("standardIds", standardIds);
                    List<JSONObject> fields = standardFieldManagerDao.batchQueryFieldByStandardIds(param);
                    Map<String, List<JSONObject>> standardId2FieldList = new HashMap<>();
                    for (JSONObject oneField : fields) {
                        String standardId = oneField.getString("standardId");
                        if (!standardId2FieldList.containsKey(standardId)) {
                            standardId2FieldList.put(standardId, new ArrayList<>());
                        }
                        standardId2FieldList.get(standardId).add(oneField);
                    }
                    // 遍历标准进行专业领域字段的赋值
                    for (Map<String, Object> oneStandard : queryPageResult) {
                        String standardId = oneStandard.get("id").toString();
                        if (!standardId2FieldList.containsKey(standardId)) {
                            continue;
                        }
                        List<JSONObject> belongFields = standardId2FieldList.get(standardId);
                        if (belongFields != null && !belongFields.isEmpty()) {
                            StringBuilder fieldIdSb = new StringBuilder();
                            StringBuilder fieldNameSb = new StringBuilder();
                            for (JSONObject oneField : belongFields) {
                                fieldIdSb.append(oneField.getString("fieldId")).append(",");
                                fieldNameSb.append(oneField.getString("fieldName")).append(",");
                            }
                            oneStandard.put("belongFieldIds", fieldIdSb.substring(0, fieldIdSb.length() - 1));
                            oneStandard.put("belongFieldNames", fieldNameSb.substring(0, fieldNameSb.length() - 1));
                        }
                    }
                }
            }
            // 查询总数
            queryParam.remove("startIndex");
            queryParam.remove("pageSize");
            int totalResult = standardDao.countFzsjbzList(queryParam);
            result.setData(queryPageResult);
            result.setTotal(totalResult);
        } catch (Exception e) {
            logger.error("Exception in queryStandard", e);
            result.setSuccess(false);
            result.setMessage("系统异常！");
        }
        return result;
    }
    // 拼接请求参数和分页排序参数
    private void getQueryStandardParams(HttpServletRequest request, Map<String, Object> queryParam) {
        queryParam.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        // 分页
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        queryParam.put("startIndex", "0" + pageIndex * pageSize);
        queryParam.put("pageSize", "0" + pageSize);
        // 排序
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            queryParam.put("sortField", sortField);
            queryParam.put("sortOrder", sortOrder);
        }
        // 过滤条件
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    if ("publishTimeFrom".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), -8));
                    }
                    if ("publishTimeTo".equalsIgnoreCase(name)) {
                        value = DateUtil.formatDate(DateUtil.addHour(DateUtil.parseDate(value), 16));
                    }
                    if ("systemIds".equalsIgnoreCase(name)) {
                        JSONArray systemIdArr = JSONArray.parseArray(value);
                        if (systemIdArr != null && !systemIdArr.isEmpty()) {
                            queryParam.put(name, systemIdArr.toJavaList(String.class));
                        }
                    } else {
                        queryParam.put(name, value);
                    }
                }
            }
        }
    }
    private void getUserId2Name(Map<String, String> userId2UserName) {
        Map<String, Object> params = new HashMap<>();
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, String>> userList = standardDao.queryUserList(params);
        if (userList != null && !userList.isEmpty()) {
            for (Map<String, String> oneMap : userList) {
                if (StringUtils.isNotBlank(oneMap.get("userName")) && StringUtils.isNotBlank(oneMap.get("userId"))) {
                    userId2UserName.put(oneMap.get("userId"), oneMap.get("userName"));
                }
            }
        }
    }
    private boolean checkStandardFileExist(String standardId, boolean isPublicStandard) {
        File file = null;
        if (!isPublicStandard) {
            file = findStandardFile(standardId, ConstantUtil.DOWNLOAD);
        } else {
            file = findPublicStandardFile(standardId);
        }
        if (file == null || !file.exists()) {
            return false;
        }
        return true;
    }
    private File findStandardFile(String standardId, String action) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = ConstantUtil.DOWNLOAD.equalsIgnoreCase(action)
                ? WebAppUtil.getProperty("standardFilePathBase") : WebAppUtil.getProperty("standardFilePathBase_preview");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }
    private File findPublicStandardFile(String standardId) {
        if (StringUtils.isBlank(standardId)) {
            logger.warn("standardId is blank");
            return null;
        }
        String filePathBase = WebAppUtil.getProperty("publicStandardFilePath");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find filePathBase");
            return null;
        }
        String fileFullPath = filePathBase + File.separator + standardId + ".pdf";
        File file = new File(fileFullPath);
        if (!file.exists()) {
            return null;
        }
        return file;
    }
    // 通过userId逗号分隔字符串获得userName逗号分隔字符串
    private String toGetUserNameByIds(String userIdStr, Map<String, String> userId2UserName) {
        StringBuilder userNameSb = new StringBuilder();
        if (StringUtils.isBlank(userIdStr)) {
            return "";
        }
        String[] userIdArr = userIdStr.split(",", -1);
        for (String userId : userIdArr) {
            if (userId2UserName.containsKey(userId)) {
                userNameSb.append(userId2UserName.get(userId)).append(",");
            }
        }
        return userNameSb.substring(0, userNameSb.length() - 1);
    }
}
