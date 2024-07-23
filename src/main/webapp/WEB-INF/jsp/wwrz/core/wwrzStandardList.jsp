<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准结构化</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/wwrz/wwrzStandardList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">标准编号: </span>
                    <input name="standardNumber" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">项目名称: </span>
                    <input name="standardName" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="addButton" class="mini-button" plain="true" onclick="addForm()">新增</a>
            <div style="display: inline-block" class="separator"></div>
            <a id="delButton" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <div style="display: inline-block" class="separator"></div>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/wwrz/core/standard/list.do" idField="id" showPager="true" allowCellWrap="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div field="id" name="id" name="action" cellCls="actionIcons" width="80px" headerAlign="center"
                 align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="standardNumber" name="standardNumber" width="150px" headerAlign="center" align="center"
                 allowSort="false">标准编号
            </div>
            <div field="standardName" name="standardName" width="250px" headerAlign="center" align="left"
                 allowSort="false">标准名称
            </div>
            <div field="standardCode" name="standardCode" width="50px" headerAlign="center" align="center">章节编号</div>
            <div field="sectorAttach"  renderer="sectorAttach" width="150px" headerAlign="center" align="center" allowSort="false">章节附件</div>
            <div field="remark" name="remark" width="150px" headerAlign="center" align="left" allowSort="false">备注</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    listGrid.on("load", function () {
        listGrid.mergeColumns(["id", "mainId", "standardNumber", "standardName"]);
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.mainId;
        var creator = record.CREATE_BY_;
        var s = '';
        if (currentUserId != creator) {
            s += '<span  title="编辑" style="color: silver">编辑</span>';
        } else {
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\',\'edit\')">编辑</span>';
        }
        // s += '<span  title="查看" onclick="editForm(\'' + id + '\',\'view\')">查看</span>';
        return s;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
