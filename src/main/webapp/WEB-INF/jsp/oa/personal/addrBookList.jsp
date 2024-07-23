<%-- 
    Document   : [AddrBook]列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="redxun" uri="http://www.redxun.cn/gridFun"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>[AddrBook]列表管理</title>
<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
<%@include file="/commons/dynamic.jspf"%>
<link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css" />
<link href="${ctxPath}/styles/commons${index}.css" rel="stylesheet" type="text/css" />
<link href="${ctxPath}/styles/list.css" rel="stylesheet" type="text/css" />
<link href="${ctxPath}/styles/icons.css" rel="stylesheet" type="text/css" />
<script src="${ctxPath}/scripts/boot.js" type="text/javascript"></script>
<script src="${ctxPath}/scripts/share.js" type="text/javascript"></script>
<script src="${ctxPath}/scripts/jquery/plugins/jQuery.download.js" type="text/javascript"></script>

</head>
<body>

	<redxun:toolbar entityName="com.redxun.oa.personal.entity.AddrBook" />

	<div class="mini-fit" style="height: 100%;">
		<div id="datagrid1" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/oa/personal/addrBook/getAddrBookByGroupId.do?groupId=${param['groupId']}" idField="addrId" multiSelect="true" showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons" onrowdblclick="getInfo">
			<div property="columns">
				<div type="indexcolumn" width="20"></div>
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>
				<div field="name" width="120" headerAlign="center" allowSort="true">姓名</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">
        	//行功能按钮
	        function onActionRenderer(e) {
	            var record = e.record;
	            var pkId = record.pkId;
	            var s = '<span  title="明细" onclick="detailRow(\'' + pkId + '\')">明细</span>'
	                    + ' <span  title="编辑" onclick="editRow(\'' + pkId + '\')">编辑</span>'
	                    + ' <span  title="删除" onclick="delRow(\'' + pkId + '\')">删除</span>';
	            return s;
	        }
        	
        	/*双击行打开该联系人查看信息*/
        	function getInfo(e){                         
	            var record = e.record;
	            var pkId = record.pkId;
	            var name = record.name;
	            _OpenWindow({
	            	title:name,
	            	height:600,
	            	width:900,
	            	url:__rootPath+"/oa/personal/addrBook/get.do?pkId="+pkId
	            });
        	}
        </script>
	<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.oa.personal.entity.AddrBook" winHeight="450" winWidth="700" entityTitle="联系人" baseUrl="oa/personal/addrBook" />
</body>
</html>