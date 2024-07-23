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
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name" />：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name1" />：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name2" />：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name3" />：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name4" />：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name5" />：</span>
                    <input class="mini-textbox" id="manualCode" name="manualCode"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.maintenanceManualFeedbackList.name6" />: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualFeedbackList.name7" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.maintenanceManualFeedbackList.name7" />..."
                           data="[{'key' : 'A-editing','value' : '编制中'},
							   {'key' : 'B-confirming','value' : '确认信息'},
							   {'key' : 'C-confirmingProduct','value' : '产品主管确认'},
							   {'key' : 'D-MCT','value' : 'MCT流程进行中'},
							   {'key' : 'E-confirming','value' : '再次确认信息'},
							   {'key' : 'F-close','value' : '结束'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManualFeedback-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualFeedbackList.name8" /></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.maintenanceManualFeedbackList.name9" /></a>
                    <f:a alias="maintenanceManualFeedback-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px"><spring:message code="page.maintenanceManualFeedbackList.name10" /></f:a>
                    <f:a alias="maintenanceManualFeedback-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualFeedbackList.name11" /></f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/maintenanceManualFeedback/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.maintenanceManualFeedbackList.name12" />
            </div>
            <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name13" /></div>
            <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name" /></div>
            <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name1" /></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name2" /></div>
            <div field="salesArea" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name3" /></div>
            <div field="manualLanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name4" /></div>
            <div field="isCE" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name14" /></div>
            <div field="manualCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name5" /></div>
            <div field="abnormalDescription" width="300" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name15" /></div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualFeedbackList.name16" />
            </div>
            <div field="publishTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualFeedbackList.name17" />
            </div>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name18" /></div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name19" /></div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message code="page.maintenanceManualFeedbackList.name6" /></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name20" />
            </div>
            <div field="isHasProblem" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name21" /></div>
            <div field="problemDescription" width="300" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message code="page.maintenanceManualFeedbackList.name22" /></div>
            <%--<div field="instructions" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">下一步</div>--%>
            <%--<div field="remark" width="200" headerAlign="center" align="center" renderer="render">备注</div>--%>
            <%--<div field="currentProcessTask" width="80" align="center" headerAlign="center" renderer="render">当前任务</div>--%>
            <%--<div field="status" width="80" headerAlign="center" align="center" allowSort="false" renderer="render">流程状态</div>--%>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + maintenanceManualFeedbackList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + maintenanceManualFeedbackList_name + '</span>';
        if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
            s += '<span  title=' + maintenanceManualFeedbackList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + maintenanceManualFeedbackList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + maintenanceManualFeedbackList_name2 + ' onclick="businessTask(\'' + record.taskId + '\')">' + maintenanceManualFeedbackList_name2 + '</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title=' + maintenanceManualFeedbackList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualFeedbackList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + maintenanceManualFeedbackList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualFeedbackList_name3 + '</span>';
        }
        return s;
    }

    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/maintenanceManualFeedback/start.do";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualFeedback/editPage.do?action=" + action +
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
            mini.alert(maintenanceManualFeedbackList_name4);
            return;
        }
        mini.confirm(maintenanceManualFeedbackList_name5, maintenanceManualFeedbackList_name6, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var haveSomeRowsWrong = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED' && currentUserId == r.applyUserId) {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert(maintenanceManualFeedbackList_name7);
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualFeedback/deleteBusiness.do",
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

        var arr = [{'key' : 'A-editing','value' : '编制中'},
            {'key' : 'B-confirming','value' : '确认信息'},
            {'key' : 'C-confirmingProduct','value' : '产品主管确认'},
            {'key' : 'D-MCT','value' : 'MCT流程进行中'},
            {'key' : 'E-confirming','value' : '再次确认信息'},
            {'key' : 'F-close','value' : '结束'}
        ];
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