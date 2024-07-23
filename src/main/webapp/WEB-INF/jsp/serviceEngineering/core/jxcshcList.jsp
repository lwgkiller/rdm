<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/jxcshcList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="padding-left: 10px">
                    <span class="text" style="width:auto"><spring:message code="page.jxcshcList.name" />：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxcshcList.name1" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxcshcList.name1" />..."
                           data="[{key:'cgb',value:'常规版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="padding-left: 10px">
                    <span class="text" style="width:auto"><spring:message code="page.jxcshcList.name2" />：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.jxcshcList.name1" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jxcshcList.name1" />..."
                           data="[{'key': 'DRAFTED', 'value': '草稿'},
                                    {'key': 'RUNNING', 'value': '运行中'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束'},
                                    {'key': 'DISCARD_END', 'value': '作废'}]"
                    />
                </li>
                <li style="padding-left: 10px">
                    <span class="text" style="width:auto"><spring:message code="page.jxcshcList.name3" />：</span>
                    <input id="groupName" name="groupName"  class="mini-textbox" style="width:98%;" />
                </li>
                <br/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.jxcshcList.name4" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.jxcshcList.name5" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addJxcshc()"><spring:message code="page.jxcshcList.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeJxcshc()"><spring:message code="page.jxcshcList.name7" /></a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="jxcshcGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true"
         showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons"
         url="${ctxPath}/serviceEngineering/core/jxcshc/jxcshcListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="35" headerAlign="center" align="center"><spring:message code="page.jxcshcList.name8" /></div>
            <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jxcshcList.name9" /></div>
            <div field="groupName" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxcshcList.name3" /></div>
            <div field="creator" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jxcshcList.name10" /></div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer"><spring:message code="page.jxcshcList.name" /></div>
            <div field="currentProcessUser" align="center" headerAlign="center" allowSort="false"><spring:message code="page.jxcshcList.name11" /></div>
            <div field="currentProcessTask" align="center" headerAlign="center"><spring:message code="page.jxcshcList.name12" /></div>
            <div field="taskStatus"  headerAlign="center" align="center" allowSort="true" renderer="taskStatusRenderer"><spring:message code="page.jxcshcList.name2" /></div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true"><spring:message code="page.jxcshcList.name13" /></div>

        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jxcshcGrid = mini.get("jxcshcGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var mainGroupName = "${mainGroupName}";
    if (mainGroupName == '质量保证部' || mainGroupName == '测试研究所') {
        mini.get("groupName").setEnabled(false);
        mini.get("groupName").setValue(mainGroupName);
    }
    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var jxcshcId = record.id;
        var instId=record.instId;
        var s = '';
        s += '<span  title=' + jxcshcList_name + ' onclick="jxcshcDetail(\'' + jxcshcId + '\',\'' + record.taskStatus + '\')">' + jxcshcList_name + '</span>';
        if (record.taskStatus == 'DRAFTED' && record.CREATE_BY_ == currentUserId) {
            s += '<span  title=' + jxcshcList_name1 + ' onclick="jxcshcEdit(\'' + jxcshcId + '\',\'' + instId + '\')">' + jxcshcList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
                s += '<span  title=' + jxcshcList_name2 + ' onclick="jxcshcTask(\'' + record.taskId + '\')">' + jxcshcList_name2 + '</span>';
            }
        }
        if (currentUserNo != 'admin') {
            if (record.taskStatus == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title=' + jxcshcList_name3 + ' onclick="removeJxcshc(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxcshcList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + jxcshcList_name3 + ' onclick="removeJxcshc(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jxcshcList_name3 + '</span>';
        }
        return s;
    }


</script>
<redxun:gridScript gridId="jxcshcGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>