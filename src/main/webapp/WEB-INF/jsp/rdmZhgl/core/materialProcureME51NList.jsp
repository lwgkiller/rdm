<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料采购申请列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/materialApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请人: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto">物料采购申请单号: </span>
                    <input class="mini-textbox" name="applyNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">申请时间 从 </span>:
                    <input name="apply_startTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:100px"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text-to" style="width:auto">至:
                    </span><input name="apply_endTime" class="mini-datepicker" format="yyyy-MM-dd" style="width:100px"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="addApply" class="mini-button" style="margin-right: 5px;" plain="true" onclick="addApply()">新增</a>
                    <a id="editApply" class="mini-button" style="margin-right: 5px" plain="true" onclick="editApply()">修改</a>
                    <a id="createToSap" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="createToSap()">创建至SAP</a>
                    <a id="sendToSap" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="sendToSap()">更新至SAP</a>
                    <a id="removeFromSap" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeFromSap()">删除</a>
                </li>
                <span style="color: red;font-size: large">每月1号SAP账号锁定，不允许进行SAP交互操作</span>
            </ul>
        </form>
    </div>
</div>
<%----%>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/materialProcureME51N/queryList.do" sortField="CREATE_TIME_" sortOrder="desc"
         idField="applyId" allowCellWrap="true" onselect="applyListGridSelect"
         multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="businessStatus" width="60" headerAlign="center" align="center" allowSort="true">业务状态</div>
            <div field="applyNo" sortField="id" width="100" headerAlign="center" align="center" allowSort="true">物料采购申请单号</div>
            <div field="applyUserName" sortField="userName" width="40" headerAlign="center" align="center">申请人</div>
            <div field="applyDeptName" sortField="userName" width="100" headerAlign="center" align="center">申请部门</div>
            <div field="reasonForUse" width="120" headerAlign="center" align="left" allowSort="false" renderer="render">领用事由</div>
            <div field="result" sortField="id" width="100" headerAlign="center" align="center" allowSort="true">SAP是否调用成功</div>
            <div field="message" sortField="id" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">SAP调用返回消息</div>
        </div>
    </div>
</div>
<%----%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var currentDay = "${currentDay}";
    //..
    $(function () {
        mini.get("editApply").setVisible(false);
        mini.get("createToSap").setVisible(false);
        mini.get("sendToSap").setVisible(false);
        mini.get("removeFromSap").setVisible(false);
    });
    //..
    function addApply() {
        var url = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?action=add";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload()
                }
                mini.get("editApply").setVisible(false);
                mini.get("createToSap").setVisible(false);
                mini.get("sendToSap").setVisible(false);
                mini.get("removeFromSap").setVisible(false);
            }
        }, 1000);
    }
    //..
    function editApply() {
        var row = applyListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.businessStatus == "删除成功") {
            mini.alert("删除成功的数据不能修改！");
            return;
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?id=" + row.id + "&action=edit";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload()
                }
                mini.get("editApply").setVisible(false);
                mini.get("createToSap").setVisible(false);
                mini.get("sendToSap").setVisible(false);
                mini.get("removeFromSap").setVisible(false);
            }
        }, 1000);
    }
    //..
    function detailApply(id) {
        var url = jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/editPage.do?id=" + id + "&action=detail";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload()
                }
                mini.get("editApply").setVisible(false);
                mini.get("createToSap").setVisible(false);
                mini.get("sendToSap").setVisible(false);
                mini.get("removeFromSap").setVisible(false);
            }
        }, 1000);
    }
    //..创建至SAP
    function createToSap() {
        var row = applyListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.businessStatus != "新增待创建") {
            mini.alert("只有“新增待创建”的数据才能进行SAP端的首次创建！");
            return;
        }
        mini.confirm("确定创建选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/createToSap.do",
                    method: 'POST',
                    data: row,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            applyListGrid.reload();
                        } else {
                            mini.alert("同步失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..更新至SAP
    function sendToSap() {
        var row = applyListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.businessStatus != "修改待推送") {
            mini.alert("只有“修改待推送”的数据才能更新至SAP！");
            return;
        }
        mini.confirm("确定推送选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/sendToSap.do",
                    method: 'POST',
                    data: row,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            applyListGrid.reload();
                        } else {
                            mini.alert("同步失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function removeFromSap() {
        var row = applyListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        if (row.businessStatus == "删除成功") {
            mini.alert("“删除成功”的数据不能再次删除！");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/materialProcureME51N/removeFromSap.do",
                    method: 'POST',
                    data: row,
                    postJson: true,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            applyListGrid.reload();
                        } else {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("同步失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var s = '<span  title="明细" onclick="detailApply(\'' + record.id + '\')">明细</span>';

        return s;
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function applyListGridSelect(e) {
        mini.get("editApply").setVisible(false);
        mini.get("createToSap").setVisible(false);
        mini.get("sendToSap").setVisible(false);
        mini.get("removeFromSap").setVisible(false);
        var record = e.record;
        if (record.businessStatus == "新增待创建") {//修改，创建，删除
            mini.get("editApply").setVisible(true);
            mini.get("createToSap").setVisible(true);
            mini.get("removeFromSap").setVisible(true);
        } else if (record.businessStatus == "新增成功") {//修改，删除
            mini.get("editApply").setVisible(true);
            mini.get("removeFromSap").setVisible(true);
        } else if (record.businessStatus == "修改待推送") {//修改，更新，删除
            mini.get("editApply").setVisible(true);
            mini.get("sendToSap").setVisible(true);
            mini.get("removeFromSap").setVisible(true);
        } else if (record.businessStatus == "修改成功") {//修改，删除
            mini.get("editApply").setVisible(true);
            mini.get("removeFromSap").setVisible(true);
        } else if (record.businessStatus == "删除成功") {//什么都不可以
        }
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
