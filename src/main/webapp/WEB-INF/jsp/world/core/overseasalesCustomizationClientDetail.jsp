<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>海外销售选配配置</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/common/UUID.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<%--布局--%>
<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
    <div id="west" showHeader="false" region="west" showSplitIcon="true" width="1200">
        <%--工具栏--%>
        <div>
            <ul class="toolBtnBox">
                <li id="operateTree">
                    <a id="onSaveInst" class="mini-button" iconCls="icon-save" onclick="onSaveInst()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name1"/>
                    </a>
                    <a id="doCreateReport" class="mini-button" iconCls="icon-print" onclick="doCreateReport()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name22"/>
                    </a>
                    <a id="doCreateReport2" class="mini-button" iconCls="icon-print" onclick="doCreateReport2()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name2"/>
                    </a>
                    <%--<a id="doCreateReport" class="mini-button" onclick="aditionalServices()">--%>
                    <%--<spring:message code="page.overseasalesCustomizationClientDetail.name17"/>--%>
                    <%--</a>--%>
                    <%--<a id="doCreateReport2" class="mini-button" onclick="financing()">--%>
                    <%--<spring:message code="page.overseasalesCustomizationClientDetail.name18"/>--%>
                    <%--</a>--%>
                </li>
                <li style="float: right">
                    <span style="padding-left:5px;"><spring:message code="page.overseasalesCustomizationClientDetail.name7"/>：</span>
                    <input class="mini-textbox" width="120" id="filterNameId" onenter="filterSystemTree()"/>
                    <a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name3"/>
                    </a>
                    <a class="mini-button" onclick="expandAll()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name4"/>
                    </a>
                    <a class="mini-button" onclick="collapseAll()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name5"/>
                    </a>
                    <a class="mini-button" iconCls="icon-reload" onclick="refreshSystemTree()">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name6"/>
                    </a>
                </li>
            </ul>
            <%--树视图--%>
            <ul id="systemTree" class="mini-treegrid"
                showTreeIcon="true" treeColumn="nodeName" expandOnLoad="0" idField="id" parentField="PARENT_ID_" resultAsTree="false"
                style="width:100%;height:100%;padding:5px;" allowResize="true" allowCellEdit="true" allowCellSelect="true"
                showCheckBox="true" showFolderCheckBox="false" checkedField="isChecked" autoCheckParent="false" checkOnTextClick="false"
                oncellbeginedit="oncellbeginedit" onnodecheck="onnodecheck">
                <div property="columns">
                    <div type="indexcolumn"></div>
                    <div name="nodeName" field="nodeName" width="300" headerAlign="center" align="center">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name7"/>
                    </div>
                    <%--<div name="nodeType" field="nodeType" width="200" headerAlign="center" align="center">节点类型</div>--%>
                    <div field="salesPrice" width="60" headerAlign="center" align="center" numberFormat="n">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name8"/>
                        <input property="editor" class="mini-spinner" style="width:100%;" minValue="-99999999" maxValue="99999999"
                               decimalPlaces="2" allowNull="true"/>
                    </div>
                    <div field="configNodeFile" width="60" headerAlign="center" align="center" renderer="configNodeFileRenderer">
                        <spring:message code="page.overseasalesCustomizationClientDetail.name9"/>
                    </div>
                </div>
            </ul>
        </div>
    </div>
    <div id="center" showHeader="false" region="center">
        <div style="display:flex;justify-content:center;align-items:center;">
            <img id="img" class="item-image" style="width:200px;height:200px;"/>
        </div>
        <div>
            <p style="font-size: 15px;font-weight: bold;">${model.salsesModel}</p>
            <p style="font-size: 15px;font-weight: bold;">Engine: ${model.engine}</p>
            <p style="font-size: 15px;font-weight: bold;">Rated power: ${model.ratedPower}</p>
            <p style="font-size: 15px;font-weight: bold;">Bucket capacity: ${model.bucketCapacity}</p>
            <p style="font-size: 15px;font-weight: bold;">Operating mass: ${model.operatingMass}</p><br>
            <p id="totalPrice" style="font-size: 30px;font-weight: bold;"></p><br>
        </div>
        <div id="output"></div>
    </div>
    <div id="south" title="<spring:message code="page.overseasalesCustomizationClientDetail.name10" />"
         region="south" showSplitIcon="true" height="300" expanded="false">
        <textarea id="specialNeedsDesc" class="mini-textarea messageInput" style="width:100%;height:100%;" onblur="onblur"></textarea>
        <div id="specialNeedsDescGrid" class="mini-datagrid" style="height: 85%" allowResize="true" allowCellWrap="false"
             showCellTip="true" autoload="true" idField="id" multiSelect="false" showPager="false"
             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowAlternating="true">
            <div property="columns">
                <div field="currencySymbol" width="150" headerAlign="center" align="center">
                    <spring:message code="page.overseasalesCustomizationClientDetail.name16"/>
                    <input property="editor" class="mini-textbox" style="width:98%;"/>
                </div>
            </div>
        </div>
    </div>
</div>
<%--JS--%>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var action = "${action}";
    var businessId = "${businessId}";
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentUserGroupId = "${currentUserGroupId}";
    var nodeTypeList = '${nodeTypeList}';
    var currentTime = new Date().format("yyyy-MM-dd hh:mm:ss");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var model = '${model}';
    var instId = "";
    var totalPrice = 0;
    var systemTree = mini.get("systemTree");
    var allNodeIdToNode = new Map();
    var nodesToReport = [];
    var specialNeedsDescGrid = mini.get("specialNeedsDescGrid");
    var currencySymbol = "";
    //..节点集合初始化
    function allNodeIdToNodeIni(json) {
        for (var i = 0, l = json.length; i < l; i++) {
            if (json[i].isChecked == "1") {
                json[i].isChecked = true;
            } else if (json[i].isChecked == "0") {
                json[i].isChecked = false;
            }
        }
        systemTree.loadList(json, "id", "PARENT_ID_");
        for (var node of json) {
            allNodeIdToNode.set(node.id, node);
        }
    }
    //..处理不同类型节点的有效性
    function allNodesEffectivenessProcess(map) {
        for (var [key, value] of map) {
            if (value.nodeType == "basicJieDian") {
                systemTree.disableNode(value);
            }
        }
    }
    //..递归处理面板明细,计价
    function outputListProcess(node) {
        if (node.nodeType == 'root' && node.salesPrice) {//根节点只计价
            debugger;
            totalPrice += parseFloat(node.salesPrice);
        } else if (node.nodeType == 'basic' || node.nodeType == 'customXuJieDian') {
            //基础包和选配虚节点输出黑体文字到面板，计价，压入出报告数据map
            var outputDiv = document.getElementById('output');
            var outputText = `<p style="font-size: 20px;font-weight: bold;">-` + node.nodeName + `</p>`;
            outputDiv.innerHTML += outputText;
            if (node.salesPrice) {
                totalPrice += parseFloat(node.salesPrice);
            }
            nodesToReport.push(node);
        } else if ((node.nodeType == 'basicJieDian' || node.nodeType == "customJieDian") && node.checked) {
            //基础和选节点如果是选中状态，输出文字到面板，计价，压入出报告数据map
            var outputDiv = document.getElementById('output');
            var outputText = `<p style="font-size: 10px;">--` + node.nodeName + `</p>`;
            outputDiv.innerHTML += outputText;
            if (node.salesPrice) {
                totalPrice += parseFloat(node.salesPrice);
            }
            nodesToReport.push(node);
        }
        if (systemTree.isLeaf(node)) {
            return;
        }
        //递归处理子节点
        var ChildNodes = systemTree.getChildNodes(node);
        for (var child of ChildNodes) {
            outputListProcess(child);
        }
    }
    //..处理特殊需求到面板,暂时不用,只压入出报告数据map
    function outputSpecialNeedsDescProcess() {
//        var outputDiv = document.getElementById('output');
//        var outputText = `<p style="font-size: 20px;font-weight: bold;">SPECIAL REQUIREMENT</p>`;
//        outputDiv.innerHTML += outputText;
//        outputText = `<p style="font-size: 10px;">--` + mini.get("specialNeedsDesc").getValue() + `</p>`;
//        outputDiv.innerHTML += outputText;
        node1 = {};
        node1.nodeType = "specialNeedsDescTitle";
        node1.nodeName = "SPECIAL REQUIREMENT"
        nodesToReport.push(node1);
        node2 = {};
        node2.nodeType = "specialNeedsDesc";
        node2.nodeName = mini.get("specialNeedsDesc").getValue();
        nodesToReport.push(node2);

    }
    //..面板显示处理
    function outputProcess() {
        totalPrice = 0;
        document.getElementById('output').innerHTML = "";
        nodesToReport.clear();
        outputListProcess(systemTree.getRootNode());
        outputSpecialNeedsDescProcess();
        document.getElementById('totalPrice').innerText = "Total price: " + currencySymbol + " " + totalPrice.toLocaleString();
    }
    //..
    function onblur() {
        outputProcess();
    }
    //..
    function onnodecheck(e) {
        var currentNode = e.node;
        if (e.checked == false) {
            oneNodeCheckedProcess(currentNode);
        } else if (e.checked == true) {
            onNodeUnCheckedProcess(currentNode);
        }
        outputProcess();
    }
    //..处理某节点的选中
    function oneNodeCheckedProcess(node) {
        debugger;
        if (node.combinNodeIds) {//首先将某节点的所有的绑定节点选中
            var combinNodeIds = node.combinNodeIds.split(",");
            for (var combinNodeId of combinNodeIds) {
                var combinNode = allNodeIdToNode.get(combinNodeId);
                systemTree.checkNode(combinNode);
            }
        }
        if (node.excluNodeIds) {
            var excluNodeIds = node.excluNodeIds.split(",");
            var excluNodesAlreadyUnchecked = [];
            for (var excluNodeId of excluNodeIds) {
                var excluNode = allNodeIdToNode.get(excluNodeId);
                systemTree.uncheckNode(excluNode);//对于某节点的任何一个互斥节点，先反选
                //..todo:理论上所有反选的地方都要加控制
                excluNodesAlreadyUnchecked.add(excluNode);//记录某节点的互斥节点的反选历史
                var parentNode = systemTree.getParentNode(excluNode);
                var allBrotherNodes = systemTree.getChildNodes(parentNode);
                var allBrotherNodesNotChecked = true;
                for (var brotherNode of allBrotherNodes) {
                    if (brotherNode.checked && brotherNode.isRequired == "1") {//todo：放在这里判断同类项
                        allBrotherNodesNotChecked = false;
                        break;//反选是合规的
                    }
                }
                var isAllBrothersNotRequired = true;//todo:还要排除都是非必选的情况
                for (var brotherNode of allBrotherNodes) {
                    if (brotherNode.isRequired == "1") {
                        isAllBrothersNotRequired = false;
                        break;
                    }
                }
                if (allBrotherNodesNotChecked && !isAllBrothersNotRequired) {//反选是不合规的
                    mini.alert(overseasalesCustomizationClientDetail_name11);
                    for (var excluNodeAlreadyUnchecked of excluNodesAlreadyUnchecked) {//将某节点的所有互斥节点的反选历史重新选择回去
                        systemTree.checkNode(excluNodeAlreadyUnchecked);
                    }
                    systemTree.uncheckNode(node);//将某节点本身反选回去
                    if (node.combinNodeIds) {//将某节点的所有的绑定节点反选回去
                        var combinNodeIds = node.combinNodeIds.split(",");
                        for (var combinNodeId of combinNodeIds) {
                            var combinNode = allNodeIdToNode.get(combinNodeId);
                            systemTree.uncheckNode(combinNode);
                        }
                    }
                    return;
                }
                //....................................
            }
        }
    }
    //..处理某节点的取消选中
    function onNodeUnCheckedProcess(node) {
        debugger;
        if (node.isRequired == '1') {//todo：只有必选节点才进行同类项判断
            var parentNode = systemTree.getParentNode(node);
            var allBrotherNodes = systemTree.getChildNodes(parentNode);
            var allBrotherNodesNotChecked = true;
            for (var brotherNode of allBrotherNodes) {
                if (brotherNode.checked && brotherNode.isRequired == "1") {//todo：放在这里判断同类项
                    allBrotherNodesNotChecked = false;
                    break;//反选是合规的
                }
            }
            //todo:还要排除都是非必选的情况?不需要!因为：//todo：只有必选节点才进行同类项判断
            if (allBrotherNodesNotChecked) {//反选是不合规的
                mini.alert(overseasalesCustomizationClientDetail_name11);
                systemTree.checkNode(node);//将某节点本身选中回去
                return;
            }
        }
        if (node.combinNodeIds) {
            var combinNodeIds = node.combinNodeIds.split(",");
            var combinNodesAlreadyUnchecked = [];
            for (var combinNodeId of combinNodeIds) {
                var combinNode = allNodeIdToNode.get(combinNodeId);
                systemTree.uncheckNode(combinNode);//对于某节点的任何一个绑定节点，先反选
                //..todo:理论上所有反选的地方都要加控制
                combinNodesAlreadyUnchecked.add(combinNode);//记录某节点的绑定节点的反选历史
                var parentNode = systemTree.getParentNode(combinNode);
                var allBrotherNodes = systemTree.getChildNodes(parentNode);
                var allBrotherNodesNotChecked = true;
                for (var brotherNode of allBrotherNodes) {
                    if (brotherNode.checked && brotherNode.isRequired == "1") {//todo：放在这里判断同类项
                        allBrotherNodesNotChecked = false;
                        break;//反选是合规的
                    }
                }
                var isAllBrothersNotRequired = true;//todo:还要排除都是非必选的情况
                for (var brotherNode of allBrotherNodes) {
                    if (brotherNode.isRequired == "1") {
                        isAllBrothersNotRequired = false;
                        break;
                    }
                }
                if (allBrotherNodesNotChecked && !isAllBrothersNotRequired) {//反选是不合规的
                    mini.alert(overseasalesCustomizationClientDetail_name11);
                    for (var combinNodeAlreadyUnchecked of combinNodesAlreadyUnchecked) {//将某节点的所有绑定节点的反选历史重新选择回去
                        systemTree.checkNode(combinNodeAlreadyUnchecked);
                    }
                    systemTree.checkNode(node);//将某节点本身选择回去
                    //相比选中处理少了一个过程，就是所有绑定节点选中的反选操作
                    return;
                }
                //....................................
            }
        }
    }
    //..
    $(function () {
        if (!currentUserGroupId) {
            window.close();
        }
        if (action == "config") {
            mini.get("doCreateReport").setVisible(false);
            mini.get("doCreateReport2").setVisible(false);
            mini.get("specialNeedsDesc").setVisible(false);
        } else if (action == "overview") {
            mini.get("onSaveInst").setVisible(false);
            mini.get("specialNeedsDescGrid").setVisible(false);
        }
        $.ajax({
            url: "${ctxPath}/world/core/overseaSalesCustomizationClient/checkAndIniInst.do?" +
            "modelId=${businessId}&clientGroupId=${currentUserGroupId}",
            method: 'get',
            success: function (json) {
                instId = json.data.id;
                var specialNeedsDescItems = JSON.parse(json.data.specialNeedsDesc);
                specialNeedsDescGrid.setData(specialNeedsDescItems);
                currencySymbol = specialNeedsDescItems[0].currencySymbol;
                $.ajax({
                    url: "${ctxPath}/world/core/overseaSalesCustomizationClient/clientTreeQuery.do?" +
                    "modelId=${businessId}&clientGroupId=${currentUserGroupId}",
                    method: 'get',
                    success: function (json) {
                        allNodeIdToNodeIni(json);
                        allNodesEffectivenessProcess(allNodeIdToNode);
                        outputProcess();
                    }
                });
                var imgElement = document.getElementById("img");
                imgElement.src = "${ctxPath}/world/core/overseaSalesCustomizationClient/imageView.do?fileId=" +
                    json.data.modelId + "&fileName=${model.fileName}";
            }
        });
    });
    //..
    function onSaveInst() {
        var changes = systemTree.getChanges();
        var postData = {instId: instId, configinsts: changes};
        if (action == "config") {
            var specialNeedsDescItems = specialNeedsDescGrid.getData();
            if (specialNeedsDescItems.length > 0) {
                postData.specialNeedsDesc = specialNeedsDescItems;
            }
        }
        _SubmitJson({
            url: jsUseCtxPath + "/world/core/overseaSalesCustomizationClient/saveClientInst.do",
            method: 'POST',
            data: postData,
            postJson: true,
            showMsg: false,
            success: function (returnData) {
                if (returnData.success) {
                    mini.alert(returnData.message);
                    refreshSystemTree();
                } else {
                    mini.alert(overseasalesCustomizationClientDetail_name12 + returnData.message);
                }
            },
            fail: function (returnData) {
                mini.alert(overseasalesCustomizationClientDetail_name12 + returnData.message);
            }
        });
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
        $.ajax({
            url: "${ctxPath}/world/core/overseaSalesCustomizationClient/checkAndIniInst.do?" +
            "modelId=${businessId}&clientGroupId=${currentUserGroupId}",
            method: 'get',
            success: function (json) {
                instId = json.data.id;
                var specialNeedsDescItems = JSON.parse(json.data.specialNeedsDesc);
                specialNeedsDescGrid.setData(specialNeedsDescItems);
                currencySymbol = specialNeedsDescItems[0].currencySymbol;
                $.ajax({
                    url: "${ctxPath}/world/core/overseaSalesCustomizationClient/clientTreeQuery.do?" +
                    "modelId=${businessId}&clientGroupId=${currentUserGroupId}",
                    method: 'get',
                    success: function (json) {
                        allNodeIdToNodeIni(json);
                        allNodesEffectivenessProcess(allNodeIdToNode);
                        outputProcess();
                    }
                });
            }
        });
    }
    //..
    function oncellbeginedit(e) {
        var record = e.record, field = e.field;
        if (action == "overview" || record.nodeType == "basic" || record.nodeType == "basicXuJieDian" || record.nodeType == "basicJieDian"
            || record.nodeType == "customXuJieDian" || record.nodeType == "customXuJieDian2") {
            e.cancel = true;
        }
    }
    //..不能选择骨架节点
    function onbeforenodecheck(e) {
        if (e.node.nodeType == "basic" || e.node.nodeType == "basicXuJieDian" || e.node.nodeType == "customXuJieDian") {
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
        if (record.id && record.isHasFile == "true") {
            cellHtml = '<span title="Show detail" style="color:#409EFF;cursor: pointer;"' +
                'onclick="openFileWindow(\'' + record.id + '\',\'' +
                'overseaSalesCustomizationConfigNodeFile' + '\',\'detail\',\'' + coverContent + '\')">Show detail</span>';
        }
        return cellHtml;
    }
    //..
    function openFileWindow(businessId, businessType, action, coverContent) {
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
    //..生成报告
    function doCreateReport() {
        var postData = {
            "allNodeIdToNode": Array.from(allNodeIdToNode.values()),
            "nodesToReport": nodesToReport,
            "totalPrice": totalPrice,
            "model": model,
            "instId": instId,
            "currentUserGroupId": currentUserGroupId,
            "type": "excel"
        }
        mini.confirm(overseasalesCustomizationClientDetail_name13,
            overseasalesCustomizationClientDetail_name14, function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/world/core/overseaSalesCustomizationClient/doCreateReport.do",
                        method: 'POST',
                        data: postData,
                        postJson: true,
                        showMsg: false,
                        success: function (returnData) {
                            if (returnData.success) {
                                var downLoadUrl = '/world/core/overseaSalesCustomization/pdfPreview.do';
                                downLoadFile("${model.salsesModel}" + ".xlsx", "${model.salsesModel}", instId,
                                    "overseaSalesCustomizationClientReport", downLoadUrl);
                            } else {
                                mini.alert(overseasalesCustomizationClientDetail_name15 + returnData.message);
                            }
                        },
                        fail: function (returnData) {
                            mini.alert(overseasalesCustomizationClientDetail_name15 + returnData.message);
                        }
                    });
                }
            });
    }
    function doCreateReport2() {
        var postData = {
            "allNodeIdToNode": Array.from(allNodeIdToNode.values()),
            "nodesToReport": nodesToReport,
            "totalPrice": totalPrice,
            "model": model,
            "instId": instId,
            "currentUserGroupId": currentUserGroupId,
            "type": "pdf"
        }
        mini.confirm(overseasalesCustomizationClientDetail_name13,
            overseasalesCustomizationClientDetail_name14, function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/world/core/overseaSalesCustomizationClient/doCreateReport.do",
                        method: 'POST',
                        data: postData,
                        postJson: true,
                        showMsg: false,
                        success: function (returnData) {
                            if (returnData.success) {
                                var downLoadUrl = '/world/core/overseaSalesCustomization/pdfPreview.do';
                                downLoadFile("${model.salsesModel}" + ".pdf", "${model.salsesModel}", instId,
                                    "overseaSalesCustomizationClientReport", downLoadUrl);
                            } else {
                                mini.alert(overseasalesCustomizationClientDetail_name15 + returnData.message);
                            }
                        },
                        fail: function (returnData) {
                            mini.alert(overseasalesCustomizationClientDetail_name15 + returnData.message);
                        }
                    });
                }
            });
    }
    //..
    function downLoadFile(fileName, fileId, businessId, businessType, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "businessId");
        inputFormId.attr("value", businessId);
        var inputBusinessType = $("<input>");
        inputBusinessType.attr("type", "hidden");
        inputBusinessType.attr("name", "businessType");
        inputBusinessType.attr("value", businessType);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.append(inputBusinessType);
        form.submit();
        form.remove();
    }
</script>
</body>
</html>