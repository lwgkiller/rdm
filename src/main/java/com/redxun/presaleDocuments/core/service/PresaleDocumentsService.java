package com.redxun.presaleDocuments.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.FileUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsDao;
import com.redxun.presaleDocuments.core.dao.PresaleDocumentsFileDao;
import com.redxun.presaleDocuments.core.util.PresaleDocumentsConst;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PresaleDocumentsService {
    private static Logger logger = LoggerFactory.getLogger(PresaleDocumentsService.class);
    @Autowired
    private PresaleDocumentsDao presaleDocumentsDao;
    @Autowired
    private PresaleDocumentsFileService presaleDocumentsFileService;
    @Autowired
    private PresaleDocumentsFileDao presaleDocumentsFileDao;
    @Autowired
    private SysDicManager sysDicManager;

    //..版本号生成器
    private String getNextVersion(String currentVersion) {
        String nextVersion = "";
        if (currentVersion.length() == 2) {
            int number = Integer.parseInt(currentVersion.substring(0, 1));
            char ver = currentVersion.charAt(1);
            if (ver < 'Z') {
                ver++;
            } else {
                ver = 'A';
                number++;
            }
            nextVersion = String.valueOf(number) + String.valueOf(ver);
        } else {
            char ver = currentVersion.charAt(0);
            if (ver < 'Z') {
                ver++;
                nextVersion = String.valueOf(ver);
            } else {
                ver = 'A';
                nextVersion = String.valueOf(1) + String.valueOf(ver);
            }
        }
        return nextVersion;
    }

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

    //..
    public JsonPageResult<?> dataListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
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
    public List<JSONObject> dataListQuery(JSONObject params) {
        List<JSONObject> businessList = presaleDocumentsDao.dataListQuery(params);
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
        return businessList;
    }

    //..
    public JSONObject getDataById(String id) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(id)) {
            return result;
        }
        JSONObject jsonObject = presaleDocumentsDao.getDataById(id);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public JSONObject getDataByApplyId(String applyId) {
        JSONObject result = new JSONObject();
        if (StringUtil.isEmpty(applyId)) {
            return result;
        }
        JSONObject jsonObject = presaleDocumentsDao.getDataByApplyId(applyId);
        if (jsonObject == null) {
            return result;
        }
        return jsonObject;
    }

    //..
    public JsonResult saveBusiness(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        if (StringUtil.isEmpty(jsonObject.getString("id"))) {
            jsonObject.put("id", IdUtil.getId());
            jsonObject.put("businessStatus", PresaleDocumentsConst.PresaleDocumentStatus.BUSINESS_STATUS_BIANJIZHONG);
            if (!jsonObject.containsKey("REF_ID_") || StringUtil.isEmpty(jsonObject.getString("REF_ID_"))) {
                //基因id在新增复制时被清除，新增时没有基因id，此时都用id作为基因id
                jsonObject.put("REF_ID_", jsonObject.getString("id"));
            }
            jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("CREATE_TIME_", new Date());
            this.processProductSpectrum(jsonObject);
            presaleDocumentsDao.insertBusiness(jsonObject);
        } else {
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", new Date());
            this.processProductSpectrum(jsonObject);
            presaleDocumentsDao.updateBusiness(jsonObject);
        }
        result.setData(jsonObject.getString("id"));
        return result;
    }

    //..
    public JsonResult deleteBusiness(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("businessIds", idList);
        List<JSONObject> files = presaleDocumentsFileService.getFileListInfos(params);
        presaleDocumentsFileService.deleteFileInfos(params);
        params.clear();
        params.put("ids", idList);
        presaleDocumentsDao.deleteBusiness(params);
        //删除相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
        for (JSONObject oneFile : files) {
            presaleDocumentsFileService.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        for (String oneBusinessId : ids) {
            presaleDocumentsFileService.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        return result;
    }

    //..docVersionForce为强制赋值版本号，
    public JsonResult doReleaseBusiness(String[] ids, String docVersionForce) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.put("ids", idList);
        List<JSONObject> businessList = this.dataListQuery(params);
        for (JSONObject business : businessList) {
            //如果有 基因码+语言+编辑中 重复的不能发布
            params.clear();
            if (business.containsKey("REF_ID_") && StringUtil.isNotEmpty(business.getString("REF_ID_"))) {
                params.put("REF_ID_", business.getString("REF_ID_"));
            } else {
                params.put("REF_ID_", "REF_ID_");
            }
            params.put("docLanguage", business.getString("docLanguage"));
            params.put("businessStatus", PresaleDocumentsConst.PresaleDocumentStatus.BUSINESS_STATUS_BIANJIZHONG);
            if (presaleDocumentsDao.countDataListQuery(params) > 1) {
                result.setSuccess(false);
                result.setMessage("设计型号：‘" + business.getString("designModel") + "’" +
                        "物料号：‘" + business.getString("materialCode") + "’" +
                        "语言:‘" + business.getString("docLanguage") + "’,此手册已经存在重复的处于‘编辑中的记录’");
                return result;
            }
        }
        for (JSONObject business : businessList) {
            if (StringUtil.isNotEmpty(docVersionForce)) {
                //如果强制指定了待发布的版本号，则将待发布的数据强制赋值版本号
                business.put("docVersion", docVersionForce);
            }
            params.clear();
            if (business.containsKey("REF_ID_") && StringUtil.isNotEmpty(business.getString("REF_ID_"))) {
                params.put("REF_ID_", business.getString("REF_ID_"));
            } else {
                params.put("REF_ID_", "REF_ID_");
            }
            params.put("docLanguage", business.getString("docLanguage"));
            params.put("businessStatus", PresaleDocumentsConst.PresaleDocumentStatus.BUSINESS_STATUS_YIFABU);
            List<JSONObject> YIFABUHistorys = presaleDocumentsDao.dataListQuery(params);//其实这里只会有0或1个已发布
            JSONObject YIFABUHistory = new JSONObject();
            if (!YIFABUHistorys.isEmpty()) {
                //当前存在已发布的同族记录
                YIFABUHistory = YIFABUHistorys.get(0);
                YIFABUHistory.put("businessStatus", PresaleDocumentsConst.PresaleDocumentStatus.BUSINESS_STATUS_LISHIBANBEN);
                YIFABUHistory.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                YIFABUHistory.put("UPDATE_TIME_", new Date());
                presaleDocumentsDao.updateBusiness(YIFABUHistory);//将同族记录更新为历史版本
                //新纪录没有版本信息的话，则按照同族记录升版
                if (!business.containsKey("docVersion") || StringUtil.isEmpty(business.getString("docVersion"))) {
                    business.put("docVersion", this.getNextVersion(YIFABUHistory.getString("docVersion")));
                }
            } else {
                //当前不存在已发布的同族记录
                //新纪录没有版本信息的话，则按照初始版本A设定
                if (!business.containsKey("docVersion") || StringUtil.isEmpty(business.getString("docVersion"))) {
                    business.put("docVersion", "A");
                }
            }
            business.put("businessStatus", PresaleDocumentsConst.PresaleDocumentStatus.BUSINESS_STATUS_YIFABU);
            business.put("releaseTime", DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMD));
            business.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            business.put("UPDATE_TIME_", new Date());
            presaleDocumentsDao.updateBusiness(business);
        }
        return result;
    }

    //..
    public void exportBusiness(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        List<JSONObject> businessList = presaleDocumentsDao.dataListQuery(params);
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
                StringBuilder CREATE_BY_NAMEBuilder = new StringBuilder();
                StringBuilder createTimeBuilder = new StringBuilder();
                for (JSONObject fileListInfo : fileListInfos) {
                    fileIdsBuilder.append(fileListInfo.getString("id")).append(",");
                    fileNamesBuilder.append(fileListInfo.getString("fileName")).append(",");
                    CREATE_BY_NAMEBuilder.append(fileListInfo.getString("CREATE_BY_NAME")).append(",");
                    createTimeBuilder.append(fileListInfo.getString("createTime")).append(",");
                }
                fileIdsBuilder.delete(fileIdsBuilder.length() - 1, fileIdsBuilder.length());
                fileNamesBuilder.delete(fileNamesBuilder.length() - 1, fileNamesBuilder.length());
                CREATE_BY_NAMEBuilder.delete(CREATE_BY_NAMEBuilder.length() - 1, CREATE_BY_NAMEBuilder.length());
                createTimeBuilder.delete(createTimeBuilder.length() - 1, createTimeBuilder.length());
                jsonObject.put("fileIds", fileIdsBuilder.toString());
                jsonObject.put("fileNames", fileNamesBuilder.toString());
                jsonObject.put("CREATE_BY_NAMEs", CREATE_BY_NAMEBuilder.toString());
                jsonObject.put("createTimes", createTimeBuilder.toString());
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "售前文件数据导出";
        String excelName = nowDate + title;
        String[] fieldNames = {"负责人", "销售区域", "语种", "版本", "系统分类",
                "销售型号", "设计型号", "物料编码", "产品主管", "适用性说明",
                "发布时间", "文件名称", "文件类型", "上传人", "上传时间"};
        String[] fieldCodes = {"repUserName", "salesArea", "docLanguage", "docVersion", "systemType",
                "saleModel", "designModel", "materialCode", "productManagerName", "applicabilityStatement",
                "releaseTime", "fileNames", "businessType", "CREATE_BY_NAMEs", "createTimes"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public JSONObject doTransAndReleaseBusiness(JSONObject apply) throws Exception {
        JSONObject masterData = this.doTransAndReleaseBusinessWithOutSynFile(apply);
        //同步文件
        JsonResult jsonResult = this.doSynFile(apply, masterData);
        if (!jsonResult.getSuccess()) {
            throw new RuntimeException(jsonResult.getMessage());
        }
        return masterData;
    }

    //..
    public JSONObject doTransAndReleaseBusinessWithOutSynFile(JSONObject apply) throws Exception {
        this.processProductSpectrum(apply);//将json格式清洗一下，去掉uid_等等
        JSONObject masterData = new JSONObject();
        masterData.put("id", IdUtil.getId());
        if (apply.containsKey("REF_ID_") && StringUtil.isNotEmpty(apply.getString("REF_ID_"))) {
            //如果是通过一个文档发起变更，肯定有REF_ID_
            masterData.put("REF_ID_", apply.getString("REF_ID_"));
        } else {
            //否则就跟新增和新增复制的情况一样，用自己的id做REF_ID_
            masterData.put("REF_ID_", masterData.getString("id"));
        }
        masterData.put("applyId", apply.getString("id"));
        masterData.put("businessType", apply.getString("businessType"));
        masterData.put("productSpectrum", apply.getString("productSpectrum"));
        masterData.put("salesArea", apply.getString("salesArea"));
        masterData.put("docLanguage", apply.getString("docLanguage"));
        masterData.put("applicabilityStatement", apply.getString("applicabilityStatement"));
        masterData.put("repUserId", apply.getString("repUserId"));
        masterData.put("systemType", apply.getString("systemType"));
        masterData.put("remarks", apply.getString("remarks"));
        JsonResult jsonResult = null;
        if (apply.getString("businessType").
                equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUWENJIANFUJIAN)) {
            //如果技术资料附件，则要区分新增还是变更的情况
            if (apply.containsKey("REF_ID_") && StringUtil.isNotEmpty(apply.getString("REF_ID_"))) {
                //如果是通过一个文档发起变更，则取回原来的masterDataId，只用于最后的文件同步
                masterData.put("id", apply.getString("masterDataId"));
            } else {
                //否则就新增主数据并发布
                presaleDocumentsDao.insertBusiness(masterData);
                jsonResult = this.doReleaseBusiness(new String[]{masterData.getString("id")}, apply.getString("docVersion"));
                if (!jsonResult.getSuccess()) {
                    throw new RuntimeException(jsonResult.getMessage());
                }
            }
        } else {
            //其余的一律新增主数据并发布
            presaleDocumentsDao.insertBusiness(masterData);
            jsonResult = this.doReleaseBusiness(new String[]{masterData.getString("id")}, apply.getString("docVersion"));
            if (!jsonResult.getSuccess()) {
                throw new RuntimeException(jsonResult.getMessage());
            }
        }
        return masterData;
    }

    //..同步文件
    private JsonResult doSynFile(JSONObject apply, JSONObject masterData) {
        JsonResult result = new JsonResult();
        try {
            List<JSONObject> fileListInfosOri = new ArrayList<>();
            List<String> businessIdList = new ArrayList<>();
            businessIdList.add(apply.getString("id"));
            JSONObject params = new JSONObject();
            params.put("businessIds", businessIdList);
            fileListInfosOri = presaleDocumentsFileService.getFileListInfos(params);
            //技术规格书特殊处理一下，只取时间最新的一个文件
            if (apply.getString("businessType")
                    .equalsIgnoreCase(PresaleDocumentsConst.PresaleDocumentType.BUSINESS_TYPE_JISHUGUIGESHU)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //使用流和Lambda表达式进行降序排序并获取第一个元素
                Optional<JSONObject> firstElementOp = fileListInfosOri.stream()
                        .sorted(Comparator.comparing(object -> {
                            try {
                                return sdf.parse(((JSONObject) object).getString("CREATE_TIME_"));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }).reversed()).findFirst();
                fileListInfosOri.clear();
                JSONObject firstElement = firstElementOp.orElse(null);
                if (firstElement != null) {
                    fileListInfosOri.add(firstElement);
                }
            }
            //源文件基础目录
            String filePathBaseOri =
                    sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "申请流程文件").getValue();
            //目标文件基础目录
            String filePathBaseTarget =
                    sysDicManager.getBySysTreeKeyAndDicKey("PresaleDocumentUploadPosition", "主数据文件").getValue();
            for (JSONObject fileInfoOri : fileListInfosOri) {
                //写入目标文件数据库
                JSONObject fileInfoTarget = new JSONObject();
                fileInfoTarget.put("id", IdUtil.getId());
                fileInfoTarget.put("businessId", masterData.getString("id"));
                fileInfoTarget.put("businessType", fileInfoOri.getString("businessType"));
                fileInfoTarget.put("fileName", fileInfoOri.getString("fileName"));
                fileInfoTarget.put("fileSize", fileInfoOri.getString("fileSize"));
                fileInfoTarget.put("fileDesc", fileInfoOri.getString("fileDesc"));
                fileInfoTarget.put("CREATE_TIME_", new Date());
                fileInfoTarget.put("CREATE_BY_", apply.getString("repUserId"));
                presaleDocumentsFileDao.insertFileInfos(fileInfoTarget);
                //扩展名，共用
                String suffix = CommonFuns.toGetFileSuffix(fileInfoOri.getString("fileName"));
                //源文件全路径
                String fileFullPathOri = filePathBaseOri + File.separator + apply.getString("id")
                        + File.separator + fileInfoOri.getString("id") + "." + suffix;
                //向目标文件下载目录中写入文件
                String filePathTarget = filePathBaseTarget + File.separator + masterData.getString("id");
                File pathFileTarget = new File(filePathTarget);
                if (!pathFileTarget.exists()) {
                    pathFileTarget.mkdirs();
                }
                String fileFullPathTarget = filePathTarget
                        + File.separator + fileInfoTarget.getString("id") + "." + suffix;
                FileUtil.copyFile(fileFullPathOri, fileFullPathTarget);
            }
            result.setSuccess(true);
            return result;
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setMessage("文件同步失败!" + ex.getMessage());
            return result;
        }
    }
}
