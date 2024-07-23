<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>SOD打分标准探测度列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：勾选探测度分数，然后点保存生效！</span>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" autoload="true"
         url="${ctxPath}/drbfm/single/showTancedu.do"
         idField="id" showPager="false"onlyCheckSelection="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[50,100,200]"  allowAlternating="true"
         allowCellEdit="false" allowCellWrap="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="selected" width="20px"></div>
            <div field="tanceId" name="tanceId" align="center" headerAlign="center" width="50px">探测度（D）</div>
            <div field="nengLi" name="nengLi" width="50px" headerAlign="center" align="center" allowSort="false">
                探测能力
            </div>
            <div field="chengShuDu" name="chengShuDu" width="200px" headerAlign="center" align="left" allowSort="false">探测方法成熟度</div>
            <div field="jiHui" name="jiHui" width="100px" headerAlign="center" align="center" allowSort="false" >探测机会</div>
            <div field="fangFa" name="fangFa" width="100px" headerAlign="center" align="center" allowSort="false" >探测方法</div>
        </div>
    </div>
</div>
<div id="ToolBar2" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：探测度打分同时考虑探测方法成熟度、时机、探测方法三者判定。</span>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");

    listGrid.on("load", function () {
        listGrid.mergeColumns(["nengLi","chengShuDu","jieDuan","beiZhu"]);
    });



    function saveData() {
        var selectedNode = listGrid.getSelected();
        if(selectedNode) {
            var data={tanceId:selectedNode.tanceId};
            window.CloseOwnerWindow(data);
        } else {
            mini.alert("未选择探测度(D)！");
        }
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
