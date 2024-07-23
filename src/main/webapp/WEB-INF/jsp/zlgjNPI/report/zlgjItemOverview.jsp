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
    <script src="${ctxPath}/scripts/mini/boot.js?static_res_version=${static_res_version}"></script>
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

    <script src="${ctxPath}/scripts/zlgjNPI/report/perDay.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/cqpm.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/responsibleDept.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/typeDept.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/typeMonth.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/leftChartOne.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/leftChartTwo.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/leftChartThree.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/rightChartOne.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/rightChartTwo.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/rightChartThree.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/CenterBottomChartOne.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/CenterBottomChartTwo.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/zlgjNPI/report/wxblx.js?version=${static_res_version}"
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
<div style="width: 100%;height: 35px;background-color: white">
	<span style="float: right;	font-size: 15px;color: #333;vertical-align:text-bottom">
	是否包含操控性评价:
	<input id="czxpj" name="czxpj" class="mini-combobox" style="width:150px;font-size: 15px;"
           textfield="value" valuefield="key" value="0"
           required="true" allowInput="false" showNullItem="false" nullItemText="否"
           data="[ {'key' : '0','value' : '否'},{'key' : '1','value' : '是'}]"
           onvaluechanged="refreshAllChart()"/>
	</span>
</div>
<div class="personalPort" style="min-width: 1679px;">
    <div class="gridster">
        <ul>
            <%--第一行--%>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="24" data-sizey="1">
                <header>
                    <p>每日完成率</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="perDayFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="perDayFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               showOkButton="false"
                               showClearButton="false"/>
						至
						<input id="perDayTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="perDayToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               showOkButton="false"
                               showClearButton="false"/>
					</span>
                </header>
                <div id="perDayChart" class="containerBox">
                </div>
            </li>
            <%--第二行--%>
            <li class="gs-w" data-row="2" data-col="1" data-sizex="7" data-sizey="1">
                <header>
                    <p>厂内问题完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="leftOneBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftOneBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               showOkButton="true"
                               showClearButton="false"/>
						至
						<input id="leftOneBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftOneBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               showOkButton="true"
                               showClearButton="false"/>
					</span>
                </header>
                <div id="leftOne" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="2" data-col="8" data-sizex="10" data-sizey="1">
                <header>
                    <p>维修便利性问题完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="wxblxBuildFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="wxblxBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						至
						<input id="wxblxBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="wxblxBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="wxblx" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="2" data-col="18" data-sizex="7" data-sizey="1">
                <header>
                    <p>新品试制改进情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="rightOneBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightOneBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						至
						<input id="rightOneBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightOneBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="rightOne" class="containerBox">
                </div>
            </li>
            <%--第三行--%>
            <li class="gs-w" data-row="3" data-col="1" data-sizex="7" data-sizey="1">
                <header>
                    <p>市场问题完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="leftTwoBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftTwoBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						至
						<input id="leftTwoBuildTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftTwoBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="leftTwo" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="3" data-col="8" data-sizex="10" data-sizey="1">
                <header style="height:60px">
                    <p>各部门问题处理人超期情况（特急3天,紧急5天,一般10天）</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                        问题类型
                        <input id="wtlxCq" name="wtlxCq" class="mini-combobox" style="width:120px;margin-right: 5px"
                               multiSelect="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               onvaluechanged="queryTimeChangeChartData()"
                               onfocus="this.blur()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                        接收问题开始月份
                        <input id="yyTimeFrom" format="yyyy-MM"
                           style="width: 115px;font-size: 10px;height: 30px"
                           onvaluechanged="queryTimeChangeChartData()" onfocus="this.blur()"
                           class="mini-monthpicker" value=""/>
                    </span>
                </header>
                <div id="cqpmBar" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="3" data-col="13" data-sizex="7" data-sizey="1">
                <header>
                    <p>新品整机改进情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="rightTwoBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightTwoBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						至
						<input id="rightTwoBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightTwoBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="rightTwo" class="containerBox">
                </div>
            </li>
            <%--第四行--%>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="7" data-sizey="1">
                <header>
                    <p>海外问题完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="leftThreeBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftThreeBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
						至
						<input id="leftThreeBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="leftThreeBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="leftThree" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="8" data-sizex="5" data-sizey="1">
                <header style="height: 60px">
                    <p>问题改进完成率</p>
                    <p style="font-size: 13px;color: #333;vertical-align: middle">
                        创建时间
                        <input id="centerBottomChartOneBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="centerBottomChartOneBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
                        至
                        <input id="centerBottomChartOneBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="centerBottomChartOneBuildToChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
                    </p>
                </header>
                <div id="centerBottomChartOne" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="13" data-sizex="5" data-sizey="1">
                <header style="height: 60px">
                    <p>新品改进完成率</p>
                    <p style="font-size: 13px;color: #333;vertical-align: middle">
                        创建时间
                        <input id="centerBottomChartTwoBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="centerBottomChartTwoBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
                        至
                        <input id="centerBottomChartTwoBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="centerBottomChartTwoBuildToChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
                    </p>
                </header>
                <div id="centerBottomChartTwo" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="18" data-sizex="7" data-sizey="1">
                <header>
                    <p>新品路试改进情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						创建时间
						<input id="rightThreeBuildFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightThreeBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
						至
						<input id="rightThreeBuildTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="rightThreeBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="rightThree" class="containerBox">
                </div>
            </li>
            <%--第五行--%>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="24" data-sizey="1">
                <header>
                    <p>责任部门完成情况</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
                        问题类型
                        <input id="wtlx" name="wtlx" class="mini-combobox" style="width:120px;margin-right: 5px"
                               multiSelect="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               onvaluechanged="responlxChanged()"
                               onfocus="this.blur()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                        处理人类型
                        <input id="zrType" name="zrType" class="mini-combobox" style="width:120px;margin-right: 5px"
                               multiSelect="false"
                               textField="value" valueField="key" emptyText="请选择..."
                               onvaluechanged="responlxChanged()"
                               onfocus="this.blur()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '第一责任人','value' : '第一责任人'},{'key' : '问题处理人','value' : '问题处理人'},{'key' : '流程处理人','value' : '流程处理人'}]"/>
						创建时间
						<input id="responsibleDeptFrom" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="responsibleDeptBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
						至
						<input id="responsibleDeptTo" format="yyyy-MM-dd"
                               style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="responsibleDeptBuildToChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
					</span>

                </header>
                <div id="responsibleDept" class="containerBox">
                </div>
            </li>
            <%--第六行--%>
            <li class="gs-w" data-row="6" data-col="1" data-sizex="12" data-sizey="1" style="height: 450px">
                <header style="height: 60px">
                    <p>各类严重程度问题责任部门完成情况</p>
                    <span style="float: left;	font-size: 13px;color: #333;vertical-align: middle">
                        问题类型
                        <input id="wtlx1" name="wtlx1" class="mini-combobox" style="width:120px;margin-right: 5px"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="typelxChanged()"
                               onfocus="this.blur()" multiSelect="true"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                        问题严重程度
                        <input id="yzcd" name="yzcd" class="mini-combobox" style="width:120px;margin-right: 5px"
                               multiSelect="true"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="typelxChanged()"
                               onfocus="this.blur()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'A','value' : 'A'},{'key' : 'B','value' : 'B'}
                               ,{'key' : 'C','value' : 'C'},{'key' : 'W','value' : 'W'}]"/>
						创建时间
						<input id="typeDeptFrom1" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="typeDeptBuildFromChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
						至
						<input id="typeDeptTo1" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="typeDeptBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="typeDataDept" class="containerBox">
                </div>
            </li>
            <li class="gs-w" data-row="5" data-col="2" data-sizex="12" data-sizey="1" style="height: 450px">
                <header style="height: 60px">
                    <p>各类严重程度问题月份完成情况</p>
                    <span style="float: left;	font-size: 13px;color: #333;vertical-align: middle">
                        问题类型
                        <input id="wtlx2" name="wtlx2" class="mini-combobox" style="width:120px;margin-right: 5px"
                               textField="value" valueField="key" emptyText="请选择..."
                               onvaluechanged="monthlxChanged()"
                               onfocus="this.blur()" multiSelect="true"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'},{'key' : 'XPLS','value' : '新品路试'},
                                   {'key' : 'CNWT','value' : '厂内问题'},{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                                   ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                        问题严重程度
                        <input id="yzcd1" name="yzcd1" class="mini-combobox" style="width:120px;margin-right: 5px"
                               multiSelect="true"
                               textField="value" valueField="key" emptyText="请选择..."
                               onvaluechanged="monthlxChanged()"
                               onfocus="this.blur()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'A','value' : 'A'},{'key' : 'B','value' : 'B'}
                               ,{'key' : 'C','value' : 'C'},{'key' : 'W','value' : 'W'}]"/>
						创建时间
						<input id="typeMonthFrom" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="typeMonthBuildFromChanged()" onfocus="this.blur()"
                               class="mini-datepicker" value=""/>
						至
						<input id="typeMonthTo" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;height: 30px"
                               onvaluechanged="typeMonthBuildToChanged()" onfocus="this.blur()" class="mini-datepicker"
                               value=""/>
					</span>
                </header>
                <div id="typeDataMonth" class="containerBox">
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
    var leftOneChart = echarts.init(document.getElementById('leftOne'));
    var leftTwoChart = echarts.init(document.getElementById('leftTwo'));
    var leftThreeChart = echarts.init(document.getElementById('leftThree'));
    var rightOneChart = echarts.init(document.getElementById('rightOne'));
    var rightTwoChart = echarts.init(document.getElementById('rightTwo'));
    var rightThreeChart = echarts.init(document.getElementById('rightThree'));
    var typeDataMonth = echarts.init(document.getElementById('typeDataMonth'));
    var typeDataDept = echarts.init(document.getElementById('typeDataDept'));
    var cqpmBar = echarts.init(document.getElementById('cqpmBar'));
    var wxblxChart = echarts.init(document.getElementById('wxblx'));
    var responsibleDeptChart = echarts.init(document.getElementById('responsibleDept'));
    var centerBottomChartOneChart = echarts.init(document.getElementById('centerBottomChartOne'));
    var centerBottomChartTwoChart = echarts.init(document.getElementById('centerBottomChartTwo'));
    //2022-10-27新增
    var perDayChart = echarts.init(document.getElementById('perDayChart'));

    function refreshAllChart() {
        //每日完成率
        queryPerDayChartData();
        //各部门问题处理人超期情况
        queryCqChartData();
        //责任部门完成情况
        queryResponsibleDeptChartData();
        //各类严重程度问题责任部门完成情况
        querytypeDeptChartData();
        //各类严重程度问题月份完成情况
        querytypeMonthChartData();
        //厂内问题完成情况
        queryLeftOneChartData();
        //市场问题完成情况
        queryLeftTwoChartData();
        //海外问题完成情况
        queryLeftThreeChartData();
        //新品试制改进情况
        queryRightOneChartData();
        //新品整机改进情况
        queryRightTwoChartData();
        //新品路试改进情况
        queryRightThreeChartData();
        //问题改进完成率
        queryCenterBottomChartOneChartData();
        //新品改进完成率
        queryCenterBottomChartTwoChartData();
        //维修便利性完成情况
        querywxblxChartData();

    }

    function overview() {
        // var totalWidth=document.body.clientWidth;
        // if(window.parent.lastClickSysName&&window.parent.lastClickSysName=='首页'&&(!window.parent.lastMenuId||window.parent.lastMenuId=='')) {
        //     totalWidth=document.body.clientWidth-220;
        // }
        var width = (1679 - 3 * 8) / 24 - 8;
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: [width, 380],
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

    perDayChart.on('click', function (params) {
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?create_startTime=" + yearMonth + "&create_endTime=" + yearMonth + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    leftOneChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01'
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=CNWT&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    leftTwoChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01'
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=SCWT&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    leftThreeChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01'
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=HWWT&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    rightOneChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01'
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=XPSZ&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    rightTwoChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01'
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=XPZDSY&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    rightThreeChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01';
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype=XPLS&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })
    cqpmBar.on('click', function (params) {
        var type = mini.get('wtlxCq').getValue();
        var create_startTime = '';
        var create_endTime = '';
        if (mini.get("yyTimeFrom").getValue()) {
            create_startTime = mini.get("yyTimeFrom").getText() + "-01";
        }
        var czxpj = mini.get("czxpj").getValue();
        create_endTime = getNowFormatDate();
        var deptName = params.name == '总计' ? '' : params.name;
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPageCq.do?wtlxtype=" + type + "&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump&deptName=" + deptName + "&chartType=1" + "&czxpj=" + czxpj;
        window.open(url);
    })
    responsibleDeptChart.on('click', function (params) {
        var zrType = mini.get('zrType').getValue();
        var type = mini.get('wtlx').getValue();
        var czxpj = mini.get("czxpj").getValue();
        var create_startTime = '';
        var create_endTime = '';
        if (mini.get("responsibleDeptFrom").getValue()) {
            create_startTime = mini.get("responsibleDeptFrom").getText();
        }
        if (mini.get("responsibleDeptTo").getValue()) {
            create_endTime = mini.get("responsibleDeptTo").getText();
        }
        var deptName = params.name == '总计' ? '' : params.name;
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPageCq.do?zrType=" + zrType + "&wtlxtype=" + type + "&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump&deptName=" + deptName + "&chartType=2&czxpj=" + czxpj;
        window.open(url);
    });
    wxblxChart.on('click', function (params) {
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        var czxpj = mini.get("czxpj").getValue();
        create_startTime = yearMonth + '-01';
        create_endTime = getDateByMonth(create_startTime);
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPage.do?wtlxtype =WXBLX&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&czxpj=" + czxpj;
        window.open(url);
    })

    function getDateByMonth(create_startTime) {
        var inDate = new Date(create_startTime)
        var year = inDate.getFullYear()
        var month = inDate.getMonth()
        var endTime = new Date(year, month + 1, 0).format("yyyy-MM-dd")
        return endTime;
    }

    //获取当前日期函数
    function getNowFormatDate() {
        let date = new Date(),
            seperator1 = '-', //格式分隔符
            year = date.getFullYear(), //获取完整的年份(4位)
            month = date.getMonth() + 1, //获取当前月份(0-11,0代表1月)
            strDate = date.getDate() // 获取当前日(1-31)
        if (month >= 1 && month <= 9) month = '0' + month // 如果月份是个位数，在前面补0
        if (strDate >= 0 && strDate <= 9) strDate = '0' + strDate // 如果日是个位数，在前面补0

        let currentdate = year + seperator1 + month + seperator1 + strDate
        return currentdate
    }

    typeDataDept.on('click', function (params) {
        var type = mini.get('wtlx1').getValue();
        var czxpj = mini.get("czxpj").getValue();
        var create_startTime = '';
        var create_endTime = '';
        if (mini.get("typeDeptFrom1").getText()) {
            create_startTime = mini.get("typeDeptFrom1").getText();
        }
        if (mini.get("typeDeptTo1").getText()) {
            create_endTime = mini.get("typeDeptTo1").getText();
        }
        var deptName = params.name == '总计' ? '' : params.name;
        var yzcd = mini.get('yzcd').getValue();
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPageCq.do?wtlxtype=" + type + "&yzcd=" + yzcd + "&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump&deptName=" + deptName + "&chartType=2&czxpj=" + czxpj;
        window.open(url);
    });
    typeDataMonth.on('click', function (params) {
        var type = mini.get('wtlx2').getValue();
        var czxpj = mini.get("czxpj").getValue();
        var create_startTime = '';
        var create_endTime = '';
        var yearMonth = params.name;
        create_startTime = yearMonth + '-01';
        create_endTime = getDateByMonth(create_startTime);
        var yzcd = mini.get('yzcd1').getValue();
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/listPageCq.do?wtlxtype=" + type + "&yzcd=" + yzcd + "&create_startTime=" + create_startTime + "&create_endTime=" + create_endTime + "&report=jump" + "&chartType=2&czxpj=" + czxpj;
        window.open(url);
    })
</script>
</body>
</html>
