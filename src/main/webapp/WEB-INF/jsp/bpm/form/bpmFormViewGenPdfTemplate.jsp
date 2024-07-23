<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
    <head>
        <title>选择模版</title>
        <%@include file="/commons/edit.jsp" %>
        <script type="text/javascript" src="${ctxPath}/scripts/share/dialog.js"></script>
        <script type="text/javascript" src="${ctxPath}/scripts/sys/bo/BoUtil.js"></script>
        <script type="text/javascript" src="${ctxPath}/scripts/flow/form/bpmMobileForm.js"></script>
		<style>
			select{
				width: 100%;
			}
		</style>
    </head>
    <body >
	<div class="topToolBar">
		<div><a class="mini-button"  onclick="next()">下一步</a></div>
	</div>
	<div class="mini-fit">
		<div class="form-container">
			<form id="form1" method="post" >
				<table class="table-detail column-two table-align" cellspacing="1" cellpadding="0">
					<caption>PDF表单模板</caption>
					<tbody id="tbody"></tbody>
				</table>
			</form>
		 </div>
	</div>
        <script type="text/javascript">
        var boDefId = "";
        $(function(){
        	var data = '${data}';
        	var jsonArr = mini.decode(data);
        	boDefId = '${boDefId}';
        	var html= baidu.template('templateList',{list:jsonArr});  
    		$("#tbody").html(html);
        });
        
        function next(){
        	var form = new mini.Form("#form1");
        	form.validate();
        	if(!form.isValid()) return;
        	
        	var viewId = "${viewId}";
        	var templates=encodeURI( getTemplate());
        	var url=__rootPath + "/bpm/form/bpmFormView/pdfTempEdit.do?boDefId=" + boDefId +"&templates=" + templates+"&viewId="+viewId;
        	
        	_OpenWindow({
        		url: url,
                title: "pdf导出表单", width: "100%", height:"100%",
                ondestroy: function(action) {
                	CloseWindow('ok');
                }
        	});
        }
        
        function getTemplate(){
        	var aryTr=$("tr",$("#tbody"));
        	var json={};
        	aryTr.each(function(){
        		var row=$(this);
        		var selObj=$("select",row);
        		var alias=selObj.attr("alias");
        		var type=selObj.attr("type");
        		var template=selObj.val();
        		var obj={};
        		obj[alias]=template;
        		if(type=="main"){
        			if(!json[type]){
        				json[type]=obj;
        			}
        		}
        		else if(type=="opinion"){
        			json["_opinion_"]=template;
        		}
        		else{
        			if(!json["sub"]){
        				json["sub"]=[obj];
        			}
        			else{
        				json["sub"].push(obj);
        			}
        		}
        	})
        	
        	var templates=JSON.stringify(json);
        	
        	return templates;
        }
        
        </script>
        <script id="templateList"  type="text/html">
		<#for(var i=0;i<list.length;i++){
			var obj=list[i];
			var tempAry=obj.template;
		#>
		<tr >
				<td ><#=obj.name#></td>
				<td>
					<select name="template" alias="<#=obj.key#>" type="<#=obj.type#>">
						<#for(var n=0;n<tempAry.length;n++){
							var tmp=tempAry[n];
						#>
							<option value="<#=tmp.alias#>"><#=tmp.name#></option>
						<#}#>
					</select>
				</td>
		</tr>
		<#}#>
		</script>

    </body>
</html>