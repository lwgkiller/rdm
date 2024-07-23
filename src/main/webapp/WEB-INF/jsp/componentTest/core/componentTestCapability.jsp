<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/list.jsp" %>
    <title>零部件测试能力展板</title>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
    <style type="text/css">
        .mini-layout-border > #center {
            background: transparent;
        }
    </style>
</head>
<body>
<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
    <div title="测试能力" region="west" width="300" showSplitIcon="true" showCollapseButton="false"
         showProxy="false" class="layout-border-r">
        <div id="treeToolBar" class="treeToolBar">
            <a class="mini-button" plain="true" onclick="addComponentTestCapability()">新增子节点</a>
            <a class="mini-button" plain="true" onclick="editComponentTestCapability()">编辑节点</a>
            <a class="mini-button" plain="true" onclick="editComponentTestCapabilityItem()">编辑测试项</a>
            <a class="mini-button btn-red" plain="true" onclick="deleteComponentTestCapability()">删除节点</a>
        </div>
        <div class="mini-fit">
            <ul id="componentTestCapabilityTree" class="mini-tree"
                url="${ctxPath}/componentTest/core/capability/getComponentTestCapabilityTree.do"
                style="width:100%;height: 100%;" showTreeIcon="true" textField="capability" idField="id" resultAsTree="false"
                parentField="pid" expandOnLoad="true">
            </ul>
        </div>
    </div>
    <div>
        <div>
            <div id="componentTestCapabilityChart" style="width: 100%; height: 1500px;"></div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var componentTestCapabilityChart = echarts.init(document.getElementById('componentTestCapabilityChart'));
    var componentTestCapabilityTree = mini.get('componentTestCapabilityTree');
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    var currentUserNo = "${currentUserNo}";
    var option = {
        title: {text: '零部件测试能力展板（右键查看/编辑测试项次）'},
        tooltip: {
            trigger: 'item',
            triggerOn: 'mousemove',
            formatter: function (param) {
                var str = '';
                for (var i = 1; i < param.treeAncestors.length; i++) {
                    var name = param.treeAncestors[i].name;
                    if (i == (param.treeAncestors.length - 1)) {
                        str += name;
                    } else {
                        str += name + '.';
                    }
                }

                return str;
            }
        },
        series: [
            {
                type: 'tree',
                id: 0,
                name: 'componentTestCapability',
                data: [],
                top: '5%',
                left: '8%',
                bottom: '5%',
                right: '20%',
                symbolSize: 10,
                edgeShape: 'polyline',
                edgeForkPosition: '5%',
                initialTreeDepth: 4,
                lineStyle: {width: 2},
                label: {
                    backgroundColor: '#fff',
                    position: 'left',
                    verticalAlign: 'middle',
                    align: 'right'
                },
                leaves: {
                    label: {
                        position: 'right',
                        verticalAlign: 'middle',
                        align: 'left'
                    }
                },
                emphasis: {
                    focus: 'descendant'
                },
                roam: true,
                expandAndCollapse: true,
                animationDuration: 550,
                animationDurationUpdate: 750,
            }
        ]
    };
    //..
    $(function () {
        if (currentUserNo == "admin") {
            isComponentTestAdmin = 'true';
        }
        if (isComponentTestAdmin == 'false') {
            $("#treeToolBar").hide();
        }
        $("#componentTestCapabilityChart").bind("contextmenu", function () {
            return false
        });
        initChart();
        componentTestCapabilityChart.on("contextmenu", openComponentTestCapabilityItemListPage);
    })
    //..
    function initChart() {
        $.ajax({
            url: jsUseCtxPath + "/componentTest/core/capability/getComponentTestCapabilityChartData.do",
            method: 'get',
            async: false,
            success: function (res) {
                option.series[0].data = res;
                componentTestCapabilityChart.setOption(option);
            }
        });
    }
    //..
    function openComponentTestCapabilityItemListPage(param) {
        mini.open({
            title: '测试能力项次',
            url: jsUseCtxPath + "/componentTest/core/capability/openComponentTestCapabilityItemListPage.do?" +
            "componentTestCapabilityId=" + param.value + "&isComponentTestAdmin=" + isComponentTestAdmin,
            width: 1200,
            height: 600,
            onload: function () {
            },
            ondestroy: function (action) {
                componentTestCapabilityTree.load();
                initChart();
            }
        });
    }
    //..
    function addComponentTestCapability() {
        var node = componentTestCapabilityTree.getSelectedNode();
        if (!node) {
            mini.alert("请选择节点");
            return;
        }
        openComponentTestCapabilityEditPage('add');
    }
    //..
    function editComponentTestCapability() {
        var node = componentTestCapabilityTree.getSelectedNode();
        if (!node) {
            mini.alert("请选择节点");
            return;
        }
        openComponentTestCapabilityEditPage('edit');
    }
    //..
    function openComponentTestCapabilityEditPage(action) {
        var pid = '';
        var path = '';
        var node = componentTestCapabilityTree.getSelectedNode();
        if (node) {
            pid = node.id;
            path = node.path;
        }
        var url = jsUseCtxPath + "/componentTest/core/capability/openComponentTestCapabilityEditPage.do?" +
            "pid=" + pid + "&path=" + path + '&action=' + action;
        var title = '新增测试能力';
        if (action == 'edit') {
            url += '&componentTestCapabilityId=' + node.id;
            title = '编辑测试能力';
        }
        mini.open({
            title: title,
            url: url,
            width: 800,
            height: 350,
            onload: function () {
            },
            ondestroy: function (action) {
                componentTestCapabilityTree.load();
                initChart();
            }
        });
    }
    //..
    function refreshComponentTestCapabilityTree() {
        componentTestCapabilityTree.load();
        initChart();
    }
    //..
    function deleteComponentTestCapability() {
        var node = componentTestCapabilityTree.getSelectedNode();
        if (!node) {
            mini.alert("请选择节点");
            return;
        }
        if (node.capability == '测试能力') {
            mini.alert("根点不允许删除");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/capability/deleteComponentTestCapability.do",
                    method: 'POST',
                    data: {path: node.path},
                    showMsg: false,
                    success: function (data) {
                        if (data) {
                            if (data.success) {
                                mini.alert('操作成功', "提示信息", function () {
                                    componentTestCapabilityTree.load();
                                    initChart();
                                });
                            }

                        }
                    }
                });
            }
        });
    }
    //..
    function editComponentTestCapabilityItem() {
        var node = componentTestCapabilityTree.getSelectedNode();
        if (!node) {
            mini.alert("请选择节点");
            return;
        }
        var componentTestCapabilityId = node.id;
        var isComponentTestAdmin = 'true';
        mini.open({
            title: '测试项次',
            url: jsUseCtxPath + "/componentTest/core/capability/openComponentTestCapabilityItemListPage.do?" +
            "componentTestCapabilityId=" + componentTestCapabilityId + "&isComponentTestAdmin=" + isComponentTestAdmin,
            width: 1200,
            height: 600,
            onload: function () {
            },
            ondestroy: function (action) {
                componentTestCapabilityTree.load();
                initChart();
            }
        });
    }
</script>
</body>
</html>
