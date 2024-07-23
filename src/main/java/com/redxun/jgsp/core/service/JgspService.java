package com.redxun.jgsp.core.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.bpm.core.manager.BpmInstManager;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.util.DateUtil;
import com.redxun.jgsp.core.dao.JgspDao;
import com.redxun.materielextend.core.service.MaterielService;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import groovy.util.logging.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.io.File;
import java.util.*;


@Service
@Slf4j
public class JgspService {
    private Logger logger = LogManager.getLogger(JgspService.class);
    @Autowired
    private JgspDao jgspDao;
    @Autowired
    private RdmZhglFileManager rdmZhglFileManager;
    @Autowired
    private RdmZhglUtil rdmZhglUtil;
    @Autowired
    private BpmInstManager bpmInstManager;

    public JsonPageResult<?> queryJgsp(HttpServletRequest request, boolean doPage) {
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
                    params.put(name, value);
                }
            }
        }
        if (doPage) {
            rdmZhglUtil.addPage(request, params);
        }
        params.put("currentUserId", ContextUtil.getCurrentUserId());
        //addJgspRoleParam(params,ContextUtil.getCurrentUserId(), ContextUtil.getCurrentUser().getUserNo());
        List<JSONObject> jgspList = jgspDao.queryJgsp(params);
        for (JSONObject jgsp : jgspList) {
            if (jgsp.get("CREATE_TIME_") != null) {
                jgsp.put("CREATE_TIME_", DateUtil.formatDate((Date) jgsp.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (jgsp.getDate("ckMonth") != null) {
                jgsp.put("ckMonth", DateUtil.formatDate(jgsp.getDate("ckMonth"), "yyyy-MM-dd"));
            }

        }
        rdmZhglUtil.setTaskInfo2Data(jgspList, ContextUtil.getCurrentUserId());
        result.setData(jgspList);
        int countJgspDataList = jgspDao.countJgspList(params);
        result.setTotal(countJgspDataList);
        return result;
    }




    public void createJgsp(JSONObject formData) {
        formData.put("jgspId", IdUtil.getId());
        formData.put("CREATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("CREATE_TIME_", new Date());
        jgspDao.createJgsp(formData);
    }

    public void updateJgsp(JSONObject formData) {
        formData.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
        formData.put("UPDATE_TIME_", new Date());
        jgspDao.updateJgsp(formData);
    }

    public void saveJgspUploadFiles(HttpServletRequest request) {
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
        String filePathBase = WebAppUtil.getProperty("jgspFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find jgspFilePathBase");
            return;
        }
        try {
            String belongId = toGetParamVal(parameters.get("jgspId"));
            String fileId = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));
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
            fileInfo.put("belongId", belongId);
            fileInfo.put("fileSize", fileSize);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            jgspDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    public JSONObject getJgspDetail(String jgspId) {
        JSONObject detailObj = jgspDao.queryJgspById(jgspId);
        if (detailObj == null) {
            return new JSONObject();
        }
        if (detailObj.getDate("ckMonth") != null) {
            detailObj.put("ckMonth", DateUtil.formatDate(detailObj.getDate("ckMonth"), "yyyy-MM-dd"));
        }
        return detailObj;
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }


    public List<JSONObject> getJgspFileList(String belongId) {
        List<JSONObject> jgspFileList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("jgspId", belongId);
        jgspFileList = jgspDao.queryJgspFileList(param);
        return jgspFileList;
    }



    public void deleteOneJgspFile(String fileId, String fileName, String jgspId) {
        rdmZhglFileManager.deleteOneFileFromDisk(fileId, fileName, jgspId, WebAppUtil.getProperty("jgspFilePathBase"));
        Map<String, Object> param = new HashMap<>();
        param.put("fileId", fileId);
        jgspDao.deleteJgspFile(param);
    }

    public JsonResult deleteJgsp(String[] jgspIdsArr, String[] instIds) {
        JsonResult result = new JsonResult(true, "操作成功");
        Map<String, Object> param = new HashMap<>();
        for (String jgspId : jgspIdsArr) {
            // 删除基本信息
            List<JSONObject> files = getJgspFileList(jgspId);
            String wjFilePathBase = WebAppUtil.getProperty("jgspFilePathBase");
            for (JSONObject oneFile : files) {
                rdmZhglFileManager.deleteOneFileFromDisk(oneFile.getString("fileId"), oneFile.getString("fileName"),
                        oneFile.getString("belongId"), wjFilePathBase);
            }
            rdmZhglFileManager.deleteDirFromDisk(jgspId, wjFilePathBase);
            param.put("jgspId", jgspId);
            jgspDao.deleteJgspFile(param);
            jgspDao.deleteJgsp(param);
        }
        for (String oneInstId : instIds) {
            // 删除实例,不是同步删除，但是总量是能一对一的
            bpmInstManager.deleteCascade(oneInstId, "");
        }
        return result;

    }
    public ResponseEntity<byte[]> importDownload() {
        try {
            String fileName = "所别-科室-机型-供应商-物料-申请人.doc";
            // 创建文件实例
            File file = new File(
                    MaterielService.class.getClassLoader().getResource("templates/jgsp/" + fileName).toURI());
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

    
}
