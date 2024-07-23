<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2024/1/28
  Time: 17:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>售前文件欧美澳下载申请列表</title>
    <%@include file="/commons/list.jsp" %>
</head>

<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请编号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请部门:</span>
                    <input class="mini-textbox" id="applyUserDeptName" name="applyUserDeptName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
<%--                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addSalefileDownload()">新增</a>--%>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeSalefileDownload()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="salefileDownloadList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/rdmZhgl/core/SaleFileOMAXiazaiApply/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" width="110" headerAlign="center" align="center" allowSort="false">申请编号</div>
            <div field="applyUserDeptName" width="70" headerAlign="center" align="center" allowSort="false">申请部门</div>
            <div field="creatorName" width="50" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="taskName" headerAlign='center' align='center' width="120">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80">当前处理人</div>
            <div field="status" width="40" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">状态</div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="65" align="center" headerAlign="center" allowSort="true">创建时间</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var salefileDownloadList = mini.get("salefileDownloadList");
    $(function () {
        searchFrm();
    });
    //..
    function addSalefileDownload() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/SaleFileXiazaiApply/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (salefileDownloadList) {
                    salefileDownloadList.reload()
                }
            }
        }, 1000);
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + "明细" + ' onclick="decorationDownloadDetail(\'' + applyId + '\',\'' + status + '\')">' + "明细" + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + "编辑" + ' onclick="decorationDownloadEdit(\'' + applyId + '\',\'' + instId + '\')">' + "编辑" + '</span>';
        }
        if (record.status == 'RUNNING') {
            if (record.myTaskId) {
                s += '<span  title=' + "办理" + ' onclick="decorationDownloadTask(\'' + record.myTaskId + '\')">' + "办理" + '</span>';
            }
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + "删除" + ' onclick="removeSalefileDownload(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + "删除" + '</span>';
        }
        return s;
    }
    function decorationDownloadDetail(applyId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdmZhgl/core/SaleFileOMAXiazaiApply/applyEditPage.do?" +
            "action=detail&applyId=" + applyId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (salefileDownloadList) {
                    salefileDownloadList.reload()
                }
            }
        }, 1000);
    }
    //..
    function decorationDownloadEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (salefileDownloadList) {
                    salefileDownloadList.reload()
                }
            }
        }, 1000);
    }

    function decorationDownloadTask(taskId) {
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
                            if (salefileDownloadList) {
                                salefileDownloadList.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeSalefileDownload(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = salefileDownloadList.getSelecteds();
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
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) || currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    mini.alert("仅草稿状态数据可由本人删除,或者联系admin强制删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdmZhgl/core/SaleFileOMAXiazaiApply/deleteApply.do",
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
</script>
<redxun:gridScript gridId="salefileDownloadList" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
