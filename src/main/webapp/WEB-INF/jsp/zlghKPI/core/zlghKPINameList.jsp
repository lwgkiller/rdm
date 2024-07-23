<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>关键绩效指标</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li style="float: left">
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="addAchievementType()">新增</a>
            <a class="mini-button" iconCls="icon-reload" onclick="refreshAchievementType()" plain="true">刷新</a>
            <a id="removeKpi" class="mini-button btn-red" plain="true" onclick="removeAchievementType()">删除</a>
        </li>
        <span class="separator"></span>
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
    <div id="kpiNameListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/strategicplanning/core/kpilist/kpiNameList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[25,50,100,200]" pageSize="25" allowAlternating="true"
         pagerButtons="#pagerButtons"  allowCellEdit="true" allowCellSelect="true" editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="order" field="order"  align="center" headerAlign="center" width="20">
                序号<span style="color: #ff0000">*</span><input property="editor" class="mini-textbox"/>
            </div>
            <div name="KPI_name" field="KPI_name" width="80" headerAlign="center" align="center" allowSort="true">
                指标名称<span style="color: #ff0000">*</span><input property="editor" class="mini-textbox"/></div>
            <div field="creator" width="60" headerAlign='center' align="center">创建人</div>
            <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url="${ctxPath}/strategicplanning/core/kpilist/kpiNameList.do?KPI_name=";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var isZLGHZY=${isZLGHZY};
    var kpiNameListGrid = mini.get("kpiNameListGrid");
    kpiNameListGrid.load();
    searchKPIName();
    qxkz();
    function searchKPIName() {
        kpiNameListGrid.setUrl(url);
        kpiNameListGrid.load();
    }

    kpiNameListGrid.on("cellbeginedit", function (e) {
        if (!isZLGHZY) {
            if(e.editor) {
                e.editor.setEnabled(false);
                return;
            }
        }
    });

    function qxkz(){
        if (!isZLGHZY){
            $("p").hide();
            mini.get("addKpi").hide();
            mini.get("removeKpi").hide();
            mini.get("saveKpi").hide();
            return;
        }

    }

    function addAchievementType() {
         var newRow = {name: "New Row"};
         kpiNameListGrid.addRow(newRow, 0);
         kpiNameListGrid.beginEditCell(newRow, "order");
    }

    function removeAchievementType() {
        if (!isZLGHZY) {
            mini.alert("没有操作权限!");
            return;
        }
        var rows = kpiNameListGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "刪除此数据对应的绩效指标也会清除，是否确定删除？",
                callback: function (action) {
                    if (action == "ok") {
                        kpiNameListGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function refreshAchievementType() {
        kpiNameListGrid.load();
    }

    kpiNameListGrid.on("beforeload", function (e) {
        if (kpiNameListGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function saveAchievementType() {
         var data = kpiNameListGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].order) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
                if (!data[i].KPI_name) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
                data[i].folk=new Date().getFullYear();
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/strategicplanning/core/kpilist/saveKpiName.do",
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
                    kpiNameListGrid.reload();
                }
            }
        });
    }

    kpiNameListGrid.on("cellcommitedit", function (e) {
    });
    kpiNameListGrid.on("cellbeginedit", function (e) {
    });

</script>
<redxun:gridScript gridId="kpiNameListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
