<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>人员扩展信息</title>
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
		<form id="userInfoForm" method="post">
			<input id="id" name="id" class="mini-hidden"/>
			<input id="userId" name="userId" class="mini-hidden"/>
			<table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
				<caption>
					人员扩展信息
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
						<input name="userName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true"
							   only_read="true" allowinput="false" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						所属部门：
					</td>
					<td align="left">
						<input name="deptName" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true"
							   only_read="true" allowinput="false" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						岗位：
					</td>
					<td align="left">
						<input name="post" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true"
							   only_read="true" allowinput="false" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
					<td align="center" style="white-space: nowrap;">
						职级：
					</td>
					<td align="left">
						<input name="duty" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true"
							   only_read="true" allowinput="false" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						学历：
					</td>
					<td align="left">
						<input id="education" name="education" class="mini-combobox rxc" plugins="mini-combobox"
							   style="width:100%;height:34px"  label="学历："
							   length="50"
							   only_read="false" required="true" allowinput="false" mwidth="100"
							   wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
							   textField="text" valueField="key_" emptyText="请选择..."
							   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=education"
							   nullitemtext="请选择..." emptytext="请选择..."/>
					</td>
					<td align="center" style="white-space: nowrap;">
						专业：
					</td>
					<td align="left">
						<input name="major" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="true"
							   only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
					</td>
				</tr>
				<tr>
					<td align="center" style="white-space: nowrap;">
						是否技术专家：
					</td>
					<td align="left">
						<input  name="expert" class="mini-radiobuttonlist" vtype="maxLength:20" true
								allowInput="false" valueField="key_"  textField="text"
								url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
								value="0"
								style="width: 90%" required="true"  />
					</td>
					<td align="center" style="white-space: nowrap;">
						证书：
					</td>
					<td align="left">
						<input name="qualification" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:50"  onvalidation=""
							   datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput" required="false"
							   only_read="false" allowinput="true" value="" format="" emptytext="" sequence="" scripts=""
							   mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
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
	var userInfoForm = new mini.Form("#userInfoForm");
	$(function () {
		if(action!='add'){
			userInfoForm.setData(applyObj);
		}
		if (action == 'view') {
			userInfoForm.setEnabled(false);
			$('#save').hide();
		}
	})
	function saveData() {
		userInfoForm.validate();
		if (!userInfoForm.isValid()) {
			return;
		}
		var formData = userInfoForm.getData();
		var config = {
			url: jsUseCtxPath+"/portrait/userInfo/save.do",
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
		let knowledgeType = mini.get('knowledgeType').getValue();
		let knowledgeStatus = mini.get('knowledgeStatus').getValue();
		let knowledgeRank = mini.get('ranking').getValue();
		if(knowledgeType&&knowledgeStatus&&knowledgeRank){
			if(knowledgeType=='invention'){
				if(knowledgeStatus=='accept'){
					if(knowledgeRank=='first'){
						score =  1;
					}else{
						score = 0;
					}
				}else if(knowledgeStatus=='authorize'){
					if(knowledgeRank=='first'){
						score =  3;
					}else if(knowledgeRank=='second'){
						score = 1;
					}else if(knowledgeRank=='third'){
						score = 0.5;
					}else{
						score = 0;
					}
				}
			}else if(knowledgeType=='utility'||knowledgeType=='viewDesign'){
				if(knowledgeStatus=='authorize'){
					if(knowledgeRank=='first'){
						score =  1;
					}else{
						score = 0;
					}
				}
			}else if(knowledgeType=='soft'){
				if(knowledgeStatus=='authorize'){
					if(knowledgeRank=='first'){
						score =  2;
					}else {
						score = 0;
					}
				}
			}
			mini.get('score').setValue(score.toFixed(2));
		}
	}
</script>
</body>
</html>
