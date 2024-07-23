package com.redxun.info.core.manager;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.dao.InfoJptzDao;
import com.redxun.info.core.model.*;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.strategicPlanning.core.domain.BaseDomain;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 竞品图纸的服务层
 *
 * @author douhongli
 * @date 2021年5月31日09:59:36
 */
@Service
public class InfoJptzService {
    private static final Logger logger = LoggerFactory.getLogger(InfoJptzService.class);

    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private InfoJptzDao infoJptzDao;

    /**
     * 国内企业供应商-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月31日11:41:57
     */
    public JsonPageResult<T> selecJptzList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
        // 查询参数
        Map<String, Object> param = new HashMap<>();
        // 返回数据
        JsonPageResult result = new JsonPageResult();
        // 分页
        if (doPage) {
            rdmZhglUtil.addPage(request, param);
        }
        // 拼接查询参数
        paramsVo.getSearchConditions().forEach(searchParamsData -> {
            param.put(searchParamsData.getName(), searchParamsData.getValue());
        });
        // 默认排序
        rdmZhglUtil.addOrder(request, param, null, null);
        // 获取查询列表
        List<InfoJptz> list = infoJptzDao.selectJptzList(param);
        result.setData(list);
        result.setTotal(infoJptzDao.selectJptzCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 国内企业供应商-批量保存、更新或删除
     *
     * @param infoJptzList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:47
     */
    public JsonResult jptzBatchOptions(List<InfoJptz> infoJptzList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoJptz> addList = new ArrayList<>();
        List<InfoJptz> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoJptzList.forEach(infoJptz -> {
            if (StringUtil.isEmpty(infoJptz.getId())) {
                // 新增
                infoJptz.setId(IdUtil.getId());
                infoJptz.setCreateBy(currentUserId);
                infoJptz.setCreateTime(new Date());
                addList.add(infoJptz);
            } else if (BaseDomain.UPDATE.equals(infoJptz.get_state())) {
                // 更新
                infoJptz.setUpdateBy(currentUserId);
                infoJptz.setUpdateTime(new Date());
                updateList.add(infoJptz);
            } else if (BaseDomain.REMOVE.equals(infoJptz.get_state())) {
                // 删除
                deleteIds.add(infoJptz.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoJptzDao.batchInsertJptz(addList);
            }
        } catch (Exception e) {
            logger.error("竞品图纸批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoJptzDao.batchUpdateJptz(updateList);
            }
        } catch (Exception e) {
            logger.error("竞品图纸批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                // 批量删除文件 包含文件夹
                batchDeleteFiles(deleteIds);
                infoJptzDao.batchDeleteJptz(deleteIds);
            }
        } catch (Exception e) {
            logger.error("竞品图纸批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 根据id删除文件夹
     * @param deleteIds 需要删除的id列表
     * @author douhongli
     * @since 2021年6月2日17:21:05
     */
    private void batchDeleteFiles(List<String> deleteIds) {
        deleteIds.forEach(id -> {
            Map<String,Object> fileParam = new HashMap<>();
            // 文件目录
            String enterpriseFileDir = WebAppUtil.getProperty("jptzFileDir") + File.separator + id;
            File file = new File(enterpriseFileDir);
            // 是否存在
            if (file.exists()) {
                recursionDeleteFile(file);
            }
        });
    }

    /**
     * 递归删除文件或文件夹
     * @param file file
     * @author douhongli
     * @since 2021年6月2日17:21:45
     */
    private void recursionDeleteFile(File file) {
        if (file.isDirectory()) {
            // 目录
            File[] subListFiles = file.listFiles();
            if (null != subListFiles && subListFiles.length > 0){
                // 递归调用
                Arrays.stream(subListFiles).forEach(this::recursionDeleteFile);
            }
        }
        if (!file.delete()) {
            logger.error("{} delete error", file.getAbsolutePath());
        }
    }


    /**
     * 竞品图纸样本文件-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月1日10:00:14
     */
    public JsonPageResult<T> selectJptzFileList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
        // 查询参数
        Map<String, Object> param = new HashMap<>();
        // 返回数据
        JsonPageResult result = new JsonPageResult();
        // 分页
        if (doPage) {
            rdmZhglUtil.addPage(request, param);
        }
        // 拼接查询参数
        paramsVo.getSearchConditions().forEach(searchParamsData -> {
            param.put(searchParamsData.getName(), searchParamsData.getValue());
        });
        // 默认排序
        rdmZhglUtil.addOrder(request, param, null, null);
        // 获取查询列表
        List<InfoJptzFiles> list = infoJptzDao.selectJptzFileList(param);
        result.setData(list);
        result.setTotal(infoJptzDao.selectJptzFileCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 竞品图纸样本文件-批量保存、更新或删除
     *
     * @param infoJptzFilesList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日10:03:55
     */
    public JsonResult jptzFileBatchOptions(List<InfoJptzFiles> infoJptzFilesList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoJptzFiles> addList = new ArrayList<>();
        List<InfoJptzFiles> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoJptzFilesList.forEach(infoJptzFiles -> {
            if (StringUtil.isEmpty(infoJptzFiles.getId())) {
                // 新增
                infoJptzFiles.setId(IdUtil.getId());
                infoJptzFiles.setCreateBy(currentUserId);
                infoJptzFiles.setCreateTime(new Date());
                addList.add(infoJptzFiles);
            } else if (BaseDomain.UPDATE.equals(infoJptzFiles.get_state())) {
                // 更新
                infoJptzFiles.setUpdateBy(currentUserId);
                infoJptzFiles.setUpdateTime(new Date());
                updateList.add(infoJptzFiles);
            } else if (BaseDomain.REMOVE.equals(infoJptzFiles.get_state())) {
                // 删除
                deleteIds.add(infoJptzFiles.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoJptzDao.batchInsertJptzFile(addList);
            }
        } catch (Exception e) {
            logger.error("竞品图纸样本批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoJptzDao.batchUpdateJptzFile(updateList);
            }
        } catch (Exception e) {
            logger.error("竞品图纸样本批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                infoJptzDao.batchDeleteJptzFile(deleteIds);
            }
        } catch (Exception e) {
            logger.error("竞品图纸样本批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 将上传的文件copy到文件夹并把数据保存到数据库
     *
     * @param request 请求参数
     * @author douhongli
     * @since 2021年6月1日16:57:13
     */
    public void saveCommonUploadFiles(HttpServletRequest request) {
        List<InfoJptzFiles> addList = new ArrayList<>();
        InfoJptzFiles infoJptzFiles = new InfoJptzFiles();
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
        try {
            // 记录成功写入磁盘的文件信息
            String fileName = parameters.get("fileName")[0];
            infoJptzFiles.setId(IdUtil.getId());
            infoJptzFiles.setFileName(fileName);
            infoJptzFiles.setBelongId(parameters.get("belongId")[0]);
            infoJptzFiles.setFileSize(parameters.get("fileSize")[0]);
            infoJptzFiles.setCreateBy(ContextUtil.getCurrentUserId());
            infoJptzFiles.setTenantId(ContextUtil.getCurrentTenantId());
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String materielExtendFileDir = WebAppUtil.getProperty("jptzFileDir") + File.separator + infoJptzFiles.getBelongId();
            File pathFile = new File(materielExtendFileDir);
            // 目录不存在则创建
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fileFullPath = materielExtendFileDir + File.separator + infoJptzFiles.getId() + "." + suffix;
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be deleted");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);
            // 插入数据库
            addList.add(infoJptzFiles);
            infoJptzDao.batchInsertJptzFile(addList);
        } catch (Exception e) {
            logger.error("Exception in saveCommonUploadFiles", e);
        }
        return;
    }

    /**
     * 删除文件
     * @param request 请求
     * @author douhongli
     * @since 2021年6月2日10:35:30
     */
    public void deleteOneFile(HttpServletRequest request) {
        try {
            List<String> ids = new ArrayList<>();
            String id = RequestUtil.getString(request, "id");
            String fileName = RequestUtil.getString(request, "fileName");
            String belongId = RequestUtil.getString(request, "belongId");
            String projectFilePathBase = WebAppUtil.getProperty("jptzFileDir");
            String suffix = "";
            String[] arr = fileName.split("\\.", -1);
            if (arr.length > 1) {
                suffix = arr[arr.length - 1];
            }
            String fullFilePath = projectFilePathBase + File.separator + belongId + File.separator + id + "." + suffix;
            // 先删除磁盘中的文件
            File file = new File(fullFilePath);
            if (file.exists()) {
                logger.info("File " + fullFilePath + " will be deleted!");
                file.delete();
            }
            String temFilePath = projectFilePathBase + File.separator + belongId + File.separator + WebAppUtil.getProperty("convertPdfDir") + File.separator + id + ".pdf";
            File tempFile = new File(temFilePath);
            if (tempFile.exists()) {
                logger.info("tempFile {} will be deleted!", temFilePath);
                tempFile.delete();
            }
            ids.add(id);
            // 删除数据库中的数据
            infoJptzDao.batchDeleteJptzFile(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteOneFile", e);
        }
    }
}
