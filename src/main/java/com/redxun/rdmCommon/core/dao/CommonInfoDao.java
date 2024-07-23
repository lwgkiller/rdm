package com.redxun.rdmCommon.core.dao;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zz
 */
@Repository
public interface CommonInfoDao {

    /**
     * 获取字典项值
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getDicValues(Map<String, Object> params);

    /**
     * 获取人员和身份证关系 身份证不为空
     *
     * @return list
     */
    List<Map<String, String>> getUserIdAndCerNo();

    /**
     * 根据用户姓名查询用户信息
     *
     * @param params
     * @return list
     */
    List<JSONObject> getUserInfoByUserName(Map<String, Object> params);

    /**
     * 根据部门名称 获取部门信息
     *
     * @param deptName
     * @return list
     */
    List<JSONObject> getDeptInfoByDeptName(String deptName);

    /**
     * 根据用户id获取部门id
     */
    String getDeptIdByUserId(String userId);

    // 根据一组人id查询所属主部门
    List<JSONObject> getDeptInfoByUserIds(Map<String, Object> params);

    /**
     * 获取用户组织
     *
     * @param userId
     * @return list
     */
    List<JSONObject> getUserOrg(String userId);

    /**
     * 根据组织名称和组织分类获取组织信息
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getGroupInfo(JSONObject jsonObject);

    /**
     * 根据部门名称和用户名称获取用户id
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getUserIdByDeptAndUserName(JSONObject jsonObject);

    /**
     * 获取本部门的拥有某个角色的人员
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getDeptRoleUsers(JSONObject jsonObject);

    /**
     * 获取某组中的用户
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getGroupUsers(JSONObject jsonObject);

    /**
     * 获取某用户所在哪个些组里面
     *
     * @param jsonObject
     * @return
     */
    List<JSONObject> getBelongGroups(JSONObject jsonObject);

    /**
     * 获取字典中文名称
     *
     * @param jsonObject
     * @return
     */
    JSONObject getDicValue(JSONObject jsonObject);

    /**
     * 获取字典key值
     *
     * @param jsonObject
     * @return
     */
    JSONObject getDicKeyByValue(JSONObject jsonObject);

    /**
     * 根据部门获取所有用户
     *
     * @param deptId
     * @return list
     */
    List<String> getUsersByDeptId(String deptId);

    /**
     * 获取部门名称
     *
     * @param groupId
     * @return
     */
    JSONObject getGroupInfoById(String groupId);

    // 查询挖掘机械研究院下所有的部门
    List<JSONObject> queryDeptUnderJSZX(String jszxId);

    /**
     * 根据字典类型和字典key值获取字典信息
     *
     * @param paramJson:dicType:字典类型，dicKey:字典值
     * @return
     */
    JSONObject getDicInfo(JSONObject paramJson);

    // 通过一组用户（userIds）查询所属部门的部门负责人
    List<JSONObject> queryDeptRespByUserIds(JSONObject param);

    // 通过一个用户（userId）查询所属部门的部门负责人
    List<JSONObject> queryDeptRespByUserId(String userId);

    // 通过部门id（deptId）查询部门名称（deptname）
    JSONObject queryDept(Map<String, Object> param);

    // 通过一组deptIds查询部门信息
    List<JSONObject> queryDeptByIds(Map<String, Object> params);

    // 根据deptId和REL_TYPE_KEY_查询USER_ID_和FULLNAME_
    List<Map<String, String>> queryUserByGroupId(Map<String, Object> params);

    // 根据groupName和REL_TYPE_KEY_查询USER_ID_和FULLNAME_
    List<Map<String, String>> queryUserByGroupName(Map<String, Object> params);

    //根据用户ID查询部门信息
    JSONObject queryDeptByUserId(Map<String, Object> params);

    //根据一组用户ID查询部门信息
    List<JSONObject> batchDeptByUserId(Map<String, Object> params);

    //..根据参数获取我最近的访问菜单的记录
    List<JSONObject> getMyMenuRecent(JSONObject params);

    //..添加我的近期访问菜单记录
    void insertMyMenuRecent(JSONObject jsonObject);

    //..更新我的近期访问菜单记录
    void updateMyMenuRecent(JSONObject jsonObject);

    //..处理菜单，保留近期访问的X条记录
    void processMyMenuRecent(JSONObject params);
    /**
     * 存储发送消息
     *
     * @param paramJson
     * */
    void insertDingMessage(JSONObject paramJson);
}
