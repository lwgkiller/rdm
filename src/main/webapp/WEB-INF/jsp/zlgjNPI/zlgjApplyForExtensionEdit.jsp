<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>延期申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%">
        <form id="formBusiness" method="post">
            <input id="businessId" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    延期申请
                </caption>
                <tr>
                    <td style="width: 5%">延期节点：</td>
                    <td>
                        <input id="extensionNode" name="extensionNode" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '产品主管/室主任分配','value' : '产品主管/室主任分配'},
                               {'key' : '责任部门审核分析','value' : '责任部门审核分析'}]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">改进流程：</td>
                    <td>
                        <input id="wtId" name="wtId" textname="zjbh" style="width:98%;" property="editor"
                               class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick()" allowInput="false"
                               onbuttonclick="selectStandard()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">机型：</td>
                    <td style="width: 30%;">
                        <input id="smallJiXing" name="smallJiXing" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">问题详细描述：</td>
                    <td style="width: 30%;">
                        <input id="wtms" name="wtms" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">延期天数：</td>
                    <td style="width: 30%">
                        <input id="extensionDays" name="extensionDays" class="mini-spinner" style="width:98%;" minValue="0"
                               maxValue="100"/>
                    </td>
                </tr>
                <tr>
                    <td>延期原因：</td>
                    <td>
                        <input id="extensionReason" name="extensionReason" class="mini-textarea" style="width:98%;height: 200px"/>
                    </td>
                </tr>

            </table>
        </form>
    </div>
</div>
<%--改进--%>
<div id="selectZlWindow" title="选择改进业务" class="mini-window" style="width:1080px;height:600px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">流程编号: </span>
        <input class="mini-textbox" width="130" id="zlgjNumber" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">机型: </span>
        <input class="mini-textbox" width="130" id="serchSmallJiXing" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">第一责任人: </span>
        <input id="zrcpzgId" name="zrcpzgId" textname="zrcpzgName" class="mini-user rxc"
               plugins="mini-user" style="width:130px;height:34px;" allowinput="false" label="" length="50" maxlength="50"
               mainfield="no" single="true"/>
        <span style="font-size: 14px;color: #777">审核责任人: </span>
        <input id="zrrId" name="zrrId" textname="zrrName" class="mini-user rxc"
               plugins="mini-user" style="width:130px;height:34px;" allowinput="false" label="" length="50" maxlength="50"
               mainfield="no" single="true"/>
        <span style="font-size: 14px;color: #777">整机编号: </span>
        <input class="mini-textbox" width="130" id="zjbh" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()">查询</a>
        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="wtId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/xjsdr/core/zlgj/getZlgjList.do">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="zlgjNumber" width="50" headerAlign="center" align="center" allowSort="false">流程编号</div>
                <div field="smallJiXing" width="50" headerAlign="center" align="center" allowSort="false">机型</div>
                <div field="zrrId" width="50" headerAlign="center" align="center" allowSort="false" visible="false">审核责任人id</div>
                <div field="zrcpzgId" width="50" headerAlign="center" align="center" allowSort="false" visible="false">第一责任人id</div>
                <div field="zrcpzgName" width="50" headerAlign="center" align="center" allowSort="false">第一责任人</div>
                <div field="zrrName" width="50" headerAlign="center" align="center" allowSort="false">审核责任人</div>
                <div field="zjbh" sortField="zjbh" width="100" headerAlign="center" align="center" allowSort="true">整机编号</div>
                <div field="wtms" sortField="wtms" width="100" headerAlign="center" align="center" allowSort="true">问题描述</div>
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

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var businessId = "${businessId}";
    var formBusiness = new mini.Form("#formBusiness");
    var zlListGrid = mini.get("zlListGrid");
    var selectZlWindow = mini.get("selectZlWindow");
    var first = "";
    //..
    $(function () {
        if (businessId) {
            var url = jsUseCtxPath + "/zlgj/applyForExtension/getApplyDetail.do";
            $.post(
                url,
                {businessId: businessId},
                function (json) {
                    formBusiness.setData(json);
                });
        }
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formBusiness.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });
    //..
    function getData() {
        var formData = _GetFormJsonMini("formBusiness");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        formData.bos = [];
        return formData;
    }
    //..
    function onSelectStandardCloseClick(e) {
        mini.get("wtId").setValue('');
        mini.get("wtId").setText('');
        mini.get("smallJiXing").setValue('');
        mini.get("wtms").setValue('');
    }
    //..
    function selectStandard() {
        var extensionNode = mini.get("extensionNode").getValue();
        if (!extensionNode) {
            mini.alert("请选择超期节点类型！");
            return;
        }
        if (extensionNode == "产品主管/室主任分配") {
            mini.get("zrcpzgId").setValue(currentUserId);
            mini.get("zrcpzgId").setText(currentUserName);
        }
        else if (extensionNode == "责任部门审核分析") {
            mini.get("zrrId").setValue(currentUserId);
            mini.get("zrrId").setText(currentUserName);
        }
        selectZlWindow.show();
        searchStandard();
    }
    //..
    function searchStandard() {
        var queryParam = [];
        //其他筛选条件
        var zjbh = $.trim(mini.get("zjbh").getValue());
        if (zjbh) {
            queryParam.push({name: "zjbh", value: zjbh});
        }
        var zlgjNumber = $.trim(mini.get("zlgjNumber").getValue());
        if (zlgjNumber) {
            queryParam.push({name: "zlgjNumber", value: zlgjNumber});
        }
        var zrcpzgId = $.trim(mini.get("zrcpzgId").getValue());
        if (zrcpzgId) {
            queryParam.push({name: "zrcpzgId", value: zrcpzgId});
        }
        var zrrId = $.trim(mini.get("zrrId").getValue());
        if (zrrId) {
            queryParam.push({name: "zrrId", value: zrrId});
        }
        var serchSmallJiXing = $.trim(mini.get("serchSmallJiXing").getValue());
        if (serchSmallJiXing) {
            queryParam.push({name: "smallJiXing", value: serchSmallJiXing});
        }
        queryParam.push({name: "instStatus", value: "RUNNING"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }
    //..
    function clearForm() {
        mini.get("zjbh").setValue("");
        mini.get("zlgjNumber").setValue("");
        mini.get("zrcpzgId").setValue("");
        mini.get("zrcpzgId").setText("");
        mini.get("zrrId").setValue("");
        mini.get("zrrId").setText("");
        mini.get("serchSmallJiXing").setValue("");
        searchStandard();
    }
    //..
    function onRowDblClick() {
        selectStandardOK();
    }
    //..
    function selectStandardOK() {
        var extensionNode = mini.get("extensionNode").getValue();
        var selectRow = zlListGrid.getSelected();
        if ((currentUserId == selectRow.zrcpzgId && extensionNode == "产品主管/室主任分配") ||
            (currentUserId == selectRow.zrrId && extensionNode == "责任部门审核分析") ||
            currentUserId == '1') {
            mini.get("wtId").setValue(selectRow.wtId);
            mini.get("wtId").setText(selectRow.zjbh);
            mini.get("smallJiXing").setValue(selectRow.smallJiXing);
            mini.get("wtms").setValue(selectRow.wtms);
            selectStandardHide();
        } else {
            mini.alert("请选择超期节点处理人为当前用户的流程");
            return;
        }
    }
    //..
    function selectStandardHide() {
        selectZlWindow.hide();
        mini.get("zjbh").setValue('');
    }
    //..
    function saveApply(e) {
        var formValid = validApplyFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }
    //..
    function validApplyFirst() {
        var wtId = $.trim(mini.get("wtId").getText())
        if (!wtId) {
            return {"result": false, "message": "请选择问题改进流程"};
        }
        var extensionReason = $.trim(mini.get("extensionReason").getValue())
        if (!extensionReason) {
            return {"result": false, "message": "请填写延期原因"};
        }
        var extensionDays = $.trim(mini.get("extensionDays").getValue())
        if (!extensionDays || extensionDays == '0') {
            return {"result": false, "message": "请填写延期天数"};
        }
        return {"result": true};
    }
    //..
    function startApplyProcess(e) {
        var formValid = validApplyFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    //..
    function applyApprove() {
        //编制阶段的下一步需要校验表单必填字段
        var formValid = validApplyFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        //检查通过
        window.parent.approve();
    }
    //..
    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }
    //..
    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
        }
        if (first != 'yes') {
            formBusiness.setEnabled(false);
        }
    }
</script>
</body>
</html>
