
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>备件开发申请列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">备件品类: </span>
					<input id="bjType" name="bjType" class="mini-combobox" style="width:98%;"
						   textField="value" valueField="key" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true"
						   nullItemText="请选择..."
						   data="[
                            {'key' : '油品','value' : '油品'},{'key' : '钎杆','value' : '钎杆'}
                           ,{'key' : '滤芯','value' : '滤芯'},{'key' : '属具','value' : '属具'}
                           ,{'key' : '斗齿','value' : '斗齿'},{'key' : '液压件','value' : '液压件'}
                           ,{'key' : '动力件','value' : '动力件'},{'key' : '底盘件','value' : '底盘件'}
                           ,{'key' : '电器件','value' : '电器件'},{'key' : '橡胶件','value' : '橡胶件'}
                           ,{'key' : '其他','value' : '其他'}]"
					/>
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">原厂件物料编码: </span>
					<input class="mini-textbox" id="wlNumber" name="wlNumber" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">名称: </span>
					<input class="mini-textbox" id="bjName" name="bjName" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">提交人: </span>
					<input class="mini-textbox" id="applyName" name="applyName" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">服务工程工程师: </span>
					<input class="mini-textbox" id="fwName" name="fwName" />
				</li>
				<li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
					<input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
						   textField="value" valueField="key" emptyText="请选择..."
						   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
						   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '审批完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
					/>
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addKfsq()">新增</a>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportKfsq()">导出</a>
					<a id="addGcs" class="mini-button" style="margin-right: 5px" plain="true" onclick="addGcs()">添加品类工程师</a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="kfsqListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/rdm/core/Kfsq/queryKfsq.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;">操作</div>
			<div field="bjType"  width="50" headerAlign="center" align="center" allowSort="true">备件品类</div>
			<div field="fwName"  width="50" headerAlign="center" align="center" allowSort="true">服务工程师</div>
			<div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">状态</div>
			<div field="userName" headerAlign='center' align='center' width="40">创建人</div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
		</div>
	</div>
</div>
<form id="excelForm" action="${ctxPath}/rdm/core/Kfsq/exportKfsqList.do" method="post" target="excelIFrame">
	<input type="hidden" name="filter" id="filter"/>
</form>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var kfsqListGrid=mini.get("kfsqListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var kfsqId = record.kfsqId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="kfsqDetail(\'' + kfsqId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="kfsqEdit(\'' + kfsqId + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="kfsqTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="删除" onclick="removeKfsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' &&(currentUserNo=='admin')) {
            s += '<span  title="删除" onclick="removeKfsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
			{'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }

    $(function () {
        if(currentUserId!='1'){
            $("#addGcs").hide();
		}
        searchFrm();
    });


    function addKfsq() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/KFSQ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (kfsqListGrid) {
                    kfsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function kfsqEdit(kfsqId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&kfsqId=" + kfsqId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (kfsqListGrid) {
                    kfsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function kfsqDetail(kfsqId) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/Kfsq/editPage.do?action=" + action + "&kfsqId=" + kfsqId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (kfsqListGrid) {
                    kfsqListGrid.reload()
                }
            }
        }, 1000);
    }
    function kfsqTask(taskId) {
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
                            if (kfsqListGrid) {
                                kfsqListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    function removeKfsq(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = kfsqListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            }else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.kfsqId);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/Kfsq/deleteKfsq.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
    function exportKfsq() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

    function addGcs() {
        mini.open({
            title: "添加品类工程师",
            url: jsUseCtxPath + "/rdm/core/Kfsq/editGcs.do?",
            width: 750,
            height: 600,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy:function () {
                searchFrm();
            }
        });
    }
</script>
<redxun:gridScript gridId="kfsqListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>