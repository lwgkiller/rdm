$(function () {
   searchSend();
});

function searchSend() {
    sendMsgListGrid.load();
}

function editMsg() {
    mini.open({
        title: "发送消息",
        url: jsUseCtxPath + "/xcmgProjectManager/core/message/edit.do",
        width: 880,
        height: 670,
        showModal:false,
        allowResize: true,
        ondestroy: function (action) {
            refreshTab();
        }
    });
}