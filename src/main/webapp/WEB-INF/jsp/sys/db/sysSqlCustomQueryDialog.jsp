<%-- 
    Document   :数据源对话框
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>自定义查询对话框</title>
<%@include file="/commons/list.jsp"%>
</head>
<body>
	<div class="mini-toolbar" id="form1"
		style="text-align: center; line-height: 30px;" borderStyle="border:0;">
			<label>名称：</label> <input class="mini-textbox" style="width:150px;" id="name" name="Q_name_S_LK" onenter="onKeyEnter" /> 
			<label>标识键：</label> <input class="mini-textbox" style="width:150px;" id="key" name="Q_key_S_LK" onenter="onKeyEnter" /> 
			<a class="mini-button"   onclick="search()">查询</a>
	</div>
	<div class="mini-fit">
		<div id="grid1" class="mini-datagrid" url="${ctxPath}/sys/db/sysSqlCustomQuery/search.do"
			style="width: 100%; height: 100%;" idField="id" allowResize="true"
			borderStyle="border-left:0;border-right:0;" multiSelect="false"
			onrowdblclick="onRowDblClick">
			<div property="columns">
				<div type="indexcolumn" width="10%">序号</div>
				<div type="checkcolumn" width="10%"></div>
				<div field="name" width="40%" headerAlign="center" allowSort="true">对话框名称</div>
				<div field="key" width="40%" headerAlign="center" allowSort="true">标识键</div>
			</div>
		</div>
	</div>
	<div class="mini-toolbar"
		style="text-align: center; padding-top: 8px; padding-bottom: 8px;"
		borderStyle="border:0;">
		<a   class="mini-button"  onclick="onOk()">确定</a>
		<a   class="mini-button" onclick="onCancel()">取消</a>
	</div>
	<script type="text/javascript">
		mini.parse();
		var grid = mini.get("grid1");
		grid.load();
		function getData() {
			var row = grid.getSelected();
			return row;
		}
		function search() {
			var name = mini.get("name").getValue();
			var key = mini.get("key").getValue();
			grid.load({
				Q_name_S_LK : name,
				Q_key_S_LK : key
			});
		}
		function onKeyEnter(e) {
			search();
		}
		
		function onRowDblClick(e) {
			onOk();
		}

		function onOk() {
			CloseWindow("ok");
		}
		
		function onCancel() {
			CloseWindow("cancel");
		}
	</script>
</body>
</html>
