$(function () {
       searchList();
})
function searchList() {
    var parent=$(".search-form",window.parent.document);
    var inputAry=$("input",parent);
    var params=[];
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    var data={};
    data.filter=mini.encode(params);
    planListGrid.load(data);
}

function loadGrid() {
    var mainId = mini.get('id').getValue();
    var paramArray = [{name: "mainId", value: mainId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    itemGrid.load(data);
}

//新增延期审批流程
function doDelayApply() {
    var planRows = [];
    planRows = planListGrid.getSelecteds();
    var sum = planRows.length;
    if (sum <= 0) {
        mini.alert(monthWorkList_name2);
        return;
    }
    var planIds = [];
    if(planRows.length>0){
        for(var i=0;i<planRows.length;i++){
            planIds.push(planRows[i].mainId);
        }
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YDJHYQSP/start.do?planIds="+planIds.join(',');
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            searchList();
        }
    }, 1000);
}

function addProjectPlan() {
    let url = jsUseCtxPath + "/rdmZhgl/core/monthWork/getEditPage.do?action=add";
    mini.open({
        title: monthWorkList_name3,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchList();
        }
    });
}
//修改
function editProjectPlan(_type) {
    //先判断 是项目计划还是非项目计划
    var planRows = [];
    planRows = planListGrid.getSelecteds();
    var sum = planRows.length;
    if (sum != 1) {
        mini.alert(monthWorkList_name4);
        return;
    }
    var mainId;
    var url;
    if(planRows.length==1){
        var responseMan = planRows[0].responseMan;
        if(responseMan!=currentUserName&& !permission){
            mini.alert(monthWorkList_name5);
            return;
        }
        mainId = planRows[0].mainId;
        //判断是否已经在审批中
        let postData = {"projectId":mainId,"type":"0"};
        let _url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isRunning.do';
        let resultData = ajaxRequest(_url,'POST',false,postData);
        if(resultData&&!resultData.success&&!isAdmin){
            if(_type=='finish'){
                if(resultData.data.processStatus!='SUCCESS_END'){
                    mini.alert(monthWorkList_name6);
                    return;
                }
            }else{
                mini.alert(monthWorkList_name7);
                return;
            }
        }

        if(_type=='finish'){
            url = jsUseCtxPath + "/rdmZhgl/core/monthWork/getEditPage.do?action=finish&&mainId="+mainId;
        }else{
            url = jsUseCtxPath + "/rdmZhgl/core/monthWork/getEditPage.do?action=edit&&mainId="+mainId;
        }
    }
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchList();
            planListGrid.deselectAll(true);

        }
    });
}
//删除记录
function removeRow() {
    //先判断 是项目计划还是非项目计划
    var planRows = [];
    planRows = planListGrid.getSelecteds();
    var sum = planRows.length;
    if (sum <= 0) {
        mini.alert(monthWorkList_name8);
        return;
    }
    if(planRows.length>0) {
        for (var i = 0, l = planRows.length; i < l; i++) {
            var r = planRows[i];
            var responseMan = r.responseMan;
            var asyncStatus = r.asyncStatus;
            if (currentUserName != responseMan&& !permission) {
                mini.alert(monthWorkList_name9);
                return;
            }
            if (asyncStatus == "1" && !isAdmin) {
                mini.alert(monthWorkList_name10);
                return;
            }
            let postData = {"projectId":r.mainId,"type":"0"};
            let _url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isRunning.do';
            let resultData = ajaxRequest(_url,'POST',false,postData);
            if(resultData&&!resultData.success&&!isAdmin){
                mini.alert(monthWorkList_name11);
                return;
            }
        }
    }
    mini.confirm(monthWorkList_name12, monthWorkList_name13, function (action) {
        if (action != 'ok') {
            return;
        } else {
            if(planRows.length>0){
                var ids = [];
                for (var i = 0, l = planRows.length; i < l; i++) {
                    var r = planRows[i];
                    ids.push(r.mainId);
                }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdmZhgl/core/monthWork/remove.do",
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            if (planListGrid) {
                                planListGrid.reload();
                            }
                        }
                    });
                }
            }
        }
    });
}
function exportBtn(){
    var params=[];
    var parent=$(".search-form",window.parent.document);
    var inputAry=$("input",parent);
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
//删除记录
function copyPlan() {
    yearForm.validate();
    if (!yearForm.isValid()) {
        mini.alert(monthWorkList_name14);
        return;
    }
    var yearMonth = mini.get('yearSelect').getText();
    //添加验证，只能复制到本月以后
   var currentYearMonth = getCurrentYearMonth();
   if(yearMonth<=currentYearMonth){
       mini.alert(monthWorkList_name15);
       return;
   }
    //先判断 是项目计划还是非项目计划
    var planRows = [];
    planRows = planListGrid.getSelecteds();
    var sum = planRows.length;
    if (sum != 1) {
        mini.alert(monthWorkList_name16);
        return;
    }
    if(planRows.length>0) {
        for (var i = 0, l = planRows.length; i < l; i++) {
            var r = planRows[i];
            var responseMan = r.responseMan;
            if (responseMan != currentUserName && !permission) {
                mini.alert(monthWorkList_name17);
                return;
            }
        }
    }
    if(planRows.length>0){
        var ids = [];
        for (var i = 0, l = planRows.length; i < l; i++) {
            var r = planRows[i];
            ids.push(r.mainId);
        }
        if (ids.length > 0) {
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/core/monthWork/copy.do",
                method: 'POST',
                data: {ids: ids.join(','),yearMonth:yearMonth},
                success: function (text) {
                    if (planListGrid) {
                        planListGrid.reload();
                    }
                }
            });
        }
    }
}
function openEditWindow() {
    yearMonthSelectedWindow.show();
}
function closeEditWindow() {
    yearMonthSelectedWindow.hide();
}

