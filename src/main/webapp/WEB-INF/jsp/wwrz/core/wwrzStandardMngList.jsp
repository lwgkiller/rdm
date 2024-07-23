<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>认证标准列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzStandardMngList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto">标准编号: </span><input
                        class="mini-textbox" id="standardNumber" name="standardNumber"></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">标准名称: </span><input
                        class="mini-textbox" id="standardName" name="standardName"></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
<%--                    <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">按条件导出</a>--%>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/wwrz/core/standardMng/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <div style="display: inline-block" class="separator"></div>
            <a id="addButton" class="mini-button" style="margin-right: 5px" plain="true" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="addItems()">新增</a>
            <a id="delButton" class="mini-button"  img="${ctxPath}/scripts/mini/miniui/res/images/cancel.png" onclick="removeRow()">删除</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/wwrz/core/standardMng/listData.do?standardType=${standardType}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30"></div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="operateRenderer" hideable="true"
                 cellStyle="padding:0;">操作
            </div>
            <div field="systemName" name="systemName" sortField="systemName" width="100" headerAlign="center" align="center"
                 allowSort="false">标准体系
            </div>
            <div field="categoryName" sortField="categoryName" width="80" headerAlign="center" align="center"
                 allowSort="false" hideable="true">标准类别
            </div>
            <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center"
                 align="center" allowSort="false" hideable="true">标准编号
            </div>
            <div field="standardName" sortField="standardName" width="190" headerAlign="center" align="left"
                 allowSort="false" hideable="true">标准名称
            </div>
            <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                 allowSort="false">归口部门
            </div>
            <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                 align="center" hideable="true"
                 allowSort="false" renderer="statusRenderer">状态
            </div>
            <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                 width="100" headerAlign="center" allowSort="false">发布时间
            </div>
            <div field="indexSort" sortField="indexSort" width="80" headerAlign="center" align="center"
                 allowSort="false">排序号
            </div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true">标准全文
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var currentUserId = "${currentUser.userId}";
    var permission = ${permission};
    var standardType =  "${standardType}";
    var currentUserRoles =${currentUserRoles};
    var currentUserZJ =${currentUserZJ};
    var tabName = "${tabName}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    if(!permission){
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
    }
    function operateRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var s = '<span  title="详情" onclick="openStandardEditWindow(\'' + standardId + '\',\'' + record.systemId + '\',\'detail\')">详情</span>';
        if (permission) {
            s += '<span  title="编辑" onclick="editForm(\'' + record.mainId + '\',\'edit\')">编辑</span>';
        }
        return s;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
