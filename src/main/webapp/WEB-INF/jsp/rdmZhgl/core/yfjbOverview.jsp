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
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbOverview.js?version=${static_res_version}"
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
            <li class="gs-w" data-row="1" data-col="1" data-sizex="6" data-sizey="4">
                <header class="deskHome-header">
                    <p> 挖掘机械研究院全年降本预测与实际降本对比</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
					    统计规则：
                         <input id="reportRule" name="reportRule" class="mini-combobox rxc" plugins="mini-combobox"
                                style="width:200px;height:34px" label="统计规则："
                                length="50" onvaluechanged="reloadPlan"
                                only_read="false" required="false" allowinput="false" mwidth="100"
                                wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                textField="text" valueField="key_" emptyText="请选择..."
                                url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-TJGZ"
                                nullitemtext="请选择..." emptytext="请选择..."/>
                       统计年份：
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="年度："
                           length="50" onvaluechanged="reloadPlan"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getNearYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div id="deskHomeProgress" class="containerBox">
                    <div id="planGrid" class="mini-datagrid" allowResize="true" style="height: 100%;width: 100%"
                         url="${ctxPath}/rdmZhgl/core/yfjb/reportAllYear.do" idField="id" showPager="false"
                         allowCellWrap="true" showColumnsMenu="false"
                         allowAlternating="true"
                         allowHeaderWrap="true" onlyCheckSelection="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="indexcolumn" align="center" headerAlign="center" width="30px">序号</div>
                            <div field="title" name="title" width="100" headerAlign="center" align="center">
                                统计类别
                            </div>
                            <div field="january" name="january" width="80" headerAlign="center" align="center">
                                1月
                            </div>
                            <div field="february" name="february" width="80" headerAlign="center" align="center">
                                2月
                            </div>
                            <div field="march" name="march" width="80" headerAlign="center" align="center">
                                3月
                            </div>
                            <div field="april" name="april" width="80" headerAlign="center" align="center">
                                4月
                            </div>
                            <div field="may" name="may" width="80" headerAlign="center" align="center">
                                5月
                            </div>
                            <div field="june" name="june" width="80" headerAlign="center" align="center">
                                6月
                            </div>
                            <div field="july" name="july" width="80" headerAlign="center" align="center">
                                7月
                            </div>
                            <div field="august" name="august" width="80" headerAlign="center" align="center">
                                8月
                            </div>
                            <div field="september" name="september" width="80" headerAlign="center" align="center">
                                9月
                            </div>
                            <div field="october" name="october" width="80" headerAlign="center" align="center">
                                10月
                            </div>
                            <div field="november" name="november" width="80" headerAlign="center" align="center">
                                11月
                            </div>
                            <div field="december" name="december" width="80" headerAlign="center" align="center">
                                12月
                            </div>
                            <div field="total" name="total" width="80" headerAlign="center" align="center">
                                总计
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="6" data-sizey="3">
                <header class="deskHome-header">
                    <p> 进度延期项目汇总</p>
                    <span style="font-size: 18px;color: #333;vertical-align: middle;float:center">
					    统计年月：
                         <input id="yearMonth" class="mini-monthpicker" style="width: 150px" name="yearMonth" onvaluechanged="reloadYfjbProcessList"  allowinput="false"/>
                       进度类别：
                    <input id="processType" name="processType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100px;height:34px" label="进度类别："
                           length="50" onvaluechanged="reloadYfjbProcessList"
                           only_read="false" textField="text" valueField="key_" emptyText="请选择..." class="mini-combobox"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JDLB"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                        </span>
                </header>
                <div  class="containerBox">
                    <div id="listGrid" class="mini-datagrid"  allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_" sortOrder="desc"
                         url="${ctxPath}/rdmZhgl/core/yfjb/list.do" idField="id" showPager="true" allowCellWrap="true"
                         multiSelect="true" showColumnsMenu="false" sizeList="[5,20,50,100,200]" pageSize="5"  allowAlternating="true"
                         pagerButtons="#pagerButtons">
                        <div property="columns">
                            <div type="indexcolumn"   align="center" headerAlign="center"  width="40px">序号</div>
                            <div field="saleModel" name="saleModel" width="200px" headerAlign="center" align="center" allowSort="false">销售型号</div>
                            <div field="designModel" name="designModel" width="200px" headerAlign="center" align="left" allowSort="false">设计型号</div>
                            <div field="infoStatus"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onStatus">项目状态</div>
                            <div field="isNewProcess"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onNewProcess">是否填写最新进度</div>
                            <div field="yearMonth"  width="150px" headerAlign="center" align="center" allowSort="false">进度年月</div>
                            <div field="type"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onProcessType">进度类型</div>
                            <div field="processStatus"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onProcessStatus">进度状态</div>
                            <div field="reason"  width="150px" headerAlign="center" align="center" >延后原因</div>
                            <div field="orgItemCode"  width="150px" headerAlign="center" align="center" >原物料编码</div>
                            <div field="orgItemName"  width="200px" headerAlign="center" align="center" allowSort="false">原物料名称</div>
                            <div field="orgItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"  renderer="onHidePrice">原物料价格</div>
                            <div field="orgSupplier"  width="200px" headerAlign="center" align="center" allowSort="false">原供应商</div>
                            <div field="basePrice" width="150px" headerAlign="center" align="center" allowSort="false"  renderer="onHidePrice">基准价格</div>
                            <div field="costType"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onJbfs">降本方式</div>
                            <div field="costMeasure"  width="200px" headerAlign="center" align="center" allowSort="false">降本措施</div>
                            <div field="newItemCode"  width="150px" headerAlign="center" align="center" allowSort="false">替代物料编码</div>
                            <div field="newItemName"  width="200px" headerAlign="center" align="center" allowSort="false">替代物料名称</div>
                            <div field="newItemPrice"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onHidePrice">替代物料价格</div>
                            <div field="newSupplier"  width="200px" headerAlign="center" align="center" allowSort="false">新供应商</div>
                            <div field="differentPrice"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="toFixNum">差额</div>
                            <div field="perSum"  width="150px" headerAlign="center" align="center" allowSort="false">单台用量</div>
                            <div field="replaceRate"  width="150px" headerAlign="center" align="center" allowSort="false">代替比例(%)</div>
                            <div field="perCost"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="toFixNum">单台降本</div>
                            <div field="achieveCost"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="toFixNum">已实现单台降本</div>
                            <div field="risk"  width="200px" headerAlign="center" align="center" allowSort="false">风险评估</div>
                            <div field="isReplace"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onReplace">生产是否切换</div>
                            <div field="jhsz_date"  width="150px" headerAlign="center" align="center" allowSort="false">计划试制时间</div>
                            <div field="sjsz_date"  width="150px" headerAlign="center" align="center" allowSort="false">实际试制时间</div>
                            <div field="jhxfqh_date"  width="180px" headerAlign="center" align="center" allowSort="false">计划下发切换通知单时间</div>
                            <div field="sjxfqh_date"  width="180px" headerAlign="center" align="center" allowSort="false">实际下发切换通知单时间</div>
                            <div field="jhqh_date"  width="150px" headerAlign="center" align="center" allowSort="false">计划切换时间</div>
                            <div field="sjqh_date"  width="150px" headerAlign="center" align="center" allowSort="false">实际切换时间</div>
                            <div field="deptName"  width="150px" headerAlign="center" align="center" allowSort="false" >所属部门</div>
                            <div field="major"  width="150px" headerAlign="center" align="center" allowSort="false" renderer="onMajor">所属专业</div>
                            <div field="responseMan"  width="150px" headerAlign="center" align="center" allowSort="false">责任人</div>
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
    var listGrid = mini.get("listGrid");
    var jbfsList = getDics("YFJB-JBFS");
    var replaceList = getDics("YESORNO");
    var majorList = getDics("YFJB-SSZY");
    var statusList = getDics("YFJB-XMZT");
    var processTypeList = getDics("YFJB-JDLB");
    var processStatusList = getDics("YFJB-JDZT");
    var exportWindow = mini.get("exportWindow");
    listGrid.frozenColumns(0, 4);
    $(function () {
        var year = new Date().getFullYear();
        mini.get('reportYear').setValue(year);
        mini.get('reportRule').setValue("new");
        loadPlanGrid();
        loadProjectGrid();
    })

    function loadPlanGrid() {
        var reportYear =  mini.get('reportYear').getValue();
        var reportRule = mini.get('reportRule').getValue();
        if(!reportYear){
            mini.alert("请选择统计年份");
            return
        }
        if(!reportRule){
            mini.alert("请选择统计规则");
            return
        }
        var paramArray = [{name: "reportYear", value: reportYear}
            ,{name: "reportRule", value:reportRule}];
        var data = {};
        data.filter = mini.encode(paramArray);
        planGrid.load(data);
    }

    function reloadPlan() {
        loadPlanGrid();
    }

    function loadProjectGrid() {
        var paramArray = [{name: "processStatus", value: 'yh'}];
        var data = {};
        data.filter = mini.encode(paramArray);
        listGrid.load(data);
    }
    function reloadYfjbProcessList() {
        var yearMonth = mini.get('yearMonth').getText();
        var processType = mini.get('processType').getValue();
        var paramArray = [{name: "processStatus", value: 'yh'},{name: "yearMonth", value: yearMonth},{name: "processType", value: processType}];
        var data = {};
        data.filter = mini.encode(paramArray);
        listGrid.load(data);
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
</script>
</body>
</html>
