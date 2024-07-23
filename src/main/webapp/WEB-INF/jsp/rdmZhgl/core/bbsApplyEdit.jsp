<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>帖子</title>
    <%@include file="/commons/edit.jsp" %>
    <script src="${ctxPath}/scripts/kindeditor4/kindeditor/kindeditor-all-min.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/bbsApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
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
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button"  onclick="processInfo()"><spring:message code="page.bbsApplyEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.bbsApplyEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="noticeForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="plate" name="plate" class="mini-hidden"/>
            <input id="model" name="model" class="mini-hidden"/>
            <input id="recordId" name="recordId" class="mini-hidden"/>
            <input id="picName" name="picName" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail grey">
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name2" />：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="publisher" name="publisher" class="mini-textbox" style="width:100%"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name3" />：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="createTime" name="createTime" class="mini-textbox" style="width:100%"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name4" />：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="bbsType" name="bbsType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="<spring:message code="page.bbsApplyEdit.name4" />：" onvaluechanged="typeChanged"
                               length="50" readonly
                               required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.bbsApplyEdit.name5" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BBSType"
                               nullitemtext="<spring:message code="page.bbsApplyEdit.name5" />..." emptytext="<spring:message code="page.bbsApplyEdit.name5" />..."/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name6" /><span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="urgency" name="urgency" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px"  label="<spring:message code="page.bbsApplyEdit.name6" />："
                               length="50"
                               required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.bbsApplyEdit.name5" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BBSUrgency"
                               nullitemtext="<spring:message code="page.bbsApplyEdit.name5" />..." emptytext="<spring:message code="page.bbsApplyEdit.name5" />..."/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name7" /><span style="color: #ff0000">*</span>：
                    </td>
                    <td align="left" colspan="1">
                        <input name="title" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:200" label="<spring:message code="page.bbsApplyEdit.name7" />：" onvalidation=""
                               datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput"
                               required="true" only_read="false" allowinput="true" value="" format="" emptytext="" sequence=""
                               scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name8" />：
                        <br>
                        <spring:message code="page.bbsApplyEdit.name9" />
                    </td>
                    <td align="left" colspan="1">
                        <input id="replyUserId" name="replyUserId" textname="replyUserName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="<spring:message code="page.bbsApplyEdit.name10" />"
                               mainfield="no"  single="false"  />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name11" />：
                    </td>
                    <td align="left" colspan="3">
                        <input name="direction" class="mini-textbox rxc" plugins="mini-textbox" vtype="length:200" label="<spring:message code="page.bbsApplyEdit.name11" />：" onvalidation=""
                               datatype="varchar" length="50" decimal="0" minnum="" maxnum="" validrule="" from="forminput"
                               required="false" only_read="false" allowinput="true" value="" format="" emptytext="" sequence=""
                               scripts="" mwidth="100" wunit="%" mheight="34" hunit="px" style="width:100%;height:34px"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name12" />：
                    </td>
                    <td align="center" colspan="1" rowspan="1">
                        <input id="isAdopt" name="isAdopt" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.bbsApplyEdit.name12" />："
                               length="50"  onvaluechanged="adoptChanged"
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.bbsApplyEdit.name5" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                               nullitemtext="<spring:message code="page.bbsApplyEdit.name5" />..." emptytext="<spring:message code="page.bbsApplyEdit.name5" />..."/>
                    </td>

                </tr>
                <tr id="planTr">
                    <td align="center"><spring:message code="page.bbsApplyEdit.name13" />：</td>
                    <td>
                        <input id="planFinishDate" name="planFinishDate" class="mini-datepicker"  allowinput="false"  format="yyyy-MM-dd" style="width:98%;"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name14" />：
                    </td>
                    <td align="left" colspan="3">
                        <textarea id="planContent" name="planContent" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:150px;line-height:25px;" label="" datatype="varchar" length="1000"
                                  vtype="length:1000" minlen="0" allowinput="true" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr id="unAdoptReasonTr">
                    <td style="text-align: center"><spring:message code="page.bbsApplyEdit.name15" />：</td>
                    <td colspan="3">
						<textarea id="unAdoptReason" name="unAdoptReason" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:80px;line-height:25px;"
                                  label="<spring:message code="page.bbsApplyEdit.name15" />" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr >
                    <td style="text-align: center"><spring:message code="page.bbsApplyEdit.name16" />：</td>
                    <td colspan="3">
						<textarea id="techOpinion" name="techOpinion" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:80px;line-height:25px;"
                                  label="<spring:message code="page.bbsApplyEdit.name16" />" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.bbsApplyEdit.name17" />:
                        <br>
                        <span style="color: red"><spring:message code="page.bbsApplyEdit.name18" /></span>
                    </td>
                    <td align="left" colspan="3">
                        <input id="content" name="content" class="mini-hidden"/>
                        <div name="editor" id="editor" rows="10" cols="80" style="height: 500px;width: 100%">
                        </div>
                    </td>
                </tr>
            </table>
            <div id="commentDiv" class="comment-container" style="margin-top: 10px">
                <div>
                    <div class="commentTopper">
                        <div class="Topbar-title"><h2 class="CommentTopbar-title">${applyObj.discussNum}<spring:message code="page.bbsApplyEdit.name19" /></h2></div>
                    </div>
                    <div class="commentFooter">
                <textarea id="replayContent" value="" style="width: 98%;height: 80px;border-radius:6px"
                          placeholder="<spring:message code="page.bbsApplyEdit.name20" />..."></textarea>
                        <button type="button" onclick="publishComment()" id="publishButton"
                                style="background-color:#06f;color: #fff;width: 100px;height: 25px;border-radius:17px;border-color: antiquewhite"><spring:message code="page.bbsApplyEdit.name21" />
                        </button>
                    </div>
                    <div class="commentList">
                        <ul id="article_comment_list" class="SG_cmp_revert">
                        </ul>
                        <div class="SG_page" id="commentPaging" style="display: none; text-align: center;margin-bottom: 20px">
                            <a href="#" style="" onclick="loadMore()"><spring:message code="page.bbsApplyEdit.name22" /></a>
                        </div>
                        <div class="SG_page" id="loadingFinish" style="display: none; text-align: center;margin-bottom: 20px">
                            <span><spring:message code="page.bbsApplyEdit.name23" /></span>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var editor ;
    var noticeForm = new mini.Form("#noticeForm");
    var applyObj = ${applyObj};
    var action = "${action}";
    var flag = '${flag}';
    var bbsType = '${bbsType}';
    var picName = '';
    var nodeVarsStr = '${nodeVars}';
    var paperId = "${applyObj.id}";
    var pageSize = 5;
    var currentIndex = 0;
    var discussNum = ${applyObj.discussNum};
    KindEditor.ready(function(K) {
        editor = K.create('#editor', {
            //这里是指定的文件上传input的的属性名
            filePostName: "uploadFile",
            uploadJson: jsUseCtxPath+"/kindeditor/upload",
            resizeType: 1,
            allowPreviewEmoticons: true,
            allowImageUpload: true,
            afterUpload: function(url, data, name) {
                picName += url.split('=')[1]+",";
            }
        });
        if(action!='edit'){
            noticeForm.setData(applyObj);
            setContent(applyObj.content);
        }else{
            noticeForm.setData(applyObj);
            setContent(applyObj.content);
            $('#commentDiv').hide();
        }
        if(action == 'task'){
            if (editForm != '1' && !isFirstNode) {
                setDisabled();
            }
        }
        if(action == 'detail'){
            setDisabled();
        }
    });
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
</script>
</body>
</html>
