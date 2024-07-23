package com.redxun.strategicPlanning.core.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redxun.strategicPlanning.core.domain.ZlghAnnualData;
import com.redxun.strategicPlanning.core.domain.ZlghKpi;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ZlghKPIDao {

    List<JSONObject> queryKPIList(Map<String, Object> param);

    int countKPIList(Map<String, Object> param);

    List<ZlghKpi> queryKPITest(Map<String, Object> param);

    List<ZlghAnnualData> queryAnnualData(@Param("kpiId") String KPIid);

    List<JSONObject> queryKpiByIdAndFolk(Map<String, Object> param);

    void createKpi(Map<String, Object> param);

    void updateKpi(@Param("list") List<JSONObject> list);

    void insertE(@Param("selE") List<JSONObject> list);

    void deleteKpi(@Param("id") String id);

    void createKpiAu(Map<String, Object> param);

    List<JSONObject> selectSaveData();

    void insertUpData(@Param("list")List<JSONObject> list);

/*=============================我是华丽的分割线=============================*/

    List<JSONObject> queryKPINameList(Map<String, Object> param);

    int countKPINameList(Map<String, Object> param);

    void updateKpiName(Map<String, Object> param);

    void createKpiName(Map<String, Object> param);

    void deleteKpiName(@Param("id") String id);

    /**
     * 查询所有的绩效数据
     * @param param 查询参数对象
     * @return List<ZlghAnnualData>
     */
    List<ZlghKpi> selectZlghAnnualDataList(Map<String, Object> param);

    /**
     * 根据参数查询kpi data
     * @param param 参数
     * @return List
     */
    List<ZlghAnnualData> selectAnnualData(Map<String, Object> param);

    /**
     * 批量新增KPI data
     * @param collect 新增集合
     * @return Integer
     */
    Integer batchInsert(List<ZlghAnnualData> collect);

    /**
     * 批量更新KPI data
     * @param collect 更新集合
     * @return Integer
     */
    Integer batchUpdate(List<ZlghAnnualData> collect);

    /**
     * 根据 id查询数据
     * @param id id
     * @return ZlghAnnualData
     */
    ZlghAnnualData selectById(@Param("id") String id);

    /**
     * 根据传入的集合id批量删除
     * @param collect id集合
     * @return Integer
     */
    Integer batchDelete(List<String> collect);
}
