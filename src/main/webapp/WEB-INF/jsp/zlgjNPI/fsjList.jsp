
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>新品附属件配置</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">产品型号: </span>
					<input class="mini-textbox" id="cpxh" name="cpxh" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">整机编号: </span>
					<input class="mini-textbox" id="zjbh" name="zjbh" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addFsj()">新增</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="fsjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/zlgjNPI/core/Fsj/queryFsj.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="deptName"  width="60" headerAlign="center" align="center" allowSort="true">责任部门</div>
			<div field="pfbz"  width="30" headerAlign="center" align="center" allowSort="true">排放标准</div>
			<div field="cpxh"  width="70" headerAlign="center" align="center" allowSort="true">产品型号</div>
			<div field="zjbh"  width="70" headerAlign="center" align="center" allowSort="true">整机编号</div>
			<div field="scpch"  width="70" headerAlign="center" align="center" allowSort="true">生产批次号</div>
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
    var fsjListGrid=mini.get("fsjListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var fsjId = record.fsjId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="fsjDetail(\'' + fsjId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="fsjEdit(\'' + fsjId + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="fsjTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeFsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title="删除" onclick="removeFsj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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
    $(function () {
        searchFrm();
    });


    function addFsj() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/XPFSJ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (fsjListGrid) {
                    fsjListGrid.reload()
                }
            }
        }, 1000);
    }

    function fsjEdit(fsjId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&fsjId=" + fsjId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (fsjListGrid) {
                    fsjListGrid.reload()
                }
            }
        }, 1000);
    }

    function fsjDetail(fsjId) {
        var action = "detail";
        var url = jsUseCtxPath + "/zlgjNPI/core/Fsj/editPage.do?action=" + action + "&fsjId=" + fsjId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (fsjListGrid) {
                    fsjListGrid.reload()
                }
            }
        }, 1000);
    }
    function fsjTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async:false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if(!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (fsjListGrid) {
                                fsjListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    function removeFsj(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = fsjListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.fsjId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/zlgjNPI/core/Fsj/deleteFsj.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="fsjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>