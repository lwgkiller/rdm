<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/wrongPartsData.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/serviceEngineering/wrongPartsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name"/>：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name1"/>：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name2"/>：</span>
                    <input class="mini-textbox" id="wrongPartName" name="wrongPartName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name3"/>：</span>
                    <input id="typeOfWrongPart" name="typeOfWrongPart" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.wrongPartsList.name4" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.wrongPartsList.name4" />..."
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name5"/>：</span>
                    <input class="mini-textbox" id="responsibleDepartmentName" name="responsibleDepartmentName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name6"/>：</span>
                    <input class="mini-textbox" id="machineCode" name="machineCode"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">业务状态: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.wrongPartsList.name8"/>：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.wrongPartsList.name4" />..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="<spring:message code="page.wrongPartsList.name4" />..."
                           data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                    {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                    />
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">是否延期(仅服务所业务): </span>
                    <input id="isPostpone" name="isPostpone" class="mini-combobox" style="width:98%"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true"
                           nullItemText="请选择..." data="[{'key' : 'true','value' : '是'},{'key' : 'false','value' : '否'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message
                            code="page.wrongPartsList.name9"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message
                            code="page.wrongPartsList.name10"/></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCjzg()"><spring:message
                            code="page.wrongPartsList.name11"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeCjzg()"><spring:message
                            code="page.wrongPartsList.name12"/></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportReview()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="wrongPartsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/wrongParts/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.wrongPartsList.name13"/></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.wrongPartsList.name14"/>
            </div>
            <div field="CREATE_TIME_" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name15"/></div>
            <div field="isPostpone" width="80" headerAlign="center" align="center" renderer="isPostponeRenderer">是否延期</div>
            <div field="wrongPartName" width="150" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name2"/></div>
            <div field="typeOfWrongPart" width="150" headerAlign="center" align="center" renderer="cjlxRenderer">
                <spring:message code="page.wrongPartsList.name3"/></div>
            <div field="isHistory" width="150" headerAlign="center" align="center" renderer="isDelayRenderer">是否历史遗留问题</div>
            <div field="currentNodeBeginTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">
                到达当前节点时间</div>
            <div field="promiseTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">
                预计完成时间</div>
            <div field="actualTime" width="120" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">
                实际完成时间</div>
            <div field="salesModel" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name"/></div>
            <div field="designModel" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name1"/></div>
            <div field="machineCode" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name6"/></div>
            <div field="agent" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name16"/></div>
            <div field="respDepName" width="150" headerAlign="center" align="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name5"/></div>
            <div field="principalUserName" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">责任人</div>
            <div field="XGSSRespUserName" width="120" headerAlign="center" align="center" renderer="isDelayRenderer">XGSS整改人</div>
            <div field="businessStatus" width="150" headerAlign="center" align="center" renderer="onStatusRenderer">业务状态</div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="80" align="center" headerAlign="center" renderer="isDelayRenderer">
                <spring:message code="page.wrongPartsList.name18"/></div>
            <div field="taskStatus" headerAlign="center" align="center" renderer="taskStatusRenderer">
                <spring:message code="page.wrongPartsList.name8"/></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var wrongPartsListGrid = mini.get("wrongPartsListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var nodeSetListWithName = '${nodeSetListWithName}';
    //..
    function onActionRenderer(e) {
        var record = e.record;
        var cjzgId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + wrongPartsList_name9 + ' onclick="cjzgDetail(\'' + cjzgId + '\',\'' + record.taskStatus + '\')">' + wrongPartsList_name9 + '</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title=' + wrongPartsList_name + ' onclick="cjzgEdit(\'' + cjzgId + '\',\'' + instId + '\')">' + wrongPartsList_name + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) ||
                currentUserId == '1') {
                s += '<span  title=' + wrongPartsList_name1 + ' onclick="cjzgTask(\'' + record.taskId + '\')">' + wrongPartsList_name1 + '</span>';
            }
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title=' + wrongPartsList_name2 + ' onclick="removeCjzg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + wrongPartsList_name2 + '</span>';
            }
        } else {
            s += '<span  title=' + wrongPartsList_name2 + ' onclick="removeCjzg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + wrongPartsList_name2 + '</span>';
        }
        return s;
    }
    //..
    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;
        var arr = mini.decode(nodeSetListWithName);
        return $.formatItemValue(arr, businessStatus);
    }
</script>
<redxun:gridScript gridId="wrongPartsListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>