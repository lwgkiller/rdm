<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<!DOCTYPE html>
<html>
    <head>
        <title>任务撤销</title>
      <%@include file="/commons/edit.jsp"%>
	</head>
<body>
	<div class="mini-toolbar" style="padding:2px;">
	    <table style="width:100%;">
	        <tr>
		        <td style="width:100%;">
		            <a class="mini-button"   plain="true" onclick="doRevokeCommmu()">撤销</a>
		            <a class="mini-button btn-red" plain="true" onclick="CloseWindow();">关闭</a>
		        </td>
	        </tr>
	     </table>
	</div>
	
	<div class="form-outer"  style="padding:6px;">
		<form id="commuForm">
			<table class="table-detail column_2_m" cellpadding="0" cellspacing="1" style="width:100%" >
				
				<tr>
					<th>意见</th>
					<td>
						<input class="mini-hidden" name="taskId" value="${param.taskId}"/>
						<textarea class="mini-textarea" name="opinion" style="width:80%"  required="true"></textarea>
					</td>
				</tr>
				
			</table>
		</form>
	</div>
	<script type="text/javascript">
		mini.parse();
		
		var form=new mini.Form('commuForm');
		
		function doRevokeCommmu(){
			form.validate();
			if(!form.isValid()){
				return;
			}
			_SubmitJson({
				method : 'POST',
				url:__rootPath+'/bpm/core/bpmTask/revokeCommu.do',
				data:form.getData(),
				success:function(text){
					CloseWindow('ok');	
				}
			});
		}
		
	</script>
</body>
</html>