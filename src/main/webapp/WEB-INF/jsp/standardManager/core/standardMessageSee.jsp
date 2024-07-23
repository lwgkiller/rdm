<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>标准通知查看</title>
    <%@include file="/commons/edit.jsp" %>
    <style>
        body,div,h2,span,p {padding:0;margin:0;color:#000;font-family:"宋体";}
        .new_body {margin:0 80px 0 80px;min-height:500px;height:99%;padding-bottom:5px;border:1px #ccc solid;background-color:#ecf5ff;}
        .new_body h2 {margin:20px 0 10px;height:30px;position:relative;line-height:30px;font-size:25px;font-family:"宋体";font-weight:bold;text-align:center;overflow:hidden;}
        .new_body div {margin:0 25px;border-top:2px #ccc ;}
        .new_body div span{display:block;height:15px;line-height:20px;font-size:15px;text-align:center;padding-top:5px;border-top:1px red solid;}
        .new_body p {line-height:25px;margin-top:10px;text-indent:2em;font-size:20px;}
    </style>
</head>
<body>
<div class="mini-panel"  style="width: 100%; height: 100%" showfooter="false" bodystyle="padding:0"
     showheader="false">
    <div class="new_body">
        <h2 id="title">
        </h2>
        <div>
            <span id="author"></span>
            <p id="content"></p>
        </div>
    </div>

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var messageObj=${messageObj};
    var standardObj=${standardObj};

    setData();

    function setData() {
        if(messageObj) {
            if(messageObj.standardMsgTitle) {
                $("#title").html(messageObj.standardMsgTitle);
            }
            var author="发布时间："+messageObj.CREATE_TIME_+" 编辑："+messageObj.creatorDepFullName+" "+messageObj.creator;
            $("#author").html(author);
            var finalContent=standardLink();
            if(messageObj.standardMsgContent) {
                finalContent+="&nbsp;&nbsp;&nbsp;&nbsp;"+messageObj.standardMsgContent
            }
            $("#content").html(finalContent);
        }
    }

    function standardLink() {
        if(standardObj && standardObj.id) {
            var url=jsUseCtxPath + "/standardManager/core/standard/management.do?1=1";
            if(standardObj.isPublicScene=='yes') {
                url=jsUseCtxPath +"/standardManager/core/standard/publicListPage.do?1=1";
            }
            if(standardObj.standardNumber) {
                url+="&standardNumber="+standardObj.standardNumber;
            }
            if(standardObj.standardName) {
                url+="&standardName="+standardObj.standardName;
            }
            if(standardObj.standardCategoryId) {
                url+="&standardCategory="+standardObj.standardCategoryId;
            }
            if(standardObj.systemCategoryId) {
                url+="&systemCategory="+standardObj.systemCategoryId;
            }
            if(standardObj.companyName) {
                url+="&companyName="+standardObj.companyName;
            }
            var linkStr='<a href="#" style="color:#0044BB;" onclick="window.open(\'' + url+ '\')">查看相关标准</a>';
            return linkStr;
        }
        return '<a href="#" style="color:#0044BB;">查看相关标准</a>';
    }
</script>
</body>
</html>
