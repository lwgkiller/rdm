<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>列表中的单元格的枚举文本值显示</title>
<%@include file="/commons/edit.jsp"%>
<script type="text/javascript" src="${ctxPath}/scripts/jquery/plugins/colorpicker/jquery.colorpicker.js"></script> 
<style>

</style>
</head>
<body>
	<div class="topToolBar">
		<div>
		`<a class="mini-button"  onclick="CloseWindow('ok')">保存</a>
<!-- 		<a class="mini-button btn-red" onclick="CloseWindow()">关闭</a> -->

		</div>
	</div>
	<div class="mini-fit">
		<div class="form-container" >
		<form id="miniForm">
			<table class="table-detail column-four table-align" cellspacing="1" cellpadding="1">
				<tr>
					<td>全格展示</td>
					<td colspan="3">
						<div name="isfull" class="mini-checkbox" readOnly="false" text="是"></div>
					</td>
				</tr>
				<tr>
					<td>颜色获取</td>
					<td colspan="3">
						<input type="text" id="fgcolor" name="fgcolor" style="height:26px;border-radius:4px;border:solid 1px #ccc"/>
						【说明】可选择该颜色填写至下表格中
					</td>
				</tr>
				<tr>
					<td>颜色配置</td>
					<td colspan="3" style="padding: 0;">
						<div class="mini-toolbar" style="width:100%">
							<a class="mini-button"  onclick="GetFromSql()">通过SQL获得初始值</a>
							<a class="mini-button"   onclick="addRowGrid('colorGrid')">添加</a>
							<a class="mini-button btn-red"  onclick="delRowGrid('colorGrid')">删除</a>
							<a class="mini-button"  onclick="upRowGrid('colorGrid')">上移</a>
							<a class="mini-button"  onclick="downRowGrid('colorGrid')">下移</a>
						</div>
						<div id="colorGrid" class="mini-datagrid" style="width:100%;min-height: 100px;"
					        allowCellEdit="true" allowCellSelect="true" multiSelect="true" allowAlternating="true" 
					        editNextOnEnterKey="true"  editNextRowCell="true" showPager="false">
					        <div property="columns">
					            <div type="indexcolumn">序号</div>
					            <div type="checkcolumn"></div>
					            <div  field="code"  width="150" >标签键
					                <input property="editor" class="mini-textbox" style="width:100%;"/>
					            </div>
					            <div field="name" width="100"  >标签名
					                 <input property="editor" class="mini-textbox" style="width:100%;"/>
					            </div>            
					            <div field="bgcolor"  width="100">背景颜色
					                <input property="editor" class="mini-textbox" style="width:100%;"/>
					            </div>    
					            <div field="fgcolor"  width="100">字体颜色
					                <input property="editor" class="mini-textbox" style="width:100%;"/>
					            </div>                     
					        </div>
					    </div>
					</td>
				</tr>

			</table>
		</form>
	</div>
	</div>
	<script type="text/javascript">
		addBody();
		mini.parse();
		var form=new mini.Form('miniForm');
		var colorGrid=mini.get('colorGrid');
		$(function(){
			$("#fgcolor").colorpicker({
			    fillcolor:true,
			    success:function(o,color){
			        $('#color_show').css("color",color); 
			    }
			});
		});
		
		function setData(data,fieldDts){
			
			if(data && data.colorConfigs){
				form.setData(data);
				colorGrid.setData(mini.decode(data.colorConfigs));
			}
		}
		
		function GetFromSql(){
			_OpenWindow({
				title:'SQL获得多值',
				height:450,
				width:680,
				max:true,
				url:__rootPath+'/sys/core/sysBoList/treeTabBuild.do?from=displayitem',
				ondestroy:function(action){
					if(action!='ok'){return;}
					var iframe = this.getIFrameEl();
					var win=iframe.contentWindow;
					colorGrid.cancelEdit();
					var data= win.getData();
					var idField=data.idField;
					var textField=data.textField;
					if(!idField || idField=='' || !textField || textField==''){
						return;
					}
					var sampleData=win.getSampleData();
					
					for(var i=0;i<sampleData.length;i++){
						var idVal=sampleData[i][idField];
						var textVal=sampleData[i][textField];
						var gdata=colorGrid.getData();
						var isFound=false;
						for(var c=0;c<gdata.length;c++){
							var row=gdata[i];
							if(row && row.name==idVal){
								colorGrid.updateRow(row,{name:textVal});
								isFound=true;
								break;
							}
						}
						
						if(!isFound){
							colorGrid.addRow({code:idVal,name:textVal});
						}
					}
				}
			});
		}
		
		function getData(){
			var data=form.getData();
			data.colorConfigs=mini.encode(colorGrid.getData());
			return data;
		}
	</script>				
</body>
</html>