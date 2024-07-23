<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title></title>
    <%@include file="/commons/customForm.jsp"%>
    <script type="text/javascript" src="${ctxPath}/scripts/customer/imgShow.js"></script>

    <script type="text/javascript">

        function changeFrameHeight(){
            var ifm= document.getElementById("formFrame");
            ifm.height=document.body.clientHeight-90-$("#errorMsg").height();
        }

        window.onresize=function(){
            changeFrameHeight();
        }
    </script>
</head>
<body>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto;">
        <form id="processForm" style="height: 100%">
            <iframe src="${formUrl}" style="width:100%; height: 100%" id="formFrame" name="formFrame" frameborder="0" scrolling="no"  ></iframe>
        </form>
    </div>
</div>
</body>
</html>