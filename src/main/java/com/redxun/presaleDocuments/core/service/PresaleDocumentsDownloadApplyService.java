package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsDao;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsDownloadApplyDao;
import com.redxun.presaleDocuments.core.util.PresaleDocumentsConst;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAApplyDao;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAXiazaiApplyDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.sys.org.entity.OsDimension;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PresaleDocumentsDownloadApplyService {
    private static final Logger logger = LoggerFactory.getLogger(PresaleDocumentsDownloadApplyService.class);
    @Autowired
    private PresaleDocumentsDownloadApplyDao presaleDocumentsDownloadApplyDao;
    @Autowired
    private PresaleDocumentsDao presaleDocumentsDao;
    @Autowired
    private PresaleDocumentsFileService presaleDocumentsFileService;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        boolean isPresaleDocumentsAdmin = commonInfoManager.judgeUserIsPointGroup("PresaleDocumentsAdmin", OsDimension.DIM_ROLE_ID,
                "0", ContextUtil.getCurrentUserId(), "1");
        if (!ContextUtil.getCurrentUser().getUserNo().equalsIgnoreCase("admin")
                && !isPresaleDocumentsAdmin) {
            params.put("currentUserId", ContextUtil.getCurrentUserId());
            params.put("roleName", "other");
        }
        List<JSONObject> businessList = presaleDocumentsDownloadApplyDao.dataListQuery(params);
        // 查询当前处理人--会签不用这个
        //xcmgProjectManager.setTaskCurrentUser(businessList);
        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = presaleDocumentsDownloadApplyDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids, String[] instIds) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        presaleDocumentsDownloadApplyDao.deleteBusiness(params);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;
    }

    //..
    public JsonPageResult<?> getDownloadDocList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        String mainId = RequestUtil.getString(request, "mainId");
        JSONObject downloadApply = this.getDataById(mainId);
        List<String> documentIds = new ArrayList<>();
        if (downloadApply.containsKey("documentIds") &&
                StringUtil.isNotEmpty(downloadApply.getString("documentIds"))) {
            documentIds = Arrays.asList(downloadApply.getString("documentIds").split(","));
        } else {
            documentIds.add("documentId");
        }
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        params.put("ids", documentIds);
        List<JSONObject> businessList = presaleDocumentsDao.dataListQuery(params);
        int businessListCount = presaleDocumentsDao.countDataListQuery(params);
        //saleModel,designModel,materialCode,productManagerName,fileIds,fileNames
        for (JSONObject jsonObject : businessList) {
            JSONArray jsonArray = jsonObject.getJSONArray("productSpectrum");
            if (jsonArray != null && !jsonArray.isEmpty()) {
                StringBuilder saleModelBuilder = new StringBuilder();
                StringBuilder designModelBuilder = new StringBuilder();
                StringBuilder materialCodeBuilder = new StringBuilder();
                StringBuilder productManagerNameBuilder = new StringBuilder();
                for (Object object : jsonArray) {
                    saleModelBuilder.append(((JSONObject) object).getString("saleModel_item")).append(",");
                    designModelBuilder.append(((JSONObject) object).getString("designModel_item")).append(",");
                    materialCodeBuilder.append(((JSONObject) object).getString("materialCode_item")).append(",");
                    productManagerNameBuilder.append(((JSONObject) object).getString("productManagerName_item")).append(",");
                }
                saleModelBuilder.delete(saleModelBuilder.length() - 1, saleModelBuilder.length());
                designModelBuilder.delete(designModelBuilder.length() - 1, designModelBuilder.length());
                materialCodeBuilder.delete(materialCodeBuilder.length() - 1, materialCodeBuilder.length());
                productManagerNameBuilder.delete(productManagerNameBuilder.length() - 1, productManagerNameBuilder.length());
                jsonObject.put("saleModel", saleModelBuilder.toString());
                jsonObject.put("designModel", designModelBuilder.toString());
                jsonObject.put("materialCode", materialCodeBuilder.toString());
                jsonObject.put("productManagerName", productManagerNameBuilder.toString());
            }
            params.clear();
            params.put("businessIds", Arrays.stream(jsonObject.getString("id").split(",")).collect(Collectors.toList()));
            List<JSONObject> fileListInfos = presaleDocumentsFileService.getFileListInfos(params);
            if (!fileListInfos.isEmpty()) {
                StringBuilder fileIdsBuilder = new StringBuilder();
                StringBuilder fileNamesBuilder = new StringBuilder();
                for (JSONObject fileListInfo : fileListInfos) {
                    fileIdsBuilder.append(fileListInfo.getString("id")).append(",");
                    fileNamesBuilder.append(fileListInfo.getString("fileName")).append(",");
                }
                fileIdsBuilder.delete(fileIdsBuilder.length() - 1, fileIdsBuilder.length());
                fileNamesBuilder.delete(fileNamesBuilder.length() - 1, fileNamesBuilder.length());
                jsonObject.put("fileIds", fileIdsBuilder.toString());
                jsonObject.put("fileNames", fileNamesBuilder.toString());
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getDataById(String id) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(id)) {
            return result;
        }
        JSONObject jsonObject = presaleDocumentsDownloadApplyDao.getDataById(id);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            this.createBusiness(jsonObject);
        } else {
            this.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public void createBusiness(JSONObject formData) {
        try {
            formData.put("id", IdUtil.getId());
            //@lwgkiller:此处是因为(草稿状态和空状态)无节点，提交后首节点会跳过，因此默认将首节点（编制中）的编号进行初始化写入
            formData.put("businessStatus", "A");
            formData.put("applyUserId", ContextUtil.getCurrentUserId());
            formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("CREATE_TIME_", new Date());
            this.processDocumentIds(formData);
            presaleDocumentsDownloadApplyDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processDocumentIds(formData);
            presaleDocumentsDownloadApplyDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..处理documentIds
    private void processDocumentIds(JSONObject jsonObject) {
        if (jsonObject.containsKey("documentListGrid")
                && StringUtil.isNotEmpty(jsonObject.getString("documentListGrid"))) {
            StringBuilder stringBuilder = new StringBuilder();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("documentListGrid");
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        stringBuilder.append(itemDataJson.getString("id")).append(",");
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        stringBuilder.append(itemDataJson.getString("id")).append(",");
                    }
                } else {//处理没变化的
                    stringBuilder.append(itemDataJson.getString("id")).append(",");
                }

            }
            if (stringBuilder.length() > 0) {
                jsonObject.put("documentIds", stringBuilder.substring(0, stringBuilder.length() - 1));
            } else {
                jsonObject.put("documentIds", "");
            }
        } else if (!jsonObject.containsKey("documentListGrid")) {
            jsonObject.put("documentIds", "");
        }
    }
}
