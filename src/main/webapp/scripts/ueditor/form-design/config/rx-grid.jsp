<%@page pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
<title>表格控件-rx-grid</title>
<%@include file="/commons/mini.jsp"%>
</head>
<body>
	<div class="form-outer">
			<form id="miniForm">
				<table class="table-detail column-four table-align" cellspacing="1" cellpadding="1">
					<tr>
						<td style="width: 110px;">
							<span class="starBox">
								子表名称<span class="star">*</span>
							</span>
						</td>
						<td colspan="3">
							<input 
								id="label" 
								class="mini-textbox" 
								name="label" 
								required="true" 
								vtype="maxLength:100"  
								style="width:100%" 
								emptytext="请输入子表名称"
								onblur="getPinyin" 
							/>
						</td>

					</tr>
                    <tr>
                        <td style="width: 110px">
							<span class="starBox">
								别　　名<span class="star">*</span>
							</span>
                        </td>
                        <td colspan="3"><%--刪除我的 colspan="3" 順便把這條注釋也刪除--%>
                            <input
                                    id="name"
                                    name="name"
                                    class="mini-textbox"
                                    required="true"
                                    style="width:100%"
                                    emptytext="请输入子表别名"
                                    onvalidation="onEnglishAndNumberValidation"
                            />
                        </td>
						<%-- 解開以下注釋的時候 請給上面的td中 colspan="3" 的這條屬性刪除 順便把這條注釋也刪除 --%>
                     <%--<td>是否显示子表名称</td>--%>
                        <%--<td>--%>
                            <%--<div--%>
                                    <%--class="mini-radiobuttonlist"--%>
                                    <%--id=""--%>
                                    <%--name=""--%>
                                    <%--value=""--%>
                                    <%--onvaluechanged="changeEditType"--%>
                                    <%--data="[{id:'yes',text:'是'},{id:'no',text:'否'}]"--%>
                            <%--></div>--%>
                        <%--</td>--%>
                    </tr>
					<tr>
						<td  style="width: 110px;">数据编辑模式</td>
						<td >
							<div 
								class="mini-radiobuttonlist" 
								id="edittype"  
								name="edittype" 
								value="inline" 
								onvaluechanged="changeEditType" 
								data="[{id:'inline',text:'行内'},{id:'openwindow',text:'弹窗'},{id:'editform',text:'弹出表单'}]"
							></div>
						</td>
						<td  style="width: 110px;">必　　填</td>
						<td colspan="">
							<div id="required" name="required" class="mini-checkbox" ></div>
						</td>
					</tr>
					<tr id="tmprow" style="display:none">
						<td  style="width: 110px;">
							编辑窗口模板
						</td>
						<td >
							<input 
								id="templateid" 
								name="templateid" 
								class="mini-combobox" 
								style="width:60%;" 
								textField="name" 
								valueField="id" 
								emptyText="请选择..." 
								url="${ctxPath}/bpm/form/bpmFormView/getDetailTemplates.do"  
								required="true"  
								showNullItem="true" nullItemText="请选择..."
							/> 
						</td>
						<td  style="width: 110px;">
							弹窗宽与高
						</td>
						<td>
							<input id="pwidth" name="pwidth" class="mini-spinner" style="width:80px" value="780" minValue="0" maxValue="2000"/>&nbsp;px
							<input id="pheight" name="pheight" class="mini-spinner" style="width:80px" value="250" minValue="0" maxValue="1300"/>&nbsp;px
						</td>
					</tr>
					<tr id="tr_form" style="display:none">
						<td style="width: 110px;">选择的表单</td>
						<td>
							<input class="mini-buttonedit" name="formkey" id="formkey" onbuttonclick="onSelectForm" style="width:80%;"/>
						</td>
						<td style="width: 110px;">弹出表单长宽</td>
						<td>
							宽：<input id="fwidth" name="fwidth" class="mini-spinner" style="width:80px" value="0" minValue="0" maxValue="1200"/>
							高：<input id="fheight" name="fheight" class="mini-spinner" style="width:80px" value="0" minValue="0" maxValue="1200"/>
						</td>
					</tr>
					<tr>
						<td  style="width: 110px;">表头配置</td>
						<td colspan="3" style="padding:0;">
							<div class="form-toolBox">
								<a class="mini-button"  id="addRowBtm" onclick="addRowGrid('hdgrid')">添加</a>
								<a class="mini-button btn-red"  onclick="delRowGrid('hdgrid')">删除</a>
								<a class="mini-button"  onclick="upRowGrid('hdgrid')">向上</a>
								<a class="mini-button"  onclick="downRowGrid('hdgrid')">向下</a>
							</div>
							<div style="display:none">
								<input  property="editor"
									id="dateFormat" 
									class="mini-combobox" 
									style="width:100%"
									required="true"
									textfield="formatName"
									valuefield="format"
			    				 	data="[{format:'yyyy-MM-dd',formatName:'yyyy-MM-dd'},{format:'yyyy-MM-dd HH:mm',formatName:'yyyy-MM-dd HH:mm'},{format:'yyyy-MM-dd HH:mm:ss',formatName:'yyyy-MM-dd HH:mm:ss'}]"  
			    				 	allowInput="false" 
			    				 	showNullItem="true" 
			    				 	nullItemText="请选择..."
		    				 	/>
		    				 	<input id="textFormat" class="mini-textbox"  property="editor"/>
							
							</div>
							<div 
								id="hdgrid" 
								class="mini-datagrid" 
								style="width:100%;min-height:150px"
								showPager="false"  
								allowCellEdit="true" 
								allowCellSelect="true" 
								multiSelect="true"
								allowAlternating="true"
							>
							    <div property="columns" class="border-right">
							        <div type="indexcolumn" width="20"></div>
							        <div type="checkcolumn" width="20"></div>
							        <div field="name" width="100" >字段名称(<span class="star">*</span>)
							        	<input property="editor" class="mini-textbox" style="width:100%;" />
							        </div>              
							        <div field="key" width="100" >字段Key(<span class="star">*</span>)
							        	<input property="editor" class="mini-textbox" style="width:100%;" />
							        </div>
							        <div field="displayfield" width="80">显示字段
							        	 <input property="editor" class="mini-textbox" style="width:100%;" />
							        </div>
							         <div field="requires" width="50">必填
							        	 <input property="editor" class="mini-checkbox" style="width:100%;" />
							        </div>
							        <div field="editcontrol" displayfield="editcontrol_name" width="80" >编辑控件
							        	<input property="editor" class="mini-combobox" style="width:100%" data="controlData" valueField="id" textField="text" allowInput="false" showNullItem="true" nullItemText="请选择"/>
							        </div>
							        <div field="width" width="50" >宽度
							        	<input property="editor" class="mini-spinner"  value="100" style="width:100%;" minValue="50" maxValue="1200"/>
							        </div>
							        <div field="datatype" width="60">数据类型
							        	<input property="editor" class="mini-combobox" value="varchar"  textField="text" valueField="id" data="[{id:'',text:'请选择'},{id:'varchar',text:'字符'},{id:'number',text:'数字'},{id:'date',text:'日期型'}]">
							        </div>
							        <div field="format" width="80" displayfield="format" >格式化
							        	<input property="editor" class="mini-textbox" style="width:100%;" />
							        </div> 
							        <div field="cellStyle" width="60" >对齐
							        	<input property="editor" class="mini-combobox" value="text-align:right"  textField="text" valueField="id" data="[{id:'text-align:center',text:'居中'},{id:'text-align:left',text:'居左'},{id:'text-align:right',text:'居右'}]">
							        </div>
							        <div field="vtype" displayfield="vtype_name" width="100" >验证规则
							        	<input property="editor" class="mini-combobox" value="text-align:right"  textField="name" valueField="value" url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=_FieldValidateRule">
							        </div>
							    </div>
							</div>
						</td>
					</tr>
					<tr>
						<td style="width: 110px;">
							按钮配置
						</td>
						<td colspan="3">
							<div class="form-toolBox">
								<a class="mini-button" onclick="addRowGrid('buttonGrid')">添加</a>
								<a class="mini-button btn-red"  onclick="delRowGrid('buttonGrid')">删除</a>
								<span class="separator"></span>
								<a class="mini-button"  onclick="upRowGrid('buttonGrid')">向上</a>
								<a class="mini-button"  onclick="downRowGrid('buttonGrid')">向下</a>
							</div>
							<div 
								id="buttonGrid" 
								class="mini-datagrid" 
								style="width:100%;min-height:150px"
								showPager="false"  
								allowCellEdit="true" 
								allowCellSelect="true" 
								multiSelect="true"
								allowAlternating="true"
                                onCellBeginEdit="changeEditor"
							>
							    <div property="columns" class="border-right">
							        <div type="indexcolumn" width="20"></div>
							        <div type="checkcolumn" width="20"></div>
							        <div field="text" width="100" >按钮名
							        	<input property="editor" class="mini-textbox" style="width:100%;" />
							        </div>
                                    <div field="type" width="100" >按钮类型
                                        <input property="editor" class="mini-combobox" style="width:100%;" data="[{'id':'common','text':'普通按钮'},{'id':'import','text':'导入按钮'}]" />
                                    </div>
							        <div field="setting" width="100" >设定
                                        <input id="settingEditor" property="editor" class="mini-buttonedit" style="width:100%" onbuttonclick="setImportConfig"
                                               selectOnFocus="true" allowInput="false"/>
							        </div>
							    </div>
							</div>
						</td>
					</tr>
					<tr>
						<td style="width: 110px;">
							树形
						</td>
						<td>
							<input name="treegrid" class="mini-checkbox" style="width:100%;vertical-align: middle" /><span style="width:100%;vertical-align: middle">&nbsp;启用树形</span>
						</td>
						<td style="width: 110px;">
							显示字段
						</td>
						<td>
							<input id="treecolumn" name="treecolumn" class="mini-combobox"  textField="name" valueField="key" emptyText="请选择..."
    						   showNullItem="true" nullItemText="请选择..." style="width: 100%;"/>
						</td>
					</tr>

					<tr>
						<td  style="width: 110px;">
							控件
						</td>
						<td colspan="3" >

							长：<input id="mwidth" name="mwidth" class="mini-spinner" style="width:80px" value="0" minValue="0" maxValue="1200"/>
							<input id="wunit" name="wunit" class="mini-combobox" style="width:50px" onvaluechanged="changeMinMaxWidth"
							data="[{'id':'px','text':'px'},{'id':'%','text':'%'}]" textField="text" valueField="id"
						    value="px"  required="true" allowInput="false" />

							高：<input id="mheight" name="mheight" class="mini-spinner" style="width:80px" value="0" minValue="0" maxValue="1200"/>
							<input id="hunit" name="hunit" class="mini-combobox" style="width:50px" onvaluechanged="changeMinMaxHeight"
							data="[{'id':'px','text':'px'},{'id':'%','text':'%'}]" textField="text" valueField="id"
						    value="px"  required="true" allowInput="false" />

						</td>
					</tr>
				</table>
			</form>
	</div>
	
	<script type="text/javascript">
		
		mini.parse();
		var form=new mini.Form('miniForm');
		
		var controlData=[{id:'mini-textbox',text:'单行文本',tag:'input',datatype:'varchar',editcontrol:'mini-textbox',editcontrol_name:'单行文本',length:'50'},
		{id:'mini-textarea',text:'多行文本',tag:'textarea',datatype:'varchar',editcontrol:'mini-textarea',editcontrol_name:'多行文本',length:'200'},
		{id:'mini-spinner',text:'数字输入',tag:'input',datatype:'number'},
		{id:'mini-datepicker',text:'日期',tag:'input',datatype:'date'},
		{id:'mini-checkbox',text:'复选框',tag:'input',datatype:'varchar'},
		{id:'mini-month',text:'月份',tag:'input',datatype:'date'},
		{id:'mini-time',text:'时间',tag:'input',datatype:'date'},
		{id:'mini-combobox',text:'下拉选择',tag:'input',datatype:'varchar'},
		{id:'mini-buttonedit',text:'编辑按钮',tag:'input',datatype:'varchar'},
		{id:'mini-user',text:'用户',tag:'input',datatype:'varchar'},
		{id:'mini-group',text:'用户组',tag:'input',datatype:'varchar'},
		{id:'mini-dep',text:'部门',tag:'input',datatype:'varchar'},
		{id:'upload-panel',text:'附件',tag:'input',datatype:'varchar'}];
		//获得控件的配置
		function getControlConfig(controlType){
			for(var i=0;i<controlData.length;i++){
				if(controlData[i].id==controlType){
					return controlData[i];
				}
			}
		}
		
		var grid=mini.get('hdgrid');
		var buttonGrid=mini.get('buttonGrid');
		//TODO
		grid.on('cellendedit',function(e){
			var record = e.record, field = e.field;
            var val=e.value;
			
            if(field=='name' && (record.key==undefined || record.key=='')){
            	//自动拼音
           		$.ajax({
           			url:__rootPath+'/pub/base/baseService/getPinyin.do',
           			method:'POST',
           			data:{words:val,isCap:'false',isHead:'true'},
           			success:function(result){
           				grid.updateRow(record,{key:result.data})
           			}
           		});
            	return;
            }
	            
			var edittype=mini.get('edittype').getValue();
			if(e.column.displayField && edittype=='editform'&&e.editor.getText()){
				var newRow=jQuery.extend({},e.record);
				newRow[e.column.displayField]=e.editor.getValue();
				newRow[e.column.field]=e.editor.getValue();
				//TODO 设置其tag
				grid.updateRow(e.record,newRow);
			}
			//设置列头
			setTreeColumn(grid.getData());
		});
		
		grid.on('cellbeginedit',function(e) {
            var record = e.record, field = e.field;
            var val=e.value;
           
            
           	if(field!='format'){
           		return;
           	}
           	if(record.datatype=='date'){
            	e.editor=mini.get('dateFormat');
            }else{
            	e.editor=mini.get('textFormat');
            }
           	
           	e.column.editor=e.editor;
           	
           	
           
        });
		
		
		//编辑的控件的值
		var oNode = null,
		thePlugins = 'rx-grid';
		var pluginLabel="${fn:trim(param['titleName'])}";
		function changeFrom(){
			var val=mini.get('from').getValue();
			if(val=='self'){
				$("#self_row").css('display','');
				$("#url_row").css('display','none');
			}else{
				$("#self_row").css('display','none');
				$("#url_row").css('display','');
			}
		}

		function changeEditType(){
			var edittype=mini.get('edittype').getValue();
			$("#tmprow").css('display','none');
			$("#tr_form").css('display','none');
			$("#addRowBtm").css('display','none');
			if(edittype=='openwindow'){
				$("#tmprow").css('display','');
				$("#addRowBtm").css('display','');
			}else if(edittype=='editform'){
				$("#tr_form").css('display','');
			}else{
				$("#addRowBtm").css('display','');
			}
		}
		
		window.onload = function() {
			//若控件已经存在，则设置回调其值
		    if( UE.plugins[thePlugins].editdom ){
		        //
		    	oNode = UE.plugins[thePlugins].editdom;
		        //获得字段名称
		        var formData={};
		        var attrs=oNode.attributes;
		        for(var i=0;i<attrs.length;i++){
		        	formData[attrs[i].name]=attrs[i].value;
		        }
		        form.setData(formData);
		        mini.get('formkey').setText(formData.formname);
		        //恢复表格的表头
		        var headers=[];
		        var row=$(oNode).find('table > thead > tr');
		        var buttonContainer=$(oNode).find('.button-container');
		        //字表按钮添加
		        var buttonData=[];
		        buttonContainer.children().each(function(){
		        	var obj=$(this);
		        	var onclick=obj.attr("onclick");
		        	var iconCls=obj.attr("iconCls");
		        	var text=obj.text();
		        	var btn={"onclick":onclick,"iconCls":iconCls,"text":text};
		        	buttonData.push(btn);
		        });
		        if(buttonData.length>0){
		        	buttonGrid.setData(buttonData);
		        }


		        row.children().each(function(){

		        	var obj=$(this);
		        	var key=getAttr(obj,'header') ;
		        	if(key){
		        		var name=$(this).html().trim();
			        	var width=getAttr(obj,'width') ;
			        	var format=getAttr(obj,'format') ;

			        	var cellStlyle=getAttr(obj,'cellStlyle') ;
			        	var displayfield=getAttr(obj,'displayfield') ;
			        	var datatype=getAttr(obj,'datatype');
			        	var editcontrol=getAttr(obj,'editcontrol');
			        	var editcontrolName=getAttr(obj,'editcontrol_name');
			        	var vtype=getAttr(obj,'vtype');
			        	var vtypeName=getAttr(obj,'vtype_name');
			        	var requires=getAttr(obj,'requires');

			        	if(width==undefined){
			        		width=100;
			        	}
			        	if(datatype=='undefined'){
			        		datatype='';
			        	}
			        	if(requires==undefined){
			        		requires='false';
			        	}
			        	headers.push({key:key,name:name,requires:requires,width:width,format:format,editcontrol:editcontrol,vtype:vtype,vtype_name:vtypeName,
			        		cellStlyle:cellStlyle,displayfield:displayfield,datatype:datatype,editcontrol_name:editcontrolName});
		        	}

		        });
		        grid.setData(headers);

		        setTreeColumn(headers);

		    }
		    else{
		    	var data=_GetFormJson("miniForm");
		    	var array=getFormData(data);
		    	initPluginSetting(array);
		    }

		    //changeFrom();
		    changeEditType();
		};

		function setTreeColumn(headers){
			var treeColumn=mini.get("treecolumn");
	       	var data=$.clone(headers);
	        treeColumn.setData(data);
		}

		function getAttr(obj,name){
			return obj.attr(name);
		}

		function OnCellBeginEdit(e) {
            var record = e.record, field = e.field;
            var type=record.datatype
           	if(field=='length'){
           		if(type=="Date" || type=="Clob"){
           			e.editor=null;
           		}
            }
            else if(field=='decimal'){
            	if(type=="Date" || type=="Clob" || type=="String"){
           			e.editor=null;
           		}
            }
        }



		//取消按钮
		dialog.oncancel = function () {
		    if( UE.plugins[thePlugins].editdom ) {
		        delete UE.plugins[thePlugins].editdom;
		    }
		};
		//确认
		dialog.onok = function (){
			form.validate();
	        if (form.isValid() == false) {
	            return false;
	        }

	        var isCreate=false;
	        var formData=form.getData();
	        var gridData=grid.getData();

			var table =$(oNode).children('table');
			table.show();

	        //创新新控件
	        if( !oNode ) {
	        	isCreate=true;
		            oNode = createElement('div',name);
		            oNode.setAttribute('class','rx-grid rxc grid-d');
		            //需要设置该属性，否则没有办法其编辑及删除的弹出菜单
		            oNode.setAttribute('plugins',thePlugins);
		            var table=$('<table style="width:100%;"></table>');
		            var thead=$("<thead></thead>");
		            var tbody=$("<tbody></tbody>");
		            var hrow=$("<tr></tr>");
		            var brow=$("<tr></tr>");
		            var html=getButtonsHtml();
		            var buttonContainer=$("<div class='button-container'>"+html+"</div>");
		            $(oNode).append(buttonContainer);
		            //添加表头
		            for(var i=0;i<gridData.length;i++){
		            	var row=gridData[i];
		            	var vtype ="";
		            	var format=row.format?row.format:'';
		            	if(!row.displayfield) row.displayfield='';
		            	if(!row.datatype)  row.datatype='';
		            	if(!row.cellStyle)  row.cellStyle='';
		            	if(!row.width) row.width='';
		            	if(!row.length) row.length='';
		            	if(!row.decimal) row.decimal='';
		            	if(!row.requires) row.requires='false';
		            	if(!row.editcontrol){
		            		row.editcontrol='mini-textbox';
		            		row.editcontrol_name='单行文本';
		            	}
		            	if(row.vtype){
		            		vtype='vtype="'+ row.vtype +'" vtype_name="'+ row.vtype_name +'"';
		            	}
		            	var td='<th class="header" displayfield="'+row.displayfield+'" datatype="' + row.datatype +'" '
		            		+' width="'+row.width + '" header="'+row.key+'" '+vtype
		            		+' length="'+row.length + '" decimal="'+row.decimal+'" '+'requires="'+row.requires +'" '
		            		+' editcontrol="'+row.editcontrol+'" '
		            		+' editcontrol_name="'+row.editcontrol_name+'" '
		            		+' cellStyle="'+row.cellStyle+'" format="'+format+'">'+row.name+'</th>';

		            	var td2=$('<td header="'+row.key+'"></td>');
		            	hrow.append(td);

		            	var tag=getTag(row.editcontrol);
		            	var contrConf=getControlConfig(row.editcontrol);

		            	var control='<'+tag+' property="editor" allowinput="true" mwidth="0" wunit="px" mheight="0" hunit="%" plugins="'+row.editcontrol+'"';
		            	if(contrConf){
		            		control+= ' editcontrol="'+row.editcontrol+'" editcontrol_name="'+row.editcontrol_name+'" ';
		            		if(!row.datatype){
		            			row.datatype=contrConf.datatype;
		            		}
		            		if(contrConf.length){
		            			control+= ' length="'+contrConf.length+'"';
		            		}
		            	}
		            	control+=' class="rxc '+row.editcontrol+'" name="'+row.key+'" label="'+row.name+'" datatype="'+row.datatype+'" format="'+row.format+'" from="forminput"';
		            	if(row.datatype=="date"){
	            			control+=" format=\""+row.format+"\"";
	            		}
	            		control+="/>";
		            	td2.append(control);
		            	brow.append(td2);
		            }
		            thead.append(hrow);
		            tbody.append(brow);
		            table.append(thead);
		            table.append(tbody);
		            $(oNode).append(table);
	        }else{//更新表头
	        	//同时需要更新其对应的子表名称
	           $(oNode).attr('name',name);
	           var html=getButtonsHtml();
	           var buttonContainer= $(oNode).find('.button-container');
	           if(buttonContainer.length==0){
	        	   buttonContainer=$("<div class='button-container' >"+html+"</div>");
	        	   $(oNode).append(buttonContainer);
	           }
	           else{
	        	   buttonContainer.empty();
	        	   buttonContainer.prepend(html);
	           }
	            var hrow=$(oNode).find('table >thead > tr:first');
	            //子表body
	            var brow=$(oNode).find('table > tbody > tr:first');
	            var nbrow=$("<tr></tr>");
	            //清空旧表头的内容
	            hrow.empty();
	            //添加表头
	            for(var i=0;i<gridData.length;i++){
	            	var row=gridData[i];
	            	var vtype ="";
	            	var format=row.format?row.format:'';
	            	if(!row.displayfield) row.displayfield='';
	            	if(!row.datatype)  row.datatype='';
	            	if(!row.cellStyle)  row.cellStyle='';
	            	if(!row.width) row.width='';
	            	if(!row.length) row.length='';
	            	if(!row.decimal) row.decimal='';
	            	if(!row.requires) row.requires='false';
	            	if(!row.editcontrol){
	            		row.editcontrol='mini-textbox';
	            		row.editcontrol_name='单行文本';
	            	}
	            	if(row.vtype){
	            		vtype='vtype="'+ row.vtype +'" vtype_name="'+ row.vtype_name +'"';
	            	}
	            	var td='<th class="header" displayfield="'+row.displayfield+'" editcontrol="'+row.editcontrol +'" '  
	            	+' editcontrol_name="'+row.editcontrol_name+'" '+vtype
	            	+' datatype="'+row.datatype+'" width="'+row.width + '" header="'+row.key+'"requires="'+row.requires+'" cellStyle="'+row.cellStyle+'" format="'+format+'">'+row.name+'</th>';
	            	hrow.append(td);
	            	var td=brow.children('[header="'+row.key+'"]');

	            	var tdHtml=$(td).html();
	            	var control=null;
	            	var th=$('<td header="'+row.key+'"></td>');
	            	//var oldEditor=$(tdHtml).attr("plugins");
	            	//
					var flag=true;
	            	for(var key in row) {
						var detailConfValue = row[key];
						//对比子表控件与明细中的配置
						if (detailConfValue && detailConfValue != $(tdHtml).attr(key)) {
							flag = false;
							break;
						}
					}

	            	//保留控件配置信息
					var confHtml = "";
					var reg = /label="(?:[^"]+)"\s+([^>]+)/ig;
					var regGroup = reg.exec(tdHtml);
					if(regGroup && regGroup.length!=0){
						confHtml = regGroup[1];
					}

	            	if(!flag){
	            		var tag=getTag(row.editcontrol);
	            		control='<'+tag+' property="editor" allowinput="true" mwidth="0" wunit="px" mheight="0" hunit="%" plugins="'+row.editcontrol
	            		+'" datatype="'+row.datatype+'" class="rxc '+row.editcontrol+'" name="'+row.key+'" label="'+row.name+'"';
	            		if(row.datatype=="date"){
	            			control+=" format=\""+row.format+"\"";
	            		}
	            		control+= confHtml + "/>";
	            		th.append(control);
	            	}else{
	            		th.append(tdHtml);
	            	}
	            	nbrow.append(th);
	            }
	            //移除旧的行
	            brow.remove();
	            //添加新的行
	            $(oNode).children('table').children('tbody').append(nbrow);
	        }
			
	        //更新控件Attributes
	        var style="";
            if(formData.mwidth!=0){
            	style+="width:"+formData.mwidth+formData.wunit+";";
            }
            if(formData.mheight!=0){
            	style+="height:"+formData.mheight+formData.hunit+";";
            }
            oNode.setAttribute('style',style);
            
            for(var key in formData){
            	oNode.setAttribute(key,formData[key]);
            }
            
			oNode.setAttribute('formname',mini.get('formkey').getText());
			oNode.setAttribute('data-options','{label:\\\''+formData.label+'\\\',required:'+mini.get('required').getValue()+'}');
            //弹出窗口
            var edittype=formData.edittype;
            var isWinExist=false;

			var operWindowDiv = $(oNode).children('.mini-window');
            if(edittype=='openwindow'){
            	if(operWindowDiv.length>0){
					operWindowDiv.remove();
				}
            	var pheight=mini.get('pheight').getValue();
            	var pwidth=mini.get('pwidth').getValue();
            	var btnS = getButtonsHtml();
            	//查找其下弹出窗口，并且加载模板显示控件，允许用户自己填写控件
            	var formWin='<div id="editWindow_'+formData.name+'" class="mini-window popup-window-d" title="编辑'+formData.label+'信息" style="width:'+pwidth+'px;height:'+pheight+'px" showMaxButton="true" showModal="true" allowResize="true" allowDrag="true">';
            	formWin+='<div class="form-toolBox" > '
            			+'<a class="mini-button button-d"  onclick="saveFormDetail(\''+formData.name+'\')">保存</a>'
            			+'<a class="mini-button button-d"  onclick="closeFormDetail(\''+formData.name+'\')">关闭</a>'
            			+'</div>';
            	formWin+='<div id="editForm_'+formData.name+'" class="form">';
				formWin+='<input class="mini-hidden" type="hidden" name="_uid"/>';
            	var json = $.ajax({
            		  url: "${ctxPath}/bpm/form/bpmFormView/getTemplateHtml.do?templateId="+formData.templateid,
            		  async: false,
            		  data:{
            			 columns:mini.encode(gridData)
            		  }
            	}).responseText; 
            	try{
	            	var html=mini.decode(json).data;
	            	formWin+=html;
            	}catch(ex){
					mini.alert(ex);
            	}
            	table.hide();
	            formWin+='</div></div>';
	            $(oNode).append(formWin);
            }else{
            	$(oNode).children('.mini-window').attr('id','editWindow_'+formData.name);
            	$(oNode).children('.mini-window').children('.form').attr('id','editForm_'+formData.name);
				if(operWindowDiv.length>0){
					operWindowDiv.remove();
				}
            }
    	 	if(isCreate){
	        	editor.execCommand('insertHtml',oNode.outerHTML);
	     	}else{
	        	delete UE.plugins[thePlugins].editdom;
	     	}
		}
		
		function getButtonsHtml(){
			var data=buttonGrid.getData();
			var ary=[];
			for(var i=0;i<data.length;i++){
				var o=data[i];
				var html="<a class='mini-button' ";
				/*if(o.iconCls){
					html+=" iconCls='"+o.iconCls+"'";
				}*/
				html+=" onclick='"+o.onclick+"'>"+o.text+"</a>";
				ary.push(html);
			}
			return ary.join("");
		}
		
		
		function getTag(id){
			for(var i=0;i<controlData.length;i++){
				var obj=controlData[i];
				if(obj.id==id){
					return obj.tag;
				}
			}
			return "input";
		}
		
		
		function onSelectForm(e){
			var btn=e.sender;
			var grid = mini.get("hdgrid");
			
			_OpenWindow({
				title:'选择表单',
				url:__rootPath+'/bpm/form/bpmFormView/onlineDialog.do?single=true',
				width:800,
				height:450,
				ondestroy:function(action){
					if(action!='ok') return;
					
					var formView=this.getIFrameEl().contentWindow.getFormView();
					var fv=formView[0];
					btn.setText(fv.name);
					btn.setValue(fv.key);
					
					$.ajax({
						url:__rootPath+'/sys/bo/sysBoEnt/getBoEntByBoDefId.do',
						method:'POST',
						data:{boDefId:fv.boDefId},
						success:function(result){
							var arr = result.sysBoAttrs;
							var name = result.name;
							var jsonData = [];
							for(var i=0;i<arr.length;i++){
								var obj=arr[i];
								var nameObj={};
								nameObj.name=obj.comment;
								nameObj.key=obj.name;
								nameObj.datatype=obj.dataType;
								if(obj.isSingle!="1"){
									nameObj.displayfield=obj.name+"_name";
								}
								jsonData.push(nameObj);
							}
							grid.setData(jsonData);
							
							var bname = mini.get("name");
							bname.setValue(name);
						}
					});
				}
			});
		}
		
		function selectIcon(e){
			 var btn=e.sender;
			 var buttonGrid=mini.get('buttonGrid');
			 buttonGrid.cancelEdit();
			 _IconSelectDlg(function(icon){
					btn.setText(icon);
					btn.setValue(icon);
					 buttonGrid.updateRow(buttonGrid.getSelected(),{iconCls:icon});
			});
		}




		<!-- 导入按钮代码块开始 -->


        function changeEditor(e){
            var field = e.field,rs=e.record,value=e.value;
            if(field=='setting'){
                if(!rs.type||rs.type==""){
                    alert('请先设置按钮类型');
                    e.cancel=true;
                }else if(rs.type=='import') {
                    var editor=mini.get('settingEditor');
                    editor.setShowButton(true);
                    editor.setAllowInput(false);
                    e.editor=editor;
                }else {
                    var editor=mini.get('settingEditor');
                    editor.setShowButton(false);
                    editor.setAllowInput(true);
                    e.editor=editor;
                }
            }
        }

        <!-- 导入按钮代码块结束 -->


	</script>
</body>
</html>
