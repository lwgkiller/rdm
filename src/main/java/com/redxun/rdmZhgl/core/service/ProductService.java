package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.excel.ExcelReaderUtil;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.ExcelUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.dao.CommonInfoDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.PdmApiManager;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.dao.ProductConfigDao;
import com.redxun.rdmZhgl.core.dao.ProductDao;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.util.ResultUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectSchedulerDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

/**
 * @author zhangzhen
 */
@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Resource
    ProductDao productDao;
    @Resource
    ProductConfigDao productConfigDao;
    @Resource
    CommonInfoManager commonInfoManager;
    @Resource
    CommonInfoDao commonInfoDao;
    @Autowired
    private XcmgProjectSchedulerDao xcmgProjectSchedulerDao;
    @Resource
    private PdmApiManager pdmApiManager;

    public static void convertDate(List<Map<String, Object>> list) {
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> oneApply : list) {
                for (String key : oneApply.keySet()) {
                    if (key.endsWith("_TIME_") || key.endsWith("_time") || key.endsWith("_date")) {
                        if (oneApply.get(key) != null) {
                            oneApply.put(key, DateUtil.formatDate((Date)oneApply.get(key), "yyyy-MM-dd"));
                        }
                    }
                }
            }
        }
    }

    public JsonPageResult query(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            // 管理员和分管领导看所有的，项目负责人和项目主管看对应的数据，添加人
            String ids = CommonFuns.nullToString(params.get("mainIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            } else {
                JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-GLY");
                // 各部门公共账号角色看所有数据
                JSONObject commmonPri = commonInfoManager.hasPermission("GBMXMGGZHJS");
                if (resultJson.getBoolean("XPSZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || commmonPri.getBoolean("GBMXMGGZHJS")
                    || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                } else {
                    params.put("userId", ContextUtil.getCurrentUserId());
                }
            }
            list = productDao.query(params);
            params = new HashMap<>(16);
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            } else {
                JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-GLY");
                JSONObject commmonPri = commonInfoManager.hasPermission("GBMXMGGZHJS");
                if (resultJson.getBoolean("XPSZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || commmonPri.getBoolean("GBMXMGGZHJS")
                    || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                } else {
                    params.put("userId", ContextUtil.getCurrentUserId());
                }
            }
            params = CommonFuns.getSearchParam(params, request, false);
            totalList = productDao.query(params);
            int flag = 1;
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(list.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", list.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(list);
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 产品主管及项目负责人能看到本人产品计划，技术管理部几个调度员、三个主任、袁部、耿总能查看全部。其余人员无权限查看此版块内容
     */
    public List<Map<String, Object>> getBaseList(HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String userId = ContextUtil.getCurrentUserId();
            params = CommonFuns.getSearchParam(params, request, false);
            JSONObject xpszJhggyJson = commonInfoManager.hasPermission("XPSZ-JHGGY");
            JSONObject adminJson = commonInfoManager.hasPermission("XPSZ-GLY");
            JSONObject fGzrJson = commonInfoManager.hasPermission("JSZXZR");
            Boolean techLeader = commonInfoManager.judgeUserIsDeptRespor(userId, RdmConst.JSGLB_NAME);
            if (xpszJhggyJson.getBoolean("XPSZ-JHGGY") || xpszJhggyJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                || fGzrJson.getBoolean("JSZXZR") || techLeader || adminJson.getBoolean("XPSZ-GLY")
                || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
            } else {
                params.put("userId", userId);
            }
            String ids = CommonFuns.nullToString(params.get("mainIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            }
            result = productDao.getBaseList(params);
            List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
            for (Map<String, Object> temp : result) {
                String field = "";
                String stage = "";
                String stageCode = "";
                int sort = 0;
                for (String key : temp.keySet()) {
                    if (key.endsWith("_date")) {
                        Object planDate = temp.get(key);
                        if (planDate != null) {
                            int sortIndex = getSort(key);
                            if (sortIndex > sort) {
                                sort = sortIndex;
                                field = key;
                            }
                        }
                    }
                }
                if (StringUtil.isNotEmpty(field)) {
                    for (int i = 0; i < itemList.size(); i++) {
                        JSONObject jsonObject = itemList.get(i);
                        if (field.equals(jsonObject.getString("itemCode"))) {
                            int index = jsonObject.getInteger("sort");
                            if (index == itemList.size()) {
                                stage = jsonObject.getString("itemName");
                                stageCode = jsonObject.getString("itemCode");
                                break;
                            } else {
                                stage = itemList.get(index).getString("itemName");
                                stageCode = itemList.get(index).getString("itemCode");
                                break;
                            }
                        }
                    }

                } else {
                    // 返回第一个节点
                    stage = itemList.get(0).getString("itemName");
                    stageCode = itemList.get(0).getString("itemCode");
                }
                // 获取阶段时间
                JSONObject obj = productDao.getPlanDate(CommonFuns.nullToString(temp.get("id")));
                temp.put("processInfo", stage);
                temp.put("plan_date", obj.getDate(stageCode));
            }
            convertDate(result);
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
        }
        return result;
    }

    public JSONArray getJsonArray(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        JSONArray list = productDao.getJsonArray(jsonObject);
        return list;
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

            // 查重,用productId，认为某一年范围内只能关联一个
            JSONObject queryResult = queryDuplicateByProductId(objBody);
            if (!queryResult.getBooleanValue("success")) {
                logger.error("所关联的产品型谱在本年度已被其他新品关联，不能重复关联！");
                return queryResult;
            }

            String id = IdUtil.getId();
            objBody.put("id", id);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
            resultJson.put("id", id);
            productDao.addObject(objBody);
            insertItem(id);
        } catch (Exception e) {
            logger.error("Exception in add 添加异常！", e);
            return ResultUtil.result(false, "添加异常！", "");
        }
        return ResultUtil.result(true, "新增成功", resultJson);
    }

    public void insertItem(String mainId) {
        Map<String, Object> param;
        param = new HashMap<>(16);
        param.put("id", IdUtil.getId());
        param.put("mainId", mainId);
        param.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        param.put("CREATE_TIME_", new Date());
        param.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        param.put("UPDATE_TIME_", new Date());
        param.put("itemType", "1");
        productDao.addItem(param);
        param.put("id", IdUtil.getId());
        param.put("itemType", "3");
        productDao.addItem(param);
        param.put("id", IdUtil.getId());
        param.put("itemType", "4");
        productDao.addItem(param);
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
            // 查重,用productId，认为某一年范围内只能关联一个
            JSONObject queryResult = queryDuplicateByProductId(objBody);
            if (!queryResult.getBooleanValue("success")) {
                logger.error("所关联的产品型谱在本年度已被其他新品关联，不能重复关联！");
                return queryResult;
            }
            productDao.updateObject(objBody);
            resultJson.put("id", objBody.get("id"));
        } catch (Exception e) {
            logger.error("Exception in update 更新异常", e);
            return ResultUtil.result(false, "更新异常！", "");
        }
        return ResultUtil.result(true, "更新成功", resultJson);
    }

    // 查重
    private JSONObject queryDuplicateByProductId(Map<String, Object> objBody) {
        if (objBody.get("productId") != null && StringUtils.isNotBlank(objBody.get("productId").toString())) {
            JSONObject paramJson = new JSONObject();
            paramJson.put("belongYear", objBody.get("belongYear"));
            paramJson.put("productId", objBody.get("productId"));
            if (objBody.get("id") != null && StringUtils.isNotBlank(objBody.get("id").toString())) {
                paramJson.put("excludeId", objBody.get("id").toString());
            }
            List<JSONObject> jsonObject = productDao.getProductObjByProductIdAndYear(paramJson);
            if (jsonObject != null && !jsonObject.isEmpty()) {
                return ResultUtil.result(false, "操作失败，产品型谱已被其他新品关联！", "");
            }
        }
        return ResultUtil.result(true, "", "");
    }

    public void saveOrUpdateItem(String changeGridDataStr) {
        JSONObject resultJson = new JSONObject();
        try {
            JSONArray changeGridDataJson = JSONObject.parseArray(changeGridDataStr);
            for (int i = 0; i < changeGridDataJson.size(); i++) {
                JSONObject oneObject = changeGridDataJson.getJSONObject(i);
                String state = oneObject.getString("_state");
                String id = oneObject.getString("id");
                if ("added".equals(state) || StringUtils.isBlank(id)) {
                    // 新增
                    oneObject.put("id", IdUtil.getId());
                    oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("CREATE_TIME_", new Date());
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    // 根据负责人id 获取对应部门
                    productDao.addItem(oneObject);
                } else if ("modified".equals(state)) {
                    oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    oneObject.put("UPDATE_TIME_", new Date());
                    productDao.updateItem(oneObject);
                } else if ("removed".equals(state)) {
                    // 删除
                    productDao.delItem(oneObject.getString("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存信息异常", e);
            return;
        }
    }

    public JSONObject remove(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            Map<String, Object> params = new HashMap<>(16);
            String ids = request.getParameter("ids");
            String[] idArr = ids.split(",", -1);
            List<String> idList = Arrays.asList(idArr);
            params.put("ids", idList);
            productDao.batchDelete(params);
            productDao.batchDeleteItems(params);
        } catch (Exception e) {
            logger.error("Exception in update 删除科技项目", e);
            return ResultUtil.result(false, "删除科技项目异常！", "");
        }
        return ResultUtil.result(true, "删除成功", resultJson);
    }

    public JSONObject getObjectById(String id) {
        JSONObject jsonObject = productDao.getObjectById(id);
        return jsonObject;
    }

    public List<JSONObject> getItemList(HttpServletRequest request) {
        List<JSONObject> resultArray = new ArrayList<>();
        try {
            String mainId = request.getParameter("mainId");
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", mainId);
            resultArray = productDao.getItemList(paramJson);
        } catch (Exception e) {
            logger.error("Exception in getItemList", e);
        }
        return resultArray;
    }

    public void updateDate(JSONObject jsonObject) {
        try {
            JSONObject paramJson = new JSONObject();
            paramJson.put("id", jsonObject.getString("id"));
            paramJson.put(jsonObject.getString("field"), jsonObject.get("date"));
            String itemType = jsonObject.getString("itemType");
            productDao.updateDate(paramJson);
            List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
            if ("3".equals(itemType)) {
                // 原始表修改
                paramJson = new JSONObject();
                paramJson.put("mainId", jsonObject.getString("mainId"));
                paramJson.put(jsonObject.getString("field"), jsonObject.get("date"));
                paramJson.put("itemType", "1");
                productDao.updateOrgDate(paramJson);
            }
            // 判断一遍整体进度状态
            if ("4".equals(itemType)) {
                String mainId = jsonObject.getString("mainId");
                JSONObject json = new JSONObject();
                json.put("mainId", mainId);
                json.put("type", "type");
                List<JSONObject> resultArray = productDao.getItemList(json);
                if (resultArray != null && resultArray.size() == 2) {
                    JSONObject planJson = resultArray.get(0);
                    JSONObject actJson = resultArray.get(1);
                    String field = "";
                    String stage = "";
                    int sort = 0;
                    for (String key : actJson.keySet()) {
                        if (key.endsWith("_date")) {
                            Object finishDate = actJson.get(key);
                            if (finishDate != null) {
                                int sortIndex = getSort(key);
                                if (sortIndex > sort) {
                                    sort = sortIndex;
                                    field = key;
                                }
                            }
                        }
                    }
                    if (StringUtil.isNotEmpty(field)) {
                        for (int i = 0; i < itemList.size(); i++) {
                            JSONObject itemJson = itemList.get(i);
                            int index = itemJson.getInteger("sort");
                            if (index == itemList.size()) {
                                stage = itemJson.getString("itemCode");
                                break;
                            }
                            if (index > sort) {
                                String currentField = itemJson.getString("itemCode");
                                if (planJson.get(currentField) != null) {
                                    stage = currentField;
                                    break;
                                }
                            }
                        }
                    } else {
                        // 返回第一个节点
                        // 返回最新计划第一个不等于null的
                        JSONObject planObj = productDao.getProductObjByMainId(mainId);
                        String stageCode = "";
                        int planSort = 26;
                        for (String key : planObj.keySet()) {
                            if (key.endsWith("_date")) {
                                Object planDate = planObj.get(key);
                                if (planDate != null) {
                                    int sortIndex = getSort(key);
                                    if (sortIndex < planSort) {
                                        planSort = sortIndex;
                                        stageCode = key;
                                    }
                                }
                            }
                        }
                        if (StringUtil.isNotEmpty(stageCode)) {
                            stage = stageCode;
                        } else {
                            stage = itemList.get(0).getString("itemCode");
                        }
                    }
                    Date planDate = planJson.getDate(stage);
                    if (planDate != null) {
                        Date actDate = actJson.getDate(stage);
                        String processStatus = "1";
                        if (actDate == null) {
                            long diff = System.currentTimeMillis() - planDate.getTime();
                            long days = diff / (1000 * 60 * 60 * 24);
                            if (days >= 4 && days <= 8) {
                                processStatus = "2";
                            } else if (days > 8) {
                                processStatus = "3";
                            }
                        }
                        Map<String, Object> objBody = new HashMap<>(16);
                        objBody.put("id", mainId);
                        objBody.put("processStatus", processStatus);
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        productDao.updateObject(objBody);
                    } else {
                        Map<String, Object> objBody = new HashMap<>(16);
                        objBody.put("id", mainId);
                        objBody.put("processStatus", "1");
                        objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                        productDao.updateObject(objBody);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateDate", e);
        }
    }

    public void updateProductStatus() {

    }

    public JSONObject verifyPic(JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        try {
            String mainId = jsonObject.getString("id");
            List<JSONObject> list = productDao.getFileListByMainId(mainId);
            boolean frontFlag = false, backFlag = false, leftFlag = false, rightFlag = false;
            if (list != null && list.size() >= 4) {
                for (JSONObject temp : list) {
                    if ("1".equals(temp.getString("picType"))) {
                        frontFlag = true;
                    }
                    if ("2".equals(temp.getString("picType"))) {
                        backFlag = true;
                    }
                    if ("3".equals(temp.getString("picType"))) {
                        leftFlag = true;
                    }
                    if ("4".equals(temp.getString("picType"))) {
                        rightFlag = true;
                    }
                }
                if (frontFlag && backFlag && leftFlag && rightFlag) {
                    resultJson = ResultUtil.result(true, "", null);
                } else {
                    resultJson = ResultUtil.result(false, "请至少上传正反左右四张样机照片！", null);
                }
            } else {
                resultJson = ResultUtil.result(false, "请至少上传正反左右四张样机照片！", null);
            }

        } catch (Exception e) {
            logger.error("Exception in verifyPic", e);
        }
        return resultJson;
    }

    public JSONObject verifyMarket(JSONObject jsonObject) {
        JSONObject resultJson = new JSONObject();
        try {
            String mainId = jsonObject.getString("mainId");
            JSONObject object = productDao.getProductInfoByMainId(mainId);
            if (object.getString("isNewModel") == null && object.getString("ssrq_date") == null) {
                resultJson = ResultUtil.result(false, "是否新销售型号与上市日期必须先填写！", null);
            } else if (object.getString("isNewModel") == null) {
                resultJson = ResultUtil.result(false, "是否新销售型号先填写！", null);
            } else if (object.getString("ssrq_date") == null) {
                resultJson = ResultUtil.result(false, "上市日期必须先填写！", null);
            } else {
                resultJson = ResultUtil.result(true, "", null);
            }

        } catch (Exception e) {
            logger.error("Exception in verifyMarket", e);
        }
        return resultJson;
    }

    /**
     * 新增保存文件到磁盘和数据库
     *
     * @param request
     */
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
            String mainId = toGetParamVal(parameters.get("mainId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = StringUtils.isBlank(mainId) ? WebAppUtil.getProperty("commonFilePathBase")
                : WebAppUtil.getProperty("xpszFilePathBase");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + (StringUtils.isNotBlank(mainId) ? (File.separator + mainId) : "");
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + id + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", id);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("mainId", mainId);
            fileInfo.put("picType", toGetParamVal(parameters.get("picType")));
            fileInfo.put("fileDesc", toGetParamVal(parameters.get("fileDesc")));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            productDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
        return;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public List<JSONObject> getFileListByMainId(HttpServletRequest request) {
        String mainId = RequestUtil.getString(request, "mainId");
        List<JSONObject> fileInfos = productDao.getFileListByMainId(mainId);
        CommonFuns.convertDateJSON(fileInfos, "yyyy-MM-dd");
        return fileInfos;
    }

    public List<JSONObject> getChangeRecordListByMainId(HttpServletRequest request) {
        String mainId = RequestUtil.getString(request, "mainId");
        List<JSONObject> list = productDao.getChangeRecordListByMainId(mainId);
        CommonFuns.convertDateJSON(list, "yyyy-MM-dd");
        return list;
    }

    public void deleteFileOnDisk(String mainId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("xpszFilePathBase"));
        String fullPath =
            fileBasePath.append(File.separator).append(mainId).append(File.separator).append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public JSONObject getReportJdzt(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String reportYear = request.getParameter("reportYear");
            String isImportant = request.getParameter("isImportant");
            JSONObject paramJson = new JSONObject();
            paramJson.put("reportYear", reportYear);
            paramJson.put("isImportant", isImportant);
            List<JSONObject> list = productDao.getReportJdzt(paramJson);
            resultJson.put("blue", "0");
            resultJson.put("yellow", "0");
            resultJson.put("red", "0");
            for (JSONObject temp : list) {
                String processStatus = temp.getString("processStatus");
                if ("1".equals(processStatus)) {
                    resultJson.put("blue", temp.get("total"));
                }
                if ("2".equals(processStatus)) {
                    resultJson.put("yellow", temp.get("total"));
                }
                if ("3".equals(processStatus)) {
                    resultJson.put("red", temp.get("total"));
                }
            }
        } catch (Exception e) {
            logger.error("Exception in getReportJdzt", e);
            return ResultUtil.result(false, "获取进度状态异常！", resultJson);
        }
        return ResultUtil.result(true, "", resultJson);
    }

    public JSONObject getReportJdtj(HttpServletRequest request) {
        JSONObject resultJson = new JSONObject();
        try {
            String startYear = request.getParameter("startYear");
            String endYear = request.getParameter("endYear");
            String isImportant = request.getParameter("isImportant");
            JSONObject paramJson = new JSONObject();
            paramJson.put("startYear", startYear);
            paramJson.put("endYear", endYear);
            paramJson.put("isImportant", isImportant);
            paramJson.put("reportType", "tzgd_date");
            JSONObject resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("tzgd", resultObj.getIntValue("num"));
            }
            paramJson.put("reportType", "zkjdh_date");
            resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("sztz", resultObj.getIntValue("num"));
            }
            paramJson.put("reportType", "yjwczcs_date");
            resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("yjwc", resultObj.getIntValue("num"));
            }
            paramJson.put("reportType", "gykhkssj_date");
            resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("gykh", resultObj.getIntValue("num"));
            }
            paramJson.put("reportType", "xplwcsj_date");
            resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("xpl", resultObj.getIntValue("num"));
            }
            paramJson.put("reportType", "ssrq_date");
            resultObj = productDao.getReportJdtj(paramJson);
            if (resultObj != null) {
                resultJson.put("ss", resultObj.getIntValue("num"));
            }
        } catch (Exception e) {
            logger.error("Exception in getReportJdtj", e);
            return ResultUtil.result(false, "获取进度统计异常！", resultJson);
        }
        return ResultUtil.result(true, "", resultJson);
    }

    public int getSort(String field) {
        List<JSONObject> itemList = productConfigDao.getItemList(new JSONObject());
        int sort = 0;
        for (int i = 0; i < itemList.size(); i++) {
            JSONObject jsonObject = itemList.get(i);
            if (field.equals(jsonObject.getString("itemCode"))) {
                sort = jsonObject.getInteger("sort");
                break;
            }
        }
        return sort;
    }

    /**
     * 模板下载
     *
     * @return
     */
    public ResponseEntity<byte[]> importTemplateDownload() {
        try {
            String fileName = "新品试制导入模板.xlsx";
            // 创建文件实例
            File file =
                new File(MaterielService.class.getClassLoader().getResource("templates/zhgl/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    public void importProduct(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
            MultipartFile fileObj = multipartRequest.getFile("importFile");
            if (fileObj == null) {
                result.put("message", "数据导入失败，内容为空！");
                return;
            }
            String belongYear = request.getParameter("belongYear");
            String fileName = ((CommonsMultipartFile)fileObj).getFileItem().getName();
            ((CommonsMultipartFile)fileObj).getFileItem().getName().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION);
            Workbook wb = null;
            if (fileName.endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)) {
                wb = new HSSFWorkbook(fileObj.getInputStream());
            } else if (fileName.endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)) {
                wb = new XSSFWorkbook(fileObj.getInputStream());
            }
            Sheet sheet = wb.getSheet("导入模板");
            if (sheet == null) {
                logger.error("找不到导入模板");
                result.put("message", "数据导入失败，找不到导入模板导入页！");
                return;
            }
            int rowNum = sheet.getPhysicalNumberOfRows();
            if (rowNum < 1) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }

            // 解析标题部分
            Row titleRow = sheet.getRow(0);
            if (titleRow == null) {
                logger.error("找不到标题行");
                result.put("message", "数据导入失败，找不到标题行！");
                return;
            }
            List<String> titleList = new ArrayList<>();
            for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                titleList.add(StringUtils.trim(titleRow.getCell(i).getStringCellValue()));
            }

            if (rowNum < 2) {
                logger.info("数据行为空");
                result.put("message", "数据导入完成，数据行为空！");
                return;
            }
            Map<String, String> userCertNo2UserId = new HashMap<>(16);
            commonInfoManager.getUserCert2Id(userCertNo2UserId);
            Map<String, Object> processStatus2Id = new HashMap<>(16);
            Map<String, Object> modelStatus2Id = new HashMap<>(16);
            Map<String, Object> SF2Id = new HashMap<>(16);
            Map<String, Object> important2Id = new HashMap<>(16);
            Map params = new HashMap<>(16);
            params.put("key", "XPSZ-CYHZT");
            CommonFuns.getCategoryName2Id(processStatus2Id, commonInfoDao.getDicValues(params));
            params = new HashMap<>(16);
            params.put("key", "YJZT");
            CommonFuns.getCategoryName2Id(modelStatus2Id, commonInfoDao.getDicValues(params));
            params.put("key", "YESORNO");
            CommonFuns.getCategoryName2Id(SF2Id, commonInfoDao.getDicValues(params));
            params.put("key", "ZYD");
            CommonFuns.getCategoryName2Id(important2Id, commonInfoDao.getDicValues(params));
            // 解析验证数据部分，任何一行的任何一项不满足条件，则直接返回失败
            List<Map<String, Object>> dataInsert = new ArrayList<>();
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 1; i < rowNum; i++) {
                Row row = sheet.getRow(i);
                JSONObject rowParse = generateDataFromRow(row, dataInsert, itemList, titleList, userCertNo2UserId,
                    processStatus2Id, modelStatus2Id, SF2Id, important2Id, belongYear);
                if (!rowParse.getBoolean("result")) {
                    result.put("message", "数据导入失败，第" + (i + 1) + "行数据错误：" + rowParse.getString("message"));
                    return;
                }
            }
            for (int i = 0; i < dataInsert.size(); i++) {
                dataInsert.get(i).put("belongYear", belongYear);
                productDao.addObject(dataInsert.get(i));
            }
            for (int j = 0; j < itemList.size(); j++) {
                productDao.addItem(itemList.get(j));
            }
            result.put("message", "数据导入成功！");
        } catch (Exception e) {
            logger.error("Exception in importProduct", e);
            result.put("message", "数据导入失败，系统异常！");
        }
    }

    private JSONObject generateDataFromRow(Row row, List<Map<String, Object>> dataInsert,
        List<Map<String, Object>> itemList, List<String> titleList, Map<String, String> userNo2UserId,
        Map<String, Object> processStatus2Id, Map<String, Object> modelStatus2Id, Map<String, Object> SF2Id,
        Map<String, Object> important2Id, String belongYear) {
        JSONObject oneRowCheck = new JSONObject();
        oneRowCheck.put("result", false);
        Map<String, Object> oneRowMap = new HashMap<>(16);
        Map<String, Object> itemRowMap = new HashMap<>(16);
        try {
            for (int i = 0; i < titleList.size(); i++) {
                String title = titleList.get(i);
                title = title.replaceAll(" ", "");
                Cell cell = row.getCell(i);
                String cellValue = null;
                if (cell != null) {
                    cellValue = ExcelUtil.getCellFormatValue(cell);
                }
                switch (title) {
                    case "序号":
                        break;
                    case "产品类型":
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "产品类型为空");
                            return oneRowCheck;
                        }
                        oneRowMap.put("productType", cellValue);
                        break;
                    case "产品型号":
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "产品型号为空");
                            return oneRowCheck;
                        }
                        // 根据产品型号从型谱中获取id，并从新品试制中查重
                        JSONObject paramJson = new JSONObject();
                        paramJson.put("belongYear", belongYear);
                        paramJson.put("designModel", cellValue);
                        List<JSONObject> jsonObjectList = productDao.queryXpszAndXpByProductName(paramJson);
                        if (jsonObjectList != null) {
                            if (jsonObjectList.size() > 0) {
                                oneRowCheck.put("message", "产品型号“" + cellValue + "”在本年度已被其他新品关联，不能重复关联！");
                                return oneRowCheck;
                            }
                        }
                        paramJson.clear();
                        paramJson.put("designModel", cellValue);
                        jsonObjectList = productDao.queryXpszAndXpByProductName(paramJson);
                        if (jsonObjectList != null) {
                            JSONObject oneObj = jsonObjectList.get(0);
                            oneRowMap.put("productId", oneObj.getString("id"));
                            oneRowMap.put("productModelReal", cellValue);
                            oneRowMap.put("productModel", cellValue);
                        }
                        break;
                    case "任务来源":
                        oneRowMap.put("taskFrom", cellValue);
                        break;
                    case "预算产品型号":
                        oneRowMap.put("budgetProductModel", cellValue);
                        break;
                    case "上报集团样机下线":
                        if (StringUtils.isNotBlank(cellValue) && SF2Id.containsKey(cellValue)) {
                            oneRowMap.put("downLine", SF2Id.get(cellValue));
                        }
                        break;
                    case "是否立项":
                        if (StringUtils.isNotBlank(cellValue) && SF2Id.containsKey(cellValue)) {
                            oneRowMap.put("isLx", SF2Id.get(cellValue));
                        }
                        break;
                    case "项目名称":
                        break;
                    case "是否倒排":
                        if (StringUtils.isNotBlank(cellValue) && SF2Id.containsKey(cellValue)) {
                            oneRowMap.put("isInverted", SF2Id.get(cellValue));
                        }
                        break;
                    case "重要度":
                        if (StringUtils.isNotBlank(cellValue) && !important2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "重要度为空");
                            return oneRowCheck;
                        }
                        oneRowMap.put("important", important2Id.get(cellValue));
                        break;
                    case "产品主管":
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "产品主管为空");
                            return oneRowCheck;
                        }
                        List<JSONObject> list = commonInfoManager.getUserIdByUserName(cellValue);
                        if (list != null && list.isEmpty()) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找不到对应的账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() > 1) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找到多个账号！");
                            return oneRowCheck;
                        } else if (list != null && list.size() == 1) {
                            String userId = list.get(0).getString("USER_ID_");
                            oneRowMap.put("productLeader", userId);
                        } else {
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                        break;
                    case "是否为新销售型号":
                        if (StringUtils.isNotBlank(cellValue) && SF2Id.containsKey(cellValue)) {
                            oneRowMap.put("isNewModel", SF2Id.get(cellValue));
                        }
                        break;
                    case "产业化状态":
                        if (StringUtils.isNotBlank(cellValue) && !processStatus2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "产业化状态为空");
                            return oneRowCheck;
                        }
                        oneRowMap.put("productStatus", processStatus2Id.get(cellValue));
                        break;
                    case "样机状态":
                        if (StringUtils.isNotBlank(cellValue) && !modelStatus2Id.containsKey(cellValue)) {
                            oneRowCheck.put("message", "样机状态为空");
                            return oneRowCheck;
                        }
                        oneRowMap.put("modelStatus", modelStatus2Id.get(cellValue));
                        break;
                    case "样机或小批量是否需要模具":
                        if (StringUtils.isNotBlank(cellValue) && SF2Id.containsKey(cellValue)) {
                            oneRowMap.put("isNeedModel", SF2Id.get(cellValue));
                        }
                        break;
                    case "项目负责人":
                        if (StringUtils.isBlank(cellValue)) {
                            oneRowCheck.put("message", "项目负责人为空");
                            return oneRowCheck;
                        }
                        List<JSONObject> userList = commonInfoManager.getUserIdByUserName(cellValue);
                        if (userList != null && userList.isEmpty()) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找不到对应的账号！");
                            return oneRowCheck;
                        } else if (userList != null && userList.size() > 1) {
                            oneRowCheck.put("message", "用户：" + cellValue + "在系统中找到多个账号！");
                            return oneRowCheck;
                        } else if (userList != null && userList.size() == 1) {
                            String userId = userList.get(0).getString("USER_ID_");
                            oneRowMap.put("responseMan", userId);
                        } else {
                            oneRowCheck.put("message", "根据用户名查找用户信息出错！");
                            return oneRowCheck;
                        }
                        break;
                    case "产品主要配置":
                        oneRowMap.put("mainConfig", cellValue.trim());
                        break;
                    case "方案会":
                        itemRowMap.put("fah_date", covertDateStr(cellValue.trim()));
                        break;
                    case "图纸归档":
                        itemRowMap.put("tzgd_date", covertDateStr(cellValue.trim()));
                        break;
                    case "图纸下发":
                        itemRowMap.put("tzxf_date", covertDateStr(cellValue.trim()));
                        break;
                    case "SAP建立":
                        itemRowMap.put("sapjl_date", covertDateStr(cellValue.trim()));
                        break;
                    case "长周期件一次性采购通知":
                        itemRowMap.put("czqj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "召开交底会":
                        itemRowMap.put("zkjdh_date", covertDateStr(cellValue.trim()));
                        break;
                    case "工艺方案":
                        itemRowMap.put("gyfa_date", covertDateStr(cellValue.trim()));
                        break;
                    case "PBOM建立":
                        itemRowMap.put("pbomjl_date", covertDateStr(cellValue.trim()));
                        break;
                    case "发试制通知单":
                        itemRowMap.put("fsztzd_date", covertDateStr(cellValue.trim()));
                        break;
                    case "生产订单":
                        itemRowMap.put("scdd_date", covertDateStr(cellValue.trim()));
                        break;
                    case "资源布局":
                        itemRowMap.put("zybj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "下料件或结构件到货时间":
                        itemRowMap.put("xljdhsj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "模具完成时间":
                        itemRowMap.put("mjwcsj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "长周期件到位":
                        itemRowMap.put("gzjdw_date", covertDateStr(cellValue.trim()));
                        break;
                    case "全部配套件完成":
                        itemRowMap.put("qbptjwc_date", covertDateStr(cellValue.trim()));
                        break;
                    case "初物检验":
                        itemRowMap.put("cwjy_date", covertDateStr(cellValue.trim()));
                        break;
                    case "样机完成转测试":
                        itemRowMap.put("yjwczcs_date", covertDateStr(cellValue.trim()));
                        break;
                    case "试制过程问题上传RDM":
                        itemRowMap.put("szgcwtqd_date", covertDateStr(cellValue.trim()));
                        break;
                    case "整机调试":
                        itemRowMap.put("zjtx_date", covertDateStr(cellValue.trim()));
                        break;
                    case "测试":
                        itemRowMap.put("cs_date", covertDateStr(cellValue.trim()));
                        break;
                    case "样机图纸整改完成时间":
                        itemRowMap.put("yjtzzgwcsj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "工业考核开始时间":
                        itemRowMap.put("gykhkssj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "小批量完成时间":
                        itemRowMap.put("xplwcsj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "三高试验完成":
                        itemRowMap.put("sgsywc_date", covertDateStr(cellValue.trim()));
                        break;
                    case "技术文件":
                        itemRowMap.put("jswj_date", covertDateStr(cellValue.trim()));
                        break;
                    case "型式试验":
                        itemRowMap.put("xssy_date", covertDateStr(cellValue.trim()));
                        break;
                    case "开发确认评审会":
                        itemRowMap.put("kfqrpsh_date", covertDateStr(cellValue.trim()));
                        break;
                    case "上市日期":
                        itemRowMap.put("ssrq_date", covertDateStr(cellValue.trim()));
                        break;
                    default:
                        oneRowCheck.put("message", "列“" + title + "”不存在");
                        return oneRowCheck;
                }
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
            oneRowCheck.put("message", e.getMessage());
            return oneRowCheck;
        }
        oneRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        oneRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        oneRowMap.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        String id = IdUtil.getId();
        oneRowMap.put("id", id);
        dataInsert.add(oneRowMap);
        itemRowMap.put("id", IdUtil.getId());
        itemRowMap.put("mainId", id);
        itemRowMap.put("itemType", "1");
        itemRowMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemRowMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        itemRowMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemList.add(itemRowMap);
        Map<String, Object> itemMap = new HashMap<>(16);
        itemMap.putAll(itemRowMap);
        itemMap.put("id", IdUtil.getId());
        itemMap.put("itemType", "3");
        itemList.add(itemMap);
        Map<String, Object> finishMap = new HashMap<>(16);
        finishMap.put("id", IdUtil.getId());
        finishMap.put("itemType", "4");
        finishMap.put("mainId", id);
        finishMap.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        finishMap.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        finishMap.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        finishMap.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        itemList.add(finishMap);
        oneRowCheck.put("result", true);
        return oneRowCheck;
    }

    public String covertDateStr(String paramStr) throws Exception {
        if ("—".equals(paramStr) || "已完成".equals(paramStr) || "——".equals(paramStr) || "完成".equals(paramStr)
            || StringUtils.isBlank(paramStr)) {
            return "";
        }
        if (paramStr.contains("-")) {
            Date dateV = DateFormatUtil.parse(paramStr, "yyyy-MM-dd");
            return DateFormatUtil.format(dateV, "yyyy-MM-dd");
        } else if (paramStr.contains("/")) {
            Date dateV = DateFormatUtil.parse(paramStr, "yyyy/MM/dd");
            return DateFormatUtil.format(dateV, "yyyy/MM/dd");
        } else {
            throw new Exception("日期值“" + paramStr + "”错误");
        }
    }

    /**
     * 导出表格
     */
    public void exportExcel(HttpServletResponse response, HttpServletRequest request) {
        try {
            Map<String, Object> params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "新品试制";
            // 管理员和分管领导看所有的，项目负责人和项目主管看对应的数据，添加人
            String ids = CommonFuns.nullToString(params.get("mainIds"));
            if (StringUtil.isNotEmpty(ids)) {
                String[] idArr = ids.split(",", -1);
                List<String> idList = Arrays.asList(idArr);
                params.put("ids", idList);
            } else {
                JSONObject resultJson = commonInfoManager.hasPermission("XPSZ-GLY");
                // 各部门公共账号角色看所有数据
                JSONObject commmonPri = commonInfoManager.hasPermission("GBMXMGGZHJS");
                if (resultJson.getBoolean("XPSZ-GLY") || resultJson.getBoolean(RdmConst.ALLDATA_QUERY_KEY)
                    || commmonPri.getBoolean("GBMXMGGZHJS")
                    || "admin".equals(ContextUtil.getCurrentUser().getUserNo())) {
                } else {
                    params.put("userId", ContextUtil.getCurrentUserId());
                }
            }
            List<Map<String, Object>> list = productDao.query(params);
            int flag = 1;
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                if (i == 0) {
                    map.put("rowNum", flag);
                    flag++;
                } else {
                    String mainId = CommonFuns.nullToString(map.get("mainId"));
                    String preMainId = CommonFuns.nullToString(list.get(i - 1).get("mainId"));
                    if (mainId.equals(preMainId)) {
                        map.put("rowNum", list.get(i - 1).get("rowNum"));
                    } else {
                        map.put("rowNum", flag);
                        flag++;
                    }
                }
            }
            convertDate(list);
            Map<String, Object> ZYD = commonInfoManager.genMap("ZYD");
            Map<String, Object> YJZT = commonInfoManager.genMap("YJZT");
            Map<String, Object> sfMap = commonInfoManager.genMap("YESORNO");
            Map<String, Object> cyhzt = commonInfoManager.genMap("XPSZ-CYHZT");
            Map<String, Object> jdzt = commonInfoManager.genMap("XPSZ-JDZT");
            Map<String, Object> timeType = commonInfoManager.genMap("timeType");
            for (Map<String, Object> map : list) {
                if (map.get("important") != null) {
                    map.put("importantText", ZYD.get(map.get("important")));
                }
                if (map.get("processStatus") != null) {
                    map.put("processStatusText", jdzt.get(map.get("processStatus")));
                }
                if (map.get("productStatus") != null) {
                    map.put("productStatusText", cyhzt.get(map.get("productStatus")));
                }
                if (map.get("modelStatus") != null) {
                    map.put("modelStatusText", YJZT.get(map.get("modelStatus")));
                }
                if (map.get("isNewModel") != null) {
                    map.put("isNewModelText", sfMap.get(map.get("isNewModel")));
                }
                if (map.get("isNeedModel") != null) {
                    map.put("isNeedModelText", sfMap.get(map.get("isNeedModel")));
                }
                if (map.get("itemType") != null) {
                    map.put("itemTypeText", timeType.get(map.get("itemType")));
                }
            }
            String[] fieldNames1 = {"序号", "产品类型", "产品设计型号", "重要度", "任务来源", "进度状态", "时间类别", "挖掘机械研究院", "挖掘机械研究院",
                "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "工艺技术部", "工艺技术部", "挖掘机械研究院", "制造管理部", "供方发展部", "采购管理部",
                "供方发展部", "采购管理部", "采购管理部", "质量保证部", "工程中心/各分厂", "工程中心/各分厂", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院",
                "工程中心/各分厂/制造管理部", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "挖掘机械研究院", "产品主管", "是否为新销售型号", "产业化状态",
                "样机状态", "样机或小批量是否需要模具", "项目负责人", "产品主要配置", "计划管理员", "备注", "变更原因", "计划变更"};
            String[] fieldNames2 = {"序号", "产品类型", "产品设计型号", "重要度", "任务来源", "进度状态", "时间类别", "方案会", "图纸归档", "图纸下发",
                "SAP建立", "长周期件一次性采购通知", "召开交底会", "工艺方案", "PBOM建立", "发试制通知单", "生产订单", "资源布局", "下料件到货时间", "模具完成时间",
                "长周期件到位", "全部配套件完成", "初物检验", "样机完成转测试", "试制过程问题长传RDM", "整机调试", "测试", "工业考核开始时间", "样机图纸整改完成时间",
                "小批量完成时间", "三高试验完成", "技术文件", " 型式试验/欧美认证/农机认证", "开发确认评审会", "上市日期", "产品主管", "是否为新销售型号", "产业化状态", "样机状态",
                "样机或小批量是否需要模具", "项目负责人", "产品主要配置", "计划管理员", "备注", "变更原因", "计划变更"};
            String[] fieldCodes = {"rowNum", "productType", "productModel", "importantText", "taskFrom",
                "processStatusText", "itemTypeText", "fah_date", "tzgd_date", "tzxf_date", "sapjl_date", "czqj_date",
                "zkjdh_date", "gyfa_date", "pbomjl_date", "fsztzd_date", "scdd_date", "zybj_date", "xljdhsj_date",
                "mjwcsj_date", "gzjdw_date", "qbptjwc_date", "cwjy_date", "yjwczcs_date", "szgcwtqd_date", "zjtx_date",
                "cs_date", "gykhkssj_date", "yjtzzgwcsj_date", "xplwcsj_date", "sgsywc_date", "jswj_date", "xssy_date",
                "kfqrpsh_date", "ssrq_date", "productLeaderName", "isNewModelText", "productStatusText",
                "modelStatusText", "isNeedModelText", "responseManName", "mainConfig", "planAdminName", "remark",
                "changeReason", "planChange"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportXpszExcel(list, fieldNames1, fieldNames2, fieldCodes);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  导出项目维度表格", e);
        }
    }

    /**
     * 导出表格
     */
    public void exportStageExcel(HttpServletResponse response, HttpServletRequest request) {
        try {
            List<Map<String, Object>> list = getBaseList(request);
            Map<String, Object> jdzt = commonInfoManager.genMap("XPSZ-JDZT");
            for (Map<String, Object> map : list) {
                if (map.get("processStatus") != null) {
                    map.put("processStatusText", jdzt.get(map.get("processStatus")));
                }
            }
            String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
            String excelName = nowDate + "重点产品当前阶段";
            String title = "重点产品当前阶段";
            String[] fieldNames = {"产品类型", "产品型号", "产品主管", "当前阶段", "计划完成日期", "进度状态"};
            String[] fieldCodes =
                {"productType", "productModel", "productLeaderName", "processInfo", "plan_date", "processStatusText"};
            HSSFWorkbook wbObj = YfjbExcelUtils.exportExcelWB(list, fieldNames, fieldCodes, title);
            // 输出
            ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in  重点产品当前阶段", e);
        }
    }

    public JSONObject batchChangePlanDate(HttpServletRequest request) {
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
            String mainId = CommonFuns.nullToString(objBody.get("id"));
            String startItemId = CommonFuns.nullToString(objBody.get("startItemId"));
            String endItemId = CommonFuns.nullToString(objBody.get("endItemId"));
            String days = CommonFuns.nullToString(objBody.get("delayDays"));
            int delayDays = Integer.parseInt(days);
            // 先将最新的数据复制一份 作为 历史数据，然后再修改数据
            JSONObject paramJson = new JSONObject();
            paramJson.put("mainId", mainId);
            paramJson.put("itemType", "3");
            Map<String, Object> resultMap = productDao.getItemObject(paramJson);
            resultMap.put("id", IdUtil.getId());
            resultMap.put("itemType", "2");
            productDao.addItem(resultMap);
            // 更新最新记录
            JSONObject startObj = productConfigDao.getItemObjById(startItemId);
            int startSort = startObj.getInteger("sort");
            JSONObject endObj = productConfigDao.getItemObjById(endItemId);
            int endSort = endObj.getInteger("sort");
            paramJson = new JSONObject();
            paramJson.put("startSort", startSort);
            paramJson.put("endSort", endSort);
            List<JSONObject> list = productConfigDao.getItemList(paramJson);
            JSONObject param = new JSONObject();
            for (JSONObject temp : list) {
                param.put(temp.getString("itemCode"), delayDays);
            }
            param.put("mainId", mainId);
            param.put("itemType", "3");
            productDao.updatePlanDate(param);
            // 更新主表
            StringBuffer changeStr = new StringBuffer();
            changeStr.append("从").append(startObj.getString("itemName")).append("节点到")
                .append(endObj.getString("itemName"));
            changeStr.append("节点延期").append(delayDays).append("天");
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", mainId);
            map.put("planChange", changeStr.toString());
            productDao.updateObject(map);
        } catch (Exception e) {
            logger.error("Exception in batchChangePlanDate ", e);
            return ResultUtil.result(false, "批量更改计划时间异常！", "");
        }
        return ResultUtil.result(true, "更改日期成功！", resultJson);
    }

    public JSONObject asyncPdmDelivery() {
        try {
            // 产品研发（整机）类的运行中项目，才需要推送
            JSONObject paramJson = new JSONObject();
            paramJson.put("status", "RUNNING");
            paramJson.put("categoryId", "02");
            List<JSONObject> projectList = xcmgProjectSchedulerDao.getProjectListByInfo(paramJson);
            String projectId;
            Map<String, Object> params = new HashMap(16);
            List<Map<String, Object>> deliveryList;
            String productIds;
            String productNames;
            for (JSONObject projectObj : projectList) {
                projectId = projectObj.getString("projectId");
                productIds = projectObj.getString("productIds");
                productNames = projectObj.getString("productNames");
                if (StringUtil.isEmpty(productIds) || "无".equals(productNames)) {
                    continue;
                }
                // 关联了产品的才推送
                params.put("projectId", projectId);
                deliveryList = pdmApiManager.getPdmProjectFiles(params);
                for (Map<String, Object> map : deliveryList) {
                    // 只有已经发布的才推送到新品试制中
                    if (CommonFuns.nullToString(map.get("approvalStatus")).contains("发布")) {
                        String deliveryId = CommonFuns.nullToString(map.get("deliveryId"));
                        String filePath = CommonFuns.nullToString(map.get("filePath"));
                        String createTime = CommonFuns.nullToString(map.get("CREATE_TIME_"));
                        String productName = getProductName(filePath);
                        // 对于PDM的交付物，只能通过路径中的文件夹解析所属的产品
                        if (StringUtil.isEmpty(productName)) {
                            continue;
                        } else {
                            sendPdmDeliveryToNewProduct(deliveryId, productName, createTime, productIds);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception in asyncPdmDelivery ", e);
            return ResultUtil.result(false, "同步失败！", "");
        }
        return ResultUtil.result(true, "同步成功！", null);
    }

    public void sendPdmDeliveryToNewProduct(String deliveryId, String productName, String createTime,
        String productIds) {
        try {
            // 从产品型谱中根据designModel获取id
            List<String> productIdList = xcmgProjectSchedulerDao.getProductIdsByName(productName);
            List<JSONObject> stageList = productConfigDao.getStageList(deliveryId);
            if (!stageList.isEmpty() && !productIdList.isEmpty()) {
                for (String productId : productIdList) {
                    if (productIds.indexOf(productId) > -1) {
                        JSONObject newProductObj = productDao.getNewProductInfo(productId);
                        if (newProductObj != null) {
                            String mainId = newProductObj.getString("id");
                            JSONObject paramJson = new JSONObject();
                            paramJson.put("mainId", mainId);
                            paramJson.put("itemType", "4");
                            int count = 0;
                            for (JSONObject temp : stageList) {
                                // 先判断阶段时间是否已经填写，已填写则不更新
                                paramJson.put("stageName", temp.getString("itemCode"));
                                String finishDate = productDao.getStageFinishDate(paramJson);
                                if (finishDate == null) {
                                    paramJson.put(temp.getString("itemCode"), createTime);
                                    count++;
                                }
                            }
                            if (count > 0) {
                                productDao.updateOrgDate(paramJson);
                            }
                        }
                    }
                }
            } else {
                logger.info(productName + "没有找到对应的产品编码或者，" + deliveryId + "交付物没有绑定新品试制节点");
            }
        } catch (Exception e) {
            logger.error("sendPdmDeliveryToNewProduct", e);
        }
    }

    public String getProductName(String filePath) {
        String productName = "";
        try {
            String[] filePathArray = filePath.split("/");
            if (filePathArray.length > 3) {
                productName = filePathArray[3];
            }
        } catch (Exception e) {
            logger.error("getProductName", e);
        }
        return productName;
    }

}
