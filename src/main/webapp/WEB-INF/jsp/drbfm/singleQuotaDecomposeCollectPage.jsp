
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>指标分解管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        .mini-grid-rows-view
        {
            background: white !important;
        }
        .mini-grid-cell-inner {
            line-height: 40px !important;
            padding: 0;
        }
        .mini-grid-cell-inner
        {
            font-size:14px !important;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="width: 100%; height: 95%;background: #fff">
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 5px">
                <ul >
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">特性名称: </span>
                        <input id="quotaNameFilter" name="quotaName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">指标状态: </span>
                        <input id="validStatusFilter" name="validStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '有效','value' : '有效'},{'key' : '作废','value' : '作废'}]"
                        />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearFormQua()">清空查询</a>
                    </li>
                    <li id="detailToolBar" style="display: none">
                        <div style="display: inline-block" class="separator"></div>
                        <a class="mini-button" id="addQuota" plain="true" style="margin-right: 5px"
                           onclick="addQuota()">添加</a>
                        <a class="mini-button btn-red" id="delQuota" plain="true" style="margin-right: 5px"
                           onclick="removeQuota()">删除</a>
                    </li>
                    <li>
                        <a class="mini-button" style="margin-right: 5px"  plain="true" id="exQuota"
                           onclick="exportQuota()">导出</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>

    <div id="quotaListGrid" class="mini-datagrid" allowResize="false" style="height: 93%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true" autoload="false"
<%--         url="${ctxPath}/drbfm/single/getQuotaList.do?belongSingleId=${singleId}"--%>
    >
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="170px" headerAlign="center" align="center"
                 renderer="onQuotaRenderer" cellStyle="padding:0;">操作</div>
            <div field="quotaName" align="center" headerAlign="center" width="200px" align="left">特性名称
            </div>
            <div field="sjStandardValue" align="center" headerAlign="center" width="100px" align="left">特性值
            </div>
            <div field="functionDesc" headerAlign="center" width="250px" align="left">关联功能
            </div>
            <div field="requestDesc" headerAlign="center" width="250px" align="left">关联特性要求
            </div>
            <div field="sjStandardNames" headerAlign="center" width="230px" align="left">设计标准
            </div>
            <div field="testStandardNames" headerAlign="center" width="230px" align="left">测试标准
            </div>
            <div field="evaluateStandardNames" headerAlign="center" width="230px" align="left">评价标准
            </div>
            <div field="validStatus" align="center" headerAlign="center" width="90px" align="left" renderer="validStatusRenderer">指标状态
            </div>
            <div field="replaceQuotaName" align="center" headerAlign="center" width="110px" align="left">代替的旧指标
            </div>
            <div field="CREATE_TIME_" align="center" headerAlign="center" width="130px">创建时间</div>
            <div field="stopTime" align="center" headerAlign="center" width="130px">作废时间</div>
        </div>
    </div>
</div>

<div id="quotaWindow" title="指标分解编辑" class="mini-window" style="width:940px;height:750px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveQuota" class="mini-button" onclick="saveQuota()">保存</a>
                <a id="closeQuota" class="mini-button btn-red" onclick="closeQuota()">关闭</a>
            </div>
        </div>
        <input id="useScene" class="mini-hidden"/>
        <input id="replaceQuotaId" name="replaceQuotaId" class="mini-hidden"/>
        <input id="quotaId" name="id" class="mini-hidden"/>
        <input id="validStatus" name="validStatus" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="text-align: center;">指标名称<span style="color:red">*</span></td>
                <td style="width: 80%">
                    <input id="quotaName"  name="quotaName"  class="mini-textbox" style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">设计标准值<span style="color:red">*</span></td>
                <td style="width: 80%">
                    <input id="sjStandardValue"  name="sjStandardValue"  class="mini-textbox" style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">关联的要求：</td>
                <td style="width: 80%">
                    <input id="relRequestId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRelRequestCloseClick()"
                           name="relRequestId" textname="relRequestDesc" allowInput="false"
                           onbuttonclick="selectRequestClick()"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">设计标准：</td>
                <td style="width: 80%">
                    <input id="sjStandardIds" name="sjStandardIds" textname="sjStandardNames" property="editor"
                           class="mini-buttonedit"
                           showClose="true" allowInput="false"
                           oncloseclick="sjStandardCloseClick()" onbuttonclick="selectStandardClick('sjStandard')"
                           style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">设计标准是否锁定<br><span style="color: red;font-size: 10px">（锁定后不允许验证人员修改）</span></td>
                <td style="width: 80%">
                    <input id="sjStandardLock" name="sjStandardLock" class="mini-checkbox" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">测试标准：</td>
                <td style="width: 80%;">
                    <input id="testStandardIds" name="testStandardIds" textname="testStandardNames"
                           property="editor" class="mini-buttonedit"
                           showClose="true" allowInput="false"
                           oncloseclick="csStandardCloseClick()" onbuttonclick="selectStandardClick('csStandard')"
                           style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">测试标准是否锁定<br><span style="color: red;font-size: 10px">（锁定后不允许验证人员修改）</span></td>
                <td style="width: 80%">
                    <input id="testStandardLock" name="testStandardLock" class="mini-checkbox" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">评价标准：</td>
                <td style="width: 80%;">
                    <input id="evaluateStandardIds" name="evaluateStandardIds" textname="evaluateStandardNames"
                           property="editor"
                           class="mini-buttonedit" showClose="true" allowInput="false"
                           oncloseclick="pjStandardCloseClick()" onbuttonclick="selectStandardClick('pjStandard')"
                           style="width:98%;"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">评价标准是否锁定<br><span style="color: red;font-size: 10px">（锁定后不允许验证人员修改）</span></td>
                <td style="width: 80%">
                    <input id="evaluateStandardLock" name="evaluateStandardLock" class="mini-checkbox" />
                </td>
            </tr>
            <tr>
                <td style="text-align: center;height: 200px">附件：</td>
                <td colspan="3">
                    <div class="mini-toolbar" style="margin-bottom: 5px" id="fileListToolBar">
                        <a id="addFile" class="mini-button" onclick="addQuotaFile()">添加附件</a>
                        <span style="color: red">注：添加附件前，请先点击“保存”</span>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                         allowResize="false"
                         idField="id"
                         showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                            <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                            <div field="creator" width="70" headerAlign="center" align="center">创建人</div>
                            <div field="CREATE_TIME_" width="90" headerAlign="center" align="center"
                                 dateFormat="yyyy-MM-dd HH:mm:ss">创建时间
                            </div>
                            <div field="action" width="100" headerAlign='center' align="center"
                                 renderer="operationRenderer">
                                操作
                            </div>
                        </div>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectRequestWindow" title="选择要求" class="mini-window" style="width:800px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">要求描述: </span><input
            class="mini-textbox" style="width: 120px" id="requestDesc" name="requestDesc"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchRequest()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectRequestListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             url="${ctxPath}/drbfm/single/getRequestList.do?belongSingleId=${singleId}">
            <div property="columns">
                <div type="checkcolumn" width="10px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="10">序号</div>
                <div field="requestDesc" headerAlign="center" width="70px" align="left"
                     renderer="renderDemandDesc">要求描述
                </div>
                <div field="relDimensionNames" headerAlign="center" width="20px" align="left">所属维度
                </div>
                <div field="relFunctionDesc" headerAlign="center" width="40px" align="left"
                     renderer="renderDemandDesc">关联功能描述
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectRequestOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectRequestHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="standardType" name="standardType" class="mini-hidden" />
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">体系类别: </span>
        <input class="mini-combobox" width="130" id="filterSystemCategory" style="margin-right: 15px" textField="text"
               valueField="id" emptyText="请选择..." value="JS"
               data="[{id:'JS',text:'技术标准'},{id:'GL',text:'管理标准'}]"/>
        <span style="font-size: 14px;color: #777">编号: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">名称: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchStandardList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
             allowAlternating="true" pagerButtons="#pagerButtons"
             url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true">标准类别
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">编号
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true">名称
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="standardStatusRenderer">状态
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true">起草人
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleId = "${singleId}";
    var action="${action}";
    var stageName = "${stageName}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var quotaListGrid=mini.get("quotaListGrid");
    var quotaWindow=mini.get("quotaWindow");

    var collectType = "${collectType}";
    var belongCollectFlowId = "${belongCollectFlowId}";

    var selectRequestWindow = mini.get("selectRequestWindow");
    var selectRequestListGrid = mini.get("selectRequestListGrid");

    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");
    var fileListGrid= mini.get("fileListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司"

    $(function () {
        if(action=='task'&&stageName=='bjfzrfxfx'){
            $("#detailToolBar").show();
        }
        if (action=='edit' && collectType=='collectType'){
            $("#detailToolBar").show();
        }
        if (action=='task' && collectType=='collectType' && (stageName=='bianzhi' || stageName=='requestedConfirm' || stageName=='directorConfirm')){
            $("#detailToolBar").show();
        }
        if (collectType=='collectType'){
            $("#exQuota").attr("style","display:none;");
            querySingleIdInterface(belongCollectFlowId);
            var urlQuo = "${ctxPath}/drbfm/single/getQuotaInterfaceList.do?belongCollectFlowId=${belongCollectFlowId}";
            if (belongCollectFlowId == ""){
                urlQuo = "${ctxPath}/drbfm/single/getQuotaInterfaceList.do?belongCollectFlowId=error";
            }
            quotaListGrid.setUrl(urlQuo);
            quotaListGrid.load();
        }
    });
    //行功能按钮
    function onQuotaRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailQuota(\'' + id +'\')">查看</span>';
        if(action=='task'&&CREATE_BY_==currentUserId&&stageName=='bjfzrfxfx'&&!record.belongCollectFlowId){
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editQuota(\'' + id +'\')">编辑</span>';
            if(record.validStatus=='有效') {
                s += '<span  style="color:#2ca9f6;cursor: pointer" title="直接作废" onclick="stopQuota(\'' + id + '\')">直接作废</span>';
                s += '<span  style="color:#2ca9f6;cursor: pointer" title="作废&创建新指标" onclick="discardAndCreateNew(' +JSON.stringify(record).replace(/"/g, '&quot;')+')">作废&创建新指标</span>';
            }
        }
        if ((action=='task' && collectType=='collectType' && (stageName=='bianzhi' || stageName=='requestedConfirm' || stageName=='directorConfirm'))||
            (action=='edit' && collectType=='collectType')){
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editQuota(\'' + id +'\')">编辑</span>';
            // if(record.validStatus=='有效') {
            //     s += '<span  style="color:#2ca9f6;cursor: pointer" title="直接作废" onclick="stopQuota(\'' + id + '\')">直接作废</span>';
            //     s += '<span  style="color:#2ca9f6;cursor: pointer" title="作废&创建新指标" onclick="discardAndCreateNew(' +JSON.stringify(record).replace(/"/g, '&quot;')+')">作废&创建新指标</span>';
            // }
        }
        if(CREATE_BY_==currentUserId) {
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="标准维护" onclick="editQuotaStandard(\'' + id +'\')">标准维护</span>';
        }
        return s;
    }

    //功能描述
    function addQuota() {
        if (collectType=='collectType' && !belongCollectFlowId){
            mini.alert("请先保存流程，再进行“添加”操作！");
            return;
        }
        mini.get("quotaName").setEnabled(true);
        mini.get("relRequestId").setEnabled(true);
        mini.get("sjStandardValue").setEnabled(true);
        mini.get("sjStandardIds").setEnabled(true);
        mini.get("testStandardIds").setEnabled(true);
        mini.get("evaluateStandardIds").setEnabled(true);
        mini.get("sjStandardLock").setEnabled(true);
        mini.get("testStandardLock").setEnabled(true);
        mini.get("evaluateStandardLock").setEnabled(true);
        $("#saveQuota").show();
        $("#fileListToolBar").show();
        quotaWindow.show();
        var url=jsUseCtxPath+"/drbfm/single/quotaFileList.do?relQuotaId=";
        fileListGrid.setUrl(url);
        fileListGrid.load();
    }

    function validQuota() {
        var quotaName = $.trim(mini.get("quotaName").getValue())
        if (!quotaName) {
            return {"result": false, "message": "请填写指标名称"};
        }
        var sjStandardValue = $.trim(mini.get("sjStandardValue").getValue())
        if (!sjStandardValue) {
            return {"result": false, "message": "请填写设计标准值"};
        }
        // var relRequestId = $.trim(mini.get("relRequestId").getValue())
        // if (!relRequestId) {
        //     return {"result": false, "message": "请填写关联的要求分解"};
        // }
        return {"result": true};
    }

    function saveQuota() {
        var formValid = validQuota();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var id = mini.get("quotaId").getValue();
        var replaceQuotaId = mini.get("replaceQuotaId").getValue();
        var quotaName = mini.get("quotaName").getValue();
        var validStatus = mini.get("validStatus").getValue();
        var relRequestId = mini.get("relRequestId").getValue();
        var sjStandardValue = mini.get("sjStandardValue").getValue();
        var sjStandardIds = mini.get("sjStandardIds").getValue();
        var testStandardIds = mini.get("testStandardIds").getValue();
        var evaluateStandardIds = mini.get("evaluateStandardIds").getValue();
        var sjStandardLock = mini.get("sjStandardLock").getValue();
        var testStandardLock = mini.get("testStandardLock").getValue();
        var evaluateStandardLock = mini.get("evaluateStandardLock").getValue();
        var data = {id:id,
            quotaName:quotaName,
            sjStandardValue:sjStandardValue,
            belongSingleId:singleId,
            relRequestId:relRequestId,
            validStatus:validStatus,
            sjStandardIds:sjStandardIds,
            sjStandardLock:sjStandardLock,
            testStandardIds:testStandardIds,
	        testStandardLock:testStandardLock,
            evaluateStandardIds:evaluateStandardIds,
            evaluateStandardLock:evaluateStandardLock,
            replaceQuotaId:replaceQuotaId,
            belongCollectFlowId:belongCollectFlowId
        };
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveQuota.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            closeQuota();
                            // editQuota(returnData.data)
                        }
                    });
                }
            }
        });
    }

    function detailQuota(id) {
        quotaWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getQuotaDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("quotaId").setValue(json.id);
                    mini.get("validStatus").setValue(json.validStatus);
                    mini.get("quotaName").setValue(json.quotaName);
                    mini.get("relRequestId").setValue(json.relRequestId);
                    mini.get("relRequestId").setText(json.requestDesc);
                    mini.get("sjStandardValue").setValue(json.sjStandardValue);
                    mini.get("sjStandardIds").setValue(json.sjStandardIds);
                    mini.get("testStandardIds").setValue(json.testStandardIds);
                    mini.get("evaluateStandardIds").setValue(json.evaluateStandardIds);
                    mini.get("sjStandardIds").setText(json.sjStandardNames);
                    mini.get("testStandardIds").setText(json.testStandardNames);
                    mini.get("evaluateStandardIds").setText(json.evaluateStandardNames);
                    mini.get("sjStandardLock").setValue(json.sjStandardLock);
                    mini.get("testStandardLock").setValue(json.testStandardLock);
                    mini.get("evaluateStandardLock").setValue(json.evaluateStandardLock);
                });
            $.ajaxSettings.async = true;
            var url=jsUseCtxPath+"/drbfm/single/quotaFileList.do?relQuotaId="+id;
            fileListGrid.setUrl(url);
            fileListGrid.load();
        }
        mini.get("quotaName").setEnabled(false);
        mini.get("relRequestId").setEnabled(false);
        mini.get("sjStandardValue").setEnabled(false);
        mini.get("sjStandardIds").setEnabled(false);
        mini.get("testStandardIds").setEnabled(false);
        mini.get("evaluateStandardIds").setEnabled(false);
        mini.get("sjStandardLock").setEnabled(false);
        mini.get("testStandardLock").setEnabled(false);
        mini.get("evaluateStandardLock").setEnabled(false);
        $("#saveQuota").hide();
        $("#fileListToolBar").hide();
    }

    function editQuotaStandard(id) {
        if(!id) {
            mini.alert("指标不存在！");
            return;
        }
        quotaWindow.show();
        var url = jsUseCtxPath + "/drbfm/single/getQuotaDetail.do";
        $.ajaxSettings.async = false;
        $.post(
            url,
            {id: id},
            function (json) {
                mini.get("quotaId").setValue(json.id);
                mini.get("validStatus").setValue(json.validStatus);
                mini.get("quotaName").setValue(json.quotaName);
                mini.get("relRequestId").setValue(json.relRequestId);
                mini.get("relRequestId").setText(json.requestDesc);
                mini.get("sjStandardValue").setValue(json.sjStandardValue);
                mini.get("sjStandardIds").setValue(json.sjStandardIds);
                mini.get("testStandardIds").setValue(json.testStandardIds);
                mini.get("evaluateStandardIds").setValue(json.evaluateStandardIds);
                mini.get("sjStandardIds").setText(json.sjStandardNames);
                mini.get("testStandardIds").setText(json.testStandardNames);
                mini.get("evaluateStandardIds").setText(json.evaluateStandardNames);
                mini.get("sjStandardLock").setValue(json.sjStandardLock);
                mini.get("testStandardLock").setValue(json.testStandardLock);
                mini.get("evaluateStandardLock").setValue(json.evaluateStandardLock);
            });
        $.ajaxSettings.async = true;
        var url=jsUseCtxPath+"/drbfm/single/quotaFileList.do?relQuotaId="+id;
        fileListGrid.setUrl(url);
        fileListGrid.load();
        mini.get("quotaName").setEnabled(false);
        mini.get("relRequestId").setEnabled(false);
        mini.get("sjStandardValue").setEnabled(false);
        mini.get("sjStandardIds").setEnabled(true);
        mini.get("testStandardIds").setEnabled(true);
        mini.get("evaluateStandardIds").setEnabled(true);
        mini.get("sjStandardLock").setEnabled(true);
        mini.get("testStandardLock").setEnabled(true);
        mini.get("evaluateStandardLock").setEnabled(true);
        $("#saveQuota").show();
        $("#fileListToolBar").hide();
    }

    function editQuota(id) {
        quotaWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getQuotaDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("quotaId").setValue(json.id);
                    mini.get("validStatus").setValue(json.validStatus);
                    mini.get("quotaName").setValue(json.quotaName);
                    mini.get("relRequestId").setValue(json.relRequestId);
                    mini.get("relRequestId").setText(json.requestDesc);
                    mini.get("sjStandardValue").setValue(json.sjStandardValue);
                    mini.get("sjStandardIds").setValue(json.sjStandardIds);
                    mini.get("testStandardIds").setValue(json.testStandardIds);
                    mini.get("evaluateStandardIds").setValue(json.evaluateStandardIds);
                    mini.get("sjStandardIds").setText(json.sjStandardNames);
                    mini.get("testStandardIds").setText(json.testStandardNames);
                    mini.get("evaluateStandardIds").setText(json.evaluateStandardNames);
                    mini.get("sjStandardLock").setValue(json.sjStandardLock);
                    mini.get("testStandardLock").setValue(json.testStandardLock);
                    mini.get("evaluateStandardLock").setValue(json.evaluateStandardLock);
                    mini.get("useScene").setValue('edit');
                });
            $.ajaxSettings.async = true;
            var url=jsUseCtxPath+"/drbfm/single/quotaFileList.do?relQuotaId="+id;
            fileListGrid.setUrl(url);
            fileListGrid.load();
        }
        mini.get("quotaName").setEnabled(true);
        mini.get("relRequestId").setEnabled(true);
        mini.get("sjStandardValue").setEnabled(true);
        mini.get("sjStandardIds").setEnabled(true);
        mini.get("testStandardIds").setEnabled(true);
        mini.get("evaluateStandardIds").setEnabled(true);
        mini.get("sjStandardLock").setEnabled(true);
        mini.get("testStandardLock").setEnabled(true);
        mini.get("evaluateStandardLock").setEnabled(true);
        $("#saveQuota").show();
        $("#fileListToolBar").show();
    }

    function closeQuota() {
        mini.get("replaceQuotaId").setValue('');
        mini.get("quotaName").setValue('');
        mini.get("sjStandardValue").setValue('');
        mini.get("quotaId").setValue('');
        mini.get("relRequestId").setValue('');
        mini.get("relRequestId").setText('');
        mini.get("validStatus").setValue('');
        mini.get("sjStandardIds").setValue('');
        mini.get("sjStandardIds").setText('');
        mini.get("sjStandardLock").setValue('');
        mini.get("testStandardIds").setText('');
        mini.get("testStandardIds").setValue('');
        mini.get("testStandardLock").setValue('');
        mini.get("evaluateStandardIds").setValue('');
        mini.get("evaluateStandardIds").setText('');
        mini.get("evaluateStandardLock").setValue('');
        mini.get("useScene").setValue('');
        quotaWindow.hide();
        quotaListGrid.reload();
    }

    function removeQuota() {
        var rows = quotaListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if(r.hasOwnProperty("belongCollectFlowId") && r.belongCollectFlowId){
                        mini.alert("接口收集流程传入的数据不能删除！");
                        return;
                    }
                    rowIds.push(r.id);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/single/deleteQuota.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message,"提示",function () {
                                quotaListGrid.reload();
                            });
                        }
                    }
                });
            }
        });
    }
    
    //指标分解选择要求
    function selectRequestClick() {
        if (collectType=='collectType' && !singleId){
            mini.alert("该部件没有关联有效的部件分析项目流程，不可填写！");
            return ;
        }
        if (collectType=='collectType' && belongCollectFlowId){
            selectRequestWindow.show();
            var url="${ctxPath}/drbfm/single/getRequestListByCollectId.do?belongCollectFlowId=" + belongCollectFlowId;
            selectRequestListGrid.setUrl(url);
            selectRequestListGrid.reload();
        }
        searchRequest();
    }

    function searchRequest() {
        var queryParam = [];
        //其他筛选条件
        var requestDesc = $.trim(mini.get("requestDesc").getValue());
        if (requestDesc) {
            queryParam.push({name: "requestDesc", value: requestDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectRequestListGrid.load(data);
    }

    function selectRequestOK() {
        var selectRow = selectRequestListGrid.getSelected();
        mini.get("relRequestId").setValue(selectRow.id);
        mini.get("relRequestId").setText(selectRow.requestDesc);
        selectRequestHide();
    }

    function selectRequestHide() {
        selectRequestWindow.hide();
        mini.get("requestDesc").setValue('');
    }

    function onRelRequestCloseClick() {
        mini.get("relRequestId").setValue('');
        mini.get("relRequestId").setText('');
    }

    function validStatusRenderer(e) {
        var record = e.record;
        var validStatus = record.validStatus;
        if(!validStatus) {
            return "";
        }
        if (validStatus=='有效') {
            return '<span style="color:green">有效</span>'
        }  else if (validStatus=='作废') {
            return '<span style="color:red">作废</span>'
        }
    }

    function sjStandardCloseClick() {
        mini.get("sjStandardIds").setValue("");
        mini.get("sjStandardIds").setText("");
    }

    function csStandardCloseClick() {
        mini.get("testStandardIds").setValue("");
        mini.get("testStandardIds").setText("");
    }

    function pjStandardCloseClick() {
        mini.get("evaluateStandardIds").setValue("");
        mini.get("evaluateStandardIds").setText("");
    }

    //标准引用
    function selectStandardClick(standardType) {
        selectStandardWindow.show();
        mini.get("standardType").setValue(standardType);
        searchStandardList();
    }

    //查询标准
    function searchStandardList() {
        var queryParam = [];
        var systemCategoryId = $.trim(mini.get("filterSystemCategory").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        //默认搜索有效的
        queryParam.push({name: "standardStatus", value: "enable"});
        var inputList = '';
        inputList = standardListGrid;
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = 0;
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var standardType=mini.get("standardType").getValue();
        var inputList = '';
        inputList = standardListGrid;
        // 这里要改成多选，只返回id，用逗号拼接
        var selectRows = inputList.getSelecteds();
        if (selectRows && selectRows.length>0) {
            var ids = [];
            var names = [];
            for (var i = 0; i < selectRows.length; i++) {
                var row = selectRows[i];
                ids.push(row.id);
                names.push('【'+row.standardNumber+'】'+row.standardName);
            }
            var idsStr = ids.join(',');
            var namesStr = names.join('，');
            if (standardType == "sjStandard") {
                var existValue=mini.get("sjStandardIds").getValue();
                if(existValue) {
                    idsStr=existValue+","+idsStr;
                    namesStr=mini.get("sjStandardIds").getText()+"，"+namesStr;
                }
                mini.get("sjStandardIds").setValue(idsStr);
                mini.get("sjStandardIds").setText(namesStr);
            } else if (standardType == "csStandard") {
                var existValue=mini.get("testStandardIds").getValue();
                if(existValue) {
                    idsStr=existValue+","+idsStr;
                    namesStr=mini.get("testStandardIds").getText()+"，"+namesStr;
                }
                mini.get("testStandardIds").setValue(idsStr);
                mini.get("testStandardIds").setText(namesStr);
            } else if (standardType == "pjStandard") {
                var existValue=mini.get("evaluateStandardIds").getValue();
                if(existValue) {
                    idsStr=existValue+","+idsStr;
                    namesStr=mini.get("evaluateStandardIds").getText()+"，"+namesStr;
                }
                mini.get("evaluateStandardIds").setValue(idsStr);
                mini.get("evaluateStandardIds").setText(namesStr);
            } else {
                mini.alert("标准选择错误！");
                return;
            }

        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectStandardHide();
    }

    function selectStandardHide() {
        selectStandardWindow.hide();
        mini.get("standardType").setValue('');
        mini.get("filterSystemCategory").setValue('JS');
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
        standardListGrid.deselectAll(true);
    }

    function standardStatusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function stopQuota(quotaId) {
        mini.confirm("该操作会作废当前指标，确定继续？", "确定？",
            function (action) {
                if (action == "ok") {
                    $.ajax({
                        url: jsUseCtxPath + "/drbfm/single/stopOldQuota.do?quotaId="+quotaId,
                        async: false,
                        contentType: 'application/json',
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message,"提示",function () {
                                    quotaListGrid.reload();
                                });
                            }
                        }
                    });
                }
            }
        );
    }

    function discardAndCreateNew(record) {
        mini.confirm("该操作会作废当前指标并创建一条新指标，确定继续？", "确定？",
            function (action) {
                if (action == "ok") {
                    if (record.id) {
                        var url = jsUseCtxPath + "/drbfm/single/getQuotaDetail.do";
                        $.ajaxSettings.async = false;
                        $.post(
                            url,
                            {id: record.id},
                            function (json) {
                                mini.get("replaceQuotaId").setValue(json.id);
                                mini.get("quotaName").setValue(json.quotaName);
                                mini.get("sjStandardValue").setValue(json.sjStandardValue);
                                mini.get("relRequestId").setValue(json.relRequestId);
                                mini.get("relRequestId").setText(json.requestDesc);
                                mini.get("sjStandardIds").setValue(json.sjStandardIds);
                                mini.get("sjStandardIds").setText(json.sjStandardNames);
                                mini.get("sjStandardLock").setValue(json.sjStandardLock);
                                mini.get("testStandardIds").setValue(json.testStandardIds);
                                mini.get("testStandardIds").setText(json.testStandardNames);
                                mini.get("testStandardLock").setValue(json.testStandardLock);
                                mini.get("evaluateStandardIds").setValue(json.evaluateStandardIds);
                                mini.get("evaluateStandardIds").setText(json.evaluateStandardNames);
                                mini.get("evaluateStandardLock").setValue(json.evaluateStandardLock);
                            });
                        $.ajaxSettings.async = true;
                    }
                    quotaWindow.show();

                    mini.get("quotaName").setEnabled(true);
                    mini.get("relRequestId").setEnabled(true);
                    mini.get("sjStandardValue").setEnabled(true);
                    mini.get("sjStandardIds").setEnabled(true);
                    mini.get("testStandardIds").setEnabled(true);
                    mini.get("evaluateStandardIds").setEnabled(true);
                    mini.get("sjStandardLock").setEnabled(true);
                    mini.get("testStandardLock").setEnabled(true);
                    mini.get("evaluateStandardLock").setEnabled(true);
                    $("#saveQuota").show();
                    $("#fileListToolBar").show();
                }
            }
        );
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title="预览" style="color: silver" >预览</span>';
        } else {
            var url = '/drbfm/testTask/preview.do?fileType=' + fileType;
            s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">预览</span>';
        }
        return s;
    }
    //指标附件
    function addQuotaFile() {
        if (collectType=='collectType' && !singleId){
            mini.alert("该部件没有关联有效的部件分析项目流程，不可填写！");
            return ;
        }
        var quotaId = mini.get("quotaId").getValue();
        if (!quotaId) {
            mini.alert('请先点击‘保存’进行指标的保存！');
            return;
        }
        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/drbfm/single/openUploadWindow.do?relQuotaId=" + quotaId + "&belongSingleId" + singleId ,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.relQuotaId = quotaId;
                projectParams.belongSingleId = singleId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.relQuotaId, coverContent);
        var downloadUrl = '/drbfm/testTask/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.relQuotaId + '\',\'' + downloadUrl + '\')">下载</span>';
        }
        var useScene = mini.get("useScene").getValue();
        if (record && record.CREATE_BY_ == currentUserId && useScene=='edit') {
            var deleteUrl = "/drbfm/testTask/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.relQuotaId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

    //导出指标分解模块
    //exportQuota
    function exportQuota(){
        var params=[];
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            var el=$(this);
            var obj={};
            obj.name=el.attr("name");
            if(!obj.name) return true;
            obj.value=el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        var url = jsUseCtxPath+"/drbfm/single/exportQuota.do?singleId="+singleId;
        excelForm.attr("action",url);
        excelForm.submit();
    }

    function clearFormQua(){
        if (collectType=='collectType'){
            clearForm();
            var urlQuo = "${ctxPath}/drbfm/single/getQuotaInterfaceList.do?belongCollectFlowId=${belongCollectFlowId}";
            if (belongCollectFlowId == ""){
                urlQuo = "${ctxPath}/drbfm/single/getQuotaInterfaceList.do?belongCollectFlowId=error";
            }
            quotaListGrid.setUrl(urlQuo);
            quotaListGrid.load();
        }else {
            clearForm();
        }

    }

    //根据id获取singleId
    function querySingleIdInterface(belongCollectFlowId){
        $.ajax({
            url: jsUseCtxPath+"/drbfm/single/querySingleIdInterface.do?id="+belongCollectFlowId,
            method: 'GET',
            async: false,
            success: function (data) {
                singleId = data;
            }
        });
    }
</script>
<redxun:gridScript gridId="quotaListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
