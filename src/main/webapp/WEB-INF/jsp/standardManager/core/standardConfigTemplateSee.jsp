<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>标准模板查看</title>
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
            <br>
            <table height="27" style="font-size: 18px" cellSpacing="0" borderColorDark="#ffffff" cellPadding="0" width="80%" borderColorLight="#000000" border="0" align=center >
                <tr>
                    <td width="10%" valign="top"><b>文件说明：</b></td>
                    <td width="90%" id="description"></td>
                </tr>
                <tr>
                    <td width="10%"><b>文件大小：</b></td>
                    <td width="90%" id="size"></td>
                </tr>
                <tr>
                    <td width="10%"><b>文件下载：</b></td>
                    <td width="90%"><a href="#" onclick="downloadTemplate()">点击下载文件</a></td>
                </tr>
            </table>
        </div>
    </div>

</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var templateObj=${templateObj};
    setData();

    function setData() {
        if(templateObj) {
            var fileName=templateObj.templateName;
            if(fileName&&fileName.lastIndexOf('.')!=-1) {
               fileName=fileName.slice(0,fileName.lastIndexOf('.'));
            }
            $("#title").html(fileName);
            var author="发布时间："+templateObj.CREATE_TIME_+" 编辑："+templateObj.creatorDepFullName+" "+templateObj.creator+" 下载次数："+templateObj.downloadNum;
            $("#author").html(author);
            $("#description").html(templateObj.description);
            $("#size").html(templateObj.size);
        }
    }

    function downloadTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/standardManager/core/standardConfig/templateDownload.do");
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", templateObj.templateName);

        var inputRelativeFilePath = $("<input>");
        inputRelativeFilePath.attr("type", "hidden");
        inputRelativeFilePath.attr("name", "relativeFilePath");
        inputRelativeFilePath.attr("value", templateObj.relativePath);


        var idAttr= $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", templateObj.id);

        $("body").append(form);
        form.append(inputFileName);
        form.append(inputRelativeFilePath);
        form.append(idAttr);
        form.submit();
        form.remove();
    }
</script>
</body>
</html>
