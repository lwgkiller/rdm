<%@ taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的桌面</title>
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
    <script type="text/javascript" src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/common/commonTools.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>

    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>

    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/i18n/language_zh_CN.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/i18n/language_en_US.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>

    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>
    <%--类别统计--%>
    <script src="${ctxPath}/scripts/xcmgProjectManager/deskHome_lbtj.js?version=${static_res_version}"
            type="text/javascript"></script>
    <%--月度积分--%>
    <script src="${ctxPath}/scripts/xcmgProjectManager/deskHome_personScore.js?version=${static_res_version}"
            type="text/javascript"></script>
    <%--进度情况分类--%>
    <script src="${ctxPath}/scripts/xcmgProjectManager/deskHome_jdqk.js?version=${static_res_version}"
            type="text/javascript"></script>

    <script src="${ctxPath}/scripts/xcmgProjectManager/xcmgProjectDeskHome.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>



        .span-header {
            line-height: 38px;
            color: #006699 !important;;
            font-size: 14px !important;
            float: left;
            clear: right;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>

            <li class="gs-w" data-row="1" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.xcmgProjectDeskHome.name"/></p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<spring:message code="page.xcmgProjectDeskHome.name1"/>
						<input id="lbtjBuildFrom" format="yyyy-MM-dd" style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="lbtjBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						<spring:message code="page.xcmgProjectDeskHome.name2"/>
						<input id="lbtjBuildTo" format="yyyy-MM-dd" style="width: 120px;font-size: 10px;height: 30px"
                               onvaluechanged="lbtjBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="lbtj" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="1" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.xcmgProjectDeskHome.name3"/></p>
                </header>
                <div id="jdqk" class="containerBox">
                </div>
            </li>
            <%--项目进度情况--%>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.xcmgProjectDeskHome.name4"/></p>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="progressListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
                         allowResize="false"
                         url="${ctxPath}/xcmgProjectManager/report/xcmgProject/deskHomeProgressReportList.do"
                         idField="projectId" showPager="false"
                         multiSelect="false" showColumnsMenu="true" pageSize="10" allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div field="projectName" width="140" headerAlign="center" align="left"
                                 renderer="jumpToDetail"><spring:message code="page.xcmgProjectDeskHome.name5"/>
                            </div>
                            <div field="status" sortField="status" width="50" allowSort="true"
                                 renderer="onStatusRenderer"><spring:message code="page.xcmgProjectDeskHome.name6"/>
                            </div>
                            <div field="currentStageName" width="100" headerAlign="center" align="center">
                                <spring:message code="page.xcmgProjectDeskHome.name7"/></div>
                            <div field="roleName" width="90" headerAlign="center" align="center"><spring:message
                                    code="page.xcmgProjectDeskHome.name8"/></div>
                            <div field="allTaskUserNames" width="60" align="center" headerAlign="center"><spring:message
                                    code="page.xcmgProjectDeskHome.name9"/></div>
                            <div width="140" headerAlign="center" align="center" renderer="onProgressRenderer">
                                <spring:message code="page.xcmgProjectDeskHome.name10"/>
                            </div>

                            <div width="80" align="center" headerAlign="center" renderer="onRiskRenderer">
                                <spring:message code="page.xcmgProjectDeskHome.name11"/></div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.xcmgProjectDeskHome.name12"/></p>
                    <input id="ydjfMonth" style="float: right;width: 100px" onfocus="this.blur()"
                           class="mini-monthpicker" onvaluechanged="changeTime()" value=""/>
                </header>
                <div id="ydjf" class="containerBox">
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
    var lbtjChart = echarts.init(document.getElementById('lbtj'));//类别统计
    var ydjfChart = echarts.init(document.getElementById('ydjf'));//积分情况
    var jdqkChart = echarts.init(document.getElementById('jdqk'));//进度情况

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

</script>
</body>
</html>
