<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>风险验证部件分解</title>
    <%@include file="/commons/list.jsp"%>
    <style type="text/css">
        .mini-layout-border>#center{
            background: transparent;
        }
        .mini-tree .mini-grid-viewport{
            background: #fff;
        }
        .icon-xia-add,
        .icon-shang-add,
        .icon-brush{
            color: #0daaf6;
        }
        .icon-addressbook,
        .icon-jia{
            color: #ff8b00;
        }
        .icon-baocun7{
            color:#2cca4e;
        }
        .icon-trash,
        .icon-offline{
            color: red;
        }
        .icon-quanxian2{
            color: #66b1ff;
        }
        .icon-button:before{
            font-size: 20px;
        }
    </style>
</head>
<body>

<div class="mini-fit" id="content">
    <div class="form-container" style="margin:0 auto; height:98%;">
        <li class="mini-toolbar">
            <ul class="toolBtnBox">
                <li>
                    <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">部件/接口名称:</p>
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

        <div class="mini-fit " style="height:100%;">
            <div
                id="groupGrid"
                class="mini-treegrid"
                style="width:100%;height:100%;"
                showTreeIcon="true"
                multiSelect="true"
                treeColumn="structName" idField="id" parentField="parentId"
                resultAsTree="false"
                allowResize="true"  allowAlternating="false"
                allowRowSelect="true"
                allowCellValid="true"
                allowCellEdit="false" allowCellSelect="true"
                autoload="true" allowCellWrap="true"
                url="${ctxPath}/drbfm/total/listStruct.do?belongTotalId=${id}"
                expandOnLoad="false"
                onbeforeload="onBeforeGridTreeLoadTotal"
            >
                <div property="columns">
                    <div name="parentId" field="parentId" visible="false"></div>
                    <div type="checkcolumn" width="20"></div>
                    <div name="sn" field="sn" align="center" width="70" headerAlign="center">序号
                        <input property="editor" changeOnMousewheel="false" class="mini-spinner"  minValue="1" maxValue="100000" required="true"/>
                    </div>
                    <div name="structName" field="structName" align="left" width="380" headerAlign="center">部件名称<span style="color:red">*</span>
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;" required="true"/>
                    </div>
                    <div name="structNumber" field="structNumber" align="left" width="150" headerAlign="center">部件编号<span style="color:red">*</span>
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;" required="true"/>
                    </div>
                    <div name="judgeNeedAnalyse" field="judgeNeedAnalyse" displayField="judgeNeedAnalyse" align="center" width="120" headerAlign="center">是否需要/参与风险分析
                        <input property="editor" class="mini-combobox" style="width:100%;" textField="judgeNeedAnalyse" valueField="judgeNeedAnalyse" value="是"
                               allowInput="false" data="[{'judgeNeedAnalyse':'是'},{'judgeNeedAnalyse':'否'}]"/>
                    </div>
                    <div field="analyseUserId" name="analyseUserId" align="center" width="110" displayField="analyseUserName" headerAlign="center">分析责任人
                        <input property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:90%;height:34px;" allowinput="false" length="200" maxlength="200" minlen="0" single="true" initlogindep="false"/>
                    </div>
                    <div name="judgeIsInterface" field="judgeIsInterface" displayField="judgeIsInterface" align="center" width="100" headerAlign="center">是否为接口
                        <input property="editor" class="mini-combobox" style="width:100%;" textField="judgeIsInterface" valueField="judgeIsInterface" value="否"
                               allowInput="false" data="[{'judgeIsInterface':'是'},{'judgeIsInterface':'否'}]"/>
                    </div>
                    <div name="interfaceAName" field="interfaceAName" align="center" width="120" headerAlign="center">接口A端要素名称
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;" required="true"/>
                    </div>
                    <div name="interfaceBName" field="interfaceBName" align="center" width="120" headerAlign="center">接口B端要素名称
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;" required="true"/>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var totalId="${id}";
    var groupGrid=mini.get('#groupGrid');


    function onKeyEnter() {
        queryNodeByName();
    }

    function queryNodeByName() {
        collapseGridAll();
        var key = $.trim(mini.get("fileNameFilter").getValue());
        if (key == "") {
            groupGrid.clearFilter();
        } else {
            key = key.toLowerCase();
            var nodes=groupGrid.findNodes(function (node) {
                var text = node.structName ? node.structName.toLowerCase() : "";
                if (text.indexOf(key) != -1) {
                    return true;
                }
            });
            //展开所有找到的节点
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                groupGrid.expandPath(node);
            }

            //第一个节点选中并滚动到视图
            var firstNode = nodes[0];
            if (firstNode) {
                groupGrid.selectNode(firstNode);
                groupGrid.scrollIntoView(firstNode);
            }
        }
    }

    function expandGrid(){
        var selectedNode = groupGrid.getSelectedNode();
        if(selectedNode) {
            groupGrid.expandNode(selectedNode);
        } else {
            mini.alert("请选择节点！");
        }
    }

    function collapseGrid(){
        var selectedNode = groupGrid.getSelectedNode();
        if(selectedNode) {
            groupGrid.collapseNode(selectedNode);
        } else {
            mini.alert("请选择节点！");
        }
    }

    function expandGridAll() {
        groupGrid.expandAll();
    }

    function collapseGridAll() {
        groupGrid.collapseAll();
    }

    function selectOk() {
        var selectedNode = groupGrid.getSelectedNodes();
        if (selectedNode.length > 2) {
            mini.alert("只能选择一或两个！");
            return;
        }
        if (selectedNode.length == 1) {
            var data = {structName: selectedNode[0].structName, interfaceRequestStructId: selectedNode[0].id};
            window.CloseOwnerWindow(data);
        } else if (selectedNode.length == 2) {
            var data = {structName: selectedNode[0].structName+"-"+selectedNode[1].structName
                , interfaceRequestStructId: selectedNode[0].id+","+selectedNode[1].id};
            window.CloseOwnerWindow(data);

        }
        else {
            mini.alert("请选择节点！");
        }
    }

    //用于展开树形结构时将当前节点的id赋值到parentId中
    function onBeforeGridTreeLoadTotal(e){
        var tree = e.sender;    //树控件
        var node = e.node;      //当前节点
        var params = e.params;  //参数对象
        //可以传递自定义的属性
        params.parentId = node.id;
    }
</script>
</body>
</html>
