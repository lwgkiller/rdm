<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>工况工法作业分类列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="addButton" class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
            <a  class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
            <a id="delButton" class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
        </li>
        <span class="separator"></span>
        <li>
            <a id="saveButton" class="mini-button" iconCls="icon-save" onclick="saveRow()">保存</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/gkgf/core/workType/dataList.do"  sortField="sortIndex" sortOrder="asc"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="workName" name="workName" width="100" headerAlign="center" align="center">作业分类名称<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="sortIndex" displayfield="sortIndex" width="20" headerAlign="center" align="center">排序号<span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-spinner"   allowLimitValue="false"
                       required="false" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  minValue="0" style="width:100%;height:34px"/>
            </div>
            <div field="updateName" width="80" headerAlign="center" align="center">更新人
            </div>
            <div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                更新时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var isAdmin = ${isAdmin};
    if (!isAdmin) {
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
        mini.get("saveButton").setEnabled(false);
    }
    listGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });

    function addRow() {
        var newRow = {name: "New Row"};
        listGrid.addRow(newRow, 0);
        listGrid.beginEditCell(newRow, "workName");
    }

    function removeRow() {
        var rows = listGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "是否确定删除！",
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

    function refresh() {
        listGrid.load();
    }

    listGrid.on("beforeload", function (e) {
        if (listGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function saveRow() {
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].workName||!data[i].sortIndex ) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/gkgf/core/workType/dealData.do",
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
                    listGrid.reload();
                }
            }
        });
    }

    listGrid.on("cellcommitedit", function (e) {
    });

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
