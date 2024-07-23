<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>战略课题</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">战略课题名称: </span>
            <input id="key" name="KPI_name" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="KPI_name"
               onclick="searchByName()">查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/strategicPlanning/core/zlghData/zlkt/list.do" idField="id"
         showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         autoLoad="false" allowCellWrap="false" pagerButtons="#pagerButtons" >
        <div property="columns">
            <div type="checkcolumn" width="10">#</div>
            <div name="zlktId" filed="zlktId" visible="false"></div>
            <div  name="ktName" field="ktName" width="40" sortField="kt_name" headerAlign="center" align="center" allowSort="true">
                战略课题名称<span style="color: #ff0000">*</span><input property="editor" class="mini-textbox"/></div>
            <div field="creator" width="20" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="30" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">创建时间</div>
        </div>
    </div>
</div>
<div class="mini-toolbar" style="text-align:center;padding-top:8px;padding-bottom:8px;" borderStyle="border:0;">
    <a class="mini-button" style="width:60px;" onclick="onOk()">确定</a>
    <span style="display:inline-block;width:25px;"></span>
    <a class="mini-button" style="width:60px;" onclick="onCancel()">取消</a>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo="${currentUserNo}";
    var currentUserId="${currentUserId}";
    var isZLGHZY=${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    var zljcId = "";
    // qxkz();
    // 回车搜索
    function onHanlerEnter() {
        searchByName();
    }
    // 搜索
    function searchByName() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({name: "ktName", value: cxName});
        }
        if (this.zljcId) {
            queryParam.push({name: "zljcId", value: this.zljcId});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data);
    }

    // 刷新
    function refreshAchievementType() {
        searchByName();
    }

    // 权限控制
    function qxkz(){
        if (!isZLGHZY){
            $("p").hide();
            mini.get("addKpi").hide();
            mini.get("removeKpi").hide();
            mini.get("saveKpi").hide();
            return;
        }

    }

    // 回调事件 获取父节点传输的数据
    function SetData(rowData) {
        if (rowData) {
            this.zljcId = rowData.id
            searchByName();
        }
    }

    // 回调事件 获取弹窗选择的行
    function GetData() {
        var row = listGrid.getSelected();
        return row;
    }

    // 双击行事件
    function onRowDblClick(e) {
        onOk();
    }
    //////////////////////////////////
    function CloseWindow(action) {
        if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
        else window.close();
    }

    function onOk() {
        CloseWindow("ok");
    }
    function onCancel() {
        CloseWindow("cancel");
    }

</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
