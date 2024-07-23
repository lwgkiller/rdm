<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>创新性打分列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
    </div>
    <div>
        <span style="color: red;font-size: large;float: left;margin-left: 10px">注：设计更改包括：设计结构、布局、原理、材质、重量、使用环境、使用方法等。</span>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" showCloseButton="false"
         url="${ctxPath}/drbfm/total/showChuangxinxing.do"
         idField="id" showPager="false"onlyCheckSelection="true"
         multiSelect="false" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         allowCellEdit="false" allowCellWrap="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" field="selected" width="20px"></div>
            <div field="score" name="fashengId" align="center" headerAlign="center" width="50px">分数</div>
            <div field="description" name="pingGu" width="200px" headerAlign="center" align="center" allowSort="false">
                分数描述
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");






    function saveData() {
        var selectedNode = listGrid.getSelected();
        if(selectedNode) {
            var data={score:selectedNode.score};
            window.CloseOwnerWindow(data);

        } else {
            mini.alert("请选择分数！");
        }
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
