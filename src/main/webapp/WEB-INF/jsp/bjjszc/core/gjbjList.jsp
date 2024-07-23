<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>关键备件列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/world/gjbjList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">类别: </span>
                    <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[
                                  {'key' : '保养件','value' : '保养件'}
                                 ,{'key' : '易损件','value' : '易损件'}
                                 ,{'key' : '维修件','value' : '维修件'}]"
                    />
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型物料编码: </span>
                    <input class="mini-textbox" id="vinCode" name="vinCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">总成物料编码: </span>
                    <input class="mini-textbox" id="holeCode" name="holeCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">总成物料描述: </span>
                    <input class="mini-textbox" id="holeDesc" name="holeDesc"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">对应开发件物料编码: </span>
                    <input class="mini-textbox" id="devCode" name="devCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">供应商: </span>
                    <input class="mini-textbox" id="supplier" name="supplier"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">创建人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">维护人: </span>
                    <input class="mini-textbox" id="whName" name="whName"/>
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
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addGjbj()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportGjbj()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="gjbjListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/rdm/core/Gjbj/queryGjbj.do" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" headerAlign='center' align='center' width="40">申请单号</div>
            <div field="creatorName" headerAlign='center' align='center' width="40">创建人</div>
            <div field="whName" headerAlign='center' align='center' width="40">维护人</div>
            <div field="taskName" headerAlign='center' align='center' width="40">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer">状态</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" align="center" allowSort="false">创建时间</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/rdm/core/Gjbj/exportGjbjList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var gjbjListGrid = mini.get("gjbjListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var gjbjId = record.gjbjId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="gjbjDetail(\'' + gjbjId + '\',\'' + status + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="gjbjEdit(\'' + gjbjId + '\',\'' + instId + '\',\'' + status + '\')">编辑</span>';
        }
        if (record.status == 'RUNNING') {
            var currentProcessUserId = record.currentProcessUserId;
            if (record.myTaskId) {
                s += '<span  title="办理" onclick="gjbjTask(\'' + record.myTaskId + '\')">办理</span>';
            }
        }
        if (status == 'DRAFTED') {
            s += '<span  title="删除" onclick="removeGjbj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == 'DISCARD_END' && (currentUserNo == 'admin')) {
            s += '<span  title="删除" onclick="removeGjbj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    $(function () {
        searchFrm();
    });


    function addGjbj() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/GJBJQD/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjbjListGrid) {
                    gjbjListGrid.reload()
                }
            }
        }, 1000);
    }

    function gjbjEdit(gjbjId, instId, status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&gjbjId=" + gjbjId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjbjListGrid) {
                    gjbjListGrid.reload()
                }
            }
        }, 1000);
    }

    function gjbjDetail(gjbjId) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdm/core/Gjbj/editPage.do?action=" + action + "&gjbjId=" + gjbjId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjbjListGrid) {
                    gjbjListGrid.reload()
                }
            }
        }, 1000);
    }

    function gjbjTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async: false,
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
                            if (gjbjListGrid) {
                                gjbjListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeGjbj(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = gjbjListGrid.getSelecteds();
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
                    rowIds.push(r.gjbjId);
                    instIds.push(r.instId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdm/core/Gjbj/deleteGjbj.do",
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

    function exportGjbj() {
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
<redxun:gridScript gridId="gjbjListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>