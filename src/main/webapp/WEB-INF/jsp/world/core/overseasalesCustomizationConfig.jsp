<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外销售选配配置</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/common/UUID.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div style="display: none">
    <input id="excluTree" property="editor" class="mini-treeselect" multiSelect="true" resultAsTree="false"
           textField="nodeName" valueField="id" parentField="PARENT_ID_" allowInput="false"
           url="${ctxPath}/world/core/overseaSalesCustomization/configTreeQuery.do?mainId=${businessId}"
           onbeforenodecheck="onbeforenodecheck"/>
    <input id="combinTree" property="editor" class="mini-treeselect" multiSelect="true" resultAsTree="false"
           textField="nodeName" valueField="id" parentField="PARENT_ID_" allowInput="false"
           url="${ctxPath}/world/core/overseaSalesCustomization/configTreeQuery.do?mainId=${businessId}"
           onbeforenodecheck="onbeforenodecheck"/>
</div>
<%--工具栏--%>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li id="operateTree">
            <a class="mini-button" onclick="onAddBefore()">插入节点前</a>
            <a class="mini-button" onclick="onAddAfter()">插入节点后</a>
            <a class="mini-button" onclick="onAddSubNode()">插入子节点</a>
            <a class="mini-button" onclick="onMoveNode()">移动</a>
            <a class="mini-button btn-red" onclick="onRemoveNode()">删除</a>
            <a class="mini-button" iconCls="icon-save" onclick="onSaveNode()">保存</a>
            <p style="display: inline-block;color: red;font-size:14px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
                所有操作后均需“保存”后生效）
            </p>
        </li>
        <li style="float: right">
            <span style="padding-left:5px;">节点名称：</span>
            <input class="mini-textbox" width="120" id="filterNameId" onenter="filterSystemTree()"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()">查找</a>
            <a class="mini-button" onclick="expandAll()">展开所有</a>
            <a class="mini-button" onclick="collapseAll()">折叠所有</a>
            <a class="mini-button" iconCls="icon-reload" onclick="refreshSystemTree()">刷新</a>
        </li>
    </ul>
</div>
<%--树视图--%>
<ul id="systemTree" class="mini-treegrid" url="${ctxPath}/world/core/overseaSalesCustomization/configTreeQuery.do?mainId=${businessId}"
    showTreeIcon="true" treeColumn="nodeName" expandOnLoad="0" idField="id" parentField="PARENT_ID_" resultAsTree="false"
    style="width:100%;height:90%;padding:5px;" allowResize="true" allowCellEdit="true" allowCellSelect="true"
    oncellbeginedit="oncellbeginedit" oncellvalidation="oncellvalidation">
    <div property="columns">
        <div field="orderNo" width="60" headerAlign="center" align="center">序号
            <input property="editor" class="mini-spinner" style="width:100%;" minValue="0" maxValue="99999999"/>
        </div>
        <div name="nodeName" field="nodeName" width="150" headerAlign="center" align="center">节点名称
            <input property="editor" class="mini-textbox" style="width:100%;"/>
        </div>
        <%--<div field="nodeType" width="50" headerAlign="center" align="center" renderer="nodeTypeRender">节点类型</div>--%>
        <div field="referencePrice" width="60" headerAlign="center" align="center" numberFormat="n">参考价格
            <input property="editor" class="mini-spinner" style="width:100%;" minValue="-99999999" maxValue="99999999"
                   decimalPlaces="2" allowNull="true"/>
        </div>
        <div type="checkboxcolumn" field="isPriceCal" trueValue="1" falseValue="0" width="80" headerAlign="center">是否参与价格计算</div>
        <div type="checkboxcolumn" field="isChecked" trueValue="1" falseValue="0" width="80" headerAlign="center">是否默认选中</div>
        <div type="checkboxcolumn" field="isRequired" trueValue="1" falseValue="0" width="80" headerAlign="center">是否属于必选项</div>
        <div field="excluNodeIds" displayField="excluNodeNames" width="200" headerAlign="center">互斥节点</div>
        <div field="combinNodeIds" displayField="combinNodeNames" width="200" headerAlign="center">组合节点</div>
        <div field="configNodeFile" width="80" headerAlign="center" align="center" renderer="configNodeFileRenderer">附件
        </div>
    </div>
</ul>
<%--目标节点用窗体--%>
<div id="moveWindow" title="选择目标节点" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true">
    <ul id="moveTree" class="mini-tree" style="width:100%;height:100%;"
        showTreeIcon="true" textField="nodeName" idField="id" expandOnLoad="0" parentField="PARENT_ID_" resultAsTree="false">
    </ul>
    <div property="footer" style="padding:5px;height: 30px">
        <table style="width:100%;height: 100%">
            <tr>
                <td>
                    <span style="font-size: 15px">插入方式：</span>
                    <select id="moveAction" style="height: 25px;width: 90px">
                        <option value="before">节点前</option>
                        <option value="after">节点后</option>
                        <option value="add" selected>节点内</option>
                    </select>
                </td>
                <td style="width:200px;text-align:right;">
                    <input type="button" style="height: 25px;width: 70px" value="确定"
                           onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessId = "${businessId}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var nodeTypeList = '${nodeTypeList}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var systemTree = mini.get("systemTree");
    var moveWindow = mini.get("moveWindow");
    var moveTree = mini.get("moveTree");
    var excluTree = mini.get("excluTree");
    var combinTree = mini.get("combinTree");
    //..新增节点工厂
    function newNodeFactory(parentNode) {
        var nodeType = "";
        var isChecked = "0";
        var isRequired = "0";
        if (parentNode.nodeType == "basicXuJieDian") {
            nodeType = "basicJieDian";
            isChecked = "1";
            isRequired = "1";
        } else if (parentNode.nodeType == "customXuJieDian") {
            nodeType = "customXuJieDian2";
        } else if (parentNode.nodeType == "customXuJieDian2") {
            nodeType = "customJieDian";
        }
        var id = Math.uuidFast();
        var newNode = {
            id: id,
            mainId: businessId,
            PARENT_ID_: parentNode.id,
            nodeType: nodeType,
            nodeDepth: parentNode.nodeDepth + 1,
            nodePath: parentNode.nodePath + "." + id,
            isChecked: isChecked,
            isRequired: isRequired
        };
        return newNode;
    }
    //..节点前添加
    function onAddBefore() {
        var selectNode = systemTree.getSelectedNode();
        if (!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        if (!selectNode.PARENT_ID_) {
            mini.alert('不允许增加根节点');
            return;
        }
        if (selectNode.nodeType == "basic" || selectNode.nodeType == "basicXuJieDian"
            || selectNode.nodeType == "customXuJieDian") {
            mini.alert('不允许增加骨架节点！');
            return;
        }
        var parentNode = systemTree.getParentNode(selectNode);
        var newNode = newNodeFactory(parentNode);
        systemTree.addNode(newNode, "before", selectNode);
        systemTree.selectNode(newNode);
        systemTree.scrollIntoView(newNode);
    }
    //..节点后添加
    function onAddAfter() {
        var selectNode = systemTree.getSelectedNode();
        if (!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        if (!selectNode.PARENT_ID_) {
            mini.alert('不允许增加根节点');
            return;
        }
        if (selectNode.nodeType == "basic" || selectNode.nodeType == "basicXuJieDian"
            || selectNode.nodeType == "customXuJieDian") {
            mini.alert('不允许增加骨架节点！');
            return;
        }
        var parentNode = systemTree.getParentNode(selectNode);
        var newNode = newNodeFactory(parentNode);
        systemTree.addNode(newNode, "after", selectNode);
        systemTree.selectNode(newNode);
        systemTree.scrollIntoView(newNode);
    }
    //..子节点添加
    function onAddSubNode() {
        var selectNode = systemTree.getSelectedNode();
        if (!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        if (selectNode.nodeType != "basicXuJieDian" && selectNode.nodeType != "customXuJieDian"
            && selectNode.nodeType != "customXuJieDian2") {
            mini.alert('请在骨架节点内增加子节点！');
            return;
        }
        var parentNode = selectNode;
        var newNode = newNodeFactory(parentNode);
        systemTree.addNode(newNode, "add", selectNode);
        systemTree.selectNode(newNode);
        systemTree.scrollIntoView(newNode);
    }
    //..移动节点
    function onMoveNode(e) {
        var selectNode = systemTree.getSelectedNode();
        if (selectNode) {
            if (!selectNode.PARENT_ID_) {
                mini.alert('不允许移动根节点');
                return;
            }
            if (selectNode.nodeType == "basic" || selectNode.nodeType == "basicXuJieDian"
                || selectNode.nodeType == "customXuJieDian") {
                mini.alert('不允许移动骨架节点！');
                return;
            }
            moveWindow.show();
            fillMoveTree(systemTree.getData());
        } else {
            mini.alert("请选择一个节点");
        }
    }
    //..
    function fillMoveTree(treeData) {
        treeData = mini.clone(treeData);
        moveTree.loadData(treeData);
        $("#moveAction").val("add");
    }
    //..删除节点
    function onRemoveNode() {
        var selectNode = systemTree.getSelectedNode();
        if (!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        //根节点不允许删除
        if (!selectNode.PARENT_ID_) {
            mini.alert('根节点不允许删除');
            return;
        }
        if (selectNode.nodeType == "basic" || selectNode.nodeType == "basicXuJieDian"
            || selectNode.nodeType == "customXuJieDian") {
            mini.alert('不允许删除骨架节点！');
            return;
        }
        mini.confirm("确定删除选中节点？", "确定？",
            function (action) {
                if (action == "ok") {
                    systemTree.removeNode(selectNode);
                }
            }
        );
    }
    //..保存节点
    function onSaveNode() {
        //检查必填项
        var checkResult = saveValidCheck();
        if (!checkResult.success) {
            mini.alert(checkResult.reason);
            return;
        }
        if (systemTree.isChanged()) {
            var message = "数据保存成功！";
            var changes = systemTree.getChanges();
            _SubmitJson({
                url: jsUseCtxPath + "/world/core/overseaSalesCustomization/saveConfigTree.do",
                method: 'POST',
                data: changes,
                postJson: true,
                showMsg: false,
                success: function (returnData) {
                    if (returnData.success) {
                        mini.alert(returnData.message);
                        refreshSystemTree();
                    } else {
                        mini.alert("数据保存失败:" + returnData.message);
                    }
                },
                fail: function (returnData) {
                    mini.alert("数据保存失败:" + returnData.message);
                }
            });
        } else {
            mini.alert("数据保存成功！");
        }
    }
    //..
    function saveValidCheck() {
        var checkResult = {};
        systemTree.validate();
        if (!systemTree.isValid()) {
            var error = systemTree.getCellErrors()[0];
            systemTree.beginEditCell(error.record, error.column);
            checkResult.success = false;
            checkResult.reason = error.column.header + error.errorText;
            return checkResult;
        }
        checkResult.success = true;
        return checkResult;
    }
    //..查找节点
    function filterSystemTree() {
        systemTree.selectNode(null);
        systemTree.clearFilter();
        collapseAll();
        var nameFilter = $.trim(mini.get("filterNameId").getValue());
        if (nameFilter) {
            nameFilter = nameFilter.toLowerCase();
            var nodes = systemTree.findNodes(function (node) {
                var nodeName = node.nodeName ? node.nodeName.toLowerCase() : "";
                if (nameFilter && nodeName.indexOf(nameFilter) == -1) {
                    return false;
                }
                return true;
            });
            //展开所有找到的节点
            for (var i = 0, l = nodes.length; i < l; i++) {
                var node = nodes[i];
                systemTree.expandPath(node);
            }
            //第一个节点选中并滚动到视图
            var firstNode = nodes[0];
            if (firstNode) {
                systemTree.selectNode(firstNode);
                systemTree.scrollIntoView(firstNode);
            }
        }
    }
    //..展开
    function expandAll() {
        systemTree.expandAll();
    }
    //..收缩
    function collapseAll() {
        systemTree.collapseAll();
    }
    //..刷新
    function refreshSystemTree() {
        systemTree.load();
        excluTree.load();
        combinTree.load();
    }
    //..
    function okWindow() {
        var moveNode = systemTree.getSelectedNode();
        var targetNode = moveTree.getSelectedNode();
        var moveAction = $("#moveAction").val();
        if (moveNode && targetNode && moveAction) {
            targetNode = systemTree.getNode(targetNode.id);
            if (systemTree.isAncestor(moveNode, targetNode)) {
                mini.alert("移动节点不能是目标节点的父节点或本身");
                return;
            }
            if (moveAction == "before" || moveAction == "after") {
                if (targetNode.nodeType == "basic" || targetNode.nodeType == "basicXuJieDian"
                    || targetNode.nodeType == "customXuJieDian" || targetNode.nodeType == "customXuJieDian2") {
                    mini.alert('不符合移动规则！');
                    return;
                }
                if (moveNode.nodeType == "customJieDian" && targetNode.nodeType == "basicJieDian") {
                    //选配节点->基础节点，强制选中，其余可保留，因为自动计算会甄别节点类型
                    moveNode.isChecked = "1";
                    moveNode.isRequired = "1";
                    moveNode.nodeType = "basicJieDian";
                } else if (moveNode.nodeType == "basicJieDian" && targetNode.nodeType == "customJieDian") {
                    //基础节点->选配节点，强制不选中，其余可保留，因为自动计算会甄别节点类型
                    moveNode.isChecked = "0";
                    moveNode.isRequired = "0";
                    moveNode.nodeType = "customJieDian";
                }
                moveNode.PARENT_ID_ = targetNode.PARENT_ID_;
            } else if (moveAction == "add") {
                if (targetNode.nodeType != "basicXuJieDian" && targetNode.nodeType != "customXuJieDian2") {
                    mini.alert('不符合移动规则！');
                    return;
                }
                if (moveNode.nodeType == "customJieDian" && targetNode.nodeType == "basicXuJieDian") {
                    //选配节点->基础节点，强制选中，其余可保留，因为自动计算会甄别节点类型
                    moveNode.isChecked = "1";
                    moveNode.isRequired = "1";
                    moveNode.nodeType = "basicJieDian";
                } else if (moveNode.nodeType == "basicJieDian" && targetNode.nodeType == "customXuJieDian2") {
                    //基础节点->选配节点，强制不选中，其余可保留，因为自动计算会甄别节点类型
                    moveNode.isChecked = "0";
                    moveNode.isRequired = "0";
                    moveNode.nodeType = "customJieDian";
                }
                moveNode.PARENT_ID_ = targetNode.id;
            }
            systemTree.moveNode(moveNode, targetNode, moveAction);
            moveWindow.hide();
        } else {
            mini.alert("请选择一个目标节点");
        }

    }
    //..
    function hideWindow() {
        moveWindow.hide();
    }
    //..
    function oncellbeginedit(e) {
        var record = e.record, field = e.field;
        //basic,basicXuJieDian,customXuJieDian 不可编辑
        if (record.nodeType == "basic" || record.nodeType == "basicXuJieDian" || record.nodeType == "customXuJieDian") {
            e.cancel = true;
        }
        //root 可填名称,价格
        if (record.nodeType == "root" &&
            (field == "isPriceCal" || field == "isChecked" || field == "isRequired" ||
            field == "excluNodeIds" || field == "combinNodeIds" || field == "orderNo")) {
            e.cancel = true;
        }
        //basicJieDian,customXuJieDian2可填名称，序号
        if ((record.nodeType == "basicJieDian" || record.nodeType == "customXuJieDian2") &&
            (field == "referencePrice" || field == "isPriceCal" || field == "isChecked" || field == "isRequired" ||
            field == "excluNodeIds" || field == "combinNodeIds")) {
            e.cancel = true;
        }
        if (field == "excluNodeIds") {
            e.editor = mini.get("excluTree");
            e.column.editor = e.editor;
        } else if (field == "combinNodeIds") {
            e.editor = mini.get("combinTree");
            e.column.editor = e.editor;
        }
    }
    //..
    function oncellvalidation(e) {
        if (e.field == 'nodeName' && !e.value) {
            e.isValid = false;
            e.errorText = '不能为空';
        }
    }
    //..不能选择骨架节点
    function onbeforenodecheck(e) {
        if (e.node.nodeType == "basic" || e.node.nodeType == "basicXuJieDian"
            || e.node.nodeType == "customXuJieDian" || e.node.nodeType == "customXuJieDian2") {
            e.cancel = true;
        }
    }
    //..节点类型渲染
    function nodeTypeRender(e) {
        return dicKeyValueRender(e, nodeTypeList);
    }
    //..
    function dicKeyValueRender(e, keyValueList) {
        var record = e.record;
        var key = record[e.field];
        var arr = mini.decode(keyValueList);
        return $.formatItemValue(arr, key);
    }
    //..
    function configNodeFileRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        if (record.id) {
            cellHtml = '<span title="编辑" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'' +
                'overseaSalesCustomizationConfigNodeFile' + '\',\'edit\',\'' + coverContent + '\')">编辑</span>';
        }
        return cellHtml;
    }
    //..
    function openFileWindow(businessId, businessType, action, coverContent) {
        var changes = systemTree.getChanges();
        if (changes.length > 0) {
            mini.alert("请先保存BOM的修改，再进行文件操作！");
            return;
        }
        mini.open({
            title: "文件列表",
            url: jsUseCtxPath + "/world/core/overseaSalesCustomization/openFileWindow.do?businessId=" +
            businessId + "&businessType=" + businessType + "&action=" + action + "&coverContent=" + coverContent,
            width: 1000,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
</script>
</body>
</html>