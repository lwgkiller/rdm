<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
<title>流程实例启动页</title>
<%@include file="/commons/customForm.jsp"%>
	<script type="text/javascript" src="${ctxPath}/scripts/customer/imgShow.js"></script>
<script src="${ctxPath }/scripts/flow/inst/bpminst.js?version=${static_res_version}"></script>

<script type="text/javascript">
	var conf={
		solId:"${bpmSolution.solId}",
		mainSolId:"${param.mainSolId}",
		taskId:"${param.taskId}",
		instId:'${instId}',
		tmpInstId:'${param.tmpInstId}',
		formType:"${formModels[0].type}",
		actDefId:"${bpmSolution.actDefId}",
		confirmStart:"${processConfig.confirmStart}",
		selectUser:"${processConfig.selectUser}",
		isSkipFirst:"${processConfig.isSkipFirst}",
		showExecPath:"${processConfig.showExecPath}",
		needOpinion:"${processConfig.needOpinion}",
		from:"${from}"
	};
	
	$(function() {
		//解析动态表单
		if(conf.formType!="SEL-DEV"){
			renderMiniHtml({});	
		}else{
			//自动高度
			// autoHeightOnLoad($("#formFrame"));
            // changeFrameHeight();
		}
	 });

    function changeFrameHeight(){
        var ifm= document.getElementById("formFrame");
        ifm.height=document.body.clientHeight-90-$("#errorMsg").height();
    }

    window.onresize=function(){
        changeFrameHeight();
    }
    var solName="${bpmSolution.name}";
</script>
</head>
<body>
	
<rxTag:processToolBar buttons="${buttons }"></rxTag:processToolBar>
<div class="mini-fit">
<div class="form-container" style="margin:0 auto;">
	<div id="errorMsg" style="margin:0 auto;<c:if test="${formModel.result}">display:none;</c:if> color:red;">
		${formModel.msg}
	</div>
	

	<form id="processForm" style="height: 100%">
			<rxTag:processForm formModels="${formModels}"></rxTag:processForm>
	</form>
</div>
</div>
</body>
</html>