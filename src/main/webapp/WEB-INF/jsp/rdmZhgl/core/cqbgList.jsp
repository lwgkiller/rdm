
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>技术交底书变更列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">提案名称: </span>
					<input class="mini-textbox" id="zlName" name="zlName" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCqbg('person')">变更人员信息</a>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCqbg('project')">变更关联项目</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="cqbgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/rdmZhgl/Cqbg/queryCqbg.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="flowType"  width="50" headerAlign="center" align="center" allowSort="true" renderer="onFlowTypeRenderer">变更类型</div>
			<div field="zlName"  width="100" headerAlign="center" align="center" allowSort="true">提案名称</div>
			<div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">状态</div>
			<div field="userName" headerAlign='center' align='center' width="40">变更申请人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">申请时间</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var cqbgListGrid=mini.get("cqbgListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var cqbgId = record.cqbgId;
        var instId = record.instId;
        var status = record.status;
        var flowType = record.flowType;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="cqbgDetail(\'' + cqbgId + '\',\''+flowType+'\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="cqbgEdit(\'' + cqbgId + '\',\'' + instId + '\',\'' + status + '\',\''+flowType+'\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="cqbgTask(\'' + record.myTaskId + '\',\''+flowType+'\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeCqbg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title="删除" onclick="removeCqbg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function onFlowTypeRenderer(e) {
        var record = e.record;
        var status = record.flowType;

        var arr = [ {'key' : 'project','value' : '变更项目'},
            {'key' : 'person','value' : '变更人员'}
        ];

        return $.formatItemValue(arr,status);
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

    function addCqbg(flowType) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/JSJDSBGSQ/start.do?flowType="+flowType;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cqbgListGrid) {
                    cqbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function cqbgEdit(cqbgId,instId,status,flowType) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&cqbgId=" + cqbgId+"&status="+status+ "&flowType=" + flowType;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cqbgListGrid) {
                    cqbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function cqbgDetail(cqbgId,flowType) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdmZhgl/Cqbg/editPage.do?action=" + action + "&cqbgId=" + cqbgId+ "&flowType=" + flowType;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cqbgListGrid) {
                    cqbgListGrid.reload()
                }
            }
        }, 1000);
    }
    function cqbgTask(taskId,flowType) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId+ "&flowType=" + flowType,
            async:false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId+ "&flowType=" + flowType;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if(!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (cqbgListGrid) {
                                cqbgListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    function removeCqbg(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = cqbgListGrid.getSelecteds();
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
                    rowIds.push(r.cqbgId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/Cqbg/deleteCqbg.do",
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
<redxun:gridScript gridId="cqbgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>