<%@ taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>首页</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>
    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <link href="${ctxPath}/styles/commons.css?version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/list.js?static_res_version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/commonTools.js?static_res_version=${static_res_version}" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/common/baiduTemplate.js?static_res_version=${static_res_version}"></script>
    <script src="${ctxPath}/scripts/index/rdmHome.js?static_res_version=${static_res_version}" type="text/javascript"></script>
    <style>
        .span-header {
            line-height: 38px;
            color: #201f35 !important;;
            font-size: 15px !important;
            float: left;
            clear: right;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }

        #quickSearchId .mini-textbox {
            height: 27px;
        }

        #quickSearchId .mini-textbox-border {
            height: 25px;
        }

        #quickSearchId .mini-textbox-input {
            height: 25px;
            line-height: 23px;
        }

        .item {
            float: left;
            /*            width:190px;*/
            height: 110px;
            border: solid 1px #ccc;
            border-radius: 4px;
            margin-left: 8px;
            margin-top: 8px;
        }

        .item-inner {
            width: 100%;
        }

        .item-image {

            height: 75px;
            width: 75px;
            margin: auto;
            margin-top: 5px;
            display: block;
            cursor: pointer;
        }

        .item-text {
            text-align: center;
            font-size: 10px;
            font-family: "微软雅黑";
            font-weight: bold;
            cursor: pointer;
        }

        .com_prompt_num {
            min-width: 12px;
            height: 12px;
            line-height: 12px;
            color: #fff;
            font-size: 10px;
            text-align: center;
            background-color: #d93e1a;
            padding: 2px 3px;
            margin-left: 5px;
            vertical-align: super;
            display: inline-block;
            -webkit-border-radius: 8px;
            -moz-border-radius: 8px;
            border-radius: 8px;
        }

        .mini-tab-text {
            line-height: 38px;
            font-size: 15px !important;
            float: left;
            clear: right;
            font-weight: bold;
        }

        /*        .mini-tabs .mini-tabs-space,.mini-tabs .mini-tabs-space2{
                    background-image: url("

        ${ctxPath}

                /styles/images/bg001.gif");
                        }*/
    </style>
</head>
<body>
<%--todo:mark一下：这是所有首页内容的容器--%>
<div class="personalPort">
    <div class="gridster">
        <ul id="rdmHomeContent">

        </ul>
    </div>
</div>
<%--todo:mark一下：这是首页中的其他九宫格菜单的容器，利用百度模板的动态模板语法，将每个子系统渲染成一个九宫格，插入动态块布局--%>
<script type="text/html" id="rdmHomeTemplate">
    <#
            for(var index=0;index
    < list.length ;index++) {
            var oneSys=list[index];
            var sysName=oneSys['name'];
            if(sysName=='首页' || sysName=='待办') {
            continue;
            }

            var sysId=oneSys['menuId'];
            var rowIndex=Math.ceil((index+1)/3.0);
            var colIndex=(index+1)%3;
            if(colIndex==0){
            colIndex=3;
            }
            var dataSizeX=1;
            var dataSizeY=1;
            #>
    <li class="gs-w" data-row="<#=rowIndex#>" data-col="<#=colIndex#>" data-sizex="<#=dataSizeX#>" data-sizey="<#=dataSizeY#>">
        <header class="deskHome-header">
            <p id="header<#=sysId#>" class="span-header">
                <#=oneSys['displayName']#>
            </p>
            <a onClick="sysMoreMenu('<#=sysId#>')" style="font-size: 14px;float: right;color:#006699;vertical-align: middle;margin-right: 10px"
               href="#"><spring:message code="page.index.more"/></a>
        </header>
        <div class="containerBox">
            <div class="mini-datagrid" id="<#=sysId#>" style="width: 100%; height: 100%;" allowResize="false" showHGridLines="false"
                 url="${ctxPath}/rdmHome/core/getMenusBySysId.do?sysId=<#=sysId#>" autoLoad="true" onload="gridProcess('<#=sysId#>')"
                 idField="id" allowAlternating="true" showPager="false" multiSelect="false" showColumns="false"
                 viewType="cardview" itemRenderer="itemRenderer">
                <div property="columns">
                </div>
            </div>
        </div>
    </li>
    <#}#>
</script>
<%--todo:mark一下：这是首页中的待办小窗口的容器，强制把它放在动态块布局第一行的最后侧--%>
<script type="text/html" id="rdmHomeTodoTemplate">
    <li class="gs-w" data-row="1" data-col="3" data-sizex="1" data-sizey="1">
        <div id="rdmHomeTab" class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
            <div title="<spring:message code="page.home.todo" /><span id='todoNum' class='com_prompt_num'>0</span>" name="todo" refreshOnClick="true"
                 url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=todo">
            </div>
            <div title="<spring:message code="page.home.message" /><span id='messageNum' class='com_prompt_num'>0</span>" name="message"
                 refreshOnClick="true" url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=message">
            </div>
            <div title="<spring:message code="page.home.notification" /><span id='noticeNum' class='com_prompt_num'>0</span>" name="notice"
                 refreshOnClick="true" url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=notice">
            </div>
            <div title="<spring:message code="page.home.projectDelay" />" name="hasDone" refreshOnClick="true"
                 url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=projectDelay">
            </div>
            <%--<div title="<spring:message code="page.home.leaderTime" />" name="workStatus" refreshOnClick="true"--%>
                 <%--url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=workStatus">--%>
            <%--</div>--%>
            <div title="<spring:message code="page.home.forumMsg" /><span id='bbsNoticeNum' class='com_prompt_num'>0</span>" name="bbsNotice"
                 refreshOnClick="true" url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=bbsNotice">
            </div>
            <div title='<spring:message code="page.home.done" />' name="hasDone" refreshOnClick="true"
                 url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=hasDone">
            </div>
        </div>
    </li>
</script>

<script type="text/javascript">
    //设置左分隔符为 <!
    baidu.template.LEFT_DELIMITER = '<#';
    //设置右分隔符为 <!
    baidu.template.RIGHT_DELIMITER = '#>';
    var bt = baidu.template;
    mini.parse();

    var jsUseCtxPath = "${ctxPath}";
    var subSysList =${subSysList};
    var data = {"list": subSysList};//..todo:mark一下：此处拿到子系统列表
    var homeContent = bt('rdmHomeTemplate', data);//..todo:mark一下：此处用子系统列表动态渲染出一个百度模板块，内容是个“动态块布局”
    var todoBlock = bt('rdmHomeTodoTemplate');//..todo:mark一下：这是一个手工绘制的静态动态块，强制指定在第1行第3列
    $("#rdmHomeContent").append(homeContent).append(todoBlock);//..todo:mark一下：依次加入布局，手工指定的动态块会自动挤占位置
    overview();

    function gridProcess(sysId) {
        var gridObj = mini.get(sysId);
        $(gridObj.el).on("click", function (event) {
            var record = gridObj.getRowByEvent(event);
            clickMenu(record)
        });
    }

    function queryTabNums() {
        $.ajax({
            url: jsUseCtxPath + "/rdmHome/core/rdmHomeTabNum.do?names=todo,message,notice,bbsNotice",
            success: function (data) {
                if (data) {
                    $("#todoNum").html(data.todo);
                    $("#messageNum").html(data.message);
                    $("#noticeNum").html(data.notice);
                    $("#bbsNoticeNum").html(data.bbsNoticeNum);
                }
            }
        });
    }

</script>
</body>
</html>
