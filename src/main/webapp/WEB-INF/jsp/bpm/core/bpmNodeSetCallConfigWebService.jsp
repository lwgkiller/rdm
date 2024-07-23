<%@page contentType="text/html" pageEncoding="UTF-8" deferredSyntaxAllowedAsLiteral="true"%>
<!DOCTYPE html>
<html>
<head>
	<title>流程全局事件或节点接口调用配置--WebService调用配置</title>
	<%@include file="/commons/edit.jsp"%>
	<link rel="stylesheet" href="${ctxPath}/scripts/codemirror/lib/codemirror.css">
	<script src="${ctxPath}/scripts/codemirror/lib/codemirror.js"></script>
	<script src="${ctxPath}/scripts/codemirror/mode/sql/sql.js"></script>
</head>
<body>

	<div id="toolbar1" class="mini-toolbar" >
		<a class="mini-button" plain="true"  onclick="CloseWindow('ok')">保存</a>
		<a class="mini-button" plain="true" onclick="CloseWindow()">关闭</a>		
	</div>
	
	<table class="table-detail column_1_m"  cellspacing="1" cellpadding="0" style="width:100%;">
		<caption>WebService调用配置</caption>
		<tr>
			<td colspan="2">
				<ul id="popVarsMenu" style="display:none" class="mini-menu">
		 			<c:forEach items="${configVars}" var="var">
			 			<li iconCls="icon-var"  onclick="appendScript('${var.key}')">${var.name}[${var.key} (${var.type})] </li>
			 		</c:forEach>
			    </ul>
				<div class="mini-toolbar" style="margin-bottom:2px;padding:10px;">
			 		<a class="mini-menubutton "  iconCls="icon-var"  menu="#popVarsMenu" >插入流程变量</a>
			 		<a class="mini-button" iconCls="icon-formAdd" plain="true" onclick="showFormFieldDlg()">从表单中添加</a>
		 		</div>
			</td>
		</tr>
		<tr>
			<td>URL</td>
			<td>
				<textarea id="script" name="script" style="width:100%;height:80px"></textarea>
			</td>
		</tr>
		
		<tr>
			<td>输入参数</td>
			<td>
				<div class="mini-toolbar" style="width:100%">
					<a class="mini-button"   onclick="addRowGrid('inputMapGrid')">添加</a>
					<a class="mini-button btn-red" iconCls="icon-remove" onclick="delRowGrid('inputMapGrid')">删除</a>
					<a class="mini-button" iconCls="icon-up" onclick="upRowGrid('inputMapGrid')">上移</a>
					<a class="mini-button" iconCls="icon-down" onclick="downRowGrid('inputMapGrid')">下移</a>
				</div>
				<div id="inputMapGrid" class="mini-datagrid" style="width:100%;" height="auto" allowAlternating="true"
					allowCellEdit="true" allowCellSelect="true"
					showPager="false" multiSelect="true">
					<div property="columns">
						<div type="indexcolumn" width="60"></div>
						<div type="checkboxcolumn" width="60"></div>
						<div name="paramName" field="Name" width="160">参数Key
							<input property="editor" class="mini-textbox"/>
						</div>
						<div type="comboboxcolumn" field="key" name="key" displayfield="name" width="100" displayfield="">映射变量
							<input property="editor" valueField="key" textField="name" class="mini-combobox" url="${ctxPath}/bpm/core/bpmSolVar/getBySolIdActDefId.do?solId=${param.solId}&actDefId=${param.actDefId}">
						</div>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td>返回XML示例</td>
			<td>
				<textarea class="mini-textarea" name="result" style="width:100%;height:120px;"></textarea>
			</td>
		</tr>
		<tr>
			<td>返回值回写变量</td>
			<td>
				<div class="mini-toolbar" style="width:100%">
					<a class="mini-button" iconCls="icon-edit" onclick="configRowGrid('outputMapGrid')">从结果生成</a>
					<a class="mini-button"   onclick="addRowGrid('outputMapGrid')">添加</a>
					<a class="mini-button btn-red" iconCls="icon-remove" onclick="delRowGrid('outputMapGrid')">删除</a>
					<a class="mini-button" iconCls="icon-up" onclick="upRowGrid('outputMapGrid')">上移</a>
					<a class="mini-button" iconCls="icon-down" onclick="downRowGrid('outputMapGrid')">下移</a>
				</div>
				<div id="outputMapGrid" class="mini-treegrid" style="width:100%;"     
					showTreeIcon="true" height="auto"
					treeColumn="paramName" resultAsTree="true"  
					allowResize="true" expandOnLoad="true">
					<div property="columns">
						<div type="indexcolumn" width="60"></div>
						<div type="checkboxcolumn" width="60"></div>
						<div name="paramKey" field="paramKey" width="160">参数Key</div>
						<div name="paramName" field="paramName" width="160">参数名称（可填）</div>
						<div field="outParam" width="80" displayField="outParamName">输出变量</div>
						<div field="scope" width="80" displayField="outScope">作用域</div>
					</div>
				</div>
			</td>
		</tr>
	</table>
		
	<script type="text/javascript">
		mini.parse();
		var scriptEditor = CodeMirror.fromTextArea(document.getElementById('script'),{
	        lineNumbers: true,
	        matchBrackets: true,
	        mode: "text/x-sql"
		});
		
		scriptEditor.setSize('auto',60);
		
		//在当前活动的tab下的加入脚本内容
		function appendScript(scriptText){
       		var doc = scriptEditor.getDoc();
       		var cursor = doc.getCursor(); // gets the line number in the cursor position
      		doc.replaceRange(scriptText, cursor); // adds a new line
		}
		
		function showFormFieldDlg(){
			openFieldDialog({
				nodeId:'${param.nodeId}',
				actDefId:'${param.actDefId}',
				solId:'${param.solId}',
				callback:function(fields){
					for(var i=0;i<fields.length;i++){
						if(i>0){
							appendScript(' ');
						}
						var f=fields[i].formField.replace('.','_');
						appendScript(f);
					}
				}
			})
		}

		function selDataSource(e) {
			var btnEdit = e.sender;
			openDatasourceDialog(function(data){
				btnEdit.setValue(data.alias);
				btnEdit.setText(data.name);
			})
		}
		
		
		//获得脚本的配置 
		function getData(){
			return scriptEditor.getValue();
		}
		
		function setData(script){
			scriptEditor.setValue(script);
		}
		
	</script>
</body>
</html>
