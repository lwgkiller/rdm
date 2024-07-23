<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>列表中的单元格的文本显示</title>
<%@include file="/commons/edit.jsp"%>
<script type="text/javascript" src="${ctxPath}/scripts/jquery/plugins/colorpicker/jquery.colorpicker.js"></script>
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
					<td>是否为百份比</td>
					<td>

						<div name="isPercent" class="mini-checkbox" checked="true" readOnly="false" text="是"></div>
					</td>

				</tr>
				<tr>
					<td>
						标签背景颜色(*)
					</td>
					<td>
						<input type="text" id="bgcolor" name="bgcolor" style="height:26px;border-radius:4px;border:solid 1px #ccc"/>
					</td>
				</tr>
				<tr>
					<td>
						标签颜色(*)
					</td>
					<td>
						<input type="text" id="fgcolor" name="fgcolor" style="height:26px;border-radius:4px;border:solid 1px #ccc"/>
					</td>
				</tr>
				<tr>
					<td>背景颜色示例</td>
					<td>
						<div id="percentSample" class="percent-bar">
							<div style="width:50%"><span>50%</span></div>
						</div>
					</td>
				</tr>
			</table>
		</form>
		</div>
	</div>
	<script type="text/javascript">
		mini.parse();
		var form=new mini.Form('miniForm');
		
		$(function(){
			$("#bgcolor").colorpicker({
			    fillcolor:true,
			    success:function(o,color){
			        $('#percentSample div').css("background-color",color); 
			    }
			});
			$("#fgcolor").colorpicker({
			    fillcolor:true,
			    success:function(o,color){
			         $('#percentSample div').css("color",color); 
			    }
			});
			
			 
		});
		
		function setData(data,fieldDts){
			form.setData(data);
			$("#bgcolor").val(data.bgcolor);
			$("#fgcolor").val(data.fgcolor);
			
			if(data.bgcolor){
				$('#percentSample div').css("background-color",data.bgcolor);
			}
			if(data.fgcolor){
				$('#percentSample div').css("color",data.fgcolor); 
			}
		}
		
		function getData(){
			var formData=form.getData();
			formData.bgcolor=$("#bgcolor").val();
			formData.fgcolor=$("#fgcolor").val();
			return formData;
		}
	</script>				
</body>
</html>