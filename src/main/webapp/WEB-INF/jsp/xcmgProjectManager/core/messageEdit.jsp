<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>发送消息</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/messageEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/kindeditor/kindeditor-all-min.js" type="text/javascript"></script>
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
<div class="mini-panel"  style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div style="margin-top:5px;margin-right:10px;float: right">
        <a class="mini-button" iconCls="icon-ok" onclick="sendMsg()">发送</a>
        <a class="mini-button btn-red" plan="true" onclick="CloseWindow()">关闭</a>
    </div>
    <div style="margin: 45px 10px 10px 10px;border:1px solid #d1d1d1;height: 84%" >
        <table class="table-detail" cellspacing="1" cellpadding="0" >
            <tr>
                <td style="width: 25%">消息类型：<span style="color: #ff0000">*</span></td>
                <td style="width: 25%;min-width:200px">
                    <input id="messageTypeId" name="typeId" class="mini-combobox" style="width:98%;"
                           textField="key" valueField="value" emptyText="请选择..."
                           data="[{key:'平台消息',value:'system'},{key:'组消息',value:'group'}]"
                           allowInput="false" showNullItem="false" onvaluechanged="typeChanged()"/>
                </td>
                <td>选择发送的客户端：</td>
                <td>
                    <input id="recType" name="recType" class="mini-checkboxlist" multiSelect="true" style="width:98%"
                           textField="key" valueField="value" value="computer" data="[{key:'电脑端',value:'computer'},{key:'手机端',value:'dingding'}]"
                    />
                </td>
            </tr>
            <tr>
                <td>是否首页弹出：</td>
                <td>
                    <input id="canPopup" name="canPopup" class="mini-radiobuttonlist" style="width:100%;"  repeatItems="2" repeatLayout="table" repeatDirection="horizontal" textfield="appName" valuefield="appKey"
                      value="no" data="[{'appName':'是','appKey':'yes'},{'appName':'否','appKey':'no'}]"/>
                </td>
                <td style="width: 25%">弹出过期时间：</td>
                <td>
                    <input id="expireTimeId" name="expireTime"  class="mini-datepicker" format="yyyy-MM-dd" showTime="false" showOkButton="false" showClearButton="true" style="width:100%"/>
                </td>
            </tr>
            <tr id="webAppTr" style="display: none">
                <td>接收平台：</td>
                <td colspan="3">
                    <input id="appName" name="appName" class="mini-radiobuttonlist" style="width:100%;" value="rdm" repeatItems="2" repeatLayout="table" repeatDirection="horizontal" textfield="appName" valuefield="appKey"
                    data="[{'appName':'研发数据集成管理平台','appKey':'rdm'},{'appName':'企业标准化管理平台','appKey':'sim'}]"/>
                </td>
            </tr>
            <tr>
                <td>收件人：<span style="color: #ff0000">*</span></td>
                <td colspan="3">
                    <input id="recUserIds" name="recUserIds" textname="recUserNames" class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="收件人"
                           mainfield="no"  single="false" onvaluechanged="checkUserNum()" />
                </td>
            </tr>
            <tr>
                <td>收件组：<span style="color: #ff0000">*</span></td>
                <td colspan="3">
                    <input id="canGroupIds" name="canGroupIds" allowInput="false" class="mini-textboxlist" style="width: 80%; min-height:32px;" />
                    <a class="mini-button"  id="canGroupBtn" plain="true" onclick="selectCanGroups()">添加</a>
                </td>
            </tr>
            <tr>
                <td>消息标题：<span style="color: #ff0000">*</span></td>
                <td colspan="3">
                    <input id="titleId" name="title" class="mini-textbox" style="width:98%;" />
                </td>
            </tr>
            <tr>
                <td>消息内容：（500字以内）</td>
                <td colspan="3">
                    <input id="contentId" name="content" class="mini-hidden"/>
                    <div name="editor" id="editor" rows="10" cols="95" style="height: 335px;width: 600px">
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var editor = KindEditor.create('#editor');
    function setContent(_text) {
        editor.html(_text);
    }
    function getContent() {
        return editor.html();
    }
    function getPlainTxt() {
        return editor.text();
    }
    function setDisabled() {
        editor.readonly();
    }
    function setEnabled() {
        editor.readonly(false);
    }

    function selectCanGroups(){
        var canGroupIds=mini.get('canGroupIds');
        _GroupCanDlg({
            tenantId:'1',
            single:false,
            width:900,
            height:500,
            title:'用户组',
            callback:function(groups){
                var uIds=[];
                var uNames=[];
                for(var i=0;i<groups.length;i++){
                    uIds.push(groups[i].groupId);
                    uNames.push(groups[i].name);
                }
                if(canGroupIds.getValue()!=''){
                    uIds.unshift(canGroupIds.getValue().split(','));
                }
                if(canGroupIds.getText()!=''){
                    uNames.unshift(canGroupIds.getText().split(','));
                }
                canGroupIds.setValue(uIds.join(','));
                canGroupIds.setText(uNames.join(','));
            }
        });
    }
</script>
</body>
</html>
