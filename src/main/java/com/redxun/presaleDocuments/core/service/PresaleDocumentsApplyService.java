package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.StringUtil;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsApplyDao;
import com.redxun.presaleDocuments.core.util.PresaleDocumentsConst;
import com.redxun.rdmCommon.core.controller.RdmApiController;
import com.redxun.rdmCommon.core.manager.CommonBpmMediator;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.manager.SysSeqIdManager;
import com.redxun.sys.org.entity.OsUser;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.sys.org.manager.OsUserManager;
import org.apache.commons.io.FileUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresaleDocumentsApplyService {
    private static Logger logger = LoggerFactory.getLogger(PresaleDocumentsApplyService.class);
    @Autowired
    private PresaleDocumentsApplyDao presaleDocumentsApplyDao;
    @Autowired
    private PresaleDocumentsFileService presaleDocumentsFileService;
    @Autowired
    private BpmInstManager bpmInstManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private OsUserManager osUserManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private SysSeqIdManager sysSeqIdManager;
    @Autowired
    private CommonBpmMediator commonBpmMediator;

    //..处理productSpectrum
    private void processProductSpectrum(JSONObject jsonObject) {
        if (jsonObject.containsKey("productSpectrum")
                && StringUtil.isNotEmpty(jsonObject.getString("productSpectrum"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("productSpectrum");
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    jsonArray.add(itemDataJson);
                }

            }
            jsonObject.put("productSpectrum", jsonArray.toString());
        } else if (!jsonObject.containsKey("productSpectrum")) {
            jsonObject.put("productSpectrum", "[]");
        }
    }

    //..处理buJianFuZeRen
    private void processBuJianFuZeRen(JSONObject jsonObject) {
        if (jsonObject.containsKey("buJianFuZeRen")
                && StringUtil.isNotEmpty(jsonObject.getString("buJianFuZeRen"))) {
            JSONArray jsonArray = new JSONArray();
            JSONArray jsonArrayItemData = jsonObject.getJSONArray("buJianFuZeRen");
            for (Object itemDataObject : jsonArrayItemData) {
                JSONObject itemDataJson = (JSONObject) itemDataObject;
                if (itemDataJson.containsKey("_state")) {//处理新增和修改
                    if (itemDataJson.getString("_state").equalsIgnoreCase("added")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        itemDataJson.put("id", IdUtil.getId());
                        jsonArray.add(itemDataJson);
                    } else if (itemDataJson.getString("_state").equalsIgnoreCase("modified")) {
                        itemDataJson.remove("_state");
                        itemDataJson.remove("_id");
                        itemDataJson.remove("_uid");
                        jsonArray.add(itemDataJson);
                    }
                } else {//处理没变化的
                    itemDataJson.remove("_state");
                    itemDataJson.remove("_id");
                    itemDataJson.remove("_uid");
                    jsonArray.add(itemDataJson);
                }

            }
            jsonObject.put("buJianFuZeRen", jsonArray.toString());
        } else if (!jsonObject.containsKey("buJianFuZeRen")) {
            List<SysDic> sysDics = sysDicManager.getByTreeKey("PresaleDocumentChenBenQingDanBuJian");
            JSONArray jsonArray = new JSONArray();
            for (SysDic sysDic : sysDics) {
                JSONObject buJianFuZeRen = new JSONObject().fluentPut("zhuanYe_item", sysDic.getKey());
                jsonArray.add(buJianFuZeRen);
            }
            jsonObject.put("buJianFuZeRen", jsonArray.toJSONString());
        }
    }

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        params.put("businessType", RequestUtil.getString(request, "businessType"));
        List<JSONObject> businessList = presaleDocumentsApplyDao.dataListQuery(params);
        // 查询当前处理人--会签不用这个
        //xcmgProjectManager.setTaskCurrentUser(businessList);
        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
        int businessListCount = presaleDocumentsApplyDao.countDataListQuery(params);
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
    public JsonResult deleteBusiness(String[] ids, String[] instIds) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("businessIds", idList);
        List<JSONObject> files = presaleDocumentsFileService.getFileListInfos(params);
        presaleDocumentsFileService.deleteFileInfos(params);
        params.clear();
        params.put("ids", idList);
        presaleDocumentsApplyDao.deleteBusiness(params);
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        //删除相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
        for (JSONObject oneFile : files) {
            presaleDocumentsFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            presaleDocumentsFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        return result;
    }

    //..
    public JSONObject getDataById(String id) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(id)) {
            return result;
        }
        JSONObject jsonObject = presaleDocumentsApplyDao.getDataById(id);
        if (jsonObject == null) {
            return result;
        }
        List<JSONObject> businessList = new ArrayList<>();
        businessList.add(jsonObject);
        rdmZhglUtil.setTaskInfo2Data(businessList, ContextUtil.getCurrentUserId());
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
            this.processProductSpectrum(formData);
            //成本清单要处理部件负责人字段
            if (formData.getString("businessType").equalsIgnoreCase(
                    PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN)) {
                this.processBuJianFuZeRen(formData);
            }
            presaleDocumentsApplyDao.insertBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public void updateBusiness(JSONObject formData) {
        try {
            formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            formData.put("UPDATE_TIME_", new Date());
            this.processProductSpectrum(formData);
            //成本清单要处理部件负责人字段
            if (formData.getString("businessType").equalsIgnoreCase(
                    PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN)) {
                this.processBuJianFuZeRen(formData);
            }
            presaleDocumentsApplyDao.updateBusiness(formData);
        } catch (Exception e) {
            throw e;
        }
    }

    //..
    public ResponseEntity<byte[]> templateDownload(String type) {
        try {
            String fileName = null;
            switch (type) {
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU:
                    fileName = "技术规格书 模板.rar";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINBIAOXUANPEIBIAO:
                    fileName = "销售型号_整机物料号_设计型号 产品标选配表_中英文模板.xlsx";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINLIANGDIANJIJINGZHENGLIFENXI:
                    fileName = "销售型号 产品亮点及竞争力分析 模板.pptx";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINJIBENJIEGOUGONGNENGYUYUANLIJIESHAO:
                    fileName = "销售型号 产品基本结构功能与原理介绍 系统分类 模板.pptx";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINDAOGOUSHOUCE:
                    fileName = "产品导购手册模板.xlsx";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_DUOGONGNENGJIJUXITONGYALILIULIANGFANWEIBIAOZHUNZHIBIAO:
                    fileName = "销售型号_整机物料号_设计型号 多功能机具系统压力流量范围标准值表 模板.xls";
                    break;
                case PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_CHANPINQUANSHENGMINGZHOUQICHENGBENQINGDAN:
                    fileName = "全生命周期成本清单 模板.rar";
                    break;
                default:
                    break;
            }
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/presaleDocuments/" + fileName).toURI());
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in templateDownload", e);
            return null;
        }
    }

    //..测试直接创建翻译
    public JsonResult testBusiness(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject dataById = this.getDataById(idList.get(0));
        OsUser osUser = osUserManager.get(dataById.getString("repUserId"));//取责任人
        commonBpmMediator.doAutoStartWorkFlow(dataById, "PresaleDocumentApply",
                "attachedDocTranslate", RdmApiController.START_PROCESS, osUser);//由责任人为发起人启动翻译流程
        return result;
    }
}
