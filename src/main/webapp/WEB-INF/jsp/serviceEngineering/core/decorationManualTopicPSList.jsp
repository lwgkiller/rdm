<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name" />：</span>
                    <input class="mini-textbox" id="businessNo" name="businessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name1" />：</span>
                    <input class="mini-textbox" id="businessNoTopic" name="businessNoTopic"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name2" />：</span>
                    <input class="mini-textbox" id="chapter" name="chapter"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name3" />：</span>
                    <input class="mini-textbox" id="system" name="system"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name4" />：</span>
                    <input class="mini-textbox" id="topicType" name="topicType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name5" />：</span>
                    <input class="mini-textbox" id="productSerie" name="productSerie"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name6" />：</span>
                    <input class="mini-textbox" id="manualVersion" name="manualVersion"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.decorationManualTopicPSList.name7" />: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.decorationManualTopicPSList.name8" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.decorationManualTopicPSList.name8" />..."/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="decorationManualTopicPS-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicPSList.name9" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.decorationManualTopicPSList.name10" /></a>
                    <f:a alias="decorationManualTopicPS-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.decorationManualTopicPSList.name11" /></f:a>
                    <f:a alias="decorationManualTopicPS-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualTopicPSList.name12" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<%-------------------------------%>
<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" allowHeaderWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/decorationManualTopicPS/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="40"></div>
            <div type="indexcolumn" width="70" headerAlign="center" align="center"><spring:message code="page.decorationManualTopicPSList.name13" /></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.decorationManualTopicPSList.name14" />
            </div>
            <div field="businessNo" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name" /></div>
            <div field="businessNoTopic" width="140" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name1" /></div>
            <div field="chapter" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name2" /></div>
            <div field="system" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name3" /></div>
            <div field="topicType" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name4" /></div>
            <div field="productSerie" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name5" /></div>
            <div field="manualVersion" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name6" /></div>
            <div field="applicant" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name15" /></div>
            <div field="remark" width="160" headerAlign="center" align="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name16" /></div>
            <div field="allTaskUserNames" sortField="allTaskUserNames" width="100" align="center" headerAlign="center"
                 allowSort="false"><spring:message code="page.decorationManualTopicPSList.name17" />
            </div>
            <div field="businessStatus" width="160" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message code="page.decorationManualTopicPSList.name7" /></div>
            <div field="createTime" width="150" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.decorationManualTopicPSList.name18" /></div>
            <%--<div field="currentProcessTask" width="80" align="center" headerAlign="center" renderer="render">当前任务</div>--%>
            <%--<div field="status" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">流程状态</div>--%>
        </div>
    </div>
</div>
<%-------------------------------%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date().format("yyyy-MM-dd");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var decorationManualTopicAdmin = "${decorationManualTopicAdmin}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    //..
    $(function () {
        mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
    });
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + decorationManualTopicPSList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + decorationManualTopicPSList_name + '</span>';
        if ((record.status == 'DRAFTED' || typeof(record.status) == "undefined") && (currentUserId == record.applicantId || currentUserNo == 'admin')) {
            s += '<span  title=' + decorationManualTopicPSList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + decorationManualTopicPSList_name1 + '</span>';
        } else {
            if (record.myTaskId || currentUserNo == 'admin') {
                s += '<span  title=' + decorationManualTopicPSList_name2 + ' onclick="businessTask(\'' + record.myTaskId + '\')">' + decorationManualTopicPSList_name2 + '</span>';
            }
        }
        if (currentUserNo != 'admin' && currentUserNo != decorationManualTopicAdmin) {
            if (record.status == 'DRAFTED' && currentUserId == record.applicantId) {
                s += '<span  title=' + decorationManualTopicPSList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + decorationManualTopicPSList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + decorationManualTopicPSList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + decorationManualTopicPSList_name3 + '</span>';
        }
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/decorationManualTopicPS/start.do";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..编辑行数据流程（后台根据配置的表单进行跳转）
    function businessEdit(businessId, instId) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..
    function businessDetail(businessId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualTopicPS/editPage.do?action=" + action +
            "&businessId=" + businessId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }
    //..跳转到任务的处理界面
    function businessTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (winObj.closed) {
                            clearInterval(loop);
                            if (businessListGrid) {
                                businessListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }
    //..
    function removeBusiness(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(decorationManualTopicPSList_name4);
            return;
        }
        mini.confirm(decorationManualTopicPSList_name5, decorationManualTopicPSList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.businessStatus == 'A' && currentUserId == r.applicantId) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin' || currentUserNo == decorationManualTopicAdmin) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert(decorationManualTopicPSList_name7);
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualTopicPS/deleteBusiness.do",
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
        });
    }
    //..状态渲染
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;
        var arr = mini.decode(nodeSetListWithName);
        return $.formatItemValue(arr, businessStatus);
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>