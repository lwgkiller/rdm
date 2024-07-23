<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的门户</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>
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

        .gridster ul li div.containerBox2 {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
        }

        <%--迁入：外购件资料收集及制作完成率--%>
        .gridster ul li div.wgjzlsjBox {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster" style="min-width: 1800px;">
        <ul>
            <%--chat1:总体情况(1-1)--%>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="1" data-sizex="3"
            >
                <header class="deskHome-header">
                    <span style="float:left;font-size: 13px;color: #333;vertical-align: middle">
                        部门名称：
                         <input style="width:120px" class="mini-textbox" id="departName" name="departName"/>
                        开始期：

						<input id="yearMonthBegin" allowinput="false" class="mini-datepicker"
                               style="width:120px" name="yearMonthBegin"/>
                        结束期：
						<input id="yearMonthEnd" allowinput="false" class="mini-datepicker"
                               style="width:120px" name="yearMonthEnd"/>
					</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        <a class="mini-button" onclick="initChart1()">查询</a>
                    </span>
                </header>
                <div id="chartOne" style="width: 700px;height:380px;"></div>
            </li>

            <%--<li class="gs-w" data-row="1" data-sizey="3" data-col="4" data-sizex="3"--%>
            <%-->--%>
                <%--<header class="deskHome-header">--%>
                <%--<span style="float:left;font-size: 13px;color: #333;vertical-align: middle">--%>
                    <%--部门名称：--%>
                     <%--<input style="width:120px" class="mini-textbox" id="departNameChartTwo" name="departNameChartTwo"/>--%>
                    <%--开始期：--%>
                    <%--<input id=" " allowinput="false" class="mini-datepicker"--%>
                           <%--style="width:120px" name="chatrTwoStartTime"/>--%>
                    <%--结束期：--%>
                    <%--<input id="chartTwoEndTime" allowinput="false" class="mini-datepicker"--%>
                           <%--style="width:120px" name="chartTwoEndTime"/>--%>
                <%--</span>--%>
                    <%--<span style="float:right;font-size: 13px;color: #333;vertical-align: middle">--%>
                    <%--<a class="mini-button" onclick="initChart2()">查询</a>--%>
                <%--</span>--%>
                <%--</header>--%>
                <%--<div id="chartTwo" style="width: 700px;height:400px;"></div>--%>
            <%--</li>--%>



        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var seKanbanAdmin = "${seKanbanAdmin}";
    var currentUserNo = "${currentUserNo}";
    var chartOne = echarts.init(document.getElementById('chartOne'));


    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 8,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

    $(function () {
        //数量类统计-通用
        // initChart1();
        //decoCompletion:装修手册需求完成情况(1-1)
        // iniDecoCompletion();

    })


    //generalSituation:总体情况(1-1)




    // 表1初始化
    function initChart1() {
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText();
        }
        else {
            mini.alert("请选择开始时间!");
            return;
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText();
        }
        else {
            mini.alert("请选择结束时间!");
            return;
        }
        if (mini.get("departName").getValue()) {
            departName = mini.get("departName").getText();
        }
        else {
            mini.alert("请输入部门名称!");
            return;
        }

        chartOne.clear();
        chartOneOption.title.text = '总览图：三类通知总下发量';

        var resultData = getChatOneData(yearMonthBegin, yearMonthEnd, departName);
        chartOneOption.dataset.source = resultData;
        chartOne.setOption(chartOneOption);
    }

    //获取表1数据
    function getChatOneData(yearMonthBegin, yearMonthEnd, departName) {
        var resultData = [];
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "departName": departName};
        $.ajax({
            url: jsUseCtxPath + '/pmd/report/reports/getPdmReportChartOne.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }


    // 表2初始化
    function initChart2() {
        chartTwo.clear();
        chartTwoOption.title.text = '总览图：三类通知总下发量';
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText();
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText();
        }
        if (mini.get("departName").getValue()) {
            departName = mini.get("departName").getText();
        }
        var resultData = getChatTwoData(yearMonthBegin, yearMonthEnd, departName);
        chartTwoOption.dataset.source = resultData;
        chartTwo.setOption(chartTwoOption);
    }

    //获取表1数据
    function getChatTwoData(yearMonthBegin, yearMonthEnd, departName) {
        var resultData = [];
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "departName": departName};
        $.ajax({
            url: jsUseCtxPath + '/pmd/report/reports/getPdmReportChatTwo.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                resultData = returnData;
            }
        })
        return resultData;
    }



    var myChart = echarts.init(document.getElementById('chartOne'));
    var chartOneOption = {
        title: {
            text: '总览图：三类通知总下发量'
        },
        legend: {
            data: ['销量']
        },
        tooltip: {

            //实际数据
            formatter: function (params) {
                var res = '<p>日期：' + params.data.yearMonth + '</p>';
                res += '<p>更改通知单：' + params.data.tzdCnt1 + '</p>';
                res += '<p>试制通知单：' + params.data.tzdCnt2 + '</p>';
                res += '<p>技术通知单：' + params.data.tzdCnt3 + '</p>';
                return res;
            }

        },
        dataset: {
            dimensions: ['yearMonth', 'tzdCnt1', 'tzdCnt2', 'tzdCnt3'],
            source: [
                ["2023-02", 1, 0, 1],
                ["2023-03", 1, 2, 3]
            ]

        },
        // xAxis: {type: 'category'},
        // 声明一个 X 轴，类目轴（category）。默认情况下，类目轴对应到 dataset 第一列。
        xAxis: {type: 'category'},
        // 声明一个 Y 轴，数值轴。
        yAxis: {},
        // 声明多个 bar 系列，默认情况下，每个系列会自动对应到 dataset 的每一列。
        series: [{type: 'bar'}, {type: 'bar'}, {type: 'bar'}]

    };
    myChart.setOption(chartOneOption);


</script>
</body>
</html>
