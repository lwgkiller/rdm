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
    <c:choose>
        <c:when test="${scene=='roleRatioRule'}">
            <span style="color: red">注：如果成员未参与项目工作，角色系数允许为0！</span>
        </c:when>
    </c:choose>
    <div id="grid_project_ruleInfo" class="mini-datagrid"
         allowResize="false" allowSortColumn="false" multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false" >
        <c:choose>
            <c:when test="${scene=='buildScoreRule'}">
                <div property="columns">
                    <div field="levelName"  headerAlign="center" align="center">项目级别
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="minScore"  headerAlign="center" align="center">最低分
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="maxScore"  headerAlign="center" align="center">最高分
                        <input property="editor" class="mini-textbox" /></div>

                </div>
            </c:when>
            <c:when test="${scene=='knotScoreRule'}">
                <div property="columns">
                    <div field="ratingName"  headerAlign="center" align="center">评价等级
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="knotScoreRange"  headerAlign="center" align="center">评价得分
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="ratioRange"  headerAlign="center" align="center">等级系数
                        <input property="editor" class="mini-textbox" /></div>

                </div>
            </c:when>
            <c:when test="${scene=='roleRatioRule'}">
                <div property="columns">
                    <div field="roleName"  headerAlign="center" align="center">角色名称
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="minRatio"  headerAlign="center" align="center">系数最低分
                        <input property="editor" class="mini-textbox" /></div>
                    <div field="maxRatio"  headerAlign="center" align="center">系数最高分
                        <input property="editor" class="mini-textbox" /></div>

                </div>
            </c:when>
        </c:choose>

    </div>

</div>
<script type="text/javascript">
    mini.parse();
    var gridData=${gridData};
    mini.get("grid_project_ruleInfo").setData(gridData);
</script>
</body>
</html>
