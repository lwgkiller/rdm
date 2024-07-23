<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>帖子详情</title>
    <%@include file="/commons/edit.jsp" %>
    <style>
        .new_body {
            margin: 0 auto;
            width: 60%;
            min-height: 500px;
            height: 86%;
            padding-bottom: 5px;
            border: 1px #ccc solid;
            background-color: #ecf5ff;
            overflow: auto;
            height: auto;
            border-radius:20px
        }

        .new_body h2 {
            margin: 20px 0 10px;
            height: 30px;
            position: relative;
            line-height: 30px;
            font-size: 25px;
            font-family: "宋体";
            font-weight: bold;
            text-align: center;
            overflow: hidden;
        }

        #author {
            display: block;
            height: 15px;
            line-height: 20px;
            font-size: 15px;
            text-align: center;
            padding-top: 5px;
            border-top: 1px red solid;
        }

        .comment-container {
            margin: 0 auto;
            width: 60%;
        }

        .commentTopper {
            border-radius: 2px;
            height: 30px;
            padding: 0 20px;
            border-bottom: 1px solid #f6f6f6;
            /*background: #fff;*/
            width: 98%;
        }

        .CommentTopbar-title {
            color: #121212;
            font-size: 15px;
            font-weight: 600
        }

        .commentFooter {
            text-align: center;
            margin-top: 5px;
        }

        .SG_cmp_revert {
            overflow: hidden;
            width: 100%
        }

        .SG_revert_Cont {
            float: none;
            margin-left: 20px !important;
            width: 100%;
            font-family: "宋体";
            padding-top: 2px
        }

        .SG_revert_Tit {
            float: left;
            font-weight: bold
        }

        .SG_revert_Inner {
            line-height: 22px;
            clear: both;
            width: 98%;
            font-size: 14px;
            margin-top: 9px;
            word-break: break-all
        }

        .SG_txtb {
            color: #7C7C7C
        }

        .xuxian {
            height: 1px;
            width: 100%;
            border-top: 1px dashed grey;
            margin-top: 5px;
        }

        .myReFrom {
            width: 98%
        }

        .SG_reply {
            margin-top: 10px
        }

        .SG_conn {
            clear: both;
            margin: 0;
            width: auto;
            height: 100%;
            margin-bottom: 10px;
        }

        .borderc {
            border-color: #DFDFDF;
            border: 1px solid #d0d0d0;
            width: 98%;
        }

        .SG_reply_con .bd {
            width: 90%;
            padding: 0 20px;
            text-align: center;
        }

        .txt {
            width: 523px;
            float: left;
            font-size: 12px;
            line-height: 20px;
        }
    </style>
</head>
<body>
<div class="mini-panel" style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div class="new_body">
        <div id="title" style="font-size:20px;font-weight:bold;text-align:center;margin-top: 10px;margin-bottom: 10px">
        </div>
        <div>
            <span id="author"></span>
        </div>
        <div id="content" style="margin-top: 20px;"></div>
    </div>
    <div class="comment-container" style="margin-top: 10px">
        <div>
            <div class="commentTopper">
                <div class="Topbar-title"><h2 class="CommentTopbar-title">${applyObj.discussNum}条评论</h2></div>
            </div>
            <div class="commentFooter">
                <textarea id="replayContent" value="" style="width: 98%;height: 80px;border-radius:6px"
                          placeholder="写下你的评论..."></textarea>
                <button type="button" onclick="publishComment()" id="publishButton"
                        style="background-color:#06f;color: #fff;width: 100px;height: 25px;border-radius:17px;border-color: antiquewhite">发表评论
                </button>
            </div>
            <div class="commentList">
                <ul id="article_comment_list" class="SG_cmp_revert">
                </ul>
                <div class="SG_page" id="commentPaging" style="display: none; text-align: center;margin-bottom: 20px">
                    <a href="#" style="" onclick="loadMore()">点击加载更多</a>
                </div>
                <div class="SG_page" id="loadingFinish" style="display: none; text-align: center;margin-bottom: 20px">
                    <span>已加载全部评论</span>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var messageObj =${applyObj};
    var paperId = "${applyObj.id}";
    var pageSize = 5;
    var currentIndex = 0;
    var discussNum = ${applyObj.discussNum};
    setData();
    $(function () {
        getCommentList(pageSize, currentIndex);
        if("${applyObj.closed}"=="1"){
            $('#publishButton').hide();
            $('.btn_reply').hide();
            $('.bottomReplay').hide();
        }
    })
    var noticeForm = new mini.Form("#noticeForm");
    noticeForm.setData(messageObj);

    function setData() {
        if (messageObj) {
            if (messageObj.title) {
                $("#title").html(messageObj.title);
            }
            var author = "发帖时间：" + messageObj.CREATE_TIME_ + " 发帖人：" + messageObj.publisher + " ";
            $("#author").html(author);
            if (messageObj.content) {
                $("#content").html(messageObj.content);
            }
        }
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
            mini.alert("请填写评论内容");
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
            mini.alert("请填写评论内容");
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
        mini.prompt("请输入评论内容：", "请输入",
            function (action, value) {
                if (action == "ok") {
                    if (value.trim() == '') {
                        mini.alert("请填写评论内容");
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
</script>
</body>
</html>
