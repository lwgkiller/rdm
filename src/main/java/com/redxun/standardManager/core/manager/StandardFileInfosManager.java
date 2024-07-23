
package com.redxun.standardManager.core.manager;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redxun.rdmCommon.core.manager.SendDDNoticeManager;
import com.redxun.serviceEngineering.core.dao.MaintenanceManualfileDao;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redxun.core.json.JsonPageResult;
import com.redxun.core.query.Page;
import com.redxun.core.util.DateFormatUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.standardManager.core.dao.StandardFileInfosDao;
import com.redxun.rdmCommon.core.manager.CommonFuns;
import com.redxun.xcmgProjectManager.core.util.ExcelUtils;

/**
 * @author zhangzhen
 */
@Service
public class StandardFileInfosManager {
    private static final Logger logger = LoggerFactory.getLogger(StandardFileInfosManager.class);
    @Resource
    private StandardFileInfosDao standardFileInfosDao;

    @Resource
    private MaintenanceManualfileDao maintenanceManualfileDao;

    @Resource
    private SendDDNoticeManager sendDDNoticeManager;

    public JSONArray getFiles(HttpServletRequest request) {
        String standardId = RequestUtil.getString(request, "standardId");
        String fileName = RequestUtil.getString(request, "fileName");
        String status = RequestUtil.getString(request, "status");
        String showType = RequestUtil.getString(request, "showType");
        String processId = RequestUtil.getString(request, "processId");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(standardId)) {
            params.put("standardId", standardId);
        }
        if (StringUtils.isNotBlank(fileName)) {
            params.put("fileName", fileName);
        }
        if (StringUtils.isNotBlank(status)) {
            params.put("status", status);
        }
        if (StringUtils.isNotBlank(showType)) {
            //管理标准列表页面通过附表按钮查看仅显示个人提交附表和已完成审批的附表
            params.put("currentUserId", ContextUtil.getCurrentUserId());
        }
        if (StringUtils.isNotBlank(processId)) {
            //主界面待办点击办理时,根据
            params.put("processId", processId);
        }

        JSONArray fileArray = standardFileInfosDao.getFiles(params);
        if (fileArray != null) {
            for (int index = 0; index < fileArray.size(); index++) {
                JSONObject oneFileObj = fileArray.getJSONObject(index);
                Date createTime = oneFileObj.getDate("CREATE_TIME_");
                if (createTime != null) {
                    oneFileObj.put("CREATE_TIME_", DateUtil.formatDate(createTime, null));
                }
            }
        }
        return fileArray;
    }

    /**
     * 新增保存文件到磁盘和数据库
     *
     * @param request
     */
    public void saveUploadFiles(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters == null || parameters.isEmpty()) {
            logger.warn("没有找到文件上传的参数");
            return;
        }

        // 多附件上传需要用到的MultipartHttpServletRequest
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap == null || fileMap.isEmpty()) {
            logger.warn("没有找到上传的文件");
            return;
        }

        try {
            String standardId = toGetParamVal(parameters.get("standardId"));
            String id = IdUtil.getId();
            String fileName = toGetParamVal(parameters.get("fileName"));
            String standardType = toGetParamVal(parameters.get("standardType"));

            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String filePathBase = WebAppUtil.getProperty("standardAttachFilePath");
            if (StringUtils.isBlank(filePathBase)) {
                logger.error("can't find filePathBase or filePathBase_preview");
                return;
            }
            // 向下载目录中写入文件
            String filePath = filePathBase + File.separator + standardId;
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
            fileInfo.put("fileSize", toGetParamVal(parameters.get("fileSize")));
            fileInfo.put("standardId", standardId);
            fileInfo.put("fileDesc", toGetParamVal(parameters.get("description")));
            fileInfo.put("CREATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("CREATE_TIME_", new Date());
            fileInfo.put("UPDATE_BY_", ContextUtil.getCurrentUserId());
            fileInfo.put("UPDATE_TIME_", new Date());
            //针对管理标准提交附表，需要设置审核，设置审批人（管理标准附表审批人员）、默认状态为待审核
            if ("GL".equals(standardType)) {
                JSONObject params = new JSONObject();
                params.put("roleKey", "GLBZFBSPRY");
                // 添加处理人信息
                List<JSONObject> roleUsers = maintenanceManualfileDao.getUsersByRolekey(params);
                List<String> processerId = new ArrayList<>();
                List<String> processer = new ArrayList<>();
                // 发送welink待办提醒
                List<JSONObject> userList = new ArrayList<>();
                for (JSONObject osUser : roleUsers) {
                    processerId.add(osUser.getString("USER_ID_"));
                    processer.add(osUser.getString("FULLNAME_"));
                    String certNo = osUser.getString("CERT_NO_");
                    osUser.put("userCertNo", certNo);
                    userList.add(osUser);
                }
                // 管理标准附件设置为未审批状态
                fileInfo.put("status", "unReview");
                fileInfo.put("processerId", String.join(",", processerId));
                fileInfo.put("processer", String.join(",", processer));
                JSONObject taskObj = new JSONObject();
                taskObj.put("content", "【管理标准附表提交】 申请流程审批，申请人："+ContextUtil.getCurrentUser().getFullname() +
                        "，提交时间：" +DateFormatUtil.getNowByString(DateUtil.DATE_FORMAT_FULL)+"，当前任务：管理标准附表审核");
                sendDDNoticeManager.sendNoticeForTask(userList, taskObj);
            }
            standardFileInfosDao.addFileInfos(fileInfo);
        } catch (Exception e) {
            logger.error("Exception in saveUploadFiles", e);
        }
    }

    private String toGetParamVal(String[] paramValArr) {
        if (paramValArr == null || paramValArr.length == 0) {
            return null;
        }
        return paramValArr[0];
    }

    /**
     * 删除标准的某一个附件文件
     *
     */
    public void deleteOneStandardFileOnDisk(String standardId, String fileId, String suffix) {
        String fullFileName = fileId + "." + suffix;
        StringBuilder fileBasePath = new StringBuilder(WebAppUtil.getProperty("standardAttachFilePath"));
        String fullPath = fileBasePath.append(File.separator).append(standardId).append(File.separator)
            .append(fullFileName).toString();
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 查询标准附表文件列表
     * 
     * @param request
     * @return
     */
    public JsonPageResult<?> getAttachFileGridList(HttpServletRequest request, boolean doPage) {
        JsonPageResult result = new JsonPageResult(true);
        Map<String, Object> params = new HashMap<>();
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
        // 分页
        if (doPage) {
            String pageIndex = RequestUtil.getString(request, "pageIndex", "0");
            String pageSize = RequestUtil.getString(request, "pageSize", String.valueOf(Page.DEFAULT_PAGE_SIZE));
            String startIndex = String.valueOf(Integer.parseInt(pageIndex) * Integer.parseInt(pageSize));
            params.put("startIndex", startIndex);
            params.put("pageSize", pageSize);
        }
        JSONArray fileArray = standardFileInfosDao.getFiles(params);
        result.setData(fileArray.toJavaList(JSONObject.class));
        if (fileArray != null) {
            for (int index = 0; index < fileArray.size(); index++) {
                JSONObject oneFileObj = fileArray.getJSONObject(index);
                Date createTime = oneFileObj.getDate("CREATE_TIME_");
                if (createTime != null) {
                    oneFileObj.put("CREATE_TIME_", DateUtil.formatDate(createTime, null));
                }
            }
        }
        if (doPage) {
            int fileNum = standardFileInfosDao.countAttachFileNum(params);
            result.setTotal(fileNum);
        }
        return result;
    }

    // 导出标准excel
    public void exportAttachFileGrid(HttpServletRequest request, HttpServletResponse response) {
        JsonPageResult fileList = getAttachFileGridList(request, false);
        List<JSONObject> fileArray = fileList.getData();
        String nowDate = DateFormatUtil.getNowByString("yyyy-MM-dd");
        String title = "标准附表文件列表";
        String excelName = nowDate + title;
        String[] fieldNames = {"文件名称", "文件大小", "所属标准", "归属部门", "文件说明", "创建人", "创建时间"};
        String[] fieldCodes =
            {"fileName", "fileSize", "standardName", "standardBelongDeptName", "fileDesc", "creator", "CREATE_TIME_"};
        HSSFWorkbook wbObj = ExcelUtils.exportExcelWB(fileArray, fieldNames, fieldCodes, title);
        // 输出
        ExcelUtils.writeWorkBook2Stream(excelName, wbObj, response);
    }

    public void updateAttachFileStatus(JSONObject formData) {
        JSONArray ids = formData.getJSONArray("ids");
        String processType = formData.getString("processType");
        HashMap<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        params.put("processType", processType);
        standardFileInfosDao.updateFileInfos(params);
    }
}
