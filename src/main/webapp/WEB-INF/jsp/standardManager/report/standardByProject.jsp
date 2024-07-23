<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2022-8-16
  Time: 18:48
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
    <title>标准按项目生成统计报表</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/standardManager/standardByProject.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul style="margin-left: 15px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name" />: </span>
                    <input class="mini-textbox" id="standardNumber" name="standardNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name1" />: </span>
                    <input class="mini-textbox" id="standardName" name="standardName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name2" />: </span>
                    <input class="mini-textbox" id="ProjectName" name="ProjectName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name3" />: </span>
                    <input class="mini-textbox" id="ProjectNumber" name="ProjectNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name4" />: </span>
                    <input class="mini-textbox" id="depName" name="depName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.standardByProject.name5" />: </span>
                    <input class="mini-textbox" id="respMan" name="respMan"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button"  style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.standardByProject.name6" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearGroupByProjectSearch()"><spring:message code="page.standardByProject.name7" /></a>
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportProjectReport()"><spring:message code="page.standardByProject.name8" /></a>
                </li>
            </ul>

        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="standardByProjectGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/standardManager/report/standard/groupByProjectQuery.do" idField="id"
         multiSelect="false" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexColumn" headerAlign="center" ><spring:message code="page.standardByProject.name9" /></div>
            <div field="outNumber"  width="100" headerAlign="center" align="center" ><spring:message code="page.standardByProject.name" /></div>
            <div field="outName" width="160" headerAlign="center" align="center" ><spring:message code="page.standardByProject.name1" /></div>
            <div field="typeName" width="80" headerAlign="center" align="center" ><spring:message code="page.standardByProject.name12" /></div>
            <div field="projectName" width="160" headerAlign="center" align="center" ><spring:message code="page.standardByProject.name2" /></div>
            <div field="number"   width="60" headerAlign="center" align="center"><spring:message code="page.standardByProject.name3" /></div>
            <div field="deptname"   width="60" headerAlign="center" align="center"><spring:message code="page.standardByProject.name10" /></div>
            <div field="respMan"  width="50" headerAlign="center" align="center"  ><spring:message code="page.standardByProject.name11" /></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/standardManager/report/standard/exportGroupByProject.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var grid=mini.get("standardSystemGrid");

</script>
<redxun:gridScript gridId="standardByProjectGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
