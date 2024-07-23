package com.redxun.rdMaterial.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.rdMaterial.core.dao.RdMaterialHandlingDao;
import com.redxun.rdMaterial.core.dao.RdMaterialInStorageDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RdMaterialHandlingService {
    private static Logger logger = LoggerFactory.getLogger(RdMaterialHandlingService.class);
    @Autowired
    private RdMaterialHandlingDao rdMaterialHandlingDao;
    @Autowired
    private RdMaterialFileService rdMaterialFileService;
    @Autowired
    private RdMaterialInStorageDao rdMaterialInStorageDao;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsGroupManager osGroupManager;

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = rdMaterialHandlingDao.dataListQuery(params);
        int businessListCount = rdMaterialHandlingDao.countDataListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getDataById(String id) throws Exception {
        return rdMaterialHandlingDao.getDataById(id);
    }

    //..
    public JsonResult deleteBusiness(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("businessIds", idList);
        List<JSONObject> files = rdMaterialFileService.getFileListInfos(params);//现获取到主档所有的文件信息备用
        rdMaterialFileService.deleteFileInfos(params);//删主档文件信息
        params.clear();
        params.put("ids", idList);
        rdMaterialHandlingDao.deleteBusiness(params);//删主档
        params.clear();
        params.put("mainIds", idList);
        List<JSONObject> itemList = rdMaterialHandlingDao.getItemList(params);//先获取到所有的明细备用
        List<String> itemIdList = new ArrayList<>();//获取到所有明细的id
        for (JSONObject item : itemList) {
            itemIdList.add(item.getString("id"));
        }
        rdMaterialHandlingDao.deleteItems(params);//删明细
        params.clear();
        params.put("businessIds", itemIdList);
        List<JSONObject> itemFiles = rdMaterialFileService.getFileListInfos(params);//现获取到明细所有的文件信息备用
        rdMaterialFileService.deleteFileInfos(params);//删明细文件信息
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //删除评审单相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoChuLiPingShen").getValue();
        for (JSONObject oneFile : files) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除评审单目录
        for (String oneBusinessId : idList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        //删除出库依据相关文件
        filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoChuKuYiJu").getValue();
        for (JSONObject oneFile : files) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除出库依据目录
        for (String oneBusinessId : idList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        //删除明细相关文件
        filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                "yanFaWuLiaoMingXi").getValue();
        for (JSONObject oneFile : itemFiles) {
            rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除明细目录
        for (String oneBusinessId : itemIdList) {
            rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        return result;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (jsonObject.containsKey("responsibleUserId") &&
                StringUtil.isNotEmpty(jsonObject.getString("responsibleUserId"))) {
            OsGroup mainDep = osGroupManager.getMainDeps(jsonObject.getString("responsibleUserId"), "1");
            jsonObject.put("responsibleDepId", mainDep.getGroupId());
            jsonObject.put("responsibleDep", mainDep.getName());
        }
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", "编辑中");
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialHandlingDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            rdMaterialHandlingDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public JsonResult doCommitBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (jsonObject.containsKey("responsibleUserId") &&
                StringUtil.isNotEmpty(jsonObject.getString("responsibleUserId"))) {
            OsGroup mainDep = osGroupManager.getMainDeps(jsonObject.getString("responsibleUserId"), "1");
            jsonObject.put("responsibleDepId", mainDep.getGroupId());
            jsonObject.put("responsibleDep", mainDep.getName());
        }
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", "已提交");
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processItems(jsonObject);
            this.processCheckQuantity(jsonObject);
            rdMaterialHandlingDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("businessStatus", "已提交");
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processItems(jsonObject);
            this.processCheckQuantity(jsonObject);
            rdMaterialHandlingDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    private void processItems(JSONObject jsonObject) throws Exception {
        if (jsonObject.containsKey("items")
                && StringUtil.isNotEmpty(jsonObject.getString("items"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("items");
            List<String> itemIds = new ArrayList<>();
            for (Object item : jsonArrayItemData) {
                JSONObject itemJson = (JSONObject) item;
                if (itemJson.containsKey("_state")) {//处理新增和修改
                    if (itemJson.getString("_state").equalsIgnoreCase("added")) {
                        itemJson.put("id", IdUtil.getId());
                        itemJson.put("mainId", jsonObject.getString("id"));
                        itemJson.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                        itemJson.put("CREATE_TIME_", new Date());
                        rdMaterialHandlingDao.insertItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("modified")) {
                        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                        jsonObject.put("UPDATE_TIME_", new Date());
                        rdMaterialHandlingDao.updateItem(itemJson);
                    } else if (itemJson.getString("_state").equalsIgnoreCase("removed")) {
                        itemIds.add(itemJson.getString("id"));
                    }
                }
            }
            JSONObject params = new JSONObject();
            params.put("ids", itemIds);
            rdMaterialHandlingDao.deleteItems(params);//统一处理删除
            params.clear();
            params.put("businessIds", itemIds);
            List<JSONObject> itemFiles = rdMaterialFileService.getFileListInfos(params);//现获取到明细所有的文件信息备用
            rdMaterialFileService.deleteFileInfos(params);//删明细文件信息
            //删除明细相关文件
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("rdMaterialUploadPosition",
                    "yanFaWuLiaoMingXi").getValue();
            for (JSONObject oneFile : itemFiles) {
                rdMaterialFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                        oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
            }
            //删除明细目录
            for (String oneBusinessId : itemIds) {
                rdMaterialFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
            }
        }
    }

    //..
    private void processCheckQuantity(JSONObject jsonObject) throws Exception{
        JSONObject params = new JSONObject();
        params.put("mainId", jsonObject.getString("id"));
        List<JSONObject> itemList = rdMaterialHandlingDao.getItemList(params);
        for (JSONObject item : itemList) {
            JSONObject inStorageItem = rdMaterialInStorageDao.getItemById(item.getString("inStorageItemId"));
            if (inStorageItem.getInteger("untreatedQuantity") < item.getInteger("handlingQuantity")) {
                throw new RuntimeException("入库单号为:“" + item.getString("businessNo") +
                        "”物料号为:“" + item.getString("materialCode") + "”的明细处理数量大于未处理数量!勾稽关系错误!");
            } else {
                inStorageItem.put("untreatedQuantity",
                        inStorageItem.getInteger("untreatedQuantity") - item.getInteger("handlingQuantity"));
                rdMaterialInStorageDao.updateItem(inStorageItem);
            }
        }
    }

    //..
    public List<JSONObject> getItemList(JSONObject params) throws Exception {
        return rdMaterialHandlingDao.getItemList(params);
    }
}
