<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>新增招标规划</title>
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
		<form id="bidPlanForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					荣誉奖项信息
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
						奖项名称：
					</td>
					<td align="left" colspan="3">
						<input name="rewardName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						获奖人：
					</td>
					<td align="left"  colspan="3">
						<input id="recUserSelectId" name="userId" textname="userName" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="姓名"
							   mainfield="no"  single="false" />
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						获奖年度：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="rewardYear" name="rewardYear" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="获奖年度："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getYearList.do"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						奖项级别：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="rewardLevel" name="rewardLevel" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="级别："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100" onvaluechanged="onLevelChanged"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=rewardLevel"
							   nullitemtext="请选择..." emptytext="请选择..."
						/>
					</td>
				</tr>
				<tr>
					<td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
						排名：
					</td>
					<td align="center" colspan="1" rowspan="1">
						<input id="rewardRank" name="rewardRank" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="类别："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..." onvaluechanged="onRankChanged"
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=awardRank"
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
	var bidPlanForm = new mini.Form("#bidPlanForm");
	var rankCombo = mini.get("rewardRank");
	var levelCombo = mini.get("rewardLevel");
	var rankRate = 0;
	var levelRate = 0;
	var score = 0;
	$(function () {
		if(action!='add'){
			bidPlanForm.setData(applyObj);
			mini.get("recUserSelectId").setEnabled(false);
		}
		if (action == 'view') {
			bidPlanForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		bidPlanForm.validate();
		if (!bidPlanForm.isValid()) {
			return;
		}
		var formData = bidPlanForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/reward/save.do",
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
	function onRankChanged(e) {
		rankRate = parseFloat(e.selected.value);
		if(levelRate != 0){
			score = rankRate*levelRate;
		}
		mini.get('score').setValue(score.toFixed(2));
	}
	function onLevelChanged(e) {
		levelRate = parseFloat(e.selected.value);
		if(rankRate != 0){
			score = rankRate*levelRate;
		}
		mini.get('score').setValue(score.toFixed(2));
	}
</script>
</body>
</html>
