//单元格字段校验
function onCellValidation(e){
	if(e.field=='userType' && (!e.value||e.value=='')){
		 e.isValid = false;
		 e.errorText='用户类型为空！';
	}
	if(e.field=='calLogic' && (!e.value||e.value=='')){
		e.isValid = false;
		 	e.errorText='计算逻辑不能为空！';
	}
	if(e.field=='sn' && (!e.value||e.value=='')){
		e.isValid = false;
		 	e.errorText='序号不能为空！';
	}
}

function saveTaskUsers(nodeId,solId,actDefId,nodeName){
	//表格检验
	grid.validate();
	if(!grid.isValid()){
    	return;
    }
	_SubmitJson({
		url:__rootPath+'/bpm/core/bpmSolUser/saveNodeUsers.do',
		method:'POST',
		data:{
			nodeId:nodeId,
			solId:solId,
			actDefId:actDefId,
			category:"task",
			nodeText:nodeName,
			usersJson:mini.encode(grid.getData())
		},
		success:function(text){
			grid.load();
		}
	});
}

function addUserRow(){
	var sn=grid.getTotalCount()+1;
	grid.addRow({sn:sn,isCal:'YES',calLogic:'OR'});
}

//删除用户选择行
function delUserRow(){
	var selRow=grid.getSelected();
	grid.removeRow(selRow);
}

//动态设置每列的编辑器
function OnCellBeginEdit(e) {
    var field = e.field,rs=e.record;
    if (field == "userType") {
        e.editor=mini.get('userTypeEditor');
    }else if(field=='config'){
        var tmpEditor=mini.get('configEditor');
        if(!rs.userType||rs.userType=='START-USER'){
        	tmpEditor.setShowButton(false);
        }else{
        	tmpEditor.setShowButton(true);
        }
        e.editor=tmpEditor;
    }
	else if(field=='isCal'){
		var data=[{id:"YES",text:"是"},{id:"NO",text:"否"}];
		if(multi){
			data.push({id:"DELAY",text:"延迟"})
		}
		e.editor.setData(data);
	}
    else if(field=='calLogic'){
    	e.editor=mini.get('logicEditor');
    }else if(field=='sn'){
    	e.editor=mini.get('snEditor');
    }
    e.column.editor=e.editor;
    curGrid=e.sender;
}

var userTypes={
	'START-USER':{config:'{}',configDescp:'发起人'},
	'BPM-INST-USER':{config:'{}',configDescp:'配置的实例用户'}
}

function OnCellEndEdit(e){
	var record=e.record;
	var grid=e.sender;
	
	var field=e.field;
	var val=e.value;
	if(field=='userType'){
		var o=userTypes[val];
		if(!o) return;
		grid.updateRow(record,o);
	}
}

var userTypeDef={
	"USER":function(row){
		var conf={
				single:false,
				showTenant:true,
				callback:function(users){
					var ids=[];
					var names=[];
					for(var i=0;i<users.length;i++){
						ids.push(users[i].userId);
						names.push(users[i].fullname);
					}
					grid.cancelEdit();
					grid.updateRow(row,{config:ids.join(','),configDescp:names.join(',')});
				}
			};
		_UserDialog(conf);
	},
	"GROUP":function(row){
		_GroupDlg(false,function(groups){
			var ids=[];
			var names=[];
			for(var i=0;i<groups.length;i++){
				ids.push(groups[i].groupId);
				names.push(groups[i].name);
			}
			grid.cancelEdit();
			grid.updateRow(row,{config:ids.join(','),configDescp:names.join(',')});
		});
	},
	"VAR":function(row){
		_OpenWindow({
			title:'流程变量选择',
			width:600,
			url:__rootPath+'/bpm/core/bpmSolVar/calUserDlg.do?solId='+row.solId +"&actDefId="+actDefId,
			height:350,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"FORMJSON":function(row){
		console.info(row.nodeId);
		_OpenWindow({
			title:'表单变量选择',
			width:600,
			url:__rootPath+'/bpm/core/bpmSolVar/calFormJsonDlg.do?solId='+solId+'&nodeId='+row.nodeId+"&actDefId="+actDefId,
			height:350,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(encodeURI(row.config));
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"SCRIPT":function(row){
		_OpenWindow({
			title:'脚本配置',
			width:750,
			url:__rootPath+'/bpm/core/bpmSolVar/scriptDlg.do?solId='+row.solId+'&nodeId='+row.nodeId+"&actDefId="+actDefId,
			height:500,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"GROUPSCRIPT":function(row){
		_OpenWindow({
			title:'人员脚本配置',
			width:800,
			url:__rootPath+'/bpm/core/bpmGroupScript/selectBuild.do?solId='+row.solId+'&nodeId='+row.nodeId+"&actDefId="+actDefId,
			height:700,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setConfig(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var scriptData=iframe.getScriptData();
				var scriptText=iframe.getScriptText();
				grid.cancelEdit();
				grid.updateRow(row,{config:scriptData,configDescp:scriptText});
			}
		});
	},
	"USER-REL":function(row){
		_OpenWindow({
			title:'通过用户关系查找',
			width:600,
			height:350,
			url:__rootPath+'/bpm/core/bpmSolUser/byUserRel.do?solId='+row.solId+'&nodeId='+row.nodeId +"&actDefId="+actDefId,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"USER-GROUP-REL":function(row){
		_OpenWindow({
			title:'通过用户与组关系查找',
			width:650,
			height:350,
			url:__rootPath+'/bpm/core/bpmSolUser/byGroupRel.do?solId='+row.solId+'&nodeId='+row.nodeId +"&actDefId="+actDefId,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"USER-CAL-BY-USER-GROUP-REL":function(row){
		_OpenWindow({
			title:'通过组与用户关系查找',
			width:600,
			height:350,
			url:__rootPath+'/bpm/core/bpmSolUser/byGroupUserRel.do?solId='+row.solId+'&nodeId='+row.nodeId +"&actDefId="+actDefId,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"GROUP-RANK-TYPE-REL":function(row){
		_OpenWindow({
			title:'用户来自发起人所在部门往上查找符合等级的部门的关系用户',
			width:600,
			height:350,
			url:__rootPath+'/bpm/core/bpmSolUser/groupRankTypeRel.do?solId='+row.solId+'&nodeId='+row.nodeId +"&actDefId="+actDefId,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"USER_CAL_DEP_GROUP":function(row){
		_OpenWindow({
			title:'用户来自用户所在部门下的用户组用户',
			width:600,
			height:350,
			url:__rootPath+'/bpm/core/bpmSolUser/byDepGroup.do?solId='+row.solId+'&nodeId='+row.nodeId +"&actDefId="+actDefId,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	'PRE_NODE_USER':function(row){
		_OpenWindow({
			title:'流程节点人员来自另一节点审批人',
			width:600,
			url:__rootPath+'/bpm/core/bpmSolUser/preNodeUser.do?solId='+row.solId +"&actDefId="+actDefId,
			height:350,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"UserByDepNameGroupName":function(row){
		_OpenWindow({
			title:'查找某个部门下的用户组下的用户',
			width:600,
			url:__rootPath+'/bpm/core/bpmSolUser/byDepNameGroupName.do?solId='+row.solId +"&actDefId="+actDefId,
			height:350,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getConfigJson();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	},
	"EXT-PROP":function(row){
		_OpenWindow({
			title:'查找用户或者用户组属性下的用户',
			width:900,
			url:__rootPath+'/bpm/core/bpmSolUser/extProp.do?solId='+row.solId +"&actDefId="+actDefId,
			height:600,
			onload:function(){
				var iframe = this.getIFrameEl().contentWindow;
				iframe.setData(row.config);
			},
			ondestroy:function(action){
				if(action!='ok'){return;}
				var iframe = this.getIFrameEl().contentWindow;
				var json=iframe.getRuls();
				grid.cancelEdit();
				grid.updateRow(row,{config:mini.encode(json.config),configDescp:json.configDescp});
			}
		});
	}
		
}


/**
 * 扩展用户类型选择。
 * @param e
 */
function selConfig(e){
	var row=grid.getSelected();
	row.solId=solId;
	var userType=row.userType;
	if(userTypeDef[userType]){
		userTypeDef[userType](row);
	}
}

function onCalcModeRender(e){
	var record = e.record;
	var json={"YES":"是","NO":"否","DELAY":"延迟"};
	return json[record.isCal];
}

function onCalLogicRender(e){
	var record = e.record;
	var json={"OR":"或","AND":"与","NOT":"非"};
	return json[record.calLogic];
}


