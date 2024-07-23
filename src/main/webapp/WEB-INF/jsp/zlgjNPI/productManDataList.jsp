<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品管控数具详情列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/zlgjNPI/productManageList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designType" name="designType"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">交付物阶段: </span>
                    <input id="parentName" name="parentName" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key': '1', 'value': '产品性能需求'},{'key': '2', 'value': '整机参数设计'},
                                {'key': '3', 'value': '零部件参数设计'},{'key': '4', 'value': '首台验证'},
                                {'key': '5', 'value': '小批量产反馈'}]" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                </li>
            </ul>
        </form>
    </div>
</div>


<%--产品管控列表--%>
<div class="mini-fit" style="height: 100%;">
    <div id="cpgkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/kfgksj/getProductManDataList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="id" name="id" width="10"></div>
            <div field="designType" name="designType" sortField="designType" width="50" headerAlign="center" align="center" allowSort="false" renderer="onRendererDetail">
                设计型号</div>
            <div field="STATUS" name="STATUS" width="40" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">认定状态
            </div>
            <div field="parentName" name="parentName" sortField="parentName" width="50" align="center" headerAlign="center" renderer="onNameRenderer">交付物阶段</div>
            <div header="交付物" headerAlign="center">
                <div property="columns">
                    <div field="demandSideType" width="50" headerAlign="center" align="center" allowSort="false">需求</div>
                    <div field="demandElement" width="100" headerAlign="center" align="center" allowSort="false">需求项</div>
                    <div field="parameter" width="100" headerAlign="center" align="center" allowSort="false">参数</div>
                    <div field="standard" width="100" headerAlign="center" align="center" allowSort="false">标准</div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var cpgkListGrid = mini.get("cpgkListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";

    var nodeVarsStr = '${nodeVars}';

    var isScUser = '';
    cpgkListGrid.on("load", function () {
        cpgkListGrid.mergeColumns(["id","designType","parentName","STATUS"]);
    });

    function onRendererDetail(e) {
        var record = e.record;
        var manageId = record.id;
        var status = record.STATUS;
        var title=record.designType;
        return '<a href="#" style="text-decoration:underline;color:#44cef6;" onclick="productManageDetail(\''+manageId+'\',\''+record.STATUS+'\')">'+title+'</a>';
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.STATUS;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '认定中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '通过认定', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '未通过认定', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    function onNameRenderer(e) {
        var record = e.record;
        var parentName = record.parentName;

        var arr = [{'key': '1', 'value': '产品性能需求'},
            {'key': '2', 'value': '整机参数设计'},
            {'key': '3', 'value': '零部件参数设计'},
            {'key': '4', 'value': '首台验证'},{'key': '5', 'value': '小批量产反馈'}
        ];

        return $.formatItemValue(arr, parentName);
    }


</script>
<redxun:gridScript gridId="cpgkListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>