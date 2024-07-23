<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>质量改进延期申请</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>

<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">整机编号: </span>
                    <input class="mini-textbox" id="zjbh" name="zjbh"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addApply()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/zlgj/applyForExtension/queryApply.do" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="onMessageActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="zjbh" width="70" headerAlign="center" align="center" allowSort="true">整机编号</div>
            <div field="extensionDays" headerAlign='center' align='center' width="40">延期天数</div>
            <div field="currentProcessTask" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="currentProcessUser" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">状态</div>
            <div field="userName" headerAlign='center' align='center' width="40">超期节点处理人</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">申请时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    $(function () {
        searchFrm();
    });
    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="applyDetail(\'' + businessId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="applyEdit(\'' + businessId + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if (record.status == 'RUNNING') {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title="办理" onclick="applyTask(\'' + record.taskId + '\')">办理</span>';
            }
        }
        if ((status == 'DRAFTED' && currentUserId == record.CREATE_BY_) || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }
    //..
    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }
    //..
    function addApply() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZlgjApplyForExtension/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function applyEdit(businessId, instId, status) {
        mini.open({
            title: "超期原因说明",
            url: jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&businessId=" + businessId + "&status=" + status,
            width: 850,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
                applyListGrid.load();
            },
            ondestroy: function (action) {
                if (applyListGrid) {
                    applyListGrid.load();
                }
            }
        });
    }
    //..
    function applyDetail(businessId) {
        var action = "detail";
        mini.open({
            title: "延期原因说明",
            url: jsUseCtxPath + "/zlgj/applyForExtension/editPage.do?action=" + action + "&businessId=" + businessId,
            width: 850,
            height: 600,
            showModal: false,
            allowResize: true,
            onload: function () {
                applyListGrid.load();
            },
            ondestroy: function (action) {
                if (applyListGrid) {
                    applyListGrid.load();
                }
            }
        });
    }
    //..
    function applyTask(taskId) {
        debugger;
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (applyListGrid) {
                                applyListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..
    function removeApply(record) {
        debugger;
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = applyListGrid.getSelecteds();
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
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                    instIds.push(r.instId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/zlgj/applyForExtension/deleteApply.do",
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
        });
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>