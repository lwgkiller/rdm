<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>功能失效关联</title>
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
<div class="searchBox">
    <form id="searchForm" class="search-form" style="margin-bottom: 5px">
        <ul>
            <li style="margin-right: 15px">
                <span class="text" style="width:auto">失效模式: </span>
                <%--<input id="sxms" name="sxms" class="mini-textbox"/>--%>
                <input id="sxms" name="sxms" class="mini-combobox rxc" plugins="mini-combobox"
                       style="width:250px;height:34px" popupWidth="400" label="失效模式："
                       length="200"
                       only_read="false" required="true" allowinput="true" mwidth="200"
                       wunit="%" mheight="34" hunit="px" shownullitem="false" multiSelect="false"
                       textField="sxmsName" valueField="sxmsName" emptyText="请选择..."
                       url="${ctxPath}/drbfm/single/getSingleSxmsListById.do?partId=${singleId}"
                       nullitemtext="请选择..." emptytext="请选择..."/>
            </li>

            <li style="margin-right: 15px">
                <span class="text" style="width:auto">功能: </span>
                <input id="functionDesc" name="functionDesc" class="mini-textbox"/>
            </li>
            <li style="margin-right: 15px">
                <span class="text" style="width:auto">特性要求: </span>
                <input id="requestDesc" name="requestDesc" class="mini-textbox"/>
            </li>
            <li style="margin-right: 15px">
                <span class="text" style="width:auto">结构分析下级: </span>
                <input id="jgfxxj" name="jgfxxj" class="mini-textbox"/>
            </li>
            <li style="margin-right: 15px">
                <span class="text" style="width:auto">向上关联: </span>
                <input id="upSearch" name="upSearch" class="mini-combobox" style="width:98%;"
                       textField="key" valueField="value"
                       emptyText="请选择..."
                       multiSelect="false"
                       data="[{key:'已关联',value:'yes'}
                             ,{key:'未关联',value:'no'}]"
                       allowInput="false" showNullItem="true"/>
            </li>
            <li style="margin-right: 15px">
                <span class="text" style="width:auto">向下关联: </span>
                <input id="downSearch" name="downSearch" class="mini-combobox" style="width:98%;"
                       textField="key" valueField="value"
                       emptyText="请选择..."
                       multiSelect="false"
                       data="[{key:'已关联',value:'yes'}
                             ,{key:'未关联',value:'no'}]"
                       allowInput="false" showNullItem="true"/></li>
            <li style="margin-left: 10px">
                <a class="mini-button" style="margin-right: 5px" plain="true" onclick="loadListGrid()">查询</a>
                <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearListGrid()">清空查询</a>


        </ul>
    </form>
</div>


<div class="mini-fit" style="width: 100%; height: 85%;" id="content">
    <div id="sxmsRelListGrid" class="mini-datagrid" allowResize="false" style="height: 100%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="false" showColumnsMenu="false" allowAlternating="true" showPager="false"
         onrowdblclick="openRowDblClick"
    <%--url="${ctxPath}/drbfm/single/getSingleSxmsList.do?partId=${singleId}"--%>
         allowCellWrap="true" showVGridLines="true">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <%--<div type="indexcolumn" headerAlign="center" align="center" width="40px">序号</div>--%>
            <div field="rowNumber" name="rowNumber" align="center" headerAlign="center" width="40px">序号</div>

            <div name="action" cellCls="actionIcons" width="50px" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="upRender" headerAlign="center" width="45px" renderer="onRelUpStatusRenderer" align="center">向上关联
            </div>
            <div field="downRender" headerAlign="center" width="45px" renderer="onRelDownStatusRenderer" align="center">
                向下关联
            </div>
            <div field="sxmsName" name="sxmsName" headerAlign="center" width="100px" align="left">失效模式
            </div>
            <div header="结构分析" headerAlign="center">
                <div property="columns">
                    <div field="jgfxsj" name="jgfxsj" headerAlign="center" width="120px" align="center">上一较高级别</div>
                    <div field="jgfxgzys" name="jgfxgzys" headerAlign="center" width="120px" align="center">关注要素</div>
                    <div field="jgfxxj" headerAlign="center" width="120px" align="center">下一较低级别</div>
                </div>
            </div>
            <div header="功能分析" headerAlign="center">
                <div property="columns">
                    <div field="gnfxsj" name="gnfxsj" headerAlign="center" width="180px" align="left">上一较高级别</div>
                    <div field="gnfxgzys" name="gnfxgzys" headerAlign="center" width="180px" align="left">关注要素</div>
                    <div field="gnfxxj" headerAlign="center" width="180px" align="left">下一较低级别</div>
                </div>
            </div>
            <%--<div field="requestDesc" headerAlign="center" width="100px" align="center">关联特性要求--%>
            <%--</div>--%>
            <%--<div field="functionDesc" headerAlign="center" width="100px" align="center">关联功能--%>
            <%--</div>--%>
            <%--<div field="structName" headerAlign="center" width="80px" align="center">部件名称--%>
            <%--</div>--%>
            <%--<div field="jixing" headerAlign="center" width="80px" align="center">机型名称--%>
            <%--</div>--%>
        </div>
    </div>
</div>

<div id="sxmsWindow" title="失效模式网" class="mini-window" style="width:1200px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false"

>

    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <div>
            <a class="mini-button" id="relUp" plain="true" style="float: left"
               onclick="relUp()">向上关联</a>
            <a class="mini-button" id="relDown" plain="true" style="float: left"
               onclick="relDown()">向下关联</a>
        </div>

    </div>
    <div class="mini-fit">
        <div id="sxmsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" multiSelect="false"
             allowAlternating="true" showPager="false"
        <%--url="${ctxPath}/drbfm/single/getSxmsNetList.do?partId=${singleId}"--%>
        >
            <%--url="${ctxPath}/drbfm/single/getSxmsList.do?partId=${singleId}&relType=up">--%>
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div name="action" cellCls="actionIcons" width="80px" headerAlign="center" align="center"
                     renderer="sxmsActionRenderer" cellStyle="padding:0;">操作
                </div>
                <div field="relType" headerAlign="center" width="100px" align="center">关联类型
                </div>
                <div field="sxmsName" headerAlign="center" width="200px" align="center">失效影响/原因
                </div>
                <div field="requestDesc" headerAlign="center" width="200px" align="center">要求描述
                </div>
                <div field="structName" headerAlign="center" width="200px" align="center">关联部件名称
                </div>
                <%--<div field="jixing" headerAlign="center" width="200px" align="center">关联整机型号--%>
                <%--</div>--%>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="关闭" onclick="sxmsOK()"/>
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
        <span class="text" style="width:auto">部件名称: </span>
        <input class="mini-textbox" style="width: 120px" id="bjName" name="bjName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchSelectSxms()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectSxmsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true"
             <%--showPager="false"--%>
             multiSelect="true"
             showPager="true"
             sizeList="[2,50,100,200]" pageSize="50"
        <%--autoload="true"--%>
        <%--url="${ctxPath}/drbfm/single/getModelSxmsList.do?partId=${singleId}&relType=up"--%>
        >
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


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var singleId = "${singleId}";
    var action = "${action}";
    var relType = "";
    var stageName = "${stageName}";
    var startOrEnd = "${startOrEnd}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var fileListGrid = mini.get("fileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var sxmsRelListGrid = mini.get("sxmsRelListGrid");
    var selectSxmsListGrid = mini.get("selectSxmsListGrid");
    var sxmsListGrid = mini.get("sxmsListGrid");
    var sxmsWindow = mini.get("sxmsWindow");
    var selectSxmsWindow = mini.get("selectSxmsWindow");


    selectSxmsListGrid.on("load", function () {
        selectSxmsListGrid.mergeColumns(["sxmsName", "requestDesc"]);
    });
    $(function () {
        // if (action == 'task' && stageName == 'bjfzrfxfx') {
        //     // mini.get("addDemand").show();
        //     // mini.get("demandCollect").show();
        //     // mini.get("delDemand").show();
        //     // $("#tips").show();
        // }
        loadListGrid();

    });


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="showNetDetail()">查看并关联</span>';
        // if (action == 'task' && CREATE_BY_ == currentUserId && stageName == 'bjfzrfxfx' && record.demandType == '自增') {
        //     s += '<span style="display: inline-block" class="separator"></span>';
        //     s += '<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editDeptDemand(\'' + id + '\')">编辑</span>';
        // }
        return s;
    }


    function sxmsActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        // s += '<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="showNetDetail(\'' + id + '\')">查看并关联</span>';
        if (action=='edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
            // s += '<span style="display: inline-block" class="separator"></span>';
            s += '<span  style="color:#2ca9f6;cursor: pointer" title="删除" onclick="deleteSxms(\'' + id + '\')">删除</span>';
        } else {
            s += '&nbsp;&nbsp;&nbsp;<span  title="删除" style="color: silver" > 删除 </span>';

        }
        return s;
    }

    // 双击某一行时发生
    function openRowDblClick(e) {
        var clickRecord = e.record;
        showNetDetail(clickRecord);
    }

    function showNetDetail(clickRecord) {
        //从功能失效网界面跳到查看并关联，查当前选择的失效模式的关联
        if (!clickRecord) {
            var record = sxmsRelListGrid.getSelected();
        } else {
            var record = clickRecord;
        }
        var sxmsId = record.id;
        url = "${ctxPath}/drbfm/single/getSxmsNetList.do?sxmsId=" + sxmsId;
        sxmsListGrid.setUrl(url);
        sxmsListGrid.load();
        if (action == 'edit'||(action == 'task' && stageName == 'bjfzrfxfx')) {
            mini.get("relUp").show();
            mini.get("relDown").show();
        } else {
            mini.get("relUp").hide();
            mini.get("relDown").hide();
        }
        if (startOrEnd == "start") {
            //向上关联灰
            mini.get("relUp").setEnabled(false);
        } else if (startOrEnd == "end") {
            //向下关联灰
            mini.get("relDown").setEnabled(false);
            // mini.get("relDown").hide();
        }
        sxmsWindow.show();

    }

    function sxmsOK() {
        loadListGrid();
        sxmsWindow.hide();

    }

    function selectSxmsOK() {
        // 这里要看关联的是向上还是向下【relType】字段，然后去建立关系

        //获取id
        var record = sxmsRelListGrid.getSelected();
        var baseId = record.id;
        var rows = selectSxmsListGrid.getSelecteds();
        if (rows.length > 0) {
            var relIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                relIds.push(r.id);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/drbfm/single/createSxmsRel.do",
                method: 'POST',
                showMsg: false,
                data: {baseId: baseId, relIds: relIds.join(','), relType: relType, partId: singleId},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        sxmsListGrid.load();
                    }
                }
            });
        }
        selectSxmsHide();
    }


    function selectSxmsHide() {
        selectSxmsWindow.hide();
        mini.get("sxmsName").setValue('');
        mini.get("bjName").setValue('');
    }

    function relUp() {
        // todo singleId 要改成saleModel整机
        selectSxmsWindow.show();
        var record = sxmsRelListGrid.getSelected();
        var baseId = record.id;
        url = "${ctxPath}/drbfm/single/getModelSxmsList.do?partId=${singleId}&baseId=" + baseId + "&relType=up";
        selectSxmsListGrid.setUrl(url);
        relType = "up";
        selectSxmsListGrid.load();
    }

    function relDown() {
        selectSxmsWindow.show();
        var record = sxmsRelListGrid.getSelected();
        var baseId = record.id;
        url = "${ctxPath}/drbfm/single/getModelSxmsList.do?partId=${singleId}&baseId=" + baseId + "&relType=down";
        selectSxmsListGrid.setUrl(url);
        relType = "down";
        selectSxmsListGrid.load();
    }

    function searchSelectSxms() {
        var queryParam = [];
        //其他筛选条件
        var sxmsName = $.trim(mini.get("sxmsName").getValue());
        if (sxmsName) {
            queryParam.push({name: "sxmsName", value: sxmsName});
        }
        var bjName = $.trim(mini.get("bjName").getValue());
        if (bjName) {
            queryParam.push({name: "bjName", value: bjName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectSxmsListGrid.load(data);
    }


    function deleteSxms(id) {

        mini.confirm("确定删除选中关联？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {

                if (id) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/drbfm/single/deleteSxmsRel.do",
                        method: 'POST',
                        showMsg: false,
                        data: {id: id},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                sxmsListGrid.load();
                            }
                        }
                    });
                }
            }
        });
    }

    function onRelUpStatusRenderer(e) {
        var record = e.record;
        var upRender = record.upRender;

        var color = '#32CD32';
        var title = "已关联";
        if (upRender == "no") {
            color = '#fb0808';
            title = "未关联";
        } else if (upRender == "blank") {
            color = '#cccccc';
            title = "无需关联";
        }
        if (startOrEnd == "start") {
            color = '#cccccc';
            title = "无需关联";
        }

        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

    function onRelDownStatusRenderer(e) {
        var record = e.record;
        var downRender = record.downRender;

        var color = '#32CD32';
        var title = "已关联";
        if (downRender == "no") {
            color = '#fb0808';
            title = "未关联";
        } else if (downRender == "blank") {
            color = '#cccccc';
            title = "无需关联";
        }
        if (startOrEnd == "end") {
            color = '#cccccc';
            title = "无需关联";
        }

        var s = '<span title= "' + title + '" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }


    function loadListGrid() {
        var sxms = $.trim(mini.get("sxms").getValue());
        var functionDesc = $.trim(mini.get("functionDesc").getValue());
        var requestDesc = $.trim(mini.get("requestDesc").getValue());
        var jgfxxj = $.trim(mini.get("jgfxxj").getValue());
        var upSearch = $.trim(mini.get("upSearch").getValue());
        var downSearch = $.trim(mini.get("downSearch").getValue());
        $.ajax({
            url: jsUseCtxPath + "/drbfm/single/getSingleSxmsList.do?partId=" + singleId +
                "&sxms=" + sxms + "&functionDesc=" + functionDesc + "&requestDesc=" + requestDesc
                + "&jgfxxj=" + jgfxxj
                + "&upSearch=" + upSearch + "&downSearch=" + downSearch,
            success: function (result) {
                if (!result.success) {
                    mini.alert(result.message);
                } else {

                    var merges = [];

                    //各列单元格合并
                    if (result.mergeNum) {
                        for (var index = 0; index < result.mergeNum.length; index++) {
                            merges.push({
                                rowIndex: result.mergeNum[index].rowIndex,
                                columnIndex: 0,
                                rowSpan: result.mergeNum[index].rowSpan,
                                colSpan: 1
                            });
                        }
                    }
                    if (result.mergeNum) {
                        for (var index = 0; index < result.mergeNum.length; index++) {
                            merges.push({
                                rowIndex: result.mergeNum[index].rowIndex,
                                columnIndex: 1,
                                rowSpan: result.mergeNum[index].rowSpan,
                                colSpan: 1
                            });
                        }
                    }

                    if (result.mergeNum) {
                        for (var index = 0; index < result.mergeNum.length; index++) {
                            merges.push({
                                rowIndex: result.mergeNum[index].rowIndex,
                                columnIndex: 2,
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
                                columnIndex: 3,
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
                                columnIndex: 4,
                                rowSpan: result.mergeNum[index].rowSpan,
                                colSpan: 1
                            });
                        }
                    }
                    //结构分析上级的合并

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
                    //结构分析关注要素的合并

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
                    //结构分析 关注要素合并 【换列】
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
                    //功能分析要素的合并 功能分析上一较高级别
                    if (result.mergeNum) {
                        for (var index = 0; index < result.mergeNum.length; index++) {
                            merges.push({
                                rowIndex: result.mergeNum[index].rowIndex,
                                columnIndex: 9,
                                rowSpan: result.mergeNum[index].rowSpan,
                                colSpan: 1
                            });
                        }
                    }
                    //功能分析关注要素合并
                    if (result.mergeNum) {
                        for (var index = 0; index < result.mergeNum.length; index++) {
                            merges.push({
                                rowIndex: result.mergeNum[index].rowIndex,
                                columnIndex: 10,
                                rowSpan: result.mergeNum[index].rowSpan,
                                colSpan: 1
                            });
                        }
                    }
                    // //针对FM列的合并
                    // if(result.mergeNum) {
                    //     for(var index=0;index<result.mergeNum.length;index++) {
                    //         merges.push({ rowIndex: result.mergeNum[index].rowIndex, columnIndex: 11, rowSpan: result.mergeNum[index].rowSpan, colSpan: 1});
                    //     }
                    // }

                    sxmsRelListGrid.setData(result.data);
                    if (merges.length > 0) {
                        sxmsRelListGrid.mergeCells(merges);
                    }
                }
            }
        });
    }


    function clearListGrid() {
        mini.get("sxms").setValue('');
        mini.get("sxms").setText('');
        mini.get("functionDesc").setValue('');
        mini.get("functionDesc").setText('');
        mini.get("requestDesc").setValue('');
        mini.get("requestDesc").setText('');
        mini.get("upSearch").setValue('');
        mini.get("upSearch").setText('');
        mini.get("downSearch").setValue('');
        mini.get("downSearch").setText('');
        mini.get("jgfxxj").setValue('');
        loadListGrid();
    }


</script>
</body>
</html>
