
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>量产产品导入新市场列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/world/lccpList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<%--<span class="text" style="width:auto">机型: </span>--%>
					<span class="text" style="width:auto"><spring:message code="page.lccpList.type" />: </span>
					<input class="mini-textbox" id="model" name="model" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.lccpList.lsh" />: </span>
					<input class="mini-textbox" id="ddNumber" name="ddNumber" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.lccpList.cpzg" />: </span>
					<input class="mini-textbox" id="cpzg" name="cpzg" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.lccpList.cx" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.lccpList.qkcx" /></a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addLccp()"><spring:message code="page.lccpList.xz" /></a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="lccpListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/Lccp/queryLccp.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.lccpList.cz" /></div>
			<div field="ddNumber"  width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.lsh" /></div>
			<div field="ckMonth"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.yf" /></div>
			<div field="model"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.cpxh" /></div>
			<div field="region"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.ckqy" /></div>
			<div field="country"  width="70" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.ckgj" /></div>
			<div field="need"  width="40" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.scrl" /></div>
			<div field="cpzg"  width="70" headerAlign="center" align="center" allowSort="true"><spring:message code="page.lccpList.fzcpzg" /></div>
			<div field="taskName" headerAlign='center' align='center' width="80"><spring:message code="page.lccpList.dqrw" /></div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.lccpList.dqclr" /></div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer"><spring:message code="page.lccpList.zt" /></div>
			<div field="userName" headerAlign='center' align='center' width="40"><spring:message code="page.lccpList.cjr" /></div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.lccpList.cjsj" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var lccpListGrid=mini.get("lccpListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var lccpId = record.lccpId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title="<spring:message code="page.lccpList.mx" />" onclick="lccpDetail(\'' + lccpId + '\',\'' + status + '\')"><spring:message code="page.lccpList.mx" /></span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="<spring:message code="page.lccpList.bj" />" onclick="lccpEdit(\'' + lccpId + '\',\'' + instId + '\',\'' + status + '\')"><spring:message code="page.lccpList.bj" /></span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title="<spring:message code="page.lccpList.bl" />" onclick="lccpTask(\'' + record.myTaskId + '\')"><spring:message code="page.lccpList.bl" /></span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title="<spring:message code="page.lccpList.sc" />" onclick="removelccp(\'' + lccpId + '\')"><spring:message code="page.lccpList.sc" /></span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title="<spring:message code="page.lccpList.sc" />" onclick="removelccp(\'' + lccpId + '\')"><spring:message code="page.lccpList.sc" /></span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '审批完成','css' : 'blue'},
			{'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }
</script>
<redxun:gridScript gridId="lccpListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>