<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>可靠性活动维护</title>
	<%@include file="/commons/edit.jsp" %>
	<link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
	<div>
		<a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="saveData()">保存</a>
	</div>
</div>
<div class="mini-fit">
	<div class="form-container" style="margin:0 auto; width: 80%;">
		<form id="itemForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					可靠性活动项目信息
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
						项目编号：
					</td>
					<td align="left" colspan="1">
						<input name="projectCode" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						项目名称：
					</td>
					<td align="left">
						<input name="projectName" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						NPI编号：
					</td>
					<td align="left">
						<input name="NPICode" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						负责人：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="response" name="response" textname="responseName" class="mini-user rxc" required="true"
							   plugins="mini-user" style="width:100%" allowinput="false" label="负责人" length="50"
							   mainfield="no"  single="true"
						/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						时间周期：
					</td>
					<td align="left" colspan="1">
						<input name="timeCycle" class="mini-textbox rxc" plugins="mini-textbox"
							   required="true" only_read="false" allowinput="true" value=""  style="width:100%;height:34px"/>
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
		if(action=='view'){
			mini.get('save').setEnabled(false)
			itemForm.setEnabled(false);
		}
	})
	function saveData() {
		itemForm.validate();
		if (!itemForm.isValid()) {
			return;
		}
		var formData = itemForm.getData();
		var config = {
			url: jsUseCtxPath+"/reliable/core/baseInfo/save.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					if(result.data.id){
						mini.get('id').setValue(result.data.id);
					}
				}else{
				};
			}
		}
		_SubmitJson(config);
	}
</script>
</body>
</html>
