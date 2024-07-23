<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准消息管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardMessageManagement.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="margin-right: 15px">
			<span class="text" style="width:auto"><spring:message code="page.standardMessageManagement.bztxlb" />: </span>
			<input id="systemCategory" style="margin-right:10px" class="mini-combobox" name="systemCategoryId" textfield="systemCategoryName" valuefield="systemCategoryId" emptyText="<spring:message code="page.standardMessageManagement.qxz" />..."
				   required="false" allowInput="false" showNullItem="false" />
			<span class="text" style="width:auto"><spring:message code="page.standardMessageManagement.bzmc" />: </span>
			<input class="mini-textbox" id="standardName" style="margin-right:10px"/>
			<a id="refresh" class="mini-button " iconCls="icon-search" onclick="refreshData()"><spring:message code="page.standardMessageManagement.cx" /></a>
			<a id="editMsg" class="mini-button " iconCls="icon-emailEdit" onclick="editMsg()"><spring:message code="page.standardMessageManagement.fbtz" /></a>
			<a id="removeMsg" class="mini-button btn-red" style="margin-right: 5px" onclick="deleteMessage()"><spring:message code="page.standardMessageManagement.sc" /></a>
		</li>
	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="standardMsgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" sizeList="[50,100,200]" pageSize="50"
		 idField="id" allowAlternating="true" showPager="true" multiSelect="true" >
		<div property="columns">
			<div type="checkcolumn" width="10"></div>
			<div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.standardMessageManagement.cz" /></div>
			<div field="standardMsgTitle" headerAlign='center' align='left' renderer="onMessageTitleRenderer"><spring:message code="page.standardMessageManagement.tzbt" /></div>
			<div field="standardName" headerAlign='center' align='center' width="60"><spring:message code="page.standardMessageManagement.xgbz" /></div>
			<div field="receiveDeptNames" headerAlign='center' align='center' width="40"><spring:message code="page.standardMessageManagement.xgbm" /></div>
			<div field="readStatus" headerAlign='center' align='center' width="40"  renderer="readStatusRenderer"><spring:message code="page.standardMessageManagement.xgxg" /></div>
			<div field="creator" headerAlign='center' align='center' width="30" ><spring:message code="page.standardMessageManagement.fbr" /></div>
			<div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center"><spring:message code="page.standardMessageManagement.fbsj" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var standardMsgListGrid=mini.get("standardMsgListGrid");
    var currentUserRoles=${currentUserRoles};
    var isManager = whetherIsStandardManager(currentUserRoles);
    var systemCategoryValue="${systemCategoryValue}";


    //操作栏
    standardMsgListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function onMessageTitleRenderer(e) {
        var record = e.record;
        var title=record.standardMsgTitle;
        return '<a href="#" style="color:#44cef6;" onclick="seeMessage(\''+record.id+'\',\''+record.relatedStandardId+'\')">'+title+'</a>';
	}

    function onMessageActionRenderer(e) {
        var record = e.record;
        var result='';
        result+= '<span title=' + standardMessageManagement_ck + ' onclick="seeMessage(\''+record.id+'\',\''+record.relatedStandardId+'\')">' + standardMessageManagement_ck + '</span>';
        result+= '<span title=' + standardMessageManagement_bj + ' onclick="editMsg(\''+record.id+'\',\''+record.relatedStandardId+'\')">' + standardMessageManagement_bj + '</span>';
		result+='<span title=' + standardMessageManagement_sc + ' onclick="deleteMessage(\''+record.id+'\')">' + standardMessageManagement_sc + '</span>';
        return result;
    }
	function typeRenderer(e) {
		var record = e.record;
		var result='';
		if(record.isSelf=='1'){
			result+= '<span>部分</span>';
		}else{
			result+= '<span>公共</span>';
		}
		return result;
	}
	function readStatusRenderer(e) {
		var record = e.record;
		var isSelf = record.isSelf;
		var result = '';

		result = '<span style="color: green;cursor: pointer;text-decoration:underline;" title=' + standardMessageManagement_bzxxqk + ' onclick="seeReadStatus(\''+record.id+'\',\''+record.isSelf+'\')">' + standardMessageManagement_bzxxqk + '</span>';

		return result;
	}
	function seeReadStatus(msgId,isSelf) {
		if(!isManager) {
			mini.alert(standardMessageManagement_myczqx);
			return;
		}
		mini.open({
			title: standardMessageManagement_xxydqk,
			url: jsUseCtxPath + "/standardManager/core/standardMessage/readStatusPage.do?msgId="+msgId+"&isSelf="+isSelf,
			width: 650,
			height: 550,
			allowResize: true,
			onload: function () {
			}
		});
	}
</script>
</body>
</html>
