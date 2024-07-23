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
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name"/>：</span>
                    <input class="mini-textbox" id="busunessNo" name="busunessNo"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name1"/>：</span>
                    <input class="mini-combobox" id="instructions" name="instructions" valueField="key" textField="value"
                           data="[{'key':'新增','value':'新增'},{'key':'变更','value':'变更'},{'key':'翻译','value':'翻译'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name2"/>：</span>
                    <input class="mini-combobox" id="collectType" name="collectType" valueField="key" textField="value"
                           data="[{'key':'技术规格资料','value':'技术规格资料'},{'key':'力矩及工具标准值资料','value':'力矩及工具标准值资料'},
                           {'key':'故障代码','value':'故障代码'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name3"/>：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name4"/>：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name5"/>：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name6"/>：</span>
                    <input class="mini-textbox" id="salesArea" name="salesArea"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name7"/>：</span>
                    <input class="mini-textbox" id="manualLanguage" name="manualLanguage"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name8"/>：</span>
                    <input class="mini-textbox" id="applyUser" name="applyUser"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name9"/>：</span>
                    <input class="mini-textbox" id="applyDep" name="applyDep"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                        code="page.decorationManualCollectList.name10"/>: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.decorationManualCollectList.name11" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.decorationManualCollectList.name11" />..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.decorationManualCollectList.name12"/>：</span>
                    <input class="mini-textbox" id="demandBusunessNo" name="demandBusunessNo"/>
                </li>
                <li style="margin-left: 10px">
                    <f:a alias="decorationManualCollect-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px"><spring:message
                            code="page.decorationManualCollectList.name13"/></f:a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message
                            code="page.decorationManualCollectList.name14"/></a>
                    <f:a alias="decorationManualCollect-addBusiness" onclick="addBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualCollectList.name15"/></f:a>
                    <f:a alias="decorationManualCollect-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualCollectList.name16"/></f:a>
                    <f:a alias="decorationManualCollect-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-right: 5px"><spring:message code="page.decorationManualCollectList.name17"/></f:a>
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
         allowCellWrap="true" showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100,500,1000,2000,5000]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/decorationManualCollect/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="150" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.decorationManualCollectList.name18"/>
            </div>
            <div field="busunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name"/></div>
            <div field="demandBusunessNo" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name12"/></div>
            <div field="instructions" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name1"/></div>
            <div field="collectType" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name2"/></div>
            <div field="salesModel" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name3"/></div>
            <div field="designModel" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name4"/></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name5"/></div>
            <div field="manualLanguage" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name7"/></div>
            <div field="salesArea" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name6"/></div>
            <div field="applyTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.decorationManualCollectList.name19"/>
            </div>
            <div field="publishTime" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"
                 dateFormat="yyyy-MM-dd"><spring:message code="page.decorationManualCollectList.name20"/>
            </div>
            <div field="manualCode" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name21"/></div>
            <div field="collector" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name22"/></div>
            <div field="collector2" width="150" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name23"/></div>
            <div field="applyUser" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name8"/></div>
            <div field="applyDep" width="100" headerAlign="center" align="center" allowSort="true" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name9"/></div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer"><spring:message
                    code="page.decorationManualCollectList.name10"/></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="100" align="center" headerAlign="center"
                 allowSort="false" renderer="render"><spring:message code="page.decorationManualCollectList.name24"/>
            </div>
            <div field="UPDATE_TIME_" width="200" headerAlign="center" align="center" renderer="renderUPT" dateFormat="yyyy-MM-dd"><spring:message
                    code="page.decorationManualCollectList.name25"/></div>
            <div field="remark" width="200" headerAlign="center" align="center" renderer="render"><spring:message
                    code="page.decorationManualCollectList.name26"/></div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/decorationManualCollect/exportList.do" method="post" target="excelIFrame">
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
    var decorationManualAdmin = "${decorationManualAdmin}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    var expIds = '${expIds}';
    //..
    $(function () {
        mini.get("businessStatus").setData(mini.decode(nodeSetListWithName));
        if (expIds) {
            var url = "${ctxPath}/serviceEngineering/core/decorationManualCollect/dataListQuery.do?expIds=" + expIds;
            businessListGrid.setUrl(url);
            businessListGrid.load();
        }
    });
    //..
    function clearForm(){
        debugger;
        if(typeof (group_userInfo) != "undefined" && group_userInfo){
            group_userInfo = {
                groupId:0,
                userInfoList:[],
                isOsUserManager:false
            };
        }
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            var el=$(this);
            el.val("");
        });
        if (expIds) {
            var url = "${ctxPath}/serviceEngineering/core/decorationManualCollect/dataListQuery.do?expIds=" + expIds;
            businessListGrid.setUrl(url);
            businessListGrid.load();
        }else{
            businessListGrid.setUrl(gdBaseUrl);
            businessListGrid.load();
        }
    }
    //..行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var businessId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + decorationManualCollectList_name + ' onclick="businessDetail(\'' + businessId + '\',\'' + record.status + '\')">' + decorationManualCollectList_name + '</span>';
        if (record.status == 'DRAFTED' && currentUserId == record.applyUserId) {
            s += '<span  title=' + decorationManualCollectList_name1 + ' onclick="businessEdit(\'' + businessId + '\',\'' + instId + '\')">' + decorationManualCollectList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + decorationManualCollectList_name2 + ' onclick="businessTask(\'' + record.taskId + '\')">' + decorationManualCollectList_name2 + '</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            if (status == 'DRAFTED' && currentUserId == record.applyUserId) {
                s += '<span  title=' + decorationManualCollectList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + decorationManualCollectList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + decorationManualCollectList_name3 + ' onclick="removeBusiness(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + decorationManualCollectList_name3 + '</span>';
        }
        return s;
    }
    //..（后台根据配置的表单进行跳转）
    function addBusiness() {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/decorationManualCollect/start.do";
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
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/editPage.do?action=" + action +
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
            mini.alert(decorationManualCollectList_name4);
            return;
        }
        mini.confirm(decorationManualCollectList_name5, decorationManualCollectList_name6, function (action) {
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
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin' || currentUserNo == decorationManualAdmin) {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        haveSomeRowsWrong = true;
                        break;
                    }
                }
                if (haveSomeRowsWrong) {
                    mini.alert(decorationManualCollectList_name7);
                    return;
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualCollect/deleteBusiness.do",
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
    //..只有流程结束才显示
    function renderUPT(e) {
        if (e.value != null && e.value != "" && e.record.businessStatus == 'Z') {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
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