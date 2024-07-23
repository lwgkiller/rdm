var editForm = '';
var isFirstNode = false;
var replyUser = false;
var techOpinion = false;
var pointReplyUser = false;
$(function () {
    mini.get("bbsType").setEnabled(false);
    mini.get("publisher").setEnabled(false);
    mini.get("createTime").setEnabled(false);
    if (action == 'task') {
        bpmPreTaskTipInForm();
        getProcessNodeVars();
        if (editForm != '1' && !isFirstNode) {
            noticeForm.setEnabled(false);
        }
        noticeForm.setData(applyObj);
    } else if (action == 'detail') {
        $("#detailToolBar").show();
        noticeForm.setEnabled(false);
        noticeForm.setData(applyObj);
    } else if (action == 'edit') {
        noticeForm.setData(applyObj);
    }
    if (flag == 'outer') {
        mini.get('closeWindow').hide();
    }
    if (applyObj.isAdopt == '0') {
        $('#planTr').hide();
        $('#unAdoptReasonTr').show();
    } else if (applyObj.isAdopt == '1') {
        $('#planTr').show();
        $('#unAdoptReasonTr').hide();
    } else {
        $('#planTr').hide();
        $('#unAdoptReasonTr').hide();
    }
    if(replyUser){
        mini.get('isAdopt').setEnabled(true);
        mini.get('planFinishDate').setEnabled(true);
        mini.get('planContent').setEnabled(true);
        mini.get('unAdoptReason').setEnabled(true);
    }else{
        mini.get("isAdopt").setEnabled(false);
    }
    if(techOpinion){
        mini.get("techOpinion").setEnabled(true);
    }else{
        mini.get("techOpinion").setEnabled(false);
    }
    if(pointReplyUser){
        mini.get("replyUserId").setEnabled(true);
    }else{
        mini.get("replyUserId").setEnabled(false);
    }
    getCommentList(pageSize, currentIndex);
    if("${applyObj.closed}"=="1"){
        $('#publishButton').hide();
        $('.btn_reply').hide();
        $('.bottomReplay').hide();
    }
});

function adoptChanged(e) {
    var obj = e.selected;
    if (obj == undefined) {
        $('#planTr').hide();
        $('#unAdoptReasonTr').hide();
    } else {
        if (obj.key_ == '0') {
            $('#planTr').hide();
            $('#unAdoptReasonTr').show();
        } else if (obj.key_ == '1') {
            $('#planTr').show();
            $('#unAdoptReasonTr').hide();
        }
    }

}


//流程实例图
function processInfo() {
    let instId = $("#instId").val();
    if(!instId){
        mini.alert(bbsApplyEdit_name);
        return
    }
    let url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
    _OpenWindow({
        url: url,
        max: true,
        title: bbsApplyEdit_name1,
        width: 800,
        height: 600
    });
}



//获取环境变量
function getProcessNodeVars() {
    if (!nodeVarsStr) {
        nodeVarsStr = "[]";
    }
    var nodeVars = $.parseJSON(nodeVarsStr);
    for (var i = 0; i < nodeVars.length; i++) {
        if (nodeVars[i].KEY_ == 'editForm') {
            editForm = nodeVars[i].DEF_VAL_;
        }
        if (nodeVars[i].KEY_ == 'isFirstNode') {
            isFirstNode = true;
        }
        if (nodeVars[i].KEY_ == 'reply') {
            replyUser = true;
        }
        if (nodeVars[i].KEY_ == 'techOpinion') {
            techOpinion = true;
        }
        if (nodeVars[i].KEY_ == 'pointReplyUser') {
            pointReplyUser = true;
        }
    }
}



//保存草稿
function saveApplyInfo(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.saveDraft(e);
}

//流程引擎调用此方法进行表单数据的获取
function getData() {
    var formData = _GetFormJsonMini("noticeForm");
    //此处用于向后台产生流程实例时替换标题中的参数时使用
    formData.bos = [];
    formData.vars = [];
    return formData;
}

//保存草稿或提交时数据是否有效
function draftOrStartValid() {
    var urgency=$.trim(mini.get("urgency").getValue());
    if(!urgency) {
        return {"result": false, "message": bbsApplyEdit_name2};
    }
    var isAdopt = $.trim(mini.get("isAdopt").getValue());
    if(replyUser){
        if(!isAdopt){
            return {"result": false, "message": bbsApplyEdit_name3};
        }
    }
    if(isAdopt=='0'){
        var unAdoptReason=$.trim(mini.get("unAdoptReason").getValue());
        if(!unAdoptReason) {
            return {"result": false, "message": bbsApplyEdit_name4};
        }
    }
    if(isAdopt=='1'){
        var planFinishDate=$.trim(mini.get("planFinishDate").getValue());
        if(!planFinishDate) {
            return {"result": false, "message": bbsApplyEdit_name5};
        }
    }
    if(getContent().trim()==''){
        return {"result": false, "message": bbsApplyEdit_name6};
    }
    mini.get('picName').setValue(picName);
    mini.get('content').setValue(getContent());
    return {"result": true};
}

//启动流程
function startApplyProcess(e) {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.startProcess(e);
}

//审批或者下一步
function applyApprove() {
    var formValid = draftOrStartValid();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    window.parent.approve();
}


function reply(replyId) {
    $('#cancel' + replyId).show();
    $('#con' + replyId).show();
    $('#reply' + replyId).hide();
    //加载回复内容
    getChildCommentList(replyId);
}
function cancelReply(replyId) {
    $('#cancel' + replyId).hide();
    $('#con' + replyId).hide();
    $('#reply' + replyId).show();
}
function publishComment() {
    var commentContent = $('#replayContent').val();
    if (commentContent.trim() == '') {
        mini.alert(bbsApplyEdit_name7);
        return
    }

    let postData = {"mainId": paperId, "content": commentContent};
    let url = jsUseCtxPath + '/rdmZhgl/core/bbs/comment/save.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (resultData) {
        mini.alert(resultData.message);
        if (resultData.success) {
            setTimeout(function () {
                window.location.reload()
            }, 500)
        }
    }
}

function getCommentList(pageSize, currentIndex) {
    let postData = {"mainId": paperId, "pageSize": pageSize, "currentIndex": currentIndex};
    let url = jsUseCtxPath + '/rdmZhgl/core/bbs/comment/list.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (resultData) {
        if (resultData.success) {
            var list = resultData.data;
            _html = '';
            for (var i = 0; i < list.length; i++) {
                var commentObj = list[i];
                var _li = '<li>\n' +
                    '<div class="SG_revert_Cont">\n' +
                    '<p><span class="SG_revert_Tit">【' + commentObj.deptName + '】 ' + commentObj.publisher + '</span></p>\n' +
                    '<textarea class="mini-textarea rxc" plugins="mini-textarea" enabled="false" style="width:98%;height:80px;" value="'+commentObj.content+'"></textarea>'+
                    /*'<div class="SG_revert_Inner SG_txtb">&nbsp;' + commentObj.content + '</div>\n' +*/
                    '<p class="myReFrom">\n' +
                    '<em class="SG_txtc">' + commentObj.CREATE_TIME_ + '</em>\n' +
                    '<span class="SG_revert_Func" style="float: right">\n' +
                    '<a href="#" id="reply' + commentObj.id + '" onclick="reply(\'' + commentObj.id + '\')" class="SG_a_fucs">\n' +
                    '<cite>回复(' + commentObj.replyNum + ')</cite>\n' +
                    '</a>\n' +
                    '<a href="#" id="cancel' + commentObj.id + '" onclick="cancelReply(\'' + commentObj.id + '\')" style="display: none" class="SG_a_fucs">\n' +
                    '<cite>取消回复</cite>\n' +
                    '</a>\n' +
                    '</span>\n' +
                    '</p>\n' +
                    '<div class="SG_reply" id="con' + commentObj.id + '" style="display: none">\n' +
                    '<div class="SG_reply_con borderc">\n' +
                    '<div class="bd">\n' +
                    '<div style="margin-top: 5px">\n' +
                    '<textarea id="replyComment' + commentObj.id + '" node-type="textEl" style="width: 80%"></textarea>\n' +
                    '<a href="#" class="SG_aBtn btn_reply" node-type="subtnEl" onclick="replyComment(\'' + commentObj.id + '\')"><cite>回复</cite></a>\n' +
                    '</div>\n' +
                    '<div class="reply_list">\n' +
                    '<ul id="replyList' + commentObj.id + '"\n' +
                    'style="display:block;"></ul>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '</div>\n' +
                    '<div class="xuxian"></div>\n' +
                    '</li>';
                _html += _li;
            }
            $('#article_comment_list').append(_html);
            if (pageSize * (currentIndex + 1) < discussNum) {
                $('#commentPaging').show();
                $('#loadingFinish').hide();
            } else {
                $('#commentPaging').hide();
                $('#loadingFinish').show();
            }
        } else {
            mini.alert(resultData.message);
        }
    }
}
function getChildCommentList(replyId) {
    let postData = {"parentId": replyId};
    let url = jsUseCtxPath + '/rdmZhgl/core/bbs/comment/childComments.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (resultData) {
        if (resultData.success) {
            var list = resultData.data;
            _html = '';
            for (var i = 0; i < list.length; i++) {
                var commentObj = list[i];
                var _li = '<li style="border-bottom: 1px dashed grey;margin-bottom: 2px">\n' +
                    '<p style="white-space:pre-wrap;">\n' +
                    '<span>【'+commentObj.newDeptName+'】'+commentObj.newReplier+'</span>&nbsp;回复&nbsp;<span>【'+commentObj.orgDeptName+'】'+commentObj.orgReplier+'：</span>'+commentObj.content+'（'+commentObj.CREATE_TIME_+'）' +
                    '<a href="#" class="bottomReplay" onclick="replys(\''+commentObj.id+'\')" >回复</a>\n' +
                    '</p>\n' +
                    '<li>'
                _html += _li;
            }
            $('#replyList'+replyId).html(_html);
            if("${applyObj.closed}"=="1"){
                $('.bottomReplay').hide();
            }
        } else {
            mini.alert(resultData.message);
        }
    }
}

function loadMore() {
    currentIndex = currentIndex + 1;
    getCommentList(pageSize, currentIndex * pageSize);
}

function replyComment(replyId) {
    var commentContent = $('#replyComment' + replyId).val();
    if (commentContent.trim() == '') {
        mini.alert(bbsApplyEdit_name7);
        return
    }
    let postData = {"mainId": paperId, "content": commentContent, "parentId": replyId};
    let url = jsUseCtxPath + '/rdmZhgl/core/bbs/comment/save.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (resultData) {
        mini.alert(resultData.message);
        if (resultData.success) {
            setTimeout(function () {
                window.location.reload()
            }, 500)
        }
    }
}

function replys(replyId) {
    mini.prompt(bbsApplyEdit_name8, bbsApplyEdit_name9,
        function (action, value) {
            if (action == "ok") {
                if (value.trim() == '') {
                    mini.alert(bbsApplyEdit_name7);
                    return
                }
                let postData = {"mainId": paperId, "content": value, "parentId": replyId};
                let url = jsUseCtxPath + '/rdmZhgl/core/bbs/comment/save.do';
                let resultData = ajaxRequest(url, 'POST', false, postData);
                if (resultData) {
                    mini.alert(resultData.message);
                    if (resultData.success) {
                        setTimeout(function () {
                            window.location.reload()
                        }, 500)
                    }
                }
            }
        },
        true
    );
}
