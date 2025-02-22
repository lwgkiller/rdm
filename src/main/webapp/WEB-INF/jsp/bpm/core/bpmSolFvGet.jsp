<%-- 
    Document   : [BpmSolFv]明细页
    Created on : 2015-3-28, 17:42:57
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>[BpmSolFv]明细</title>
     <%@include file="/commons/get.jsp"%>   
    </head>
    <body>
        <rx:toolbar toolbarId="toolbar1"/>
        <div id="form1" class="form-outer">
             <div style="padding:5px;">
                    <table style="width:100%" class="table-detail" cellpadding="0" cellspacing="1">
                    	<caption>[BpmSolFv]基本信息</caption>
                        																														<tr>
						 		<th>
						 			解决方案ID：
						 		</th>
	                            <td>
	                                ${bpmSolFv.solId}
	                            </td>
						</tr>
																																				<tr>
						 		<th>
						 			节点ID：
						 		</th>
	                            <td>
	                                ${bpmSolFv.nodeId}
	                            </td>
						</tr>
																																				<tr>
						 		<th>
						 			节点名称：
						 		</th>
	                            <td>
	                                ${bpmSolFv.nodeText}
	                            </td>
						</tr>
																																				<tr>
						 		<th>
						 			表单类型：
						 		</th>
	                            <td>
	                                ${bpmSolFv.formType}
	                            </td>
						</tr>
																																				<tr>
						 		<th>
						 			表单地址：
						 		</th>
	                            <td>
	                                ${bpmSolFv.formUri}
	                            </td>
						</tr>
																																																																																																																																										                     </table>
                 </div>
	            <div style="padding:5px">
					 <table class="table-detail" cellpadding="0" cellspacing="1">
					 	<caption>更新信息</caption>
						<tr>
							<th>创建人</th>
							<td><rxc:userLabel userId="${bpmSolFv.createBy}"/></td>
							<th>创建时间</th>
							<td><fmt:formatDate value="${bpmSolFv.createTime}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
						<tr>
							<th>更新人</th>
							<td><rxc:userLabel userId="${bpmSolFv.updateBy}"/></td>
							<th>更新时间</th>
							<td><fmt:formatDate value="${bpmSolFv.updateTime}" pattern="yyyy-MM-dd HH:mm" /></td>
						</tr>
					</table>
	        	</div>
        	</div>
        <rx:detailScript baseUrl="bpm/core/bpmSolFv" formId="form1"/>
    </body>
</html>