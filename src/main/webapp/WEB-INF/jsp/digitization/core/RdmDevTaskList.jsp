
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>RDM功能开发列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/digitization/rdmDevTaskList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请单号: </span>
                    <input id="applyNum" name="applyNum" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input id="applyUserName" name="applyUserName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">申请部门:</span>
                    <input id="applyDeptId" name="applyDeptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:160px;height:34px"
                           allowinput="false" label="部门" textname="applyDeptName" single="true" required="false" initlogindep="false"
                    />
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">开发类型: </span>
                    <input id="devType" name="devType" class="mini-combobox" style="width:120px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '新功能开发','value' : 'add'},{'key' : '优化功能开发','value' : 'modify'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请说明: </span>
                    <input class="mini-textbox" id="applyName" name="applyName" />
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">开发负责人: </span>
                    <input id="devRespUserName" name="devRespUserName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程节点: </span>
                    <input id="currentProcessTask" name="currentProcessTask" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},
							   {'key' : 'DISCARD_END','value' : '申请作废'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请时间：</span>
                    <input id="applyTimeBegin" name="applyTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="applyTimeEnd" name="applyTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">计划完成时间：</span>
                    <input id="planEndTimeBegin" name="planEndTimeBegin" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至：</span>
                    <input id="planEndTimeEnd" name="planEndTimeEnd" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addApply()">新增申请</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="devTaskListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/devTask/core/getDevList.do" idField="applyId"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="40px"></div>
            <div name="action" cellCls="actionIcons" width="110px" headerAlign="center" align="left" renderer="onActionRenderer" cellStyle="padding:0">操作</div>
            <div field="applyNum"  sortField="applyNum" width="110px" headerAlign="center" align="center" allowSort="true">申请单号</div>
            <div field="applyUserDeptName"  sortField="applyUserDeptName" width="110px" headerAlign="center" align="center" allowSort="true">申请人部门</div>
            <div field="applyUserName" sortField="applyUserName" width="60px" headerAlign="center" align="center" allowSort="true">申请人</div>
            <div field="devType"   width="80px" headerAlign="center" align="center" renderer="onTypeRenderer">开发类型</div>
            <div field="applyName"  width="320px" headerAlign="center" align="center" allowSort="false">申请说明</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="80px" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">提交时间</div>
            <div field="planStartTime" sortField="planStartTime" width="80px" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">计划开始<br>时间</div>
            <div field="planEndTime" sortField="planEndTime" width="80px" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">计划结束<br>时间</div>
            <div field="gzlEvaluate" sortField="gzlEvaluate" width="70px" headerAlign="center" align="center" allowSort="true">预估工作<br>(人天)</div>
            <div field="devRespUserName"  sortField="devRespUserName"  width="70px" align="center" headerAlign="center" allowSort="false">开发责任人</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="70px" align="center" headerAlign="center" allowSort="false">流程处理人</div>
            <div field="currentProcessTask" width="120px" align="center" headerAlign="center">流程节点</div>
            <div field="status"  width="70px" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var devTaskListGrid=mini.get("devTaskListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.applyId;
        var instId=record.instId;
        var s = '<span  title="查看" onclick="devTaskDetail(\'' + applyId +'\',\''+record.status+ '\')">查看</span>';
        if(record.status=='DRAFTED') {
            s+='<span  title="编辑" onclick="devTaskEdit(\'' + applyId +'\',\''+instId+ '\')">编辑</span>';
        } else {
            var currentProcessUserId=record.currentProcessUserId;
            // 添加上管理员
            if((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1)||currentUserNo=='admin') {
                s+='<span  title="办理" onclick="devTaskDo(\'' + record.taskId + '\')">办理</span>';
            }
        }

        var status=record.status;
        if(currentUserNo!='admin') {
            if (status == 'DRAFTED' && currentUserId ==record.CREATE_BY_) {
                s+='<span  title="删除" onclick="removeApply('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
            }
        } else {
            s+='<span  title="删除" onclick="removeApply('+JSON.stringify(record).replace(/"/g, '&quot;')+')">删除</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '申请作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    function devStatusRender(e) {
        var record = e.record;
        var devStatus = record.devStatus;

        var arr = [ {'key' : '待开发','value' : '待开发','css' : 'red'},
            {'key' : '开发中','value' : '开发中','css' : 'orange'},
            {'key' : '已上线','value' : '已上线','css' : 'blue'}
        ];

        return $.formatItemValue(arr,devStatus);
    }

    function onTypeRenderer(e) {
        var record = e.record;
        var devType = record.devType;
        if(devType=='add') {
            return "新功能开发";
        }
        if(devType=='modify') {
            return "优化功能开发";
        }
    }
</script>
<redxun:gridScript gridId="devTaskListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>