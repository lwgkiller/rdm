<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>国外供应商</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">供应商名称: </span>
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
         url="${ctxPath}/info/core/enterprise/gw/list.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         allowCellSelect="true" allowCellWrap="false" allowCellEdit="true" editNextOnEnterKey="true"
         oncellvalidation="onCellValidation"
         editNextRowCell="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <%--<div name="action" cellCls="actionIcons" width="20" headerAlign="center" align="center" renderer="renderer">操作</div>--%>
            <div name="area" field="area" width="40" headerAlign="center" align="center">
                所属区域
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="gfName" field="gfName" width="40" headerAlign="center" align="center" >
                供方名称
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="code" field="code" width="15" headerAlign="center" align="center">
                代码
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="is_hg" field="is_hg" width="15" headerAlign="center" align="center"
                 renderer="onValuableRenderer">
                是否合格
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-combobox" textField="text" valueField="id" style="width:100%;"
                       data="valuable"/>
            </div>
            <div name="ptCategory" field="ptCategory" width="20" headerAlign="center" align="center">
                配套类别
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="wlName" field="wlName" width="40" headerAlign="center" align="center">
                物料名称
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="wlCategory" field="wlCategory" width="20" headerAlign="center" align="center">
                物料分类
                <span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox" style="width:100%;"/>
            </div>
            <div name="action" cellCls="actionIcons" width="10" headerAlign="center" align="center"
                 renderer="contactRenderer">联系人
            </div>
            <div name="action" cellCls="actionIcons" width="15" headerAlign="center" align="center"
                 renderer="productDemoRenderer">产品样本
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
    var url = "${ctxPath}/info/core/enterprise/gw/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    searchByName();
    qxkz();

    // 操作列渲染
    function renderer(e) {
        let record = e.record;
        var s = '<span title="编辑" onclick="openEditorWindow(\'' + record._uid + '\')">编辑</span>';
        s += '<span title="删除" onclick="removeAchievementType()">删除</span>';
        return s;
    }

    var valuable = [{id: 0, text: '否'}, {id: 1, text: '是'}];

    // 是否合格渲染
    function onValuableRenderer(e) {
        for (var i = 0, l = valuable.length; i < l; i++) {
            var g = valuable[i];
            if (g.id == e.value) return g.text;
        }
        return "";
    }

    // 联系人列渲染
    function contactRenderer(e) {
        let record = e.record;
        if (record._state === 'added') {
            return "-";
        } else {
            return '<span title="明细" onclick="openContactWindow(\'' + record._uid + '\')">明细</span>';
        }
    }

    // 产品样本列渲染
    function productDemoRenderer(e) {
        let record = e.record;
        if (record._state === 'added') {
            return "-";
        } else {
            return '<span title="明细" onclick="openProductDemoWindow(\'' + record._uid + '\')">明细</span>';
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
            queryParam.push({name: "gfName", value: cxName});
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
        if (!isZLGHZY && currentUserNo!='dongyuzhong' && currentUserNo!='liuchengliang') {
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
        if (isZLGHZY || currentUserNo=='dongyuzhong' || currentUserNo=='liuchengliang') {
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

    // 新增和编辑弹窗-联系人明细
    function openContactWindow(uid) {
        let rowData = listGrid.getRowByUID(uid);
        let data = {row: rowData};
        mini.open({
            url: jsUseCtxPath + "/info/core/enterprise/contacts/page.do",
            title: "编辑供应商联系人",
            width: 1024,
            height: 550,
            onload: function () {
                var iframe = this.getIFrameEl();
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                if (action == "ok") {
                    searchByName();
                }
            }
        });
    }

    // 新增和编辑弹窗-样本附件明细
    function openProductDemoWindow(uid) {
        let rowData = listGrid.getRowByUID(uid);
        let data = {row: rowData};
        mini.open({
            url: jsUseCtxPath + "/info/core/enterprise/files/page.do?id=" + rowData.id,
            title: "编辑供应商样本附件",
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
        var newRow = {
            is_hg: "1"
        };
        listGrid.addRow(newRow, 0);
    }

    // 删除行
    function removeAchievementType() {
        if (!isZLGHZY && currentUserNo!='dongyuzhong' && currentUserNo!='liuchengliang') {
            mini.alert("没有操作权限!");
            return;
        }
        var rows = listGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "刪除此数据对应的联系人和样本也会清除，是否确定删除？",
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
            case "gfName":
                verifyValue(e, "供方名称");
                break;
            case "code":
                verifyValue(e, "代码");
                break;
            case "area":
                verifyValue(e, "所属区域");
                break;
            case "is_hg":
                verifyValue(e, "是否合格");
                break;
            case "ptCategory":
                verifyValue(e, "配套类别");
                break;
            case "wlName":
                verifyValue(e, "物料名称");
                break;
            case "wlCategory":
                verifyValue(e, "物料分类");
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
                url: jsUseCtxPath + "/info/core/enterprise/gw/batchOptions.do",
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


    // 冻结
    // listGrid.frozenColumns(2, 2);

    // 合并单元格
    // listGrid.on("load", function () {
    //     listGrid.mergeColumns(["zljcName"])
    // });

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
