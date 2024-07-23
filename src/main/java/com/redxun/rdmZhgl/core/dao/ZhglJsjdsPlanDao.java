package com.redxun.rdmZhgl.core.dao;

import com.redxun.rdmZhgl.core.domain.ZhglJsjdsPlan;
import com.redxun.strategicPlanning.core.domain.ZlghHdnf;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 综合管理-技术交底书计划展示的数据传输层
 * @author douhongli
 * @date 2021年6月9日16:32:50
 */
@Repository
public interface ZhglJsjdsPlanDao {

    /**
     * 根据id 查询技术交底书计划数据
     * @param id 技术交底书计划id
     * @return ZhglJsjdsPlan
     * @author douhongli
     * @date 2021年6月9日16:34:18
     */
    ZhglJsjdsPlan queryJsjdsPlan(@Param("id") String id);

    /**
     * 技术交底书计划 - 根据参数查询总条数
     * @param param 参数
     * @return Integer
     * @author douhongli
     * @since 2021年6月9日17:52:38
     */
    Integer selectJsjdsPlanCount(Map<String, Object> param);

    /**
     * 根据参数查询所有的技术交底书计划列表,为空也要查询
     * @param param 查询参数
     * @return List<ZhglJsjdsPlan>
     * @author douhongli
     * @date 2021年6月9日16:35:55
     */
    List<ZhglJsjdsPlan> selectJsjdsPlanList(Map<String, Object> param);

    /**
     * 批量新增技术交底书计划数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月9日16:39:17
     */
    Integer batchInsertJsjdsPlan(List<ZhglJsjdsPlan> addList);

    /**
     * 批量更新技术交底书计划数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月9日17:52:58
     */
    Integer batchUpdateJsjdsPlan(List<ZhglJsjdsPlan> updateList);
}
