package com.redxun.trialPartsProcess.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.materielextend.core.dao.MaterielApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.trialPartsProcess.core.dao.TrialPartsProcessDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.camel.util.FileUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import us.codecraft.webmagic.selector.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;

@Service
public class TrialPartsProcessService {
    private static final Logger logger = LoggerFactory.getLogger(TrialPartsProcessService.class);

    @Autowired
    private TrialPartsProcessDao trialPartsProcessDao;

    @Autowired
    private SysDicManager sysDicManager;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private MaterielApplyDao materielApplyDao;

    public JsonPageResult<?> queryBaseInfoList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "trialparts_process_base.CREATE_TIME_", "desc");
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    params.put(name, value);
                }
            }
        }
        // 增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = trialPartsProcessDao.queryBaseInfoList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        // 根据分页进行subList截取
        List<JSONObject> finalSubList = new ArrayList<JSONObject>();
        if (doPage) {
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            if (startSubListIndex < applyList.size()) {
                finalSubList = applyList.subList(startSubListIndex,
                    endSubListIndex < applyList.size() ? endSubListIndex : applyList.size());
            }
        } else {
            finalSubList = applyList;
        }
        result.setData(finalSubList);
        result.setTotal(applyList.size());
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public void saveBaseInfo(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("deptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("deptName", ContextUtil.getCurrentUser().getMainGroupName());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        trialPartsProcessDao.insertTrialPartsProcessBase(formData);
    }

    public void saveBatchInfo(HttpServletRequest request, String postBody) {
        JSONObject oneData = JSONObject.parseObject(postBody);
        String applyId = RequestUtil.getString(request, "applyId", "");
        try {
            JSONObject batchInfo = new JSONObject();
            batchInfo.put("applyId", applyId);
            batchInfo.put("trialBatch", oneData.getString("trialBatch"));
            batchInfo.put("trialBatchNum", oneData.getString("trialBatchNum"));
            batchInfo.put("trialProcess", oneData.getString("trialProcess"));
            batchInfo.put("finishDate", oneData.getString("finishDate"));
            if (StringUtils.isNotBlank(oneData.getString("id"))) {
                batchInfo.put("id", oneData.getString("id"));
                batchInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                batchInfo.put("UPDATE_TIME_", new Date());
                trialPartsProcessDao.updateTrialPartsProcessBatch(batchInfo);
            } else {
                batchInfo.put("id", IdUtil.getId());
                batchInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                batchInfo.put("CREATE_TIME_", new Date());
                trialPartsProcessDao.insertTrialPartsProcessBatch(batchInfo);
            }
        } catch (Exception e) {
            logger.error("Exception in saveBatchInfo", e);
        }
    }

    public JsonResult deleteBaseInfo(String[] ids) {
        List<String> applyIdList = Arrays.asList(ids);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (ids.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        // 删除附件
//        for (String applyId : applyIdList) {
//            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("SZLBJJDGZ", "wjscwz").getValue();
//            String filePath = filePathBase + File.separator + applyId;
//            File fileDir = new File(filePath);
//            FileUtil.removeDir(fileDir);
//        }
        // 删除主表(仅草稿状态可删除，当前只有删除主表可使用)
        param.put("applyIds", applyIdList);
        trialPartsProcessDao.deleteTrialPartsProcessBaseALL(param);
        // 删除批次表
//        trialPartsProcessDao.deleteTrialPartsProcessBatchALL(param);
//        // 删除批次详情
//        trialPartsProcessDao.deleteTrialPartsProcessBatchDetailALL(param);
//        // 删除附件信息
//        trialPartsProcessDao.deleteFileALL(param);
        return result;
    }

    public void saveBatchDetail(HttpServletRequest request, String postBody) {
        JSONArray jsonArray = JSONArray.parseArray(postBody);
        String applyId = RequestUtil.getString(request, "applyId", "");
        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject oneData = jsonArray.getJSONObject(i);
                JSONObject detailInfo = new JSONObject();
                detailInfo.put("applyId", applyId);
                detailInfo.put("trialBatch", oneData.getString("trialBatch"));
                detailInfo.put("vinCode", oneData.getString("vinCode"));
                detailInfo.put("workDuration", oneData.getString("workDuration"));
                detailInfo.put("deadline", oneData.getString("deadline"));
                if (StringUtils.isNotBlank(oneData.getString("id"))) {
                    detailInfo.put("id", oneData.getString("id"));
                    detailInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                    detailInfo.put("UPDATE_TIME_", new Date());
                    trialPartsProcessDao.updateTrialPartsProcessBatchDetail(detailInfo);
                } else {
                    detailInfo.put("id", IdUtil.getId());
                    detailInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                    detailInfo.put("CREATE_TIME_", new Date());
                    trialPartsProcessDao.insertTrialPartsProcessBatchDetail(detailInfo);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in saveBatchDetail", e);
        }
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
            String id = IdUtil.getId();
            String applyId = toGetParamVal(parameters.get("applyId"));
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("SZLBJJDGZ", "wjscwz").getValue();
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + applyId;
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
            fileInfo.put("applyId", applyId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileType", fileType);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            trialPartsProcessDao.insertFile(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public void updateBaseInfo(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        trialPartsProcessDao.updateTrialPartsProcessBase(formData);
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = trialPartsProcessDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        return obj;
    }

    public List<JSONObject> queryBatchInfo(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("applyId", RequestUtil.getString(request, "applyId", ""));
        rdmZhglUtil.addOrder(request, params, "trialparts_process_batch.CREATE_TIME_", "desc");
        List<JSONObject> batchList = trialPartsProcessDao.queryBatchInfo(params);
        for (JSONObject one : batchList) {
            if (one.get("finishDate") != null) {
                one.put("finishDate", DateUtil.formatDate((Date)one.get("finishDate"), "yyyy-MM-dd"));
            }
        }
        return batchList;
    }

    public List<JSONObject> queryBatchDetailInfo(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("applyId", RequestUtil.getString(request, "applyId", ""));
        params.put("trialBatch", RequestUtil.getString(request, "trialBatch", ""));
        rdmZhglUtil.addOrder(request, params, "trialparts_process_batch_detail.CREATE_TIME_", "desc");
        List<JSONObject> batchDetailList = trialPartsProcessDao.queryBatchDetailInfo(params);
        for (JSONObject one : batchDetailList) {
            if (one.get("deadline") != null) {
                one.put("deadline", DateUtil.formatDate((Date)one.get("deadline"), "yyyy-MM-dd"));
            }
        }
        return batchDetailList;
    }

    public List<JSONObject> queryFileList(JSONObject params) {
        List<JSONObject> fileList = trialPartsProcessDao.queryFileList(params);
        return fileList;
    }

    public JSONObject queryBatchInfoById(HttpServletRequest request) {
        String id = RequestUtil.getString(request, "id", "");
        if (StringUtils.isBlank(id)) {
            JSONObject result = new JSONObject();
            return result;
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", id);
            JSONObject res = trialPartsProcessDao.queryBatchInfoById(params);
            if (res.get("finishDate") != null) {
                res.put("finishDate", DateUtil.formatDate((Date)res.get("finishDate"), "yyyy-MM-dd"));
            }
            return res;
        }
    }

    public Map<String, String> queryUserNameByIds(Set<String> userIds) {
        Map<String, String> userId2Name = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return userId2Name;
        }
        List<JSONObject> userInfos = materielApplyDao.queryUserInfosByIds(new ArrayList<>(userIds));
        if (userInfos == null || userInfos.isEmpty()) {
            return userId2Name;
        }
        for (JSONObject oneUserInfo : userInfos) {
            userId2Name.put(oneUserInfo.getString("userId"), oneUserInfo.getString("userName"));
        }
        return userId2Name;
    }

    public String toGetUserNamesByIds(Map<String, String> userId2Name, List<String> userIdList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : userIdList) {
            if (userId2Name.containsKey(userId)) {
                stringBuilder.append(userId2Name.get(userId)).append(",");
            }
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return "";
    }

    public void exportTrialPratsInfo(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryBaseInfoList(request, false);
        List<JSONObject> listData = result.getData();
        for (JSONObject oneData : listData) {
            if (oneData.get("status") != null && StringUtils.isNotBlank(oneData.get("status").toString())) {
                String status = oneData.get("status").toString();
                switch (status) {
                    case "SUCCESS_END":
                        status = "可维护";
                        break;
                    case "DRAFTED":
                        status = "草稿";
                        break;
                    case "RUNNING":
                        status = "进行中";
                        break;
                    case "DISCARD_END":
                        status = "流程作废";
                        break;
                }
                oneData.put("status", status);
            }
            if (oneData.get("planDate") != null) {
                oneData.put("planDate", DateUtil.formatDate((Date)oneData.get("planDate"), "yyyy-MM-dd"));
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "试制零部件进度跟踪";
        String excelName = nowDate + title;
        String[] fieldNames = {"申请编号","申请人", "申请部门", "机型", "分类", "试制数量","试制单号","物料号（更改前）","物料描述（更改前）","供应商（更改前）","物料号（更改后）","物料描述（更改后）","供应商（更改后）",
                "互换性", "主要差异性", "计划上线日期","故障数","故障率","状态"};
        String[] fieldCodes = {"id","creater", "deptName", "model", "category", "trialNum","trialBillNum","trialMaterielNumBe", "trialMaterialDescBe", "supplierBe", "trialMaterielNumAf", "trialMaterialDescAf","supplierAf",
                "interchange", "differ","planDate","faultNum","faultRate", "status"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
