
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>售前文件变更列表管理</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
	<div class="searchBox">
		<form id="searchForm" class="search-form" style="margin-bottom: 25px">
			<ul >
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.sqbgList.name" />: </span>
					<input class="mini-textbox" id="saleId" name="saleId" />
				</li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.sqbgList.name1" />: </span>
					<input class="mini-textbox" style="width: 150px" name="designModel"/></li>
				<li style="margin-right: 15px">
					<span class="text" style="width:auto"><spring:message code="page.sqbgList.name2" />: </span>
					<input class="mini-textbox" style="width: 150px" name="saleModel"/></li>
				<li style="margin-left: 10px">
					<a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()"><spring:message code="page.sqbgList.name3" /></a>
					<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()"><spring:message code="page.sqbgList.name4" /></a>
					<div style="display: inline-block" class="separator"></div>
					<a class="mini-button" style="margin-right: 5px" plain="true" onclick="addSqbg()"><spring:message code="page.sqbgList.name5" /></a>
				</li>
			</ul>
		</form>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="sqbgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
		 url="${ctxPath}/Sqbg/querySqbg.do?applyType=${applyType}" idField="id"
		 multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.sqbgList.name6" /></div>
			<div field="id"  width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.sqbgList.name7" /></div>
			<div field="designModel"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.sqbgList.name1" /></div>
			<div field="saleModel"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.sqbgList.name2" /></div>
			<div field="newVersion"  width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.sqbgList.name8" /></div>
			<div field="fileType" width="80" headerAlign="center" align="center" allowSort="true"
				 renderer="onWSwitchType"><spring:message code="page.sqbgList.name9" />
			</div>
			<div field="taskName" headerAlign='center' align='center' width="40"><spring:message code="page.sqbgList.name10" /></div>
			<div field="allTaskUserNames" headerAlign='center' align='center' width="40"><spring:message code="page.sqbgList.name11" /></div>
			<div field="status" headerAlign='center' align='center' width="40" renderer="onStatusRenderer"><spring:message code="page.sqbgList.name12" /></div>
			<div field="userName" headerAlign='center' align='center' width="40"><spring:message code="page.sqbgList.name13" /></div>
			<div field="CREATE_TIME_"  width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.sqbgList.name14" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var sqbgListGrid=mini.get("sqbgListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";
    var applyType = "${applyType}";
    var typeList = getDics("saleFile_WJFL");
    function onWSwitchType(e) {
        var record = e.record;
        var fileType = record.fileType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == fileType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }
    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var sqbgId = record.sqbgId;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED'){
            s += '<span  title=' + sqbgList_name + ' onclick="sqbgDetail(\'' + sqbgId + '\',\'' + status + '\')">' + sqbgList_name + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + sqbgList_name1 + ' onclick="sqbgEdit(\'' + sqbgId + '\',\'' + instId + '\',\'' + status + '\')">' + sqbgList_name1 + '</span>';
        }
        if(record.status =='RUNNING'){
            var currentProcessUserId=record.currentProcessUserId;
            if(record.myTaskId) {
                s+='<span  title=' + sqbgList_name2 + ' onclick="sqbgTask(\'' + record.myTaskId + '\')">' + sqbgList_name2 + '</span>';
            }
        }
        if (status == 'DRAFTED' ) {
            s += '<span  title=' + sqbgList_name3 + ' onclick="removeSqbg(\'' + sqbgId + '\')">' + sqbgList_name3 + '</span>';
        }
        if (status == 'SUCCESS_END' &&(currentUserNo=='admin')) {
            s += '<span  title=' + sqbgList_name3 + ' onclick="removeSqbg(\'' + sqbgId + '\')">' + sqbgList_name3 + '</span>';
        }
        return s;
    }


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }
    $(function () {
        searchFrm();
    });


    function addSqbg() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/SQWJBG/start.do?applyType="+applyType;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (sqbgListGrid) {
                    sqbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function sqbgEdit(sqbgId,instId,status) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId+ "&sqbgId=" + sqbgId+"&status="+status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (sqbgListGrid) {
                    sqbgListGrid.reload()
                }
            }
        }, 1000);
    }

    function sqbgDetail(sqbgId) {
        var action = "detail";
        var url = jsUseCtxPath + "/Sqbg/editPage.do?action=" + action + "&sqbgId=" + sqbgId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (sqbgListGrid) {
                    sqbgListGrid.reload()
                }
            }
        }, 1000);
    }
    function sqbgTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async:false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if(!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (sqbgListGrid) {
                                sqbgListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    function removeSqbg(sqbgId) {
        mini.confirm(sqbgList_name4, sqbgList_name5, function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/Sqbg/deleteSqbg.do",
                    method: 'POST',
                    showMsg:false,
                    data: {id: sqbgId},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="sqbgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>