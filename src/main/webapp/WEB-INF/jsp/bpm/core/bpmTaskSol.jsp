<%-- 
    Document   : 流程任务列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>我的流程任务列表管理</title>
<%@include file="/commons/list.jsp"%>
<script type="text/javascript" src="${ctxPath}/scripts/form/customFormUtil.js"></script>
<style type="text/css">
	.Bar  { 
		position: relative; 
		width: 96%; 
	 	border: 1px solid green; 
		padding: 1px;
	}
	.Bar div {
	 	display: block; 
		position: relative;
		background:#00F;
		color: #333333; 
		height: 20px; 
		line-height: 20px;
	}
	
	.Bar div span { position: absolute; width: 96%; text-align: center; font-weight: bold; }
</style>
</head>
<body>


				<div class="mini-toolbar" >
					<div class="searchBox">
						<form id="searchForm" class="search-form" >						
							<ul>
								<li>
									<span class="text">审批环节：</span><input name="Q_name_S_LK" class="mini-textbox"/>
								</li>
								<li>
									<span class="text">事项：</span><input name="Q_description_S_LK" class="mini-textbox"/>
					        		<input type="hidden" name="crsf_token" class="mini-hidden"  value="${sessionScope.crsf_token}"/>
								</li>
								<li class="liBtn">
									<a class="mini-button " onclick="searchForm(this)" >搜索</a>
									<a class="mini-button  btn-red" onclick="onClearList(this)">清空</a>
									<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
										<em>展开</em>
										<i class="unfoldIcon"></i>
									</span>
								</li>
							</ul>
							<div id="moreBox">
								<ul>
									<li>
										<span class="text">创建时间从：</span>
										<input name="Q_CREATE_TIME__D_GE" class="mini-datepicker" />
									</li>
									<li>
										<span class="text-to">至：</span>
										<input name="Q_CREATE_TIME__D_LE" class="mini-datepicker"/>
									</li>
								</ul>
							</div>
						</form>
					</div>
					<ul class="toolBtnBox">
						<li>
							<a class="mini-button" plain="true" onclick="communicateTask">沟通任务</a>
						</li>

					</ul>
					<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
						<i class="icon-sc-lower"></i>
					</span>
			     </div>
				<div class="mini-fit rx-grid-fit" style="height: 100%;">
					<div 
						id="datagrid1" 
						class="mini-datagrid" 
						style="width: 100%; height: 100%;" 
						allowResize="false" 
						onrowdblclick ="showTask(e)"
						url="${ctxPath}/bpm/core/bpmTask/getSolTasks/${solKey}.do" 
						idField="id" 
						multiSelect="true" 
						showColumnsMenu="true" 
						sizeList="[5,10,20,50,100,200,500]"
						pageSize="20" 
						allowAlternating="true" 
					>
						<div property="columns">
							<div type="indexcolumn" width="20" headerAlign="center">序号</div>
							<div 
								name="action" 
								cellCls="actionIcons" 
								width="40"
								renderer="onActionRenderer" 
								cellStyle="padding:0;"
							>操作</div>
							<div field="description" sortField="description_" width="180" headerAlign="" allowSort="true">事项</div>
							<div field="name" width="80" headerAlign="">当前节点</div>
							<div field="assigneeNames" width="50" headerAlign="">处理人</div>
							<div 
								field="createTime" 
								sortField="CREATE_TIME_" 
								width="60" 
								dateFormat="yyyy-MM-dd HH:mm:ss" 
								headerAlign=""
								allowSort="true"
							>创建时间</div>
						</div>
					</div>
				</div>
	
	<redxun:gridScript gridId="datagrid1" 
		entityName="com.redxun.bpm.core.entity.BpmTask" winHeight="450" winWidth="700" entityTitle="流程任务"
		baseUrl="bpm/core/bpmTask" />
	<script type="text/javascript">
	
			function showTask(e){
				var record = e.record;
				var uid=record._uid;
				handTask(uid);
			}

			function onSearch(){
				var formData=$("#searchForm").serializeArray();
				var data=jQuery.param(formData);
				grid.setUrl('${ctxPath}/bpm/core/bpmTask/getSolTasks/${solKey}.do?'+data);
				grid.load();
			}
        	//行功能按钮
			function onActionRenderer(e) {
				var record = e.record;
				var pkId = record.pkId;
				var locked = record.locked;
				var uid=record._uid;
				var s= '<span class="" title="办理" onclick="checkAndHandTask(\'' + uid + '\')">办理</span>';
				if(locked){
					s+= '<span class="" title="释放任务" onclick="releaseTask(\'' + uid + '\')">释放任务</span>';
				}
				return s;
			}
        	
	        grid.on("drawcell", function (e) {
	            var record = e.record,
		        field = e.field,
		        value = e.value;
	            //优先级
				if(field=='priority'){
					var s='<div class="Bar"><div style="width:'+value+'%;"><span></span></div></div>';
					e.cellHtml= s;
				}
				 if(field=='description'){
		            	if(value){
		            		var str= '';
		            		if(record.taskType=='CMM'){
		            			str+='<img src="'+__rootPath+'/styles/icons/commute.png"> ';
		            		}
		            		e.cellHtml=str+record.description;
		            	}else{
		            		e.cellHtml=record.name;
		            	}
		         }
	            
	            if(field=='dueDate'){
	            	if(value){
	            		var c=new Date();
	            		if(c<value){
	            			e.cellHtml = mini.formatDate(value, "yyyy-MM-dd HH:mm");
	            		}else{
	            			e.cellHtml = "<span style='color:red;font-weight:bold'>"+mini.formatDate(value, "yyyy-MM-dd HH:mm")+"</span>";
	            		}
	            	}else{
	            		e.cellHtml='<span style="color:green">不限制</span>';
	            	}
	            }
	            
	            if(field=='owner'){
	            	if(value){
	            		e.cellHtml='<a class="mini-user" iconCls="icon-user" userId="'+value+'"</a>';
	            	}else{
	            		e.cellHtml='<span style="color:red">无</span>';
	            	}
	            }
	            if(field=='agentUserId'){
	            	if(value){
	            		e.cellHtml='<a class="mini-user" iconCls="icon-user" userId="'+value+'"</a>';
	            	}else{
	            		e.cellHtml='<span style="color:red">无</span>';
	            	}
	            }
	        });
	       
	        
	        grid.on("update",function(e){
	        	_LoadUserInfo();
	        });
	        
	      //批量审批
	      function batchApproval(){
	    	  location.href="${ctxPath}/bpm/core/bpmBatchApproval/mgr.do";
	      }
	        
	      //沟通用户
		  function communicateTask() {
				var rows = grid.getSelecteds();
				var pkIds = [];
				var s = "";

				if (rows.length == 0) {
					mini.alert('请选择任务行！');
					return;
				}
				if (rows.length > 0) {
					s = s+"<ul>";
					for (var i = 0; i < rows.length; i++) {
						s = s + "<li><a class='subject' href='javascript:void(0);' href1='rootPath/bpm/core/bpmInst/inform.do?actInstId=" + rows[i].procInstId + "'>" + rows[i].description + '</a></li>';
					}
					s = s+"</ul>";
					
				}
				
				_OpenWindow({
					url : "${ctxPath}/oa/info/infInnerMsg/send.do?pkId=" + pkIds,
					title : "沟通任务",
					width : 750,
					height : 400,
					iconCls:'icon-newMsg',
					onload:function(){
						var iframe = this.getIFrameEl();
						iframe.contentWindow.setSubject(s);
					}
				});
			}
        </script>

		
</body>
</html>