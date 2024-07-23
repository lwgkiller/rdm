$(function () {
});


function typeChanged() {
    var type = mini.get("messageTypeId").getValue();
    if (type == 'system') {
        mini.get("recUserIds").setEnabled(false);
        mini.get("canGroupBtn").setEnabled(false);
        mini.get("recType").setEnabled(false);
        mini.get("recType").setValue('computer');
    }else{
        mini.get("recUserIds").setEnabled(true);
        mini.get("canGroupBtn").setEnabled(true);
        mini.get("recType").setEnabled(true);
    }
}

//发送消息
function sendMsg() {
    //检查必填项
    var messageTypeId = mini.get("messageTypeId").getValue();
    if(!messageTypeId) {
        mini.alert('请选择消息类型！');
        return;
    }
    var recType = mini.get("recType").getValue();
    if(!recType) {
        mini.alert('请选择发送的客户端！');
        return;
    }
    var recUserIds = mini.get("recUserIds").getValue();
    if(!recUserIds) {
        recUserIds='';
    }
    var canGroupIds = mini.get("canGroupIds").getValue();
    if(!canGroupIds) {
        canGroupIds='';
    }
    if (messageTypeId != 'system' && !recUserIds && !canGroupIds) {
        mini.alert('请选择收件人或者收件组！');
        return;
    }
    var title = mini.get("titleId").getValue();
    if(!$.trim(title)) {
        mini.alert('请填写消息标题！');
        return;
    }

    var messageData={};
    messageData.recUserIds=recUserIds;
    messageData.recGroupIds=canGroupIds;
    messageData.title=title;
    messageData.content=getContent();
    //用于钉钉消息
    messageData.plainTxt=getPlainTxt();
    messageData.messageType=messageTypeId;
    messageData.canPopup=mini.get("canPopup").getValue();
    var expireTime=mini.get("expireTimeId").getText()
    messageData.expireTime=expireTime?expireTime:"";
    messageData.appName = mini.get("appName").getValue();
    messageData.recType = mini.get("recType").getValue();
    messageData = mini.encode(messageData);
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/message/send.do',
        type: 'POST',
        data: messageData,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            mini.alert("消息发送成功！","提醒",function (action) {
                CloseWindow();
            });
        }
    });
}
