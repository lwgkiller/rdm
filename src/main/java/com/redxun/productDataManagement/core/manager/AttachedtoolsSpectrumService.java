package com.redxun.productDataManagement.core.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.productDataManagement.core.dao.AttachedtoolsSpectrumDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.manager.CommonInfoManager;
import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.entity.SysDic;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.org.entity.OsGroup;
import com.redxun.sys.org.manager.OsGroupManager;
import com.redxun.world.core.dao.OverseaSalesCustomizationClientDao;
import com.redxun.world.core.dao.OverseaSalesCustomizationDao;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectAPIDao;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.redxun.rdmCommon.core.util.RdmCommonUtil.toGetParamVal;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

@Service
public class AttachedtoolsSpectrumService {
    private static Logger logger = LoggerFactory.getLogger(AttachedtoolsSpectrumService.class);

    static final class businessStatus {
        public static final String EDIT = "编辑中";
        public static final String REVIEW = "审核中";
        public static final String PUBLISHED = "已发布";
    }

    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private XcmgProjectAPIDao xcmgProjectAPIDao;
    @Autowired
    private AttachedtoolsSpectrumDao attachedtoolsSpectrumDao;
    @Autowired
    private SendDDNoticeManager sendDDNoticeManager;
    @Autowired
    private CommonInfoManager commonInfoManager;

    //..
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = attachedtoolsSpectrumDao.modelListQuery(params);
        int businessListCount = attachedtoolsSpectrumDao.countModelListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONArray modelGroupQuery(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jsonArray = new JSONArray();
        JSONObject title = new JSONObject().fluentPut("id", "title").fluentPut("text", "类型");
        jsonArray.add(title);
        List<SysDic> sysDicList = sysDicManager.getByTreeKeyTopOne("attachedtoolsType");
        for (SysDic sysDic : sysDicList) {
            JSONObject jsonObject = new JSONObject().fluentPut("id", sysDic.getKey()).
                    fluentPut("text", sysDic.getValue()).fluentPut("pid", "title");
            jsonArray.fluentAdd(jsonObject);
        }
        return jsonArray;
    }

    //..
    public JSONArray modelQueryByGroup(HttpServletRequest request, HttpServletResponse response) {
        String attachedtoolsType = RequestUtil.getString(request, "attachedtoolsType");
        JSONArray jsonArray = new JSONArray();
        JSONObject params = new JSONObject();
        params.put("attachedtoolsType", attachedtoolsType);
        params.put("sortField", "orderNo");
        params.put("sortOrder", "asc");
        List<JSONObject> modelList = attachedtoolsSpectrumDao.modelListQuery(params);
        for (JSONObject model : modelList) {
            JSONObject jsonObject = new JSONObject()
                    .fluentPut("id", model.getString("id"))
                    .fluentPut("attachedtoolsType2", model.getString("attachedtoolsType2"))
                    .fluentPut("fileName", model.getString("fileName"));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    //..
    public JSONObject getModelDataById(String id) throws Exception {
        JSONObject model = attachedtoolsSpectrumDao.getModelDataById(id);
        return model;
    }

    //..
    public JsonResult saveModel(HttpServletRequest request) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, String[]> parameters = multipartRequest.getParameterMap();
        MultipartFile fileObj = multipartRequest.getFile("businessFile");
        if (parameters == null || parameters.isEmpty()) {
            result.setMessage("操作失败，表单内容为空！");
            result.setSuccess(false);
            return result;
        }
        Map<String, Object> objBody = new HashMap<>();
        this.constructBusinessParam(parameters, objBody);
        this.addOrUpdateBusiness(objBody, fileObj);
        result.setData(objBody.get("id").toString());
        return result;
    }

    //..
    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) throws Exception {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
            objBody.put("id", parameters.get("id")[0]);
        }
        if (parameters.get("orderNo") != null && parameters.get("orderNo").length != 0
                && StringUtils.isNotBlank(parameters.get("orderNo")[0])) {
            objBody.put("orderNo", parameters.get("orderNo")[0]);
        }
        if (parameters.get("attachedtoolsType") != null && parameters.get("attachedtoolsType").length != 0
                && StringUtils.isNotBlank(parameters.get("attachedtoolsType")[0])) {
            objBody.put("attachedtoolsType", parameters.get("attachedtoolsType")[0]);
        }
        if (parameters.get("attachedtoolsType2") != null && parameters.get("attachedtoolsType2").length != 0
                && StringUtils.isNotBlank(parameters.get("attachedtoolsType2")[0])) {
            objBody.put("attachedtoolsType2", parameters.get("attachedtoolsType2")[0]);
        }
        if (parameters.get("functionIntroduction") != null && parameters.get("functionIntroduction").length != 0
                && StringUtils.isNotBlank(parameters.get("functionIntroduction")[0])) {
            objBody.put("functionIntroduction", parameters.get("functionIntroduction")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
                && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }
    }

    //..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws Exception {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (StringUtil.isEmpty(id)) {
            JSONObject param = new JSONObject();
            param.put("attachedtoolsType", objBody.get("attachedtoolsType").toString());
            param.put("attachedtoolsType2", objBody.get("attachedtoolsType2").toString());
            if (attachedtoolsSpectrumDao.countModelListQuery(param) > 0) {
                throw new RuntimeException("操作失败，已经存在此类别！，不能重复创建");
            }
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                this.updateFile2Disk(newId, fileObj);
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            attachedtoolsSpectrumDao.insertModel(new JSONObject(objBody));
        } else {
            if (fileObj != null) {
                this.deleteFileFromDisk(id);
                this.updateFile2Disk(id, fileObj);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            attachedtoolsSpectrumDao.updateModel(new JSONObject(objBody));
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition",
                "attachedtoolsSpectrumProductPic").getValue();
        // 处理下载目录的更新
        File pathFile = new File(filePathBase);
        // 目录不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String fileFullPath = filePathBase + File.separator + id + "." + fileObj.getOriginalFilename().split("\\.")[1];
        File file = new File(fileFullPath);
        // 文件存在则更新掉
        if (file.exists()) {
            file.delete();
        }
        FileCopyUtils.copy(fileObj.getBytes(), file);
    }

    //..
    public JsonResult deleteModel(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> idList = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        //1.删明细
        params.put("mainIds", idList);
        //attachedtoolsSpectrumDao.deleteItems(params);//用this的删除方法替代，包含图片删除
        this.deleteItems(attachedtoolsSpectrumDao.itemListQuery(params)
                .stream()
                .map(jsonObject -> jsonObject.getString("id"))
                .toArray(String[]::new));
        //2.删型号
        params.clear();
        params.put("ids", idList);
        attachedtoolsSpectrumDao.deleteModel(params);
        //3.删示例图片
        for (String id : ids) {
            this.deleteFileFromDisk(id);//删除产品示例图片
        }
        return result;
    }

    //..
    private void deleteFileFromDisk(String id) throws IOException {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition",
                "attachedtoolsSpectrumProductPic").getValue();
        File directory = new File(filePathBase);
        if (!directory.exists()) {
            return;
        }
        //获取目录中所有文件名
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        //遍历文件数组，删除与指定文件名相同的文件
        for (File file : files) {
            if (file.isFile() && file.getName().split("\\.")[0].equals(id)) {
                Files.delete(Paths.get(file.toURI()));
            }
        }
    }

    //..
    public JsonResult getParameterCfg(String mainId) throws Exception {
        JsonResult result = new JsonResult(true, "获取参数配置成功！");
        List<JSONObject> parameterCfg = new ArrayList<>();
        JSONObject model = attachedtoolsSpectrumDao.getModelDataById(mainId);
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("attachedtoolsType", model.getString("attachedtoolsType2"));
        if (sysDic == null) {
            result.setMessage("没有对应的参数字典配置！");
            result.setSuccess(false);
            return result;
        }
        List<SysDic> sysDics = sysDicManager.getByParentId(sysDic.getDicId());
        if (sysDics.isEmpty()) {
            result.setMessage("没有对应的参数字典配置！");
            result.setSuccess(false);
            return result;
        }
        for (SysDic dic : sysDics) {
            parameterCfg.add(new JSONObject().fluentPut("key", dic.getKey()).fluentPut("value", dic.getValue()));
        }
        result.setData(parameterCfg);
        return result;
    }

    //..
    public JsonPageResult<?> itemListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        params.put("mainId", RequestUtil.getString(request, "mainId"));
        List<JSONObject> businessList = attachedtoolsSpectrumDao.itemListQuery(params);
        int businessListCount = attachedtoolsSpectrumDao.countItemListQuery(params);
        //..处理parameters
        JSONObject model = attachedtoolsSpectrumDao.getModelDataById(RequestUtil.getString(request, "mainId"));
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("attachedtoolsType", model.getString("attachedtoolsType2"));
        if (sysDic == null) {
            result.setMessage("没有对应的参数字典配置！");
            result.setSuccess(false);
            return result;
        }
        List<SysDic> sysDics = sysDicManager.getByParentId(sysDic.getDicId());
        if (sysDics.isEmpty()) {
            result.setMessage("没有对应的参数字典配置！");
            result.setSuccess(false);
            return result;
        }
        for (JSONObject item : businessList) {
            JSONObject parameters = item.getJSONObject("parameters");
            for (SysDic dic : sysDics) {
                item.put(dic.getKey(), parameters.containsKey(dic.getKey()) ? parameters.getString(dic.getKey()) : "");
            }
        }
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonPageResult<?> itemListQueryWithOutParamProcess(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = attachedtoolsSpectrumDao.itemListQuery(params);
        int businessListCount = attachedtoolsSpectrumDao.countItemListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JsonResult saveItems(JSONArray jsonArray, String mainId) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        for (Object object : jsonArray) {
            JSONObject jsonObject = (JSONObject) object;
            if (StringUtils.isBlank(jsonObject.getString("id"))) {
                jsonObject.put("id", IdUtil.getId());
                jsonObject.put("businessStatus", businessStatus.EDIT);
                jsonObject.put("mainId", mainId);
                jsonObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                jsonObject.put("CREATE_TIME_", new Date());
                //处理参数
                this.processParameters(jsonObject);
                attachedtoolsSpectrumDao.insertItem(jsonObject);
            } else {
                jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                jsonObject.put("UPDATE_TIME_", new Date());
                //处理参数
                this.processParameters(jsonObject);
                attachedtoolsSpectrumDao.updateItem(jsonObject);
            }
        }
        return result;
    }

    //..
    public void createItemByApply(JSONObject applyJsonObject) throws Exception {
        JSONObject params = new JSONObject();
        params.put("attachedtoolsType", applyJsonObject.getString("attachedtoolsType"));
        params.put("attachedtoolsType2", applyJsonObject.getString("attachedtoolsType2"));
        List<JSONObject> modelListQuery = attachedtoolsSpectrumDao.modelListQuery(params);
        if (modelListQuery.isEmpty()) {
            throw new RuntimeException("无法创建型谱明细，没有建立其大类信息！");
        }
        JSONObject itemJsonObject = new JSONObject();
        itemJsonObject.put("id", IdUtil.getId());
        itemJsonObject.put("businessStatus", businessStatus.EDIT);
        itemJsonObject.put("mainId", modelListQuery.get(0).getString("id"));
        itemJsonObject.put("applyId", applyJsonObject.getString("id"));
        itemJsonObject.put("designModel", applyJsonObject.getString("designModel"));
        itemJsonObject.put("suitableTonnage", applyJsonObject.getString("suitableTonnage"));
        itemJsonObject.put("responsibleId", applyJsonObject.getString("responsibleId"));
        itemJsonObject.put("CREATE_BY_", applyJsonObject.getString("applyUserId"));
        itemJsonObject.put("CREATE_TIME_", new Date());
        //处理参数
        this.processParameters(itemJsonObject);
        attachedtoolsSpectrumDao.insertItem(itemJsonObject);
    }

    //..
    private void processParameters(JSONObject jsonObject) throws Exception {
        JSONObject model = attachedtoolsSpectrumDao.getModelDataById(jsonObject.getString("mainId"));
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("attachedtoolsType", model.getString("attachedtoolsType2"));
        if (sysDic == null) {
            throw new RuntimeException("没有对应的参数字典配置！");
        }
        List<SysDic> sysDics = sysDicManager.getByParentId(sysDic.getDicId());
        if (sysDics.isEmpty()) {
            throw new RuntimeException("没有对应的参数字典配置！");
        }
        JSONObject parameters = new JSONObject();
        for (SysDic dic : sysDics) {
            parameters.put(dic.getKey(), jsonObject.containsKey(dic.getKey()) ? jsonObject.getString(dic.getKey()) : "");
        }
        jsonObject.put("parameters", parameters.toJSONString());
    }

    //..
    public JsonResult doSubmitItem(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        jsonObject.put("businessStatus", businessStatus.REVIEW);
        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_TIME_", new Date());
        attachedtoolsSpectrumDao.updateItemStatus(jsonObject);
        List<Map<String, String>> list =
                commonInfoManager.queryUserByGroupNameAndRelType("属具型谱管理员", "GROUP-USER-BELONG");
        StringBuilder userIdBuild = new StringBuilder();
        for (Map<String, String> map : list) {
            userIdBuild.append(map.get("USER_ID_").toString()).append(",");
        }
        JSONObject noticeObj = new JSONObject().fluentPut("content",
                jsonObject.getString("responsibleName") + " 负责的‘" +
                        jsonObject.getString("attachedtoolsType") + "-" +
                        jsonObject.getString("attachedtoolsType2") +
                        "’类型下,设计型号为‘" + jsonObject.getString("designModel") +
                        "’的属具已经提交审核，请及时处理！");
        sendDDNoticeManager.sendNoticeForCommon(userIdBuild.toString(), noticeObj);
        return result;
    }

    //..
    public JsonResult doReviewItem(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        jsonObject.put("businessStatus", businessStatus.PUBLISHED);
        jsonObject.put("reviewerId", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_TIME_", new Date());
        attachedtoolsSpectrumDao.updateItemStatus(jsonObject);
        return result;
    }

    //..
    public JsonResult doBackItem(JSONObject jsonObject) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        jsonObject.put("businessStatus", businessStatus.EDIT);
        jsonObject.put("reviewerId", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        jsonObject.put("UPDATE_TIME_", new Date());
        attachedtoolsSpectrumDao.updateItemStatus(jsonObject);
        JSONObject noticeObj = new JSONObject().fluentPut("content", "您负责的‘" +
                jsonObject.getString("attachedtoolsType") + "-" +
                jsonObject.getString("attachedtoolsType2") +
                "’类型下,设计型号为‘" + jsonObject.getString("designModel") +
                "’的属具被驳回，请及时处理！");
        sendDDNoticeManager.sendNoticeForCommon(jsonObject.getString("responsibleId"), noticeObj);
        return result;
    }

    //..
    public JsonResult deleteItems(String[] ids) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        JSONObject params = new JSONObject();
        params.clear();
        params.put("ids", businessIds);
        attachedtoolsSpectrumDao.deleteItems(params);
        params.clear();
        params.put("businessIds", businessIds);
        List<JSONObject> itemFiles = this.getFileListInfos(params);//现获取到明细所有的文件信息备用
        attachedtoolsSpectrumDao.deleteFileInfos(params);//删明细文件信息
        //删除明细相关文件
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition",
                "attachedtoolsSpectrumItemFile").getValue();
        for (JSONObject oneFile : itemFiles) {
            this.deleteOneFileFromDisk(oneFile.getString("id"),
                    oneFile.getString("fileName"), oneFile.getString("businessId"), filePathBase);
        }
        //删除明细目录
        for (String oneBusinessId : businessIds) {
            this.deleteDirFromDisk(oneBusinessId, filePathBase);
        }
        return result;
    }

    //..
    public void exportItems(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, false);
        params.put("mainId", RequestUtil.getString(request, "mainId"));
        List<JSONObject> businessList = attachedtoolsSpectrumDao.itemListQuery(params);
        int businessListCount = attachedtoolsSpectrumDao.countItemListQuery(params);
        //..处理parameters
        JSONObject model = attachedtoolsSpectrumDao.getModelDataById(RequestUtil.getString(request, "mainId"));
        SysDic sysDic = sysDicManager.getBySysTreeKeyAndDicKey("attachedtoolsType", model.getString("attachedtoolsType2"));
        if (sysDic == null) {
            throw new RuntimeException("没有对应的参数字典配置！");
        }
        List<SysDic> sysDics = sysDicManager.getByParentId(sysDic.getDicId());
        if (sysDics.isEmpty()) {
            throw new RuntimeException("没有对应的参数字典配置！");
        }
        for (JSONObject item : businessList) {
            JSONObject parameters = item.getJSONObject("parameters");
            for (SysDic dic : sysDics) {
                item.put(dic.getKey(), parameters.containsKey(dic.getKey()) ? parameters.getString(dic.getKey()) : "");
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "属具数据导出";
        String excelName = nowDate + title;
        List<String> fieldNameList = new ArrayList<>();
        List<String> fieldCodeList = new ArrayList<>();
        fieldNameList.add("状态");
        fieldCodeList.add("businessStatus");
        fieldNameList.add("产品名称");
        fieldCodeList.add("attachedtoolsType2");
        fieldNameList.add("功能简介");
        fieldCodeList.add("functionIntroduction");
        fieldNameList.add("销售型号");
        fieldCodeList.add("salesModel");
        fieldNameList.add("设计型号");
        fieldCodeList.add("designModel");
        fieldNameList.add("物料编码");
        fieldCodeList.add("materialCode");
        fieldNameList.add("适配挖机吨位");
        fieldCodeList.add("suitableTonnage");
        fieldNameList.add("对接负责人");
        fieldCodeList.add("responsibleName");
        for (SysDic dic : sysDics) {
            fieldNameList.add(dic.getValue());
            fieldCodeList.add(dic.getKey());
        }
        fieldNameList.add("销售区域");
        fieldCodeList.add("salesArea");
        fieldNameList.add("自主研发/外购");
        fieldCodeList.add("indepRechOrExtProc");
        fieldNameList.add("是否单独成册");
        fieldCodeList.add("isSeparately");
        fieldNameList.add("建议对标品牌");
        fieldCodeList.add("benchmarkingBrands");
        fieldNameList.add("竞品资料情况");
        fieldCodeList.add("benchmarkingBrands");
        fieldNameList.add("备注");
        fieldCodeList.add("remarks");
        String[] fieldNames = fieldNameList.toArray(new String[0]);
        String[] fieldCodes = fieldCodeList.toArray(new String[0]);
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(businessList, fieldNames, fieldCodes, title);
        //输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..获取文件信息
    public List<JSONObject> getFileListInfos(JSONObject params) {
        List<JSONObject> fileList = new ArrayList<>();
        fileList = attachedtoolsSpectrumDao.getFileListInfos(params);
        return fileList;
    }

    //..文件上传命令
    public JsonResult saveFiles(HttpServletRequest request) throws Exception {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return new JsonResult(false, "没有找到上传的参数");
        }
        //多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return new JsonResult(false, "没有找到上传的文件");
        }
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition",
                toGetParamVal(parameters.get("businessType"))).getValue();
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("没有找到上传的路径");
            return new JsonResult(false, "没有找到上传的路径");
        }
        try {
            String businessId = toGetParamVal(parameters.get("businessId"));
            String businessType = toGetParamVal(parameters.get("businessType"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileDesc = toGetParamVal(parameters.get("fileDesc"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + businessId;
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
            fileInfo.put("businessId", businessId);
            fileInfo.put("businessType", businessType);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            attachedtoolsSpectrumDao.insertFileInfos(fileInfo);
            return new JsonResult(true, "上传成功");
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            throw e;
        }
    }

    //..文件的下载或者是pdf文件的预览
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId,
                                                       String fileBasePath) {
        try {
            if (StringUtil.isEmpty(fileName)) {
                logger.error("操作失败，文件名为空！");
                return null;
            }
            if (StringUtil.isEmpty(fileId)) {
                logger.error("操作失败，文件Id为空！");
                return null;
            }
            if (StringUtil.isEmpty(fileBasePath)) {
                logger.error("操作失败，找不到存储路径");
                return null;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fullFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                    + File.separator + fileId + "." + suffix;
            // 创建文件实例
            File file = new File(fullFilePath);
            // 修改文件名的编码格式
            String downloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 downloadFileName
            headers.setContentDispositionFormData("attachment", downloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in fileDownload", e);
            return null;
        }
    }

    //..
    public void officeFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                  HttpServletResponse response) {
        if (StringUtil.isEmpty(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtil.isEmpty(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtil.isEmpty(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
        String convertPdfPath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + convertPdfDir + File.separator + fileId + ".pdf";
        OfficeDocPreview.previewOfficeDoc(originalFilePath, convertPdfPath, response);
    }

    //..
    public void imageFilePreview(String fileName, String fileId, String formId, String fileBasePath,
                                 HttpServletResponse response) {
        if (StringUtil.isEmpty(fileName)) {
            logger.error("操作失败，文件名为空！");
            return;
        }
        if (StringUtil.isEmpty(fileId)) {
            logger.error("操作失败，文件Id为空！");
            return;
        }
        if (StringUtil.isEmpty(fileBasePath)) {
            logger.error("操作失败，找不到存储路径");
            return;
        }
        String suffix = CommonFuns.toGetFileSuffix(fileName);
        String originalFilePath = fileBasePath + (StringUtil.isEmpty(formId) ? "" : (File.separator + formId))
                + File.separator + fileId + "." + suffix;
        OfficeDocPreview.imagePreview(originalFilePath, response);
    }

    //..文件删除命令
    public void deleteFile(String fileId, String fileName, String businessId, String businessType) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("pdmUploadPosition", businessType).getValue();
        this.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        JSONObject params = new JSONObject();
        params.put("id", fileId);
        attachedtoolsSpectrumDao.deleteFileInfos(params);
    }

    //..从磁盘中删除文件（下载目录以及tmp中的pdf）
    public void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase) {
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("can't find filePathBase");
            return;
        }
        try {
            // 删除下载目录中文件
            String filePath = filePathBase;
            if (StringUtil.isNotEmpty(formId)) {
                filePath += File.separator + formId;
            }
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                return;
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            file.delete();

            // 删除预览目录中pdf文件
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String filePathPreviewPdf = filePath + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(filePathPreviewPdf);
            if (!pathFilePreviewPdf.exists()) {
                return;
            }
            String fileFullPathPreviewPdf = filePathPreviewPdf + File.separator + fileId + ".pdf";
            File previewFilePdf = new File(fileFullPathPreviewPdf);
            previewFilePdf.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteOneFileFromDisk", e);
        }
    }

    //..从磁盘中删除某一个表单文件夹（包括预览文件夹），前提是已经将目录下的文件都删除完了
    public void deleteDirFromDisk(String formId, String filePathBase) {
        if (StringUtil.isEmpty(filePathBase)) {
            logger.error("can't find FilePathBase");
            return;
        }
        try {
            // 删除目录下的tmp文件夹
            String convertPdfDir = WebAppUtil.getProperty("convertPdfDir");
            String previewPdfPath = filePathBase + File.separator + formId + File.separator + convertPdfDir;
            File pathFilePreviewPdf = new File(previewPdfPath);
            pathFilePreviewPdf.delete();

            // 删除目录
            String filePathDir = filePathBase + File.separator + formId;
            File pathFile = new File(filePathDir);
            pathFile.delete();
        } catch (Exception e) {
            logger.error("Exception in deleteDirFromDisk", e);
        }
    }

    //..
    public boolean checkDesignModelExist(JSONObject params) {
        String designModel = params.getString("designModel");
        if (StringUtil.isEmpty(designModel)) {
            return true;
        }
        boolean exist = false;
        List<JSONObject> res = attachedtoolsSpectrumDao.checkDesignModelExist(params);
        if (res.size() > 0) {
            exist = true;
        }
        return exist;
    }
}