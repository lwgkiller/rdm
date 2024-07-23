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
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">用途说明: </span>
                    <input class="mini-textbox" id="usedirection" name="usedirection"/>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="toolListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/researchTool/train/queryTrain.do?type=rdmTrain"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div field="name" width="50" headerAlign='center' align='left'>名称</div>
            <div field="size" width="30" headerAlign="center" align="center" allowSort="true">大小</div>
            <div field="usedirection" headerAlign='center' align='left'>用途说明</div>
            <div field="download" headerAlign='center' align='center' width="50" renderer="downloadAdress">下载地址（需要登录网盘）
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var toolListGrid = mini.get("toolListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";


    function downloadAdress(e) {
        var record = e.record;
        var url = record.download;
        var url = url;
        var linkStr='<a href="#" style="color:#0044BB;" onclick="window.open(\'' + url+ '\')">'+url+'</a>';
        return linkStr;
    }
</script>
<redxun:gridScript gridId="toolListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
