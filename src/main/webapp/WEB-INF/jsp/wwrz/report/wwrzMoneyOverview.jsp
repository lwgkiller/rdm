<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>认证费用报表</title>
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
    <script src="${ctxPath}/scripts/wwrz/report/wwrzOverview_rzProjectNum.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/wwrz/report/wwrzOverview_money.js?version=${static_res_version}"
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
            <li class="gs-w" data-col="1" data-row="1" data-sizex="6" data-sizey="1">
                <header>
                    <p>费用总览</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
                       统计年份：
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reloadData"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div id="report" class="containerBox">
                    <div style="text-align: center;height: 100px;line-height: 80px;position: inherit">
                        <span style="font-size: x-large">
                             <span id="year" style="font-size: larger;background-color: orange"></span>年共发生整机委外认证项目
                            <span id="totalNum" style="font-size: larger;background-color: #29a5bf"></span>项共产生认证费用
                            <span id="totalMoney" style="font-size: larger;background-color: #29a5bf"></span>万元，已支付
                            <span id="payMoney"style="font-size: larger;background-color: #29a5bf"></span>万元。
                        </span>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="2" data-sizex="6" data-sizey="3">
                <header>
                    <p>认证项目统计</p>
                </header>
                <div id="projectNum" class="containerBox">
                </div>
            </li>
            <ul>
                <li class="gs-w" data-col="1" data-row="5" data-sizex="6" data-sizey="3">
                    <header>
                        <p>认证费用统计</p>
                    </header>
                    <div id="money" class="containerBox">
                    </div>
                </li>
            </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var projectNumChart = echarts.init(document.getElementById('projectNum'));
    var moneyChart = echarts.init(document.getElementById('money'));
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    var userName = "${userName}";
    $(function () {
        var year = new Date().getFullYear();
        mini.get('reportYear').setValue(year);
        initRzProjectNumChart();
        initProjectMoneyChart();
        initMoneyView();
    })

    function reloadData() {
        initRzProjectNumChart();
        initProjectMoneyChart();
        initMoneyView();
    }

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
    function initMoneyView() {
        var reportYear = '';
        if (mini.get("reportYear").getValue()) {
            reportYear = mini.get("reportYear").getText();
        }else{
            reportYear = new Date().getFullYear();
        }
        var resultDate = getProjectMoneyViewData(reportYear);
        $('#year').html(reportYear);
        $('#totalNum').html(resultDate.totalNum);
        $('#totalMoney').html(resultDate.totalMoney.toFixed(2));
        $('#payMoney').html(resultDate.payMoney.toFixed(2));
    }
    function getProjectMoneyViewData(reportYear) {
        var resultDate = {};
        $.ajax({
            url: jsUseCtxPath + '/wwrz/core/report/reportMoneyViewData.do?reportYear=' + reportYear,
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    resultDate = returnData.data;
                } else {
                    mini.alert(returnData.message);
                }
            }
        })
        return resultDate;
    }
</script>
</body>
</html>
