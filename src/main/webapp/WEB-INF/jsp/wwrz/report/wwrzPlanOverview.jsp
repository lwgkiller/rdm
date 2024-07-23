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
    <script src="${ctxPath}/scripts/share/dialog.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/report/wwrzPlan_ztjhfhqk.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/wwrz/report/wwrzPlan_gsjhfhqk.js?version=${static_res_version}"
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
                    <p>总体计划符合情况</p>
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
                        选择所：
                             <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                                    data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                                    style="width:160px;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
                                    maxlength="500" minlen="0" single="true" required="false" initlogindep="true" showclose="false"
                                    mwidth="160" wunit="px" mheight="34" hunit="px"/>
                           <a class="mini-button" style="margin-right: 5px;height: 30px;line-height: 27px" plain="true" onclick="reloadData()">统计</a>
                        </span>
                </header>
                <div id="all" class="containerBox">
                </div>
            </li>
            <ul>
                <li class="gs-w" data-col="1" data-row="4" data-sizex="6" data-sizey="3">
                    <header>
                        <p>各所计划符合情况</p>
                        <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
                       统计年月从：
                    <input id="reportYearMonthStart" allowinput="false"  class="mini-monthpicker"
                            style="width: 100px" name="reportYearMonthStart"  onvaluechanged="reloadDeptData"/>至
                            <input id="reportYearMonthEnd" allowinput="false"  class="mini-monthpicker"
                                    style="width: 100px" name="reportYearMonthEnd" onvaluechanged="reloadDeptData"/>
                        </span>
                    </header>
                    <div id="dept" class="containerBox">
                    </div>
                </li>
            </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var allChart = echarts.init(document.getElementById('all'));
    var deptChart = echarts.init(document.getElementById('dept'));
    var permission = ${permission};
    var userName = "${userName}";
    $(function () {
        var year = new Date().getFullYear();
        mini.get('reportYear').setValue(year);
        mini.get('reportYearMonthStart').setValue(year+'-01');
        mini.get('reportYearMonthEnd').setValue(getCurrentYearMonth());


        initAllChart();
        initDeptChart();
    })

    function reloadData() {
        initAllChart();
    }
    function reloadDeptData() {
        initDeptChart();
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
    function getCurrentYearMonth() {
        var year = new Date().getFullYear();
        var month = new Date().getMonth()+1;
        var yearMonth = '';
        if(month<10){
            yearMonth = year+'-0'+month;
        }else{
            yearMonth = year+'-'+month;
        }
        return yearMonth;
    }
</script>
</body>
</html>
