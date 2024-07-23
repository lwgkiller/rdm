<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>战略课题</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">战略课题名称: </span>
            <input id="key" name="KPI_name" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="KPI_name"
               onclick="searchByName()">查询</a>
            <a class="mini-button btn-red" onclick="refreshAchievementType()" plain="true">清空查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>
        <span class="separator"></span>
        <li>
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="openEditorWindow()">新增</a>
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
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/strategicPlanning/core/zlghData/zlkt/list.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons" allowCellSelect="true" allowCellWrap="false">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div name="action" cellCls="actionIcons" width="20" headerAlign="center" align="center" renderer="renderer">操作</div>
            <div name="zljcName" field="zljcName" headerAlign="center" align="center" >
                战略举措名称
            </div>
            <div name="zlktId" filed="zlktId" visible="false"></div>
            <div  name="ktName" field="ktName" width="40" sortField="kt_name" headerAlign="center" align="center" allowSort="true">
                战略课题名称
                <%--<span style="color: #ff0000">*</span>--%>
            </div>
            <div field="creator" width="20" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="30" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url="${ctxPath}/strategicPlanning/core/zlghData/zlkt/list.do";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var isZLGHZY=${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    searchByName();
    qxkz();

    // 操作列渲染
    function renderer(e) {
        let record = e.record;
        var s = '<span title="编辑" onclick="openEditorWindow(\''+record._uid+'\')">编辑</span>';
        s+='<span title="删除" onclick="removeAchievementType()">删除</span>';
        return s;
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
            queryParam.push({name: "ktName", value: cxName});
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
        // searchByName();
        listGrid.load();
    }

    // 权限控制
    function qxkz(){
        if (!isZLGHZY){
            $("p").hide();
            mini.get("addKpi").hide();
            mini.get("removeKpi").hide();
            mini.get("saveKpi").hide();
            return;
        }

    }

    // 新增和编辑弹窗
    function openEditorWindow(uid) {
        let rowData = listGrid.getRowByUID(uid);
        let data = { row: rowData};
        let title;
        if (rowData) {
            title = "编辑战略课题";
            data.state = "modified";
        } else {
            title = "新增战略课题";
            data.state = "added";
        }
        mini.open({
            url: jsUseCtxPath + "/strategicPlanning/core/zlghData/zlkt/formPage.do",
            title: title,
            width: 750,
            height: 350,
            onload: function () {
                   var iframe = this.getIFrameEl();
                   iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                if (action == "ok") {
                    searchByName();
                    // var iframe = this.getIFrameEl();
                    // var data = iframe.contentWindow.GetData();
                    // data = mini.clone(data);    //必须
                    // listGrid.cancelEdit();
                    // var row = listGrid.getSelected();
                    // listGrid.updateRow(row, {
                    //     manager: data.id,
                    //     manager_name: data.name
                    // });

                }
            }
        });
    }

    // 弹出窗编辑器
    function onButtonEdit(e) {
        mini.open({
            url: jsUseCtxPath + "/strategicPlanning/core/zlghData/zlkt/jcPage.do",
            title: "选择战略举措",
            width: 750,
            height: 550,
            onload: function () {
//                    var iframe = this.getIFrameEl();
//                    iframe.contentWindow.SetData(null);
            },
            ondestroy: function (action) {
                if (action == "ok") {
                    var iframe = this.getIFrameEl();
                    var data = iframe.contentWindow.GetData();
                    data = mini.clone(data);    //必须
                    listGrid.cancelEdit();
                    var row = grid.getSelected();
                    listGrid.updateRow(row, {
                        id: data.id,
                        zljcName: data.zljcName
                    });
                }
            }
        });
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
                message: "刪除此数据对应的战略课题和活动也会清除，是否确定删除？",
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

    // 保存数据
    function saveAchievementType() {
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].ktName) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/strategicPlanning/core/zlghData/zlkt/batchOptions.do",
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
    // 冻结
    // listGrid.frozenColumns(2, 2);

    // 合并单元格
    listGrid.on("load", function () {
        listGrid.mergeColumns(["zljcName"])

    });

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
