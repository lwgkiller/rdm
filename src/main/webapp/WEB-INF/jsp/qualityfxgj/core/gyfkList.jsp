
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>改进计划列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/qualityfxgj/gyfkList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">责任人: </span>
					<input class="mini-textbox" id="repPerson" name="repPerson" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">责任部门: </span>
					<input class="mini-textbox" id="repDep" name="repDep" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addGyfk()">新增</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="gyfkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/qualityfxgj/core/Gyfk/queryGyfk.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="repPerson"  width="50" headerAlign="center" align="center" allowSort="true">责任人</div>
			<div field="repDep"  width="70" headerAlign="center" align="center" allowSort="true">责任部门</div>
			<div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">状态</div>
			<div field="userName" headerAlign='center' align='center' width="40">创建人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var gyfkListGrid=mini.get("gyfkListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var gyfkId = record.gyfkId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="gyfkDetail(\'' + gyfkId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="gyfkEdit(\'' + gyfkId + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="gyfkTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeGyfk(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title="删除" onclick="removeGyfk(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }
</script>
<redxun:gridScript gridId="gyfkListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>