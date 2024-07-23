<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/list.jsp" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>会议管理情况报表</title>
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

    <script src="${ctxPath}/scripts/hygl/hyTypeOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/hygl/meetingDelayOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/hygl/rwfjFinishOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/hygl/zrMeetingDelayOverview.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/hygl/zrRwfjFinishOverview.js?version=${static_res_version}"
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
                    <p>各类型会议数量</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    会议时间从
                    <input id="numTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryHyTypeChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="numTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryHyTypeChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="hyTypeBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="3" data-row="1" data-sizex="16" data-sizey="1">
                <header>
                    <p>组织部门会议纪要超期统计(轻微<=7天，一般<=14天，严重>14天)</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    创建时间从
                    <input id="riskTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryMeetingDelayChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="riskTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryMeetingDelayChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="meetingDelayBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="5" data-sizex="24" data-sizey="1">
                <header>
                    <p>组织部门会议纪要完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="finishTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryRwfjFinishChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="finishTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryRwfjFinishChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="rwfjFinishBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="10" data-sizex="24" data-sizey="1">
                <header>
                    <p>责任部门会议纪要超期统计(轻微<=7天，一般<=14天，严重>14天)</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    创建时间从
                    <input id="zrRiskTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZRMeetingDelayChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="zrRiskTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZRMeetingDelayChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="zrMeetingDelayBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="17" data-row="15" data-sizex="24" data-sizey="1">
                <header>
                    <p>责任部门会议纪要完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                    提交时间从
                    <input id="zrFinishTimeFrom" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZRRwfjFinishChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    至
                    <input id="zrFinishTimeTo" format="yyyy-MM-dd"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryZRRwfjFinishChartData()" onfocus="this.blur()"
                           class="mini-datepicker" value=""/>
                    </span>
                </header>
                <div id="zrRwfjFinishBar" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    //各类型会议图表
    var hyTypeBar = echarts.init(document.getElementById('hyTypeBar'));
    //组织部门会议纪要图表
    //超期统计
    var meetingDelayBar = echarts.init(document.getElementById('meetingDelayBar'));
    //完成情况
    var rwfjFinishBar = echarts.init(document.getElementById('rwfjFinishBar'));
    //责任部门会议纪要图表
    //超期统计
    var zrMeetingDelayBar = echarts.init(document.getElementById('zrMeetingDelayBar'));
    //完成情况
    var zrRwfjFinishBar = echarts.init(document.getElementById('zrRwfjFinishBar'));


    $(function () {
    });


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

    meetingDelayBar.on('click', function (params) {
        var startTime=mini.get("riskTimeFrom").getText();
        if (!startTime) {
            mini.alert("请选择开始时间");
            return;
        }
        var endTime=mini.get("riskTimeTo").getText();
        if (!endTime) {
            mini.alert("请选择结束时间");
            return;
        }
        var deptName = params.name;
        var delayFlag = true;
        var url = jsUseCtxPath + "/rdm/core/rwfj/rwfjListPage.do?deptName=" + deptName +"&delayFlag=" + delayFlag +"&startTime=" + startTime +"&endTime=" + endTime;
        window.open(url);
    })

    rwfjFinishBar.on('click', function (params) {
        var startTime=mini.get("finishTimeFrom").getText();
        if (!startTime) {
            mini.alert("请选择开始时间");
            return;
        }
        var endTime=mini.get("finishTimeTo").getText();
        if (!endTime) {
            mini.alert("请选择结束时间");
            return;
        }
        var deptName = params.name;
        var finishFlag = true;
        var url = jsUseCtxPath + "/rdm/core/rwfj/rwfjListPage.do?deptName=" + deptName +"&finishFlag=" + finishFlag + "&startTime=" + startTime +"&endTime=" + endTime;
        window.open(url);
    })

    zrMeetingDelayBar.on('click', function (params) {
        var startTime=mini.get("zrRiskTimeFrom").getText();
        if (!startTime) {
            mini.alert("请选择开始时间");
            return;
        }
        var endTime=mini.get("zrRiskTimeTo").getText();
        if (!endTime) {
            mini.alert("请选择结束时间");
            return;
        }
        var zrDeptName = params.name;
        var delayFlag = true;
        var url = jsUseCtxPath + "/rdm/core/rwfj/rwfjListPage.do?zrDeptName=" + zrDeptName +"&delayFlag=" + delayFlag +"&startTime=" + startTime +"&endTime=" + endTime;
        window.open(url);
    })

    zrRwfjFinishBar.on('click', function (params) {
        var startTime=mini.get("zrFinishTimeFrom").getText();
        if (!startTime) {
            mini.alert("请选择开始时间");
            return;
        }
        var endTime=mini.get("zrFinishTimeTo").getText();
        if (!endTime) {
            mini.alert("请选择结束时间");
            return;
        }
        var zrDeptName = params.name;
        var finishFlag = true;
        var url = jsUseCtxPath + "/rdm/core/rwfj/rwfjListPage.do?zrDeptName=" + zrDeptName +"&finishFlag=" + finishFlag + "&startTime=" + startTime +"&endTime=" + endTime;
        window.open(url);
    })

</script>
</body>
</html>
