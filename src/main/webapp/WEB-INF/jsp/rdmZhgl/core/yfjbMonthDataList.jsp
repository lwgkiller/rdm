<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>整机成本</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="addButton" class="mini-button" iconCls="icon-add" onclick="addRow()">新增</a>
            <a id="refreshButton" class="mini-button" iconCls="icon-reload" onclick="refresh()" plain="true">刷新</a>
            <a id="delButton" class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
        </li>
        <span class="separator"></span>
        <li>
            <a id="saveButton" class="mini-button" iconCls="icon-save" onclick="save()">保存</a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、删除、编辑后都需要进行保存操作）
                <span style="color: red">（只有降本专员可以维护数据）</span>
            </p>
        </li>
    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/yfjb/monthData/listData.do" pageSize="15"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
            <div field="yearMonth" name="yearMonth" dateFormat="yyyy-MM"   width="100" headerAlign="center" align="center">年月<span
                    style="color: #ff0000">*</span>
                <input property="editor"  allowinput="false"   name="yearMonth"  class="mini-monthpicker" />
            </div>
            <div field="realCostNew" name="realCostNew"   width="150" headerAlign="center" align="center">实际降本-新口径
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="realCostOld" name="realCostOld"   width="150" headerAlign="center" align="center">实际降本-原口径
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="realProduce" name="realProduce"   width="150" headerAlign="center" align="center">实际产量
                <input property="editor" class="mini-spinner"  minValue="0" maxValue="100000000"  decimalPlaces="0"  />
            </div>
            <div field="planMatCost" name="planMatCost"   width="150" headerAlign="center" align="center">预计材料成本
                <input property="editor" class="mini-spinner"  minValue="0"  allowLimitValue="false"  decimalPlaces="2"/>
            </div>
            <div field="realMatCost" name="realMatCost"   width="150" headerAlign="center" align="center">实际材料成本
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2"/>
            </div>
            <div field="creator" width="80" headerAlign="center" align="center">创建人
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                创建时间
                <input property="editor" class="mini-textbox" readonly/>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    if (!permission) {
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
        mini.get('saveButton').setEnabled(false);
        mini.get('refreshButton').setEnabled(false);
    }
    listGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });

    function addRow() {
        var newRow = {name: "New Row"};
        listGrid.addRow(newRow, 0);
        listGrid.beginEditCell(newRow, "costYear");
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

    function save() {
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].yearMonth) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/monthData/dealData.do",
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
