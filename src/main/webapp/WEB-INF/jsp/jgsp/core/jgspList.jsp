<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>保密协议列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="sqr" name="sqr" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门: </span>
                    <input class="mini-textbox" id="sqbm" name="sqbm" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">科室: </span>
                    <input id="ks" name="ks" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[
                            {'key' : '液压','value' : '液压'},{'key' : '覆盖件','value' : '覆盖件'}
                           ,{'key' : '动力','value' : '动力'},{'key' : '底盘','value' : '底盘'}
                           ,{'key' : '电气','value' : '电气'},{'key' : '工装','value' : '工装'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">是否紧急: </span>
                    <input name="sfjj" class="mini-combobox" style="width:150px;"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : 'true'},{'key' : '否','value' : 'false'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJgsp()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jgspListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/jgsp/queryJgsp.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="15"></div>
            <div type="indexcolumn" width="15" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="sfjj" width="30" headerAlign="center" align="center" allowSort="false" renderer="urgentRender">是否紧急</div>
            <div field="applyName" width="30" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="deptName" width="40" headerAlign="center" align="center" allowSort="false">申请部门</div>
            <div field="ks" width="20" headerAlign="center" align="center" allowSort="false">科室</div>
            <div field="yyjx" width="50" headerAlign="center" align="center" allowSort="false">应用机型</div>
            <div field="gys" width="50" headerAlign="center" align="center" allowSort="false">供应商</div>
            <div field="wlms" width="80" headerAlign="center" align="center" allowSort="false">物料描述</div>
            <div field="taskName" width="40" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" width="30" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" width="40" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var jgspId = "${jgspId}";
    var jgspListGrid=mini.get("jgspListGrid");

    function onActionRenderer(e) {
        var record = e.record;
        var jgspId = record.jgspId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="明细" onclick="jgspDetail(\'' + jgspId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="jgspEdit(\'' + jgspId + '\',\'' + instId + '\')">编辑</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="办理" onclick="jgspTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED' || status == 'DISCARD_END') {
            s += '<span  title="删除" onclick="removeJgsp(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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
    function urgentRender(e) {
        var record = e.record;
        var urgent = record.sfjj;
        if(urgent=='true') {
            return '<span style="color:red">是</span>';
        } else {
            return '<span>否</span>';
        }
    }
    
    function addJgsp() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/JGSP/start.do?";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jgspListGrid) {
                    jgspListGrid.reload()
                }
            }
        }, 1000);
    }
    function jgspEdit(jgspId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&jgspId=" + jgspId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jgspListGrid) {
                    jgspListGrid.reload()
                }
            }
        }, 1000);
    }


    function jgspDetail(jgspId) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/jgsp/editPage.do?action=" + action + "&jgspId=" + jgspId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jgspListGrid) {
                    jgspListGrid.reload()
                }
            }
        }, 1000);
    }

    function jgspTask(taskId) {
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
                            if (jgspListGrid) {
                                jgspListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeJgsp(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = jgspListGrid.getSelecteds();
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
                    rowIds.push(r.jgspId);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/jgsp/deleteJgsp.do",
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
    function exportJgsp() {
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
</script>
<redxun:gridScript gridId="jgspListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

