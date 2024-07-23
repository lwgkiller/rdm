<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>型谱变更申请列表</title>
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
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                    <span class="text" style="width:auto">申请部门: </span>
                    <input class="mini-textbox" id="departName" name="departName"/>
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                    <span class="text" style="width:auto">申请权限类别: </span>
                    <input property="editor" class="mini-combobox"
                           style="width:98%;"
                           enabled="true"
                           id="aimType" name="aimType"
                           textField="key" valueField="value" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..."
                           data="[{'key' : '研发状态','value' : '研发状态'}
                                       ,{'key' : '其他内容','value' : '其他内容'}
                                       ,{'key' : '销售状态','value' : '销售状态'}
                                       ,{'key' : '制造状态','value' : '制造状态'}
                                  ]"
                    />

                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addXpbgsq()">新增</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="xpbgsqListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/pdm/core/xpbgsq/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="applyNumber" headerAlign="center" align="center" allowSort="false">申请单号</div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false" width="60">创建人</div>
            <div field="departName" headerAlign="center" align="center" allowSort="false"width="80">申请部门</div>
            <div field="designModel" headerAlign="center" align="center" allowSort="false">设计型号</div>
            <div field="aimType" headerAlign="center" align="center" allowSort="false">申请权限类型</div>
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
    var xpbgsqListGrid = mini.get("xpbgsqListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + "明细" + ' onclick="xpbgsqDetail(\'' + applyId + '\',\'' + instId + '\')">' + '明细' + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + "编辑" + ' onclick="xpbgsqEdit(\'' + applyId + '\',\'' + instId + '\')">' + '编辑' + '</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title=' + "办理" + ' onclick="xpbgsqTask(\'' + record.myTaskId + '\')">' + '办理' + '</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + "删除" + ' onclick="removeXpbgsq(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + '删除' + '</span>';
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


    function addXpbgsq() {
        if (isFbr == "false" && currentUserId != "1") {
            mini.alert("只有通信协议更改发布人才可以新建");
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CPXPZTBG/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (xpbgsqListGrid) {
                    xpbgsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function xpbgsqEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (xpbgsqListGrid) {
                    xpbgsqListGrid.reload()
                }
            }
        }, 1000);
    }


    function xpbgsqDetail(applyId) {
        var action = "detail";
        var url = jsUseCtxPath + "/pdm/core/xpbgsq/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (xpbgsqListGrid) {
                    xpbgsqListGrid.reload()
                }
            }
        }, 1000);
    }

    function xpbgsqTask(taskId) {
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
                            if (xpbgsqListGrid) {
                                xpbgsqListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeXpbgsq(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = xpbgsqListGrid.getSelecteds();
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
                        url: jsUseCtxPath + "/pdm/core/xpbgsq/deleteApply.do",
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
<redxun:gridScript gridId="xpbgsqListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

