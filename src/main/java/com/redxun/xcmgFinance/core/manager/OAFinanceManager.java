package com.redxun.xcmgFinance.core.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.rdmZhgl.core.service.RdmZhglFileManager;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.xcmgFinance.core.dao.OAFinanceDao;
import com.redxun.xcmgFinance.core.wsdl.AttachmentForm;
import com.redxun.xcmgFinance.core.wsdl.IXcmgReviewWebserviceService;
import com.redxun.xcmgFinance.core.wsdl.IXcmgReviewWebserviceServiceServiceLocator;
import com.redxun.xcmgFinance.core.wsdl.XcmgReviewUpdateParamterForm;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;

@Service
public class OAFinanceManager {
    private static Logger logger = LoggerFactory.getLogger(OAFinanceManager.class);

    @Autowired
    private OAFinanceDao oaFinanceDao;
    @Resource
    private RdmZhglFileManager rdmZhglFileManager;

    // 创建发运通知单及对应的发运明细
    public void createOAFinanceBasic(JSONObject result, String postBody) {
        logger.info("OA机器人节点推送 财务成本资料申请流程 基础内容，" + postBody);
        JSONObject postBodyObj = JSONObject.parseObject(postBody);
        if (postBodyObj == null || postBodyObj.isEmpty()) {
            result.put("success", false);
            result.put("message", "传输的消息内容为空！");
            return;
        }
        JSONObject resultObj = new JSONObject();

        /**
         * 基础表单
         */
        // OA流程ID
        String oaFlowId = postBodyObj.getString("oaFlowId");
        resultObj.put("oaFlowId", oaFlowId);
        // 主题
        String theme = postBodyObj.getString("theme1");
        resultObj.put("theme", theme);
        // 申请部门
        String deptName = postBodyObj.getString("deptName");
        resultObj.put("deptName", deptName);
        // 申请人
        String applyName = postBodyObj.getString("applyName");
        resultObj.put("applyName", applyName);
        // 申请类型
        String applyType = postBodyObj.getString("applyType");
        resultObj.put("applyType", applyType);
        // 申请内容及原因
        String applyText = postBodyObj.getString("applyText");
        resultObj.put("applyText", applyText);
        // 机器人节点审批人
        String userName = postBodyObj.getString("userName");
        resultObj.put("userName", userName);
        String userId = postBodyObj.getString("userId");
        resultObj.put("userId", userId);
        // 节点处理状态（手动加入）
        resultObj.put("submitType", "未提交");
        // 处理人与处理时间
        resultObj.put("CREATE_BY_", 1);
        resultObj.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
        resultObj.put("id", IdUtil.getId());

        // 根据oaFlowId查询是否之前已经存着数据，如果有，删除掉
        JSONObject jsonObject = oaFinanceDao.getOAFinanceDetailById(oaFlowId);
        if (jsonObject != null) {
            // 删除oa审批节点数据
            oaFinanceDao.deleteOAFinanceBasicById(oaFlowId);
            // 删除oa物料表单数据
            oaFinanceDao.deleteOAFinanceFormById(oaFlowId);
            // TODO 删除文件表和磁盘上的文件
            // 根据oaFlowId查询文件列表
            List<JSONObject> fileInfos = queryOAFileList(oaFlowId);
            if (fileInfos != null && !fileInfos.isEmpty()) {
                String oaFinanceFilePathBase = WebAppUtil.getProperty("oaFinanceFilePathBase");
                for (JSONObject oneFile : fileInfos) {
                    String id = oneFile.getString("id");
                    String fileName = oneFile.getString("fileName");
                    rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, oaFlowId, oaFinanceFilePathBase);
                    oaFinanceDao.deleteFileById(id);
                }
            }
        }

        oaFinanceDao.insertOAFinanceBasic(resultObj);
        String formText1 = postBodyObj.getString("formText1").toString();
        String formText2 = postBodyObj.getString("formText2").toString();

        if (!(formText1.equalsIgnoreCase("[]") && formText2.equalsIgnoreCase("[]"))) {
            JSONArray form1Arr = JSONArray.parseArray(formText1);
            JSONArray form2Arr = JSONArray.parseArray(formText2);
            for (int i = 0; i < form1Arr.size(); i++) {
                JSONObject param = new JSONObject();
                JSONObject form1Json = JSONObject.parseObject(form1Arr.get(i).toString());
                JSONObject form2Json = JSONObject.parseObject(form2Arr.get(i).toString());
                param.put("id", IdUtil.getId());
                param.put("oaFlowId", oaFlowId);
                param.put("typeName", form1Json.getString("formText11"));
                param.put("number", form2Json.getString("formText22"));
                param.put("CREATE_BY_", 1);
                param.put("CREATE_TIME_", XcmgProjectUtil.getNowUTCDateStr("yyyy-MM-dd HH:mm:ss"));
                oaFinanceDao.insertOAFinanceForm(param);
            }
        }

        /**
         * 获取列表内容
         */

        result.put("success", true);
        result.put("message", "操作成功！");
//        return;

    }

    // list查询
    public JsonPageResult<?> queryOAFinanceFlowList(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
        // params.put("sortField", "budget_projectInfo.yearMonth desc");
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

        // 查询
        List<JSONObject> dataListOr = oaFinanceDao.queryOAFinanceFlowList(params);
        List<JSONObject> dataList = new ArrayList<>();
        String userName = ContextUtil.getCurrentUser().getFullname();
        String loginName = ContextUtil.getCurrentUser().getUserNo();
        IUser user = ContextUtil.getCurrentUser();
        // 过滤与替换
        for (JSONObject one : dataListOr) {
            if (userName.equalsIgnoreCase("管理员") || loginName.equalsIgnoreCase("wlkc_cw")
                || loginName.equalsIgnoreCase(one.getString("userId"))) {
                dataList.add(one);
            }
        }

        // 封装
        List<JSONObject> finalSubProjectList = new ArrayList<>();

        // 根据分页进行subList截取
        int pageIndex = RequestUtil.getInt(request, "pageIndex", 0);
        int pageSize = RequestUtil.getInt(request, "pageSize", Page.DEFAULT_PAGE_SIZE);
        int startSubListIndex = pageIndex * pageSize;
        int endSubListIndex = startSubListIndex + pageSize;
        if (startSubListIndex < dataList.size()) {
            finalSubProjectList = dataList.subList(startSubListIndex,
                endSubListIndex < dataList.size() ? endSubListIndex : dataList.size());
        }
        if (finalSubProjectList != null && !finalSubProjectList.isEmpty()) {
            for (Map<String, Object> oneProject : finalSubProjectList) {
                if (oneProject.get("CREATE_TIME_") != null) {
                    oneProject.put("CREATE_TIME_",
                        DateUtil.formatDate((Date)oneProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
                }
            }
        }
        result.setData(finalSubProjectList);
        result.setTotal(dataList.size());
        return result;

    }

    public JSONObject getDetailJson(String oaFlowId) {
        JSONObject xcmgProject = oaFinanceDao.getOAFinanceDetailById(oaFlowId);
        if (xcmgProject != null && !xcmgProject.isEmpty()) {
            if (xcmgProject.get("CREATE_TIME_") != null) {
                xcmgProject.put("CREATE_TIME_",
                    DateUtil.formatDate((Date)xcmgProject.get("CREATE_TIME_"), "yyyy-MM-dd"));
            }
            if (xcmgProject.getString("applyType").equalsIgnoreCase("0")) {
                xcmgProject.put("applyType", "常规");
            }
            if (xcmgProject.getString("applyType").equalsIgnoreCase("1")) {
                xcmgProject.put("applyType", "代理商特殊需求");
            }
        }
        List<Map<String, Object>> OAList = oaFinanceDao.queryOAFinanceDetailList(oaFlowId);
        for (Map<String, Object> one : OAList) {
            one.put("oaFlowId", oaFlowId);
        }
        xcmgProject.put("OAList", OAList);
        return xcmgProject;
    }

    public void saveForm(JsonResult result, String businessDataStr, String oaFlowId, String filePath)
        throws IOException, ServiceException {
        //同步至OA
        saveFormToOA(result, businessDataStr, oaFlowId, filePath);
        //保存
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }
        // 循环更新新内容
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject)object;
            oaFinanceDao.updateOAFinanceDetailList(businessObj);
        }
    }

    public void saveFormToOA(JsonResult result, String businessDataStr, String oaFlowId, String filePath)
        throws IOException, ServiceException {
        JSONArray businessObjs = JSONObject.parseArray(businessDataStr);
        if (businessObjs == null || businessObjs.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("内容为空，操作失败！");
            return;
        }

        String materials = "";
        String descriptions = "";
        String targetCosts = "";
        // 循环更新新内容
        for (Object object : businessObjs) {
            JSONObject businessObj = (JSONObject)object;
            oaFinanceDao.updateOAFinanceDetailList(businessObj);
            materials = materials + "\"" + businessObj.getString("material") + "\"" + ",";
            descriptions = descriptions + "\"" + businessObj.getString("description") + "\"" + ",";
            targetCosts = targetCosts + "\"" + businessObj.getString("targetCost") + "\"" + ",";
        }
        // 物料号，物料描述，目标成本
        toOa(oaFlowId, materials, descriptions, targetCosts);
    }

    // TODO: 2023/1/14 重写改接口，改变传入的值
    public void toOa(String oaFlowId, String materials, String descriptions, String targetCosts)
        throws ServiceException, IOException {
        IXcmgReviewWebserviceServiceServiceLocator locator = new IXcmgReviewWebserviceServiceServiceLocator();
        IXcmgReviewWebserviceService ss = locator.getIXcmgReviewWebserviceServicePort();
        // 构建参数
        XcmgReviewUpdateParamterForm form = new XcmgReviewUpdateParamterForm();
        form.setFdId(oaFlowId);
        String paramJson = "{\n" + "            \t\"fd_394c1a02267d06.fd_3957d9d46a2a4c\":[" + materials + "],\n"
            + "            \t\"fd_394c1a02267d06.fd_3957d9d51164c2\":[" + descriptions + "],\n"
            + "            \t\"fd_394c1a02267d06.fd_3991d9a11224a6\":[" + targetCosts + "]}";
        form.setFormValues(paramJson);
        ss.updateFormData(form);
    }

    public void nextPoint(JsonResult result, String oaFlowId) throws ServiceException, RemoteException {
        if (oaFlowId == null || oaFlowId.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("操作失败,请联系系统管理员！");
            return;
        }
        IXcmgReviewWebserviceServiceServiceLocator locator = new IXcmgReviewWebserviceServiceServiceLocator();
        IXcmgReviewWebserviceService ss = locator.getIXcmgReviewWebserviceServicePort();
        ss.wakeupReview(oaFlowId);

        JSONObject param = new JSONObject();
        param.put("oaFlowId", oaFlowId);
        param.put("submitType", "已提交");
        oaFinanceDao.updateOAFinanceStatus(param);
    }

    public List<JSONObject> queryOAFileList(String oaFlowId) {
        // 查询oaFlowId对应的所有文件
        List<JSONObject> fileInfos = oaFinanceDao.queryOAFileList(oaFlowId);
        return fileInfos;
    }

    public JSONObject saveUploadFiles(HttpServletRequest request) {
        JSONObject param = new JSONObject();
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return null;
        }
        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return null;
        }
        String filePathBase = WebAppUtil.getProperty("oaFinanceFilePathBase");
        if (StringUtils.isBlank(filePathBase)) {
            logger.error("can't find oaFinanceFilePathBase");
            return null;
        }
        try {
            String oaFlowId = toGetParamVal(parameters.get("oaFlowId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String fileSize = toGetParamVal(parameters.get("fileSize"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();

            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + oaFlowId;
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
            fileInfo.put("oaFlowId", oaFlowId);
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            oaFinanceDao.addFileInfos(fileInfo);
            param.put("id", id);
            param.put("oaFlowId", oaFlowId);
            param.put("fileName", fileName);
            param.put("fileFullPath", fileFullPath);
            oaFinanceDao.updateFileInfos(param);
            return param;
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
            return null;
        }
    }

    public void sendFile2OA(HttpServletRequest request) throws ServiceException, IOException {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到上传的参数");
            return;
        }
        String oaFlowId = toGetParamVal(parameters.get("oaFlowId"));
        List<JSONObject> list = queryOAFileList(oaFlowId);
        int num = list.size();
        int i = 0;
        IXcmgReviewWebserviceServiceServiceLocator locator = new IXcmgReviewWebserviceServiceServiceLocator();
        IXcmgReviewWebserviceService ss = locator.getIXcmgReviewWebserviceServicePort();
        // 构建参数
        XcmgReviewUpdateParamterForm form = new XcmgReviewUpdateParamterForm();
        form.setFdId(oaFlowId);
        String paramJson = "{}";
        form.setFormValues(paramJson);
        AttachmentForm[] ats = new AttachmentForm[num];
        for (JSONObject one : list) {
            String fileFullPath = one.getString("fileFullPath");
            String fileName = one.getString("fileName");
            AttachmentForm attachmentForm = new AttachmentForm();
            attachmentForm.setFdKey("fd_39587a4a71fff4");
            attachmentForm.setFdFileName(fileName);
            File file = new File(fileFullPath);
            DataSource dataSource = new FileDataSource(file);
            DataHandler dataHandler = new DataHandler(dataSource);
            //..@lwgkiller
            // 从DataHandler获取输入流
            InputStream inputStream = dataHandler.getInputStream();
            //使用ByteArrayOutputStream来读取输入流并转换为byte[]
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byteArrayOutputStream.flush();
            // 获取最终的byte[]
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            attachmentForm.setFdAttachment(byteArray);
            //..付疆疆只包了个byte的壳 todo:mark一下，真是服了！！！
            //byte[] b = new byte[dataHandler.getInputStream().available()];
            //attachmentForm.setFdAttachment(b);
            ats[i] = attachmentForm;
            i++;
        }
        if (num != 0) {
            form.setAttachmentForms(ats);
            ss.updateFormData(form);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    public void delOAUploadFile(String id, String fileName, String oaFlowId) {
        String oaFinanceFilePathBase = WebAppUtil.getProperty("oaFinanceFilePathBase");
        rdmZhglFileManager.deleteOneFileFromDisk(id, fileName, oaFlowId, oaFinanceFilePathBase);
        oaFinanceDao.deleteFileById(id);
    }

    // 根据userName查询财务OA流程机器人节点的信息（暂，后迭代为userId）
    public List<JSONObject> getOAFinanceDetailByCurrentUserId(String userId) {
        Map<String, Object> oneRowMap = new HashMap<>(16);
        // oneRowMap.put("userName", userName);
        oneRowMap.put("userId", userId);
        if (userId == null || userId.equalsIgnoreCase("")) {
            List<JSONObject> needTodos = new ArrayList<>();
            return needTodos;
        } else {
            List<JSONObject> needTodos = oaFinanceDao.getOAFinanceDetailByCurrentUserId(oneRowMap);
            return needTodos;
        }
    }
}
