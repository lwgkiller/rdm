package com.redxun.rdmZhgl.core.service;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.core.util.StringUtil;
import org.apache.commons.lang.StringUtils;
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
import com.redxun.rdmZhgl.core.dao.SaleFileApplyDao;
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
 * @author zz
 */
@Service
@Slf4j
public class SaleFileApplyService {
    private static Logger logger = LoggerFactory.getLogger(SaleFileApplyService.class);
    @Resource
    private BpmInstManager bpmInstManager;
    @Resource
    private SaleFileApplyDao saleFileApplyDao;
    @Resource
    private XcmgProjectManager xcmgProjectManager;
    @Resource
    private XcmgProjectOtherDao xcmgProjectOtherDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    public void add(Map<String, Object> params) {
        String preFix = "SP-";
        String id = preFix + XcmgProjectUtil.getNowLocalDateStr("yyyyMMddHHmmssSSS") + (int)(Math.random() * 100);
        params.put("id", id);
        if (params.get("CREATE_BY_") == null) {
            params.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        }
        params.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        params.put("TENANT_ID_", ContextUtil.getCurrentTenantId());
        saleFileApplyDao.add(params);

    }

    public void update(Map<String, Object> params) {
        params.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        params.put("UPDATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        saleFileApplyDao.update(params);
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
            List<JSONObject> files = saleFileApplyDao.getSaleFiles(param);
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("applyId"), saleFileUrl);
            }
            rdmZhglFileManager.deleteDirFromDisk(applyId, saleFileUrl);
            saleFileApplyDao.delete(applyId);
            // 删除实例
            bpmInstManager.deleteCascade(instIdArr[i], "");
            saleFileApplyDao.delSaleFileByApplyId(applyId);
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
            List<Map<String, Object>> applyList = saleFileApplyDao.queryList(params);

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
                            DateUtil.formatDate((Date)oneApply.get("CREATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                            DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                            DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
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
            // 自己是当前流程处理人
            if (oneApply.get("currentProcessUserId") != null
                && oneApply.get("currentProcessUserId").toString().contains(currentUserId)) {
                oneApply.put("processTask", true);
                result.add(oneApply);
            } else if (showAll) {
                // 分管领导和项目管理人员查看所有非草稿的数据或者草稿但是创建人CREATE_BY_是自己的
                if (oneApply.get("instStatus") != null && !"DRAFTED".equals(oneApply.get("instStatus").toString())) {
                    result.add(oneApply);
                } else {
                    if (oneApply.get("CREATE_BY_").toString().equals(currentUserId)) {
                        result.add(oneApply);
                    }
                }
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
                //@zwt放开权限，其他人也能对非草稿的且申请人部门是当前部门，或者草稿但是创建人CREATE_BY_是自己的数据变更
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
        return result;
    }

    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
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
            saleFileApplyDao.addSaleFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public List<JSONObject> getSaleFileListByApplyId(HttpServletRequest request) {
        String scene = RequestUtil.getString(request, "scene", "");
        String applyId = request.getParameter("applyId");
        List<JSONObject> saleFileList = new ArrayList<>();
        if ("apply".equalsIgnoreCase(scene) && StringUtils.isBlank(applyId)) {
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
        saleFileList = saleFileApplyDao.getSaleFiles(param);
        for (JSONObject onedata : saleFileList) {
            if (onedata.get("CREATE_TIME_") != null) {
                onedata.put("CREATE_TIME_", DateUtil.formatDate((Date)onedata.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        return saleFileList;
    }

    public void deleteOneSaleFile(String fileId, String fileName, String applyId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, applyId,
            SysPropertiesUtil.getGlobalProperty("saleFileUrl"));
        Map<String, Object> param = new HashMap<>();
        param.put("id", fileId);
        saleFileApplyDao.delSaleFile(param);
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
            list = saleFileApplyDao.getSaleFileList(params);
            params = new HashMap<>(16);
            params = CommonFuns.getSearchParam(params, request, false);
            params.put("applyType", applyType);
            totalList = saleFileApplyDao.getSaleFileList(params);
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
        List<JSONObject> applyList = saleFileApplyDao.queryRegionFromSpectrum(params);
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



}
