package com.redxun.serviceEngineering.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.rdmCommon.core.util.RdmConst;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.serviceEngineering.core.dao.JxbzzbshDao;
import com.redxun.serviceEngineering.core.dao.JxcshcDao;
import com.redxun.serviceEngineering.core.dao.StandardvalueShipmentnotmadeDao;
import com.redxun.sys.core.manager.SysDicManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JxbzzbshService {
    private static Logger logger = LoggerFactory.getLogger(JxbzzbshService.class);
    @Autowired
    private JxbzzbshDao jxbzzbshDao;

    @Autowired
    private SysDicManager sysDicManager;

    @Autowired
    private RdmZhglUtil rdmZhglUtil;

    @Autowired
    private JxcshcDao jxcshcDao;

    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;

    @Autowired
    private StandardvalueShipmentnotmadeDao standardvalueShipmentnotmadeDao;

    // ..
    public JsonPageResult<?> jxbzzbshListQuery(HttpServletRequest request, HttpServletResponse response,
        boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortOrder)) {
            params.put("sortField", sortField);
            params.put("sortOrder", sortOrder);
        }
        if (StringUtils.isEmpty(sortField)) {
            params.put("sortField", "CREATE_TIME_");
            params.put("sortOrder", "desc");
        }
        String filterParams = request.getParameter("filter");
        if (StringUtils.isNotBlank(filterParams)) {
            JSONArray jsonArray = JSONArray.parseArray(filterParams);
            for (int i = 0; i < jsonArray.size(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String value = jsonArray.getJSONObject(i).getString("value");
                if (StringUtils.isNotBlank(value)) {
                    // 数据库中存储的时间是UTC时间，因此需要将前台传递的北京时间转化
                    params.put(name, value);
                }
            }
        }
        // 增加分页条件
        if (doPage) {
            params.put("startIndex", 0);
            params.put("pageSize", 20);
            String pageIndex = request.getParameter("pageIndex");
            String pageSize = request.getParameter("pageSize");
            if (StringUtils.isNotBlank(pageIndex) && StringUtils.isNotBlank(pageSize)) {
                params.put("startIndex", Integer.parseInt(pageSize) * Integer.parseInt(pageIndex));
                params.put("pageSize", Integer.parseInt(pageSize));
            }
        }
        // 增加角色过滤的条件（需要自己办理的目前已包含在下面的条件中）
        addJxbzzbshRoleParam(params, ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> jxbzzbshList = jxbzzbshDao.jxbzzbshListQuery(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (JSONObject jxbzzbsh : jxbzzbshList) {
            if (jxbzzbsh.get("CREATE_TIME_") != null) {
                jxbzzbsh.put("CREATE_TIME_", sdf.format(jxbzzbsh.get("CREATE_TIME_")));
            }
        }
        // 向业务数据中写入任务相关的信息
        rdmZhglUtil.setTaskInfo2Data(jxbzzbshList, ContextUtil.getCurrentUserId());
        result.setData(jxbzzbshList);
        int count = jxbzzbshDao.countJxbzzbshQuery(params);
        result.setTotal(count);
        return result;
    }

    // 1、admin所有，相当于不加条件；
    // 2、分管领导、服务工程人员、其他人看所有提交的，自己的；
    private void addJxbzzbshRoleParam(Map<String, Object> params, String userId, String userNo) {
        params.put("currentUserId", userId);
        if (userNo.equalsIgnoreCase("admin")) {
            return;
        }
        boolean isAllDataQuery = rdmZhglUtil.judgeIsPointRoleUser(userId, RdmConst.AllDATA_QUERY_NAME);
        if (isAllDataQuery) {
            params.put("roleName", "fgld");
            return;
        }
        params.put("roleName", "ptyg");
    }

    // ..
    public JsonResult deleteJxbzzbsh(String[] ids) {
        JsonResult result = new JsonResult(true, "操作成功！");
        List<String> businessIds = Arrays.asList(ids);
        Map<String, Object> param = new HashMap<>();
        param.put("ids", businessIds);
        param.put("masterIds", businessIds);
        List<JSONObject> files = jxcshcDao.queryFileList(param);
        String jxbzzbFilePathBase = WebAppUtil.getProperty("jxbzzbFilePathBase");
        if (files.size() > 0) {
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("id"), oneFile.getString("fileName"),
                    oneFile.getString("masterId"), jxbzzbFilePathBase);
            }
            for (String id : ids) {
                rdmZhglFileManager.deleteDirFromDisk(id, jxbzzbFilePathBase);
            }
            jxcshcDao.delFileByMasterId(param);
        }
        jxbzzbshDao.deleteJxbzzbsh(param);
        return result;
    }

    public JSONObject getJxbzzbshDetail(String jxbzzbshId) {
        JSONObject detailObj = jxbzzbshDao.queryJxbzzbshById(jxbzzbshId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JSONObject getOldJxbzzbshDetail(String jxbzzbshId) {
        JSONObject detailObj = jxbzzbshDao.getOldJxbzzbshDetail(jxbzzbshId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public void createJxbzzbsh(JSONObject formData) {
        formData.put("id", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        setApplicationNumber(formData);
        jxbzzbshDao.insertJxbzzbsh(formData);
    }

    public void updateJxbzzbsh(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jxbzzbshDao.updateJxbzzbsh(formData);
    }

    public ResponseEntity<byte[]> downloadJxbzzbshTemplate(String productType, String versionType) {
        try {

            String fileName = "";
            String fileId = "";
            String url = "";
            if (productType.equals("lunWa")) {
                url = sysDicManager
                    .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "standardValueTemplate_lunwa")
                    .getValue();
                if (versionType.equals("cgb")) {
                    fileId = "lunwa_cgb.xlsx";
                    fileName =
                        sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lunwa_cgb").getValue();
                } else if (versionType.equals("csb")) {
                    fileId = "lunwa_csb.xlsx";
                    fileName =
                        sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lunwa_csb").getValue();
                } else {
                    fileId = "lunwa_wzb.xlsx";
                    fileName =
                        sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lunwa_wzb").getValue();
                }
            } else if (productType.equals("dianWa")) {
                url = sysDicManager
                        .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "standardValueTemplate_lunwa")
                        .getValue();
                if (versionType.equals("cgb")) {
                    fileId = "dianwa_cgb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "dianwa_cgb").getValue();
                } else if (versionType.equals("csb")) {
                    fileId = "dianwa_csb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "dianwa_csb").getValue();
                } else {
                    fileId = "dianwa_wzb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "dianwa_wzb").getValue();
                }
            }
            else if (productType.equals("teWa")) {
                url = sysDicManager
                        .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "standardValueTemplate_lunwa")
                        .getValue();
                if (versionType.equals("cgb")) {
                    fileId = "tewa_cgb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "tewa_cgb").getValue();
                } else if (versionType.equals("csb")) {
                    fileId = "tewa_csb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "tewa_csb").getValue();
                } else {
                    fileId = "tewa_wzb.xlsx";
                    fileName =
                            sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "tewa_wzb").getValue();
                }
            } else {
                url = sysDicManager
                    .getBySysTreeKeyAndDicKey("serviceEngineeringUploadPosition", "standardValueTemplate_lvwa")
                    .getValue();
                if (versionType.equals("cgb")) {
                    fileId = "lvwa_cgb.xlsx";
                    fileName = sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lvwa_cgb").getValue();
                } else if (versionType.equals("csb")) {
                    fileId = "lvwa_csb.xlsx";
                    fileName = sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lvwa_csb").getValue();
                } else {
                    fileId = "lvwa_wzb.xlsx";
                    fileName = sysDicManager.getBySysTreeKeyAndDicKey("standardValueTableNames", "lvwa_wzb").getValue();
                }
            }
            // 创建文件实例
            File file = new File(url + fileId);
            String finalDownloadFileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");

            // 设置httpHeaders,使浏览器响应下载
            HttpHeaders headers = new HttpHeaders();
            // 告诉浏览器执行下载的操作，“attachment”告诉了浏览器进行下载,下载的文件 文件名为 finalDownloadFileName
            headers.setContentDispositionFormData("attachment", finalDownloadFileName);
            // 设置响应方式为二进制，以二进制流传输
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Exception in importTemplateDownload", e);
            return null;
        }
    }

    public boolean checkShipmentnotmadeIdUnique(JSONObject jsonObject) {
        String id = jsonObject.getString("id");
        JSONObject jxbzzbsh = jxbzzbshDao.checkShipmentnotmadeIdUnique(jsonObject);
        if (jxbzzbsh != null && !jxbzzbsh.getString("id").equals(id)) {
            return false;
        }
        return true;
    }

    public JsonResult discardJxbzzbshInst(String shipmentnotmadeId) {
        JsonResult result = new JsonResult(true, "");
        JSONObject standardvalueShipmentnotmadeDetail = standardvalueShipmentnotmadeDao.queryById(shipmentnotmadeId);
        standardvalueShipmentnotmadeDetail.put("betaCompletion", "dzz");
        standardvalueShipmentnotmadeDao.updateData(standardvalueShipmentnotmadeDetail);
        return result;
    }

    public void setApplicationNumber(JSONObject formData) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        JSONObject param = new JSONObject();
        param.put("salesModel", formData.getString("salesModel"));
        param.put("year", year + "-");
        String maxApplicationNumber = jxbzzbshDao.queryMaxApplicationNumber(param);
        if (StringUtils.isBlank(maxApplicationNumber)) {
            formData.put("applicationNumber", year + "-1");
        } else {
            int num = Integer.parseInt(maxApplicationNumber.split("-")[1]);
            formData.put("applicationNumber", year + "-" + (num + 1));
        }
    }

    public List<JSONObject> queryFileList(String jxbzzbshId) {
        //查询本表单附件
        Map<String, Object> params = new HashMap<>();
        params.put("masterId", jxbzzbshId);
        List<JSONObject> fileList = jxbzzbshDao.queryFileList(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        //如果是完整版,再查询该物料号测试版附件
        JSONObject detailObj = jxbzzbshDao.queryJxbzzbshById(jxbzzbshId);
        if(detailObj!=null){
            if(StringUtils.isNotBlank(detailObj.getString("versionType"))&&"wzb".equalsIgnoreCase(detailObj.getString("versionType"))){
                params.put("materialCode", detailObj.getString("materialCode"));
                List<JSONObject> fileInfos = queryFileListByMaterialCode(params);
                fileList.addAll(fileInfos);
            }
        }
        return fileList;
    }

    public List<JSONObject> queryFileListByMaterialCode(Map<String, Object> params) {
        List<JSONObject> fileList = jxbzzbshDao.queryFileListByMaterialCode(params);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (fileList.size() > 0) {
            for (JSONObject file : fileList) {
                if (file.get("CREATE_TIME_") != null) {
                    file.put("CREATE_TIME_", sdf.format(file.get("CREATE_TIME_")));
                }
            }
        }
        return fileList;
    }

    public void exportJxbzzbsh(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = jxbzzbshListQuery(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        //流程状态处理
        for (Map<String, Object> one:listData){
            if (!one.containsKey("taskStatus")){
                one.put("taskStatus","");
            }else if(one.get("taskStatus").toString().equalsIgnoreCase("DRAFTED")){
                one.put("taskStatus","草稿");
            }else if(one.get("taskStatus").toString().equalsIgnoreCase("RUNNING")){
                one.put("taskStatus","运行中");
            }else if(one.get("taskStatus").toString().equalsIgnoreCase("SUCCESS_END")){
                one.put("taskStatus","成功结束");
            }else if(one.get("taskStatus").toString().equalsIgnoreCase("DISCARD_END")){
                one.put("taskStatus","作废");
            }

            if (!one.containsKey("versionType")){
                one.put("versionType","");
            }else if(one.get("versionType").toString().equalsIgnoreCase("csb")){
                one.put("versionType","测试版");
            }else if(one.get("versionType").toString().equalsIgnoreCase("cgb")){
                one.put("versionType","常规版");
            }else if(one.get("versionType").toString().equalsIgnoreCase("wzb")){
                one.put("versionType","完整版");
            }

            if (!one.containsKey("productType")){
                one.put("productType","");
            }else if(one.get("productType").toString().equalsIgnoreCase("lunWa")){
                one.put("productType","轮挖");
            }else if(one.get("productType").toString().equalsIgnoreCase("lvWa")){
                one.put("productType","履挖");
            }else if(one.get("productType").toString().equalsIgnoreCase("teWa")){
                one.put("productType","特挖");
            }else if(one.get("productType").toString().equalsIgnoreCase("dianWa")){
                one.put("productType","电挖");
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "检修标准值流程列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"物料编码","产品所","产品类型","销售型号","设计型号","版本类型","申请编号","版本号","手册编码","适用第四位PIN码",
                "备注","当前节点","当前处理人", "任务状态","创建者","创建时间"
        };
        String[] fieldCodes = {"materialCode","productDepartment","productType","salesModel","materialName","versionType","applicationNumber"
                ,"versionNum","gssNum","pinFour", "note","taskName","allTaskUserNames","taskStatus", "creator", "CREATE_TIME_"
        };
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }
}
