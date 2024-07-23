var openWindows=[];

function overview() {
    var totalWidth=window.screen.width-20;
/*    if(!window.parent.lastClickSysName||window.parent.lastClickSysName!='首页') {
        totalWidth=window.screen.width-20;
        totalWidth=document.body.clientWidth-20;
    }*/
    var width=(totalWidth-4*8)/3;
    var gridster = $(".personalPort .gridster > ul").gridster({
        widget_base_dimensions: [width, 500],
        autogenerate_stylesheet: true,
        widget_margins: [8, 8],
        draggable: {
            handle: 'header'
        },
        avoid_overlapped_widgets: true
    }).data('gridster');
    $('.personalPort  .gridster  ul').css({'padding': '0'});

}

function clickMenu(menuObj) {
    if(!menuObj) {
        return;
    }
    if(!menuObj.children || menuObj.children.length==0) {
        if(!menuObj.url) {
            mini.alert(locale_notOnline);
            return;
        }
        for(var index=0;index<openWindows.length;index++) {
            var oneObj=openWindows[index];
            oneObj.hide();
        }
        openWindows.clear();
        jumpMenu(menuObj.sysId,menuObj.menuId);
    } else {
        var windowObj=mini.open({
            title: menuObj.displayName,
            url: jsUseCtxPath + "/rdmHome/core/menuWindow.do",
            width: 650,
            height: 450,
            showModal:true,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                openWindows.push(windowObj);
                var data = { children: menuObj.children,openWindows: openWindows};
                iframe.contentWindow.setSubMenuData(data);
            },
            ondestroy: function (action) {
            }
        });

    }
}

function sysMoreMenu(sysId) {
    window.parent.clickSys(sysId);
}

function jumpMenu(sysId,menuId) {
    window.parent.clickSys(sysId,menuId);
}

function itemRenderer(record, rowIndex, meta, grid) {
    meta.rowCls = "item";
    var menu=record.url;
    var children=record.children;
    var totalWidth=window.screen.width-20;
    var containerBoxWidth=(totalWidth-4*8)/3;
    var itemWidth=(containerBoxWidth-5*8)/3;
    var html = '<div class="item-inner" style="width: '+itemWidth+'px">'
        + '<img class="item-image" src="'+jsUseCtxPath+'/styles/images/';
    if(!menu && (!children || children.length==0)) {
        html+='rdmMenuNo.png"/>'+ '<div class="item-text" style="color:red">' + record.displayName +'</div></div>';
    } else {
        html+='rdmMenuYes.png"/>' + '<div class="item-text">' + record.displayName+'</div></div>';
    }

    return html;
}