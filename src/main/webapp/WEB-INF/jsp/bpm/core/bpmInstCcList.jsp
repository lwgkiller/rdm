<%-- 
    Document   : [BpmInstCc]列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>[BpmInstCc]列表管理</title>
        <%@include file="/commons/list.jsp"%> 
    </head>
    <body>
        
        <redxun:toolbar entityName="com.redxun.bpm.core.entity.BpmInstCc" excludeButtons="popupAddMenu,edit,detail,importData,export"/>
        
        <div class="mini-fit" style="height:100%;">
            <div id="datagrid1" class="mini-datagrid" style="width:100%;height:100%;" allowResize="false"
                 url="${ctxPath}/bpm/core/bpmInstCc/listData.do"  idField="ccId" multiSelect="true" showColumnsMenu="true"
                 sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
                <div property="columns">
                    <div type="checkcolumn" width="20"></div>
                    <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>  
	                <div field="instId" width="120" headerAlign="center" allowSort="true">流程实例ID</div>
						<div field="subject" width="120" headerAlign="center" allowSort="true">抄送标题</div>
						<div field="nodeId" width="120" headerAlign="center" allowSort="true">节点ID</div>
						<div field="nodeName" width="120" headerAlign="center" allowSort="true">节点名称</div>
						<div field="fromUser" width="120" headerAlign="center" allowSort="true">抄送人</div>
																	 																							 																							 																							 																							 																							                  </div>
            </div>
        </div>
        
        <script type="text/javascript">
        	//行功能按钮
	        function onActionRenderer(e) {
	            var record = e.record;
	            var pkId = record.pkId;
	            var s = /* '<span  title="明细" onclick="detailRow(\'' + pkId + '\')">明细</span>'
	                    + ' <span  title="编辑" onclick="editRow(\'' + pkId + '\')">编辑</span>'
	                    +  */
	                    ' <span  title="删除" onclick="delRow(\'' + pkId + '\')">删除</span>';
	            return s;
	        }
        </script>
        <script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
        <redxun:gridScript gridId="datagrid1" entityName="com.redxun.bpm.core.entity.BpmInstCc" 
        	winHeight="450" winWidth="700"
          	entityTitle="[BpmInstCc]" baseUrl="bpm/core/bpmInstCc"/>
    </body>
</html>