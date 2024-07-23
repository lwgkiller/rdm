
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>改进计划列表管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/world/ckddList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.ckddList.jx" />: </span>
					<input class="mini-textbox" id="model" name="model" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.ckddList.ddh" />: </span>
					<input class="mini-textbox" id="ddNumber" name="ddNumber" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.ckddList.cpzg" />: </span>
					<input class="mini-textbox" id="cpzg" name="cpzg" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.ckddList.tsddsqh" />: </span>
					<input class="mini-textbox" id="rdmSpecialddNumber" name="rdmSpecialddNumber" />
				</li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.ckddList.cx" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.ckddList.qkcx" /></a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCkdd()"><spring:message code="page.ckddList.xz" /></a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="ckddListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/Ckdd/queryCkdd.do" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.ckddList.cz" /></div>
			<div field="rdmSpecialddNumber"  width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.tsddsqh" /></div>
			<div field="ddNumber"  width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.ddh" /></div>
			<div field="ckMonth"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.yf" /></div>
			<div field="model"  width="40" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.cpxh" /></div>
			<div field="country"  width="55" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.ckgj" /></div>
			<div field="need"  width="40" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.xqsl" /></div>
			<div field="cpzg"  width="45" headerAlign="center" align="center" allowSort="true"><spring:message code="page.ckddList.fzcpzg" /></div>
			<div field="taskName" headerAlign='center' align='center' width="60"><spring:message code="page.ckddList.dqrw" /></div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.ckddList.dqclr" /></div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer"><spring:message code="page.ckddList.zt" /></div>
			<div field="userName" headerAlign='center' align='center' width="40"><spring:message code="page.ckddList.cjr" /></div>
			<div field="CREATE_TIME_"  width="50" headerAlign="center" align="center" allowSort="false"><spring:message code="page.ckddList.cjsj" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var ckddListGrid=mini.get("ckddListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var ckddId = record.ckddId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + ckddList_mx + ' onclick="ckddDetail(\'' + ckddId + '\',\'' + status + '\')"><spring:message code="page.ckddList.mx" /></span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + ckddList_bj + ' onclick="ckddEdit(\'' + ckddId + '\',\'' + instId + '\',\'' + status + '\')"><spring:message code="page.ckddList.bj" /></span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title=' + ckddList_bl + ' onclick="ckddTask(\'' + record.myTaskId + '\')"><spring:message code="page.ckddList.bl" /></span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title=' + ckddList_schu + ' onclick="removeCkdd(\'' + ckddId + '\')"><spring:message code="page.ckddList.sc" /></span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title=' + ckddList_schu + ' onclick="removeCkdd(\'' + ckddId + '\')"><spring:message code="page.ckddList.sc" /></span>';
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
<redxun:gridScript gridId="ckddListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>