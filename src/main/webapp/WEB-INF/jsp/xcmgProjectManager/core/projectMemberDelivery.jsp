<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>名称列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：勾选负责交付物并选择对应的产品，然后保存！</span>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/xcmgProjectManager/core/xcmgProject/listDeliveryData.do?projectId=${projectId}&userId=${userId}"
         idField="id" showPager="false"onlyCheckSelection="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditDetail"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn"  field="selected" width="20px"></div>
            <div type="indexcolumn"   align="center" headerAlign="center" width="30px">序号</div>
            <div field="stageName" name="stageName" width="100px" headerAlign="center" align="center" allowSort="false">
                阶段名称
            </div>
            <div field="deliveryName" name="deliveryName" width="200px" headerAlign="center" align="left" allowSort="false">交付物名称</div>
            <div field="deliveryLevel" width="60px" headerAlign="center" align="center" allowSort="false" renderer="onDeliveryLevelRenderer">必填交付物</div>
            <div field="productIds" displayField="productNames" headerAlign="center" align="center" width="120">产品设计型号
                <input property="editor" class="mini-combobox" textField="productName" valueField="productId"
                valueField="productId" readonly
                emptyText="请选择..." multiSelect="true"
                showNullItem="true" nullItemText="请选择..."
                url="${ctxPath}/xcmgProjectManager/core/xcmgProject/projectProductList.do?projectId=${projectId}"/>
            </div>
            <div field="planTime" width="100px" dateFormat="yyyy-MM-dd" headerAlign="center" align="center" allowSort="false">完成时间</div>
            <div field="itemName" width="100px" headerAlign="center" align="center" allowSort="false">新品试制计划节点</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var projectId = "${projectId}";
    var userId = "${userId}";
    var editable = "${editable}";
    listGrid.on("load", function (e) {
        listGrid.mergeColumns(["stageName","deliveryName","deliveryLevel"]);
    });
    if(editable=='false'){
        mini.get('save').setEnabled(false);
    }
    listGrid.on("update", function (e) {
        var rows = grid.findRows(function (row) {
            if (row.selected) {
                return true;
            }
        });
        grid.selects(rows);
    });
    function OnCellBeginEditDetail(e) {
        if(e.field != 'productIds'){
            return;
        }
        // var record = e.record;
        // var deliveryLevel = record.deliveryLevel;
        // if(deliveryLevel>0){
        //     e.cancel = true;
        // }
    }


    function saveData() {
        var data = listGrid.getSelecteds();
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + "/xcmgProjectManager/core/xcmgProject/saveProjectDeliveryProduct.do?projectId=" + projectId + "&userId=" + userId,
            data: json,
            type: "post",
            contentType: 'application/json',
            async: false,
            success: function (text) {
                if (text && text.message) {
                    mini.alert("保存成功！");
                    searchFrm();
                }
            }
        });
    }
    function onDeliveryLevelRenderer(e) {
        var record = e.record;
        var deliveryLevel = record.deliveryLevel;
        var _html = '';
        if(deliveryLevel==0){
            _html = '否'
        }else{
            _html = '是'
        }

        return _html;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
