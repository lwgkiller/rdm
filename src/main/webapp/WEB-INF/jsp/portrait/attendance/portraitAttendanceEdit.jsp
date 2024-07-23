<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增考勤信息</title>
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
		<form id="attendanceForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					月度考勤信息
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
						姓名：
					</td>
					<td align="left">
						<input id="recUserSelectId" name="userId" textname="userName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
							   mainfield="no"  single="false" />
					</td>
					<td align="center" style="white-space: nowrap;">
						年月：
					</td>
					<td align="left">
						<input id="date1" name="yearMonth" class="mini-monthpicker" required="true"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						排名占比：
					</td>
					<td align="left">
						<input id="attendanceRank" name="attendanceRank" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="排名占比："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..." onvaluechanged="onTypeChanged"
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=attendanceRank"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="score"  name="score" class="mini-textbox rxc" readonly />
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
	var attendanceForm = new mini.Form("#attendanceForm");
	var score = 0;
	$(function () {
		if(action!='add'){
			attendanceForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			attendanceForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		attendanceForm.validate();
		if (!attendanceForm.isValid()) {
			return;
		}
		var formData = attendanceForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/attendance/save.do",
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
