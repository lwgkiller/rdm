//..todo:mark一下：这里有一些改动，主加了许多控制用的变量
var clickMenuId;
var lastClickSysName;
var currentClickSysName;
var lastMenuId;
var currentMenuId;
$(function () {
    clickMenuId = jumpClickMenuId;
    var winWidth = $(window).width();
    var winHeight = $(window).height();
    $('.side').height(winHeight - 130);

    $('.iframe').height(winHeight - 185);
    $('.content').height(winHeight - 130);
    $('.split').height(winHeight - 130);
    $('.content').width(winWidth - 241);

    mini.layout();
    //首页窗口变化 延时自适应大小；  ------yangxin;
    $(window).resize(function () {
        windowResizes()
    });

    // attachMenuEvents();
    $('.close').click(function () {
        $(this).parent('div').remove();
    });

    /*左右伸缩*/
    $(document).on("click", ".mini-tools-collapse", function () {
        windowResizes();
    });
   /* $(document).on("click",".mini-layout-spliticon",function(){
        windowResizes();
        mini.layout();
    });*/
   /* 点击首页顶部的折叠按钮 来触发 树的折叠事件 ---yangxin*/
    $("#toggleBtn").click(function () {
        console.log("***************toggleBtn clcik")
            $(this).toggleClass("btn_select");
            $("#west.nav  .mini-tools-collapse").trigger('click');
            mini.layout();
    });
   
   /*首页头部导航*/
    $("#headerNav").on("click","li",function (event) {
        var sysName=$(this).attr("name");
        var url = $(this).attr("url");
        var id = $(this).attr("id");
        var type = $(this).attr("type");
        var sysId = $(this).attr("menuId");
        var windowOpen = $(this).attr("windowOpen");
        var key=$(this).attr('key');
        if(!url)url="";
        var nav_url=$(this).attr('nav_url');
        //内部子系统
        if(type=="inner"){
            $(this).attr("url","");
            url="";

            if(id=="navMore")return;
            lastClickSysName=currentClickSysName;
            currentClickSysName=sysName;
            initMenu(sysId,sysName);
            $(this).siblings("li#navMore").find("li").removeClass("act");
            $(this).addClass("act").siblings().removeClass("act");
        } else if(type=="external"){
            if(nav_url){
                if (!clickMenuId) {
                    clickMenuId = '';
                }
                window.open(jsUseCtxPath+nav_url+"&clickMenuId="+clickMenuId,'_blank');
                clickMenuId='';
            }
        }
    });
})

function windowResizes() {
    var times = setInterval(function () {
        mini.layout();
        headerNav();
    }, 20);
    setTimeout(function () {
        window.clearInterval(times);
    }, 1000);
}

$(function () {
    if (self != top) top.location = self.location;
    top['index'] = window;
    var count = '${newMsgCount}', winW, logoW, userW;
    winW = $(document).innerWidth();
    userW = $('#fr').innerWidth();
    window.onload = function () {
        logoW = $('.logo').innerWidth();
        $('#tabsbox').width(winW - logoW - userW - 1);
    }
})

//消息的显示
$(function () {
    //打开新的页面；--yangxin;
    /*logo字体设置--yangxin*/
    fontSizes();
    function fontSizes() {
        var logoTitle_width =  $("#logoTitle").innerWidth();
        var logo_with = $("#logoTitle span").innerWidth();
        var _number = 24 ;
        if(logo_with > logoTitle_width ){
            for (var i = 0 ;i < 12 ;i++){
                _number--;
                $("#logoTitle span").css("fontSize",_number);
                var logoTitle_width =  $("#logoTitle").innerWidth();
                var logo_with = $("#logoTitle span").innerWidth();
                if(logo_with < logoTitle_width){
                    return;
                }
            }
        }
    }
    console.log("******click headerNav");
    $("#headerNav li[class='act']").myFunction();
});

$.fn.myFunction = function(event) {
    // 在这里编写你的代码
    var sysName=$(this).attr("name");
    $(this).siblings("li#navMore").find("li").removeClass("act");
    $(this).addClass("act").siblings().removeClass("act");
    var url = $(this).attr("url");
    var id = $(this).attr("id");
    var sysId = $(this).attr("menuId");
    if(!url)url="";
    $(this).attr("url","");
    url="";

    if(id=="navMore")return;
    lastClickSysName=currentClickSysName;
    currentClickSysName=sysName;
    initMenu(sysId,sysName);
};

function clickSys(sysId,menuId) {
    if(menuId) {
        clickMenuId=menuId;
    }
    $("#headerNav li[menuid='"+sysId+"']").click();
}

//防止频繁点击
// var fl=false;
//..todo:mark一下：这里有一些改动，主要是首页的左侧菜单锁定不显示
function initMenu(sysId,sysName) {
	// if(fl)return;
	// fl=true;
	if(!sysId)sysId="";

    if(!sysName) {
        sysName="";
    }
    var url = __rootPath + "/getMenusByMenuId.do?menuId="+ sysId+"&menuName="+encodeURIComponent(sysName);
    $.ajaxSettings.async = false;
    $.get(url, function (data) {
        // fl=false;
    	//构建左侧导航
        buildLeftMemu(data,'');
        //检测是否需要显示tab滑动按钮
        var tabsbox_W = $('.tabsbox').innerWidth(),
            tabwrap_W = $('.movewrap').innerWidth();
        if (tabsbox_W < tabwrap_W) {
            $('#tabsbox .fr').css('display', 'block');
        } else {
            $('#tabsbox .fr').css('display', 'none');
        }
    //    如果sysName是“首页”，则收缩
    //     if(sysName=='首页') {
    //         mini.get('mainLayout').updateRegion('west', {visible: false});
    //         windowResizes();
    //     }
        //知识管理是打开的新窗口，不让进入知识管理页面
/*        if(sysName=='知识管理') {
            $("#headerNav li[name='首页']").click();
        }*/
    });
    $.ajaxSettings.async = true;
}

var menuData;

function buildLeftMemu(data,url) {
    console.log("**************buidleftmenu");
    menuData = data;
    //若子系统下有子菜单，则展示左边
    if (menuData) {
        try {
            mainLayout = mini.get('mainLayout');
            if (mainLayout) {
                mainLayout.updateRegion('west', {visible: true});
                windowResizes();
            }
        } catch (e) {
        }
        var data = {"list": menuData,"sysUrl":url};
        var menuHtml = bt('leftMenuTemplate', data);
        $("#sidemenu").html(menuHtml).hide().slideToggle(10);
        attachMenuEvents(clickMenuId);
        clickMenuId='';
    }
}






