package com.redxun.rdmZhgl.core.service;

import com.redxun.core.json.JsonPageResult;
import com.redxun.core.json.JsonResult;
import com.redxun.core.json.JsonResultUtil;
import com.redxun.rdmZhgl.core.dao.ZhglJsjdsPlanDao;
import com.redxun.rdmZhgl.core.domain.ZhglJsjdsPlan;
import com.redxun.saweb.context.ContextUtil;
import com.redxun.saweb.util.IdUtil;
import com.redxun.strategicPlanning.core.domain.BaseDomain;
import com.redxun.strategicPlanning.core.domain.vo.ParamsVo;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 技术交底书计划展示的服务层
 * @author douhongli
 * @since 2021年6月9日15:46:38
 */
@Service
public class ZhglJsjdsPlanService {
    private static final Logger logger = LoggerFactory.getLogger(ZhglJsjdsPlanService.class);

    @Resource
    private RdmZhglUtil rdmZhglUtil;

    @Resource
    private ZhglJsjdsPlanDao zhglJsjdsPlanDao;

    /**
     * 技术交底书计划-分页查询列表
     *
     * @param paramsVo 参数对象
     * @param request  请求
     * @param doPage   是否分页
     * @return JsonPageResult
     * @author douhongli
     * @date 2021年6月9日17:40:14
     */
    public JsonPageResult<T> selecList(ParamsVo paramsVo, HttpServletRequest request, Boolean doPage) {
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
        List<ZhglJsjdsPlan> list = zhglJsjdsPlanDao.selectJsjdsPlanList(param);
        result.setData(list);
        result.setTotal(zhglJsjdsPlanDao.selectJsjdsPlanCount(param));
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    /**
     * 技术交底书计划-批量保存、更新或删除
     *
     * @param zhglJsjdsPlans 需要操作的数据列表
     * @return JsonResult
     * @author douhongli
     * @date 2021年6月9日17:52:12
     */
    public JsonResult batchOptions(List<ZhglJsjdsPlan> zhglJsjdsPlans) {
        // 当前操作人id
        String currentUserId = ContextUtil.getCurrentUserId();
        // 获取对应状态的list数据
        List<ZhglJsjdsPlan> addList = new ArrayList<>();
        List<ZhglJsjdsPlan> updateList = new ArrayList<>();
        // 添加必备字段值
        zhglJsjdsPlans.forEach(zhglJsjdsPlan -> {
            if (BaseDomain.ADD.equals(zhglJsjdsPlan.get_state())) {
                if (zhglJsjdsPlan.getId() == null) {
                    zhglJsjdsPlan.setId(IdUtil.getId());
                }
                zhglJsjdsPlan.setCreateBy(currentUserId);
                zhglJsjdsPlan.setCreateTime(new Date());
                addList.add(zhglJsjdsPlan);
            }
            if (BaseDomain.UPDATE.equals(zhglJsjdsPlan.get_state())) {
                zhglJsjdsPlan.setUpdateBy(currentUserId);
                zhglJsjdsPlan.setUpdateTime(new Date());
                updateList.add(zhglJsjdsPlan);
            }
        });
        try {
            if (addList.size() > 0) {
                zhglJsjdsPlanDao.batchInsertJsjdsPlan(addList);
            }
        } catch (Exception e) {
            logger.error("技术交底书计划批量新增失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("保存失败!");
        }
        try {
            if (updateList.size() > 0) {
                zhglJsjdsPlanDao.batchUpdateJsjdsPlan(updateList);
            }
        } catch (Exception e) {
            logger.error("技术交底书计划更新失败,异常原因:{}", e.getMessage());
            return JsonResultUtil.fail("更新失败!");
        }
        return JsonResultUtil.success("操作成功", null);
    }
}
