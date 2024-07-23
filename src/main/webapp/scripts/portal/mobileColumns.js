Vue.component('checkbox-list', {
	props: ['items',
		'value'],
	data: function () {
		return {
			checkItem:[]
		}
	},
	created:function(){
		this.checkItem=this.value.split(",");

		for(var i=0;i<items.length;i++){
			var item=items[i];
			for(var j=0;j<this.checkItem.length;j++){
				if(item.value==this.checkItem[j]){
					item.checked=true;
				}
			}
		}
	},
	template: "<ul class='checkbox-list'><li v-for='(item, index) in items'> <input @change='change' type='checkbox' :checked='item.checked' ref='input' :value='item.id' />{{item.text}}</li></ul>",
	methods:{
		change:function(){
			var checkedArray = this.$refs.input;
			var tmpAry=[];
			for(var i=0;i<checkedArray.length;i++){
				var o=checkedArray[i];
				if(o.checked){
					tmpAry.push(o.value);
				}
			}
			var tmp=tmpAry.join(",");

			this.$emit('input', tmp);
		}
	}
})


var dataTypeAry=[{name:"字符串",val:"varchar"},{name:"数字",val:"number"}];
var dataTxtAry=[{name:"字符串",val:"varchar"},{name:"大字段",val:"clob"}];
var dataVarcharAry=[{name:"字符串",val:"varchar"},{name:"大字段",val:"clob"}];
var dataDateAry=[{name:"日期",val:"date"}];

var controlAry=[{name:"单行文本框",val:"mini-textbox",showDataType:true,dataType:dataTypeAry,defaultDataType:"varchar",showLength:true,defaultLen:50,needRequire:true,ext:["allowinput","vtype","rtype","rtypeName","format","from","sequence","script"]},
	{name:"多行文本框",val:"mini-textarea",showDataType:true,dataType:dataTxtAry,defaultDataType:"varchar",showLength:true,defaultLen:200,needRequire:true,ext:["allowinput","minlen"]},
	{name:"复选框",val:"mini-checkbox",showDataType:false,dataType:dataVarcharAry,defaultDataType:"varchar",defaultLen:20,showLength:true,needRequire:true,ext:[]},
	{name:"单选按钮",val:"mini-radiobuttonlist",showDataType:false,dataType:dataVarcharAry,defaultDataType:"varchar",defaultLen:20,showLength:true,needRequire:true,ext:["from","data","url","textfield","valuefield","sqlName","sqlKey","defaultvalue","dicKey","dicName","refField"]},
	{name:"复选框列表",val:"mini-checkboxlist",showDataType:false,dataType:dataVarcharAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["from","data","url","textfield","valuefield","sqlName","sqlKey","defaultvalue","dicKey","dicName","refField"]},
	{name:"下拉框",val:"mini-combobox",showDataType:false,dataType:dataVarcharAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["from","data","url","textfield","valuefield","sqlName","sqlKey","defaultvalue","dicKey","dicName","refField"]},
	{name:"日期控件",val:"mini-datepicker",showDataType:false,dataType:dataDateAry,defaultDataType:"date",defaultLen:0,showLength:false,needRequire:true,ext:["showtime","showokbutton","showclearbutton","allowinput","initcurtime","format"]},
	{name:"月份控件",val:"mini-month",showDataType:false,dataType:dataTypeAry,defaultDataType:"varchar",defaultLen:0,showLength:false,needRequire:true,ext:["allowinput","initcurtime"]},
	{name:"时间控件",val:"mini-time",showDataType:false,dataType:dataTypeAry,defaultDataType:"varchar",defaultLen:0,showLength:false,needRequire:true,ext:["allowinput","initcurtime","format"]},
	{name:"数字输入框",val:"mini-spinner",showDataType:false,defaultDataType:"number",defaultLen:0,showLength:false,needRequire:true,ext:["minvalue","maxvalue","increment","allowinput","format"]},
	{name:"富文本框",val:"mini-ueditor",showDataType:true,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:200,showLength:true,needRequire:true,ext:[]},
	{name:"用户选择",val:"mini-user",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["single","initloginuser","refField"]},
	{name:"用户组选择",val:"mini-group",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["single","initlogingroup","showdim","dimId","level","refField"]},
	{name:"文件上传",val:"upload-panel",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:200,showLength:true,needRequire:true,ext:["single","sizelimit","filetype"]},
	{name:"隐藏域",val:"mini-hidden",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:false,ext:[]},
	{name:"编辑型按钮",val:"mini-buttonedit",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:[]},
	{name:"部门选择",val:"mini-dep",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["single","initlogindep","level","refconfig","groupids","groupidsText","level","refField"]},
	{name:"树形选择框",val:"mini-treeselect",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:50,showLength:true,needRequire:true,ext:["from","url","textfield","valuefield","parentfield","customSqlName","customSql","checkrecursive","showfoldercheckbox","autocheckparent","expand","expandType","layer","multiselect","refField"]},
	{name:"文本盒子",val:"mini-textboxlist",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:100,showLength:true,needRequire:true,ext:["allowinput","isurl","url","data","textfield","valuefield"]},
	{name:"office控件",val:"mini-office",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:200,showLength:false,needRequire:true,ext:["version"]},
	{name:"图片控件",val:"mini-img",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:100,showLength:false,needRequire:true,ext:["imgtype"]},
	{name:"引用字段",val:"mini-ref",showDataType:false,dataType:dataTxtAry,defaultDataType:"varchar",defaultLen:100,showLength:false,needRequire:true,ext:[]},
	{name:"标准字段",val:"mini-commonfield",showDataType:false,dataType:dataTxtAry,defaultDataType:"default",defaultLen:100,showLength:false,needRequire:true,ext:["refField"]},
];

var controlMap={};
for(var i=0;i<controlAry.length;i++){
	var o=controlAry[i];
	controlMap[o.val]=o;
}

//originName 已保存字段名, 记录原来字段名称，在服务端判断的时候 获取 原来的字段名称，确定是新增，添加还是删除
var _FieldDefault={
	name:"",
	originName:"",
	comment:"",
	control:"",
	dataType:"varchar",
	length:50,
	decimalLength:0,

	extJson:{
		required:false,
		data:[],

		vtype:"",
		rtype:"",
		rtypeName:"",
		//radiolist,combox,checkboxlist
		from:"",
		data:[],
		dicName:"",
		dicKey:"",
		sqlName:"",
		sqlKey:"",
		sqlParent:"",
		sqlParam:"",
		textfield:"",
		valuefield:"",
		defaultvalue:"",

		//date
		showtime:false,
		showokbutton:false,
		showclearbutton:false,
		allowinput:true,
		initcurtime:false,
		//month
		//time
		format:"",
		//spinner
		minvalue:1,
		maxvalue:100,
		increment:1,
		//user
		single:true,
		initloginuser:true,
		refField:'',
		//group
		initlogingroup:true,
		showdim:true,
		dimid:"1",
		level:"",

		showdim:true,

		filetype:"",
		sizelimit:50,

		initlogindep:true,
		refconfig:"",
		groupidsText:"",
		groupids:"",

		version:true,
		imgtype:"upload",

		src_:"URL",
		checkrecursive:false,
		showfoldercheckbox:false,
		autocheckparent:false,
		expand:false,
		expandType:"all",
		layer:0,
		multiselect:false,
		parentfield:"",
		customSqlName:"",
		customSql:"",

		isurl:"",
		//textox
		sequence:"",
		script:"",

		//checkbox
		truevalue:"是" ,
		falsevalue:"否" ,
		checked:false ,
		//用户字段
		relField:"",
	}
}

function saveAttr(){

	vm.errMsg="";
	var isMainBoEnt=vm.isMainBoEnt;
	var row=$.clone(vm.attr);
	if(row.control.length==0 && isMainBoEnt) {
		vm.errMsg="请输入控件类型";
		return;
	};
	if(row.name.length==0 && isMainBoEnt) {
		vm.errMsg="请输入字段名";
		return;
	};

	var rows=grid.getData();
	var isExist=false;

	for(var i=0;i<rows.length;i++){
		var o=rows[i];
		if(row.name==o.name || (!isAdd_ && o.originName==row.originName)){
			isExist=true;
			row.originName = row.name;
			grid.updateRow(o,row);
		}
	}

	if(!isExist){
		if(isMainBoEnt==1){
			row.originName = row.name;
			grid.addRow(row);
		}
		else{
			vm.errMsg="复制的实体不能添加字段!";
		}
	}

	vm.attr=$.clone(_FieldDefault);

}

function onDel(e){
	var rows=grid.getSelecteds();
	if(rows.length==0) return;
	grid.removeRows(rows,true);
}


function renderDataType(e){
	var record = e.record;
	return getDataType(record);
}

function getDataType(record){
	var val = record.dataType;
	if(val=="varchar"){
		return val +"("+record.length+")";
	}
	else if(val=="number"){
		if(record.decimalLength==0){
			return val +"("+record.length+")";
		}
		else{
			return val +"("+record.length +","+record.decimalLength+")"
		}
	}
	return val;
}

function rowClick(e){

	var row=e.record;
	var type=row.control;
	vm.changeControlType(type);
	var defaultJson=$.clone(_FieldDefault);
	var obj=$.clone(row);
	$.extend(defaultJson, obj );

	vm.attr=defaultJson;

	//
}

var isAdd_=true;

function initEnt(entId){
	var form=new mini.Form("#formDiv");
	if(!entId) return ;
	isAdd_=false;
	var url=__rootPath +"/sys/bo/sysBoEnt/getByEntId/"+entId+".do";
	$.get(url,function(data){
		var rows=data.sysBoAttrs;
		for(var i=0;i<rows.length;i++){
			var row=rows[i];
			//复制
			row.originName=row.name;
			row.extJson=eval("(" +row.extJson +")");
			if(!row.extJson.data){
				row.extJson.data=[];
			}
		}
		grid.setData(rows);
		form.setData(data);
		var isMain=data.isMain==1;
		var txtName=mini.get("txtName");
		var btnDel=mini.get("btnDel");
		var genTableCtl=mini.get("genTable");
		var thGenTable=$("#thGenTable");
		var tdGenTable=$("#tdGenTable");

		var tdFromDb=$("#tdFromDb");

		var genTable=data.genTable;
		var genMode=data.genMode;

		if(genMode=="db"){
			tdFromDb.show();
			var pkField=mini.get("pkField");
			var fkField=mini.get("fkField");
			var parentField=mini.get("parentField");
			var pkRows=$.clone(rows);
			var fkRows=$.clone(rows);
			var parentRows=$.clone(rows);
			pkField.setData(pkRows);
			fkField.setData(fkRows);
			parentField.setData(parentRows);

		}


		if(genTable=="yes"){
			tdGenTable.hide();
			thGenTable.html("已生成表");
		}
		else{
			if(isMain){
				tdGenTable.show();
			}
			else{
				thGenTable.html("未生成表");
				tdGenTable.hide();
			}
		}

		if(isMain){
			btnDel.setVisible(true);
		}
		else{
			btnDel.setVisible(false);
		}
		var canEditName=false;
		if(isMain && genTable=="no"){
			canEditName=true;
		}
		txtName.setAllowInput(canEditName);
		mini.get("delFromEnt").setValue(1);

		vm.isMainBoEnt=data.isMain;
		vm.genMode=data.genMode;
		vm.delFromEnt=1;

		var list=$.clone(rows);
		vm.fileds=list;
	})
}

function initColumns(){
	var defaultAttr=$.clone(_FieldDefault);
	return new Vue({
		el: '#mobileColumns',
		data: {
            columns:[]
		},
		created:function(){
		},

		methods:{

		},
		watch:{

		}
	})
}

function getPinyin(e){
	var val=mini.get("comment").getValue();
	if(!val) return;
	convertToPy(val,function(result){
		var obj=mini.get("txtName");
		var v=obj.getValue();
		if(v) return;
		obj.setValue(result);
	});
}

function saveBoEnt(){
	var form=new mini.Form("#formDiv");
	var genTableVal = mini.get("genTable").getValue(); //是否生成物理表
	form.validate();
	if(!form.isValid()) return;
	var cols=grid.getData();
	for(var i=0;i<cols.length;i++){
		var col=cols[i];
		var control=col.control;
		var ctlObj=controlMap[control];
		var ext=ctlObj.ext;
		var needRequire=ctlObj.needRequire;
		var json={};
		var extJson=col.extJson;

		for(var j=0;j<ext.length;j++){
			var key=ext[j];
			if(extJson[key]){
				json[key]=extJson[key];
			}
		}
		if(needRequire){
			json.required=extJson.required;
		}

		delete col.extJson;
		delete col._id;
		delete col._uid;
		col.extJson=JSON.stringify(json);
		if(genTableVal == "yes"){
			col.status = "base";
		}
	}
	var url=__rootPath +"/sys/bo/sysBoEnt/saveBoEnt.do";
	_SaveJson("#formDiv",url,function(result){
		CloseWindow('ok');
	},function(data){
		data.sysBoAttrs=cols;
	})
}


function renderControlType(e){
	var record = e.record;
	var val = record.control;
	if(val.length==0) return;
	var display=controlMap[val].name;
	if(val=="mini-ref"){
		return "<span style='color:red'>"+display+"</span>";
	}
	return display;
}

/**
 * 选择数据源
 * @param e
 * @returns
 */
function onDatasource(e) {
	var btnEdit = this;
	var callBack = function(data) {
		btnEdit.setValue(data.alias);
		btnEdit.setText(data.name);
	}
	openDatasourceDialog(callBack);
}

/**
 * 从数据库加载数据。
 * @param tbName
 * @returns
 */
function loadByTable(tbName){
	var url=__rootPath+'/sys/db/sysDb/getByName.do';
	var dsName="";
	var params={dsName:dsName,tbName:tbName};
	$.post(url,params,function(data){


		var list=conertData(data.columnList);
		grid.setData(list);

		//数据库配置
		var pkList=$.clone(list);
		var fkList=$.clone(list);
		var parentList=$.clone(list);

		var pkField=mini.get("pkField");
		var fkField=mini.get("fkField");
		var parentField=mini.get("parentField");
		var genMode=mini.get("genMode");
		var genTableCtl=mini.get("genTable");
		var btnDel=mini.get("btnDel");
		var comment=mini.get("comment");
		var txtName=mini.get("txtName");
		var tableName=mini.get("tableName");

		txtName.setValue(data.tableName);
		tableName.setValue(data.tableName);
		comment.setValue(data.comment);
		genMode.setValue("db");
		genTableCtl.setChecked(true);

		var thGenTable=$("#thGenTable");
		var tdGenTable=$("#tdGenTable");
		tdGenTable.hide();
		thGenTable.html("已生成表");

		pkField.setData(pkList);
		var field= getPkField(pkList);
		pkField.setValue(field);

		fkField.setData(fkList);
		parentField.setData(parentList);

		btnDel.setVisible(false);
		//显示数据库配置
		$("#tdFromDb").show();

		vm.genMode="db";

		vm.fileds=list;
	})
}

function getPkField(list){
	for(var i=0;i<list.length;i++){
		var o=list[i];
		if(o.isPk){
			return o.name;
		}
	}
	return "";
}

/**
 * 数据列表转换。
 * @param list
 * @returns
 */
function conertData(list){
	var rtnList=[];
	for(var i=0;i<list.length;i++){
		var tmp=list[i];
		var o={};
		o.name=tmp.fieldName;
		o.comment=tmp.comment;
		o.dataType=tmp.columnType;
		o.extJson={};
		o.extJson.required=!tmp.isNull;
		o.isPk=tmp.isPk;



		o.decimalLength=tmp.decimalLen;
		if(tmp.columnType=="number"){
			o.length=tmp.intLen;
			o.control="mini-textbox";
		}
		else if(tmp.columnType=="date"){
			o.control="mini-datepicker";
		}
		else if(tmp.columnType=="clob"){
			o.control="mini-textarea";
		}
		else{
			o.control="mini-textbox";
			o.length=tmp.charLen;
		}
		rtnList.push(o);
	}
	return rtnList;
}

/**
 * 从数据库加载。
 * @returns
 */
function getFromDb(){
	var ds="";
	openTableDialog(ds,function(data){
		var type=data.type;
		var tableName=data.tableName;
		loadByTable(tableName);
	})
}