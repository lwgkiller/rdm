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
    <%--<div class="mini-toolbar" style="padding:0px;border-bottom:0;">
        <table style="width:100%;">
            <tr>
                <td style="width:100%;">
                    <a href="#" style="float:right">更多</a>
                </td>
            </tr>
        </table>
    </div>--%>
    <div id="messageDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;" maskOnLoad="false"
         allowResize="false" showHGridLines="false"showVGridLines="false"  autoLoad="true" onload="dataGridLoad"
         idField="id" allowAlternating="true" showPager="true" multiSelect="false" showColumns="false"
         sizeList="[8,15,50,100]" pageSize="8"
         url="${ctxPath}/rdmHome/core/rdmHomeTabContent.do?name=message">
        <div property="columns">
            <div field="title" headerAlign='center' width="430" align='left' renderer="onMessageTitleRenderer">标题</div>
            <div field="creator" width="50" headerAlign='center' align="right">发布人</div>
            <div field="CREATE_TIME_" width="90" dateFormat="yyyy-MM-dd" headerAlign='center' align="right">发布时间</div>
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
        var typeName=record.typeName;
        if(typeName) {
            title='【'+typeName+'】'+title;
        }
        if(typeName=="标准关联"){
            var result='<a href="#" style="color:#0044BB;" onclick="linkMessage(\'' + record.url+ '\')">'+title+'</a>';
        }else {
            var targetSubSysKey=record.targetSubSysKey;
            if (!targetSubSysKey) {
                targetSubSysKey='';
            }
            var result='<a href="#" style="color:#0044BB;" onclick="seeMessage(\'' + record.url+'\',\''+targetSubSysKey+ '\')">'+title+'</a>';
        }
        var CREATE_TIME_=record.CREATE_TIME_;
        var oneWeekBeforeNow=new Date();
        oneWeekBeforeNow.setDate(new Date().getDate()-7);
        var createDate=new Date(CREATE_TIME_);
        if(createDate>oneWeekBeforeNow) {
            result+='<span class="lui_dataview_classic_new"></span>';
        }
        if(record.isRead){
            if(record.isRead=='0'){
                result = '<img style="margin-bottom:-4px" src="${ctxPath}/styles/images/standard/noticeUnRead.png" />'+result
            }
        }
        return result;
    }

    //各个跳转的url后台自己处理消息为已读
    function seeMessage(pageUrl,targetSubSysKey) {
        var url = jsUseCtxPath+"/pageJumpRedirect.do?targetSubSysKey="+targetSubSysKey+"&targetUrl="+encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url,"seeMessage");
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                if(grid){
                    grid.load();
                }
            }
        }, 1000);
    }

    function linkMessage(url) {
        mini.open({
            title: "编辑列表",
            url: jsUseCtxPath+url,
            width: 1000,
            height: 700,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                if(grid){
                    grid.load();
                }
            }
        });

    }
</script>
</body>
</html>
