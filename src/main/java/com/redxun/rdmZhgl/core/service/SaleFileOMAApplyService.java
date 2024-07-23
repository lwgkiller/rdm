package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAApplyDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.dao.XcmgProjectOtherDao;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ConstantUtil;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

import groovy.util.logging.Slf4j;

/**
 * @author zwt
 */
@Service
@Slf4j
public class SaleFileOMAApplyService {
    private static Logger logger = LoggerFactory.getLogger(SaleFileOMAApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private SaleFileOMAApplyDao saleFileOMAApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private SysDicManager sysDicManager;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int) (Math.random() * 100);
        params.put("id", id);
        //添加文件版本
        params.put("version", XcmgProjectUtil.getNowLocalDateStr("yyyyMMdd"));
        if (params.get("CREATE_BY_") == null) {
            params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        }
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());

        saleFileOMAApplyDao.add(params);

    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));

        saleFileOMAApplyDao.update(params);
    }

    /**
     * 删除表单及关联的所有信息
     */
    public JsonResult delete(String[] applyIdArr, String[] instIdArr) {
        if (applyIdArr.length != instIdArr.length) {
            return new JsonResult(false, "表单和流程个数不相同！");
        }
        String saleFileUrl = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        Map<String, Object> param = new HashMap<>();
        for (int i = 0; i < applyIdArr.length; i++) {
            String applyId = applyIdArr[i];
            param.put("applyId", applyId);
            List<JSONObject> files = saleFileOMAApplyDao.getSaleFiles(param);
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                        oneFile.getString("applyId"), saleFileUrl);
            }
            rdmZhglFileManager.deleteDirFromDisk(applyId, saleFileUrl);
            saleFileOMAApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            saleFileOMAApplyDao.delSaleFileByApplyId(applyId);
        }
        return new JsonResult(true, "成功删除!");
    }

    /**
     * 查询变更列表
     */
    public JsonPageResult<?> queryList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();
            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            if (params.get("applyType") == null) {
                String applyType = request.getParameter("applyType");
                params.put("applyType", applyType);
            }
            List<Map<String, Object>> applyList = saleFileOMAApplyDao.saleFilequeryList(params);

            // 增加不同角色和部门的人看到的数据不一样的过滤
            List<Map<String, Object>> finalAllApplyList = null;
            finalAllApplyList = filterApplyListByDepRole(applyList);
            // 根据分页进行subList截取
            int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
            int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
            int startSubListIndex = pageIndex * pageSize;
            int endSubListIndex = startSubListIndex + pageSize;
            List<Map<String, Object>> finalSubApplyList = new ArrayList<Map<String, Object>>();
            if (startSubListIndex < finalAllApplyList.size()) {
                finalSubApplyList = finalAllApplyList.subList(startSubListIndex,
                        endSubListIndex < finalAllApplyList.size() ? endSubListIndex : finalAllApplyList.size());
            }

            if (finalSubApplyList != null && !finalSubApplyList.isEmpty()) {
                for (Map<String, Object> oneApply : finalSubApplyList) {
                    if (oneApply.get("CREATE_TIME_") != null) {
                        oneApply.put("CREATE_TIME_",
                                DateUtil.formatDate((Date) oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                                DateUtil.formatDate((Date) oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                                DateUtil.formatDate((Date) oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            // 返回结果
            result.setData(finalSubApplyList);
            result.setTotal(finalAllApplyList.size());
        } catch (Exception e) {
            logger.error("Exception in queryList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    /**
     * 根据登录人部门、角色对列表进行过滤
     */
    public List<Map<String, Object>> filterApplyListByDepRole(List<Map<String, Object>> applyList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (applyList == null || applyList.isEmpty()) {
            return result;
        }
        // 刷新任务的当前执行人
        xcmgProjectManager.setTaskCurrentUser(applyList);
        // 管理员查看所有的包括草稿数据
        if (ConstantUtil.ADMIN.equals(ContextUtil.getCurrentUser().getUserNo())) {
            return applyList;
        }
        // 分管领导的查看权限等同于项目管理人员
        boolean showAll = false;
        Map<String, Object> params = new HashMap<>();
        params.put("userId", ContextUtil.getCurrentUser().getUserId());
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        List<Map<String, Object>> currentUserRoles = xcmgProjectOtherDao.queryUserRoles(params);
        for (Map<String, Object> oneRole : currentUserRoles) {
            if (oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-LEADER")
                    || oneRole.get("REL_TYPE_KEY_").toString().equalsIgnoreCase("GROUP-USER-BELONG")) {
                if (RdmConst.AllDATA_QUERY_NAME.equalsIgnoreCase(oneRole.get("NAME_").toString())
                        || RdmConst.SQWJ_AllDATA.equalsIgnoreCase(oneRole.get("NAME_").toString())) {
                    showAll = true;
                    break;
                }
            }
        }

        // 确定当前登录人是否是部门负责人
        JSONObject currentUserDepInfo = xcmgProjectManager.isCurrentUserDepRespman();
        if (!ConstantUtil.SUCCESS.equals(currentUserDepInfo.getString("result"))) {
            return result;
        }
        boolean isDepRespMan = currentUserDepInfo.getBoolean("isDepRespMan");
        String currentUserMainDepId = currentUserDepInfo.getString("currentUserMainDepId");
        String currentUserId = ContextUtil.getCurrentUserId();
        String currentUserMainDepName = currentUserDepInfo.getString("currentUserMainDepName");
        boolean isDepProjectManager =
                XcmgProjectUtil.judgeIsDepProjectManager(currentUserMainDepName, currentUserRoles);
        // 过滤
        for (Map<String, Object> oneApply : applyList) {
            if (oneApply.get("currentProcessUserId") != null
                    && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (isDepRespMan || isDepProjectManager) {
                // 部门负责人对于非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    if (oneApply.get("applyUserDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            } else {
                // 其他人对于创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    if (oneApply.get("applyUserDepId").toString().equals(currentUserMainDepId)) {
                        result.add(oneApply);
                    }
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
            }
        }
        // 售前文件查看所有数据人包括草稿数据
        if (showAll) {
            return applyList;
        }
        return result;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }
        String filePathBase = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find saleFileUrl");
            return;
        }
        try {
            String applyId = CommonFuns.toGetParamVal(parameters.get("applyId"));
            String id = IdUtil.getId();
            String fileName = CommonFuns.toGetParamVal(parameters.get("fileName"));
            String fileSize = CommonFuns.toGetParamVal(parameters.get("fileSize"));
            String fileDesc = CommonFuns.toGetParamVal(parameters.get("fileDesc"));
            String fileModel = CommonFuns.toGetParamVal(parameters.get("fileModel"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

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
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("applyId", applyId);
            fileInfo.put("fileModel", fileModel);
            fileInfo.put("fileDesc", fileDesc);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            saleFileOMAApplyDao.addSaleFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> getSaleFileListByApplyId(HttpServletRequest request) {
        String applyId = request.getParameter("applyId");
        List<JSONObject> saleFileList = new ArrayList<>();
        if (StringUtils.isBlank(applyId)) {
            return saleFileList;
        }
        Map<String, Object> param = new HashMap<>();
        String designModel = request.getParameter("designModel");
        String saleModel = request.getParameter("saleModel");
        String fileType = request.getParameter("fileType");
        String language = request.getParameter("language");
        String fileModel = request.getParameter("fileModel");
        param.put("designModel", designModel);
        param.put("saleModel", saleModel);
        param.put("fileType", fileType);
        param.put("language", language);
        param.put("fileModel", fileModel);
        param.put("applyId", applyId);
        saleFileList = saleFileOMAApplyDao.getSaleFiles(param);
        for (JSONObject onedata : saleFileList) {
            if (onedata.get("CREATE_TIME_") != null) {
                onedata.put("CREATE_TIME_", DateUtil.formatDate((Date) onedata.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return saleFileList;
    }

    public void deleteOneSaleFile(String fileId, String fileName, String applyId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, applyId,
                SysPropertiesUtil.getGlobalProperty("saleFileUrl"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        saleFileOMAApplyDao.delSaleFile(param);
    }

    public JsonPageResult<?> getSaleFileList(HttpServletRequest request) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            String applyType = request.getParameter("applyType");
            Map<String, Object> params = new HashMap<>(16);
            List<Map<String, Object>> list = new ArrayList<>();
            List<Map<String, Object>> totalList = new ArrayList<>();
            params = CommonFuns.getSearchParam(params, request, true);
            params.put("applyType", applyType);
            list = saleFileOMAApplyDao.getSaleFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("applyType", applyType);
            totalList = saleFileOMAApplyDao.getSaleFileList(params);
            CommonFuns.convertDate(list);
            // 返回结果
            result.setData(list);
            result.setTotal(totalList.size());
        } catch (Exception e) {
            logger.error("Exception in 查询异常", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    //@mh  从型谱获取指定型号的区域，如果型号是多个，需要拼接
    public String getRegionFromSpectrum(String designModel) {
        // 判断是否有逗号，有逗号需要分割
        String res = "";
        JSONObject params = new JSONObject();
        if (designModel.contains(",")) {
            // 这步去重也可以不用
            designModel = dropDuplicateListStr(designModel);
            String[] designModelStr = designModel.split(",");
            params.put("designModels", designModelStr);
        } else {
            params.put("designModel", designModel);
        }
        List<JSONObject> applyList = saleFileOMAApplyDao.queryRegionFromSpectrum(params);
        if (applyList.size() == 0) {
            return res;
        }
        if (applyList.size() > 1) {
            StringBuilder regionBuilder = new StringBuilder();
            for (JSONObject obj : applyList) {
                String regionStr = obj.getString("region");
                //一堆空数据
                if (StringUtil.isNotEmpty(regionStr)) {
                    regionBuilder.append(regionStr).append(",");
                }
            }
            if (regionBuilder.length() > 1) {
                regionBuilder.deleteCharAt(regionBuilder.length() - 1);

            }
            res = regionBuilder.toString();
            //这里res是必含逗号的，直接去重即可
            if (StringUtil.isNotEmpty(res)) {
                res = dropDuplicateListStr(res);
            }
        } else {
            // 之前sql返回了空，size=1 get是空
            String region = applyList.get(0).getString("region");
            if (StringUtil.isNotEmpty(region)) {
                return region;
            } else {
                return res;
            }
        }
        return res;
    }

    // 将有逗号的字符串分割成列表,返回去重后的拼接的字符串
    public String dropDuplicateListStr(String objStr) {
//        @mh \s是去空格
        List<String> objList = Arrays.asList(objStr.split("\\s*,\\s*"));
        HashSet<String> set = new HashSet<String>();
        for (String str : objList) {
            set.add(str);
        }
        return String.join(",", set);
    }

    //..
    public void exportList(HttpServletRequest request, HttpServletResponse response) {

        Map<String, Object> params = new HashMap<>();
        params = XcmgProjectUtil.getSearchParam(params, request);
        List<Map<String, Object>> applyList = saleFileOMAApplyDao.saleFilequeryList(params);
        Map<String, String> filetypeMap = sysDicManager.getSysDicKeyValueMapByTreeKey("sailFileOMA_WJFL");
        Map<String, String> systemtypeMap = sysDicManager.getSysDicKeyValueMapByTreeKey("sailFileOMA_XTFL");
        List<Map<String, Object>> listDataTrue = new ArrayList<>();
        for (Map<String, Object> jsonObject : applyList) {
            String f = jsonObject.get("fileType").toString();
            jsonObject.put("fileType", filetypeMap.get(f));
            jsonObject.put("systemType", systemtypeMap.get(jsonObject.get("systemType").toString()));
            JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("note").toString());
            if (jsonArray.size() == 0) {
                listDataTrue.add(jsonObject);
                continue;
            } else {
                for (Object o : jsonArray) {
                    JSONObject jsonObjectNote = (JSONObject) o;
                    if (jsonObjectNote.size() == 0) {
//                        listDataTrue.add(jsonObject);
                        continue;
                    }
                    if(jsonObjectNote.getString("materialCode_item") !=null){
                        jsonObjectNote.put("materialCode", jsonObjectNote.getString("materialCode_item"));
                    }
                    if(jsonObjectNote.getString("designModel_item") !=null) {
                        jsonObjectNote.put("designModel", jsonObjectNote.getString("designModel_item"));
                    }
                    if(jsonObjectNote.getString("salesModel_item") !=null) {
                        jsonObjectNote.put("saleModel", jsonObjectNote.getString("salesModel_item"));
                    }
                    if(jsonObjectNote.getString("cpzgId_item") !=null) {
                        jsonObjectNote.put("director", jsonObjectNote.getString("cpzgId_item"));
                    }
                    if(jsonObjectNote.getString("cpzgId_item_name") !=null) {
                        jsonObjectNote.put("directorName", jsonObjectNote.getString("cpzgId_item_name"));
                    }
                    if(jsonObject.get("id")!=null){
                        jsonObjectNote.put("id", jsonObject.get("id").toString());
                    }

                    if(jsonObject.get("fileType")!=null){
                        jsonObjectNote.put("fileType", jsonObject.get("fileType").toString());
                    }

                    if(jsonObject.get("systemType")!=null){
                        jsonObjectNote.put("systemType", jsonObject.get("systemType").toString());
                    }

                    if(jsonObject.get("applicabilityDoc")!=null){
                        jsonObjectNote.put("applicabilityDoc", jsonObject.get("applicabilityDoc").toString());
                    }

                    if(jsonObject.get("version")!=null){
                        jsonObjectNote.put("version", jsonObject.get("version").toString());
                    }

                    if(jsonObject.get("language")!=null){
                        jsonObjectNote.put("language", jsonObject.get("language").toString());
                    }

                    if(jsonObject.get("region")!=null){
                        jsonObjectNote.put("region", jsonObject.get("region").toString());
                    }


                    listDataTrue.add(jsonObjectNote);
                }
            }

            if (jsonObject.get("designModel") != null) {
                listDataTrue.add(jsonObject);
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "欧美澳＋类中国售前文件";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码", "设计型号", "销售型号", "产品主管", "文件类型", "系统类型", "适用性说明",
                "文件版本", "语言", "销售区域", "文件名称"};

        String[] fieldCodes = {"materialCode", "designModel", "saleModel", "directorName", "fileType", "systemType", "applicabilityDoc",
                "version", "language", "region", "fileName"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listDataTrue, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    //..
    public void saveBusiness(JSONObject result, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            Map<String, String[]> parameters = multipartRequest.getParameterMap();
            Map<String, Object> objBody = new HashMap<>();
            constructBusinessParam(parameters, objBody);
            addOrUpdateBusiness(objBody);
            result.put("message", "保存成功！");
            result.put("id", objBody.get("id"));
            result.put("success", true);
        } catch (Exception e) {
            logger.error("Exception in saveBusiness", e);
            result.put("message", "系统异常！");
            result.put("success", false);
        }
    }


    private void constructBusinessParam(Map<String, String[]> parameters, Map<String, Object> objBody) {
        if (parameters.get("id") != null && parameters.get("id").length != 0
                && StringUtils.isNotBlank(parameters.get("id")[0])) {
//            String id =parameters.get("id")[0] +parameters.get("language")[0];
            objBody.put("id",parameters.get("id")[0]);
        }
        if (parameters.get("editorUserId") != null && parameters.get("editorUserId").length != 0
                && StringUtils.isNotBlank(parameters.get("editorUserId")[0])) {
            objBody.put("editorUserId", parameters.get("editorUserId")[0]);
        }
        if (parameters.get("editorUserDeptId") != null && parameters.get("editorUserDeptId").length != 0
                && StringUtils.isNotBlank(parameters.get("editorUserDeptId")[0])) {
            objBody.put("editorUserDeptId", parameters.get("editorUserDeptId")[0]);
        }
        if (parameters.get("saleModel") != null && parameters.get("saleModel").length != 0
                && StringUtils.isNotBlank(parameters.get("saleModel")[0])) {
            objBody.put("saleModel", parameters.get("saleModel")[0]);
        }
        if (parameters.get("designModel") != null && parameters.get("designModel").length != 0
                && StringUtils.isNotBlank(parameters.get("designModel")[0])) {
            objBody.put("designModel", parameters.get("designModel")[0]);
        }
        if (parameters.get("materialCode") != null && parameters.get("materialCode").length != 0
                && StringUtils.isNotBlank(parameters.get("materialCode")[0])) {
            objBody.put("materialCode", parameters.get("materialCode")[0]);
        }
        if (parameters.get("fileType") != null && parameters.get("fileType").length != 0
                && StringUtils.isNotBlank(parameters.get("fileType")[0])) {
            objBody.put("fileType", parameters.get("fileType")[0]);
        }
        if (parameters.get("systemType") != null && parameters.get("systemType").length != 0
                && StringUtils.isNotBlank(parameters.get("systemType")[0])) {
            objBody.put("systemType", parameters.get("systemType")[0]);
        }
        if (parameters.get("director") != null && parameters.get("director").length != 0
                && StringUtils.isNotBlank(parameters.get("director")[0])) {
            objBody.put("director", parameters.get("director")[0]);
        }
        if (parameters.get("applicabilityDoc") != null && parameters.get("applicabilityDoc").length != 0
                && StringUtils.isNotBlank(parameters.get("applicabilityDoc")[0])) {
            objBody.put("applicabilityDoc", parameters.get("applicabilityDoc")[0]);
        }
        if (parameters.get("remark") != null && parameters.get("remark").length != 0
                && StringUtils.isNotBlank(parameters.get("remark")[0])) {
            objBody.put("remark", parameters.get("remark")[0]);
        }

        if (parameters.get("version") != null && parameters.get("version").length != 0
                && StringUtils.isNotBlank(parameters.get("version")[0])) {
            objBody.put("version", parameters.get("version")[0]);
        }
        if (parameters.get("language") != null && parameters.get("language").length != 0
                && StringUtils.isNotBlank(parameters.get("language")[0])) {
            objBody.put("language", parameters.get("language")[0]);
        }
        if (parameters.get("region") != null && parameters.get("region").length != 0
                && StringUtils.isNotBlank(parameters.get("region")[0])) {
            objBody.put("region", parameters.get("region")[0]);
        }
        if (parameters.get("yjUserIds") != null && parameters.get("yjUserIds").length != 0
                && StringUtils.isNotBlank(parameters.get("yjUserIds")[0])) {
            objBody.put("yjUserIds", parameters.get("yjUserIds")[0]);
        }
        if (parameters.get("yjUserNames") != null && parameters.get("yjUserNames").length != 0
                && StringUtils.isNotBlank(parameters.get("yjUserNames")[0])) {
            objBody.put("yjUserNames", parameters.get("yjUserNames")[0]);
        }
        if (parameters.get("fileStatus") != null && parameters.get("fileStatus").length != 0
                && StringUtils.isNotBlank(parameters.get("fileStatus")[0])) {
            objBody.put("fileStatus", parameters.get("fileStatus")[0]);
        }

    }

    private void addOrUpdateBusiness(Map<String, Object> objBody) throws IOException {
        JSONObject result = new JSONObject();
        result = saleFileOMAApplyDao.getJsonObject(objBody.get("id").toString());
        if (result == null || result.isEmpty()) {
            //新增文件
            String[] parts = objBody.get("id").toString().split("_");
            String preId ="";
            if (parts.length > 0) {
                preId = parts[0];
            } else {
                logger.error("not found"+objBody.get("id").toString());
            }
            JSONObject preIdJson = saleFileOMAApplyDao.getJsonObject(preId);
            objBody.put("note",preIdJson.get("note").toString());
            objBody.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            objBody.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
            saleFileOMAApplyDao.add(objBody);
        } else {
            objBody.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            objBody.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            saleFileOMAApplyDao.update(objBody);
        }
    }

    public void releaseBusiness(JSONObject result, String id) {
        try {
            //将本条状态变为已发布
            JSONObject jsonObject = saleFileOMAApplyDao.getJsonObject(id);
            jsonObject.put("fileStatus", "已发布");
            jsonObject.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            jsonObject.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
            saleFileOMAApplyDao.update(jsonObject);
            result.put("message", "发布成功！");
        } catch (Exception e) {
            logger.error("Exception in delete", e);
            result.put("message", "系统异常！");
        }
    }


}
