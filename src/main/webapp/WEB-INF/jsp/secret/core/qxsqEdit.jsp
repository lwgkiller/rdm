<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>权限申请表单</title>
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
        <form id="txxyForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREAT_BY_" name="CREAT_BY_"/>
            <input class="mini-hidden" id="departName" name="departName"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">权限申请表单</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">子系统名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="subSysId" name="subSysId" textname="subSysName" property="editor"
                               class="mini-combobox" style="width:98%" multiSelect="false"
                               url="${ctxPath}/secret/core/qxsq/querySubsystemList.do"
                               valueField="KEY_" textField="NAME_"/>
                    </td>
                    <td style="text-align: center;width: 20%">菜单名称：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="menuName" name="menuName" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>

                </tr>

                <tr>

                    <td style="text-align: center;width: 20%">使用环境：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <%--<input id="networkEnv" name="networkEnv" class="mini-textbox" style="width:98%;"--%>
                               <%--enabled="true"/>--%>
                        <input property="editor" id="networkEnv" name="networkEnv" class="mini-combobox"
                               style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               multiSelect="true"
                               nullItemText="请选择..."
                               data="[{'key' : '管理网','value' : '管理网'}
                                       ,{'key' : '技术网','value' : '技术网'}
                                  ]"
                        />


                    </td>

                    <td style="text-align: center;width: 20%">申请人：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                </tr>
                <%--<tr>--%>

                    <%--<td style="text-align: center;width: 20%">申请人部门：<span style="color:red">*</span></td>--%>
                    <%--<td style="min-width:170px">--%>
                        <%--<input id="departName" name="departName" class="mini-textbox" style="width:98%;"--%>
                               <%--enabled="false"/>--%>
                    <%--</td>--%>
                <%--</tr>--%>
                <tr>
                    <td style="text-align: center">申请原因：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="applyReason" name="applyReason" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="申请原因" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入申请原因..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
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

    var txxyForm = new mini.Form("#txxyForm");

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
        var url = jsUseCtxPath + "/secret/core/qxsq/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                txxyForm.setData(json);
            });
        if (action == 'detail') {
            txxyForm.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("txxyForm");
        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }
        return formData;
    }

    //保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validQxsq();
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
            var formValid = validQxsq();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }


    function validQxsq() {
        var  subSysId= $.trim(mini.get("subSysId").getValue());
        if (!subSysId) {
            return {"result": false, "message": "请选择子系统"};
        }
        var menuName = $.trim(mini.get("menuName").getValue());
        if (!menuName) {
            return {"result": false, "message": "请填写菜单名称"};
        }

        var networkEnv = $.trim(mini.get("networkEnv").getValue());
        if (!networkEnv) {
            return {"result": false, "message": "请选择使用环境"};
        }

        var applyReason = $.trim(mini.get("applyReason").getValue());
        if (!applyReason) {
            return {"result": false, "message": "请填写申请原因"};
        }
        return {"result": true};
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
            txxyForm.setEnabled(false);
        }

    }




</script>
</body>
</html>
