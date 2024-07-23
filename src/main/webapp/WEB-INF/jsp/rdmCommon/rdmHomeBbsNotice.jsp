<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
    <style>
        .lui_dataview_classic_new {
            background:url(${ctxPath}/styles/images/list_new.png) no-repeat;
            display: inline-block;
            width: 23px;
            height: 11px;
            margin-top: 3px;
            margin-left: 10px;
        }
    </style>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <div id="messageDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;" maskOnLoad="false"
         allowResize="false" showHGridLines="false"showVGridLines="false"  autoLoad="true" onload="dataGridLoad"
         idField="id" allowAlternating="true" showPager="true" multiSelect="false" showColumns="false"
         sizeList="[8,15,50,100]" pageSize="8"
         url="${ctxPath}/rdmHome/core/rdmHomeTabContent.do?name=bbsNotice">
        <div property="columns">
            <div field="title" headerAlign='center' width="200" align='left' renderer="onMessageTitleRenderer">标题</div>
            <div field="plateName" headerAlign='center' width="100" align='center' >板块</div>
            <div field="publisher" width="50" headerAlign='center' align="center">发布人</div>
            <div field="CREATE_TIME_" width="80" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">发布时间</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var grid=mini.get("messageDatagrid");

    function dataGridLoad(e) {
        window.parent.queryTabNums();
    }
    //各模块的消息宣贯直接返回需要跳转的url
    function onMessageTitleRenderer(e) {
        var record = e.record;
        var title=record.title;
        var result='<a href="#" style="color:#0044BB;" onclick="seeMessage(\'' + record.mainId+ '\')">'+title+'</a>';
        if(record.isRead){
            if(record.isRead=='0'){
                result = '<img style="margin-bottom:-4px" src="${ctxPath}/styles/images/standard/noticeUnRead.png" />'+result
            }
        }
        return result;
    }
    //各个跳转的url后台自己处理消息为已读
    function seeMessage(id) {
        var url = jsUseCtxPath + "/rdmZhgl/core/bbs/viewPage.do?id="+id+"&readStatus=1";
        var winObj = openNewWindow(url,"帖子详情");
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                if(grid){
                    grid.load();
                }
            }
        }, 1000);
    }
</script>
</body>
</html>
