<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>SOD打分标准发生度列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：勾选发生度分数，然后点保存生效！</span>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         showCloseButton="false"
         url="${ctxPath}/drbfm/single/showFashengdu.do"
         idField="id" showPager="false" onlyCheckSelection="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         allowCellEdit="false" allowCellWrap="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="selected" width="20px"></div>
            <div field="fashengId" name="fashengId" align="center" headerAlign="center" width="50px">频度（O）</div>
            <div field="pingGu" name="pingGu" width="100px" headerAlign="center" align="center" allowSort="false">
                对失效起因发生的预测
            </div>
            <div field="chanPinJingYan" width="200px" headerAlign="center" align="left" allowSort="false">
                产品经验（新设计和/或新应用）
            </div>
            <div field="yuFangCuoShi" width="200px" headerAlign="center" align="left" allowSort="false">预防控制预期效果</div>
        </div>
    </div>
</div>
<div id="ToolBar2" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：a.频度可根据产品验证活动降低。
            b.频度评估时产品经验和预防控制为“和/或”的关系。
        </span>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");

    listGrid.on("load", function (e) {
        listGrid.mergeColumns(["pingGu"]);
    });


    function saveData() {
        var selectedNode = listGrid.getSelected();
        if (selectedNode) {
            var data = {fashengId: selectedNode.fashengId};
            window.CloseOwnerWindow(data);

        } else {
            mini.alert("请选择发生度（O）！");
        }
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
