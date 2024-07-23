<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>组件表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
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
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="secretForm" method="post" style="height: 95%;width: 100%">
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="secretId" name="secretId" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">编号(提交后自动生成)：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="numinfo"  name="numinfo"  class="mini-textbox"  readonly style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="projectName"  name="projectName"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">合作相关方：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="partner"  name="partner"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目合作开始日期：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="hzdates" name="hzdates"
                               showTime="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目合作结束日期：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="hzdatee" name="hzdatee"
                               showTime="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">保密期限：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input id="bmdate"  name="bmdate"  class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">签订日期：<span style="color: #ff0000">*</span></td>
                    <td>
                        <input style="width:98%;" class="mini-datepicker" id="qddate" name="qddate"
                               showTime="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">经办人：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="apply" name="applyId" textname="applyName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">部门负责人：<span style="color: #ff0000">*</span></td>
                    <td style="min-width:170px">
                        <input id="res" name="resId" textname="resName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td>
                        <input style="width:98%;" class="mini-textbox" id="note" name="note"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">纸质已归档：</td>
                    <td>
                        <input class="mini-checkbox" name="onfile" id="onfile"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath="${ctxPath}";
    var secretForm = new mini.Form("#secretForm");
    var secretId="${secretId}";
    var action="${action}";
    var status="${status}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var resId="${resId}";
    var resName="${resName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    var first="";
    var second="";
    $(function () {
        mini.get("onfile").setEnabled(false);
        if(secretId) {
            var url = jsUseCtxPath + "/rdm/core/secret/getSecret.do";
            $.post(
                url,
                {secretId: secretId},
                function (json) {
                    secretForm.setData(json);
                });
        }else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
            mini.get("res").setValue(resId);
            mini.get("res").setText(resName);
        }
        //变更入口
        if(action=='task') {
            taskActionProcess();
        }else if(action == 'detail'){
            secretForm.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    
    function getData() {
        var formData = _GetFormJsonMini("secretForm");
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }
    function validFirst() {
        var hzdates=$.trim(mini.get("hzdates").getValue())
        if(!hzdates) {
            return {"result": false, "message": "请填写项目合作开始日期"};
        }
        var hzdatee=$.trim(mini.get("hzdatee").getValue())
        if(!hzdatee) {
            return {"result": false, "message": "请填写项目合作结束日期"};
        }
        var qddate=$.trim(mini.get("qddate").getValue())
        if(!qddate) {
            return {"result": false, "message": "请填写签订日期"};
        }
        return {"result": true};
    }
    function saveSecret(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.saveDraft(e);
    }

    function validSecret() {
        var projectName=$.trim(mini.get("projectName").getValue())
        if(!projectName) {
            return {"result": false, "message": "请填写项目"};
        }
        var partner=$.trim(mini.get("partner").getValue())
        if(!partner) {
            return {"result": false, "message": "请填写合作相关方"};
        }
        var hzdates=$.trim(mini.get("hzdates").getValue())
        if(!hzdates) {
            return {"result": false, "message": "请填写项目合作开始日期"};
        }
        var hzdatee=$.trim(mini.get("hzdatee").getValue())
        if(!hzdatee) {
            return {"result": false, "message": "请填写项目合作结束日期"};
        }
        var bmdate=$.trim(mini.get("bmdate").getValue());
        if(!bmdate) {
            return {"result": false, "message": "请填写保密期限"};
        }
        var qddate=$.trim(mini.get("qddate").getValue())
        if(!qddate) {
            return {"result": false, "message": "请填写签订日期"};
        }
        var apply=$.trim(mini.get("apply").getValue())
        if(!apply) {
            return {"result": false, "message": "请填写经办人"};
        }
        var res=$.trim(mini.get("res").getValue())
        if(!res) {
            return {"result": false, "message": "请填写部门负责人"};
        }
        return {"result": true};
    }
    function startSecretProcess(e) {
        var formValid = validSecret();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }
    function secretApprove() {
        if (first == 'yes'||second =='yes') {
            var formValid = validSecret();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }
        window.parent.approve();
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
            if (nodeVars[i].KEY_ == 'first') {
                first = nodeVars[i].DEF_VAL_;
            }
            if (nodeVars[i].KEY_ == 'second') {
                second = nodeVars[i].DEF_VAL_;
            }
        }
        secretForm.setEnabled(false);
        if(first== 'yes') {
            secretForm.setEnabled(true);
            mini.get("onfile").setEnabled(false);
        }
        if(second== 'yes') {
            mini.get("qddate").setEnabled(true);
            mini.get("projectName").setEnabled(true);
            mini.get("partner").setEnabled(true);
            mini.get("hzdates").setEnabled(true);
            mini.get("hzdatee").setEnabled(true);
            mini.get("onfile").setEnabled(true);
        }
    }





    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
</script>
</body>
</html>
