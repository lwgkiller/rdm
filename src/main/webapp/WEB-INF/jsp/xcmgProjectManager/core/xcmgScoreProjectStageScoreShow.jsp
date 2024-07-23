<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <%@include file="/commons/edit.jsp" %>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 2px 2px 2px 2px;
        }

    </style>
</head>
<body>
<div class="mini-panel" style="width: 100%; height: 100%" showfooter="true" bodystyle="padding:0" borderStyle="border:0"
     showheader="false">
    <div id="grid_project_stageScore" class="mini-datagrid"
         allowResize="false" allowSortColumn="false" multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" >
        <div property="columns">
            <div field="stageName"  headerAlign="center" align="center"><spring:message code="page.xcmgScoreProjectStageScoreShow.name" /></div>
            <div field="roleName"  headerAlign="center" align="center"><spring:message code="page.xcmgScoreProjectStageScoreShow.name4" /></div>
            <div field="stageRoleRatio"  headerAlign="center" align="center"><spring:message code="page.xcmgScoreProjectStageScoreShow.name1" /></div>
            <div field="stageScore"  headerAlign="center" align="center"><spring:message code="page.xcmgScoreProjectStageScoreShow.name2" /></div>
            <div field="scoreGetTime"  headerAlign="center" align="center"><spring:message code="page.xcmgScoreProjectStageScoreShow.name3" /></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var gridData=${gridData};
    mini.get("grid_project_stageScore").setData(gridData);
</script>
</body>
</html>
