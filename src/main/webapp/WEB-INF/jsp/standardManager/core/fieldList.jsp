<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准专业领域列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/fieldList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.fieldList.name" />: </span>
                    <input class="mini-textbox" style="width: 150px" name="fieldName" id="fieldName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.fieldList.name1" />: </span>
                    <input id="systemCategory" class="mini-combobox" name="systemCategoryId" textfield="systemCategoryName" valuefield="systemCategoryId" emptyText="<spring:message code="page.fieldList.name2" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.fieldList.name2" />..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.fieldList.name3" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.fieldList.name4" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchMyBelong()"><spring:message code="page.fieldList.name5" /></a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a class="mini-button" style="margin-right: 5px" onclick="addField()"><spring:message code="page.fieldList.name6" /></a>
            <a  class="mini-button btn-red" onclick="removeRow()"><spring:message code="page.fieldList.name7" /></a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fieldListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/standardManager/core/standardField/fieldList.do" idField="fieldId" showPager="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action"  cellCls="actionIcons" width="160" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.fieldList.name8" />
            </div>
            <div field="fieldName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name" /></div>
            <div field="systemCategoryName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name1" /></div>
            <div field="ywRespUserName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name11" /></div>
            <div field="respUserName" width="120" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name12" /></div>
            <div field="createName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name13" /></div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.fieldList.name14" /></div>
            <div field="updateName" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.fieldList.name15" /></div>
            <div field="UPDATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.fieldList.name16" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId="${currentUserId}";
    var fieldListGrid = mini.get("fieldListGrid");
    var currentUserNo="${currentUserNo}";

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.fieldId;
        var s = '';
        s += '<span  title=' + fieldList_bj + ' onclick="editField(\'' + id +'\',\''+record.CREATE_BY_+ '\')">' + fieldList_bj + '</span>';
        s += '<span  title=' + fieldList_rygl + ' onclick="fieldUserMg(\'' + id +'\',\''+record.CREATE_BY_+ '\')">' + fieldList_rygl + '</span>';
        return s;
    }
</script>
<redxun:gridScript gridId="fieldListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>