<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增课程信息</title>
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
		<form id="courseForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					课程信息
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
							   mainfield="no"  single="true" />
					</td>
					<td align="center" style="white-space: nowrap;">
						培训课件名称：
					</td>
					<td align="left">
						<input name="courseName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						课程人数：
					</td>
					<td align="center" colspan="1" rowspan="1">
                        <input name="studentNum" id="studentNum"  class="mini-textbox rxc" vtype="int"    onblur="calculateScore" required="true" style="width:100%;height:34px"/>
                    </td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						课时：
					</td>
					<td align="left">
						<input name="courseHour" id="courseHour"   class="mini-textbox rxc" vtype="float"  onblur="calculateScore" required="true" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						课程日期：
					</td>
					<td align="left">
						<input name="courseDate" class="mini-datepicker"  style="width:100%;height:34px"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="score"  name="score" class="mini-textbox rxc" vtype="float" readonly  />
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
	var courseForm = new mini.Form("#courseForm");
	var score = 0;
	$(function () {
		if(action!='add'){
			courseForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			courseForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		courseForm.validate();
		if (!courseForm.isValid()) {
			return;
		}
		var formData = courseForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/course/save.do",
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
	function calculateScore() {
		var studentNum = mini.get('studentNum').getValue();
		var courseHour = mini.get('courseHour').getValue();
		if(studentNum&&courseHour){
			if(studentNum>=20&&courseHour>=1.5){
				score = 0.2;
			}else{
				score = 0.1;
			}
			mini.get('score').setValue(score);
		}
	}
</script>
</body>
</html>
