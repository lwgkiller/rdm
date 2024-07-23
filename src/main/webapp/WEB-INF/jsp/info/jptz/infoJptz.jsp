<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>国内供应商</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">厂家名称: </span>
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
         url="${ctxPath}/info/core/jptz/list.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         allowCellSelect="true" allowCellWrap="false" allowCellEdit="true" editNextOnEnterKey="true"
         oncellvalidation="onCellValidation"
         editNextRowCell="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <%--<div name="action" cellCls="actionIcons" width="20" headerAlign="center" align="center" renderer="renderer">操作</div>--%>
            <div name="paperCategory" field="paperCategory" width="20" headerAlign="center" align="center">
                类别
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="paperFactory" field="paperFactory" width="40" headerAlign="center" align="center">
                竞品厂家
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="paperDesc" field="paperDesc" width="40" headerAlign="center" align="center">
                图纸说明
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="action" cellCls="actionIcons" width="15" headerAlign="center" align="center"
                 renderer="productDemoRenderer">附件
            </div>
            <div field="creator" width="10" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="20" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">
                创建时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url = "${ctxPath}/info/core/jptz/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    var isLDR = ${isLDR};

    searchByName();
    qxkz();

    // 操作列渲染
    function renderer(e) {
        let record = e.record;
        var s = '<span title="编辑" onclick="openEditorWindow(\'' + record._uid + '\')">编辑</span>';
        s += '<span title="删除" onclick="removeAchievementType()">删除</span>';
        return s;
    }

    // 产品样本列渲染
    function productDemoRenderer(e) {
        let record = e.record;
        if (record._state === 'added') {
            return "-";
        } else {
            if (isLDR || record.createBy == currentUserId || currentUserId == '1' || isZLGHZY) {
                return '<span title="明细" onclick="openProductDemoWindow(\'' + record._uid + '\')">明细</span>';
            } else {
                return '<span  title="明细" style="color: silver" >明细</span>';
            }
        }
    }

    // 回车搜索
    function onHanlerEnter() {
        searchByName();
    }

    // 搜索
    function searchByName() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({name: "paperFactory", value: cxName});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data);
    }

    // 刷新
    function refreshAchievementType() {
        key.setValue("");
        listGrid.load();
    }

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

    // 新增和编辑弹窗-样本附件明细
    function openProductDemoWindow(uid) {
        let rowData = listGrid.getRowByUID(uid);
        let data = {row: rowData};
        mini.open({
            url: jsUseCtxPath + "/info/core/jptz/files/page.do?id=" + rowData.id,
            title: "编辑竞品图纸样本附件",
            width: 1024,
            height: 550,
            onload: function () {
                // var iframe = this.getIFrameEl();
                // iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                if (action == "ok") {
                    searchByName();
                }
            }
        });
    }

    // 新增行
    function addAchievementType() {
        var newRow = {};
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
                message: "刪除此数据对应的样本附件也会清除，是否确定删除？",
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

    // 刷新提示
    listGrid.on("beforeload", function (e) {
        let changes = listGrid.getChanges();
        if (changes.length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    // 自定义验证
    function onCellValidation(e) {
        switch (e.field) {
            case "paperFactory":
                verifyValue(e, "竞品厂家");
                break;
            case "paperCategory":
                verifyValue(e, "竞品图纸类别");
                break;
            case "paperDesc":
                verifyValue(e, "图纸说明");
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
                url: jsUseCtxPath + "/info/core/jptz/batchOptions.do",
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
    // 操作列样式渲染
    listGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    // 冻结
    // listGrid.frozenColumns(2, 2);

    // 合并单元格
    // listGrid.on("load", function () {
    //     listGrid.mergeColumns(["zljcName"])
    // });

</script>
<%--<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>--%>
</body>
</html>
