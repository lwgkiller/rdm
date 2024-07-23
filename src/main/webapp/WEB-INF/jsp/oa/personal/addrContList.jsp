<%-- 
    Document   : 通讯录联系人信息列表页
    Created on : 2015-3-21, 0:11:48
    Author     : csx
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="redxun" uri="http://www.redxun.cn/gridFun"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>通讯录联系人信息列表管理</title>
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

	<redxun:toolbar entityName="com.redxun.oa.personal.entity.AddrCont" />

	<div class="mini-fit" style="height: 100%;">
		<div id="datagrid1" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/oa/personal/addrCont/listData.do" idField="contId" multiSelect="true" showColumnsMenu="true" sizeList="[5,10,20,50,100,200,500]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
				<div type="checkcolumn" width="20"></div>
				<div name="action" cellCls="actionIcons" width="22" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">#</div>
				<div field="addrId" width="120" headerAlign="center" allowSort="true">联系人ID</div>
				<div field="type" width="120" headerAlign="center" allowSort="true">类型 手机号=MOBILE 家庭地址=HOME_ADDRESS 工作地址=WORK_ADDRESS QQ=QQ 微信=WEI_XIN GoogleTalk=GOOGLE-TALK 工作=WORK_INFO 备注=MEMO</div>
				<div field="contact" width="120" headerAlign="center" allowSort="true">联系主信息</div>
				<div field="ext1" width="120" headerAlign="center" allowSort="true">联系扩展字段1</div>
				<div field="ext2" width="120" headerAlign="center" allowSort="true">联系扩展字段2</div>
				<div field="ext3" width="120" headerAlign="center" allowSort="true">联系扩展字段3</div>
				<div field="ext4" width="120" headerAlign="center" allowSort="true">联系扩展字段4</div>
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
        </script>
	<script src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
	<redxun:gridScript gridId="datagrid1" entityName="com.redxun.oa.personal.entity.AddrCont" winHeight="450" winWidth="700" entityTitle="[AddrCont]" baseUrl="oa/personal/addrCont" />
</body>
</html>