package com.redxun.info.core.manager;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.info.core.dao.InfoEnterpriseDao;
import com.redxun.info.core.model.InfoCpybFiles;
import com.redxun.info.core.model.InfoGngys;
import com.redxun.info.core.model.InfoGwgys;
import com.redxun.info.core.model.InfoGyslxr;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.saweb.util.RequestUtil;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.strategicPlanning.core.dao.ZlghDataDao;
import com.redxun.strategicPlanning.core.dao.ZlghPlanningDao;
import com.redxun.strategicPlanning.core.domain.BaseDomain;
import com.redxun.strategicPlanning.core.domain.ZlghHdnf;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import com.redxun.xcmgProjectManager.core.util.XcmgProjectUtil;
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
import java.util.stream.Collectors;

/**
 * 企业供应商展示的服务层
 *
 * @author douhongli
 * @date 2021年5月31日09:59:36
 */
@Service
public class InfoEnterpriseService {
    private static final Logger logger = LoggerFactory.getLogger(InfoEnterpriseService.class);

    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private InfoEnterpriseDao infoEnterpriseDao;

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
    public JsonPageResult<T> selecGnList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<InfoGngys> list = infoEnterpriseDao.selectGnList(param);
        result.setData(list);
        result.setTotal(infoEnterpriseDao.selectGnCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 国内企业供应商-批量保存、更新或删除
     *
     * @param infoGngysList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:47
     */
    public JsonResult gnBatchOptions(List<InfoGngys> infoGngysList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoGngys> addList = new ArrayList<>();
        List<InfoGngys> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoGngysList.forEach(infoGngys -> {
            if (StringUtil.isEmpty(infoGngys.getId())) {
                // 新增
                infoGngys.setId(IdUtil.getId());
                infoGngys.setCreateBy(currentUserId);
                infoGngys.setCreateTime(new Date());
                addList.add(infoGngys);
            } else if (BaseDomain.UPDATE.equals(infoGngys.get_state())) {
                // 更新
                infoGngys.setUpdateBy(currentUserId);
                infoGngys.setUpdateTime(new Date());
                updateList.add(infoGngys);
            } else if (BaseDomain.REMOVE.equals(infoGngys.get_state())) {
                // 删除
                deleteIds.add(infoGngys.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoEnterpriseDao.batchInsertGn(addList);
            }
        } catch (Exception e) {
            logger.error("国内企业供应商批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoEnterpriseDao.batchUpdateGn(updateList);
            }
        } catch (Exception e) {
            logger.error("国内企业供应商批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                // 批量删除文件 包含文件夹
                batchDeleteFiles(deleteIds);
                infoEnterpriseDao.batchDeleteGn(deleteIds);
            }
        } catch (Exception e) {
            logger.error("国内企业供应商批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 国外企业供应商-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月31日11:41:57
     */
    public JsonPageResult<T> selecGwList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<InfoGwgys> list = infoEnterpriseDao.selectGwList(param);
        result.setData(list);
        result.setTotal(infoEnterpriseDao.selectGwCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 国外企业供应商-批量保存、更新或删除
     *
     * @param infoGwgysList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:47
     */
    public JsonResult gwBatchOptions(List<InfoGwgys> infoGwgysList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoGwgys> addList = new ArrayList<>();
        List<InfoGwgys> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoGwgysList.forEach(infoGwgys -> {
            if (StringUtil.isEmpty(infoGwgys.getId())) {
                // 新增
                infoGwgys.setId(IdUtil.getId());
                infoGwgys.setCreateBy(currentUserId);
                infoGwgys.setCreateTime(new Date());
                addList.add(infoGwgys);
            } else if (BaseDomain.UPDATE.equals(infoGwgys.get_state())) {
                // 更新
                infoGwgys.setUpdateBy(currentUserId);
                infoGwgys.setUpdateTime(new Date());
                updateList.add(infoGwgys);
            } else if (BaseDomain.REMOVE.equals(infoGwgys.get_state())) {
                // 删除
                deleteIds.add(infoGwgys.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoEnterpriseDao.batchInsertGw(addList);
            }
        } catch (Exception e) {
            logger.error("国外企业供应商批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoEnterpriseDao.batchUpdateGw(updateList);
            }
        } catch (Exception e) {
            logger.error("国外企业供应商批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                // 批量删除文件 包含文件夹
                batchDeleteFiles(deleteIds);
                infoEnterpriseDao.batchDeleteGw(deleteIds);
            }
        } catch (Exception e) {
            logger.error("国外企业供应商批量删除失败,异常原因:{}", e.getMessage());
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
            String enterpriseFileDir = WebAppUtil.getProperty("enterpriseFileDir") + File.separator + id;
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
                Arrays.stream(subListFiles).forEach(this::recursionDeleteFile);
            }
        }
        if (!file.delete()) {
            logger.error("{} delete error", file.getAbsolutePath());
        }
    }

    /**
     * 企业供应商联系人-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月1日09:53:58
     */
    public JsonPageResult<T> selectEnterpriseContactsList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<InfoGyslxr> list = infoEnterpriseDao.selectEnterpriseContactsList(param);
        result.setData(list);
        result.setTotal(infoEnterpriseDao.selectEnterpriseContactsCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 企业供应商联系人-批量保存、更新或删除
     *
     * @param infoGyslxrList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日09:53:49
     */
    public JsonResult enterpriseContactsBatchOptions(List<InfoGyslxr> infoGyslxrList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoGyslxr> addList = new ArrayList<>();
        List<InfoGyslxr> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoGyslxrList.forEach(infoGyslxr -> {
            if (StringUtil.isEmpty(infoGyslxr.getId())) {
                // 新增
                infoGyslxr.setId(IdUtil.getId());
                infoGyslxr.setCreateBy(currentUserId);
                infoGyslxr.setCreateTime(new Date());
                addList.add(infoGyslxr);
            } else if (BaseDomain.UPDATE.equals(infoGyslxr.get_state())) {
                // 更新
                infoGyslxr.setUpdateBy(currentUserId);
                infoGyslxr.setUpdateTime(new Date());
                updateList.add(infoGyslxr);
            } else if (BaseDomain.REMOVE.equals(infoGyslxr.get_state())) {
                // 删除
                deleteIds.add(infoGyslxr.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoEnterpriseDao.batchInsertEnterpriseContacts(addList);
            }
        } catch (Exception e) {
            logger.error("企业供应商联系人批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoEnterpriseDao.batchUpdateEnterpriseContacts(updateList);
            }
        } catch (Exception e) {
            logger.error("企业供应商联系人批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                infoEnterpriseDao.batchDeleteEnterpriseContacts(deleteIds);
            }
        } catch (Exception e) {
            logger.error("企业供应商联系人批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 企业供应商样本文件-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月1日10:00:14
     */
    public JsonPageResult<T> selectEnterpriseFileList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<InfoCpybFiles> list = infoEnterpriseDao.selectEnterpriseFileList(param);
        result.setData(list);
        result.setTotal(infoEnterpriseDao.selectEnterpriseFileCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 企业供应商样本文件-批量保存、更新或删除
     *
     * @param infoCpybFilesList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月1日10:03:55
     */
    public JsonResult enterpriseFileBatchOptions(List<InfoCpybFiles> infoCpybFilesList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<InfoCpybFiles> addList = new ArrayList<>();
        List<InfoCpybFiles> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        infoCpybFilesList.forEach(infoCpybFile -> {
            if (StringUtil.isEmpty(infoCpybFile.getId())) {
                // 新增
                infoCpybFile.setId(IdUtil.getId());
                infoCpybFile.setCreateBy(currentUserId);
                infoCpybFile.setCreateTime(new Date());
                addList.add(infoCpybFile);
            } else if (BaseDomain.UPDATE.equals(infoCpybFile.get_state())) {
                // 更新
                infoCpybFile.setUpdateBy(currentUserId);
                infoCpybFile.setUpdateTime(new Date());
                updateList.add(infoCpybFile);
            } else if (BaseDomain.REMOVE.equals(infoCpybFile.get_state())) {
                // 删除
                deleteIds.add(infoCpybFile.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                infoEnterpriseDao.batchInsertEnterpriseFile(addList);
            }
        } catch (Exception e) {
            logger.error("企业供应商样本批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            if (updateList.size() > 0) {
                infoEnterpriseDao.batchUpdateEnterpriseFile(updateList);
            }
        } catch (Exception e) {
            logger.error("企业供应商样本批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                infoEnterpriseDao.batchDeleteEnterpriseFile(deleteIds);
            }
        } catch (Exception e) {
            logger.error("企业供应商样本批量删除失败,异常原因:{}", e.getMessage());
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
        List<InfoCpybFiles> addList = new ArrayList<>();
        InfoCpybFiles infoCpybFiles = new InfoCpybFiles();
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
            infoCpybFiles.setId(IdUtil.getId());
            infoCpybFiles.setFileName(fileName);
            infoCpybFiles.setBelongId(parameters.get("belongId")[0]);
            infoCpybFiles.setFileSize(parameters.get("fileSize")[0]);
            infoCpybFiles.setCreateBy(ContextUtil.getCurrentUserId());
            infoCpybFiles.setTenantId(ContextUtil.getCurrentTenantId());
            Iterator<MultipartFile> it = fileMap.values().iterator();
            MultipartFile mf = it.next();
            String materielExtendFileDir = WebAppUtil.getProperty("enterpriseFileDir") + File.separator + infoCpybFiles.getBelongId();
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
            String fileFullPath = materielExtendFileDir + File.separator + infoCpybFiles.getId() + "." + suffix;
            File file = new File(fileFullPath);
            // 文件存在则更新掉
            if (file.exists()) {
                logger.warn("File " + fileFullPath + " will be deleted");
                file.delete();
            }
            FileCopyUtils.copy(mf.getBytes(), file);
            // 插入数据库
            addList.add(infoCpybFiles);
            infoEnterpriseDao.batchInsertEnterpriseFile(addList);
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
            String projectFilePathBase = WebAppUtil.getProperty("enterpriseFileDir");
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
            infoEnterpriseDao.batchDeleteEnterpriseFile(ids);
        } catch (Exception e) {
            logger.error("Exception in deleteOneFile", e);
        }
    }
}
