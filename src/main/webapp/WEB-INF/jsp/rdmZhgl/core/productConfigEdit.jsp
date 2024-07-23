<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
	<script src="${ctxPath}/scripts/rdmZhgl/productConfigEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="saveData()">保存</a>
		<a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 100%;">
		<form id="planForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					配置信息
				</caption>
				<tbody>
				<tr class="firstRow displayTr">
					<td align="center"></td>
					<td align="left"></td>
					<td align="center"></td>
					<td align="left"></td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						部门名称<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="deptName" class="mini-textbox rxc" style="width:100%;height:34px" >
					</td>
					<td align="center" style="white-space: nowrap;">
						排序号<span style="color: #ff0000">*</span>：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input name="sort"  class="mini-spinner" style="width:100%;height:34px" >
					</td>
				</tr>
				</tbody>
			</table>
		</form>

		<div class="mini-toolbar">
			<ul class="toolBtnBox">
				<li style="float: left">
					<a class="mini-button"  onclick="addItem()">新增</a>
					<a class="mini-button btn-red" plain="true" onclick="removeRow()">删除</a>
				</li>
			</ul>
		</div>
		<div class="mini-fit" style="height: 100%;margin-top: 10px">
			<div id="itemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
				 url="${ctxPath}/rdmZhgl/core/productConfig/items.do"
				 idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
				 allowCellSelect="true" allowCellWrap="true"
				 editNextOnEnterKey="true" editNextRowCell="true">
				<div property="columns">
					<div type="checkcolumn" width="10"></div>
					<div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
					<div field="itemName" displayfield="itemName" width="80" headerAlign="center" align="center">节点名称<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textbox"   allowLimitValue="false"
							   required="true" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="itemCode" displayfield="itemCode" width="80" headerAlign="center" align="center">节点编码<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-textbox"   allowLimitValue="false"
							   required="true" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
					<div field="deliveryId" displayField="deliveryName"  headerAlign="center" align="center" width="120">项目交付物
						<input property="editor" class="mini-combobox" textField="deliveryName" valueField="deliveryId"
							   emptyText="请选择..."
							   showNullItem="true" nullItemText="请选择..." multiSelect="false"
							   url="${ctxPath}/rdmZhgl/core/productConfig/deliverList.do?categoryId=02"/>
					</div>
					<div field="deptId"  align="center"  displayField="deptNames" headerAlign="center" >部门
						<input property="editor" class="mini-dep rxc" plugins="mini-dep"  style="width:90%;height:34px;" allowinput="false" length="500" maxlength="500"  mainfield="no"  single="false" />
					</div>
					<div field="sort" displayfield="sort" width="80" headerAlign="center" align="center">排序号<span
							style="color: #ff0000">*</span>
						<input property="editor" class="mini-spinner"   allowLimitValue="false"
							   required="true" only_read="false" allowinput="true"allowNull="true" value="null"  decimalPlaces="0"  style="width:100%;height:34px"/>
					</div>
				</div>
			</div>
		</div>
	</div>


</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var action = '${action}';
	var planForm = new mini.Form("#planForm");
	var itemGrid = mini.get("itemGrid");

</script>
</body>
</html>
