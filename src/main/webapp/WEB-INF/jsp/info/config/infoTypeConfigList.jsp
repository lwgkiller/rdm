<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>情报类别维护</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
            <a class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
            <a class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
        </li>
        <span class="separator"></span>
        <li>
            <a class="mini-button" iconCls="icon-save" onclick="saveRow()">保存</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="typeConfigGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/info/type/infoTypeList.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="infoTypeName" name="infoTypeName" width="100" headerAlign="center" align="center">情报类别<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="updateName" name="updateName" width="80" headerAlign="center" align="center">更新人
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="UPDATE_TIME_" name="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                更新时间
                <input property="editor" class="mini-textbox" readonly/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var typeConfigGrid = mini.get("typeConfigGrid");

    typeConfigGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });

    function addRow() {
        var newRow = {name: "New Row"};
        typeConfigGrid.addRow(newRow, 0);
        typeConfigGrid.beginEditCell(newRow, "infoTypeName");
    }

    function removeRow() {
        var rows = typeConfigGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
                callback: function (action) {
                    if (action == "ok") {
                        typeConfigGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refresh() {
        typeConfigGrid.load();
    }

    typeConfigGrid.on("beforeload", function (e) {
        if (typeConfigGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function saveRow() {
        var data = typeConfigGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].infoTypeName ) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/info/type/dealData.do",
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
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    typeConfigGrid.reload();
                }
            }
        });
    }

    typeConfigGrid.on("cellcommitedit", function (e) {
    });

</script>
<redxun:gridScript gridId="typeConfigGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
