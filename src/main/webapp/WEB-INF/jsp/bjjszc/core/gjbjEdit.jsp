<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>关键备件清单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    4
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 98%">
        <form id="formGjbj" method="post">
            <input id="gjbjId" name="gjbjId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    关键备件清单
                </caption>
                <tr>
                    <td style="width: 15%">提交人：</td>
                    <td>
                        <input id="apply" name="creatorName" textname="creatorName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                               enabled="false"
                        />
                    </td>

                    <td style="width: 15%">维护人：<span style="color:red">*</span></td>
                    <td>
                        <input id="wh" name="whId" textname="whName"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="true"
                        />
                    </td>
                </tr>
                <tr>
                    <td style="width:15%;height: 800px">详细信息表：</td>
                    <td colspan="5">
                        <div style="margin-bottom: 2px">
                            <a id="addGjbjDetail" class="mini-button" onclick="addGjbjDetail()">添加</a>
                            <a id="removeGjbjDetail" class="mini-button" onclick="removeGjbjDetail()">删除</a>
                            <a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
                            <a class="mini-button" id="exportGjbjDetail" style="margin-right: 5px" plain="true"
                               onclick="exportGjbjDetail()">导出</a>
                            <span style="color: red">注：添加前请先进行表单的保存、添加或删除后请点击保存以生效</span>
                        </div>
                        <div id="detailListGrid" class="mini-datagrid" style="width: 1700px; height: 85%"
                             allowResize="false" allowCellWrap="false"
                             idField="id" url="${ctxPath}/rdm/core/Gjbj/getDetailList.do?belongId=${gjbjId}"
                             autoload="true"
                        <%--virtualColumns="true"--%>
                        <%--virtualScroll="true"--%>
                             allowHeaderWrap = "true"
                             oncellbeginedit="OnCellBeginEditDetail"
                             showCellTip="true"
                             allowCellEdit="true" allowCellSelect="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div field="partsType" width="40" headerAlign="center" align="center">类别
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."
                                           data="[
                                                    {'key' : '保养件','value' : '保养件'},{'key' : '易损件','value' : '易损件'}
                                                   ,{'key' : '维修件','value' : '维修件'}]"
                                    /></div>
                                <div field="desginModel" width="60" headerAlign="center" align="center">设计机型
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="vinCode" width="60" headerAlign="center" align="center">机型物料编码
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="productDepartName" width="60" headerAlign="center" align="center">产品所
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="holeCode" width="60" headerAlign="center" align="center">总成物料编码
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="holeDesc" width="60" headerAlign="center" align="center">总成物料描述
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="materialCode" width="60" headerAlign="center" align="center">物料编码
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="materialDesc" width="60" headerAlign="center" align="center">物料描述
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="supplier" width="40" headerAlign="center" align="center">供应商
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="partsNum" width="40" headerAlign="center" align="center">装配数量
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="remark" width="60" headerAlign="center" align="center">备注
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="devCode" width="60" headerAlign="center" align="center">开发件物料编码
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="devDesc" width="60" headerAlign="center" align="center">开发件物料描述
                                    <input property="editor" class="mini-textbox"/></div>
                                <div field="planActTime" width="60" headerAlign="center" align="center">备件开发计划时间
                                    <input property="editor" class="mini-textbox"/></div>
                                <%--<input property="editor" class="mini-datepicker" dateFormat="yyyy-MM-dd"--%>
                                <%--showTime="false" showOkButton="true" showClearButton="false"/>--%>
                                <%--</div>--%>
                                <div field="needExp" width="40" headerAlign="center" align="center">是否有试验验证需求
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="value" valueField="key"
                                           required="false" allowInput="false" showNullItem="false"
                                           data="[
                                                    {'key' : '是','value' : '是'},
                                                   {'key' : '否','value' : '否'}]"
                                    /></div>

                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="importWindow" title="关键备件导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportOil()">关键备件导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/rdm/core/Gjbj/exportGjbjDetail.do?belongId=${gjbjId}" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>

<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var currentUserName = "${currentUserName}";
    var detailListGrid = mini.get("detailListGrid");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var formGjbj = new mini.Form("#formGjbj");
    var gjbjId = "${gjbjId}";
    var currentUserMainDepId = "${currentUserMainDepId}";


    var stageName = "";
    $(function () {
        if (gjbjId) {
            var url = jsUseCtxPath + "/rdm/core/Gjbj/getGjbjDetail.do";
            $.post(
                url,
                {gjbjId: gjbjId},
                function (json) {
                    formGjbj.setData(json);
                });
        } else {
            mini.get("apply").setValue(currentUserId);
            mini.get("apply").setText(currentUserName);
        }
        formGjbj.setEnabled(false);
        // mini.get("exportGjbjDetail").setEnabled(false);
        mini.get("addGjbjDetail").setEnabled(true);
        mini.get("removeGjbjDetail").setEnabled(true);
        mini.get("importId").setEnabled(true);
        mini.get("apply").setEnabled(false);
        //变更入口
        if (action == 'task') {
            taskActionProcess();
        } else if (action == "detail") {
            formGjbj.setEnabled(false);
            detailListGrid.setAllowCellEdit(false);
            mini.get("addGjbjDetail").setEnabled(false);
            mini.get("removeGjbjDetail").setEnabled(false);
            mini.get("importId").setEnabled(false);
            $("#detailToolBar").show();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        }
    });

    function saveGjbjInProcess() {
        // var formValid = validJsmm();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formGjbj");
        formData.gjbj = detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Gjbj/saveGjbj.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        window.location.reload();
                    } else {
                        mini.alert("数据保存失败" + data.message);
                    }
                }
            }
        });
    }

    function saveDetailInProcess() {
        // var formValid = validJsmm();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        var formData = _GetFormJsonMini("formGjbj");
        formData.gjbj = detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Gjbj/saveGjbj.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    var message = "";
                    if (data.success) {
                        detailListGrid.reload();
                    } else {
                        mini.alert("数据保存失败" + data.message);
                    }
                }
            }
        });
    }

    function getData() {
        var formData = _GetFormJsonMini("formGjbj");
        formData.gjbj = detailListGrid.getChanges();
        //此处用于向后台产生流程实例时替换标题中的参数时使用
        // formData.bos=[];
        // formData.vars=[{key:'companyName',val:formData.companyName}];
        return formData;
    }

    function saveGjbj(e) {
        // var formValid = validZero();
        // if (!formValid.result) {
        //     mini.alert(formValid.message);
        //     return;
        // }
        window.parent.saveDraft(e);
    }

    function validZero() {
        var apply = $.trim(mini.get("apply").getValue());
        if (!apply) {
            return {"result": false, "message": "请填写提交人"};
        }
        var wh = $.trim(mini.get("wh").getValue());
        if (!wh) {
            return {"result": false, "message": "请选择维护人"};
        }
        var fw = $.trim(mini.get("fw").getValue());
        if (!fw) {
            return {"result": false, "message": "请选择服务工程审核人"};
        }
        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写详细信息表"};
        }
        return {"result": true};
    }

    function startGjbjProcess(e) {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start' || action == "edit") {
            var formValid = validFirst();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else if (stageName == "select") {
            var whId = mini.get("wh").getValue();
            if (whId == null || whId == "") {
                mini.alert("请选择维护人员");
                return;
            }
        }
        // 维护反馈这步沟通说是非必填，验证先关闭 ,如有需要再开启
        // else if (stageName == "whfk") {
        //     var formValid = validSecond();
        //     if (!formValid.result) {
        //         mini.alert(formValid.message);
        //         return;
        //     }
        //
        // }

        //检查通过
        window.parent.approve();
    }


    function validFirst() {

        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写详细信息表"};
        } else {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {
                if (detail[i].partsType == undefined || detail[i].partsType == "") {
                    return {"result": false, "message": "请选择第" + (i + 1) + "行类别！"};
                }
                if (detail[i].desginModel == undefined || detail[i].desginModel == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行设计机型！"};
                }
                if (detail[i].vinCode == undefined || detail[i].vinCode == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行机型物料编码！"};
                }
                if (detail[i].productDepartName == undefined || detail[i].productDepartName == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行产品所！"};
                }
                if (detail[i].holeCode == undefined || detail[i].holeCode == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行总成物料编码！"};
                }
                if (detail[i].holeDesc == undefined || detail[i].holeDesc == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行总成物料描述！"};
                }
                if (detail[i].materialCode == undefined || detail[i].materialCode == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行物料编码！"};
                }
                if (detail[i].materialDesc == undefined || detail[i].materialDesc == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行物料描述！"};
                }
                if (detail[i].supplier == undefined || detail[i].supplier == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行供应商！"};
                }
                if (detail[i].partsNum == undefined || detail[i].partsNum == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行装配数量！"};
                }

                // 这个是后续填的
                // if (detail[i].devCode == undefined || detail[i].devCode == "") {
                //     return {"result": false, "message": "请填写第" + (i + 1) + "行开发件物料编码！"};
                // }
                // if (detail[i].devDesc == undefined || detail[i].devDesc == "") {
                //     return {"result": false, "message": "请填写第" + (i + 1) + "行开发件物料描述！"};
                // }
                // if (detail[i].planActTime == undefined || detail[i].planActTime == "") {
                //     return {"result": false, "message": "请填写第" + (i + 1) + "行备件开发计划时间！"};
                // }
                // if (detail[i].needExp == undefined || detail[i].needExp == "") {
                //     return {"result": false, "message": "请填写第" + (i + 1) + "行是否有试验验证需求！"};
                // }

            }
        }
        return {"result": true};
    }

    function validSecond() {

        var detail = detailListGrid.getData();
        if (detail.length < 1) {
            return {"result": false, "message": "请填写详细信息表"};
        } else {
            var detail = detailListGrid.getData();
            for (var i = 0; i < detail.length; i++) {


                // 这个是后续填的
                if (detail[i].devCode == undefined || detail[i].devCode == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行开发件物料编码！"};
                }
                if (detail[i].devDesc == undefined || detail[i].devDesc == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行开发件物料描述！"};
                }
                if (detail[i].planActTime == undefined || detail[i].planActTime == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行备件开发计划时间！"};
                }
                if (detail[i].needExp == undefined || detail[i].needExp == "") {
                    return {"result": false, "message": "请填写第" + (i + 1) + "行是否有试验验证需求！"};
                }

            }
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
        formGjbj.setEnabled(false);
        mini.get("addGjbjDetail").setEnabled(false);
        mini.get("removeGjbjDetail").setEnabled(false);
        mini.get("importId").setEnabled(false);

        if (stageName == "start") {
            detailListGrid.setAllowCellEdit(true);
            mini.get("addGjbjDetail").setEnabled(true);
            mini.get("removeGjbjDetail").setEnabled(true);
            mini.get("importId").setEnabled(true);
        } else if (stageName == "select") {
            // detailListGrid.setAllowCellEdit(true);
            mini.get("wh").setEnabled(true);
        } else if (stageName == "whfk") {
            detailListGrid.setAllowCellEdit(true);
            mini.get("importId").setEnabled(true);

        }

    }


    function addGjbjDetail() {
        var formId = mini.get("gjbjId").getValue();
        if (!formId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        } else {
            var row = {};
            detailListGrid.addRow(row);
        }
    }

    function removeGjbjDetail() {
        var selecteds = detailListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }

    function openImportWindow() {
        var formId = mini.get("gjbjId").getValue();
        if (!formId) {
            mini.alert('请先点击‘保存’进行表单的保存！');
            return;
        }
        importWindow.show();
    }

    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        detailListGrid.reload();
    }

    //导入模板下载
    function downImportOil() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdm/core/Gjbj/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //文件类型判断及文件名显示
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls' || fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }

    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //上传批量导入
    function importProduct() {
        var gjbjId = mini.get("gjbjId").getValue();
        ;
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                            detailListGrid.reload();
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/rdm/core/Gjbj/importExcel.do?belongId=' + gjbjId, false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }

    function exportGjbjDetail() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;
        if ((action == "edit" || stageName == "start")
            && (field == "partsType" || field == "desginModel" || field == "vinCode" || field == "productDepartName"
                || field == "holeCode" || field == "holeDesc" || field == "materialCode" || field == "materialDesc"
                || field == "supplier" || field == "partsNum" || field == "remark")
        ) {
            e.cancel = false;
        }
        if ((action == "task" && stageName == "whfk")
            && (field == "devCode" || field == "devDesc" || field == "planActTime" || field == "needExp")
        ) {
            e.cancel = false;
        }
    }


</script>
</body>
</html>
