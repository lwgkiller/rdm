<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>风险验证总项目</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/drbfm/totalDecompose.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        .mini-layout-border > #center {
            background: transparent;
        }

        .mini-tree .mini-grid-viewport {
            background: #fff;
        }

        .icon-xia-add,
        .icon-shang-add,
        .icon-brush {
            color: #0daaf6;
        }

        .icon-addressbook,
        .icon-jia {
            color: #ff8b00;
        }

        .icon-baocun7 {
            color: #2cca4e;
        }

        .icon-trash,
        .icon-offline {
            color: red;
        }

        .icon-quanxian2 {
            color: #66b1ff;
        }

        .icon-button:before {
            font-size: 20px;
        }
    </style>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveTotalApply" class="mini-button" onclick="saveTotalApply()">保存</a>
        <a id="commitAndCreateSingle" class="mini-button" onclick="commitAndCreateSingle()">提交创建部件分析项目</a>
        <a id="copyTotalApply" class="mini-button" onclick="copyTotalApply()">复制</a>
        <%--<a id="copyAndCreateSingle" class="mini-button" onclick="copyAndCreateSingle()">复制总项及分析项目</a>--%>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div id="loading" class="loading" style="display:none;text-align:center;"><img
        src="${ctxPath}/styles/images/loading.gif"></div>
<div class="mini-fit" id="content">
    <div class="form-container" style="margin:0 auto; height:98%;">
        <p style="font-size: 16px;font-weight: bold;margin-top: 5px">基本信息</p>
        <hr>
        <form id="totalBaseInfoForm" method="post" style="height: 50px;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 10%">设计型号<span style="color:red">*</span></td>
                    <td style="width: 250px">
                        <input id="jixing" name="jixing" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 10%">风险分析总项目名称<span style="color:red">*</span></td>
                    <td style="width: 300px">
                        <input id="analyseName" name="analyseName" class="mini-textbox" style="width:98%;"/>
                    </td>

                    <td style="text-align: center;width: 8%">关联的科技项目：</td>
                    <td>
                        <input id="projectId" name="projectId" class="mini-combobox" style="width:98%;"
                               textField="projectName" valueField="projectId" emptyText="请选择..."
                               required="false" allowInput="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 10%">FMEA类型<span style="color:red">*</span></td>
                    <td>
                        <input id="femaType" name="femaType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="请选择FMEA类型..."
                               required="false" allowInput="false"
                               multiSelect="false"
                               data="[{'key' : 'product','value' : '产品FMEA'}
                                       ,{'key' : 'base','value' : '基础FMEA'}
                                       ]"
                        />
                    <td style="text-align: center;width: 10%">所属部门：<span style="color:red">*</span></td>
                    <td style="width: 14%;min-width:170px">
                        <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px"
                               allowinput="false" textname="department" length="200" maxlength="200" minlen="0"
                               single="true" initlogindep="false"/>
                    </td>
                    </td>
                </tr>
            </table>
        </form>
        <p style="font-size: 16px;font-weight: bold;margin-top: 5px">部件分解</p>
        <hr>
        <li class="mini-toolbar">
            <ul class="toolBtnBox">
                <li>
                    <p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">部件名称:</p>
                    <input id="fileNameFilter" class="mini-textbox" style="width:200px" onenter="onKeyEnter"/>
                    <a class="mini-button" onclick="queryNodeByName()">查找</a>
                    <a class="mini-button" onclick="expandGridAll()">展开所有</a>
                    <a class="mini-button" onclick="collapseGridAll()">折叠所有</a>
                    <a class="mini-button" onclick="expandGrid()">展开选中</a>
                    <a class="mini-button" onclick="collapseGrid()">折叠选中</a>
                </li>
                <li>
                    <%--<a class="mini-button"   onclick="addRootRow()">新增根节点</a>--%>
                    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
                        （注意：新增/编辑节点后需要点击页面上方“保存”；删除节点在确认后立即生效）
                    </p>
                </li>
            </ul>
        </li>

        <div class="mini-fit " style="height:100%;">
            <div
                    id="groupGrid"
                    class="mini-treegrid"
                    style="width:100%;height:100%;"
                    showTreeIcon="true"
                    treeColumn="structName" idField="id" parentField="parentId"
                    resultAsTree="false"
                    allowResize="true" allowAlternating="false"
                    allowRowSelect="true"
                    allowCellValid="true"
                    allowCellEdit="true" allowCellSelect="true"
                    autoload="true" allowCellWrap="true"
                    url="${ctxPath}/drbfm/total/listStruct.do?belongTotalId=${id}"
                    expandOnLoad="false"
                    oncellvalidation="onCellValidationTotal"
                    onbeforeload="onBeforeGridTreeLoadTotal"
            >
                <div property="columns">
                    <div name="parentId" field="parentId" visible="false"></div>
                    <div name="csfxpg" field="csfxpg" visible="false"></div>
                    <div name="chuangxinxingLevel" field="chuangxinxingLevel" visible="false"></div>
                    <div name="yanzhongxingLevel" field="yanzhongxingLevel" visible="false"></div>
                    <div name="action" cellCls="actionIcons" width="100" renderer="onActionRenderer"
                         cellStyle="padding:0;" align="center" headerAlign="center">操作
                    </div>
                    <div name="sn" field="sn" align="center" width="70" headerAlign="center">序号
                        <input property="editor" changeOnMousewheel="false" class="mini-spinner" minValue="1"
                               maxValue="100000" required="true"/>
                    </div>
                    <div name="structName" field="structName" align="left" width="380" headerAlign="center">部件名称<span
                            style="color:red">*</span>
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;"
                               required="true"/>
                    </div>
                    <div name="structNumber" field="structNumber" align="left" width="150" headerAlign="center">
                        部件代号<span style="color:red">*</span>
                        <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;"
                               required="true"/>
                    </div>
                    <%--<div name="judgeNeedAnalyse" field="judgeNeedAnalyse" displayField="judgeNeedAnalyse" align="center" width="120" headerAlign="center">是否需要/参与风险分析--%>
                    <%--<input property="editor" class="mini-combobox" style="width:100%;" textField="judgeNeedAnalyse" valueField="judgeNeedAnalyse" value="是"--%>
                    <%--allowInput="false" data="[{'judgeNeedAnalyse':'是'},{'judgeNeedAnalyse':'否'}]"/>--%>
                    <%--</div>--%>
                    <div name="judgeNeedAnalyse" field="judgeNeedAnalyse" displayField="judgeNeedAnalyse" align="center"
                         width="120" headerAlign="center">是否需要/参与风险分析
                        <input property="editor" id="judgeNeedAnalyse" style="width:98%;" class="mini-buttonedit"
                               showClose="true"
                               oncloseclick="onYanzhongduCloseClick()"
                               name="judgeNeedAnalyse" textname="judgeNeedAnalyse" allowInput="false"
                               onbuttonclick="judgeNeedAnalyseButton()"/>
                    </div>


                    <div field="analyseUserId" name="analyseUserId" align="center" width="110"
                         displayField="analyseUserName" headerAlign="center">分析责任人
                        <input property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:90%;height:34px;" allowinput="false" length="200" maxlength="200" minlen="0"
                               single="true" initlogindep="false"/>
                    </div>
                    <div field="relSingle" align="center" width="110" headerAlign="center" renderer="relSingleRender">
                        已创建部件分析项目
                    </div>
                    <div field="stageStatus" name="stageStatus" width="60" headerAlign="center" align="center"
                         allowSort="true">流程阶段
                    </div>

                    <%--<div name="structNumber" field="structNumber" align="left" width="50" headerAlign="center">--%>
                    <%--流程阶段<span style="color:red">*</span>--%>
                    <%--<input property="editor" class="mini-textarea" style="width:100%;overflow: auto;"--%>
                    <%--required="true" enabled="false"/>--%>
                    <%--</div>--%>
                    <div field="singleInterfaceCollect" align="center" width="110" headerAlign="center"
                         renderer="singleInterfaceCollectRender">接口需求收集
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>
<div id="singleWindow" title="已创建部件项目" class="mini-window" style="width:800px;height:250px;"
     showModal="true" showFooter="false" allowResize="true" showCloseButton="true">
    <div class="mini-fit">
        <div id="singleListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false">
            <div property="columns">
                <div name="action" cellCls="actionIcons" width="120" headerAlign="center" align="center"
                     renderer="onSingleCheckRenderer" cellStyle="padding:0;">操作
                </div>
                <div field="singleNumber" width="140" headerAlign="center" align="center" allowSort="true">项目流水号</div>
                <div field="analyseUserName" width="70" headerAlign="center" align="center" allowSort="true">部件/接口责任人
                </div>
                <div field="instStatus" width="60" headerAlign="center" align="center" allowSort="true"
                     renderer="onStatusRenderer">流程状态
                </div>
                <div field="currentProcessUser" sortField="currentProcessUser" width="60" align="center"
                     headerAlign="center" allowSort="false">当前处理人
                </div>
                <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程节点</div>
                <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="70" headerAlign="center"
                     dateFormat="yyyy-MM-dd" align="center" allowSort="true">创建时间
                </div>
            </div>
        </div>
    </div>
</div>

<div id="sffxfxWindow" title="是否需要风险分析" class="mini-window" style="width:660px;height:380px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveNeedAnalysis" class="mini-button" onclick="saveNeedAnalysis()">保存</a>
                <a id="closeNeedAnalysis" class="mini-button btn-red" onclick="closeNeedAnalysis()">关闭</a>
            </div>
        </div>
        <input id="singleId" name="singleId" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">

            <tr>
                <td style="text-align: center;width: 20%">设计创新性打分：<span style="color: #ff0000">*</span></td>
                <td style="min-width:170px">
                    <input id="chuangxinxingLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onChuangxinxingCloseClick()"
                           name="chuangxinxingLevel" textname="chuangxinxingLevel" allowInput="false"
                           onbuttonclick="selectChuangxinxingLevel()"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;width: 20%">所担心的质量问题对系统层<br>或整机层影响的严重性打分：<span
                        style="color: #ff0000">*</span></td>
                <td style="min-width:170px">
                    <input id="yanzhongxingLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onYanzhongxingCloseClick()"
                           name="yanzhongxingLevel" textname="yanzhongxingLevel" allowInput="false"
                           onbuttonclick="selectYanzhongxingLevel()"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;width: 20%">初始风险评估：</td>
                <td style="min-width:170px">
                    <input id="csfxpg" style="width:98%;" class="mini-textbox" enabled="false"/>
                </td>
            </tr>


        </table>
    </div>

</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var totalId = "${id}";
    var action = "${action}";
    var commitFlag = "${commitFlag}";
    var isBaseFemaAdmin = "${isBaseFemaAdmin}";
    var totalBaseInfoForm = new mini.Form("#totalBaseInfoForm");
    var groupGrid = mini.get('#groupGrid');

    var singleWindow = mini.get("singleWindow");
    var sffxfxWindow = mini.get("sffxfxWindow");
    var singleListGrid = mini.get("singleListGrid");
    var currentUserId = "${currentUserId}";


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
            var nodes = groupGrid.findNodes(function (node) {
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

    function expandGrid() {
        var selectedNode = groupGrid.getSelectedNode();
        if (selectedNode) {
            groupGrid.expandNode(selectedNode);
        } else {
            mini.alert("请选择节点！");
        }
    }

    function collapseGrid() {
        var selectedNode = groupGrid.getSelectedNode();
        if (selectedNode) {
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

    function relSingleRender(e) {
        var record = e.record;
        var hasSingle = record.hasSingle;
        var s = '';
        if (hasSingle && hasSingle == true) {
            s = '<span style="cursor: pointer;color: #0a7ac6" onclick="openSingleData(\'' + record.id + '\')">查看</span>';
        }
        return s;
    }

    function openSingleData(structId) {
        singleWindow.show();
        //查询
        var url = jsUseCtxPath + "/drbfm/single/querySingleBaseList.do?structIds=" + structId;
        singleListGrid.setUrl(url);
        singleListGrid.load();
    }

    function singleInterfaceCollectRender(e) {
        var record = e.record;
        //此处实际传递为structId
        var id = record.id;
        var s = '';
        s = '<span style="cursor: pointer;color: #0a7ac6" onclick="addSingleInterfaceCollectFlow(\'' + id + '\')">需求收集</span>';
        return s;
    }

    function getSingleData(structId) {
        var rows = [];
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/querySingleBaseList.do?structIds=" + structId,
            method: 'GET',
            async: false,
            success: function (data) {
                rows.push(data.data[0]);
            }
        });
        var id = '';
        if (!rows[0]) {
            return id;
        }
        for (var i = 0; i < rows.length; i++) {
            if (rows[i].instStatus == "SUCCESS_END") {
                return 'end';
            } else if (rows[i].instStatus != "DISCARD_END") {
                id = rows[i].id;
            }
        }
        return id;
    }

    //用户组操作列表
    function onActionRenderer(e) {
        var record = e.record;
        var uid = record._uid;
        var parentId = record.parentId;
        var s = '';
        if (action == 'detail') {
            return s;
        }
        if (!parentId || parentId != '0') {
            s += '<span class="icon-button icon-shang-add" title="在前新增项" onclick="newBeforeRow(\'' + uid + '\')"></span>';
            s += '<span class="icon-button icon-xia-add" title="在后新增项" onclick="newAfterRow(\'' + uid + '\')"></span>';
        }
        s += '<span class="icon-button icon-jia" title="新增子项" onclick="newGroupSubRow()"></span>';
        if (!parentId || parentId != '0') {
            s += '<span class="icon-button icon-trash" title="删除" onclick="delGroupRow(\'' + uid + '\')"></span>';
        }
        return s;
    }

    function onSingleCheckRenderer(e) {
        //todo 改编辑  需要传是否能编辑/ 分析责任人是否是当前人,是否是基础fema管理员
        debugger;
        var selectedNode = groupGrid.getSelectedNode();
        var analyseUserId = selectedNode.analyseUserId;
        var record = e.record;
        var applyId = record.id;
        var s = '<span style="cursor: pointer;color: #0a7ac6" title="查看" onclick="singleDetail(\'' + applyId + '\',\'' + record.status + '\')">查看</span>';


        if (currentUserId == "1" || isBaseFemaAdmin == "true" || currentUserId == analyseUserId) {
            // if (isBaseFemaAdmin=="true"|| currentUserId==analyseUserId) {
            s += '&nbsp;&nbsp;&nbsp;<span style="cursor: pointer;color: #0a7ac6" title="编辑" onclick="singleEdit(\'' + applyId + '\',\'' + record.status + '\')">编辑</span>';
        }
        var currentProcessUserId = record.currentProcessUserId;
        if (currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) {
            s += '&nbsp;&nbsp;&nbsp;<span  style="cursor: pointer;color: #0a7ac6" title="办理" onclick="devTaskDo(\'' + record.taskId + '\')">办理</span>';
        }
        return s;
    }

    //新增根节点
    function addRootRow() {
        if (!totalId) {
            mini.alert("请先点击“保存”按钮进行表单保存！");
            return;
        }
        var treeGrid = groupGrid.getData();
        if (treeGrid && treeGrid[0] && treeGrid[0].parentId == '0') {
            mini.alert("已存在根节点！");
            return;
        }

        var node = groupGrid.getRowByUID("1");
        groupGrid.addNode({judgeNeedAnalyse: '是', judgeIsInterface: '否', sn: 1, parentId: '0'}, "before", node);
        groupGrid.cancelEdit();
        groupGrid.beginEditRow(node);
    }

    //在当前选择行的下添加子记录
    function newGroupSubRow() {
        var node = groupGrid.getSelectedNode();
        var newNode = {judgeNeedAnalyse: '是', judgeIsInterface: '否', sn: 1};
        groupGrid.addNode(newNode, "add", node);
        groupGrid.expandNode(node);
    }

    //在后新增行
    function newAfterRow(row_uid) {
        var node = groupGrid.getRowByUID(row_uid);
        var sn = 1;
        if (node.sn) {
            sn = node.sn + 1;
        }

        groupGrid.addNode({judgeNeedAnalyse: '是', judgeIsInterface: '否', sn: sn}, "after", node);
        groupGrid.cancelEdit();
        groupGrid.beginEditRow(node);
    }

    //在前新增行
    function newBeforeRow(row_uid) {
        var node = groupGrid.getRowByUID(row_uid);
        var sn = 1;
        if (node.sn) {
            sn = node.sn - 1;
            if (sn < 1) {
                sn = 1;
            }
        }
        groupGrid.addNode({judgeNeedAnalyse: '是', judgeIsInterface: '否', sn: sn}, "before", node);
        groupGrid.cancelEdit();
        groupGrid.beginEditRow(node);
    }

    function delGroupRow(row_uid) {
        var row = null;
        if (row_uid) {
            row = groupGrid.getRowByUID(row_uid);
        } else {
            row = groupGrid.getSelected();
        }

        if (!row) {
            mini.alert("请选择删除的数据！");
            return;
        }
        var partName = row.structName;
        if (!confirm("此操作会将" + partName + "所有子节点同步删除，确定继续？（已关联部件分析项目的节点及父节点不允许删除）")) {
            return;
        }

        //未保存的节点直接删除
        if (row && !row.id) {
            groupGrid.removeNode(row);
        } else if (row.id) {
            //后台判断如果在风险分析中已经选择，不允许删除
            _SubmitJson({
                url: __rootPath + "/drbfm/total/delStruct.do?id=" + row.id,
                success: function (returnData) {
                    if (returnData.success) {
                        groupGrid.removeNode(row);
                    }
                }
            });
        }
    }

    function showLoading() {
        $("#loading").css('display', '');
        $("#content").css('display', 'none');
    }

    function hideLoading() {
        $("#loading").css('display', 'none');
        $("#content").css('display', '');
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.instStatus;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '审批完成', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function judgeNeedAnalyseButton() {
        var record = groupGrid.getSelected();
        if (!record.id) {
            mini.alert("请先保存后再进行选择！");
            return;
        }
        mini.get("chuangxinxingLevel").setValue(record.chuangxinxingLevel);
        mini.get("chuangxinxingLevel").setText(record.chuangxinxingLevel);
        mini.get("yanzhongxingLevel").setValue(record.yanzhongxingLevel);
        mini.get("yanzhongxingLevel").setText(record.yanzhongxingLevel);
        mini.get("singleId").setValue(record.id);
        mini.get("csfxpg").setValue(record.csfxpg);


        sffxfxWindow.show();
    }

    function closeNeedAnalysis() {
        mini.get("chuangxinxingLevel").setValue("");
        mini.get("chuangxinxingLevel").setText("");
        mini.get("yanzhongxingLevel").setValue("");
        mini.get("yanzhongxingLevel").setText("");
        mini.get("csfxpg").setValue("");
        sffxfxWindow.hide();
    }

    function selectChuangxinxingLevel() {
        var chuangxinxingLevel = mini.get("chuangxinxingLevel").value;
        mini.open({
            title: "创新性打分",
            url: jsUseCtxPath + "/drbfm/total/needAnalysisCxx.do",
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            showCloseButton: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.score) {
                    mini.get("chuangxinxingLevel").setValue(returnData.score);
                    mini.get("chuangxinxingLevel").setText(returnData.score);
                    scoreChange();
                } else if (!chuangxinxingLevel) {
                    mini.alert("未选择创新性分数！");
                }
            }

        });
    }

    function selectYanzhongxingLevel() {
        var yanzhongxingLevel = mini.get("yanzhongxingLevel").value;
        mini.open({
            title: "严重性打分",
            url: jsUseCtxPath + "/drbfm/total/needAnalysisYzx.do",
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            showCloseButton: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.score) {
                    mini.get("yanzhongxingLevel").setValue(returnData.score);
                    mini.get("yanzhongxingLevel").setText(returnData.score);
                    scoreChange();
                } else if (!yanzhongxingLevel) {
                    mini.alert("未选择创新性分数！");
                }
            }

        });
    }

    function scoreChange() {
        var cxx = mini.get("chuangxinxingLevel").getValue();
        var yzx = mini.get("yanzhongxingLevel").getValue();
        var risk = "";
        if (cxx && yzx) {
            var cxxInt = cxx * 1;
            var yzxInt = yzx * 1;
            var sum = cxxInt + yzxInt;
            if (sum >= 8) {
                risk = "很高";
            } else if (sum > 6) {
                risk = "高";
            } else if (sum == 6 && yzxInt >= 3) {
                risk = "高";
            } else if (sum == 6 && yzxInt < 3) {
                risk = "中";
            } else if (sum == 5) {
                risk = "中";
            } else if (sum == 4) {
                risk = "低";
            } else if (sum == 3 && cxxInt == 2) {
                risk = "低";
            } else {
                risk = "很低";
            }

            mini.get("csfxpg").setValue(risk);
        }
    }

    function saveNeedAnalysis() {
        var singleId = mini.get("singleId").getValue();
        var cxx = mini.get("chuangxinxingLevel").getValue();
        if (!cxx) {
            mini.alert("请选择设计性创新打分！");
            return
        }
        var yzx = mini.get("yanzhongxingLevel").getValue();
        if (!yzx) {
            mini.alert("请选择严重性打分！");
            return
        }
        var csfxpg = mini.get("csfxpg").getValue();

        var data = {
            id: singleId,
            cxx: cxx,
            yzx: yzx,
            csfxpg: csfxpg
        };
        $.ajax({
            url: jsUseCtxPath + "/drbfm/total/saveNeedAnalysis.do",
            type: 'POST',
            contentType: 'application/json',
            data: mini.encode(data),
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            groupGrid.reload();
                            closeNeedAnalysis();
                        }
                    });
                }
            }
        });

    }


</script>
</body>
</html>
