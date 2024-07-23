<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>企业供应商联系人</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">联系人名称: </span>
            <input id="key" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="KPI_name"
               onclick="searchByName()">查询</a>
            <a class="mini-button btn-red" onclick="refreshAchievementType()" plain="true">清空查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>
        <span id="firstSeparator" class="separator"></span>
        <li>
            <%--<a id="addKpi" class="mini-button" iconCls="icon-add" onclick="openEditorWindow()">新增</a>--%>
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="addAchievementType()">新增</a>
            <a id="removeKpi" class="mini-button btn-red" plain="true" onclick="removeAchievementType()">删除</a>
        </li>
        <span id="secondSeparator" class="separator"></span>
        <li>
            <a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveAchievementType()">保存</a>
            <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
            </p>
        </li>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/info/core/enterprise/enterpriseContacts/list.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true" editNextOnEnterKey="true"
         editNextRowCell="true" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="15"></div>
            <div name="contactName" field="contactName" width="30" headerAlign="center" align="center">
                联系人名称<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="post" field="post" width="50" headerAlign="center" align="center">
                职位<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="phone" field="phone" width="50" headerAlign="center" align="center">
                手机号<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div field="creator" width="30" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="40" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">
                创建时间
            </div>
        </div>
    </div>
</div>
<%--<div class="mini-toolbar" style="text-align:center;padding-top:8px;padding-bottom:8px;" borderStyle="border:0;">--%>
    <%--<a class="mini-button" style="width:60px;" onclick="onOk()">确定</a>--%>
    <%--<span style="display:inline-block;width:25px;"></span>--%>
    <%--<a class="mini-button btn-red" plain="true" style="width:60px;" onclick="onCancel()">取消</a>--%>
<%--</div>--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url = "${ctxPath}/info/core/enterprise/enterpriseContacts/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    var belongId = "";
    qxkz();
    // 权限控制
    function qxkz() {
        if (!isZLGHZY) {
            $("p").hide();
            $("#firstSeparator").hide();
            $("#secondSeparator").hide();
            mini.get("addKpi").hide();
            mini.get("removeKpi").hide();
            mini.get("saveKpi").hide();
            return;
        }
    }

    // 单元格编辑事件控制
    listGrid.on("cellbeginedit", function (e) {
        // if (currentUserId === record.initiatorId || isZLGHZY) {
        if (isZLGHZY) {
            // 当前登录人是情报专员 ===> 允许
            e.cancel = false;
        } else {
            mini.showTips({
                content: "<span style=\'font-size: 16px\'>只允许情报专员编辑</span>",
                state: 'warning',
                x: 'right',
                y: 'top',
                timeout: 1500
            });
            // 不允许
            e.cancel = true;
        }
    });

    function onHanlerEnter() {
        searchByName();
    }

    function searchByName() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({name: "contactName", value: cxName});
        }
        if (belongId) {
            queryParam.push({name: "belongId", value: belongId});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data);
    }

    function refreshAchievementType() {
        key.setValue("");
        searchByName();
    }

    // 新增行
    function addAchievementType() {
        var newRow = {
            "belongId": belongId
        };
        listGrid.addRow(newRow, 0);
    }

    // 删除行
    function removeAchievementType() {
        if (!isZLGHZY) {
            mini.alert("没有操作权限!");
            return;
        }
        var rows = listGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除？",
                callback: function (action) {
                    if (action == "ok") {
                        listGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    // 自定义验证
    function onCellValidation(e) {
        switch (e.field) {
            case "contactName":
                verifyValue(e, "联系人名称");
                break;
            case "post":
                verifyValue(e, "职位");
                break;
            case "phone":
                verifyValue(e, "手机号");
                break;
            default:
                break;
        }
    }

    function verifyValue(e, headerName) {
        let value = e.value;
        if (value === undefined || value === null || value === "") {
            e.isValid = false;
            e.errorText = headerName + "必填";
        } else {
            e.isValid = true
        }
    }

    // 保存数据
    function saveAchievementType() {
        // 验证表格
        listGrid.validate();
        // 验证是否通过
        if (listGrid.isValid() === false) {
            var error = listGrid.getCellErrors()[0];
            listGrid.beginEditCell(error.record, error.column);
            showTips(error.errorText);
            return;
        }
        // 获取变化数据
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        if (data.length > 0) {
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/info/core/enterprise/enterpriseContacts/batchOptions.do",
                data: json,
                type: "post",
                contentType: 'application/json',
                async: false,
                success: function (text) {
                    if (text && text.message) {
                        message = text.message;
                    }
                }
            });
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok") {
                    listGrid.reload();
                }
            }
        });
    }

    // 展示提示
    function showTips(message) {
        mini.showMessageBox({
            showModal: false,
            width: 250,
            title: "提示",
            iconCls: "mini-messagebox-warning",
            message: message,
            timeout: 2000,
            x: "right",
            y: "bottom"
        });
    }

    //标准方法接口定义
    function SetData(data) {
        //跨页面传递的数据对象，克隆后才可以安全使用
        data = mini.clone(data.row);
        belongId = data.id
        searchByName();
    }

    // 回调事件 获取弹窗选择的行
    function GetData() {
        var row = grid.getSelected();
        return row;
    }

    // 双击行事件
    function onRowDblClick(e) {
        onOk();
    }

    //////////////////////////////////
    function CloseWindow(action) {
        if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
        else window.close();
    }

    function onOk() {
        CloseWindow("ok");
    }

    function onCancel() {
        CloseWindow("cancel");
    }

</script>
<%--<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>--%>
</body>
</html>
