<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/list.jsp" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>专利解读情况报表</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script src="${ctxPath}/scripts/mini/boot.js?static_res_version=${static_res_version}"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/share/dialog.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/list.js?static_res_version=${static_res_version}"
            type="text/javascript"></script>

    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>

    <script src="${ctxPath}/scripts/rdmZhgl/report/zljdValueOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/zljdNumOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/reCreateOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/zljdRankOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/report/reRankOverview.js?version=${static_res_version}"
            type="text/javascript"></script>


    <style>
        .gridster ul li header {
            background: none repeat scroll 0% 0% #f5f7fa;
            display: block;
            font-size: 20px;
            line-height: normal;
            padding: 4px 0px 6px;
            cursor: move;
            text-align: center;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }
    </style>
</head>
<body>
<div class="personalPort" style="min-width: 1679px;">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-col="1" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <p>专利解读数量统计</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="numTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdNumChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="numTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdNumChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="zljdNumBar" class="containerBox">
                </div>
            </li>

            <li class="gs-w" data-col="2" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <p>再创新专利申请量</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="reTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryreCreateChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="reTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryreCreateChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="reCreateBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="3" data-row="1" data-sizex="8" data-sizey="1">
                <header>
                    <p>专利应用价值统计</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="valueTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdValueChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="valueTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdValueChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="zljdValueBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="2" data-sizex="24" data-sizey="1">
                <header>
                    <p>各公司各专业技术分支专利布局</p>
                    <div class="searchBox">
                        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    专业类别
                    <input id="zlType" name="zlType" class="mini-combobox"
                           style="width: 115px;font-size: 10px;height: 30px"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"
                           valueField="key" textField="value" onvaluechanged="searchFrm()"/>
                    </span>
                        </form>
                    </div>
                </header>
                <div id="ipcNumListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                     allowCellWrap="true"
                     showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
                     oncellvalidation="onCellValidation"
                     allowHeaderWrap="true"
                     url="${ctxPath}/zljd/core/overview/queryIPCNum"
                     multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50"
                     allowAlternating="true">
                    <div property="columns">
                        <div name="description" field="description" width="120" headerAlign="center" align="center"
                             allowSort="true">
                            技术分支
                        </div>
                        <div field="IPCMainNo" width="80" headerAlign="center" align="center" allowSort="true">
                            IPC主分类号
                        </div>
                        <div field="catNum" width="60" headerAlign="center" align="center" allowSort="true">
                            卡特专利数量
                        </div>
                        <div field="sanyNum" width="60" headerAlign="center" align="center" allowSort="true">
                            三一专利数量
                        </div>
                        <div field="xcmgNum" width="60" headerAlign="center" align="center" allowSort="true" renderer="statusRenderer">
                            徐工专利数量
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="4" data-sizex="24" data-sizey="1">
                <header>
                    <p>专利解读标兵</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="zljdrankTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdRankChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="zljdrankTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZljdRankChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="zljdRankBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="5" data-sizex="24" data-sizey="1">
                <header>
                    <p>专利再创新标兵</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="rerankTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryReRankChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="rerankTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryReRankChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="reRankBar" class="containerBox">
                </div>
            </li>

            <li class="gs-w" data-col="17" data-row="3" data-sizex="24" data-sizey="1">
                <%--<header>--%>
                <%--<p>卡特彼勒各技术分支发展趋势</p>--%>
                <%--<span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">--%>
                <%--专业类别--%>
                <%--<input id="professionalCategory" name="professionalCategory" class="mini-combobox"--%>
                <%--style="width: 115px;font-size: 10px;height: 30px"--%>
                <%--url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"--%>
                <%--valueField="key" textField="value" onvaluechanged="queryApplyChartData()"/>--%>
                <%--</span>--%>
                <%--</header>--%>
                <%--<div id="zlApplyBar" class="containerBox">--%>
                <%--</div>--%>
                <header>
                    <p>卡特彼勒各技术分支发展趋势</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    专业类别
                    <input id="professionalCategory" name="professionalCategory" class="mini-combobox"
                           style="width: 115px;font-size: 10px;height: 30px"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=professionalCategory"
                           valueField="key" textField="value" onvaluechanged="queryNewApplyNum()"/>
                    </span>
                </header>
                <div id="newApplyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                     allowCellWrap="true"
                     showCellTip="true" idField="id" allowCellEdit="true" allowCellSelect="true"
                     oncellvalidation="onCellValidation"
                     allowHeaderWrap="true"
                     multiSelect="true" showColumnsMenu="false" sizeList="[50,100]" pageSize="50"
                     allowAlternating="true">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var zljdValueBar = echarts.init(document.getElementById('zljdValueBar'));
    var zljdNumBar = echarts.init(document.getElementById('zljdNumBar'));
    var reCreateBar = echarts.init(document.getElementById('reCreateBar'));
    var zljdRankBar = echarts.init(document.getElementById('zljdRankBar'));
    var reRankBar = echarts.init(document.getElementById('reRankBar'));
    // var zlApplyBar = echarts.init(document.getElementById('zlApplyBar'));
    var ipcNumListGrid = mini.get("ipcNumListGrid");
    var newApplyListGrid = mini.get("newApplyListGrid");
    ipcNumListGrid.on("load", function () {
        ipcNumListGrid.mergeColumns(["description"]);
    });

    $(function () {
        zlTypeInit();
    });

    function zlTypeInit() {
        var nowDate = new Date();
        var columns = newApplyListGrid.getColumns();
        var nowYear = nowDate.getFullYear();
        var startTime ="2016";
        var technical = { header: "技术分支", field: "technialName", width: 100, headerAlign: "center",align:"center"};
        columns.push(technical);
        for(var start=2016;start<=nowYear;start++){
            var column = { header: start+"年申请量", field: start, width: 60, headerAlign: "center",align:"center"};
            columns.push(column);
        }
        var sum = { header: "合计", field: "sum", width: 60, headerAlign: "center",align:"center"};
        columns.push(sum);
        newApplyListGrid.setColumns(columns);
        mini.get("professionalCategory").setValue("控制");
        mini.get("professionalCategory").setText("控制");
        mini.get("zlType").setValue("控制");
        mini.get("zlType").setText("控制");
        queryNewApplyNum();
        searchFrm();
    }

    function queryNewApplyNum() {
        var zlType = mini.get("professionalCategory").getText();
        $.ajax({
            url: jsUseCtxPath + '/zljd/core/overview/queryNewApplyNum.do?professionalCategory=' + zlType,
            contentType: 'application/json',
            success: function (resp) {
                if (!resp.success) {
                    return;
                }
                let returnData = resp.data;
                if (returnData) {
                    newApplyListGrid.setData(returnData);
                }
            }
        });
    }


    function overview() {
        var width = (1679 - 3 * 8) / 24 - 8;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 480],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 24,
            widget_margins: [8, 8],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        gridster.disable();
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

    function statusRenderer(e) {
        var record = e.record;
        var xcmgNum = record.xcmgNum;
        var arr = [{'key': '0', 'value': '0(数据待维护)'}
        ];

        return $.formatItemValue(arr,xcmgNum);
    }
</script>
<redxun:gridScript gridId="ipcNumListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
