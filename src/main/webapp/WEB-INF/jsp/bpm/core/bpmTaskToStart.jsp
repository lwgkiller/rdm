<%-- 
    Document   : 流程任务列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html >
<head>
<title>流程任务处理页</title>
<%@include file="/commons/customForm.jsp"%>
	<link rel="shortcut icon" href="${ctxPath}/styles/images/index/icon4.ico">
	<script type="text/javascript" src="${ctxPath}/scripts/customer/imgShow.js"></script>
<script src="${ctxPath }/scripts/flow/inst/bpmtask.js?version=${static_res_version}"></script>
<script src="${ctxPath }/scripts/flow/inst/opinion.js?version=${static_res_version}"></script>
<script src="${ctxPath}/scripts/share/dialog.js?version=${static_res_version}" ></script>
<style>
#errorMsg{
	margin: auto; 
	width: 690px; 
	white-space:normal;
	color:red;
	border:solid 1px red;
	padding:5px;
}

.form-title-running h1{
	font-size:22px !important;
}
.table-info{
	width:100%;padding-bottom: 4px;
}

.table-info span{
	font-size:14px;
}

h2{
	font-size:14px;
	color:#000;
	font-weight: bold;
	width:100%;
	text-align: left;
}
h1{
	text-align: center;
	color: #555;
}
.table-detail > tbody > tr.displayTr{
	display: none;
}
.blue{
	color:#44cef6;
}
.green{
	color:#2edfa3;
}
.red{
	color:#ff4c00;
}

.orange{
	color:#ffa400;
}
.purple{
	color:#8d4bbb;
}
.grey{
	color:grey;
}
</style>
</head>
<body>
<rxTag:taskToolBar bpmTask="${bpmTask}" userTaskConfig="${ taskConfig}"
	isShowDiscardBtn="${isShowDiscardBtn }" canReject="${canReject }"   ></rxTag:taskToolBar>
<div class="mini-fit">
	<div class="form-container" id="formcontainer" style="min-height: 100%;height: 100%;">
<%--		<div class="form-title form-title-running" id="taskTitle">
			<h1>${bpmInst.subject }</h1>
		</div>--%>
		<table class="table-info" id="taskInfo">
			<tr>
				<td style="text-align: left;">
                    上一环节：<span id="preNodeName" style="font-weight: bold"></span>&nbsp;&nbsp;&nbsp;
                    上一环节处理人：<span id="preNodeHandler" style="font-weight: bold"></span>&nbsp;&nbsp;&nbsp;
                    处理结果：<div id="preNodeResult" style="display: inline;font-weight: bold"></div>&nbsp;&nbsp;&nbsp;
                    处理意见：<span id="preNodeRemark" style="font-weight: bold"></span>
                </td>
				<td style="text-align:right;">当前流程节点：<span style="color: #0bb20c;font-weight: bold">${bpmTask.name}</span></td>
			</tr>
		</table>
		<br/>
		<div id="errorMsg" style="<c:if test="${empty bpmInst.errors && allowTask.success}">display:none;</c:if>">
			${bpmInst.errors}${allowTask.message}
			<c:if test="${!formModel.result }">${formModel.msg}</c:if>
		</div>
		<form id="processForm" style="height: 95%">
			<c:if test="${not empty formModels}">
				<c:set var="formModel" value="${formModels[0]}"></c:set>
				<c:choose>
					<c:when test="${formModel.type!='SEL-DEV'}">
						<div class="customform" style="width: 100%" id="form-panel">
							<c:choose>
							   <c:when test="${fn:length(formModels)==1}">
									<div class="form-model" id="formModel1"  boDefId="${formModel.boDefId}" formKey="${formModel.formKey}">
										${formModel.content}
									</div>
							   </c:when>
							   <c:otherwise>
									<div class="mini-tabs" activeIndex="0" style="width:100%;">
										<c:forEach var="model" items="${formModels}" varStatus="i" >
											<div title="${model.description}">
												<div class="form-model" id="formModel${i.index}" boDefId="${model.boDefId}" formKey="${model.formKey}">
													${model.content}
												</div>
											</div>
										</c:forEach>
									</div>
							   </c:otherwise>
							</c:choose>
						</div>
					</c:when>
					<c:otherwise>
						<iframe src="${formModel.content}" style="width:100%; height: 99%" id="formFrame" name="formFrame" frameborder="0" scrolling="no" ></iframe>
					</c:otherwise>
				</c:choose>
			</c:if>
		</form>
	</div>
</div>
<script type="text/javascript">
	var formType="${formModel.type}";
	var solId='${bpmInst.solId}';
	var instId='${bpmInst.instId}';
	var actInstId='${bpmInst.actInstId}';
	var procInstId='${bpmTask.procInstId}';
	var taskId='${param.taskId}';
	var token='${bpmTask.token}';
	var nodeId='${bpmTask.taskDefKey}';
	var description = '${bpmTask.description}';
	//是否允许执行。
	var allowTask =${allowTask.success};
	var currentUserId = "${currentUserId}";
		
	$(function(){
		initForm();
	});
		
	function approve(){
		approveTask({
			taskId:taskId,
			formType:formType,
			token:token
		})
	}
    function reject(){
        rejectTask({
            taskId:taskId,
            formType:formType,
            token:token
        })
    }

    function transferTaskTest() {
        transferTaskSelf(currentUserId,taskId);
    }

    function changeFrameHeight(){
        var ifm= document.getElementById("formFrame");
        ifm.height=$("#formcontainer").height()-$("#errorMsg").height()-$("#tableInfo").height();
    }
    window.onresize=function(){
        changeFrameHeight();
    }
</script>
	
</body>
</html>