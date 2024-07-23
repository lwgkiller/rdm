<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>委外认证项目总览</title>
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
    <script src="${ctxPath}/scripts/wwrz/report/wwrzOverview_report.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/wwrz/report/wwrzOverview_reportType.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/wwrz/report/wwrzOverview_deptProject.js?version=${static_res_version}"
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
            <li class="gs-w" data-col="1" data-row="1" data-sizex="6" data-sizey="3">
                <header>
                    <p>认证报告统计</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
                       统计截止年份：
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
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="4" data-sizex="6" data-sizey="3">
                <header>
                    <p>认证报告类别统计</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
                       统计年份从：
                    <input id="reportYearStart" name="reportYearStart" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reloadTypeData"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>至
                              <input id="reportYearEnd" name="reportYearEnd" class="mini-combobox rxc"
                                     plugins="mini-combobox"
                                     style="width:100px;height:34px" label="年度："
                                     length="50" onvaluechanged="reloadTypeData"
                                     only_read="false" required="true" allowinput="false" mwidth="100"
                                     wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                     textField="text" valueField="value" emptyText="请选择..."
                                     url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                                     nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div id="reportType" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="7" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header">项目进展情况
                        <a class="mini-button" style="margin-left: 5px;height: 30px;line-height: 27px" plain="true" onclick="moreData()">展开</a>
                    </p>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="listGrid" class="mini-datagrid" allowResize="false" style="width: 100%; height: 100%;"
                         sortField="UPDATE_TIME_" sortOrder="desc" onrowdblclick="onRowDblClick()"
                         url="${ctxPath}/wwrz/core/report/queryApplyList.do"  idField="id"
                         showPager="true" allowCellWrap="false"
                         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" pageSize="6"
                         allowAlternating="true" autoload="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                            <div field="id" width="120px" headerAlign="center" align="center"
                                 allowSort="false">流程编号
                            </div>
                            <div field="status" width="80px" headerAlign="center" align="center" renderer="onStatusRenderer" allowSort="false">
                                运行状态
                            </div>
                            <div field="planId" width="100px" headerAlign="center" align="center" allowSort="false" renderer="onPlanRenderer">
                                认证计划
                            </div>
                            <div field="productModel" width="100px" headerAlign="center" align="center"
                                 allowSort="false">销售型号
                            </div>
                            <div field="itemNames" width="120px" headerAlign="center" align="left"
                                 allowSort="false">认证项目
                            </div>
                            <div field="productLeaderName" width="100px" headerAlign="center" align="center"
                                 allowSort="false">产品主管
                            </div>
                            <div field="taskName" width="100px" headerAlign="center" align="center"
                                 allowSort="false">当前阶段
                            </div>
                            <div field="allTaskUserNames" width="100px" headerAlign="center" align="center"
                                 allowSort="false">当前处理人
                            </div>
                            <div  width="80px" headerAlign="center" align="center"  renderer="onProgressRenderer"
                                 allowSort="false">项目进度
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-col="1" data-row="10" data-sizex="6" data-sizey="3">
                <header>
                    <p>认证项目统计</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
                       统计年份从：
                    <input id="reportDeptProjectYearStart" name="reportDeptProjectYearStart" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reloadDeptProjectData"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>至
                              <input id="reportDeptProjectYearEnd" name="reportDeptProjectYearEnd" class="mini-combobox rxc"
                                     plugins="mini-combobox"
                                     style="width:100px;height:34px" label="年度："
                                     length="50" onvaluechanged="reloadDeptProjectData"
                                     only_read="false" required="true" allowinput="false" mwidth="100"
                                     wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                     textField="text" valueField="value" emptyText="请选择..."
                                     url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                                     nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div id="reportDeptProject" class="containerBox">
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var lastMonth = "${lastMonth}";
    var reportChart = echarts.init(document.getElementById('report'));
    var reportTypeChart = echarts.init(document.getElementById('reportType'));
    var reportDeptProjectChart = echarts.init(document.getElementById('reportDeptProject'));
    var listGrid = mini.get("listGrid");
    var permission = ${permission};
    var userName = "${userName}";
    $(function () {
        var year = new Date().getFullYear();
        mini.get('reportYear').setValue(year);
        mini.get('reportYearEnd').setValue(year);
        mini.get('reportDeptProjectYearStart').setValue(year-1);
        mini.get('reportDeptProjectYearEnd').setValue(year);
        initReportChart();
        initReportTypeChart();
        initReportDeptProjectChart();
    })

    function reloadData() {
        initReportChart();
    }
    function reloadTypeData() {
        initReportTypeChart();
    }
    function reloadDeptProjectData() {
        initReportDeptProjectChart();
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

    reportChart.on('click', function (params) {
        if (!permission && params.name != userName) {
            return;
        }
        var yearMonth = '';
        var barType = '';
        if (mini.get("yearMonth").getValue()) {
            yearMonth = mini.get("yearMonth").getText();
        }
        var seriesName = params.seriesName;
        if (seriesName == '计划') {
            barType = 'all';
        } else {
            barType = 'unFinish';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/plan/reportViewPage.do?barType=" + barType + "&userName=" + params.name + "&yearMonth=" + yearMonth;
        window.open(url);
    })
    reportTypeChart.on('click', function (params) {
        if (!permission) {
            return;
        }
        var yearMonth = '';
        var barType = '';
        if (mini.get("yearMonth").getValue()) {
            yearMonth = mini.get("yearMonth").getText();
        }
        var seriesName = params.seriesName;
        if (seriesName == '计划') {
            barType = 'all';
        } else {
            barType = 'unFinish';
        }
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/plan/reportViewPage.do?barType=" + barType + "&deptName=" + params.name + "&yearMonth=" + yearMonth;
        window.open(url);
    })

    function showDetail(e) {
        var s = '';
        var record = e.record;
        var detailId = record.detailId;
        var productName = record.productName;
        var chargerManName = record.chargerManName;
        var responsorName = record.responsorName;
        if (!permission && chargerManName != userName && responsorName != userName) {
            s = '<span  >' + productName + '</span>';
        } else {
            s = '<span   style="cursor: pointer;color: #0a7ac6" onclick="openDetail(\'' + detailId + '\')">' + productName + '</span>';
        }
        return s;
    }

    function openDetail(detailId) {
        var url = jsUseCtxPath + "/rdmZhgl/core/ndkfjh/planDetail/planViewPage.do?detailId=" + detailId;
        window.open(url);
    }
    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }
    function onProgressRenderer(e) {
        var record = e.record;
        var progressNum = 0;
        if(record.status == 'SUCCESS_END') {
            progressNum = 100;
        }else{
            var currentProcessTask = record.taskName;
            var stage = currentProcessTask.split("");
            var stageInt = '';
            for(var i=0;i<stage.length;i++){
                if(i==0){
                    stageInt = stage[i];
                }
                if(i==1){
                    var flag = isNaN(stage[i])
                    if(!flag){
                        stageInt += stage[i];
                    }
                }
            }
            if(stageInt==''){
                stageInt = '0';
            }
            progressNum = (parseInt(stageInt)/16*100).toFixed(2);
        }
        var s ='<div class="mini-progressbar" id="p1" style="border-width: 0px;width:120px"><div class="mini-progressbar-border"><div class="mini-progressbar-bar" style="width: '+progressNum+'%;"></div><div class="mini-progressbar-text" id="p1$text">'+progressNum+'%</div></div></div>';
        return s;
    }
    function onPlanRenderer(e) {
        var record = e.record;
        var s = '';
        if(record.planId){
            s = '<span style="color: black">计划内</span>'
        }else{
            s = '<span style="color: red">计划外</span>'
        }
        return s;
    }
    function onRowDblClick() {
        rows = listGrid.getSelecteds();
        var id = rows[0].id;
        var action = "detail";
        var url = jsUseCtxPath + "/wwrz/core/test/edit.do?action=" + action + "&id=" + id + "&status=" + status;
        var winObj = window.open(url);
    }
    function moreData() {
        var url = jsUseCtxPath + "/wwrz/core/report/projectProcessPage.do";
        window.open(url);
    }
</script>
</body>
</html>
