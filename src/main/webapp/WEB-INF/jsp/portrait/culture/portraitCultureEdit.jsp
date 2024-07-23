<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增培养信息</title>
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
		<form id="cultureForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					培养信息
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
						导师姓名：
					</td>
					<td align="left">
						<input id="recUserSelectId" name="teacherUserId" textname="userName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
							   mainfield="no"  single="false" />
					</td>
					<td align="center" style="white-space: nowrap;">
						徒弟姓名：
					</td>
					<td align="left">
						<input  name="studentUserId" textname="studentName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
							   mainfield="no"  single="true" />
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						确定日期：
					</td>
					<td align="left">
						<input name="conformDate" class="mini-datepicker"  style="width:100%;height:34px"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="score"  name="score" value="1" class="mini-textbox rxc" readonly />
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
	var cultureForm = new mini.Form("#cultureForm");
	var score = 0;
	$(function () {
		if(action!='add'){
			cultureForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			cultureForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		cultureForm.validate();
		if (!cultureForm.isValid()) {
			return;
		}
		var formData = cultureForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/culture/save.do",
			method: 'POST',
			data: formData,
			success: function (result) {
				//如果存在自定义的函数，则回调
				var result=mini.decode(result);
				if(result.success){
					CloseWindow("ok");
				}else{
				};
			}
		}
		_SubmitJson(config);
	}
	function onTypeChanged(e) {
		score = parseFloat(e.selected.value);
		mini.get('score').setValue(score.toFixed(2));
	}
</script>
</body>
</html>
