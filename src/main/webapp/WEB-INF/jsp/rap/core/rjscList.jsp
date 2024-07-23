<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rap/rjscList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">软件名称: </span>
                    <input class="mini-textbox" id="rjName" name="rjName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">版本号: </span>
                    <input class="mini-textbox" id="rjNo" name="rjNo"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="rjscListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Rjsc/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="rjName" headerAlign='center' align='center' width="40">软件名称</div>
            <div field="rjNo" headerAlign='center' align='center' width="40">版本号</div>
            <div field="term" headerAlign='center' align='center' width="40">有效期</div>
            <div field="userName" headerAlign='center' align='center' width="40">上传人</div>
            <div field="CREATE_TIME_" width="25" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">上传时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var rjscListGrid = mini.get("rjscListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";

    function onMessageActionRenderer(e) {
        var record = e.record;
        var rjId = record.rjId;
        var s = '';
        s += '<span  title="查看" onclick="rjscDetail(\'' + rjId + '\')">查看</span>';
        if(record.CREATE_BY_ ==currentUserId){
            s += '<span  title="编辑" onclick="addNew(\'' + rjId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeRjsc(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }


</script>
<redxun:gridScript gridId="rjscListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
