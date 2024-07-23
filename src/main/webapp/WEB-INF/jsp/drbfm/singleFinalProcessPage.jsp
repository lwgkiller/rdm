<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>部件分析-验证改进优化</title>
    <%@include file="/commons/list.jsp" %>
    <style>
        .mini-grid-rows-view {
            background: white !important;
        }

        .mini-grid-cell-inner {
            line-height: 40px !important;
            padding: 0;
        }

        .mini-grid-cell-inner {
            font-size: 14px !important;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="width: 100%; height: 100%;background: #fff">
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">验证改进后风险评估</p>
    <hr>
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 5px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">改进后风险措施优先级: </span>
                        <input id="riskLevelFinalFilter" name="riskLevelFinal" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="key_" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CSYXJ"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">改进前风险措施优先级: </span>
                        <input id="csyxjFilter" name="csyxj" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="key_" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CSYXJ"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchRiskFinal()">查询</a>
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportAll()">导出全部</a>

                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div id="requestListGrid" class="mini-datagrid" allowResize="false" style="height: 60%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true" autoload="true"
         url="${ctxPath}/drbfm/single/getFinalProcessList.do?belongSingleId=${singleId}"
    >
        <div property="columns">
            <div type="checkcolumn" width="60px"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="fxpgId" headerAlign="center" width="0px" align="center">
            </div>
            <div field="gjyfcs" headerAlign="center" width="150px" align="center">采取的改进预防措施
            </div>
            <div field="gjtccs" headerAlign="center" width="150px" align="center">采取的改进探测措施
            </div>
            <div field="riskLevelFinal" headerAlign="center" width="150px" align="center">验证改进后措施优先级
            </div>
            <%--            <div field="riskLevel" headerAlign="center" width="150px" align="center" renderer="riskLevelRenderer">验证改进前措施优先级--%>
            <%--            </div>--%>
            <div field="csyxj" headerAlign="center" width="150px" align="center">验证改进前措施优先级
            </div>
            <div field="sxyyName" headerAlign="center" width="490px" align="left">失效起因</div>


            <%--            <div field="riskDesc" headerAlign="center" width="200px" align="left"--%>
            <%--                 renderer="renderDemandDesc">存在风险--%>
            <%--            </div>--%>
            <div field="sxmsName" name="sxmsName" headerAlign="center" width="200px" align="left"
                 renderer="renderDemandDesc">失效模式
            </div>
            <div field="requestDesc" headerAlign="center" width="620px" align="left"
                 renderer="renderDemandDesc">特性要求
            </div>
            <div field="relFunctionDesc" headerAlign="center" width="400px" align="left"
                 renderer="renderDemandDesc">关联功能描述
            </div>
            <div field="relDimensionNames" headerAlign="center" width="180px" align="center">维度
            </div>
            <%--            <div field="requestChanges" headerAlign="center" width="400px" align="left"--%>
            <%--                 renderer="renderDemandDesc">变化点--%>
            <%--            </div>--%>

            <%--            <div field="relEffect" headerAlign="center" width="200px" align="left"--%>
            <%--                 renderer="renderDemandDesc">变化点带来的与该要求相关的客观影响--%>
            <%--            </div>--%>
            <%--            <div field="compareToJP" headerAlign="center" width="200px" align="left"--%>
            <%--                 renderer="renderDemandDesc">与竞品对比--%>
            <%--            </div>--%>
        </div>
    </div>
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">下一步工作：持续改进项目、更新基础FMEA等</p>
    <hr>
    <div class="topToolBar" id="nextWorkToolBar">
        <div style="position: relative!important;">
            <a class="mini-button" plain="true" style="float: left"
               onclick="addNextWork()">添加</a>
            <a class="mini-button btn-red" plain="true" style="float: left"
               onclick="removeNextWork()">删除</a>
            <span style="color: red;float: left;margin-left: 10px">(注：由验证部门填写的下一步工作，不能被编辑和删除)</span>
        </div>
    </div>
    <div id="nextWorkListGrid" class="mini-datagrid" allowResize="false" style="height: 180px"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true" autoload="true"
         url="${ctxPath}/drbfm/single/queryNextWork.do?belongSingleId=${singleId}"
    >
        <div property="columns">
            <div type="checkcolumn" width="60px"></div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="nextWorkActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="nextWorkContent" headerAlign="center" width="750px" align="left">下一步工作
            </div>
            <div field="finishFlag" headerAlign="center" width="350px" align="left">完成标志
            </div>
            <div field="finishTime" headerAlign="center" width="150px" align="center">完成时间
            </div>
            <div field="respDeptNames" headerAlign="center" width="200px" align="center">责任部门
            </div>
            <div field="creator" width="70" headerAlign="center" align="center">创建人</div>
            <div field="CREATE_TIME_" width="100" headerAlign="center" dateFormat="yyyy-MM-dd" align="center">创建时间</div>
        </div>
    </div>
</div>

<div id="nextWorkWindow" title="下一步工作" class="mini-window" style="width:750px;height:400px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveNextWorkBtn" class="mini-button" onclick="saveNextWork()">保存</a>
                <a id="closeNextWorkBtn" class="mini-button btn-red" onclick="closeNextWork()">关闭</a>
            </div>
        </div>
        <input id="nextWorkId" name="id" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 12%">下一步工作：<span style="color:red">*</span></td>
                <td style="width: 38%;">
                    <input id="nextWorkContent" name="nextWorkContent" class="mini-textarea rxc"
                           plugins="mini-textarea" style="width:95%;;height:90px;line-height:25px;" label="下一步工作"
                           datatype="varchar" length="500"
                           vtype="length:500" minlen="0" allowinput="true" emptytext="请输入下一步工作..."/>
                </td>
            </tr>
            <tr>
                <td style="width: 12%">完成标志：</td>
                <td style="width: 38%;">
                    <input id="finishFlag" name="finishFlag" class="mini-textarea rxc"
                           plugins="mini-textarea" style="width:95%;;height:90px;line-height:25px;" label="完成标志"
                           datatype="varchar" length="500"
                           vtype="length:500" minlen="0" allowinput="true" emptytext="请输入完成标志..."/>
                </td>
            </tr>
            <tr>
                <td style="width: 12%">完成时间：</td>
                <td style="width: 38%;">
                    <input id="finishTime" name="finishTime" class="mini-datepicker" format="yyyy-MM-dd"
                           allowInput="false"
                           showTime="false" showOkButton="false" showClearButton="true" style="width:95%;"/>
                </td>
            </tr>
            <tr>
                <td style="width: 12%">责任部门：<span style="color:red">*</span></td>
                <td style="width: 38%;">
                    <input id="respDeptIds" name="respDeptIds" textname="respDeptNames" class="mini-dep rxc"
                           plugins="mini-dep"
                           style="width:95%;height:34px" allowinput="false" length="500" maxlength="500" minlen="0"
                           single="false" initlogindep="false"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="riskLevelWindow" title="改进后风险评定" class="mini-window" style="width:590px;height:350px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveRiskLevelBtn" class="mini-button" onclick="saveRiskLevel()">保存</a>
                <a id="closeRiskLevelBtn" class="mini-button btn-red" onclick="closeRiskLevel()">关闭</a>
            </div>
        </div>
        <input id="requestId" name="id" class="mini-hidden"/>
        <table class="table-detail" cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width:12%">采取的改进预防措施</td>
                <td style="width: 38%;">
                    <input id="gjyfcs" name="gjyfcs" class="mini-textarea"
                           plugins="mini-textarea" style="width:95%;;height:50px;line-height:25px;" label="改进预防措施"
                           allowinput="true" emptytext="请输入采取的改进预防措施..."/>
                </td>
            </tr>
            <tr>
                <td style="width:12%">采取的改进探测措施</td>
                <td style="width: 38%;">
                    <input id="gjtccs" name="gjtccs" class="mini-textarea"
                           plugins="mini-textarea" style="width:95%;;height:50px;line-height:25px;" label="改进探测措施"
                           allowinput="true" emptytext="请输入采取的改进探测措施..."/>
                </td>
            </tr>
            <td style="width: 8%;">验证改进后措施优先级：</td>
            <td style="width: 35%;">
                <input id="riskLevelFinal" name="riskLevelFinal" style="width:98%;" class="mini-buttonedit"
                       showClose="true"
                       oncloseclick="onRiskLevelFinalCloseClick()"
                       textname="riskLevelFinal" allowInput="false"
                       onbuttonclick="selectRiskLevelFinal()"/>
            </td>
        </table>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var singleId = "${singleId}";
    var action = "${action}";
    var stageName = "${stageName}";

    var nextWorkListGrid = mini.get("nextWorkListGrid");
    var nextWorkWindow = mini.get("nextWorkWindow");
    var riskLevelWindow = mini.get("riskLevelWindow");
    var requestListGrid = mini.get("requestListGrid");
    requestListGrid.on("load", function () {
        requestListGrid.mergeColumns(["sxmsName"]);
    });

    $(function () {
        if (action != 'task' || stageName != 'processFinal') {
            $("#nextWorkToolBar").hide();
        }
    });

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        // 验证改进后措施优先级换数据表位置后，requestId改成风险评估id
        var requestId = record.fxpgId;
        var riskLevelFinal = "";
        var gjyfcs = "";
        var gjtccs = "";
        if (record.riskLevelFinal) {
            riskLevelFinal = record.riskLevelFinal;
        }
        if (record.gjyfcs) {
            gjyfcs = record.gjyfcs;
        }
        if (record.gjtccs) {
            gjtccs = record.gjtccs;
        }
        var s = '';
        if (action != 'task' || stageName != 'processFinal') {
            s += '<span   style="color: silver" >风险评定</span>';
            return s;
        }
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="风险评定" onclick="openRiskLevel(\'' + requestId + '\',\'' + riskLevelFinal + '\',\'' + gjyfcs + '\',\'' + gjtccs + '\')">风险评定</span>';
        return s;
    }

    function nextWorkActionRenderer(e) {
        var record = e.record;
        var s = '';
        if (action != 'task' || stageName != 'processFinal' || record.relTestTaskId) {
            s += '<span   style="color: silver" >编辑</span>';
            return s;
        }
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editNextWork(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">编辑</span>';
        return s;
    }

    function openRiskLevel(requestId, riskLevelFinal, gjyfcs, gjtccs) {
        riskLevelWindow.show();
        mini.get("requestId").setValue(requestId);
        mini.get("riskLevelFinal").setText(riskLevelFinal);
        mini.get("riskLevelFinal").setValue(riskLevelFinal);
        mini.get("gjyfcs").setValue(gjyfcs);
        mini.get("gjtccs").setValue(gjtccs);
    }

    function saveRiskLevel() {
        var formData = {};
        formData.id = mini.get("requestId").getValue();
        formData.riskLevelFinal = mini.get("riskLevelFinal").getValue();
        formData.gjyfcs = mini.get("gjyfcs").getValue();
        formData.gjtccs = mini.get("gjtccs").getValue();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/updateRequestRiskLevelFinal.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        closeRiskLevel();
                    } else {
                        mini.alert("数据保存失败，" + data.message);
                    }
                }
            }
        });
    }

    function closeRiskLevel() {
        riskLevelWindow.hide();
        mini.get("requestId").setValue("");
        mini.get("riskLevelFinal").setValue("");
        requestListGrid.reload();
    }

    function addNextWork() {
        nextWorkWindow.show();
    }

    function editNextWork(record) {
        nextWorkWindow.show();
        mini.get("nextWorkId").setValue(record.id);
        mini.get("nextWorkContent").setValue(record.nextWorkContent);
        mini.get("respDeptIds").setValue(record.respDeptIds);
        mini.get("respDeptIds").setText(record.respDeptNames);
        mini.get("finishFlag").setValue(record.finishFlag);
        mini.get("finishTime").setValue(record.finishTime);
    }

    function saveNextWork() {
        var formData = {};
        formData.id = mini.get("nextWorkId").getValue();
        formData.belongSingleId = singleId;
        formData.nextWorkContent = mini.get("nextWorkContent").getValue();
        formData.respDeptIds = mini.get("respDeptIds").getValue();
        formData.respDeptNames = mini.get("respDeptIds").getText();
        formData.finishFlag = mini.get("finishFlag").getValue();
        formData.finishTime = mini.get("finishTime").getText();
        if (!formData.nextWorkContent) {
            mini.alert("请填写下一步工作！");
            return;
        }
        if (!formData.respDeptIds) {
            mini.alert("请选择责任部门！");
            return;
        }
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/saveNextWork.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        closeNextWork();
                    } else {
                        mini.alert("数据保存失败，" + data.message);
                    }
                }
            }
        });
    }

    function closeNextWork() {
        nextWorkWindow.hide();
        mini.get("nextWorkId").setValue("");
        mini.get("nextWorkContent").setValue("");
        mini.get("respDeptIds").setValue("");
        mini.get("respDeptIds").setText("");
        mini.get("finishFlag").setValue("");
        mini.get("finishTime").setValue("");
        nextWorkListGrid.reload();
    }

    function removeNextWork() {
        var rows = nextWorkListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (!r.relTestTaskId) {
                        rowIds.push(r.id);
                    }
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteNextWork.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            nextWorkListGrid.reload();
                        }
                    }
                });
            }
        });
    }

    function riskLevelFinalRenderer(e) {
        var record = e.record;
        var riskLevelFinal = record.riskLevelFinal;

        var arr = [{'key': '高风险', 'value': '高风险', 'css': 'red'},
            {'key': '中风险', 'value': '中风险', 'css': 'orange'},
            {'key': '低风险', 'value': '低风险', 'css': 'green'}
        ];

        return $.formatItemValue(arr, riskLevelFinal);
    }

    function riskLevelRenderer(e) {
        var record = e.record;
        var riskLevel = record.riskLevel;

        var arr = [{'key': '高风险', 'value': '高风险', 'css': 'red'},
            {'key': '中风险', 'value': '中风险', 'css': 'orange'},
            {'key': '低风险', 'value': '低风险', 'css': 'green'}
        ];

        return $.formatItemValue(arr, riskLevel);
    }

    function searchRiskFinal() {
        var queryParam = [];
        //其他筛选条件
        var riskLevelFinalFilter = mini.get("riskLevelFinalFilter").getValue();
        if (riskLevelFinalFilter) {
            queryParam.push({name: "riskLevelFinal", value: riskLevelFinalFilter});
        }
        var csyxjFilter = mini.get("csyxjFilter").getValue();
        if (csyxjFilter) {
            queryParam.push({name: "csyxj", value: csyxjFilter});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        requestListGrid.load(data);
    }

    function selectRiskLevelFinal() {
        var id = mini.get("requestId").getValue();
        if (!id) {
            mini.alert('没有获取到必要信息，无法打分！');
            return;
        }
        mini.open({
            title: "风险评级打分",
            url: jsUseCtxPath + "/drbfm/single/selectRiskLevelFinal.do?requestId=" + id,
            width: 600,
            height: 450,
            showModal: true,
            allowResize: true,
            showCloseButton: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if (returnData && returnData.riskLevel) {
                    mini.get("riskLevelFinal").setText(returnData.riskLevel);
                    mini.get("riskLevelFinal").setValue(returnData.riskLevel);
                }
            }

        });
    }

    function onRiskLevelFinalCloseClick() {
        mini.get("riskLevelFinal").setText('');
        mini.get("riskLevelFinal").setValue('');
    }

    //导出 功能分解 模块
    function exportAll() {
        var params = [];
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        var url = jsUseCtxPath + "/drbfm/single/exportAll.do?singleId=" + singleId;
        excelForm.attr("action", url);
        excelForm.submit();
    }

</script>
</body>
</html>
