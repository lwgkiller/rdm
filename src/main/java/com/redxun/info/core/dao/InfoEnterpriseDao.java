package com.redxun.info.core.dao;

import com.redxun.info.core.model.InfoCpybFiles;
import com.redxun.info.core.model.InfoGngys;
import com.redxun.info.core.model.InfoGwgys;
import com.redxun.info.core.model.InfoGyslxr;
import com.redxun.strategicPlanning.core.domain.ZlghHdnf;
import com.redxun.strategicPlanning.core.domain.dto.ZlghDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 企业服务商展示的数据传输层
 * @author douhongli
 * @date 2021年5月31日10:00:38
 */
@Repository
public interface InfoEnterpriseDao {
    /**
     * 根据参数查询国内企业供应商列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoGngys>
     * @author douhongli
     * @date 2021年5月31日11:51:51
     */
    List<InfoGngys> selectGnList(Map<String, Object> param);

    /**
     * 根据参数查询国内企业供应商列表总数
     * @param param 查询参数
     * @return Integer
     * @date 2021年5月31日11:52:04
     */
    Integer selectGnCount(Map<String, Object> param);

    /**
     * 批量新增国内企业供应方数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:00:25
     */
    Integer batchInsertGn(List<InfoGngys> addList);

    /**
     * 批量更新国内企业供应方数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:01:07
     */
    Integer batchUpdateGn(List<InfoGngys> updateList);

    /**
     * 根据传入的集合id批量删除国内企业供应方
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:18:13
     */
    Integer batchDeleteGn(List<String> ids);

    /*===============国外企业供应方================*/

    /**
     * 根据参数查询国外企业供应商列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoGngys>
     * @author douhongli
     * @date 2021年5月31日11:51:51
     */
    List<InfoGwgys> selectGwList(Map<String, Object> param);

    /**
     * 根据参数查询国外企业供应商列表总数
     * @param param 查询参数
     * @return Integer
     * @date 2021年5月31日11:52:04
     */
    Integer selectGwCount(Map<String, Object> param);

    /**
     * 批量新增国外企业供应方数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:00:25
     */
    Integer batchInsertGw(List<InfoGwgys> addList);

    /**
     * 批量更新国外企业供应方数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:01:07
     */
    Integer batchUpdateGw(List<InfoGwgys> updateList);

    /**
     * 根据传入的集合id批量删除国外企业供应方
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年5月31日14:18:13
     */
    Integer batchDeleteGw(List<String> ids);

    /*===============国内外企业供应方联系人================*/

    /**
     * 根据参数查询国内外企业供应商联系人列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoGyslxr>
     * @author douhongli
     * @date 2021年6月1日09:07:01
     */
    List<InfoGyslxr> selectEnterpriseContactsList(Map<String, Object> param);

    /**
     * 根据参数查询国内外企业供应商联系人总数
     * @param param 查询参数
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日09:07:01
     */
    Integer selectEnterpriseContactsCount(Map<String, Object> param);

    /**
     * 批量新增企业供应商联系人数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日09:54:09
     */
    Integer batchInsertEnterpriseContacts(List<InfoGyslxr> addList);

    /**
     * 批量更新企业供应商联系人数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日09:54:14
     */
    Integer batchUpdateEnterpriseContacts(List<InfoGyslxr> updateList);

    /**
     * 根据传入的集合id批量删除企业供应商联系人
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日09:54:19
     */
    Integer batchDeleteEnterpriseContacts(List<String> ids);

    /**
     * 根据外键删除国内外企业供应商联系人
     * @param belongIds 外键
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:26:21
     */
    Integer deleteEnterpriseContactsBy(List<String> belongIds);

    /*===============国内外企业供应方样本文件================*/

    /**
     * 根据参数查询国内外企业供应商样本文件列表,为空也要查询
     * @param param 查询参数
     * @return List<InfoCpybFiles>
     * @author douhongli
     * @date 2021年6月1日10:00:58
     */
    List<InfoCpybFiles> selectEnterpriseFileList(Map<String, Object> param);

    /**
     * 根据参数查询国内外企业供应商样本文件总数
     * @param param 查询参数
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:03:12
     */
    Integer selectEnterpriseFileCount(Map<String, Object> param);

    /**
     * 批量新增企业供应商样本文件数据
     * @param addList 需要新增的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:08:33
     */
    Integer batchInsertEnterpriseFile(List<InfoCpybFiles> addList);

    /**
     * 批量更新企业供应商样本文件数据
     * @param updateList 需要更新的数据
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:08:58
     */
    Integer batchUpdateEnterpriseFile(List<InfoCpybFiles> updateList);

    /**
     * 根据传入的集合id批量删除企业供应商样本文件
     * @param ids id集合
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:09:09
     */
    Integer batchDeleteEnterpriseFile(List<String> ids);

    /**
     * 根据外键删除国内外企业供应商样本文件
     * @param belongIds 外键
     * @return Integer
     * @author douhongli
     * @date 2021年6月1日10:26:21
     */
    Integer deleteEnterpriseFileBy(List<String> belongIds);
}
