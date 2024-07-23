<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>板块维护</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
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
		<form id="itemForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					附属机具分类信息
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
						附属机具板块：
					</td>
					<td align="left" colspan="3">
						<input name="fsjjTypeId" class="mini-combobox" required="true"
							   style="width:100%;" emptyText="请选择..." showNullItem="true" nullItemText="请选择..."
							   textField="plateName" valueField="plateId" emptyText="请选择..."
							   url="${ctxPath}/gkgf/core/fsjj/getDicItem.do"
							   allowInput="false"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						附属机具类别：
					</td>
					<td align="left" colspan="13">
						<input name="typeName" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						排序：
					</td>
					<td align="left" colspan="13">
						<input  name="sortIndex" class="mini-spinner"   allowLimitValue="false"
							   required="false" only_read="false" allowinput="true" allowNull="true"
								value="null"  decimalPlaces="0"  minValue="0" style="width:100%;height:34px"/>
					</td>
				</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>
<script type="text/javascript">
	mini.parse();
	var jsUseCtxPath = "${ctxPath}";
	var applyObj = ${applyObj};
	var action = '${action}';
	var itemForm = new mini.Form("#itemForm");
	$(function () {
		if(action!='add'){
			itemForm.setData(applyObj);
		}
	})
	function saveData() {
		itemForm.validate();
		if (!itemForm.isValid()) {
			return;
		}
		var formData = itemForm.getData();
		var config = {
			url: jsUseCtxPath+"/gkgf/core/fsjj/save.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					CloseWindow('ok');
				}else{
				};
			}
		}
		_SubmitJson(config);
	}
</script>
</body>
</html>
