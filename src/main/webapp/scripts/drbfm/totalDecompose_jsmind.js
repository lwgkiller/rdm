var jm = null;
var zoomInButton = document.getElementById('zoom-in-button');
var zoomOutButton = document.getElementById('zoom-out-button');
$(function () {
    //同步方式查询科技项目列表
    var url = jsUseCtxPath + "/drbfm/total/projectList.do";
    $.ajax({
        url:url,
        method:'get',
        async:false,
        success:function (json) {
            mini.get("projectId").setData(json);
        }
    });

    load_jsmind();
    mouseWheel();
    $("#jsmind_container").bind("contextmenu",function(){ return false});
    $("#jsmind_container").on("contextmenu",editNode);
});

function load_jsmind() {
    var mind = {
        meta: {
            name: 'demo'
        },
        format: 'node_array',
        data: [],
    };
    var options = {
        container: 'jsmind_container',
        editable: true,
        theme: 'primary',
        mode:'side',
        view:{
            engine:'svg',
            draggable:true,
            hide_scrollbars_when_draggable:true
        }
    };
    mind.data.push({id:jsMind.util.uuid.newid(), isroot: true, topic: 'XX机型整机'});
    jm = jsMind.show(options, mind);
}

// 鼠标滚轮放大缩小
function mouseWheel () {
    var scrollFunc =function (e) {
        var wheelDelta=0;
        if(e && (e.wheelDelta || e.detail)) {
            if(e.wheelDelta) {
                wheelDelta = e.wheelDelta;
            } else {
                wheelDelta =-e.detail*50;
            }
        } else {
            wheelDelta=window.event.wheelDelta;
        }
        if (wheelDelta > 0) {
            //向上则放大
            jm.view.zoomIn();
        } else if (wheelDelta < 0) {
            jm.view.zoomOut();
        }
    };

    $("#jsmind_container").bind('DOMMouseScroll', scrollFunc, false);
    $("#jsmind_container").on("mousewheel",scrollFunc);
}

