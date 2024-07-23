<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <li class="mini-toolbar">
        <ul class="toolBtnBox">
            <li>
                <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">名称:</p>
                <input id="fileNameFilter" class="mini-textbox" style="width:200px" onenter="onKeyEnter"/>
                <a class="mini-button"  onclick="queryNodeByName()" >查找</a>
                <a class="mini-button"  onclick="expandGridAll()" >展开所有</a>
                <a class="mini-button"  onclick="collapseGridAll()" >折叠所有</a>
                <a class="mini-button" onclick="expandGrid()" >展开选中</a>
                <a class="mini-button" onclick="collapseGrid()" >折叠选中</a>
                <div style="display: inline-block" class="separator"></div>
                <a class="mini-button btn-red" onclick="selectOk()" >确定</a>
            </li>
        </ul>
    </li>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-treegrid" style="width: 100%; height: 100%;" showTreeIcon="true" multiSelect="false"
         treeColumn="description" idField="id" parentField="parentId" resultAsTree="false" virtualScroll="true"
         allowResize="true"  allowAlternating="false"
         allowRowSelect="true"
         allowCellValid="true"
         allowCellEdit="false" allowCellSelect="true"
         autoload="true" allowCellWrap="true"
         url="${ctxPath}/zhgl/core/patentInterpretation/technologyBranch/dataListQuery.do"
         expandOnLoad="true" expandOnDblClick="false">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" width="20" headerAlign="center" align="center">序号</div>
            <div name="description" field="description" width="180" headerAlign="center" align="center">名称</div>
            <div name="liableUser" field="liableUser" width="180" headerAlign="center" align="center">责任人</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentId = ""

    businessListGrid.on("rowdblclick", function (e) {
        var record = e.record;
        var businessId = record.id;
        detailBusiness(businessId);
    });

    businessListGrid.on("load", function (e) {
        gotoCurrentById(currentId);
    })
    //..


    //..
    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/zhgl/core/patentInterpretation/technologyBranch" +
            "/editPage.do?businessId=" + businessId + "&action=detail";
        var winObj = window.open(url, '');
    }
    function onKeyEnter() {
        queryNodeByName();
    }
    function queryNodeByName() {
        collapseGridAll();
        var key = $.trim(mini.get("fileNameFilter").getValue());
        if (key == "") {
            businessListGrid.clearFilter();
        } else {
            key = key.toLowerCase();
            var nodes=businessListGrid.findNodes(function (node) {
                var text = node.description ? node.description.toLowerCase() : "";
                if (text.indexOf(key) != -1) {
                    return true;
                }
            });
            //展开所有找到的节点
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                businessListGrid.expandPath(node);
            }

            //第一个节点选中并滚动到视图
            var firstNode = nodes[0];
            if (firstNode) {
                businessListGrid.selectNode(firstNode);
                businessListGrid.scrollIntoView(firstNode);
            }
        }
    }
    function gotoCurrentById(currentId) {
        var key = currentId;
        if (key == "") {

        } else {
            //查找到节点
            var nodes = businessListGrid.findNodes(function (node) {
                var text = node.id ? node.id : "";
                if (text == key) {
                    return true;
                }
            });
            //展开所有找到的节点
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                //第一层节点
                if (node.children && node.parentId == "") {
                    businessListGrid.expandNode(node);
                }//其他节点
                else {
                    businessListGrid.expandPath(node);
                }
            }
            //第一个节点选中并滚动到视图
            var firstNode = nodes[0];
            if (firstNode) {
                businessListGrid.selectNode(firstNode);
                businessListGrid.scrollIntoView(firstNode);
            }
        }
    }

    function expandGridAll() {
        businessListGrid.expandAll();
    }

    function collapseGridAll() {
        businessListGrid.collapseAll();
    }
    function expandGrid(){
        var selectedNode = businessListGrid.getSelectedNode();
        if(selectedNode) {
            businessListGrid.expandNode(selectedNode);
        } else {
            mini.alert("请选择节点！");
        }
    }
    function collapseGrid(){
        var selectedNode = businessListGrid.getSelectedNode();
        if(selectedNode) {
            businessListGrid.collapseNode(selectedNode);
        } else {
            mini.alert("请选择节点！");
        }
    }
    function selectOk() {

        var selectedNode = businessListGrid.getSelectedNode();
        if(selectedNode.children)
        {
            mini.alert("请展开选择最后一级节点！");
            return ;
        }

        if(selectedNode) {
            var data={description:selectedNode.description,liableUser:selectedNode.liableUser};
            window.CloseOwnerWindow(data);
        } else {
            mini.alert("请选择节点！");
        }
    }
</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>