package com.redxun.info.core.dao;

import com.redxun.info.core.model.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 竞品图纸的数据传输层
 * @author douhongli
 * @date 2021年5月31日10:00:38
 */
@Repository
public interface InfoJptzDao {
    /**
     * 根据参数查询竞品图纸列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoJptz>
     * @author douhongli
     * @date 2021年5月31日11:51:51
     */
    List<InfoJptz> selectJptzList(Map<String, Object> param);

    /**
     * 根据参数查询竞品图纸列表总数
     * @param param 查询参数
     * @return Integer
     * @date 2021年5月31日11:52:04
     */
    Integer selectJptzCount(Map<String, Object> param);

    /**
     * 批量新增竞品图纸数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:00:25
     */
    Integer batchInsertJptz(List<InfoJptz> addList);

    /**
     * 批量更新竞品图纸数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:01:07
     */
    Integer batchUpdateJptz(List<InfoJptz> updateList);

    /**
     * 根据传入的集合id批量删除竞品图纸
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:18:13
     */
    Integer batchDeleteJptz(List<String> ids);

    /*===============竞品图纸样本文件================*/

    /**
     * 根据参数查询竞品图纸样本文件列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoJptzFiles>
     * @author douhongli
     * @date 2021年6月1日10:00:58
     */
    List<InfoJptzFiles> selectJptzFileList(Map<String, Object> param);

    /**
     * 根据参数查询竞品图纸样本文件总数
     * @param param 查询参数
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:03:12
     */
    Integer selectJptzFileCount(Map<String, Object> param);

    /**
     * 批量新增竞品图纸样本文件数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:08:33
     */
    Integer batchInsertJptzFile(List<InfoJptzFiles> addList);

    /**
     * 批量更新竞品图纸样本文件数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:08:58
     */
    Integer batchUpdateJptzFile(List<InfoJptzFiles> updateList);

    /**
     * 根据传入的集合id批量删除竞品图纸样本文件
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:09:09
     */
    Integer batchDeleteJptzFile(List<String> ids);

    /**
     * 根据外键删除竞品图纸样本文件
     * @param belongIds 外键
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:26:21
     */
    Integer deleteJptzFileBy(List<String> belongIds);
}
