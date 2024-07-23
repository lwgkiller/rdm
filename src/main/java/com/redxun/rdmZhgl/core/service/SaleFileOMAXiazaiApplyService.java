package com.redxun.rdmZhgl.core.service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAApplyDao;
import com.redxun.rdmZhgl.core.dao.SaleFileOMAXiazaiApplyDao;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.DecorationDownloadApplyDao;
import com.redxun.serviceEngineering.core.dao.ManualFileDownloadApplyDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.serviceEngineering.core.service.DecorationDownloadApplyService;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.sys.core.util.SysPropertiesUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Service
public class SaleFileOMAXiazaiApplyService {
    private static final Logger logger = LoggerFactory.getLogger(SaleFileOMAXiazaiApplyService.class);
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private SaleFileOMAXiazaiApplyDao saleFileOMAXiazaiApplyDao;
    @Autowired
    private SaleFileOMAApplyDao saleFileOMAApplyDao;
    @Autowired
    private SysDicManager sysDicManager;

    public JsonPageResult<?> queryApplyList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        rdmZhglUtil.addOrder(request, params, "salefileoma_xiazaiapply.CREATE_BY_", "desc");
        // 增加分页条件
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
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
        //增加角色过滤的条件
        addRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> applyList = saleFileOMAXiazaiApplyDao.queryApplyList(params);
        for (JSONObject oneApply : applyList) {
            if (oneApply.get("CREATE_TIME_") != null) {
                oneApply.put("CREATE_TIME_", DateUtil.formatDate((Date) oneApply.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        //查询当前处理人--非会签
        //xcmgProjectManager.setTaskCurrentUser(businessList);
        //查询当前处理人--会签
        rdmZhglUtil.setTaskInfo2Data(applyList, ContextUtil.getCurrentUserId());
        result.setData(applyList);
        int countApplyList = saleFileOMAXiazaiApplyDao.countApplyList(params);
        result.setTotal(countApplyList);
        return result;
    }

    private void addRoleParam(Map<String, Object> params, String userId, String userNo) {
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        params.put("currentUserId", userId);
        params.put("roleName", "other");
    }

    public JSONObject queryApplyDetail(String id) {
        JSONObject result = new JSONObject();
        if (StringUtils.isBlank(id)) {
            return result;
        }
        JSONObject params = new JSONObject();
        params.put("id", id);
        JSONObject obj = saleFileOMAXiazaiApplyDao.queryApplyDetail(params);
        if (obj == null) {
            return result;
        }
        return obj;
    }

    /**
     * 查询欧美澳文件列表
     */
    public JsonPageResult<?> querySaleFileOMAList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        try {
            Map<String, Object> params = new HashMap<>();

            // 传入条件(不包括分页)
            params = XcmgProjectUtil.getSearchParam(params, request);
            params.put("instStatus","SUCCESS_END");
            List<Map<String, Object>> applyList = saleFileOMAApplyDao.saleFilequeryList(params);
            Map<String,String>  filetypeMap  = sysDicManager.getSysDicKeyValueMapByTreeKey("sailFileOMA_WJFL");
            Map<String,String>  systemtypeMap = sysDicManager.getSysDicKeyValueMapByTreeKey("sailFileOMA_XTFL");
            List<Map<String,Object>> listTrue = new ArrayList<>();
            if(applyList !=null && !applyList.isEmpty()){
                for (Map<String, Object> oneApply : applyList) {
                    String f = oneApply.get("fileType").toString();
                    oneApply.put("fileType",filetypeMap.get(f));
                    oneApply.put("systemType",systemtypeMap.get(oneApply.get("systemType").toString()));
                    if (oneApply.get("applyTime") != null) {
                        oneApply.put("applyTime",
                                DateUtil.formatDate((Date)oneApply.get("applyTime"), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (oneApply.get("UPDATE_TIME_") != null) {
                        oneApply.put("UPDATE_TIME_",
                                DateUtil.formatDate((Date)oneApply.get("UPDATE_TIME_"), "yyyy-MM-dd HH:mm:ss"));
                    }

                    JSONArray jsonArray = JSONArray.parseArray(oneApply.get("note").toString());
                    if(jsonArray.size() ==0){
                        listTrue.add(oneApply);
                        continue;
                    }else{
                        for(Object o : jsonArray){
                            JSONObject jsonObjectNote = (JSONObject) o;
                            if(jsonObjectNote.size() == 0){
                                continue;
                            }
                            jsonObjectNote.put("materialCode", jsonObjectNote.getString("materialCode_item"));
                            jsonObjectNote.put("designModel", jsonObjectNote.getString("designModel_item"));
                            jsonObjectNote.put("saleModel", jsonObjectNote.getString("salesModel_item"));
                            jsonObjectNote.put("director", jsonObjectNote.getString("cpzgId_item"));
                            jsonObjectNote.put("directorName", jsonObjectNote.getString("cpzgId_item_name"));
                            jsonObjectNote.put("id", oneApply.get("id").toString());
                            jsonObjectNote.put("fileType", oneApply.get("fileType").toString());
                            jsonObjectNote.put("systemType", oneApply.get("systemType").toString());
                            jsonObjectNote.put("applicabilityDoc", oneApply.get("applicabilityDoc").toString());
                            jsonObjectNote.put("version", oneApply.get("version").toString());
                            jsonObjectNote.put("language", oneApply.get("language").toString());
                            jsonObjectNote.put("region", oneApply.get("region").toString());
                            listTrue.add(jsonObjectNote);
                        }
                    }

                    if(oneApply.get("designModel") !=null){
                        listTrue .add(oneApply);
                    }


                }
            }

            // 返回结果
            result.setData(listTrue);
            result.setTotal(listTrue.size());
        } catch (Exception e) {
            logger.error("Exception in querySaleFileOMAList", e);
            result.setSuccess(false);
            result.setMessage("系统异常");
        }
        return result;
    }

    public void saveInProcess(JsonResult result, String data) {
        JSONObject object = JSONObject.parseObject(data);
        if (object == null || object.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("表单内容为空，操作失败！");
            return;
        }
        if (StringUtils.isBlank(object.getString("id"))) {
            createApply(object);
            result.setData(object.getString("id"));
        } else {
            updateApply(object);
            result.setData(object.getString("id"));
        }
    }
    //..
    public void createApply(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("creatorDeptId", ContextUtil.getCurrentUser().getMainGroupId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        //基本信息
        saleFileOMAXiazaiApplyDao.insertApply(formData);
        //成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeSalefileOMAGrid"));

    }

    //..
    public void updateApply(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        // 基本信息
        saleFileOMAXiazaiApplyDao.updateApply(formData);
        // 成员信息
        demandProcess(formData.getString("id"), formData.getJSONArray("changeSalefileOMAGrid"));
    }
    private void demandProcess(String applyId, JSONArray demandArr) {
        if (demandArr == null || demandArr.isEmpty()) {
            return;
        }
        JSONObject param = new JSONObject();
        param.put("ids", new JSONArray());
        for (int index = 0; index < demandArr.size(); index++) {
            JSONObject oneObject = demandArr.getJSONObject(index);
            String id = oneObject.getString("id");
            String state = oneObject.getString("_state");
            if ("added".equals(state) || StringUtils.isBlank(id)) {
                // 新增
                oneObject.put("id", IdUtil.getId());
                oneObject.put("applyId", applyId);
                oneObject.put("CREATE_BY_", ContextUtil.getCurrentUserId());
                oneObject.put("CREATE_TIME_", new Date());
                saleFileOMAXiazaiApplyDao.insertDemand(oneObject);
            } else if ("removed".equals(state)) {
                // 删除
                param.getJSONArray("ids").add(oneObject.getString("id"));
            }
        }
        if (!param.getJSONArray("ids").isEmpty()) {
            saleFileOMAXiazaiApplyDao.deleteDemand(param);
        }
    }

    public List<JSONObject> queryDemandList(JSONObject params) {
        List<JSONObject> demandList = saleFileOMAXiazaiApplyDao.queryDemandList(params);
        return demandList;
    }

    //..
    public JsonResult delApplys(String[] applyIdArr) {
        List<String> applyIdList = Arrays.asList(applyIdArr);
        JsonResult result = new JsonResult(true, "操作成功！");
        if (applyIdArr.length == 0) {
            return result;
        }
        JSONObject param = new JSONObject();
        param.put("applyIds", applyIdList);
        //删除需求子表
        saleFileOMAXiazaiApplyDao.deleteDemand(param);
        param.put("ids", applyIdList);
        saleFileOMAXiazaiApplyDao.deleteApply(param);
        return result;
    }

    public ResponseEntity<byte[]> Download(HttpServletRequest request, String id, String description,String fileId) {
        try {
            if (StringUtils.isBlank(id)) {
                logger.error("id is blank");
                return null;
            }
//            String filePathBase = sysDicManager.getBySysTreeKeyAndDicKey(
//                    "serviceEngineeringUploadPosition", "decorationManual").getValue();
            String fileBasePath = SysPropertiesUtil.getGlobalProperty("saleFileUrl");
            if (StringUtils.isBlank(fileBasePath)) {
                logger.error("操作失败，找不到存储根路径");
                return null;
            }
            String fileSuffix = CommonFuns.toGetFileSuffix(description);
            String fileName = fileId +"."+ fileSuffix;
            String originalPdfFullFilePath = fileBasePath + File.separator +id + File.separator+fileName;
            File originalPdfFile = new File(originalPdfFullFilePath);
            if (!originalPdfFile.exists()) {
                logger.error("can't find originalPdfFile " + originalPdfFullFilePath);
                return null;
            }
            byte[] fileByteArr = new byte[0];
            fileByteArr = FileUtils.readFileToByteArray(originalPdfFile);

            // 下载文件的名字强制为“编号 标准名.pdf”修改文件名的编码格式
            String downloadFileName = description;
            String finalDownloadFileName = new String(downloadFileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(fileByteArr, headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in publicDownload", e);
            return null;
        }
    }

}
