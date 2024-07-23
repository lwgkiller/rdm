package com.redxun.xcmgbudget.core.util;

/**
 * @author zz
 */
public interface ConstantUtil {
    String DESK_HOME = "deskHome";
    String PROJECT_PROGRESS_TYPE = "projectTypeCondition";
    String PERSON_PROJECT_LIST = "personProjectProgress";
    String ADMIN = "admin";
    String SUCCESS = "success";
    String LEVEL_A = "A";
    String LEVEL_B = "B";
    String LEVEL_C = "C";
    String NULL = "null";
    String PDM_APPROVAL_FINAL = "已发布";
    int SHOW_NUM = 10;
    String PREVIEW = "preview";
    String DOWNLOAD = "download";

    // 季度的编号
    String QUARTER1_NO = "01";
    String QUARTER2_NO = "02";
    String QUARTER3_NO = "03";
    String QUARTER4_NO = "04";
    String[] QUARTER_NO_LIST = {QUARTER1_NO, QUARTER2_NO, QUARTER3_NO, QUARTER4_NO};

    // 初始还是实际季度
    String QUARTER_SCENE_ORIGINAL = "original";
    String QUARTER_SCENE_ACTUAL = "actual";

    // 表格数据的状态
    String STATE_ADD = "added";
    String STATE_MODIFIED = "modified";
    String STATE_REMOVED = "removed";

    // 项目评审配置的开关状态
    String GRADE_CONFIG_ON = "1";
    String GRADE_CONFIG_OFF = "0";
    // 评审配置的场景（立项结项）
    String GRADE_SCENE_START = "start";
    String GRADE_SCENE_END = "end";

    long ONE_DAY_MILLSECONDS = 24 * 60 * 60 * 1000;
    // 延迟一天扣分1分
    double DEDUCT_SCORE_ONEDAY = 1.0;

    // 重大项目季度完成情报提报截止时间
    String ENDTIME_QUARTER_ONE = "-04-05 00:00:00";
    String ENDTIME_QUARTER_TWO = "-07-05 00:00:00";
    String ENDTIME_QUARTER_THREE = "-10-05 00:00:00";
    String ENDTIME_QUARTER_FOUR = "-12-12 00:00:00";

}
