<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术人员加班审批列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/delayApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申报人: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申报部门: </span>
                    <input class="mini-textbox" name="deptName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                      style="width:auto">申请单流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="delButton" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeApply()">删除</a>
                    <a id="deptApply" class="mini-button " style="margin-left: 10px;" plain="true"
                       onclick="doDeptApply()">新增申请</a>
                    <a id="exportDelayApply" class="mini-button" style="margin-left: 5px" plain="true" onclick="exportDelayApply()">导出</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto">申请单号: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">申请时间 从 </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input name="apply_endTime"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:100px"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<form id="excelForm" action=""
      method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex"/>
    <input type="hidden" name="pageSize"/>
    <input type="hidden" name="filter" id="filter">
</form>
<iframe id="excelUnIFrame" name="excelIFrame" style="display: none;"></iframe>


<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/delayApply/queryList.do"
         idField="applyId" sortOrder="desc" sortField="applyTime"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="id" sortField="id" width="100" headerAlign="center" align="center" allowSort="true">申请单号</div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true">
                申报人
            </div>
            <div field="deptName" sortField="deptName" width="60" headerAlign="center" align="center" allowSort="true">
                申报部门
            </div>
            <div field="title" sortField="title" width="120" headerAlign="center" align="center" allowSort="true">
                申报标题
            </div>

            <div field="instStatus" width="60" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false">流程状态
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
                 headerAlign="center" allowSort="false">当前处理人
            </div>
            <div field="currentProcessTask" width="100" align="center"
                 headerAlign="center" allowSort="false">当前待办任务
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true">申报时间
            </div>
            <div field="remark" width="80" headerAlign="center" align="center" allowSort="false">备注</div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var permission = ${permission};
    var techAdmin = ${techAdmin};
    var currentMainGroupId = ${currentMainGroupId};
    if (!permission && !techAdmin) {
        mini.get("deptApply").setEnabled(false);
        mini.get('delButton').setEnabled(false);
    }
    if(currentUserId =='1')
    {
        mini.get("deptApply").setEnabled(true);
        mini.get('delButton').setEnabled(true);
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var applyType = record.applyType;
        var s = '<span  title="明细" onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\',\'' + applyType + '\')">明细</span>';
        if (record.instStatus == 'DRAFTED') {
            s += '<span  title="编辑" onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\',\'' + applyType + '\')">编辑</span>';

        } else if (record.currentProcessTask && record.currentProcessTask == '人资专员填写') {
            if (record.deptId == currentMainGroupId) {
                s += '<span  title="编辑" onclick="doApplyTask(\'' + record.taskId + '\',\'' + applyType + '\')">编辑</span>';
            } else {
                s += '<span  title="编辑" style="color: silver">编辑</span>';
            }
        } else {
            if (record.processTask) {
                s += '<span  title="办理" onclick="doApplyTask(\'' + record.taskId + '\',\'' + applyType + '\')">办理</span>';
            } else {
                s += '<span  title="办理" style="color: silver">办理</span>';
            }
        }
        if ((record.CREATE_BY_ == currentUserId || currentUserId == '1') && (record.instStatus == 'DRAFTED' || record.instStatus == 'DISCARD_END')) {
            s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        } else {
            s += '<span  title="删除" style="color: silver">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    //新增提报审批流程
    function doDeptApply() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/JBSQFlow/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload();
                }
            }
        }, 1000);
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
