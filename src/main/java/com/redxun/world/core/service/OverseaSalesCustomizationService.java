package com.redxun.world.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.HttpClientUtil;
import com.redxun.core.util.OfficeDocPreview;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
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
import org.apache.commons.collections.map.HashedMap;
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

@Service
public class OverseaSalesCustomizationService {
    private static Logger logger = LoggerFactory.getLogger(OverseaSalesCustomizationService.class);

    //节点类型
    static final class overseaSalesCustomizationNodeType {
        public static final String ROOT = "root";
        public static final String BASIC = "basic";
        public static final String BASIC_XUJIEDIAN = "basicXuJieDian";
        public static final String BASIC_JIEDIAN = "basicJieDian";
        public static final String CUSTOM_XUJIEDIAN = "customXuJieDian";
        public static final String CUSTOM_XUJIEDIAN2 = "customXuJieDian2";
        public static final String CUSTOM_JIEDIAN = "customJieDian";
    }

    //海外销售标选配BOM模板-基本配置
    static final class overseaSalesCustomizationBomTemplate {
        public static final String BASIC_CONGIGURATIONG = "BASIC_CONGIGURATIONG";
    }

    //是否
    static final class yesOrNo {
        public static final String YES = "1";
        public static final String NO = "0";
    }

    @Autowired
    private SysDicManager sysDicManager;
    @Autowired
    private OsGroupManager osGroupManager;
    @Autowired
    private OverseaSalesCustomizationDao overseaSalesCustomizationDao;
    @Autowired
    private OverseaSalesCustomizationClientDao overseaSalesCustomizationClientDao;
    @Autowired
    private XcmgProjectAPIDao xcmgProjectAPIDao;

    //..
    public JsonPageResult<?> modelListQuery(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        JSONObject params = new JSONObject();
        CommonFuns.getSearchParam(params, request, true);
        List<JSONObject> businessList = overseaSalesCustomizationDao.modelListQuery(params);
        int businessListCount = overseaSalesCustomizationDao.countModelListQuery(params);
        result.setData(businessList);
        result.setTotal(businessListCount);
        return result;
    }

    //..
    public JSONObject getModelDataById(String id) throws Exception {
        JSONObject model = overseaSalesCustomizationDao.getModelDataById(id);
        if (model.containsKey("groupIds")) {
            String[] groupIds = model.getString("groupIds").split(",");
            StringBuilder stringBuilder = new StringBuilder();
            for (String groupId : groupIds) {
                OsGroup osGroup = osGroupManager.get(groupId);
                if (osGroup != null) {
                    stringBuilder.append(osGroup.getName()).append(",");
                }
            }
            if (stringBuilder.length() > 1) {
                model.put("groupNames", stringBuilder.substring(0, stringBuilder.length() - 1));
            }
        }
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
    public JsonResult copyModel(HttpServletRequest request) throws Exception {
        JsonResult result = new JsonResult(true, "操作成功！");
        String id = RequestUtil.getString(request, "id");
        JSONObject model = overseaSalesCustomizationDao.getModelDataById(id);
        JSONObject modelNew = new JSONObject();
        modelNew.put("id", IdUtil.getId());
        modelNew.put("orderNo", model.getString("orderNo"));
        modelNew.put("productGroup", model.getString("productGroup"));
        modelNew.put("salsesModel", model.getString("salsesModel") + "复制");
        modelNew.put("engine", model.getString("engine"));
        modelNew.put("ratedPower", model.getString("ratedPower"));
        modelNew.put("bucketCapacity", model.getString("bucketCapacity"));
        modelNew.put("operatingMass", model.getString("operatingMass"));
        modelNew.put("responsibleUserId", ContextUtil.getCurrentUserId());
        modelNew.put("responsibleUser", ContextUtil.getCurrentUser().getFullname());
        modelNew.put("productTypeSpectrumId", model.getString(""));
        modelNew.put("designModel", model.getString(""));
        modelNew.put("materialCode", model.getString(""));
        modelNew.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        modelNew.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        overseaSalesCustomizationDao.insertModel(modelNew);
        JSONObject params = new JSONObject();
        params.put("mainId", id);
        List<JSONObject> configTreeList = overseaSalesCustomizationDao.configTreeQuery(params);
        //以下处理节点复制
        JSONObject rootNode = new JSONObject();//根节点
        for (JSONObject node : configTreeList) {
            if (node.getString("nodeType").equalsIgnoreCase(overseaSalesCustomizationNodeType.ROOT)) {
                rootNode = node;
                break;
            }
        }
        Map<String, JSONObject> idToNode = configTreeList.stream()//所有节点
                .collect(Collectors.toMap(
                        jsonObject -> jsonObject.getString("id"),
                        jsonObject -> jsonObject
                ));
        Map<String, List<JSONObject>> parentIdToChildrens = new HashedMap();//所有一级枝干
        for (JSONObject node : configTreeList) {
            String parentId = node.getString("PARENT_ID_");
            if (StringUtil.isNotEmpty(parentId)) {//存在父节点
                if (!parentIdToChildrens.containsKey(parentId)) {//一级枝干还没有
                    List<JSONObject> childs = new ArrayList<>();
                    childs.add(node);
                    parentIdToChildrens.put(parentId, childs);
                } else {//一级枝干已存在
                    parentIdToChildrens.get(parentId).add(node);
                }
            }
        }
        List<JSONObject> configTreeListNew = new ArrayList<>();
        this.copyTreeByRoot(rootNode, null, modelNew.getString("id"),
                idToNode, parentIdToChildrens, configTreeListNew);//递归生成新的树
        for (JSONObject nodeNew : configTreeListNew) {
            overseaSalesCustomizationDao.insertConfigNode(nodeNew);
        }
        return result;
    }

    //..
    private void copyTreeByRoot(JSONObject rootNode,
                                JSONObject newParentNode,
                                String newMainId,
                                Map<String, JSONObject> allNodes,
                                Map<String, List<JSONObject>> parentIdToChildrens,
                                List<JSONObject> configTreeListNew) {
        JSONObject currentNode = rootNode;
        JSONObject newNode = (JSONObject) currentNode.clone();
        newNode.put("id", IdUtil.getId());
        newNode.put("mainId", newMainId);
        newNode.put("nodePath", newParentNode == null ? newNode.getString("id") :
                newParentNode.getString("nodePath") + "." + newNode.getString("id"));
        newNode.put("combinNodeIds", "");
        newNode.put("combinNodeNames", "");
        newNode.put("excluNodeIds", "");
        newNode.put("excluNodeNames", "");
        newNode.put("PARENT_ID_", newParentNode == null ? "" : newParentNode.getString("id"));
        configTreeListNew.add(newNode);
        if (parentIdToChildrens.containsKey(currentNode.getString("id"))) {
            List<JSONObject> currentChilds = parentIdToChildrens.get(currentNode.getString("id"));
            for (JSONObject currentChild : currentChilds) {
                copyTreeByRoot(currentChild, newNode, newMainId, allNodes, parentIdToChildrens, configTreeListNew);
            }
        } else {
            return;
        }
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
        if (parameters.get("productGroup") != null && parameters.get("productGroup").length != 0
                && StringUtils.isNotBlank(parameters.get("productGroup")[0])) {
            objBody.put("productGroup", parameters.get("productGroup")[0]);
        }
        if (parameters.get("salsesModel") != null && parameters.get("salsesModel").length != 0
                && StringUtils.isNotBlank(parameters.get("salsesModel")[0])) {
            objBody.put("salsesModel", parameters.get("salsesModel")[0]);
        }
        if (parameters.get("engine") != null && parameters.get("engine").length != 0
                && StringUtils.isNotBlank(parameters.get("engine")[0])) {
            objBody.put("engine", parameters.get("engine")[0]);
        }
        if (parameters.get("ratedPower") != null && parameters.get("ratedPower").length != 0
                && StringUtils.isNotBlank(parameters.get("ratedPower")[0])) {
            objBody.put("ratedPower", parameters.get("ratedPower")[0]);
        }
        if (parameters.get("bucketCapacity") != null && parameters.get("bucketCapacity").length != 0
                && StringUtils.isNotBlank(parameters.get("bucketCapacity")[0])) {
            objBody.put("bucketCapacity", parameters.get("bucketCapacity")[0]);
        }
        if (parameters.get("operatingMass") != null && parameters.get("operatingMass").length != 0
                && StringUtils.isNotBlank(parameters.get("operatingMass")[0])) {
            objBody.put("operatingMass", parameters.get("operatingMass")[0]);
        }
        if (parameters.get("responsibleUserId") != null && parameters.get("responsibleUserId").length != 0
                && StringUtils.isNotBlank(parameters.get("responsibleUserId")[0])) {
            objBody.put("responsibleUserId", parameters.get("responsibleUserId")[0]);
        }
        if (parameters.get("responsibleUser") != null && parameters.get("responsibleUser").length != 0
                && StringUtils.isNotBlank(parameters.get("responsibleUser")[0])) {
            objBody.put("responsibleUser", parameters.get("responsibleUser")[0]);
        }
        if (parameters.get("groupIds") != null && parameters.get("groupIds").length != 0
                && StringUtils.isNotBlank(parameters.get("groupIds")[0])) {
            objBody.put("groupIds", parameters.get("groupIds")[0]);
        }
        if (parameters.get("fileName") != null && parameters.get("fileName").length != 0
                && StringUtils.isNotBlank(parameters.get("fileName")[0])) {
            objBody.put("fileName", parameters.get("fileName")[0]);
        }
        if (parameters.get("productTypeSpectrumId") != null && parameters.get("productTypeSpectrumId").length != 0
                && StringUtils.isNotBlank(parameters.get("productTypeSpectrumId")[0])) {
            objBody.put("productTypeSpectrumId", parameters.get("productTypeSpectrumId")[0]);
        }
        if (parameters.get("designModel") != null && parameters.get("designModel").length != 0
                && StringUtils.isNotBlank(parameters.get("designModel")[0])) {
            objBody.put("designModel", parameters.get("designModel")[0]);
        }
        if (parameters.get("materialCode") != null && parameters.get("materialCode").length != 0
                && StringUtils.isNotBlank(parameters.get("materialCode")[0])) {
            objBody.put("materialCode", parameters.get("materialCode")[0]);
        }
        if (parameters.get("saleArea") != null && parameters.get("saleArea").length != 0
                && StringUtils.isNotBlank(parameters.get("saleArea")[0])) {
            objBody.put("saleArea", parameters.get("saleArea")[0]);
        }
        if (parameters.get("saleCountry") != null && parameters.get("saleCountry").length != 0
                && StringUtils.isNotBlank(parameters.get("saleCountry")[0])) {
            objBody.put("saleCountry", parameters.get("saleCountry")[0]);
        }
    }

    //..
    private void addOrUpdateBusiness(Map<String, Object> objBody, MultipartFile fileObj) throws Exception {
        String id = objBody.get("id") == null ? "" : objBody.get("id").toString();
        if (objBody.containsKey("responsibleUserId") &&
                StringUtil.isEmpty(objBody.get("responsibleUserId").toString())) {
            objBody.put("responsibleUserId", ContextUtil.getCurrentUserId());
            objBody.put("responsibleUser", ContextUtil.getCurrentUser().getFullname());
        }
        if (StringUtil.isEmpty(id)) {
            //新增文件
            String newId = IdUtil.getId();
            if (fileObj != null) {
                this.updateFile2Disk(newId, fileObj);
            }
            objBody.put("id", newId);
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            this.processConfigIni(new JSONObject(objBody));
            overseaSalesCustomizationDao.insertModel(new JSONObject(objBody));
        } else {
            if (fileObj != null) {
                this.deleteFileFromDisk(id);
                this.updateFile2Disk(id, fileObj);
            }
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            overseaSalesCustomizationDao.updateModel(new JSONObject(objBody));
        }
    }

    //..
    private void updateFile2Disk(String id, MultipartFile fileObj) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                "overseaSalesCustomizationProductPic").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            throw new RuntimeException("找不到文件路径");
        }
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
        //提前获取客户实例列表,因为关联型号
        params.put("modelIds", idList);
        List<JSONObject> clientInsts = overseaSalesCustomizationClientDao.clientInstQuery(params);
        List<String> instIds = new ArrayList<>();
        for (JSONObject clientInst : clientInsts) {
            instIds.add(clientInst.getString("id"));
        }
        //删客户配置实例
        params.clear();
        params.put("ids", instIds);
        overseaSalesCustomizationClientDao.deleteClientInst(params);
        params.clear();
        params.put("instIds", instIds);
        overseaSalesCustomizationClientDao.deleteClientConfigInst(params);
        //1.删型号
        params.clear();
        params.put("ids", idList);
        overseaSalesCustomizationDao.deleteModel(params);
        //2.删示例图片,节点文件
        for (String id : ids) {
            this.deleteFileFromDisk(id);//删除产品示例图片
            //提前获取节点文件信息列表
            params.clear();
            params.put("mainId", id);
            List<JSONObject> configNodeList = overseaSalesCustomizationDao.configTreeQuery(params);
            List<String> configIdList = new ArrayList<>();
            for (JSONObject confgiNode : configNodeList) {
                configIdList.add(confgiNode.getString("id"));
            }
            params.clear();
            params.put("businessIds", configIdList);
            List<JSONObject> fileListInfos = this.getFileListInfos(params);
            overseaSalesCustomizationDao.deleteileInfos(params);
            //节点文件基础目录
            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                    "overseaSalesCustomizationConfigNodeFile").getValue();
            //遍历节点文件相关的文件信息列表
            for (JSONObject fileInfo : fileListInfos) {
                //删除节点文件
                this.deleteOneFileFromDisk(fileInfo.getString("id"),
                        fileInfo.getString("fileName"),
                        fileInfo.getString("businessId"), filePathBase);

            }
            //遍历节点文件子数据id列表
            for (String configId : configIdList) {
                this.deleteDirFromDisk(configId, filePathBase);//删除节点文件子目录
            }
        }
        //3.删节点
        params.clear();
        params.put("mainIds", idList);
        overseaSalesCustomizationDao.deleteConfigNode(params);
        return result;
    }

    //..删除机型预览图片用
    private void deleteFileFromDisk(String id) throws IOException {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                "overseaSalesCustomizationProductPic").getValue();
        if (StringUtils.isBlank(filePathBase)) {
            throw new RuntimeException("找不到文件路径");
        }
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
    private void processConfigIni(JSONObject jsonObject) throws Exception {
        //增加某个机型的根节点
        String id = IdUtil.getId();
        JSONObject root = new JSONObject().fluentPut("id", id)
                .fluentPut("mainId", jsonObject.getString("id"))
                .fluentPut("nodeName", jsonObject.getString("salsesModel"))
                .fluentPut("isPriceCal", yesOrNo.NO)//不计价
                .fluentPut("isChecked", yesOrNo.NO)//不选中
                .fluentPut("nodeType", overseaSalesCustomizationNodeType.ROOT)
                .fluentPut("nodePath", id)
                .fluentPut("nodeDepth", 0);
        overseaSalesCustomizationDao.insertConfigNode(root);
        //拉取海外销售标选配BOM模板
        List<SysDic> treeKeyTopOne = sysDicManager.getByTreeKeyTopOne("overseaSalesCustomizationBomTemplate");
        //轮询模板，基本配置轮询第二层结构，建立BOM骨架节点
        int orderNoLevel1 = 0;
        int orderNoLevel2 = 0;
        for (SysDic topOneDic : treeKeyTopOne) {
            if (topOneDic.getKey().equalsIgnoreCase(overseaSalesCustomizationBomTemplate.BASIC_CONGIGURATIONG)) {//基本节点
                id = IdUtil.getId();
                JSONObject topOneNode = new JSONObject().fluentPut("id", id)
                        .fluentPut("mainId", jsonObject.getString("id"))
                        .fluentPut("nodeName", topOneDic.getValue())
                        .fluentPut("isPriceCal", yesOrNo.YES)//总体计价
                        .fluentPut("isChecked", yesOrNo.NO)//不选中
                        .fluentPut("nodeType", overseaSalesCustomizationNodeType.BASIC)
                        .fluentPut("nodePath", root.getString("nodePath") + "." + id)
                        .fluentPut("nodeDepth", 1)
                        .fluentPut("PARENT_ID_", root.getString("id"))
                        .fluentPut("orderNo", orderNoLevel1++);
                overseaSalesCustomizationDao.insertConfigNode(topOneNode);
                List<SysDic> treeKeySecondOne = sysDicManager.getByParentId(topOneDic.getDicId());
                for (SysDic secondOneDic : treeKeySecondOne) {
                    id = IdUtil.getId();
                    JSONObject secondOneNode = new JSONObject().fluentPut("id", id)
                            .fluentPut("mainId", jsonObject.getString("id"))
                            .fluentPut("nodeName", secondOneDic.getValue())
                            .fluentPut("isPriceCal", yesOrNo.NO)//不计价
                            .fluentPut("isChecked", yesOrNo.NO)//不选中
                            .fluentPut("nodeType", overseaSalesCustomizationNodeType.BASIC_XUJIEDIAN)
                            .fluentPut("nodePath", topOneNode.getString("nodePath") + "." + id)
                            .fluentPut("nodeDepth", 2)
                            .fluentPut("PARENT_ID_", topOneNode.getString("id"))
                            .fluentPut("orderNo", orderNoLevel2++);
                    overseaSalesCustomizationDao.insertConfigNode(secondOneNode);
                }
            } else {//选配虚节点
                id = IdUtil.getId();
                JSONObject topOneNode = new JSONObject().fluentPut("id", id)
                        .fluentPut("mainId", jsonObject.getString("id"))
                        .fluentPut("nodeName", topOneDic.getValue())
                        .fluentPut("isPriceCal", yesOrNo.NO)//不计价
                        .fluentPut("isChecked", yesOrNo.NO)//不选中
                        .fluentPut("nodeType", overseaSalesCustomizationNodeType.CUSTOM_XUJIEDIAN)
                        .fluentPut("nodePath", root.getString("nodePath") + "." + id)
                        .fluentPut("nodeDepth", 1)
                        .fluentPut("PARENT_ID_", root.getString("id"))
                        .fluentPut("orderNo", orderNoLevel1++);
                overseaSalesCustomizationDao.insertConfigNode(topOneNode);
            }
        }
    }

    //..
    public List<JSONObject> configTreeQuery(HttpServletRequest request, HttpServletResponse response) {
        String mainId = RequestUtil.getString(request, "mainId");
        JSONObject params = new JSONObject();
        params.put("mainId", mainId);
        List<JSONObject> businessList = overseaSalesCustomizationDao.configTreeQuery(params);
        return businessList;
    }

    //..
    public JsonResult saveConfigTree(JSONArray jsonArray) throws Exception {
        JsonResult result = new JsonResult(true, "数据保存成功！");
        JSONObject params = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject oneObject = jsonArray.getJSONObject(i);
            String state = oneObject.getString("_state");
            if ("added".equals(state)) {
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                overseaSalesCustomizationDao.insertConfigNode(oneObject);
            } else if ("modified".equals(state)) {
                oneObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("UPDATE_TIME_", DateFormatUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                overseaSalesCustomizationDao.updateConfigNode(oneObject);
            } else if ("removed".equals(state)) {
                //1.删节点
                params.put("id", oneObject.getString("id"));
                overseaSalesCustomizationDao.deleteConfigNode(params);
                //2.删节点文件信息
                List<String> configIdList = new ArrayList<>();
                configIdList.add(oneObject.getString("id"));
                params.clear();
                params.put("businessIds", configIdList);
                List<JSONObject> fileListInfos = this.getFileListInfos(params);
                overseaSalesCustomizationDao.deleteileInfos(params);
                //节点文件基础目录
                String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
                        "overseaSalesCustomizationConfigNodeFile").getValue();
                //遍历节点文件相关的文件信息列表
                for (JSONObject fileInfo : fileListInfos) {
                    //删除节点文件
                    this.deleteOneFileFromDisk(fileInfo.getString("id"),
                            fileInfo.getString("fileName"),
                            fileInfo.getString("businessId"), filePathBase);

                }
                //遍历节点文件子数据id列表
                for (String configId : configIdList) {
                    this.deleteDirFromDisk(configId, filePathBase);//删除节点文件子目录
                }
                //3.删节点客户配置实例
                params.clear();
                params.put("configIds", configIdList);
                overseaSalesCustomizationClientDao.deleteClientConfigInst(params);
            }
        }
        return result;
    }

    //..以下文件处理
    public List<JSONObject> getFileListInfos(JSONObject params) {
        List<JSONObject> fileList = new ArrayList<>();
        fileList = overseaSalesCustomizationDao.getFileListInfos(params);
        return fileList;
    }

    //..
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
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition",
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
            overseaSalesCustomizationDao.insertFileInfos(fileInfo);
            return new JsonResult(true, "上传成功");
        } catch (Exception e) {
            logger.error("Exception in saveFiles", e);
            throw e;
        }
    }

    //..
    public ResponseEntity<byte[]> pdfPreviewOrDownLoad(String fileName, String fileId, String formId, String fileBasePath) {
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
            logger.error("Exception in pdfPreviewOrDownLoad", e);
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

    //..
    public void deleteFile(String fileId, String fileName, String businessId, String businessType) throws Exception {
        String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey("worldUploadPosition", businessType).getValue();
        this.deleteOneFileFromDisk(fileId, fileName, businessId, filePathBase);
        JSONObject params = new JSONObject();
        params.put("id", fileId);
        overseaSalesCustomizationDao.deleteileInfos(params);
    }

    //..
    private void deleteOneFileFromDisk(String fileId, String fileName, String formId, String filePathBase) {
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

    //..
    private void deleteDirFromDisk(String formId, String filePathBase) {
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

    //..获取产品型谱
    public List<JSONObject> productTypeSpectrumListQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return xcmgProjectAPIDao.queryProductApiList(null);
    }
}
