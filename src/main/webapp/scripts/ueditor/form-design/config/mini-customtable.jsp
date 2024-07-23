<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<title></title>
<%@include file="/commons/mini.jsp"%>
<link href="${ctxPath}/styles/customform.css" rel="stylesheet" type="text/css" />
	<style>
		.formTemplate,
		.formTemplateTable>tbody>tr>td{
			border:1px dashed #ddd;
		}
		.formTemplateTable .gridHeader, .formTemplateTable .gridBox{
			border: 1px solid #ddd;
		}
		.formTemplateTable .gridBox{
			border: 1px dashed #c0d6ea;
			background: #eef6ff;
		}

	</style>
</head>
<body>
	<div class="form-outer">
		<form id="miniForm">
			<table class="table-detail column-four table-align" cellspacing="0" cellpadding="1">
				<tr>
					<td>选择模板</td>
					<td>
						<div id="templateRadio" class="mini-radiobuttonlist"  onvaluechanged="templateRadio()"
							 textField="text" valueField="id" value="templateOne"
							 data="[{id:'templateOne',text:'模板一'},{id:'templateTwo',text:'模板二'}]" >
						</div>
					<%--	<div id="templateRadio" name="" class="mini-radiobuttonlist"  onvaluechanged="previewHtml2()"
							 textField="text" valueField="id" value=""  data="[{id:'',text:'模板一'},{id:'',text:'模板二'}]" >
						</div>--%>
					</td>
					<td style="background-color: white">行数</td>
					<td>
						<input id="row"
							   name="row"
							   style="width: 100%"
							   allowinput="true"
							   changeOnMousewheel="false"
							   class="mini-spinner"
							   minValue="1"
							   maxValue="50"
							   onvaluechanged="previewHtml()"/>
					</td>
				</tr>
				<tr class="templateOne">
					<td style="background-color: white">列数</td>
					<td>
						<div id="column" name="column" class="mini-radiobuttonlist"  onvaluechanged="previewHtml()"
							 textField="text" valueField="id" value="column-four"
							 data="[{id:'column-two',text:'双列'},{id:'column-four',text:'四列'},{id:'column-six',text:'六列'}]"
						>
						</div>
					</td>
					<td style="background-color: white">样式风格</td>
					<td class="toggleColor">
						<label><input name="color" type="radio" value="transparent" checked onclick="radioChange()"/>透明</label>
						<label><input name="color" type="radio" value="blue" onclick="radioChange()"/>浅蓝</label>
						<label><input name="color" type="radio" value="green"  onclick="radioChange()"/>淡绿</label>
						<label><input name="color" type="radio" value="brown"  onclick="radioChange()"/>树棕</label>
						<label><input name="color" type="radio" value="grey"  onclick="radioChange()"/>银灰</label>
					</td>
				</tr>
				<tr>
					<td>一对一子表</td>
					<td colspan="3">
						<input class="mini-checkbox" name="subOneToOne" id="subOneToOne" onvaluechanged="subOneToOneChange"/>
						<span id="spanSubTableName">
							表名：<input class="mini-textbox" name="subTableName" id="subTableName" width="200"/>
							备注：<input class="mini-textbox" name="comment" id="comment" width="200"/>
						</span>
					</td>
				</tr>
			</table>
		</form>

		<script id="templateList"  type="text/html">
			<# 
			var row=rows;
			if(isPreview) row=2;
			#>
			<div class="tableBox" id="<#=formId#>"
				<# if(subOneToOne){#>
				relationtype="onetoone"
				tablename="<#=subTableName#>"
				comment="<#=comment#>"
				<#}
				else{#> relationtype="main" <#}#>
			>
				<table cellspacing="1" cellpadding="0" class="table-detail <#=column#> <#=colorStyle#> " cellspacing="1" cellpadding="0" >
					<caption>标题</caption>
					<# for(var i=0; i< row + 1; i++){#>
						<#if (column=='column-two'){#>
							<tr><td align="center"></td><td align="left"></td></tr>
						<#}else if(column=='column-four'){#>
							<tr> <td align="center"></td><td align="left"></td><td align="center"></td><td align="left"></td> </tr>
						<#}else if(column=='column-six'){#>
							<tr> <td align="center"></td><td align="left"></td><td align="center"></td><td align="left"></td><td align="center"></td><td align="left"></td></tr>
						<#}else if(column=='column-eight'){#>
							<tr><td align="center"></td><td></td><td align="center"></td><td></td> <td align="center"></td><td></td> <td align="center"></td><td></td></tr>
						<#}
					}
					#>
				</table>
			</div>
		</script>
		<script id="templateList2"  type="text/html">
			<#
			var row=rows;
			if(isPreview) row=1;
			#>
			<div class="formTemplate">
				<div class="tableHeader" >标题名字</div>
				<table class="formTemplateTable" border="" cellspacing="" cellpadding="">
					<# for(var i=0; i< row + 1; i++){#>
						<tr>
							<td align="left" valign="top"><div class="gridSpan" plugins="mini-grid-div"><div class="gridHeader" >名称</div><div class="gridBox" ></div></div></td>
							<td align="left" valign="top"><div class="gridSpan" plugins="mini-grid-div"><div class="gridHeader" >名称</div><div class="gridBox" ></div></div></td>
							<td align="left" valign="top"><div class="gridSpan" plugins="mini-grid-div"><div class="gridHeader" >名称</div><div class="gridBox" ></div></div></td>
						</tr>
					<#}
					#>
				</table>
			</div>
		</script>

		<div id="previewForm"></div>

	</div>
		<script type="text/javascript">
			mini.parse();
			//预览HTML
			previewHtml();
			templateRadio();
			//切换子表是否显示。
			toggleSubOneToOne()
			
	        function buildHtml(isPreview){
	    		var column=mini.get("column").getValue();//列数
	    		var row=mini.get("row").getValue();//行数
	    		var colorStyle=$("input[name='color']:checked").val();
	    		var subTableName=mini.get("subTableName").getValue();
	    		var subOneToOne=mini.get("subOneToOne").getChecked();
	    		var formId="form_" + ( subOneToOne?subTableName:"main") +new Date().format("mmss");
	    		var comment=mini.get("comment").getValue();
	    		var data={rows:row,colorStyle:colorStyle,column:column,isPreview:isPreview,
	    				subOneToOne:subOneToOne,subTableName:subTableName,formId:formId,comment:comment};
	    		var html=baiduTemplate('templateList',data);
	    		console.log(html);
	            return html;
	        }
			function buildHtml2(isPreview){
				var row= mini.get("row").getValue();//行数
	/*			var subTableName=mini.get("subTableName").getValue();
				var subOneToOne=mini.get("subOneToOne").getChecked();
				var formId="form_" + ( subOneToOne?subTableName:"main") +new Date().format("mmss");
				var comment=mini.get("comment").getValue();*/
				var data={rows:row,isPreview:isPreview,};
				var html=baiduTemplate('templateList2',data);
				console.log(html);
				return html;
			}
			function previewHtml(){
				var templateRadio = mini.get("templateRadio").getValue();
				if ( templateRadio.indexOf('templateOne') > -1) {
					$("#previewForm").html(buildHtml(true));
					$("#previewForm").hide();
					$("#previewForm").fadeIn();
				}else {

					$("#previewForm").html(buildHtml2(true));
					$("#previewForm").hide();
					$("#previewForm").fadeIn();
				}

			}
			
			function radioChange(){
				previewHtml();
			}

			/*模板二*/
			function templateRadio(){
				var templateRadio = mini.get("templateRadio");
				var thisVal = templateRadio.getValue();
				if ( thisVal.indexOf('templateOne') > -1) {
					$(".templateOne").show();
					$("#previewForm").empty();
					$("#previewForm").html(buildHtml(true));
					$("#previewForm").hide();
					$("#previewForm").fadeIn();
				}
				else {
					$(".templateOne").hide();
					$("#previewForm").empty();
					$("#previewForm").html(buildHtml2(true));
					$("#previewForm").hide();
					$("#previewForm").fadeIn();
				}
			}

			//确认
			dialog.onok = function (){
				var templateRadio = mini.get("templateRadio");
				var thisVal = templateRadio.getValue();
				if ( thisVal.indexOf('templateOne') > -1) {
					editor.execCommand('insertHtml',buildHtml(false));
				}else {
					editor.execCommand('insertHtml',buildHtml2(false));
				}

			};
			
			function toggleSubOneToOne(){
				var subOneToOne=mini.get("subOneToOne").getChecked();
				var spanSubTableName=$("#spanSubTableName");
				if(subOneToOne){
					spanSubTableName.css("visibility","");
				}
				else{
					spanSubTableName.css("visibility","hidden");
				}
			} 
			
			function subOneToOneChange(){
				toggleSubOneToOne();
			}
		
	</script>
</body>
</html>
