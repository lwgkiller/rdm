
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/list.jsp" %>
    <title>仿真能力展板</title>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts-5.1.2/dist/echarts.min.js"></script>
    <style type="text/css">
        .mini-layout-border>#center{
            background: transparent;
        }
    </style>
</head>
<body>
    <div id="layout1" class="mini-layout" style="width:100%;height:100%;">
        <div
                title="仿真对象"
                region="west"
                width="300"
                showSplitIcon="true"
                showCollapseButton="false"
                showProxy="false"
                class="layout-border-r"
        >
            <div id="treeToolBar" class="treeToolBar">
                <a class="mini-button"   plain="true" onclick="addFzdx()">新增子节点</a>
                <a class="mini-button" plain="true" onclick="editFzdx()">编辑子节点</a>
                <a class="mini-button" plain="true" onclick="editFzfxx()">编辑仿真项</a>
                <a class="mini-button btn-red" plain="true" onclick="deleteFzdx()">删除节点</a>
            </div>
            <div class="mini-fit">
                <ul
                        id="fzdxTree"
                        class="mini-tree"
                        url="${ctxPath}/fzsj/core/fzdx/getFzdxTree.do"
                        style="width:100%;height: 100%;"
                        showTreeIcon="true"
                        textField="fzdx"
                        idField="id"
                        resultAsTree="false"
                        parentField="pid"
                        expandOnLoad="true"
                >
                </ul>
            </div>
        </div>
        <div>
            <div>
                <div id="fznlChart" style="width: 100%; height: 1000px;"></div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var jsUseCtxPath = "${ctxPath}";
        var fznlChart = echarts.init(document.getElementById('fznlChart'));
        var fzdxTree=mini.get('fzdxTree');
        var fzsjgly = "${fzsjgly}";
        var option = {
            title: {text: '仿真能力展板（右键查看/编辑仿真分析项）'},
            tooltip: {
                trigger:'item',
                triggerOn:'mousemove',
                formatter: function (param) {
                    var str = '';
                    for(var i=1; i < param.treeAncestors.length; i++) {
                        var name = param.treeAncestors[i].name;
                        if (i == (param.treeAncestors.length -1)) {
                            str += name;
                        } else {
                            str += name + '.';
                        }
                    }

                    return str;
                }
            },
            series:[
                {
                    type: 'tree',
                    id: 0,
                    name: 'fznl',
                    data: [],
                    top: '10%',
                    left: '8%',
                    bottom: '22%',
                    right: '20%',
                    symbolSize: 7,
                    edgeShape: 'polyline',
                    edgeForkPosition: '63%',
                    initialTreeDepth: 3,
                    lineStyle:{width: 2},
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
                    emphasis:{
                        focus: 'descendant'
                    },
                    roam: true,//鼠标滚轮缩放
                    expandAndCollapse: true,
                    animationDuration: 550,
                    animationDurationUpdate: 750,
                }
            ]
        };
        $(function () {
            if (fzsjgly =='false') {
                $("#treeToolBar").hide();
            }
            $("#fznlChart").bind("contextmenu",function(){ return false});
            initChart();
            fznlChart.on("contextmenu",openFzfxxWindow);
        })
        function initChart() {
            $.ajax({
                url:jsUseCtxPath + "/fzsj/core/fzdx/getFzdxEchartsData.do",
                method:'get',
                async: false,
                success:function (res) {
                    option.series[0].data = res;
                    fznlChart.setOption(option);
                }
            });
        }
        function openFzfxxWindow(param) {
            mini.open({
                title: '仿真分析项',
                url: jsUseCtxPath + "/fzsj/core/fzdx/openFzfxxWindow.do?fzdxId="+param.value+"&fzsjgly="+fzsjgly,
                width: 1200,
                height: 600,
                onload: function () {
                },
                ondestroy: function (action) {
                    fzdxTree.load();
                    initChart();
                }
            });
        }
        function addFzdx() {
            var node = fzdxTree.getSelectedNode();
            if(!node) {
                mini.alert("请选择节点");
                return;
            }
            openFzdxEditPage('add');
        }
        function editFzdx() {
            var node = fzdxTree.getSelectedNode();
            if(!node) {
                mini.alert("请选择节点");
                return;
            }
            openFzdxEditPage('edit');
        }
        function openFzdxEditPage(action) {
            var pid = '';
            var path = '';
            var node = fzdxTree.getSelectedNode();
            if (node) {
                pid = node.id;
                path = node.path;
            }
            var url = jsUseCtxPath + "/fzsj/core/fzdx/openfzdxEditPage.do?pid=" + pid  + "&path=" + path +'&action=' + action;
            var title = '新增仿真对象';
            if (action == 'edit') {
                url += '&fzdxId=' + node.id;
                title='编辑仿真对象';
            }
            mini.open({
                title: title,
                url: url,
                width: 800,
                height: 350,
                onload: function () {
                },
                ondestroy: function (action) {
                    fzdxTree.load();
                    initChart();
                }
            });
        }
        function refreshFzdxTree(){
            fzdxTree.load();
            initChart();
        }
        function deleteFzdx() {
            var node = fzdxTree.getSelectedNode();
            if(!node) {
                mini.alert("请选择节点");
                return;
            }
            if (node.fzdx == '整机') {
                mini.alert("根点不允许删除");
                return;
            }
            mini.confirm("确定删除选中记录？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {
                    _SubmitJson({
                        url: jsUseCtxPath + "/fzsj/core/fzdx/deleteFzdx.do",
                        method: 'POST',
                        data: {path: node.path},
                        showMsg:false,
                        success: function (data) {
                            if (data) {
                                if(data.success) {
                                    mini.alert('操作成功',"提示信息",function () {
                                        fzdxTree.load();
                                        initChart();
                                    });
                                }

                            }
                        }
                    });
                }
            });
        }
        function editFzfxx() {
            var node = fzdxTree.getSelectedNode();
            if(!node) {
                mini.alert("请选择节点");
                return;
            }
            var fzdxId = node.id;
            var fzsjgly = 'true';
            mini.open({
                title: '仿真分析项',
                url: jsUseCtxPath + "/fzsj/core/fzdx/openFzfxxWindow.do?fzdxId="+fzdxId+"&fzsjgly="+fzsjgly,
                width: 1200,
                height: 600,
                onload: function () {
                },
                ondestroy: function (action) {
                    fzdxTree.load();
                    initChart();
                }
            });
        }
    </script>
</body>
</html>
