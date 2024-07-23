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
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button"  showNoRight="false" style="margin-right: 5px"plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button " style="margin-right: 5px" plain="true" onclick="freshStatus()">刷新RDM流程状态</a>
                    <a id="deleteBusiness" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="deleteBusiness()">删除</a>
                    <a id="exportBusiness" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportBusiness()">导出</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         allowCellWrap="true" showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true" oncellvalidation="onCellValidation"
         multiSelect="true" showColumnsMenu="false" sizeList="[50,100,500,1000,2000,5000]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons" url="${ctxPath}/serviceEngineering/core/decorationManualIntegrity/storeDataListQuery.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="materialCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料编码</div>
            <div field="materialName" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料名称</div>
            <div field="materialDesc" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">物料描述</div>
            <div field="useDescBook" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">使用说明书</div>
            <div field="useTopicCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">Topic编码</div>
            <div field="useBpmStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">RDM流程状态</div>
            <div field="repairBook" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">维修要领书</div>
            <div field="repairTopicCode" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">Topic编码</div>
            <div field="repairBpmStatus" width="100" headerAlign="center" align="center" allowSort="true" renderer="render">RDM流程状态</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/decorationManualIntegrity/exportStoreExcel.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var decorationManualAdmin = "${decorationManualAdmin}";
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    //..
    $(function () {

    });

    function freshStatus(){
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/decorationManualIntegrity/storeDataListFresh.do',
            success: function (result) {

                    top._ShowTips({
                        msg: result.message
                    });

                    businessListGrid.reload();

            }
        })
    }
    function  deleteBusiness(record){
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }

        if(currentUserNo != 'admin' && currentUserNo !=decorationManualAdmin){
            mini.alert("只有管理员或者手册管理员可以删除");
            return;
        }

        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.materialCode);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/serviceEngineering/core/decorationManualIntegrity/deleteStoreItem.do",
                    method: 'POST',
                    data: {ids: rowIds.join(',')},
                    postJson: false,
                    showMsg: false,
                    success: function (returnData) {
                        if (returnData.success) {
                            mini.alert(returnData.message);
                            searchFrm();
                        } else {
                            mini.alert("删除失败:" + returnData.message);
                        }
                    },
                    fail: function (returnData) {
                        mini.alert("删除失败:" + returnData.message);
                    }
                });
            }
        });
    }
    //..
    function exportBusiness() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>