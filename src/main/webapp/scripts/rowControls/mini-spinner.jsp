<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>数字框属性</title>
<%@include file="/commons/edit.jsp"%>

</head>
<body>
	<div class="mini-toolbar">
			<a class="mini-button"  onclick="CloseWindow('ok')">保存</a>
			<a class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
	</div>
	<form id="miniForm">
		<table class="table-detail column_2_m" cellspacing="1" cellpadding="1">
			<tr>
				<td>是否必填</td>
				<td>
					<input class="mini-checkbox" name="required" trueValue="true" falseValue="false" value="false">
				</td>
				<td>增量值</td>
				<td>
					<input name="increment" class="mini-spinner" style="width:100px" value="1" />
				</td>
			</tr>
			<tr>
				<td>格式化</td>
				<td colspan="3">
					<input class="mini-textbox" name="format" style="width:68%"/> 
					<br/>数字格式如：,###.00,#.00,#.000
				</td>				
			</tr>
			<tr>
				<td>最小值</td>
				<td>
					<input name="minvalue" class="mini-spinner" style="width:80%" value="0" minValue="-100000000000" maxValue="100000000000"/>
				</td>
				
				<td>最大值</td>
				<td>
					<input name="maxvalue" class="mini-spinner" style="width:80%" value="0" minValue="-100000000000" maxValue="100000000000" />
				</td>
			</tr>
		</table>
	</form>
	<script type="text/javascript">
	
	
		mini.parse();
		
		var form=new mini.Form('miniForm');
		
		function setData(data){
			form.setData(data);
		}
		
		function getData(){
			var formData=form.getData();
			return formData;
		}
	</script>				
</body>
</html>