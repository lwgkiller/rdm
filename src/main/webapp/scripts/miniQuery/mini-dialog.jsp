<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<title>对话框</title>
<%@include file="/commons/edit.jsp"%>
</head>
<body>
	<div class="topToolBar">
		<div>
			<a class="mini-button"  onclick="CloseWindow('ok')">保存</a>
			<a class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
		</div>
	</div>
	<div class="mini-fit">
		<div class="form-container">
			<form id="miniForm">
		<table class="table-detail column-four table-align" cellspacing="1" cellpadding="1">
			<tr>
				<td>字段备注*</td>
				<td>
					<input class="mini-textbox" name="fieldLabel" required="true" vtype="maxLength:100,chinese"  style="width:90%" emptytext="请输入字段备注" />
				</td>
				<td>
					空文本提示
				</td>
				<td>
					<input class="mini-textbox" name="emptytext" style="width:90%"/>
				</td>
			</tr>
			<tr>
				<td>字段标识*</td>
				<td>
					<input id="fieldName" class="mini-combobox" name="fieldName"  allowInput="true"
						textField="header" valueField="field"
						style="width:90%;">
				</td>
				<th>比较类型</th>
				<td>
					<input class="mini-combobox" id="opType" name="opType"  data="opData" textField="fieldOpLabel" valueField="fieldOp">
				</td>
			</tr>
			<tr>
				<td>对话框*</td>
				<td colspan="3">
					<input class="mini-buttonedit" id="dialogalias" allowInput="false" name="dialogalias" onbuttonclick="selectDialog" style="width:80%"/>
					<input class="mini-hidden" name="dialogname" id="dialogname"/>
				</td>
			</tr>
			<tr>
				<td>查询值</td>
				<td>
					<input class="mini-combobox" id="valueField" name="valueField" allowInput="true"   textField="field" valueField="field">
				</td>
				<td>显示值</td>
				<td>
					<input class="mini-combobox" id="textField" name="textField" allowInput="true"  textField="field" valueField="field">
				</td>
			</tr>
		</table>
	</form>
		</div>
	</div>
	<script type="text/javascript">
	var opData=[
	        	{'fieldOp':'EQ','fieldOpLabel':'等于'},
				{'fieldOp':'LT','fieldOpLabel':'小于'},
				{'fieldOp':'LET','fieldOpLabel':'小于等于'},
				{'fieldOp':'GT','fieldOpLabel':'大于'},
				{'fieldOp':'GET','fieldOpLabel':'大于等于'},
	            {'fieldOp':'LK','fieldOpLabel':'%模糊匹配%'},
	            {'fieldOp':'LEK','fieldOpLabel':'%左模糊匹配'},
	            {'fieldOp':'RIK','fieldOpLabel':'右模糊匹配%'}];
		mini.parse();
		
		var form=new mini.Form('miniForm');
		
		function setData(data,fieldDts){
			form.setData(data);
			mini.get('fieldName').setData(fieldDts);
			mini.get('opType').setText(data.fieldOpLabel);
			mini.get('dialogalias').setText(data.dialogName);
		}
		function getData(){
			var formData=form.getData();
			formData.fieldOpLabel=mini.get('opType').getText();
			formData.dialogName=mini.get('dialogalias').getText();
			return formData;
		}
		
		function selectDialog(){
			_OpenWindow({
				url:'${ctxPath}/sys/core/sysBoList/dialog.do',
				height:450,
				width:800,
				title:'选择对话框',
				ondestroy:function(action){
					if(action!='ok'){
						return;
					}
					var win=this.getIFrameEl().contentWindow;
					var rs=win.getData();
					if(rs){
						mini.get('dialogalias').setValue(rs.key);
						mini.get('dialogalias').setText(rs.name);
						mini.get('dialogname').setValue(rs.name);
						var headerJson=rs.fieldsJson;
						var comData=mini.decode(headerJson);
						mini.get('valueField').setData(comData);
						mini.get('textField').setData(comData);
					}
				}
			});
		}
	</script>				
</body>
</html>