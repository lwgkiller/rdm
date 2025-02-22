<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>范围验证</title>
	<%@include file="/commons/get.jsp"%>
</head>
<body>
	<div id="p1" class="form-outer" style="height: 80%">
		<form id="form1" method="post">
			<div class="form-inner">
				<table class="table-detail" cellspacing="1" cellpadding="0">
					<tr>
						<th>最小值</th>
						<td>
							<input class="mini-spinner" name="min" style="width: 90%;" value="0" minValue="-100000000000" maxValue="100000000000" />
						</td>
					</tr>
					<tr>
						<th>最大值</th>
						<td>
							<input class="mini-spinner" name="max" style="width: 90%;" value="0" minValue="-100000000000" maxValue="100000000000" />
						</td>
					</tr>
				</table>
			</div>
		</form>
	</div>
	<div class="mini-toolbar" style="text-align: center;">
		<a class="mini-button"   onclick="onOk()">确定</a>
		<a class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
	
	<script type="text/javascript">
		mini.parse();
		
		var form = new mini.Form("#form1");
		
		function onOk() {
			if(!form.validate())return;
			var data = form.getData();
			if(data.min>data.max){
				alert("最小长度不能大于最大长度");
				return;
			}
			CloseWindow("ok");
		}
		
		function setData(data){
			if(!data)return;
			form.setData(data);
		}
		
		function getData(){
			var data = form.getData();
			if(!data.min){
				data.min = 0;
			}
			if(!data.max){
				data.max = 0;
			}
			return data;
		}
	</script>
</body>
</html>