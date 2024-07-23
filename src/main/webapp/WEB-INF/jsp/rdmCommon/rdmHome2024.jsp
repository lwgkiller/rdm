<%@ taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的桌面</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts.min.js"></script>
    <script type="text/javascript" src="${ctxPath }/scripts/sys/echarts/echarts-wordcloud.min.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/common/list.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/common/commonTools.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>

    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>

    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/common/baiduTemplate.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/customer/mini-custom.js" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/sys/echarts/echartsFrontCustom.js?t=1.5.137" type="text/javascript"></script>

    <script src="${ctxPath}/scripts/sys/echarts/roll.js" type="text/javascript"></script>
    <link href="${ctxPath}/scripts/sys/echarts/css/roll.css" rel="stylesheet" type="text/css"/>
    <style>
        .span-header {
            line-height: 38px;
            color: #e6e8ea !important;;
            font-size: 14px !important;
            float: left;
            clear: right;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }

        .item {
            float: left;
            width: 190px;
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
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-col="1" data-row="1" data-sizex="6" data-sizey="1">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">常用系统跳转</p>
                </header>
                <ol class="messageBoxs">
                    <li>
                        <a href="http://pdmapp.xcmg.com/Windchill/app/" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-Process-instance" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>PDM</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="${ctxPath}/xcmgTdm/core/requestapi/tdmSso.do?urlcut=工作区" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-debug-white" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>TDM</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="${ctxPath}/xcmgTdmii/core/requestapi/loginToTdmii.do" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-edit-in" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>新品试制</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.1.76.4:8080/ui/" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-home" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>SDM</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://xgssapp.xcmg.com/Windchill/app/" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-buchongiconsvg14" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>GSS</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.90.12.28:31699/#/login" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-group-dialog" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>物联网</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.15.32.65:2203/index/list.htm" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-zhanghaoguanli" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>QMS</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.15.10.133:8080/epm/index.do" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-kanchadanliebiao" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>EPM</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.15.0.10/" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-book-18" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>网盘</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a href="http://10.188.0.145/sys/portal/page.jsp" target="_blank">
                            <div class="icons">
                                <span class="iconfont icon-mailUp" style="color:#878ef2"></span>
                            </div>
                            <div class="contentBox">
                                <p>OA</p>
                            </div>
                        </a>
                    </li>
                </ol>
            </li>
            <li class="gs-w" data-row="2" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">
                        <spring:message code="page.home.todo"/>(<span id="todoNum" style="color: red;width: auto">0</span>)</p>
                </header>
                <div class="containerBox">
                    <div id="todo" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=todo">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="2" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">
                        <spring:message code="page.home.message"/>(<span id="messageNum" style="color: red;width: auto">0</span>)</p>
                </header>
                <div class="containerBox">
                    <div id="message" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=message">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="5" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header"><spring:message code="page.home.projectDelay"/></p>
                </header>
                <div class="containerBox">
                    <div id="projectDelay" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=projectDelay">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="5" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">
                        <spring:message code="page.home.notification"/>(<span id="noticeNum" style="color: red;width: auto">0</span>)</p>
                </header>
                <div class="containerBox">
                    <div id="notice" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=notice">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="8" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header"><spring:message code="page.home.done"/></p>
                </header>
                <div class="containerBox">
                    <div id="projectDelay" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=hasDone">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="8" data-col="4" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">
                        <spring:message code="page.home.forumMsg"/>(<span id="bbsNoticeNum" style="color: red;width: auto">0</span>)</p>
                </header>
                <div class="containerBox">
                    <div id="notice" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=bbsNotice">
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="10" data-col="1" data-sizex="3" data-sizey="3">
                <header class="deskHome-header-2024">
                    <p style="width: 400px" class="span-header">
                        项目交付物</p>
                </header>
                <div class="containerBox">
                    <div id="delivery" class="mini-panel" showHeader="false" style="width:100%;height:100%;"
                         refreshOnClick="true"
                         url="${ctxPath}/rdmHome/core/rdmHomeTabPage.do?name=delivery">
                    </div>
                </div>
            </li>
        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    overview();
    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }
    //..老页面迁移
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
