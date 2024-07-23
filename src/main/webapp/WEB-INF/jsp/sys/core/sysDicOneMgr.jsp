<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<%@include file="/commons/list.jsp" %>
	<title>数据字典分类管理</title>
	<style>
		span.icon-button{
			font-size: 16px;
		}
		.icon-shang-add,
		.icon-xia-add{
			color: #0daaf6;
		}
		.icon-jia{
			color: #ff8b00;
		}
		.icon-zhuanhuan{
			color: #9e7ed0;;
		}
		.icon-trash{
			color: red;
		}
		.icon-grant{
			color: #66b1ff;
		}
		.icon-baocun7{
			color:#2cca4e;
		}
	</style>
</head>
<body>
			<div id="toolbar1" class="mini-toolbar" >
				<ul class="toolBtnBox">
					<li>【${sysTree.name}】<spring:message code="page.sysDicOneMgr.name" /></li>
					<li>
						<a class="mini-button" onclick="saveItems()"><spring:message code="page.sysDicOneMgr.name1" /></a>
						<a class="mini-button" onclick="newRow();"><spring:message code="page.sysDicOneMgr.name2" /></a>
						<c:if test="${showType=='TREE'}">
							<a class="mini-button" onclick="newSubRow();"><spring:message code="page.sysDicOneMgr.name3" /></a>
						</c:if>
						<a class="mini-button btn-red" onclick="delRow();"><spring:message code="page.sysDicOneMgr.name4" /></a>
					</li>
				</ul>
	        </div>
	         <div class="mini-fit rx-grid-fit" style="border:0;background-color: white;">
		        <div id="sysdicGrid" class="mini-treegrid" style="width:100%;height:100%;"     
		            showTreeIcon="true" 
		            treeColumn="name" idField="dicId" parentField="parentId"  valueField="dicId"
		            resultAsTree="false"  allowAlternating="true"
		            allowResize="true" expandOnLoad="true" 
		            allowCellValid="true" oncellvalidation="onCellValidation" 
		            allowCellEdit="true" allowCellSelect="true" url="${ctxPath}/sys/core/sysDic/listByTreeId.do?treeId=${treeId}">
		            <div property="columns">
		            	<div name="action" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>
		                <div name="name" field="name" width="160"><spring:message code="page.sysDicOneMgr.name5" />
		                	<input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
		                </div>
		                <div field="key" name="key" width="80"><spring:message code="page.sysDicOneMgr.name6" />
		                	<input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
		                </div>
		                <div field="value" name="value" width="80"><spring:message code="page.sysDicOneMgr.name7" />
		                	<input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
		                </div>
		                <div name="descp" field="descp" width="120"><spring:message code="page.sysDicOneMgr.name8" />
		                	<input property="editor" class="mini-textbox" style="width:100%;" />
		                </div>
		                <div name="sn" field="sn" width="60"><spring:message code="page.sysDicOneMgr.name9" />
		                	<input property="editor" changeOnMousewheel="false" class="mini-spinner"  minValue="1" maxValue="100000" required="true"/>
		                </div>
		            </div>
		        </div>
	        </div>
	        
	       
			
	   <script type="text/javascript">
	   	mini.parse();
	   	
	   	var catKey='${catKey}';
	   	
	   	var showType='${showType}';
	   	
	   	var grid=mini.get("sysdicGrid");
	   	
	    var treeId='${treeId}';
	
	   	function onCellValidation(e){
        	if(e.field=='key' && (!e.value||e.value=='')){
        		 e.isValid = false;
        		 e.errorText=sysDicOneMgr_name;
        	}
        	if(e.field=='name' && (!e.value||e.value=='')){
        		e.isValid = false;
       		 	e.errorText=sysDicOneMgr_name1;
        	}
        	
        	if(e.field=='value' && (!e.value||e.value=='')){
        		e.isValid = false;
       		 	e.errorText=sysDicOneMgr_name2;
        	}
        	
        	if(e.field=='sn' && (!e.value||e.value=='')){
        		e.isValid = false;
       		 	e.errorText=sysDicOneMgr_name3;
        	}
        }
	   	
	   	function onActionRenderer(e) {
            var record = e.record;
            var uid = record._uid;
            
            var s = '<span class="icon-button icon-shang-add" title=' + sysDicOneMgr_name4 + ' onclick="newBeforeRow(\''+uid+'\')"></span>';
            s+=' <span class="icon-button icon-xia-add" title=' + sysDicOneMgr_name5 + ' onclick="newAfterRow(\''+uid+'\')"></span>';
            
            //为树类型才允许往下添加
            if(showType=='TREE'){
            	s+= '<span class="icon-button icon-jia" title=' + sysDicOneMgr_name6 + ' onclick="newSubRow()"></span>';
            }
            s+=' <span class="icon-button icon-baocun7" title=' + sysDicOneMgr_name7 + ' onclick="saveRow(\'' + uid + '\')"></span>';
            s+=' <span class="icon-button icon-trash" title=' + sysDicOneMgr_name8 + ' onclick="delRow(\'' + uid + '\')"></span>';
            return s;
        }
		//在当前选择行的下添加子记录
        function newSubRow(){
			
			var node = grid.getSelectedNode();
			 //为树类型才允许往下添加
            if(showType=='TREE'){
	            grid.addNode({}, "add", node);
            }else{
	            grid.addNode({}, "before", node);
            }
           
        }
		
        function newRow() {            
        	
        	var node = grid.getSelectedNode();
            grid.addNode({}, "after", node);
        }

        function newAfterRow(row_uid){
        	var node = grid.getRowByUID(row_uid);
        	grid.addNode({}, "after", node);
        	grid.cancelEdit();
        	grid.beginEditRow(node);
        }
        
        function newBeforeRow(row_uid){
        	var node = grid.getRowByUID(row_uid);
        	grid.addNode({}, "before", node);
        	grid.cancelEdit();
        	grid.beginEditRow(node);
        }
			
	   	//保存单行
	    function saveRow(row_uid) {
        	//表格检验
        	grid.validate();
        	if(!grid.isValid()){
            	return;
            }
			var row = grid.getRowByUID(row_uid);

            var json = mini.encode(row);
            
            _SubmitJson({
            	url: "${ctxPath}/sys/core/sysDic/rowSave.do",
            	data:{
            		treeId:treeId,
            		data:json},
            	method:'POST',
            	success:function(text){
            		var result=mini.decode(text);
            		if(result.data && result.data.treeId){
                		row.treeId=result.data.treeId;
                	}
            		grid.acceptRecord(row);
            		grid.load();
            	}
            });
        }
        
      	//批量多行数据字典
        function saveItems(){
			
        	//表格检验
        	grid.validate();
        	if(!grid.isValid()){
            	return;
            }
        	
        	//获得表格的每行值
        	var data = grid.getData();
        	if(data.length<=0)return;
            var json = mini.encode(data);
            
            var postData={treeId:treeId,gridData:json};
            
            _SubmitJson({
            	url: "${ctxPath}/sys/core/sysDic/batchSave.do",
            	data:postData,
            	method:'POST',
            	success:function(text){
            		grid.load();
            	}
            });
        }
      	
        //删除数据项
        function delRow(row_uid) {
        	
        	var row=null;
        	if(row_uid){
        		row = grid.getRowByUID(row_uid);
        	}else{
        		row = grid.getSelected();	
        	}
        	
        	if(!row){
        		alert(sysDicOneMgr_name9);
        		return;
        	}
        	
        	if (!confirm(sysDicOneMgr_name10)) {return;}

            if(row && !row.dicId){
            	grid.removeNode(row);
            	return;
            }else if(row.dicId){
            	_SubmitJson({
            		url: "${ctxPath}/sys/core/sysDic/del.do?ids="+row.dicId,
                	success:function(text){
                		grid.removeNode(row);
                	}
                });
            } 
        }
	</script>
</body>
</html>