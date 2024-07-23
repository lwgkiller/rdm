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
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name1"/>：</span>
                    <input class="mini-textbox" id="busunessNo" name="busunessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name2"/>：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name3"/>：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name4"/>：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name5"/>：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name6"/>：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name7"/>：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name8"/>：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.maintenanceManualMctList.name9"/>: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.maintenanceManualMctList.name10" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.maintenanceManualMctList.name10" />..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name11"/>：</span>
                    <input class="mini-textbox" id="demandBusunessNo" name="demandBusunessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.maintenanceManualMctList.name12"/>：</span>
                    <input class="mini-textbox" id="demandListNo" name="demandListNo"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="maintenanceManualMct-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message
                            code="page.maintenanceManualMctList.name13"/></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message
                            code="page.maintenanceManualMctList.name14"/></a>
                    <f:a alias="maintenanceManualMct-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualMctList.name15"/></f:a>
                    <f:a alias="maintenanceManualMct-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualMctList.name16"/></f:a>
                    <f:a alias="maintenanceManualMct-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.maintenanceManualMctList.name17"/></f:a>
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
         url="${ctxPath}/serviceEngineering/core/maintenanceManualMct/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div field="status" width="70" headerAlign="center" align="center" renderer="onStatusRenderer2"><spring:message
                    code="page.maintenanceManualMctList.name18"/></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.maintenanceManualMctList.name19"/>
            </div>
            <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name1"/></div>
            <div field="demandBusunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name11"/></div>
            <div field="demandListNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="demandApplyDetailRender"><spring:message
                    code="page.maintenanceManualMctList.name12"/></div>
            <div field="demandListStatus" width="300" headerAlign="center" align="center" allowSort="true" renderer="render">原始需求异动</div>
            <div field="amHandle" width="200" headerAlign="center" align="center" allowSort="true" renderer="render">异动处理状态</div>
            <div field="instructions" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name20"/></div>
            <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name2"/></div>
            <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name3"/></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name4"/></div>
            <div field="salesArea" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name5"/></div>
            <div field="manualLanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name6"/></div>
            <div field="isCE" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name21"/></div>
            <div field="isSelfDeclaration" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">是否需要自声明</div>
            <div field="CEOnlyNum" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">EC自声明识别码</div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualMctList.name22"/>
            </div>
            <div field="publishTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualMctList.name23"/>
            </div>
            <div field="estimateTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.maintenanceManualMctList.name24"/>
            </div>
            <div field="CREATE_TIME_" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd">创建时间
            </div>
            <%--<div field="manualCode" width="150" headerAlign="center" align="center" allowSort="true" renderer="render">变更或翻译的手册编号</div>--%>
            <%--<div field="manualCodeNew" width="120" headerAlign="center" align="center" allowSort="true" renderer="render">新编参考手册编号</div>--%>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name7"/></div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name8"/></div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message
                    code="page.maintenanceManualMctList.name9"/></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render"><spring:message code="page.maintenanceManualMctList.name25"/>
            </div>
            <div field="Bconfirming" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name26"/></div>
            <div field="UPDATE_TIME_" width="200" headerAlign="center" align="center" renderer="renderUPT" dateFormat="yyyy-MM-dd"><spring:message
                    code="page.maintenanceManualMctList.name27"/></div>
            <div field="remark" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                    code="page.maintenanceManualMctList.name28"/></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/maintenanceManualMct/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
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
        s += '<span  title=' + maintenanceManualMctList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + maintenanceManualMctList_name + '</span>';
        if (record.status == 'DRAFTED' && (currentUserId == record.applyUserId || currentUserNo == 'admin')) {
            s += '<span  title=' + maintenanceManualMctList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + maintenanceManualMctList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + maintenanceManualMctList_name2 + ' onclick="businessTask(\'' + record.taskId + '\')">' + maintenanceManualMctList_name2 + '</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title=' + maintenanceManualMctList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualMctList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + maintenanceManualMctList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + maintenanceManualMctList_name3 + '</span>';
        }
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/maintenanceManualMct/start.do";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/maintenanceManualMct/editPage.do?action=" + action +
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
            mini.alert(maintenanceManualMctList_name4);
            return;
        }
        mini.confirm(maintenanceManualMctList_name5, maintenanceManualMctList_name6, function (action) {
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
                    mini.alert(maintenanceManualMctList_name7);
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/maintenanceManualMct/deleteBusiness.do",
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
            if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "G-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > 0 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #ef1b01" >' + e.value + '</span>';
            } else if (e.record.publishTime != null && e.record.publishTime != "" && e.record.businessStatus != "G-close" &&
                new Date().subtract(e.record.publishTime.parseDate()) > -3 && e.record.status != 'DISCARD_END') {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;color: #eecc53" >' + e.value + '</span>';
            } else {
                var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            }
            return html;
        }
    }
    //..只有流程结束才显示
    function renderUPT(e) {
        if (e.value != null && e.value != "" && e.record.businessStatus == 'G-close') {
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
        html += '<span style="white-space:pre-wrap;color: #0c59a8;" >' + demandListStatus + '</span>';
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
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>