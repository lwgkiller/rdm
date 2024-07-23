<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>原理图下载申请列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyList.name" />: </span>
                    <input class="mini-textbox" id="applyNumber" name="applyNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyList.name1" />: </span>
                    <input class="mini-textbox" id="creatorName" name="creatorName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyList.name2" />:</span>
                    <input class="mini-textbox" id="applyUserDeptName" name="applyUserDeptName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.schematicDownloadApplyList.name3" />: </span>
                    <input id="status" name="status" class="mini-combobox" style="width:120px;" multiSelect="false"
                           textField="value" valueField="key" emptyText="<spring:message code="page.schematicDownloadApplyList.name4" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.schematicDownloadApplyList.name4" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.schematicDownloadApplyList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.schematicDownloadApplyList.name6" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addSchematicDownload()"><spring:message code="page.schematicDownloadApplyList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeSchematicDownload()"><spring:message code="page.schematicDownloadApplyList.name8" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="schematicDownloadList" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/schematicDownloadApply/applyList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="75" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.schematicDownloadApplyList.name9" />
            </div>
            <div field="applyNumber" width="110" headerAlign="center" align="center" allowSort="false"><spring:message code="page.schematicDownloadApplyList.name" /></div>
            <div field="applyUserDeptName" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.schematicDownloadApplyList.name2" /></div>
            <div field="creatorName" width="50" headerAlign="center" align="center" allowSort="false"><spring:message code="page.schematicDownloadApplyList.name1" /></div>
            <div field="taskName" headerAlign='center' align='center' width="120"><spring:message code="page.schematicDownloadApplyList.name10" /></div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="80"><spring:message code="page.schematicDownloadApplyList.name11" /></div>
            <div field="status" width="40" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer"><spring:message code="page.schematicDownloadApplyList.name12" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd" width="65" align="center" headerAlign="center" allowSort="true"><spring:message code="page.schematicDownloadApplyList.name13" /></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var schematicDownloadList = mini.get("schematicDownloadList");
    //..
    $(function () {
        searchFrm();
    });
    //..新增流程
    function addSchematicDownload() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/schematicDownloadApply/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (schematicDownloadList) {
                    schematicDownloadList.reload()
                }
            }
        }, 1000);
    }
    //..
    function schematicDownloadEdit(applyId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (schematicDownloadList) {
                    schematicDownloadList.reload()
                }
            }
        }, 1000);
    }
    //..
    function schematicDownloadDetail(applyId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/schematicDownloadApply/applyEditPage.do?" +
            "action=detail&applyId=" + applyId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (schematicDownloadList) {
                    schematicDownloadList.reload()
                }
            }
        }, 1000);
    }
    //..
    function schematicDownloadTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async: false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (schematicDownloadList) {
                                schematicDownloadList.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..
    function removeSchematicDownload(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = schematicDownloadList.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(schematicDownloadApplyList_name);
            return;
        }
        mini.confirm(schematicDownloadApplyList_name1, schematicDownloadApplyList_name2, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    console.log("控制台输出");
                    console.log(r.status);
                    console.log(r.CREATE_BY_);
                    console.log(currentUserId);
                    console.log(currentUserNo);
                    if ((r.status == 'DRAFTED' && r.CREATE_BY_ == currentUserId) || currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    }
                    else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    mini.alert(schematicDownloadApplyList_name3);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/serviceEngineering/core/schematicDownloadApply/deleteApply.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(','), instIds: instIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title=' + schematicDownloadApplyList_name4 + ' onclick="schematicDownloadDetail(\'' + applyId + '\',\'' + status + '\')">' + schematicDownloadApplyList_name4 + '</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + schematicDownloadApplyList_name5 + ' onclick="schematicDownloadEdit(\'' + applyId + '\',\'' + instId + '\')">' + schematicDownloadApplyList_name5 + '</span>';
        }
        if (record.status == 'RUNNING') {
            if (record.myTaskId) {
                s += '<span  title=' + schematicDownloadApplyList_name6 + ' onclick="schematicDownloadTask(\'' + record.myTaskId + '\')">' + schematicDownloadApplyList_name6 + '</span>';
            }
        }
        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title=' + schematicDownloadApplyList_name7 + ' onclick="removeSchematicDownload(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + schematicDownloadApplyList_name7 + '</span>';
        }
        return s;
    }
    //..
    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
</script>
<redxun:gridScript gridId="schematicDownloadList" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

