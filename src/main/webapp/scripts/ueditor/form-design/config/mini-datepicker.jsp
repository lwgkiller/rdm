<%@page pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
<title>日期控件-mini-datepicker</title>
<%@include file="/commons/mini.jsp"%>
</head>
<body>
	<div class="form-outer">
			<form id="miniForm">
				<table class="table-detail column-four table-align" cellspacing="1" cellpadding="1">
					<tr>
						<td>
							字段备注<span class="star">*</span>
						</td>
						<td>
							<input 
								id="label" 
								class="mini-textbox" 
								name="label" 
								required="true" 
								vtype="maxLength:100,chinese"  
								style="width:100%"
								emptytext="请输入字段备注" 
								onblur="getPinyin" 
							/>
						</td>
						<td>
							<span class="starBox">
								字段标识<span class="star">*</span>
							</span>
						</td>
						<td>
							<input id="name" name="name" class="mini-textbox" required="true" onvalidation="onEnglishAndNumberValidation"/>
						</td>
					</tr>
					<tr>
						<td>日期格式</td>
						<td>
							<input 
								name="format" 
								class="mini-combobox" 
								style="width:100%"
								required="true"
		    				 	data="[{id:'yyyy-MM-dd',text:'yyyy-MM-dd'},{id:'yyyy-MM-dd HH:mm',text:'yyyy-MM-dd HH:mm'},{id:'yyyy-MM-dd HH:mm:ss',text:'yyyy-MM-dd HH:mm:ss'}]"  
		    				 	allowInput="false" 
		    				 	showNullItem="true" 
		    				 	nullItemText="请选择..."
	    				 	/>
						</td>
						<td>
							<span class="starBox">
								必　　填<span class="star">*</span>
							</span>
						</td>
						<td>
							<input class="mini-checkbox" name="required" id="required"/>
						</td>
					</tr>
					<tr>
						<td>
							显示时间
						</td>
						<td>
							<input type="checkbox" name="showtime" class="mini-checkbox"/>
						</td>
						<td>
							显示确定按钮
						</td>
						<td>
							<input type="checkbox" name="showokbutton" class="mini-checkbox"/>
						</td>
					</tr>
					<tr>
						<td>
							显示清除按钮
						</td>
						<td>
							<input  name="showclearbutton" class="mini-checkbox" checked="true"/>
						</td>
						<td>允许文本输入</td>
						<td>
							<input class="mini-checkbox" name="allowinput" id="allowinput" />
						</td>
					</tr>
					<tr>
						<td>
							初始当前时间
						</td>
						<td>
							<input class="mini-checkbox" name="initcurtime" id="initcurtime"/>
						</td>
						<td>
							默认值
						</td>
						<td>
							<input class="mini-textbox" name="value" style="width:100%"/>
						</td>
					</tr>
					<tr>
						<td>
							相对日期
						</td>
						<td colspan="3">
							<table>
								<tr>
									<td>
										<div 
											id="type"
											class="mini-radiobuttonlist" 
											style="display:inline-block" 
											data="[{id:'none',text:'不设置'},{id:'today',text:'今天'},{id:'control',text:'控件'}]" 
											value="none"  
											textField="text" 
											valueField="id" 
											onValuechanged="typeChange(e)"
										></div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td>
							比较
						</td>
						<td colspan="3">
							<table>
								<tr>
									<td>
										<div 
											id="compare" 
											class="mini-radiobuttonlist" 
											style="display:inline-block" 
											data="[{id:'LT',text:'小于'},{id:'LTE',text:'小于等于'},{id:'GT',text:'大于'},{id:'GTE',text:'大于等于'}]" 
											value="GTE"   
											textField="text" 
											valueField="id" 
										></div>
									</td>
									<td id="thControl">
										<span>日期控件</span>
										<input 
											id="control" 
											name="control" 
											style="display:inline-block"
											width="130"
											class="mini-combobox" 
											textField="comment" 
											valueField="name"     
											showNullItem="true" 
										/>    
									</td>
								</tr>
							</table>
							
						</td>
					</tr>
					<tr>
						<td>
							控  件
						</td>
						<td colspan="3">
							长：<input id="mwidth" name="mwidth" class="mini-spinner" style="width:80px" value="160" minValue="0" maxValue="1200"/>
							
							<input id="wunit" name="wunit" class="mini-combobox" style="width:50px" onvaluechanged="changeMinMaxWidth"
							data="[{'id':'px','text':'px'},{'id':'%','text':'%'}]" textField="text" valueField="id"
						    value="px"  required="true" allowInput="false" />

							高：<input id="mheight" name="mheight" class="mini-spinner" style="width:80px" value="34" minValue="0" maxValue="1200"/>
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
		//编辑的控件的值
		var oNode = null,
		thePlugins = 'mini-datepicker';
		var pluginLabel="${param['titleName']}";
		
		
		
		window.onload = function() {
			bindDateControl();
			//若控件已经存在，则设置回调其值
		    if( UE.plugins[thePlugins].editdom ){
		        //
		    	oNode = UE.plugins[thePlugins].editdom;
		        //获得字段名称
		        var formData={};
		        var attrs=oNode.attributes;
		        
		        for(var i=0;i<attrs.length;i++){
		        	if(attrs[i].name!='control'){
		        		formData[attrs[i].name]=attrs[i].value;
		        	}
		        	
		        }
		        
		        form.setData(formData);
		        
		        initDateStart(oNode);
		    }
		    else{
		    	var data=_GetFormJson("miniForm");
		    	var array=getFormData(data);
		    	initPluginSetting(array);
		    }
			
		    
		};
		//取消按钮
		dialog.oncancel = function () {
		    if( UE.plugins[thePlugins].editdom ) {
		        delete UE.plugins[thePlugins].editdom;
		    }
		};
		
		function typeChange(e){
			var typeVal=e.value;
			if(typeVal=='control'){
				$("#thControl").show();
			}else{
				$("#thControl").hide();
			}
		}
		
		function bindDateControl(){
			var curEditEl=UE.plugins[thePlugins].editdom ;
			var fields=getDateControls(editor,curEditEl);
			var control=mini.get("control");
			control.setData(fields);
		}
		
		function initDateStart(oNode){
			
			var json= oNode.getAttribute("data-options");
			if(!json) return;
			var jsonObj= eval("("+json+")");
			if(!jsonObj.startDate) return;
			
			
			var startJson=jsonObj.startDate;
			var type=startJson.type;
			mini.get("compare").setValue(startJson.compare);
			mini.get("type").setValue(type);
			var thControl=$("#thControl");
			if(type=="control"){
				mini.get("control").setValue(startJson.control);	
				thControl.show();
			}
			else{
				thControl.hide();
			}
		}
		
		//确认
		dialog.onok = function (){
			form.validate();
	        if (form.isValid() == false) {
	            return false;
	        }
	        
	        var formData=form.getData();
	        var isCreate=false;
		    //控件尚未存在，则创建新的控件，否则进行更新
		    if( !oNode ) {
		        try {
		            oNode = createElement('input',name);
		            oNode.setAttribute('class','mini-datepicker rxc');
		            //需要设置该属性，否则没有办法其编辑及删除的弹出菜单
		            oNode.setAttribute('plugins',thePlugins);
		        } catch (e) {
		            try {
		                editor.execCommand('error');
		            } catch ( e ) {
						mini.alert('控件异常，请联系技术支持');
		            }
		            return false;
		        }
		        isCreate=true;
		    }
		    //设置默认的时间
		  	oNode.setAttribute('timeformat','H:mm:ss');
		    for(var key in formData){
            	oNode.setAttribute(key,formData[key]);
            }
		    
		  	//更新控件Attributes
	        var style="";
            if(formData.mwidth!=0){
            	style+="width:"+formData.mwidth+formData.wunit;
            }
            if(formData.mheight!=0){
            	if(style!=""){
            		style+=";";
            	}
            	style+="height:"+formData.mheight+formData.hunit;
            }
            oNode.setAttribute('style',style);
            
            
            var json= oNode.getAttribute("data-options");
            var dateJson=getDateStart();
            if(dateJson){
            	 var rtnJson={};
                 if(json){
                	 rtnJson=eval("(" +json+")");
                 }
                 rtnJson["startDate"]=dateJson;
                 oNode.setAttribute("data-options",JSON.stringify(rtnJson));
            }
            
            if(isCreate){
	            editor.execCommand('insertHtml',oNode.outerHTML);
            }else{
            	delete UE.plugins[thePlugins].editdom;
            }
            	
		};
		
		function getDateControls(editor,el){
			var container=$(editor.getContent());
			var elObj=$(el);
			var grid=elObj.closest(".rx-grid");
			//定义全局的变量是否主表。
			var isMain=grid.length==0;
			var dateFields=[];
			
			if(isMain){
				var els=$("[plugins]:not(div.rx-grid [plugins])",container);
				els.each(function(){
					var obj=$(this);
					//排除子表。
					var plugins=obj.attr("plugins");
					
					if(plugins!="mini-datepicker") return true;
					
					var label=obj.attr("label");
					var name=obj.attr("name");
					if(el && name==el.name) return true;
					var fieldObj={name: name, comment: label};   
					dateFields.push(fieldObj);
				});
			}
			else{
				$("[plugins]",grid).each(function(){
					var obj= $(this);
					var label=obj.attr("label");
					var name=obj.attr("name");
					var plugins=obj.attr("plugins");
					if(plugins!="mini-datepicker") return true;
					if(name==el.name) return true;
					var fieldObj={name: name, comment: label};   
					dateFields.push(fieldObj);
				});
			}
			return dateFields;
		}
		
		//startDate:{type:"today",compare:"GT,GTE"}
		function getDateStart(){
			var compare=mini.get("compare").getValue();
			var type=mini.get("type").getValue();
			var control=mini.get("control").getValue();
			//if(type=="none") return "";
			var obj={type:type};
			if(type=="control"){
				obj.control=control;
			}
			obj.compare=compare;
			
			return obj;
			
		}
		
		
	</script>
</body>
</html>
