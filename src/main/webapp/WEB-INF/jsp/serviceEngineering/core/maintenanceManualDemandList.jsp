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
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name"/>：</span>
                    <input class="mini-textbox" id="busunessNo" name="busunessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name1"/>：</span>
                    <input class="mini-textbox" id="demandListNo" name="demandListNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name2"/>：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name3"/>：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name4"/>：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name5"/>：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name6"/>：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name7"/>：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualDemandList.name8"/>：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.maintenanceManualDemandList.name9"/>: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualDemandList.name10" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.maintenanceManualDemandList.name10" />..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">异动处理: </span>
                    <input id="amHandle" name="amHandle" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key"
                           required="false" allowInput="false" showNullItem="true"
                           data="[{'key':'已处理','value':'已处理'},{'key':'未处理','value':'未处理'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManualDemand-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message
                            code="page.maintenanceManualDemandList.name11"/></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message
                            code="page.maintenanceManualDemandList.name12"/></a>
                    <f:a alias="maintenanceManualDemand-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualDemandList.name13"/></f:a>
                    <f:a alias="maintenanceManualDemand-copyBusiness" onclick="copyBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualDemandList.name14"/></f:a>
                    <f:a alias="maintenanceManualDemand-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualDemandList.name15"/></f:a>
                    <f:a alias="maintenanceManualDemand-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualDemandList.name16"/></f:a>
                    <f:a alias="maintenanceManualDemand-amHandle" onclick="amHandle()" showNoRight="false"
                         style="margin-right: 5px">异动处理完成</f:a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" id="collectBtn" onclick="myCollect()"><spring:message
                            code="page.maintenanceManualDemandList.name17"/></a>
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
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100,500,1000,2000,5000]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" sortField="applyTime" sortOrder="desc"
         url="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer2"><spring:message
                    code="page.maintenanceManualDemandList.name18"/></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.maintenanceManualDemandList.name19"/>
            </div>
            <div field="busunessNo" width="150" headerAlign="lwft" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name"/></div>
            <div field="demandListNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="demandApplyDetailRender">
                <spring:message code="page.maintenanceManualDemandList.name1"/></div>
            <div field="demandListStatus" width="300" headerAlign="center" align="center" allowSort="true" renderer="render">原始需求异动</div>
            <div field="amHandle" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">异动处理状态</div>
            <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name6"/></div>
            <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name7"/></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name8"/></div>
            <div field="manualLanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name20"/></div>
            <div field="isCE" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name21"/></div>
            <div field="isSelfDeclaration" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">是否需要自声明</div>
            <div field="CEOnlyNum" width="100" headerAlign="center" align="center" allowSort="true" renderer="CEOnlyNumRender">EC自声明识别码</div>
            <div field="CELanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">EC自声明语言</div>
            <div field="salesArea" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name2"/></div>
            <div field="quantity" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name22"/></div>
            <div field="configurationDescription" width="300" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name23"/></div>
            <div field="manualCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name24"/></div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualDemandList.name25"/>
            </div>
            <div field="publishTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualDemandList.name26"/>
            </div>
            <div field="CREATE_TIME_" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd">创建时间
            </div>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name4"/></div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name5"/></div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message
                    code="page.maintenanceManualDemandList.name9"/></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render"><spring:message code="page.maintenanceManualDemandList.name27"/>
            </div>
            <div field="Bconfirming" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name28"/></div>
            <div field="UPDATE_TIME_" width="200" headerAlign="center" align="center" renderer="renderUPT" dateFormat="yyyy-MM-dd"><spring:message
                    code="page.maintenanceManualDemandList.name29"/></div>
            <div field="remark" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                    code="page.maintenanceManualDemandList.name30"/></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<%--收藏夹--%>
<div id="collectWindow" title="<spring:message code="page.maintenanceManualDemandList.name31" />" class="mini-window"
     style="width:1050px;height:650px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div id="collectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" autoload="true"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/serviceEngineering/core/maintenanceManualDemand/dataListQuery.do?collectorId=${currentUserId}">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onCollectActionRenderer"
                     cellStyle="padding:0;"><spring:message code="page.maintenanceManualDemandList.name19"/>
                </div>
                <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name"/></div>
                <div field="demandListNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name1"/></div>
                <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name6"/></div>
                <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name7"/></div>
                <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name8"/></div>
                <div field="manualLanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name20"/></div>
                <div field="isCE" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name21"/></div>
                <div field="salesArea" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name2"/></div>
                <div field="quantity" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name22"/></div>
                <div field="configurationDescription" width="300" headerAlign="center" align="center" allowSort="true" renderer="render">
                    <spring:message code="page.maintenanceManualDemandList.name23"/></div>
                <div field="manualCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name24"/></div>
                <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                     dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualDemandList.name25"/>
                </div>
                <div field="publishTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                     dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualDemandList.name26"/>
                </div>
                <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name4"/></div>
                <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name5"/></div>
                <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message
                        code="page.maintenanceManualDemandList.name9"/></div>
                <div field="status" width="100" headerAlign="center" align="center" renderer="onStatusRenderer2"><spring:message
                        code="page.maintenanceManualDemandList.name18"/></div>
                <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                     allowSort="false" renderer="render"><spring:message code="page.maintenanceManualDemandList.name27"/>
                </div>
                <div field="Bconfirming" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name28"/></div>
                <div field="UPDATE_TIME_" width="200" headerAlign="center" align="center" renderer="renderUPT" dateFormat="yyyy-MM-dd"><spring:message
                        code="page.maintenanceManualDemandList.name29"/></div>
                <div field="remark" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                        code="page.maintenanceManualDemandList.name30"/></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.maintenanceManualDemandList.name32" />"
                           onclick="myCollectHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var maintenanceManualAdmin = "${maintenanceManualAdmin}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var collectWindow = mini.get("collectWindow");
    var collectListGrid = mini.get("collectListGrid");
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
        s += '<span  title=' + maintenanceManualDemandList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + maintenanceManualDemandList_name + '</span>';
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + maintenanceManualDemandList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + maintenanceManualDemandList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1)
                || currentUserNo == 'admin' || currentUserNo == maintenanceManualAdmin) {
                s += '<span  title=' + maintenanceManualDemandList_name2 + ' onclick="businessTask(\'' + record.taskId + '\')">' + maintenanceManualDemandList_name2 + '</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title=' + maintenanceManualDemandList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualDemandList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + maintenanceManualDemandList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualDemandList_name3 + '</span>';
        }
        s += '<span title=' + maintenanceManualDemandList_name4 + ' onclick="collectBusiness(\'' + currentUserId + '\')">' + maintenanceManualDemandList_name4 + '</span>';
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/maintenanceManualDemand/start.do";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/editPage.do?action=" + action +
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
            mini.alert(maintenanceManualDemandList_name5);
            return;
        }
        mini.confirm(maintenanceManualDemandList_name6, maintenanceManualDemandList_name7, function (action) {
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
                    } else if (currentUserNo == 'admin' || currentUserNo == maintenanceManualAdmin) {
                        rowIds.push(r.id);
                        instIds.push(r.INST_ID_);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert(maintenanceManualDemandList_name8);
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/deleteBusiness.do",
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
    //..
    function amHandle(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert(maintenanceManualDemandList_name5);
            return;
        }
        mini.confirm("确定已处理完成?", maintenanceManualDemandList_name7, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    rowIds.push(rows[i].id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/amHandle.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
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
    //..
    function copyBusiness() {
        var row = businessListGrid.getSelected();
        if (row == null) {
            mini.alert(maintenanceManualDemandList_name9);
            return;
        }
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/copyBusiness.do",
            method: 'POST',
            showMsg: false,
            data: {businessId: row.id},
            success: function (data) {
                if (data) {
                    searchFrm();
                }
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
    //..流程状态渲染
    function onStatusRenderer2(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常终止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //..所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "F-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > 0 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            }
            else if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "F-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > -3 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #eecc53" >' + e.value + '</span>';
            }
            else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }
    //..只有流程结束才显示
    function renderUPT(e) {
        if (e.value != null && e.value != "" && e.record.businessStatus == 'F-close') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function renderContent(e) {
        var record = e.record;
        var html = "<div style='line-height: 20px;height:100px;overflow: auto' >";
        var demandListStatus = record.demandListStatus;
        if (demandListStatus == null) {
            demandListStatus = "";
        }
        html += '<span style="white-space:pre-wrap;color: #0c59a8;">' + demandListStatus + '</span>';
        html += '</div>'
        return html;
    }
    //..
    function demandApplyDetailRender(e) {
        var record = e.record;
        var demandListNo = record.demandListNo;
        if (demandListNo) {
            return '<span style="text-decoration:underline;cursor: pointer;color:#409EFF" onclick="demandDetail(\'' + demandListNo + '\')">' + demandListNo + '</span>';
        }
    }
    //..
    function demandDetail(demandNo) {
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/demandEditPage.do?demandNo=" + demandNo;
        var winObj = window.open(url);
    }
    //..
    function CEOnlyNumRender(e) {
        var record = e.record;
        var CEOnlyNum = record.CEOnlyNum;
        if (CEOnlyNum) {
            return '<span style="text-decoration:underline;cursor: pointer;color:#409EFF" onclick="ceinfoDetail(\'' + CEOnlyNum + '\')">' + CEOnlyNum + '</span>';
        }
    }
    //..
    function ceinfoDetail(CEOnlyNum) {
        var action = "detail";
        mini.open({
            title: "明细",
            url: jsUseCtxPath + "/wwrz/core/CE/edit.do?action=" + action + "&onlyNum=" + CEOnlyNum,
            width: 1050,
            height: 550,
            allowResize: true
        });
    }
    //..
    function exportBusiness() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    //..收藏夹对话框按钮栏手动渲染
    collectListGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });
    //..收藏夹行操作
    function onCollectActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + maintenanceManualDemandList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + maintenanceManualDemandList_name + '</span>';
        if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
            s += '<span  title=' + maintenanceManualDemandList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + maintenanceManualDemandList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + maintenanceManualDemandList_name2 + ' onclick="businessTask(\'' + record.taskId + '\')">' + maintenanceManualDemandList_name2 + '</span>';
            }
        }
        s += '<span  title=' + maintenanceManualDemandList_name10 + ' onclick="removeCollect(\'' + businessId + '\',\'' + currentUserId + '\')">' + maintenanceManualDemandList_name10 + '</span>';
        return s;
    }
    //..收藏
    function collectBusiness(userId) {
        var rows = businessListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert(maintenanceManualDemandList_name5);
            return;
        }
        var ids = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.id);
        }
        _SubmitJson({
            url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/saveCollect.do?userId=" + userId,
            method: 'POST',
            showMsg: false,
            data: {ids: ids.join(',')},
            success: function (data) {
                if (data) {
                    mini.alert(data.message);
                    collectListGrid.reload();
                }
            }
        });
    }
    //..移除收藏
    function removeCollect(businessId, userId) {
        mini.confirm(maintenanceManualDemandList_name6, maintenanceManualDemandList_name7, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                rowIds.push(businessId);
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualDemand/deleteCollect.do?userId=" + userId,
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            collectListGrid.reload();
                        }
                    }
                });
            }
        });
    }
    //..我的收藏夹
    function myCollect() {
        collectWindow.show();
    }
    //..关闭收藏夹
    function myCollectHide() {
        collectWindow.hide();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>