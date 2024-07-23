<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目评价等级配置</title>
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
            <a class="mini-button" iconCls="icon-add" onclick="addAchievementType()"><spring:message code="page.configAchievementType.name" /></a>
            <a class="mini-button" iconCls="icon-reload" onclick="refreshAchievementType()" plain="true"><spring:message code="page.configAchievementType.name1" /></a>
            <a class="mini-button btn-red" plain="true" onclick="removeAchievementType()"><spring:message code="page.configAchievementType.name2" /></a>
        </li>
        <span class="separator"></span>
        <li>
            <a class="mini-button" iconCls="icon-save" onclick="saveAchievementType()"><spring:message code="page.configAchievementType.name3" /></a>
            <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                <spring:message code="page.configAchievementType.name4" />）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="achievementGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/xcmgProjectManager/core/config/achievementTypeList.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="20"><spring:message code="page.configAchievementType.name5" /></div>
            <div field="catagoryName" name="catagoryName" width="100" headerAlign="center" align="center"><spring:message code="page.configAchievementType.name6" /><span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
            <div field="sequence" name="sequence" width="60" headerAlign="center" align="center"><spring:message code="page.configAchievementType.name7" /><span
                    style="color: #ff0000">*</span>
                <input property="editor" class="mini-spinner" minValue="0" maxValue="1000"/>
            </div>
            <div field="creator" width="80" headerAlign="center" align="center"><spring:message code="page.configAchievementType.name8" />
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                <spring:message code="page.configAchievementType.name9" />
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="updator" width="80" headerAlign="center" align="center"><spring:message code="page.configAchievementType.name10" />
                <input property="editor" class="mini-textbox" readonly/>
            </div>
            <div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center">
                <spring:message code="page.configAchievementType.name11" />
                <input property="editor" class="mini-textbox" readonly/>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});

    var achievementGrid = mini.get("achievementGrid");
    achievementGrid.load();

    achievementGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if (!isManager) {
            e.editor.setEnabled(false);
        }
    });

    function addAchievementType() {
        if (!isManager) {
            mini.alert(configAchievementType_name);
            return;
        }
        var newRow = {name: "New Row"};
        achievementGrid.addRow(newRow, 0);
        achievementGrid.beginEditCell(newRow, "ratingName");
    }

    function removeAchievementType() {
        if (!isManager) {
            mini.alert(configAchievementType_name);
            return;
        }
        var rows = achievementGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: configAchievementType_name1,
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: configAchievementType_name2,
                callback: function (action) {
                    if (action == "ok") {
                        achievementGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert(configAchievementType_name3);
            return;
        }
    }

    function refreshAchievementType() {
        achievementGrid.load();
    }

    achievementGrid.on("beforeload", function (e) {
        if (achievementGrid.getChanges().length > 0) {
            if (confirm(configAchievementType_name4)) {
                e.cancel = true;
            }
        }
    });

    function saveAchievementType() {
        var data = achievementGrid.getChanges();
        var message = configAchievementType_name5;
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].catagoryName || !data[i].sequence) {
                    message = configAchievementType_name6;
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/xcmgProjectManager/core/config/saveAchievementType.do",
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
            title: configAchievementType_name7,
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    achievementGrid.reload();
                }
            }
        });
    }

    achievementGrid.on("cellcommitedit", function (e) {
    });

</script>
</body>
</html>