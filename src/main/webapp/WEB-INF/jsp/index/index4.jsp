<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${appName}</title>
    <%@include file="/commons/index.jsp" %>
    <link type="text/css" rel="stylesheet" href="${ctxPath}/scripts/index/index4/manage3.css"/>
    <link type="text/css" rel="stylesheet" href="${ctxPath}/scripts/index/index4/index4.css?static_res_version=${static_res_version}"/>
    <link href="${ctxPath}/styles/commons.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <link href="${ctxPath}/styles/commonsindex4.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="${ctxPath}/scripts/index/index4/manage.js?static_res_version=${static_res_version}"></script>
    <style type="text/css">
        .msgnum {
            color: white;
            font-size: 13px !important;
            background-color: red;
            /*			min-width: 17px;
                        min-height: 17px;
                        line-height: 17px;*/
            top: -10px;
            border-radius: 24px;
            text-align: center;
            margin-left: 4px;
        }

        .mini-buttonedit {
            height: 30px;
        }

        .mini-buttonedit-border {
            height: 28px;
        }

        .mini-buttonedit-input {
            height: 28px;
            line-height: 26px;
        }

        .mini-buttonedit-button {
            height: 25px;
        }

        .mini-buttonedit-icon {

            width: 28px;
            height: 25px;
        }
    </style>
</head>
<body>
<%--新标签页打开右键菜单--%>
<ul id="dimNodeMenu" class="mini-contextmenu">
    <li onclick="newTabOpen()">新标签页打开</li>
</ul>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setBundle basename="message.i18n"/>
<fmt:setLocale value="zh_CN"/>
<%--主布局--%>
<div id="mainLayout" class="mini-layout top_one" style="width: 100%; height: 100%;">
    <%--左侧区域--%>
    <div showHeader="false" region="west" showSplit="true" class="nav" width="225"
         showSplitIcon="true" showCollapseButton="true" style="border-top: 0px;">
        <div class="logoBoxs" style="text-align:center">
            <em class="iconLogo"></em>
            <p id="logoTitle">
                <span>${appName}</span>
            </p>
        </div>
        <div class="mini-fit">
            <ul class="sidemenu" id="sidemenu"></ul>
        </div>
    </div>
    <%--中间区域--%>
    <div title="center" region="center" showSplitIcon="false" showCollapseButton="false" showTreeIcon="true">
        <div class="mini-layout" style="width: 100%;height: 100%">
            <%--顶部菜单条，如果注掉整个顶部操作区域都没有了--%>
            <div class="topBar" region="north" height="50" showHeader="false" style="border: 0px"
                 allowResize="false" showSplit="false" showSplitIcon="false"
                 showProxy="false" splitSize="0">
                <%--顶部盒子，如果注掉只有顶部菜单条一个空旷的区域--%>
                <div class="headerBox" id="headerBox">
                    <%--无用--%>
                    <div id="toggleBtn">
                        <span></span>
                    </div>
                    <%--顶部盒子内部的子系统导航条，如果注掉就没有子系统导航了--%>
                    <ul id="headerNav">
                        <c:forEach items="${menus}" var="menu" varStatus="i">
                            <li
                                    <c:if test="${i.count== actSysIndex}">class="act"</c:if> type="${menu.type }"
                                    iconCls="${menu.iconCls }" showType="${menu.showType }"
                                    key="${menu.key}"
                                    menuId="${menu.menuId}" name="${menu.name}" nav_url="${menu.url }"
                                    windowOpen="${menu.windowOpen }"
                            >
                                <i class="iconfont ${menu.iconCls }"></i>
                                <spring:message code="subsys.${menu.key}" text="${menu.name}"/>
                            </li>
                        </c:forEach>
                    </ul>
                    <%--顶部盒子内部的右侧便捷操作条，如果注掉就没有便捷操作条了--%>
                    <ul id="headerToolbar">
                        <%--便捷操作条里面的功能快捷搜索--%>
                        <li style="margin-right: 20px;margin-bottom: 5px" id="searchMenuBtn">
                            <input id="menuSearch" class="mini-autocomplete searchbox" emptyText="<spring:message code="page.index.searchFun"/>"
                                   onitemclick="menuClick()" onenter="menuClick()"
                                   url="${ctxPath}/rdmHome/core/menuSearch.do" popupEmptyText="无结果" loadingText="加载中..."
                                   valueField="menuId" textField="menuName" showPopupOnClick="true" onclick="searchMyMenuRecent()"/>
                            <i id="searchBtn" title="功能搜索"></i>
                        </li>
                        <%--便捷操作条里面的退出按钮--%>
                        <li class="_top2 quit" style="margin-top: 5px" onclick="loginout()">
                            <i class="iconfont icon-tuichu3" style="vertical-align: middle;color: #f7794d;" title="退出"></i>
                        </li>
                        <%--便捷操作条里面的个人信息--%>
                        <li class="user" style="margin-top: 5px" onclick="editInfo()">
                            <span style="vertical-align: middle">
							    ${curUser.fullname}
                                <c:if test="${not empty curDep }">&nbsp;[${curDep.name}]</c:if>
                            </span>
                            <i id="userMessage" title="个人信息"></i>
                        </li>
                    </ul>
                </div>
            </div>
            <%--中部页面加载--%>
            <div title="center" region="center" showSplitIcon="false" showCollapseButton="false" showTreeIcon="true">
                <div class="mini-fit">
                    <div id="mainTab" class="mini-tabs" activeIndex="0" arrowPosition="side" showNavMenu="true"
                         style="width: 100%!important; height: 100%; border: 0px;" plain="false" onactivechanged="onTabsActiveChanged"
                         bodyCls="width:100%;">
                        <%--<div title="首页"  url="${ctxPath}/oa/info/insPortalDef/home.do" ></div>--%>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%--动态布局，主要是生成菜单，会填充到左侧区域的sidemenu下面--%>
<script type="text/html" id="leftMenuTemplate">
    <#for(var i=0;i
    <list.length ;i++){
            var menu=list[i];

            #>
    <li class="firstmenu">
        <p menuId="<#=menu.menuId#>" name="<#=menu.name#>" url="<#=menu.url#>" showType="<#=menu.showType#>" iconCls="<#=menu.iconCls#>"
           class="menu base" onclick="clickCount('<#=menu.menuId#>','<#=menu.name#>','<#=menu.url#>','<#=menu.sysName#>')">
            <i class="<#=menu.iconCls#>"></i>
            <span><#=menu.displayName#><#if(menu.msgNum>0){#><span class="msgnum"><#=menu.msgNum#></span><#}#></span>
            <#if(menu.children){#>
			<span class="spread">
					<span class="horizontal"></span><span class="vertical"></span>
				</span>
                <#}#>
        </p>
        <#
                if(!menu.children) continue;
                for(var k=0;k
        <
        menu.children.length ;k++){
        var menu2=menu.children[k];
        #>
        <ul class="secondmenu">
            <li class="secondmenuli">
                <p iconCls="<#=menu2.iconCls#>" showType="<#=menu2.showType#>" menuId="<#=menu2.menuId#>" name="<#=menu2.name#>" url="<#=menu2.url#>"
                   onclick="clickCount('<#=menu2.menuId#>','<#=menu2.name#>','<#=menu2.url#>','<#=menu.sysName#>')">
                    <i class="iconfont <#=menu2.iconCls#>"></i>
                    <span><#=menu2.displayName#><#if(menu2.msgNum>0){#><span class="msgnum"><#=menu2.msgNum#></span><#}#></span>
                    <#if(menu2.children){#>
					<span class="spread">
								<span class="horizontal"></span><span class="vertical"></span>
							</span>
                        <#}#>
                </p>
                <#if(menu2.children){#>
                    <ul class="threemenu">
                        <#for(var m=0;m
                        <
                        menu2.children.length ;m++){
                        var menu3=menu2.children[m];
                        #>
                        <li class="threemenuli" menuId="<#=menu3.menuId#>" iconCls="<#=menu3.iconCls#>" name="<#=menu3.name#>" url="<#=menu3.url#>"
                            showType="<#=menu.showType#>">
                            <p showType="<#=menu3.showType#>" iconCls="<#=menu3.iconCls#>" menuId="<#=menu3.menuId#>" name="<#=menu3.name#>"
                               url="<#=menu3.url#>" onclick="clickCount('<#=menu3.menuId#>','<#=menu3.name#>','<#=menu3.url#>','<#=menu.sysName#>')">
                                <i class="iconfont <#=menu3.iconCls#>"></i>
                                <span><#=menu3.displayName#><#if(menu3.msgNum>0){#><span class="msgnum"><#=menu3.msgNum#></span><#}#></span>
                                <#if(menu3.children){#>
							<span class="spread">
												<span class="horizontal"></span><span class="vertical"></span>
											</span>
                                    <#}#>
                            </p>

                            <#if(menu3.children){
                                    var menusfour=menu3.children;
                                    #>

                                <ul class="fourmenu">
                                    <#for(var n=0;n
                                    <
                                    menusfour.length ;n++){
                                    var menu4=menusfour[n];
                                    #>
                                    <li class="fourmenuli" menuId="<#=menu4.menuId#>" name="<#=menu4.name#>" iconCls="<#=menu.iconCls#>"
                                        iconCls="<#=menu4.iconCls#>" url="<#=menu4.url#>" showType="<#=menu.showType#>">
                                        <p iconCls="<#=menu4.iconCls#>" showType="<#=menu.showType#>" menuId="<#=menu4.menuId#>"
                                           name="<#=menu4.name#>" url="<#=menu4.url#>"
                                           onclick="clickCount('<#=menu4.menuId#>','<#=menu4.name#>','<#=menu4.url#>','<#=menu.sysName#>')">
                                            <i class="<#=menu4.iconCls#>"></i>
                                            <span><#=menu4.displayName#></span>
                                            <#if(menu4.children){#>
									<span class="spread">
														<span class="horizontal"></span><span class="vertical"></span>
													</span>
                                                <#}#>
                                        </p>
                                    </li>
                                    <#}#>
                                </ul>

                                <#}#>
                        </li>
                        <#}#>
                    </ul>
                    <#}#>
            </li>
        </ul>
        <#}#>
    </li>
    <#}#>
</script>
<script>
    mini.parse();
    var systemMsg =${noReadMsgs};
    var cxMsgArray =${cxMsgArray};
    var gzdmMsgArray =${gzdmMsgArray};
    var txxyMsgArray =${txxyMsgArray};
    var kzxtMsgArray =${kzxtMsgArray};
    var kzccMsgArray =${kzccMsgArray};
    var standardMsgList = ${standardMsgList};
    var userId = "${userId}";
    var webappName = "${webappName}";
    var jsUseCtxPath = "${ctxPath}";
    var leaderScheduleNotice = ${leaderScheduleNotice};
    var ydgztbRemind = ${ydgztbRemind};
    var weakPwd = "${weakPwd}";
    var developOrProduce = "${developOrProduce}";
    var jumpClickMenuId = "${clickMenuId}";

    var contextMenuObj;


    /*---------头部导航适应---------*/
    function loginout() {
        mini.confirm("确定退出登录", "确定？",
            function (action) {
                if (action != "ok") return;
                location.href = '${ctxPath}/logout';
            }
        );
    }

    function clickCount(menuId, menuName, menuUrl, sysName) {
        if (menuUrl && webappName == 'rdm' && (sysName != '首页' && sysName != '组织架构' && sysName != '系统配置' && sysName != '门户首页' && sysName != '开发配置' )) {
            var formData = {menuId: menuId, menuName: menuName}
            $.ajax({
                url: jsUseCtxPath + '/digitization/core/Szh/clickCount.do',
                type: 'post',
                async: false,
                data: mini.encode(formData),
                contentType: 'application/json',
                success: function (data) {
                }
            });
        }
    }

    function menuClick() {
        var menuIdSysId = mini.get("menuSearch").getValue();
        var temp = menuIdSysId.split("_");
        var menuId = temp[0];
        var sysId = temp[1];
        clickSys(sysId, menuId);
        //@lwgkiller记录最近访问的菜单
        $.ajax({
            url: jsUseCtxPath + '/rdmHome/core/addMyMenuRecent.do?menuIdSysId=' + menuIdSysId,
            type: 'post',
            async: false,
            contentType: 'application/json',
            success: function (data) {
            }
        });
    }

    $(function () {
        if (webappName != 'rdm') {
            $("#searchMenuBtn").hide();
        }

        $(window).resize(function () {
            setTimeout(function () {
                headerNav();
            }, 500);
        });

        headerNav();
        //如果为弱密码，弹框进行修改
        if (weakPwd == "yes" && "develop" != developOrProduce) {
            mini.open({
                title: "密码修改提醒",
                url: jsUseCtxPath + "/weakPwdReset.do?userId=" + userId,
                width: 800,
                height: 300,
                showModal: true,
                allowResize: true,
                showCloseButton: false,
                onload: function () {
                }
            });
        }

        //弹出平台消息
        if (systemMsg) {
            $.each(systemMsg, function (key, value) {
                var msgBoxObj = {
                    width: 700,
                    title: value.title,
                    buttons: ["ok"],
                    html: '<div style="padding: 0px;">' + value.content + '</div>',
                    callback: function (action) {
                        $.ajax({
                            url: '${ctxPath}/xcmgProjectManager/core/message/setToRead.do?messageId=' + value.id,
                            type: 'get',
                            contentType: 'application/json',
                            success: function (data) {
                                //此次如果不刷新，则办公桌面的消息不好处理未读件
                                window.location.reload();
                            }
                        });
                    }
                };
                mini.showMessageBox(msgBoxObj);
            });
        }
        //弹出标准宣贯消息
        if (standardMsgList) {
            $.each(standardMsgList, function (key, value) {
                var msgBoxObj = {
                    showModal: false,
                    title: '<div style="font-size:10px"><b>标准宣贯</b></div>',
                    buttons: ["查看<div style='display: none'>C-" + value.messageId + "," + value.relatedStandardId + "-C</div>"],
                    width: 450,
                    showCloseButton: true,
                    message: '<div style="font-size:15px;">' + value.content + '</div>',
                    callback: function (action) {
                        if (action != 'close') {
                            var idStr = action.substring(action.indexOf("C-") + 2, action.indexOf("-C"));
                            var idArr = idStr.split(",");
                            seeMessage(idArr[0], idArr[1]);
                        }
                    },
                    x: 'right',
                    y: 'bottom'
                };
                mini.showMessageBox(msgBoxObj);
            });
        }
        if (cxMsgArray) {
            $.each(cxMsgArray, function (key, value) {
                var msgBoxObj = {
                    showModal: false,
                    title: '<div style="font-size:10px"><b>程序发布通知</b></div>',
                    buttons: ["查看<div style='display: none'>C-" + value.cxResId + "," + value.msgId + "-C</div>"],
                    width: 450,
                    showCloseButton: true,
                    message: '<div style="font-size:15px;">' + '程序名称' + value.cxName + ',设计型号' + value.designTypeName + ',' + value.content + '</div>',
                    callback: function (action) {
                        if (action != 'close') {
                            var idStr = action.substring(action.indexOf("C-") + 2, action.indexOf("-C"));
                            var idArr = idStr.split(",");
                            seeCxMsg(idArr[0], idArr[1]);
                        }
                    },
                    x: 'right',
                    y: 'bottom'
                };
                mini.showMessageBox(msgBoxObj);
            });
        }
        /*if(gzdmMsgArray) {
         $.each(gzdmMsgArray,function(key,value){
         var msgBoxObj={
         showModal:false,
         title: '<div style="font-size:10px"><b>故障代码新增</b></div>',
         buttons: ["查看<div style='display: none'>C-"+value.dmgzId+","+value.msgId+"-C</div>"],
         width:450,
         showCloseButton:true,
         message: '<div style="font-size:15px;">'+value.content+'</div>',
         callback: function (action) {
         if(action!='close') {
         var idStr=action.substring(action.indexOf("C-")+2,action.indexOf("-C"));
         var idArr=idStr.split(",");
         seeDmgzMsg(idArr[0],idArr[1]);
         }
         },
         x:'right',
         y:'bottom'
         };
         mini.showMessageBox(msgBoxObj);
         });
         }*/
        if (txxyMsgArray) {
            $.each(txxyMsgArray, function (key, value) {
                var msgBoxObj = {
                    showModal: false,
                    title: '<div style="font-size:10px"><b>通信协议新增</b></div>',
                    buttons: ["查看<div style='display: none'>C-" + value.txxyId + "," + value.msgId + "-C</div>"],
                    width: 450,
                    showCloseButton: true,
                    message: '<div style="font-size:15px;">' + value.content + '</div>',
                    callback: function (action) {
                        if (action != 'close') {
                            var idStr = action.substring(action.indexOf("C-") + 2, action.indexOf("-C"));
                            var idArr = idStr.split(",");
                            seeTxxyMsg(idArr[0], idArr[1]);
                        }
                    },
                    x: 'right',
                    y: 'bottom'
                };
                mini.showMessageBox(msgBoxObj);
            });
        }

        if (kzxtMsgArray) {
            $.each(kzxtMsgArray, function (key, value) {
                var msgBoxObj = {
                    showModal: false,
                    title: '<div style="font-size:10px"><b>控制系统功能开发</b></div>',
                    buttons: ["查看<div style='display: none'>C-" + value.kzxtId + "," + value.msgId + "-C</div>"],
                    width: 450,
                    showCloseButton: true,
                    message: '<div style="font-size:15px;">' + value.content + '</div>',
                    callback: function (action) {
                        if (action != 'close') {
                            var idStr = action.substring(action.indexOf("C-") + 2, action.indexOf("-C"));
                            var idArr = idStr.split(",");
                            seeKzxtMsg(idArr[0], idArr[1]);
                        }
                    },
                    x: 'right',
                    y: 'bottom'
                };
                mini.showMessageBox(msgBoxObj);
            });
        }

        if (kzccMsgArray) {
            $.each(kzccMsgArray, function (key, value) {
                var msgBoxObj = {
                    showModal: false,
                    title: '<div style="font-size:10px"><b>控制器存储区管理</b></div>',
                    buttons: ["查看<div style='display: none'>C-" + value.storageAreaId + "," + value.msgId + "-C</div>"],
                    width: 450,
                    showCloseButton: true,
                    message: '<div style="font-size:15px;">' + value.content + '</div>',
                    callback: function (action) {
                        if (action != 'close') {
                            var idStr = action.substring(action.indexOf("C-") + 2, action.indexOf("-C"));
                            var idArr = idStr.split(",");
                            seeKzccMsg(idArr[0], idArr[1]);
                        }
                    },
                    x: 'right',
                    y: 'bottom'
                };
                mini.showMessageBox(msgBoxObj);
            });
        }

        if (leaderScheduleNotice && leaderScheduleNotice.title) {
            var msgBoxObj = {
                width: 700,
                title: leaderScheduleNotice.title,
                buttons: ["ok"],
                html: '<div style="padding: 0px;font-size:14px">' + leaderScheduleNotice.content + '</div>',
                callback: function (action) {
                    $.ajax({
                        url: '${ctxPath}/rdmHome/core/readScheduleNotice.do',
                        type: 'get',
                        contentType: 'application/json',
                        success: function (data) {
                            //此次如果不刷新，则办公桌面的消息不好处理未读件
                            window.location.reload();
                        }
                    });
                }
            };
            mini.showMessageBox(msgBoxObj);
        }

        if (ydgztbRemind && ydgztbRemind.content) {
            var msgBoxObj = {
                showModal: false,
                width: 700,
                title: "部门月度工作提报优秀案例评选提醒",
                buttons: ["前往评选", "不再提醒"],
                showCloseButton: false,
                message: '<div style="font-size:15px;">' + ydgztbRemind.content + '</div>',
                callback: function (action) {
                    if (action == '前往评选') {
                        //    跳转到列表页面进行选择
                        selectYdgztb();
                    } else if (action == '不再提醒') {
                        //    取消本月提醒，记录下来
                        cancelRemindYdgztb();
                    }
                },
                x: 'right',
                y: 'bottom'
            };
            mini.showMessageBox(msgBoxObj);
        }
    });

    function selectYdgztb() {
        var url = "${ctxPath}/zhgl/core/ydgztb/applyListPage.do?scene=indexPage";
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }

    function cancelRemindYdgztb() {
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/ydgztb/cancelRemind.do',
            type: 'post',
            async: true,
            contentType: 'application/json',
            success: function (data) {
            }
        });
    }

    function seeMessage(id, relatedStandardId) {
        var url = "${ctxPath}/standardManager/core/standardMessage/see.do?id=" + id + "&standardId=" + relatedStandardId;
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }
    function seeCxMsg(cxResId, msgId) {
        var action = 'detail';
        var pageUrl = "/embedsoft/core/cxRes/editPage.do?action=" + action + "&cxResId=" + cxResId
            + "&msgId=" + msgId + "&read=yes";
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey=" + subsys_soft_key + "&targetUrl=" + encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }
    function seeDmgzMsg(dmgzId, msgId) {
        var action = 'detail';
        var pageUrl = "/rdm/core/Dmgz/editPage.do?&action=" + action + "&dmgzId=" + dmgzId
            + "&msgId=" + msgId + "&read=yes";
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey=" + subsys_soft_key + "&targetUrl=" + encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }
    function seeTxxyMsg(txxyId, msgId) {
        var action = 'detail';
        var pageUrl = "/embedsoft/core/txxyAdd/applyEditPage.do?action=" + action + "&applyId=" + txxyId
            + "&msgId=" + msgId + "&read=yes";
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey=" + subsys_soft_key + "&targetUrl=" + encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }

    function seeKzxtMsg(kzxtId, msgId) {
        var action = 'detail';
        var pageUrl = "/embedsoft/core/kzxtgnkf/applyEditPage.do?action=" + action + "&applyId=" + kzxtId
            + "&msgId=" + msgId + "&read=yes";
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey=" + subsys_soft_key + "&targetUrl=" + encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }

    function seeKzccMsg(storageAreaId, msgId) {
        var action = 'detail';
        var pageUrl = "/embedsoft/core/storageArea/applyEditPage.do?action=" + action + "&applyId=" + storageAreaId
            + "&msgId=" + msgId + "&read=yes";
        var url = "${ctxPath}/pageJumpRedirect.do?targetSubSysKey=" + subsys_soft_key + "&targetUrl=" + encodeURIComponent(pageUrl);
        var winObj = openNewWindow(url, "");
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                window.location.reload();
            }
        }, 1000);
    }

    function headerNav() {
        var _liAll = $("#headerNav li[id != 'navMore']");
        var arryAll = [];
        for (var k = 0; k < _liAll.length; k++) {
            arryAll.push(_liAll.eq(k));
        }
        $("#headerNav").empty();
        $.each(arryAll, function (s, val) {
            $("#headerNav").append(val);
        })
        var headerBox_width = $("#headerBox").width();
        var toggleBtn_width = $("#toggleBtn").width();
        var headerToolbar_width = $("#headerToolbar").innerWidth();
        var max_headerNav_width = headerBox_width - headerToolbar_width - toggleBtn_width - 15;
        $("#headerNav").width(max_headerNav_width);
        var _width1 = 0;
        var _li = $("#headerNav li");
        for (var i = 0; i < _li.length; i++) {
            _width1 = _width1 + _li.eq(i).outerWidth() + 12;
        }
        if (_width1 < max_headerNav_width) {
            return;
        } else {
            var _width2 = 0;
            var arry1 = [];
            var arry2 = [];
            for (var k = 0; k < _li.length; k++) {
                _width2 = _width2 + _li.eq(k).innerWidth() + 12;
                if (_width2 + 90 > max_headerNav_width) {
                    arry2.push(_li.eq(k));
                } else {
                    arry1.push(_li.eq(k));
                }
            }
            var html_more = $('<li id="navMore">' +
                '<spring:message code="page.index.more" />&nbsp;<i class="iconfont icon-shouqi3"></i>' +
                '<div class="navMoreBoxs">' +
                '<ul class="moreSelect" id="moreSelect" style="height:350px;overflow:auto;">' +
                '</ul>' +
                '</div>' +
                '</li>')
            $("#headerNav").empty();
            $("#headerNav").append(arry1);
            $("#headerNav").append(html_more);
            $("#moreSelect").append(arry2)
        }
    }

    function newTabOpen() {
        if (contextMenuObj) {
            var node = {
                menuId: contextMenuObj.attr('menuId'),
                showType: 'NEW_WIN',
                title: contextMenuObj.attr('name'),
                iconCls: '',
                url: contextMenuObj.attr('url')
            };
            showTabPage(node);
        }
    }

    //..@lwgkiller激发获取最近访问的事件
    function searchMyMenuRecent() {
        var menuSearch = mini.get("menuSearch");
        var text = menuSearch.getText();
        ;
        if (text == "") {
            $.ajax({
                url: jsUseCtxPath + '/rdmHome/core/searchMyMenuRecent.do?clickUserId=' + userId,
                type: 'post',
                async: true,
                contentType: 'application/json',
                success: function (data) {
                    menuSearch.setData(data);
                    console.log(data);
                }
            });
        }
    }
</script>
</body>
</html>
