<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib  prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html >
<head>
	<title>部件验证项目</title>
	<%@include file="/commons/edit.jsp"%>
	<script src="${ctxPath}/scripts/drbfm/singleFramePage.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
	<style>
		.table-detail > tbody > tr > td{
			border: 1px solid #ccc;
		}
	</style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
	<div>
		<a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
		<a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
	</div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div class="form-container" style="margin: 0;padding-top: 0">
        <form id="formProject" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
        </form>
        <div id="systemTab" class="mini-tabs" style="width:100%;height:100%;" >
            <div title="基本信息" name="baseInfo" refreshOnClick="true"></div>
            <div title="经验教训" name="expInfo" refreshOnClick="true"></div>
            <div title="部门需求" name="deptDemand" refreshOnClick="true"></div>
            <div title="变化点" name="changeInfo" refreshOnClick="true"></div>
            <div title="功能&特性要求" name="functionAndRequest" refreshOnClick="true"></div>
            <div title="结构功能分析" name="functionNet" refreshOnClick="true"></div>
            <div title="失效风险分析" name="riskAnalysis" refreshOnClick="true"></div>
            <div title="特性值" name="quotaDecompose" refreshOnClick="true"></div>
            <div title="验证任务编制&执行结果" name="testTask" refreshOnClick="true"></div>
            <div title="指标综合评价" name="quotaEvaluate" refreshOnClick="true"></div>
            <div title="验证改进优化" name="finalProcess" refreshOnClick="true"></div>
        </div>
	</div>
</div>

<div id="copySelectedWindow" title="选择需要快速引入的部件项目" class="mini-window" style="width:1500px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目流水号: </span>
                        <input class="mini-textbox" id="singleNumber" name="singleNumber" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">设计型号: </span>
                        <input class="mini-textbox" id="jixing" name="jixing" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">总项目名称: </span>
                        <input class="mini-textbox" id="analyseName" name="analyseName" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件名称: </span>
                        <input id="structName" name="structName" class="mini-textbox"/>
                    </li>
                    <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchSingleGridData()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearSingleGridData()">清空查询</a>
                    </li>
                    <li style="display: inline-block;float: right;">
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="copySingleProcess()">确认</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="closeSingleGridData()">关闭</a>
                    </li>
                </ul>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">部件类型: </span>
                            <input id="structType" name="structType" class="mini-combobox" style="width:120px;"
                                   textField="value" valueField="key" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                   data="[ {'key':'整机','value':'整机'},{'key' : '新零部件','value' : '新零部件'},{'key' : '延用国三件','value' : '延用国三件'}]"
                            />
                        </li>
                        <li style="margin-right: 15px"><span class="text" style="width:auto">产品主管:</span>
                            <input id="creator" name="creator" class="mini-textbox"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">流程状态: </span>
                            <input id="instStatus" name="instStatus" class="mini-combobox" style="width:120px;"
                                   textField="value" valueField="key" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '已完成'},
							   {'key' : 'DISCARD_END','value' : '作废'}]"
                            />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">部件责任人: </span>
                            <input id="analyseUserName" name="analyseUserName" class="mini-textbox"/>
                        </li>
                    </ul>
                </div>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="singleListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/drbfm/single/getSelectSingleList.do" idField="id" allowCellWrap="true"
             multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="singleNumber" width="140" headerAlign="center" align="center" allowSort="true">项目流水号</div>
                <div field="structName" width="100" headerAlign="center" align="center" allowSort="true">部件名称</div>
                <div field="structNumber" width="100" headerAlign="center" align="center" allowSort="true">部件代号</div>
                <div field="structType" width="70" headerAlign="center" align="center" allowSort="true">类型</div>
                <div field="parentStructName" width="100" headerAlign="center" align="center" allowSort="true">上层部件</div>
                <div field="jixing" width="70" headerAlign="center" align="center" allowSort="true">设计型号</div>
                <div field="analyseName" width="100" headerAlign="center" align="center" allowSort="true">所属总项目名称</div>
                <div field="analyseUserName" width="70" headerAlign="center" align="center" allowSort="true">部件责任人</div>
                <div field="instStatus"  width="60" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">流程状态</div>
                <div field="currentProcessUser"  sortField="currentProcessUser"  width="60" align="center" headerAlign="center" allowSort="false">当前处理人</div>
                <div field="currentProcessTask" width="100" align="center" headerAlign="center">当前流程节点</div>
                <div field="creator" sortField="creator" width="60" headerAlign="center" align="center" allowSort="true">产品主管</div>
                <div field="CREATE_TIME_" sortField="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center" allowSort="true">创建时间</div>
            </div>
        </div>
    </div>
</div>

<script>
    mini.parse();
	var jsUseCtxPath="${ctxPath}";
    var stageName ="${stageName}";
    var action="${action}";
    var status="${status}";
	var currentUserNo = "${currentUserNo}";
	var currentUserId = "${currentUserId}";
    var systemTab=mini.get("systemTab");
    var singleId = "${singleId}";
    var startOrEnd = "${startOrEnd}";
    var formProject = new mini.Form("formProject");
    var searchFormProject = new mini.Form("searchForm");
    var copySelectedWindow=mini.get("copySelectedWindow");
    var singleListGrid=mini.get("singleListGrid");
    //用于传选择的产品型号
    var selectRowNames = "";
    var selectRowIds = "";


    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.instStatus;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '已完成','css' : 'blue'},
            {'key' : 'DISCARD_END','value' : '作废','css' : 'red'}
        ];

        return $.formatItemValue(arr,status);
    }
</script>
</body>
</html>