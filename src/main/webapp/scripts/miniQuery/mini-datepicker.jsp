<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>日期选择</title>
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
					<td>
						<span cass="starBox">
							字段备注<span class="star">*</span>
						</span>
					</td>
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
					<td>
						<span cass="starBox">
							字段标识<span class="star">*</span>
						</span>
					</td>
					<td>
						<input id="fieldName" class="mini-combobox" name="fieldName"  allowInput="true"
							textField="header" valueField="field"
							style="width:90%;">
					</td>
					<td>比较类型</td>
					<td>
						<input class="mini-combobox" id="opType" name="opType"  data="opData" textField="fieldOpLabel" valueField="fieldOp">
					</td>
				</tr>
				<tr>
					<td>日期格式</td>
					<td colspan="3">
						<input name="format" class="mini-combobox" style="width:80%"  required="true"
						 data="[{id:'yyyy-MM-dd',text:'yyyy-MM-dd'},{id:'yyyy-MM-dd HH:mm:ss',text:'yyyy-MM-dd HH:mm:ss'}]"  allowInput="false" showNullItem="true" nullItemText="请选择..."/>
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
		            {'fieldOp':'GET','fieldOpLabel':'大于等于'}];
		mini.parse();
		
		var form=new mini.Form('miniForm');
		
		function setData(data,fieldDts){
			form.setData(data);
			mini.get('fieldName').setData(fieldDts);
			mini.get('opType').setText(data.fieldOpLabel);
		}
		
		function getData(){
			var formData=form.getData();
			formData.fieldOpLabel=mini.get('opType').getText();
			return formData;
		}
		
	</script>				
</body>
</html>