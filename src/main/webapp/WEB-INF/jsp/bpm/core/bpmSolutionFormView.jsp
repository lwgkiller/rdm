<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="ui" uri="http://www.redxun.cn/formUI"%>
<html>
<head>
	<title>业务流程解决方案管理-流程定义节点配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/share/dialog.js" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/sys/bo/BoUtil.js" type="text/javascript"></script>
	<style>
		body{
			background-color: white;
		}
		.column_2{
			border-collapse: collapse;
		}
		.mini-toolbar{
			text-align:left;
		}
	</style>
</head>
<body>
<div style="display:none;">
	<input
			id="snEditor"
			class="mini-spinner"
			value="1"
			minValue="1"
			maxValue="200"
			style="width:100%"
	/>
	<input
			id="textEditor"
			class="mini-textbox"
			style="width:100%"
	/>

	<ui:dicCombox id="formTypeEditor" name="formTypeEditor" dicKey="_FORM_TYPE" required="true" value=""/>
</div>


<div class="mini-fit" style="margin-top: 0;">

		<div class="form_row">
			<div class="form-toolBox" >
				<span>业务数据模型:</span>
				<input id="btnBoDefId" name="boDefId" class="mini-buttonedit" text="${bodefName}"  allowInput="false"
					   showClose="true" oncloseclick="clearButtonEdit" value="${bodefId }" onbuttonclick="onSelectBo" style="width:300px"/>
				<a class="separator"></a>
				<a class="mini-button"  plain="true" onclick="saveFormView()">保存</a>
				<a class="mini-button" plain="true"  onclick="newFormView()" >新建表单</a>
			</div>

			<div class="form_row_tow">
				<span style="vertical-align:5px;">数据保存方式:</span>
				<div
						id="dataSaveMode"
						name="dataSaveMode"
						class="mini-radiobuttonlist"
						textField="text"
						valueField="id"
						value="${dataSaveMode }"
						data="[{id:'json',text:'实例表'},{id:'db',text:'业务表'},{id:'all',text:'两者'}]"
						repeatItems="4"
						repeatLayout="table"
						repeatDirection="horizontal"
						style="display:inline-block;"></div>
				</div>
			</div>
		<div class="mini-fit">
			<div
					id="formGrid"
					class="mini-datagrid"
					oncellbeginedit="OnCellBeginEdit"
					allowCellEdit="true"
					allowCellSelect="true"
					ondrawgroup="onDrawGroup"
					allowAlternating="true"
					url="${ctxPath}/bpm/core/bpmSolution/getNodeFormView.do?solId=${param.solId}&actDefId=${param.actDefId}"
					idField="Id"
					showPager="false"
					style="height: 100%;"
			>
				<div property="columns">
					<div type="indexcolumn" width="20"></div>
					<div name="action" width="140" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding-left:4px;">#</div>
					<div field="nodeText" width="120" headerAlign="center" >节点名称</div>
					<div field="condForms" name="condForms" renderer="onFormRenderer" width="160" headerAlign="center" >审批表单
						<input
								property="editor"
								name="condForms"
								class="mini-buttonedit"
								emptyText="请选择表单..."
								onclick="setRightForm"
								allowInput=false

								style="width:100%"
						/>
					</div>
					<div field="mobileForms" renderer="onFormRenderer" width="120" headerAlign="center" >手机表单
						<input
								property="editor"
								name="mobileForms"
								class="mini-buttonedit"
								emptyText="请选择表单..."
								onclick="setMobileRightForm"
								allowInput=false
								style="width:100%"
						/>
					</div>
					<div field="sn" name="sn" width="50" headerAlign="center" >序号</div>
				</div>
			</div>
		</div>
</div>
<script type="text/javascript">
	mini.parse();
	var solId='${param.solId}';
	var formGrid=mini.get('formGrid');
	var actDefId='${param.actDefId}';
	formGrid.groupBy("groupTitle", "asc");

	var mobileEditor=null;

	formGrid.load();

	var mobileGrid = mini.get("mobileFormGrid");

	function onDrawGroup(e) {
		e.cellHtml = e.value;
	}

	//tab权限按钮的显示与否
	var showTabOrNot=false;
	function onActionRenderer(e) {
		var record = e.record;
		var field=e.field;
		var uid = record._uid;
		var pkId=record.id;
		var formUrl=record.formUri;
		var title=record.tabTitle;
		if(record.tabTitle&&record.tabTitle.indexOf("#page#")>=0){
			showTabOrNot=true;
		}else if(record.nodeId=="_PROCESS"&&record.tabTitle.indexOf("#page#")<0){
			showTabOrNot=false;
		}

		var s='<span class="icon-button icon-clear" title="表单清空" onclick="formViewClear(\''+uid+'\')"></span>';
		if(showTabOrNot==true){
			s+=' <span class="icon-button icon-tabGrant" title="表单tab权限" onclick="tabGrant(\''+pkId+'\')"></span>';
		}
		s+=' <span class="icon-button icon-boSet" title="业务模型数据设置" onclick="nodeBoSetting(\''+uid+'\')"></span>';

		return s;
	}

	function onFormRenderer(e){
		var record = e.record;
		var field=e.field;
		var mbForms=record[field];

		if(!mbForms) return "";
		var aryForms=mini.decode(mbForms);
		if(aryForms.length==0) return "";

		var aryName=[];
		for(var i=0;i<aryForms.length;i++){
			var form=aryForms[i];
			if(!form.formAlias) continue;
			if(field=="mobileForms"){
				aryName.push( form.formName +"("+form.formAlias+"),");
			}
			else{
				aryName.push("<a href='javascript:;' onclick='showFormEditor(\""+form.formAlias+"\")'>"+ form.formName +"("+form.formAlias+")</a>");
			}

		}
		return aryName.join(",");
	}



	//动态设置每列的编辑器
	function OnCellBeginEdit(e) {
		var record = e.record, field = e.field;
		if(field=='sn'){
			e.editor=mini.get('snEditor');
		}
	}

	//保存表单视图
	function saveFormView(){

		var formData={solId:solId,actDefId:actDefId};
		formData.gridJson=mini.encode(formGrid.getData());

		var boDefId=mini.get("btnBoDefId").getValue();
		var dataSaveMode=mini.get("dataSaveMode").getValue();

		formData.boDefId=boDefId;
		formData.dataSaveMode=dataSaveMode;


		_SubmitJson({
			url:__rootPath+'/bpm/core/bpmSolution/saveFormView.do',
			data:formData,
			method:'POST',
			success:function(text){

				formGrid.reload();
			}
		});
	}


	//显示表单编辑器
	function showFormEditor(formUri){

		_OpenWindow({
			title:'表单编辑管理',
			url:__rootPath+'/bpm/form/bpmFormView/previewByKey/'+formUri+'.do',
			height:450,
			width:800,
			max:true
		});
	}


	function setMobileRightForm(e){
		var sender=e.sender;
		var row=formGrid.getSelected();
		var nodeId=row.nodeId;
		var bodefId=mini.get("btnBoDefId").getValue();
		if(!bodefId){
			mini.showTips({
				content: "<b>提示</b> <br/>请先设置业务模型",
				state: 'warning',
				x: 'center',
				y: 'center',
				timeout: 3000
			});
			return;
		}

		_OpenWindow({
			title:row.nodeText + '权限表单设置',
			height:500,
			width:800,
			max:true,
			url:__rootPath+'/bpm/form/bpmMobileForm/rightViews.do?boDefIds='+bodefId,
			onload:function(){
				this.getIFrameEl().contentWindow.setGridData(row.mobileForms);
			},
			ondestroy:function(action){
				if(action!='ok') return;
				var condForms=this.getIFrameEl().contentWindow.getCondForms();
				formGrid.cancelEdit();
				formGrid.updateRow(row,{mobileForms:condForms});
			}
		});
	}


	function setRightForm(event){
		var sender=event.sender;
		var field=sender.name;
		var row=formGrid.getSelected();
		var nodeId=row.nodeId;
		var bodefId=mini.get("btnBoDefId").getValue();
		_OpenWindow({
			title:row.nodeText + '--表单配置',
			height:500,
			width:800,
			max:true,
			url:__rootPath+'/bpm/form/bpmFormView/rightViews.do?boDefIds='+bodefId+'&solId='+solId+'&actDefId='+actDefId + '&nodeId='+nodeId,
			onload:function(){
				this.getIFrameEl().contentWindow.setGridData(row[field]);
			},
			ondestroy:function(action){
				if(action!='ok') return;
				var condForms=this.getIFrameEl().contentWindow.getCondForms();
				var json={};
				var isUrl=false;
				var jsonAry=mini.decode(condForms);
				for(var i=0;i<jsonAry.length;i++){
					var o=jsonAry[i];
					if(o.type=="SEL-DEV"){
						isUrl=true;
					}
				}
				if(isUrl && jsonAry.length>1){
					alert("URL表单只能配置一个!");
					return ;
				}
				if(isUrl){
					json.formType="SEL-DEV";
				}
				json[field]=condForms;
				formGrid.cancelEdit();
				formGrid.updateRow(row,json);
			}
		});
	}

	function formViewClear(uid){
		var row=formGrid.getRowByUID(uid);
		formGrid.updateRow(row,{condForms: '',printForms:'',mobileForms: ''});
	}



	function onSelectBo(e){
		var btn=e.sender;
		var type=mini.get("dataSaveMode").getValue();
		var ancientBtnValue=[];
		var ancientBtnText=[];
		if(btn.getValue()){
			ancientBtnValue=btn.getValue().split(',');
			ancientBtnText=btn.getText().split(',');
		}

		openBoDefDialog(type,function(action,data){
			var aryVal=[];
			var aryName=[];
			for(var i=0; i < ancientBtnValue.length; i++){
				aryVal.push(ancientBtnValue[i]);
				aryName.push(ancientBtnText[i]);
			}
			//用来设置第一个bo
			var boId='';
			for(var i=0;i<data.length;i++){
				var o=data[i];
				if(i==0&&o.id){
				    boId=o.id;
				}
				if(ancientBtnValue.contains(o.id))continue;
				aryVal.push(o.id);
				aryName.push(o.name);
			}
			btn.setValue(aryVal.join(","));
			btn.setText(aryName.join(","));

			//根据第一个bo，自动去生成该流程的全局、开始、明细、第一个节点的表单等属性配置，以简化配置人员的工作
			if(boId!=''){
				_SubmitJson({
                    showProcessTips:false,
                    showMsg:false,
					url:__rootPath+'/bpm/core/bpmFormRight/doInitFormRights.do?solId='+solId+'&actDefId='+actDefId+'&boDefId='+boId,
					success:function(result){
						var data=result.data;
						var nodeIds=data.nodeIds.join(',');
						delete data.nodeIds;
						var rows=formGrid.getData();
						for(var i=0;i<rows.length;i++){
						    if(rows[i].condForms!=null){
						        var jsonCondForms=mini.decode(rows[i].condForms);
						        if(jsonCondForms && jsonCondForms.length>0) {
                                    continue;
                                }
							}
							if(nodeIds.indexOf(rows[i].nodeId)!=-1){
                                var condForms=[];
                                condForms.push(data);
                                formGrid.updateRow(rows[i],{condForms:mini.encode(condForms)});
							}
						}
					}
				});
			}
		},true);
	}





	/*表单tab的权限*/
	function  tabGrant(pkId){
		var boDefId=mini.get("btnBoDefId").getValue();
		if(!boDefId){
			mini.showTips({
				content: "<b>提示</b> <br/>请先设置业务模型",
				state: 'warning',
				x: 'center',
				y: 'center',
				timeout: 3000
			});
			return;
		}

		_OpenWindow({
			url:__rootPath+'/bpm/core/bpmSolFv/formViewTabRights.do?pkId='+pkId,
			height:400,
			width:700,
			title:'tab权限页面',
			ondestroy:function(action){
				var iframe = this.getIFrameEl();
				var tabRight = iframe.contentWindow.getTabRight();
				var row = formGrid.findRow(function(row){
					if(row.id==pkId) return true;
				});
				row.tabRights=tabRight;
				formGrid.updateRow(row,row);
			}
		});


	}

	/*节点BO设置*/
	function  nodeBoSetting(uid){
		var boDefId=mini.get("btnBoDefId").getValue();
		var row=formGrid.getRowByUID(uid);

		if(!boDefId){
			mini.showTips({
				content: "<b>提示</b> <br/>请先设置业务数据模型",
				state: 'warning',
				x: 'center',
				y: 'center',
				timeout: 3000
			});
			return;
		}

		_OpenWindow({
			url:__rootPath+'/bpm/core/bpmSolFv/boSetting.do?solId='+solId+'&nodeId='+row.nodeId+'&boDefId='+boDefId + '&actDefId='+actDefId,
			height:400,
			width:700,
			title:'节点业务数据模型设置',
			ondestroy:function(action){
				var iframe = this.getIFrameEl();
				if(action!='ok') return;

				var iframe = this.getIFrameEl();
				var dataConfs = iframe.contentWindow.getDataConf();
				var json={dataConfs:dataConfs};
				formGrid.updateRow(row,json);
			}
		});

	}
	//在线新建流程表单
	function newFormView() {
		_OpenWindow({
			url: __rootPath + "/bpm/form/bpmFormView/edit.do",
			title: "新增业务表单视图",
			max: true,
			ondestroy: function (action) {
			}
		});
	}

</script>
</body>
</html>