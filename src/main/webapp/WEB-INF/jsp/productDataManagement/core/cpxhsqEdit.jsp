<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>产品设计型号申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="cpxhsqForm" method="post产品设计文件夹存放位置" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREATE_BY_" name="CREATE_BY_"/>
            <input class="mini-hidden" id="creatorName" name="creatorName"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">产品设计型号申请</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">产品名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="productName" name="productName" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请部门：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="departName" name="departName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">产品设计型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="designModel" name="designModel" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">排放阶段：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="dischargeStage" name="dischargeStage" class="mini-combobox" style="width:98%;"
                               textField="text" valueField="key_" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false"
                               multiSelect="true"
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=PFBZ"/>
                    </td>
                </tr>


                <tr>
                    <td style="text-align: center;width: 20%">主参数代号及单位：<span style="color:red">*</span></td>
                    <%--<td colspan="3">--%>
                    <td style="min-width:170px">
                        <input id="mainParamDesc" name="mainParamDesc" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">产品主管：<span style="color:red">*</span>
                    </td>
                    <td style="min-width:170px">
                        <input id="productManager" name="productManagerId" textname="productManagerName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center">产品说明：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="productNotes" name="productNotes" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="产品说明" datatype="varchar" length="2000" vtype="length:2000" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入产品说明..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>



                <tr>
                    <td style="text-align: center;width: 20%">发动机：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="motor" name="motor" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%">主泵：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="mainPump" name="mainPump" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">主阀：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="mainValve" name="mainValve" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.productSpectrumEdit.ghnwxs"/>：<span style="color:red">*</span>
                    </td>
                    <td style="min-width:170px">
                        <input id="abroad" name="abroad" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                               multiSelect="true"
                               data="[{'key' : '国内','value' : '国内'}
                                       ,{'key' : '出口','value' : '出口'}
                                       ]"
                        />
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%"><spring:message
                            code="page.productSpectrumEdit.ghxsqy"/>：<span style="color:red">*</span>
                    </td>
                    <td colspan="3">
                        <input id="region" name="region" class="mini-combobox" style="width:98%"
                               multiSelect="true"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPGHXSQYGJ"
                               valueField="name" textField="name"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">产品设计文件夹存放位置：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="location" name="location" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 20%">技术规格表模板：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <input id="jsggType" name="jsggType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="请选择模板类型..."
                               required="false" allowInput="false"
                               <%--showNullItem="true"--%>
                               <%--nullItemText="请选择..."--%>
                               multiSelect="false"
                               data="[{'key' : '中大挖','value' : '中大挖'}
                                       ,{'key' : '微小挖','value' : '微小挖'}
                                       ,{'key' : '轮挖','value' : '轮挖'}
                                       ]"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">物料大类：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="wlNumber" name="wlNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">PIN第8位特征代码：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="pin8" name="pin8" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>


            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';

    var cpxhsqForm = new mini.Form("#cpxhsqForm");

    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/pdm/core/cpxhsq/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                cpxhsqForm.setData(json);
            });
        if (action == 'detail') {
            cpxhsqForm.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("cpxhsqForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        formData.designModel = $.trim(formData.designModel);
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        var productName = $.trim(mini.get("productName").getValue());
        if (!productName) {
            mini.alert("请填写产品名称!");
            return;
        }
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validCpxhsq();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validCpxhsq();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        if (stageName == 'standardEdit') {
            var pin8 = $.trim(mini.get("pin8").getValue());
            if (!pin8) {
                mini.alert("请填写pin码！");
                return;
            }
            var wlNumber = $.trim(mini.get("wlNumber").getValue());
            if (!wlNumber) {
                mini.alert("请填写物料大类！");
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function hasRomanCharacters(str) {
        //这里加了两个空格 一个中文一个英文=
        var romanRegex = /[ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫ  ]/i;
        return romanRegex.test(str);
    }

    function validCpxhsq() {
        //所有字段都必填
        var productName = $.trim(mini.get("productName").getValue());
        if (!productName) {
            return {"result": false, "message": "请填写产品名称"};
        }
        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写产品设计型号"};
        }
        //这里要验证罗马字符
        if (hasRomanCharacters(designModel)) {
            return {"result": false, "message": "设计型号中不能包含罗马字符及空格"};
        }
        var dischargeStage = $.trim(mini.get("dischargeStage").getValue());
        if (!dischargeStage) {
            return {"result": false, "message": "请选择排放阶段"};
        }

        var mainParamDesc = $.trim(mini.get("mainParamDesc").getValue());
        if (!mainParamDesc) {
            return {"result": false, "message": "请填写主参数代号及单位"};
        }
        var productManager = $.trim(mini.get("productManager").getValue());
        if (!productManager) {
            return {"result": false, "message": "请选择产品主管！"};
        }
        var productNotes = $.trim(mini.get("productNotes").getValue());
        if (!productNotes) {
            return {"result": false, "message": "请填写产品说明"};
        }
        var motor = $.trim(mini.get("motor").getValue());
        if (!motor) {
            return {"result": false, "message": "请填写发动机信息"};
        }
        var mainPump = $.trim(mini.get("mainPump").getValue());
        if (!mainPump) {
            return {"result": false, "message": "请填写主泵信息"};
        }
        var mainValve = $.trim(mini.get("mainValve").getValue());
        if (!mainValve) {
            return {"result": false, "message": "请填写主阀信息"};
        }
        var abroad = $.trim(mini.get("abroad").getValue());
        if (!abroad) {
            return {"result": false, "message": "请填写规划内外销售区域"};
        }
        var region = $.trim(mini.get("region").getValue());
        if (!region) {
            return {"result": false, "message": "请选择规划销售区域"};
        }
        var jsggType = $.trim(mini.get("jsggType").getValue());
        if (!jsggType) {
            return {"result": false, "message": "技术规格表模板"};
        }
        var location = $.trim(mini.get("location").getValue());
        if (!location) {
            return {"result": false, "message": "请填写产品设计文件夹存放位置"};
        }
        //验证产品型号申请中是否有除自身之外的running或success_end的数据，如果没有再检查型谱表中是否有这个产品设计型号
        var formId = mini.get("id").getValue();
        if (!formId) {
            formId = '';
        }
        var result = {"result": true};
        $.ajax({
            url: jsUseCtxPath + '/pdm/core/cpxhsq/checkDesignModelValid.do?designModel='+ designModel+"&id="+formId,
            async: false,
            success: function (returnData) {
                if (!returnData.success) {
                    result = {"result": false, "message": returnData.message};
                }
            }
        });
        return result;
    }

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


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            cpxhsqForm.setEnabled(false);
        }
        if (stageName == 'standardEdit') {
            mini.get("pin8").setEnabled(true);
            mini.get("wlNumber").setEnabled(true);
        }

    }


</script>
</body>
</html>
