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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.monthWorkPersonView.name1" /></p>
                    <span style="font-size: 13px;color: #333;vertical-align: middle;">
						<spring:message code="page.monthWorkPersonView.name2" /><input id="yearMonthStart" allowinput="false" onvaluechanged="reloadPlan"
                                   class="mini-monthpicker"
                                   style="width:100px" name="yearMonthStart"/>
                        <spring:message code="page.monthWorkPersonView.name3" /><input id="yearMonthEnd" allowinput="false" onvaluechanged="reloadPlan"
                                class="mini-monthpicker"
                                style="width:100px" name="yearMonthEnd"/>
					</span>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="planGrid" class="mini-datagrid" allowResize="true" style="max-height: 360px"
                         url="${ctxPath}/rdmZhgl/core/monthWork/personPlan.do" idField="id" showPager="false"
                         allowCellWrap="true" showColumnsMenu="false"
                         allowAlternating="true"
                         allowHeaderWrap="true" onlyCheckSelection="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
                            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkPersonView.name4" /></div>
                            <div field="projectName" name="projectName" width="200px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name5" />
                            </div>
                            <div field="number" name="number" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name6" /></div>
                            <div field="processRate" name="processRate" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name7" /></div>
                            <div field="processStatus" name="processStatus" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name8" /></div>
                            <div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name9" />
                            </div>
                            <div field="stageName" name="stageName" width="100px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name10" />
                            </div>
                            <div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name11" />
                            </div>
                            <div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name12" /></div>
                            <div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name13" /></div>
                            <div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name14" /></div>
                            <div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name15" /></div>
                            <div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center"
                                 renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkPersonView.name16" />
                            </div>
                            <div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onFinish"><spring:message code="page.monthWorkPersonView.name17" />
                            </div>
                            <div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name18" />
                            </div>
                            <div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkPersonView.name19" />
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.monthWorkPersonView.name20" /></p>
                </header>
                <div id="unPlanBox" class="containerBox">
                    <div id="unPlanListGrid" class="mini-datagrid" style="max-height: 360px" allowResize="true"
                         url="${ctxPath}/rdmZhgl/core/monthUnProjectPlan/personUnProject.do" idField="id" showPager="false" allowCellWrap="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
                            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkPersonView.name4" /></div>
                            <div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name21" />
                            </div>
                            <div field="projectCode" name="projectCode" width="150px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name22" />
                            </div>
                            <div field="processRate" name="processRate" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name7" /></div>
                            <div field="processStatusText" name="processStatusText" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name8" /></div>
                            <div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name23" />
                            </div>
                            <div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name24" />
                            </div>
                            <div field="stageId" name="stageId" width="100px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name25" />
                            </div>
                            <div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name26" />
                            </div>
                            <div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name12" /></div>
                            <div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name13" /></div>
                            <div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name14" /></div>
                            <div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name15" /></div>
                            <div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center"
                                 renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkPersonView.name16" />
                            </div>
                            <div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onFinish"><spring:message code="page.monthWorkPersonView.name17" />
                            </div>
                            <div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name18" />
                            </div>
                            <div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkPersonView.name19" />
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="7" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.monthWorkPersonView.name27" /></p>
					</span>
                </header>
                <div id="unPlanTaskBox" class="containerBox">
                    <div id="unPlanTaskGrid" class="mini-datagrid" style="min-height: 660px;max-height: 750px" allowResize="true"
                         url="${ctxPath}/rdmZhgl/core/monthUnPlanTask/personUnPlanTask.do" idField="id" showPager="false" allowCellWrap="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="checkcolumn" field="mainId" name="mainId" width="30px"></div>
                            <div field="rowNum" name="rowNum" align="center" headerAlign="center" width="40px"><spring:message code="page.monthWorkPersonView.name4" /></div>
                            <div field="taskName" name="taskName" width="200px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name21" />
                            </div>
                            <div field="projectCode" name="projectCode" width="150px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name22" />
                            </div>
                            <div field="taskFrom" name="taskFrom" width="150px" headerAlign="center" align="center" allowSort="false">
                                <spring:message code="page.monthWorkPersonView.name23" />
                            </div>
                            <div field="startEndDate" name="startEndDate" width="200px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name24" />
                            </div>
                            <div field="responseMan" name="responseMan" width="100px" headerAlign="center" align="center"
                                 allowSort="false"><spring:message code="page.monthWorkPersonView.name26" />
                            </div>
                            <div field="workContent" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name12" /></div>
                            <div field="finishFlag" width="250px" headerAlign="center" align="left" allowSort="false"><spring:message code="page.monthWorkPersonView.name13" /></div>
                            <div field="planResponseMan" width="100px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name14" /></div>
                            <div field="responseDeptId" width="150px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name15" /></div>
                            <div field="isCompanyLevel" name="isCompanyLevel" width="100px" headerAlign="center" align="center"
                                 renderer="onCompany" allowSort="false"><spring:message code="page.monthWorkPersonView.name16" />
                            </div>
                            <div field="finishStatus" name="finishStatus" width="100px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onFinish"><spring:message code="page.monthWorkPersonView.name17" />
                            </div>
                            <div field="remark" name="remark" width="200px" headerAlign="center" align="center" allowSort="false"><spring:message code="page.monthWorkPersonView.name18" />
                            </div>
                            <div field="isDelayApply" name="isDelayApply" width="150px" headerAlign="center" align="center"
                                 allowSort="false" renderer="onDelayApplyRate"><spring:message code="page.monthWorkPersonView.name19" />
                            </div>
                        </div>
                    </div>
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
    var planGrid = mini.get("planGrid");
    var unPlanListGrid = mini.get("unPlanListGrid");
    var unPlanTaskGrid = mini.get("unPlanTaskGrid");
    var yesOrNo = getDics("YESORNO");
    var jhjbList = getDics("YDJH-JHJB");
    var finishStatusList = getDics("WCQK");
    planGrid.frozenColumns(0, 3);
    planGrid.on("load", function () {
        planGrid.mergeColumns(["mainId","rowNum","projectName", "number", "processRate","processStatus","startEndDate", "stageName", "responseMan", "isCompanyLevel","finishStatus","remark"]);
    });
    unPlanListGrid.frozenColumns(0, 3);
    unPlanListGrid.on("load", function () {
        unPlanListGrid.mergeColumns(["mainId","rowNum","projectName", "number", "processRate","processStatus","startEndDate", "stageName", "responseMan", "isCompanyLevel","finishStatus","remark"]);
    });
    unPlanTaskGrid.frozenColumns(0, 3);
    unPlanTaskGrid.on("load", function () {
        unPlanTaskGrid.mergeColumns(["mainId","rowNum","taskName","projectCode","taskFrom", "startEndDate",  "responseMan", "isCompanyLevel","finishStatus","remark"]);
    });
    $(function () {
        var year = new Date().getFullYear();
        var month = new Date().getMonth();
        month = month + 1;
        var nowDate = year + "-" + month;
        mini.get('yearMonthStart').setValue(nowDate);
        mini.get('yearMonthEnd').setValue(nowDate);
        loadPlanGrid();
        loadUnProjectPlanGrid();
        loadUnPlanTaskGrid();
    })
    planGrid.on("load", function () {
        planGrid.mergeColumns(["rowNum", "projectName", "responseMan","finishStatusText"]);
    });

    function loadPlanGrid() {
        var paramArray = [{name: "yearMonthStart", value: mini.get('yearMonthStart').getText()}
                            ,{name: "yearMonthEnd", value: mini.get('yearMonthEnd').getText()}];
        var data = {};
        data.filter = mini.encode(paramArray);
        planGrid.load(data);
    }
    function loadUnProjectPlanGrid() {
        var paramArray = [{name: "yearMonthStart", value: mini.get('yearMonthStart').getText()}
            ,{name: "yearMonthEnd", value: mini.get('yearMonthEnd').getText()}];
        var data = {};
        data.filter = mini.encode(paramArray);
        unPlanListGrid.load(data);
    }
    function loadUnPlanTaskGrid() {
        var paramArray = [{name: "yearMonthStart", value: mini.get('yearMonthStart').getText()}
            ,{name: "yearMonthEnd", value: mini.get('yearMonthEnd').getText()}];
        var data = {};
        data.filter = mini.encode(paramArray);
        unPlanTaskGrid.load(data);
    }

    function reloadPlan() {
        loadPlanGrid();
        loadUnProjectPlanGrid();
        loadUnPlanTaskGrid();
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
    function onCompany(e) {
        var record = e.record;
        var resultValue = record.isCompanyLevel;
        var resultText = '';
        for (var i = 0; i < jhjbList.length; i++) {
            if (jhjbList[i].key_ == resultValue) {
                resultText = jhjbList[i].text;
                break
            }
        }
        return resultText;
    }

    function onFinish(e) {
        var record = e.record;
        var resultValue = record.finishStatus;
        var resultText = '';
        for (var i = 0; i < finishStatusList.length; i++) {
            if (finishStatusList[i].key_ == resultValue) {
                resultText = finishStatusList[i].text;
                break
            }
        }
        var _html = '';
        if (resultValue == '1' && record.isDelayApply != '1') {
            _html = '<span style="color: red">' + resultText + '</span>'
        } else if (resultValue == '0') {
            _html = '<span style="color: green">' + resultText + '</span>'
        } else if (resultValue == '1' && record.isDelayApply == '1') {
            _html = '<span style="color: green">' + resultText + '</span>'
        }
        return _html;
    }

    function onDelayApplyRate(e) {
        var record = e.record;
        var delayApply = record.isDelayApply;
        var resultText = '';
        for (var i = 0; i < yesOrNo.length; i++) {
            if (yesOrNo[i].key_ == delayApply) {
                resultText = yesOrNo[i].text;
                break
            }
        }
        return resultText;
    }

    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async: false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
</body>
</html>
