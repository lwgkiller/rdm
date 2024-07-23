package com.redxun.strategicPlanning.core.dao;

import com.redxun.strategicPlanning.core.domain.ZlghHdnf;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 战略规划整体展示的服务层
 * @author douhongli
 * @date 2021年5月27日17:06:50
 */
@Repository
public interface ZlghPlanningDao {
    /**
     * 根据参数查询所有的活动年份列表,为空也要查询
     * @param param 查询参数
     * @return List<ZlghDto>
     * @author douhongli
     * @date 2021年5月27日17:08:17
     */
    List<ZlghDto> selectZlghList(Map<String, Object> param);

    /**
     * 根据条件查询活动年份数据
     * @param param 查询参数
     * @return List<ZlghHdnf>
     * @author douhongli
     * @date 2021年5月27日17:08:40
     */
    List<ZlghHdnf> selectZlghHdnfList(Map<String, Object> param);

    /**
     * 批量新增活动年份数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月27日17:09:30
     */
    Integer batchInsertZlghHdnf(List<ZlghHdnf> addList);

    /**
     * 批量更新活动年份数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月27日17:10:17
     */
    Integer batchUpdateZlghHdnf(List<ZlghHdnf> updateList);

    /**
     * 根据id 查询活动年份数据
     * @param id 活动年份id
     * @return ZlghHdnf
     * @author douhongli
     * @date 2021年5月27日17:17:15
     */
    ZlghHdnf queryZlghHdnf(@Param("id") String id);
}
