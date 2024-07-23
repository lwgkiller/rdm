<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">零部件名称：</span>
                    <input class="mini-textbox" id="partsDescription" name="partsDescription"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">研究方向：</span>
                    <input class="mini-textbox" id="researchDirection" name="researchDirection"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">涉及整机型号：</span>
                    <input class="mini-textbox" id="model" name="model"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">研究方向来源：</span>
                    <input class="mini-textbox" id="researchDirectionSource" name="researchDirectionSource"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">研究目标：</span>
                    <input class="mini-textbox" id="researchObjective" name="researchObjective"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">预计完成时间：</span>
                    <input id="completionTime" name="completionTime" class="mini-datepicker"
                           format="yyyy-MM-dd" showTime="false"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <f:a alias="PartsResearchDirection-searchFrm" onclick="searchFrm()" showNoRight="false" style="margin-right: 5px">查询</f:a>
                    <f:a alias="PartsResearchDirection-addBusiness" onclick="addBusiness()" showNoRight="false" style="margin-right: 5px">新增</f:a>
                    <f:a alias="PartsResearchDirection-editBusiness" onclick="editBusiness()" showNoRight="false" style="margin-right: 5px">编辑</f:a>
                    <f:a alias="PartsResearchDirection-removeBusiness" onclick="removeBusiness()" showNoRight="false"
                         style="margin-right: 5px">删除</f:a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" idField="id" multiSelect="false"
         sizeList="[20,50]" pageSize="20" pagerButtons="#pagerButtons"
    <%--todo:测--%>
         url="${ctxPath}/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/dataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div field="model" width="180" headerAlign="center" align="center" renderer="render">零部件名称</div>
            <div field="researchDirection" width="100" headerAlign="center" align="center" renderer="render">研究方向</div>
            <div field="model" width="200" headerAlign="center" align="center" renderer="render">涉及整机型号</div>
            <div field="researchDirectionSource" width="200" headerAlign="center" align="center" renderer="render">研究方向来源</div>
            <div field="researchObjective" width="200" headerAlign="center" align="center" renderer="render">研究目标</div>
            <div field="completionTime" width="200" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">预计完成时间</div>
            <div field="completedBy" width="200" headerAlign="center" align="center" renderer="render">主要完成人</div>
            <div field="CREATE_TIME_" width="200" headerAlign="center" align="center" dateFormat="yyyy-MM-dd">创建日期</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    //..
    businessListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var businessId = record.id;
        detailBusiness(businessId);
    });

    //..
    function removeBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection/deleteData.do",
                    method: 'POST',
                    data: {id: row.id},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            businessListGrid.reload();
                        }
                    }
                });
            }
        });
    }
    //..
    function addBusiness() {
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection" +
            "/editPage.do?action=add";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function editBusiness() {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection" +
            "/editPage.do?businessId=" + row.id + "&action=edit";
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/powerApplicationTechnology/core/partsIntegration/partsResearchDirection" +
            "/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
    }
    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>