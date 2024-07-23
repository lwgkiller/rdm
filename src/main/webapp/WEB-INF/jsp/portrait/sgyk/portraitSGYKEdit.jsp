<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增三高一可信息</title>
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
		<form id="noticeForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					三高一可信息
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
						指标名称：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="indexId" name="indexId" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="通报编号："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="value" emptyText="请选择..." onvaluechanged="onTypeChanged"
							   url="${ctxPath}/portrait/index/indexDic.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						指标目标值：
					</td>
					<td align="left">
						<input id="indexValue"  name="indexValue" class="mini-textbox rxc" readonly />
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						得分标准：
					</td>
					<td align="left" colspan="3">
						<textarea class="mini-textarea" id="scoreRule" name="scoreRule" style="width:100%;height:100px"  readonly></textarea>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						指标分值：
					</td>
					<td align="left">
						<input id="indexScore"  name="indexScore" class="mini-textbox rxc" readonly />
					</td>
					<td align="center" style="white-space: nowrap;">
						完成率：
					</td>
					<td align="left">
						<input  id="finishRate" name="finishRate"  class="mini-textbox rxc"  onblur="calculateScore" required="true" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						权重：
					</td>
					<td align="left">
						<input name="weight" id="weight"   class="mini-textbox rxc" onblur="calculateScore" required="true" style="width:100%;height:34px"/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						最终得分：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="score"  name="score" class="mini-textbox rxc"  />
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
	var noticeForm = new mini.Form("#noticeForm");
	var typeCombo = mini.get("noticeType");
	var score = 0;
	$(function () {
		if(action!='add'){
			noticeForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			noticeForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		noticeForm.validate();
		if (!noticeForm.isValid()) {
			return;
		}
		var formData = noticeForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/sgyk/save.do",
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
		mini.get('indexValue').setValue(e.selected.indexValue);
		mini.get('scoreRule').setValue(e.selected.scoreRule);
		mini.get('indexScore').setValue(e.selected.indexScore);
	}
	function calculateScore() {
		var indexScore = mini.get('indexScore').getValue();
		var finishRate = mini.get('finishRate').getValue();
		var weight = mini.get('weight').getValue();
		if(indexScore&&finishRate&&weight){
			score = indexScore*finishRate*weight;
			mini.get('score').setValue(score.toFixed(2));
		}
	}
</script>
</body>
</html>
