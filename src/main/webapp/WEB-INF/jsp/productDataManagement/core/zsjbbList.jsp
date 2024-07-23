<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>主数据属性治理工作提报</title>
    <%@include file="/commons/list.jsp" %>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">提报人: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                    <span class="text" style="width:auto">部门名称: </span>
                    <input class="mini-textbox" id="departName" name="departName"/>
                    <span class="text " style="width:auto">月份: </span>
                    <input class="mini-monthpicker" id="yearMonth" name="yearMonth"/>
<%--                    <span class="text" style="width:auto">工作报告名称: </span>--%>
<%--                    <input class="mini-textbox" id="workReportName" name="workReportName"/>--%>
<%--                    <span class="text" style="width:auto">是否优秀案例: </span>--%>
<%--                    <input id="isYxal" name="isYxal" class="mini-combobox" style="width:150px;"--%>
<%--                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"--%>
<%--                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."--%>
<%--                           data="[ {'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"--%>
<%--                    />                    <span class="text" style="width:auto">未提报: </span>--%>
<%--                    <input id="isWtb" name="isWtb" class="mini-combobox" style="width:150px;"--%>
<%--                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"--%>
<%--                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."--%>
<%--                           data="[ {'key' : 'yes','value' : '是'}]"--%>
<%--                    />--%>
                    <span class="text" style="width:auto">流程状态: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />

                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addBusiness()">新增</a>
<%--                    <a class="mini-button" style="margin-right: 5px" plain="true"--%>
<%--                       onclick="editBusiness(e)">管理员编辑</a>--%>
<%--                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="chooseYxal()">选为优秀案例</a>--%>
<%--                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="cancelYxal()">取消优秀案例</a>--%>
                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>
<%--                    <a class="mini-button" id = "batchCreateButton" style="margin-left: 5px" plain="true" onclick="batchCreate()">月初手动建流程</a>--%>

                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="zsjbbListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/productDataManagement/core/zsjbb/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="creatorName" headerAlign="center" align="center" allowSort="false">申请人</div>
            <div field="departName" headerAlign="center" align="center" allowSort="false">申请部门</div>
            <div field="yearMonth" dateFormat="yyyy-MM" headerAlign="center" align="center"
                 allowSort="false">年月
            </div>
<%--            <div field="workReportName" headerAlign="center" align="center" allowSort="false">工作报告名称</div>--%>
<%--            <div field="isOverTime" headerAlign="center" align="center" width="40" allowSort="false" renderer="overTimeRender">是否超时</div>--%>
<%--            <div field="isYxal" headerAlign="center" align="center" allowSort="false" width="40" renderer="yxalRender">优秀案例</div>--%>
<%--            <div field="yxalTime" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"--%>
<%--                 allowSort="true">评选时间--%>
<%--            </div>--%>
<%--            <div field="isWtb" name="isWtb" headerAlign="center" align="center" allowSort="false" width="40" renderer="sftbRender" >是否提报</div>--%>
            <div field="taskName" headerAlign='center' align='center' width="60">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="60">当前处理人</div>
            <div field="status" width="35" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>
        </div>
    </div>
</div>


<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/productDataManagement/core/zsjbb/exportExecl.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>



<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var yearMonth = "${yearMonth}";
    var currentDay = "${currentDay}";
    var deptName = "${deptName}";
    <%--var isSuozhang = "${isSuozhang}";--%>
    var scene = "${scene}";
    var isTbAdmin = "${isTbAdmin}";
    var zsjbbListGrid = mini.get("zsjbbListGrid");



    $(function () {

    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="zsjbbDetail(\'' + applyId + '\',\'' + instId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="zsjbbEdit(\'' + applyId + '\',\'' + instId + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="zsjbbTask(\'' + record.myTaskId + '\')">办理</span>';
        }

        if ((status == 'DRAFTED'&&currentUserId ==record.CREATE_BY_) || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status != "SUCCESS_END" && isTbAdmin == "true") {
            s += '<span  title="作废" onclick="discardInst(\'' + record.instId + '\')">作废</span>';
        }

        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'ABORT_END', 'value': '作废', 'css': 'red'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    $(function () {

        if (scene == "indexPage") {
            mini.get("yearMonth").setValue(yearMonth);
            mini.get("departName").setValue(deptName);
        }
        searchFrm();
    });


    function addBusiness() {

        var url = jsUseCtxPath + "/bpm/core/bpmInst/ZSJZLBB/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zsjbbListGrid) {
                    zsjbbListGrid.reload()
                }
            }
        }, 1000);
    }

    function zsjbbEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zsjbbListGrid) {
                    zsjbbListGrid.reload()
                }
            }
        }, 1000);
    }


    function zsjbbDetail(applyId) {

        var url = jsUseCtxPath + "/productDataManagement/core/zsjbb/applyEditPage.do?action=detail&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (zsjbbListGrid) {
                    zsjbbListGrid.reload()
                }
            }
        }, 1000);
    }

    function zsjbbTask(taskId) {
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
                            if (zsjbbListGrid) {
                                zsjbbListGrid.reload();
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
            rows = zsjbbListGrid.getSelecteds();
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
                    if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId)||currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("仅草稿状态数据可由本人删除或者admin删除");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/productDataManagement/core/zsjbb/deleteApply.do",
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

    //
    // function chooseYxal(record) {
    //     if (isSuozhang != "true") {
    //         mini.alert("只有技术中心所长有操作权限！");
    //         return;
    //     }
    //     var rows = [];
    //     if (record) {
    //         rows.push(record);
    //     } else {
    //         rows = zsjbbListGrid.getSelecteds();
    //     }
    //     if (rows.length <= 0) {
    //         mini.alert("请至少选中一条记录!");
    //         return;
    //     }
    //     mini.confirm("确认选为优秀案例？", "提示", function (action) {
    //         if (action != 'ok') {
    //             return;
    //         } else {
    //             var rowIds = [];
    //             var existStartInst = false;
    //             for (var i = 0, l = rows.length; i < l; i++) {
    //                 var r = rows[i];
    //                 if ((r.status == "SUCCESS_END"  && r.departName == deptName && r.isYxal != "yes"&& r.isOverTime!="yes")) {
    //                     rowIds.push(r.id);
    //                 }
    //                 else {
    //                     existStartInst = true;
    //                     continue;
    //                 }
    //             }
    //             if (existStartInst) {
    //                 alert("仅本部门领导可评选流程已结束未评选未超时的报告！");
    //             }
    //             if (rowIds.length > 0) {
    //                 _SubmitJson({
    //                     url: jsUseCtxPath + "/productDataManagement/core/zsjbb/chooseYxal.do",
    //                     method: 'POST',
    //                     showMsg: false,
    //                     data: {ids: rowIds.join(',')},
    //                     success: function (data) {
    //                         if (data) {
    //                             mini.alert(data.message);
    //                             searchFrm();
    //                         }
    //                     }
    //                 });
    //             }
    //         }
    //     });
    // }



    // function cancelYxal(record) {
    //     if (isSuozhang != "true") {
    //         mini.alert("只有技术中心所长有操作权限！");
    //         return;
    //     }
    //     var rows = [];
    //     if (record) {
    //         rows.push(record);
    //     } else {
    //         rows = zsjbbListGrid.getSelecteds();
    //     }
    //     if (rows.length <= 0) {
    //         mini.alert("请至少选中一条记录!");
    //         return;
    //     }
    //     mini.confirm("确认取消优秀案例？", "提示", function (action) {
    //         if (action != 'ok') {
    //             return;
    //         } else {
    //             var rowIds = [];
    //             var existStartInst = false;
    //             for (var i = 0, l = rows.length; i < l; i++) {
    //                 var r = rows[i];
    //                 if ((r.status == "SUCCESS_END"  && r.departName == deptName)) {
    //                     rowIds.push(r.id);
    //                 }
    //                 else {
    //                     existStartInst = true;
    //                     continue;
    //                 }
    //             }
    //             if (existStartInst) {
    //                 alert("仅本部门领导可取消流程已结束的报告！");
    //             }
    //             if (rowIds.length > 0) {
    //                 _SubmitJson({
    //                     url: jsUseCtxPath + "/productDataManagement/core/zsjbb/cancelYxal.do",
    //                     method: 'POST',
    //                     showMsg: false,
    //                     data: {ids: rowIds.join(',')},
    //                     success: function (data) {
    //                         if (data) {
    //                             mini.alert(data.message);
    //                             searchFrm();
    //                         }
    //                     }
    //                 });
    //             }
    //         }
    //     });
    // }

    function exportExcel() {
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

    // function editBusiness() {
    //     if (isTbAdmin == "false" && currentUserId != "1") {
    //         mini.alert("只有月度工作提报管理员才可以编辑");
    //         return;
    //     }
    //     var rows = zsjbbListGrid.getSelecteds();
    //     if (rows.length > 1) {
    //         mini.alert("请选择一条数据编辑！");
    //         return;
    //     }
    //     var row = rows[0];
    //     // 2023年7月11日17:49:24 刘晋要求已完成的也要有修改权限
    //     // if (row.status == "SUCCESS_END") {
    //     //     mini.alert("只允许修改草稿和审批中的数据！");
    //     //     return;
    //     // }
    //     var id = row.id;
    //     var url = jsUseCtxPath + "/productDataManagement/core/zsjbb/applyEditPage.do?applyId=" + id + '&action=spEdit' ;
    //     var winObj = window.open(url, '');
    //     var loop = setInterval(function () {
    //         if (winObj.closed) {
    //             clearInterval(loop);
    //             if (zsjbbListGrid) {
    //                 zsjbbListGrid.reload();
    //             }
    //         }
    //     }, 1000);
    // }

    function discardInst(instId) {

        if (!confirm('流程终止后不可恢复，确定要作废该流程吗?')) {
            return;
        }
        _SubmitJson({
            url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
            data: {
                instId: instId
            },
            method: 'POST',
            success: function () {
                searchFrm();
            }
        })
    }




</script>
<redxun:gridScript gridId="zsjbbListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

