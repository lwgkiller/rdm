package com.redxun.environment.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.environment.core.dao.GsClhbDao;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgProjectManager.core.manager.XcmgProjectManager;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
@Slf4j
public class GsClhbService {
    private Logger logger = LogManager.getLogger(GsClhbService.class);
    @Autowired
    private GsClhbDao gsClhbDao;
    @Autowired
    private XcmgProjectManager xcmgProjectManager;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryCx(HttpServletRequest request, HttpServletResponse response, boolean doPage) {
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
                params.put(name, value);

            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId",ContextUtil.getCurrentUserId());
        // 增加分页条件
        List<Map<String, Object>> cxList = gsClhbDao.queryCx(params);
        for (Map<String, Object> cx : cxList) {
            if (cx.get("CREATE_TIME_") != null) {
                cx.put("CREATE_TIME_", DateUtil.formatDate((Date) cx.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
        }
        xcmgProjectManager.setTaskCurrentUser(cxList);
        result.setData(cxList);
        int countQbgzDataList = gsClhbDao.countCxList(params);
        result.setTotal(countQbgzDataList);
        return result;
    }

    public void insertCx(JSONObject formData) {
        formData.put("cxId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        gsClhbDao.insertCx(formData);
    }

    public void updateCx(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        gsClhbDao.updateCx(formData);
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }
    public void saveCxUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("gsclhbFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find gsclhbFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("cxId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
            String fileType = toGetParamVal(parameters.get("fileType"));
            String upNode = toGetParamVal(parameters.get("upNode"));
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + belongId;
            File pathFile = new File(filePath);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = CommonFuns.toGetFileSuffix(fileName);
            String fileFullPath = filePath + File.separator + fileId + "." + suffix;
            File file = new File(fileFullPath);
            FileCopyUtils.copy(mf.getBytes(), file);

            // 写入数据库
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("fileId", fileId);
            fileInfo.put("fileName", fileName);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileType", fileType);
            fileInfo.put("upNode", upNode);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            gsClhbDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getCxDetail(String cxId) {
        JSONObject detailObj = gsClhbDao.queryCxById(cxId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public JSONObject getOldCxDetail(String cxId) {
        JSONObject detailObj = gsClhbDao.queryOldCxById(cxId);
        if (detailObj == null) {
            return new JSONObject();
        }
        return detailObj;
    }

    public List<JSONObject> getCxFileList(String cxId,String upNode) {
        List<JSONObject> cxFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("cxId", cxId);
        param.put("upNode", upNode);
        cxFileList = gsClhbDao.queryCxFileList(param);
        return cxFileList;
    }

    public void deleteOneCxFile(String fileId, String fileName, String cxId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, cxId, WebAppUtil.getProperty("gsclhbFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        gsClhbDao.deleteCxFile(param);
    }

    public JsonResult deleteCx(String[] cxIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String cxId : cxIdsArr) {
            // 删除基本信息
            String fileType=null;
            List<JSONObject> files = getCxFileList(cxId,fileType);
            String wjFilePathBase = WebAppUtil.getProperty("gsclhbFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                        oneFile.getString("cxId"), wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(cxId, wjFilePathBase);
            param.put("cxId", cxId);
            gsClhbDao.deleteCxFile(param);
            gsClhbDao.deleteCx(param);
            for (String oneInstId : instIds) {
                // 删除实例,不是同步删除，但是总量是能一对一的
                bpmInstManager.deleteCascade(oneInstId, "");
            }
        }
        return result;
    }

    public String toGetClhbNumber() {
        String num = null;
        String nowYearStart = DateFormatUtil.getNowByString("yyyyMM");
        Map<String, Object> param = new HashMap<>();
        param.put("applyTimeStart", nowYearStart);
        JSONObject maxClhb = gsClhbDao.queryMaxClhbNum(param);
        int existNumber = 0;
        if (maxClhb != null) {
            existNumber = Integer.parseInt(maxClhb.getString("num").substring(15));
        }
        int thisNumber = existNumber + 1;
        String newDate = DateFormatUtil.getNowByString("yyyyMM");
        if(thisNumber < 10) {
            num= "WJHB-" + newDate+ "-G4-" + "0" +thisNumber;
        }else if(thisNumber < 100&&thisNumber >= 10) {
            num= "WJHB-" + newDate + "-G4-" + thisNumber;
        }
        return num;
    }

    public ResponseEntity<byte[]> downloadPic() {
        try {
            String fileName = "商标示意图.jpg";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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

    public ResponseEntity<byte[]> importGsClhbDownload() {
        try {
            String fileName = "国四阶段环保信息标签打刻模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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
    public ResponseEntity<byte[]> importDjxxDownload() {
        try {
            String fileName = "单机信息上传内容模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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
    public ResponseEntity<byte[]> importCzDownload() {
        try {
            String fileName = "车载终端或精准定位系统防拆除技术措施说明模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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
    public ResponseEntity<byte[]> importWjDownload() {
        try {
            String fileName = "挖掘机各运行模式特点介绍模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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
    public ResponseEntity<byte[]> importPfDownload() {
        try {
            String fileName = "排放质保零部件清单模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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
    public ResponseEntity<byte[]> importSxDownload() {
        try {
            String fileName = "失效策略合规声明模板.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/environment/" + fileName).toURI());
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

    public JSONObject getApiList(JSONObject result, String searchValue) {

        // 以searchValue为查询条件 返回查询到的列表
        String resUrlBase = "http://wjrdm.xcmg.com/rdm/environment/core/Cx/edit.do?action=detail&cxId=";
        List<JSONObject> resList = gsClhbDao.queryApiList(searchValue);
        JSONArray resAry = result.getJSONArray("result");
        //
        if (resList.size() > 0) {
            if (resAry.isEmpty()) {
                for (JSONObject oneRes : resList) {
                    oneRes.put("url", resUrlBase + oneRes.getString("cxId"));
                }
                result.put("result", resList);

            } else {
                for (JSONObject oneRes : resList) {
                    oneRes.put("url", resUrlBase + oneRes.getString("cxId"));
                    resAry.add(oneRes);
                }
                result.put("result", resAry);
            }
        } else {
            result.put("success", false);
            result.put("message", "无查询结果，请检查搜索条件！");
        }
        return result;
    }

    public void exportGsClhbList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = queryCx(request, response, false);
        List<Map<String, Object>> listData = result.getData();
        //流程状态处理
        for (Map<String, Object> one:listData){
            if (!one.containsKey("status")){
                one.put("status","");
            }else if(one.get("status").toString().equalsIgnoreCase("DRAFTED")){
                one.put("status","草稿");
            }else if(one.get("status").toString().equalsIgnoreCase("RUNNING")){
                one.put("status","审批中");
            }else if(one.get("status").toString().equalsIgnoreCase("SUCCESS_END")){
                one.put("status","批准");
            }else if(one.get("status").toString().equalsIgnoreCase("DISCARD_END")){
                one.put("status","作废");
            }
        }
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "国四车辆环保信息列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"环保编号","内部编号","机械系族名称","整机设计型号","机械型号","整机物料号","环保信息状态","机械名称","质量名称",
                "质量(kg)","外形尺寸","机械的标识方法和位置", "机械环保代码在机体上的打刻位置","环保信息标签的位置",
                "最大设计总质量","最大设计车速（km/h）","额定工作参数及内容","是否有多种运行模式","车载终端数据是否上传VECC官网","诊断口是否下线访问检查","额定功率(kW)",
                "机械分类代码","商标","机械分类-大类","机械分类-小类","制造商名称","生产地址",
                "排放控制诊断系统接口位置","排放控制诊断系统接口替代位置","远程车载终端型号","远程车载终端生产厂","远程车载终端安装位置",
                "燃料规格","冷却风扇驱动方式","燃料类型","燃料箱数量","燃油箱各油箱容积（L）",
                "燃油箱总容积（L）","燃油箱材料","选择其他时填写相应内容","燃油箱安装位置`",
                "发动机总成物料号","发动机系族","发动机型号","发动机商标","发动机品牌","发动机环保信息公开编号","最大基准扭矩(N.m)","反应剂存储罐容积(L)","整机环保信息公开号",
                "流程状态","发布人","申请部门","发布时间"
        };
        String[] fieldCodes = {"num","nbbh","jxxzmc","zjsjxh","cpjxxh","zjwlh","noteStatus","jxmc","zlname",
                "zl","wxcc","jxsbff","hbdmwz", "hbbqwz",
                "zzl","zdcs","edgzcs", "dzyx","upvecc","xxfwjc","cyjedgl",
                "fldm","sb","jxdl","jxxl", "zzsmc","scdz",
                "pfkzwz","pfkztdwz","czzdxh","czzdscc","czzdwz",
                "rlgg","fsqd","rllx","rlxsl","rlxrj",
                "ryxzrj","ryxcl","ryxqt","ryxwz",
                "fdjzcwlh","cyjxs","fdjxh","fdjsb","fdjzzs","fdjgkbh","zdjznj","fyjccgrj","zjgkh",
                "status","applyName","deptName","CREATE_TIME_"

        };
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(listData, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public JsonResult statusChange(String cxId,String noteStatus) {
        JsonResult result = new JsonResult(true, "操作成功！");
        JSONObject idJson = new JSONObject();
        idJson.put("cxId", cxId);
        if("有效".equals(noteStatus)){
            idJson.put("noteStatus", "作废");
        }else if ("作废".equals(noteStatus)) {
            idJson.put("noteStatus", "有效");
        }else {
            idJson.put("noteStatus", "有效");
        }
        // 基本信息
        gsClhbDao.statusChange(idJson);
        return result;
    }

    public List<JSONObject> getEnvironmentByMaterial(String postData) {
        JSONObject result = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        try {
            JSONObject postDataJson = JSONObject.parseObject(postData);
            String materialCodes = postDataJson.getString("materialCodes");
            if(materialCodes.endsWith(";")){
                materialCodes = materialCodes.substring(0,materialCodes.length()-1);
            }
            if(StringUtils.isEmpty(materialCodes)){
                result.put("code", 500);
                result.put("messages", "整机物料号不能为空！");
                return list;
            }

            String[] idArr = materialCodes.split(";", -1);
            List<String> idList = Arrays.asList(idArr);
            JSONObject paramJson = new JSONObject();
            paramJson.put("ids", idList);
            list = gsClhbDao.getEnvironmentByMaterial(paramJson);
            result.put("code", 200);
            result.put("messages", "获取成功！");
            result.put("data",list);
        } catch (Exception e) {
            logger.error("查询环保信息公开报错" + e.getMessage(), e);
            result.put("code", 500);
            result.put("messages", "获取失败！");
            result.put("data",new ArrayList<>());
            throw e;
        }
        return list;
    }
}
