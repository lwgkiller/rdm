<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>通信协议变更列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请单号: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                    <span class="text" style="width:auto">产品名称: </span>
                    <input class="mini-textbox" id="productName" name="productName"/>
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                    <span class="text" style="width:auto">产品说明: </span>
                    <input class="mini-textbox" id="productNotes" name="productNotes"/>
                    <span class="text" style="width:auto">主要参数: </span>
                    <input class="mini-textbox" id="mainParamDesc" name="mainParamDesc"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCpxhsq()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="cpxhsqListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/pdm/core/cpxhsq/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="40" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="130" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" width="220" headerAlign="center" align="center" allowSort="false">申请单号</div>
            <%--<div field="creatorName" headerAlign="center" align="center" allowSort="false">创建人</div>--%>
            <div field="productName" width="100" headerAlign="center" align="center" allowSort="false">产品名称</div>
            <div field="departName" width="80" headerAlign="center" align="center" allowSort="false">申请部门</div>
            <div field="designModel" width="100" headerAlign="center" align="center" allowSort="false">设计型号</div>
            <div field="dischargeStage" width="80" headerAlign="center" align="center" allowSort="false">排放阶段</div>
            <div field="mainParamDesc"  width="120" headerAlign="center" align="center" allowSort="false">主参数代号及单位</div>
            <div field="productNotes"  width="120" headerAlign="center" align="center" allowSort="false">产品说明</div>
            <div field="abroad"  width="120" headerAlign="center" align="center" allowSort="false">国内外</div>
            <div field="region"  width="120" headerAlign="center" align="center" allowSort="false">销售区域</div>
            <div field="creatorName"  width="100" headerAlign="center" align="center" allowSort="false">创建人</div>
            <div field="taskName"  width="120"headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames"  width="80" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="80" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var cpxhsqListGrid = mini.get("cpxhsqListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="cpxhsqDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="cpxhsqEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="cpxhsqTask(\'' + record.myTaskId + '\')">办理</span>';
        }

                if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeCpxhsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

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

    $(function () {
        searchFrm();
    });


    function addCpxhsq() {
        // if (isFbr == "false" && currentUserId != "1") {
        //     mini.alert("只有通信协议更改发布人才可以新建");
        //     return;
        // }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CPSJXHSQ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpxhsqListGrid) {
                    cpxhsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function cpxhsqEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpxhsqListGrid) {
                    cpxhsqListGrid.reload()
                }
            }
        }, 1000);
    }


    function cpxhsqDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/pdm/core/cpxhsq/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (cpxhsqListGrid) {
                    cpxhsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function cpxhsqTask(taskId) {
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
                            if (cpxhsqListGrid) {
                                cpxhsqListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeCpxhsq(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = cpxhsqListGrid.getSelecteds();
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
                    if (r.status == 'DRAFTED' && (r.CREATE_BY_ == currentUserId || currentUserId=='1')) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("仅草稿状态数据可由本人删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/pdm/core/cpxhsq/deleteApply.do",
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
<redxun:gridScript gridId="cpxhsqListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

