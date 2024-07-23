
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>试验任务列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/drbfm/testTaskList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">任务编号: </span>
                    <input id="testNumber" name="testNumber" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="jixing" name="jixing" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">验证责任部门: </span>
                    <input id="respDeptName" name="respDeptName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">验证责任人: </span>
                    <input id="testRespUserName" name="testRespUserName" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '已完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">验证类型: </span>
                    <input id="testType" name="testType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '专项测试','value' : '专项测试'},{'key' : '常规附带','value' : '常规附带'}]"
                    />
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()">删除</a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件/接口名称: </span>
                        <input class="mini-textbox" id="structName" name="structName" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件负责人: </span>
                        <input id="analyseUserName" name="analyseUserName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">验证方式: </span>
                        <input id="abilityName" name="abilityName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">风险分析总项目名称: </span>
                        <input class="mini-textbox" id="analyseName" name="analyseName" />
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 99%;">
    <div id="testTaskListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/drbfm/testTask/getTestTaskList.do?doPage=true" idField="applyId"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="testNumber" width="140" headerAlign="center" align="center" allowSort="true">验证任务编号</div>
            <div field="testType" width="70" headerAlign="center" align="center" allowSort="true">验证类型</div>
            <div field="abilityName" width="70" headerAlign="center" align="center" allowSort="true">验证方式</div>
            <div field="structName" width="100" headerAlign="center" align="center" allowSort="true">部件/接口名称</div>
            <div field="analyseUserName" width="70" headerAlign="center" align="center" allowSort="true">部件负责人</div>
            <div field="jixing" width="70" headerAlign="center" align="center" allowSort="true">设计型号</div>
            <div field="analyseName" width="100" headerAlign="center" align="center" allowSort="true">风险分析总项目名称</div>
            <div field="respDeptName" width="100" headerAlign="center" align="center" allowSort="true">验证责任部门</div>
            <div field="testRespUserName" width="80" headerAlign="center" align="center" allowSort="true">验证责任人</div>
            <div field="instStatus"  width="50" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer">流程状态</div>
            <div field="currentProcessUser"  sortField="currentProcessUser"  width="70" align="center" headerAlign="center" allowSort="false">当前处理人</div>
            <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程节点</div>
            <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var testTaskListGrid=mini.get("testTaskListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";


    //行功能按钮

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var testType = record.testType;
        var s = '';
        if(testType=='专项测试') {
            var instId = record.instId;
            var status = record.status;
            var currentProcessUserId = record.currentProcessUserId;
            if (status != 'DRAFTED') {
                s += '<span  title="查看" onclick="testTaskDetail(\'' + applyId + '\',\'' + status+'\',\''+testType+ '\')">查看</span>';
            }
            if (record.status == 'DRAFTED') {
                s += '<span  title="编辑" onclick="testTaskEdit(\'' + applyId + '\',\'' + instId +'\',\''+testType+ '\')">编辑</span>';
            }

            if(currentProcessUserId && currentProcessUserId.indexOf(currentUserId) !=-1){
                s += '<span  title="办理" onclick="testTaskDo(\'' + record.taskId + '\')">办理</span>';

            }

            if ((status == 'DISCARD_END' || status == 'DRAFTED') && currentUserId ==record.CREATE_BY_) {
                s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        } else if(testType=='常规附带') {
            var createBy = record.CREATE_BY_;
            s += '<span  title="查看" onclick="testTaskDetail(\'' + applyId + '\',\'\',\''+testType+ '\')">查看</span>';
            if (currentUserId ==createBy) {
                s += '<span  title="编辑" onclick="testTaskEdit(\'' + applyId + '\',\'\',\''+testType+ '\')">编辑</span>';
                s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
            }
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '已完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }
</script>
<redxun:gridScript gridId="testTaskListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>