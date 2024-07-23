<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术文件申请列表</title>
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
                    <span class="text" style="width:auto">零部件类型: </span>
                    <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[{'key' : '发动机','value' : '发动机'}
                                       ,{'key' : '主泵','value' : '主泵'}
                                       ,{'key' : '液压马达','value' : '液压马达'}
                                       ,{'key' : '油缸','value' : '油缸'}
                                       ,{'key' : '阀','value' : '阀'}
                                       ,{'key' : '电气件','value' : '电气件'}]"
                    />
                    <span class="text" style="width:auto">零部件型号: </span>
                    <input class="mini-textbox" id="partsModel" name="partsModel"/>
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJswjsq()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jswjsqListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/jswjsq/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" headerAlign="center" align="center" allowSort="false">申请单号</div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="partsType" headerAlign="center" align="center" allowSort="false">零部件类型</div>
            <div field="partsModel" headerAlign="center" align="center" allowSort="false">零部件型号</div>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="40">当前处理人</div>
            <div field="status" width="25" headerAlign="center" align="center" allowSort="true"
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
    var isZks = "${isZks}";
    var isFbr = "${isFbr}";
    var jswjsqListGrid = mini.get("jswjsqListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="jswjsqDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="jswjsqEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="jswjsqTask(\'' + record.myTaskId + '\')">办理</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeJswjsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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


    function addJswjsq() {
        if (isFbr == "false" && currentUserId != "1") {
            mini.alert("只有通信协议更改发布人才可以新建");
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/JSWJSQ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jswjsqListGrid) {
                    jswjsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function jswjsqEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jswjsqListGrid) {
                    jswjsqListGrid.reload()
                }
            }
        }, 1000);
    }


    function jswjsqDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/jswjsq/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jswjsqListGrid) {
                    jswjsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function jswjsqTask(taskId) {
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
                            if (jswjsqListGrid) {
                                jswjsqListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeJswjsq(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = jswjsqListGrid.getSelecteds();
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
                    if (r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) {
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
                        url: jsUseCtxPath + "/serviceEngineering/core/jswjsq/deleteApply.do",
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
<redxun:gridScript gridId="jswjsqListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

