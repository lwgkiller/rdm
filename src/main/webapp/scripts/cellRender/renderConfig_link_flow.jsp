<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>关联流程类型的渲染</title>
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
						显示流程标题
					</td>
					<td>
						<div name="showTitle" class="mini-checkbox" value="true" text="显示标题"></div>
					</td>
					<td>
						显示流程明细连接
					</td>
					<td>
						<div name="showBpmInstLink" class="mini-checkbox" value="true" text="显示流程明细连接"></div>
					</td>
				</tr>
				<tr>
					<td>
						显示任务
					</td>
					<td>
						<div name="showTask" class="mini-checkbox" value="true" text="显示任务"></div>
					</td>
					<td>
						显示待办人信息
					</td>
					<td colspan="3">
						<div name="showTaskHandler" class="mini-checkbox" value="true" text="显示待办人信息"></div>
					</td>
				</tr>
			</table>
		</form>
		</div>
	</div>
	<script type="text/javascript">
		mini.parse();
		
		var form=new mini.Form('miniForm');
		
		function setData(data,fieldDts){
			form.setData(data);
		}
		
		function getData(){
			var formData=form.getData();
			return formData;
		}
	</script>				
</body>
</html>