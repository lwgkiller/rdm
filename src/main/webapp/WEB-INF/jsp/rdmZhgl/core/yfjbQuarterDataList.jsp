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
         url="${ctxPath}/rdmZhgl/core/yfjb/monthData/quarterListData.do" pageSize="20"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="40">序号</div>
            <div field="year" name="year" dateFormat="yyyy" width="100" headerAlign="center" align="center">年<span
                    style="color: #ff0000">*</span>
                <input property="editor"  allowinput="true" name="year"  class="mini-textbox" />
            </div>
            <div field="deptName"  align="center"  displayField="deptName" headerAlign="center"  width="150">部门<span
                    style="color: #ff0000">*</span>
                <input property="editor"  allowinput="true" name="deptName"  class="mini-textbox" />
<%--                <input property="editor" class="mini-dep rxc" plugins="mini-dep"--%>
<%--                       style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"  single="true" />--%>
            </div>
            <div field="planTimeRateOne" name="planTimeRateOne"   width="180" headerAlign="center" align="center">一季度预计实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="realTimeRateOne" name="realTimeRateOne" width="180" headerAlign="center" align="center">一季度实际实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="planTimeRateTwo" name="planTimeRateTwo"   width="180" headerAlign="center" align="center">二季度预计实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="realTimeRateTwo" name="realTimeRateTwo" width="180" headerAlign="center" align="center">二季度实际实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2" />
            </div>
            <div field="planTimeRateThree" name="planTimeRateThree" width="180" headerAlign="center" align="center">三季度预计实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2"  />
            </div>
            <div field="realTimeRateThree" name="realTimeRateThree" width="180" headerAlign="center" align="center">三季度实际实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0" allowLimitValue="false"  decimalPlaces="2"  />
            </div>
            <div field="planTimeRateFour" name="planTimeRateFour" width="180" headerAlign="center" align="center">四季度预计实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0"  allowLimitValue="false"  decimalPlaces="2"/>
            </div>
            <div field="realTimeRateFour" name="realTimeRateFour" width="180" headerAlign="center" align="center">四季度实际实时降本率（%）
                <input property="editor" class="mini-spinner"  minValue="0"  allowLimitValue="false"  decimalPlaces="2"/>
            </div>
<%--            <div field="creator" width="80" headerAlign="center" align="center">创建人--%>
<%--                <input property="editor" class="mini-textbox" readonly/>--%>
<%--            </div>--%>
<%--            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" align="center" width="100" headerAlign="center">--%>
<%--                创建时间--%>
<%--                <input property="editor" class="mini-textbox" readonly/>--%>
<%--            </div>--%>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    listGrid.on("cellbeginedit", function (e) {
        var record = e.record;
    });

    function addRow() {
        var newRow = {name: "New Row"};
        listGrid.addRow(newRow, 0);
        // listGrid.beginEditCell(newRow, "costYear");
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
                if (!data[i].year) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
                if (!data[i].deptName) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/monthData/dealQuarterData.do",
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
