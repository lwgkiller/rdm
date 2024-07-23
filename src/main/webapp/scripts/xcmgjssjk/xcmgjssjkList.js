//明细
function jssjkDetail(jssjkId) {
    var action = "detail";
    var url = jsUseCtxPath + "/jssj/core/config/editPage.do?jssjkId=" + jssjkId+ "&action=" + action ;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            if (jssjkListGrid) {
                jssjkListGrid.reload()
            }
        }
    }, 1000);
}

//修改
function updateJssjk(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jssjkListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请选中一条记录");
        return;
    }

    //技术负责人
    if(rows[0].jsfzrId != currentUserId ){
        mini.alert("技术负责人非本人不允许修改");
        return;
    }

    var selectId= rows[0].jssjkId;
    var ifcz =false;
    var sbjsNum="";
    var splx="";
    $.ajax({
        url: jsUseCtxPath + '/jssj/core/config/zxDataQuery.do?selectId='+selectId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data != null && data !="") {
                ifcz = true;
                sbjsNum=data.sbjsNum;
                splx=data.splx;
            }
        }
    });
    if(ifcz){
        var splxStr="";
        if(splx=='update') {
            splxStr='修改';
        }
        if(splx=='delete') {
            splxStr='删除';
        }
        mini.alert("本条技术已存在"+splxStr+"流程正在执行中，请审批完成后再试！");
        return;
    }
    mini.confirm("对本条数据复制修改生成新的技术数据审批流程，请确认是否继续？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {

            var sptype = "update";
            var url = jsUseCtxPath + "/bpm/core/bpmInst/JSSJK/start.do?sptype=" + sptype + "&selectId=" + selectId;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    if (jssjkListGrid) {
                        jssjkListGrid.reload()
                    }
                }
            }, 1000);
        }
    });
}

function removeJssjk(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = jssjkListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请选中一条记录");
        return;
    }
    //不是技术负责人或者不是管理员

    if(rows[0].jsfzrId != currentUserId  ){
        if (currentUserNo != 'admin'){
            mini.alert("技术负责人非本人不允许删除");
            return;
        }
    }

    var selectId= rows[0].jssjkId;
    var ifcz =false;
    var sbjsNum="";
    var splx="";
    $.ajax({
        url: jsUseCtxPath + '/jssj/core/config/zxDataQuery.do?selectId='+selectId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if (data != null && data !="") {
                ifcz = true;
                sbjsNum=data.sbjsNum;
                splx=data.splx;
            }
        }
    });
    if(ifcz){
        var splxStr="";
        if(splx=='update') {
            splxStr='修改';
        }
        if(splx=='delete') {
            splxStr='删除';
        }
        mini.alert("本条技术已存在"+splxStr+"流程正在执行中，请审批完成后再试！");
        return;
    }
    mini.confirm("对本条数据启动申请删除流程，请确认是否继续？", "提示", function (action){
        if (action != 'ok') {
            return;
        } else {
            var sptype="delete";
            var url = jsUseCtxPath + "/bpm/core/bpmInst/JSSJK/start.do?sptype=" + sptype +"&selectId=" + selectId;
            var winObj = window.open(url);
            var loop = setInterval(function () {
                if (winObj.closed) {
                    clearInterval(loop);
                    if (jssjkListGrid) {
                        jssjkListGrid.reload()
                    }
                }
            }, 1000);
        }
    });

}