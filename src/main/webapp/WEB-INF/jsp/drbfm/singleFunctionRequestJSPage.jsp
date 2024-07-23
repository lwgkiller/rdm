
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>功能&要求描述管理</title>
    <%@include file="/commons/list.jsp"%>
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
<div class="mini-fit" style="width: 100%; height: 100%;background: #fff">
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">功能描述</p>
    <hr>
    <div id="functionToolBar" class="topToolBar">
        <div style="position: relative!important;">
            <a class="mini-button" id="addFunction" plain="true" style="float: left"
               onclick="addFunction()">添加</a>
            <a class="mini-button btn-red" id="delFunction" plain="true" style="float: left"
               onclick="removeFunction()">删除</a>
        </div>
    </div>
    <div id="functionListGrid" class="mini-datagrid" allowResize="false" style="height: 30%" autoload="true"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true"
         url="${ctxPath}/drbfm/single/getFunctionListByJSId.do?jsId=${jsId}">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onFunctionRenderer" cellStyle="padding:0;">操作</div>
            <div field="functionDesc" headerAlign="center" width="600px" align="left">功能描述<span style="color:red">*</span>
            </div>
            <div field="relDimensionNames" headerAlign="center" width="200px" align="center">所属维度<span style="color:red">*</span>
            </div>
            <div field="demandDesc" headerAlign="center" width="400px" align="left">关联部门需求描述
            </div>
            <div field="structName" headerAlign="center" width="150px" align="left">接口名称
            </div>
        </div>
    </div>
    <p style="font-size: 16px;font-weight: bold;margin-top: 5px">要求描述（必填）</p>
    <hr>
    <div id="requestToolBar" class="topToolBar">
        <div style="position: relative!important;">
            <a class="mini-button" id="addDemand" plain="true" style="float: left"
               onclick="addRequest()">添加</a>
            <a class="mini-button btn-red" id="delDemand" plain="true" style="float: left"
               onclick="removeRequest()">删除</a>
        </div>
    </div>
    <div id="requestListGrid" class="mini-datagrid" allowResize="false" style="height: 46%"
         idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
         multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
         allowCellWrap="false" showVGridLines="true" autoload="true"
         url="${ctxPath}/drbfm/single/getRequestListByJSId.do?jsId=${jsId}">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" width="50px">序号</div>
            <div name="action" cellCls="actionIcons" width="100px" headerAlign="center" align="center"
                 renderer="onRequestRenderer" cellStyle="padding:0;">操作</div>
            <div renderer="riskLevelRenderer" field="riskLevel" headerAlign="center" width="100px" align="center">风险评级<span style="color:red">*</span>
            </div>
            <div field="requestDesc" headerAlign="center" width="750px" align="left"
                 >特性要求<span style="color:red">*</span>
            </div>
            <div field="relDimensionNames" headerAlign="center" width="200px" align="center">所属维度
            </div>
            <div field="riskDesc" headerAlign="center" width="200px" align="left"
            >存在风险<span style="color:red">*</span>
            </div>
            <div field="relFunctionDesc" headerAlign="center" width="400px" align="left"
                 >关联功能描述
            </div>
            <div field="requestChanges" headerAlign="center" width="400px" align="left"
                 >较上一代或相似产品变化点
            </div>
            <div field="relEffect" headerAlign="center" width="200px" align="left"
                 >变化点带来的与该要求相关的客观影响
            </div>
            <div field="compareToJP" headerAlign="center" width="200px" align="left"
                 >与竞品对比
            </div>
            <div field="structName" headerAlign="center" width="150px" align="left">接口名称
            </div>
        </div>
    </div>
</div>

<div id="functionWindow" title="功能编辑" class="mini-window" style="width:660px;height:320px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveFunction" class="mini-button" onclick="saveFunction()">保存</a>
                <a id="closeFunction" class="mini-button btn-red" onclick="closeFunction()">关闭</a>
            </div>
        </div>
        <input id="functionId" name="id" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 15%">功能描述(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id="functionDesc" name="functionDesc" class="mini-textarea"
                              plugins="mini-textarea" style="width:95%;;height:120px;line-height:25px;" label="功能描述"
                              allowinput="true" emptytext="请输入功能描述..."/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%;">所属维度：<span style="color:red">*</span></td>
                <td style="width: 85%;">
                    <input id="relDimensionKeys" name="relDimensionKeys" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" multiSelect="true"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SSWD"/>
                </td>
            </tr>
            <tr style="display: none;">
                <td style="width: 15%">关联的部门需求：</td>
                <td style="width: 35%;">
                    <input id="relDeptDemandId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRelDemandCloseClick()"
                           name="relDeptDemandId" textname="relDeptDemandDesc" allowInput="false"
                           onbuttonclick="selectDemandClick()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">接口名称：</td>
                <td style="width: 35%;">
                    <input id="interfaceRequestStructId" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRequestStructCloseClick()"
                           name="interfaceRequestStructId" textname="structName" allowInput="false"
                           onbuttonclick="selectRequestStructClick()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectDemandWindow" title="选择部门需求" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">部门需求描述: </span><input
            class="mini-textbox" style="width: 120px" id="demandDesc" name="demandDesc"/>
        <span class="text" style="width:auto">需求部门: </span><input
            class="mini-textbox" style="width: 120px" id="deptNames" name="deptNames"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDemand()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectDemandListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             url="${ctxPath}/drbfm/single/getSingleDeptDemandList.do?belongSingleId=${singleId}">
            <div property="columns">
                <div type="checkcolumn" width="10px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
                <div field="demandDesc" headerAlign="center" width="90px" align="left">部门需求描述
                </div>
                <div field="deptIds" displayField="deptNames" headerAlign="center" align="center" width="50px">需求部门
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectDemandOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectDemandHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="requestWindow" title="要求编辑" class="mini-window" style="width:950px;height:850px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
     <div style="float: right;margin-right: 20px">
         <a id="saveRequest" class="mini-button" onclick="saveRequest()">保存</a>
         <a id="closeRequest" class="mini-button btn-red" onclick="closeRequest()" >关闭</a>
     </div>
     <div style="clear: both"></div>
     <div class="mini-fit">
         <input id="requestId" name="id" class="mini-hidden"/>
         <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%">
            <tr>
                <td style="width: 8%">关联的功能：</td>
                <td style="width: 92%;">
                    <input id="relFunctionId" style="width:99%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRelFunctionCloseClick()"
                           name="relFunctionId" textname="relFunctionDesc" allowInput="false"
                           onbuttonclick="selectFunctionClick()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">所属维度：</td>
                <td style="width: 92%;">
                    <input id="relDimensionKeysR" name="relDimensionKeys" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" multiSelect="true"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SSWD"/>
                </td>
            </tr>

            <tr>
                <td style="width: 8%">要求描述(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="requestDesc" name="requestDesc" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:100px;line-height:25px;" label="要求描述"
                           allowinput="true" emptytext="请输入要求描述..."/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">较上一代或相似产品变化点<br>(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="requestChanges" name="requestChanges" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;height:100px;line-height:25px;" label="较上一代或相似产品变化点"
                           allowinput="true" emptytext="与该要求相关的内外部变化，明确对比产品型号，外部变化分析纬度：关联部件的变化，销售地，光，温度，振动，噪音，供应商，加工、组装等。无对比机型则填“无对比机型”"/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">变化点带来的与该要求<br>相关的客观影响(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="relEffect" name="relEffect" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:100px;line-height:25px;" label="变化点带来的与该要求相关的客观影响"
                           allowinput="true" emptytext="变化点带来的客观确定性的影响，较上一代产品或相近产品变化点会导致自身、上一级部件、关联系统部件、整机的什么功能要求发生变化。无关联影响则填“无”"/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">存在风险(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="riskDesc" name="riskDesc" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:100px;line-height:25px;" label="存在风险"
                           allowinput="true" emptytext="失效是要求的反面，如果是无风险、低风险，说明无风险、低风险的理由。如果是中高风险，说明失效的原因，失效模式和对上一级，关联部件的失效影响。"/>
                </td>
            </tr>
            <tr>
                <td style="width: 8%;">与竞品对比(500字以内)：<span style="color:red">*</span></td>
                <td style="width: 92%;" colspan="3">
                    <input id="compareToJP" name="compareToJP" class="mini-textarea"
                           plugins="mini-textarea" style="width:99%;;height:100px;line-height:25px;" label="与竞品对比"
                           allowinput="true" emptytext="明确竞品产品型号，其他挖机厂家无此产品则填“行业无此竞品”。对比内容包含不限于指标分解模块中各指标与竞品设计参数值的对比结果。" />
                </td>
            </tr>

            <tr>
                <td style="width: 8%;">风险评级：<span style="color:red">*</span></td>
                <td style="width: 35%;">
                    <input id="riskLevel" name="riskLevel" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRiskLevelCloseClick()"
                           textname="riskLevelName" allowInput="false"
                           onbuttonclick="selectInterfaceRiskLevel()"/>
                </td>
            </tr>
            <tr>
                <td style="width: 15%">接口名称：</td>
                <td style="width: 35%;">
                    <input id="interfaceRequestStructId_R" style="width:98%;" class="mini-buttonedit" showClose="true"
                           oncloseclick="onRequestStructCloseClick_R()"
                           name="interfaceRequestStructId" textname="structName" allowInput="false"
                           onbuttonclick="selectRequestStructClick_R()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectFunctionWindow" title="选择功能描述" class="mini-window" style="width:1050px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">功能描述: </span><input
            class="mini-textbox" style="width: 120px" id="functionDesc2" name="functionDesc2"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchFunction()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="selectFunctionListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false"
             allowAlternating="true" showPager="false"
             url="${ctxPath}/drbfm/single/getFunctionList.do?belongSingleId=${singleId}">
            <div property="columns">
                <div type="checkcolumn" width="20px"></div>
                <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
                <div field="functionDesc" headerAlign="center" width="290px" align="left">功能描述
                </div>
                <div field="relDimensionNames" headerAlign="center" width="160px" align="left">所属维度
                </div>
                <div field="demandDesc" headerAlign="center" width="180px" align="left">关联部门需求描述
                </div>
                <div field="structName" headerAlign="center" width="140px" align="center">接口名称
                </div>
                <div field="interfaceRequestStructId"  visible="false"></div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectFunctionOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectFunctionHide    ()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var singleId = "${singleId}";
    var jsId = "${jsId}";
    var action="${action}";
    var stageName = "${stageName}";
    var currentUserId = "${currentUserId}";

    var functionListGrid=mini.get("functionListGrid");
    var functionWindow=mini.get("functionWindow");

    var selectDemandWindow = mini.get("selectDemandWindow");
    var selectDemandListGrid = mini.get("selectDemandListGrid");

    var requestListGrid=mini.get("requestListGrid");
    var requestWindow=mini.get("requestWindow");

    var selectFunctionWindow = mini.get("selectFunctionWindow");
    var selectFunctionListGrid = mini.get("selectFunctionListGrid");

    function riskLevelRenderer(e) {
        var record = e.record;
        var riskLevel = record.riskLevel;

        var arr = [{'key': '高风险', 'value': '高风险', 'css': 'red'},
            {'key': '中风险', 'value': '中风险', 'css': 'orange'},
            {'key': '低风险', 'value': '低风险', 'css': 'green'}
        ];

        return $.formatItemValue(arr, riskLevel);
    }

    $(function () {
        $("#requestToolBar").hide();
        $("#functionToolBar").hide();
        // if(action=='task'&&stageName=='bjfzrfxfx'){
        if(action=='edit' || action=='add'){
            $("#requestToolBar").show();
            $("#functionToolBar").show();
        }
    });

    //行功能按钮
    function onFunctionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailFunction(\'' + id +'\')">查看</span>';
        if((action=='edit' || action=='add')&&CREATE_BY_==currentUserId){
            s+='<span style="display: inline-block" class="separator"></span>';
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editFunction(\'' + id +'\')">编辑</span>';
        }
        return s;
    }

    //行功能按钮
    function onRequestRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        s+='<span  style="color:#2ca9f6;cursor: pointer" title="查看" onclick="detailRequest(\'' + id +'\')">查看</span>';
        if((action=='edit' || action=='add')&&CREATE_BY_==currentUserId){
            s+='<span style="display: inline-block" class="separator"></span>';
            s+='<span  style="color:#2ca9f6;cursor: pointer" title="编辑" onclick="editRequest(\'' + id +'\')">编辑</span>';
        }
        return s;
    }

    //功能描述
    function addFunction() {
        if(!jsId){
            mini.alert('请先点击‘保存’进行表单的保存！');
            return ;
        }
        mini.get("interfaceRequestStructId").setEnabled(true);
        mini.get("relDimensionKeys").setEnabled(true);
        mini.get("relDeptDemandId").setEnabled(true);
        mini.get("functionDesc").setEnabled(true);
        mini.get("saveFunction").show();
        functionWindow.show();
    }

    function validFunction() {
        var functionDesc = $.trim(mini.get("functionDesc").getValue())
        if (!functionDesc) {
            return {"result": false, "message": "请填写功能描述"};
        }
        var relDimensionKeys = $.trim(mini.get("relDimensionKeys").getValue())
        if (!relDimensionKeys) {
            return {"result": false, "message": "请选择所属维度"};
        }
        return {"result": true};
    }

    function saveFunction() {
        var formValid = validFunction();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var functionDesc = mini.get("functionDesc").getValue();
        var relDimensionKeys = mini.get("relDimensionKeys").getValue();
        var relDimensionNames = mini.get("relDimensionKeys").getText();
        var id = mini.get("functionId").getValue();
        var relDeptDemandId = mini.get("relDeptDemandId").getValue();
        var interfaceRequestStructId = mini.get("interfaceRequestStructId").getValue();
        var data = {id:id,functionDesc:functionDesc,relDimensionKeys:relDimensionKeys,
            relDimensionNames:relDimensionNames,belongJSId:jsId,relDeptDemandId:relDeptDemandId,
            interfaceRequestStructId:interfaceRequestStructId};
        var json = mini.encode(data);
        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveFunction.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            functionListGrid.reload();
                            closeFunction();
                        }
                    });
                }
            }
        });
    }

    function detailFunction(id) {
        functionWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getFunctionDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("functionDesc").setValue(json.functionDesc);
                    mini.get("relDeptDemandId").setValue(json.relDeptDemandId);
                    mini.get("relDeptDemandId").setText(json.demandDesc);
                    mini.get("functionId").setValue(json.id);
                    mini.get("relDimensionKeys").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeys").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(json.structName);
                });
            $.ajaxSettings.async = true;
        }

        mini.get("interfaceRequestStructId").setEnabled(false);
        mini.get("relDimensionKeys").setEnabled(false);
        mini.get("relDeptDemandId").setEnabled(false);
        mini.get("functionDesc").setEnabled(false);
        mini.get("saveFunction").hide();
    }

    function editFunction(id) {
        functionWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getFunctionDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("functionDesc").setValue(json.functionDesc);
                    mini.get("relDeptDemandId").setValue(json.relDeptDemandId);
                    mini.get("relDeptDemandId").setText(json.demandDesc);
                    mini.get("functionId").setValue(json.id);
                    mini.get("relDimensionKeys").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeys").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(json.structName);
                    mini.get("riskLevel").setValue(json.riskLevel);
                    mini.get("riskLevel").setText(json.riskLevel);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("interfaceRequestStructId").setEnabled(true);
        mini.get("relDimensionKeys").setEnabled(true);
        mini.get("relDeptDemandId").setEnabled(true);
        mini.get("functionDesc").setEnabled(true);
        mini.get("saveFunction").show();
    }

    function closeFunction() {
        mini.get("functionDesc").setValue('');
        mini.get("functionId").setValue('');
        mini.get("relDimensionKeys").setValue('');
        mini.get("relDimensionKeys").setText('');
        mini.get("relDeptDemandId").setValue('');
        mini.get("relDeptDemandId").setText('');
        mini.get("interfaceRequestStructId").setValue('');
        mini.get("interfaceRequestStructId").setText('');
        functionWindow.hide();
    }

    function removeFunction() {
        var rows = functionListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/drbfm/single/deleteFunction.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            functionListGrid.reload();
                        }
                    }
                });
            }
        });
    }
    //功能分解选择部门需求
    function selectDemandClick() {
        selectDemandWindow.show();
        searchDemand();
    }

    function searchDemand() {
        var queryParam = [];
        //其他筛选条件
        var deptNames = $.trim(mini.get("deptNames").getValue());
        if (deptNames) {
            queryParam.push({name: "deptNames", value: deptNames});
        }
        var demandDesc = $.trim(mini.get("demandDesc").getValue());
        if (demandDesc) {
            queryParam.push({name: "demandDesc", value: demandDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectDemandListGrid.load(data);
    }

    function selectDemandOK() {
        var selectRow = selectDemandListGrid.getSelected();
        mini.get("relDeptDemandId").setValue(selectRow.id);
        mini.get("relDeptDemandId").setText(selectRow.demandDesc);
        selectDemandHide();
    }

    function selectDemandHide() {
        selectDemandWindow.hide();
        mini.get("demandDesc").setValue('');
        mini.get("deptNames").setValue('');
    }

    function onRelDemandCloseClick() {
        mini.get("relDeptDemandId").setValue('');
        mini.get("relDeptDemandId").setText('');
    }

    //功能分解选择部门需求
    function selectRequestStructClick() {
        if (!singleId){
            mini.alert("该部件没有关联有效的部件分析项目流程，不可填写！");
            return ;
        }
        mini.open({
            title: "部件选择",
            url: jsUseCtxPath + "/drbfm/single/drbfmAllSingleTree.do?singleId="+singleId,
            width: 1300,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if(returnData && returnData.interfaceRequestStructId && returnData.structName) {
                    mini.get("interfaceRequestStructId").setValue(returnData.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId").setText(returnData.structName);
                } else {
                    mini.alert("未选择部件！");
                }
            }
        });
    }

    function onRequestStructCloseClick() {
        mini.get("interfaceRequestStructId").setValue('');
        mini.get("interfaceRequestStructId").setText('');
    }

    //功能分解选择部门需求
    function selectRequestStructClick_R() {
        if (!singleId){
            mini.alert("该部件没有关联有效的部件分析项目流程，不可填写！");
            return ;
        }
        mini.open({
            title: "部件选择",
            url: jsUseCtxPath + "/drbfm/single/drbfmAllSingleTree.do?singleId="+singleId,
            width: 1300,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {

            },
            ondestroy: function (returnData) {
                if(returnData && returnData.interfaceRequestStructId && returnData.structName) {
                    mini.get("interfaceRequestStructId_R").setValue(returnData.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(returnData.structName);
                } else {
                    mini.alert("未选择部件！");
                }
            }
        });
    }

    function onRequestStructCloseClick_R() {
        mini.get("interfaceRequestStructId_R").setValue('');
        mini.get("interfaceRequestStructId_R").setText('');
    }

    //要求分解
    function addRequest() {
        if(!jsId){
            mini.alert('请先点击‘保存’进行表单的保存！');
            return ;
        }
        mini.get("relFunctionId").setEnabled(true);
        mini.get("relDimensionKeysR").setEnabled(true);
        mini.get("interfaceRequestStructId_R").setEnabled(true);
        mini.get("requestDesc").setEnabled(true);
        mini.get("requestChanges").setEnabled(true);
        mini.get("relEffect").setEnabled(true);
        mini.get("riskDesc").setEnabled(true);
        mini.get("compareToJP").setEnabled(true);
        mini.get("saveRequest").show();
        mini.get("riskLevel").setEnabled(true);
        requestWindow.show();
    }

    function validRequest() {
        var requestDesc = $.trim(mini.get("requestDesc").getValue());
       /* if (!requestDesc) {
            return {"result": false, "message": "请填写要求描述"};
        }*/
        if(requestDesc.length>500) {
            return {"result": false, "message": "要求描述500字以内"};
        }
        var requestChanges = $.trim(mini.get("requestChanges").getValue())
        /*if (!requestChanges) {
            return {"result": false, "message": "请填写较上一代或相似产品变化点"};
        }*/
        if(requestChanges.length>500) {
            return {"result": false, "message": "变化点500字以内"};
        }
        var relEffect = $.trim(mini.get("relEffect").getValue())
        /*if (!relEffect) {
            return {"result": false, "message": "请填写关联影响"};
        }*/
        if(relEffect.length>500) {
            return {"result": false, "message": "变化点带来的与该要求相关的客观影响500字以内"};
        }
        var riskDesc = $.trim(mini.get("riskDesc").getValue())
        /*if (!riskDesc) {
            return {"result": false, "message": "请填写存在风险"};
        }*/
        if(riskDesc.length>500) {
            return {"result": false, "message": "存在风险500字以内"};
        }
        var compareToJP = $.trim(mini.get("compareToJP").getValue())
        /*if (!compareToJP) {
            return {"result": false, "message": "请填写与竞品对比"};
        }*/
        if(compareToJP.length>500) {
            return {"result": false, "message": "与竞品对比500字以内"};
        }
        var riskLevel = $.trim(mini.get("riskLevel").getValue())
        // if (!riskLevel) {
        //     return {"result": false, "message": "请选择风险评级"};
        // }
        return {"result": true};
    }

    function saveRequest() {
        var formValid = validRequest();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var requestDesc = mini.get("requestDesc").getValue();
        var relDimensionKeys = mini.get("relDimensionKeysR").getValue();
        var relDimensionNames = mini.get("relDimensionKeysR").getText();
        var id = mini.get("requestId").getValue();
        var requestChanges = mini.get("requestChanges").getValue();
        var relEffect = mini.get("relEffect").getValue();
        var riskDesc = mini.get("riskDesc").getValue();
        var compareToJP = mini.get("compareToJP").getValue();
        var riskLevel = mini.get("riskLevel").getValue();
        if (!riskLevel){
            riskLevel = mini.get("riskLevel").getText();
        }
        var relFunctionId = mini.get("relFunctionId").getValue();
        var interfaceRequestStructId = mini.get("interfaceRequestStructId_R").getValue();
        var data = {id:id,requestDesc:requestDesc,relDimensionKeys:relDimensionKeys,
            relDimensionNames:relDimensionNames,belongJSId:jsId,requestChanges:requestChanges,
            requestChanges:requestChanges,relEffect:relEffect,riskDesc:riskDesc,compareToJP:compareToJP,
            riskLevel:riskLevel,relFunctionId:relFunctionId,interfaceRequestStructId:interfaceRequestStructId};
        var json = mini.encode(data);

        $.ajax({
            url: jsUseCtxPath + '/drbfm/single/saveRequest.do',
            type: 'POST',
            data: json,
            async: false,
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                           requestListGrid.reload();
                            mini.get("requestId").setValue(returnData.data);
                        }
                    });
                }
            }
        });
    }

    function detailRequest(id) {
        requestWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getRequestDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("relDimensionKeysR").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeysR").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId_R").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(json.structName);
                    mini.get("requestDesc").setValue(json.requestDesc);
                    mini.get("requestId").setValue(json.id);
                    mini.get("relFunctionId").setValue(json.relFunctionId);
                    mini.get("relFunctionId").setText(json.functionDesc);
                    mini.get("requestChanges").setValue(json.requestChanges);
                    mini.get("relEffect").setValue(json.relEffect);
                    mini.get("riskDesc").setValue(json.riskDesc);
                    mini.get("compareToJP").setValue(json.compareToJP);
                    mini.get("riskLevel").setValue(json.riskLevel);
                    mini.get("riskLevel").setText(json.riskLevel);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("requestDesc").setEnabled(false);
        mini.get("relDimensionKeysR").setEnabled(false);
        mini.get("interfaceRequestStructId_R").setEnabled(false);
        mini.get("relFunctionId").setEnabled(false);
        mini.get("requestChanges").setEnabled(false);
        mini.get("relEffect").setEnabled(false);
        mini.get("riskDesc").setEnabled(false);
        mini.get("compareToJP").setEnabled(false);
        mini.get("riskLevel").setEnabled(false);
        mini.get("saveRequest").hide();
    }

    function editRequest(id) {
        requestWindow.show();
        if (id) {
            var url = jsUseCtxPath + "/drbfm/single/getRequestDetail.do";
            $.ajaxSettings.async = false;
            $.post(
                url,
                {id: id},
                function (json) {
                    mini.get("relDimensionKeysR").setValue(json.relDimensionKeys);
                    mini.get("relDimensionKeysR").setText(json.relDimensionNames);
                    mini.get("interfaceRequestStructId_R").setValue(json.interfaceRequestStructId);
                    mini.get("interfaceRequestStructId_R").setText(json.structName);
                    mini.get("requestDesc").setValue(json.requestDesc);
                    mini.get("requestId").setValue(json.id);
                    mini.get("relFunctionId").setValue(json.relFunctionId);
                    mini.get("relFunctionId").setText(json.functionDesc);
                    mini.get("requestChanges").setValue(json.requestChanges);
                    mini.get("relEffect").setValue(json.relEffect);
                    mini.get("riskDesc").setValue(json.riskDesc);
                    mini.get("compareToJP").setValue(json.compareToJP);
                    mini.get("riskLevel").setValue(json.riskLevel);
                    mini.get("riskLevel").setText(json.riskLevel);
                });
            $.ajaxSettings.async = true;
        }
        mini.get("requestDesc").setEnabled(true);
        mini.get("relDimensionKeysR").setEnabled(true);
        mini.get("interfaceRequestStructId_R").setEnabled(true);
        mini.get("relFunctionId").setEnabled(true);
        mini.get("requestChanges").setEnabled(true);
        mini.get("relEffect").setEnabled(true);
        mini.get("riskDesc").setEnabled(true);
        mini.get("compareToJP").setEnabled(true);
        mini.get("riskLevel").setEnabled(true);
        mini.get("saveRequest").show();
    }

    function closeRequest() {
        mini.get("requestId").setValue('');
        mini.get("requestDesc").setValue('');
        mini.get("relDimensionKeysR").setValue('');
        mini.get("relDimensionKeysR").setText('');
        mini.get("interfaceRequestStructId_R").setValue('');
        mini.get("interfaceRequestStructId_R").setText('');
        mini.get("relFunctionId").setValue('');
        mini.get("relFunctionId").setText('');
        mini.get("requestChanges").setValue('');
        mini.get("relEffect").setValue('');
        mini.get("riskDesc").setValue('');
        mini.get("compareToJP").setValue('');
        mini.get("riskLevel").setValue('');
        mini.get("riskLevel").setText('');
        requestWindow.hide();
    }

    function removeRequest() {
        var rows = requestListGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/drbfm/single/deleteRequest.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            requestListGrid.reload();
                        }
                    }
                });
            }
        });
    }
    //要求分解选择功能
    function selectFunctionClick() {
        selectFunctionWindow.show();
        searchFunction();
    }

    function searchFunction() {
        var queryParam = [];
        //其他筛选条件
        var functionDesc = $.trim(mini.get("functionDesc2").getValue());
        if (functionDesc) {
            queryParam.push({name: "functionDesc", value: functionDesc});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectFunctionListGrid.load(data);
    }

    function selectFunctionOK() {
        var selectRow = selectFunctionListGrid.getSelected();
        mini.get("relFunctionId").setValue(selectRow.id);
        mini.get("relFunctionId").setText(selectRow.functionDesc);
        var relDimensionKeysR = mini.get("relDimensionKeysR").getValue();
        if(!relDimensionKeysR) {
            mini.get("relDimensionKeysR").setValue(selectRow.relDimensionKeys);
            mini.get("relDimensionKeysR").setText(selectRow.relDimensionNames);
        }
        mini.get("interfaceRequestStructId_R").setValue(selectRow.interfaceRequestStructId);
        mini.get("interfaceRequestStructId_R").setText(selectRow.structName);
        selectFunctionHide();
    }

    function selectFunctionHide() {
        selectFunctionWindow.hide();
        mini.get("functionDesc2").setValue('');
    }

    function onRelFunctionCloseClick() {
        mini.get("relFunctionId").setValue('');
        mini.get("relFunctionId").setText('');
    }

    //风险评级打分
    function selectInterfaceRiskLevel(){
        var id = mini.get("requestId").getValue();
        if (!id) {
            mini.alert('请先点击‘保存’进行表单的保存，再编辑风险评级（SOD）分数！');
            return;
        }
        mini.open({
            title:"风险评级打分",
            url:jsUseCtxPath+"/drbfm/single/selectRiskLevel.do?requestId="+id,
            width:600,
            height:450,
            showModal:true,
            allowResize:true,
            showCloseButton:true,
            onload:function(){

            },
            ondestroy:function (returnData){
                if(returnData && returnData.riskLevel ) {
                    mini.get("riskLevel").setText(returnData.riskLevel);
                    mini.get("riskLevel").setValue(returnData.riskLevel);
                }
            }

        });

    }
    function onRiskLevelCloseClick(){
        mini.get("riskLevel").setText('');
        mini.get("riskLevel").setValue('');
    }
</script>
</body>
</html>
