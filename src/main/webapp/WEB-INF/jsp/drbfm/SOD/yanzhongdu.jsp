<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>SOD打分标准严重度列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：勾选严重度分数，然后点保存生效！</span>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/drbfm/single/showYanzhongdu.do"
         idField="id" showPager="false"onlyCheckSelection="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         allowCellEdit="false" allowCellWrap="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="selected" width="20px"></div>
            <div field="yanzhongId" name="yanzhongId" align="center" headerAlign="center" width="50px">严重度(S)</div>
            <div field="yingXiang" name="yingXiang" width="100px" headerAlign="center" align="center" allowSort="false">
                影响
            </div>
            <div field="biaoZhun" width="200px" headerAlign="center" align="left" allowSort="false">严重度标准</div>
            <div field="beiZhu" name="beiZhu" width="200px" headerAlign="center" align="center" allowSort="false" >备注/示例</div>
        </div>
    </div>
</div>
<div id="ToolBar2" class="toolbar" >
    <div>
        <span style="color: red;font-size: medium">注：严重度评分标准中提及的基本/主要功能和次要功能是针对整机的，不是指零部件自身的主要功能和次要功能，这两者不完全是一回事。
        <br>  例如：车内阅读灯的主要功能是发光，其失效对整机的主要功能（行驶、制动、转向、驾驶视线等）没有任何影响。</span>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var yanzhongId ="${yanzhongId}";
    listGrid.on("load", function (e) {
        listGrid.mergeColumns(["yingXiang","beiZhu"]);
    });


    function saveData() {
        var selectedNode = listGrid.getSelected();
        if(selectedNode) {
            var data={yanzhongId:selectedNode.yanzhongId};
            window.CloseOwnerWindow(data);
        } else {
            mini.alert("请选择严重度(S)！");
        }
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
