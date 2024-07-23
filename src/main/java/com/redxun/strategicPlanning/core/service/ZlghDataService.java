package com.redxun.strategicPlanning.core.service;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.BeanUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.strategicPlanning.core.dao.ZlghDataDao;
import com.redxun.strategicPlanning.core.domain.BaseDomain;
import com.redxun.strategicPlanning.core.domain.ZlghZljc;
import com.redxun.strategicPlanning.core.domain.ZlghZlkt;
import com.redxun.strategicPlanning.core.domain.ZlghZyhd;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZlktDto;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZyhdDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 战略规划关于战略举措，战略课题，主要活动的服务层
 *
 * @author douhongli
 */
@Service
public class ZlghDataService {
    private static final Logger logger = LoggerFactory.getLogger(ZlghDataService.class);

    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZlghDataDao zlghDataDao;

    /**
     * 战略举措-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     */
    public JsonPageResult<T> selectZljcList(ParamsVo paramsVo, HttpServletRequest request, boolean doPage) {
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
        rdmZhglUtil.addOrder(request, param, "CREATE_TIME_", "asc");
        // 获取查询列表
        List<ZlghZljc> list = zlghDataDao.selectZljcList(param);
        result.setData(list);
        result.setTotal(zlghDataDao.selectZljcCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 查询所有战略举措
     * @return List
     * @author douhongli
     * @date 2021年5月26日13:57:39
     */
    public List<ZlghZljc> listZljcText() {
        return zlghDataDao.listZljcText();
    }

    /**
     * 战略规划-批量保存、更新或删除战略举措
     *
     * @param zlghZljcList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月24日09:36:50
     */
    public JsonResult zljcBatchOptions(List<ZlghZljc> zlghZljcList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<ZlghZljc> addList = new ArrayList<>();
        List<ZlghZljc> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        zlghZljcList.forEach(zlghZljc -> {
            if (BaseDomain.ADD.equals(zlghZljc.get_state())) {
                if (zlghZljc.getId() == null) {
                    zlghZljc.setId(IdUtil.getId());
                }
                zlghZljc.setCreateBy(currentUserId);
                zlghZljc.setCreateTime(new Date());
                addList.add(zlghZljc);
            }
            if (BaseDomain.UPDATE.equals(zlghZljc.get_state())) {
                zlghZljc.setUpdateBy(currentUserId);
                zlghZljc.setUpdateTime(new Date());
                updateList.add(zlghZljc);
            }
            if (BaseDomain.REMOVE.equals(zlghZljc.get_state())) {
                deleteIds.add(zlghZljc.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                zlghDataDao.batchInsertZljc(addList);
            }
        } catch (Exception e) {
            logger.error("战略举措批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            if (updateList.size() > 0) {
                zlghDataDao.batchUpdateZljc(updateList);
            }
        } catch (Exception e) {
            logger.error("战略举措批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                zlghDataDao.batchDeleteZljc(deleteIds);
                // 通过举措id,查询要删除战略课题列表
                List<ZlghZlkt> zlghZlktList = zlghDataDao.selectZlktListByZljcId(deleteIds);
                // 获取要删除的战略课题ids
                List<String> zlktIds = zlghZlktList.stream().map(ZlghZlkt::getId).collect(Collectors.toList());
                // 删除战略课题
                zlghDataDao.batchDeleteZlkt(zlktIds);
                // 获取要删除的主要活动
                List<ZlghZyhd> zlghZyhdList = zlghDataDao.selectZyhdListByZlktId(zlktIds);
                // 删除主要活动和活动年份
                zlghDataDao.batchDeleteZyhd(zlghZyhdList.stream().map(ZlghZyhd::getId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("战略举措批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 战略课题-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月24日11:33:12
     */
    public JsonPageResult<T> selectZlktList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        rdmZhglUtil.addOrder(request, param, "zlgh_zlkt.CREATE_TIME_", "asc");
        // 获取查询列表
        List<ZlghZljc> list = zlghDataDao.selectZlktList(param);
        result.setData(list);
        result.setTotal(zlghDataDao.selectZlktCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 根据战略举措id查询战略课题列表数据
     * @return List
     * @author douhongli
     * @date 2021年5月26日17:55:33
     */
    public List<ZlghZlkt> listZlktByZljcId(String zljcId) {
        return zlghDataDao.listZlktByZljcId(zljcId);
    }

    /**
     * 战略规划-批量保存、更新或删除战略课题
     *
     * @param zlghZlktList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月25日10:27:47
     */
    public JsonResult zlktBatchOptions(List<ZlghZlktDto> zlghZlktList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<ZlghZlkt> addList = new ArrayList<>();
        List<ZlghZlkt> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        zlghZlktList.forEach(zlghZlktDto -> {
            ZlghZlkt zlghZlkt = new ZlghZlkt();
            try {
                BeanUtil.copyNotNullProperties(zlghZlkt, zlghZlktDto);
            } catch (Exception e) {
                logger.error("bean拷贝异常:{}", e.getMessage());
            }
            zlghZlkt.setId(zlghZlktDto.getZlktId());
            zlghZlkt.setZljcId(zlghZlktDto.getId());
            if (StringUtil.isEmpty(zlghZlkt.getId())) {
                // 新增
                zlghZlkt.setId(IdUtil.getId());
                zlghZlkt.setCreateBy(currentUserId);
                zlghZlkt.setCreateTime(new Date());
                addList.add(zlghZlkt);
            } else if (BaseDomain.UPDATE.equals(zlghZlkt.get_state())) {
                zlghZlkt.setUpdateBy(currentUserId);
                zlghZlkt.setUpdateTime(new Date());
                updateList.add(zlghZlkt);
            } else if (BaseDomain.REMOVE.equals(zlghZlkt.get_state())){
                deleteIds.add(zlghZlkt.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                zlghDataDao.batchInsertZlkt(addList);
            }
        } catch (Exception e) {
            logger.error("战略课题批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            if (updateList.size() > 0) {
                zlghDataDao.batchUpdateZlkt(updateList);
            }
        } catch (Exception e) {
            logger.error("战略课题批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                zlghDataDao.batchDeleteZlkt(deleteIds);
                // 根据需要删除的战略课题id,查询主要活动
                List<ZlghZyhd> zlghZyhdList = zlghDataDao.selectZyhdListByZlktId(deleteIds);
                zlghDataDao.batchDeleteZyhd(zlghZyhdList.stream().map(ZlghZyhd::getId).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            logger.error("战略课题批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }

    /**
     * 主要活动-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月25日13:51:54
     */
    public JsonPageResult<T> selectZyhdList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<ZlghZyhdDto> list = zlghDataDao.selectZyhdList(param);
        result.setData(list);
        result.setTotal(zlghDataDao.selectZyhdCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 战略规划-批量保存、更新或删除主要活动
     *
     * @param zlghZyhdList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日09:34:46
     */
    public JsonResult zyhdBatchOptions(List<ZlghZyhdDto> zlghZyhdList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<ZlghZyhd> addList = new ArrayList<>();
        List<ZlghZyhd> updateList = new ArrayList<>();
        List<String> deleteIds = new ArrayList<>();
        // 添加必备字段值
        zlghZyhdList.forEach(zlghZlktDto -> {
            ZlghZyhd zlghZyhd = new ZlghZyhd();
            try {
                // bean属性拷贝
                BeanUtil.copyNotNullProperties(zlghZyhd, zlghZlktDto);
            } catch (Exception e) {
                logger.error("bean拷贝异常:{}", e.getMessage());
            }
            // 赋值拷贝错误的属性
            zlghZyhd.setId(zlghZlktDto.getZyhdId());
            zlghZyhd.setZljcId(zlghZlktDto.getId());
            // 判空
            if (StringUtil.isEmpty(zlghZyhd.getId())) {
                // 新增
                zlghZyhd.setId(IdUtil.getId());
                zlghZyhd.setCreateBy(currentUserId);
                zlghZyhd.setCreateTime(new Date());
                addList.add(zlghZyhd);
            } else if (BaseDomain.UPDATE.equals(zlghZyhd.get_state())) {
                // 更新
                zlghZyhd.setUpdateBy(currentUserId);
                zlghZyhd.setUpdateTime(new Date());
                updateList.add(zlghZyhd);
            } else if (BaseDomain.REMOVE.equals(zlghZyhd.get_state())){
                // 删除
                deleteIds.add(zlghZyhd.getId());
            }
        });
        try {
            if (addList.size() > 0) {
                zlghDataDao.batchInsertZyhd(addList);
            }
        } catch (Exception e) {
            logger.error("主要活动批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            if (updateList.size() > 0) {
                zlghDataDao.batchUpdateZyhd(updateList);
            }
        } catch (Exception e) {
            logger.error("主要活动批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        try {
            if (deleteIds.size() > 0) {
                zlghDataDao.batchDeleteZyhd(deleteIds);
            }
        } catch (Exception e) {
            logger.error("主要活动批量删除失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("删除失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }
}
