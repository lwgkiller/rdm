<%--
  Created by IntelliJ IDEA.
  User: zhangyonggan
  Date: 2024/5/7
  Time: 14:07
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>试制零部件进度跟踪列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料号: </span>
                    <input class="mini-textbox" id="trialMaterielNumAf" name="trialMaterielNumAf"/>
                    <span class="text" style="width:auto">试制单号: </span>
                    <input class="mini-textbox" id="trialBillNum" name="trialBillNum"/>
                    <span class="text" style="width:auto">分类: </span>
                    <input class="mini-combobox" id="category" name="category"
                           data= '[{key:"电气",value:"电气"},{key:"液压",value:"液压"},{key:"动力",value:"动力"},{key:"转台",value:"转台"},{key:"底盘",value:"底盘"},{key:"覆盖件",value:"覆盖件"},{key:"工作装置",value:"工作装置"}]';
                           textField="value" valueField="key"
                    />
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" id="creater" name="creater"/>
                    <span class="text " style="width:auto">部门: </span>
                    <input class="mini-textbox" id="deptName" name="deptName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeBusiness()">删除</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportTrialPratsInfo()">按条件导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="trialBillListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/trialPartsProcess/core/trialPartsProcess/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="creater" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="deptName" headerAlign="center" align="center" allowSort="false">部门</div>
            <div field="trialBillNum"  headerAlign="center" align="center" allowSort="false">试制单号</div>
            <div field="trialMaterielNumAf" headerAlign="center" align="center" allowSort="false">物料号</div>
            <div field="category" headerAlign="center" align="center" allowSort="false">分类</div>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="60">当前处理人</div>
            <div field="status" width="35" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" sortMode="client"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/trialPartsProcess/core/trialPartsProcess/exportTrialPratsInfo.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script>
    mini.parse();

    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var trialBillListGrid = mini.get("trialBillListGrid");

    //新增试制流程
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/SZLBJJDGZ/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (trialBillListGrid) {
                    trialBillListGrid.reload()
                }
            }
        }, 1000);
    }

    function caseEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (trialBillListGrid) {
                    trialBillListGrid.reload()
                }
            }
        }, 1000);
    }

    function caseDetail(applyId) {
        var url = jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (trialBillListGrid) {
                    trialBillListGrid.reload()
                }
            }
        }, 1000);
    }

    function caseMain(applyId) {
        var url = jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/applyEditPage.do?action=maintenance&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (trialBillListGrid) {
                    trialBillListGrid.reload()
                }
            }
        }, 1000);
    }

    function caseTask(taskId) {
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
                            if (trialBillListGrid) {
                                trialBillListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = trialBillListGrid.getSelecteds();
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
                    if ((r.status == 'DRAFTED'||r.status=="DISCARD_END") && (r.CREATE_BY_ == currentUserId)) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("仅草稿状态数据可由本人删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/trialPartsProcess/core/trialPartsProcess/deleteBaseInfo.do",
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

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var maintanceUsers = record.memberInfo.split(",");
        maintanceUsers.shift();
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="caseDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="caseEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="caseTask(\'' + record.myTaskId + '\')">办理</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == "SUCCESS_END" &&( currentUserNo == 'admin' || maintanceUsers.contains(currentUserId))) {
            s += '<span  title="维护" onclick="caseMain(\'' + applyId + '\',\'' + instId+'\')">维护</span>';
        }
        // if (status != "SUCCESS_END" && currentUserNo == "admin") {
        //     s += '<span  title="作废" onclick="discardInst(\'' + record.instId + '\')">作废</span>';
        // }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '进行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '可维护', 'css': 'blue'},
            {'key': 'ABORT_END', 'value': '终止', 'css': 'red'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];
        return $.formatItemValue(arr, status);
    }

    function exportTrialPratsInfo() {
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
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
<redxun:gridScript gridId="trialBillListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
