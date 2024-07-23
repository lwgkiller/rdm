<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准子管理员用户组列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/subManagerGroupList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.subManagerGroupList.name1" />: </span>
                    <input id="systemCategory" class="mini-combobox" name="systemCategoryId" textfield="systemCategoryName" valuefield="systemCategoryId" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.subManagerGroupList.name2" />: </span>
                    <input class="mini-textbox" style="width: 150px" name="groupName" id="subManagerGroupName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.subManagerGroupList.name3" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.subManagerGroupList.name4" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchMyBelong()"><spring:message code="page.subManagerGroupList.name5" /></a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a class="mini-button" style="margin-right: 5px" onclick="addGroup()"><spring:message code="page.subManagerGroupList.name6" /></a>
            <a  class="mini-button btn-red" onclick="removeRow()"><spring:message code="page.subManagerGroupList.name7" /></a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="groupListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/standardManager/core/subManagerGroup/groupList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.subManagerGroupList.name8" />
            </div>
            <div field="groupName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerGroupList.name9" /></div>
            <div field="systemCategoryName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerGroupList.name1" /></div>
            <div field="systemName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerGroupList.name11" /></div>
            <div field="createName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerGroupList.name12" /></div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.subManagerGroupList.name13" /></div>
            <div field="updateName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.subManagerGroupList.name14" /></div>
            <div field="UPDATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.subManagerGroupList.name15" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId="${currentUserId}";
    var groupListGrid = mini.get("groupListGrid");
    var systemCategoryValue="${systemCategoryValue}";
    var grid=mini.get("groupListGrid");

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        s += '<span  title=' + subManagerGroupList_bj + ' onclick="viewForm(\'' + id +'\',\''+record.CREATE_BY_+ '\',\'edit\')">' + subManagerGroupList_bj + '</span>';
        s += '<span  title=' + subManagerGroupList_ryxq + ' onclick="groupUserList(\'' + id +'\',\''+record.CREATE_BY_+ '\')">' + subManagerGroupList_ryxq + '</span>';
        return s;
    }
</script>
</body>
</html>