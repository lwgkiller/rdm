<%-- 
    Document   : [BpmSolFv]列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>[BpmSolFv]列表管理</title>
     <%@include file="/commons/list.jsp"%>   
    </head>
    <body>
        
        <redxun:toolbar entityName="com.redxun.bpm.core.entity.BpmSolFv"/>
        
        <div class="mini-fit" style="height:100%;">
            <div id="datagrid1" class="mini-datagrid" style="width:100%;height:100%;" allowResize="false"
                 url="${ctxPath}/bpm/core/bpmSolFv/listData.do"  idField="id" multiSelect="true" showColumnsMenu="true"
                 sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
                <div property="columns">
                    <div type="checkcolumn" width="20"></div>
                    <div name="action" cellCls="actionIcons" width="22" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>        
	                 											 																		<div field="solId" width="120" headerAlign="center" allowSort="true">解决方案ID</div>
																	 																		<div field="nodeId" width="120" headerAlign="center" allowSort="true">节点ID</div>
																	 																		<div field="nodeText" width="120" headerAlign="center" allowSort="true">节点名称</div>
																	 																		<div field="formType" width="120" headerAlign="center" allowSort="true">表单类型</div>
																	 																		<div field="formUri" width="120" headerAlign="center" allowSort="true">表单地址</div>
																	 																							 																							 																							 																							 																							                  </div>
            </div>
        </div>
        
        <script type="text/javascript">
        	//行功能按钮
	        function onActionRenderer(e) {
	            var record = e.record;
	            var uid = record.pkId;
	            var s = '<span  title="明细" onclick="detailRow(\'' + uid + '\')"></span>'
	                    + ' <span  title="编辑" onclick="editRow(\'' + uid + '\')"></span>'
	                    + ' <span  title="删除" onclick="delRow(\'' + uid + '\')"></span>';
	            return s;
	        }
        </script>
        <script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
        <redxun:gridScript gridId="datagrid1" entityName="com.redxun.bpm.core.entity.BpmSolFv" 
        	winHeight="450" winWidth="700"
          	entityTitle="[BpmSolFv]" baseUrl="bpm/core/bpmSolFv"/>
    </body>
</html>