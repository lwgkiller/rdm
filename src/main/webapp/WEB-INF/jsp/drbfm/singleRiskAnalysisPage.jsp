<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>失效风险分析</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view {
            background: white !important;
        }

        .mini-grid-cell-inner {
            line-height: 20px !important;
            padding: 0;
        }

        .mini-grid-cell-inner {
            font-size: 14px !important;
        }
    </style>
</head>
<body>
<div id="loading" class="loading" style="display:none;text-align:center;"><img
        src="${ctxPath}/styles/images/loading.gif"></div>
<%--<div class="topToolBar">--%>
<%--    <div>--%>
<%--        &lt;%&ndash;<a class="mini-button" id="relUp" plain="true" style="float: left;display: none"&ndash;%&gt;--%>
<%--        <a class="mini-button" id="addNewSxms" plain="true" style="float: left"--%>
<%--           onclick="addNewSxms()">新增失效起因</a>--%>
<%--        &lt;%&ndash;<a class="mini-button" id="relDown" plain="true" style="float: left;display: none"&ndash;%&gt;--%>
<%--        &lt;%&ndash;<a class="mini-button" id="relDown" plain="true" style="float: left"&ndash;%&gt;--%>
<%--        &lt;%&ndash;onclick="relDown()">向下关联</a>&ndash;%&gt;--%>
<%--        &lt;%&ndash;<span id="tips" style="color: red;float: left;display: none">（注：“流程收集”类型的需求不允许删除）</span>&ndash;%&gt;--%>
<%--    </div>--%>
<%--</div>--%>
<div class="mini-fit" style="width: 100%; height: 85%;" id="content">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 5px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">功能: </span>
                    <input id="functionDesc" name="functionDesc" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">特性要求: </span>
                    <input id="requestDesc" name="requestDesc" class="mini-textbox"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">措施优先级: </span>
                    <%--<input id="csyxj" name="csyxj" class="mini-textbox"/>--%>
                    <input id="csyxj" name="csyxj" class="mini-combobox" style="width:98%;"
                           textField="key" valueField="value"
                           emptyText="请选择..."
                           multiSelect="false"
                           data="[{key:'H',value:'H'}
                             ,{key:'M',value:'M'}
                             ,{key:'L',value:'L'}
                             ]"
                           allowInput="false" showNullItem="true"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">失效模式: </span>
                    <input id="sxms" name="sxms" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:250px;height:34px" popupWidth="400" label="失效模式："
                           length="50"
                           only_read="false" required="true" allowinput="true" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="false" multiSelect="false"
                           textField="sxmsName" valueField="sxmsName" emptyText="请选择..."
                           url="${ctxPath}/drbfm/single/getSingleSxmsListById.do?partId=${singleId}"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">失效起因: </span>
                    <input id="sxqy" name="sxqy" class="mini-textbox"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="loadListGrid()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearListGrid()">清空查询</a>

                </li>
                <li style="margin-left: 10px">
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" id="addNewSxms" plain="true"
                       onclick="addNewSxms()">新增失效起因</a>
                </li>


            </ul>
        </form>
    </div>
    <div id="fxsxfxListGrid" class="mini-datagrid" allowResize="false" style="height: 90%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
    <%--url="${ctxPath}/drbfm/single/getRiskAnalysisList.do?partId=${singleId}"--%>
         allowCellWrap="true" showVGridLines="true">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="80px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="sxmsId" visible="false" headerAlign="center" width="100px" align="center">sxmsId</div>
            <div field="sxyyId" visible="false" headerAlign="center" width="100px" align="center">sxyyId</div>
            <div header="功能分析" headerAlign="center">
                <div property="columns">
                    <div field="functionDesc" name="functionDesc" headerAlign="center" width="100px" align="left">功能
                    </div>
                    <div field="requestDesc" name="requestDesc" headerAlign="center" width="100px" align="left" renderer="requestDescRenderer">特性要求
                    </div>
                </div>
            </div>
            <div header="失效分析" headerAlign="center">
                <div property="columns">
                    <div field="sxyxName" headerAlign="center" width="250px" align="left">失效影响(FE)</div>
                    <div field="yzd" headerAlign="center" width="50px" align="center">严重度</div>
                    <div field="sxmsAllName" headerAlign="center" width="245px" align="left" renderer="sxmsAllNameRenderer">失效模式(FM)</div>
                    <div field="sxyyAllName" headerAlign="center" width="245px" align="left">失效起因(FC)</div>
                </div>
            </div>
            <div header="风险分析" headerAlign="center">
                <div property="columns">
                    <div field="xxyfkz" headerAlign="center" width="180px" align="left">当前预防控制</div>
                    <div field="fsd" headerAlign="center" width="40px" align="center">频度</div>
                    <div field="tckzfc" headerAlign="center" width="140px" align="left">当前探测控制对(FC)</div>
                    <div field="tckzfm" headerAlign="center" width="140px" align="left">当前探测控制对(FM)</div>
                    <div field="tcd" headerAlign="center" width="50px" align="center">探测度</div>
                    <div field="csyxj" headerAlign="center" width="75px" align="center">措施优先级</div>
                </div>
            </div>
        </div>
    </div>

    <div id="sxfxWindow" title="风险分析编辑" class="mini-window" style="width:660px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-fit">
            <div class="topToolBar" style="float: right;">
                <div style="position: relative!important;">
                    <a id="saveRiskAnalysis" class="mini-button" onclick="saveRiskAnalysis()">保存</a>
                    <a id="closeFunction" class="mini-button btn-red" onclick="closeRiskAnalysis()">关闭</a>
                </div>
            </div>
            <input id="sxfxId" name="id" class="mini-hidden"/>
            <input id="sxmsfxId" name="sxmsfxId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
                <tr>
                    <td style="text-align: center;width: 20%">严重度打分：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="yanzhongduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onYanzhongduCloseClick()"
                               name="yanzhongduLevel" textname="yanzhongduLevel" allowInput="false"
                               onbuttonclick="selectYanzhongduLevel()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 15%">当前预防控制(PC)：</td>
                    <td style="width: 85%;">
                        <%--<input id="xxyfkz" name="xxyfkz" class="mini-textbox" style="width:98%;"/>--%>
                        <input id="xxyfkz" name="xxyfkz" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"
                               emptyText=""
                               data="[{key:'参考成熟产品XX',value:'参考成熟产品XX'}
                                       ,{key:'对标XX',value:'对标XX'}
                                       ,{key:'XXCAE分析',value:'XXCAE分析'}
                                       ,{key:'标准XX',value:'标准XX'}
                                       ,{key:'XX设计计算',value:'XX设计计算'}
                                       ,{key:'三维模型校核',value:'三维模型校核'}
                                       ,{key:'防错设计',value:'防错设计'}]"
                               allowInput="true" showNullItem="false"/>

                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">频度打分：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="fashengduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onFashengduCloseClick()"
                               name="fashengduLevel" textname="fashengduLevel" allowInput="false"
                               onbuttonclick="selectFashengduLevel()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">探测度打分(FM)：</td>
                    <td style="min-width:170px">
                        <input id="tanceduLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onTanceduCloseClick()"
                               name="tanceduLevel" textname="tanceduLevel" allowInput="false"
                               onbuttonclick="selectTanceduLevel()"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">当前探测控制针对(FM)：</td>
                    <td style="width: 35%;">
                        <input id="tckzfm" name="tckzfm" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"
                               emptyText=""
                               data="[{key:'XX设计评审',value:'XX设计评审'}
                                       ,{key:'整机测试（XX）',value:'整机测试（XX）'}
                                       ,{key:'整机调试',value:'整机调试'}
                                       ,{key:'台架试验（XX）',value:'台架试验（XX）'}
                                       ,{key:'样机装配验证',value:'样机装配验证'}
                                       ,{key:'维修便利性拆装评价',value:'维修便利性拆装评价'}
                                       ,{key:'其他',value:'其他'}]"
                               allowInput="true" showNullItem="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">探测度打分(FC)：</td>
                    <td style="min-width:170px">
                        <input id="tanceduLevelFc" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onTanceduFcCloseClick()"
                               name="tanceduLevelFc" textname="tanceduLevelFc" allowInput="false"
                               onbuttonclick="selectTanceduLevelFc()"/>
                    </td>
                </tr>

                <%--<tr>--%>
                <%--<td style="width: 15%;">发生度(O)：<span style="color:red">*</span></td>--%>
                <%--<td style="width: 85%;">--%>
                <%--<input id="fsd" name="fsd" class="mini-textbox" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--</tr>--%>
                <%--<tr>--%>
                <%--<td style="width: 15%;">严重度(S)：<span style="color:red">*</span></td>--%>
                <%--<td style="width: 85%;">--%>
                <%--<input id="yzd" name="yzd" class="mini-textbox" style="width:98%;"/>--%>
                <%--</td>--%>
                <%--</tr>--%>
                <tr>
                    <td style="text-align: center;width: 20%">当前探测控制针对(FC)：</td>
                    <td style="width: 35%;">
                        <%--<input id="tckzfc" name="tckzfc" class="mini-textbox" style="width:98%;"/>--%>
                        <input id="tckzfc" name="tckzfc" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"
                               emptyText=""
                               data="[{key:'XX设计评审',value:'XX设计评审'}
                                       ,{key:'整机测试（XX）',value:'整机测试（XX）'}
                                       ,{key:'整机调试',value:'整机调试'}
                                       ,{key:'台架试验（XX）',value:'台架试验（XX）'}
                                       ,{key:'样机装配验证',value:'样机装配验证'}
                                       ,{key:'维修便利性拆装评价',value:'维修便利性拆装评价'}
                                       ,{key:'其他',value:'其他'}]"
                               allowInput="true" showNullItem="false"/>
                    </td>
                </tr>



            </table>
        </div>
    </div>


    <div id="xzsxmsWindow" title="新增失效起因" class="mini-window" style="width:660px;height:380px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-fit">
            <div class="topToolBar" style="float: right;">
                <div style="position: relative!important;">
                    <a id="saveSxms" class="mini-button" onclick="saveSxms()">保存</a>
                    <a id="closeSxms" class="mini-button btn-red" onclick="closeSxms()">关闭</a>
                </div>
            </div>
            <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
                <tr>
                    <td style="width: 15%">失效起因：<span style="color:red">*</span></td>
                    <td style="width: 85%;">
                        <input id="xzsxmsName" name="xzsxmsName" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">选择关联失效模式：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="chooseSxms" style="width:98%;" class="mini-buttonedit" showClose="true"
                        <%--oncloseclick="onYanzhongduCloseClick()"--%>
                               name="chooseSxms" textname="chooseSxms" allowInput="false"
                               onbuttonclick="selectRelSxms()"/>
                    </td>
                </tr>


            </table>
        </div>
    </div>

    <div id="selectSxmsWindow" title="选择失效模式" class="mini-window" style="width:1050px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">失效模式: </span>
            <input class="mini-textbox" style="width: 120px" id="sxmsName" name="sxmsName"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchSelectSxms()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="selectSxmsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 idField="id" showColumnsMenu="false"
                 allowAlternating="true" showPager="false"
            <%--autoload="true"--%>
            <%--url="${ctxPath}/drbfm/single/getModelSxmsList.do?partId=${singleId}&relType=up"--%>
            >
                <%--url="${ctxPath}/drbfm/single/getSxmsList.do?partId=${singleId}&relType=up">--%>
                <div property="columns">
                    <div type="checkcolumn" width="20px"></div>
                    <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                    <div field="sxmsName" headerAlign="center" width="220px" align="center">失效模式
                    </div>
                    <div field="requestDesc" headerAlign="center" width="220px" align="center">特性要求
                    </div>
                    <div field="relDimensionNames" headerAlign="center" width="220px" align="center">所属维度
                    </div>
                    <div field="structName" headerAlign="center" width="220px" align="center">部件名称
                    </div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 40px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectSxmsOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消"
                               onclick="selectSxmsHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

</div>
<script type="text/javascript">
        mini.parse();
        var jsUseCtxPath = "${ctxPath}";
        var singleId = "${singleId}";
        var action = "${action}";
        var relType = "";
        var stageName = "${stageName}";
        var currentUserId = "${currentUserId}";
        var currentUserName = "${currentUserName}";
        var currentTime = "${currentTime}";
        var fileListGrid = mini.get("fileListGrid");
        var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
        var fxsxfxListGrid = mini.get("fxsxfxListGrid");
        var selectSxmsListGrid = mini.get("selectSxmsListGrid");
        var sxmsListGrid = mini.get("sxmsListGrid");
        var sxfxWindow = mini.get("sxfxWindow");
        var xzsxmsWindow = mini.get("xzsxmsWindow");
        var selectSxmsWindow = mini.get("selectSxmsWindow");


        $(function () {
            if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
                mini.get("addNewSxms").setEnabled(true);
            } else {
                mini.get("addNewSxms").setEnabled(false);
            }
            loadListGrid();
        });


        //行功能按钮
        function onActionRenderer(e) {
            var record = e.record;
            var id = record.id;
            var CREATE_BY_ = record.CREATE_BY_;
            var sxmsId = record.sxmsId;
            var sxyyId = record.sxyyId;
            var delFlag = record.delFlag;
            var s = '';
            // detailRequest(\'' + id + '\')
            if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
                s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editDetail(\'' + sxmsId + '\',\'' + sxyyId+ '\',\'' + singleId + '\')">编辑</span>';
            } else {
                s += '<span  title="编辑" style="color: silver" > 编辑 </span>';
            }
            if (action == 'task' && record.delFlag == 'true') {
                s += '<span style="display: inline-block" class="separator"></span>';
                s += '<span  style="color:#2ca9f6;cursor: pointer" title="删除" onclick="delNewSxyy(\'' + sxyyId + '\')">删除</span>';
            }
            return s;
        }

        // function sxmsActionRenderer(e) {
        //     var record = e.record;
        //     var id = record.id;
        //     var CREATE_BY_ = record.CREATE_BY_;
        //     var s = '';
        //     // s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="showNetDetail(\'' + id + '\')">查看并关联</span>';
        //     if (action == 'task') {
        //         // s += '<span style="display: inline-block" class="separator"></span>';
        //         s += '<span  style="color:#2ca9f6;cursor: pointer" title="删除" onclick="deleteSxms(\'' + id + '\')">删除</span>';
        //     } else {
        //         s += '&nbsp;&nbsp;&nbsp;<span  title="删除" style="color: silver" > 删除 </span>';
        //
        //     }
        //     return s;
        // }


        function editDetail(sxmsId, sxyyId,partId) {
            // todo 改url

            if (sxyyId == "undefined") {
                mini.alert("失效起因为空不能编辑！");
                return;
            }
            sxfxWindow.show();

            if (sxyyId) {
                var url = jsUseCtxPath + "/drbfm/single/getRiskAnalysisDetail.do";
                $.ajaxSettings.async = false;
                $.post(
                    url,
                    {sxmsId: sxmsId, sxyyId: sxyyId,partId:partId},
                    function (json) {
                        mini.get("sxfxId").setValue(json.id);
                        mini.get("sxmsfxId").setValue(json.sxmsfxId);
                        mini.get("fashengduLevel").setValue(json.fsd);
                        mini.get("fashengduLevel").setText(json.fsd);
                        mini.get("xxyfkz").setValue(json.xxyfkz);
                        mini.get("tckzfc").setValue(json.tckzfc);
                        mini.get("tanceduLevel").setValue(json.tcdfm);
                        mini.get("tanceduLevel").setText(json.tcdfm);
                        mini.get("tanceduLevelFc").setValue(json.tcdfc);
                        mini.get("tanceduLevelFc").setText(json.tcdfc);
                        mini.get("yanzhongduLevel").setValue(json.yzd);
                        mini.get("yanzhongduLevel").setText(json.yzd);
                        mini.get("tckzfm").setValue(json.tckzfm);

                        // mini.get("riskLevel").setValue(json.riskLevel);
                        // mini.get("riskLevel").setText(json.riskLevel);
                    });
                $.ajaxSettings.async = true;
            }
            // mini.get("interfaceRequestStructId").setEnabled(true);
            // mini.get("relDimensionKeys").setEnabled(true);
            // mini.get("relDeptDemandId").setEnabled(true);
            // mini.get("functionDesc").setEnabled(true);
            mini.get("saveRiskAnalysis").show();
        }

        function saveRiskAnalysis() {
            // todo 改

            var S = mini.get("yanzhongduLevel").getValue();
            if (!S) {
                mini.alert('还未选择严重度分数！');
                return;
            }
            var O = mini.get("fashengduLevel").getValue();
            if (!O) {
                mini.alert('还未选择发生度分数！');
                return;
            }
            var D = mini.get("tanceduLevel").getValue();
            var E = mini.get("tanceduLevelFc").getValue();
            if (!D&&!E) {
                mini.alert('还未选择探测度分数！');
                return;
            }
            if (!D && E) {
                D = E;
            }
            //探测度值要取小的那个 parseInt不行的话用*1
            if (D&&E&&(parseInt(E) < parseInt(D))) {
                D = E;
            }

            var record = fxsxfxListGrid.getSelected();
            var sxmsId = record.sxmsId;
            var sxyyId = record.sxyyId;

            var xxyfkz = mini.get("xxyfkz").getValue();
            var fsd = mini.get("fashengduLevel").getValue();
            var yzd = mini.get("yanzhongduLevel").getValue();
            var tckzfc = mini.get("tckzfc").getValue();
            var tckzfm = mini.get("tckzfm").getValue();
            var tcdfm = mini.get("tanceduLevel").getValue();
            var tcdfc = mini.get("tanceduLevelFc").getValue();
            var tcd = D;
            var id = mini.get("sxfxId").getValue();
            var sxmsfxId = mini.get("sxmsfxId").getValue();
            var csyxj = "";

            //这里加措施优先级


            var H = '高风险';
            var M = '中风险';
            var L = '低风险';
            var returnData = {};
            if (S >= 9 && S <= 10) {
                if (O >= 8 && O <= 10) {
                    csyxj = "H";
                }

                if (O >= 6 && O <= 7) {
                    csyxj = "H";
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 2 && D <= 10) {
                        csyxj = "H";
                    }
                    if (D <= 1) {
                        csyxj = "M";
                    }

                }
                if (O >= 2 && O <= 3) {
                    if (D >= 7 && D <= 10) {
                        csyxj = "H";
                    }
                    if (D >= 5 && D <= 6) {
                        csyxj = "M";
                    }
                    if (D >= 2 && D <= 4) {
                        csyxj = "L";
                    }
                    if (D <= 1) {
                        csyxj = "L";
                    }
                }
                if (O == 1) {
                    csyxj = "L";
                }

            }

            if (S >= 7 && S <= 8) {
                if (O >= 8 && O <= 10) {
                    csyxj = "H";
                }
                if (O >= 6 && O <= 7) {
                    if (D >= 2 && D <= 10) {
                        csyxj = "H";
                    }
                    if (D <= 1) {
                        csyxj = "M";
                    }
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 7 && D <= 10) {
                        csyxj = "H";
                    }
                    if (D >= 1 && D <= 6) {
                        csyxj = "M";
                    }
                }
                if (O >= 2 && O <= 3) {
                    if (D >= 5 && D <= 10) {
                        csyxj = "M";
                    }
                    if (D >= 1 && D <= 4) {
                        csyxj = "L";
                    }
                }
                if (O == 1) {
                    csyxj = "L";
                }
            }

            if (S >= 4 && S <= 6) {
                if (O >= 8 && O <= 10) {
                    if (D >= 5 && D <= 10) {
                        csyxj = "H";
                    }
                    if (D >= 1 && D <= 4) {
                        csyxj = "M";
                    }

                }
                if (O >= 6 && O <= 7) {
                    if (D >= 2 && D <= 10) {
                        csyxj = "M";
                    }
                    if (D <= 1) {
                        csyxj = "L";

                    }
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 7 && D <= 10) {
                        csyxj = "M";

                    }
                    if (D >= 1 && D <= 6) {
                        csyxj = "L";

                    }
                }
                if (O >= 2 && O <= 3) {
                    csyxj = "L";

                }
                if (O == 1) {
                    csyxj = "L";

                }
            }

            if (S >= 2 && S <= 3) {
                if (O >= 8 && O <= 10) {
                    if (D >= 5 && D <= 10) {
                        csyxj = "M";

                    }
                    if (D >= 1 && D <= 4) {
                        csyxj = "L";

                    }

                }
                if (O >= 6 && O <= 7) {
                    csyxj = "L";
                }
                if (O >= 4 && O <= 5) {
                    csyxj = "L";
                }
                if (O >= 2 && O <= 3) {
                    csyxj = "L";

                }
                if (O == 1) {
                    csyxj = "L";
                }
            }

            if (S == 1) {
                csyxj = "L";
            }


            var data = {
                id: id, xxyfkz: xxyfkz, fsd: fsd, yzd: yzd, tckzfc: tckzfc, tckzfm: tckzfm, tcd: tcd,
                sxmsId: sxmsId, sxyyId: sxyyId, sxmsfxId: sxmsfxId, csyxj: csyxj,tcdfm:tcdfm,tcdfc:tcdfc,
                partId: singleId
            };
            var json = mini.encode(data);


            $.ajax({
                url: jsUseCtxPath + '/drbfm/single/saveRiskAnalysis.do',
                type: 'POST',
                data: json,
                async: false,
                contentType: 'application/json',
                success: function (returnData) {
                    if (returnData && returnData.message) {
                        mini.alert(returnData.message, '提示', function () {
                            if (returnData.success) {
                                // mini.get("requestId").setValue(returnData.data);
                                // sxfxWindow.hide();
                                loadListGrid();
                                closeRiskAnalysis();
                            }
                        });
                    }
                }
            });
        }

        function closeRiskAnalysis() {
            mini.get("xxyfkz").setValue('');
            mini.get("fashengduLevel").setValue('');
            mini.get("fashengduLevel").setText('');
            mini.get("yanzhongduLevel").setValue('');
            mini.get("yanzhongduLevel").setText('');
            mini.get("tckzfc").setValue('');
            mini.get("tckzfm").setValue('');
            mini.get("tanceduLevel").setValue('');
            mini.get("tanceduLevel").setText('');
            mini.get("tanceduLevelFc").setValue('');
            mini.get("tanceduLevelFc").setText('');
            sxfxWindow.hide();
        }

        function validRiskAnalysis() {
            var xxyfkz = $.trim(mini.get("xxyfkz").getValue())
            if (!xxyfkz) {
                return {"result": false, "message": "请填写当前预防控制(PC)"};
            }
            var fsd = $.trim(mini.get("fashengduLevel").getValue())
            if (!fsd) {
                return {"result": false, "message": "请填写发生度"};
            }
            var yzd = $.trim(mini.get("yanzhongduLevel").getValue())
            if (!yzd) {
                return {"result": false, "message": "请填写严重度"};
            }
            return {"result": true};
        }


        function selectYanzhongduLevel() {
            var yanzhongduLevel = mini.get("yanzhongduLevel").value;
            mini.open({
                title: "SOD严重度打分",
                url: jsUseCtxPath + "/drbfm/single/yanzhongdu.do?yanzhongduLevel=" + yanzhongduLevel,
                width: 1000,
                height: 700,
                showModal: true,
                allowResize: true,
                showCloseButton: true,
                onload: function () {

                },
                ondestroy: function (returnData) {
                    if (returnData && returnData.yanzhongId) {
                        mini.get("yanzhongduLevel").setValue(returnData.yanzhongId);
                        mini.get("yanzhongduLevel").setText(returnData.yanzhongId);
                    } else if (!yanzhongduLevel) {
                        mini.alert("未选择严重度分数！");
                    }
                }

            });
        }

        function selectFashengduLevel() {
            var fashengduLevel = mini.get("fashengduLevel").value;
            mini.open({
                title: "SOD发生度打分",
                url: jsUseCtxPath + "/drbfm/single/fashengdu.do",
                width: 1000,
                height: 700,
                showModal: true,
                allowResize: true,
                showCloseButton: true,
                onload: function () {

                },
                ondestroy: function (returnData) {
                    if (returnData && returnData.fashengId) {
                        mini.get("fashengduLevel").setValue(returnData.fashengId);
                        mini.get("fashengduLevel").setText(returnData.fashengId);
                    } else if (!fashengduLevel) {
                        mini.alert("未选择发生度分数！");
                    }
                }

            });
        }

        function selectTanceduLevel() {
            var tanceduLevel = mini.get("tanceduLevel").value;
            mini.open({
                title: "SOD探测度打分",
                url: jsUseCtxPath + "/drbfm/single/tancedu.do",
                width: 1000,
                height: 700,
                showModal: true,
                allowResize: true,
                showCloseButton: true,
                onload: function () {

                },
                ondestroy: function (returnData) {
                    if (returnData && returnData.tanceId) {
                        mini.get("tanceduLevel").setValue(returnData.tanceId);
                        mini.get("tanceduLevel").setText(returnData.tanceId);
                    } else if (!tanceduLevel) {
                        mini.alert("未选择探测度分数！");
                    }
                }

            });
        }

        function selectTanceduLevelFc() {
            var tanceduLevelFc = mini.get("tanceduLevelFc").value;
            mini.open({
                title: "SOD探测度打分",
                url: jsUseCtxPath + "/drbfm/single/tancedu.do",
                width: 1000,
                height: 700,
                showModal: true,
                allowResize: true,
                showCloseButton: true,
                onload: function () {

                },
                ondestroy: function (returnData) {
                    if (returnData && returnData.tanceId) {
                        mini.get("tanceduLevelFc").setValue(returnData.tanceId);
                        mini.get("tanceduLevelFc").setText(returnData.tanceId);
                    } else if (!tanceduLevelFc) {
                        mini.alert("未选择探测度分数！");
                    }
                }

            });
        }

        function onYanzhongduCloseClick() {
            mini.get("yanzhongduLevel").setValue('');
            mini.get("yanzhongduLevel").setText('');
        }

        function onFashengduCloseClick() {
            mini.get("fashengduLevel").setValue('');
            mini.get("fashengduLevel").setText('');
        }

        function onTanceduCloseClick() {
            mini.get("tanceduLevel").setValue('');
            mini.get("tanceduLevel").setText('');
        }

        function onTanceduFcCloseClick() {
            mini.get("tanceduLevelFc").setValue('');
            mini.get("tanceduLevelFc").setText('');
        }

        function saveData() {

            var formData = new mini.Form("formBusiness");
            var data = formData.getData();
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + '/drbfm/single/saveSOD?id=' + id,
                type: 'POST',
                data: json,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (!data.success) {
                        mini.alert(data.message, "提示消息");

                    }
                }
            });
            var S = mini.get("yanzhongduLevel").getValue();
            if (!S) {
                mini.alert('还未选择严重度分数！');
                return;
            }
            var O = mini.get("fashengduLevel").getValue();
            if (!O) {
                mini.alert('还未选择发生度分数！');
                return;
            }
            var D = mini.get("tanceduLevel").getValue();


            var H = '高风险';
            var M = '中风险';
            var L = '低风险';
            var returnData = {};
            if (S >= 9 && S <= 10) {
                if (O >= 8 && O <= 10) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);

                }

                if (O >= 6 && O <= 7) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 2 && D <= 10) {
                        returnData = {riskLevel: H};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D <= 1) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }

                }
                if (O >= 2 && O <= 3) {
                    if (D >= 7 && D <= 10) {
                        returnData = {riskLevel: H};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 5 && D <= 6) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 2 && D <= 4) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D <= 1) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O == 1) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }

            }

            if (S >= 7 && S <= 8) {
                if (O >= 8 && O <= 10) {
                    returnData = {riskLevel: H};
                    window.CloseOwnerWindow(returnData);
                }
                if (O >= 6 && O <= 7) {
                    if (D >= 2 && D <= 10) {
                        returnData = {riskLevel: H};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D <= 1) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 7 && D <= 10) {
                        returnData = {riskLevel: H};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 1 && D <= 6) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O >= 2 && O <= 3) {
                    if (D >= 5 && D <= 10) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 1 && D <= 4) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O == 1) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }

            if (S >= 4 && S <= 6) {
                if (O >= 8 && O <= 10) {
                    if (D >= 5 && D <= 10) {
                        returnData = {riskLevel: H};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 1 && D <= 4) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }

                }
                if (O >= 6 && O <= 7) {
                    if (D >= 2 && D <= 10) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D <= 1) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O >= 4 && O <= 5) {
                    if (D >= 7 && D <= 10) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 1 && D <= 6) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }
                }
                if (O >= 2 && O <= 3) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
                if (O == 1) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }

            if (S >= 2 && S <= 3) {
                if (O >= 8 && O <= 10) {
                    if (D >= 5 && D <= 10) {
                        returnData = {riskLevel: M};
                        window.CloseOwnerWindow(returnData);
                    }
                    if (D >= 1 && D <= 4) {
                        returnData = {riskLevel: L};
                        window.CloseOwnerWindow(returnData);
                    }

                }
                if (O >= 6 && O <= 7) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
                if (O >= 4 && O <= 5) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
                if (O >= 2 && O <= 3) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
                if (O == 1) {
                    returnData = {riskLevel: L};
                    window.CloseOwnerWindow(returnData);
                }
            }

            if (S == 1) {
                returnData = {riskLevel: L};
                window.CloseOwnerWindow(returnData);
            }


        }

        function loadListGrid() {
            var functionDesc = mini.get("functionDesc").getValue();
            var requestDesc = mini.get("requestDesc").getValue();
            var csyxj = mini.get("csyxj").getValue();
            var sxms = mini.get("sxms").getValue();
            var sxqy = mini.get("sxqy").getValue();

            $.ajax({
                url: jsUseCtxPath + "/drbfm/single/getRiskAnalysisList.do?partId=" + singleId
                    + "&functionDesc=" + functionDesc + "&requestDesc=" + requestDesc + "&csyxj=" + csyxj
                    + "&sxms=" + sxms + "&sxqy=" + sxqy,
                success: function (result) {
                    if (!result.success) {
                        mini.alert(result.message);
                    } else {

                        var merges = [];

                        // //操作列的合并
                        // if (result.mergeYqNum) {
                        //     for (var index = 0; index < result.mergeYqNum.length; index++) {
                        //         merges.push({
                        //             rowIndex: result.mergeYqNum[index].rowIndex,
                        //             columnIndex: 2,
                        //             rowSpan: result.mergeYqNum[index].rowSpan,
                        //             colSpan: 1
                        //         });
                        //     }
                        // }

                        //功能列的合并
                        if (result.mergeYqNum) {
                            for (var index = 0; index < result.mergeYqNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeYqNum[index].rowIndex,
                                    columnIndex: 3,
                                    rowSpan: result.mergeYqNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }
                        //要求列的合并
                        if (result.mergeYqNum) {
                            for (var index = 0; index < result.mergeYqNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeYqNum[index].rowIndex,
                                    columnIndex: 4,
                                    rowSpan: result.mergeYqNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }
                        //失效影响列的合并
                        if (result.mergeNum) {
                            for (var index = 0; index < result.mergeNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeNum[index].rowIndex,
                                    columnIndex: 5,
                                    rowSpan: result.mergeNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }
                        //严重度列的合并
                        if (result.mergeNum) {
                            for (var index = 0; index < result.mergeNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeNum[index].rowIndex,
                                    columnIndex: 6,
                                    rowSpan: result.mergeNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }
                        //失效模式列的合并
                        if (result.mergeNum) {
                            for (var index = 0; index < result.mergeNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeNum[index].rowIndex,
                                    columnIndex: 7,
                                    rowSpan: result.mergeNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }

                        //针对FM列的合并
                        if (result.mergeNum) {
                            for (var index = 0; index < result.mergeNum.length; index++) {
                                merges.push({
                                    rowIndex: result.mergeNum[index].rowIndex,
                                    columnIndex: 12,
                                    rowSpan: result.mergeNum[index].rowSpan,
                                    colSpan: 1
                                });
                            }
                        }

                        fxsxfxListGrid.setData(result.data);
                        if (merges.length > 0) {
                            fxsxfxListGrid.mergeCells(merges);
                        }
                    }
                }
            });
        }

        function clearListGrid() {
            mini.get("functionDesc").setValue('');
            mini.get("functionDesc").setText('');
            mini.get("requestDesc").setValue('');
            mini.get("requestDesc").setText('');
            mini.get("csyxj").setValue('');
            mini.get("csyxj").setText('');
            mini.get("sxms").setValue('');
            mini.get("sxqy").setValue('');
            loadListGrid();
        }

        function addNewSxms() {
            //需要弹窗，失效模式名称和 关联的失效模式
            xzsxmsWindow.show();
        }


        function closeSxms() {
            mini.get("xzsxmsName").setValue('');
            mini.get("chooseSxms").setValue('');
            mini.get("chooseSxms").setText('');
            xzsxmsWindow.hide();
        }


        function selectRelSxms() {
            //这个要1建立失效模式【部件级别】 2 建立关系 以新建的模式为基础，关联失效模式【向上关联】
            selectSxmsWindow.show();
            url = "${ctxPath}/drbfm/single/getModelSxmsList.do?partId=${singleId}";
            selectSxmsListGrid.setUrl(url);
            // relType = "up";
            selectSxmsListGrid.load();
        }


        function saveSxms() {
            // todo 改
            var xzsxmsName = $.trim(mini.get("xzsxmsName").getValue())
            if (!xzsxmsName) {
                mini.alert("请填写新增的失效模式！");
                return;
            }
            var chooseSxmsId = $.trim(mini.get("chooseSxms").getValue())
            if (!chooseSxmsId) {
                mini.alert("请选择关联失效模式！");
                return;
            }


            var data = {
                chooseSxmsId: chooseSxmsId, sxmsName: xzsxmsName, partId: singleId
            };
            var json = mini.encode(data);

            $.ajax({
                url: jsUseCtxPath + '/drbfm/single/saveNewSxms.do',
                type: 'POST',
                data: json,
                async: false,
                contentType: 'application/json',
                success: function (returnData) {
                    if (returnData && returnData.message) {
                        mini.alert(returnData.message, '提示', function () {
                            if (returnData.success) {
                                // fxsxfxListGrid.reload();
                                // mini.get("requestId").setValue(returnData.data);
                                // sxfxWindow.hide();
                                loadListGrid();
                                closeSxms();
                            }
                        });
                    }
                }
            });
        }

        function selectSxmsOK() {

            //获取id和名称赋值到上级菜单
            var relRecord = selectSxmsListGrid.getSelected();
            var relId = relRecord.id;
            var relName = relRecord.sxmsName;

            mini.get("chooseSxms").setValue(relId);
            mini.get("chooseSxms").setText(relName);
            selectSxmsHide();
        }


        function selectSxmsHide() {
            selectSxmsWindow.hide();
            mini.get("sxmsName").setValue('');
        }

        function delNewSxyy(id) {

            mini.confirm("确定删除失效起因及关联？", "提示", function (action) {
                if (action != 'ok') {
                    return;
                } else {

                    if (id) {
                        _SubmitJson({
                            url: jsUseCtxPath + "/drbfm/single/deleteSxyy.do",
                            method: 'POST',
                            showMsg: false,
                            data: {id: id},
                            success: function (data) {
                                if (data) {
                                    mini.alert(data.message);
                                    loadListGrid();
                                }
                            }
                        });
                    }
                }
            });
        }

        function searchSelectSxms() {
            var queryParam = [];
            //其他筛选条件
            var sxmsName = $.trim(mini.get("sxmsName").getValue());
            if (sxmsName) {
                queryParam.push({name: "sxmsName", value: sxmsName});
            }
            var data = {};
            data.filter = mini.encode(queryParam);
            //查询
            selectSxmsListGrid.load(data);
        }

        function requestDescRenderer(e) {
            var record = e.record;
            var isRelationChange = record.isRelChange;
            var requestDesc = record.requestDesc;
            if (isRelationChange == 'yes') {
                return "<span style='color: red;font-weight: 900;'>" + requestDesc + "</span>";
            } else {
                return "<span style='color: black;'>" + requestDesc + "</span>";
            }
        }
        function sxmsAllNameRenderer(e) {
            var record = e.record;
            var isRelationSxms = record.isRelSxms;
            var sxmsAllName = record.sxmsAllName;
            if (isRelationSxms == 'yes') {
                return "<span style='color: red;font-weight: 900;'>" + sxmsAllName + "</span>";
            } else {
                return "<span style='color: black;'>" + sxmsAllName + "</span>";
            }
        }

    </script>

</body>
</html>
