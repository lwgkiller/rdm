package com.redxun.strategicPlanning.core.dao;

import com.redxun.strategicPlanning.core.domain.ZlghZljc;
import com.redxun.strategicPlanning.core.domain.ZlghZlkt;
import com.redxun.strategicPlanning.core.domain.ZlghZyhd;
import com.redxun.strategicPlanning.core.domain.dto.ZlghZyhdDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 战略规划关于战略举措，战略课题，主要活动的 数据传输层
 * @author douhongli
 */
@Repository
public interface ZlghDataDao {
    /**
     * 战略举措 - 根据参数查询列表数据
     * @param param 参数
     * @return List<ZlghZljc>
     */
    List<ZlghZljc> selectZljcList(Map<String, Object> param);

    /**
     * 查询所有战略举措
     * @return List
     * @author douhongli
     * @date 2021年5月26日13:57:39
     */
    List<ZlghZljc> listZljcText();

    /**
     * 战略举措 - 根据参数查询总条数
     * @param param 参数
     * @return Integer
     */
    Integer selectZljcCount(Map<String, Object> param);
    /**
     * 战略举措 - 根据id查询数据
     * @param id 参数
     * @return List<ZlghZljc>
     */
    ZlghZljc selectZljcById(@Param("id") String id);

    /**
     * 批量新增战略举措
     * @param zljcList 新增集合
     * @return Integer
     */
    Integer batchInsertZljc(List<ZlghZljc> zljcList);

    /**
     * 批量更新战略举措
     * @param zljcList 新增集合
     * @return Integer
     */
    Integer batchUpdateZljc(List<ZlghZljc> zljcList);

    /**
     * 根据传入的集合id批量删除战略举措
     * @param ids id集合
     * @return Integer
     */
    Integer batchDeleteZljc(List<String> ids);

    /**
     * 战略课题 - 根据参数查询列表数据
     * @param param 参数
     * @return List<ZlghZljc>
     */
    List<ZlghZljc> selectZlktList(Map<String, Object> param);

    /**
     * 战略课题列表-根据战略举措id查询
     * @param zljcIds 战略举措id
     * @return List<ZlghZlkt>
     * @author douhongli
     * @date 2021年5月27日10:32:48
     */
    List<ZlghZlkt> selectZlktListByZljcId(List<String> zljcIds);

    /**
     * 根据战略举措id查询战略课题列表数据
     * @return List
     * @author douhongli
     * @date 2021年5月26日17:55:33
     */
    List<ZlghZlkt> listZlktByZljcId(@Param("zljcId") String zljcId);

    /**
     * 战略课题 - 根据参数查询总条数
     * @param param 参数
     * @return Integer
     */
    Integer selectZlktCount(Map<String, Object> param);

    /**
     * 战略课题 - 根据id查询数据
     * @param id 参数
     * @return List<ZlghZljc>
     */
    ZlghZljc selectZlktById(@Param("id") String id);

    /**
     * 批量新增战略课题
     * @param zlktList 新增集合
     * @return Integer
     */
    Integer batchInsertZlkt(List<ZlghZlkt> zlktList);

    /**
     * 批量更新战略课题
     * @param zlktList 新增集合
     * @return Integer
     */
    Integer batchUpdateZlkt(List<ZlghZlkt> zlktList);

    /**
     * 根据传入的集合id批量删除战略课题
     * @param ids id集合
     * @return Integer
     */
    Integer batchDeleteZlkt(List<String> ids);

    /**
     * 主要活动 - 根据参数查询列表数据
     * @param param 参数
     * @return List<ZlghZyhdDto>
     */
    List<ZlghZyhdDto> selectZyhdList(Map<String, Object> param);

    /**
     * 根据战略课题ids,查询主要活动列表
     * @param zlktIds 战略课题ids
     * @return List<ZlghZyhd>
     *
     * @author douhongli
     * @date 2021年5月27日10:12:21
     */
    List<ZlghZyhd> selectZyhdListByZlktId(List<String> zlktIds);

    /**
     * 主要活动 - 根据参数查询总条数
     * @param param 参数
     * @return Integer
     */
    Integer selectZyhdCount(Map<String, Object> param);

    /**
     * 批量新增主要活动
     * @param zlktList 新增集合
     * @return Integer
     */
    Integer batchInsertZyhd(List<ZlghZyhd> zlktList);

    /**
     * 批量更新主要活动
     * @param zlktList 新增集合
     * @return Integer
     */
    Integer batchUpdateZyhd(List<ZlghZyhd> zlktList);

    /**
     * 根据传入的集合id批量删除主要活动
     * @param ids id集合
     * @return Integer
     */
    Integer batchDeleteZyhd(List<String> ids);

}
