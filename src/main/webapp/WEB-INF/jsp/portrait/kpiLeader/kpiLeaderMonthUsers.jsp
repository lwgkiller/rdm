<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>挖掘机械研究院所长月度绩效表被考核人员管理</title>
	<%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto">人员名称: </span>
					<input id="bkhUserName" name="bkhUserName" style="width:120px;margin-right: 5px" class="mini-textbox" />
					<span class="text" style="width:auto">岗位: </span>
					<input id="post" name="post" style="width:110px;margin-right: 5px" class="mini-textbox" />
					<span class="text" style="width:auto">类型: </span>
					<input id="type" name="type" property="editor" class="mini-combobox" style="width:auto" textField="id" valueField="text"
						   showNullItem="true" allowInput="false" nullItemText="请选择..." emptyText="请选择..."
						   data="[{id:'归属类型',text:'归属类型'},{id:'非归属类型',text:'非归属类型'}]"
					/>
				</li>
				<li>
					<a class="mini-button" onclick="searchFrm()" style="margin-right: 10px" plain="true">查询</a>
					<a class="mini-button btn-red" plain="true" onclick="clearForm()">清空查询</a>
				</li>
				<span class="separator"></span>
				<li>
					<a class="mini-button" style="margin-right: 10px" onclick="addRow()">新增</a>
					<a class="mini-button btn-red" style="margin-right: 10px" plain="true" onclick="removeRow()">删除</a>
					<a class="mini-button" style="margin-right: 10px" onclick="saveRow()">保存</a>
					<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
						<image src="${ctxPath}/styles/images/warn.png"
							   style="margin-right:5px;vertical-align: middle;height: 15px"/>
						新增、删除、编辑后都需要进行保存操作）
					</p>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="usersListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
		 url="${ctxPath}/kpiLeader/core/getUsersList.do"
		 idField="id" allowAlternating="true" multiSelect="true" allowCellEdit="true"
		 allowCellSelect="true" pagerButtons="#pagerButtons"
		 sizeList="[5,10,20,50,100,200,500]" pageSize="50"
		 editNextOnEnterKey="true" editNextRowCell="true">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
			<div field="bkhUserId" displayField="bkhUserName" width="40" headerAlign="center" align="center">被考核人员名称
				<input name="bkhUserId" textname="bkhUserName"
					   property="editor" class="mini-user rxc" plugins="mini-user"
					   style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"/>
			</div>
			<div field="post" name="post" width="60" headerAlign="center" align="center">岗位
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="type" name="type" headerAlign="center" width="60" align="center">考核类型<span style="color: red">*</span>（归属类型需要打附加分）
				<input property="editor" class="mini-combobox" style="width: 100%;height: 50px" textField="id" valueField="text"
					   showNullItem="true" allowInput="false" nullItemText="请选择..." emptyText="请选择..."
					   data="[{id:'归属类型',text:'归属类型'},{id:'非归属类型',text:'非归属类型'}]"
				/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var usersListGrid = mini.get("usersListGrid");

    usersListGrid.on("beforeload", function (e) {
        if (usersListGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    function addRow() {
        var newRow = {name: "New Row"};
        usersListGrid.addRow(newRow, 0);
    }

    function removeRow() {
        var rows = usersListGrid.getSelecteds();
        if (rows.length > 0) {
            mini.showMessageBox({
                title: "提示信息！",
                iconCls: "mini-messagebox-info",
                buttons: ["ok", "cancel"],
                message: "确定删除?",
                callback: function (action) {
                    if (action == "ok") {
                        usersListGrid.removeRows(rows, false);
                    }
                }
            });

        } else {
            mini.alert("请至少选中一条记录");
            return;
        }
    }

    function saveRow() {
        var data = usersListGrid.getChanges();
        var message = "保存成功";
        var needReload = true;
        debugger
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
                if (!data[i].bkhUserName) {
                    message = "请填写人员名称！";
                    needReload = false;
                    break;
                }
                if (!data[i].post) {
                    message = "请填写人员岗位！";
                    needReload = false;
                    break;
                }
                if (!data[i].type) {
                    message = "请填写人员类型！";
                    needReload = false;
                    break;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/kpiLeader/core/saveUsersList.do",
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
                    usersListGrid.reload();
                }
            }
        });
    }

</script>
<redxun:gridScript gridId="usersListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
