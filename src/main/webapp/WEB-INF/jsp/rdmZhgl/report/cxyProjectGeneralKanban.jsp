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

        .wgjzlsj {
            background: #fff;
        }

        .gridster ul li div.wgjzlsjBox {
            height: calc(100% - 40px);
            width: calc(48%);
            box-sizing: border-box;
            display: inline-block;
        }

        .mini-buttonedit-input {
            font-size: 13px;
        }

        .jfzb-jb {
            width: 43px;
            float: left;
            text-align: left;
            color: #333
        }

        .jfzb-fs {
            width: 44px;
            float: left;
            text-align: center;
            color: #333
        }

        .jfzb-zb {
            width: 51px;
            float: left;
            text-align: right;
            color: #333
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">产学研项目按项目性质统计</span>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        开始期：
						<input id="yearMonthBegin" allowinput="true" class="mini-monthpicker"
                               style="width:100px" name="yearMonthBegin" onValuechanged="yearMonthChange"/>
                        结束期：
						<input id="yearMonthEnd" allowinput="true" class="mini-monthpicker"
                               style="width:100px" name="yearMonthEnd" onValuechanged="yearMonthChange"/>
					</span>
                </header>
                <div id="byProjectProperties" class="containerBox">
                </div>
            </li>
        </ul>
        <ul>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">产学研项目按部门统计</span>
                </header>
                <div id="byDepartmentChart" class="containerBox">
                </div>
            </li>
        </ul>
        <ul>
            <li class="gs-w" data-row="7" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">产学研项目按完成情况统计</span>
                </header>
                <div id="byImplementationChart" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var byDepartmentChart = echarts.init(document.getElementById('byDepartmentChart'));
    var byImplementationChart = echarts.init(document.getElementById('byImplementationChart'));
    var byProjectPropertiesChart = echarts.init(document.getElementById('byProjectProperties'));
    var currentYear = "";
    var currentMonth = "";
    var currentYear2After = "";
    var totalCount;

    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }
    $(function () {
        var date = new Date();
//        if ((date.getMonth() + 1).toString().length == 1) {
//            currentMonth = "0" + (date.getMonth() + 1).toString();
//        } else {
//            currentMonth = (date.getMonth() + 1).toString();
//        }
//        currentYear = date.getFullYear().toString();
//        currentYear2After = (date.getFullYear() + 2).toString();
//        mini.get("yearMonthBegin").setValue(currentYear + "-01");
//        mini.get("yearMonthEnd").setValue(currentYear2After + "-" + currentMonth);
        iniTotal();
        iniByProjectPropertiesChart();
        iniByDepartmentChart();
        iniByImplementationChart();
    })
    function yearMonthChange() {
        iniTotal();
        iniByDepartmentChart();
        iniByImplementationChart();
    }

    //获取看板数据
    function getKanbanData(yearMonthBegin, yearMonthEnd, action) {
        var resultData = [];
        var postData = {"yearMonthBegin": yearMonthBegin, "yearMonthEnd": yearMonthEnd, "action": action};
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/cxyGeneralKanban/getKanbanData.do',
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
    //初始化产学研项目总数
    function iniTotal() {
        var postData = {
            "yearMonthBegin": mini.get("yearMonthBegin").getValue() ? mini.get("yearMonthBegin").getText() + "-01" : "",
            "yearMonthEnd": mini.get("yearMonthEnd").getValue() ? mini.get("yearMonthEnd").getText() + "-31" : ""
        };
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/cxyGeneralKanban/getTotal.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (total) {
                totalCount = total;
            }
        })
    }

    //初始化性质柱状图
    function iniByProjectPropertiesChart() {
        byProjectPropertiesChart.clear();
        optionByProjectProperties.title.text = '总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "byProjectProperties");
        optionByProjectProperties.dataset.source = resultData;
        byProjectPropertiesChart.setOption(optionByProjectProperties);
    }
    //性质柱状图容器
    var optionByProjectProperties = {
        title: {text: ''},
        legend: {
            right: '15%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['项目性质', '数量',],
            source: [
                {日期: '111', 数量: 111}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                fontSize: '15',
                rotate: '22'
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    byProjectPropertiesChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (byProjectPropertiesChart.containPixel('grid', pointInPixel)) {
            var xIndex = byProjectPropertiesChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
            var option = byProjectPropertiesChart.getOption();
            var clickProjectProperties = option.dataset[0].source[xIndex].项目性质;
            var yearMonthBegin = '';
            var yearMonthEnd = '';
            if (mini.get("yearMonthBegin").getValue()) {
                yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
            }
            if (mini.get("yearMonthEnd").getValue()) {
                yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
            }
            var url = jsUseCtxPath + "/zhgl/core/cxy/dataListPage.do?clickProjectProperties=" + clickProjectProperties +
                "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd;
            window.open(url);
        }
    });


    //初始化部门柱状图
    function iniByDepartmentChart() {
        byDepartmentChart.clear();
        optionByDepartment.title.text = '总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "byDepartment");
        optionByDepartment.dataset.source = resultData;
        byDepartmentChart.setOption(optionByDepartment);
    }
    //部门柱状图容器
    var optionByDepartment = {
        title: {text: ''},
        legend: {
            right: '15%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['部门', '数量',],
            source: [
                {日期: '111', 数量: 111}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                fontSize: '15',
                rotate: '22'
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    byDepartmentChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (byDepartmentChart.containPixel('grid', pointInPixel)) {
            var xIndex = byDepartmentChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
            var option = byDepartmentChart.getOption();
            var clickDepartment = option.dataset[0].source[xIndex].部门;
            var yearMonthBegin = '';
            var yearMonthEnd = '';
            if (mini.get("yearMonthBegin").getValue()) {
                yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
            }
            if (mini.get("yearMonthEnd").getValue()) {
                yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
            }
            var url = jsUseCtxPath + "/zhgl/core/cxy/dataListPage.do?clickDepartment=" + clickDepartment +
                "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd;
            window.open(url);
        }
    });


    //初始化完成情况柱状图
    function iniByImplementationChart() {
        byImplementationChart.clear();
        optionByImplementation.title.text = '总数： ' + totalCount;
        var yearMonthBegin = '';
        var yearMonthEnd = '';
        if (mini.get("yearMonthBegin").getValue()) {
            yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
        }
        if (mini.get("yearMonthEnd").getValue()) {
            yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
        }
        var resultData = getKanbanData(yearMonthBegin, yearMonthEnd, "byImplementation");
        optionByImplementation.dataset.source = resultData;
        byImplementationChart.setOption(optionByImplementation);
    }
    //完成情况柱状图容器
    var optionByImplementation = {
        title: {text: ''},
        legend: {
            right: '15%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['完成情况', '数量',],
            source: [
                {日期: '111', 数量: 111}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                fontSize: '15',
                rotate: '22'
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#61a0a8',
                label: {
                    show: true,
                    position: 'top'
                }
            }
        ]
    };
    byImplementationChart.getZr().on('click', params => {
        var pointInPixel = [params.offsetX, params.offsetY]
        if (byImplementationChart.containPixel('grid', pointInPixel)) {
            var xIndex = byImplementationChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
            var option = byImplementationChart.getOption();
            var clickImplementation = option.dataset[0].source[xIndex].完成情况;
            var yearMonthBegin = '';
            var yearMonthEnd = '';
            if (mini.get("yearMonthBegin").getValue()) {
                yearMonthBegin = mini.get("yearMonthBegin").getText() + "-01";
            }
            if (mini.get("yearMonthEnd").getValue()) {
                yearMonthEnd = mini.get("yearMonthEnd").getText() + "-31";
            }
            var url = jsUseCtxPath + "/zhgl/core/cxy/dataListPage.do?clickImplementation=" + clickImplementation +
                "&yearMonthBegin=" + yearMonthBegin + "&yearMonthEnd=" + yearMonthEnd;
            window.open(url);
        }
    });
</script>
</body>
</html>
