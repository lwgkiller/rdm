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
					板块信息
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
						作业类别：
					</td>
					<td align="left" colspan="1">
						<input name="workTypeId" class="mini-combobox" required="true"
							   style="width:100%;" emptyText="请选择..." showNullItem="true" nullItemText="请选择..."
							   textField="workName" valueField="workId" emptyText="请选择..."
							   url="${ctxPath}/gkgf/core/workType/dictWorkType.do"
							   allowInput="false"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						板块编码：
					</td>
					<td align="left">
						<input name="plateCode" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						板块名称：
					</td>
					<td align="left">
						<input name="plateName" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						责任人：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="responseUserIds" name="responseUserIds" textname="responseUserNames" class="mini-user rxc"
							   plugins="mini-user" style="width:100%" allowinput="false" label="跟踪人" length="50"
							   mainfield="no"  single="false"
						/>
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
			url: jsUseCtxPath+"/gkgf/core/plate/save.do",
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
