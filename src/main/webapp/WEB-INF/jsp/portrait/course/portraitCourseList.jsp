<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>培训课程列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/course/portraitCourseList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/assetsManager/core/assetReceive/export.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
<%--            <a id="addButton" class="mini-button" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addRow()">新增</a>--%>
<%--            <a id="delButton" class="mini-button" style="margin-left: 10px"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>--%>
            <a id="asyncButton" class="mini-button" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/xhtml.png"
               onclick="asyncData()">数据同步</a>
            <div style="display: inline-block" class="separator"></div>

        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="courseListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/course/courseList.do" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">所属部门</div>
            <div field="userName" width="80" headerAlign="center" align="center" allowSort="true">姓名</div>
            <div field="courseDate" width="80" headerAlign="center" align="center" allowSort="true">课程日期</div>
            <div field="courseName" width="120" headerAlign="center" align="left" allowSort="true">课程名称</div>
            <div field="studentNum" width="80" headerAlign="center" align="center" allowSort="true" >参加人数</div>
            <div field="courseHour" width="80" headerAlign="center" align="center" allowSort="true" >课时</div>
            <div field="score" width="80" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">同步日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var courseListGrid = mini.get("courseListGrid");
    var permission = ${permission};
    if(!permission){
        mini.get("addButton").setEnabled(false);
        mini.get("delButton").setEnabled(false);
        mini.get("asyncButton").setEnabled(false);
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
        if(permission){
            s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="courseListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
