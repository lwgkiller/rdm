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
    <%--迁入：外购件资料收集及制作完成率--%>
    <script src="${ctxPath}/scripts/serviceEngineering/purchasedPartsChart.js?version=${static_res_version}" type="text/javascript"></script>
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
            <%--generalSituation:总体情况(1-1)--%>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="1" data-sizex="3">
                <header class="deskHome-header">
                    <p>总体情况</p>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                    <a id="refreshCacheGenSit" class="mini-button" onclick="refreshCacheGenSit()" visible="false">刷新</a>
                    </span>
                </header>
                <div id="generalSituation" class="containerBox"></div>
            </li>
            <%--atDocAcpRate:随机文件完成率(1-2)--%>
            <li class="gs-w" data-row="1" data-sizey="3" data-col="4" data-sizex="5">
                <header class="deskHome-header">
                    <p>随机文件完成率</p>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
                        <input id="signYearAtDocAcpRate" name="signYearAtDocAcpRate"
                               class="mini-combobox" onValuechanged="signYearChangeAtDocAcpRate"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                        <a id="refreshCacheAtDocAcpRate" class="mini-button" onclick="refreshCacheAtDocAcpRate()" visible="false">刷新</a>
                    </span>
                </header>
                <div id="atDocAcpRate" class="containerBox"></div>
            </li>
            <%--exportAtDocReDisRate:出口产品随机文件补发完成率(2-1) todo--%>
            <li class="gs-w" data-row="4" data-sizey="3" data-col="1" data-sizex="3">
                <header class="deskHome-header">
                    <p>出口产品补发随机文件完成率</p>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                    <a id="refreshCacheAtDocReDisRate" class="mini-button" onclick="refreshCacheAtDocReDisRate()" visible="false">刷新</a>
                    </span>
                </header>
                <div>
                    <%--零件图册--%>
                    <div id="partsAtlasReDisRate" class="containerBox2"></div>
                    <%--操保手册--%>
                    <div id="maintManualReDisRate" class="containerBox2"></div>
                </div>
            </li>
            <%--exportAtDocAcpRate:出口产品随机文件完成率(2-2)--%>
            <li class="gs-w" data-row="4" data-sizey="3" data-col="4" data-sizex="5">
                <header class="deskHome-header">
                    <p>出口产品随机文件完成率</p>
                    <span style="float:right;font-size: 13px;color: #333;vertical-align: middle">
                        年份：
                        <input id="signYearExportAtDocAcpRate" name="signYearExportAtDocAcpRate"
                               class="mini-combobox" onValuechanged="signYearChangeExAtDocAcpRate"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=signYear"
                               valueField="key" textField="value"/>
                    </span>
                </header>
                <div id="exportAtDocAcpRate" class="containerBox"></div>
            </li>
            <%--decoCompletion:装修手册需求完成情况(3-1)--%>
            <li class="gs-w" data-row="7" data-sizey="3" data-col="1" data-sizex="3">
                <header class="deskHome-header">
                    <p>装修手册需求完成情况&nbsp;&nbsp;&nbsp;</p>
                    <span style="float:left;font-size:13px;color:#333;vertical-align:middle">
                        <div style="float:left;color:#0c7aa8;" id="decoAmount">累计发布装修手册数量:</div><br/>
                        <div style="float:left;color:#0c7aa8;" id="decoSubAmount">累计发布分子手册数量:</div>
                    </span>
                </header>
                <div id="decoCompletion" class="containerBox"></div>
            </li>
            <%--maintTransRate:中文操保手册英文译本翻译率(3-2)--%>
            <li class="gs-w" data-row="7" data-sizey="3" data-col="4" data-sizex="2">
                <header class="deskHome-header">
                    <p>中文操保手册英文译本翻译率</p>
                </header>
                <div id="maintTransRate" class="containerBox"></div>
            </li>
            <%--manualMinorLanguage:小语种手册数量(3-3)--%>
            <li class="gs-w" data-row="7" data-sizey="3" data-col="6" data-sizex="3">
                <header class="deskHome-header">
                    <p>小语种手册数量</p>
                </header>
                <div>
                    <%--操保手册--%>
                    <div id="maintManualMinorLang" class="containerBox2"></div>
                    <%--装修手册--%>
                    <div id="decoManualMinorLang" class="containerBox2"></div>
                </div>
            </li>
            <%--迁入：外购件资料收集及制作完成率--%>
            <li class="gs-w" data-row="9" data-sizey="3" data-col="1" data-sizex="3">
                <header class="deskHome-header">
                    <span style="float:left;font-size: 20px;color: #333;vertical-align: middle">外购件资料收集率及制作完成率</span>
                </header>
                <div clas="wgjzlsj">
                    <%--收集率--%>
                    <div id="sjlChart" class="wgjzlsjBox"></div>
                    <%--制作率--%>
                    <div id="zzlChart" class="wgjzlsjBox"></div>
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var seKanbanAdmin = "${seKanbanAdmin}";
    var currentUserNo = "${currentUserNo}";
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
        mini.get("signYearExportAtDocAcpRate").setValue(new Date().getFullYear().toString());
        mini.get("signYearAtDocAcpRate").setValue(new Date().getFullYear().toString());
        //数量类统计-通用
        iniAmount();
        //decoCompletion:装修手册需求完成情况(1-1)
        iniDecoCompletion();
        //maintTransRate:中文操保手册英文译本翻译率(1-2)
        iniMaintTransRate();
        //manualMinorLanguage:小语种手册数量(1-3)
        iniMaintManualMinorLang();//操保
        iniDecoManualMinorLang();//装修
        //generalSituation:总体情况(2-1)
        iniGeneralSituation();
        //exportAtDocAcpRate:出口产品随机文件完成率(2-2)
        iniExportAtDocAcpRate();
        //exportAtDocReDisRate:出口产品随机文件补发完成率(3-1)todo
        iniPartsAtlasReDisRate();//零件
        iniMaintManualReDisRate();//操保
        //atDocAcpRate:随机文件完成率(3-2)
        iniAtDocAcpRate();
        if (currentUserNo == 'admin' || currentUserNo == seKanbanAdmin) {
            mini.get("refreshCacheGenSit").setVisible(true);
            mini.get("refreshCacheAtDocReDisRate").setVisible(true);
            mini.get("refreshCacheAtDocAcpRate").setVisible(true);
        }
    })
    function signYearChangeExAtDocAcpRate() {
        iniExportAtDocAcpRate();
    }
    function signYearChangeAtDocAcpRate() {
        iniAtDocAcpRate();
    }
    function refreshCacheGenSit() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/refreshCacheGenSit.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    iniGeneralSituation();
                } else {
                    mini.alert("刷新缓存失败:" + returnData.message)
                }
            }
        })
    }
    function refreshCacheAtDocAcpRate() {
        var signYear = '';
        if (mini.get("signYearAtDocAcpRate").getValue()) {
            signYear = mini.get("signYearAtDocAcpRate").getText();
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/refreshCacheAtDocAcpRate.do?signYear=' + signYear,
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    iniAtDocAcpRate();
                } else {
                    mini.alert("刷新缓存失败:" + returnData.message)
                }
            }
        })
    }
    function refreshCacheAtDocReDisRate() {
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/refreshCacheAtDocReDisRate.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData.success) {
                    iniPartsAtlasReDisRate();
                    iniMaintManualReDisRate();
                } else {
                    mini.alert("刷新缓存失败:" + returnData.message)
                }
            }
        })
    }
    //数量类统计-通用
    var decoAmount;//累计发布装修手册数量
    var decoSubAmount;//累计发布分子手册数量
    function iniAmount() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getAmount.do',
            type: 'POST',
            async: false,
            contentType: 'application/json',
            data: mini.encode(postData),
            success: function (returnData) {
                decoAmount = returnData.decoAmount;
                decoSubAmount = returnData.decoSubAmount;
            }
        })
    }

    //decoCompletion:装修手册需求完成情况(3-1)
    var decoCompletion = echarts.init(document.getElementById('decoCompletion'));
    decoCompletion.on('click', function (params) {
        var url = jsUseCtxPath + "/serviceEngineering/core/decorationManualFile/dataListPage.do";
        window.open(url);
    })
    function getDecoCompletionData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getDecoCompletionData.do',
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
    function iniDecoCompletion() {
        decoCompletion.clear();
        var resultData = getDecoCompletionData();
        decoCompletionOption.dataset = resultData;
        decoCompletionOption.title.text = '累计发布装修手册数量: ' + decoAmount + '\n' +
            '累计发布分子手册数量: ' + decoSubAmount;
        decoCompletion.setOption(decoCompletionOption);
        document.getElementById("decoAmount").innerHTML =
            document.getElementById("decoAmount").innerHTML + "(" + decoAmount + ")";
        document.getElementById("decoSubAmount").innerHTML =
            document.getElementById("decoSubAmount").innerHTML + "(" + decoSubAmount + ")";
    }
    var decoCompletionOption = {
        title: {
            text: '',
            textStyle: {
                fontSize: 13,
                lineHeight: 0
            }
        },
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>' + params.name + '</p>';
                res += '<p>需求制作：' + params.data.需求制作 + '</p>';
                res += '<p>已完成：' + params.data.已完成 + '</p>';
                var percent = (params.data.已完成 / params.data.需求制作 * 100).toFixed(2);
                res += '<p>完成率：' + percent + '%<p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['销售区域', '需求制作', '已完成'],
            source: [
                {销售区域: '', 需求制作: 0, 已完成: 0},
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };

    //maintTransRate:中文操保手册英文译本翻译率(3-2)
    var maintTransRate = echarts.init(document.getElementById('maintTransRate'));
    function getMaintTransRateData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getMaintTransRateData.do',
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
    function iniMaintTransRate() {
        maintTransRate.clear();
        var resultData = getMaintTransRateData();
        maintTransRateOption.series[0].data = resultData;
        maintTransRate.setOption(maintTransRateOption);
    }
    var maintTransRateOption = {
        title: {text: ''},
        tooltip: {
            trigger: 'item',
            formatter: function (param) {
                var res = '总数：' + param.data.total +
                    '<br/>' + param.data.name + '：' + param.data.value
                if (param.data.name == '已完成') {
                    res += '<br/>完成率：' + param.percent + '%';
                } else if (param.data.name == '待完成') {
                    res += '<br/>未完成率：' + param.percent + '%';
                }
                return res
            }
        },
        legend: {
            orient: 'horizontal',
            right: '10%'
        },
        color: ['#0c7aa8', '#d4af60'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "50%"],
                label: {
                    normal: {
                        formatter: '{b}：{c}个',
                        position: 'outside'
                    }
                },
                data: [
                    {value: 0, name: '已完成', total: 0},
                    {value: 0, name: '待完成', total: 0}
                ]
            }
        ]
    };

    //manualMinorLanguage:小语种手册数量(3-3)
    var maintManualMinorLang = echarts.init(document.getElementById('maintManualMinorLang'));//操保
    function getMaintManualMinorLangData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getMaintManualMinorLangData.do',
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
    function iniMaintManualMinorLang() {
        maintManualMinorLang.clear();
        var resultData = getMaintManualMinorLangData();
        maintManualMinorLangOption.series[0].data = resultData;
        maintManualMinorLang.setOption(maintManualMinorLangOption);
    }
    var maintManualMinorLangOption = {
        title: {text: '操保手册', x: '0', y: '40'},
        tooltip: {
            trigger: 'item',
            formatter: function (param) {
                var res = param.data.name + '：' + param.data.value + '个' +
                    '<br/>占比：' + param.percent + '%';
                return res
            }
        },
        legend: {
            orient: 'horizontal',
            type: 'scroll'
        },
        color: ['#0c7aa8', '#d4af60', '#478500', '#5667A8', '#A85044'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "55%"],
                label: {show: true, position: 'inner'},
                data: [
                    {value: 0, name: '某语种1'},
                    {value: 0, name: '某语种2'},
                    {value: 0, name: '某语种3'},
                    {value: 0, name: '某语种4'},
                    {value: 0, name: '某语种5'},
                    {value: 0, name: '某语种6'},
                    {value: 0, name: '某语种7'}
                ]
            }
        ]
    };

    var decoManualMinorLang = echarts.init(document.getElementById('decoManualMinorLang'));//装修
    function getDecoManualMinorLangData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getDecoManualMinorLangData.do',
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
    function iniDecoManualMinorLang() {
        decoManualMinorLang.clear();
        var resultData = getDecoManualMinorLangData();
        decoManualMinorLangOption.series[0].data = resultData;
        decoManualMinorLang.setOption(decoManualMinorLangOption);
    }
    var decoManualMinorLangOption = {
        title: {text: '装修手册', x: '0', y: '40'},
        tooltip: {
            trigger: 'item',
            formatter: function (param) {
                var res = param.data.name + '：' + param.data.value + '个' +
                    '<br/>占比：' + param.percent + '%';
                return res
            }
        },
        legend: {
            orient: 'horizontal',
            type: 'scroll'
        },
        color: ['#0c7aa8', '#d4af60', '#478500', '#5667A8', '#A85044'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "55%"],
                label: {show: true, position: 'inner'},
                data: [
                    {value: 0, name: '某语种1'},
                    {value: 0, name: '某语种2'},
                    {value: 0, name: '某语种3'}
                ]
            }
        ]
    };

    //generalSituation:总体情况(1-1)
    var generalSituation = echarts.init(document.getElementById('generalSituation'));
    var accumulatedInstance;//覆盖的实例图册总数量,随时渲染用的
    function getGeneralSituationData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getGeneralSituationData.do',
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
    function iniGeneralSituation() {
        generalSituation.clear();
        var resultData = getGeneralSituationData();
        accumulatedInstance = resultData.accumulatedInstance;
        generalSituationOption.dataset = resultData;
        generalSituationOption.title.text = '覆盖的实例图册总数量:' + resultData.accumulatedInstance;
        generalSituation.setOption(generalSituationOption);
    }
    var generalSituationOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {},
        dataset: {
            dimensions: ['图册类型', '数量'],
            source: [
                {图册类型: 'A', 数量: 10},
                {图册类型: 'B', 数量: 20},
                {图册类型: 'C', 数量: 10},
                {图册类型: 'D', 数量: 130}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                barWidth: '50%',
                label: {
                    show: true,
                    position: 'inside',
                    formatter: function (params) {
                        if (params.name == '零件图册') {
                            var res = params.data.数量 + '\n' + '覆盖实例: ' + accumulatedInstance + '';
                        } else {
                            var res = params.data.数量;
                        }
                        return res;
                    }
                }
            }
        ]
    };

    //exportAtDocAcpRate:出口产品随机文件完成率(2-2)
    var exportAtDocAcpRate = echarts.init(document.getElementById('exportAtDocAcpRate'));
    function getExportAtDocAcpRateData(signYear) {
        var resultData;
        var postData = {"signYear": signYear};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getExportAtDocAcpRateData.do',
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
    function iniExportAtDocAcpRate() {
        var signYear = '';
        if (mini.get("signYearExportAtDocAcpRate").getValue()) {
            signYear = mini.get("signYearExportAtDocAcpRate").getText();
        }
        exportAtDocAcpRate.clear();
        var resultData = getExportAtDocAcpRateData(signYear);
        exportAtDocAcpRateOption.dataset = resultData;
        exportAtDocAcpRate.setOption(exportAtDocAcpRateOption);
    }
    var exportAtDocAcpRateOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>月份：' + params.data.月份 + '</p>';
                res += '<p>月度发运产品总数：' + params.data.月度发运产品总数 + '' + '</p>';
                var percent = (params.data.实例图册发布数量 / params.data.月度发运产品总数 * 100).toFixed(2);
                res += '<p>实例图册发布数量：' + params.data.实例图册发布数量 + '(完成率:' + percent + '%)</p>';
                percent = (params.data.操保手册发布数量 / params.data.月度发运产品总数 * 100).toFixed(2);
                res += '<p>操保手册发布数量：' + params.data.操保手册发布数量 + '(完成率:' + percent + '%)</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['月份', '月度发运产品总数', '实例图册发布数量', '操保手册发布数量'],
            source: [
                {月份: '1月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '2月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '3月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '4月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110},
                {月份: '5月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '6月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '7月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '8月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110},
                {月份: '9月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '10月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '11月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '12月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                label: {
                    show: true,
                    position: 'inside',
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#17a884',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };

    //exportAtDocReDisRate:出口产品随机文件补发完成率(2-1)
    var partsAtlasReDisRate = echarts.init(document.getElementById('partsAtlasReDisRate'));//零件
    function getPartsAtlasReDisRateData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getPartsAtlasReDisRateData.do',
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
    function iniPartsAtlasReDisRate() {
        partsAtlasReDisRate.clear();
        var resultData = getPartsAtlasReDisRateData();
        partsAtlasReDisRateOption.series[0].data = resultData;
        partsAtlasReDisRate.setOption(partsAtlasReDisRateOption);
    }
    var partsAtlasReDisRateOption = {
        title: {text: '零件图册', x: '0', y: '40'},
        tooltip: {
            trigger: 'item',
            formatter: function (param) {
                var res = '总数：' + param.data.total +
                    '<br/>' + param.data.name + '：' + param.data.value
                if (param.data.name == '已补发') {
                    res += '<br/>补发率：' + param.percent + '%';
                } else if (param.data.name == '需补发') {
                    res += '<br/>未补发率：' + param.percent + '%';
                }
                return res
            }
        },
        legend: {
            orient: 'horizontal',
            type: 'scroll'
        },
        color: ['#0c7aa8', '#d4af60'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "55%"],
                label: {
                    normal: {
                        formatter: '{b}：{c}',
                        position: 'inner'
                    }
                },
                data: [
                    {value: 0, name: '已补发', total: 0},
                    {value: 0, name: '需补发', total: 0}
                ]
            }
        ]
    };

    var maintManualReDisRate = echarts.init(document.getElementById('maintManualReDisRate'));//装修
    function getMaintManualReDisRateData() {
        var resultData;
        var postData = {};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getMaintManualReDisRateData.do',
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
    function iniMaintManualReDisRate() {
        maintManualReDisRate.clear();
        var resultData = getMaintManualReDisRateData();
        maintManualReDisRateOption.series[0].data = resultData;
        maintManualReDisRate.setOption(maintManualReDisRateOption);
    }
    var maintManualReDisRateOption = {
        title: {text: '操保手册', x: '0', y: '40'},
        tooltip: {
            trigger: 'item',
            formatter: function (param) {
                var res = '总数：' + param.data.total +
                    '<br/>' + param.data.name + '：' + param.data.value
                if (param.data.name == '已补发') {
                    res += '<br/>补发率：' + param.percent + '%';
                } else if (param.data.name == '需补发') {
                    res += '<br/>未补发率：' + param.percent + '%';
                }
                return res
            }
        },
        legend: {
            orient: 'horizontal',
            type: 'scroll'
        },
        color: ['#0c7aa8', '#d4af60'],
        series: [
            {
                type: 'pie',
                radius: '75%',
                center: ["50%", "55%"],
                label: {
                    normal: {
                        formatter: '{b}：{c}',
                        position: 'inner'
                    }
                },
                data: [
                    {value: 0, name: '已补发', total: 0},
                    {value: 0, name: '需补发', total: 0}
                ]
            }
        ]
    };

    //atDocAcpRate:随机文件完成率(1-2)
    var atDocAcpRate = echarts.init(document.getElementById('atDocAcpRate'));
    function getAtDocAcpRateData(signYear) {
        var resultData;
        var postData = {"signYear": signYear};
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/seGeneralKanbanNew/getAtDocAcpRateData.do',
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
    function iniAtDocAcpRate() {
        var signYear = '';
        if (mini.get("signYearAtDocAcpRate").getValue()) {
            signYear = mini.get("signYearAtDocAcpRate").getText();
        }
        atDocAcpRate.clear();
        var resultData = getAtDocAcpRateData(signYear);
        atDocAcpRateOption.dataset = resultData;
        atDocAcpRate.setOption(atDocAcpRateOption);
    }
    var atDocAcpRateOption = {
        title: {text: ''},
        legend: {
            right: '10%'
        },
        tooltip: {
            formatter: function (params) {
                var res = '<p>月份：' + params.data.月份 + '</p>';
                res += '<p>月度发运产品总数：' + params.data.月度发运产品总数 + '' + '</p>';
                var percent = (params.data.实例图册发布数量 / params.data.月度发运产品总数 * 100).toFixed(2);
                res += '<p>实例图册发布数量：' + params.data.实例图册发布数量 + '(完成率:' + percent + '%)</p>';
                percent = (params.data.操保手册发布数量 / params.data.月度发运产品总数 * 100).toFixed(2);
                res += '<p>操保手册发布数量：' + params.data.操保手册发布数量 + '(完成率:' + percent + '%)</p>';
                return res;
            }
        },
        dataset: {
            dimensions: ['月份', '月度发运产品总数', '实例图册发布数量', '操保手册发布数量'],
            source: [
                {月份: '1月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '2月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '3月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '4月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110},
                {月份: '5月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '6月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '7月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '8月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110},
                {月份: '9月', 月度发运产品总数: 100, 实例图册发布数量: 99, 操保手册发布数量: 55},
                {月份: '10月', 月度发运产品总数: 200, 实例图册发布数量: 190, 操保手册发布数量: 95},
                {月份: '11月', 月度发运产品总数: 100, 实例图册发布数量: 90, 操保手册发布数量: 50},
                {月份: '12月', 月度发运产品总数: 130, 实例图册发布数量: 120, 操保手册发布数量: 110}
            ]
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
                formatter: function (params) {
                    if (params != null && params != '') {
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
                }
            }
        },
        yAxis: {},
        series: [
            {
                type: 'bar',
                barGap: '0%',
                color: '#0c7aa8',
                label: {
                    show: true,
                    position: 'inside',
                    formatter: function (params) {
//                        if (params.name == '零件图册') {
//                            var res = params.data.数量 + '\n' + '覆盖实例: ' + accumulatedInstance + '';
//                        } else {
//                            var res = params.data.数量;
//                        }
//                        return res;
                    }
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#d4af60',
                label: {
                    show: true,
                    position: 'inside'
                }
            },
            {
                type: 'bar',
                barGap: '0%',
                color: '#17a884',
                label: {
                    show: true,
                    position: 'inside'
                }
            }
        ]
    };

    //<%--迁入：外购件资料收集及制作完成率--%>
    var sjlChart = echarts.init(document.getElementById('sjlChart'));
    var zzlChart = echarts.init(document.getElementById('zzlChart'));
</script>
</body>
</html>
