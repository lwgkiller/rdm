<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>挖掘机械研究院所长月度绩效表单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveDraft" class="mini-button" style="display: none" onclick="saveDraft()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formProject" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="INST_ID_" name="INST_ID_" class="mini-hidden"/>
            <input id="bkhUserId" name="bkhUserId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    挖掘机械研究院所长月度绩效
                </caption>
                <tr style="margin-left: 30px;">
                    <span style="color: red;font-size: 17px;">月度总分：<span id="scoreTotal" style="color: red;font-size: 17px;">--</span></span>
                </tr>
                <tr>
                    <td style="text-align: center;width: 15%">姓名：</td>
                    <td style="min-width:170px">
                        <input id="bkhUserName" name="bkhUserName" class="mini-textbox" style="width:100%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">岗位：</td>
                    <td style="min-width:170px">
                        <input id="post" name="post" class="mini-textbox" style="width:100%;" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 15%">考核期：</td>
                    <td style="min-width:170px">
                        <input id="yearMonth" name="yearMonth" class="mini-textbox" style="width:100%;" enabled="false"/>
                    </td>

                    <td id="projectTr" style="text-align: center;width: 15%">附加分：</td>
                    <td id="projectTr1" style="min-width:170px">
                        <input id="fjScore" name="fjScore" class="mini-textbox" style="width:100%" enabled="false"/>
                    </td>
                </tr>
            </table>
            <div id="matListGrid" class="mini-datagrid" allowResize="false" allowCellWrap="true"
                 idField="id" autoload="false" allowCellEdit="true" allowCellSelect="true"
                 multiSelect="true" showPager="false" showColumnsMenu="true" allowAlternating="true"
                 oncellbeginedit="OnCellBeginEdit"
            >
                <div property="columns">
                    <div type="checkcolumn"width="20"></div>
                    <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                    <div field="id" name="id" visible="false"></div>
                    <div field="metricDesc" name="metricDesc" headerAlign="center" align="center" width="70" >考核指标
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="targetValue" name="targetValue" headerAlign="center" align="center" width="20" >目标值
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="targetUpperValue" name="targetUpperValue" headerAlign="center" align="center" width="30" renderer="onTargetLimitRenderer">目标上下限
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="weight" name="weight" headerAlign="center" align="center" width="20" renderer="weightRenderer">权重
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="computeDesc" name="computeDesc" headerAlign="center" align="center" width="200" >计算方法及考核规则
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="period" name="period" headerAlign="center" align="center" width="30" >考核周期
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="zkUserName" name="zkUserName" headerAlign="center" align="center" width="30" >执考人
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="score" name="score" headerAlign="center" align="center" width="40" >指标得分<span style="color: red">*</span>
                        <input property="editor" class="mini-textbox" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="jqScore" name="jqScore" headerAlign="center" align="center" width="40" renderer="weightScoreRenderer">加权得分
                        <input property="editor" class="mini-textbox" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="scoreDesc" name="scoreDesc" headerAlign="center" align="center" width="40" >扣分依据<span style="color: red">*</span>
                        <input property="editor" class="mini-textbox" style="width: 100%;height: 50px"/>
                    </div>
                    <div field="completeStatus" name="completeStatus" headerAlign="center" align="center" width="30" >完成情况<span style="color: red">*</span>
                        <input property="editor" class="mini-combobox" style="width: 100%;height: 50px"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[
                                   {'key' : '已完成','value' : '已完成'},
                                   {'key' : '未完成','value' : '未完成'}
							   ]"
                        />
                    </div>
                    <div field="description" name="description" headerAlign="center" align="center" width="100" >情况说明<span style="color: red">*</span>
                        <input property="editor" class="mini-textarea" style="width: 100%;height: 50px"/>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var matListGrid = mini.get("matListGrid");
    var formProject = new mini.Form("#formProject");
    var coverContent = "<br/>徐州徐工挖掘机械有限公司";
    var id = "${id}";
    var nodeVarsStr='${nodeVars}';
    var action = "${action}";
    var stageName = "";

    $(function () {
        initProjectForm();
        if (action == 'detail'){
            $("#detailToolBar").show();
        }
        else if (action == 'edit'){
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#saveDraft").show();
            }
        }
        else if (action == 'task') {
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
            if (stageName == "fujia") {
                $("#projectTr").attr("style","text-align: center;width: 15%")
                $("#projectTr1").attr("style","min-width:170px")
                mini.get("fjScore").setEnabled(true);
            }
        }
    });

    function OnCellBeginEdit(e){
        e.cancel = true;
        if (action == 'edit') {
            if (e.field == "completeStatus" || e.field == "description" || e.field == "score" || e.field == "scoreDesc" || e.field == "score" || e.field == "scoreDesc") {
                e.cancel = false;
            }
        }
    };

    function weightRenderer(e) {
        var record=e.record;
        return record.weight+'%';
    }

    //目标上限和下限都优先展示公式，最后拼接成"下限"~"上限"的形式
    function onTargetLimitRenderer(e) {
        var record=e.record;
        var lower='';
        if(record.targetLowerValue) {
            lower=record.targetLowerValue;
        }
        var upper='';
        if(record.targetUpperValue) {
            upper=record.targetUpperValue;
        }
        return lower+'~'+upper;
    }

    //初始化表单数据（同步）
    function initProjectForm() {
        $.ajaxSettings.async = false;
        if (id) {
            var url = jsUseCtxPath + "/kpiLeader/core/getJsonReport.do?id="+id;
            $.get(url,function(data){
                formProject.setData(data.baseInfo);
                var deList = data.detail;
                if (deList != null && deList.length > 0) {
                    matListGrid.setData(deList);
                }
                if (data.score) {
                    $("#scoreTotal").html(data.score);
                } else {
                    $("#scoreTotal").html("--");
                }
            });
        }
        $.ajaxSettings.async = true;
    }

    //流程引擎调用此方法进行表单数据的获取
    function getData() {
        var formData = _GetFormJsonMini("formProject");
        formData.bos = [];
        formData.vars = [];
        return formData;
    }

    function isNumber(val){
        return /^[-]?[\.\d]+$/.test(val);
    }

    function weightScoreRenderer(e) {
        var record=e.record;
        if(record.score) {
            var scoreValue = parseFloat(record.score);
            // if (record.score > record.targetUpperValue){
            //     scoreValue=parseFloat(record.targetUpperValue);
            // }
            // if (record.score < record.targetLowerValue){
            //     scoreValue=parseFloat(0);
            // }
            // if ((record.score <= record.targetUpperValue) && (record.score >= record.targetLowerValue)){
            //     scoreValue=parseFloat(record.score);
            //
            // }
            var weight=parseFloat(record.weight);
            scoreValue = scoreValue*weight/100;
            return scoreValue.toFixed(2);
        }
        return "";
    }



    //..流程信息浏览
    function processInfo() {
        var instId = $("#INST_ID_").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }

    function saveDraft() {
        var data = matListGrid.getChanges();
        var message = "保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/kpiLeader/core/saveDetailList.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (text) {
                        if (text && text.message) {
                            message = text.message;
                        }
                    }
                });
            }
        }
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    // matListGrid.reload();
                    initProjectForm();
                }
            }
        });
    }
</script>
</body>
</html>
