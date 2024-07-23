<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>技术文件制作申请</title>
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
        <form id="jswjsqForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">技术文件制作申请</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">零部件类型：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="partsType" name="partsType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..." onvaluechanged="setApply()"
                               data="[{'key' : '发动机','value' : '发动机'}
                                       ,{'key' : '主泵','value' : '主泵'}
                                       ,{'key' : '液压马达','value' : '液压马达'}
                                       ,{'key' : '油缸','value' : '油缸'}
                                       ,{'key' : '阀','value' : '阀'}
                                       <%--,{'key' : '电气件','value' : '电气件'}--%>
                                       ]"
                        />
                    </td>


                    <td style="text-align: center;width: 20%">零部件型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input id="partsModel" name="partsModel" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>


                    <td style="text-align: center;width: 20%">审核人：</td>
                    <td>
                        <input id="checker" name="checkerId" textname="checkerName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                               enabled="false"
                        />
                    </td>

                </tr>

                <tr>
                    <td style="text-align: center">申请原因：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="applyReason" name="applyReason" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="申请原因" datatype="varchar" length="200" vtype="length:200" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入申请原因..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">备注：</td>
                    <td colspan="3">
                        <input id="remark" name="remark" class="mini-textbox" style="width:98%;"
                               enabled="true"/>
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

    var jswjsqForm = new mini.Form("#jswjsqForm");

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

    function setApply() {
        var partsType = mini.get("partsType").getValue();
        if (!partsType) {
            mini.get("checker").setValue('');
            mini.get("checker").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/serviceEngineering/core/jswjsq/getUserInfoByPartsType.do?partsType=' + partsType,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("checker").setValue(data.resId);
                mini.get("checker").setText(data.resName);
            }
        });
    }

    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/serviceEngineering/core/jswjsq/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                jswjsqForm.setData(json);
            });
        if (action == 'detail') {
            jswjsqForm.setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("jswjsqForm");

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
        var formValid = validJswjsq();
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
            var formValid = validJswjsq();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }


    function validJswjsq() {
        var partsType = $.trim(mini.get("partsType").getValue());
        if (!partsType) {
            return {"result": false, "message": "请选择零部件类型"};
        }
        var partsModel = $.trim(mini.get("partsModel").getValue());
        if (!partsModel) {
            return {"result": false, "message": "请填写零部件型号"};
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
            jswjsqForm.setEnabled(false);
        }

    }


</script>
</body>
</html>
