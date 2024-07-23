package com.redxun.strategicPlanning.core.service;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.core.util.DateUtil;
import com.redxun.core.util.StringUtil;
import com.redxun.rdmZhgl.core.service.RdmZhglUtil;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.strategicPlanning.core.dao.ZlghDataDao;
import com.redxun.strategicPlanning.core.dao.ZlghPlanningDao;
import com.redxun.strategicPlanning.core.domain.ZlghHdnf;
import com.redxun.strategicPlanning.core.domain.ZlghZljc;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZyhdDto;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 战略规划整体展示的服务层
 * @author douhongli
 */
@Service
public class ZlghPlanningService {
    private static final Logger logger = LoggerFactory.getLogger(ZlghPlanningService.class);

    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZlghPlanningDao zlghPlanningDao;

    @Resource
    private ZlghDataDao zlghDataDao;

    /**
     * 战略规划-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年5月27日15:14:26
     */
    public JsonPageResult<T> selecZlghList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<ZlghDto> list = zlghPlanningDao.selectZlghList(param);
        list.forEach(zlghDto -> {
            Map<String, Object> zlghHdnfParams = new HashMap<>();
            zlghHdnfParams.put("zyhdId", zlghDto.getZyhdId());
            rdmZhglUtil.addOrder(request, zlghHdnfParams, "year", "asc");
            // 查询当前活动的当前年的前一年至后两年的活动年份数据
            List<ZlghHdnf> zlghHdnfList = zlghPlanningDao.selectZlghHdnfList(zlghHdnfParams);
            // 循环赋值
            zlghHdnfList.forEach(zlghHdnf -> {
                // 年份
                int year = Integer.valueOf(zlghHdnf.getYear());
                // 当前年
                int curYear = DateUtil.getCurYear();
                if (year == (curYear-1)) {
                    // 前一年
                    zlghDto.setPreHdnfId(zlghHdnf.getId());
                    zlghDto.setPreJhTarget(zlghHdnf.getJhTarget());
                    zlghDto.setPreNdWczp(zlghHdnf.getNdWczp());
                    zlghDto.setPreNdXsjdl(zlghHdnf.getNdXsjdl());
                    zlghDto.setPreNzWczp(zlghHdnf.getNzWczp());
                    zlghDto.setPreNzXsjdl(zlghHdnf.getNzXsjdl());
                    zlghDto.setPreYear(zlghHdnf.getYear());
                }
                if (year == curYear) {
                    // 当前年
                    zlghDto.setCurrentHdnfId(zlghHdnf.getId());
                    zlghDto.setCurrentJhTarget(zlghHdnf.getJhTarget());
                    zlghDto.setCurrentNdWczp(zlghHdnf.getNdWczp());
                    zlghDto.setCurrentNdXsjdl(zlghHdnf.getNdXsjdl());
                    zlghDto.setCurrentNzWczp(zlghHdnf.getNzWczp());
                    zlghDto.setCurrentNzXsjdl(zlghHdnf.getNzXsjdl());
                    zlghDto.setCurrentYear(zlghHdnf.getYear());
                }
                if (year == (curYear+1)) {
                    // 后一年
                    zlghDto.setAftOneHdnfId(zlghHdnf.getId());
                    zlghDto.setAftOneJhTarget(zlghHdnf.getJhTarget());
                    zlghDto.setAftOneNdWczp(zlghHdnf.getNdWczp());
                    zlghDto.setAftOneNdXsjdl(zlghHdnf.getNdXsjdl());
                    zlghDto.setAftOneNzWczp(zlghHdnf.getNzWczp());
                    zlghDto.setAftOneNzXsjdl(zlghHdnf.getNzXsjdl());
                    zlghDto.setAftOneYear(zlghHdnf.getYear());
                }
                if (year == (curYear+2)) {
                    // 后二年
                    zlghDto.setAftTwoHdnfId(zlghHdnf.getId());
                    zlghDto.setAftTwoJhTarget(zlghHdnf.getJhTarget());
                    zlghDto.setAftTwoNdWczp(zlghHdnf.getNdWczp());
                    zlghDto.setAftTwoNdXsjdl(zlghHdnf.getNdXsjdl());
                    zlghDto.setAftTwoNzWczp(zlghHdnf.getNzWczp());
                    zlghDto.setAftTwoNzXsjdl(zlghHdnf.getNzXsjdl());
                    zlghDto.setAftTwoYear(zlghHdnf.getYear());
                }
            });
        });
        result.setData(list);
        result.setTotal(zlghDataDao.selectZyhdCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 战略规划-批量保存、更新或删除
     *
     * @param zlghList 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年5月27日15:15:47
     */
    public JsonResult zlghBatchOptions(List<ZlghDto> zlghList) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<ZlghHdnf> addList = new ArrayList<>();
        List<ZlghHdnf> updateList = new ArrayList<>();
        // 添加必备字段值
        zlghList.forEach(zlghDto -> {
            // 前一年
            if (StringUtil.isEmpty(zlghDto.getPreHdnfId())) {
                // 新增
                addList.add(new ZlghHdnf(zlghDto.getZyhdId(), zlghDto.getPreYear(), zlghDto.getPreJhTarget(), zlghDto.getPreNzXsjdl(),
                        zlghDto.getPreNzWczp(), zlghDto.getPreNdXsjdl(), zlghDto.getPreNdWczp(), currentUserId));
            } else {
                // 更新
                updateList.add(new ZlghHdnf(zlghDto.getPreHdnfId(),zlghDto.getZyhdId(), zlghDto.getPreYear(), zlghDto.getPreJhTarget(),
                        zlghDto.getPreNzXsjdl(), zlghDto.getPreNzWczp(), zlghDto.getPreNdXsjdl(), zlghDto.getPreNdWczp(), currentUserId));
            }
            // 当前年
            if (StringUtil.isEmpty(zlghDto.getCurrentHdnfId())) {
                // 新增
                addList.add(new ZlghHdnf(zlghDto.getZyhdId(), zlghDto.getCurrentYear(), zlghDto.getCurrentJhTarget(), zlghDto.getCurrentNzXsjdl(),
                        zlghDto.getCurrentNzWczp(), zlghDto.getCurrentNdXsjdl(), zlghDto.getCurrentNdWczp(), currentUserId));
            } else {
                // 更新
                updateList.add(new ZlghHdnf(zlghDto.getCurrentHdnfId(),zlghDto.getZyhdId(), zlghDto.getCurrentYear(), zlghDto.getCurrentJhTarget(),
                        zlghDto.getCurrentNzXsjdl(), zlghDto.getCurrentNzWczp(), zlghDto.getCurrentNdXsjdl(), zlghDto.getCurrentNdWczp(), currentUserId));
            }
            // 后一年
            if (StringUtil.isEmpty(zlghDto.getAftOneHdnfId())) {
                // 新增
                addList.add(new ZlghHdnf(zlghDto.getZyhdId(), zlghDto.getAftOneYear(), zlghDto.getAftOneJhTarget(), zlghDto.getAftOneNzXsjdl(),
                        zlghDto.getAftOneNzWczp(), zlghDto.getAftOneNdXsjdl(), zlghDto.getAftOneNdWczp(), currentUserId));
            } else {
                // 更新
                updateList.add(new ZlghHdnf(zlghDto.getAftOneHdnfId(),zlghDto.getZyhdId(), zlghDto.getAftOneYear(), zlghDto.getAftOneJhTarget(), zlghDto.getAftOneNzXsjdl(),
                        zlghDto.getAftOneNzWczp(), zlghDto.getAftOneNdXsjdl(), zlghDto.getAftOneNdWczp(), currentUserId));
            }
            // 后二年
            if (StringUtil.isEmpty(zlghDto.getAftTwoHdnfId())) {
                // 新增
                addList.add(new ZlghHdnf(zlghDto.getZyhdId(), zlghDto.getAftTwoYear(), zlghDto.getAftTwoJhTarget(), zlghDto.getAftTwoNzXsjdl(),
                        zlghDto.getAftTwoNzWczp(), zlghDto.getAftTwoNdXsjdl(), zlghDto.getAftTwoNdWczp(), currentUserId));
            } else {
                // 更新
                updateList.add(new ZlghHdnf(zlghDto.getAftTwoHdnfId(),zlghDto.getZyhdId(), zlghDto.getAftTwoYear(), zlghDto.getAftTwoJhTarget(), zlghDto.getAftTwoNzXsjdl(),
                        zlghDto.getAftTwoNzWczp(), zlghDto.getAftTwoNdXsjdl(), zlghDto.getAftTwoNdWczp(), currentUserId));
            }
        });
        try {
            if (addList.size() > 0) {
                zlghPlanningDao.batchInsertZlghHdnf(addList);
            }
        } catch (Exception e) {
            logger.error("战略规划活动年份批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            // 筛选出数据被更改的活动年份对象
            List<ZlghHdnf> needUpdateList = updateList.stream().filter(zlghHdnf -> {
                ZlghHdnf zlghHdnfTemp = zlghPlanningDao.queryZlghHdnf(zlghHdnf.getId());
                return !zlghHdnf.equals(zlghHdnfTemp);
            }).collect(Collectors.toList());
            if (needUpdateList.size() > 0) {
                zlghPlanningDao.batchUpdateZlghHdnf(needUpdateList);
            }
        } catch (Exception e) {
            logger.error("战略规划活动年份批量更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }
}
