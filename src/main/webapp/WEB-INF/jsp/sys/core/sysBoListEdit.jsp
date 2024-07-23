
<%-- 
    Document   : [单据数据列表]编辑页
    Created on : 2017-05-21 12:11:18
    Author     : mansan
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>[单据数据列表]编辑</title>
<%@include file="/commons/edit.jsp"%>
<link rel="stylesheet" href="${ctxPath}/scripts/codemirror/lib/codemirror.css">
<script src="${ctxPath}/scripts/codemirror/lib/codemirror.js"></script>
<script src="${ctxPath}/scripts/codemirror/mode/sql/sql.js"></script>
<script src="${ctxPath}/scripts/codemirror/mode/groovy/groovy.js" type="text/javascript"></script>	
<script src="${ctxPath}/scripts/codemirror/addon/edit/matchbrackets.js"></script>
<style type="text/css">
	#tr_freemarkerSql>td .CodeMirror{
	    width: 100% !important;
	    border-top: 1px solid #ececec;
	    box-sizing: border-box;
	}
	#tr_freemarkerSql .mini-toolbar>.mini-button{
		margin: 0 0 8px 8px;
	}
</style>


</head>
<body>
	<div class="topToolBar">
		<div>
	       	<c:if test="${not empty sysBoList.id }">
	  			<a class="mini-button"  plain="true" onclick="onNext">下一步</a>
	     	</c:if>
	     	<a class="mini-button" plain="true" onclick="onSaveNext">保存&下一步</a>
     	</div>
    </div>
	<div class="mini-fit">
		<div class="form-container">
			<form id="form1" method="post">
				<input id="pkId" name="id" class="mini-hidden" value="${sysBoList.id}" />
				<input id="isDialog" name="isDialog" class="mini-hidden" value="${sysBoList.isDialog}" />
				<table class="table-detail column-four table-align" cellspacing="1" cellpadding="0">
					<caption>[单据数据列表]基本信息</caption>
					<c:choose>
						<c:when test="${sysBoList.isDialog!='YES'}">
							<tr>
								<td>表单方案(编辑)*</td>
								<td>
										<input name="formAlias" value="${sysBoList.formAlias}" text="${sysBoList.formName}" allowInput="false" class="mini-buttonedit"
											   onbuttonclick="selectFormSolution" showClose="true" oncloseclick="_ClearButtonEdit" style="width: 100%" />
								</td>
								<td>表单方案(明细)</td>
								<td>
									<input name="formDetailAlias" value="${sysBoList.formDetailAlias}" text="${sysBoList.formDetailName}" allowInput="false"
										   class="mini-buttonedit" onbuttonclick="selectFormSolution" showClose="true" oncloseclick="_ClearButtonEdit" style="width: 100%" />
								</td>
							</tr>
							<tr>
								<td>分　　类 *</td>
								<td>
									<input id="treeId" name="treeId" class="mini-treeselect" url="${ctxPath}/sys/core/sysTree/listByCatKey.do?catKey=CAT_BO_LIST"
										   multiSelect="false" textField="name" valueField="treeId" parentField="parentId"  required="true" value="${sysBoList.treeId}"
										   showFolderCheckBox="false"  expandOnLoad="true" showClose="true" oncloseclick="_ClearButtonEdit"
										   popupWidth="" style="width:100%"/>
								</td>
								<td>
									启用行编辑
								</td>
								<td>
									<ui:radioBoolean name="rowEdit" value="${sysBoList.rowEdit}"/>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>
									<span class="starBox">
										分　　类<span class="star">*</span>
									</span>
								</td>
								<td colspan="3">
									<input id="treeId" name="treeId" class="mini-treeselect" url="${ctxPath}/sys/core/sysTree/listByCatKey.do?catKey=CAT_BO_LIST_DLG"
										   multiSelect="false" textField="name" valueField="treeId" parentField="parentId"  required="true" value="${sysBoList.treeId}"
										   showFolderCheckBox="false"  expandOnLoad="true" showClose="true" oncloseclick="_ClearButtonEdit"
										   popupWidth="300" style="width:300px"/>
								</td>
							</tr>
							<tr>
								<td>高　　度</td>
								<td>
									<input name="height" class="mini-spinner" value="${sysBoList.height}" minValue="100" maxValue="1500"/>
								</td>

								<td>宽　　度</td>
								<td>
									<input name="width" class="mini-spinner" value="${sysBoList.width}" minValue="100" maxValue="1500"/>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
					<tr>
						<td>
							名　　称<span class="star">*</span>
						</td>
						<td>
							<input id="name" name="name" value="${sysBoList.name}" class="mini-textbox" required="true"  style="width: 100%" />
						</td>
						<td>
							标  识  键<span class="star">*</span>
						</td>
						<td>
							<input id="key" name="key" value="${sysBoList.key}" required="true" class="mini-textbox"   style="width: 100%" />
						</td>
					</tr>
					<c:if test="${not empty sysBoList.id}">
					<tr>
						<td>访问地址</td>
						<td colspan="3" >
							<c:choose>
								<c:when test="${sysBoList.isDialog=='YES'}">
									/dev/cus/customData/${sysBoList.key}/dialog.do
								</c:when>
								<c:otherwise>
									/sys/core/sysBoList/${sysBoList.key}/list.do
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					</c:if>

					<tr>
						<td>是否多选</td>
						<td>
							<div class="mini-radiobuttonlist" name="multiSelect" value="${sysBoList.multiSelect}" data="[{id:'true',text:'是'},{id:'false',text:'否'}]"/>
						</td>
						<td>是否分页</td>
						<td>
							<ui:radioBoolean name="isPage" value="${sysBoList.isPage}"/>
						</td>
					</tr>

					<tr>
						<td>启用导航树</td>
						<td>
							<ui:radioBoolean id="isLeftTree" name="isLeftTree" value="${sysBoList.isLeftTree}" onValueChanged="onChangeLeftTree"/>
						</td>
						<td>左树导航名</td>
						<td>
							<input class="mini-textbox" readOnly="true" name="leftNav" id="leftNav" style="width:80%" value="${sysBoList.leftNav}"/>
						</td>
					</tr>
					<tr>
						<td>
							数据展示类型
						</td>
						<td>
							<div class="mini-radiobuttonlist" name="dataStyle" value="${sysBoList.dataStyle}" data="[{id:'list',text:'数据列表'},{id:'tree',text:'树列表'}]"/>
						</td>
						<td>
							SQL构建方式
						</td>
						<td>
							<div class="mini-radiobuttonlist" id="useCondSql" name="useCondSql" value="${sysBoList.useCondSql}" data="[{id:'NO',text:'Freemarker SQL'},{id:'YES',text:'Groovy SQL'}]" onvaluechanged="changeConditionSql"></div>
						</td>
					</tr>
					<tr>
						<td>
							<span>数  据  源</span>
						</td>
						<td>
							<input id="dbAs" name="dbAs" text="${dsName}" value="${sysBoList.dbAs}" class="mini-buttonedit" onbuttonclick="onSelDatasource" oncloseclick="_ClearButtonEdit" showClose="true" style="width: 280px" />
						</td>
						<td>
							<span>是否统计行</span>
						</td>
						<td>
							<ui:radioBoolean name="showSummaryRow" value="${sysBoList.showSummaryRow}"/>
						</td>
					</tr>
					<tr>
						<td>
							<span>是否初始化数据</span>
							<td colspan="3">
								<div class="mini-radiobuttonlist" name="isInitData" required="true" value="${sysBoList.isInitData==null?true:sysBoList.isInitData}" data="[{id:'true',text:'是'},{id:'false',text:'否'}]"/>
							</td>
						</td>
					</tr>
					<tr id="tr_freemarkerSql">
						<td>SQL语句<br/>
						(Freemarker语法)</td>
						<td colspan="3" style="padding: 0">
							<div class="mini-toolbar" style="border-top:none;border-left:none;border-right:none; padding: 8px 0 0 0;">

								<a class="mini-menubutton " plain="true" menu="popupMenu" >插入上下文的SQL条件参数</a>
								<!-- a class="mini-button" plain="true" iconCls="icon-run" onclick="onRun">执行</a-->
								<ul id="popupMenu" class="mini-menu" style="display:none;">
									<li onclick="onSqlExp('{CREATE_BY_}')">当前用户ID</li>
									<li onclick="onSqlExp('{DEP_ID_}')">当前部门ID</li>
									<li onclick="onSqlExp('{TENANT_ID_}')">当前机构ID</li>
								</ul>
								<textarea id="sql" name="sql" cols="60" rows="4" style="width: 90%">${sysBoList.sql}</textarea>
							</div>
						</td>
					</tr>
					<tr id="tr_groovySql" style="display:none">
						<td>Groovy构建SQL</td>
						<td colspan="3" >
							<textarea id="condSqls" name="condSqls">${sysBoList.condSqls}</textarea>
						</td>
					<tr>
					<tr>
						<td>描　　述</td>
						<td colspan="3">
							<textarea name="descp" value="${sysBoList.descp}" class="mini-textarea" style="width:100%;height:80px;" ></textarea>
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
	<rx:formScript formId="form1" baseUrl="sys/core/sysBoList"
		entityName="com.redxun.sys.core.entity.SysBoList" />
	<script type="text/javascript">
		addBody();
		var conditionGrid=mini.get('conditionGrid');
		function onSelDatasource(e){
			var btnEdit=e.sender;
			mini.open({
				url : "${ctxPath}/sys/core/sysDatasource/dialog.do",
				title : "选择数据源",
				width : 650,
				height : 380,
				ondestroy : function(action) {
					if (action == "ok") {
						var iframe = this.getIFrameEl();
						var data = iframe.contentWindow.GetData();
						data = mini.clone(data);
						if (data) {
							btnEdit.setValue(data.alias);
							btnEdit.setText(data.name);
						}
					}
				}
			});
		}
		
		function changeConditionSql(){
			
			var useCondSql=mini.get('useCondSql').getValue();
			
			if(useCondSql=="YES"){
				$("#tr_groovySql").css('display','');
				$("#tr_freemarkerSql").css('display','none');
			}else{
				$("#tr_groovySql").css('display','none');
				$("#tr_freemarkerSql").css('display','');
			}
		}
		
		$(function(){
			onChangeLeftTree();
			changeConditionSql();
		});
		
		function addSqlRow(){
			_OpenWindow({
				title:'SQL编辑',
				url:__rootPath+'/sys/core/sysBoList/conSqlsqlEditor.do',
				height:400,
				width:800,
				max:true,
				ondestroy:function(action){
					if(action!='ok'){
						return ;
					}
					var win=this.getIFrameEl().contentWindow;
					var data=win.getData();
					conditionGrid.addRow(data);
				}
			});
		}
		
		function editSqlRow(){
			var row=conditionGrid.getSelected();
			if(row==null){
				alert('请选择行！');
				return;
			}
			_OpenWindow({
				title:'SQL编辑',
				url:__rootPath+'/sys/core/sysBoList/conSqlsqlEditor.do',
				height:400,
				width:800,
				max:true,
				onload:function(){
					var win=this.getIFrameEl().contentWindow;
					win.setData(row);
				},
				ondestroy:function(action){
					if(action!='ok'){
						return ;
					}
					var win=this.getIFrameEl().contentWindow;
					var data=win.getData();
					conditionGrid.updateRow(row,data);
				}
			});
		}
		
		function removeSqlRow(){
			var rows=conditionGrid.getSelecteds();
			conditionGrid.removeRows(rows);
		}
		
		function selectFormSolution(e){
			var btn=e.sender;
			_OpenWindow({
				title:'表单方案选择',
				height:450,
				width:800,
				url:__rootPath +'/sys/customform/sysCustomFormSetting/dialog.do',
				ondestroy:function(action){
					if(action!='ok'){
						return;
					}
					var win=this.getIFrameEl().contentWindow;
					var data=win.getData();
					if(data!=null){
						btn.setText(data.name);
						btn.setValue(data.alias);
						mini.get('name').setValue(data.name+'列表');
                        mini.get('key').setValue(data.alias+'_list');
					}

					if(data.bodefId!=''){
					    _SubmitJson({
							url:__rootPath+'/sys/bo/sysBoEnt/getMainBoDefId.do?boDefId='+data.bodefId,
                            showProcessTips:false,
                            showMsg:false,
							success:function(result){
							    if(result.data){
							        var val=sqlEditor.getValue();
							        if(val.trim()==''){
                                        sqlEditor.setValue('select * from ' + result.data.tableName);
									}
								}
							}
						});
					}
				}
			});
		}
		
		function onChangeLeftTree(){
			var btn=mini.get('isLeftTree');
			if(btn.getValue()=='YES'){
				mini.get('leftNav').setReadOnly(false);
				mini.get('leftNav').setRequired(true);
			}else{
				mini.get('leftNav').setReadOnly(true);
				mini.get('leftNav').setRequired(false);
			}
		}
		
		var sqlEditor = CodeMirror.fromTextArea(document.getElementById("sql"), {
	        lineNumbers: true,
	        matchBrackets: true,
	        mode: "text/x-sql"
	      });
		sqlEditor.setSize('auto',220);
		
		var groovyEditor = CodeMirror.fromTextArea(document.getElementById("condSqls"), {
	        //lineNumbers: true,
	        matchBrackets: true,
	        mode: "text/x-groovy"
	     });
	     
		groovyEditor.setSize('auto',220);
	
		function onSqlExp(text){
			var doc = sqlEditor.getDoc();
       		var cursor = doc.getCursor(); // gets the line number in the cursor position
      		doc.replaceRange("$"+text, cursor); // adds a new line
		}
		//执行
		function onRun(){
			var sampleGrid=mini.get('sampleDataGrid');
			var ds=mini.get('dbAs').getValue();
			var sql=sqlEditor.getValue();
			_SubmitJson({
				url:__rootPath+'/sys/core/sysBoList/onRun.do',
				data:{
					ds:ds,
					sql:sql
				},
				method:'POST',
				success:function(result){
					
					var fields=result.data.headers;
					var cols=[{type: "indexcolumn"}];
					
					for(var i=0;i<fields.length;i++){
						cols.push({
							header:fields[i].header,
							field:fields[i].field,
							sqlEditor: { type: "textbox"}
						});
					}
					sampleGrid.setUrl(__rootPath+'/sys/core/sysBoList/getDataBySql.do');
					sampleGrid.set({
						columns:cols
					});
					sampleGrid.load({
						ds:ds,
						sql:sql
					});
					$("#resultTr").css('display','');
				}
			});
		}
		
		function onNext(){
			var id='${sysBoList.id}';
			location.href=__rootPath+'/sys/core/sysBoList/edit2.do?id='+id;
		}
		
		function onSaveNext(){
			var form=new mini.Form('form1');
			form.validate();
			if(!form.isValid()){
				return;
			}
			var formData=form.getData();
			/* 
			var dataStyle = formData.dataStyle;
			var rowEdit = formData.rowEdit;
			if(dataStyle=="tree" && rowEdit!="YES"){
				var row = mini.getByName("rowEdit");
				row.setIsValid(false);
				row.setErrorText("树列表需启动行编辑");
				return;
			} 
			*/
			var sql=sqlEditor.getValue();
			var condSqls=groovyEditor.getValue();
			formData.sql=sql;
			formData.condSqls=condSqls;
			_SubmitJson({
				url:__rootPath+'/sys/core/sysBoList/save.do',
				data:formData,
				method:'POST',
				success:function(result){
					var id=result.data.id;
					location.href=__rootPath+'/sys/core/sysBoList/edit2.do?id='+id;
				}
			});
		}		
	</script>
</body>
</html>