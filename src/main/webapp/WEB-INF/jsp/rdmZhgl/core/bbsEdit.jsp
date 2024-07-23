<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>帖子</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/kindeditor4/kindeditor/kindeditor-all-min.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/bbsEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="publish" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png" onclick="publish()"><spring:message code="page.bbsEdit.fb" /></a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif" onclick="CloseWindow()"><spring:message code="page.bbsEdit.gb" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="noticeForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="plate" name="plate" class="mini-hidden"/>
            <input id="model" name="model" class="mini-hidden"/>
            <input id="recordId" name="recordId" class="mini-hidden"/>
            <input id="picName" name="picName" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-two grey">
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsEdit.tzfl" /><span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input id="bbsType" name="bbsType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="<spring:message code="page.bbsEdit.tzfl" />：" onvaluechanged="typeChanged"
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.bbsEdit.qxz" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BBSType"
                               nullitemtext="<spring:message code="page.bbsEdit.qxz" />..." emptytext="<spring:message code="page.bbsEdit.qxz" />..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsEdit.bt" /><span style="color: #ff0000">*</span>：
                    </td>
                    <td align="left">
                        <input id="title" name="title" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:200" label="<spring:message code="page.bbsEdit.bt" />：" onvalidation=""
                               datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput"
                               required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence=""
                               scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsEdit.zt" />：
                    </td>
                    <td align="left">
                        <input name="direction" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:200" label="<spring:message code="page.bbsEdit.zt" />：" onvalidation=""
                               datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput"
                               required="false" only_read="false" allowinput="true" value="" format="" emptytext="" sequence=""
                               scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsEdit.nr" />:
                        <br>
                        <span style="color: red"><spring:message code="page.bbsEdit.z" /></span>
                    </td>
                    <td align="left">
                        <input id="content" name="content" class="mini-hidden"/>
                        <div name="editor" id="editor" rows="10" cols="80" style="height: 500px">
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var editor ;
    var noticeForm = new mini.Form("#noticeForm");
    var applyObj = ${applyObj};
    var action = '${action}';
    var flag = '${flag}';
    var picName = '';
    var plate = '${applyObj.plate}';
    var model = '${applyObj.model}';

    function setContent(_text) {
        editor.html(_text);
    }
    function getContent() {
        return editor.html();
    }
    function setDisabled() {
        editor.readonly();
    }
    function setEnabled() {
        editor.readonly(false);
    }
    function publish() {
        noticeForm.validate();
        if (!noticeForm.isValid()) {
            return;
        }
        if(mini.get('title').getValue()==''){
            mini.alert(bbsEdit_btbn);
            return;
        }
        mini.get('picName').setValue(picName);
        mini.get('content').setValue(getContent());
        if(getContent().trim()==''){
            mini.alert(bbsEdit_nrbn);
            return;
        }
        var formData = noticeForm.getData();
        var config = {
            url: jsUseCtxPath+"/rdmZhgl/core/bbs/save.do",
            method: 'POST',
            data: formData,
            success: function (result) {
                //如果存在自定义的函数，则回调
                var result=mini.decode(result);
                if(result.success){
                    if(flag=='outer'){
                        window.parent.closeDialog();
                    }else{
                        CloseWindow('ok');
                    }
                }else{
                };
            }
        }
        _SubmitJson(config);
    }
</script>
</body>
</html>
